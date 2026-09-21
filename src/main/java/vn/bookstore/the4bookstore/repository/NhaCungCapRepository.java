package vn.bookstore.the4bookstore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.bookstore.the4bookstore.entity.NhaCungCap;

@Repository
public interface NhaCungCapRepository extends JpaRepository<NhaCungCap, Integer> {
}
