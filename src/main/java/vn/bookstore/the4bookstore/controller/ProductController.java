package vn.bookstore.the4bookstore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.bookstore.the4bookstore.entity.DanhMuc;
import vn.bookstore.the4bookstore.entity.SanPham;
import vn.bookstore.the4bookstore.service.DanhMucService;
import vn.bookstore.the4bookstore.service.SanPhamService;
import vn.bookstore.the4bookstore.entity.KhachHang;
import vn.bookstore.the4bookstore.service.TacGiaService;
import vn.bookstore.the4bookstore.service.UserInteractionService;
import org.springframework.security.core.Authentication;

@Controller
@RequiredArgsConstructor
@RequestMapping("/san-pham")
public class ProductController {

    public record MajorCategoryPreview(
            vn.bookstore.the4bookstore.entity.DanhMucLon major,
            List<DanhMuc> subs,
            List<SanPham> previewProducts
    ) {}


    private final SanPhamService sanPhamService;
    private final TacGiaService tacGiaService;
    private final UserInteractionService userInteractionService;
    private final vn.bookstore.the4bookstore.service.ShopService shopService;
    private final vn.bookstore.the4bookstore.service.CurrentUserService currentUserService;
    private final DanhMucService danhMucService;

    private KhachHang getCurrentKhachHang(Authentication auth, jakarta.servlet.http.HttpServletRequest request) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) return null;
        if (request != null) {
            Object cached = request.getAttribute("CURRENT_KHACH_HANG_CACHE");
            if (cached instanceof KhachHang kh) return kh;
            if (Boolean.TRUE.equals(request.getAttribute("CURRENT_KHACH_HANG_CACHE_NULL"))) return null;
        }
        KhachHang kh = currentUserService.getCurrentKhachHang(auth);
        if (request != null) {
            if (kh != null) {
                request.setAttribute("CURRENT_KHACH_HANG_CACHE", kh);
            } else {
                request.setAttribute("CURRENT_KHACH_HANG_CACHE_NULL", true);
            }
        }
        return kh;
    }

    // CẤP 0: Landing page — Tất cả sản phẩm (hoặc hiển thị kết quả nếu người dùng tìm kiếm / lọc)
    @GetMapping
    public String landing(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String loaiSP,
            @RequestParam(required = false) Integer danhMuc,
            @RequestParam(required = false) Integer tacGia,
            @RequestParam(required = false) Integer nxb,
            @RequestParam(required = false) Integer giaTu,
            @RequestParam(required = false) Integer giaDen,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        // TỰ ĐỘNG CHỌN DANH MỤC LỚN THEO THỂ LOẠI (Nếu người dùng chọn thể loại trước hoặc đổi thể loại)
        if (danhMuc != null && danhMuc > 0) {
            Optional<DanhMuc> catOpt = danhMucService.getById(danhMuc);
            if (catOpt.isPresent() && catOpt.get().getLoaiSP() != null && !catOpt.get().getLoaiSP().isBlank()) {
                String inferredLoai = catOpt.get().getLoaiSP();
                if (loaiSP == null || loaiSP.isBlank() || "ALL".equalsIgnoreCase(loaiSP) || !loaiSP.equalsIgnoreCase(inferredLoai)) {
                    loaiSP = inferredLoai;
                }
            }
        }

        if (loaiSP != null && !loaiSP.isBlank() && !"ALL".equalsIgnoreCase(loaiSP)) {
            if (!"Sach".equalsIgnoreCase(loaiSP)) {
                tacGia = null;
                nxb = null;
            }
        } else {
            tacGia = null;
            nxb = null;
        }

        if (giaTu != null && giaDen != null && giaTu > giaDen) {
            int temp = giaTu;
            giaTu = giaDen;
            giaDen = temp;
        }

        boolean isFiltering = (q != null && !q.isBlank()) ||
                              (loaiSP != null && !loaiSP.isBlank() && !"ALL".equalsIgnoreCase(loaiSP)) ||
                              (danhMuc != null && danhMuc > 0) ||
                              (tacGia != null && tacGia > 0) ||
                              (nxb != null && nxb > 0) ||
                              (giaTu != null && giaTu > 0) ||
                              (giaDen != null && giaDen > 0);

        List<DanhMuc> categoriesSach = sanPhamService.getCategoriesByLoaiSP("Sach");
        List<DanhMuc> categoriesVpp = sanPhamService.getCategoriesByLoaiSP("VanPhongPham");
        List<DanhMuc> categoriesQuaTang = sanPhamService.getCategoriesByLoaiSP("QuaTang");

        if (tacGia != null && tacGia > 0 && danhMuc != null && danhMuc > 0) {
            if (!tacGiaService.isAuthorInCategory(tacGia, danhMuc)) {
                tacGia = null;
            }
        }

        model.addAttribute("categoriesSach",    categoriesSach);
        model.addAttribute("categoriesVpp",     categoriesVpp);
        model.addAttribute("categoriesQuaTang", categoriesQuaTang);
        model.addAttribute("authors",           tacGiaService.getAuthorsByLoaiSP("Sach"));
        model.addAttribute("authorCategoryMap", tacGiaService.getAuthorCategoryMap());
        model.addAttribute("publishers",        List.of());
        model.addAttribute("giaTu",             giaTu);
        model.addAttribute("giaDen",            giaDen);
        model.addAttribute("keyword",           q != null ? q : "");
        model.addAttribute("sortBy",            sort);

        if (loaiSP != null && !loaiSP.isBlank() && !"ALL".equalsIgnoreCase(loaiSP)) {
            model.addAttribute("categories", sanPhamService.getCategoriesByLoaiSP(loaiSP));
        } else {
            model.addAttribute("categories", List.of());
        }

        if (isFiltering) {
            int pageSize = 12;
            Page<SanPham> productPage;
            if ((giaTu != null && giaTu > 0) || (giaDen != null && giaDen > 0)) {
                productPage = sanPhamService.searchAndFilter(loaiSP, q, danhMuc, tacGia, nxb, giaTu, giaDen, sort, page, pageSize);
            } else {
                productPage = sanPhamService.searchAndFilter(loaiSP, q, danhMuc, tacGia, nxb, sort, page, pageSize);
            }
            List<vn.bookstore.the4bookstore.entity.Shop> matchedShops = List.of();
            if (q != null && !q.isBlank()) {
                matchedShops = shopService.searchActiveShops(q);
            }
            model.addAttribute("matchedShops",    matchedShops);
            model.addAttribute("products",        productPage.getContent());
            model.addAttribute("currentPage",     productPage.getNumber());
            model.addAttribute("totalPages",      productPage.getTotalPages());
            model.addAttribute("totalElements",   productPage.getTotalElements());
            model.addAttribute("groupName",       getGroupName(loaiSP));
            model.addAttribute("loaiSP",          loaiSP != null && !loaiSP.isBlank() ? loaiSP : "ALL");
            model.addAttribute("baseUrl",         "/san-pham");
            model.addAttribute("keyword",         q != null ? q : "");
            model.addAttribute("sortBy",          sort);
            model.addAttribute("selectedDanhMuc", danhMuc);
            model.addAttribute("selectedTacGia",  tacGia);
            model.addAttribute("selectedNxb",     nxb);
            model.addAttribute("giaTu",           giaTu);
            model.addAttribute("giaDen",          giaDen);
            return "products/list";
        }

        // Mặc định: hiện preview TẤT CẢ các Danh mục lớn (Sách, VPP, Quà tặng, Boardgame...)
        List<MajorCategoryPreview> majorPreviews = new java.util.ArrayList<>();
        List<vn.bookstore.the4bookstore.entity.DanhMucLon> activeMajors = danhMucService.getActiveMajorCategories();
        for (vn.bookstore.the4bookstore.entity.DanhMucLon major : activeMajors) {
            List<DanhMuc> subs = sanPhamService.getCategoriesByLoaiSP(major.getMaLoai());
            List<SanPham> preview = sanPhamService.getPreviewByLoaiSP(major.getMaLoai());
            majorPreviews.add(new MajorCategoryPreview(major, subs, preview));
        }
        model.addAttribute("majorPreviews",     majorPreviews);
        model.addAttribute("sachPreview",       sanPhamService.getPreviewByLoaiSP("Sach"));
        model.addAttribute("vppPreview",        sanPhamService.getPreviewByLoaiSP("VanPhongPham"));
        model.addAttribute("quaTangPreview",    sanPhamService.getPreviewByLoaiSP("QuaTang"));
        return "products/index";
    }

    // CẤP 1 — Danh mục Sách
    @GetMapping("/sach")
    public String sachList(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(required = false) Integer danhMuc,
            @RequestParam(required = false) Integer tacGia,
            @RequestParam(required = false) Integer nxb,
            @RequestParam(required = false) Integer giaTu,
            @RequestParam(required = false) Integer giaDen,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Integer validDanhMuc = danhMuc;
        if (validDanhMuc != null && validDanhMuc > 0) {
            final Integer checkId = validDanhMuc;
            boolean valid = sanPhamService.getCategoriesByLoaiSP("Sach").stream()
                    .anyMatch(c -> c.getMaDanhMuc().equals(checkId));
            if (!valid) validDanhMuc = null;
        }

        if (tacGia != null && tacGia > 0 && validDanhMuc != null && validDanhMuc > 0) {
            if (!tacGiaService.isAuthorInCategory(tacGia, validDanhMuc)) {
                tacGia = null;
            }
        }

        if (giaTu != null && giaDen != null && giaTu > giaDen) {
            int temp = giaTu;
            giaTu = giaDen;
            giaDen = temp;
        }

        int pageSize = 12;
        Page<SanPham> productPage;
        if ((giaTu != null && giaTu > 0) || (giaDen != null && giaDen > 0)) {
            productPage = sanPhamService.searchAndFilter("Sach", q, validDanhMuc, tacGia, nxb, giaTu, giaDen, sort, page, pageSize);
        } else {
            productPage = sanPhamService.searchAndFilter("Sach", q, validDanhMuc, tacGia, nxb, sort, page, pageSize);
        }
        List<vn.bookstore.the4bookstore.entity.Shop> matchedShops = List.of();
        if (q != null && !q.isBlank()) {
            matchedShops = shopService.searchActiveShops(q);
        }
        model.addAttribute("matchedShops",    matchedShops);
        model.addAttribute("products",        productPage.getContent());
        model.addAttribute("currentPage",     productPage.getNumber());
        model.addAttribute("totalPages",      productPage.getTotalPages());
        model.addAttribute("totalElements",   productPage.getTotalElements());
        model.addAttribute("groupName",       "Sách");
        model.addAttribute("loaiSP",          "Sach");
        model.addAttribute("baseUrl",         "/san-pham/sach");
        model.addAttribute("keyword",         q);
        model.addAttribute("sortBy",          sort);
        model.addAttribute("categories",      sanPhamService.getCategoriesByLoaiSP("Sach"));
        model.addAttribute("categoriesSach",  sanPhamService.getCategoriesByLoaiSP("Sach"));
        model.addAttribute("categoriesVpp",   sanPhamService.getCategoriesByLoaiSP("VanPhongPham"));
        model.addAttribute("categoriesQuaTang", sanPhamService.getCategoriesByLoaiSP("QuaTang"));
        model.addAttribute("authors",         tacGiaService.getAuthorsByLoaiSP("Sach"));
        model.addAttribute("authorCategoryMap", tacGiaService.getAuthorCategoryMap());
        model.addAttribute("publishers",      List.of());
        model.addAttribute("selectedDanhMuc", validDanhMuc);
        model.addAttribute("selectedTacGia",  tacGia);
        model.addAttribute("selectedNxb",     nxb);
        model.addAttribute("giaTu",           giaTu);
        model.addAttribute("giaDen",          giaDen);
        return "products/list";
    }

    // CẤP 1 — Danh mục Văn phòng phẩm
    @GetMapping("/van-phong-pham")
    public String vppList(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(required = false) Integer danhMuc,
            @RequestParam(required = false) Integer giaTu,
            @RequestParam(required = false) Integer giaDen,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        buildGroupModel("VanPhongPham", "Văn Phòng Phẩm", "/san-pham/van-phong-pham", q, danhMuc, giaTu, giaDen, sort, page, model);
        return "products/list";
    }

    // CẤP 1 — Danh mục Quà tặng & Trang trí
    @GetMapping("/qua-tang")
    public String quaTangList(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(required = false) Integer danhMuc,
            @RequestParam(required = false) Integer giaTu,
            @RequestParam(required = false) Integer giaDen,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        buildGroupModel("QuaTang", "Quà Tặng & Trang Trí", "/san-pham/qua-tang", q, danhMuc, giaTu, giaDen, sort, page, model);
        return "products/list";
    }

    // CẤP 1 — Danh mục Board Game & Đồ chơi
    @GetMapping("/board-game")
    public String boardGameList(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(required = false) Integer danhMuc,
            @RequestParam(required = false) Integer giaTu,
            @RequestParam(required = false) Integer giaDen,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        buildGroupModel("BoardGame", "Board Game & Đồ Chơi", "/san-pham/board-game", q, danhMuc, giaTu, giaDen, sort, page, model);
        return "products/list";
    }

    // Chi tiết sản phẩm
    @GetMapping("/{id:[0-9]+}")
    public String productDetail(@PathVariable Integer id, Authentication auth, jakarta.servlet.http.HttpServletRequest request, Model model, RedirectAttributes ra) {
        Optional<SanPham> opt = sanPhamService.getById(id);
        if (opt.isEmpty()) {
            if (ra != null) ra.addFlashAttribute("errorMessage", "Sản phẩm không tồn tại!");
            return "redirect:/san-pham";
        }
        SanPham sanPham = opt.get();
        boolean isProductLocked = "BiKhoaBoiAdmin".equalsIgnoreCase(sanPham.getTrangThaiKhoa())
                || "Khoa".equalsIgnoreCase(sanPham.getTrangThaiKhoa())
                || "NgungBan".equalsIgnoreCase(sanPham.getTrangThai());
        boolean isShopLocked = sanPham.getShop() != null && !"HoatDong".equalsIgnoreCase(sanPham.getShop().getTrangThai());

        if (isProductLocked || isShopLocked) {
            if (ra != null) ra.addFlashAttribute("errorMessage", "Sản phẩm này hiện đang tạm ngừng kinh doanh hoặc gian hàng đang bị tạm khóa!");
            return "redirect:/san-pham";
        }

        KhachHang kh = getCurrentKhachHang(auth, request);
        boolean isFavorite = false;
        List<Integer> eligibleOrderIds = List.of();
        boolean hasPurchased = false;

        if (kh != null) {
            userInteractionService.recordView(kh, sanPham);
            isFavorite = userInteractionService.isFavorite(kh, id);
            eligibleOrderIds = userInteractionService.getEligibleOrderIdsForReview(kh, id);
            hasPurchased = userInteractionService.hasPurchasedProduct(kh, id);
        }

        List<vn.bookstore.the4bookstore.entity.DanhGia> reviews = userInteractionService.getReviewsForProduct(sanPham);
        Double avgRating = userInteractionService.getAverageRating(sanPham);

        model.addAttribute("sanPham", sanPham);
        model.addAttribute("relatedBooks", sanPhamService.getRelatedBooks(sanPham, 4));
        model.addAttribute("isFavorite", isFavorite);
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewCount", reviews.size());
        model.addAttribute("avgRating", avgRating);
        Map<String, Object> reviewStats = sanPhamService.getProductReviewStats(id);
        if (reviewStats != null) {
            model.addAttribute("reviewStats", reviewStats);
        }
        model.addAttribute("eligibleOrderIds", eligibleOrderIds);
        model.addAttribute("canReview", !eligibleOrderIds.isEmpty());
        model.addAttribute("hasPurchased", hasPurchased);
        model.addAttribute("isLoggedIn", kh != null);

        return "products/detail";
    }

    // API tìm kiếm sản phẩm phân trang qua Stored Procedure sp_TimKiemSachPhanTrang
    @GetMapping("/api/san-pham/tim-kiem-sp")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiSearchProductsWithProcedure(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer maDanhMuc,
            @RequestParam(required = false) Integer giaTu,
            @RequestParam(required = false) Integer giaDen,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        if (giaTu != null && giaDen != null && giaTu > giaDen) {
            int temp = giaTu;
            giaTu = giaDen;
            giaDen = temp;
        }
        List<Map<String, Object>> items = sanPhamService.searchBooksWithProcedure(q, maDanhMuc, giaTu, giaDen, page, size);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", items);
        response.put("page", page);
        response.put("size", size);
        return ResponseEntity.ok(response);
    }

    private void buildGroupModel(String loaiSP, String groupName, String baseUrl,
                                  String q, Integer maDanhMuc, Integer giaTu, Integer giaDen, String sort, int page, Model model) {
        if (giaTu != null && giaDen != null && giaTu > giaDen) {
            int temp = giaTu;
            giaTu = giaDen;
            giaDen = temp;
        }
        Integer validDanhMuc = maDanhMuc;
        if (validDanhMuc != null && validDanhMuc > 0) {
            final Integer checkId = validDanhMuc;
            boolean valid = sanPhamService.getCategoriesByLoaiSP(loaiSP).stream()
                    .anyMatch(c -> c.getMaDanhMuc().equals(checkId));
            if (!valid) validDanhMuc = null;
        }

        int pageSize = 12;
        Page<SanPham> productPage;
        if ((giaTu != null && giaTu > 0) || (giaDen != null && giaDen > 0)) {
            productPage = sanPhamService.searchAndFilter(loaiSP, q, validDanhMuc, null, null, giaTu, giaDen, sort, page, pageSize);
        } else {
            productPage = sanPhamService.searchAndFilter(loaiSP, q, validDanhMuc, sort, page, pageSize);
        }
        List<vn.bookstore.the4bookstore.entity.Shop> matchedShops = List.of();
        if (q != null && !q.isBlank()) {
            matchedShops = shopService.searchActiveShops(q);
        }
        model.addAttribute("matchedShops",    matchedShops);
        model.addAttribute("products",        productPage.getContent());
        model.addAttribute("currentPage",     productPage.getNumber());
        model.addAttribute("totalPages",      productPage.getTotalPages());
        model.addAttribute("totalElements",   productPage.getTotalElements());
        model.addAttribute("groupName",       groupName);
        model.addAttribute("loaiSP",          loaiSP);
        model.addAttribute("baseUrl",         baseUrl);
        model.addAttribute("keyword",         q);
        model.addAttribute("sortBy",          sort);
        model.addAttribute("selectedDanhMuc", validDanhMuc);
        model.addAttribute("categories",      sanPhamService.getCategoriesByLoaiSP(loaiSP));
        model.addAttribute("categoriesSach",  sanPhamService.getCategoriesByLoaiSP("Sach"));
        model.addAttribute("categoriesVpp",   sanPhamService.getCategoriesByLoaiSP("VanPhongPham"));
        model.addAttribute("categoriesQuaTang", sanPhamService.getCategoriesByLoaiSP("QuaTang"));
        model.addAttribute("authors",         List.of());
        model.addAttribute("publishers",      List.of());
        model.addAttribute("selectedTacGia",  null);
        model.addAttribute("selectedNxb",     null);
        model.addAttribute("giaTu",           giaTu);
        model.addAttribute("giaDen",          giaDen);
    }

    private String getGroupName(String loaiSP) {
        if ("Sach".equalsIgnoreCase(loaiSP)) return "Sách";
        if ("VanPhongPham".equalsIgnoreCase(loaiSP)) return "Văn Phòng Phẩm";
        if ("QuaTang".equalsIgnoreCase(loaiSP)) return "Quà Tặng & Trang Trí";
        if (danhMucService != null && loaiSP != null && !loaiSP.isBlank() && !"ALL".equalsIgnoreCase(loaiSP)) {
            return danhMucService.getMajorCategoryByMaLoai(loaiSP)
                    .map(vn.bookstore.the4bookstore.entity.DanhMucLon::getTenLoai)
                    .orElse("Tất Cả Sản Phẩm");
        }
        return "Tất Cả Sản Phẩm";
    }
}
