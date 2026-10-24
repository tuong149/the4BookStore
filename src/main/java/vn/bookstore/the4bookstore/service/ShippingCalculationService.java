package vn.bookstore.the4bookstore.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import vn.bookstore.the4bookstore.entity.ChiTietGioHang;
import vn.bookstore.the4bookstore.entity.NhaVanChuyen;
import vn.bookstore.the4bookstore.entity.Shop;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ShippingCalculationService {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double ROAD_FACTOR = 1.25; // Hệ số uốn lượn đường bộ Việt Nam
    private static final int DEFAULT_BASE_CARRIER_FEE = 25000;

    // Tọa độ địa lý (Lat, Lng)
    @Data
    @AllArgsConstructor
    public static class GeoPoint {
        private double lat;
        private double lng;
        private String displayName;
    }

    // Kết quả tính toán phí vận chuyển
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingEstimate {
        private int shippingFee;
        private double distanceKm;
        private String tierName;
        private String estimatedDays;
        private String originProvince;
        private String destinationProvince;
    }

    // Kết quả tính theo từng Shop / Kiện hàng trong giỏ
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShopShippingBreakdown {
        private Integer shopId;
        private String shopName;
        private String shopAddress;
        private double distanceKm;
        private int shippingFee;
        private int shippingDiscount;
        private int finalFee;
        private String tierName;
        private String estimatedDays;
        private int itemCount;
        private int subtotal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartShippingCalculationResult {
        private int totalOriginalShippingFee;
        private int totalShippingDiscount;
        private int finalShippingFee;
        private List<ShopShippingBreakdown> shopBreakdowns;
    }

    // Bảng tra cứu tọa độ 63 tỉnh/thành & các quận/huyện lớn ở TP.HCM và Hà Nội
    private static final Map<String, GeoPoint> GEO_DATABASE = new LinkedHashMap<>();

    static {
        // --- 1. CÁC QUẬN NỘI THÀNH TP. HỒ CHÍ MINH (Phân biệt siêu gần < 5km và nội thành 10-25km) ---
        GEO_DATABASE.put("quan 1", new GeoPoint(10.7769, 106.7009, "Quận 1, TP.HCM"));
        GEO_DATABASE.put("quan 3", new GeoPoint(10.7844, 106.6844, "Quận 3, TP.HCM"));
        GEO_DATABASE.put("quan 4", new GeoPoint(10.7578, 106.7012, "Quận 4, TP.HCM"));
        GEO_DATABASE.put("quan 5", new GeoPoint(10.7554, 106.6669, "Quận 5, TP.HCM"));
        GEO_DATABASE.put("quan 6", new GeoPoint(10.7481, 106.6352, "Quận 6, TP.HCM"));
        GEO_DATABASE.put("quan 7", new GeoPoint(10.7340, 106.7218, "Quận 7, TP.HCM"));
        GEO_DATABASE.put("quan 8", new GeoPoint(10.7241, 106.6286, "Quận 8, TP.HCM"));
        GEO_DATABASE.put("quan 10", new GeoPoint(10.7679, 106.6668, "Quận 10, TP.HCM"));
        GEO_DATABASE.put("quan 11", new GeoPoint(10.7630, 106.6431, "Quận 11, TP.HCM"));
        GEO_DATABASE.put("quan 12", new GeoPoint(10.8672, 106.6414, "Quận 12, TP.HCM"));
        GEO_DATABASE.put("binh thanh", new GeoPoint(10.8034, 106.6990, "Quận Bình Thạnh, TP.HCM"));
        GEO_DATABASE.put("phu nhuan", new GeoPoint(10.7992, 106.6803, "Quận Phú Nhuận, TP.HCM"));
        GEO_DATABASE.put("go vap", new GeoPoint(10.8388, 106.6658, "Quận Gò Vấp, TP.HCM"));
        GEO_DATABASE.put("tan binh", new GeoPoint(10.8015, 106.6526, "Quận Tân Bình, TP.HCM"));
        GEO_DATABASE.put("tan phu", new GeoPoint(10.7901, 106.6281, "Quận Tân Phú, TP.HCM"));
        GEO_DATABASE.put("binh tan", new GeoPoint(10.7654, 106.6038, "Quận Bình Tân, TP.HCM"));
        GEO_DATABASE.put("thu duc", new GeoPoint(10.8494, 106.7537, "TP. Thủ Đức, TP.HCM"));
        GEO_DATABASE.put("quan 2", new GeoPoint(10.7872, 106.7498, "Quận 2 (Thủ Đức), TP.HCM"));
        GEO_DATABASE.put("quan 9", new GeoPoint(10.8428, 106.8287, "Quận 9 (Thủ Đức), TP.HCM"));
        GEO_DATABASE.put("hoc mon", new GeoPoint(10.8839, 106.5912, "Huyện Hóc Môn, TP.HCM"));
        GEO_DATABASE.put("binh chanh", new GeoPoint(10.6874, 106.5939, "Huyện Bình Chánh, TP.HCM"));
        GEO_DATABASE.put("nha be", new GeoPoint(10.6953, 106.7297, "Huyện Nhà Bè, TP.HCM"));
        GEO_DATABASE.put("cu chi", new GeoPoint(11.0067, 106.4946, "Huyện Củ Chi, TP.HCM"));
        GEO_DATABASE.put("can gio", new GeoPoint(10.4114, 106.9546, "Huyện Cần Giờ, TP.HCM"));

        // --- 2. CÁC QUẬN NỘI THÀNH HÀ NỘI ---
        GEO_DATABASE.put("hoan kiem", new GeoPoint(21.0307, 105.8524, "Quận Hoàn Kiếm, Hà Nội"));
        GEO_DATABASE.put("ba dinh", new GeoPoint(21.0341, 105.8235, "Quận Ba Đình, Hà Nội"));
        GEO_DATABASE.put("dong da", new GeoPoint(21.0181, 105.8276, "Quận Đống Đa, Hà Nội"));
        GEO_DATABASE.put("hai ba trung", new GeoPoint(21.0091, 105.8569, "Quận Hai Bà Trưng, Hà Nội"));
        GEO_DATABASE.put("cau giay", new GeoPoint(21.0362, 105.7906, "Quận Cầu Giấy, Hà Nội"));
        GEO_DATABASE.put("thanh xuan", new GeoPoint(20.9937, 105.8115, "Quận Thanh Xuân, Hà Nội"));
        GEO_DATABASE.put("tay ho", new GeoPoint(21.0716, 105.8223, "Quận Tây Hồ, Hà Nội"));
        GEO_DATABASE.put("hoang mai", new GeoPoint(20.9765, 105.8617, "Quận Hoàng Mai, Hà Nội"));
        GEO_DATABASE.put("long bien", new GeoPoint(21.0360, 105.8942, "Quận Long Biên, Hà Nội"));
        GEO_DATABASE.put("nam tu liem", new GeoPoint(21.0125, 105.7645, "Quận Nam Từ Liêm, Hà Nội"));
        GEO_DATABASE.put("bac tu liem", new GeoPoint(21.0645, 105.7562, "Quận Bắc Từ Liêm, Hà Nội"));
        GEO_DATABASE.put("ha dong", new GeoPoint(20.9723, 105.7766, "Quận Hà Đông, Hà Nội"));
        GEO_DATABASE.put("gia lam", new GeoPoint(21.0189, 105.9427, "Huyện Gia Lâm, Hà Nội"));
        GEO_DATABASE.put("dong anh", new GeoPoint(21.1372, 105.8458, "Huyện Đông Anh, Hà Nội"));
        GEO_DATABASE.put("soc son", new GeoPoint(21.2727, 105.8466, "Huyện Sóc Sơn, Hà Nội"));

        // --- 3. TOÀN BỘ 63 TỈNH / THÀNH PHỐ VIỆT NAM ---
        GEO_DATABASE.put("ho chi minh", new GeoPoint(10.7769, 106.7009, "TP. Hồ Chí Minh"));
        GEO_DATABASE.put("tphcm", new GeoPoint(10.7769, 106.7009, "TP. Hồ Chí Minh"));
        GEO_DATABASE.put("sai gon", new GeoPoint(10.7769, 106.7009, "TP. Hồ Chí Minh"));
        GEO_DATABASE.put("ha noi", new GeoPoint(21.0285, 105.8542, "Hà Nội"));
        GEO_DATABASE.put("da nang", new GeoPoint(16.0544, 108.2022, "Đà Nẵng"));
        GEO_DATABASE.put("hai phong", new GeoPoint(20.8449, 106.6881, "Hải Phòng"));
        GEO_DATABASE.put("can tho", new GeoPoint(10.0452, 105.7469, "Cần Thơ"));

        GEO_DATABASE.put("an giang", new GeoPoint(10.5216, 105.1259, "An Giang"));
        GEO_DATABASE.put("ba ria vung tau", new GeoPoint(10.4114, 107.1362, "Bà Rịa - Vũng Tàu"));
        GEO_DATABASE.put("vung tau", new GeoPoint(10.4114, 107.1362, "Bà Rịa - Vũng Tàu"));
        GEO_DATABASE.put("bac giang", new GeoPoint(21.2731, 106.1946, "Bắc Giang"));
        GEO_DATABASE.put("bac kan", new GeoPoint(22.1470, 105.8348, "Bắc Kạn"));
        GEO_DATABASE.put("bac lieu", new GeoPoint(9.2941, 105.7278, "Bạc Liêu"));
        GEO_DATABASE.put("bac ninh", new GeoPoint(21.1861, 106.0763, "Bắc Ninh"));
        GEO_DATABASE.put("ben tre", new GeoPoint(10.2434, 106.3753, "Bến Tre"));
        GEO_DATABASE.put("binh dinh", new GeoPoint(13.7830, 109.2197, "Bình Định"));
        GEO_DATABASE.put("quy nhon", new GeoPoint(13.7830, 109.2197, "Bình Định"));
        GEO_DATABASE.put("binh duong", new GeoPoint(11.1664, 106.6496, "Bình Dương"));
        GEO_DATABASE.put("binh phuoc", new GeoPoint(11.7511, 106.7234, "Bình Phước"));
        GEO_DATABASE.put("binh thuan", new GeoPoint(10.9805, 108.2615, "Bình Thuận"));
        GEO_DATABASE.put("phan thiet", new GeoPoint(10.9805, 108.2615, "Bình Thuận"));
        GEO_DATABASE.put("ca mau", new GeoPoint(9.1769, 105.1524, "Cà Mau"));
        GEO_DATABASE.put("cao bang", new GeoPoint(22.6666, 106.2639, "Cao Bằng"));
        GEO_DATABASE.put("dak lak", new GeoPoint(12.6667, 108.0500, "Đắk Lắk"));
        GEO_DATABASE.put("buon ma thuot", new GeoPoint(12.6667, 108.0500, "Đắk Lắk"));
        GEO_DATABASE.put("dak nong", new GeoPoint(12.0041, 107.6875, "Đắk Nông"));
        GEO_DATABASE.put("dien bien", new GeoPoint(21.3860, 103.0230, "Điện Biên"));
        GEO_DATABASE.put("dong nai", new GeoPoint(10.9460, 106.8242, "Đồng Nai"));
        GEO_DATABASE.put("bien hoa", new GeoPoint(10.9460, 106.8242, "Đồng Nai"));
        GEO_DATABASE.put("dong thap", new GeoPoint(10.4938, 105.6882, "Đồng Tháp"));
        GEO_DATABASE.put("gia lai", new GeoPoint(13.9833, 108.0000, "Gia Lai"));
        GEO_DATABASE.put("pleiku", new GeoPoint(13.9833, 108.0000, "Gia Lai"));
        GEO_DATABASE.put("ha giang", new GeoPoint(22.8233, 104.9836, "Hà Giang"));
        GEO_DATABASE.put("ha nam", new GeoPoint(20.5835, 105.9230, "Hà Nam"));
        GEO_DATABASE.put("ha tinh", new GeoPoint(18.3560, 105.8990, "Hà Tĩnh"));
        GEO_DATABASE.put("hai duong", new GeoPoint(20.9373, 106.3150, "Hải Dương"));
        GEO_DATABASE.put("hau giang", new GeoPoint(9.7844, 105.4701, "Hậu Giang"));
        GEO_DATABASE.put("hoa binh", new GeoPoint(20.8171, 105.3376, "Hòa Bình"));
        GEO_DATABASE.put("hung yen", new GeoPoint(20.6464, 106.0511, "Hưng Yên"));
        GEO_DATABASE.put("khanh hoa", new GeoPoint(12.2388, 109.1967, "Khánh Hòa"));
        GEO_DATABASE.put("nha trang", new GeoPoint(12.2388, 109.1967, "Khánh Hòa"));
        GEO_DATABASE.put("kien giang", new GeoPoint(10.0125, 105.0809, "Kiên Giang"));
        GEO_DATABASE.put("rach gia", new GeoPoint(10.0125, 105.0809, "Kiên Giang"));
        GEO_DATABASE.put("phu quoc", new GeoPoint(10.2289, 103.9572, "Phú Quốc (Kiên Giang)"));
        GEO_DATABASE.put("kon tum", new GeoPoint(14.3497, 108.0005, "Kon Tum"));
        GEO_DATABASE.put("lai chau", new GeoPoint(22.3862, 103.4754, "Lai Châu"));
        GEO_DATABASE.put("lam dong", new GeoPoint(11.9404, 108.4583, "Lâm Đồng"));
        GEO_DATABASE.put("da lat", new GeoPoint(11.9404, 108.4583, "Lâm Đồng"));
        GEO_DATABASE.put("lang son", new GeoPoint(21.8537, 106.7620, "Lạng Sơn"));
        GEO_DATABASE.put("lao cai", new GeoPoint(22.4856, 103.9707, "Lào Cai"));
        GEO_DATABASE.put("long an", new GeoPoint(10.5333, 106.4000, "Long An"));
        GEO_DATABASE.put("nam dinh", new GeoPoint(20.4344, 106.1772, "Nam Định"));
        GEO_DATABASE.put("nghe an", new GeoPoint(18.6734, 105.6813, "Nghệ An"));
        GEO_DATABASE.put("vinh", new GeoPoint(18.6734, 105.6813, "Nghệ An"));
        GEO_DATABASE.put("ninh binh", new GeoPoint(20.2506, 105.9745, "Ninh Bình"));
        GEO_DATABASE.put("ninh thuan", new GeoPoint(11.5653, 108.9882, "Ninh Thuận"));
        GEO_DATABASE.put("phan rang", new GeoPoint(11.5653, 108.9882, "Ninh Thuận"));
        GEO_DATABASE.put("phu tho", new GeoPoint(21.3228, 105.2280, "Phú Thọ"));
        GEO_DATABASE.put("phu yen", new GeoPoint(13.0882, 109.0929, "Phú Yên"));
        GEO_DATABASE.put("tuy hoa", new GeoPoint(13.0882, 109.0929, "Phú Yên"));
        GEO_DATABASE.put("quang binh", new GeoPoint(17.4690, 106.6226, "Quảng Bình"));
        GEO_DATABASE.put("dong hoi", new GeoPoint(17.4690, 106.6226, "Quảng Bình"));
        GEO_DATABASE.put("quang nam", new GeoPoint(15.5394, 108.0277, "Quảng Nam"));
        GEO_DATABASE.put("hoi an", new GeoPoint(15.8801, 108.3380, "Quảng Nam"));
        GEO_DATABASE.put("quang ngai", new GeoPoint(15.1205, 108.7923, "Quảng Ngãi"));
        GEO_DATABASE.put("quang ninh", new GeoPoint(21.0069, 107.2925, "Quảng Ninh"));
        GEO_DATABASE.put("ha long", new GeoPoint(20.9505, 107.0734, "Quảng Ninh"));
        GEO_DATABASE.put("quang tri", new GeoPoint(16.7500, 107.1855, "Quảng Trị"));
        GEO_DATABASE.put("soc trang", new GeoPoint(9.6033, 105.9800, "Sóc Trăng"));
        GEO_DATABASE.put("son la", new GeoPoint(21.3256, 103.9188, "Sơn La"));
        GEO_DATABASE.put("tay ninh", new GeoPoint(11.3101, 106.0983, "Tây Ninh"));
        GEO_DATABASE.put("thai binh", new GeoPoint(20.4463, 106.3366, "Thái Bình"));
        GEO_DATABASE.put("thai nguyen", new GeoPoint(21.5942, 105.8482, "Thái Nguyên"));
        GEO_DATABASE.put("thanh hoa", new GeoPoint(19.8067, 105.7852, "Thanh Hóa"));
        GEO_DATABASE.put("thua thien hue", new GeoPoint(16.4637, 107.5909, "Thừa Thiên Huế"));
        GEO_DATABASE.put("hue", new GeoPoint(16.4637, 107.5909, "Thừa Thiên Huế"));
        GEO_DATABASE.put("tien giang", new GeoPoint(10.3600, 106.3600, "Tiền Giang"));
        GEO_DATABASE.put("my tho", new GeoPoint(10.3600, 106.3600, "Tiền Giang"));
        GEO_DATABASE.put("tra vinh", new GeoPoint(9.9347, 106.3455, "Trà Vinh"));
        GEO_DATABASE.put("tuyen quang", new GeoPoint(21.8236, 105.2180, "Tuyên Quang"));
        GEO_DATABASE.put("vinh long", new GeoPoint(10.2537, 105.9722, "Vĩnh Long"));
        GEO_DATABASE.put("vinh phuc", new GeoPoint(21.3089, 105.6049, "Vĩnh Phúc"));
        GEO_DATABASE.put("yen bai", new GeoPoint(21.7168, 104.9113, "Yên Bái"));
    }

    // Bảng ánh xạ từ viết tắt quận/huyện phổ biến
    private static final Map<String, String> ABBR_DISTRICT_MAP = new LinkedHashMap<>();
    // Bảng ánh xạ từ viết tắt tỉnh/thành phố phổ biến
    private static final Map<String, String> ABBR_PROVINCE_MAP = new LinkedHashMap<>();

    static {
        // Viết tắt các quận/huyện TP.HCM
        ABBR_DISTRICT_MAP.put("td", "thu duc");
        ABBR_DISTRICT_MAP.put("tptd", "thu duc");
        ABBR_DISTRICT_MAP.put("vvn", "thu duc");      // Đường Võ Văn Ngân (HCMUTE / Linh Chiểu)
        ABBR_DISTRICT_MAP.put("lt", "thu duc");       // Phường Linh Chiểu / Linh Trung
        ABBR_DISTRICT_MAP.put("linh chieu", "thu duc");
        ABBR_DISTRICT_MAP.put("linh trung", "thu duc");
        ABBR_DISTRICT_MAP.put("linh tay", "thu duc");
        ABBR_DISTRICT_MAP.put("linh dong", "thu duc");
        ABBR_DISTRICT_MAP.put("tang nhon phu", "thu duc");
        ABBR_DISTRICT_MAP.put("hcmute", "thu duc");

        for (int i = 1; i <= 12; i++) {
            ABBR_DISTRICT_MAP.put("q" + i, "quan " + i);
            ABBR_DISTRICT_MAP.put("q " + i, "quan " + i);
        }
        ABBR_DISTRICT_MAP.put("bt", "binh thanh");
        ABBR_DISTRICT_MAP.put("bth", "binh thanh");
        ABBR_DISTRICT_MAP.put("pn", "phu nhuan");
        ABBR_DISTRICT_MAP.put("gv", "go vap");
        ABBR_DISTRICT_MAP.put("tb", "tan binh");
        ABBR_DISTRICT_MAP.put("bc", "binh chanh");
        ABBR_DISTRICT_MAP.put("hm", "hoc mon");
        ABBR_DISTRICT_MAP.put("nb", "nha be");
        ABBR_DISTRICT_MAP.put("cc", "cu chi");
        ABBR_DISTRICT_MAP.put("cg", "can gio");

        // Viết tắt các quận/huyện Hà Nội
        ABBR_DISTRICT_MAP.put("hk", "hoan kiem");
        ABBR_DISTRICT_MAP.put("tx", "thanh xuan");
        ABBR_DISTRICT_MAP.put("hd", "ha dong");
        ABBR_DISTRICT_MAP.put("lb", "long bien");
        ABBR_DISTRICT_MAP.put("ntl", "nam tu liem");
        ABBR_DISTRICT_MAP.put("btl", "bac tu liem");
        ABBR_DISTRICT_MAP.put("dd", "dong da");
        ABBR_DISTRICT_MAP.put("hbt", "hai ba trung");

        // Viết tắt tỉnh / thành phố
        ABBR_PROVINCE_MAP.put("hcm", "ho chi minh");
        ABBR_PROVINCE_MAP.put("tphcm", "ho chi minh");
        ABBR_PROVINCE_MAP.put("sg", "ho chi minh");
        ABBR_PROVINCE_MAP.put("hn", "ha noi");
        ABBR_PROVINCE_MAP.put("tphn", "ha noi");
        ABBR_PROVINCE_MAP.put("dn", "da nang");
        ABBR_PROVINCE_MAP.put("hp", "hai phong");
        ABBR_PROVINCE_MAP.put("ct", "can tho");
        ABBR_PROVINCE_MAP.put("bd", "binh duong");
        ABBR_PROVINCE_MAP.put("vt", "vung tau");
        ABBR_PROVINCE_MAP.put("brvt", "ba ria vung tau");
    }

    /**
     * Chuẩn hóa chuỗi tiếng Việt: Bỏ dấu, chữ thường, loại bỏ ký tự lạ
     */
    public String normalizeAddress(String input) {
        if (input == null || input.isBlank()) return "";
        String nfd = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String result = pattern.matcher(nfd).replaceAll("")
                .replace('đ', 'd')
                .replace('Đ', 'd')
                .toLowerCase();
        // Giữ lại chữ cái, số và khoảng trắng
        return result.replaceAll("[^a-z0-9\\s]", " ").replaceAll("\\s+", " ").trim();
    }

    /**
     * Tìm tọa độ GeoPoint từ chuỗi địa chỉ
     */
    public GeoPoint resolveGeoPoint(String address) {
        String normalized = normalizeAddress(address);
        if (normalized.isBlank()) {
            // Mặc định là trung tâm TP.HCM nếu không xác định được
            return GEO_DATABASE.get("ho chi minh");
        }

        // 1. Quét tìm quận/huyện đầy đủ từ database trước
        for (Map.Entry<String, GeoPoint> entry : GEO_DATABASE.entrySet()) {
            String key = entry.getKey();
            if (isDistrictKey(key) && containsWordOrPhrase(normalized, key)) {
                return entry.getValue();
            }
        }

        // 2. Quét tìm từ viết tắt quận/huyện / địa danh đặc trưng (td, vvn, lt, q1..q12, tx, cg,...)
        for (Map.Entry<String, String> entry : ABBR_DISTRICT_MAP.entrySet()) {
            if (containsWordOrPhrase(normalized, entry.getKey())) {
                GeoPoint pt = GEO_DATABASE.get(entry.getValue());
                if (pt != null) return pt;
            }
        }

        // 3. Quét tìm tên tỉnh/thành phố đầy đủ
        for (Map.Entry<String, GeoPoint> entry : GEO_DATABASE.entrySet()) {
            String key = entry.getKey();
            if (!isDistrictKey(key) && containsWordOrPhrase(normalized, key)) {
                return entry.getValue();
            }
        }

        // 4. Quét tìm từ viết tắt tỉnh/thành phố (hcm, hn, dn, ct, hp, bd, vt,...)
        for (Map.Entry<String, String> entry : ABBR_PROVINCE_MAP.entrySet()) {
            if (containsWordOrPhrase(normalized, entry.getKey())) {
                GeoPoint pt = GEO_DATABASE.get(entry.getValue());
                if (pt != null) return pt;
            }
        }

        // Mặc định trung tâm TP.HCM
        return GEO_DATABASE.get("ho chi minh");
    }

    private boolean isDistrictKey(String key) {
        return key.startsWith("quan ") || key.startsWith("huyen ") ||
                key.equals("binh thanh") || key.equals("phu nhuan") || key.equals("go vap") ||
                key.equals("tan binh") || key.equals("tan phu") || key.equals("binh tan") ||
                key.equals("thu duc") || key.equals("hoc mon") || key.equals("binh chanh") ||
                key.equals("nha be") || key.equals("cu chi") || key.equals("can gio") ||
                key.equals("hoan kiem") || key.equals("ba dinh") || key.equals("dong da") ||
                key.equals("hai ba trung") || key.equals("cau giay") || key.equals("thanh xuan") ||
                key.equals("tay ho") || key.equals("hoang mai") || key.equals("long bien") ||
                key.equals("nam tu liem") || key.equals("bac tu liem") || key.equals("ha dong") ||
                key.equals("gia lam") || key.equals("dong anh") || key.equals("soc son");
    }

    private boolean containsWordOrPhrase(String text, String phrase) {
        // Kiểm tra khớp từ nguyên vẹn với regex ranh giới từ (\b)
        String regex = "(?i).*\\b" + Pattern.quote(phrase) + "\\b.*";
        return text.matches(regex);
    }

    /**
     * Tính khoảng cách Haversine giữa 2 tọa độ (km)
     */
    public double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Tính khoảng cách đường bộ ước tính (km) giữa 2 địa chỉ
     */
    public double calculateDistanceKm(String addressOrigin, String addressDestination) {
        GeoPoint origin = resolveGeoPoint(addressOrigin);
        GeoPoint dest = resolveGeoPoint(addressDestination);

        if (origin.getDisplayName().equals(dest.getDisplayName())) {
            // Cùng một quận hoặc cùng địa điểm
            return 3.5; // Ước lượng nội quận ~3.5 km
        }

        double directDist = haversineDistance(origin.getLat(), origin.getLng(), dest.getLat(), dest.getLng());
        // Nhân hệ số uốn lượn đường sá
        double roadDist = directDist * ROAD_FACTOR;
        return Math.round(roadDist * 10.0) / 10.0;
    }

    /**
     * Tính cước phí vận chuyển và thời gian dự kiến cho 1 đơn/kiện từ Shop đến Khách hàng
     */
    public ShippingEstimate calculateFee(String shopAddress, String customerAddress, NhaVanChuyen nvc) {
        GeoPoint origin = resolveGeoPoint(shopAddress);
        GeoPoint dest = resolveGeoPoint(customerAddress);

        double distanceKm;
        if (origin.getDisplayName().equalsIgnoreCase(dest.getDisplayName())) {
            distanceKm = 3.5;
        } else {
            double direct = haversineDistance(origin.getLat(), origin.getLng(), dest.getLat(), dest.getLng());
            distanceKm = Math.round(direct * ROAD_FACTOR * 10.0) / 10.0;
        }

        int baseFee;
        String tierName;
        String estimatedDays;

        if (distanceKm <= 5.0) {
            baseFee = 16000;
            tierName = "Nội thành siêu gần (< 5 km)";
            estimatedDays = "1 - 2 ngày";
        } else if (distanceKm <= 25.0) {
            baseFee = 20000;
            tierName = "Nội thành / Tỉnh (< 25 km)";
            estimatedDays = "1 - 2 ngày";
        } else if (distanceKm <= 150.0) {
            baseFee = 28000;
            tierName = "Cùng miền / Tỉnh lân cận";
            estimatedDays = "2 - 3 ngày";
        } else if (distanceKm <= 500.0) {
            baseFee = 32000;
            tierName = "Cận miền";
            estimatedDays = "2 - 4 ngày";
        } else {
            baseFee = 38000;
            tierName = "Liên miền (Bắc - Nam)";
            estimatedDays = "3 - 5 ngày";
        }

        // Điều chỉnh theo tỷ lệ cước của Nhà vận chuyển được chọn
        int finalFee = baseFee;
        if (nvc != null && nvc.getPhiCoBan() != null && nvc.getPhiCoBan() > 0) {
            double ratio = (double) nvc.getPhiCoBan() / (double) DEFAULT_BASE_CARRIER_FEE;
            // Làm tròn đến hàng nghìn đồng
            finalFee = (int) (Math.round((baseFee * ratio) / 1000.0) * 1000);
            if (nvc.getThoiGianDuKien() != null && !nvc.getThoiGianDuKien().isBlank()) {
                estimatedDays = nvc.getThoiGianDuKien();
            }
        }

        return ShippingEstimate.builder()
                .shippingFee(finalFee)
                .distanceKm(distanceKm)
                .tierName(tierName)
                .estimatedDays(estimatedDays)
                .originProvince(origin.getDisplayName())
                .destinationProvince(dest.getDisplayName())
                .build();
    }

    /**
     * Bóc tách và tính toán phí vận chuyển chi tiết theo từng Shop cho danh sách sản phẩm trong giỏ
     */
    public CartShippingCalculationResult calculateCartShipping(List<ChiTietGioHang> items,
                                                              String customerAddress,
                                                              NhaVanChuyen nvc,
                                                              int freeShippingThreshold,
                                                              int voucherShippingDiscount) {
        if (items == null || items.isEmpty()) {
            return CartShippingCalculationResult.builder()
                    .totalOriginalShippingFee(0)
                    .totalShippingDiscount(0)
                    .finalShippingFee(0)
                    .shopBreakdowns(Collections.emptyList())
                    .build();
        }

        // Gom nhóm sản phẩm theo Shop
        Map<Integer, List<ChiTietGioHang>> itemsByShopId = items.stream()
                .collect(Collectors.groupingBy(item -> (item.getSanPham().getShop() != null && item.getSanPham().getShop().getMaShop() != null)
                        ? item.getSanPham().getShop().getMaShop() : 0));

        List<ShopShippingBreakdown> breakdowns = new ArrayList<>();
        int totalRawFee = 0;
        int grandSubtotal = 0;

        for (Map.Entry<Integer, List<ChiTietGioHang>> entry : itemsByShopId.entrySet()) {
            List<ChiTietGioHang> shopItems = entry.getValue();
            Shop shop = shopItems.get(0).getSanPham().getShop();
            String shopName = (shop != null && shop.getTenShop() != null) ? shop.getTenShop() : "The4BookStore Official";
            String shopAddress = (shop != null && shop.getDiaChiShop() != null && !shop.getDiaChiShop().isBlank())
                    ? shop.getDiaChiShop() : "Quận 1, TP. Hồ Chí Minh";

            int shopSubtotal = shopItems.stream()
                    .mapToInt(i -> i.getSanPham().getGiaBan() * i.getSoLuong())
                    .sum();
            int shopItemCount = shopItems.stream()
                    .mapToInt(ChiTietGioHang::getSoLuong)
                    .sum();
            grandSubtotal += shopSubtotal;

            ShippingEstimate estimate = calculateFee(shopAddress, customerAddress, nvc);
            totalRawFee += estimate.getShippingFee();

            breakdowns.add(ShopShippingBreakdown.builder()
                    .shopId(entry.getKey())
                    .shopName(shopName)
                    .shopAddress(shopAddress)
                    .distanceKm(estimate.getDistanceKm())
                    .shippingFee(estimate.getShippingFee())
                    .shippingDiscount(0)
                    .finalFee(estimate.getShippingFee())
                    .tierName(estimate.getTierName())
                    .estimatedDays(estimate.getEstimatedDays())
                    .itemCount(shopItemCount)
                    .subtotal(shopSubtotal)
                    .build());
        }

        // Phân bổ giảm giá Freeship (từ mốc miễn phí sàn hoặc Voucher Freeship)
        int totalDiscount = 0;
        if (grandSubtotal >= freeShippingThreshold) {
            // Đạt mốc miễn phí sàn: Miễn phí toàn bộ
            totalDiscount = totalRawFee;
            for (ShopShippingBreakdown b : breakdowns) {
                b.setShippingDiscount(b.getShippingFee());
                b.setFinalFee(0);
            }
        } else if (voucherShippingDiscount > 0) {
            // Giảm theo voucher: ưu tiên giảm cho kiện hàng có phí cao nhất trước
            int remainingDiscount = Math.min(totalRawFee, voucherShippingDiscount);
            totalDiscount = remainingDiscount;

            // Sắp xếp giảm trừ cho shop có phí ship cao trước
            List<ShopShippingBreakdown> sorted = new ArrayList<>(breakdowns);
            sorted.sort(Comparator.comparingInt(ShopShippingBreakdown::getShippingFee).reversed());

            for (ShopShippingBreakdown b : sorted) {
                int apply = Math.min(b.getShippingFee(), remainingDiscount);
                b.setShippingDiscount(apply);
                b.setFinalFee(b.getShippingFee() - apply);
                remainingDiscount -= apply;
                if (remainingDiscount <= 0) break;
            }
        }

        return CartShippingCalculationResult.builder()
                .totalOriginalShippingFee(totalRawFee)
                .totalShippingDiscount(totalDiscount)
                .finalShippingFee(Math.max(0, totalRawFee - totalDiscount))
                .shopBreakdowns(breakdowns)
                .build();
    }
}
