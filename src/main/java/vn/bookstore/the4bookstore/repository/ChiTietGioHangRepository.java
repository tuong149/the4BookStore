package vn.bookstore.the4bookstore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.bookstore.the4bookstore.entity.ChiTietGioHang;
import vn.bookstore.the4bookstore.entity.ChiTietGioHangId;

@Repository
public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHang, ChiTietGioHangId> {
}

