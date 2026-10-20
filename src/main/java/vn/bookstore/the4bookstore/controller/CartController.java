package vn.bookstore.the4bookstore.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.bookstore.the4bookstore.entity.*;
import vn.bookstore.the4bookstore.repository.*;
import vn.bookstore.the4bookstore.security.CustomOAuth2User;
import vn.bookstore.the4bookstore.security.CustomOidcUser;
import vn.bookstore.the4bookstore.security.CustomUserDetails;
import vn.bookstore.the4bookstore.service.DonHangService;
import vn.bookstore.the4bookstore.service.GioHangService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CartController {

    private final GioHangService gioHangService;
    private final DonHangService donHangService;
    private final KhachHangRepository khachHangRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final KhuyenMaiRepository khuyenMaiRepository;
    private final DiaChiGiaoHangRepository diaChiGiaoHangRepository;
    private final NhaVanChuyenRepository nhaVanChuyenRepository;

    public CartController(GioHangService gioHangService,
                          DonHangService donHangService,
                          KhachHangRepository khachHangRepository,
                          TaiKhoanRepository taiKhoanRepository,
                          KhuyenMaiRepository khuyenMaiRepository,
                          DiaChiGiaoHangRepository diaChiGiaoHangRepository,
                          NhaVanChuyenRepository nhaVanChuyenRepository) {
        this.gioHangService = gioHangService;
        this.donHangService = donHangService;
        this.khachHangRepository = khachHangRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.khuyenMaiRepository = khuyenMaiRepository;
        this.diaChiGiaoHangRepository = diaChiGiaoHangRepository;
        this.nhaVanChuyenRepository = nhaVanChuyenRepository;
    }

    // ==================== Helper: Lấy TaiKhoan từ Authentication ====================

    private TaiKhoan getCurrentTaiKhoan(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            if (userDetails.getTaiKhoan() != null && userDetails.getTaiKhoan().getMaTaiKhoan() != null) {
                return taiKhoanRepository.findById(userDetails.getTaiKhoan().getMaTaiKhoan())
                        .orElse(userDetails.getTaiKhoan());
            }
        }
        if (principal instanceof CustomOAuth2User oAuth2User) {
            if (oAuth2User.getTaiKhoan() != null && oAuth2User.getTaiKhoan().getMaTaiKhoan() != null) {
                return taiKhoanRepository.findById(oAuth2User.getTaiKhoan().getMaTaiKhoan()).orElse(null);
            }
            if (oAuth2User.getEmail() != null && !oAuth2User.getEmail().isBlank()) {
                return taiKhoanRepository.findByEmail(oAuth2User.getEmail()).orElse(null);
            }
        }
        if (principal instanceof CustomOidcUser oidcUser) {
            if (oidcUser.getTaiKhoan() != null && oidcUser.getTaiKhoan().getMaTaiKhoan() != null) {
                return taiKhoanRepository.findById(oidcUser.getTaiKhoan().getMaTaiKhoan()).orElse(null);
            }
            if (oidcUser.getEmail() != null && !oidcUser.getEmail().isBlank()) {
                return taiKhoanRepository.findByEmail(oidcUser.getEmail()).orElse(null);
            }
        }
        if (principal instanceof OidcUser oidcUser) {
            String email = oidcUser.getEmail();
            if (email != null && !email.isBlank()) {
                return taiKhoanRepository.findByEmail(email).orElse(null);
            }
        }
        if (principal instanceof OAuth2User oauth2User) {
            Object emailObj = oauth2User.getAttribute("email");
            if (emailObj != null && !emailObj.toString().isBlank()) {
                return taiKhoanRepository.findByEmail(emailObj.toString()).orElse(null);
            }
        }

        String authName = authentication.getName();
        if (authName != null && !authName.isBlank()) {
            return taiKhoanRepository.findByEmail(authName)
                    .or(() -> taiKhoanRepository.findByTenDangNhap(authName))
                    .orElse(null);
        }
        return null;
    }

    private KhachHang getCurrentKhachHang(Authentication authentication) {
        TaiKhoan tk = getCurrentTaiKhoan(authentication);
        if (tk == null) return null;

        return khachHangRepository.findByTaiKhoan(tk).orElseGet(() -> {
            KhachHang kh = new KhachHang();
            kh.setTaiKhoan(tk);
            kh.setHoTen(tk.getTenDangNhap());
            kh.setEmail(tk.getEmail());
            kh.setNgayDangKy(LocalDateTime.now());
            return khachHangRepository.save(kh);
        });
    }

    // ==================== Trang Giỏ Hàng ====================

    @GetMapping("/gio-hang")
    public String viewCart(@RequestParam(value = "datHangThanhCong", required = false) Boolean datHangThanhCong,
                           @RequestParam(value = "maDH", required = false) Integer maDH,
                           Authentication authentication,
                           jakarta.servlet.http.HttpSession session,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        KhachHang kh = getCurrentKhachHang(authentication);

        if (kh == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để xem và quản lý giỏ hàng của bạn!");
            return "redirect:/login";
        }

        model.addAttribute("datHangThanhCong", Boolean.TRUE.equals(datHangThanhCong));
        if (!model.containsAttribute("orderSuccess")) {
            model.addAttribute("orderSuccess", false);
        }
        if (maDH != null) {
            model.addAttribute("maDH", maDH);
        }

        // Tự động gộp giỏ hàng session nếu có
        gioHangService.mergeSessionCartToDb(kh, session);

        List<ChiTietGioHang> cartItems = gioHangService.getCartItems(kh);
        int subtotal = gioHangService.calculateSubtotal(kh);
        int cartCount = gioHangService.getCartItemCount(kh);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("cartCount", cartCount);
        model.addAttribute("isAuthenticated", true);
        model.addAttribute("khachHang", kh);
        model.addAttribute("diaChiList", diaChiGiaoHangRepository.findByKhachHangOrderByLaMacDinhDescNgayTaoDesc(kh));
        model.addAttribute("carriers", nhaVanChuyenRepository.findByTrangThai("HoatDong"));

        return "cart/cart";
    }

    // ==================== Thêm vào giỏ hàng ====================

    @PostMapping({"/gio-hang/them", "/gio-hang/add"})
    public String addToCart(@RequestParam("maSP") Integer maSP,
                            @RequestParam(value = "soLuong", defaultValue = "1") Integer soLuong,
                            @RequestHeader(value = "Referer", required = false) String referer,
                            Authentication authentication,
                            jakarta.servlet.http.HttpSession session,
                            RedirectAttributes redirectAttributes) {
        KhachHang kh = getCurrentKhachHang(authentication);

        if (kh == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!");
            return "redirect:/login";
        }

        try {
            gioHangService.addToCart(kh, maSP, soLuong);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm sản phẩm vào giỏ hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }
        return "redirect:/gio-hang";
    }

    // ==================== Cập nhật số lượng ====================

    @PostMapping("/gio-hang/cap-nhat")
    public String updateCartItem(@RequestParam("maSP") Integer maSP,
                                 @RequestParam("soLuong") Integer soLuong,
                                 Authentication authentication,
                                 jakarta.servlet.http.HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        KhachHang kh = getCurrentKhachHang(authentication);
        if (kh == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để thao tác với giỏ hàng!");
            return "redirect:/login";
        }

        try {
            gioHangService.updateCartItemQuantity(kh, maSP, soLuong);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật giỏ hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/gio-hang";
    }

    // ==================== Xóa sản phẩm khỏi giỏ ====================

    @PostMapping("/gio-hang/xoa")
    public String removeFromCart(@RequestParam("maSP") Integer maSP,
                                 Authentication authentication,
                                 jakarta.servlet.http.HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        KhachHang kh = getCurrentKhachHang(authentication);
        if (kh == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để thao tác với giỏ hàng!");
            return "redirect:/login";
        }

        try {
            gioHangService.removeFromCart(kh, maSP);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/gio-hang";
    }

    // ==================== Xóa tất cả ====================

    @PostMapping("/gio-hang/xoa-tat-ca")
    public String clearCart(Authentication authentication,
                            jakarta.servlet.http.HttpSession session,
                            RedirectAttributes redirectAttributes) {
        KhachHang kh = getCurrentKhachHang(authentication);
        if (kh == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để thao tác với giỏ hàng!");
            return "redirect:/login";
        }

        gioHangService.clearCart(kh);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa tất cả sản phẩm trong giỏ hàng!");

        return "redirect:/gio-hang";
    }

    // ==================== Đặt hàng (Checkout) ====================

    @PostMapping("/gio-hang/dat-hang")
    public String placeOrder(@RequestParam(value = "maDiaChi", required = false) Integer maDiaChi,
                             @RequestParam(value = "diaChiGiao", required = false) String diaChiGiao,
                             @RequestParam(value = "soDienThoaiGiao", required = false) String soDienThoaiGiao,
                             @RequestParam(value = "maNvc", required = false) Integer maNvc,
                             @RequestParam(value = "phuongThuc", defaultValue = "COD") String phuongThuc,
                             @RequestParam(value = "ghiChu", required = false) String ghiChu,
                             @RequestParam(value = "selectedProductIds", required = false) List<Integer> selectedProductIds,
                             @RequestParam(value = "maVoucher", required = false) String maVoucher,
                             @RequestParam(value = "maVoucherOrder", required = false) String maVoucherOrder,
                             @RequestParam(value = "maVoucherShipping", required = false) String maVoucherShipping,
                             jakarta.servlet.http.HttpServletRequest request,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        KhachHang kh = getCurrentKhachHang(authentication);
        if (kh == null) {
            return "redirect:/login";
        }

        String finalDiaChi = diaChiGiao;
        String finalSoDienThoai = soDienThoaiGiao;

        if (maDiaChi != null) {
            DiaChiGiaoHang dc = diaChiGiaoHangRepository.findById(maDiaChi).orElse(null);
            if (dc != null && dc.getKhachHang().getMaKH().equals(kh.getMaKH())) {
                finalDiaChi = dc.getDiaChiDayDu();
                finalSoDienThoai = dc.getSoDienThoai();
            }
        }

        if (finalDiaChi == null || finalDiaChi.isBlank()) {
            finalDiaChi = kh.getDiaChi() != null ? kh.getDiaChi() : "Chưa cung cấp địa chỉ";
        }
        if (finalSoDienThoai == null || finalSoDienThoai.isBlank()) {
            finalSoDienThoai = kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "0900000000";
        }

        // Tương thích ngược: Nếu chỉ có maVoucher thì xem là maVoucherOrder
        String orderVoucher = (maVoucherOrder != null && !maVoucherOrder.isBlank()) ? maVoucherOrder : maVoucher;
        String shippingVoucher = maVoucherShipping;

        try {
            List<DonHang> createdOrders = donHangService.createOrders(kh, finalDiaChi, finalSoDienThoai, phuongThuc, ghiChu, selectedProductIds, orderVoucher, shippingVoucher, maNvc);

            if (createdOrders == null || createdOrders.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể tạo đơn hàng, vui lòng thử lại!");
                return "redirect:/gio-hang";
            }

            DonHang primaryOrder = createdOrders.get(0);
            boolean isMultiShop = createdOrders.size() > 1;

            if ("VIETQR".equalsIgnoreCase(phuongThuc) || "ChuyenKhoan".equalsIgnoreCase(phuongThuc)) {
                if (isMultiShop) {
                    String orderCodes = createdOrders.stream().map(d -> "#TB-" + d.getMaDH()).collect(Collectors.joining(", "));
                    redirectAttributes.addFlashAttribute("successMessage",
                            "Đặt hàng thành công " + createdOrders.size() + " kiện hàng từ các gian hàng: " + orderCodes + ". Vui lòng quét mã VietQR để thanh toán từng đơn.");
                    return "redirect:/don-hang";
                } else {
                    redirectAttributes.addFlashAttribute("successMessage",
                            "Đặt hàng thành công! Vui lòng quét mã VietQR bên dưới để thanh toán đơn hàng #TB-" + primaryOrder.getMaDH());
                    return "redirect:/don-hang/" + primaryOrder.getMaDH();
                }
            }

            if (isMultiShop) {
                String orderCodes = createdOrders.stream().map(d -> "#TB-" + d.getMaDH()).collect(Collectors.joining(", "));
                redirectAttributes.addFlashAttribute("orderSuccess", true);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Đặt hàng thành công! Đơn hàng được tự động phân tách thành " + createdOrders.size() + " kiện hàng theo từng Shop: " + orderCodes);
                return "redirect:/don-hang";
            } else {
                redirectAttributes.addFlashAttribute("orderSuccess", true);
                redirectAttributes.addFlashAttribute("maDH", primaryOrder.getMaDH());
                redirectAttributes.addFlashAttribute("successMessage",
                        "Đặt hàng thành công! Mã đơn hàng: #TB-" + primaryOrder.getMaDH());
                return "redirect:/gio-hang?datHangThanhCong=true&maDH=" + primaryOrder.getMaDH();
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi đặt hàng: " + e.getMessage());
            return "redirect:/gio-hang";
        }
    }

    // ==================== API Kiểm Tra Mã Giảm Giá ====================
    @PostMapping("/api/gio-hang/kiem-tra-voucher")
    @ResponseBody
    public Map<String, Object> apiValidateVoucher(@RequestParam("code") String code,
                                                  @RequestParam(value = "subtotal", defaultValue = "0") Integer subtotal) {
        Map<String, Object> result = new HashMap<>();
        if (code == null || code.trim().isEmpty()) {
            result.put("valid", false);
            result.put("message", "Vui lòng nhập mã giảm giá");
            return result;
        }

        String cleanCode = code.trim().toUpperCase();

        // 1. Kiểm tra trong Database KHUYEN_MAI
        Optional<KhuyenMai> kmOpt = khuyenMaiRepository.findByMaCode(cleanCode);
        if (kmOpt.isPresent()) {
            KhuyenMai km = kmOpt.get();
            if (!"HoatDong".equalsIgnoreCase(km.getTrangThai())) {
                result.put("valid", false);
                result.put("message", "Mã giảm giá đã hết hạn hoặc tạm ngưng sử dụng");
                return result;
            }
            if (km.getNgayBatDau() != null && LocalDateTime.now().isBefore(km.getNgayBatDau())) {
                result.put("valid", false);
                result.put("message", "Mã giảm giá chưa đến ngày áp dụng");
                return result;
            }
            if (km.getNgayKetThuc() != null && LocalDateTime.now().isAfter(km.getNgayKetThuc())) {
                result.put("valid", false);
                result.put("message", "Mã giảm giá đã hết hạn sử dụng");
                return result;
            }
            if (km.getSoLuongToiDa() != null && km.getSoLuongDaDung() != null && km.getSoLuongDaDung() >= km.getSoLuongToiDa()) {
                result.put("valid", false);
                result.put("message", "Mã giảm giá đã hết lượt sử dụng");
                return result;
            }
            if (km.getDonToiThieu() != null && subtotal < km.getDonToiThieu()) {
                result.put("valid", false);
                result.put("message", "Đơn hàng chưa đạt mức tối thiểu " + String.format("%,d đ", km.getDonToiThieu()));
                return result;
            }

            int discount = 0;
            // 2 loại mã giảm giá:
            // 1) Giảm theo sản phẩm (PhanTram hoặc TienCoDinh - chỉ tính trên subtotal sản phẩm)
            // 2) Giảm tiền vận chuyển (Freeship)
            if ("Freeship".equalsIgnoreCase(km.getLoaiGiam()) || km.isShippingDiscount()) {
                // Giảm phí vận chuyển: giá trị giảm tối đa theo cấu hình (mặc định 30.000đ)
                discount = km.getGiaTriGiam() != null ? km.getGiaTriGiam() : 30000;
            } else if ("PhanTram".equalsIgnoreCase(km.getLoaiGiam())) {
                discount = (int) Math.round(subtotal * (km.getGiaTriGiam() / 100.0));
                if (km.getGiamToiDa() != null && discount > km.getGiamToiDa()) {
                    discount = km.getGiamToiDa();
                }
                if (discount > subtotal) {
                    discount = subtotal;
                }
            } else if ("TienCoDinh".equalsIgnoreCase(km.getLoaiGiam())) {
                discount = Math.min(subtotal, km.getGiaTriGiam());
            } else {
                discount = Math.min(subtotal, km.getGiaTriGiam() != null ? km.getGiaTriGiam() : 0);
            }

            result.put("valid", true);
            result.put("code", cleanCode);
            result.put("discountAmount", discount);
            result.put("loaiGiam", km.getLoaiGiam());
            result.put("voucherType", km.getVoucherType()); // ORDER_DISCOUNT hoặc SHIPPING_DISCOUNT
            result.put("discountType", km.getDiscountType()); // PERCENTAGE hoặc FIXED_AMOUNT
            result.put("giaTriGiam", km.getGiaTriGiam());
            result.put("giamToiDa", km.getGiamToiDa());
            result.put("maxDiscountValue", km.getGiamToiDa());
            result.put("donToiThieu", km.getDonToiThieu());
            result.put("minOrderValue", km.getDonToiThieu());
            result.put("tenKM", km.getTenKM());
            result.put("message", "Áp dụng thành công mã [" + cleanCode + "] - " + km.getTenKM());
            return result;
        }

        // Chỉ chấp nhận mã được tạo trong trang Quản lý Khuyến Mãi của Admin
        result.put("valid", false);
        result.put("message", "Mã giảm giá không tồn tại hoặc không hợp lệ!");
        return result;
    }

    // ==================== REST API Endpoints (cho AJAX) ====================

    @PostMapping("/api/gio-hang/them")
    @ResponseBody
    public Map<String, Object> apiAddToCart(@RequestParam("maSP") Integer maSP,
                                            @RequestParam(value = "soLuong", defaultValue = "1") Integer soLuong,
                                            Authentication authentication,
                                            jakarta.servlet.http.HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        KhachHang kh = getCurrentKhachHang(authentication);

        if (kh == null) {
            result.put("success", false);
            result.put("requireLogin", true);
            result.put("loginUrl", "/login");
            result.put("message", "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!");
            return result;
        }

        try {
            gioHangService.addToCart(kh, maSP, soLuong);
            int cartCount = gioHangService.getCartItemCount(kh);
            result.put("success", true);
            result.put("message", "Đã thêm sản phẩm vào giỏ hàng!");
            result.put("cartCount", cartCount);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    @PostMapping("/api/gio-hang/cap-nhat")
    @ResponseBody
    public Map<String, Object> apiUpdateCartItem(@RequestParam("maSP") Integer maSP,
                                                  @RequestParam("soLuong") Integer soLuong,
                                                  Authentication authentication,
                                                  jakarta.servlet.http.HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        KhachHang kh = getCurrentKhachHang(authentication);

        if (kh == null) {
            result.put("success", false);
            result.put("requireLogin", true);
            result.put("loginUrl", "/login");
            result.put("message", "Vui lòng đăng nhập để cập nhật giỏ hàng!");
            return result;
        }

        try {
            gioHangService.updateCartItemQuantity(kh, maSP, soLuong);
            int cartCount = gioHangService.getCartItemCount(kh);
            int subtotal = gioHangService.calculateSubtotal(kh);

            result.put("success", true);
            result.put("cartCount", cartCount);
            result.put("subtotal", subtotal);
            result.put("message", "Đã cập nhật giỏ hàng!");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    @PostMapping("/api/gio-hang/xoa")
    @ResponseBody
    public Map<String, Object> apiRemoveFromCart(@RequestParam("maSP") Integer maSP,
                                                  Authentication authentication,
                                                  jakarta.servlet.http.HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        KhachHang kh = getCurrentKhachHang(authentication);

        if (kh == null) {
            result.put("success", false);
            result.put("requireLogin", true);
            result.put("loginUrl", "/login");
            result.put("message", "Vui lòng đăng nhập để thao tác với giỏ hàng!");
            return result;
        }

        try {
            gioHangService.removeFromCart(kh, maSP);
            int cartCount = gioHangService.getCartItemCount(kh);
            int subtotal = gioHangService.calculateSubtotal(kh);

            result.put("success", true);
            result.put("cartCount", cartCount);
            result.put("subtotal", subtotal);
            result.put("message", "Đã xóa sản phẩm khỏi giỏ hàng!");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    @GetMapping("/api/gio-hang/count")
    @ResponseBody
    public Map<String, Object> apiGetCartCount(Authentication authentication,
                                                jakarta.servlet.http.HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        KhachHang kh = getCurrentKhachHang(authentication);

        if (kh != null) {
            result.put("cartCount", gioHangService.getCartItemCount(kh));
        } else {
            result.put("cartCount", 0);
        }

        return result;
    }
}
