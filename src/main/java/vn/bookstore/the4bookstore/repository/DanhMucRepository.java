package vn.bookstore.the4bookstore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.bookstore.the4bookstore.entity.DanhMuc;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {
}
