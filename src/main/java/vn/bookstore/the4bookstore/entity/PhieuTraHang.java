package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PHIEU_TRA_HANG")
@Data @NoArgsConstructor @AllArgsConstructor
public class PhieuTraHang {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maPhieuTra;
    
    @ManyToOne
    @JoinColumn(name = "maNCC", nullable = false)
    private NhaCungCap nhaCungCap;
    
    @ManyToOne
    @JoinColumn(name = "maNV", nullable = false)
    private NhanVien nhanVien;
    
    @Column(nullable = false)
    private LocalDateTime ngayTra = LocalDateTime.now();
    @Column(nullable = false, length = 500)
    private String lyDo;
    @Column(nullable = false, length = 20)
    private String trangThai = "ChoDuyet";
    @Column(length = 500)
    private String ghiChu;
}