package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "KHO_HANG", uniqueConstraints =
    @UniqueConstraint(columnNames = {"maKho", "maSP"}))
@Data @NoArgsConstructor @AllArgsConstructor
public class KhoHang {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maKhoHang;

    @ManyToOne
    @JoinColumn(name = "maKho", nullable = false)
    private Kho kho;

    @ManyToOne
    @JoinColumn(name = "maSP", nullable = false)
    private SanPham sanPham;

    @Column(nullable = false)
    private Integer soLuongTon = 0;
    @Column(nullable = false)
    private Integer mucToiThieu = 0;
    @Column(nullable = false)
    private Integer mucToiDa = 10000;
    @Column(nullable = false)
    private LocalDateTime ngayCapNhat = LocalDateTime.now();
}
