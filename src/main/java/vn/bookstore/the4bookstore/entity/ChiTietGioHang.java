package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CHI_TIET_GIO_HANG")
@IdClass(ChiTietGioHangId.class)
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ChiTietGioHang {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maGioHang")
    private GioHang gioHang;
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maSP")
    private SanPham sanPham;
    
    @Column(nullable = false)
    private Integer soLuong;

    public ChiTietGioHang() {
    }

    public ChiTietGioHang(GioHang gioHang, SanPham sanPham, Integer soLuong) {
        this.gioHang = gioHang;
        this.sanPham = sanPham;
        this.soLuong = soLuong;
    }
}