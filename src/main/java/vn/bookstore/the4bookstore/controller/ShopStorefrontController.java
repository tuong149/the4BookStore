package vn.bookstore.the4bookstore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.bookstore.the4bookstore.entity.KhachHang;
import vn.bookstore.the4bookstore.entity.KhuyenMai;
import vn.bookstore.the4bookstore.entity.SanPham;
import vn.bookstore.the4bookstore.entity.Shop;
import vn.bookstore.the4bookstore.entity.TaiKhoan;
import vn.bookstore.the4bookstore.repository.KhachHangRepository;
import vn.bookstore.the4bookstore.repository.KhuyenMaiRepository;
import vn.bookstore.the4bookstore.repository.SanPhamRepository;
import vn.bookstore.the4bookstore.repository.TaiKhoanRepository;
import vn.bookstore.the4bookstore.repository.VoucherDaLuuRepository;
import vn.bookstore.the4bookstore.security.CustomUserDetails;
import vn.bookstore.the4bookstore.service.ShopService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopStorefrontController {

    private final ShopService shopService;
    private final SanPhamRepository sanPhamRepository;
    private final KhuyenMaiRepository khuyenMaiRepository;
    private final VoucherDaLuuRepository voucherDaLuuRepository;
    private final KhachHangRepository khachHangRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    private KhachHang getCurrentKhachHang(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        Object principal = auth.getPrincipal();
        TaiKhoan tk = null;
        if (principal instanceof CustomUserDetails ud) {
            tk = ud.getTaiKhoan();
        } else {
            String name = auth.getName();
            tk = taiKhoanRepository.findByEmail(name).or(() -> taiKhoanRepository.findByTenDangNhap(name)).orElse(null);
        }
        if (tk == null) return null;
        return khachHangRepository.findByTaiKhoan(tk).orElse(null);
    }

    @GetMapping("/{slug}")
    public String shopPage(@PathVariable String slug,
                           @RequestParam(required = false) String q,
                           @RequestParam(defaultValue = "0") int page,
                           Authentication auth,
                           Model model) {
        Shop shop = shopService.findBySlug(slug)
                .orElseGet(() -> {
                    try {
                        return shopService.findById(Integer.parseInt(slug)).orElse(null);
                    } catch (Exception e) {
                        return null;
                    }
                });

        if (shop == null) {
            return "redirect:/?shopNotFound=true";
        }

        if (!"HoatDong".equalsIgnoreCase(shop.getTrangThai())) {
            return "redirect:/?shopPending=true";
        }

        Page<SanPham> bookPage;
        if (q != null && !q.isBlank()) {
            bookPage = sanPhamRepository.findActiveBooksByShopAndKeyword(shop.getMaShop(), q.trim(), PageRequest.of(page, 12));
        } else {
            bookPage = sanPhamRepository.findActiveBooksByShop(shop.getMaShop(), PageRequest.of(page, 12));
        }

        // Chỉ hiển thị voucher còn hạn và còn số lượng (chưa vượt quá max usage hoặc 100)
        List<KhuyenMai> vouchers = khuyenMaiRepository.findByShop_MaShopOrderByMaKMDesc(shop.getMaShop()).stream()
                .filter(km -> "HoatDong".equalsIgnoreCase(km.getTrangThai()))
                .filter(km -> Boolean.TRUE.equals(km.getHienThiCongKhai()))
                .filter(KhuyenMai::isDangDienRa)
                .toList();

        Set<Integer> savedIds = new HashSet<>();
        KhachHang kh = getCurrentKhachHang(auth);
        if (kh != null) {
            savedIds.addAll(voucherDaLuuRepository.findSavedVoucherIdsByKhachHang(kh));
        }

        model.addAttribute("shop", shop);
        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("page", bookPage);
        model.addAttribute("vouchers", vouchers);
        model.addAttribute("savedIds", savedIds);
        model.addAttribute("loggedIn", kh != null);
        model.addAttribute("keyword", q != null ? q : "");
        return "shop/index";
    }
}
