package vn.bookstore.the4bookstore.component;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.bookstore.the4bookstore.entity.*;
import vn.bookstore.the4bookstore.repository.*;

import java.time.LocalDateTime;

@Component
@ConditionalOnProperty(name = "app.seeder.enabled", havingValue = "true", matchIfMissing = false)
public class DataSeeder implements CommandLineRunner {

    private final TaiKhoanRepository taiKhoanRepository;
    private final KhachHangRepository khachHangRepository;
    private final DanhMucRepository danhMucRepository;
    private final SanPhamRepository sanPhamRepository;
    private final TacGiaRepository tacGiaRepository;
    private final SanPhamTacGiaRepository sanPhamTacGiaRepository;
    private final PasswordEncoder passwordEncoder;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @org.springframework.beans.factory.annotation.Value("${spring.mail.username:}")
    private String mailUsername;

    public DataSeeder(TaiKhoanRepository taiKhoanRepository, 
                      KhachHangRepository khachHangRepository,
                      DanhMucRepository danhMucRepository,
                      SanPhamRepository sanPhamRepository,
                      TacGiaRepository tacGiaRepository,
                      SanPhamTacGiaRepository sanPhamTacGiaRepository,
                      PasswordEncoder passwordEncoder,
                      @org.springframework.beans.factory.annotation.Autowired(required = false) org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.khachHangRepository = khachHangRepository;
        this.danhMucRepository = danhMucRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.tacGiaRepository = tacGiaRepository;
        this.sanPhamTacGiaRepository = sanPhamTacGiaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // Tối ưu tốc độ khởi động: Nếu cơ sở dữ liệu đã có dữ liệu thì bỏ qua seeder,
        // tránh gửi hàng chục câu query kiểm tra qua mạng internet làm chậm server boot.
        if (sanPhamRepository.count() > 0 && taiKhoanRepository.count() > 0) {
            return;
        }

        if (jdbcTemplate != null) {
            try {
                jdbcTemplate.execute("ALTER TABLE khach_hang MODIFY so_dien_thoai VARCHAR(20) NULL");
            } catch (Exception ignored) {}
        }

        String defaultPassword = passwordEncoder.encode("Abc@123");

        // 1. Tạo tài khoản mẫu nếu chưa tồn tại trong cơ sở dữ liệu
        createAccountIfNotFound("admin", "ADMIN", defaultPassword, "admin@the4bookstore.vn");
        createAccountIfNotFound("manager", "MANAGER", defaultPassword, "manager@the4bookstore.vn");
        createAccountIfNotFound("user", "USER", defaultPassword, "user@the4bookstore.vn");
        for (String vendorUser : java.util.List.of("vendor_demo", "vendor_fahasa", "vendor_hoasen")) {
            createAccountIfNotFound(vendorUser, "VENDOR", defaultPassword, vendorUser + "@the4bookstore.vn");
        }

        seedBookstoreData();
        seedAuthorsAndLinks();
        seedStationeryAndGifts();
    }

    private void createAccountIfNotFound(String username, String role, String password, String email) {
        if (taiKhoanRepository.findByTenDangNhap(username).isEmpty()) {
            TaiKhoan tk = new TaiKhoan();
            tk.setTenDangNhap(username);
            tk.setMatKhauHash(password);
            tk.setVaiTro(role);
            tk.setTrangThai("HoatDong");
            tk.setEmail(email != null ? email : username + "@the4bookstore.vn");
            tk = taiKhoanRepository.save(tk);

            if ("USER".equals(role) || "KHACHHANG".equals(role)) {
                if (khachHangRepository.findByTaiKhoan(tk).isEmpty()) {
                    KhachHang kh = new KhachHang();
                    kh.setHoTen("Người Dùng Test");
                    kh.setSoDienThoai(null);
                    kh.setEmail(tk.getEmail());
                    kh.setDiaChi(null);
                    kh.setTaiKhoan(tk);
                    khachHangRepository.save(kh);
                }
            }
        }
    }

