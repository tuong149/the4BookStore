package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "PHIEU_NHAP")
@Data @NoArgsConstructor @AllArgsConstructor
public class PhieuNhap {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maPN;

    @ManyToOne
    @JoinColumn(name = "maNCC", nullable = false)
    private NhaCungCap nhaCungCap;

    @ManyToOne
    @JoinColumn(name = "maNV", nullable = false)
    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "maKho", nullable = false)
    private Kho kho;

    @Column(nullable = false)
    private LocalDateTime ngayNhap = LocalDateTime.now();
    @Column(nullable = false)
    private Integer tongTien = 0;
    @Column(nullable = false, length = 20)
    private String trangThai = "DaNhap";
    @Column(length = 500)
    private String ghiChu;

    @OneToMany(mappedBy = "phieuNhap")
    private List<ChiTietPhieuNhap> chiTietPhieuNhaps;
}