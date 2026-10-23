package vn.bookstore.the4bookstore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.bookstore.the4bookstore.entity.TacGia;

import java.util.Optional;
import java.util.List;

public interface TacGiaRepository extends JpaRepository<TacGia, Integer> {
    Optional<TacGia> findByTenTacGia(String tenTacGia);
    Optional<TacGia> findByTenTacGiaIgnoreCase(String tenTacGia);
    List<TacGia> findByTenTacGiaContainingIgnoreCase(String keyword);

    @Query("SELECT DISTINCT tg FROM TacGia tg " +
           "JOIN tg.sanPhamTacGias sptg " +
           "JOIN sptg.sanPham sp " +
           "WHERE sp.danhMuc.maDanhMuc = :maDanhMuc " +
           "AND sp.trangThai != 'DaXoa' AND sp.trangThai != 'NgungBan' " +
           "ORDER BY tg.tenTacGia ASC")
    List<TacGia> findByDanhMucId(@Param("maDanhMuc") Integer maDanhMuc);

    @Query("SELECT DISTINCT tg FROM TacGia tg " +
           "JOIN tg.sanPhamTacGias sptg " +
           "JOIN sptg.sanPham sp " +
           "WHERE UPPER(sp.loaiSP) = UPPER(:loaiSP) " +
           "AND sp.trangThai != 'DaXoa' AND sp.trangThai != 'NgungBan' " +
           "ORDER BY tg.tenTacGia ASC")
    List<TacGia> findByLoaiSP(@Param("loaiSP") String loaiSP);

    @Query("SELECT DISTINCT tg.maTacGia, sp.danhMuc.maDanhMuc FROM TacGia tg " +
           "JOIN tg.sanPhamTacGias sptg " +
           "JOIN sptg.sanPham sp " +
           "WHERE UPPER(sp.loaiSP) = 'SACH' " +
           "AND sp.trangThai != 'DaXoa' AND sp.trangThai != 'NgungBan'")
    List<Object[]> findAuthorCategoryMappingsForSach();
}
