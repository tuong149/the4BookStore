package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DANH_MUC", indexes = {
    @Index(name = "idx_dm_trangthai", columnList = "trangThai")
})
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

    @Transient
    public String getTargetUrl() {
        if (tenDanhMuc == null) return "/san-pham";
        String name = tenDanhMuc.toLowerCase();
        if (name.contains("bút") || name.contains("sổ") || name.contains("dụng cụ") || name.contains("văn phòng phẩm") || name.contains("vpp")) {
            return "/san-pham/van-phong-pham?danhMuc=" + maDanhMuc;
        }
        if (name.contains("túi") || name.contains("book nook") || name.contains("boardgame") || name.contains("quà tặng") || name.contains("nến") || name.contains("decor") || name.contains("trang trí")) {
            return "/san-pham/qua-tang?danhMuc=" + maDanhMuc;
        }
        return "/san-pham/sach?danhMuc=" + maDanhMuc;
    }

    @Transient
    public String getIcon() {
        if (tenDanhMuc == null) return "category";
        String name = tenDanhMuc.toLowerCase();
        if (name.contains("bút") || name.contains("sổ") || name.contains("dụng cụ") || name.contains("văn phòng phẩm") || name.contains("vpp")) {
            return "edit_note";
        }
        if (name.contains("túi") || name.contains("book nook") || name.contains("boardgame") || name.contains("quà tặng") || name.contains("nến") || name.contains("decor") || name.contains("trang trí")) {
            return "redeem";
        }
        return "menu_book";
    }
}