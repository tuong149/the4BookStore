package vn.bookstore.the4bookstore.entity;

import java.io.Serializable;
import java.util.Objects;

public class ChiTietKiemKeId implements Serializable {
    private Integer phieuKiemKe;
    private Integer sanPham;

    public ChiTietKiemKeId() {}

    public ChiTietKiemKeId(Integer phieuKiemKe, Integer sanPham) {
        this.phieuKiemKe = phieuKiemKe;
        this.sanPham = sanPham;
    }

    public Integer getPhieuKiemKe() {
        return phieuKiemKe;
    }

    public void setPhieuKiemKe(Integer phieuKiemKe) {
        this.phieuKiemKe = phieuKiemKe;
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
        ChiTietKiemKeId that = (ChiTietKiemKeId) o;
        return Objects.equals(phieuKiemKe, that.phieuKiemKe) && Objects.equals(sanPham, that.sanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phieuKiemKe, sanPham);
    }
}
