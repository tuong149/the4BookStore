package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "DON_HANG")
@Data @NoArgsConstructor @AllArgsConstructor
public class DonHang {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maDH;
    @Column(nullable = false)
    private LocalDateTime ngayDat = LocalDateTime.now();
    
    @ManyToOne
    @JoinColumn(name = "maKH", nullable = false)
    private KhachHang khachHang;
    
    @Column(nullable = false, length = 20)
    private String soDienThoaiGiao;
    @Column(nullable = false, length = 255)
    private String diaChiGiao;
    @Column(nullable = false)
    private Integer tongTien = 0;
    
    @ManyToOne
    @JoinColumn(name = "maKM")
    private KhuyenMai khuyenMai;
    
    @Column(nullable = false)
    private Integer tienGiam = 0;
    @Column(nullable = false, length = 30)
    private String trangThai = "ChoXuLy";
    @Column(length = 500)
    private String lyDoTuChoi;
    @Column(length = 500)
    private String lyDoHuy;
    private LocalDateTime ngayXacNhan;
    private LocalDateTime ngayHoanThanh;
    
    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL)
    private List<ChiTietDonHang> chiTietDonHangs;
}