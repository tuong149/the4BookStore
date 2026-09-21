package vn.bookstore.the4bookstore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.bookstore.the4bookstore.entity.ChiTietKiemKe;
import vn.bookstore.the4bookstore.entity.ChiTietKiemKeId;

@Repository
public interface ChiTietKiemKeRepository extends JpaRepository<ChiTietKiemKe, ChiTietKiemKeId> {
}

