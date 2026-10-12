package vn.bookstore.the4bookstore.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "yeu_cau_cap_hang", indexes = {
    @Index(name = "idx_ycch_shop", columnList = "ma_shop"),
    @Index(name = "idx_ycch_kho", columnList = "ma_kho"),
    @Index(name = "idx_ycch_trangthai", columnList = "trang_thai")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YeuCauCapHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_yeu_cau")
    private Integer maYeuCau;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_shop", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_kho", nullable = false)
    private Kho kho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nhan_vien_duyet")
    private NhanVien nhanVienDuyet;

    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai = "ChoDuyet"; // ChoDuyet, DaDuyet, DaGiao, TuChoi

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "ngay_duyet")
    private LocalDateTime ngayDuyet;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "ly_do_tu_choi", length = 500)
    private String lyDoTuChoi;

    @OneToMany(mappedBy = "yeuCauCapHang", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ChiTietCapHang> chiTietCapHangs = new ArrayList<>();
}
