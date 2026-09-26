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

@Controller
public class CartController {

    private final GioHangService gioHangService;
    private final DonHangService donHangService;
    private final KhachHangRepository khachHangRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final SanPhamRepository sanPhamRepository;

    public CartController(GioHangService gioHangService,
                          DonHangService donHangService,
                          KhachHangRepository khachHangRepository,
                          TaiKhoanRepository taiKhoanRepository,
                          SanPhamRepository sanPhamRepository) {
        this.gioHangService = gioHangService;
        this.donHangService = donHangService;
        this.khachHangRepository = khachHangRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.sanPhamRepository = sanPhamRepository;
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
    public String viewCart(Authentication authentication,
                           jakarta.servlet.http.HttpSession session,
                           Model model) {
        KhachHang kh = getCurrentKhachHang(authentication);

        if (kh != null) {
            // Tự động gộp giỏ hàng session nếu trước đó khách thêm giỏ khi chưa đăng nhập
            gioHangService.mergeSessionCartToDb(kh, session);

            List<ChiTietGioHang> cartItems = gioHangService.getCartItems(kh);
            int subtotal = gioHangService.calculateSubtotal(kh);
            int cartCount = gioHangService.getCartItemCount(kh);

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("subtotal", subtotal);
            model.addAttribute("cartCount", cartCount);
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("khachHang", kh);
        } else {
            List<ChiTietGioHang> cartItems = gioHangService.getSessionCartItems(session);
            int subtotal = gioHangService.calculateSessionSubtotal(session);
            int cartCount = gioHangService.getSessionCartItemCount(session);

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("subtotal", subtotal);
            model.addAttribute("cartCount", cartCount);
            model.addAttribute("isAuthenticated", false);
        }

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

        try {
            if (kh != null) {
                gioHangService.addToCart(kh, maSP, soLuong);
            } else {
                gioHangService.addToSessionCart(session, maSP, soLuong);
            }
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

        try {
            if (kh != null) {
                gioHangService.updateCartItemQuantity(kh, maSP, soLuong);
            } else {
                gioHangService.updateSessionCartQuantity(session, maSP, soLuong);
            }
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

        try {
            if (kh != null) {
                gioHangService.removeFromCart(kh, maSP);
            } else {
                gioHangService.removeFromSessionCart(session, maSP);
            }
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

        if (kh != null) {
            gioHangService.clearCart(kh);
        } else {
            gioHangService.clearSessionCart(session);
        }
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa tất cả sản phẩm trong giỏ hàng!");

        return "redirect:/gio-hang";
    }

    // ==================== Đặt hàng (Checkout) ====================

    @PostMapping("/gio-hang/dat-hang")
    public String placeOrder(@RequestParam("diaChiGiao") String diaChiGiao,
                             @RequestParam("soDienThoaiGiao") String soDienThoaiGiao,
                             @RequestParam(value = "phuongThuc", defaultValue = "COD") String phuongThuc,
                             @RequestParam(value = "ghiChu", required = false) String ghiChu,
                             @RequestParam(value = "selectedProductIds", required = false) List<Integer> selectedProductIds,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        KhachHang kh = getCurrentKhachHang(authentication);
        if (kh == null) {
            return "redirect:/login";
        }

        try {
            DonHang donHang = donHangService.createOrder(kh, diaChiGiao, soDienThoaiGiao, phuongThuc, ghiChu, selectedProductIds);
            redirectAttributes.addFlashAttribute("orderSuccess", true);
            redirectAttributes.addFlashAttribute("maDH", donHang.getMaDH());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đặt hàng thành công! Mã đơn hàng: #TB-" + donHang.getMaDH());
            return "redirect:/gio-hang?datHangThanhCong=true&maDH=" + donHang.getMaDH();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi đặt hàng: " + e.getMessage());
            return "redirect:/gio-hang";
        }
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

        try {
            int cartCount;
            if (kh != null) {
                gioHangService.addToCart(kh, maSP, soLuong);
                cartCount = gioHangService.getCartItemCount(kh);
            } else {
                gioHangService.addToSessionCart(session, maSP, soLuong);
                cartCount = gioHangService.getSessionCartItemCount(session);
            }
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

        try {
            int cartCount;
            int subtotal;
            if (kh != null) {
                gioHangService.updateCartItemQuantity(kh, maSP, soLuong);
                cartCount = gioHangService.getCartItemCount(kh);
                subtotal = gioHangService.calculateSubtotal(kh);
            } else {
                gioHangService.updateSessionCartQuantity(session, maSP, soLuong);
                cartCount = gioHangService.getSessionCartItemCount(session);
                subtotal = gioHangService.calculateSessionSubtotal(session);
            }
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

        try {
            int cartCount;
            int subtotal;
            if (kh != null) {
                gioHangService.removeFromCart(kh, maSP);
                cartCount = gioHangService.getCartItemCount(kh);
                subtotal = gioHangService.calculateSubtotal(kh);
            } else {
                gioHangService.removeFromSessionCart(session, maSP);
                cartCount = gioHangService.getSessionCartItemCount(session);
                subtotal = gioHangService.calculateSessionSubtotal(session);
            }
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
            result.put("cartCount", gioHangService.getSessionCartItemCount(session));
        }

        return result;
    }
}
