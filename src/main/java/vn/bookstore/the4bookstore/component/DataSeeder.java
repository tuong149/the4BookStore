package vn.bookstore.the4bookstore.component;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.bookstore.the4bookstore.entity.*;
import vn.bookstore.the4bookstore.repository.*;

import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private final TaiKhoanRepository taiKhoanRepository;
    private final KhachHangRepository khachHangRepository;
    private final DanhMucRepository danhMucRepository;
    private final NhaXuatBanRepository nhaXuatBanRepository;
    private final NhaCungCapRepository nhaCungCapRepository;
    private final SanPhamRepository sanPhamRepository;
    private final PasswordEncoder passwordEncoder;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @org.springframework.beans.factory.annotation.Value("${spring.mail.username:}")
    private String mailUsername;

    public DataSeeder(TaiKhoanRepository taiKhoanRepository, 
                      KhachHangRepository khachHangRepository,
                      DanhMucRepository danhMucRepository,
                      NhaXuatBanRepository nhaXuatBanRepository,
                      NhaCungCapRepository nhaCungCapRepository,
                      SanPhamRepository sanPhamRepository,
                      PasswordEncoder passwordEncoder,
                      @org.springframework.beans.factory.annotation.Autowired(required = false) org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.khachHangRepository = khachHangRepository;
        this.danhMucRepository = danhMucRepository;
        this.nhaXuatBanRepository = nhaXuatBanRepository;
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        if (jdbcTemplate != null) {
            try {
                jdbcTemplate.execute("ALTER TABLE khach_hang MODIFY so_dien_thoai VARCHAR(20) NULL");
            } catch (Exception ignored) {}
        }

        String defaultPassword = passwordEncoder.encode("123456");

        createAccountIfNotFound("khachhang", "KHACHHANG", defaultPassword);
        createAccountIfNotFound("banhang", "NHANVIENBANHANG", defaultPassword);
        createAccountIfNotFound("thukho", "NHANVIENKHO", defaultPassword);
        createAccountIfNotFound("admin", "ADMIN", defaultPassword);
        createAccountIfNotFound("quanly", "QUANLY", defaultPassword);

        // Khởi tạo tài khoản kiểm thử cho email SMTP nếu được cấu hình trong .env
        String configuredEmail = (mailUsername != null && !mailUsername.isBlank()) ? mailUsername.trim() : System.getenv("MAIL_USERNAME");
        if (configuredEmail != null && !configuredEmail.isBlank() && taiKhoanRepository.findByEmail(configuredEmail.trim()).isEmpty()) {
            TaiKhoan tkMail = new TaiKhoan();
            tkMail.setTenDangNhap("the4bookstore_user");
            tkMail.setEmail(configuredEmail.trim());
            tkMail.setMatKhauHash(defaultPassword);
            tkMail.setVaiTro("KHACHHANG");
            tkMail.setTrangThai("HoatDong");
            tkMail.setAuthProvider("LOCAL");
            taiKhoanRepository.save(tkMail);
        }

        seedBookstoreData();
    }

    private void createAccountIfNotFound(String username, String role, String password) {
        if (taiKhoanRepository.findByTenDangNhap(username).isEmpty()) {
            TaiKhoan tk = new TaiKhoan();
            tk.setTenDangNhap(username);
            tk.setMatKhauHash(password);
            tk.setVaiTro(role);
            tk.setTrangThai("HoatDong");
            tk.setEmail(username + "@gmail.com");
            tk = taiKhoanRepository.save(tk);

            if ("KHACHHANG".equals(role)) {
                KhachHang kh = new KhachHang();
                kh.setHoTen("Khách hàng mặc định");
                kh.setSoDienThoai("0999999999");
                kh.setEmail(tk.getEmail());
                kh.setDiaChi("TP.HCM");
                kh.setTaiKhoan(tk);
                khachHangRepository.save(kh);
            }
        }
    }

    private void seedBookstoreData() {
        if (sanPhamRepository.count() > 0) {
            return;
        }

        // Seed Categories
        DanhMuc catFiction = createCategoryIfNotFound("Tiểu Thuyết & Văn Học", "Tác phẩm tiểu thuyết trong và ngoài nước");
        DanhMuc catSelfHelp = createCategoryIfNotFound("Phát Triển Bản Thân", "Kỹ năng sống, tư duy và phát triển cá nhân");
        DanhMuc catEcon = createCategoryIfNotFound("Kinh Tế & Tài Chính", "Kinh doanh, đầu tư và tâm lý tài chính");
        DanhMuc catHistory = createCategoryIfNotFound("Khoa Học & Lịch Sử", "Lịch sử nhân loại, vũ trụ và khoa học thường thức");
        DanhMuc catSciFi = createCategoryIfNotFound("Khoa Học Viễn Tưởng", "Tiểu thuyết viễn tưởng kinh điển và hiện đại");

        // Seed Publisher & Supplier
        NhaXuatBan nxb = new NhaXuatBan();
        nxb.setTenNXB("Nhà Xuất Bản Hội Nhà Văn");
        nxb.setDiaChi("Hà Nội");
        nxb.setSoDienThoai("0243822222");
        nxb.setEmail("nxbhnv@gmail.com");
        nxb = nhaXuatBanRepository.save(nxb);

        NhaCungCap ncc = new NhaCungCap();
        ncc.setTenNCC("Công ty Sách Phương Nam");
        ncc.setDiaChi("TP.HCM");
        ncc.setSoDienThoai("0283833333");
        ncc.setEmail("contact@phuongnam.com");
        ncc.setTrangThai("HoatDong");
        ncc = nhaCungCapRepository.save(ncc);

        // Seed Books
        createBook("The Midnight Library", "978-0525559474", 189000, 25, catFiction, nxb, ncc,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuAJxhxEUoGYZXYaOldMaT7d3dcCRrYvCD-3AWG8q4nOBMKeBIhtPi1dKb9NiDZHTXWMxMULBn3rsm5dU8_IVOjwK8Bnkg8xf-IGsBbsQoLtc5D6EWqcU1KfQ1LzI9jMW8JHZBBy9sHF_7t1onS8pagaubUOfOqYifnPkQMRgKeMgWgWQ71XMqmHdC9fJzuMiB3aqbLZt8bE5qj4HVtt62KzVUKztkjut685dS6dOCMbTJ_WPnz-9N7y");

        createBook("Atomic Habits - Thay Đổi Tí Hon", "978-0735211292", 220000, 0, catSelfHelp, nxb, ncc,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuC0ZepDd_foKXu3i6ER1m8WWEdur0sxuw_PzY9MNfI4REnQ3rf6R_2Cm9EnfFAhXCPxQTTtVl4s2CrHSmtbVz08lHsAHDSiErKboS8Y4p5NQiHrun0Ek56kgIt1soK3hBbriVWEntfW_SHfXzkp7nmY2OyfVyfhAuJ50SpGyqZseN_277pw-RUdZgMOpRkN3ksmBAnBDCRnpGWTd3UWhZiP2GhZTpYd0mZ4A0F2WZ20z_Y2aCKwxl9w");

        createBook("Tâm Lý Học Về Tiền", "978-0857197689", 150000, 3, catEcon, nxb, ncc,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuAUSZ5dqoRMUTssXIap1lFtbGqNj76EO4_JpeLICxgK6i8RQ-b6FBevsmTpBNYLqOGOhEJmM72f7BeRd_P6wYVJ0WrTJJCFwjmrX7y4IYbT9cGZhUYpfZ41NtYpK8wDRUwUU5JolPlF4JYof0nf-Mnjr2hBZFwxagtPhNGSVfaR65hlzsL-oDdP7Q53Ww7C4vzv_B90IFfI-_FjyHy7V4wP5GHZfTpmShIiBp4oEL19xwu0tRaTGEgW");

        createBook("Sapiens: Lược Sử Loài Người", "978-0062316097", 265000, 38, catHistory, nxb, ncc,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuCG70bn_Qqg9AKCp8J9XwZoQ0H8otME2a1VxaIQ9mbLAQk60DgJssT5k7D_fsGf6ttO0j-TROb1NzE4QR1_sbRLD856RV8UIFgynUd7SujhVFdhLBJkAPHxPynAmPFKFoKuLSY2tyJX9T_1SDen13Mk4MN2urDBWhtknhDsTK8-O2oLxA8yjhXNZy0YUWK2U5gHXvRhAIj2d-YivtCOD_hFsb8Qu8Z00zBpmbfcRC4at7Dd35UbsRB0");

        createBook("Cây Cam Ngọt Của Tôi", "978-6047781234", 108000, 45, catFiction, nxb, ncc,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuAJxhxEUoGYZXYaOldMaT7d3dcCRrYvCD-3AWG8q4nOBMKeBIhtPi1dKb9NiDZHTXWMxMULBn3rsm5dU8_IVOjwK8Bnkg8xf-IGsBbsQoLtc5D6EWqcU1KfQ1LzI9jMW8JHZBBy9sHF_7t1onS8pagaubUOfOqYifnPkQMRgKeMgWgWQ71XMqmHdC9fJzuMiB3aqbLZt8bE5qj4HVtt62KzVUKztkjut685dS6dOCMbTJ_WPnz-9N7y");

        createBook("Dune - Xứ Cát", "978-0441013593", 245000, 18, catSciFi, nxb, ncc,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuCG70bn_Qqg9AKCp8J9XwZoQ0H8otME2a1VxaIQ9mbLAQk60DgJssT5k7D_fsGf6ttO0j-TROb1NzE4QR1_sbRLD856RV8UIFgynUd7SujhVFdhLBJkAPHxPynAmPFKFoKuLSY2tyJX9T_1SDen13Mk4MN2urDBWhtknhDsTK8-O2oLxA8yjhXNZy0YUWK2U5gHXvRhAIj2d-YivtCOD_hFsb8Qu8Z00zBpmbfcRC4at7Dd35UbsRB0");
    }

    private DanhMuc createCategoryIfNotFound(String name, String desc) {
        return danhMucRepository.findAll().stream()
                .filter(d -> d.getTenDanhMuc().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    DanhMuc dm = new DanhMuc();
                    dm.setTenDanhMuc(name);
                    dm.setMoTa(desc);
                    dm.setTrangThai(true);
                    return danhMucRepository.save(dm);
                });
    }

    private void createBook(String title, String isbn, int price, int stock, DanhMuc category, NhaXuatBan nxb, NhaCungCap ncc, String coverUrl) {
        SanPham sp = new SanPham();
        sp.setTenSP(title);
        sp.setISBN(isbn);
        sp.setLoaiSP("Sach");
        sp.setGiaBan(price);
        sp.setSoLuongTon(stock);
        sp.setMucTonToiThieu(5);
        sp.setDanhMuc(category);
        sp.setNhaXuatBan(nxb);
        sp.setNhaCungCap(ncc);
        sp.setMoTa(coverUrl);
        sp.setTrangThai(stock > 0 ? "DangBan" : "HetHang");
        sp.setNgayTao(LocalDateTime.now());
        sanPhamRepository.save(sp);
    }
}
