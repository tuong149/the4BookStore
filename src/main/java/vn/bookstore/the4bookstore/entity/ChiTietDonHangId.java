package vn.bookstore.the4bookstore.entity;

import java.io.Serializable;
import java.util.Objects;

public class ChiTietDonHangId implements Serializable {
    private Integer donHang;
    private Integer sanPham;

    public ChiTietDonHangId() {}

    public ChiTietDonHangId(Integer donHang, Integer sanPham) {
        this.donHang = donHang;
        this.sanPham = sanPham;
    }

    public Integer getDonHang() {
        return donHang;
    }

    public void setDonHang(Integer donHang) {
        this.donHang = donHang;
    }

    public Integer getSanPham() {
        return sanPham;
    }

    public void setSanPham(Integer sanPham) {
        this.sanPham = sanPham;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietDonHangId that = (ChiTietDonHangId) o;
        return Objects.equals(donHang, that.donHang) && Objects.equals(sanPham, that.sanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(donHang, sanPham);
    }
}
