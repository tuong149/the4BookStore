package vn.bookstore.the4bookstore.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.bookstore.the4bookstore.entity.*;
import vn.bookstore.the4bookstore.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DonHangService {

    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final SanPhamRepository sanPhamRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final GioHangService gioHangService;

    public DonHangService(DonHangRepository donHangRepository,
                          ChiTietDonHangRepository chiTietDonHangRepository,
                          SanPhamRepository sanPhamRepository,
                          ThanhToanRepository thanhToanRepository,
                          GioHangService gioHangService) {
        this.donHangRepository = donHangRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.thanhToanRepository = thanhToanRepository;
        this.gioHangService = gioHangService;
    }

    /**
     * Tạo đơn hàng từ giỏ hàng của khách hàng.
     * - Validate giỏ hàng không rỗng
     * - Validate tồn kho đủ
     * - Tạo DON_HANG và CHI_TIET_DON_HANG
     * - Trừ số lượng tồn kho
     * - Tạo bản ghi THANH_TOAN
     * - Xóa giỏ hàng
     */
    /**
     * Tạo đơn hàng từ giỏ hàng của khách hàng (có thể chọn lọc danh sách sản phẩm cần mua).
     * - selectedProductIds: danh sách mã sản phẩm được tích chọn mua (nếu null/rỗng thì mua toàn bộ giỏ)
     * - Validate tồn kho đủ cho các món được chọn
     * - Tạo DON_HANG và CHI_TIET_DON_HANG cho các món được chọn
     * - Trừ số lượng tồn kho
     * - Tạo bản ghi THANH_TOAN
     * - Xóa các món được chọn khỏi giỏ hàng
     */
    @Transactional
    public DonHang createOrder(KhachHang khachHang, String diaChiGiao,
                               String soDienThoaiGiao, String phuongThuc, String ghiChu) {
        return createOrder(khachHang, diaChiGiao, soDienThoaiGiao, phuongThuc, ghiChu, null);
    }

    @Transactional
    public DonHang createOrder(KhachHang khachHang, String diaChiGiao,
                               String soDienThoaiGiao, String phuongThuc, String ghiChu,
                               List<Integer> selectedProductIds) {

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

        // 2. Kiểm tra tồn kho
        for (ChiTietGioHang ct : cartItems) {
            SanPham sp = ct.getSanPham();
            if (sp.getSoLuongTon() < ct.getSoLuong()) {
                throw new RuntimeException("Sản phẩm \"" + sp.getTenSP() + "\" chỉ còn " 
                        + sp.getSoLuongTon() + " cuốn trong kho!");
            }
        }

        // 3. Tạo đơn hàng
        DonHang donHang = new DonHang();
        donHang.setKhachHang(khachHang);
        donHang.setNgayDat(LocalDateTime.now());
        donHang.setDiaChiGiao(diaChiGiao);
        donHang.setSoDienThoaiGiao(soDienThoaiGiao);
        donHang.setTrangThai("ChoXuLy");
        donHang.setTienGiam(0);

        // Lưu đơn hàng trước để lấy maDH
        donHang = donHangRepository.save(donHang);

        // 4. Tạo chi tiết đơn hàng và tính tổng tiền
        int tongTien = 0;
        List<ChiTietDonHang> chiTietList = new ArrayList<>();

        for (ChiTietGioHang ctGH : cartItems) {
            SanPham sp = ctGH.getSanPham();

            ChiTietDonHang ctDH = new ChiTietDonHang();
            ctDH.setDonHang(donHang);
            ctDH.setSanPham(sp);
            ctDH.setSoLuong(ctGH.getSoLuong());
            ctDH.setDonGia(sp.getGiaBan());

            chiTietDonHangRepository.save(ctDH);
            chiTietList.add(ctDH);

            // 5. Trừ tồn kho
            sp.setSoLuongTon(sp.getSoLuongTon() - ctGH.getSoLuong());
            sanPhamRepository.save(sp);

            tongTien += sp.getGiaBan() * ctGH.getSoLuong();
        }

        // 6. Cập nhật tổng tiền
        donHang.setTongTien(tongTien);
        donHang.setChiTietDonHangs(chiTietList);
        donHang = donHangRepository.save(donHang);

        // 7. Tạo bản ghi thanh toán
        ThanhToan thanhToan = new ThanhToan();
        thanhToan.setDonHang(donHang);
        thanhToan.setPhuongThuc(phuongThuc != null ? phuongThuc : "COD");
        thanhToan.setTrangThai("ChoThanhToan");
        thanhToan.setSoTien(tongTien);
        thanhToan.setNoiDung("Thanh toán đơn hàng #" + donHang.getMaDH());
        thanhToanRepository.save(thanhToan);

        // 8. Xóa các món đã đặt khỏi giỏ hàng
        for (ChiTietGioHang ctGH : cartItems) {
            gioHangService.removeFromCart(khachHang, ctGH.getSanPham().getMaSP());
        }

        return donHang;
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
     * Hủy đơn hàng (chỉ khi trạng thái là ChoXuLy).
     * Hoàn lại số lượng tồn kho.
     */
    @Transactional
    public void cancelOrder(Integer maDH, KhachHang khachHang, String lyDoHuy) {
        DonHang donHang = donHangRepository.findById(maDH)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + maDH));

        // Kiểm tra quyền sở hữu
        if (!donHang.getKhachHang().getMaKH().equals(khachHang.getMaKH())) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này!");
        }

        // Chỉ cho phép hủy khi trạng thái là ChoXuLy
        if (!"ChoXuLy".equals(donHang.getTrangThai())) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng ở trạng thái 'Chờ xử lý'!");
        }

        // Hoàn lại tồn kho
        if (donHang.getChiTietDonHangs() != null) {
            for (ChiTietDonHang ct : donHang.getChiTietDonHangs()) {
                SanPham sp = ct.getSanPham();
                sp.setSoLuongTon(sp.getSoLuongTon() + ct.getSoLuong());
                sanPhamRepository.save(sp);
            }
        }

        donHang.setTrangThai("DaHuy");
        donHang.setLyDoHuy(lyDoHuy != null && !lyDoHuy.isBlank() ? lyDoHuy : "Khách hàng tự hủy");
        donHangRepository.save(donHang);
    }
}
