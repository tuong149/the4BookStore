package vn.bookstore.the4bookstore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.bookstore.the4bookstore.entity.PhieuNhap;

@Repository
public interface PhieuNhapRepository extends JpaRepository<PhieuNhap, Integer> {
}
