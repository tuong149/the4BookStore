package vn.bookstore.the4bookstore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.bookstore.the4bookstore.entity.TacGia;

import java.util.Optional;
import java.util.List;

@Repository
public interface TacGiaRepository extends JpaRepository<TacGia, Integer> {
    Optional<TacGia> findByTenTacGia(String tenTacGia);
    List<TacGia> findByTenTacGiaContainingIgnoreCase(String keyword);
}
