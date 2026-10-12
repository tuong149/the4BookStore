package vn.bookstore.the4bookstore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.bookstore.the4bookstore.entity.Shop;
import vn.bookstore.the4bookstore.entity.YeuCauCapHang;

import java.util.List;

public interface YeuCauCapHangRepository extends JpaRepository<YeuCauCapHang, Integer> {

    Page<YeuCauCapHang> findByShopOrderByNgayTaoDesc(Shop shop, Pageable pageable);

    List<YeuCauCapHang> findByShopOrderByNgayTaoDesc(Shop shop);

    Page<YeuCauCapHang> findByTrangThaiOrderByNgayTaoDesc(String trangThai, Pageable pageable);

    @Query("SELECT y FROM YeuCauCapHang y JOIN FETCH y.shop JOIN FETCH y.kho LEFT JOIN FETCH y.nhanVienDuyet ORDER BY y.ngayTao DESC")
    List<YeuCauCapHang> findAllWithShopAndKho();

    @Query("SELECT y FROM YeuCauCapHang y JOIN FETCH y.shop JOIN FETCH y.kho LEFT JOIN FETCH y.chiTietCapHangs ct LEFT JOIN FETCH ct.sanPham WHERE y.maYeuCau = :id")
    YeuCauCapHang findByIdWithDetails(@Param("id") Integer id);

    long countByTrangThai(String trangThai);
}
