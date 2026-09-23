package vn.bookstore.the4bookstore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.bookstore.the4bookstore.entity.DonHang;

import java.time.LocalDateTime;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Integer> {

    @Query("SELECT SUM(dh.tongTien) FROM DonHang dh WHERE dh.trangThai = 'DaGiao' AND dh.ngayDat >= :startDate AND dh.ngayDat < :endDate")
    Long getRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(dh) FROM DonHang dh WHERE dh.trangThai = 'DaGiao' AND dh.ngayDat >= :startDate AND dh.ngayDat < :endDate")
    Long getOrderCountByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    java.util.List<DonHang> findByKhachHangOrderByNgayDatDesc(vn.bookstore.the4bookstore.entity.KhachHang khachHang);

    Long countByKhachHang(vn.bookstore.the4bookstore.entity.KhachHang khachHang);

    Long countByKhachHangAndTrangThai(vn.bookstore.the4bookstore.entity.KhachHang khachHang, String trangThai);
}
