package vn.bookstore.the4bookstore.entity;

import java.io.Serializable;
import java.util.Objects;

public class ChiTietGioHangId implements Serializable {
    private Integer gioHang;
    private Integer sanPham;

    public ChiTietGioHangId() {}

    public ChiTietGioHangId(Integer gioHang, Integer sanPham) {
        this.gioHang = gioHang;
        this.sanPham = sanPham;
    }

    public Integer getGioHang() {
        return gioHang;
    }

    public void setGioHang(Integer gioHang) {
        this.gioHang = gioHang;
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
        ChiTietGioHangId that = (ChiTietGioHangId) o;
        return Objects.equals(gioHang, that.gioHang) && Objects.equals(sanPham, that.sanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gioHang, sanPham);
    }
}
