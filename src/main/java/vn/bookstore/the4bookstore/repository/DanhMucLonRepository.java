package vn.bookstore.the4bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.bookstore.the4bookstore.entity.DanhMucLon;

import java.util.List;
import java.util.Optional;

public interface DanhMucLonRepository extends JpaRepository<DanhMucLon, Integer> {

    Optional<DanhMucLon> findByMaLoai(String maLoai);

    boolean existsByMaLoai(String maLoai);

    boolean existsByMaLoaiAndIdNot(String maLoai, Integer id);

    List<DanhMucLon> findByTrangThaiTrueOrderByThuTuAscIdAsc();

    List<DanhMucLon> findAllByOrderByThuTuAscIdAsc();
}
