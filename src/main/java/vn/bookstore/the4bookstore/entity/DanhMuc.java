package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DANH_MUC")
@Data @NoArgsConstructor @AllArgsConstructor
public class DanhMuc {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maDanhMuc;
    @Column(unique = true, nullable = false, length = 100)
    private String tenDanhMuc;
    @Column(length = 500)
    private String moTa;
    @Column(nullable = false)
    private Boolean trangThai = true;
}