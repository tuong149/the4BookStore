package vn.bookstore.the4bookstore.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.bookstore.the4bookstore.entity.SanPham;
import vn.bookstore.the4bookstore.repository.SanPhamRepository;

import java.util.List;

@Controller
public class HomeController {

    private final SanPhamRepository sanPhamRepository;

    public HomeController(SanPhamRepository sanPhamRepository) {
        this.sanPhamRepository = sanPhamRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            model.addAttribute("username", auth.getName());
        }

        // Fetch first 8 books for featured section
        List<SanPham> featuredBooks = sanPhamRepository.findAll();
        if (featuredBooks.size() > 8) {
            featuredBooks = featuredBooks.subList(0, 8);
        }
        model.addAttribute("featuredBooks", featuredBooks);

        return "home/index";
    }
}
