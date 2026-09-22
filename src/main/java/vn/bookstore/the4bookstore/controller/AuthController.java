package vn.bookstore.the4bookstore.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.bookstore.the4bookstore.dto.RegisterRequest;
import vn.bookstore.the4bookstore.entity.KhachHang;
import vn.bookstore.the4bookstore.entity.TaiKhoan;
import vn.bookstore.the4bookstore.repository.KhachHangRepository;
import vn.bookstore.the4bookstore.repository.TaiKhoanRepository;

@Controller
public class AuthController {

    private final TaiKhoanRepository taiKhoanRepository;
    private final KhachHangRepository khachHangRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(TaiKhoanRepository taiKhoanRepository, KhachHangRepository khachHangRepository, PasswordEncoder passwordEncoder) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.khachHangRepository = khachHangRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("registerRequest") RegisterRequest request, Model model) {
        if (taiKhoanRepository.findByTenDangNhap(request.getTenDangNhap()).isPresent()) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "auth/register";
        }
        if (taiKhoanRepository.findByEmail(request.getEmail()).isPresent()) {
            model.addAttribute("error", "Email đã được sử dụng!");
            return "auth/register";
        }
        if (khachHangRepository.findBySoDienThoai(request.getSoDienThoai()).isPresent()) {
            model.addAttribute("error", "Số điện thoại đã được sử dụng!");
            return "auth/register";
        }

        // Tạo tài khoản
        TaiKhoan tk = new TaiKhoan();
        tk.setTenDangNhap(request.getTenDangNhap());
        tk.setEmail(request.getEmail());
        tk.setMatKhauHash(passwordEncoder.encode(request.getMatKhau()));
        tk.setVaiTro("KHACHHANG");
        tk.setTrangThai("HoatDong");
        taiKhoanRepository.save(tk);

        // Tạo hồ sơ khách hàng
        KhachHang kh = new KhachHang();
        kh.setHoTen(request.getHoTen());
        kh.setSoDienThoai(request.getSoDienThoai());
        kh.setEmail(request.getEmail());
        kh.setDiaChi(request.getDiaChi());
        kh.setTaiKhoan(tk);
        khachHangRepository.save(kh);

        return "redirect:/login?registered=true";
    }
}
