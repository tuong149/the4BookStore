package vn.bookstore.the4bookstore.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.bookstore.the4bookstore.dto.OrderNotificationDTO;
import vn.bookstore.the4bookstore.entity.*;
import vn.bookstore.the4bookstore.repository.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@lombok.extern.slf4j.Slf4j
@Service
public class DonHangService {

    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final SanPhamRepository sanPhamRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final GioHangService gioHangService;
    private final KhuyenMaiRepository khuyenMaiRepository;
    private final VoucherDaLuuRepository voucherDaLuuRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ShopRepository shopRepository;
    private final NhaVanChuyenRepository nhaVanChuyenRepository;
    private final ShippingCalculationService shippingCalculationService;

    public DonHangService(DonHangRepository donHangRepository,
                          ChiTietDonHangRepository chiTietDonHangRepository,
                          SanPhamRepository sanPhamRepository,
                          ThanhToanRepository thanhToanRepository,
                          GioHangService gioHangService,
                          KhuyenMaiRepository khuyenMaiRepository,
                          VoucherDaLuuRepository voucherDaLuuRepository,
                          SimpMessagingTemplate messagingTemplate,
                          ShopRepository shopRepository,
                          NhaVanChuyenRepository nhaVanChuyenRepository,
                          ShippingCalculationService shippingCalculationService) {
        this.donHangRepository = donHangRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.thanhToanRepository = thanhToanRepository;
        this.gioHangService = gioHangService;
        this.khuyenMaiRepository = khuyenMaiRepository;
        this.voucherDaLuuRepository = voucherDaLuuRepository;
        this.messagingTemplate = messagingTemplate;
        this.shopRepository = shopRepository;
        this.nhaVanChuyenRepository = nhaVanChuyenRepository;
        this.shippingCalculationService = shippingCalculationService;
    }

    /**
     * Tạo đơn hàng từ giỏ hàng của khách hàng (có thể chọn lọc danh sách sản phẩm cần mua và áp mã giảm giá).
     */
    @Transactional
    public DonHang createOrder(KhachHang khachHang, String diaChiGiao,
                               String soDienThoaiGiao, String phuongThuc, String ghiChu) {
        return createOrder(khachHang, diaChiGiao, soDienThoaiGiao, phuongThuc, ghiChu, null, null);
    }

    @Transactional
    public DonHang createOrder(KhachHang khachHang, String diaChiGiao,
                               String soDienThoaiGiao, String phuongThuc, String ghiChu,
                               List<Integer> selectedProductIds) {
        return createOrder(khachHang, diaChiGiao, soDienThoaiGiao, phuongThuc, ghiChu, selectedProductIds, null);
    }

    @Transactional
    public DonHang createOrder(KhachHang khachHang, String diaChiGiao,
                               String soDienThoaiGiao, String phuongThuc, String ghiChu,
                               List<Integer> selectedProductIds,
                               String maVoucher) {
        return createOrder(khachHang, diaChiGiao, soDienThoaiGiao, phuongThuc, ghiChu, selectedProductIds, maVoucher, null);
    }

    @Transactional
    public DonHang createOrder(KhachHang khachHang, String diaChiGiao,
                               String soDienThoaiGiao, String phuongThuc, String ghiChu,
                               List<Integer> selectedProductIds,
                               String maVoucher,
                               Integer maNvc) {
        return createOrder(khachHang, diaChiGiao, soDienThoaiGiao, phuongThuc, ghiChu, selectedProductIds, maVoucher, null, maNvc);
    }

    @Transactional
    public DonHang createOrder(KhachHang khachHang, String diaChiGiao,
                               String soDienThoaiGiao, String phuongThuc, String ghiChu,
                               List<Integer> selectedProductIds,
                               String maVoucherOrder,
                               String maVoucherShipping,
                               Integer maNvc) {
        List<DonHang> orders = createOrders(khachHang, diaChiGiao, soDienThoaiGiao, phuongThuc, ghiChu,
                selectedProductIds, maVoucherOrder, maVoucherShipping, maNvc);
        return orders.isEmpty() ? null : orders.get(0);
    }

