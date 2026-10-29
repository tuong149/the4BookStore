package vn.bookstore.the4bookstore.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.bookstore.the4bookstore.entity.DanhMuc;
import vn.bookstore.the4bookstore.entity.DanhMucLon;
import vn.bookstore.the4bookstore.repository.DanhMucLonRepository;
import vn.bookstore.the4bookstore.repository.DanhMucRepository;
import vn.bookstore.the4bookstore.repository.SanPhamRepository;

import java.util.List;

@Component
public class CategoryDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(CategoryDataInitializer.class);
    private final DanhMucRepository danhMucRepository;
    private final DanhMucLonRepository danhMucLonRepository;
    private final SanPhamRepository sanPhamRepository;

    public CategoryDataInitializer(DanhMucRepository danhMucRepository,
                                   DanhMucLonRepository danhMucLonRepository,
                                   SanPhamRepository sanPhamRepository) {
        this.danhMucRepository = danhMucRepository;
        this.danhMucLonRepository = danhMucLonRepository;
        this.sanPhamRepository = sanPhamRepository;
    }

    private record StandardCategory(String name, String desc, String loaiSP) {}

    private static final List<StandardCategory> STANDARD_CATEGORIES = List.of(
            // Sách
            new StandardCategory("Văn Học Trong Nước", "Tiểu thuyết, truyện ngắn, phóng sự và tác phẩm văn học Việt Nam", "Sach"),
            new StandardCategory("Văn Học Nước Ngoài", "Tác phẩm dịch kinh điển, văn học thế giới đạt giải Nobel, Booker", "Sach"),
            new StandardCategory("Kinh Tế - Khởi Nghiệp", "Quản trị kinh doanh, đầu tư, tài chính và khởi nghiệp đổi mới", "Sach"),
            new StandardCategory("Công Nghệ Thông Tin", "Lập trình, kiến trúc phần mềm, khoa học dữ liệu và an toàn thông tin", "Sach"),
            new StandardCategory("Thiếu Nhi & Truyện Tranh", "Manga, comic, truyện thiếu nhi và văn học giáo dục sớm", "Sach"),
            new StandardCategory("Tâm Lý & Kỹ Năng Sống", "Sách phát triển bản thân, tư duy tích cực và thấu hiểu nội tâm", "Sach"),

            // Văn Phòng Phẩm
            new StandardCategory("Sổ Tay & Vở Viết", "Sổ tay bìa da, sổ dot grid, sổ còng và vở ghi chép cao cấp", "VanPhongPham"),
            new StandardCategory("Bút & Dụng Cụ Viết", "Bút máy, bút gel, bút ký và mực viết thư pháp chính hãng", "VanPhongPham"),
            new StandardCategory("Dụng Cụ Học Tập & Văn Phòng", "Bookmark kim loại, thước, kẹp tài liệu và dụng cụ văn phòng", "VanPhongPham"),

            // Quà Tặng & Trang Trí
            new StandardCategory("Đèn Đọc Sách & Phụ Kiện Sách", "Đèn LED kẹp sách bảo vệ mắt, giá đỡ sách thông minh và phụ kiện", "QuaTang"),
            new StandardCategory("Mô Hình Book Nook & Trang Trí", "Mô hình gỗ 3D Book Nook lắp ráp trang trí giá sách tinh tế", "QuaTang"),
            new StandardCategory("Túi Tote & Thời Trang Sách", "Túi vải canvas thời trang, áo thun và phụ kiện phong cách mọt sách", "QuaTang")
    );

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    @CacheEvict(value = {"allActiveCategories", "categories", "categoriesByLoaiSP", "home_preview", "allMajorCategories", "allActiveMajorCategories"}, allEntries = true)
    public void syncStandardCategories() {
        try {
            // 1. Dọn dẹp tàn dư BoardGame tự tạo cũ nếu có
            cleanupDeprecatedBoardGame();

            // 2. Khởi tạo 3 Danh Mục Lớn tiêu chuẩn (Sách, VPP, Quà Tặng) CHỈ KHI bảng danh mục lớn đang trống
            initStandardMajorCategories();

            // 3. Khởi tạo danh mục con tiêu chuẩn CHỈ KHI cơ sở dữ liệu chưa có danh mục nào
            if (danhMucRepository.count() == 0) {
                for (StandardCategory std : STANDARD_CATEGORIES) {
                    DanhMuc newDm = new DanhMuc();
                    newDm.setTenDanhMuc(std.name());
                    newDm.setMoTa(std.desc());
                    newDm.setLoaiSP(std.loaiSP());
                    newDm.setTrangThai(true);
                    danhMucRepository.save(newDm);
                    log.info("Khởi tạo danh mục tiêu chuẩn lần đầu: '{}' [{}]", std.name(), std.loaiSP());
                }
            } else {
                // Đã có dữ liệu: Tôn trọng hoàn toàn thao tác của Admin (không tự ý thêm lại các danh mục Admin đã xóa)
                // Chỉ chuẩn hóa an toàn các trường bắt buộc nếu đang rỗng
                List<DanhMuc> currentList = danhMucRepository.findAll();
                for (DanhMuc dm : currentList) {
                    boolean changed = false;
                    if (dm.getLoaiSP() == null || dm.getLoaiSP().isBlank()) {
                        dm.setLoaiSP("Sach");
                        changed = true;
                    }
                    if (dm.getTrangThai() == null) {
                        dm.setTrangThai(true);
                        changed = true;
                    }
                    if (changed) {
                        danhMucRepository.save(dm);
                    }
                }
            }

        } catch (Exception e) {
            log.warn("Không thể đồng bộ danh mục lúc khởi động: {}", e.getMessage());
        }
    }

    private void initStandardMajorCategories() {
        if (danhMucLonRepository.count() == 0) {
            DanhMucLon dmlSach = new DanhMucLon(null, "Sach", "Sách", "menu_book", "Ấn bản, tác phẩm, văn học, giáo trình...", true, 1);
            DanhMucLon dmlVpp = new DanhMucLon(null, "VanPhongPham", "Văn Phòng Phẩm", "edit_note", "Sổ tay, bút viết, bookmark, dụng cụ văn phòng...", true, 2);
            DanhMucLon dmlQuaTang = new DanhMucLon(null, "QuaTang", "Quà Tặng & Trang Trí", "redeem", "Đèn đọc sách, mô hình book nook, túi tote, phụ kiện...", true, 3);

            danhMucLonRepository.saveAll(List.of(dmlSach, dmlVpp, dmlQuaTang));
            log.info("Đã khởi tạo 3 danh mục lớn tiêu chuẩn: Sách, Văn Phòng Phẩm, Quà Tặng & Trang Trí");
        }
    }

    private void cleanupDeprecatedBoardGame() {
        try {
            // Xóa hoặc chuyển các danh mục con mang loaiSP = 'BoardGame'
            List<DanhMuc> bgCategories = danhMucRepository.findByLoaiSP("BoardGame");
            for (DanhMuc dm : bgCategories) {
                long productCount = sanPhamRepository.countByDanhMuc_MaDanhMuc(dm.getMaDanhMuc());
                if (productCount == 0) {
                    danhMucRepository.delete(dm);
                    log.info("Đã dọn dẹp danh mục con tự tạo cũ: '{}'", dm.getTenDanhMuc());
                } else {
                    dm.setLoaiSP("QuaTang");
                    danhMucRepository.save(dm);
                    log.info("Chuyển danh mục '{}' từ BoardGame về Quà Tặng do có sản phẩm", dm.getTenDanhMuc());
                }
            }

            // Xóa danh mục lớn BoardGame nếu không có sản phẩm liên kết
            danhMucLonRepository.findByMaLoai("BoardGame").ifPresent(dml -> {
                long productCount = sanPhamRepository.countByLoaiSPIgnoreCase("BoardGame");
                if (productCount == 0) {
                    danhMucLonRepository.delete(dml);
                    log.info("Đã dọn dẹp danh mục lớn 'BoardGame' tự động tạo cũ");
                }
            });
        } catch (Exception ex) {
            log.warn("Lỗi khi dọn dẹp BoardGame cũ: {}", ex.getMessage());
        }
    }
}
