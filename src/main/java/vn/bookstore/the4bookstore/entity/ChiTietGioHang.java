package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CHI_TIET_GIO_HANG")
@IdClass(ChiTietGioHangId.class)
@Data @NoArgsConstructor @AllArgsConstructor
public class ChiTietGioHang {
    @Id
    @ManyToOne
    @JoinColumn(name = "maGioHang")
    private GioHang gioHang;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "maSP")
    private SanPham sanPham;
    
    @Column(nullable = false)
    private Integer soLuong;
}