package vn.bookstore.the4bookstore.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SanPhamYeuThichId implements Serializable {
    private Integer maKH;
    private Integer maSP;

    public SanPhamYeuThichId() {}

    public SanPhamYeuThichId(Integer maKH, Integer maSP) {
        this.maKH = maKH;
        this.maSP = maSP;
    }

    public Integer getMaKH() {
        return maKH;
    }

    public void setMaKH(Integer maKH) {
        this.maKH = maKH;
    }

    public Integer getMaSP() {
        return maSP;
    }

    public void setMaSP(Integer maSP) {
        this.maSP = maSP;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SanPhamYeuThichId that = (SanPhamYeuThichId) o;
        return Objects.equals(maKH, that.maKH) && Objects.equals(maSP, that.maSP);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKH, maSP);
    }
}
