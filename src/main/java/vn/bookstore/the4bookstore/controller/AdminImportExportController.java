package vn.bookstore.the4bookstore.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.bookstore.the4bookstore.entity.DanhMuc;
import vn.bookstore.the4bookstore.entity.DonHang;
import vn.bookstore.the4bookstore.entity.SanPham;
import vn.bookstore.the4bookstore.entity.Shop;
import vn.bookstore.the4bookstore.entity.TaiKhoan;
import vn.bookstore.the4bookstore.repository.DanhMucRepository;
import vn.bookstore.the4bookstore.repository.DonHangRepository;
import vn.bookstore.the4bookstore.repository.NhaXuatBanRepository;
import vn.bookstore.the4bookstore.repository.SanPhamRepository;
import vn.bookstore.the4bookstore.repository.ShopRepository;
import vn.bookstore.the4bookstore.repository.TaiKhoanRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/import-export")
@RequiredArgsConstructor
public class AdminImportExportController {

    private final SanPhamRepository sanPhamRepository;
    private final DonHangRepository donHangRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final ShopRepository shopRepository;
    private final DanhMucRepository danhMucRepository;
    private final NhaXuatBanRepository nhaXuatBanRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("totalProducts", sanPhamRepository.count());
        model.addAttribute("totalOrders", donHangRepository.count());
        model.addAttribute("totalUsers", taiKhoanRepository.count());
        model.addAttribute("totalShops", shopRepository.count());
        return "admin/import_export";
    }


    // ==================== XUẤT TOÀN BỘ SẢN PHẨM ====================
    @GetMapping("/export/products")
    public void exportProductsCsv(HttpServletResponse response) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"danh_sach_san_pham_" + timestamp + ".csv\"");

        List<SanPham> list = sanPhamRepository.findAll();

        try (OutputStream os = response.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            os.write(0xEF); os.write(0xBB); os.write(0xBF);

            writer.println("Mã SP,ISBN,Tên Sản Phẩm,Giá Bán (VNĐ),Số Lượng Tồn,Số Lượng Đã Bán,Loại Sản Phẩm,Danh Mục,Nhà Xuất Bản,Gian Hàng,Trạng Thái Bán,Trạng Thái Khóa");

            for (SanPham p : list) {
                String shopName = p.getShop() != null ? escapeCsv(p.getShop().getTenShop()) : "The4BookStore Official";
                String nxbName = p.getNhaXuatBan() != null ? escapeCsv(p.getNhaXuatBan().getTenNXB()) : "";
                String dmName = p.getDanhMuc() != null ? escapeCsv(p.getDanhMuc().getTenDanhMuc()) : "";

                writer.println(String.format("%d,%s,%s,%d,%d,%d,%s,%s,%s,%s,%s,%s",
                        p.getMaSP(),
                        p.getISBN() != null ? escapeCsv(p.getISBN()) : "",
                        escapeCsv(p.getTenSP()),
                        p.getGiaBan() != null ? p.getGiaBan() : 0,
                        p.getSoLuongTon() != null ? p.getSoLuongTon() : 0,
                        p.getSoLuongDaBan() != null ? p.getSoLuongDaBan() : 0,
                        escapeCsv(p.getLoaiSP()),
                        dmName,
                        nxbName,
                        shopName,
                        escapeCsv(p.getTrangThai()),
                        escapeCsv(p.getTrangThaiKhoa())
                ));
            }
            writer.flush();
        }
    }

    // ==================== XUẤT DANH SÁCH ĐƠN HÀNG ====================
    @GetMapping("/export/orders")
    public void exportOrdersCsv(HttpServletResponse response) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"danh_sach_don_hang_" + timestamp + ".csv\"");

        List<DonHang> list = donHangRepository.findAll();

        try (OutputStream os = response.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            os.write(0xEF); os.write(0xBB); os.write(0xBF);

            writer.println("Mã ĐH,Ngày Đặt,Khách Hàng,SĐT Giao,Địa Chỉ Giao,Gian Hàng,Đơn Vị Vận Chuyển,Phí Ship,Tiền Giảm,Tổng Tiền (VNĐ),Phương Thức TT,Trạng Thái TT,Trạng Thái Đơn");

            for (DonHang d : list) {
                String khName = d.getKhachHang() != null ? escapeCsv(d.getKhachHang().getHoTen()) : "Khách vãng lai";
                String shopName = d.getShop() != null ? escapeCsv(d.getShop().getTenShop()) : "The4BookStore Official";
                String nvcName = d.getNhaVanChuyen() != null ? escapeCsv(d.getNhaVanChuyen().getTenNvc()) : "Tiêu chuẩn";
                String ngayDat = d.getNgayDat() != null ? d.getNgayDat().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";

                writer.println(String.format("%d,%s,%s,%s,%s,%s,%s,%d,%d,%d,%s,%s,%s",
                        d.getMaDH(),
                        ngayDat,
                        khName,
                        d.getSoDienThoaiGiao() != null ? escapeCsv(d.getSoDienThoaiGiao()) : "",
                        d.getDiaChiGiao() != null ? escapeCsv(d.getDiaChiGiao()) : "",
                        shopName,
                        nvcName,
                        d.getPhiVanChuyen() != null ? d.getPhiVanChuyen() : 0,
                        d.getTienGiam() != null ? d.getTienGiam() : 0,
                        d.getTongTien() != null ? d.getTongTien() : 0,
                        escapeCsv(d.getPhuongThucThanhToan()),
                        escapeCsv(d.getTrangThaiThanhToan()),
                        escapeCsv(d.getTrangThai())
                ));
            }
            writer.flush();
        }
    }

    // ==================== XUẤT DANH SÁCH TÀI KHOẢN ====================
    @GetMapping("/export/users")
    public void exportUsersCsv(HttpServletResponse response) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"danh_sach_tai_khoan_" + timestamp + ".csv\"");

        List<TaiKhoan> list = taiKhoanRepository.findAll();

        try (OutputStream os = response.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            os.write(0xEF); os.write(0xBB); os.write(0xBF);

            writer.println("Mã TK,Tên Đăng Nhập,Email,Vai Trò,Trạng Thái,Hình Thức Đăng Nhập,Ngày Tạo");

            for (TaiKhoan u : list) {
                String ngayTao = u.getNgayTao() != null ? u.getNgayTao().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
                writer.println(String.format("%d,%s,%s,%s,%s,%s,%s",
                        u.getMaTaiKhoan(),
                        escapeCsv(u.getTenDangNhap()),
                        escapeCsv(u.getEmail()),
                        escapeCsv(u.getVaiTro()),
                        escapeCsv(u.getTrangThai()),
                        escapeCsv(u.getAuthProvider() != null ? u.getAuthProvider() : "LOCAL"),
                        ngayTao
                ));
            }
            writer.flush();
        }
    }

    // ==================== XUẤT DANH SÁCH GIAN HÀNG ====================
    @GetMapping("/export/shops")
    public void exportShopsCsv(HttpServletResponse response) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"danh_sach_gian_hang_" + timestamp + ".csv\"");

        List<Shop> list = shopRepository.findAll();

        try (OutputStream os = response.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            os.write(0xEF); os.write(0xBB); os.write(0xBF);

            writer.println("Mã Shop,Tên Shop,Slug,Tài Khoản Chủ Shop,SĐT,Email,Địa Chỉ,Chiết Khấu (%),Trạng Thái,Ngày Tạo");

            for (Shop s : list) {
                String user = s.getTaiKhoan() != null ? escapeCsv(s.getTaiKhoan().getTenDangNhap()) : "";
                String ngayTao = s.getNgayTao() != null ? s.getNgayTao().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";

                writer.println(String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                        s.getMaShop(),
                        escapeCsv(s.getTenShop()),
                        escapeCsv(s.getSlug()),
                        user,
                        s.getSoDienThoai() != null ? escapeCsv(s.getSoDienThoai()) : "",
                        s.getEmailShop() != null ? escapeCsv(s.getEmailShop()) : "",
                        s.getDiaChiShop() != null ? escapeCsv(s.getDiaChiShop()) : "",
                        s.getChietKhauPhanTram() != null ? s.getChietKhauPhanTram().toString() : "5.00",
                        escapeCsv(s.getTrangThai()),
                        ngayTao
                ));
            }
            writer.flush();
        }
    }


    private String escapeCsv(String value) {
        if (value == null) return "";
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\"") || escaped.contains(";")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }

    private String[] parseCsvLine(String line) {
        List<String> tokens = new java.util.ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }

    private int parseIntSafe(String val, int def) {
        try {
            return Integer.parseInt(val.trim().replace(".", "").replace(",", ""));
        } catch (Exception e) {
            return def;
        }
    }

    private Integer parseIntegerSafe(String val) {
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
