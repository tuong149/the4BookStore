package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "HOA_DON")
@Data @NoArgsConstructor @AllArgsConstructor
public class HoaDon {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maHD;
    
    @OneToOne
    @JoinColumn(name = "maDH", nullable = false, unique = true)
    private DonHang donHang;
    
    @ManyToOne
    @JoinColumn(name = "maNV", nullable = false)
    private NhanVien nhanVien;
    
    @Column(nullable = false)
    private LocalDateTime ngayLap = LocalDateTime.now();
    @Column(nullable = false)
    private Integer tongTienThanhToan;
    @Column(nullable = false, length = 20)
    private String trangThai = "ChuaThanhToan";
}