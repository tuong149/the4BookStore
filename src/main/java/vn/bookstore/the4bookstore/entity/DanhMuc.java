package vn.bookstore.the4bookstore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DANH_MUC", indexes = {
    @Index(name = "idx_dm_trangthai", columnList = "trangThai"),
    @Index(name = "idx_dm_loaisp", columnList = "loaisp")
})
@org.hibernate.annotations.BatchSize(size = 50)
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DanhMuc {

    public DanhMuc() {}

    public DanhMuc(Integer maDanhMuc, String tenDanhMuc, String moTa, Boolean trangThai, String loaiSP) {
        this.maDanhMuc = maDanhMuc;
        this.tenDanhMuc = tenDanhMuc;
        this.moTa = moTa;
        this.trangThai = trangThai;
        this.loaiSP = loaiSP;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maDanhMuc;

    @Column(unique = true, nullable = false, length = 100)
    private String tenDanhMuc;

    @Column(length = 500)
    private String moTa;

    @Column(nullable = false)
    private Boolean trangThai = true;

    @Column(name = "loaisp", nullable = false, length = 30)
    private String loaiSP = "Sach"; // Sach, VanPhongPham, QuaTang

    @Transient
    public String getTargetUrl() {
        if ("VanPhongPham".equalsIgnoreCase(loaiSP)) {
            return "/san-pham/van-phong-pham?danhMuc=" + maDanhMuc;
        }
        if ("QuaTang".equalsIgnoreCase(loaiSP)) {
            return "/san-pham/qua-tang?danhMuc=" + maDanhMuc;
        }
        if ("Sach".equalsIgnoreCase(loaiSP)) {
            return "/san-pham/sach?danhMuc=" + maDanhMuc;
        }

        if (loaiSP != null && !loaiSP.isBlank()) {
            return "/san-pham?loaiSP=" + loaiSP + "&danhMuc=" + maDanhMuc;
        }

        // Fallback theo tên nếu loaiSP chưa được set
        if (tenDanhMuc != null) {
            String name = tenDanhMuc.toLowerCase();
            if (name.contains("bút") || name.contains("sổ") || name.contains("dụng cụ") || name.contains("văn phòng phẩm") || name.contains("vpp")) {
                return "/san-pham/van-phong-pham?danhMuc=" + maDanhMuc;
            }
            if (name.contains("túi") || name.contains("book nook") || name.contains("boardgame") || name.contains("quà tặng") || name.contains("nến") || name.contains("decor") || name.contains("trang trí")) {
                return "/san-pham/qua-tang?danhMuc=" + maDanhMuc;
            }
        }
        return "/san-pham?danhMuc=" + maDanhMuc;
    }

    @Transient
    public String getIcon() {
        if ("VanPhongPham".equalsIgnoreCase(loaiSP)) return "edit_note";
        if ("QuaTang".equalsIgnoreCase(loaiSP)) return "redeem";
        if ("Sach".equalsIgnoreCase(loaiSP)) return "menu_book";

        if (tenDanhMuc != null) {
            String name = tenDanhMuc.toLowerCase();
            if (name.contains("bút") || name.contains("sổ") || name.contains("dụng cụ") || name.contains("văn phòng phẩm") || name.contains("vpp")) {
                return "edit_note";
            }
            if (name.contains("túi") || name.contains("book nook") || name.contains("boardgame") || name.contains("quà tặng") || name.contains("nến") || name.contains("decor") || name.contains("trang trí")) {
                return "redeem";
            }
        }
        return "menu_book";
    }

    @Transient
    public String getLoaiDisplay() {
        if ("VanPhongPham".equalsIgnoreCase(loaiSP)) return "Văn Phòng Phẩm";
        if ("QuaTang".equalsIgnoreCase(loaiSP)) return "Quà Tặng & Trang Trí";
        return "Sách";
    }
}