    /**
     * Tự động phân tách đơn hàng theo từng Shop (Multi-Vendor Split Orders).
     * Mỗi Shop sẽ nhận 1 đơn hàng độc lập, xử lý riêng và nhận doanh thu riêng.
     */
    @Transactional
    public List<DonHang> createOrders(KhachHang khachHang, String diaChiGiao,
                                      String soDienThoaiGiao, String phuongThuc, String ghiChu,
                                      List<Integer> selectedProductIds,
                                      String maVoucherOrder,
                                      String maVoucherShipping,
                                      Integer maNvc) {

        // 1. Lấy giỏ hàng
        List<ChiTietGioHang> allCartItems = gioHangService.getCartItems(khachHang);
        if (allCartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống, không thể đặt hàng!");
        }

        // Lọc theo các sản phẩm được tích chọn (nếu có chỉ định)
        List<ChiTietGioHang> cartItems;
        if (selectedProductIds != null && !selectedProductIds.isEmpty()) {
            cartItems = allCartItems.stream()
                    .filter(ct -> selectedProductIds.contains(ct.getSanPham().getMaSP()))
                    .toList();
        } else {
            cartItems = allCartItems;
        }

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Vui lòng chọn ít nhất một sản phẩm để đặt hàng!");
        }

        // 2. Khóa và kiểm tra tồn kho bằng Pessimistic Lock (tránh race condition)
        Map<Integer, SanPham> lockedProductMap = new HashMap<>();
        for (ChiTietGioHang ct : cartItems) {
            Integer maSP = ct.getSanPham().getMaSP();
            SanPham sp = sanPhamRepository.findByIdWithLock(maSP)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm mã: " + maSP));

            boolean isProductLocked = "BiKhoaBoiAdmin".equalsIgnoreCase(sp.getTrangThaiKhoa())
                    || "Khoa".equalsIgnoreCase(sp.getTrangThaiKhoa())
                    || "NgungBan".equalsIgnoreCase(sp.getTrangThai());
            boolean isShopLocked = sp.getShop() != null && !"HoatDong".equalsIgnoreCase(sp.getShop().getTrangThai());
            if (isProductLocked || isShopLocked) {
                throw new RuntimeException("Sản phẩm \"" + sp.getTenSP() + "\" hiện đang tạm ngừng kinh doanh hoặc gian hàng đang bị tạm khóa, vui lòng bỏ chọn sản phẩm này để đặt hàng!");
            }

            if (sp.getSoLuongTon() < ct.getSoLuong()) {
                throw new RuntimeException("Sản phẩm \"" + sp.getTenSP() + "\" không đủ số lượng tồn kho (chỉ còn " 
                        + sp.getSoLuongTon() + " sản phẩm)!");
            }
            lockedProductMap.put(maSP, sp);
        }

        // 3. Nhóm các sản phẩm theo từng Shop
        Map<Integer, List<ChiTietGioHang>> itemsByShopId = new LinkedHashMap<>();
        for (ChiTietGioHang ct : cartItems) {
            SanPham sp = lockedProductMap.get(ct.getSanPham().getMaSP());
            Shop spShop = sp.getShop();
            Integer sId = (spShop != null && spShop.getMaShop() != null) ? spShop.getMaShop() : null;
            if (sId == null) {
                Shop defShop = shopRepository.findAll().stream()
                        .filter(s -> "HoatDong".equalsIgnoreCase(s.getTrangThai()))
                        .findFirst().orElse(null);
                if (defShop != null) {
                    sId = defShop.getMaShop();
                    sp.setShop(defShop);
                    sanPhamRepository.save(sp);
                } else {
                    sId = 1;
                }
            }
            itemsByShopId.computeIfAbsent(sId, k -> new ArrayList<>()).add(ct);
        }

