package vn.bookstore.the4bookstore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "danh_muc_lon", indexes = {
    @Index(name = "idx_dml_maloai", columnList = "ma_loai", unique = true),
    @Index(name = "idx_dml_trangthai", columnList = "trang_thai")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class DanhMucLon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_loai", unique = true, nullable = false, length = 50)
    private String maLoai; // e.g., "Sach", "VanPhongPham", "QuaTang", "ThoiTrang"

    @Column(name = "ten_loai", nullable = false, length = 100)
    private String tenLoai; // e.g., "Sách", "Văn Phòng Phẩm", "Quà Tặng & Trang Trí"

    @Column(name = "icon", length = 50)
    private String icon = "category"; // Material symbol icon

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai = true;

    @Column(name = "thu_tu")
    private Integer thuTu = 0;

    @Transient
    public String getSlugUrl() {
        if ("Sach".equalsIgnoreCase(maLoai)) return "/san-pham/sach";
        if ("VanPhongPham".equalsIgnoreCase(maLoai)) return "/san-pham/van-phong-pham";
        if ("QuaTang".equalsIgnoreCase(maLoai)) return "/san-pham/qua-tang";
        if ("BoardGame".equalsIgnoreCase(maLoai)) return "/san-pham/board-game";
        return "/san-pham?loaiSP=" + maLoai;
    }
}
