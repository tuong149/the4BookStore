package vn.bookstore.the4bookstore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.bookstore.the4bookstore.entity.PhieuKiemKe;

@Repository
public interface PhieuKiemKeRepository extends JpaRepository<PhieuKiemKe, Integer> {
}
