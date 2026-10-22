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

    @Autowired
    private vn.bookstore.the4bookstore.repository.DanhMucRepository danhMucRepository;

    @Autowired
    private vn.bookstore.the4bookstore.repository.ShopRepository shopRepository;

    @Test
    public void testDeleteProductWithoutRollback() {
        // Tạo một sản phẩm test chuyên dụng để xóa
        SanPham testProduct = new SanPham();
        testProduct.setTenSP("Sản phẩm test xóa an toàn " + System.currentTimeMillis());
        testProduct.setGiaBan(50000);
        testProduct.setSoLuongTon(10);
        testProduct.setTrangThai("HoatDong");
        danhMucRepository.findAll().stream().findFirst().ifPresent(testProduct::setDanhMuc);
        shopRepository.findAll().stream().findFirst().ifPresent(testProduct::setShop);
        testProduct = sanPhamRepository.save(testProduct);
        Integer id = testProduct.getMaSP();

        assertTrue(sanPhamRepository.existsById(id), "Sản phẩm test phải tồn tại trước khi xóa");

        // Gọi deleteProduct
        assertDoesNotThrow(() -> {
            sanPhamService.deleteProduct(id);
        }, "Hàm deleteProduct không được ném ngoại lệ hoặc rollback");

        // Kiểm tra sau khi xóa: sản phẩm hoặc bị xóa khỏi DB hoặc có trạng thái DaXoa
        SanPham sp = sanPhamRepository.findById(id).orElse(null);
        assertTrue(sp == null || "DaXoa".equalsIgnoreCase(sp.getTrangThai()), "Sản phẩm phải không còn hiển thị hoặc đã chuyển DaXoa");
    }
}
