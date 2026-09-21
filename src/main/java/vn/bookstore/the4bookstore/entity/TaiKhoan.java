package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TAI_KHOAN")
@Data @NoArgsConstructor @AllArgsConstructor
public class TaiKhoan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maTaiKhoan;
    @Column(unique = true, nullable = false, length = 50)
    private String tenDangNhap;
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    @Column(nullable = false, length = 255)
    private String matKhauHash;
    @Column(nullable = false, length = 30)
    private String vaiTro;
    @Column(nullable = false, length = 20)
    private String trangThai = "HoatDong";
    @Column(nullable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();
}