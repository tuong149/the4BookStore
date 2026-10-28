package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "SAN_PHAM", indexes = {
    @Index(name = "idx_sp_loaisp_trangthai", columnList = "loaiSP, trangThai"),
    @Index(name = "idx_sp_danhmuc_trangthai", columnList = "maDanhMuc, trangThai"),
    @Index(name = "idx_sp_tensp", columnList = "tenSP"),
    @Index(name = "idx_sp_ngaytao", columnList = "ngayTao"),
    @Index(name = "idx_sp_giaban", columnList = "giaBan")
})
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SanPham {

    public SanPham() {}

    public SanPham(Integer maSP, String tenSP, DanhMuc danhMuc, String loaiSP, String ISBN, Integer giaBan, Integer soLuongTon, Integer mucTonToiThieu, String moTa, Shop shop, String trangThaiKhoa, Integer soLuongDaBan, String hinhAnh, String trangThai, LocalDateTime ngayTao, List<SanPhamTacGia> sanPhamTacGias) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.danhMuc = danhMuc;
        this.loaiSP = loaiSP;
        this.ISBN = ISBN;
        this.giaBan = giaBan;
        this.soLuongTon = soLuongTon;
        this.mucTonToiThieu = mucTonToiThieu;
        this.moTa = moTa;
        this.shop = shop;
        this.trangThaiKhoa = trangThaiKhoa;
        this.soLuongDaBan = soLuongDaBan;
        this.hinhAnh = hinhAnh;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
        this.sanPhamTacGias = sanPhamTacGias;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maSP;
    @Column(nullable = false, length = 200)
    private String tenSP;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maDanhMuc", nullable = false)
    private DanhMuc danhMuc;
    
    @Column(nullable = false, length = 30)
    private String loaiSP = "SACH";
    @Column(unique = true, length = 20)
    private String ISBN;
    @Column(nullable = false)
    private Integer giaBan;
    @Column(nullable = false)
    private Integer soLuongTon = 0;
    @Column(nullable = false)
    private Integer mucTonToiThieu = 10;
    @Column(length = 1000)
    private String moTa;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_shop")
    private Shop shop;

    @Column(name = "trang_thai_khoa", nullable = false, length = 30)
    private String trangThaiKhoa = "BinhThuong"; // BinhThuong, BiKhoaBoiAdmin

    @Column(name = "so_luong_da_ban", nullable = false)
    private Integer soLuongDaBan = 0;

    @Column(name = "hinh_anh", length = 500)
    private String hinhAnh;

    @Column(nullable = false, length = 20)
    private String trangThai = "DangBan";
    @Column(nullable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();

    @OneToMany(mappedBy = "sanPham", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @org.hibernate.annotations.BatchSize(size = 50)
    private List<SanPhamTacGia> sanPhamTacGias = new java.util.ArrayList<>();

    public String getDanhSachTacGia() {
        try {
            if (sanPhamTacGias == null || sanPhamTacGias.isEmpty()) {
                return "";
            }
            return sanPhamTacGias.stream()
                    .filter(sptg -> sptg != null && sptg.getTacGia() != null)
                    .map(sptg -> sptg.getTacGia().getTenTacGia())
                    .filter(name -> name != null && !name.isBlank())
                    .collect(java.util.stream.Collectors.joining(", "));
        } catch (Exception e) {
            return "";
        }
    }


    public String getTenShop() {
        try {
            if (shop != null && org.hibernate.Hibernate.isInitialized(shop)) {
                return shop.getTenShop() != null ? shop.getTenShop() : "";
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public String getShopSlug() {
        try {
            if (shop != null && org.hibernate.Hibernate.isInitialized(shop)) {
                return shop.getSlug() != null ? shop.getSlug() : "";
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public String getHinhAnh() {
        if (hinhAnh != null && !hinhAnh.isBlank()) {
            return hinhAnh;
        }
        if (moTa != null && moTa.startsWith("http")) {
            return moTa;
        }
        return null;
    }
}
