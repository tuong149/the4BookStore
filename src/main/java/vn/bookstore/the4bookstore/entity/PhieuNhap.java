package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "PHIEU_NHAP", indexes = {
    @Index(name = "idx_pn_ngaynhap", columnList = "ngayNhap"),
    @Index(name = "idx_pn_trangthai", columnList = "trangThai")
})
@Data @NoArgsConstructor @AllArgsConstructor
public class PhieuNhap {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maPN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_shop")
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNCC", nullable = true)
    private NhaCungCap nhaCungCap;

    @Column(name = "nha_cung_cap_ten", length = 255)
    private String nhaCungCapTen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNV", nullable = true)
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKho", nullable = true)
    private Kho kho;

    @Column(nullable = false)
    private LocalDateTime ngayNhap = LocalDateTime.now();
    @Column(nullable = false)
    private Integer tongTien = 0;
    @Column(nullable = false, length = 20)
    private String trangThai = "DaNhap";
    @Column(length = 500)
    private String ghiChu;

    @OneToMany(mappedBy = "phieuNhap", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietPhieuNhap> chiTietPhieuNhaps;
}