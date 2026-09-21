package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CHI_TIET_KIEM_KE")
@IdClass(ChiTietKiemKeId.class)
@Data @NoArgsConstructor @AllArgsConstructor
public class ChiTietKiemKe {
    @Id
    @ManyToOne
    @JoinColumn(name = "maPhieuKiemKe")
    private PhieuKiemKe phieuKiemKe;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "maSP")
    private SanPham sanPham;
    
    @Column(nullable = false)
    private Integer soLuongHeThong;
    @Column(nullable = false)
    private Integer soLuongThucTe;
    @Column(length = 500)
    private String lyDo;
}