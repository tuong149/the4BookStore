package vn.bookstore.the4bookstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.bookstore.the4bookstore.entity.*;
import vn.bookstore.the4bookstore.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
public class VendorService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ShopRepository shopRepository;
    private final SanPhamRepository sanPhamRepository;
    private final DonHangRepository donHangRepository;
    private final KhuyenMaiRepository khuyenMaiRepository;
    private final DanhMucRepository danhMucRepository;
    private final SanPhamService sanPhamService;
    private final ThanhToanRepository thanhToanRepository;
    private final TacGiaRepository tacGiaRepository;
    private final SanPhamTacGiaRepository sanPhamTacGiaRepository;
    private final vn.bookstore.the4bookstore.repository.VoucherDaLuuRepository voucherDaLuuRepository;
    private final ReportService reportService;

    public VendorService(ShopRepository shopRepository,
                         SanPhamRepository sanPhamRepository,
                         DonHangRepository donHangRepository,
                         KhuyenMaiRepository khuyenMaiRepository,
                         DanhMucRepository danhMucRepository,
                         SanPhamService sanPhamService,
                         ThanhToanRepository thanhToanRepository,
                         TacGiaRepository tacGiaRepository,
                         SanPhamTacGiaRepository sanPhamTacGiaRepository,
                         vn.bookstore.the4bookstore.repository.VoucherDaLuuRepository voucherDaLuuRepository,
                         ReportService reportService) {
        this.shopRepository = shopRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.donHangRepository = donHangRepository;
        this.khuyenMaiRepository = khuyenMaiRepository;
        this.danhMucRepository = danhMucRepository;
        this.sanPhamService = sanPhamService;
        this.thanhToanRepository = thanhToanRepository;
        this.tacGiaRepository = tacGiaRepository;
        this.sanPhamTacGiaRepository = sanPhamTacGiaRepository;
        this.voucherDaLuuRepository = voucherDaLuuRepository;
        this.reportService = reportService;
    }

    // --- DASHBOARD THỐNG KÊ CỦA SHOP ---
    @Transactional(readOnly = true)
    public Map<String, Object> getShopDashboardStats(Integer maShop) {
        Map<String, Object> stats = new HashMap<>();

        Integer activeBookCount = null;
        try {
            activeBookCount = shopRepository.countActiveBooksByShop(maShop);
        } catch (Exception ignored) {}
        long totalBooks = (activeBookCount != null) ? activeBookCount.longValue() : sanPhamRepository.countActiveByShop(maShop);
        long newOrders = donHangRepository.countByShop_MaShopAndTrangThaiIn(maShop, List.of("DonHangMoi", "ChoXuLy"));
        long confirmedOrders = donHangRepository.countByShop_MaShopAndTrangThai(maShop, "DaXacNhan");
        long pickedUpOrders = donHangRepository.countByShop_MaShopAndTrangThai(maShop, "DaLayHang");
        long shippingOrders = donHangRepository.countByShop_MaShopAndTrangThai(maShop, "DangGiao");
        long deliveredOrders = donHangRepository.countByShop_MaShopAndTrangThai(maShop, "DaGiao");
        long completedOrders = donHangRepository.countByShop_MaShopAndTrangThai(maShop, "HoanTat");
        long cancelRequests = donHangRepository.countByShop_MaShopAndTrangThai(maShop, "YeuCauHuy");
        long cancelledOrders = donHangRepository.countByShop_MaShopAndTrangThaiIn(maShop, List.of("DaHuy", "Huy"));
        long returnRefundOrders = donHangRepository.countByShop_MaShopAndTrangThai(maShop, "TraHangHoanTien");
        long disputeOrders = donHangRepository.countByShop_MaShopAndTrangThai(maShop, "TranhChap");

        java.time.LocalDate now = java.time.LocalDate.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);

        Long thisMonthRevenue = donHangRepository.getShopRevenueByDateRange(maShop, startOfMonth, endOfMonth);
        Long thisMonthPlatformFee = donHangRepository.getShopPlatformFeeByDateRange(maShop, startOfMonth, endOfMonth);
        Long totalRevenue = donHangRepository.getShopRevenue(maShop);
        Long totalPlatformFee = donHangRepository.getShopPlatformFeePaid(maShop);

        stats.put("totalBooks", totalBooks);
        stats.put("newOrders", newOrders);
        stats.put("confirmedOrders", confirmedOrders);
        stats.put("pickedUpOrders", pickedUpOrders);
        stats.put("shippingOrders", shippingOrders);
        stats.put("deliveredOrders", deliveredOrders);
        stats.put("completedOrders", completedOrders);
        stats.put("cancelRequests", cancelRequests);
        stats.put("cancelledOrders", cancelledOrders);
        stats.put("returnRefundOrders", returnRefundOrders);
        stats.put("disputeOrders", disputeOrders);

        stats.put("thisMonthRevenue", thisMonthRevenue != null ? thisMonthRevenue : 0L);
        stats.put("thisMonthPlatformFee", thisMonthPlatformFee != null ? thisMonthPlatformFee : 0L);
        stats.put("platformFee", thisMonthPlatformFee != null ? thisMonthPlatformFee : 0L);
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0L);
        stats.put("totalPlatformFee", totalPlatformFee != null ? totalPlatformFee : 0L);
        stats.put("netPayout", (totalRevenue != null ? totalRevenue : 0L));
        stats.put("currentMonth", now.getMonthValue());
        stats.put("stockWarningCount", reportService.getShopStockWarnings(maShop).size());

        return stats;
    }

    // --- BÁO CÁO DOANH THU THEO THÁNG CỦA SHOP (GỌI SP sp_DoanhThuShopTheoThang) ---
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getShopMonthlyRevenue(Integer maShop) {
        return reportService.getShopMonthlyRevenue(maShop);
    }

    // --- BẢNG CẢNH BÁO TỒN KHO CỦA SHOP (TRUY VẤN VIEW vw_CanhBaoTonKhoShop LỌC THEO ma_shop) ---
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getShopStockWarnings(Integer maShop) {
        return reportService.getShopStockWarnings(maShop);
    }

    // --- QUẢN LÝ SẢN PHẨM CỦA SHOP ---
    @Transactional(readOnly = true)
    public Page<SanPham> getShopProducts(Integer maShop, String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return sanPhamRepository.findActiveByShopAndKeyword(maShop, keyword.trim(), pageable);
        }
        return sanPhamRepository.findActiveByShop(maShop, pageable);
    }

    @Transactional(readOnly = true)
    public Page<SanPham> getShopProducts(Integer maShop, String keyword, Integer maDanhMuc, String loaiSP, String sortBy, Pageable pageable) {
        Sort sortOrder;
        if ("oldest".equalsIgnoreCase(sortBy)) {
            sortOrder = Sort.by(Sort.Direction.ASC, "ngayTao", "maSP");
        } else if ("price_asc".equalsIgnoreCase(sortBy)) {
            sortOrder = Sort.by(Sort.Direction.ASC, "giaBan");
        } else if ("price_desc".equalsIgnoreCase(sortBy)) {
            sortOrder = Sort.by(Sort.Direction.DESC, "giaBan");
        } else if ("bestseller".equalsIgnoreCase(sortBy)) {
            sortOrder = Sort.by(Sort.Direction.DESC, "soLuongDaBan");
        } else {
            sortOrder = Sort.by(Sort.Direction.DESC, "ngayTao", "maSP");
        }
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);

        String trimmedKeyword = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        String trimmedLoaiSP = (loaiSP != null && !loaiSP.isBlank()) ? loaiSP.trim() : null;

        return sanPhamRepository.filterShopProducts(maShop, trimmedKeyword, maDanhMuc, trimmedLoaiSP, sortedPageable);
    }

    public SanPham saveShopProduct(Integer maShop, SanPham sanPham, Integer maDanhMuc) {
        return saveShopProduct(maShop, sanPham, maDanhMuc, (String) null);
    }

    @org.springframework.cache.annotation.CacheEvict(value = {"authors", "home_featured_books", "home_preview", "guest_top_sold", "book_of_the_month", "categories"}, allEntries = true)
    public SanPham saveShopProduct(Integer maShop, SanPham sanPham, Integer maDanhMuc, String tenTacGia) {
        Shop shop = shopRepository.findById(maShop)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy shop: " + maShop));

        // Auto-generate ISBN if empty or null so user never has to invent or type code
        if (sanPham.getISBN() != null && !sanPham.getISBN().trim().isEmpty()) {
            sanPham.setISBN(sanPham.getISBN().trim());
        } else {
            sanPham.setISBN("BK-" + (System.currentTimeMillis() % 100000000));
        }
        if (sanPham.getMucTonToiThieu() == null) {
            sanPham.setMucTonToiThieu(10);
        }
        if (sanPham.getSoLuongTon() == null) {
            sanPham.setSoLuongTon(0);
        }
        if (sanPham.getGiaBan() == null) {
            sanPham.setGiaBan(0);
        }

        SanPham saved;
        if (sanPham.getMaSP() != null) {
            SanPham existing = sanPhamRepository.findById(sanPham.getMaSP())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
            if (existing.getShop() == null || !existing.getShop().getMaShop().equals(maShop)) {
                throw new SecurityException("Bạn không có quyền chỉnh sửa sản phẩm của shop khác!");
            }
            existing.setTenSP(sanPham.getTenSP());
            existing.setGiaBan(sanPham.getGiaBan());
            existing.setSoLuongTon(sanPham.getSoLuongTon());
            if (sanPham.getMucTonToiThieu() != null && sanPham.getMucTonToiThieu() > 0) {
                existing.setMucTonToiThieu(sanPham.getMucTonToiThieu());
            } else if (existing.getMucTonToiThieu() == null || existing.getMucTonToiThieu() <= 0) {
                existing.setMucTonToiThieu(10);
            }
            existing.setMoTa(sanPham.getMoTa());
            existing.setISBN(sanPham.getISBN());
            if (sanPham.getLoaiSP() != null && !sanPham.getLoaiSP().isBlank()) {
                existing.setLoaiSP("sach".equalsIgnoreCase(sanPham.getLoaiSP()) ? "Sach" : sanPham.getLoaiSP());
            } else if (existing.getLoaiSP() == null || "sach".equalsIgnoreCase(existing.getLoaiSP())) {
                existing.setLoaiSP("Sach");
            }
            if (existing.getTrangThaiKhoa() == null || "ChoDuyet".equalsIgnoreCase(existing.getTrangThaiKhoa())) {
                existing.setTrangThaiKhoa("BinhThuong");
            }
            if (sanPham.getHinhAnh() != null && !sanPham.getHinhAnh().isBlank() && !"/images/default-book.png".equals(sanPham.getHinhAnh())) {
                existing.setHinhAnh(sanPham.getHinhAnh());
            }
            existing.setTrangThai(sanPham.getTrangThai());

            if (maDanhMuc != null) {
                DanhMuc dm = danhMucRepository.findById(maDanhMuc).orElse(null);
                if (dm != null) {
                    existing.setDanhMuc(dm);
                    if (dm.getLoaiSP() != null && !dm.getLoaiSP().isBlank()) {
                        existing.setLoaiSP(dm.getLoaiSP());
                    }
                }
            }
            saved = sanPhamRepository.save(existing);
        } else {
            sanPham.setShop(shop);
            sanPham.setTrangThaiKhoa("BinhThuong");
            sanPham.setSoLuongDaBan(0);
            if (sanPham.getMucTonToiThieu() == null || sanPham.getMucTonToiThieu() <= 0) {
                sanPham.setMucTonToiThieu(10);
            }
            sanPham.setNgayTao(LocalDateTime.now());
            if (sanPham.getHinhAnh() == null || sanPham.getHinhAnh().isBlank() || "/images/default-book.png".equals(sanPham.getHinhAnh())) {
                sanPham.setHinhAnh("https://res.cloudinary.com/vkqtqiha/image/upload/v1791217232/the4bookstore/books/wqfgfnpkibjhi1vheb1n.png");
            }
            if (maDanhMuc != null) {
                DanhMuc dm = danhMucRepository.findById(maDanhMuc).orElse(null);
                if (dm != null) {
                    sanPham.setDanhMuc(dm);
                    if (dm.getLoaiSP() != null && !dm.getLoaiSP().isBlank()) {
                        sanPham.setLoaiSP(dm.getLoaiSP());
                    }
                }
            }
            if (sanPham.getDanhMuc() == null) {
                sanPham.setDanhMuc(danhMucRepository.findAll().stream().findFirst().orElse(null));
            }
            if (sanPham.getLoaiSP() == null || "sach".equalsIgnoreCase(sanPham.getLoaiSP())) {
                sanPham.setLoaiSP(sanPham.getDanhMuc() != null && sanPham.getDanhMuc().getLoaiSP() != null ? sanPham.getDanhMuc().getLoaiSP() : "Sach");
            }
            saved = sanPhamRepository.save(sanPham);
        }

        // Xử lý Tác Giả (hỗ trợ 1 hoặc nhiều tác giả cách nhau bởi dấu phẩy/chấm phẩy)
        if (tenTacGia != null) {
            if (sanPhamTacGiaRepository != null && saved.getMaSP() != null) {
                sanPhamTacGiaRepository.deleteBySanPhamId(saved.getMaSP());
                sanPhamTacGiaRepository.flush();
            }
            if (!tenTacGia.trim().isEmpty() && tacGiaRepository != null && sanPhamTacGiaRepository != null) {
                String[] authorNames = tenTacGia.split("[,;]");
                int order = 1;
                List<SanPhamTacGia> sptgList = new java.util.ArrayList<>();
                java.util.Set<Integer> processedAuthorIds = new java.util.HashSet<>();
                for (String rawName : authorNames) {
                    String cleanName = rawName.trim();
                    if (!cleanName.isEmpty()) {
                        TacGia tg = tacGiaRepository.findByTenTacGiaIgnoreCase(cleanName)
                                .orElseGet(() -> {
                                    TacGia newTg = new TacGia();
                                    newTg.setTenTacGia(cleanName);
                                    newTg.setMoTa("Tác giả sách");
                                    return tacGiaRepository.saveAndFlush(newTg);
                                });
                        if (tg.getMaTacGia() != null && !processedAuthorIds.contains(tg.getMaTacGia())) {
                            processedAuthorIds.add(tg.getMaTacGia());
                            SanPhamTacGia sptg = new SanPhamTacGia(saved, tg, order++);
                            sanPhamTacGiaRepository.save(sptg);
                            sptgList.add(sptg);
                        }
                    }
                }
                sanPhamTacGiaRepository.flush();
                saved.setSanPhamTacGias(sptgList);
            } else {
                saved.setSanPhamTacGias(new java.util.ArrayList<>());
            }
            if (entityManager != null) {
                entityManager.flush();
            }
        }

        return saved;
    }

    @org.springframework.cache.annotation.CacheEvict(value = {"authors", "home_featured_books", "home_preview", "guest_top_sold", "book_of_the_month", "categories"}, allEntries = true)
    public void deleteShopProduct(Integer maShop, Integer maSP) {
        SanPham sp = sanPhamRepository.findById(maSP)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
        if (sp.getShop() == null || !sp.getShop().getMaShop().equals(maShop)) {
            throw new SecurityException("Không có quyền xóa sản phẩm của shop khác!");
        }
        sanPhamService.deleteProduct(maSP);
    }

    // --- QUY TRÌNH QUẢN LÝ ĐƠN HÀNG 6 GIAI ĐOẠN & NGOẠI LỆ ---
    @Transactional(readOnly = true)
    public Page<DonHang> getShopOrders(Integer maShop, String trangThai, Pageable pageable) {
        if (trangThai != null && !trangThai.isBlank() && !"ALL".equalsIgnoreCase(trangThai)) {
            if ("DonHangMoi".equalsIgnoreCase(trangThai) || "ChoXuLy".equalsIgnoreCase(trangThai)) {
                return donHangRepository.findByShop_MaShopAndTrangThaiInOrderByNgayDatDesc(maShop, List.of("DonHangMoi", "ChoXuLy"), pageable);
            }
            if ("DaHuy".equalsIgnoreCase(trangThai) || "Huy".equalsIgnoreCase(trangThai)) {
                return donHangRepository.findByShop_MaShopAndTrangThaiInOrderByNgayDatDesc(maShop, List.of("DaHuy", "Huy"), pageable);
            }
            return donHangRepository.findByShop_MaShopAndTrangThaiOrderByNgayDatDesc(maShop, trangThai, pageable);
        }
        return donHangRepository.findByShop_MaShopOrderByNgayDatDesc(maShop, pageable);
    }

    // 0. Xác nhận đã thanh toán thành công
    public DonHang xacNhanThanhToan(Integer maDH, Integer maShop) {
        DonHang dh = checkAndGetShopOrder(maDH, maShop);
        if ("DaThanhToan".equalsIgnoreCase(dh.getTrangThaiThanhToan())) {
            return dh;
        }
        dh.setTrangThaiThanhToan("DaThanhToan");
        if ("ChoThanhToan".equalsIgnoreCase(dh.getTrangThai())) {
            dh.setTrangThai("ChoXuLy");
        }
        ThanhToan tt = thanhToanRepository.findFirstByDonHangOrderByMaThanhToanDesc(dh).orElse(null);
        if (tt != null) {
            tt.setTrangThai("ThanhCong");
            tt.setNgayThanhToan(LocalDateTime.now());
            thanhToanRepository.save(tt);
        } else {
            ThanhToan newTt = new ThanhToan();
            newTt.setDonHang(dh);
            newTt.setPhuongThuc(dh.getPhuongThucThanhToan() != null ? dh.getPhuongThucThanhToan() : "COD");
            newTt.setSoTien(dh.getTongTien() != null ? dh.getTongTien() : 0);
            newTt.setTrangThai("ThanhCong");
            newTt.setNgayThanhToan(LocalDateTime.now());
            newTt.setNoiDung("Shop xác nhận đã thanh toán thành công");
            thanhToanRepository.save(newTt);
        }
        return donHangRepository.save(dh);
    }

    // 1. Xác nhận đơn: Tự động sinh mã vận đơn, chuyển DaXacNhan
    public DonHang xacNhanDonHang(Integer maDH, Integer maShop) {
        DonHang dh = checkAndGetShopOrder(maDH, maShop);
        if (!"DonHangMoi".equalsIgnoreCase(dh.getTrangThai()) && !"ChoXuLy".equalsIgnoreCase(dh.getTrangThai()) && !"ChoXacNhan".equalsIgnoreCase(dh.getTrangThai())) {
            throw new IllegalStateException("Đơn hàng không ở trạng thái chờ xác nhận!");
        }
        dh.setTrangThai("DaXacNhan");
        dh.setNgayXacNhan(LocalDateTime.now());

        // Tự động sinh mã vận đơn nếu chưa có
        if (dh.getMaVanDon() == null || dh.getMaVanDon().isBlank()) {
            String prefix = "GHN";
            if (dh.getNhaVanChuyen() != null && dh.getNhaVanChuyen().getTenNvc() != null) {
                String ten = dh.getNhaVanChuyen().getTenNvc().toUpperCase();
                if (ten.contains("GHN") || ten.contains("GIAO HÀNG NHANH")) prefix = "GHN";
                else if (ten.contains("GHTK") || ten.contains("TIẾT KIỆM")) prefix = "GHTK";
                else if (ten.contains("VIETTEL")) prefix = "VTP";
                else if (ten.contains("VNPOST")) prefix = "VNPOST";
                else if (dh.getNhaVanChuyen().getMaNvc() != null) prefix = "NVC" + dh.getNhaVanChuyen().getMaNvc();
            }
            dh.setMaVanDon(prefix + "-" + dh.getMaDH() + "-" + (System.currentTimeMillis() % 100000));
        }

        try {
            donHangRepository.executeSpShopXacNhanDonHang(maDH, maShop, dh.getMaVanDon());
            return donHangRepository.findById(maDH).orElse(dh);
        } catch (Exception ignored) {
            return donHangRepository.save(dh);
        }
    }

    // 2. Đã lấy hàng / Đang đóng gói: Bàn giao shipper
    public DonHang daLayHang(Integer maDH, Integer maShop) {
        DonHang dh = checkAndGetShopOrder(maDH, maShop);
        if (!"DaXacNhan".equalsIgnoreCase(dh.getTrangThai())) {
            throw new IllegalStateException("Đơn hàng phải được xác nhận trước khi bàn giao vận chuyển!");
        }
        dh.setTrangThai("DaLayHang");
        return donHangRepository.save(dh);
    }

    // 3. Đang giao hàng: Shipper đang trên đường giao
    public DonHang dangGiaoHang(Integer maDH, Integer maShop) {
        DonHang dh = checkAndGetShopOrder(maDH, maShop);
        dh.setTrangThai("DangGiao");
        return donHangRepository.save(dh);
    }

    // 4. Đã giao hàng: Shipper giao thành công, kích hoạt thời gian chờ khiếu nại (tiền trong Escrow)
    public DonHang daGiaoHang(Integer maDH, Integer maShop) {
        DonHang dh = checkAndGetShopOrder(maDH, maShop);
        dh.setTrangThai("DaGiao");
        dh.setTrangThaiThanhToan("DaThanhToan");
        return donHangRepository.save(dh);
    }

    // 5. Hoàn tất & Quyết toán ví shop: Tiền về ví Shop = Giá trị đơn - Phí sàn (% Commission)
    public DonHang hoanTatDonHang(Integer maDH, Integer maShop) {
        DonHang dh = checkAndGetShopOrder(maDH, maShop);
        dh.setTrangThai("HoanTat");
        dh.setNgayHoanThanh(LocalDateTime.now());
        dh.setTrangThaiThanhToan("DaThanhToan");

        // Tính phí sàn (gọi fn_TinhPhiSan) và tiền thực nhận ví shop
        BigDecimal feeRate = (dh.getShop() != null && dh.getShop().getChietKhauPhanTram() != null)
                ? dh.getShop().getChietKhauPhanTram()
                : new BigDecimal("5.00");
        int tongTien = dh.getTongTien() != null ? dh.getTongTien() : 0;
        Integer dbFee = null;
        try {
            dbFee = donHangRepository.calculatePlatformFee(tongTien, maShop);
        } catch (Exception ignored) {}
        int feeAmount = (dbFee != null) ? dbFee : feeRate.multiply(new BigDecimal(tongTien)).divide(new BigDecimal(100), java.math.RoundingMode.HALF_UP).intValue();

        dh.setChietKhauAppPhanTram(feeRate);
        dh.setTienPhiSan(feeAmount);
        dh.setTienThucNhanShop(Math.max(0, tongTien - feeAmount));

        return donHangRepository.save(dh);
    }

    // Ngoại lệ A1: Shop chủ động hủy (do hết hàng đột xuất) -> Nhả tồn kho (gọi sp_HuyDonHang)
    public DonHang huyDonHang(Integer maDH, Integer maShop, String lyDo) {
        DonHang dh = checkAndGetShopOrder(maDH, maShop);
        String reason = (lyDo != null && !lyDo.isBlank()) ? lyDo : "Shop hết hàng đột xuất";
        try {
            donHangRepository.executeSpHuyDonHang(maDH, reason);
            return donHangRepository.findById(maDH).orElse(dh);
        } catch (Exception ignored) {
            dh.setTrangThai("DaHuy");
            dh.setLyDoHuy(reason);
            releaseOrderStock(dh);
            if ("DaThanhToan".equalsIgnoreCase(dh.getTrangThaiThanhToan())) {
                dh.setTrangThaiThanhToan("DaHoanTien");
            }
            return donHangRepository.save(dh);
        }
    }

    // Ngoại lệ A2: Shop chấp nhận yêu cầu hủy từ khách -> Nhả tồn kho (gọi sp_HuyDonHang)
    public DonHang chapNhanHuyDon(Integer maDH, Integer maShop) {
        DonHang dh = checkAndGetShopOrder(maDH, maShop);
        String reason = (dh.getLyDoHuy() != null ? dh.getLyDoHuy() + " (" : "") + "Shop chấp nhận hủy" + (dh.getLyDoHuy() != null ? ")" : "");
        try {
            donHangRepository.executeSpHuyDonHang(maDH, reason);
            return donHangRepository.findById(maDH).orElse(dh);
        } catch (Exception ignored) {
            dh.setTrangThai("DaHuy");
            dh.setLyDoHuy(reason);
            releaseOrderStock(dh);
            if ("DaThanhToan".equalsIgnoreCase(dh.getTrangThaiThanhToan())) {
                dh.setTrangThaiThanhToan("DaHoanTien");
            }
            return donHangRepository.save(dh);
        }
    }

    // Ngoại lệ A3: Shop từ chối yêu cầu hủy của khách -> Giữ nguyên DaXacNhan để tiếp tục giao
    public DonHang tuChoiHuyDon(Integer maDH, Integer maShop, String lyDo) {
        DonHang dh = checkAndGetShopOrder(maDH, maShop);
        dh.setTrangThai("DaXacNhan");
        dh.setLyDoTuChoi(lyDo != null && !lyDo.isBlank() ? lyDo : "Shop đã đóng gói và chuẩn bị bàn giao cho bưu tá");
        return donHangRepository.save(dh);
    }

    // Ngoại lệ B1: Shop đồng ý yêu cầu trả hàng / hoàn tiền -> Nhận lại sách, nhả tồn kho, hoàn tiền
    public DonHang dongYTraHang(Integer maDH, Integer maShop) {
        DonHang dh = checkAndGetShopOrder(maDH, maShop);
        if ("ChoXuLy".equalsIgnoreCase(dh.getTrangThai()) || "DonHangMoi".equalsIgnoreCase(dh.getTrangThai())) {
            throw new IllegalStateException("Đơn hàng chưa được xác nhận, không thể hoàn hàng!");
        }

        dh.setTrangThai("DaHoan");
        if ("DaThanhToan".equalsIgnoreCase(dh.getTrangThaiThanhToan()) || "HoanTat".equalsIgnoreCase(dh.getTrangThai())) {
            dh.setTrangThaiThanhToan("DaHoanTien");
            dh.setTienThucNhanShop(0);
            dh.setTienPhiSan(0);
        }

        // Nhận lại sách và nhả tồn kho
        releaseOrderStock(dh);

        return donHangRepository.save(dh);
    }

    // Ngoại lệ B2: Shop từ chối trả hàng -> Chuyển sang Tranh Chấp để Admin/Manager phân xử
    public DonHang tuChoiTraHang(Integer maDH, Integer maShop, String lyDo) {
        DonHang dh = checkAndGetShopOrder(maDH, maShop);
        dh.setTrangThai("TranhChap");
        dh.setLyDoTuChoi(lyDo != null && !lyDo.isBlank() ? lyDo : "Shop từ chối trả hàng");
        return donHangRepository.save(dh);
    }

    // Helper: Giải phóng và hoàn trả số lượng tồn kho của đơn hàng
    private void releaseOrderStock(DonHang dh) {
        if (dh.getChiTietDonHangs() != null) {
            for (ChiTietDonHang ct : dh.getChiTietDonHangs()) {
                if (ct.getSanPham() != null && ct.getSoLuong() != null) {
                    sanPhamRepository.increaseStock(ct.getSanPham().getMaSP(), ct.getSoLuong());
                    SanPham sp = sanPhamRepository.findById(ct.getSanPham().getMaSP()).orElse(null);
                    if (sp != null && sp.getSoLuongDaBan() != null) {
                        sp.setSoLuongDaBan(Math.max(0, sp.getSoLuongDaBan() - ct.getSoLuong()));
                        sanPhamRepository.save(sp);
                    }
                }
            }
        }
    }

    private DonHang checkAndGetShopOrder(Integer maDH, Integer maShop) {
        DonHang dh = donHangRepository.findById(maDH)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + maDH));
        if (dh.getShop() == null || !dh.getShop().getMaShop().equals(maShop)) {
            throw new SecurityException("Không có quyền can thiệp vào đơn hàng của shop khác!");
        }
        return dh;
    }

    // --- KHUYẾN MÃI CỦA SHOP ---
    @Transactional(readOnly = true)
    public List<KhuyenMai> getShopPromotions(Integer maShop) {
        return khuyenMaiRepository.findByShop_MaShopOrderByMaKMDesc(maShop);
    }

    public KhuyenMai createShopPromotion(Integer maShop, KhuyenMai km) {
        Shop shop = shopRepository.findById(maShop)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy shop: " + maShop));

        if (km.getMaCode() == null || km.getMaCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập mã voucher!");
        }

        String cleanCode = km.getMaCode().trim().toUpperCase();
        km.setMaCode(cleanCode);

        if (khuyenMaiRepository.existsByMaCode(cleanCode)) {
            String shopPrefix = (shop.getSlug() != null && !shop.getSlug().isBlank())
                    ? shop.getSlug().replace("-", "_").toUpperCase()
                    : ("SHOP" + shop.getMaShop());
            throw new IllegalArgumentException("Mã voucher '" + cleanCode + "' đã tồn tại trên sàn (đã được Admin hoặc Shop khác sử dụng)! Vui lòng chọn mã riêng cho shop (ví dụ: " + shopPrefix + "_" + cleanCode + " hoặc FS_" + shopPrefix + ").");
        }

        if (km.getTenKM() == null || km.getTenKM().trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập tên chương trình khuyến mãi!");
        }
        km.setTenKM(km.getTenKM().trim());

        if (km.getNgayBatDau() != null && km.getNgayKetThuc() != null) {
            if (km.getNgayBatDau().isAfter(km.getNgayKetThuc())) {
                throw new IllegalArgumentException("Thời gian bắt đầu không được muộn hơn thời gian kết thúc!");
            }
        }

        if ("Freeship".equalsIgnoreCase(km.getLoaiGiam())) {
            km.setLoaiKhuyenMai("MIEN_PHI_VAN_CHUYEN");
            if (km.getGiaTriGiam() == null || km.getGiaTriGiam() <= 0) {
                km.setGiaTriGiam(30000);
            }
        } else if (km.getLoaiKhuyenMai() == null || km.getLoaiKhuyenMai().isBlank()) {
            km.setLoaiKhuyenMai("GIAM_GIA_SAN_PHAM");
        }

        if (km.getGiamToiDa() != null && km.getGiamToiDa() <= 0) {
            km.setGiamToiDa(null);
        }

        km.setShop(shop);
        km.setPhamVi("SHOP");
        km.setTrangThai("HoatDong");
        return khuyenMaiRepository.save(km);
    }

    @Transactional
    public void deleteShopPromotion(Integer maShop, Integer maKM) {
        KhuyenMai km = khuyenMaiRepository.findById(maKM)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã khuyến mãi #" + maKM));

        if (km.getShop() == null || !km.getShop().getMaShop().equals(maShop)) {
            throw new IllegalArgumentException("Bạn không có quyền thao tác với voucher của shop khác!");
        }

        Long orderCount = donHangRepository.countByKhuyenMai(km);
        if (orderCount != null && orderCount > 0) {
            km.setTrangThai("VoHieuHoa");
            khuyenMaiRepository.save(km);
            throw new IllegalStateException("Voucher đã có " + orderCount + " đơn hàng sử dụng nên hệ thống đã tự động chuyển sang trạng thái Vô Hiệu Hóa thay vì xóa hẳn.");
        }

        voucherDaLuuRepository.deleteByKhuyenMai(km);
        khuyenMaiRepository.delete(km);
    }

    @Transactional
    public void toggleShopPromotionStatus(Integer maShop, Integer maKM) {
        KhuyenMai km = khuyenMaiRepository.findById(maKM)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã khuyến mãi #" + maKM));

        if (km.getShop() == null || !km.getShop().getMaShop().equals(maShop)) {
            throw new IllegalArgumentException("Bạn không có quyền thao tác với voucher của shop khác!");
        }

        if ("HoatDong".equalsIgnoreCase(km.getTrangThai())) {
            km.setTrangThai("VoHieuHoa");
        } else {
            km.setTrangThai("HoatDong");
        }
        khuyenMaiRepository.save(km);
    }
}