    private void seedBookstoreData() {
        if (sanPhamRepository.count() > 0) {
            return;
        }

        // Seed Categories
        DanhMuc catFiction = createCategoryIfNotFound("Tiểu Thuyết & Văn Học", "Tác phẩm tiểu thuyết trong và ngoài nước", "Sach");
        DanhMuc catSelfHelp = createCategoryIfNotFound("Phát Triển Bản Thân", "Kỹ năng sống, tư duy và phát triển cá nhân", "Sach");
        DanhMuc catEcon = createCategoryIfNotFound("Kinh Tế & Tài Chính", "Kinh doanh, đầu tư và tâm lý tài chính", "Sach");
        DanhMuc catHistory = createCategoryIfNotFound("Khoa Học & Lịch Sử", "Lịch sử nhân loại, vũ trụ và khoa học thường thức", "Sach");
        DanhMuc catSciFi = createCategoryIfNotFound("Khoa Học Viễn Tưởng", "Tiểu thuyết viễn tưởng kinh điển và hiện đại", "Sach");

        // Seed Books
        createBook("The Midnight Library", "978-0525559474", 189000, 25, catFiction,
                "https://res.cloudinary.com/vkqtqiha/image/upload/v1791217232/the4bookstore/books/wqfgfnpkibjhi1vheb1n.png");

        createBook("Atomic Habits - Thay Đổi Tí Hon", "978-0735211292", 220000, 0, catSelfHelp,
                "https://res.cloudinary.com/vkqtqiha/image/upload/v1791217232/the4bookstore/books/wqfgfnpkibjhi1vheb1n.png");

        createBook("Tâm Lý Học Về Tiền", "978-0857197689", 150000, 3, catEcon,
                "https://res.cloudinary.com/vkqtqiha/image/upload/v1791217232/the4bookstore/books/wqfgfnpkibjhi1vheb1n.png");

        createBook("Sapiens: Lược Sử Loài Người", "978-0062316097", 265000, 38, catHistory,
                "https://res.cloudinary.com/vkqtqiha/image/upload/v1791217232/the4bookstore/books/wqfgfnpkibjhi1vheb1n.png");

        createBook("Cây Cam Ngọt Của Tôi", "978-6047781234", 108000, 45, catFiction,
                "https://res.cloudinary.com/vkqtqiha/image/upload/v1791217232/the4bookstore/books/wqfgfnpkibjhi1vheb1n.png");

        createBook("Dune - Xứ Cát", "978-0441013593", 245000, 18, catSciFi,
                "https://res.cloudinary.com/vkqtqiha/image/upload/v1791217232/the4bookstore/books/wqfgfnpkibjhi1vheb1n.png");
    }

