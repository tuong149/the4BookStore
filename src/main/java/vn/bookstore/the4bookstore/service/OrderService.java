package vn.bookstore.the4bookstore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.bookstore.the4bookstore.dto.OrderNotificationDTO;
import vn.bookstore.the4bookstore.entity.ChiTietDonHang;
import vn.bookstore.the4bookstore.entity.DonHang;
import vn.bookstore.the4bookstore.repository.DonHangRepository;
import vn.bookstore.the4bookstore.repository.SanPhamRepository;

import java.time.LocalDateTime;

@Service
public class OrderService {
    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void updateStatus(Long id, String status) {
        updateStatus(id, status, null);
    }

    @Transactional
    public void updateStatus(Long id, String status, String reason) {
        DonHang dh = donHangRepository.findById(id.intValue())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + id));

        String oldStatus = dh.getTrangThai();
        dh.setTrangThai(status);

        if ("DaGiao".equals(status) || "HoanTat".equals(status)) {
            if (dh.getNgayHoanThanh() == null) {
                dh.setNgayHoanThanh(LocalDateTime.now());
            }
        } else if ("DaXacNhan".equals(status)) {
            if (dh.getNgayXacNhan() == null) {
                dh.setNgayXacNhan(LocalDateTime.now());
            }
            if (dh.getMaVanDon() == null || dh.getMaVanDon().isBlank()) {
                String prefix = "GHN";
                if (dh.getNhaVanChuyen() != null && dh.getNhaVanChuyen().getTenNvc() != null) {
                    String ten = dh.getNhaVanChuyen().getTenNvc().toUpperCase();
                    if (ten.contains("GHTK")) prefix = "GHTK";
                    else if (ten.contains("VIETTEL")) prefix = "VTPOST";
                    else if (ten.contains("VNPOST")) prefix = "VNPOST";
                    else prefix = "GHN";
                }
                dh.setMaVanDon(prefix + "-" + dh.getMaDH() + "-" + (System.currentTimeMillis() % 100000));
            }
        } else if ("DaHuy".equals(status) || "Huy".equals(status)) {
            if (reason != null && !reason.isBlank()) {
                dh.setLyDoHuy(reason);
            }
            // Hoàn lại số lượng tồn kho nếu đơn chuyển từ trạng thái chưa hủy sang hủy
            if (!"DaHuy".equalsIgnoreCase(oldStatus) && !"Huy".equalsIgnoreCase(oldStatus) && dh.getChiTietDonHangs() != null) {
                for (ChiTietDonHang ct : dh.getChiTietDonHangs()) {
                    if (ct.getSanPham() != null && ct.getSoLuong() != null) {
                        sanPhamRepository.increaseStock(ct.getSanPham().getMaSP(), ct.getSoLuong());
                    }
                }
            }
        }
        donHangRepository.save(dh);

        // Gửi thông báo WebSocket cập nhật trạng thái đơn hàng (cho Admin & Client)
        try {
            String statusText = switch (status) {
                case "DaXacNhan" -> "Đã được xác nhận";
                case "DaLayHang" -> "Đã bàn giao cho đơn vị vận chuyển";
                case "DangGiao" -> "Đang được giao đến bạn";
                case "DaGiao" -> "Đã giao hàng thành công (chờ xác nhận)";
                case "HoanTat" -> "Đã hoàn tất thành công";
                case "DaHuy", "Huy" -> "Đã bị hủy";
                default -> status;
            };

            OrderNotificationDTO noti = OrderNotificationDTO.builder()
                    .maDH(dh.getMaDH())
                    .tenKhachHang(dh.getKhachHang() != null ? dh.getKhachHang().getHoTen() : "Khách hàng")
                    .soDienThoai(dh.getSoDienThoaiGiao())
                    .diaChiGiao(dh.getDiaChiGiao())
                    .tongTien(dh.getTongTien())
                    .trangThai(dh.getTrangThai())
                    .ngayDat(dh.getNgayDat())
                    .message("Đơn hàng #" + dh.getMaDH() + " " + statusText)
                    .build();

            // Broadcast tới kênh chung admin và kênh riêng của khách hàng
            messagingTemplate.convertAndSend("/topic/admin/orders", noti);
            if (dh.getKhachHang() != null && dh.getKhachHang().getMaKH() != null) {
                messagingTemplate.convertAndSend("/topic/user/" + dh.getKhachHang().getMaKH() + "/orders", noti);
            }
        } catch (Exception ignored) {
        }
    }
}

