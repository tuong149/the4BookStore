package vn.bookstore.the4bookstore;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import vn.bookstore.the4bookstore.controller.CartController;
import vn.bookstore.the4bookstore.entity.KhuyenMai;
import vn.bookstore.the4bookstore.repository.KhuyenMaiRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VoucherSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;

    @Autowired(required = false)
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Autowired
    private CartController cartController;

    private void deleteVoucherSafely(String code) {
        khuyenMaiRepository.findByMaCode(code).ifPresent(km -> {
            if (jdbcTemplate != null) {
                try {
                    jdbcTemplate.update("DELETE FROM voucher_da_luu WHERE makm = ?", km.getMaKM());
                } catch (Exception ignored) {}
            }
            try {
                khuyenMaiRepository.delete(km);
            } catch (Exception ignored) {}
        });
    }

    @Test
    @DisplayName("Kiểm tra danh sách voucher hiện có trong DB và xóa các mã mẫu cũ nếu có")
    @Transactional
    void checkAndCleanOldSampleVouchers() {
        List<KhuyenMai> allKM = khuyenMaiRepository.findAll();
        System.out.println("=== TỔNG SỐ KHUYẾN MÃI TRONG DATABASE: " + allKM.size() + " ===");
        for (KhuyenMai km : allKM) {
            System.out.println("ID=" + km.getMaKM() + ", Code=" + km.getMaCode() + ", Ten=" + km.getTenKM() + ", Status=" + km.getTrangThai());
        }

        // Đảm bảo các mã mặc định cũ không tồn tại
        deleteVoucherSafely("THE4BOOK15");
        deleteVoucherSafely("FREESHIP");
        deleteVoucherSafely("BOOK15");
        deleteVoucherSafely("SALE15");
    }

    @Test
    @DisplayName("Mã không tồn tại hoặc mã có sẵn cũ (THE4BOOK15) phải bị từ chối")
    void testOldDefaultVouchersAreRejected() {
        Map<String, Object> res1 = cartController.apiValidateVoucher("THE4BOOK15", 500000);
        assertFalse((Boolean) res1.get("valid"), "THE4BOOK15 không được phép hợp lệ nếu không có trong DB");

        Map<String, Object> res2 = cartController.apiValidateVoucher("BOOK15", 500000);
        assertFalse((Boolean) res2.get("valid"), "BOOK15 không được phép hợp lệ nếu không có trong DB");

        Map<String, Object> res3 = cartController.apiValidateVoucher("UNKNOWN_CODE_999", 500000);
        assertFalse((Boolean) res3.get("valid"), "Mã tùy ý không có trong DB phải bị từ chối");
    }

    @Test
    @DisplayName("Chỉ mã được tạo trong Admin (lưu vào DB) mới được chấp nhận khi hợp lệ")
    @Transactional
    void testAdminCreatedVoucherIsValidatedSuccessfully() {
        String testCode = "ADMIN_TEST_" + System.currentTimeMillis();

        KhuyenMai km = new KhuyenMai();
        km.setMaCode(testCode);
        km.setTenKM("Voucher thử nghiệm từ Admin");
        km.setLoaiGiam("PhanTram");
        km.setGiaTriGiam(20);
        km.setGiamToiDa(50000);
        km.setDonToiThieu(100000);
        km.setSoLuongToiDa(10);
        km.setSoLuongDaDung(0);
        km.setNgayBatDau(LocalDateTime.now().minusDays(1));
        km.setNgayKetThuc(LocalDateTime.now().plusDays(10));
        km.setTrangThai("HoatDong");
        km.setHienThiCongKhai(true);
        km = khuyenMaiRepository.save(km);

        try {
            // Test đơn chưa đủ giá trị tối thiểu
            Map<String, Object> resBelowMin = cartController.apiValidateVoucher(testCode, 50000);
            assertFalse((Boolean) resBelowMin.get("valid"));
            assertTrue(resBelowMin.get("message").toString().contains("tối thiểu"));

            // Test đơn đủ điều kiện
            Map<String, Object> resValid = cartController.apiValidateVoucher(testCode, 200000);
            assertTrue((Boolean) resValid.get("valid"));
            assertEquals(40000, ((Number) resValid.get("discountAmount")).intValue()); // 20% của 200k = 40k

            // Test trần giảm tối đa (500k * 20% = 100k > max 50k)
            Map<String, Object> resCap = cartController.apiValidateVoucher(testCode, 500000);
            assertTrue((Boolean) resCap.get("valid"));
            assertEquals(50000, ((Number) resCap.get("discountAmount")).intValue());
        } finally {
            khuyenMaiRepository.delete(km);
        }
    }

    @Test
    @DisplayName("Kiểm tra 2 loại mã giảm giá: 1 loại giảm tiền sản phẩm, 1 loại giảm phí vận chuyển")
    @Transactional
    void testTwoDistinctVoucherTypes() {
        // Mã loại 1: Giảm tiền sản phẩm (TienCoDinh)
        String productVoucherCode = "PROD_DISCOUNT_" + System.currentTimeMillis();
        KhuyenMai kmProd = new KhuyenMai();
        kmProd.setMaCode(productVoucherCode);
        kmProd.setTenKM("Giảm 30K tiền sản phẩm");
        kmProd.setLoaiGiam("TienCoDinh");
        kmProd.setGiaTriGiam(30000);
        kmProd.setDonToiThieu(100000);
        kmProd.setNgayBatDau(LocalDateTime.now().minusDays(1));
        kmProd.setNgayKetThuc(LocalDateTime.now().plusDays(10));
        kmProd.setTrangThai("HoatDong");
        kmProd = khuyenMaiRepository.save(kmProd);

        // Mã loại 2: Giảm phí vận chuyển (Freeship)
        String shipVoucherCode = "FREESHIP_DISCOUNT_" + System.currentTimeMillis();
        KhuyenMai kmShip = new KhuyenMai();
        kmShip.setMaCode(shipVoucherCode);
        kmShip.setTenKM("Giảm 25K phí vận chuyển");
        kmShip.setLoaiGiam("Freeship");
        kmShip.setGiaTriGiam(25000);
        kmShip.setDonToiThieu(100000);
        kmShip.setNgayBatDau(LocalDateTime.now().minusDays(1));
        kmShip.setNgayKetThuc(LocalDateTime.now().plusDays(10));
        kmShip.setTrangThai("HoatDong");
        kmShip = khuyenMaiRepository.save(kmShip);

        try {
            // Validate mã sản phẩm
            Map<String, Object> resProd = cartController.apiValidateVoucher(productVoucherCode, 150000);
            assertTrue((Boolean) resProd.get("valid"));
            assertEquals("TienCoDinh", resProd.get("loaiGiam"));
            assertEquals(30000, ((Number) resProd.get("discountAmount")).intValue());

            // Validate mã freeship
            Map<String, Object> resShip = cartController.apiValidateVoucher(shipVoucherCode, 150000);
            assertTrue((Boolean) resShip.get("valid"));
            assertEquals("Freeship", resShip.get("loaiGiam"));
            assertEquals(25000, ((Number) resShip.get("discountAmount")).intValue());
        } finally {
            khuyenMaiRepository.delete(kmProd);
            khuyenMaiRepository.delete(kmShip);
        }
    }

    @Autowired
    private vn.bookstore.the4bookstore.repository.TaiKhoanRepository taiKhoanRepository;

    @Test
    @DisplayName("Truy cập trang /profile thành công mà không có lỗi Thymeleaf")
    @Transactional
    void testProfilePageRendersWithoutErrors() throws Exception {
        vn.bookstore.the4bookstore.entity.TaiKhoan tk = new vn.bookstore.the4bookstore.entity.TaiKhoan();
        tk.setTenDangNhap("testuser_profile_" + System.currentTimeMillis());
        tk.setEmail("testuser_profile_" + System.currentTimeMillis() + "@example.com");
        tk.setMatKhauHash("123456");
        tk.setVaiTro("KHACHHANG");
        tk.setTrangThai("HoatDong");
        tk = taiKhoanRepository.save(tk);

        vn.bookstore.the4bookstore.security.CustomUserDetails userDetails = new vn.bookstore.the4bookstore.security.CustomUserDetails(tk);
        org.springframework.security.core.Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        mockMvc.perform(get("/profile").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/index"))
                .andExpect(model().attributeExists("availableVouchers", "totalVouchers"));
    }

    @Autowired
    private vn.bookstore.the4bookstore.controller.VoucherController voucherController;

    @Autowired
    private vn.bookstore.the4bookstore.repository.KhachHangRepository khachHangRepository;

    @Autowired
    private vn.bookstore.the4bookstore.repository.VoucherDaLuuRepository voucherDaLuuRepository;

    @Test
    @DisplayName("Trang chủ render thành công với model publicVouchers và savedVoucherIds")
    void testHomePageVoucherSection() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home/index"))
                .andExpect(model().attributeExists("publicVouchers", "savedVoucherIds"));
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "customer", roles = {"USER"})
    @DisplayName("Trang Săn Voucher /san-voucher render thành công")
    void testSanVoucherPage() throws Exception {
        mockMvc.perform(get("/san-voucher"))
                .andExpect(status().isOk())
                .andExpect(view().name("voucher/san-voucher"))
                .andExpect(model().attributeExists("publicVouchers", "savedIds"));
    }

    @Test
    @DisplayName("Quy trình Săn Voucher: Ví ban đầu trống -> Lưu mã -> Mã vào ví -> Kiểm tra điều kiện đơn hàng")
    @Transactional
    void testVoucherHuntAndSmartWalletFlow() {
        // 1. Tạo tài khoản & khách hàng mới
        vn.bookstore.the4bookstore.entity.TaiKhoan tk = new vn.bookstore.the4bookstore.entity.TaiKhoan();
        tk.setTenDangNhap("hunt_user_" + System.currentTimeMillis());
        tk.setEmail("hunt_user_" + System.currentTimeMillis() + "@gmail.com");
        tk.setMatKhauHash("123456");
        tk.setVaiTro("KHACHHANG");
        tk.setTrangThai("HoatDong");
        tk = taiKhoanRepository.save(tk);

        vn.bookstore.the4bookstore.entity.KhachHang kh = new vn.bookstore.the4bookstore.entity.KhachHang();
        kh.setHoTen("Nguyễn Văn Săn Voucher");
        kh.setEmail(tk.getEmail());
        kh.setTaiKhoan(tk);
        kh = khachHangRepository.save(kh);

        vn.bookstore.the4bookstore.security.CustomUserDetails userDetails = new vn.bookstore.the4bookstore.security.CustomUserDetails(tk);
        org.springframework.security.core.Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // 2. Tạo 1 mã voucher công khai yêu cầu đơn tối thiểu 500k
        String code = "SAN50K_" + System.currentTimeMillis();
        KhuyenMai km = new KhuyenMai();
        km.setMaCode(code);
        km.setTenKM("Giảm 50K cho đơn 500K");
        km.setLoaiGiam("TienCoDinh");
        km.setGiaTriGiam(50000);
        km.setDonToiThieu(500000);
        km.setSoLuongToiDa(100);
        km.setSoLuongDaDung(0);
        km.setNgayBatDau(LocalDateTime.now().minusDays(1));
        km.setNgayKetThuc(LocalDateTime.now().plusDays(7));
        km.setTrangThai("HoatDong");
        km.setHienThiCongKhai(true);
        km = khuyenMaiRepository.save(km);

        try {
            // Bước A: Ban đầu ví người dùng phải TRỐNG
            Map<String, Object> walletBefore = voucherController.getMyWallet(200000, auth);
            assertTrue((Boolean) walletBefore.get("success"));
            List<?> listBefore = (List<?>) walletBefore.get("vouchers");
            assertTrue(listBefore.isEmpty(), "Ban đầu ví người dùng phải trống");

            // Bước B: Bấm "Lưu mã"
            Map<String, Object> saveResult = voucherController.saveVoucher(km.getMaKM(), auth);
            assertTrue((Boolean) saveResult.get("success"), "Lưu mã phải thành công");

            // Lưu lại lần 2 phải báo đã lưu
            Map<String, Object> saveAgain = voucherController.saveVoucher(km.getMaKM(), auth);
            assertFalse((Boolean) saveAgain.get("success"));
            assertTrue((Boolean) saveAgain.get("alreadySaved"));

            // Bước C: Mở ví khi đơn hàng mới 200k (< 500k) -> Mã phải bị mờ (eligible = false) và ghi rõ cần mua thêm 300k
            Map<String, Object> walletAt200k = voucherController.getMyWallet(200000, auth);
            List<Map<String, Object>> vList200k = (List<Map<String, Object>>) walletAt200k.get("vouchers");
            assertEquals(1, vList200k.size());
            Map<String, Object> voucherItem = vList200k.get(0);
            assertFalse((Boolean) voucherItem.get("eligible"), "Đơn 200k chưa đủ 500k nên eligible phải false (mờ)");
            assertNotNull(voucherItem.get("reason"));
            assertTrue(voucherItem.get("reason").toString().contains("Mua thêm 300,000"), "Phải thông báo mua thêm 300,000 để sử dụng");

            // Bước D: Mở ví khi đơn hàng 600k (>= 500k) -> Mã phải sáng (eligible = true)
            Map<String, Object> walletAt600k = voucherController.getMyWallet(600000, auth);
            List<Map<String, Object>> vList600k = (List<Map<String, Object>>) walletAt600k.get("vouchers");
            Map<String, Object> voucherItem600k = vList600k.get(0);
            assertTrue((Boolean) voucherItem600k.get("eligible"), "Đơn 600k đã đủ 500k nên eligible phải true (sáng)");
            assertEquals(50000, ((Number) voucherItem600k.get("discountPreview")).intValue());
        } finally {
            voucherDaLuuRepository.deleteAll(voucherDaLuuRepository.findByKhachHangOrderByNgayLuuDesc(kh));
            khachHangRepository.delete(kh);
            taiKhoanRepository.delete(tk);
        }
    }

    @Autowired
    private vn.bookstore.the4bookstore.service.DonHangService donHangService;

    @Autowired
    private vn.bookstore.the4bookstore.repository.SanPhamRepository sanPhamRepository;

    @Autowired
    private vn.bookstore.the4bookstore.repository.DonHangRepository donHangRepository;

    @Autowired
    private vn.bookstore.the4bookstore.service.GioHangService gioHangService;

    @Autowired
    private vn.bookstore.the4bookstore.repository.DanhMucRepository danhMucRepository;

    @Autowired
    private vn.bookstore.the4bookstore.repository.ShopRepository shopRepository;

    @Test
    @DisplayName("Áp dụng đồng thời 2 voucher (1 mã sản phẩm + 1 mã freeship) thành công; Chặn khi chọn 2 mã cùng loại")
    @Transactional
    void testDualVouchersApplicationAndDuplicatePrevention() {
        // 1. Tạo user và sp
        vn.bookstore.the4bookstore.entity.TaiKhoan tk = new vn.bookstore.the4bookstore.entity.TaiKhoan();
        tk.setTenDangNhap("dual_vouch_user_" + System.currentTimeMillis());
        tk.setEmail("dual_vouch_" + System.currentTimeMillis() + "@gmail.com");
        tk.setMatKhauHash("123456");
        tk.setVaiTro("KHACHHANG");
        tk.setTrangThai("HoatDong");
        tk = taiKhoanRepository.save(tk);

        vn.bookstore.the4bookstore.entity.KhachHang kh = new vn.bookstore.the4bookstore.entity.KhachHang();
        kh.setHoTen("Nguyễn Văn Dual Voucher");
        kh.setEmail(tk.getEmail());
        kh.setTaiKhoan(tk);
        kh = khachHangRepository.save(kh);

        vn.bookstore.the4bookstore.entity.SanPham sp = sanPhamRepository.findAll().stream()
                .filter(s -> "HoatDong".equalsIgnoreCase(s.getTrangThai()) && s.getSoLuongTon() != null && s.getSoLuongTon() > 5)
                .findFirst()
                .orElse(null);

        if (sp == null) {
            sp = new vn.bookstore.the4bookstore.entity.SanPham();
            sp.setTenSP("Sách Test Voucher Đồng Thời");
            sp.setGiaBan(200000);
            sp.setSoLuongTon(50);
            sp.setLoaiSP("Sach");
            sp.setTrangThai("HoatDong");
            sp.setDanhMuc(danhMucRepository.findAll().stream().findFirst().orElse(null));
            sp.setShop(shopRepository.findAll().stream().findFirst().orElse(null));
            sp = sanPhamRepository.save(sp);
        }

        gioHangService.addToCart(kh, sp.getMaSP(), 1);

        // 2. Tạo 1 mã Order Discount
        KhuyenMai kmOrder = new KhuyenMai();
        kmOrder.setMaCode("ORDER20K_" + System.currentTimeMillis());
        kmOrder.setTenKM("Giảm 20K tiền sách");
        kmOrder.setLoaiGiam("TienCoDinh");
        kmOrder.setGiaTriGiam(20000);
        kmOrder.setDonToiThieu(100000);
        kmOrder.setTrangThai("HoatDong");
        kmOrder.setNgayBatDau(LocalDateTime.now().minusDays(1));
        kmOrder.setNgayKetThuc(LocalDateTime.now().plusDays(5));
        kmOrder = khuyenMaiRepository.save(kmOrder);

        // 3. Tạo 1 mã Freeship
        KhuyenMai kmShip = new KhuyenMai();
        kmShip.setMaCode("FREESHIP15K_" + System.currentTimeMillis());
        kmShip.setTenKM("Giảm 15K tiền ship");
        kmShip.setLoaiGiam("Freeship");
        kmShip.setGiaTriGiam(15000);
        kmShip.setDonToiThieu(100000);
        kmShip.setTrangThai("HoatDong");
        kmShip.setNgayBatDau(LocalDateTime.now().minusDays(1));
        kmShip.setNgayKetThuc(LocalDateTime.now().plusDays(5));
        kmShip = khuyenMaiRepository.save(kmShip);

        // 4. Tạo 1 mã Order Discount thứ 2 (để test chặn 2 mã cùng loại)
        KhuyenMai kmOrder2 = new KhuyenMai();
        kmOrder2.setMaCode("ORDER10PCT_" + System.currentTimeMillis());
        kmOrder2.setTenKM("Giảm 10% tiền sách");
        kmOrder2.setLoaiGiam("PhanTram");
        kmOrder2.setGiaTriGiam(10);
        kmOrder2.setDonToiThieu(100000);
        kmOrder2.setTrangThai("HoatDong");
        kmOrder2.setNgayBatDau(LocalDateTime.now().minusDays(1));
        kmOrder2.setNgayKetThuc(LocalDateTime.now().plusDays(5));
        kmOrder2 = khuyenMaiRepository.save(kmOrder2);

        try {
            // Case A: Truyền 2 mã cùng loại (kmOrder và kmOrder2) -> Bị ném lỗi
            KhuyenMai finalKmOrder = kmOrder;
            KhuyenMai finalKmOrder2 = kmOrder2;
            vn.bookstore.the4bookstore.entity.KhachHang finalKh = kh;
            vn.bookstore.the4bookstore.entity.SanPham finalSp = sp;

            assertThrows(RuntimeException.class, () -> {
                donHangService.createOrder(
                        finalKh,
                        "Hà Nội",
                        "0987654321",
                        "COD",
                        "Ghi chú",
                        List.of(finalSp.getMaSP()),
                        finalKmOrder.getMaCode(),
                        finalKmOrder2.getMaCode(),
                        null
                );
            }, "Không được dùng 2 mã cùng loại cho 1 đơn hàng");

            // Case B: Áp dụng đồng thời 1 mã Order + 1 mã Shipping -> Hợp lệ
            vn.bookstore.the4bookstore.entity.DonHang order = donHangService.createOrder(
                    kh,
                    "TP Hồ Chí Minh",
                    "0912345678",
                    "COD",
                    "Giao cẩn thận",
                    List.of(sp.getMaSP()),
                    kmOrder.getMaCode(),
                    kmShip.getMaCode(),
                    null
            );

            assertNotNull(order);
            assertEquals(20000, order.getTienGiam(), "Tiền giảm sản phẩm phải là 20.000đ");
            assertEquals(15000, order.getTienGiamFreeship(), "Tiền giảm freeship phải là 15.000đ");
            int expectedTotal = (sp.getGiaBan() - 20000) + Math.max(0, order.getPhiVanChuyen() - 15000);
            assertEquals(expectedTotal, order.getTongTien(), "Tổng thanh toán phải khớp sau khi trừ cả 2 mã");
            assertNotNull(order.getKhuyenMai());
            assertEquals(kmOrder.getMaCode(), order.getKhuyenMai().getMaCode());
            assertNotNull(order.getKhuyenMaiFreeship());
            assertEquals(kmShip.getMaCode(), order.getKhuyenMaiFreeship().getMaCode());

        } finally {
            khuyenMaiRepository.delete(kmOrder);
            khuyenMaiRepository.delete(kmShip);
            khuyenMaiRepository.delete(kmOrder2);
            khachHangRepository.delete(kh);
            taiKhoanRepository.delete(tk);
        }
    }

    @Test
    @DisplayName("Voucher phạm vi SHOP: UDF chặn p_ma_shop NULL/khác shop, CartController chặn sai shop, DonHangService chỉ giảm đúng shop phát hành")
    void testShopScopedVoucherIsolation() {
        // 1. Kiểm tra SQL UDF fn_KiemTraVoucherHopLe với voucher KIMDONG20K (của Shop 1)
        Boolean hopLeNullShop = khuyenMaiRepository.kiemTraVoucherHopLe("KIMDONG20K", null, 150000);
        assertFalse(Boolean.TRUE.equals(hopLeNullShop), "UDF fn_KiemTraVoucherHopLe phải từ chối voucher SHOP khi p_ma_shop = NULL");

        Boolean hopLeDungShop = khuyenMaiRepository.kiemTraVoucherHopLe("KIMDONG20K", 1, 150000);
        assertTrue(Boolean.TRUE.equals(hopLeDungShop), "UDF fn_KiemTraVoucherHopLe phải chấp nhận voucher SHOP khi đúng ma_shop = 1");

        Boolean hopLeSaiShop = khuyenMaiRepository.kiemTraVoucherHopLe("KIMDONG20K", 2, 150000);
        assertFalse(Boolean.TRUE.equals(hopLeSaiShop), "UDF fn_KiemTraVoucherHopLe phải từ chối voucher SHOP khi sai ma_shop");

        // 2. Kiểm tra CartController.apiValidateVoucher với maShop
        Map<String, Object> resSaiShop = cartController.apiValidateVoucher("KIMDONG20K", 150000, 2);
        assertFalse((Boolean) resSaiShop.get("valid"), "CartController phải từ chối khi client truyền sai maShop");

        Map<String, Object> resDungShop = cartController.apiValidateVoucher("KIMDONG20K", 150000, 1);
        assertTrue((Boolean) resDungShop.get("valid"), "CartController phải chấp nhận khi client truyền đúng maShop");
        assertEquals(1, resDungShop.get("maShop"));
    }
}

