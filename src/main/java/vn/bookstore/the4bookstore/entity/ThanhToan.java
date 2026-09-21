package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "THANH_TOAN")
@Data @NoArgsConstructor @AllArgsConstructor
public class ThanhToan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maThanhToan;
    
    @ManyToOne
    @JoinColumn(name = "maDH", nullable = false)
    private DonHang donHang;
    
    @Column(nullable = false, length = 30)
    private String phuongThuc;
    @Column(nullable = false, length = 30)
    private String trangThai = "ChoThanhToan";
    @Column(nullable = false)
    private Integer soTien;
    private LocalDateTime ngayThanhToan;
    @Column(length = 100)
    private String maGiaoDich;
    @Column(length = 500)
    private String noiDung;
}