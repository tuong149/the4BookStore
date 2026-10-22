package vn.bookstore.the4bookstore.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "KHUYEN_MAI")
@Data @NoArgsConstructor public class KhuyenMai {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maKM;

    @Column(nullable = false, length = 150)
    private String tenKM;

    @Column(unique = true, length = 50)
    private String maCode;

    @Column(length = 500)
    private String moTa;

    // PhanTram, TienCoDinh, Freeship
    @Column(nullable = false, length = 20)
    private String loaiGiam;

    @Column(nullable = false)
    private Integer giaTriGiam;

    private Integer giamToiDa;

    @Column(name = "don_toi_thieu", nullable = false)
    private Integer donToiThieu = 0;

    private Integer soLuongToiDa;

    @Column(nullable = false)
    private Integer soLuongDaDung = 0;

    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(nullable = false)
    private LocalDateTime ngayBatDau;

    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(nullable = false)
    private LocalDateTime ngayKetThuc;

    // HoatDong, VoHieuHoa
    @Column(nullable = false, length = 20)
    private String trangThai = "HoatDong";

    // Khối 1: Hiển thị công khai hay mã ẩn/riêng tư
    @Column(nullable = false)
    private Boolean hienThiCongKhai = true;

    @Transient
    private String apDungCho = "ALL";

    @Transient
    private String danhSachDanhMucIds;

    @Transient
    private String danhSachSanPhamIds;

    @Transient
    private String loaiTruSanPhamIds;

    // ALL (mọi người), NEW (khách mới), VIP (thành viên VIP)
    @Column(length = 20)
    private String doiTuongKhachHang = "ALL";

    // Khối 4: Giới hạn trên mỗi khách hàng
    @Column(nullable = false)
    private Integer gioiHanMoiKhachHang = 1;

    @Column(name = "loai_khuyen_mai", nullable = false, length = 30)
    private String loaiKhuyenMai = "GIAM_GIA_SAN_PHAM"; // GIAM_GIA_SAN_PHAM, MIEN_PHI_VAN_CHUYEN

    @Column(name = "pham_vi", nullable = false, length = 20)
    private String phamVi = "TOAN_SAN"; // TOAN_SAN, SHOP

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_shop")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Shop shop;

    /**
     * Trạng thái tính toán tự động dựa trên thời gian và số lượng:
     * - "VoHieuHoa": Admin chủ động tắt
     * - "HetHan": Quá hạn hoặc hết lượt
     * - "SapToi": Chưa tới thời gian bắt đầu
     * - "DangDienRa": Đang trong thời gian và còn lượt dùng
     */
    public String getComputedStatus() {
        if ("VoHieuHoa".equalsIgnoreCase(trangThai) || "TamDung".equalsIgnoreCase(trangThai)) {
            return "VoHieuHoa";
        }
        LocalDateTime now = LocalDateTime.now();
        if (ngayBatDau != null && now.isBefore(ngayBatDau)) {
            return "SapToi";
        }
        if (ngayKetThuc != null && now.isAfter(ngayKetThuc)) {
            return "HetHan";
        }
        int maxUsage = (soLuongToiDa != null && soLuongToiDa > 0) ? soLuongToiDa : 100;
        if (soLuongDaDung != null && soLuongDaDung >= maxUsage) {
            return "HetHan";
        }
        return "DangDienRa";
    }

    public String getStatusBadgeText() {
        switch (getComputedStatus()) {
            case "DangDienRa": return "Đang diễn ra";
            case "SapToi": return "Sắp diễn ra";
            case "HetHan": return "Đã hết hạn";
            case "VoHieuHoa": return "Đã vô hiệu hóa";
            default: return "Không xác định";
        }
    }

    public boolean isDangDienRa() {
        return "DangDienRa".equals(getComputedStatus());
    }

    /**
     * Phân loại loại voucher:
     * - SHIPPING_DISCOUNT: Mã giảm chi phí vận chuyển (Freeship hoặc MIEN_PHI_VAN_CHUYEN)
     * - ORDER_DISCOUNT: Mã giảm giá tiền sản phẩm (PhanTram, TienCoDinh, GIAM_GIA_SAN_PHAM)
     */
    public String getVoucherType() {
        if ("Freeship".equalsIgnoreCase(loaiGiam) || "MIEN_PHI_VAN_CHUYEN".equalsIgnoreCase(loaiKhuyenMai) || "SHIPPING_DISCOUNT".equalsIgnoreCase(loaiKhuyenMai)) {
            return "SHIPPING_DISCOUNT";
        }
        return "ORDER_DISCOUNT";
    }

    public boolean isShippingDiscount() {
        return "SHIPPING_DISCOUNT".equals(getVoucherType());
    }

    public boolean isOrderDiscount() {
        return "ORDER_DISCOUNT".equals(getVoucherType());
    }

    /**
     * Phân loại hình thức chiết khấu:
     * - PERCENTAGE: Giảm theo tỷ lệ %
     * - FIXED_AMOUNT: Giảm theo số tiền cụ thể
     */
    public String getDiscountType() {
        if ("PhanTram".equalsIgnoreCase(loaiGiam) || "PERCENTAGE".equalsIgnoreCase(loaiGiam)) {
            return "PERCENTAGE";
        }
        return "FIXED_AMOUNT";
    }

