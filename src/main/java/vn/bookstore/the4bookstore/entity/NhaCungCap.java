package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "NHA_CUNG_CAP")
@Data @NoArgsConstructor @AllArgsConstructor
public class NhaCungCap {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maNCC;
    @Column(nullable = false, length = 150)
    private String tenNCC;
    @Column(length = 255)
    private String diaChi;
    @Column(length = 20)
    private String soDienThoai;
    @Column(length = 100)
    private String email;
    @Column(length = 50)
    private String maSoThue;
    @Column(nullable = false, length = 20)
    private String trangThai = "HoatDong";
}