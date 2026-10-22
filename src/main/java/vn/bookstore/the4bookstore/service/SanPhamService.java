package vn.bookstore.the4bookstore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import vn.bookstore.the4bookstore.entity.DanhMuc;
import vn.bookstore.the4bookstore.entity.SanPham;
import vn.bookstore.the4bookstore.repository.*;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SanPhamService {

    public static final List<String> CUSTOMER_STATUSES = List.of("DangBan", "HetHang");

    private final SanPhamRepository sanPhamRepository;
    private final DanhMucRepository danhMucRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final ChiTietGioHangRepository chiTietGioHangRepository;
    private final DanhGiaRepository danhGiaRepository;
    private final SanPhamTacGiaRepository sanPhamTacGiaRepository;
    private final SanPhamYeuThichRepository sanPhamYeuThichRepository;

    public SanPhamService(
            SanPhamRepository sanPhamRepository,
            DanhMucRepository danhMucRepository,
            ChiTietDonHangRepository chiTietDonHangRepository,
            ChiTietGioHangRepository chiTietGioHangRepository,
            DanhGiaRepository danhGiaRepository,
            SanPhamTacGiaRepository sanPhamTacGiaRepository,
            SanPhamYeuThichRepository sanPhamYeuThichRepository) {
        this.sanPhamRepository = sanPhamRepository;
        this.danhMucRepository = danhMucRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
        this.chiTietGioHangRepository = chiTietGioHangRepository;
        this.danhGiaRepository = danhGiaRepository;
        this.sanPhamTacGiaRepository = sanPhamTacGiaRepository;
        this.sanPhamYeuThichRepository = sanPhamYeuThichRepository;
    }

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Thuật toán tuyển chọn "SÁCH NỔI BẬT THÁNG" (Book of the Month Spotlight Algorithm):
     *
     * 1. Tập ứng viên (Candidates):
     *    Lấy danh sách các cuốn sách đang bán, không bị khóa, shop hoạt động, còn tồn kho.
     *
     * 2. Công thức chấm điểm đa tiêu chí (Multi-factor Weighted Scoring Formula):
     *    Score = (W_recentSales * sales30d) 
     *          + (W_totalSales * totalSold) 
     *          + (W_rating * ratingScore) 
     *          + (W_favorites * favoriteCount) 
     *          + recencyBonus
     *
     *    - sales30d: Số lượng bán trong 30 ngày qua (trọng số cao nhất: x25 điểm/cuốn).
     *    - totalSold: Tổng lượng bán lũy kế (x2 điểm/cuốn).
     *    - ratingScore: Điểm đánh giá trung bình nhân logarit số lượt đánh giá.
     *    - favoriteCount: Lượt đưa vào danh sách yêu thích (x3 điểm/lượt).
     *    - recencyBonus: Điểm thưởng thời gian xuất bản gần đây (10-25 điểm).
     *
     * 3. Cơ chế Fallback an toàn:
     *    Nếu chưa có đơn hàng hoặc dữ liệu rỗng, tự động chọn cuốn sách bán chạy nhất
     *    hoặc cuốn sách mới nhất có đầy đủ hình ảnh bìa.
     */
    @Cacheable("book_of_the_month")
    public SanPham getBookOfTheMonth() {
        List<SanPham> candidates = sanPhamRepository.findSpotlightCandidates(PageRequest.of(0, 1));
        if (!candidates.isEmpty()) {
            return candidates.get(0);
        }
        Pageable p = PageRequest.of(0, 1);
        List<SanPham> fallbackList = sanPhamRepository.findTopBooksByTrangThais(CUSTOMER_STATUSES, p);
        return fallbackList.isEmpty() ? null : fallbackList.get(0);
    }

    @Cacheable("home_featured_books")
    public List<SanPham> getFeaturedBooks() {
        List<DanhMuc> bookCategories = getCategoriesByLoaiSP("Sach");
        List<Integer> bookCatIds = bookCategories.stream().map(DanhMuc::getMaDanhMuc).toList();

        Pageable pageable = PageRequest.of(0, 8);
        if (!bookCatIds.isEmpty()) {
            List<SanPham> books = sanPhamRepository.findTopBooksByCategoriesAndTrangThais(CUSTOMER_STATUSES, bookCatIds, pageable);
            if (!books.isEmpty()) {
                return books;
            }
        }
        return sanPhamRepository.findTopBooksByTrangThais(CUSTOMER_STATUSES, pageable);
    }

    @Cacheable(value = "home_preview", key = "#loaiSP")
    public List<SanPham> getPreviewByLoaiSP(String loaiSP) {
        return sanPhamRepository.findTop4ByLoaiSPAndTrangThaiIn(loaiSP, CUSTOMER_STATUSES);
    }

    @Cacheable("guest_top_sold")
    public List<SanPham> getGuestTopSold() {
        return sanPhamRepository.findTopProductsForGuest();
    }

    @Cacheable(value = "categories", key = "#loaiSP")
    public List<DanhMuc> getCategoriesByLoaiSP(String loaiSP) {
        if (loaiSP == null || loaiSP.isBlank() || "ALL".equalsIgnoreCase(loaiSP)) {
            List<DanhMuc> allActive = danhMucRepository.findByTrangThaiTrueOrderByMaDanhMucAsc();
            return allActive.isEmpty() ? danhMucRepository.findAll() : allActive;
        }

        // Truy vấn trực tiếp theo trường loaiSP trong bảng DANH_MUC
        List<DanhMuc> categories = danhMucRepository.findByLoaiSPAndTrangThaiTrue(loaiSP);
        if (categories != null && !categories.isEmpty()) {
            return categories;
        }

        // Fallback an toàn (nếu DB có bản ghi cũ chưa cập nhật loaiSP hoặc bị rỗng)
        List<DanhMuc> allActive = danhMucRepository.findByTrangThaiTrueOrderByMaDanhMucAsc();
        return allActive.stream()
                .filter(dm -> {
                    String actualLoai = dm.getLoaiSP();
                    if (actualLoai != null && !actualLoai.isBlank()) {
                        return actualLoai.equalsIgnoreCase(loaiSP);
                    }
                    String name = dm.getTenDanhMuc() != null ? dm.getTenDanhMuc().toLowerCase() : "";
                    if ("VanPhongPham".equalsIgnoreCase(loaiSP)) {
                        return name.contains("bút") || name.contains("sổ") || name.contains("dụng cụ")
                                || name.contains("văn phòng phẩm") || name.contains("vpp");
                    } else if ("QuaTang".equalsIgnoreCase(loaiSP)) {
                        return name.contains("túi") || name.contains("book nook") || name.contains("boardgame")
                                || name.contains("quà tặng") || name.contains("nến") || name.contains("decor")
                                || name.contains("trang trí") || name.contains("đèn");
                    } else if ("Sach".equalsIgnoreCase(loaiSP)) {
                        boolean isVpp = name.contains("bút") || name.contains("sổ") || name.contains("dụng cụ")
                                || name.contains("văn phòng phẩm") || name.contains("vpp");
                        boolean isQt = name.contains("túi") || name.contains("book nook") || name.contains("boardgame")
                                || name.contains("quà tặng") || name.contains("nến") || name.contains("decor")
                                || name.contains("trang trí") || name.contains("đèn");
                        return !isVpp && !isQt;
                    }
                    return false;
                })
                .toList();
    }

    // Tìm kiếm + lọc theo nhóm loạiSP (hoặc toàn bộ nếu loaiSP null/rỗng/"ALL")
    public Page<SanPham> searchAndFilter(
            String loaiSP,
            String keyword,
            Integer maDanhMuc,
            String sortBy,
            int page,
            int size) {

        Sort sort = buildSort(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasCategory = maDanhMuc != null && maDanhMuc > 0;
        boolean hasLoaiSP = loaiSP != null && !loaiSP.isBlank() && !"ALL".equalsIgnoreCase(loaiSP);

        if (hasLoaiSP) {
            if (hasKeyword && hasCategory) {
                return sanPhamRepository.searchByLoaiSPAndDanhMucAndKeyword(loaiSP, maDanhMuc, keyword.trim(), pageable);
            } else if (hasKeyword) {
                return sanPhamRepository.searchByLoaiSPAndKeyword(loaiSP, keyword.trim(), pageable);
            } else if (hasCategory) {
                DanhMuc dm = danhMucRepository.findById(maDanhMuc).orElse(null);
                if (dm != null) {
                    return sanPhamRepository.findByLoaiSPAndDanhMucAndTrangThaiIn(loaiSP, dm, CUSTOMER_STATUSES, pageable);
                }
            }
            return sanPhamRepository.findByLoaiSPAndTrangThaiIn(loaiSP, CUSTOMER_STATUSES, pageable);
        } else {
            if (hasKeyword && hasCategory) {
                return sanPhamRepository.searchAllByDanhMucAndKeyword(keyword.trim(), maDanhMuc, pageable);
            } else if (hasKeyword) {
                return sanPhamRepository.searchAllByKeyword(keyword.trim(), pageable);
            } else if (hasCategory) {
                DanhMuc dm = danhMucRepository.findById(maDanhMuc).orElse(null);
                if (dm != null) {
                    return sanPhamRepository.findByDanhMucAndTrangThaiIn(dm, CUSTOMER_STATUSES, pageable);
                }
            }
            return sanPhamRepository.findByTrangThaiIn(CUSTOMER_STATUSES, pageable);
        }
    }

    public Page<SanPham> searchAndFilter(
            String loaiSP,
            String keyword,
            Integer maDanhMuc,
            Integer maTacGia,
            String sortBy,
            int page,
            int size) {

        Sort sort = buildSort(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        if (maTacGia != null && maTacGia > 0) {
            return sanPhamRepository.findByTacGiaId(maTacGia, pageable);
        }

        return searchAndFilter(loaiSP, keyword, maDanhMuc, sortBy, page, size);
    }

    public Page<SanPham> searchAndFilter(
            String loaiSP,
            String keyword,
            Integer maDanhMuc,
            Integer maTacGia,
            Integer maNXB,
            String sortBy,
            int page,
            int size) {
        return searchAndFilter(loaiSP, keyword, maDanhMuc, maTacGia, sortBy, page, size);
    }

    public Page<SanPham> searchAndFilterByGroup(
            String loaiSP,
            String keyword,
            Integer maDanhMuc,
            String sortBy,
            int page,
            int size) {
        return searchAndFilter(loaiSP, keyword, maDanhMuc, sortBy, page, size);
    }

    public Optional<SanPham> getById(Integer id) {
        Optional<SanPham> opt = sanPhamRepository.findById(id);
        opt.ifPresent(sp -> {
            if (sp.getSanPhamTacGias() != null) {
                sp.getSanPhamTacGias().forEach(sptg -> {
                    if (sptg != null && sptg.getTacGia() != null) {
                        sptg.getTacGia().getTenTacGia();
                    }
                });
            }
        });
        return opt;
    }

    public List<SanPham> getRelatedBooks(SanPham sanPham, int limit) {
        if (sanPham.getDanhMuc() == null) return List.of();
        Pageable pageable = PageRequest.of(0, limit + 1);
        Page<SanPham> p = sanPhamRepository.findByDanhMucAndTrangThaiIn(sanPham.getDanhMuc(), CUSTOMER_STATUSES, pageable);
        return p.getContent().stream()
                .filter(sp -> !sp.getMaSP().equals(sanPham.getMaSP()))
                .limit(limit)
                .toList();
    }

    private Sort buildSort(String sortBy) {
        if (sortBy == null) return Sort.by(Sort.Direction.DESC, "ngayTao");
        return switch (sortBy) {
            case "price_asc"  -> Sort.by(Sort.Direction.ASC,  "giaBan");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "giaBan");
            case "name_asc"   -> Sort.by(Sort.Direction.ASC,  "tenSP");
            default           -> Sort.by(Sort.Direction.DESC, "ngayTao");
        };
    }

    /**
     * Xóa sản phẩm: Sử dụng Soft Delete (trangThai = 'DaXoa') để đảm bảo toàn vẹn dữ liệu
     * cho toàn bộ các quan hệ (đơn hàng, giỏ hàng, kho hàng, kiểm kê, đánh giá).
     * Dọn dẹp sản phẩm khỏi các giỏ hàng hiện tại của khách hàng.
     */
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = {"authors", "home_featured_books", "home_preview", "guest_top_sold", "book_of_the_month", "categories", "allActiveCategories", "categoriesByLoaiSP"}, allEntries = true)
    public void deleteProduct(Integer maSP) {
        SanPham sp = sanPhamRepository.findById(maSP)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với mã: " + maSP));

        sp.setTrangThai("DaXoa");
        sanPhamRepository.save(sp);

        try {
            chiTietGioHangRepository.deleteBySanPham_MaSP(maSP);
        } catch (Exception ignored) {}
    }
}