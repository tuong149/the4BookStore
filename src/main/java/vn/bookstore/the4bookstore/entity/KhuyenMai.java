package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "KHUYEN_MAI")
@Data @NoArgsConstructor @AllArgsConstructor
public class KhuyenMai {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maKM;
    @Column(nullable = false, length = 150)
    private String tenKM;
    @Column(unique = true, length = 50)
    private String maCode;
    @Column(nullable = false, length = 20)
    private String loaiGiam;
    @Column(nullable = false)
    private Integer giaTriGiam;
    private Integer giamToiDa;
    private Integer donToiThieu;
    private Integer soLuongToiDa;
    @Column(nullable = false)
    private Integer soLuongDaDung = 0;
    @Column(nullable = false)
    private LocalDateTime ngayBatDau;
    @Column(nullable = false)
    private LocalDateTime ngayKetThuc;
    @Column(nullable = false, length = 20)
    private String trangThai = "HoatDong";
}