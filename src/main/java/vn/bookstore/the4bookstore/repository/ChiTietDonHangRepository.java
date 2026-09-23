package vn.bookstore.the4bookstore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.bookstore.the4bookstore.entity.ChiTietDonHang;
import vn.bookstore.the4bookstore.entity.ChiTietDonHangId;

@Repository
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, ChiTietDonHangId> {

    @Query("SELECT SUM(ct.soLuong) FROM ChiTietDonHang ct JOIN ct.donHang dh WHERE dh.trangThai = 'DaGiao'")
    Long getTotalBooksSold();
}

