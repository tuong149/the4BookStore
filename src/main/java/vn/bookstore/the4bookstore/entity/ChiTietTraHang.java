package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CHI_TIET_TRA_HANG")
@IdClass(ChiTietTraHangId.class)
@Data @NoArgsConstructor @AllArgsConstructor
public class ChiTietTraHang {
    @Id
    @ManyToOne
    @JoinColumn(name = "maPhieuTra")
    private PhieuTraHang phieuTraHang;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "maSP")
    private SanPham sanPham;
    
    @Column(nullable = false)
    private Integer soLuong;
    @Column(nullable = false)
    private Integer donGia;
}