package vn.bookstore.the4bookstore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.bookstore.the4bookstore.entity.KhachHang;
import vn.bookstore.the4bookstore.entity.SanPham;
import vn.bookstore.the4bookstore.service.CloudinaryService;
import vn.bookstore.the4bookstore.service.CurrentUserService;
import vn.bookstore.the4bookstore.service.UserInteractionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class UserInteractionController {

    private final UserInteractionService interactionService;
    private final CurrentUserService currentUserService;

    @Autowired(required = false)
    private CloudinaryService cloudinaryService;

    public UserInteractionController(UserInteractionService interactionService, CurrentUserService currentUserService) {
        this.interactionService = interactionService;
        this.currentUserService = currentUserService;
    }

    // --- DANH SÁCH YÊU THÍCH (WISHLIST) ---
    @GetMapping("/profile/favorites")
    public String wishlistPage(Authentication auth, Model model) {
        KhachHang kh = currentUserService.getCurrentKhachHang(auth);
        if (kh == null) return "redirect:/login";

        List<SanPham> favorites = interactionService.getFavorites(kh);
        model.addAttribute("khachHang", kh);
        model.addAttribute("favorites", favorites);
        return "profile/favorites";
    }

    @PostMapping("/api/favorites/toggle/{maSP}")
    @ResponseBody
    public ResponseEntity<?> toggleFavorite(Authentication auth, @PathVariable Integer maSP) {
        KhachHang kh = currentUserService.getCurrentKhachHang(auth);
        if (kh == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Vui lòng đăng nhập để yêu thích sản phẩm"));
        }
        boolean isFav = interactionService.toggleFavorite(kh, maSP);
        return ResponseEntity.ok(Map.of("success", true, "isFavorite", isFav));
    }

    // --- SẢN PHẨM ĐÃ XEM ---
    @GetMapping("/profile/recently-viewed")
    public String recentlyViewedPage(Authentication auth, Model model) {
        KhachHang kh = currentUserService.getCurrentKhachHang(auth);
        if (kh == null) return "redirect:/login";

        List<SanPham> recentlyViewed = interactionService.getRecentlyViewed(kh);
        model.addAttribute("khachHang", kh);
        model.addAttribute("recentlyViewed", recentlyViewed);
        return "profile/recently_viewed";
    }

    // --- ĐÁNH GIÁ SẢN PHẨM & TẢI ẢNH LÊN CLOUDINARY ---
    @PostMapping("/api/danh-gia/submit")
    public String submitReview(Authentication auth,
                               @RequestParam Integer maSP,
                               @RequestParam Integer maDH,
                               @RequestParam int soSao,
                               @RequestParam String noiDung,
                               @RequestParam(value = "files", required = false) MultipartFile[] files,
                               @RequestParam(value = "mediaUrl", required = false) String mediaUrl,
                               RedirectAttributes redirectAttributes) {
        KhachHang kh = currentUserService.getCurrentKhachHang(auth);
        if (kh == null) return "redirect:/login";

        try {
            List<String> mediaUrls = new ArrayList<>();

            // 1. Tải ảnh từ máy tính lên Cloudinary (có fallback local)
            if (files != null && files.length > 0 && cloudinaryService != null) {
                for (MultipartFile file : files) {
                    if (file != null && !file.isEmpty()) {
                        String uploadedUrl = cloudinaryService.uploadImage(file, "reviews");
                        if (uploadedUrl != null && !uploadedUrl.isBlank()) {
                            mediaUrls.add(uploadedUrl);
                        }
                    }
                }
            }

            // 2. Nhận thêm liên kết ngoài nếu người dùng có nhập
            if (mediaUrl != null && !mediaUrl.isBlank()) {
                String[] parts = mediaUrl.split(",");
                for (String p : parts) {
                    String trimmed = p.trim();
                    if (!trimmed.isEmpty() && !mediaUrls.contains(trimmed)) {
                        mediaUrls.add(trimmed);
                    }
                }
            }

            interactionService.submitReview(kh, maSP, maDH, soSao, noiDung, mediaUrls);
            redirectAttributes.addFlashAttribute("successMessage", "Cảm ơn bạn đã gửi đánh giá chi tiết cho sản phẩm!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/san-pham/" + maSP + "#reviews-section";
    }
}
