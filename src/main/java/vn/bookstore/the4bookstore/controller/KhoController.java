package vn.bookstore.the4bookstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.bookstore.the4bookstore.dto.CreatePhieuKeRequest;
import vn.bookstore.the4bookstore.dto.CreatePhieuNhapRequest;
import vn.bookstore.the4bookstore.repository.NhaCungCapRepository;
import vn.bookstore.the4bookstore.repository.SanPhamRepository;
import vn.bookstore.the4bookstore.service.KhoService;

import org.springframework.security.core.Authentication;

import java.util.Map;

@Controller
@RequestMapping("/admin/kho")
public class KhoController {

    private final KhoService khoService;
    private final SanPhamRepository sachRepository;
    private final NhaCungCapRepository nhaCungCapRepository;
    private final vn.bookstore.the4bookstore.repository.NhanVienRepository nhanVienRepository;
    private final vn.bookstore.the4bookstore.repository.KhoHangRepository khoHangRepository;

    public KhoController(KhoService khoService,
                         SanPhamRepository sachRepository,
                         NhaCungCapRepository nhaCungCapRepository,
                         vn.bookstore.the4bookstore.repository.NhanVienRepository nhanVienRepository,
                         vn.bookstore.the4bookstore.repository.KhoHangRepository khoHangRepository) {
        this.khoService = khoService;
        this.sachRepository = sachRepository;
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.khoHangRepository = khoHangRepository;
    }

    private boolean isManagerOrAdmin(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;
        return auth.getAuthorities().stream().anyMatch(a -> {
            String role = a.getAuthority().toUpperCase();
            return role.equals("ROLE_ADMIN") || role.equals("ROLE_MANAGER") || role.equals("ROLE_QUANLY");
        });
    }

    @GetMapping
    public String listKho(Model model) {
        model.addAttribute("khos", khoService.getAllKho());
        return "admin/kho/list";
    }

    @GetMapping("/{id}/detail")
    public String khoDetail(@PathVariable Integer id, Model model) {
        vn.bookstore.the4bookstore.entity.Kho kho = khoService.getKhoById(id);
        model.addAttribute("kho", kho);
        model.addAttribute("khoHangs", khoHangRepository.findByKho(kho));
        return "admin/kho/detail";
    }

    @PostMapping("/save")
    public String saveKho(@RequestParam(required = false) Integer maKho, 
                          @RequestParam String tenKho, 
                          @RequestParam String diaChi,
                          @RequestParam(required = false) String soDienThoai,
                          @RequestParam(required = false) String ghiChu) {
        vn.bookstore.the4bookstore.entity.Kho k = new vn.bookstore.the4bookstore.entity.Kho();
        k.setTenKho(tenKho);
        k.setDiaChi(diaChi);
        k.setSoDienThoai(soDienThoai);
        k.setGhiChu(ghiChu);
        if (maKho != null) {
            khoService.updateKho(maKho, k);
        } else {
            khoService.createKho(k);
        }
        return "redirect:/admin/kho";
    }

    @PostMapping("/delete/{id}")
    public String deleteKho(@PathVariable Integer id) {
        try {
            khoService.deleteKho(id);
        } catch (Exception e) {}
        return "redirect:/admin/kho";
    }

    @GetMapping("/phieu-nhap")
    public String listPhieuNhap(Model model) {
        model.addAttribute("phieuNhaps", khoService.getAllPhieuNhap());
        return "admin/kho/phieu-nhap";
    }

    @GetMapping("/phieu-nhap/{id}")
    public String phieuNhapDetail(@PathVariable Integer id, Model model) {
        vn.bookstore.the4bookstore.entity.PhieuNhap pn = khoService.getPhieuNhapById(id);
        model.addAttribute("phieuNhap", pn);
        model.addAttribute("chiTiets", khoService.getChiTietPhieuNhap(id));
        return "admin/kho/phieu-nhap-detail";
    }

    @GetMapping("/phieu-nhap/create")
    public String createPhieuNhapForm(Model model) {
        model.addAttribute("khos", khoService.getAllKho());
        model.addAttribute("nhaCungCaps", nhaCungCapRepository.findAll());
        model.addAttribute("sachs", sachRepository.findAll());
        model.addAttribute("nhanViens", khoService.getDanhSachNhanVienHoatDong());
        return "admin/kho/phieu-nhap-form";
    }