        // 4. Tính toán tiền hàng từng Shop và tổng tiền hàng cả giỏ
        int totalSubtotal = 0;
        Map<Integer, Integer> shopSubtotals = new LinkedHashMap<>();
        for (Map.Entry<Integer, List<ChiTietGioHang>> entry : itemsByShopId.entrySet()) {
            int sub = 0;
            for (ChiTietGioHang ct : entry.getValue()) {
                SanPham sp = lockedProductMap.get(ct.getSanPham().getMaSP());
                sub += sp.getGiaBan() * ct.getSoLuong();
            }
            shopSubtotals.put(entry.getKey(), sub);
            totalSubtotal += sub;
        }

        // 5. Xác định Nhà Vận Chuyển
        NhaVanChuyen nvc = null;
        if (maNvc != null) {
            nvc = nhaVanChuyenRepository.findById(maNvc).orElse(null);
        }
        if (nvc == null) {
            nvc = nhaVanChuyenRepository.findAll().stream()
                    .filter(c -> "HoatDong".equalsIgnoreCase(c.getTrangThai()))
                    .findFirst().orElse(null);
        }

        // Tính cước vận chuyển riêng cho từng Shop theo khoảng cách địa lý (Haversine)
        Map<Integer, ShippingCalculationService.ShippingEstimate> shopShippingEstimates = new HashMap<>();
        int totalCalculatedShippingFee = 0;
        for (Integer sId : itemsByShopId.keySet()) {
            Shop s = shopRepository.findById(sId).orElse(null);
            String sAddr = (s != null && s.getDiaChiShop() != null && !s.getDiaChiShop().isBlank())
                    ? s.getDiaChiShop() : "Quận 1, TP. Hồ Chí Minh";
            ShippingCalculationService.ShippingEstimate est = shippingCalculationService.calculateFee(sAddr, diaChiGiao, nvc);
            shopShippingEstimates.put(sId, est);
            totalCalculatedShippingFee += est.getShippingFee();
        }

        // 6. Xử lý giảm giá từ mã khuyến mãi toàn sàn
        int totalOrderDiscount = 0;
        int totalShippingDiscount = 0;
        KhuyenMai voucherOrder = null;
        KhuyenMai voucherShipping = null;

        // Nếu client cũ gửi 1 mã duy nhất qua maVoucherOrder
        if ((maVoucherShipping == null || maVoucherShipping.isBlank()) && maVoucherOrder != null && !maVoucherOrder.isBlank()) {
            Optional<KhuyenMai> kmOpt = khuyenMaiRepository.findByMaCode(maVoucherOrder.trim().toUpperCase());
            if (kmOpt.isPresent() && kmOpt.get().isShippingDiscount()) {
                maVoucherShipping = maVoucherOrder;
                maVoucherOrder = null;
            }
        }

        // Xử lý mã giảm giá sản phẩm (ORDER_DISCOUNT)
        if (maVoucherOrder != null && !maVoucherOrder.isBlank()) {
            String cleanCode = maVoucherOrder.trim().toUpperCase();
            KhuyenMai km = khuyenMaiRepository.findByMaCode(cleanCode)
                    .orElseThrow(() -> new RuntimeException("Mã giảm giá [" + cleanCode + "] không tồn tại trên hệ thống!"));

            if (!km.isOrderDiscount()) {
                throw new RuntimeException("Mã [" + cleanCode + "] không phải là mã giảm giá sản phẩm! Vui lòng nhập vào ô mã vận chuyển.");
            }
            if (!"HoatDong".equalsIgnoreCase(km.getTrangThai())) {
                throw new RuntimeException("Mã giảm giá [" + cleanCode + "] đã hết hạn hoặc tạm ngưng sử dụng!");
            }
            if (km.getNgayBatDau() != null && LocalDateTime.now().isBefore(km.getNgayBatDau())) {
                throw new RuntimeException("Mã giảm giá [" + cleanCode + "] chưa đến ngày áp dụng!");
            }
            if (km.getNgayKetThuc() != null && LocalDateTime.now().isAfter(km.getNgayKetThuc())) {
                throw new RuntimeException("Mã giảm giá [" + cleanCode + "] đã hết hạn sử dụng!");
            }
            if (km.getSoLuongToiDa() != null && km.getSoLuongDaDung() != null && km.getSoLuongDaDung() >= km.getSoLuongToiDa()) {
                throw new RuntimeException("Mã giảm giá [" + cleanCode + "] đã hết lượt sử dụng!");
            }
            if (km.getDonToiThieu() != null && totalSubtotal < km.getDonToiThieu()) {
                throw new RuntimeException("Đơn hàng chưa đạt mức tối thiểu " + String.format("%,d đ", km.getDonToiThieu()) + " để áp dụng mã [" + cleanCode + "]!");
            }

            if ("PhanTram".equalsIgnoreCase(km.getLoaiGiam())) {
                totalOrderDiscount = (int) Math.round(totalSubtotal * (km.getGiaTriGiam() / 100.0));
                if (km.getGiamToiDa() != null && totalOrderDiscount > km.getGiamToiDa()) {
                    totalOrderDiscount = km.getGiamToiDa();
                }
                if (totalOrderDiscount > totalSubtotal) {
                    totalOrderDiscount = totalSubtotal;
                }
            } else if ("TienCoDinh".equalsIgnoreCase(km.getLoaiGiam())) {
                totalOrderDiscount = Math.min(totalSubtotal, km.getGiaTriGiam());
            } else {
                totalOrderDiscount = Math.min(totalSubtotal, km.getGiaTriGiam() != null ? km.getGiaTriGiam() : 0);
            }

            km.setSoLuongDaDung(km.getSoLuongDaDung() != null ? km.getSoLuongDaDung() + 1 : 1);
            khuyenMaiRepository.save(km);
            voucherOrder = km;

            markVoucherUsedInWallet(khachHang, km);
        }

