package vn.bookstore.the4bookstore.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import vn.bookstore.the4bookstore.dto.MonthlyRevenueDTO;
import vn.bookstore.the4bookstore.dto.TopSellingBookDTO;
import vn.bookstore.the4bookstore.repository.ChiTietDonHangRepository;
import vn.bookstore.the4bookstore.repository.DonHangRepository;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final DonHangRepository donHangRepository;

    private final ChiTietDonHangRepository chiTietDonHangRepository;

    public ReportService(DonHangRepository donHangRepository,
                         ChiTietDonHangRepository chiTietDonHangRepository) {
        this.donHangRepository = donHangRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
    }

    @PersistenceContext
    private EntityManager entityManager;

    public BigDecimal getRevenueByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        Long revenue = donHangRepository.getRevenueByDateRange(startOfDay, endOfDay);
        return revenue != null ? new BigDecimal(revenue) : BigDecimal.ZERO;
    }

    public BigDecimal getThisMonthRevenue() {
        try {
            LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime nextMonth = startOfMonth.plusMonths(1);

            Long revenue = donHangRepository.getRevenueByDateRange(startOfMonth, nextMonth);
            if (revenue != null) {
                return new BigDecimal(revenue);
            }
        } catch (Exception e) {
            try {
                int currentMonth = LocalDate.now().getMonthValue();
                for (MonthlyRevenueDTO dto : getRevenueByMonth()) {
                    if (dto.getMonth().equals(String.format("Tháng %02d", currentMonth))) {
                        return dto.getRevenue();
                    }
                }
            } catch (Exception ignored) {}
        }
        return BigDecimal.ZERO;
    }

    public List<MonthlyRevenueDTO> getRevenueByMonth() {
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        Map<Integer, BigDecimal> dbMap = new HashMap<>();

        try {
            String sql = "CALL sp_DoanhThuTheoThang()";
            Query query = entityManager.createNativeQuery(sql);
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();

            if (results != null) {
                for (Object[] row : results) {
                    if (row != null && row.length >= 3 && row[0] != null && row[1] != null) {
                        Integer year = ((Number) row[0]).intValue();
                        Integer month = ((Number) row[1]).intValue();
                        if (year == currentYear) {
                            BigDecimal revenue = row[2] != null ? new BigDecimal(((Number) row[2]).longValue()) : BigDecimal.ZERO;
                            dbMap.put(month, revenue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Keep dbMap empty on query error and continue to build full month list
        }

        List<MonthlyRevenueDTO> dtos = new ArrayList<>();
        // Trả về từ Tháng 1 đến Tháng hiện tại (Tháng 10) theo thứ tự thời gian tăng dần
        for (int m = 1; m <= currentMonth; m++) {
            BigDecimal revenue = dbMap.getOrDefault(m, BigDecimal.ZERO);
            dtos.add(new MonthlyRevenueDTO(String.format("Tháng %02d", m), revenue));
        }
        return dtos;
    }

    public List<TopSellingBookDTO> getTopSellingBooks() {
        try {
            String sql = "CALL sp_TopSachBanChay(10)";
            Query query = entityManager.createNativeQuery(sql);
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();

            List<TopSellingBookDTO> dtos = new ArrayList<>();
            if (results != null) {
                for (Object[] row : results) {
                    if (row != null && row.length >= 3 && row[0] != null) {
                        Long maSP = ((Number) row[0]).longValue();
                        String tenSP = row[1] != null ? (String) row[1] : "Sản phẩm #" + maSP;
                        Long soLuongBan = row[2] != null ? ((Number) row[2]).longValue() : 0L;
                        Integer giaBan = (row.length > 3 && row[3] != null) ? ((Number) row[3]).intValue() : 0;
                        Long doanhThuDongGop = (row.length > 4 && row[4] != null) ? ((Number) row[4]).longValue() : 0L;
                        dtos.add(new TopSellingBookDTO(maSP, tenSP, soLuongBan, giaBan, doanhThuDongGop));
                    }
                }
            }
            return dtos;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Long getTodayOrderCount() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();

        Long count = donHangRepository.getOrderCountByDateRange(startOfDay, endOfDay);
        return count != null ? count : 0L;
    }

    public Long getThisMonthOrderCount() {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime nextMonth = startOfMonth.plusMonths(1);

        Long count = donHangRepository.getOrderCountByDateRange(startOfMonth, nextMonth);
        return count != null ? count : 0L;
    }

    public Long getTotalBooksSold() {
        Long total = chiTietDonHangRepository.getTotalBooksSold();
        return total != null ? total : 0L;
    }

    public List<Map<String, Object>> getShopMonthlyRevenue(Integer maShop) {
        String sql = "CALL sp_DoanhThuShopTheoThang(:maShop)";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("maShop", maShop);
        List<Object[]> results = query.getResultList();

        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("nam", row[0]);
            map.put("thang", row[1]);
            map.put("tongDonHang", row[2]);
            map.put("tongDoanhThu", row[3]);
            map.put("tongPhiSan", row[4]);
            map.put("thucNhanShop", row[5]);
            list.add(map);
        }
        return list;
    }

    // ====================================================================
    // VIEW 4: CẢNH BÁO TỒN KHO CỦA SHOP (vw_CanhBaoTonKhoShop LỌC THEO ma_shop)
    // ====================================================================
    public List<Map<String, Object>> getShopStockWarnings(Integer maShop) {
        String sql = "SELECT masp, tensp, so_luong_ton, muc_ton_toi_thieu, muc_do_canh_bao " +
                     "FROM vw_CanhBaoTonKhoShop WHERE ma_shop = :maShop " +
                     "ORDER BY so_luong_ton ASC, masp ASC";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("maShop", maShop);
        List<Object[]> results = query.getResultList();

        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("masp", row[0]);
            map.put("tensp", row[1]);
            map.put("soLuongTon", row[2]);
            map.put("mucTonToiThieu", row[3]);
            map.put("mucDoCanhBao", row[4]);
            list.add(map);
        }
        return list;
    }

    // ====================================================================
    // VIEW 1: BÁO CÁO DOANH THU THEO THÁNG TOÀN SÀN (vw_BaoCaoDoanhThuTheoThang)
    // ====================================================================
    public List<Map<String, Object>> getMonthlyRevenueReportView() {
        String sql = "SELECT nam, thang, tong_so_don, so_don_thanh_cong, so_don_huy, tong_doanh_thu_hang, hoa_hong_san_thu, tong_tien_tra_shop " +
                     "FROM vw_BaoCaoDoanhThuTheoThang ORDER BY nam DESC, thang DESC";
        Query query = entityManager.createNativeQuery(sql);
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        List<Map<String, Object>> list = new ArrayList<>();
        if (results != null) {
            for (Object[] row : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("nam", row[0]);
                map.put("thang", row[1]);
                map.put("tongSoDon", row[2]);
                map.put("soDonThanhCong", row[3]);
                map.put("soDonHuy", row[4]);
                map.put("tongDoanhThuHang", row[5]);
                map.put("hoaHongSanThu", row[6]);
                map.put("tongTienTraShop", row[7]);
                list.add(map);
            }
        }
        return list;
    }

    // ====================================================================
    // VIEW 2: DOANH THU VÀ HOA HỒNG TỪNG SHOP (vw_DoanhThuTungShop)
    // ====================================================================
    public List<Map<String, Object>> getShopRevenuesView() {
        String sql = "SELECT ma_shop, ten_shop, so_dien_thoai, chiet_khau_phan_tram, tong_don_dat, don_hoan_thanh, tong_tien_ban, phi_san_da_nop, thuc_nhan_ve_shop " +
                     "FROM vw_DoanhThuTungShop ORDER BY tong_tien_ban DESC";
        Query query = entityManager.createNativeQuery(sql);
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        List<Map<String, Object>> list = new ArrayList<>();
        if (results != null) {
            for (Object[] row : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("maShop", row[0]);
                map.put("tenShop", row[1]);
                map.put("soDienThoai", row[2]);
                map.put("chietKhauPhanTram", row[3]);
                map.put("tongDonDat", row[4]);
                map.put("donHoanThanh", row[5]);
                map.put("tongTienBan", row[6]);
                map.put("phiSanDaNop", row[7]);
                map.put("thucNhanVeShop", row[8]);
                list.add(map);
            }
        }
        return list;
    }

    // ====================================================================
    // VIEW 3: TOP SÁCH BÁN CHẠY TOÀN SÀN (vw_TopSachBanChayToanSan)
    // ====================================================================
    public List<Map<String, Object>> getTopSellingBooksView(int limit) {
        String sql = "SELECT masp, tensp, ten_shop, ten_danh_muc, gia_ban, so_luong_ton, so_luong_da_ban, tong_doanh_thu_sach " +
                     "FROM vw_TopSachBanChayToanSan ORDER BY so_luong_da_ban DESC LIMIT :limit";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("limit", limit > 0 ? limit : 10);
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        List<Map<String, Object>> list = new ArrayList<>();
        if (results != null) {
            for (Object[] row : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("masp", row[0]);
                map.put("tensp", row[1]);
                map.put("tenShop", row[2]);
                map.put("tenDanhMuc", row[3]);
                map.put("giaBan", row[4]);
                map.put("soLuongTon", row[5]);
                map.put("soLuongDaBan", row[6]);
                map.put("tongDoanhThuSach", row[7]);
                list.add(map);
            }
        }
        return list;
    }

    // ====================================================================
    // VIEW 5: THỐNG KÊ ĐÁNH GIÁ SẢN PHẨM (vw_ThongKeDanhGiaSanPham)
    // ====================================================================
    public Map<String, Object> getProductReviewStatsView(Integer maSP) {
        if (maSP == null) return null;
        try {
            String sql = "SELECT masp, tensp, ten_shop, tong_luot_danh_gia, diem_sao_trung_binh " +
                         "FROM vw_ThongKeDanhGiaSanPham WHERE masp = :masp";
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("masp", maSP);
            Object[] row = (Object[]) query.getSingleResult();
            Map<String, Object> map = new HashMap<>();
            map.put("masp", row[0]);
            map.put("tensp", row[1]);
            map.put("tenShop", row[2]);
            map.put("tongLuotDanhGia", row[3]);
            map.put("diemSaoTrungBinh", row[4]);
            return map;
        } catch (Exception e) {
            return null;
        }
    }

    // ====================================================================
    // THỦ TỤC 8: BÁO CÁO DOANH THU THEO KHOẢNG NGÀY (sp_BaoCaoDoanhThuTheoKhoangNgay)
    // ====================================================================
    public List<Map<String, Object>> getRevenueByDateRangeProcedure(LocalDateTime fromDate, LocalDateTime toDate) {
        String sql = "CALL sp_BaoCaoDoanhThuTheoKhoangNgay(:tuNgay, :denNgay)";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("tuNgay", fromDate != null ? fromDate : LocalDateTime.now().minusMonths(1));
        query.setParameter("denNgay", toDate != null ? toDate : LocalDateTime.now());
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        List<Map<String, Object>> list = new ArrayList<>();
        if (results != null) {
            for (Object[] row : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("maShop", row[0]);
                map.put("tenShop", row[1]);
                map.put("soDon", row[2]);
                map.put("tongTienBan", row[3]);
                map.put("phiSan", row[4]);
                map.put("shopNhan", row[5]);
                list.add(map);
            }
        }
        return list;
    }
}