    @PostMapping("/phieu-nhap")
    @ResponseBody
    public ResponseEntity<?> createPhieuNhap(@RequestBody CreatePhieuNhapRequest request) {
        try {
            var pn = khoService.createPhieuNhap(request.getKhoId(), request.getNhaCungCapId(), request.getNhanVienId(), request.getChiTiets());
            return ResponseEntity.ok(Map.of("id", pn.getMaPN(), "success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/phieu-nhap/{id}/confirm")
    @ResponseBody
    public ResponseEntity<?> confirmPhieuNhap(@PathVariable Long id, Authentication auth) {
        if (!isManagerOrAdmin(auth)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "error", "Chỉ Quản trị viên (Admin) hoặc Quản lý (Manager) mới có quyền duyệt phiếu nhập kho!"));
        }
        try {
            khoService.confirmPhieuNhap(id.intValue());
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/phieu-ke")
    public String listPhieuKe(Model model) {
        model.addAttribute("phieuKes", khoService.getAllPhieuKe());
        return "admin/kho/phieu-ke";
    }

    @GetMapping("/phieu-ke/{id}")
    public String phieuKeDetail(@PathVariable Integer id, Model model) {
        vn.bookstore.the4bookstore.entity.PhieuKiemKe pk = khoService.getPhieuKeById(id);
        model.addAttribute("phieuKe", pk);
        model.addAttribute("chiTiets", khoService.getChiTietPhieuKe(id));
        return "admin/kho/phieu-ke-detail";
    }

    @GetMapping("/phieu-ke/create")
    public String createPhieuKeForm(Model model) {
        model.addAttribute("khos", khoService.getAllKho());
        model.addAttribute("sachs", sachRepository.findAll());
        model.addAttribute("nhanViens", khoService.getDanhSachNhanVienHoatDong());
        return "admin/kho/phieu-ke-form";
    }

    @PostMapping("/phieu-ke")
    @ResponseBody
    public ResponseEntity<?> createPhieuKe(@RequestBody CreatePhieuKeRequest request) {
        try {
            var pk = khoService.createPhieuKe(request.getKhoId(), request.getNhanVienId(), request.getChiTiets(), request.getGhiChu());
            return ResponseEntity.ok(Map.of("id", pk.getMaPhieuKiemKe(), "success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/phieu-ke/{id}/confirm")
    @ResponseBody
    public ResponseEntity<?> confirmPhieuKe(@PathVariable Long id, Authentication auth) {
        if (!isManagerOrAdmin(auth)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "error", "Chỉ Quản trị viên (Admin) hoặc Quản lý (Manager) mới có quyền duyệt / cân hàng phiếu kiểm kê!"));
        }
        try {
            khoService.confirmPhieuKe(id.intValue());
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/warnings")
    public String lowStockWarnings(@RequestParam Long khoId, Model model) {
        model.addAttribute("warnings", khoService.getLowStockWarnings(khoId.intValue()));
        model.addAttribute("kho", khoService.getKhoById(khoId.intValue()));
        return "admin/kho/warnings";
    }

    @GetMapping("/api/products-by-ncc")
    @ResponseBody
    public ResponseEntity<?> getProductsByNCC(@RequestParam Integer nccId) {
        java.util.List<vn.bookstore.the4bookstore.entity.SanPham> list = sachRepository.findByNhaCungCapId(nccId);
        java.util.List<Map<String, Object>> result = list.stream().map(sp -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("maSP", sp.getMaSP());
            map.put("tenSP", sp.getTenSP());
            map.put("isbn", sp.getISBN() != null ? sp.getISBN() : "");
            map.put("giaBan", sp.getGiaBan() != null ? sp.getGiaBan() : 0);
            return map;
        }).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/products-by-kho")
    @ResponseBody
    public ResponseEntity<?> getProductsByKho(@RequestParam Integer khoId) {
        java.util.List<vn.bookstore.the4bookstore.entity.KhoHang> list = khoHangRepository.findByKhoIdWithSanPham(khoId);
        java.util.List<Map<String, Object>> result = list.stream().map(kh -> {
            Map<String, Object> map = new java.util.HashMap<>();
            vn.bookstore.the4bookstore.entity.SanPham sp = kh.getSanPham();
            map.put("maSP", sp.getMaSP());
            map.put("tenSP", sp.getTenSP());
            map.put("isbn", sp.getISBN() != null ? sp.getISBN() : "");
            map.put("soLuongTon", kh.getSoLuongTon() != null ? kh.getSoLuongTon() : 0);
            return map;
        }).toList();
        return ResponseEntity.ok(result);
    }
}