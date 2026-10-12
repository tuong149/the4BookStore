package vn.bookstore.the4bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.bookstore.the4bookstore.entity.KhachHang;
import vn.bookstore.the4bookstore.entity.SanPham;
import vn.bookstore.the4bookstore.entity.SanPhamDaXem;
import vn.bookstore.the4bookstore.entity.SanPhamDaXemId;

import java.util.List;

@Repository
public interface SanPhamDaXemRepository extends JpaRepository<SanPhamDaXem, SanPhamDaXemId> {
    List<SanPhamDaXem> findTop10ByKhachHangOrderByThoiGianXemDesc(KhachHang khachHang);
    boolean existsByKhachHangAndSanPham(KhachHang khachHang, SanPham sanPham);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(value = "INSERT INTO san_pham_da_xem (makh, masp, thoi_gian_xem) VALUES (:maKH, :maSP, NOW()) ON DUPLICATE KEY UPDATE thoi_gian_xem = NOW()", nativeQuery = true)
    void upsertRecentlyViewed(@org.springframework.data.repository.query.Param("maKH") Integer maKH, @org.springframework.data.repository.query.Param("maSP") Integer maSP);
}
