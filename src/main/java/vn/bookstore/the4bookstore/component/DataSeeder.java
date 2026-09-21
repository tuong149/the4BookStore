package vn.bookstore.the4bookstore.component;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.bookstore.the4bookstore.entity.TaiKhoan;
import vn.bookstore.the4bookstore.repository.TaiKhoanRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(TaiKhoanRepository taiKhoanRepository, PasswordEncoder passwordEncoder) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String defaultPassword = passwordEncoder.encode("123456");

        createAccountIfNotFound("khachhang", "KHACHHANG", defaultPassword);
        createAccountIfNotFound("banhang", "NHANVIENBANHANG", defaultPassword);
        createAccountIfNotFound("thukho", "NHANVIENKHO", defaultPassword);
        createAccountIfNotFound("admin", "ADMIN", defaultPassword);
        createAccountIfNotFound("quanly", "QUANLY", defaultPassword);
    }

    private void createAccountIfNotFound(String username, String role, String password) {
        if (taiKhoanRepository.findByTenDangNhap(username).isEmpty()) {
            TaiKhoan tk = new TaiKhoan();
            tk.setTenDangNhap(username);
            tk.setMatKhauHash(password);
            tk.setVaiTro(role);
            tk.setTrangThai("HoatDong");
            tk.setEmail(username + "@gmail.com");
            taiKhoanRepository.save(tk);
        }
    }
}
