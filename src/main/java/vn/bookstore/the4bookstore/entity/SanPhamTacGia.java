package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SAN_PHAM_TAC_GIA")
@IdClass(SanPhamTacGiaId.class)
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SanPhamTacGia {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maSP")
    private SanPham sanPham;
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maTacGia")
    private TacGia tacGia;
    
    @Column(name = "thu_tu_tac_gia")
    private Integer thuTuTacGia;

    public SanPhamTacGia() {}

    public SanPhamTacGia(SanPham sanPham, TacGia tacGia, Integer thuTuTacGia) {
        this.sanPham = sanPham;
        this.tacGia = tacGia;
        this.thuTuTacGia = thuTuTacGia;
    }
}
