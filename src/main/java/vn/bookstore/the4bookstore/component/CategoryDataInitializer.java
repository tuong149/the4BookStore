package vn.bookstore.the4bookstore.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.bookstore.the4bookstore.entity.DanhMuc;
import vn.bookstore.the4bookstore.entity.DanhMucLon;
import vn.bookstore.the4bookstore.repository.DanhMucLonRepository;
import vn.bookstore.the4bookstore.repository.DanhMucRepository;

import java.util.List;

@Component
public class CategoryDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(CategoryDataInitializer.class);
    private final DanhMucRepository danhMucRepository;
    private final DanhMucLonRepository danhMucLonRepository;

    public CategoryDataInitializer(DanhMucRepository danhMucRepository, DanhMucLonRepository danhMucLonRepository) {
        this.danhMucRepository = danhMucRepository;
        this.danhMucLonRepository = danhMucLonRepository;
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
            new StandardCategory("Túi Tote & Thời Trang Sách", "Túi vải canvas thời trang, áo thun và phụ kiện phong cách mọt sách", "QuaTang"),
            new StandardCategory("Boardgame & Hộp Quà Tặng", "Trò chơi boardgame trí tuệ, set quà tặng độc đáo cho người yêu sách", "QuaTang")
    );

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void syncStandardCategories() {
        try {
            // 0. Khởi tạo các Danh Mục Lớn (Ngành hàng) tiêu chuẩn nếu chưa có
            initStandardMajorCategories();

            // 1. Cập nhật các danh mục con hiện có nếu thiếu loaiSP hoặc tên chưa chuẩn
            List<DanhMuc> currentList = danhMucRepository.findAll();
            for (DanhMuc dm : currentList) {
                boolean changed = false;
                String name = dm.getTenDanhMuc() != null ? dm.getTenDanhMuc().trim() : "";

                if (name.equalsIgnoreCase("Sổ tay")) {
                    dm.setTenDanhMuc("Sổ Tay & Vở Viết");
                    dm.setLoaiSP("VanPhongPham");
                    dm.setMoTa("Sổ tay bìa da, sổ dot grid, sổ còng và vở ghi chép cao cấp");
                    changed = true;
                } else {
                    for (StandardCategory std : STANDARD_CATEGORIES) {
                        if (std.name().equalsIgnoreCase(name)) {
                            if (dm.getLoaiSP() == null || dm.getLoaiSP().isBlank() || !std.loaiSP().equalsIgnoreCase(dm.getLoaiSP())) {
                                dm.setLoaiSP(std.loaiSP());
                                changed = true;
                            }
                            if (dm.getMoTa() == null || dm.getMoTa().isBlank()) {
                                dm.setMoTa(std.desc());
                                changed = true;
                            }
                            break;
                        }
                    }
                }

                if (dm.getTrangThai() == null) {
                    dm.setTrangThai(true);
                    changed = true;
                }

                if (changed) {
                    danhMucRepository.save(dm);
                }
            }

            // 2. Thêm các danh mục con chuẩn còn thiếu
            List<DanhMuc> refreshedList = danhMucRepository.findAll();
            for (StandardCategory std : STANDARD_CATEGORIES) {
                boolean exists = refreshedList.stream()
                        .anyMatch(d -> d.getTenDanhMuc() != null && d.getTenDanhMuc().trim().equalsIgnoreCase(std.name()));
                if (!exists) {
                    DanhMuc newDm = new DanhMuc();
                    newDm.setTenDanhMuc(std.name());
                    newDm.setMoTa(std.desc());
                    newDm.setLoaiSP(std.loaiSP());
                    newDm.setTrangThai(true);
                    danhMucRepository.save(newDm);
                    log.info("Khởi tạo danh mục tiêu chuẩn: '{}' [{}]", std.name(), std.loaiSP());
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
            DanhMucLon dmlQuaTang = new DanhMucLon(null, "QuaTang", "Quà Tặng & Trang Trí", "redeem", "Đèn đọc sách, mô hình book nook, túi tote, boardgame...", true, 3);

            danhMucLonRepository.saveAll(List.of(dmlSach, dmlVpp, dmlQuaTang));
            log.info("Đã khởi tạo 3 danh mục lớn tiêu chuẩn: Sách, Văn Phòng Phẩm, Quà Tặng & Trang Trí");
        } else {
            // Đảm bảo tối thiểu 3 danh mục lớn cơ bản luôn có
            ensureMajorCategoryExists("Sach", "Sách", "menu_book", "Ấn bản, tác phẩm, văn học, giáo trình...", 1);
            ensureMajorCategoryExists("VanPhongPham", "Văn Phòng Phẩm", "edit_note", "Sổ tay, bút viết, bookmark, dụng cụ văn phòng...", 2);
            ensureMajorCategoryExists("QuaTang", "Quà Tặng & Trang Trí", "redeem", "Đèn đọc sách, mô hình book nook, túi tote, boardgame...", 3);
        }
    }

    private void ensureMajorCategoryExists(String maLoai, String tenLoai, String icon, String moTa, int thuTu) {
        if (!danhMucLonRepository.existsByMaLoai(maLoai)) {
            DanhMucLon dml = new DanhMucLon(null, maLoai, tenLoai, icon, moTa, true, thuTu);
            danhMucLonRepository.save(dml);
            log.info("Bổ sung danh mục lớn: '{}' [{}]", tenLoai, maLoai);
        }
    }
}
