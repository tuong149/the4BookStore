package vn.bookstore.the4bookstore.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import vn.bookstore.the4bookstore.entity.SanPham;
import vn.bookstore.the4bookstore.repository.SanPhamRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductDeleteTest {

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Test
    public void testDeleteProductWithoutRollback() {
        // Kiểm tra xem sản phẩm 15 có tồn tại không
        assertTrue(sanPhamRepository.existsById(15), "Sản phẩm test 15 phải tồn tại");

        // Gọi deleteProduct
        assertDoesNotThrow(() -> {
            sanPhamService.deleteProduct(15);
        }, "Hàm deleteProduct không được ném ngoại lệ hoặc rollback");

        // Kiểm tra sau khi xóa: sản phẩm hoặc bị xóa khỏi DB hoặc có trạng thái DaXoa
        SanPham sp = sanPhamRepository.findById(15).orElse(null);
        assertTrue(sp == null || "DaXoa".equalsIgnoreCase(sp.getTrangThai()), "Sản phẩm phải không còn hiển thị hoặc đã chuyển DaXoa");
    }
}
