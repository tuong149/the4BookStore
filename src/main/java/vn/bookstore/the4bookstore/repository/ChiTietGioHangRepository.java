package vn.bookstore.the4bookstore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.bookstore.the4bookstore.entity.ChiTietGioHang;
import vn.bookstore.the4bookstore.entity.ChiTietGioHangId;
import vn.bookstore.the4bookstore.entity.GioHang;
import vn.bookstore.the4bookstore.entity.SanPham;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHang, ChiTietGioHangId> {
    List<ChiTietGioHang> findByGioHang(GioHang gioHang);
    Optional<ChiTietGioHang> findByGioHangAndSanPham(GioHang gioHang, SanPham sanPham);
    void deleteByGioHang(GioHang gioHang);
    long countByGioHang(GioHang gioHang);
}
