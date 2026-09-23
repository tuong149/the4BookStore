package vn.bookstore.the4bookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.bookstore.the4bookstore.entity.DanhMuc;
import vn.bookstore.the4bookstore.entity.SanPham;
import vn.bookstore.the4bookstore.repository.DanhMucRepository;
import vn.bookstore.the4bookstore.repository.NhaCungCapRepository;
import vn.bookstore.the4bookstore.repository.NhaXuatBanRepository;
import vn.bookstore.the4bookstore.repository.SanPhamRepository;

import org.springframework.http.ResponseEntity;
import vn.bookstore.the4bookstore.service.KhoService;
import vn.bookstore.the4bookstore.service.ReportService;
import vn.bookstore.the4bookstore.service.OrderService;
import vn.bookstore.the4bookstore.repository.DonHangRepository;
import vn.bookstore.the4bookstore.entity.DonHang;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final SanPhamRepository sanPhamRepository;
    private final DanhMucRepository danhMucRepository;
    private final NhaXuatBanRepository nhaXuatBanRepository;
    private final NhaCungCapRepository nhaCungCapRepository;

    @Autowired private KhoService khoService;
    @Autowired private ReportService reportService;
    @Autowired private OrderService orderService;
    @Autowired private DonHangRepository donHangRepository;

    public AdminController(SanPhamRepository sanPhamRepository,
                           DanhMucRepository danhMucRepository,
                           NhaXuatBanRepository nhaXuatBanRepository,
                           NhaCungCapRepository nhaCungCapRepository) {
        this.sanPhamRepository = sanPhamRepository;
        this.danhMucRepository = danhMucRepository;
        this.nhaXuatBanRepository = nhaXuatBanRepository;
        this.nhaCungCapRepository = nhaCungCapRepository;
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("monthlyRevenue", reportService.getRevenueByMonth());
        model.addAttribute("topBooks", reportService.getTopSellingBooks());
        model.addAttribute("todayOrders", reportService.getTodayOrderCount());
        model.addAttribute("totalSold", reportService.getTotalBooksSold());

        List<SanPham> books = sanPhamRepository.findAll();
        long totalBooks = books.size();
        long lowStock = books.stream().filter(b -> b.getSoLuongTon() != null && b.getSoLuongTon() > 0 && b.getSoLuongTon() <= 5).count();
        long outOfStock = books.stream().filter(b -> b.getSoLuongTon() == null || b.getSoLuongTon() == 0).count();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("'Ngày' dd 'tháng' MM, yyyy");
        String formattedDate = LocalDate.now().format(dtf);

        model.addAttribute("totalBooksCount", totalBooks);
        model.addAttribute("lowStockCount", lowStock);
        model.addAttribute("outOfStockCount", outOfStock);
        model.addAttribute("currentDate", formattedDate);

        return "admin/dashboard";
    }

    @GetMapping("/orders")
    public String orders(@RequestParam(required = false) String status, Model model) {
        List<DonHang> orders;
        if (status != null && !status.isEmpty()) {
            // Need a findByTrangThai in donHangRepo, but we'll manually filter for safety if it doesn't exist yet, 
            // actually let's assume finding all and filtering or just returning all if no repository method.
            orders = donHangRepository.findAll().stream()
                    .filter(dh -> status.equals(dh.getTrangThai())).toList();
        } else {
            orders = donHangRepository.findAll();
        }
        model.addAttribute("orders", orders);
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    @ResponseBody
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            orderService.updateStatus(id, status);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/books")
    public String inventory(Model model) {
        List<SanPham> books = sanPhamRepository.findAll();
        List<DanhMuc> categories = danhMucRepository.findAll();

        long lowStock = books.stream().filter(b -> b.getSoLuongTon() != null && b.getSoLuongTon() > 0 && b.getSoLuongTon() <= 5).count();
        long outOfStock = books.stream().filter(b -> b.getSoLuongTon() == null || b.getSoLuongTon() == 0).count();

        model.addAttribute("books", books);
        model.addAttribute("categories", categories);
        model.addAttribute("lowStockCount", lowStock);
        model.addAttribute("outOfStockCount", outOfStock);

        return "admin/inventory";
    }

    @PostMapping("/books/save")
    public String saveBook(@RequestParam(value = "maSP", required = false) Integer maSP,
                           @RequestParam("tenSP") String tenSP,
                           @RequestParam(value = "ISBN", required = false) String isbn,
                           @RequestParam("maDanhMuc") Integer maDanhMuc,
                           @RequestParam("soLuongTon") Integer soLuongTon,
                           @RequestParam("giaBan") Integer giaBan,
                           @RequestParam(value = "moTa", required = false) String moTa) {
        SanPham book;
        if (maSP != null) {
            book = sanPhamRepository.findById(maSP).orElse(new SanPham());
        } else {
            book = new SanPham();
            book.setLoaiSP("Sach");
            // Set default publisher and supplier if available
            nhaXuatBanRepository.findAll().stream().findFirst().ifPresent(book::setNhaXuatBan);
            nhaCungCapRepository.findAll().stream().findFirst().ifPresent(book::setNhaCungCap);
        }

        book.setTenSP(tenSP);
        book.setISBN(isbn);
        book.setSoLuongTon(soLuongTon != null ? soLuongTon : 0);
        book.setGiaBan(giaBan != null ? giaBan : 0);
        book.setMoTa(moTa);
        danhMucRepository.findById(maDanhMuc).ifPresent(book::setDanhMuc);

        sanPhamRepository.save(book);
        return "redirect:/admin/books";
    }

    @PostMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable("id") Integer id) {
        if (sanPhamRepository.existsById(id)) {
            sanPhamRepository.deleteById(id);
        }
        return "redirect:/admin/books";
    }
}
