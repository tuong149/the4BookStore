package vn.bookstore.the4bookstore.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.bookstore.the4bookstore.dto.CreatePhieuKeRequest;
import vn.bookstore.the4bookstore.dto.CreatePhieuNhapRequest;
import vn.bookstore.the4bookstore.dto.PhieuKeCTDTO;
import vn.bookstore.the4bookstore.dto.PhieuNhapCTDTO;
import vn.bookstore.the4bookstore.entity.*;
import vn.bookstore.the4bookstore.repository.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class KhoService {

    @Autowired private KhoRepository khoRepository;
    @Autowired private KhoHangRepository khoHangRepository;
    @Autowired private PhieuNhapRepository phieuNhapRepository;
    @Autowired private PhieuKiemKeRepository phieuKiemKeRepository;
    @Autowired private SanPhamRepository sanPhamRepository;
    @Autowired private NhaCungCapRepository nhaCungCapRepository;
    @Autowired private ChiTietPhieuNhapRepository chiTietPhieuNhapRepository;
    @Autowired private ChiTietKiemKeRepository chiTietKiemKeRepository;
    @Autowired private NhanVienRepository nhanVienRepository;

    // ===== KHO =====

    public List<Kho> getAllKho() {
        return khoRepository.findAll();
    }

    public Kho getKhoById(Integer id) {
        return khoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kho với mã: " + id));
    }

    public Kho createKho(Kho kho) {
        kho.setNgayTao(LocalDateTime.now());
        return khoRepository.save(kho);
    }

    public Kho updateKho(Integer id, Kho khoDetails) {
        Kho kho = getKhoById(id);
        kho.setTenKho(khoDetails.getTenKho());
        kho.setDiaChi(khoDetails.getDiaChi());
        return khoRepository.save(kho);
    }

    public void deleteKho(Integer id) {
        // Can't delete if it has inventory or bills. Basic check or let DB handle FK constraint.
        khoRepository.deleteById(id);
    }
    // ===== PHIEU NHAP =====

    public PhieuNhap createPhieuNhap(Long khoId, Long nhaCungCapId, Long nhanVienId, List<PhieuNhapCTDTO> chiTiets) {
        Kho kho = getKhoById(khoId.intValue());
        NhaCungCap ncc = nhaCungCapRepository.findById(nhaCungCapId.intValue())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy NCC với mã: " + nhaCungCapId));

        PhieuNhap pn = new PhieuNhap();
        pn.setKho(kho);
        pn.setNhaCungCap(ncc);
        pn.setNgayNhap(LocalDateTime.now());
        pn.setTrangThai("ChuaDuyet");
        pn.setTongTien(0);
        NhanVien nv = nhanVienRepository.findById(nhanVienId.intValue())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy Nhân viên với mã: " + nhanVienId));
        pn.setNhanVien(nv);
        phieuNhapRepository.save(pn);

        int tongTien = 0;
        for (PhieuNhapCTDTO ct : chiTiets) {
            SanPham sp = sanPhamRepository.findById(ct.getSachId().intValue())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm: " + ct.getSachId()));

            ChiTietPhieuNhap ctpn = new ChiTietPhieuNhap();
            ctpn.setPhieuNhap(pn);
            ctpn.setSanPham(sp);
            ctpn.setSoLuong(ct.getSoLuong());
            ctpn.setDonGiaNhap(ct.getGiaNhap().intValue());
            chiTietPhieuNhapRepository.save(ctpn);

            tongTien += ct.getSoLuong() * ct.getGiaNhap().intValue();
        }

        pn.setTongTien(tongTien);
        return phieuNhapRepository.save(pn);
    }

    public PhieuNhap confirmPhieuNhap(Integer phieuNhapId) {
        PhieuNhap pn = phieuNhapRepository.findById(phieuNhapId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu nhập: " + phieuNhapId));

        if (!"ChuaDuyet".equals(pn.getTrangThai())) {
            throw new RuntimeException("Phiếu nhập đã được xác nhận hoặc không hợp lệ");
        }

        List<ChiTietPhieuNhap> chiTiets = chiTietPhieuNhapRepository.findByPhieuNhap(pn);
        for (ChiTietPhieuNhap ct : chiTiets) {
            KhoHang khoHang = khoHangRepository.findBySanPhamAndKho(ct.getSanPham(), pn.getKho())
                    .orElse(null);

            if (khoHang == null) {
                khoHang = new KhoHang();
                khoHang.setKho(pn.getKho());
                khoHang.setSanPham(ct.getSanPham());
                khoHang.setSoLuongTon(0);
            }

            khoHang.setSoLuongTon(khoHang.getSoLuongTon() + ct.getSoLuong());
            khoHang.setNgayCapNhat(LocalDateTime.now());
            khoHangRepository.save(khoHang);
        }

        pn.setTrangThai("DaNhap");
        return phieuNhapRepository.save(pn);
    }

    public List<PhieuNhap> getAllPhieuNhap() {
        return phieuNhapRepository.findAll();
    }

    public List<PhieuNhap> getPhieuNhapByKho(Integer khoId) {
        Kho kho = getKhoById(khoId);
        return phieuNhapRepository.findByKho(kho);
    }

    public void deletePhieuNhap(Integer phieuNhapId) {
        PhieuNhap pn = phieuNhapRepository.findById(phieuNhapId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu nhập: " + phieuNhapId));

        if ("DaNhap".equals(pn.getTrangThai())) {
            throw new RuntimeException("Không thể xóa phiếu nhập đã xác nhận");
        }

        List<ChiTietPhieuNhap> chiTiets = chiTietPhieuNhapRepository.findByPhieuNhap(pn);
        chiTietPhieuNhapRepository.deleteAll(chiTiets);
        phieuNhapRepository.delete(pn);
    }

    // ===== PHIEU KIEM KE =====

    public PhieuKiemKe createPhieuKe(Long khoId, Long nhanVienId, List<PhieuKeCTDTO> chiTiets, String ghiChu) {
        Kho kho = getKhoById(khoId.intValue());

        PhieuKiemKe pk = new PhieuKiemKe();
        pk.setKho(kho);
        pk.setNgayKiemKe(LocalDateTime.now());
        pk.setTrangThai("ChoDuyet");
        pk.setGhiChu(ghiChu);
        NhanVien nv = nhanVienRepository.findById(nhanVienId.intValue())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy Nhân viên với mã: " + nhanVienId));
        pk.setNhanVien(nv);
        phieuKiemKeRepository.save(pk);

        for (PhieuKeCTDTO ct : chiTiets) {
            SanPham sp = sanPhamRepository.findById(ct.getSachId().intValue())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm: " + ct.getSachId()));

            KhoHang khoHang = khoHangRepository.findBySanPhamAndKho(sp, kho).orElse(null);
            int soLuongHeThong = (khoHang != null) ? khoHang.getSoLuongTon() : 0;

            ChiTietKiemKe ctkk = new ChiTietKiemKe();
            ctkk.setPhieuKiemKe(pk);
            ctkk.setSanPham(sp);
            ctkk.setSoLuongHeThong(soLuongHeThong);
            ctkk.setSoLuongThucTe(ct.getSoLuongThucTe());
            chiTietKiemKeRepository.save(ctkk);
        }

        return pk;
    }

    public PhieuKiemKe confirmPhieuKe(Integer phieuKeId) {
        PhieuKiemKe pk = phieuKiemKeRepository.findById(phieuKeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu kiểm kê: " + phieuKeId));

        if (!"ChoDuyet".equals(pk.getTrangThai())) {
            throw new RuntimeException("Phiếu kiểm kê đã được xác nhận hoặc không hợp lệ");
        }

        List<ChiTietKiemKe> chiTiets = chiTietKiemKeRepository.findByPhieuKiemKe(pk);
        for (ChiTietKiemKe ct : chiTiets) {
            KhoHang khoHang = khoHangRepository.findBySanPhamAndKho(ct.getSanPham(), pk.getKho())
                    .orElse(null);

            if (khoHang == null) {
                khoHang = new KhoHang();
                khoHang.setKho(pk.getKho());
                khoHang.setSanPham(ct.getSanPham());
            }

            khoHang.setSoLuongTon(ct.getSoLuongThucTe());
            khoHang.setNgayCapNhat(LocalDateTime.now());
            khoHangRepository.save(khoHang);
        }

        pk.setTrangThai("DaDuyet");
        return phieuKiemKeRepository.save(pk);
    }

    public List<PhieuKiemKe> getAllPhieuKe() {
        return phieuKiemKeRepository.findAll();
    }

    public List<PhieuKiemKe> getPhieuKeByKho(Integer khoId) {
        Kho kho = getKhoById(khoId);
        return phieuKiemKeRepository.findByKho(kho);
    }

    // ===== TON KHO =====

    public List<KhoHang> getLowStockWarnings(Integer khoId) {
        Kho kho = getKhoById(khoId);
        List<KhoHang> allInKho = khoHangRepository.findByKho(kho);
        return allInKho.stream()
                .filter(kh -> kh.getSoLuongTon() < kh.getMucToiThieu())
                .toList();
    }

    public List<KhoHang> getAlmostOutOfStock(Integer khoId, Integer threshold) {
        Kho kho = getKhoById(khoId);
        List<KhoHang> allInKho = khoHangRepository.findByKho(kho);
        return allInKho.stream()
                .filter(kh -> kh.getSoLuongTon() <= threshold)
                .toList();
    }
}
