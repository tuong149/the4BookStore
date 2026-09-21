package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CHI_TIET_PHIEU_NHAP")
@IdClass(ChiTietPhieuNhapId.class)
@Data @NoArgsConstructor @AllArgsConstructor
public class ChiTietPhieuNhap {
    @Id
    @ManyToOne
    @JoinColumn(name = "maPN")
    private PhieuNhap phieuNhap;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "maSP")
    private SanPham sanPham;
    
    @Column(nullable = false)
    private Integer soLuong;
    @Column(nullable = false)
    private Integer donGiaNhap;
}