        // Kiểm tra chính sách Miễn phí vận chuyển toàn sàn:
        final int FREE_SHIPPING_THRESHOLD = 350000;
        if (totalSubtotal >= FREE_SHIPPING_THRESHOLD) {
            totalShippingDiscount = totalCalculatedShippingFee;
        }

        // Xử lý mã miễn phí vận chuyển (SHIPPING_DISCOUNT)
        if (maVoucherShipping != null && !maVoucherShipping.isBlank()) {
            String cleanCode = maVoucherShipping.trim().toUpperCase();
            KhuyenMai km = khuyenMaiRepository.findByMaCode(cleanCode)
                    .orElseThrow(() -> new RuntimeException("Mã miễn phí vận chuyển [" + cleanCode + "] không tồn tại trên hệ thống!"));

            if (!km.isShippingDiscount()) {
                throw new RuntimeException("Mã [" + cleanCode + "] không phải là mã giảm phí vận chuyển! Vui lòng nhập vào ô mã giảm giá đơn hàng.");
            }
            if (voucherOrder != null && voucherOrder.getMaCode().equalsIgnoreCase(cleanCode)) {
                throw new RuntimeException("Không thể sử dụng cùng một mã cho cả 2 loại ưu đãi!");
            }
            if (!"HoatDong".equalsIgnoreCase(km.getTrangThai())) {
                throw new RuntimeException("Mã vận chuyển [" + cleanCode + "] đã hết hạn hoặc tạm ngưng sử dụng!");
            }
            if (km.getNgayBatDau() != null && LocalDateTime.now().isBefore(km.getNgayBatDau())) {
                throw new RuntimeException("Mã vận chuyển [" + cleanCode + "] chưa đến ngày áp dụng!");
            }
            if (km.getNgayKetThuc() != null && LocalDateTime.now().isAfter(km.getNgayKetThuc())) {
                throw new RuntimeException("Mã vận chuyển [" + cleanCode + "] đã hết hạn sử dụng!");
            }
            if (km.getSoLuongToiDa() != null && km.getSoLuongDaDung() != null && km.getSoLuongDaDung() >= km.getSoLuongToiDa()) {
                throw new RuntimeException("Mã vận chuyển [" + cleanCode + "] đã hết lượt sử dụng!");
            }
            if (km.getDonToiThieu() != null && totalSubtotal < km.getDonToiThieu()) {
                throw new RuntimeException("Đơn hàng chưa đạt mức tối thiểu " + String.format("%,d đ", km.getDonToiThieu()) + " để áp dụng mã vận chuyển [" + cleanCode + "]!");
            }

            int maxFreeshipDiscount = km.getGiaTriGiam() != null ? km.getGiaTriGiam() : 30000;
            totalShippingDiscount = Math.max(totalShippingDiscount, Math.min(totalCalculatedShippingFee, maxFreeshipDiscount));

            km.setSoLuongDaDung(km.getSoLuongDaDung() != null ? km.getSoLuongDaDung() + 1 : 1);
            khuyenMaiRepository.save(km);
            voucherShipping = km;

            markVoucherUsedInWallet(khachHang, km);
        }

