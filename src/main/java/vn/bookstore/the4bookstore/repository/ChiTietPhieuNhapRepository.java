package vn.bookstore.the4bookstore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.bookstore.the4bookstore.entity.ChiTietPhieuNhap;
import vn.bookstore.the4bookstore.entity.ChiTietPhieuNhapId;

@Repository
public interface ChiTietPhieuNhapRepository extends JpaRepository<ChiTietPhieuNhap, ChiTietPhieuNhapId> {
}

