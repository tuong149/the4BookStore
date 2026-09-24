package vn.bookstore.the4bookstore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.bookstore.the4bookstore.service.DanhMucService;
import vn.bookstore.the4bookstore.service.SanPhamService;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final SanPhamService sanPhamService;
    private final DanhMucService danhMucService;

    @GetMapping("/")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            model.addAttribute("username", auth.getName());
        }
        model.addAttribute("featuredBooks", sanPhamService.getFeaturedBooks());
        model.addAttribute("categories", sanPhamService.getCategoriesByLoaiSP("Sach"));
        return "home/index";
    }
}