        // 7. Tạo từng đơn hàng độc lập theo từng Shop
        List<DonHang> createdOrders = new ArrayList<>();
        int shopCount = itemsByShopId.size();
        int currentShopIdx = 0;
        int allocatedOrderDiscount = 0;
        int allocatedShippingDiscount = 0;

        for (Map.Entry<Integer, List<ChiTietGioHang>> entry : itemsByShopId.entrySet()) {
            Integer sId = entry.getKey();
            List<ChiTietGioHang> shopItems = entry.getValue();
            int shopSub = shopSubtotals.get(sId);
            Shop shop = shopRepository.findById(sId).orElse(null);

            // Phân bổ giảm giá sản phẩm theo tỷ lệ giá trị tiền hàng của từng Shop
            int shopOrderDiscount = 0;
            if (totalOrderDiscount > 0 && totalSubtotal > 0) {
                if (currentShopIdx == shopCount - 1) {
                    shopOrderDiscount = Math.max(0, totalOrderDiscount - allocatedOrderDiscount);
                } else {
                    shopOrderDiscount = (int) Math.round((double) shopSub / totalSubtotal * totalOrderDiscount);
                    allocatedOrderDiscount += shopOrderDiscount;
                }
                if (shopOrderDiscount > shopSub) {
                    shopOrderDiscount = shopSub;
                }
            }

            // Phí ship và giảm giá freeship cho shop theo khoảng cách:
            ShippingCalculationService.ShippingEstimate est = shopShippingEstimates.get(sId);
            int shopShippingFee = (est != null) ? est.getShippingFee() : 25000;
            int shopShippingDiscount = 0;
            if (totalSubtotal >= FREE_SHIPPING_THRESHOLD) {
                shopShippingDiscount = shopShippingFee;
            } else if (totalShippingDiscount > 0 && totalCalculatedShippingFee > 0) {
                if (currentShopIdx == shopCount - 1) {
                    shopShippingDiscount = Math.min(shopShippingFee, Math.max(0, totalShippingDiscount - allocatedShippingDiscount));
                } else {
                    shopShippingDiscount = Math.min(shopShippingFee, (int) Math.round((double) shopShippingFee / totalCalculatedShippingFee * totalShippingDiscount));
                    allocatedShippingDiscount += shopShippingDiscount;
                }
            }

            int shopTienHangSauGiam = Math.max(0, shopSub - shopOrderDiscount);
            int shopPhiShipSauGiam = Math.max(0, shopShippingFee - shopShippingDiscount);
            int shopFinalTotal = Math.max(0, shopTienHangSauGiam + shopPhiShipSauGiam);

            java.math.BigDecimal chietKhauApp = (shop != null && shop.getChietKhauPhanTram() != null)
                    ? shop.getChietKhauPhanTram() : new java.math.BigDecimal("10.00");
            int tienPhiSan = (int) Math.round(shopSub * (chietKhauApp.doubleValue() / 100.0));
            int tienThucNhanShop = Math.max(0, shopTienHangSauGiam - tienPhiSan);

            DonHang donHang = new DonHang();
            donHang.setKhachHang(khachHang);
            donHang.setShop(shop);
            donHang.setNhaVanChuyen(nvc);
            donHang.setPhiVanChuyen(shopShippingFee);
            donHang.setTienGiamFreeship(shopShippingDiscount);
            donHang.setChietKhauAppPhanTram(chietKhauApp);
            donHang.setTienPhiSan(tienPhiSan);
            donHang.setTienThucNhanShop(tienThucNhanShop);
            donHang.setTongTien(shopFinalTotal);
            donHang.setTienGiam(shopOrderDiscount);
            donHang.setKhuyenMai(voucherOrder);
            donHang.setKhuyenMaiFreeship(voucherShipping);
            donHang.setPhuongThucThanhToan(phuongThuc != null ? phuongThuc : "COD");
            if ("VIETQR".equalsIgnoreCase(phuongThuc) || "ChuyenKhoan".equalsIgnoreCase(phuongThuc)) {
                donHang.setTrangThai("ChoThanhToan");
                donHang.setTrangThaiThanhToan("ChuaThanhToan");
            } else {
                donHang.setTrangThai("ChoXuLy");
                donHang.setTrangThaiThanhToan("ChuaThanhToan");
            }
            donHang.setNgayDat(LocalDateTime.now());
            donHang.setDiaChiGiao(diaChiGiao);
            donHang.setSoDienThoaiGiao(soDienThoaiGiao);

            donHang = donHangRepository.save(donHang);

            // Chi tiết đơn hàng và trừ tồn kho
            List<ChiTietDonHang> chiTietList = new ArrayList<>();
            for (ChiTietGioHang ctGH : shopItems) {
                SanPham sp = lockedProductMap.get(ctGH.getSanPham().getMaSP());
                ChiTietDonHang ctDH = new ChiTietDonHang();
                ctDH.setDonHang(donHang);
                ctDH.setSanPham(sp);
                ctDH.setSoLuong(ctGH.getSoLuong());
                ctDH.setDonGia(sp.getGiaBan());

                chiTietDonHangRepository.save(ctDH);
                chiTietList.add(ctDH);

                sp.setSoLuongTon(sp.getSoLuongTon() - ctGH.getSoLuong());
                sp.setSoLuongDaBan((sp.getSoLuongDaBan() != null ? sp.getSoLuongDaBan() : 0) + ctGH.getSoLuong());
                sanPhamRepository.save(sp);
            }
            donHang.setChiTietDonHangs(chiTietList);
            donHang = donHangRepository.save(donHang);
            createdOrders.add(donHang);

            // Bản ghi thanh toán cho đơn của shop
            ThanhToan thanhToan = new ThanhToan();
            thanhToan.setDonHang(donHang);
            thanhToan.setPhuongThuc(phuongThuc != null ? phuongThuc : "COD");
            boolean isOnlinePayment = "VIETQR".equalsIgnoreCase(phuongThuc) || "ChuyenKhoan".equalsIgnoreCase(phuongThuc);
            thanhToan.setTrangThai(isOnlinePayment ? "ChoThanhToan" : "ChuaThanhToan");
            thanhToan.setSoTien(shopFinalTotal);
            thanhToan.setNoiDung("Thanh toán đơn hàng #" + donHang.getMaDH() + " (" + (shop != null ? shop.getTenShop() : "Gian hàng") + ")");
            thanhToanRepository.save(thanhToan);

            // Bắn thông báo Real-time qua WebSocket
            try {
                OrderNotificationDTO noti = OrderNotificationDTO.builder()
                        .maDH(donHang.getMaDH())
                        .tenKhachHang(khachHang.getHoTen() != null ? khachHang.getHoTen() : "Khách hàng #" + khachHang.getMaKH())
                        .soDienThoai(donHang.getSoDienThoaiGiao())
                        .diaChiGiao(donHang.getDiaChiGiao())
                        .tongTien(donHang.getTongTien())
                        .soLuongMon(shopItems.size())
                        .trangThai(donHang.getTrangThai())
                        .ngayDat(donHang.getNgayDat())
                        .maShop(shop != null ? shop.getMaShop() : null)
                        .tenShop(shop != null ? shop.getTenShop() : null)
                        .message("Có đơn hàng mới #" + donHang.getMaDH() + " từ " + (khachHang.getHoTen() != null ? khachHang.getHoTen() : "Khách hàng"))
                        .build();
                if (shop != null && shop.getMaShop() != null) {
                    messagingTemplate.convertAndSend("/topic/vendor/" + shop.getMaShop() + "/orders", noti);
                    log.info(">>> Đã gửi WebSocket thông báo đơn hàng #{} tới Shop #{} ({})", donHang.getMaDH(), shop.getMaShop(), shop.getTenShop());
                } else {
                    messagingTemplate.convertAndSend("/topic/admin/orders", noti);
                }
            } catch (Exception e) {
                log.error("Lỗi gửi WebSocket thông báo đơn hàng #{}: ", donHang.getMaDH(), e);
            }

            currentShopIdx++;
        }

