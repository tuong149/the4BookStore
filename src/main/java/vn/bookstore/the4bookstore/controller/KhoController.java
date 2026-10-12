package vn.bookstore.the4bookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Quản lý kho đã được bàn giao hoàn toàn cho Vendor (Nhà bán hàng) tự kiểm kê và quản trị.
 * Admin không còn quản lý kho tập trung, chuyển hướng toàn bộ truy cập /admin/kho về Dashboard.
 */
@Controller
@RequestMapping("/admin/kho")
public class KhoController {

    @GetMapping({"", "/**"})
    public String redirectKho() {
        return "redirect:/admin/dashboard";
    }
}