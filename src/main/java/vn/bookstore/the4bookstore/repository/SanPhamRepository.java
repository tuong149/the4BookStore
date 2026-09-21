package vn.bookstore.the4bookstore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.bookstore.the4bookstore.entity.SanPham;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {
}
