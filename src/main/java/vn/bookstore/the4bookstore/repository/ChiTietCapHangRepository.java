package vn.bookstore.the4bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.bookstore.the4bookstore.entity.ChiTietCapHang;
import vn.bookstore.the4bookstore.entity.YeuCauCapHang;

import java.util.List;

public interface ChiTietCapHangRepository extends JpaRepository<ChiTietCapHang, Integer> {
    List<ChiTietCapHang> findByYeuCauCapHang(YeuCauCapHang yeuCauCapHang);
}
