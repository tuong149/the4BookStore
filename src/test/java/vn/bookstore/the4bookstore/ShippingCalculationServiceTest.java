package vn.bookstore.the4bookstore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vn.bookstore.the4bookstore.entity.ChiTietGioHang;
import vn.bookstore.the4bookstore.entity.NhaVanChuyen;
import vn.bookstore.the4bookstore.entity.SanPham;
import vn.bookstore.the4bookstore.entity.Shop;
import vn.bookstore.the4bookstore.service.ShippingCalculationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShippingCalculationServiceTest {

    private ShippingCalculationService shippingService;
    private NhaVanChuyen nvc;

    @BeforeEach
    void setUp() {
        shippingService = new ShippingCalculationService();
        nvc = new NhaVanChuyen();
        nvc.setMaNvc(1);
        nvc.setTenNvc("Giao Hàng Nhanh");
        nvc.setPhiCoBan(25000);
        nvc.setThoiGianDuKien("2 - 3 ngày");
    }

    @Test
    void testSameDistrictOrUltraCloseDistance() {
        String shopAddr = "123 Nguyễn Trãi, Phường 2, Quận 5, TP. Hồ Chí Minh";
        String customerAddr = "456 Trần Hưng Đạo, Phường 7, Quận 5, TP. Hồ Chí Minh";

        ShippingCalculationService.ShippingEstimate estimate = shippingService.calculateFee(shopAddr, customerAddr, nvc);

        assertNotNull(estimate);
        assertTrue(estimate.getDistanceKm() <= 5.0, "Khoảng cách cùng quận phải <= 5km");
        assertEquals(16000, estimate.getShippingFee(), "Cước nội thành siêu gần phải là 16.000 đ");
    }

    @Test
    void testInterRegionDistanceHanoiToHcm() {
        String shopAddr = "15 Duy Tân, Dịch Vọng Hậu, Cầu Giấy, Hà Nội";
        String customerAddr = "1 Võ Văn Ngân, Linh Chiểu, TP. Thủ Đức, TP. Hồ Chí Minh";

        ShippingCalculationService.ShippingEstimate estimate = shippingService.calculateFee(shopAddr, customerAddr, nvc);

        assertNotNull(estimate);
        assertTrue(estimate.getDistanceKm() > 1000.0, "Khoảng cách Hà Nội - TP.HCM phải > 1000km");
        assertEquals(38000, estimate.getShippingFee(), "Cước liên miền Bắc - Nam phải là 38.000 đ");
        assertEquals("Liên miền (Bắc - Nam)", estimate.getTierName());
    }

    @Test
    void testMultiVendorOrderSplit() {
        // Shop 1 ở TP.HCM
        Shop shop1 = new Shop();
        shop1.setMaShop(1);
        shop1.setTenShop("Gian Hàng Sách Sài Gòn");
        shop1.setDiaChiShop("Quận 1, TP. Hồ Chí Minh");

        SanPham sp1 = new SanPham();
        sp1.setMaSP(101);
        sp1.setTenSP("Đắc Nhân Tâm");
        sp1.setGiaBan(100000);
        sp1.setShop(shop1);

        ChiTietGioHang ct1 = new ChiTietGioHang();
        ct1.setSanPham(sp1);
        ct1.setSoLuong(1);

        // Shop 2 ở Hà Nội
        Shop shop2 = new Shop();
        shop2.setMaShop(2);
        shop2.setTenShop("Nhà Sách Tràng An");
        shop2.setDiaChiShop("Cầu Giấy, Hà Nội");

        SanPham sp2 = new SanPham();
        sp2.setMaSP(202);
        sp2.setTenSP("Lịch Sử Văn Minh");
        sp2.setGiaBan(120000);
        sp2.setShop(shop2);

        ChiTietGioHang ct2 = new ChiTietGioHang();
        ct2.setSanPham(sp2);
        ct2.setSoLuong(1);

        String customerAddr = "Bến Nghé, Quận 1, TP. Hồ Chí Minh";

        ShippingCalculationService.CartShippingCalculationResult result = shippingService.calculateCartShipping(
                List.of(ct1, ct2), customerAddr, nvc, 350000, 0
        );

        assertEquals(2, result.getShopBreakdowns().size(), "Phải phân tách thành 2 kiện hàng theo 2 shop");

        ShippingCalculationService.ShopShippingBreakdown b1 = result.getShopBreakdowns().stream()
                .filter(b -> b.getShopId().equals(1)).findFirst().orElseThrow();
        ShippingCalculationService.ShopShippingBreakdown b2 = result.getShopBreakdowns().stream()
                .filter(b -> b.getShopId().equals(2)).findFirst().orElseThrow();

        assertEquals(16000, b1.getShippingFee(), "Kiện Shop 1 (Quận 1 -> Quận 1) phải là 16.000 đ");
        assertEquals(38000, b2.getShippingFee(), "Kiện Shop 2 (Hà Nội -> Quận 1) phải là 38.000 đ");

        assertEquals(54000, result.getTotalOriginalShippingFee(), "Tổng tiền ship 2 kiện phải là 54.000 đ");
        assertEquals(54000, result.getFinalShippingFee(), "Tổng thanh toán ship phải là 54.000 đ khi chưa có voucher");
    }
}
