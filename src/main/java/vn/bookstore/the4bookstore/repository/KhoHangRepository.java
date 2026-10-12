package vn.bookstore.the4bookstore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.bookstore.the4bookstore.entity.Kho;
import vn.bookstore.the4bookstore.entity.KhoHang;
import vn.bookstore.the4bookstore.entity.SanPham;

import java.util.List;
import java.util.Optional;


public interface KhoHangRepository extends JpaRepository<KhoHang, Integer> {
    Optional<KhoHang> findBySanPhamAndKho(SanPham sanPham, Kho kho);
    List<KhoHang> findByKho(Kho kho);

    @Query("SELECT kh FROM KhoHang kh WHERE kh.soLuongTon < kh.mucToiThieu")
    List<KhoHang> findLowStockItems();

    @Query("SELECT kh FROM KhoHang kh JOIN FETCH kh.sanPham sp WHERE kh.kho.maKho = :maKho AND sp.trangThai != 'DaXoa'")
    List<KhoHang> findByKhoIdWithSanPham(@org.springframework.data.repository.query.Param("maKho") Integer maKho);
}
