package vn.bookstore.the4bookstore.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.bookstore.the4bookstore.entity.*;
import vn.bookstore.the4bookstore.repository.*;
import vn.bookstore.the4bookstore.security.CustomOAuth2User;
import vn.bookstore.the4bookstore.security.CustomOidcUser;
import vn.bookstore.the4bookstore.security.CustomUserDetails;
import vn.bookstore.the4bookstore.service.ShopService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;

@Slf4j
@Controller
@RequestMapping("/vendor/inventory")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDOR', 'ADMIN', 'MANAGER')")
public class VendorInventoryController {

    private final ShopService shopService;
    private final SanPhamRepository sanPhamRepository;
    private final PhieuKiemKeRepository phieuKiemKeRepository;
    private final ChiTietKiemKeRepository chiTietKiemKeRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final vn.bookstore.the4bookstore.service.InventoryService inventoryService;

    private TaiKhoan getCurrentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) return null;
        TaiKhoan tk = null;
        Object p = auth.getPrincipal();
        if (p instanceof CustomUserDetails ud && ud.getTaiKhoan() != null && ud.getTaiKhoan().getMaTaiKhoan() != null) {
            tk = taiKhoanRepository.findById(ud.getTaiKhoan().getMaTaiKhoan()).orElse(ud.getTaiKhoan());
        } else if (p instanceof CustomOAuth2User o && o.getTaiKhoan() != null && o.getTaiKhoan().getMaTaiKhoan() != null) {
            tk = taiKhoanRepository.findById(o.getTaiKhoan().getMaTaiKhoan()).orElse(o.getTaiKhoan());
        } else if (p instanceof CustomOidcUser o && o.getTaiKhoan() != null && o.getTaiKhoan().getMaTaiKhoan() != null) {
            tk = taiKhoanRepository.findById(o.getTaiKhoan().getMaTaiKhoan()).orElse(o.getTaiKhoan());
        } else if (p instanceof CustomOAuth2User o && o.getEmail() != null) {
            tk = taiKhoanRepository.findByEmail(o.getEmail()).orElse(null);
        }
        if (tk == null && auth.getName() != null) {
            tk = taiKhoanRepository.findByEmail(auth.getName())
                    .or(() -> taiKhoanRepository.findByTenDangNhap(auth.getName()))
                    .orElse(null);
        }
        return tk;
    }
    private Shop getCurrentShop(Authentication auth) {
        TaiKhoan user = getCurrentUser(auth);
        if (user == null) return null;
        Optional<Shop> userShop = shopService.findByTaiKhoan(user);
        if (userShop.isPresent()) {
            return userShop.get();
        }
        if ("ADMIN".equalsIgnoreCase(user.getVaiTro()) || "MANAGER".equalsIgnoreCase(user.getVaiTro()) || "QUANLY".equalsIgnoreCase(user.getVaiTro())) {
            return shopService.findById(1).orElse(null);
        }
        return null;
    }

    private String checkShopAccess(Shop shop) {
        if (shop == null) return "redirect:/vendor/register";
        if ("ChoDuyet".equalsIgnoreCase(shop.getTrangThai())) {
            return "redirect:/vendor/pending";
        }
        if ("TuChoi".equalsIgnoreCase(shop.getTrangThai())) {
            return "redirect:/vendor/rejected";
        }
        if ("BiKhoa".equalsIgnoreCase(shop.getTrangThai())) {
            return "redirect:/vendor/blocked";
        }
        if ("DaXoa".equalsIgnoreCase(shop.getTrangThai())) {
            return "redirect:/vendor/register";
        }
        return null;
    }


    // --- 1. DANH SÁCH BIÊN BẢN KIỂM KÊ CỦA VENDOR ---
    @GetMapping
    public String listInventoryAudits(@RequestParam(defaultValue = "0") int page,
                                     Authentication auth,
                                     Model model) {
        Shop shop = getCurrentShop(auth);
        String redirect = checkShopAccess(shop);
        if (redirect != null) return redirect;

        Page<PhieuKiemKe> auditPage = phieuKiemKeRepository.findByShop_MaShopOrderByNgayKiemKeDesc(shop.getMaShop(), PageRequest.of(page, 10));
        long totalProducts = sanPhamRepository.countByShop_MaShop(shop.getMaShop());

        model.addAttribute("shop", shop);
        model.addAttribute("auditPage", auditPage);
        model.addAttribute("audits", auditPage.getContent());
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("currentPage", page);
        return "vendor/inventory_list";
    }

    // --- 2. TRANG LẬP PHIẾU KIỂM KÊ MỚI CỦA SHOP ---
    @GetMapping("/create")
    public String showCreateAuditForm(Authentication auth, Model model) {
        Shop shop = getCurrentShop(auth);
        String redirect = checkShopAccess(shop);
        if (redirect != null) return redirect;

        List<SanPham> products = sanPhamRepository.findByShop_MaShop(shop.getMaShop());

        model.addAttribute("shop", shop);
        model.addAttribute("products", products);
        return "vendor/inventory_form";
    }

    // --- 3. LƯU & CÂN BẰNG TỒN KHO CHO GIAN HÀNG ---
    @PostMapping("/create")
    public String submitAudit(@RequestParam(required = false) String ghiChu,
                              @RequestParam("productIds") List<Integer> productIds,
                              @RequestParam("soLuongThucTes") List<Integer> soLuongThucTes,
                              @RequestParam(value = "lyDos", required = false) List<String> lyDos,
                              Authentication auth,
                              RedirectAttributes ra) {
        Shop shop = getCurrentShop(auth);
        String redirect = checkShopAccess(shop);
        if (redirect != null) return redirect;

        try {
            PhieuKiemKe pkk = inventoryService.createInventoryAudit(shop, ghiChu, productIds, soLuongThucTes, lyDos);
            ra.addFlashAttribute("successMessage", "Lập phiếu kiểm kê #" + pkk.getMaPhieuKiemKe() + " và cân bằng tồn kho thành công!");
            return "redirect:/vendor/inventory";
        } catch (Exception e) {
            log.error("Loi lap phieu kiem ke vendor: ", e);
            ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/vendor/inventory/create";
        }
    }

    // --- 4. XEM CHI TIẾT BIÊN BẢN KIỂM KÊ ---
    @GetMapping("/{id}")
    public String viewAuditDetail(@PathVariable Integer id,
                                  Authentication auth,
                                  Model model) {
        Shop shop = getCurrentShop(auth);
        String redirect = checkShopAccess(shop);
        if (redirect != null) return redirect;

        PhieuKiemKe pkk = phieuKiemKeRepository.findById(id).orElse(null);
        if (pkk == null || pkk.getShop() == null || !pkk.getShop().getMaShop().equals(shop.getMaShop())) {
            return "redirect:/vendor/inventory?error=notFound";
        }

        List<ChiTietKiemKe> chiTiets = chiTietKiemKeRepository.findByPhieuKiemKe(pkk);

        model.addAttribute("shop", shop);
        model.addAttribute("audit", pkk);
        model.addAttribute("chiTiets", chiTiets);
        return "vendor/inventory_detail";
    }
}
