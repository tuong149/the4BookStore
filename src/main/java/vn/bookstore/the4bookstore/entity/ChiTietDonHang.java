package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CHI_TIET_DON_HANG")
@IdClass(ChiTietDonHangId.class)
@Data @NoArgsConstructor @AllArgsConstructor
public class ChiTietDonHang {
    @Id
    @ManyToOne
    @JoinColumn(name = "maDH")
    private DonHang donHang;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "maSP")
    private SanPham sanPham;
    
    @Column(nullable = false)
    private Integer soLuong;
    @Column(nullable = false)
    private Integer donGia;
}