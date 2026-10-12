package vn.bookstore.the4bookstore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.bookstore.the4bookstore.entity.*;
import vn.bookstore.the4bookstore.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CapHangService {

    private final YeuCauCapHangRepository yeuCauCapHangRepository;
    private final ChiTietCapHangRepository chiTietCapHangRepository;
    private final KhoRepository khoRepository;
    private final KhoHangRepository khoHangRepository;
    private final SanPhamRepository sanPhamRepository;
    private final ShopRepository shopRepository;

    @Transactional(readOnly = true)
    public Page<YeuCauCapHang> getRequestsByShop(Shop shop, Pageable pageable) {
        return yeuCauCapHangRepository.findByShopOrderByNgayTaoDesc(shop, pageable);
    }

    @Transactional(readOnly = true)
    public List<YeuCauCapHang> getAllRequestsWithDetails() {
        return yeuCauCapHangRepository.findAllWithShopAndKho();
    }

    @Transactional(readOnly = true)
    public YeuCauCapHang getRequestByIdWithDetails(Integer id) {
        return yeuCauCapHangRepository.findByIdWithDetails(id);
    }

    @Transactional
    public YeuCauCapHang createRequest(Shop shop, Integer maKho, String ghiChu, Map<Integer, Integer> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Danh sách mặt hàng yêu cầu cấp không được rỗng!");
        }

        Kho kho = khoRepository.findById(maKho)
                .orElseThrow(() -> new IllegalArgumentException("Kho tổng không tồn tại với ID: " + maKho));

        YeuCauCapHang request = new YeuCauCapHang();
        request.setShop(shop);
        request.setKho(kho);
        request.setGhiChu(ghiChu);
        request.setTrangThai("ChoDuyet");
        request.setNgayTao(LocalDateTime.now());

        YeuCauCapHang savedRequest = yeuCauCapHangRepository.save(request);

        for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
            Integer maSP = entry.getKey();
            Integer soLuong = entry.getValue();

            if (soLuong == null || soLuong <= 0) continue;

            SanPham sp = sanPhamRepository.findById(maSP)
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại: " + maSP));

            // Chỉ cho phép yêu cầu cấp hàng cho sản phẩm thuộc quyền quản lý của shop
            if (sp.getShop() != null && !sp.getShop().getMaShop().equals(shop.getMaShop())) {
                throw new IllegalArgumentException("Sản phẩm " + sp.getTenSP() + " không thuộc sở hữu của shop bạn!");
            }

            ChiTietCapHang chiTiet = new ChiTietCapHang();
            chiTiet.setYeuCauCapHang(savedRequest);
            chiTiet.setSanPham(sp);
            chiTiet.setSoLuongYeuCau(soLuong);
            chiTiet.setSoLuongDuyet(soLuong); // Mặc định duyệt bằng số lượng yêu cầu
            chiTietCapHangRepository.save(chiTiet);
            savedRequest.getChiTietCapHangs().add(chiTiet);
        }

        return savedRequest;
    }

    @Transactional
    public void approveAndDispatch(Integer maYeuCau, NhanVien adminOrManager) {
        YeuCauCapHang request = yeuCauCapHangRepository.findByIdWithDetails(maYeuCau);
        if (request == null) {
            throw new IllegalArgumentException("Yêu cầu cấp hàng không tồn tại: " + maYeuCau);
        }

        if (!"ChoDuyet".equalsIgnoreCase(request.getTrangThai())) {
            throw new IllegalStateException("Yêu cầu này đã được xử lý (trạng thái: " + request.getTrangThai() + ")!");
        }

        Kho kho = request.getKho();
        Shop shop = request.getShop();

        // Kiểm tra tồn kho tổng trước khi xuất
        for (ChiTietCapHang ct : request.getChiTietCapHangs()) {
            SanPham sp = ct.getSanPham();
            int soLuongXuat = ct.getSoLuongDuyet() != null ? ct.getSoLuongDuyet() : ct.getSoLuongYeuCau();

            KhoHang khoHang = khoHangRepository.findBySanPhamAndKho(sp, kho)
                    .orElseThrow(() -> new IllegalStateException("Sách \"" + sp.getTenSP() + "\" chưa có bản ghi trong kho tổng " + kho.getTenKho()));

            if (khoHang.getSoLuongTon() < soLuongXuat) {
                throw new IllegalStateException("Tồn kho tổng không đủ xuất cho cuốn \"" + sp.getTenSP() + 
                        "\" (Còn tồn: " + khoHang.getSoLuongTon() + ", Yêu cầu: " + soLuongXuat + ")");
            }

            // Trừ tồn kho tổng
            khoHang.setSoLuongTon(khoHang.getSoLuongTon() - soLuongXuat);
            khoHang.setNgayCapNhat(LocalDateTime.now());
            khoHangRepository.save(khoHang);

            // Tăng tồn kho của gian hàng (Vendor Shop Stock)
            sp.setSoLuongTon((sp.getSoLuongTon() != null ? sp.getSoLuongTon() : 0) + soLuongXuat);
            if ("HetHang".equalsIgnoreCase(sp.getTrangThai()) && sp.getSoLuongTon() > 0) {
                sp.setTrangThai("DangBan");
            }
            sanPhamRepository.save(sp);
        }

        request.setTrangThai("DaGiao");
        request.setNgayDuyet(LocalDateTime.now());
        request.setNhanVienDuyet(adminOrManager);
        yeuCauCapHangRepository.save(request);
        log.info("Duyet va xuat kho cap hang thanh cong cho ma yeu cau: {}, Shop: {}", maYeuCau, shop.getTenShop());
    }

    @Transactional
    public void rejectRequest(Integer maYeuCau, NhanVien adminOrManager, String lyDoTuChoi) {
        YeuCauCapHang request = yeuCauCapHangRepository.findById(maYeuCau)
                .orElseThrow(() -> new IllegalArgumentException("Yêu cầu cấp hàng không tồn tại: " + maYeuCau));

        if (!"ChoDuyet".equalsIgnoreCase(request.getTrangThai())) {
            throw new IllegalStateException("Yêu cầu này đã được xử lý!");
        }

        request.setTrangThai("TuChoi");
        request.setNgayDuyet(LocalDateTime.now());
        request.setNhanVienDuyet(adminOrManager);
        request.setLyDoTuChoi(lyDoTuChoi != null ? lyDoTuChoi : "Tổng bộ từ chối cấp hàng.");
        yeuCauCapHangRepository.save(request);
    }
}
