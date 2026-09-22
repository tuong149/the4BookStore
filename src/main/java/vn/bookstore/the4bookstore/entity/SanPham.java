package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "SAN_PHAM")
@Data @NoArgsConstructor @AllArgsConstructor
public class SanPham {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maSP;
    @Column(nullable = false, length = 200)
    private String tenSP;
    
    @ManyToOne
    @JoinColumn(name = "maDanhMuc", nullable = false)
    private DanhMuc danhMuc;
    
    @ManyToOne
    @JoinColumn(name = "maNXB")
    private NhaXuatBan nhaXuatBan;
    
    @ManyToOne
    @JoinColumn(name = "maNCC")
    private NhaCungCap nhaCungCap;
    
    @Column(nullable = false, length = 30)
    private String loaiSP;
    @Column(unique = true, length = 20)
    private String ISBN;
    @Column(nullable = false)
    private Integer giaBan;
    @Column(nullable = false)
    private Integer soLuongTon = 0;
    @Column(nullable = false)
    private Integer mucTonToiThieu = 0;
    @Column(length = 1000)
    private String moTa;
    @Column(nullable = false, length = 20)
    private String trangThai = "DangBan";
    @Column(nullable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();

    public String getHinhAnh() {
        if (moTa != null && moTa.startsWith("http")) {
            return moTa;
        }
        return null;
    }
}