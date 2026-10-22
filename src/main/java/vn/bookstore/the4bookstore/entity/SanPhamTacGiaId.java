package vn.bookstore.the4bookstore.entity;

import java.io.Serializable;
import java.util.Objects;

public class SanPhamTacGiaId implements Serializable {
    private Integer sanPham;
    private Integer tacGia;

    public SanPhamTacGiaId() {}

    public SanPhamTacGiaId(Integer sanPham, Integer tacGia) {
        this.sanPham = sanPham;
        this.tacGia = tacGia;
    }

    public Integer getSanPham() {
        return sanPham;
    }

    public void setSanPham(Integer sanPham) {
        this.sanPham = sanPham;
    }

    public Integer getTacGia() {
        return tacGia;
    }

    public void setTacGia(Integer tacGia) {
        this.tacGia = tacGia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SanPhamTacGiaId that = (SanPhamTacGiaId) o;
        return Objects.equals(sanPham, that.sanPham) && Objects.equals(tacGia, that.tacGia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sanPham, tacGia);
    }
}
