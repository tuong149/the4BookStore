package vn.bookstore.the4bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotificationDTO {
    private Integer maDH;
    private String tenKhachHang;
    private String soDienThoai;
    private String diaChiGiao;
    private Integer tongTien;
    private Integer soLuongMon;
    private String trangThai;
    private LocalDateTime ngayDat;
    private String message;
}
