package vn.bookstore.the4bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.bookstore.the4bookstore.entity.DanhMuc;

import java.util.List;

public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {

    @Query("SELECT d FROM DanhMuc d WHERE LOWER(d.loaiSP) = LOWER(:loaiSP) AND d.trangThai = true ORDER BY d.maDanhMuc ASC")
    List<DanhMuc> findByLoaiSPAndTrangThaiTrue(@Param("loaiSP") String loaiSP);

    @Query("SELECT d FROM DanhMuc d WHERE LOWER(d.loaiSP) = LOWER(:loaiSP) ORDER BY d.maDanhMuc ASC")
    List<DanhMuc> findByLoaiSP(@Param("loaiSP") String loaiSP);

    List<DanhMuc> findByTrangThaiTrueOrderByMaDanhMucAsc();
}
