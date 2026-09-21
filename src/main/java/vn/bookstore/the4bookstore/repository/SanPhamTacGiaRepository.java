package vn.bookstore.the4bookstore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.bookstore.the4bookstore.entity.SanPhamTacGia;
import vn.bookstore.the4bookstore.entity.SanPhamTacGiaId;

@Repository
public interface SanPhamTacGiaRepository extends JpaRepository<SanPhamTacGia, SanPhamTacGiaId> {
}

