package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "PHIEU_KIEM_KE")
@Data @NoArgsConstructor @AllArgsConstructor
public class PhieuKiemKe {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maPhieuKiemKe;
    @Column(nullable = false)
    private LocalDateTime ngayKiemKe = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "maNV", nullable = false)
    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "maKho", nullable = false)
    private Kho kho;

    @Column(nullable = false, length = 30)
    private String trangThai = "ChoDuyet";

    @ManyToOne
    @JoinColumn(name = "maNVPheDuyet")
    private NhanVien nvPheDuyet;

    private LocalDateTime ngayPheDuyet;
    @Column(length = 500)
    private String ghiChu;

    @OneToMany(mappedBy = "phieuKiemKe")
    private List<ChiTietKiemKe> chiTietKiemKes;
}