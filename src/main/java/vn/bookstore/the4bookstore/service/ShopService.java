package vn.bookstore.the4bookstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.bookstore.the4bookstore.entity.SanPham;
import vn.bookstore.the4bookstore.entity.Shop;
import vn.bookstore.the4bookstore.entity.TaiKhoan;
import vn.bookstore.the4bookstore.repository.SanPhamRepository;
import vn.bookstore.the4bookstore.repository.ShopRepository;
import vn.bookstore.the4bookstore.repository.TaiKhoanRepository;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Transactional
public class ShopService {

    private final ShopRepository shopRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final SanPhamRepository sanPhamRepository;
    private final vn.bookstore.the4bookstore.repository.DonHangRepository donHangRepository;
    private final vn.bookstore.the4bookstore.repository.KhuyenMaiRepository khuyenMaiRepository;
    private final vn.bookstore.the4bookstore.repository.CauHinhHeThongRepository cauHinhHeThongRepository;

    public ShopService(ShopRepository shopRepository,
                       TaiKhoanRepository taiKhoanRepository,
                       SanPhamRepository sanPhamRepository,
                       vn.bookstore.the4bookstore.repository.DonHangRepository donHangRepository,
                       vn.bookstore.the4bookstore.repository.KhuyenMaiRepository khuyenMaiRepository,
                       vn.bookstore.the4bookstore.repository.CauHinhHeThongRepository cauHinhHeThongRepository) {
        this.shopRepository = shopRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.donHangRepository = donHangRepository;
        this.khuyenMaiRepository = khuyenMaiRepository;
        this.cauHinhHeThongRepository = cauHinhHeThongRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Shop> findByTaiKhoan(TaiKhoan taiKhoan) {
        return shopRepository.findByTaiKhoan(taiKhoan);
    }

    @Transactional(readOnly = true)
    public Optional<Shop> findBySlug(String slug) {
        return shopRepository.findBySlug(slug);
    }

    @Transactional(readOnly = true)
    public Optional<Shop> findById(Integer maShop) {
        return shopRepository.findById(maShop);
    }

    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable("pendingShopCount")
    public long countPendingShops() {
        return shopRepository.countByTrangThai("ChoDuyet");
    }

    @org.springframework.cache.annotation.CacheEvict(value = "pendingShopCount", allEntries = true)
    public Shop registerShop(TaiKhoan taiKhoan, String tenShop, String moTa, String diaChi, String soDienThoai, String email) {
        Optional<Shop> existingOpt = shopRepository.findByTaiKhoan(taiKhoan);
        if (existingOpt.isPresent()) {
            Shop existing = existingOpt.get();
            // Nếu hồ sơ trước đó đã bị từ chối hoặc đã xóa, cho phép cập nhật lại thông tin và gửi lại đơn
            if ("TuChoi".equalsIgnoreCase(existing.getTrangThai()) || "DaXoa".equalsIgnoreCase(existing.getTrangThai())) {
                if (!existing.getTenShop().equalsIgnoreCase(tenShop.trim()) && shopRepository.existsByTenShop(tenShop.trim())) {
                    throw new IllegalArgumentException("Tên shop đã tồn tại, vui lòng chọn tên khác!");
                }
                String slug = toSlug(tenShop);
                existing.setTenShop(tenShop.trim());
                existing.setSlug(slug);
                existing.setMoTa(moTa);
                existing.setDiaChiShop(diaChi);
                existing.setSoDienThoai(soDienThoai);
                existing.setEmailShop(email);
                existing.setTrangThai("ChoDuyet"); // Reset trạng thái về Chờ Duyệt
                existing.setNgayTao(LocalDateTime.now());
                existing.setNgayCapNhat(LocalDateTime.now());
                return shopRepository.save(existing);
            }
            throw new IllegalArgumentException("Tài khoản này đã đăng ký mở shop!");
        }
        if (shopRepository.existsByTenShop(tenShop)) {
            throw new IllegalArgumentException("Tên shop đã tồn tại, vui lòng chọn tên khác!");
        }

        String slug = toSlug(tenShop);
        if (shopRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis() % 10000;
        }

        Shop shop = new Shop();
        shop.setTaiKhoan(taiKhoan);
        shop.setTenShop(tenShop.trim());
        shop.setSlug(slug);
        shop.setMoTa(moTa);
        shop.setDiaChiShop(diaChi);
        shop.setSoDienThoai(soDienThoai);
        shop.setEmailShop(email);
        shop.setLogo("https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=150");
        shop.setBanner("https://images.unsplash.com/photo-1507842229451-7f01be45c06b?w=1200");
        shop.setTrangThai("ChoDuyet"); // Chờ Admin kiểm tra và duyệt gian hàng
        BigDecimal defaultCommission = cauHinhHeThongRepository.findById("PHI_SAN_MAC_DINH")
                .map(c -> {
                    try {
                        return new BigDecimal(c.getGiaTri());
                    } catch (Exception e) {
                        return new BigDecimal("5.00");
                    }
                })
                .orElse(new BigDecimal("5.00"));
        shop.setChietKhauPhanTram(defaultCommission);
        shop.setNgayTao(LocalDateTime.now());

        // Vai trò tài khoản vẫn giữ nguyên KHACHHANG/USER cho đến khi được Admin duyệt
        return shopRepository.save(shop);
    }

    public Shop updateShopProfile(Integer maShop, String tenShop, String moTa, String diaChi, String soDienThoai, String email, String logo, String banner) {
        Shop shop = shopRepository.findById(maShop)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy shop: " + maShop));

        if (!shop.getTenShop().equalsIgnoreCase(tenShop.trim()) && shopRepository.existsByTenShop(tenShop.trim())) {
            throw new IllegalArgumentException("Tên shop mới đã được sử dụng!");
        }

        shop.setTenShop(tenShop.trim());
        shop.setMoTa(moTa);
        shop.setDiaChiShop(diaChi);
        shop.setSoDienThoai(soDienThoai);
        shop.setEmailShop(email);
        if (logo != null && !logo.isBlank()) shop.setLogo(logo);
        if (banner != null && !banner.isBlank()) shop.setBanner(banner);
        shop.setNgayCapNhat(LocalDateTime.now());

        return shopRepository.save(shop);
    }

    @org.springframework.cache.annotation.CacheEvict(value = "pendingShopCount", allEntries = true)
    public void duyetShop(Integer maShop) {
        Shop shop = shopRepository.findById(maShop)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy shop"));
        shop.setTrangThai("HoatDong");
        shop.setNgayCapNhat(LocalDateTime.now());

        // Nâng cấp vai trò người dùng thành VENDOR khi đã được duyệt
        if (shop.getTaiKhoan() != null) {
            TaiKhoan tk = shop.getTaiKhoan();
            tk.setVaiTro("VENDOR");
            taiKhoanRepository.save(tk);
        }

        shopRepository.save(shop);
    }

    @org.springframework.cache.annotation.CacheEvict(value = "pendingShopCount", allEntries = true)
    public void tuChoiShop(Integer maShop) {
        Shop shop = shopRepository.findById(maShop)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy shop: " + maShop));
        shop.setTrangThai("TuChoi");
        shop.setNgayCapNhat(LocalDateTime.now());
        shopRepository.save(shop);
    }

    @org.springframework.cache.annotation.CacheEvict(value = {"book_of_the_month", "home_featured_books", "home_preview", "guest_top_sold"}, allEntries = true)
    public void khoaShop(Integer maShop) {
        Shop shop = shopRepository.findById(maShop)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy shop"));
        shop.setTrangThai("BiKhoa");
        shop.setNgayCapNhat(LocalDateTime.now());
        shopRepository.save(shop);

        // Khóa toàn bộ sản phẩm của shop
        List<SanPham> products = sanPhamRepository.findByShop_MaShop(maShop);
        for (SanPham sp : products) {
            sp.setTrangThaiKhoa("BiKhoaBoiAdmin");
            sanPhamRepository.save(sp);
        }
    }

    @org.springframework.cache.annotation.CacheEvict(value = {"book_of_the_month", "home_featured_books", "home_preview", "guest_top_sold"}, allEntries = true)
    public void moKhoaShop(Integer maShop) {
        Shop shop = shopRepository.findById(maShop)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy shop"));
        shop.setTrangThai("HoatDong");
        shop.setNgayCapNhat(LocalDateTime.now());
        shopRepository.save(shop);

        // Mở khóa các sản phẩm của shop
        List<SanPham> products = sanPhamRepository.findByShop_MaShop(maShop);
        for (SanPham sp : products) {
            sp.setTrangThaiKhoa("BinhThuong");
            sanPhamRepository.save(sp);
        }
    }

    @org.springframework.cache.annotation.CacheEvict(value = {"pendingShopCount", "book_of_the_month", "home_featured_books", "home_preview", "guest_top_sold"}, allEntries = true)
    public void xoaShop(Integer maShop) {
        Shop shop = shopRepository.findById(maShop)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy shop: " + maShop));

        // 1. Khóa và gỡ liên kết shop khỏi toàn bộ sản phẩm của shop
        List<SanPham> products = sanPhamRepository.findByShop_MaShop(maShop);
        for (SanPham sp : products) {
            sp.setShop(null);
            sp.setTrangThaiKhoa("BiKhoaBoiAdmin");
            sp.setTrangThai("NgungBan");
            sanPhamRepository.save(sp);
        }

        // 2. Gỡ liên kết shop khỏi các đơn hàng lịch sử
        donHangRepository.detachShopFromOrders(maShop);

        // 3. Gỡ liên kết shop khỏi khuyến mãi
        khuyenMaiRepository.detachShopFromPromotions(maShop);

        // 4. Trả vai trò tài khoản chủ shop về USER / KHACHHANG
        if (shop.getTaiKhoan() != null) {
            TaiKhoan tk = shop.getTaiKhoan();
            tk.setVaiTro("USER");
            taiKhoanRepository.save(tk);
        }

        // 5. Xóa hoàn toàn bản ghi gian hàng khỏi cơ sở dữ liệu
        shopRepository.delete(shop);
    }

    @Transactional(readOnly = true)
    public List<Shop> searchActiveShops(String keyword) {
        if (keyword == null || keyword.isBlank()) return List.of();
        return shopRepository.findByTenShopContainingIgnoreCaseAndTrangThai(keyword.trim(), "HoatDong");
    }

    public void updateChietKhau(Integer maShop, BigDecimal chietKhau) {
        if (chietKhau == null || chietKhau.compareTo(BigDecimal.ZERO) < 0 || chietKhau.compareTo(new BigDecimal("50.0")) > 0) {
            throw new IllegalArgumentException("Tỷ lệ chiết khấu phí sàn cho từng shop phải nằm trong khoảng từ 0% đến 50%!");
        }
        Shop shop = shopRepository.findById(maShop)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy shop"));
        shop.setChietKhauPhanTram(chietKhau);
        shop.setNgayCapNhat(LocalDateTime.now());
        shopRepository.save(shop);
    }

    @Transactional(readOnly = true)
    public Page<Shop> getAllShops(Pageable pageable) {
        return shopRepository.findByTrangThaiNot("DaXoa", pageable);
    }

    @Transactional(readOnly = true)
    public List<Shop> getPendingShops() {
        return shopRepository.findByTrangThai("ChoDuyet");
    }

    @Transactional(readOnly = true)
    public Page<Shop> searchShops(String keyword, Pageable pageable) {
        return searchShopsByCriteria(keyword, "all", pageable);
    }

    @Transactional(readOnly = true)
    public Page<Shop> searchShopsByCriteria(String keyword, String searchType, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return getAllShops(pageable);
        }
        String type = (searchType != null && !searchType.isBlank()) ? searchType.trim() : "all";
        return shopRepository.searchShopsByCriteria(keyword.trim(), type, pageable);
    }

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String toSlug(String input) {
        if (input == null || input.isBlank()) return "shop-" + (System.currentTimeMillis() % 100000);
        String s = input.replace('đ', 'd').replace('Đ', 'D');
        String nowhitespace = WHITESPACE.matcher(s.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalized).replaceAll("");
        slug = NONLATIN.matcher(slug).replaceAll("");
        slug = slug.replaceAll("-+", "-").toLowerCase(Locale.ENGLISH);
        slug = slug.replaceAll("^-|-$", "");
        return slug.isBlank() ? "shop-" + (System.currentTimeMillis() % 100000) : slug;
    }
}
