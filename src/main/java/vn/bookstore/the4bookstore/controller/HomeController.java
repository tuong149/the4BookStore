package vn.bookstore.the4bookstore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.bookstore.the4bookstore.entity.DanhMuc;
import vn.bookstore.the4bookstore.service.DanhMucService;
import vn.bookstore.the4bookstore.service.SanPhamService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final SanPhamService sanPhamService;
    private final DanhMucService danhMucService;

    @GetMapping("/")
    public String home(
            @RequestParam(name = "catPage", required = false) Integer catPage,
            @RequestParam(name = "page", required = false) Integer page,
            Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            model.addAttribute("username", auth.getName());
        }

        int requestedPage = (catPage != null) ? catPage : (page != null ? page : 0);
        if (requestedPage < 0) requestedPage = 0;

        int pageSize = 10;
        List<DanhMuc> allActive = danhMucService.getAllActive();
        Page<DanhMuc> categoryPage = danhMucService.getAllActivePaged(requestedPage, pageSize);

        // Section: Sách Bán Chạy
        model.addAttribute("featuredBooks", sanPhamService.getFeaturedBooks());

        // Section: Tất Cả Sản Phẩm (Previews & Subcategories)
        model.addAttribute("sachPreview", sanPhamService.getPreviewByLoaiSP("Sach"));
        model.addAttribute("vppPreview", sanPhamService.getPreviewByLoaiSP("VanPhongPham"));
        model.addAttribute("quaTangPreview", sanPhamService.getPreviewByLoaiSP("QuaTang"));
        model.addAttribute("categoriesSach", sanPhamService.getCategoriesByLoaiSP("Sach"));
        model.addAttribute("categoriesVpp", sanPhamService.getCategoriesByLoaiSP("VanPhongPham"));
        model.addAttribute("categoriesQuaTang", sanPhamService.getCategoriesByLoaiSP("QuaTang"));

        // Section: Các Danh Mục (ở cuối, 10 mục / trang)
        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("allCategories", allActive);
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("catCurrentPage", categoryPage.getNumber());
        model.addAttribute("catTotalPages", categoryPage.getTotalPages());
        model.addAttribute("catTotalElements", categoryPage.getTotalElements());

        return "home/index";
    }
}
