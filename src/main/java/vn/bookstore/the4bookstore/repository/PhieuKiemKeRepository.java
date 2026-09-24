package vn.bookstore.the4bookstore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.bookstore.the4bookstore.entity.Kho;
import vn.bookstore.the4bookstore.entity.PhieuKiemKe;

import java.util.List;

@Repository
public interface PhieuKiemKeRepository extends JpaRepository<PhieuKiemKe, Integer> {
    List<PhieuKiemKe> findByKho(Kho kho);
    List<PhieuKiemKe> findByTrangThai(String trangThai);
}
