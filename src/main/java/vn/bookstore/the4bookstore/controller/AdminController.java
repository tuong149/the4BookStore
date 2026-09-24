package vn.bookstore.the4bookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import vn.bookstore.the4bookstore.entity.DanhMuc;
import vn.bookstore.the4bookstore.entity.SanPham;
import vn.bookstore.the4bookstore.entity.DonHang;
import vn.bookstore.the4bookstore.entity.TacGia;
import vn.bookstore.the4bookstore.entity.NhaXuatBan;
import vn.bookstore.the4bookstore.entity.SanPhamTacGia;
import vn.bookstore.the4bookstore.repository.DanhMucRepository;
import vn.bookstore.the4bookstore.repository.NhaCungCapRepository;
import vn.bookstore.the4bookstore.repository.NhaXuatBanRepository;
import vn.bookstore.the4bookstore.repository.SanPhamRepository;
import vn.bookstore.the4bookstore.repository.DonHangRepository;
import vn.bookstore.the4bookstore.repository.TacGiaRepository;
import vn.bookstore.the4bookstore.repository.SanPhamTacGiaRepository;
import vn.bookstore.the4bookstore.service.KhoService;
import vn.bookstore.the4bookstore.service.ReportService;
import vn.bookstore.the4bookstore.service.OrderService;
import vn.bookstore.the4bookstore.service.TacGiaService;
import vn.bookstore.the4bookstore.service.NhaXuatBanService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final SanPhamRepository sanPhamRepository;
    private final DanhMucRepository danhMucRepository;
    private final NhaXuatBanRepository nhaXuatBanRepository;
    private final NhaCungCapRepository nhaCungCapRepository;
    private final TacGiaRepository tacGiaRepository;
    private final SanPhamTacGiaRepository sanPhamTacGiaRepository;

    @Autowired private KhoService khoService;
    @Autowired private ReportService reportService;
    @Autowired private OrderService orderService;
    @Autowired private DonHangRepository donHangRepository;
    @Autowired private TacGiaService tacGiaService;
    @Autowired private NhaXuatBanService nhaXuatBanService;

    public AdminController(SanPhamRepository sanPhamRepository,
                           DanhMucRepository danhMucRepository,
                           NhaXuatBanRepository nhaXuatBanRepository,
                           NhaCungCapRepository nhaCungCapRepository,
                           TacGiaRepository tacGiaRepository,
                           SanPhamTacGiaRepository sanPhamTacGiaRepository) {
        this.sanPhamRepository = sanPhamRepository;
        this.danhMucRepository = danhMucRepository;
        this.nhaXuatBanRepository = nhaXuatBanRepository;
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.tacGiaRepository = tacGiaRepository;
        this.sanPhamTacGiaRepository = sanPhamTacGiaRepository;
    }

    // ==================== DASHBOARD ====================
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

    // ==================== ĐƠN HÀNG ====================
    @GetMapping("/orders")
    public String orders(@RequestParam(required = false) String status, Model model) {
        List<DonHang> orders;
        if (status != null && !status.isEmpty()) {
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

    // ==================== QUẢN LÝ SẢN PHẨM (CRUD) ====================
    @GetMapping({"/products", "/books"})
    public String inventory(
            @RequestParam(required = false) String loaiSP,
            @RequestParam(required = false) Integer danhMuc,
            @RequestParam(required = false) String stock,
            @RequestParam(required = false) String q,
            Model model) {

        List<SanPham> allProducts = sanPhamRepository.findAll();
        List<DanhMuc> categories = danhMucRepository.findAll();

        long lowStock = allProducts.stream().filter(b -> b.getSoLuongTon() != null && b.getSoLuongTon() > 0 && b.getSoLuongTon() <= 5).count();
        long outOfStock = allProducts.stream().filter(b -> b.getSoLuongTon() == null || b.getSoLuongTon() == 0).count();

        List<SanPham> filtered = allProducts.stream()
                .filter(p -> loaiSP == null || loaiSP.isBlank() || loaiSP.equalsIgnoreCase("all") || loaiSP.equalsIgnoreCase(p.getLoaiSP()))
                .filter(p -> danhMuc == null || danhMuc == 0 || (p.getDanhMuc() != null && p.getDanhMuc().getMaDanhMuc().equals(danhMuc)))
                .filter(p -> {
                    if ("in_stock".equalsIgnoreCase(stock)) return p.getSoLuongTon() != null && p.getSoLuongTon() > 5;
                    if ("low_stock".equalsIgnoreCase(stock)) return p.getSoLuongTon() != null && p.getSoLuongTon() > 0 && p.getSoLuongTon() <= 5;
                    if ("out_of_stock".equalsIgnoreCase(stock)) return p.getSoLuongTon() == null || p.getSoLuongTon() == 0;
                    return true;
                })
                .filter(p -> {
                    if (q == null || q.isBlank()) return true;
                    String kw = q.trim().toLowerCase();
                    return (p.getTenSP() != null && p.getTenSP().toLowerCase().contains(kw)) ||
                           (p.getISBN() != null && p.getISBN().toLowerCase().contains(kw));
                })
                .toList();

        model.addAttribute("books", filtered);
        model.addAttribute("totalBooks", allProducts.size());
        model.addAttribute("categories", categories);
        model.addAttribute("authors", tacGiaRepository.findAll());
        model.addAttribute("publishers", nhaXuatBanRepository.findAll());
        model.addAttribute("lowStockCount", lowStock);
        model.addAttribute("outOfStockCount", outOfStock);
        model.addAttribute("selectedLoaiSP", loaiSP != null ? loaiSP : "all");
        model.addAttribute("selectedDanhMuc", danhMuc != null ? danhMuc : 0);
        model.addAttribute("selectedStock", stock != null ? stock : "all");
        model.addAttribute("searchQuery", q != null ? q : "");

        return "admin/inventory";
    }

    @PostMapping({"/products/save", "/books/save"})
    public String saveProduct(@RequestParam(value = "maSP", required = false) Integer maSP,
                              @RequestParam("tenSP") String tenSP,
                              @RequestParam(value = "loaiSP", defaultValue = "Sach") String loaiSP,
                              @RequestParam(value = "ISBN", required = false) String isbn,
                              @RequestParam("maDanhMuc") Integer maDanhMuc,
                              @RequestParam(value = "maNXB", required = false) Integer maNXB,
                              @RequestParam(value = "maTacGia", required = false) Integer maTacGia,
                              @RequestParam(value = "soLuongTon", defaultValue = "0") Integer soLuongTon,
                              @RequestParam(value = "giaBan", defaultValue = "0") Integer giaBan,
                              @RequestParam(value = "trangThai", defaultValue = "DangBan") String trangThai,
                              @RequestParam(value = "moTa", required = false) String moTa,
                              RedirectAttributes ra) {
        try {
            SanPham product;
            if (maSP != null && maSP > 0) {
                product = sanPhamRepository.findById(maSP).orElse(new SanPham());
                ra.addFlashAttribute("successMessage", "Cập nhật sản phẩm thành công!");
            } else {
                product = new SanPham();
                product.setNgayTao(LocalDateTime.now());
                nhaCungCapRepository.findAll().stream().findFirst().ifPresent(product::setNhaCungCap);
                ra.addFlashAttribute("successMessage", "Thêm mới sản phẩm thành công!");
            }

            product.setTenSP(tenSP.trim());
            product.setLoaiSP(loaiSP != null && !loaiSP.isBlank() ? loaiSP : "Sach");
            product.setISBN(isbn != null && !isbn.isBlank() ? isbn.trim() : null);
            product.setSoLuongTon(soLuongTon != null ? soLuongTon : 0);
            product.setGiaBan(giaBan != null ? giaBan : 0);
            product.setTrangThai(trangThai != null ? trangThai : "DangBan");
            product.setMoTa(moTa != null ? moTa.trim() : null);
            danhMucRepository.findById(maDanhMuc).ifPresent(product::setDanhMuc);

            if (maNXB != null && maNXB > 0) {
                nhaXuatBanRepository.findById(maNXB).ifPresent(product::setNhaXuatBan);
            } else {
                product.setNhaXuatBan(null);
            }

            SanPham savedProduct = sanPhamRepository.save(product);

            if (maTacGia != null && maTacGia > 0) {
                sanPhamTacGiaRepository.deleteBySanPhamId(savedProduct.getMaSP());
                tacGiaRepository.findById(maTacGia).ifPresent(author -> {
                    SanPhamTacGia sptg = new SanPhamTacGia(savedProduct, author, 1);
                    sanPhamTacGiaRepository.save(sptg);
                });
            }

        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi lưu sản phẩm: " + (e.getMessage() != null && e.getMessage().contains("ISBN") ? "Mã ISBN đã tồn tại!" : e.getMessage()));
        }
        return "redirect:/admin/products";
    }

    @PostMapping({"/products/delete/{id}", "/books/delete/{id}"})
    public String deleteProduct(@PathVariable("id") Integer id, RedirectAttributes ra) {
        if (sanPhamRepository.existsById(id)) {
            try {
                sanPhamTacGiaRepository.deleteBySanPhamId(id);
                sanPhamRepository.deleteById(id);
                ra.addFlashAttribute("successMessage", "Đã xóa sản phẩm thành công!");
            } catch (Exception e) {
                sanPhamRepository.findById(id).ifPresent(sp -> {
                    sp.setTrangThai("NgungBan");
                    sanPhamRepository.save(sp);
                });
                ra.addFlashAttribute("warningMessage", "Sản phẩm đã có dữ liệu liên kết nên được chuyển sang trạng thái 'Ngừng Bán'!");
            }
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/products/{id}/json")
    @ResponseBody
    public ResponseEntity<?> getProductJson(@PathVariable Integer id) {
        return sanPhamRepository.findById(id)
                .map(sp -> {
                    Integer authorId = null;
                    if (sp.getSanPhamTacGias() != null && !sp.getSanPhamTacGias().isEmpty()) {
                        authorId = sp.getSanPhamTacGias().get(0).getTacGia() != null ? sp.getSanPhamTacGias().get(0).getTacGia().getMaTacGia() : null;
                    }
                    Map<String, Object> data = new HashMap<>();
                    data.put("maSP", sp.getMaSP());
                    data.put("tenSP", sp.getTenSP());
                    data.put("loaiSP", sp.getLoaiSP() != null ? sp.getLoaiSP() : "Sach");
                    data.put("maDanhMuc", sp.getDanhMuc() != null ? sp.getDanhMuc().getMaDanhMuc() : 0);
                    data.put("maNXB", sp.getNhaXuatBan() != null ? sp.getNhaXuatBan().getMaNXB() : 0);
                    data.put("maTacGia", authorId != null ? authorId : 0);
                    data.put("isbn", sp.getISBN() != null ? sp.getISBN() : "");
                    data.put("giaBan", sp.getGiaBan() != null ? sp.getGiaBan() : 0);
                    data.put("soLuongTon", sp.getSoLuongTon() != null ? sp.getSoLuongTon() : 0);
                    data.put("trangThai", sp.getTrangThai() != null ? sp.getTrangThai() : "DangBan");
                    data.put("moTa", sp.getMoTa() != null ? sp.getMoTa() : "");
                    return ResponseEntity.ok(data);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== QUẢN LÝ DANH MỤC (CRUD) ====================
    @GetMapping("/categories")
    public String categories(Model model) {
        List<DanhMuc> categories = danhMucRepository.findAll();
        Map<Integer, Long> productCounts = new HashMap<>();
        for (DanhMuc dm : categories) {
            productCounts.put(dm.getMaDanhMuc(), sanPhamRepository.countByDanhMuc_MaDanhMuc(dm.getMaDanhMuc()));
        }

        long activeCount = categories.stream().filter(c -> Boolean.TRUE.equals(c.getTrangThai())).count();
        long inactiveCount = categories.size() - activeCount;

        model.addAttribute("categories", categories);
        model.addAttribute("productCounts", productCounts);
        model.addAttribute("totalCategories", categories.size());
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("inactiveCount", inactiveCount);

        return "admin/categories";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@RequestParam(value = "maDanhMuc", required = false) Integer maDanhMuc,
                               @RequestParam("tenDanhMuc") String tenDanhMuc,
                               @RequestParam(value = "moTa", required = false) String moTa,
                               @RequestParam(value = "trangThai", defaultValue = "true") Boolean trangThai,
                               RedirectAttributes ra) {
        try {
            DanhMuc dm;
            if (maDanhMuc != null && maDanhMuc > 0) {
                dm = danhMucRepository.findById(maDanhMuc).orElse(new DanhMuc());
                ra.addFlashAttribute("successMessage", "Cập nhật danh mục thành công!");
            } else {
                dm = new DanhMuc();
                ra.addFlashAttribute("successMessage", "Thêm mới danh mục thành công!");
            }
            dm.setTenDanhMuc(tenDanhMuc.trim());
            dm.setMoTa(moTa != null ? moTa.trim() : "");
            dm.setTrangThai(trangThai != null ? trangThai : true);

            danhMucRepository.save(dm);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi lưu danh mục: Tên danh mục có thể đã tồn tại hoặc không hợp lệ!");
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Integer id, RedirectAttributes ra) {
        long count = sanPhamRepository.countByDanhMuc_MaDanhMuc(id);
        if (count > 0) {
            ra.addFlashAttribute("errorMessage", "Không thể xóa danh mục này vì đang có " + count + " sản phẩm liên kết!");
            return "redirect:/admin/categories";
        }
        try {
            danhMucRepository.deleteById(id);
            ra.addFlashAttribute("successMessage", "Đã xóa danh mục thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Không thể xóa danh mục: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/toggle/{id}")
    public String toggleCategory(@PathVariable Integer id, RedirectAttributes ra) {
        danhMucRepository.findById(id).ifPresent(dm -> {
            boolean current = Boolean.TRUE.equals(dm.getTrangThai());
            dm.setTrangThai(!current);
            danhMucRepository.save(dm);
            ra.addFlashAttribute("successMessage", "Đã " + (!current ? "kích hoạt" : "tạm khóa") + " danh mục '" + dm.getTenDanhMuc() + "'");
        });
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/{id}/json")
    @ResponseBody
    public ResponseEntity<?> getCategoryJson(@PathVariable Integer id) {
        return danhMucRepository.findById(id)
                .map(dm -> ResponseEntity.ok(Map.of(
                        "maDanhMuc", dm.getMaDanhMuc(),
                        "tenDanhMuc", dm.getTenDanhMuc(),
                        "moTa", dm.getMoTa() != null ? dm.getMoTa() : "",
                        "trangThai", Boolean.TRUE.equals(dm.getTrangThai())
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== QUẢN LÝ TÁC GIẢ (CRUD) ====================
    @GetMapping({"/authors", "/tac-gia"})
    public String authors(Model model) {
        List<TacGia> authors = tacGiaRepository.findAll();
        Map<Integer, Long> bookCounts = new HashMap<>();
        for (TacGia tg : authors) {
            bookCounts.put(tg.getMaTacGia(), sanPhamTacGiaRepository.countByTacGia_MaTacGia(tg.getMaTacGia()));
        }
        long authorsWithBooks = bookCounts.values().stream().filter(c -> c > 0).count();

        model.addAttribute("authors", authors);
        model.addAttribute("bookCounts", bookCounts);
        model.addAttribute("totalAuthors", authors.size());
        model.addAttribute("authorsWithBooks", authorsWithBooks);

        return "admin/authors";
    }

    @PostMapping({"/authors/save", "/tac-gia/save"})
    public String saveAuthor(@RequestParam(value = "maTacGia", required = false) Integer maTacGia,
                             @RequestParam("tenTacGia") String tenTacGia,
                             @RequestParam(value = "moTa", required = false) String moTa,
                             RedirectAttributes ra) {
        try {
            TacGia tg;
            if (maTacGia != null && maTacGia > 0) {
                tg = tacGiaRepository.findById(maTacGia).orElse(new TacGia());
                ra.addFlashAttribute("successMessage", "Cập nhật tác giả thành công!");
            } else {
                tg = new TacGia();
                ra.addFlashAttribute("successMessage", "Thêm mới tác giả thành công!");
            }
            tg.setTenTacGia(tenTacGia.trim());
            tg.setMoTa(moTa != null ? moTa.trim() : "");
            tacGiaRepository.save(tg);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi lưu tác giả: " + e.getMessage());
        }
        return "redirect:/admin/authors";
    }

    @PostMapping({"/authors/delete/{id}", "/tac-gia/delete/{id}"})
    public String deleteAuthor(@PathVariable Integer id, RedirectAttributes ra) {
        long count = sanPhamTacGiaRepository.countByTacGia_MaTacGia(id);
        if (count > 0) {
            ra.addFlashAttribute("errorMessage", "Không thể xóa tác giả này vì đang có " + count + " đầu sách liên kết!");
            return "redirect:/admin/authors";
        }
        try {
            tacGiaRepository.deleteById(id);
            ra.addFlashAttribute("successMessage", "Đã xóa tác giả thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Không thể xóa tác giả: " + e.getMessage());
        }
        return "redirect:/admin/authors";
    }

    @GetMapping({"/authors/{id}/json", "/tac-gia/{id}/json"})
    @ResponseBody
    public ResponseEntity<?> getAuthorJson(@PathVariable Integer id) {
        return tacGiaRepository.findById(id)
                .map(tg -> ResponseEntity.ok(Map.of(
                        "maTacGia", tg.getMaTacGia(),
                        "tenTacGia", tg.getTenTacGia(),
                        "moTa", tg.getMoTa() != null ? tg.getMoTa() : ""
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping({"/authors/{id}/books", "/tac-gia/{id}/books"})
    @ResponseBody
    public ResponseEntity<?> getAuthorBooks(@PathVariable Integer id) {
        List<SanPham> books = sanPhamRepository.findBooksByTacGiaId(id);
        List<Map<String, Object>> bookDtos = books.stream().map(b -> {
            Map<String, Object> map = new HashMap<>();
            map.put("maSP", b.getMaSP());
            map.put("tenSP", b.getTenSP());
            map.put("isbn", b.getISBN() != null ? b.getISBN() : "");
            map.put("giaBan", b.getGiaBan());
            map.put("soLuongTon", b.getSoLuongTon());
            map.put("trangThai", b.getTrangThai());
            map.put("hinhAnh", b.getHinhAnh());
            map.put("danhMuc", b.getDanhMuc() != null ? b.getDanhMuc().getTenDanhMuc() : "");
            map.put("nhaXuatBan", b.getNhaXuatBan() != null ? b.getNhaXuatBan().getTenNXB() : "");
            return map;
        }).toList();
        return ResponseEntity.ok(bookDtos);
    }

    // ==================== QUẢN LÝ NHÀ XUẤT BẢN (CRUD) ====================
    @GetMapping({"/publishers", "/nha-xuat-ban"})
    public String publishers(Model model) {
        List<NhaXuatBan> publishers = nhaXuatBanRepository.findAll();
        Map<Integer, Long> bookCounts = new HashMap<>();
        for (NhaXuatBan nxb : publishers) {
            bookCounts.put(nxb.getMaNXB(), sanPhamRepository.countByNhaXuatBan_MaNXB(nxb.getMaNXB()));
        }
        long publishersWithBooks = bookCounts.values().stream().filter(c -> c > 0).count();

        model.addAttribute("publishers", publishers);
        model.addAttribute("bookCounts", bookCounts);
        model.addAttribute("totalPublishers", publishers.size());
        model.addAttribute("publishersWithBooks", publishersWithBooks);

        return "admin/publishers";
    }

    @PostMapping({"/publishers/save", "/nha-xuat-ban/save"})
    public String savePublisher(@RequestParam(value = "maNXB", required = false) Integer maNXB,
                                @RequestParam("tenNXB") String tenNXB,
                                @RequestParam(value = "diaChi", required = false) String diaChi,
                                @RequestParam(value = "soDienThoai", required = false) String soDienThoai,
                                @RequestParam(value = "email", required = false) String email,
                                RedirectAttributes ra) {
        try {
            NhaXuatBan nxb;
            if (maNXB != null && maNXB > 0) {
                nxb = nhaXuatBanRepository.findById(maNXB).orElse(new NhaXuatBan());
                ra.addFlashAttribute("successMessage", "Cập nhật nhà xuất bản thành công!");
            } else {
                nxb = new NhaXuatBan();
                ra.addFlashAttribute("successMessage", "Thêm mới nhà xuất bản thành công!");
            }
            nxb.setTenNXB(tenNXB.trim());
            nxb.setDiaChi(diaChi != null ? diaChi.trim() : "");
            nxb.setSoDienThoai(soDienThoai != null ? soDienThoai.trim() : "");
            nxb.setEmail(email != null ? email.trim() : "");
            nhaXuatBanRepository.save(nxb);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi lưu nhà xuất bản: " + e.getMessage());
        }
        return "redirect:/admin/publishers";
    }

    @PostMapping({"/publishers/delete/{id}", "/nha-xuat-ban/delete/{id}"})
    public String deletePublisher(@PathVariable Integer id, RedirectAttributes ra) {
        long count = sanPhamRepository.countByNhaXuatBan_MaNXB(id);
        if (count > 0) {
            ra.addFlashAttribute("errorMessage", "Không thể xóa nhà xuất bản này vì đang có " + count + " đầu sách liên kết!");
            return "redirect:/admin/publishers";
        }
        try {
            nhaXuatBanRepository.deleteById(id);
            ra.addFlashAttribute("successMessage", "Đã xóa nhà xuất bản thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Không thể xóa nhà xuất bản: " + e.getMessage());
        }
        return "redirect:/admin/publishers";
    }

    @GetMapping({"/publishers/{id}/json", "/nha-xuat-ban/{id}/json"})
    @ResponseBody
    public ResponseEntity<?> getPublisherJson(@PathVariable Integer id) {
        return nhaXuatBanRepository.findById(id)
                .map(nxb -> ResponseEntity.ok(Map.of(
                        "maNXB", nxb.getMaNXB(),
                        "tenNXB", nxb.getTenNXB(),
                        "diaChi", nxb.getDiaChi() != null ? nxb.getDiaChi() : "",
                        "soDienThoai", nxb.getSoDienThoai() != null ? nxb.getSoDienThoai() : "",
                        "email", nxb.getEmail() != null ? nxb.getEmail() : ""
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping({"/publishers/{id}/books", "/nha-xuat-ban/{id}/books"})
    @ResponseBody
    public ResponseEntity<?> getPublisherBooks(@PathVariable Integer id) {
        List<SanPham> books = sanPhamRepository.findByNhaXuatBan_MaNXB(id);
        List<Map<String, Object>> bookDtos = books.stream().map(b -> {
            Map<String, Object> map = new HashMap<>();
            map.put("maSP", b.getMaSP());
            map.put("tenSP", b.getTenSP());
            map.put("isbn", b.getISBN() != null ? b.getISBN() : "");
            map.put("giaBan", b.getGiaBan());
            map.put("soLuongTon", b.getSoLuongTon());
            map.put("trangThai", b.getTrangThai());
            map.put("hinhAnh", b.getHinhAnh());
            map.put("danhMuc", b.getDanhMuc() != null ? b.getDanhMuc().getTenDanhMuc() : "");
            return map;
        }).toList();
        return ResponseEntity.ok(bookDtos);
    }
}
