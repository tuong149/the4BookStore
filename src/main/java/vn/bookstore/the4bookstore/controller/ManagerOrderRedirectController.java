package vn.bookstore.the4bookstore.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'QUANLY')")
public class ManagerOrderRedirectController {

    @GetMapping("/manager/orders")
    public String redirectManagerOrders(HttpServletRequest request) {
        String query = request.getQueryString();
        return "redirect:/admin/orders" + (query != null && !query.isBlank() ? "?" + query : "");
    }

    @GetMapping("/manager/orders/{id}")
    public String redirectManagerOrderDetail(@PathVariable Integer id) {
        return "redirect:/admin/orders/" + id;
    }
}
