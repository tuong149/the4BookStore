package vn.bookstore.the4bookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Quản lý tồn kho sản phẩm thuộc trách nhiệm của Vendor (Nhà bán hàng).
 * Admin không còn quản lý kho tập trung, chuyển hướng toàn bộ truy cập /admin/kho về Dashboard.
 */
@Controller
@RequestMapping("/admin/kho")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'QUANLY')")
public class KhoController {

    @GetMapping({"", "/**"})
    public String redirectKho() {
        return "redirect:/admin/dashboard";
    }
}