        // 8. Xóa các món đã đặt khỏi giỏ hàng
        for (ChiTietGioHang ctGH : cartItems) {
            gioHangService.removeFromCart(khachHang, ctGH.getSanPham().getMaSP());
        }

        return createdOrders;
    }

    /**
     * Lấy danh sách đơn hàng của khách hàng, sắp xếp theo ngày đặt giảm dần
     */
    public List<DonHang> getOrdersByKhachHang(KhachHang khachHang) {
        return donHangRepository.findByKhachHangOrderByNgayDatDesc(khachHang);
    }

    /**
     * Lấy đơn hàng theo ID
     */
    public DonHang getOrderById(Integer maDH) {
        return donHangRepository.findById(maDH)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + maDH));
    }

    /**
     * Hủy đơn hàng (khi trạng thái là ChoXuLy / DonHangMoi).
     * Hoàn lại số lượng tồn kho và kích hoạt hoàn tiền nếu đã thanh toán trực tuyến.
     */
    @Transactional
    public void cancelOrder(Integer maDH, KhachHang khachHang, String lyDoHuy) {
        DonHang donHang = donHangRepository.findById(maDH)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + maDH));

        // Kiểm tra quyền sở hữu
        if (!donHang.getKhachHang().getMaKH().equals(khachHang.getMaKH())) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này!");
        }

        // Cho phép hủy khi trạng thái là ChoXuLy hoặc DonHangMoi
        if (!"ChoXuLy".equalsIgnoreCase(donHang.getTrangThai()) && !"DonHangMoi".equalsIgnoreCase(donHang.getTrangThai())) {
            throw new RuntimeException("Chỉ có thể hủy trực tiếp đơn hàng ở trạng thái 'Đơn mới / Chờ xử lý'!");
        }

        // Hoàn lại tồn kho và số lượng đã bán
        if (donHang.getChiTietDonHangs() != null) {
            for (ChiTietDonHang ct : donHang.getChiTietDonHangs()) {
                if (ct.getSanPham() != null && ct.getSoLuong() != null) {
                    sanPhamRepository.increaseStock(ct.getSanPham().getMaSP(), ct.getSoLuong());
                    SanPham sp = sanPhamRepository.findById(ct.getSanPham().getMaSP()).orElse(null);
                    if (sp != null && sp.getSoLuongDaBan() != null) {
                        sp.setSoLuongDaBan(Math.max(0, sp.getSoLuongDaBan() - ct.getSoLuong()));
                        sanPhamRepository.save(sp);
                    }
                }
            }
        }

        donHang.setTrangThai("DaHuy");
        if ("DaThanhToan".equalsIgnoreCase(donHang.getTrangThaiThanhToan())) {
            donHang.setTrangThaiThanhToan("DaHoanTien");
        }
        donHang.setLyDoHuy(lyDoHuy != null && !lyDoHuy.isBlank() ? lyDoHuy : "Khách hàng tự hủy");
        donHangRepository.save(donHang);
    }

    private void markVoucherUsedInWallet(KhachHang khachHang, KhuyenMai km) {
        try {
            if (khachHang != null && km != null) {
                Optional<VoucherDaLuu> vdlOpt = voucherDaLuuRepository.findByKhachHangAndKhuyenMai(khachHang, km);
                if (vdlOpt.isPresent()) {
                    VoucherDaLuu vdl = vdlOpt.get();
                    vdl.setTrangThai("DaDung");
                    vdl.setNgaySuDung(LocalDateTime.now());
                    voucherDaLuuRepository.save(vdl);
                }
            }
        } catch (Exception ignored) {}
    }
}