    public Integer getDonToiThieu() {
        return donToiThieu != null ? donToiThieu : 0;
    }

    public void setDonToiThieu(Integer donToiThieu) {
        this.donToiThieu = (donToiThieu != null && donToiThieu >= 0) ? donToiThieu : 0;
    }

    public Integer getSoLuongDaDung() {
        return soLuongDaDung != null ? soLuongDaDung : 0;
    }

    public void setSoLuongDaDung(Integer soLuongDaDung) {
        this.soLuongDaDung = (soLuongDaDung != null && soLuongDaDung >= 0) ? soLuongDaDung : 0;
    }

    public Integer getGioiHanMoiKhachHang() {
        return (gioiHanMoiKhachHang != null && gioiHanMoiKhachHang > 0) ? gioiHanMoiKhachHang : 1;
    }

    public void setGioiHanMoiKhachHang(Integer gioiHanMoiKhachHang) {
        this.gioiHanMoiKhachHang = (gioiHanMoiKhachHang != null && gioiHanMoiKhachHang > 0) ? gioiHanMoiKhachHang : 1;
    }

    public Boolean getHienThiCongKhai() {
        return hienThiCongKhai != null ? hienThiCongKhai : true;
    }

    public void setHienThiCongKhai(Boolean hienThiCongKhai) {
        this.hienThiCongKhai = (hienThiCongKhai != null) ? hienThiCongKhai : true;
    }

    public String getTrangThai() {
        return (trangThai != null && !trangThai.isBlank()) ? trangThai : "HoatDong";
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = (trangThai != null && !trangThai.isBlank()) ? trangThai : "HoatDong";
    }

    public String getPhamVi() {
        if (phamVi != null && !phamVi.isBlank()) return phamVi;
        return (shop != null) ? "SHOP" : "TOAN_SAN";
    }

    public void setPhamVi(String phamVi) {
        this.phamVi = (phamVi != null && !phamVi.isBlank()) ? phamVi : "TOAN_SAN";
    }

    public String getDoiTuongKhachHang() {
        return (doiTuongKhachHang != null && !doiTuongKhachHang.isBlank()) ? doiTuongKhachHang : "ALL";
    }

    public void setDoiTuongKhachHang(String doiTuongKhachHang) {
        this.doiTuongKhachHang = (doiTuongKhachHang != null && !doiTuongKhachHang.isBlank()) ? doiTuongKhachHang : "ALL";
    }

    public String getLoaiKhuyenMai() {
        if ("Freeship".equalsIgnoreCase(loaiGiam) || "MIEN_PHI_VAN_CHUYEN".equalsIgnoreCase(loaiKhuyenMai)) {
            return "MIEN_PHI_VAN_CHUYEN";
        }
        return (loaiKhuyenMai != null && !loaiKhuyenMai.isBlank()) ? loaiKhuyenMai : "GIAM_GIA_SAN_PHAM";
    }

    public void setLoaiKhuyenMai(String loaiKhuyenMai) {
        this.loaiKhuyenMai = (loaiKhuyenMai != null && !loaiKhuyenMai.isBlank()) ? loaiKhuyenMai : "GIAM_GIA_SAN_PHAM";
    }

    @PrePersist
    @PreUpdate
    public void prePersistOrUpdate() {
        if (this.donToiThieu == null || this.donToiThieu < 0) {
            this.donToiThieu = 0;
        }
        if (this.soLuongDaDung == null || this.soLuongDaDung < 0) {
            this.soLuongDaDung = 0;
        }
        if (this.gioiHanMoiKhachHang == null || this.gioiHanMoiKhachHang <= 0) {
            this.gioiHanMoiKhachHang = 1;
        }
        if (this.hienThiCongKhai == null) {
            this.hienThiCongKhai = true;
        }
        if (this.trangThai == null || this.trangThai.isBlank()) {
            this.trangThai = "HoatDong";
        }
        if (this.doiTuongKhachHang == null || this.doiTuongKhachHang.isBlank()) {
            this.doiTuongKhachHang = "ALL";
        }
        if (this.phamVi == null || this.phamVi.isBlank()) {
            this.phamVi = (this.shop != null) ? "SHOP" : "TOAN_SAN";
        }
        if ("Freeship".equalsIgnoreCase(this.loaiGiam) || "MIEN_PHI_VAN_CHUYEN".equalsIgnoreCase(this.loaiKhuyenMai)) {
            this.loaiKhuyenMai = "MIEN_PHI_VAN_CHUYEN";
        } else if (this.loaiKhuyenMai == null || this.loaiKhuyenMai.isBlank()) {
            this.loaiKhuyenMai = "GIAM_GIA_SAN_PHAM";
        }
        if (this.ngayBatDau == null) {
            this.ngayBatDau = LocalDateTime.now().withSecond(0).withNano(0);
        }
        if (this.ngayKetThuc == null) {
            this.ngayKetThuc = this.ngayBatDau.plusMonths(1);
        }
    }
}