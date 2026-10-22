package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "GIO_HANG")
@Data @NoArgsConstructor public class GioHang {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maGioHang;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKH", nullable = false)
    private KhachHang khachHang;
    
    @Column(nullable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();
    @Column(nullable = false)
    private LocalDateTime ngayCapNhat = LocalDateTime.now();
}