    private DanhMuc createCategoryIfNotFound(String name, String desc, String loaiSP) {
        return danhMucRepository.findAll().stream()
                .filter(d -> d.getTenDanhMuc().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    DanhMuc dm = new DanhMuc();
                    dm.setTenDanhMuc(name);
                    dm.setMoTa(desc);
                    dm.setLoaiSP(loaiSP != null ? loaiSP : "Sach");
                    dm.setTrangThai(true);
                    return danhMucRepository.save(dm);
                });
    }

    private void createBook(String title, String isbn, int price, int stock, DanhMuc category, String coverUrl) {
        SanPham sp = new SanPham();
        sp.setTenSP(title);
        sp.setISBN(isbn);
        sp.setLoaiSP("Sach");
        sp.setGiaBan(price);
        sp.setSoLuongTon(stock);
        sp.setMucTonToiThieu(5);
        sp.setDanhMuc(category);
        sp.setMoTa(coverUrl);
        sp.setTrangThai(stock > 0 ? "DangBan" : "HetHang");
        sp.setNgayTao(LocalDateTime.now());
        sanPhamRepository.save(sp);
    }

    private void seedStationeryAndGifts() {
        // Danh mục của Văn phòng phẩm
        DanhMuc catSoTay = createCategoryIfNotFound("Sổ Tay & Vở Viết", "Sổ tay da, sổ ghi chép, sổ kế hoạch", "VanPhongPham");
        DanhMuc catBut = createCategoryIfNotFound("Bút & Dụng Cụ Viết", "Các loại bút viết, bút máy, bút bi cao cấp", "VanPhongPham");
        DanhMuc catDungCu = createCategoryIfNotFound("Dụng Cụ Học Tập & Văn Phòng", "Kẹp sách, đèn đọc sách, thước kẻ và phụ kiện", "VanPhongPham");

        // Danh mục của Quà tặng & Trang trí
        DanhMuc catTuiTote = createCategoryIfNotFound("Túi Tote & Thời Trang Sách", "Túi vải canvas thời trang phong cách mọt sách", "QuaTang");
        DanhMuc catBookNook = createCategoryIfNotFound("Mô Hình Book Nook & Trang Trí", "Mô hình gỗ 3D Book Nook trang trí giá sách", "QuaTang");
        DanhMuc catBoardgame = createCategoryIfNotFound("Boardgame & Hộp Quà Tặng", "Trò chơi boardgame trí tuệ và hộp quà tặng độc đáo", "QuaTang");

        createProductItem("Sổ Tay Bìa Da Vintage Classic", "VPP-001", "VanPhongPham", 85000, 40, catSoTay,
                "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=500&auto=format&fit=crop&q=80");
        createProductItem("Bút Máy Thư Pháp Pilot Kakuno", "VPP-002", "VanPhongPham", 210000, 25, catBut,
                "https://images.unsplash.com/photo-1583485088034-697b5bc54ccd?w=500&auto=format&fit=crop&q=80");
        createProductItem("Bộ Bookmark Kim Loại Mạ Vàng Cổ Điển", "VPP-003", "VanPhongPham", 45000, 60, catDungCu,
                "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=500&auto=format&fit=crop&q=80");
        createProductItem("Đèn Kẹp Đọc Sách Bảo Vệ Mắt LED", "VPP-004", "VanPhongPham", 135000, 15, catDungCu,
                "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=500&auto=format&fit=crop&q=80");

        createProductItem("Túi Canvas The4BookStore Vintage Tote", "QT-001", "QuaTang", 120000, 35, catTuiTote,
                "https://images.unsplash.com/photo-1544816155-12df9643f363?w=500&auto=format&fit=crop&q=80");
        createProductItem("Mô Hình Book Nook Gỗ 3D Hẻm Xéo", "QT-002", "QuaTang", 450000, 10, catBookNook,
                "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=500&auto=format&fit=crop&q=80");
        createProductItem("Bộ Boardgame Catan Bản Tiếng Việt", "QT-003", "QuaTang", 590000, 8, catBoardgame,
                "https://images.unsplash.com/photo-1610890716171-6b1bb98ffd09?w=500&auto=format&fit=crop&q=80");
        createProductItem("Hộp Quà Tặng Người Yêu Sách Reader Box", "QT-004", "QuaTang", 320000, 18, catBoardgame,
                "https://images.unsplash.com/photo-1513519245088-0e12902e5a38?w=500&auto=format&fit=crop&q=80");
    }

    private void createProductItem(String title, String isbn, String loaiSP, int price, int stock, DanhMuc category, String coverUrl) {
        if (sanPhamRepository.findAll().stream().anyMatch(p -> isbn.equalsIgnoreCase(p.getISBN()))) {
            return;
        }
        SanPham sp = new SanPham();
        sp.setTenSP(title);
        sp.setISBN(isbn);
        sp.setLoaiSP(loaiSP);
        sp.setGiaBan(price);
        sp.setSoLuongTon(stock);
        sp.setMucTonToiThieu(5);
        sp.setDanhMuc(category);
        sp.setMoTa(coverUrl);
        sp.setTrangThai(stock > 0 ? "DangBan" : "HetHang");
        sp.setNgayTao(LocalDateTime.now());
        sanPhamRepository.save(sp);
    }

    private void seedAuthorsAndLinks() {
        // Seed Tác Giả
        TacGia tgMattHaig = createAuthorIfNotFound("Matt Haig", "Tiểu thuyết gia và nhà báo nổi tiếng người Anh, tác giả cuốn sách best-seller quốc tế The Midnight Library.");
        TacGia tgJamesClear = createAuthorIfNotFound("James Clear", "Chuyên gia hàng đầu thế giới về hình thành thói quen và tối ưu hóa năng suất cá nhân, tác giả Atomic Habits.");
        TacGia tgMorganHousel = createAuthorIfNotFound("Morgan Housel", "Đối tác tại The Collaborative Fund, cựu nhà phân tích tài chính tại The Motley Fool và The Wall Street Journal, tác giả Tâm Lý Học Về Tiền.");
        TacGia tgYuval = createAuthorIfNotFound("Yuval Noah Harari", "Giáo sư khoa Lịch sử tại Đại học Hebrew Jerusalem, tác giả bộ sách Sapiens Lược sử loài người kinh điển.");
        TacGia tgJose = createAuthorIfNotFound("José Mauro de Vasconcelos", "Nhà văn lỗi lạc người Brazil, tác giả kiệt tác văn học kinh điển Cây Cam Ngọt Của Tôi.");
        TacGia tgFrank = createAuthorIfNotFound("Frank Herbert", "Đại văn hào người Mỹ, tác giả thiên sử thi khoa học viễn tưởng Dune (Xứ Cát).");
        createAuthorIfNotFound("Nguyễn Nhật Ánh", "Nhà văn nổi tiếng của bao thế hệ độc giả Việt Nam với các tác phẩm như Cho Tôi Xin Một Vé Đi Tuổi Thơ, Mắt Biếc.");
        createAuthorIfNotFound("Rosie Nguyễn", "Tác giả của cuốn sách bán chạy Tuổi Trẻ Đáng Giá Bao Nhiêu, blogger và người truyền cảm hứng sống.");

        // Liên kết Sách với Tác giả
        linkBookAuthor("The Midnight Library", tgMattHaig);
        linkBookAuthor("Atomic Habits - Thay Đổi Tí Hon", tgJamesClear);
        linkBookAuthor("Tâm Lý Học Về Tiền", tgMorganHousel);
        linkBookAuthor("Sapiens: Lược Sử Loài Người", tgYuval);
        linkBookAuthor("Cây Cam Ngọt Của Tôi", tgJose);
        linkBookAuthor("Dune - Xứ Cát", tgFrank);
    }

    private void linkBookAuthor(String bookTitle, TacGia author) {
        sanPhamRepository.findAll().stream()
                .filter(sp -> sp.getTenSP() != null && sp.getTenSP().equalsIgnoreCase(bookTitle))
                .findFirst()
                .ifPresent(sp -> {
                    if (author != null && sanPhamTacGiaRepository.findBySanPham_MaSP(sp.getMaSP()).isEmpty()) {
                        SanPhamTacGia sptg = new SanPhamTacGia(sp, author, 1);
                        sanPhamTacGiaRepository.save(sptg);
                    }
                });
    }

    private TacGia createAuthorIfNotFound(String name, String bio) {
        return tacGiaRepository.findAll().stream()
                .filter(t -> t.getTenTacGia().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    TacGia tg = new TacGia();
                    tg.setTenTacGia(name);
                    tg.setMoTa(bio);
                    return tacGiaRepository.save(tg);
                });
    }
}
