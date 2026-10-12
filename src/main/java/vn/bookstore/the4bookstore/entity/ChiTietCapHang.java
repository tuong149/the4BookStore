package vn.bookstore.the4bookstore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chi_tiet_cap_hang", indexes = {
    @Index(name = "idx_ctch_yeucau", columnList = "ma_yeu_cau"),
    @Index(name = "idx_ctch_sanpham", columnList = "masp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietCapHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_chi_tiet")
    private Integer maChiTiet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_yeu_cau", nullable = false)
    private YeuCauCapHang yeuCauCapHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "masp", nullable = false)
    private SanPham sanPham;

    @Column(name = "so_luong_yeu_cau", nullable = false)
    private Integer soLuongYeuCau;

    @Column(name = "so_luong_duyet")
    private Integer soLuongDuyet;
}
