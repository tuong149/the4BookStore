package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CHI_TIET_DON_HANG")
@IdClass(ChiTietDonHangId.class)
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ChiTietDonHang {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maDH")
    private DonHang donHang;
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maSP")
    private SanPham sanPham;
    
    @Column(nullable = false)
    private Integer soLuong;
    @Column(nullable = false)
    private Integer donGia;

    public ChiTietDonHang() {
    }

    public ChiTietDonHang(DonHang donHang, SanPham sanPham, Integer soLuong, Integer donGia) {
        this.donHang = donHang;
        this.sanPham = sanPham;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }
}