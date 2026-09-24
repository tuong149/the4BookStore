package vn.bookstore.the4bookstore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.bookstore.the4bookstore.entity.Kho;
import vn.bookstore.the4bookstore.entity.KhoHang;
import vn.bookstore.the4bookstore.entity.SanPham;

import java.util.List;
import java.util.Optional;

@Repository
public interface KhoHangRepository extends JpaRepository<KhoHang, Integer> {
    Optional<KhoHang> findBySanPhamAndKho(SanPham sanPham, Kho kho);
    List<KhoHang> findByKho(Kho kho);

    @Query("SELECT kh FROM KhoHang kh WHERE kh.soLuongTon < kh.mucToiThieu")
    List<KhoHang> findLowStockItems();
}
