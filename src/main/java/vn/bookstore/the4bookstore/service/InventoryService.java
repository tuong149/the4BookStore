package vn.bookstore.the4bookstore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.bookstore.the4bookstore.entity.ChiTietKiemKe;
import vn.bookstore.the4bookstore.entity.PhieuKiemKe;
import vn.bookstore.the4bookstore.entity.SanPham;
import vn.bookstore.the4bookstore.entity.Shop;
import vn.bookstore.the4bookstore.repository.ChiTietKiemKeRepository;
import vn.bookstore.the4bookstore.repository.PhieuKiemKeRepository;
import vn.bookstore.the4bookstore.repository.SanPhamRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final PhieuKiemKeRepository phieuKiemKeRepository;
    private final ChiTietKiemKeRepository chiTietKiemKeRepository;
    private final SanPhamRepository sanPhamRepository;

    @Transactional
    public PhieuKiemKe createInventoryAudit(Shop shop,
                                           String ghiChu,
                                           List<Integer> productIds,
                                           List<Integer> soLuongThucTes,
                                           List<String> lyDos) {
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Không có sản phẩm nào được chọn để kiểm kê!");
        }

        // Tạo phiếu kiểm kê cho Shop
        PhieuKiemKe pkk = new PhieuKiemKe();
        pkk.setShop(shop);
        pkk.setNgayKiemKe(LocalDateTime.now());
        pkk.setNgayPheDuyet(LocalDateTime.now());
        pkk.setTrangThai("DaCanBang"); // Đúng theo CHECK constraint: ('DaHuy', 'DaCanBang', 'DangKiemKe')
        pkk.setGhiChu(ghiChu != null && !ghiChu.isBlank() ? ghiChu.trim() : "Biên bản kiểm kê định kỳ gian hàng");
        pkk = phieuKiemKeRepository.save(pkk);

        List<ChiTietKiemKe> chiTiets = new ArrayList<>();

        for (int i = 0; i < productIds.size(); i++) {
            Integer pid = productIds.get(i);
            Integer actualQty = (soLuongThucTes != null && i < soLuongThucTes.size()) ? soLuongThucTes.get(i) : null;
            String reason = (lyDos != null && i < lyDos.size()) ? lyDos.get(i) : "";

            if (pid != null && actualQty != null && actualQty >= 0) {
                SanPham sp = sanPhamRepository.findById(pid).orElse(null);
                if (sp != null && sp.getShop() != null && sp.getShop().getMaShop().equals(shop.getMaShop())) {
                    int systemQty = sp.getSoLuongTon() != null ? sp.getSoLuongTon() : 0;

                    ChiTietKiemKe ct = new ChiTietKiemKe();
                    ct.setPhieuKiemKe(pkk);
                    ct.setSanPham(sp);
                    ct.setSoLuongHeThong(systemQty);
                    ct.setSoLuongThucTe(actualQty);
                    ct.setChenhLech(actualQty - systemQty);
                    ct.setLyDo(reason != null && !reason.isBlank() ? reason.trim() : (actualQty != systemQty ? "Cân bằng theo kiểm đếm thực tế" : "Khớp số lượng"));
                    chiTiets.add(ct);

                    // Cân bằng trực tiếp vào số lượng tồn của sản phẩm thuộc Shop
                    sp.setSoLuongTon(actualQty);
                    sanPhamRepository.save(sp);
                }
            }
        }

        if (!chiTiets.isEmpty()) {
            chiTietKiemKeRepository.saveAll(chiTiets);
        }

        log.info("[INVENTORY] Đã lập phiếu kiểm kê #{} cho Shop #{} ({}) và cân bằng {} sản phẩm",
                pkk.getMaPhieuKiemKe(), shop.getMaShop(), shop.getTenShop(), chiTiets.size());

        return pkk;
    }
}
