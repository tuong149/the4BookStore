package vn.bookstore.the4bookstore.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.bookstore.the4bookstore.entity.DonHang;
import vn.bookstore.the4bookstore.entity.KhuyenMai;
import vn.bookstore.the4bookstore.repository.DonHangRepository;
import vn.bookstore.the4bookstore.repository.KhuyenMaiRepository;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class KhuyenMaiService {

    private final KhuyenMaiRepository khuyenMaiRepository;
    private final DonHangRepository donHangRepository;
    private final vn.bookstore.the4bookstore.repository.VoucherDaLuuRepository voucherDaLuuRepository;
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public KhuyenMaiService(KhuyenMaiRepository khuyenMaiRepository,
                            DonHangRepository donHangRepository,
                            vn.bookstore.the4bookstore.repository.VoucherDaLuuRepository voucherDaLuuRepository) {
        this.khuyenMaiRepository = khuyenMaiRepository;
        this.donHangRepository = donHangRepository;
        this.voucherDaLuuRepository = voucherDaLuuRepository;
    }

    public List<KhuyenMai> getAllPromotions(String keyword, String loaiGiam, String trangThai, LocalDate fromDate, LocalDate toDate) {
        List<KhuyenMai> list = khuyenMaiRepository.findAllByOrderByMaKMDesc();

        return list.stream().filter(km -> {
            // Filter keyword
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = keyword.trim().toLowerCase();
                boolean matchCode = km.getMaCode() != null && km.getMaCode().toLowerCase().contains(kw);
                boolean matchName = km.getTenKM() != null && km.getTenKM().toLowerCase().contains(kw);
                if (!matchCode && !matchName) return false;
            }

            // Filter loaiGiam
            if (loaiGiam != null && !loaiGiam.trim().isEmpty() && !loaiGiam.equalsIgnoreCase("ALL")) {
                if (!loaiGiam.equalsIgnoreCase(km.getLoaiGiam())) return false;
            }

            // Filter trangThai (computed)
            if (trangThai != null && !trangThai.trim().isEmpty() && !trangThai.equalsIgnoreCase("ALL")) {
                if (!trangThai.equalsIgnoreCase(km.getComputedStatus())) return false;
            }

            // Filter date range
            if (fromDate != null) {
                if (km.getNgayKetThuc() != null && km.getNgayKetThuc().toLocalDate().isBefore(fromDate)) {
                    return false;
                }
            }
            if (toDate != null) {
                if (km.getNgayBatDau() != null && km.getNgayBatDau().toLocalDate().isAfter(toDate)) {
                    return false;
                }
            }

            return true;
        }).collect(Collectors.toList());
    }

    public Optional<KhuyenMai> getById(Integer id) {
        return khuyenMaiRepository.findById(id);
    }

    @org.springframework.cache.annotation.Cacheable("public_vouchers")
    public List<KhuyenMai> getActivePublicVouchers() {
        return khuyenMaiRepository.findAll().stream()
                .filter(km -> "HoatDong".equalsIgnoreCase(km.getTrangThai()))
                .filter(km -> Boolean.TRUE.equals(km.getHienThiCongKhai()))
                .filter(KhuyenMai::isDangDienRa)
                .toList();
    }

    @org.springframework.cache.annotation.CacheEvict(value = "public_vouchers", allEntries = true)
    public KhuyenMai save(KhuyenMai khuyenMai) {
        if (khuyenMai.getMaCode() != null) {
            khuyenMai.setMaCode(khuyenMai.getMaCode().trim().toUpperCase());
        }
        if (khuyenMai.getDonToiThieu() == null || khuyenMai.getDonToiThieu() < 0) {
            khuyenMai.setDonToiThieu(0);
        }
        if (khuyenMai.getSoLuongDaDung() == null || khuyenMai.getSoLuongDaDung() < 0) {
            khuyenMai.setSoLuongDaDung(0);
        }
        if (khuyenMai.getTrangThai() == null || khuyenMai.getTrangThai().trim().isEmpty()) {
            khuyenMai.setTrangThai("HoatDong");
        }
        if (khuyenMai.getHienThiCongKhai() == null) {
            khuyenMai.setHienThiCongKhai(true);
        }
        if (khuyenMai.getGioiHanMoiKhachHang() == null || khuyenMai.getGioiHanMoiKhachHang() <= 0) {
            khuyenMai.setGioiHanMoiKhachHang(1);
        }
        if (khuyenMai.getApDungCho() == null || khuyenMai.getApDungCho().trim().isEmpty()) {
            khuyenMai.setApDungCho("ALL");
        }
        if (khuyenMai.getDoiTuongKhachHang() == null || khuyenMai.getDoiTuongKhachHang().trim().isEmpty()) {
            khuyenMai.setDoiTuongKhachHang("ALL");
        }
        if (khuyenMai.getPhamVi() == null || khuyenMai.getPhamVi().trim().isEmpty()) {
            khuyenMai.setPhamVi(khuyenMai.getShop() != null ? "SHOP" : "TOAN_SAN");
        }
        if ("Freeship".equalsIgnoreCase(khuyenMai.getLoaiGiam())) {
            khuyenMai.setLoaiKhuyenMai("MIEN_PHI_VAN_CHUYEN");
        } else if (khuyenMai.getLoaiKhuyenMai() == null || khuyenMai.getLoaiKhuyenMai().isBlank()) {
            khuyenMai.setLoaiKhuyenMai("GIAM_GIA_SAN_PHAM");
        }
        return khuyenMaiRepository.save(khuyenMai);
    }

    public boolean isCodeUnique(String maCode, Integer maKM) {
        if (maCode == null || maCode.trim().isEmpty()) return false;
        String clean = maCode.trim().toUpperCase();
        if (maKM == null) {
            return !khuyenMaiRepository.existsByMaCode(clean);
        } else {
            return !khuyenMaiRepository.existsByMaCodeAndMaKMNot(clean, maKM);
        }
    }

    @org.springframework.cache.annotation.CacheEvict(value = "public_vouchers", allEntries = true)
    public void toggleStatus(Integer id) {
        KhuyenMai km = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã khuyến mãi #" + id));
        if ("HoatDong".equalsIgnoreCase(km.getTrangThai())) {
            km.setTrangThai("VoHieuHoa");
        } else {
            km.setTrangThai("HoatDong");
        }
        khuyenMaiRepository.save(km);
    }

    @org.springframework.cache.annotation.CacheEvict(value = "public_vouchers", allEntries = true)
    public String delete(Integer id) {
        KhuyenMai km = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã khuyến mãi #" + id));

        long orderUsage = (donHangRepository.countByKhuyenMai(km) != null ? donHangRepository.countByKhuyenMai(km) : 0)
                + (donHangRepository.countByKhuyenMaiFreeship(km) != null ? donHangRepository.countByKhuyenMaiFreeship(km) : 0);

        if (orderUsage > 0) {
            // Đã có đơn hàng sử dụng mã: bảo toàn dữ liệu bằng cách vô hiệu hóa và ẩn mã
            km.setTrangThai("VoHieuHoa");
            km.setHienThiCongKhai(false);
            khuyenMaiRepository.save(km);
            // Dọn dẹp các mã chưa sử dụng trong ví khách hàng
            try {
                voucherDaLuuRepository.deleteByKhuyenMaiAndTrangThai(km, "ChuaDung");
            } catch (Exception ignored) {}
            return "Mã khuyến mãi [" + km.getMaCode() + "] đã có " + orderUsage + " đơn hàng sử dụng nên hệ thống đã tự động chuyển sang trạng thái Vô Hiệu Hóa (ẩn khỏi hệ thống) để bảo toàn đối soát lịch sử đơn hàng.";
        } else {
            // Chưa có đơn hàng nào dùng: Xóa an toàn toàn bộ dữ liệu trong ví trước, sau đó xóa mã
            try {
                voucherDaLuuRepository.deleteByKhuyenMai(km);
            } catch (Exception ignored) {}
            khuyenMaiRepository.delete(km);
            return "Đã xóa vĩnh viễn mã khuyến mãi [" + km.getMaCode() + "] thành công!";
        }
    }

    @org.springframework.cache.annotation.CacheEvict(value = "public_vouchers", allEntries = true)
    public KhuyenMai duplicate(Integer id) {
        KhuyenMai source = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã khuyến mãi #" + id));

        KhuyenMai clone = new KhuyenMai();
        clone.setTenKM("[Bản sao] " + source.getTenKM());

        // Sinh mã code bản sao không trùng
        String baseCode = (source.getMaCode() != null ? source.getMaCode() : "VOUCHER") + "-COPY";
        String candidateCode = baseCode;
        int counter = 1;
        while (khuyenMaiRepository.existsByMaCode(candidateCode)) {
            candidateCode = baseCode + counter++;
        }
        clone.setMaCode(candidateCode);

        clone.setMoTa(source.getMoTa());
        clone.setLoaiGiam(source.getLoaiGiam());
        clone.setGiaTriGiam(source.getGiaTriGiam());
        clone.setGiamToiDa(source.getGiamToiDa());
        clone.setDonToiThieu(source.getDonToiThieu() != null ? source.getDonToiThieu() : 0);
        clone.setSoLuongToiDa(source.getSoLuongToiDa());
        clone.setSoLuongDaDung(0);
        clone.setNgayBatDau(source.getNgayBatDau());
        clone.setNgayKetThuc(source.getNgayKetThuc());
        clone.setTrangThai("HoatDong");
        clone.setHienThiCongKhai(source.getHienThiCongKhai());
        clone.setApDungCho(source.getApDungCho());
        clone.setDanhSachDanhMucIds(source.getDanhSachDanhMucIds());
        clone.setDanhSachSanPhamIds(source.getDanhSachSanPhamIds());
        clone.setLoaiTruSanPhamIds(source.getLoaiTruSanPhamIds());
        clone.setDoiTuongKhachHang(source.getDoiTuongKhachHang());
        clone.setGioiHanMoiKhachHang(source.getGioiHanMoiKhachHang());
        clone.setPhamVi(source.getPhamVi() != null ? source.getPhamVi() : "TOAN_SAN");
        clone.setShop(source.getShop());
        clone.setLoaiKhuyenMai(source.getLoaiKhuyenMai() != null ? source.getLoaiKhuyenMai() : "GIAM_GIA_SAN_PHAM");

        return khuyenMaiRepository.save(clone);
    }

    public List<KhuyenMai> generateBatch(String prefix, int quantity, KhuyenMai template) {
        if (quantity <= 0) quantity = 10;
        if (quantity > 500) quantity = 500; // Giới hạn an toàn

        String cleanPrefix = (prefix != null && !prefix.trim().isEmpty()) ? prefix.trim().toUpperCase() : "BATCH-";
        if (!cleanPrefix.endsWith("-") && !cleanPrefix.endsWith("_")) {
            cleanPrefix += "-";
        }

        List<KhuyenMai> created = new ArrayList<>();
        Set<String> generatedCodes = new HashSet<>();

        for (int i = 0; i < quantity; i++) {
            String code;
            do {
                code = cleanPrefix + generateRandomString(6);
            } while (generatedCodes.contains(code) || khuyenMaiRepository.existsByMaCode(code));

            generatedCodes.add(code);

            KhuyenMai km = new KhuyenMai();
            km.setMaCode(code);
            km.setTenKM(template.getTenKM() + " #" + (i + 1));
            km.setMoTa(template.getMoTa());
            km.setLoaiGiam(template.getLoaiGiam());
            km.setGiaTriGiam(template.getGiaTriGiam());
            km.setGiamToiDa(template.getGiamToiDa());
            km.setDonToiThieu(template.getDonToiThieu() != null ? template.getDonToiThieu() : 0);
            // Mã batch thường là mã 1 lần dùng
            km.setSoLuongToiDa(template.getSoLuongToiDa() != null ? template.getSoLuongToiDa() : 1);
            km.setSoLuongDaDung(0);
            km.setNgayBatDau(template.getNgayBatDau() != null ? template.getNgayBatDau() : LocalDateTime.now());
            km.setNgayKetThuc(template.getNgayKetThuc() != null ? template.getNgayKetThuc() : LocalDateTime.now().plusMonths(1));
            km.setTrangThai("HoatDong");
            km.setHienThiCongKhai(template.getHienThiCongKhai() != null ? template.getHienThiCongKhai() : false);
            km.setApDungCho(template.getApDungCho() != null ? template.getApDungCho() : "ALL");
            km.setDoiTuongKhachHang(template.getDoiTuongKhachHang() != null ? template.getDoiTuongKhachHang() : "ALL");
            km.setGioiHanMoiKhachHang(1);
            km.setPhamVi(template.getPhamVi() != null ? template.getPhamVi() : (template.getShop() != null ? "SHOP" : "TOAN_SAN"));
            km.setShop(template.getShop());
            km.setLoaiKhuyenMai(template.getLoaiKhuyenMai() != null ? template.getLoaiKhuyenMai() : "GIAM_GIA_SAN_PHAM");

            created.add(km);
        }

        return khuyenMaiRepository.saveAll(created);
    }

    public static String generateRandomCode(String prefix, int length) {
        String p = (prefix != null && !prefix.trim().isEmpty()) ? prefix.trim().toUpperCase() : "KM";
        return p + "-" + generateRandomString(length > 0 ? length : 8);
    }

    private static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    // Thống kê đối soát
    public Long countOrdersByKhuyenMai(KhuyenMai km) {
        return donHangRepository.countByKhuyenMai(km);
    }

    public Long sumDiscountByKhuyenMai(KhuyenMai km) {
        return donHangRepository.sumTienGiamByKhuyenMai(km);
    }

    public Long sumRevenueByKhuyenMai(KhuyenMai km) {
        return donHangRepository.sumTongTienByKhuyenMai(km);
    }

    public List<DonHang> getOrdersByKhuyenMai(KhuyenMai km) {
        return donHangRepository.findByKhuyenMaiOrderByNgayDatDesc(km);
    }

    // Xuất CSV
    public byte[] exportToCsv(List<KhuyenMai> list) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(baos, true, StandardCharsets.UTF_8)) {
            // UTF-8 BOM cho Excel mở tiếng Việt không lỗi font
            baos.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

            writer.println("Mã ID,Tên chương trình,Mã Code,Loại giảm giá,Giá trị giảm,Giảm tối đa,Đơn tối thiểu,Đã dùng,Tổng số lượng,Bắt đầu,Kết thúc,Trạng thái,Công khai");

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (KhuyenMai km : list) {
                String loaiGiamStr = "Phần trăm";
                if ("TienCoDinh".equalsIgnoreCase(km.getLoaiGiam())) loaiGiamStr = "Tiền cố định";
                else if ("Freeship".equalsIgnoreCase(km.getLoaiGiam())) loaiGiamStr = "Miễn phí vận chuyển";

                String batDauStr = km.getNgayBatDau() != null ? km.getNgayBatDau().format(dtf) : "";
                String ketThucStr = km.getNgayKetThuc() != null ? km.getNgayKetThuc().format(dtf) : "";

                writer.printf("\"%d\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%d\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        km.getMaKM(),
                        escapeCsv(km.getTenKM()),
                        escapeCsv(km.getMaCode()),
                        loaiGiamStr,
                        km.getGiaTriGiam() != null ? ("PhanTram".equals(km.getLoaiGiam()) ? km.getGiaTriGiam() + "%" : km.getGiaTriGiam() + "đ") : "",
                        km.getGiamToiDa() != null ? km.getGiamToiDa() + "đ" : "Không giới hạn",
                        km.getDonToiThieu() != null ? km.getDonToiThieu() + "đ" : "0đ",
                        km.getSoLuongDaDung() != null ? km.getSoLuongDaDung() : 0,
                        km.getSoLuongToiDa() != null ? km.getSoLuongToiDa() : "Không giới hạn",
                        batDauStr,
                        ketThucStr,
                        km.getStatusBadgeText(),
                        Boolean.TRUE.equals(km.getHienThiCongKhai()) ? "Có" : "Không"
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi xuất file CSV: " + e.getMessage(), e);
        }
        return baos.toByteArray();
    }

    private String escapeCsv(String str) {
        if (str == null) return "";
        return str.replace("\"", "\"\"");
    }
}
