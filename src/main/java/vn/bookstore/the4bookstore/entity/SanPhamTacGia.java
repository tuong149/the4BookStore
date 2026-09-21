package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SAN_PHAM_TAC_GIA")
@IdClass(SanPhamTacGiaId.class)
@Data @NoArgsConstructor @AllArgsConstructor
public class SanPhamTacGia {
    @Id
    @ManyToOne
    @JoinColumn(name = "maSP")
    private SanPham sanPham;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "maTacGia")
    private TacGia tacGia;
    
    private Integer thuTuTacGia;
}