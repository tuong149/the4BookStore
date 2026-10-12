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
            // NẾU ĐƠN HÀNG ĐÃ GIAO / HOÀN TẤT THÌ KHÔNG ĐƯỢC HỦY, CHỈ ĐƯỢC HOÀN HÀNG
            if ("DaGiao".equalsIgnoreCase(oldStatus) || "HoanTat".equalsIgnoreCase(oldStatus)) {
                throw new IllegalStateException("Đơn hàng đã giao thành công không thể hủy, chỉ có thể thực hiện Hoàn hàng / Trả hàng!");
            }

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
        } else if ("DaHoan".equalsIgnoreCase(status) || "TraHangHoanTien".equalsIgnoreCase(status)) {
            // CHỈ ĐƯỢC HOÀN NẾU ĐƠN ĐÃ ĐƯỢC XÁC NHẬN
            if ("ChoXuLy".equalsIgnoreCase(oldStatus) || "DonHangMoi".equalsIgnoreCase(oldStatus) || "ChoDuyet".equalsIgnoreCase(oldStatus)) {
                throw new IllegalStateException("Đơn hàng chưa được xác nhận, chỉ có thể hủy đơn chứ không thể hoàn hàng!");
            }

            if (reason != null && !reason.isBlank()) {
                dh.setLyDoTraHang(reason);
            }

            // Xử lý hoàn tiền / trừ thu nhập: Nếu đã từng thu tiền hoặc hoàn tất thì trừ tiền (đổi sang DaHoanTien và xóa doanh thu shop)
            boolean daThuTien = "DaThanhToan".equalsIgnoreCase(dh.getTrangThaiThanhToan()) || "HoanTat".equalsIgnoreCase(oldStatus);
            if (daThuTien) {
                dh.setTrangThaiThanhToan("DaHoanTien");
                dh.setTienThucNhanShop(0);
                dh.setTienPhiSan(0);
            }

            // Hoàn lại số lượng tồn kho và giảm số lượng đã bán
            if (!"DaHoan".equalsIgnoreCase(oldStatus) && !"TraHangHoanTien".equalsIgnoreCase(oldStatus) && dh.getChiTietDonHangs() != null) {
                for (ChiTietDonHang ct : dh.getChiTietDonHangs()) {
                    if (ct.getSanPham() != null && ct.getSoLuong() != null) {
                        sanPhamRepository.increaseStock(ct.getSanPham().getMaSP(), ct.getSoLuong());
                        var sp = sanPhamRepository.findById(ct.getSanPham().getMaSP()).orElse(null);
                        if (sp != null && sp.getSoLuongDaBan() != null) {
                            sp.setSoLuongDaBan(Math.max(0, sp.getSoLuongDaBan() - ct.getSoLuong()));
                            sanPhamRepository.save(sp);
                        }
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
                case "DaHoan", "TraHangHoanTien" -> "Đã hoàn hàng thành công";
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

