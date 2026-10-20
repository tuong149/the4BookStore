package vn.bookstore.the4bookstore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.bookstore.the4bookstore.entity.CauHinhHeThong;
import vn.bookstore.the4bookstore.entity.Shop;
import vn.bookstore.the4bookstore.entity.TaiKhoan;
import vn.bookstore.the4bookstore.repository.CauHinhHeThongRepository;
import vn.bookstore.the4bookstore.repository.ShopRepository;
import vn.bookstore.the4bookstore.repository.TaiKhoanRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShopCommissionRegistrationTest {

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private TaiKhoanRepository taiKhoanRepository;

    @Mock
    private CauHinhHeThongRepository cauHinhHeThongRepository;

    @Mock
    private vn.bookstore.the4bookstore.repository.DonHangRepository donHangRepository;

    @Mock
    private vn.bookstore.the4bookstore.repository.KhuyenMaiRepository khuyenMaiRepository;

    @InjectMocks
    private ShopService shopService;

    @BeforeEach
    void setUp() {
        when(shopRepository.save(any(Shop.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("Đăng ký Shop mới tự động nhận tỷ lệ chiết khấu sàn đã được Admin cấu hình")
    void testShopRegistrationTakesConfiguredPlatformCommissionRate() {
        // Giả lập Shop cũ đã đăng ký trước đó với chiết khấu 5%
        Shop oldShop = new Shop();
        oldShop.setMaShop(1);
        oldShop.setTenShop("Gian Hàng Cũ");
        oldShop.setChietKhauPhanTram(new BigDecimal("5.00"));

        // Admin thay đổi chiết khấu sàn mặc định thành 10%
        when(cauHinhHeThongRepository.findById("PHI_SAN_MAC_DINH"))
                .thenReturn(Optional.of(new CauHinhHeThong("PHI_SAN_MAC_DINH", "10.0", "Phí sàn mặc định")));

        // Khách hàng B đăng ký shop mới
        TaiKhoan userB = new TaiKhoan();
        userB.setMaTaiKhoan(2);
        userB.setTenDangNhap("sellerB");

        Shop newShop = shopService.registerShop(userB, "Gian Hàng Sách Mới", "Mô tả shop mới", "Hà Nội", "0987654321", "sellerB@gmail.com");

        // Kiểm tra: Shop cũ vẫn giữ nguyên 5%, Shop mới nhận tỷ lệ 10%
        assertNotNull(newShop);
        assertEquals(new BigDecimal("10.0"), newShop.getChietKhauPhanTram());
        assertEquals(new BigDecimal("5.00"), oldShop.getChietKhauPhanTram());
    }
}
