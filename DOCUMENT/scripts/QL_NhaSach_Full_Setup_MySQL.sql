-- ====================================================================
-- THE4BOOKSTORE - HỆ THỐNG CƠ SỞ DỮ LIỆU SÀN THƯƠNG MẠI ĐIỆN TỬ SÁCH
-- FILE TẠO CSDL TOÀN DIỆN VÀ MỚI NHẤT DÀNH CHO MYSQL 8.0+ / AIVEN CLOUD
-- PHIÊN BẢN CHUYỂN ĐỔI TỪ QL_NhaSach_Full_Setup.sql
-- MẬT KHẨU MẶC ĐỊNH TẤT CẢ TÀI KHOẢN: 123456
-- ====================================================================

CREATE DATABASE IF NOT EXISTS `defaultdb` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `defaultdb`;

SET FOREIGN_KEY_CHECKS = 0;

-- ====================================================================
-- DỌN DẸP TRIỆT ĐỂ CÁC BẢNG CŨ KHÔNG CÒN SỬ DỤNG
-- ====================================================================
DROP TABLE IF EXISTS `chi_tiet_phieu_tra`;
DROP TABLE IF EXISTS `phieu_tra_hang`;
DROP TABLE IF EXISTS `chi_tiet_phieu_nhap`;
DROP TABLE IF EXISTS `phieu_nhap`;
DROP TABLE IF EXISTS `hoa_don`;
DROP TABLE IF EXISTS `kho_hang`;
DROP TABLE IF EXISTS `kho`;
DROP TABLE IF EXISTS `yeu_cau_cap_hang`;
DROP TABLE IF EXISTS `nhan_vien`;
DROP TABLE IF EXISTS `nha_cung_cap`;
DROP TABLE IF EXISTS `nha_xuat_ban`;

-- ====================================================================
-- PHẦN 1: TẠO BẢNG DỮ LIỆU & RÀNG BUỘC (TABLES & CONSTRAINTS)
-- ====================================================================

-- 1. Bảng: tai_khoan
DROP TABLE IF EXISTS `tai_khoan`;
CREATE TABLE `tai_khoan` (
    `ma_tai_khoan` INT AUTO_INCREMENT PRIMARY KEY,
    `ten_dang_nhap` VARCHAR(50) NOT NULL UNIQUE,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `mat_khau_hash` VARCHAR(255) NULL,
    `vai_tro` VARCHAR(30) NOT NULL,
    `trang_thai` VARCHAR(30) NOT NULL DEFAULT 'HoatDong',
    `auth_provider` VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
    `provider_id` VARCHAR(100) NULL,
    `token_version` INT NOT NULL DEFAULT 1,
    `otp_code` VARCHAR(10) NULL,
    `otp_expiry` DATETIME NULL,
    `ngay_tao` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `CK_tai_khoan_vaitro` CHECK (`vai_tro` IN ('USER', 'KHACHHANG', 'VENDOR', 'MANAGER', 'ADMIN'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Bảng: khach_hang
DROP TABLE IF EXISTS `khach_hang`;
CREATE TABLE `khach_hang` (
    `makh` INT AUTO_INCREMENT PRIMARY KEY,
    `ma_tai_khoan` INT NULL UNIQUE,
    `ho_ten` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NULL,
    `so_dien_thoai` VARCHAR(20) NULL,
    `dia_chi` VARCHAR(255) NULL,
    `anh_dai_dien` VARCHAR(500) NULL,
    `ngay_dang_ky` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `FK_khachhang_taikhoan` FOREIGN KEY (`ma_tai_khoan`) REFERENCES `tai_khoan` (`ma_tai_khoan`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Bảng: dia_chi_giao_hang
DROP TABLE IF EXISTS `dia_chi_giao_hang`;
CREATE TABLE `dia_chi_giao_hang` (
    `ma_dia_chi` INT AUTO_INCREMENT PRIMARY KEY,
    `makh` INT NOT NULL,
    `ten_nguoi_nhan` VARCHAR(100) NOT NULL,
    `so_dien_thoai` VARCHAR(20) NOT NULL,
    `dia_chi_chi_tiet` VARCHAR(255) NOT NULL,
    `phuong_xa` VARCHAR(100) NULL,
    `quan_huyen` VARCHAR(100) NULL,
    `tinh_thanh` VARCHAR(100) NOT NULL,
    `la_mac_dinh` BOOLEAN NOT NULL DEFAULT FALSE,
    `ngay_tao` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `FK_diachi_khachhang` FOREIGN KEY (`makh`) REFERENCES `khach_hang` (`makh`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Bảng: shop
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop` (
    `ma_shop` INT AUTO_INCREMENT PRIMARY KEY,
    `ma_tai_khoan` INT NOT NULL UNIQUE,
    `ten_shop` VARCHAR(150) NOT NULL,
    `slug` VARCHAR(150) NOT NULL UNIQUE,
    `logo` VARCHAR(500) NULL,
    `banner` VARCHAR(500) NULL,
    `mo_ta` LONGTEXT NULL,
    `dia_chi_shop` VARCHAR(255) NULL,
    `so_dien_thoai` VARCHAR(20) NULL,
    `email_shop` VARCHAR(100) NULL,
    `chiet_khau_phan_tram` DECIMAL(5,2) NOT NULL DEFAULT 5.00,
    `trang_thai` VARCHAR(30) NOT NULL DEFAULT 'ChoDuyet',
    `ngay_tao` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `ngay_cap_nhat` DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `FK_shop_taikhoan` FOREIGN KEY (`ma_tai_khoan`) REFERENCES `tai_khoan` (`ma_tai_khoan`) ON DELETE CASCADE,
    CONSTRAINT `CK_shop_trangthai` CHECK (`trang_thai` IN ('BiKhoa', 'HoatDong', 'ChoDuyet', 'TuChoi', 'DaXoa'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Bảng: danh_muc
DROP TABLE IF EXISTS `danh_muc`;
CREATE TABLE `danh_muc` (
    `ma_danh_muc` INT AUTO_INCREMENT PRIMARY KEY,
    `ten_danh_muc` VARCHAR(100) NOT NULL,
    `mo_ta` VARCHAR(500) NULL,
    `trang_thai` BOOLEAN NOT NULL DEFAULT TRUE,
    `loaisp` VARCHAR(30) NOT NULL DEFAULT 'Sach',
    INDEX `idx_danhmuc_trangthai` (`trang_thai`),
    INDEX `idx_danhmuc_loaisp` (`loaisp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Bảng: tac_gia
DROP TABLE IF EXISTS `tac_gia`;
CREATE TABLE `tac_gia` (
    `ma_tac_gia` INT AUTO_INCREMENT PRIMARY KEY,
    `ten_tac_gia` VARCHAR(150) NOT NULL,
    `mo_ta` LONGTEXT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Bảng: san_pham
DROP TABLE IF EXISTS `san_pham`;
CREATE TABLE `san_pham` (
    `masp` INT AUTO_INCREMENT PRIMARY KEY,
    `ma_shop` INT NOT NULL,
    `ma_danh_muc` INT NOT NULL,
    `tensp` VARCHAR(200) NOT NULL,
    `isbn` VARCHAR(20) NULL,
    `gia_ban` INT NOT NULL,
    `so_luong_ton` INT NOT NULL DEFAULT 0,
    `so_luong_da_ban` INT NOT NULL DEFAULT 0,
    `muc_ton_toi_thieu` INT NOT NULL DEFAULT 5,
    `loaisp` VARCHAR(50) NOT NULL DEFAULT 'Sach',
    `mo_ta` LONGTEXT NULL,
    `hinh_anh` VARCHAR(500) NULL,
    `trang_thai` VARCHAR(30) NOT NULL DEFAULT 'DangBan',
    `trang_thai_khoa` VARCHAR(30) NOT NULL DEFAULT 'BinhThuong',
    `ngay_tao` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_sanpham_giaban` (`gia_ban`),
    INDEX `idx_sanpham_shop_trangthai` (`ma_shop`, `trang_thai`),
    CONSTRAINT `FK_sanpham_danhmuc` FOREIGN KEY (`ma_danh_muc`) REFERENCES `danh_muc` (`ma_danh_muc`),
    CONSTRAINT `FK_sanpham_shop` FOREIGN KEY (`ma_shop`) REFERENCES `shop` (`ma_shop`) ON DELETE CASCADE,
    CONSTRAINT `CK_sanpham_giaban` CHECK (`gia_ban` >= 0),
    CONSTRAINT `CK_sanpham_tonkho` CHECK (`so_luong_ton` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Bảng: san_pham_tac_gia
DROP TABLE IF EXISTS `san_pham_tac_gia`;
CREATE TABLE `san_pham_tac_gia` (
    `masp` INT NOT NULL,
    `ma_tac_gia` INT NOT NULL,
    `thu_tu_tac_gia` INT NULL,
    PRIMARY KEY (`masp`, `ma_tac_gia`),
    CONSTRAINT `FK_sptg_sanpham` FOREIGN KEY (`masp`) REFERENCES `san_pham` (`masp`) ON DELETE CASCADE,
    CONSTRAINT `FK_sptg_tacgia` FOREIGN KEY (`ma_tac_gia`) REFERENCES `tac_gia` (`ma_tac_gia`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Bảng: phieu_kiem_ke
DROP TABLE IF EXISTS `phieu_kiem_ke`;
CREATE TABLE `phieu_kiem_ke` (
    `ma_phieu_kiem_ke` INT AUTO_INCREMENT PRIMARY KEY,
    `ma_shop` INT NOT NULL,
    `ngay_kiem_ke` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `ngay_phe_duyet` DATETIME NULL,
    `trang_thai` VARCHAR(30) NOT NULL DEFAULT 'DangKiemKe',
    `ghi_chu` VARCHAR(500) NULL,
    CONSTRAINT `FK_kiemke_shop` FOREIGN KEY (`ma_shop`) REFERENCES `shop` (`ma_shop`) ON DELETE CASCADE,
    CONSTRAINT `CK_kiemke_trangthai` CHECK (`trang_thai` IN ('DaHuy', 'DaCanBang', 'DangKiemKe'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. Bảng: chi_tiet_kiem_ke
DROP TABLE IF EXISTS `chi_tiet_kiem_ke`;
CREATE TABLE `chi_tiet_kiem_ke` (
    `ma_phieu_kiem_ke` INT NOT NULL,
    `masp` INT NOT NULL,
    `so_luong_he_thong` INT NOT NULL,
    `so_luong_thuc_te` INT NOT NULL,
    `chenh_lech` INT NOT NULL DEFAULT 0,
    `ly_do` VARCHAR(500) NULL,
    PRIMARY KEY (`ma_phieu_kiem_ke`, `masp`),
    CONSTRAINT `FK_ctkk_phieu` FOREIGN KEY (`ma_phieu_kiem_ke`) REFERENCES `phieu_kiem_ke` (`ma_phieu_kiem_ke`) ON DELETE CASCADE,
    CONSTRAINT `FK_ctkk_sanpham` FOREIGN KEY (`masp`) REFERENCES `san_pham` (`masp`),
    CONSTRAINT `CK_ctkk_soluongthucte` CHECK (`so_luong_thuc_te` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. Bảng: nha_van_chuyen
DROP TABLE IF EXISTS `nha_van_chuyen`;
CREATE TABLE `nha_van_chuyen` (
    `ma_nvc` INT AUTO_INCREMENT PRIMARY KEY,
    `ten_nvc` VARCHAR(100) NOT NULL,
    `phi_co_ban` INT NOT NULL DEFAULT 25000,
    `thoi_gian_du_kien` VARCHAR(50) NOT NULL DEFAULT '2 - 3 ngày',
    `trang_thai` VARCHAR(30) NOT NULL DEFAULT 'HoatDong'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. Bảng: khuyen_mai
DROP TABLE IF EXISTS `khuyen_mai`;
CREATE TABLE `khuyen_mai` (
    `makm` INT AUTO_INCREMENT PRIMARY KEY,
    `ma_code` VARCHAR(50) NOT NULL UNIQUE,
    `tenkm` VARCHAR(150) NOT NULL,
    `mo_ta` VARCHAR(500) NULL,
    `loai_giam` VARCHAR(20) NOT NULL,
    `gia_tri_giam` INT NOT NULL,
    `giam_toi_da` INT NULL,
    `don_toi_thieu` INT NOT NULL DEFAULT 0,
    `so_luong_toi_da` INT NULL,
    `so_luong_da_dung` INT NOT NULL DEFAULT 0,
    `pham_vi` VARCHAR(20) NOT NULL DEFAULT 'TOAN_SAN',
    `ma_shop` INT NULL,
    `ngay_bat_dau` DATETIME NOT NULL,
    `ngay_ket_thuc` DATETIME NOT NULL,
    `trang_thai` VARCHAR(30) NOT NULL DEFAULT 'HoatDong',
    `hien_thi_cong_khai` BOOLEAN NOT NULL DEFAULT TRUE,
    `gioi_han_moi_khach_hang` INT NOT NULL DEFAULT 1,
    `doi_tuong_khach_hang` VARCHAR(20) NOT NULL DEFAULT 'ALL',
    `loai_khuyen_mai` VARCHAR(30) NOT NULL DEFAULT 'GIAM_GIA_SAN_PHAM',
    INDEX `idx_khuyenmai_code` (`ma_code`, `trang_thai`),
    CONSTRAINT `FK_km_shop` FOREIGN KEY (`ma_shop`) REFERENCES `shop` (`ma_shop`) ON DELETE CASCADE,
    CONSTRAINT `CK_km_giatri` CHECK (`gia_tri_giam` > 0),
    CONSTRAINT `CK_km_loaigiam` CHECK (`loai_giam` IN ('Freeship', 'TienCoDinh', 'PhanTram')),
    CONSTRAINT `CK_km_ngay` CHECK (`ngay_ket_thuc` >= `ngay_bat_dau`),
    CONSTRAINT `CK_km_phamvi` CHECK (`pham_vi` IN ('SHOP', 'TOAN_SAN'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. Bảng: voucher_da_luu
DROP TABLE IF EXISTS `voucher_da_luu`;
CREATE TABLE `voucher_da_luu` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `makh` INT NOT NULL,
    `makm` INT NOT NULL,
    `trang_thai` VARCHAR(30) NOT NULL DEFAULT 'ChuaDung',
    `ngay_luu` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `ngay_su_dung` DATETIME NULL,
    UNIQUE KEY `UQ_voucherdaluu` (`makh`, `makm`),
    CONSTRAINT `FK_voucherdaluu_km` FOREIGN KEY (`makm`) REFERENCES `khuyen_mai` (`makm`),
    CONSTRAINT `FK_voucherdaluu_khachhang` FOREIGN KEY (`makh`) REFERENCES `khach_hang` (`makh`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 14. Bảng: gio_hang
DROP TABLE IF EXISTS `gio_hang`;
CREATE TABLE `gio_hang` (
    `ma_gio_hang` INT AUTO_INCREMENT PRIMARY KEY,
    `makh` INT NOT NULL UNIQUE,
    `ngay_tao` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `ngay_cap_nhat` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `FK_giohang_khachhang` FOREIGN KEY (`makh`) REFERENCES `khach_hang` (`makh`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 15. Bảng: chi_tiet_gio_hang
DROP TABLE IF EXISTS `chi_tiet_gio_hang`;
CREATE TABLE `chi_tiet_gio_hang` (
    `ma_gio_hang` INT NOT NULL,
    `masp` INT NOT NULL,
    `so_luong` INT NOT NULL,
    PRIMARY KEY (`ma_gio_hang`, `masp`),
    CONSTRAINT `FK_ctgh_giohang` FOREIGN KEY (`ma_gio_hang`) REFERENCES `gio_hang` (`ma_gio_hang`) ON DELETE CASCADE,
    CONSTRAINT `FK_ctgh_sanpham` FOREIGN KEY (`masp`) REFERENCES `san_pham` (`masp`),
    CONSTRAINT `CK_ctgh_soluong` CHECK (`so_luong` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 16. Bảng: don_hang
DROP TABLE IF EXISTS `don_hang`;
CREATE TABLE `don_hang` (
    `madh` INT AUTO_INCREMENT PRIMARY KEY,
    `makh` INT NOT NULL,
    `ma_shop` INT NOT NULL,
    `ma_nvc` INT NULL,
    `makm` INT NULL,
    `ma_km_freeship` INT NULL,
    `dia_chi_giao` VARCHAR(255) NOT NULL,
    `so_dien_thoai_giao` VARCHAR(20) NOT NULL,
    `tong_tien` INT NOT NULL,
    `phi_van_chuyen` INT NOT NULL DEFAULT 0,
    `tien_giam` INT NOT NULL DEFAULT 0,
    `tien_giam_freeship` INT NOT NULL DEFAULT 0,
    `chiet_khau_app_phan_tram` DECIMAL(5,2) NOT NULL DEFAULT 5.00,
    `tien_phi_san` INT NOT NULL DEFAULT 0,
    `tien_thuc_nhan_shop` INT NOT NULL DEFAULT 0,
    `phuong_thuc_thanh_toan` VARCHAR(30) NOT NULL DEFAULT 'COD',
    `trang_thai_thanh_toan` VARCHAR(30) NOT NULL DEFAULT 'ChuaThanhToan',
    `trang_thai` VARCHAR(30) NOT NULL DEFAULT 'ChoXacNhan',
    `ma_van_don` VARCHAR(50) NULL,
    `ly_do_huy` VARCHAR(500) NULL,
    `ly_do_tu_choi` VARCHAR(500) NULL,
    `ngay_dat` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `ngay_xac_nhan` DATETIME NULL,
    `ngay_hoan_thanh` DATETIME NULL,
    `ly_do_tra_hang` LONGTEXT NULL,
    `phan_quyet_tranh_chap` LONGTEXT NULL,
    `nguoi_xu_ly_tranh_chap` INT NULL,
    INDEX `idx_donhang_makh_ngaydat` (`makh`, `ngay_dat`),
    INDEX `idx_donhang_shop_trangthai` (`ma_shop`, `trang_thai`),
    INDEX `idx_donhang_km_freeship` (`ma_km_freeship`),
    CONSTRAINT `FK_donhang_km` FOREIGN KEY (`makm`) REFERENCES `khuyen_mai` (`makm`),
    CONSTRAINT `FK_donhang_km_freeship` FOREIGN KEY (`ma_km_freeship`) REFERENCES `khuyen_mai` (`makm`),
    CONSTRAINT `FK_donhang_khachhang` FOREIGN KEY (`makh`) REFERENCES `khach_hang` (`makh`),
    CONSTRAINT `FK_donhang_nvc` FOREIGN KEY (`ma_nvc`) REFERENCES `nha_van_chuyen` (`ma_nvc`),
    CONSTRAINT `FK_donhang_nguoixuly` FOREIGN KEY (`nguoi_xu_ly_tranh_chap`) REFERENCES `tai_khoan` (`ma_tai_khoan`),
    CONSTRAINT `FK_donhang_shop` FOREIGN KEY (`ma_shop`) REFERENCES `shop` (`ma_shop`),
    CONSTRAINT `CK_donhang_chietkhau` CHECK (`chiet_khau_app_phan_tram` BETWEEN 0 AND 100),
    CONSTRAINT `CK_donhang_tongtien` CHECK (`tong_tien` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 17. Bảng: chi_tiet_don_hang
DROP TABLE IF EXISTS `chi_tiet_don_hang`;
CREATE TABLE `chi_tiet_don_hang` (
    `madh` INT NOT NULL,
    `masp` INT NOT NULL,
    `don_gia` INT NOT NULL,
    `so_luong` INT NOT NULL,
    PRIMARY KEY (`madh`, `masp`),
    CONSTRAINT `FK_ctdh_donhang` FOREIGN KEY (`madh`) REFERENCES `don_hang` (`madh`) ON DELETE CASCADE,
    CONSTRAINT `FK_ctdh_sanpham` FOREIGN KEY (`masp`) REFERENCES `san_pham` (`masp`),
    CONSTRAINT `CK_ctdh_dongia` CHECK (`don_gia` >= 0),
    CONSTRAINT `CK_ctdh_soluong` CHECK (`so_luong` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 18. Bảng: thanh_toan
DROP TABLE IF EXISTS `thanh_toan`;
CREATE TABLE `thanh_toan` (
    `ma_thanh_toan` INT AUTO_INCREMENT PRIMARY KEY,
    `madh` INT NOT NULL,
    `ma_giao_dich` VARCHAR(100) NULL,
    `phuong_thuc` VARCHAR(30) NOT NULL,
    `so_tien` INT NOT NULL,
    `noi_dung` VARCHAR(500) NULL,
    `trang_thai` VARCHAR(30) NOT NULL DEFAULT 'ChoThanhToan',
    `ngay_thanh_toan` DATETIME NULL,
    CONSTRAINT `FK_thanhtoan_donhang` FOREIGN KEY (`madh`) REFERENCES `don_hang` (`madh`) ON DELETE CASCADE,
    CONSTRAINT `CK_thanhtoan_sotien` CHECK (`so_tien` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 19. Bảng: danh_gia
DROP TABLE IF EXISTS `danh_gia`;
CREATE TABLE `danh_gia` (
    `ma_danh_gia` INT AUTO_INCREMENT PRIMARY KEY,
    `madh` INT NOT NULL,
    `makh` INT NOT NULL,
    `masp` INT NOT NULL,
    `so_sao` INT NOT NULL,
    `noi_dung` VARCHAR(1000) NULL,
    `hinh_anh_danh_gia` VARCHAR(500) NULL,
    `trang_thai` VARCHAR(30) NOT NULL DEFAULT 'HienThi',
    `ngay_danh_gia` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `UQ_danhgia_makh_masp_madh` (`madh`, `makh`, `masp`),
    CONSTRAINT `FK_danhgia_donhang` FOREIGN KEY (`madh`) REFERENCES `don_hang` (`madh`),
    CONSTRAINT `FK_danhgia_khachhang` FOREIGN KEY (`makh`) REFERENCES `khach_hang` (`makh`),
    CONSTRAINT `FK_danhgia_sanpham` FOREIGN KEY (`masp`) REFERENCES `san_pham` (`masp`),
    CONSTRAINT `CK_danhgia_sosao` CHECK (`so_sao` BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 20. Bảng: danh_gia_media
DROP TABLE IF EXISTS `danh_gia_media`;
CREATE TABLE `danh_gia_media` (
    `ma_media` INT AUTO_INCREMENT PRIMARY KEY,
    `ma_danh_gia` INT NOT NULL,
    `loai_media` VARCHAR(10) NOT NULL DEFAULT 'IMAGE',
    `url` VARCHAR(500) NOT NULL,
    CONSTRAINT `FK_dgmedia_danhgia` FOREIGN KEY (`ma_danh_gia`) REFERENCES `danh_gia` (`ma_danh_gia`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 21. Bảng: san_pham_yeu_thich
DROP TABLE IF EXISTS `san_pham_yeu_thich`;
CREATE TABLE `san_pham_yeu_thich` (
    `makh` INT NOT NULL,
    `masp` INT NOT NULL,
    `ngay_thich` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`makh`, `masp`),
    CONSTRAINT `FK_spyt_khachhang` FOREIGN KEY (`makh`) REFERENCES `khach_hang` (`makh`) ON DELETE CASCADE,
    CONSTRAINT `FK_spyt_sanpham` FOREIGN KEY (`masp`) REFERENCES `san_pham` (`masp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 22. Bảng: san_pham_da_xem
DROP TABLE IF EXISTS `san_pham_da_xem`;
CREATE TABLE `san_pham_da_xem` (
    `makh` INT NOT NULL,
    `masp` INT NOT NULL,
    `thoi_gian_xem` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`makh`, `masp`),
    CONSTRAINT `FK_spdx_khachhang` FOREIGN KEY (`makh`) REFERENCES `khach_hang` (`makh`) ON DELETE CASCADE,
    CONSTRAINT `FK_spdx_sanpham` FOREIGN KEY (`masp`) REFERENCES `san_pham` (`masp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 23. Bảng: password_reset_token
DROP TABLE IF EXISTS `password_reset_token`;
CREATE TABLE `password_reset_token` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `otp_code` VARCHAR(10) NOT NULL,
    `token` VARCHAR(100) NULL,
    `ma_tai_khoan` INT NOT NULL,
    `expiry_date` DATETIME NOT NULL,
    `used` BOOLEAN NOT NULL DEFAULT FALSE,
    `ngay_tao` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `FK_prt_taikhoan` FOREIGN KEY (`ma_tai_khoan`) REFERENCES `tai_khoan` (`ma_tai_khoan`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 24. Bảng: cau_hinh_he_thong
DROP TABLE IF EXISTS `cau_hinh_he_thong`;
CREATE TABLE `cau_hinh_he_thong` (
    `ma_cau_hinh` VARCHAR(50) PRIMARY KEY,
    `gia_tri` VARCHAR(255) NOT NULL,
    `mo_ta` VARCHAR(255) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 25. Bảng: nhat_ky_bao_mat
DROP TABLE IF EXISTS `nhat_ky_bao_mat`;
CREATE TABLE `nhat_ky_bao_mat` (
    `ma_nhat_ky` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `ten_bang` VARCHAR(64) NOT NULL,
    `hanh_dong` VARCHAR(20) NOT NULL,
    `ban_ghi_id` INT NULL,
    `du_lieu_cu` LONGTEXT NULL,
    `du_lieu_moi` LONGTEXT NULL,
    `nguoi_thuc_hien` VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    `thoi_gian` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- ====================================================================
-- PHẦN 2: HÀM NGƯỜI DÙNG (USER-DEFINED FUNCTIONS)
-- ====================================================================

DELIMITER $$

-- 1. Hàm: fn_TinhDiemDanhGiaTrungBinh
DROP FUNCTION IF EXISTS `fn_TinhDiemDanhGiaTrungBinh`$$
CREATE FUNCTION `fn_TinhDiemDanhGiaTrungBinh`(p_masp INT) 
RETURNS DECIMAL(2,1)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_diem DECIMAL(2,1);
    SELECT ROUND(AVG(so_sao), 1) INTO v_diem
    FROM danh_gia
    WHERE masp = p_masp AND trang_thai = 'HienThi';
    RETURN COALESCE(v_diem, 5.0);
END$$

-- 2. Hàm: fn_DemDonHang
DROP FUNCTION IF EXISTS `fn_DemDonHang`$$
CREATE FUNCTION `fn_DemDonHang`(p_startDate DATETIME, p_endDate DATETIME) 
RETURNS BIGINT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_dem BIGINT DEFAULT 0;
    SELECT COUNT(*) INTO v_dem
    FROM don_hang dh
    WHERE dh.ngay_dat >= p_startDate 
      AND dh.ngay_dat < p_endDate
      AND dh.trang_thai NOT IN ('DaHuy', 'Huy', 'YeuCauHuy', 'TranhChap', 'TraHangHoanTien', 'DaHoan', 'DaHoanTien', 'ChoThanhToan')
      AND NOT (dh.phuong_thuc_thanh_toan != 'COD' AND dh.trang_thai_thanh_toan = 'ChuaThanhToan');
    RETURN COALESCE(v_dem, 0);
END$$

-- 3. Hàm: fn_DemSachCuaShop
DROP FUNCTION IF EXISTS `fn_DemSachCuaShop`$$
CREATE FUNCTION `fn_DemSachCuaShop`(p_ma_shop INT) 
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_count INT DEFAULT 0;
    SELECT COUNT(*) INTO v_count
    FROM san_pham
    WHERE ma_shop = p_ma_shop
      AND (trang_thai_khoa IS NULL OR (trang_thai_khoa != 'DaKhoa' AND trang_thai_khoa != 'BiKhoaBoiAdmin'))
      AND (trang_thai != 'DaXoa');
    RETURN COALESCE(v_count, 0);
END$$

-- 4. Hàm: fn_TinhDoanhThu
DROP FUNCTION IF EXISTS `fn_TinhDoanhThu`$$
CREATE FUNCTION `fn_TinhDoanhThu`(p_startDate DATETIME, p_endDate DATETIME) 
RETURNS BIGINT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_tong BIGINT DEFAULT 0;
    SELECT SUM(dh.tong_tien) INTO v_tong
    FROM don_hang dh
    WHERE dh.trang_thai IN ('DaGiao', 'GiaoThanhCong', 'HoanTat')
      AND (dh.trang_thai_thanh_toan IS NULL OR dh.trang_thai_thanh_toan != 'DaHoanTien')
      AND COALESCE(dh.ngay_hoan_thanh, dh.ngay_dat) >= p_startDate 
      AND COALESCE(dh.ngay_hoan_thanh, dh.ngay_dat) < p_endDate;
    RETURN COALESCE(v_tong, 0);
END$$

-- 5. Hàm: fn_TinhDoanhThuKhuyenMai
DROP FUNCTION IF EXISTS `fn_TinhDoanhThuKhuyenMai`$$
CREATE FUNCTION `fn_TinhDoanhThuKhuyenMai`(p_makm INT) 
RETURNS BIGINT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_tong BIGINT DEFAULT 0;
    SELECT SUM(tong_tien) INTO v_tong
    FROM don_hang
    WHERE makm = p_makm;
    RETURN COALESCE(v_tong, 0);
END$$

-- 6. Hàm: fn_TinhPhiSan
DROP FUNCTION IF EXISTS `fn_TinhPhiSan`$$
CREATE FUNCTION `fn_TinhPhiSan`(p_tongTien INT, p_maShop INT) 
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_pt DECIMAL(5,2) DEFAULT 5.00;
    IF p_maShop IS NOT NULL THEN
        SELECT COALESCE(chiet_khau_phan_tram, 5.00) INTO v_pt FROM shop WHERE ma_shop = p_maShop LIMIT 1;
    END IF;
    RETURN CAST(ROUND(p_tongTien * (v_pt / 100.0), 0) AS SIGNED);
END$$

-- 7. Hàm: fn_TinhTienGiamKhuyenMai
DROP FUNCTION IF EXISTS `fn_TinhTienGiamKhuyenMai`$$
CREATE FUNCTION `fn_TinhTienGiamKhuyenMai`(p_makm INT) 
RETURNS BIGINT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_tong BIGINT DEFAULT 0;
    SELECT SUM(tien_giam) INTO v_tong
    FROM don_hang
    WHERE makm = p_makm;
    RETURN COALESCE(v_tong, 0);
END$$

-- 8. Hàm: fn_TongSachDaBan
DROP FUNCTION IF EXISTS `fn_TongSachDaBan`$$
CREATE FUNCTION `fn_TongSachDaBan`() 
RETURNS BIGINT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_total BIGINT DEFAULT 0;
    SELECT SUM(ct.so_luong) INTO v_total
    FROM chi_tiet_don_hang ct
    JOIN don_hang dh ON ct.madh = dh.madh
    WHERE dh.trang_thai IN ('DaGiao', 'GiaoThanhCong', 'HoanTat')
      AND (dh.trang_thai_thanh_toan IS NULL OR dh.trang_thai_thanh_toan != 'DaHoanTien');
    RETURN COALESCE(v_total, 0);
END$$

-- 9. Hàm: fn_KiemTraVoucherHopLe
DROP FUNCTION IF EXISTS `fn_KiemTraVoucherHopLe`$$
CREATE FUNCTION `fn_KiemTraVoucherHopLe`(p_ma_code VARCHAR(50), p_ma_shop INT, p_tong_tien INT)
RETURNS BOOLEAN
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_hop_le BOOLEAN DEFAULT FALSE;
    IF EXISTS (
        SELECT 1 FROM khuyen_mai
        WHERE ma_code = p_ma_code
          AND trang_thai = 'HoatDong'
          AND NOW() BETWEEN ngay_bat_dau AND ngay_ket_thuc
          AND (so_luong_toi_da IS NULL OR so_luong_da_dung < so_luong_toi_da)
          AND p_tong_tien >= don_toi_thieu
          AND (pham_vi = 'TOAN_SAN' OR (pham_vi = 'SHOP' AND ma_shop = p_ma_shop))
    ) THEN
        SET v_hop_le = TRUE;
    END IF;
    RETURN v_hop_le;
END$$

-- 10. Hàm: fn_TinhTienGiamVoucher
DROP FUNCTION IF EXISTS `fn_TinhTienGiamVoucher`$$
CREATE FUNCTION `fn_TinhTienGiamVoucher`(p_makm INT, p_tong_tien INT)
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_loai_giam VARCHAR(20);
    DECLARE v_gia_tri INT;
    DECLARE v_giam_toi_da INT;
    DECLARE v_tien_giam INT DEFAULT 0;

    SELECT loai_giam, gia_tri_giam, giam_toi_da 
    INTO v_loai_giam, v_gia_tri, v_giam_toi_da
    FROM khuyen_mai
    WHERE makm = p_makm
    LIMIT 1;

    IF v_loai_giam = 'PhanTram' THEN
        SET v_tien_giam = CAST(ROUND(p_tong_tien * (v_gia_tri / 100.0), 0) AS SIGNED);
        IF v_giam_toi_da IS NOT NULL AND v_tien_giam > v_giam_toi_da THEN
            SET v_tien_giam = v_giam_toi_da;
        END IF;
    ELSEIF v_loai_giam = 'TienCoDinh' THEN
        SET v_tien_giam = v_gia_tri;
        IF v_tien_giam > p_tong_tien THEN
            SET v_tien_giam = p_tong_tien;
        END IF;
    END IF;

    RETURN v_tien_giam;
END$$

DELIMITER ;

-- ====================================================================
-- PHẦN 3: KHUNG NHÌN (VIEWS)
-- ====================================================================

-- View 1: Báo cáo doanh thu toàn sàn theo tháng
DROP VIEW IF EXISTS `vw_BaoCaoDoanhThuTheoThang`;
CREATE VIEW `vw_BaoCaoDoanhThuTheoThang` AS
SELECT 
    YEAR(ngay_dat) AS nam,
    MONTH(ngay_dat) AS thang,
    COUNT(madh) AS tong_so_don,
    SUM(CASE WHEN trang_thai IN ('GiaoThanhCong', 'HoanTat', 'DaGiao') AND (trang_thai_thanh_toan IS NULL OR trang_thai_thanh_toan != 'DaHoanTien') THEN 1 ELSE 0 END) AS so_don_thanh_cong,
    SUM(CASE WHEN trang_thai = 'DaHuy' THEN 1 ELSE 0 END) AS so_don_huy,
    SUM(CASE WHEN trang_thai IN ('GiaoThanhCong', 'HoanTat', 'DaGiao') AND (trang_thai_thanh_toan IS NULL OR trang_thai_thanh_toan != 'DaHoanTien') THEN tong_tien ELSE 0 END) AS tong_doanh_thu_hang,
    SUM(CASE WHEN trang_thai IN ('GiaoThanhCong', 'HoanTat', 'DaGiao') AND (trang_thai_thanh_toan IS NULL OR trang_thai_thanh_toan != 'DaHoanTien') THEN tien_phi_san ELSE 0 END) AS hoa_hong_san_thu,
    SUM(CASE WHEN trang_thai IN ('GiaoThanhCong', 'HoanTat', 'DaGiao') AND (trang_thai_thanh_toan IS NULL OR trang_thai_thanh_toan != 'DaHoanTien') THEN tien_thuc_nhan_shop ELSE 0 END) AS tong_tien_tra_shop
FROM don_hang
GROUP BY YEAR(COALESCE(ngay_hoan_thanh, ngay_dat)), MONTH(COALESCE(ngay_hoan_thanh, ngay_dat));

-- View 2: Báo cáo kết quả kinh doanh của từng Shop
DROP VIEW IF EXISTS `vw_DoanhThuTungShop`;
CREATE VIEW `vw_DoanhThuTungShop` AS
SELECT 
    s.ma_shop,
    s.ten_shop,
    s.so_dien_thoai,
    s.chiet_khau_phan_tram,
    COUNT(dh.madh) AS tong_don_dat,
    SUM(CASE WHEN dh.trang_thai IN ('GiaoThanhCong', 'HoanTat', 'DaGiao') AND (dh.trang_thai_thanh_toan IS NULL OR dh.trang_thai_thanh_toan != 'DaHoanTien') THEN 1 ELSE 0 END) AS don_hoan_thanh,
    COALESCE(SUM(CASE WHEN dh.trang_thai IN ('GiaoThanhCong', 'HoanTat', 'DaGiao') AND (dh.trang_thai_thanh_toan IS NULL OR dh.trang_thai_thanh_toan != 'DaHoanTien') THEN dh.tong_tien ELSE 0 END), 0) AS tong_tien_ban,
    COALESCE(SUM(CASE WHEN dh.trang_thai IN ('GiaoThanhCong', 'HoanTat', 'DaGiao') AND (dh.trang_thai_thanh_toan IS NULL OR dh.trang_thai_thanh_toan != 'DaHoanTien') THEN dh.tien_phi_san ELSE 0 END), 0) AS phi_san_da_nop,
    COALESCE(SUM(CASE WHEN dh.trang_thai IN ('GiaoThanhCong', 'HoanTat', 'DaGiao') AND (dh.trang_thai_thanh_toan IS NULL OR dh.trang_thai_thanh_toan != 'DaHoanTien') THEN dh.tien_thuc_nhan_shop ELSE 0 END), 0) AS thuc_nhan_ve_shop
FROM shop s
LEFT JOIN don_hang dh ON s.ma_shop = dh.ma_shop
GROUP BY s.ma_shop, s.ten_shop, s.so_dien_thoai, s.chiet_khau_phan_tram;

-- View 3: Top 10 cuốn sách bán chạy nhất toàn sàn
DROP VIEW IF EXISTS `vw_TopSachBanChayToanSan`;
CREATE VIEW `vw_TopSachBanChayToanSan` AS
SELECT 
    sp.masp,
    sp.tensp,
    s.ten_shop,
    dm.ten_danh_muc,
    sp.gia_ban,
    sp.so_luong_ton,
    sp.so_luong_da_ban,
    (sp.gia_ban * sp.so_luong_da_ban) AS tong_doanh_thu_sach
FROM san_pham sp
JOIN shop s ON sp.ma_shop = s.ma_shop
JOIN danh_muc dm ON sp.ma_danh_muc = dm.ma_danh_muc
ORDER BY sp.so_luong_da_ban DESC
LIMIT 10;

-- View 4: Cảnh báo sách sắp hết hàng của các Shop
DROP VIEW IF EXISTS `vw_CanhBaoTonKhoShop`;
CREATE VIEW `vw_CanhBaoTonKhoShop` AS
SELECT 
    sp.masp,
    sp.tensp,
    s.ma_shop,
    s.ten_shop,
    s.so_dien_thoai AS sdt_shop,
    sp.so_luong_ton,
    sp.muc_ton_toi_thieu,
    CASE 
        WHEN sp.so_luong_ton = 0 THEN 'HẾT HÀNG'
        ELSE 'SẮP HẾT'
    END AS muc_do_canh_bao
FROM san_pham sp
JOIN shop s ON sp.ma_shop = s.ma_shop
WHERE sp.so_luong_ton <= sp.muc_ton_toi_thieu AND sp.trang_thai IN ('HoatDong', 'DangBan');

-- View 5: Thống kê đánh giá sách
DROP VIEW IF EXISTS `vw_ThongKeDanhGiaSanPham`;
CREATE VIEW `vw_ThongKeDanhGiaSanPham` AS
SELECT 
    sp.masp,
    sp.tensp,
    s.ten_shop,
    COUNT(dg.ma_danh_gia) AS tong_luot_danh_gia,
    COALESCE(ROUND(AVG(dg.so_sao), 1), 5.0) AS diem_sao_trung_binh
FROM san_pham sp
JOIN shop s ON sp.ma_shop = s.ma_shop
LEFT JOIN danh_gia dg ON sp.masp = dg.masp AND dg.trang_thai = 'HienThi'
GROUP BY sp.masp, sp.tensp, s.ten_shop;

-- ====================================================================
-- PHẦN 4: THỦ TỤC LƯU TRỮ (STORED PROCEDURES)
-- ====================================================================

DELIMITER $$

-- 1. Thủ tục: sp_DoanhThuTheoThang
DROP PROCEDURE IF EXISTS `sp_DoanhThuTheoThang`$$
CREATE PROCEDURE `sp_DoanhThuTheoThang`()
BEGIN
    SELECT 
        YEAR(COALESCE(dh.ngay_hoan_thanh, dh.ngay_dat)) AS nam, 
        MONTH(COALESCE(dh.ngay_hoan_thanh, dh.ngay_dat)) AS thang, 
        SUM(dh.tong_tien) AS doanh_thu 
    FROM don_hang dh 
    WHERE dh.trang_thai IN ('DaGiao', 'GiaoThanhCong', 'HoanTat') 
      AND (dh.trang_thai_thanh_toan IS NULL OR dh.trang_thai_thanh_toan != 'DaHoanTien')
      AND YEAR(COALESCE(dh.ngay_hoan_thanh, dh.ngay_dat)) = YEAR(CURRENT_DATE())
    GROUP BY YEAR(COALESCE(dh.ngay_hoan_thanh, dh.ngay_dat)), MONTH(COALESCE(dh.ngay_hoan_thanh, dh.ngay_dat)) 
    ORDER BY nam DESC, thang DESC;
END$$

-- 2. Thủ tục: sp_TopSachBanChay
DROP PROCEDURE IF EXISTS `sp_TopSachBanChay`$$
CREATE PROCEDURE `sp_TopSachBanChay`(IN p_limit INT)
BEGIN
    IF p_limit IS NULL OR p_limit <= 0 THEN
        SET p_limit = 10;
    END IF;
    SELECT 
        s.masp,
        s.tensp,
        SUM(ct.so_luong) AS tong_da_ban,
        s.gia_ban,
        SUM(ct.so_luong * ct.don_gia) AS doanh_thu_dong_gop
    FROM san_pham s
    JOIN chi_tiet_don_hang ct ON s.masp = ct.masp
    JOIN don_hang dh ON ct.madh = dh.madh
    WHERE dh.trang_thai IN ('DaGiao', 'HoanTat')
    GROUP BY s.masp, s.tensp, s.gia_ban
    ORDER BY tong_da_ban DESC
    LIMIT p_limit;
END$$

-- 3. Thủ tục: sp_DoanhThuShopTheoThang
DROP PROCEDURE IF EXISTS `sp_DoanhThuShopTheoThang`$$
CREATE PROCEDURE `sp_DoanhThuShopTheoThang`(IN p_ma_shop INT)
BEGIN
    SELECT 
        YEAR(COALESCE(dh.ngay_hoan_thanh, dh.ngay_dat)) AS nam,
        MONTH(COALESCE(dh.ngay_hoan_thanh, dh.ngay_dat)) AS thang,
        COUNT(dh.madh) AS tong_don_hang,
        COALESCE(SUM(dh.tong_tien), 0) AS tong_doanh_thu,
        COALESCE(SUM(dh.tien_phi_san), 0) AS tong_phi_san,
        COALESCE(SUM(dh.tien_thuc_nhan_shop), 0) AS thuc_nhan_shop
    FROM don_hang dh
    WHERE dh.ma_shop = p_ma_shop
      AND dh.trang_thai IN ('DaGiao', 'GiaoThanhCong', 'HoanTat')
      AND (dh.trang_thai_thanh_toan IS NULL OR dh.trang_thai_thanh_toan != 'DaHoanTien')
    GROUP BY YEAR(COALESCE(dh.ngay_hoan_thanh, dh.ngay_dat)), MONTH(COALESCE(dh.ngay_hoan_thanh, dh.ngay_dat))
    ORDER BY nam DESC, thang DESC;
END$$

-- 4. Thủ tục: sp_HuyDonHang
DROP PROCEDURE IF EXISTS `sp_HuyDonHang`$$
CREATE PROCEDURE `sp_HuyDonHang`(IN p_madh INT, IN p_ly_do VARCHAR(500))
proc_label: BEGIN
    DECLARE v_trang_thai VARCHAR(30);
    DECLARE v_makm INT;
    DECLARE v_ma_km_freeship INT;

    START TRANSACTION;

    SELECT trang_thai, makm, ma_km_freeship INTO v_trang_thai, v_makm, v_ma_km_freeship
    FROM don_hang
    WHERE madh = p_madh
    FOR UPDATE;

    IF v_trang_thai IS NULL THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Đơn hàng không tồn tại!';
        LEAVE proc_label;
    END IF;

    IF v_trang_thai IN ('DangGiao', 'GiaoThanhCong', 'HoanTat', 'DaGiao') THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Đơn hàng đang giao hoặc đã hoàn tất, không thể hủy!';
        LEAVE proc_label;
    END IF;

    -- Hoàn lại tồn kho sách
    UPDATE san_pham sp
    JOIN chi_tiet_don_hang ct ON sp.masp = ct.masp
    SET sp.so_luong_ton = sp.so_luong_ton + ct.so_luong,
        sp.so_luong_da_ban = CASE WHEN sp.so_luong_da_ban >= ct.so_luong THEN sp.so_luong_da_ban - ct.so_luong ELSE 0 END
    WHERE ct.madh = p_madh;

    -- Cập nhật đơn hàng
    UPDATE don_hang
    SET trang_thai = 'DaHuy',
        ly_do_huy = p_ly_do
    WHERE madh = p_madh;

    -- Hoàn lại lượt dùng voucher giảm giá
    IF v_makm IS NOT NULL THEN
        UPDATE khuyen_mai
        SET so_luong_da_dung = CASE WHEN so_luong_da_dung > 0 THEN so_luong_da_dung - 1 ELSE 0 END
        WHERE makm = v_makm;
    END IF;

    -- Hoàn lại lượt dùng voucher freeship
    IF v_ma_km_freeship IS NOT NULL THEN
        UPDATE khuyen_mai
        SET so_luong_da_dung = CASE WHEN so_luong_da_dung > 0 THEN so_luong_da_dung - 1 ELSE 0 END
        WHERE makm = v_ma_km_freeship;
    END IF;

    COMMIT;
END$$

-- 5. Thủ tục: sp_ShopXacNhanDonHang
DROP PROCEDURE IF EXISTS `sp_ShopXacNhanDonHang`$$
CREATE PROCEDURE `sp_ShopXacNhanDonHang`(IN p_madh INT, IN p_ma_shop INT, IN p_ma_van_don VARCHAR(50))
proc_label: BEGIN
    START TRANSACTION;

    IF NOT EXISTS (
        SELECT 1 FROM don_hang 
        WHERE madh = p_madh AND ma_shop = p_ma_shop AND trang_thai IN ('ChoXacNhan', 'ChoXuLy', 'DonHangMoi')
    ) THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Đơn hàng không hợp lệ hoặc không ở trạng thái Chờ xác nhận!';
        LEAVE proc_label;
    END IF;

    UPDATE don_hang 
    SET trang_thai = 'DangGiao',
        ngay_xac_nhan = NOW(),
        ma_van_don = p_ma_van_don 
    WHERE madh = p_madh;

    COMMIT;
END$$

-- 6. Thủ tục: sp_ShopXacNhanKiemKe
DROP PROCEDURE IF EXISTS `sp_ShopXacNhanKiemKe`$$
CREATE PROCEDURE `sp_ShopXacNhanKiemKe`(IN p_ma_phieu_kiem_ke INT, IN p_ma_shop INT)
proc_label: BEGIN
    START TRANSACTION;

    IF NOT EXISTS (
        SELECT 1 FROM phieu_kiem_ke 
        WHERE ma_phieu_kiem_ke = p_ma_phieu_kiem_ke AND ma_shop = p_ma_shop
    ) THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Phiếu kiểm kê không tồn tại hoặc không thuộc quyền quản lý của Shop!';
        LEAVE proc_label;
    END IF;

    -- Cập nhật số lượng thực tế sang tồn kho san_pham
    UPDATE san_pham sp
    JOIN chi_tiet_kiem_ke ctkk ON sp.masp = ctkk.masp
    SET sp.so_luong_ton = ctkk.so_luong_thuc_te
    WHERE ctkk.ma_phieu_kiem_ke = p_ma_phieu_kiem_ke;

    -- Đổi trạng thái phiếu kiểm kê
    UPDATE phieu_kiem_ke
    SET trang_thai = 'DaCanBang',
        ngay_phe_duyet = NOW()
    WHERE ma_phieu_kiem_ke = p_ma_phieu_kiem_ke;

    COMMIT;
END$$

-- 7. Thủ tục: sp_DatHangVaTruTonKho
DROP PROCEDURE IF EXISTS `sp_DatHangVaTruTonKho`$$
CREATE PROCEDURE `sp_DatHangVaTruTonKho`(
    IN p_makh INT,
    IN p_ma_shop INT,
    IN p_ma_nvc INT,
    IN p_makm INT,
    IN p_ma_km_freeship INT,
    IN p_dia_chi_giao VARCHAR(255),
    IN p_so_dien_thoai_giao VARCHAR(20),
    IN p_tong_tien INT,
    IN p_phi_van_chuyen INT,
    IN p_tien_giam INT,
    IN p_tien_giam_freeship INT,
    IN p_phuong_thuc VARCHAR(30),
    OUT p_new_madh INT
)
BEGIN
    DECLARE v_chiet_khau DECIMAL(5,2) DEFAULT 5.00;
    DECLARE v_phi_san INT;
    DECLARE v_shop_nhan INT;

    START TRANSACTION;

    SELECT COALESCE(chiet_khau_phan_tram, 5.00) INTO v_chiet_khau FROM shop WHERE ma_shop = p_ma_shop LIMIT 1;
    SET v_phi_san = CAST(ROUND(p_tong_tien * (v_chiet_khau / 100.0), 0) AS SIGNED);
    SET v_shop_nhan = p_tong_tien - v_phi_san;

    INSERT INTO don_hang (
        makh, ma_shop, ma_nvc, makm, ma_km_freeship, dia_chi_giao, so_dien_thoai_giao,
        tong_tien, phi_van_chuyen, tien_giam, tien_giam_freeship, chiet_khau_app_phan_tram,
        tien_phi_san, tien_thuc_nhan_shop,
        phuong_thuc_thanh_toan, trang_thai, ngay_dat
    )
    VALUES (
        p_makh, p_ma_shop, p_ma_nvc, p_makm, p_ma_km_freeship, p_dia_chi_giao, p_so_dien_thoai_giao,
        p_tong_tien, COALESCE(p_phi_van_chuyen, 0), COALESCE(p_tien_giam, 0), COALESCE(p_tien_giam_freeship, 0), v_chiet_khau,
        v_phi_san, v_shop_nhan,
        COALESCE(p_phuong_thuc, 'COD'), 'ChoXacNhan', NOW()
    );

    SET p_new_madh = LAST_INSERT_ID();

    IF p_makm IS NOT NULL THEN
        UPDATE khuyen_mai
        SET so_luong_da_dung = so_luong_da_dung + 1
        WHERE makm = p_makm;
    END IF;

    IF p_ma_km_freeship IS NOT NULL THEN
        UPDATE khuyen_mai
        SET so_luong_da_dung = so_luong_da_dung + 1
        WHERE makm = p_ma_km_freeship;
    END IF;

    COMMIT;
END$$

-- 8. Thủ tục: sp_DonDepSanPhamDaXem
DROP PROCEDURE IF EXISTS `sp_DonDepSanPhamDaXem`$$
CREATE PROCEDURE `sp_DonDepSanPhamDaXem`(IN p_SoGioToiDa INT)
BEGIN
    IF p_SoGioToiDa IS NULL THEN
        SET p_SoGioToiDa = 1;
    END IF;
    DELETE FROM san_pham_da_xem 
    WHERE thoi_gian_xem < DATE_SUB(NOW(), INTERVAL p_SoGioToiDa HOUR);
    SELECT ROW_COUNT() AS SoBanGhiDaDonDep;
END$$

-- 9. Thủ tục: sp_TimKiemSachPhanTrang
DROP PROCEDURE IF EXISTS `sp_TimKiemSachPhanTrang`$$
CREATE PROCEDURE `sp_TimKiemSachPhanTrang`(
    IN p_tu_khoa VARCHAR(100),
    IN p_ma_danh_muc INT,
    IN p_gia_tu INT,
    IN p_gia_den INT,
    IN p_page_index INT,
    IN p_page_size INT
)
BEGIN
    DECLARE v_offset INT;
    IF p_page_index IS NULL OR p_page_index < 1 THEN SET p_page_index = 1; END IF;
    IF p_page_size IS NULL OR p_page_size < 1 THEN SET p_page_size = 12; END IF;
    SET v_offset = (p_page_index - 1) * p_page_size;

    SELECT 
        sp.masp,
        sp.tensp,
        sp.gia_ban,
        sp.hinh_anh,
        sp.so_luong_ton,
        sp.so_luong_da_ban,
        s.ma_shop,
        s.ten_shop,
        dm.ten_danh_muc,
        fn_TinhDiemDanhGiaTrungBinh(sp.masp) AS diem_sao
    FROM san_pham sp
    JOIN shop s ON sp.ma_shop = s.ma_shop
    JOIN danh_muc dm ON sp.ma_danh_muc = dm.ma_danh_muc
    WHERE sp.trang_thai IN ('DangBan', 'HoatDong')
      AND (sp.trang_thai_khoa = 'BinhThuong' OR sp.trang_thai_khoa IS NULL)
      AND (s.trang_thai = 'HoatDong' OR s.trang_thai IS NULL)
      AND (p_tu_khoa IS NULL OR sp.tensp LIKE CONCAT('%', p_tu_khoa, '%'))
      AND (p_ma_danh_muc IS NULL OR sp.ma_danh_muc = p_ma_danh_muc)
      AND (p_gia_tu IS NULL OR sp.gia_ban >= p_gia_tu)
      AND (p_gia_den IS NULL OR sp.gia_ban <= p_gia_den)
    ORDER BY sp.so_luong_da_ban DESC, sp.masp DESC
    LIMIT p_page_size OFFSET v_offset;
END$$

-- 10. Thủ tục: sp_BaoCaoDoanhThuTheoKhoangNgay
DROP PROCEDURE IF EXISTS `sp_BaoCaoDoanhThuTheoKhoangNgay`$$
CREATE PROCEDURE `sp_BaoCaoDoanhThuTheoKhoangNgay`(IN p_tu_ngay DATETIME, IN p_den_ngay DATETIME)
BEGIN
    SELECT 
        s.ma_shop,
        s.ten_shop,
        COUNT(dh.madh) AS so_don,
        COALESCE(SUM(dh.tong_tien), 0) AS tong_tien_ban,
        COALESCE(SUM(dh.tien_phi_san), 0) AS phi_san,
        COALESCE(SUM(dh.tien_thuc_nhan_shop), 0) AS shop_nhan
    FROM shop s
    JOIN don_hang dh ON s.ma_shop = dh.ma_shop
    WHERE dh.trang_thai IN ('GiaoThanhCong', 'HoanTat', 'DaGiao')
      AND (dh.trang_thai_thanh_toan IS NULL OR dh.trang_thai_thanh_toan != 'DaHoanTien')
      AND COALESCE(dh.ngay_hoan_thanh, dh.ngay_dat) BETWEEN p_tu_ngay AND p_den_ngay
    GROUP BY s.ma_shop, s.ten_shop;
END$$

DELIMITER ;

-- ====================================================================
-- PHẦN 5: TRIGGERS (RÀNG BUỘC TOÀN VẸN TỰ ĐỘNG)
-- ====================================================================

DELIMITER $$

-- Trigger 1: Kiểm tra tồn kho và trừ tồn kho khi thêm chi_tiet_don_hang
DROP TRIGGER IF EXISTS `trg_ChiTietDonHang_TruTonKho`$$
CREATE TRIGGER `trg_ChiTietDonHang_TruTonKho`
BEFORE INSERT ON `chi_tiet_don_hang`
FOR EACH ROW
BEGIN
    DECLARE v_ton INT;
    SELECT so_luong_ton INTO v_ton FROM san_pham WHERE masp = NEW.masp;
    IF v_ton < NEW.so_luong THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Lỗi: Số lượng sách trong kho của Shop không đủ để đáp ứng đơn hàng!';
    ELSE
        UPDATE san_pham 
        SET so_luong_ton = so_luong_ton - NEW.so_luong,
            so_luong_da_ban = so_luong_da_ban + NEW.so_luong
        WHERE masp = NEW.masp;
    END IF;
END$$

-- Trigger 2: Tự động tính phí sàn và tiền shop nhận trước khi tạo/cập nhật đơn hàng
DROP TRIGGER IF EXISTS `trg_DonHang_TinhTienPhiSan_BeforeInsert`$$
CREATE TRIGGER `trg_DonHang_TinhTienPhiSan_BeforeInsert`
BEFORE INSERT ON `don_hang`
FOR EACH ROW
BEGIN
    IF NEW.chiet_khau_app_phan_tram IS NULL OR NEW.chiet_khau_app_phan_tram = 0 THEN
        SET NEW.chiet_khau_app_phan_tram = 5.00;
    END IF;
    SET NEW.tien_phi_san = CAST(ROUND(NEW.tong_tien * (NEW.chiet_khau_app_phan_tram / 100.0), 0) AS SIGNED);
    SET NEW.tien_thuc_nhan_shop = NEW.tong_tien - NEW.tien_phi_san;
END$$

DROP TRIGGER IF EXISTS `trg_DonHang_TinhTienPhiSan_BeforeUpdate`$$
CREATE TRIGGER `trg_DonHang_TinhTienPhiSan_BeforeUpdate`
BEFORE UPDATE ON `don_hang`
FOR EACH ROW
BEGIN
    SET NEW.tien_phi_san = CAST(ROUND(NEW.tong_tien * (NEW.chiet_khau_app_phan_tram / 100.0), 0) AS SIGNED);
    SET NEW.tien_thuc_nhan_shop = NEW.tong_tien - NEW.tien_phi_san;
END$$

-- Trigger 3: Audit ghi vết thay đổi vai trò hoặc trạng thái tài khoản
DROP TRIGGER IF EXISTS `trg_AuditThayDoiVaiTroTaiKhoan`$$
CREATE TRIGGER `trg_AuditThayDoiVaiTroTaiKhoan`
AFTER UPDATE ON `tai_khoan`
FOR EACH ROW
BEGIN
    IF (OLD.vai_tro <> NEW.vai_tro) OR (OLD.trang_thai <> NEW.trang_thai) THEN
        INSERT INTO nhat_ky_bao_mat (ten_bang, hanh_dong, ban_ghi_id, du_lieu_cu, du_lieu_moi, nguoi_thuc_hien, thoi_gian)
        VALUES (
            'tai_khoan',
            'UPDATE',
            NEW.ma_tai_khoan,
            CONCAT('VaiTro: ', OLD.vai_tro, ', TrangThai: ', OLD.trang_thai),
            CONCAT('VaiTro: ', NEW.vai_tro, ', TrangThai: ', NEW.trang_thai),
            USER(),
            NOW()
        );
    END IF;
END$$

DELIMITER ;

-- ====================================================================
-- PHẦN 6: DỮ LIỆU MẪU ĐẦY ĐỦ (SEED DATA)
-- ====================================================================

SET FOREIGN_KEY_CHECKS = 0;

-- 1. tai_khoan
INSERT INTO `tai_khoan` (`ma_tai_khoan`, `ten_dang_nhap`, `email`, `mat_khau_hash`, `vai_tro`, `trang_thai`, `auth_provider`, `provider_id`, `token_version`, `otp_code`, `otp_expiry`, `ngay_tao`) VALUES
(1, 'admin', 'admin@the4bookstore.vn', '$2a$10$SCy7moQtkixA39J791LR3.EGHlLW.xjKMz/uSmCt20n1mWHOnYd0y', 'ADMIN', 'HoatDong', 'LOCAL', NULL, 1, NULL, NULL, '2026-10-16 15:04:53'),
(3, 'vendor_kimdong', 'kimdong@gmail.com', '$2a$10$SCy7moQtkixA39J791LR3.EGHlLW.xjKMz/uSmCt20n1mWHOnYd0y', 'VENDOR', 'HoatDong', 'LOCAL', NULL, 1, NULL, NULL, '2026-10-16 15:04:53'),
(4, 'vendor_nhanam', 'nhanam@gmail.com', '$2a$10$SCy7moQtkixA39J791LR3.EGHlLW.xjKMz/uSmCt20n1mWHOnYd0y', 'VENDOR', 'HoatDong', 'LOCAL', NULL, 1, NULL, NULL, '2026-10-16 15:04:53'),
(5, 'khachhang_an', 'nguyenvanan@gmail.com', '$2a$10$SCy7moQtkixA39J791LR3.EGHlLW.xjKMz/uSmCt20n1mWHOnYd0y', 'KHACHHANG', 'HoatDong', 'LOCAL', NULL, 1, NULL, NULL, '2026-10-16 15:04:53'),
(6, 'khachhang_binh', 'lethibinh@gmail.com', '$2a$10$SCy7moQtkixA39J791LR3.EGHlLW.xjKMz/uSmCt20n1mWHOnYd0y', 'KHACHHANG', 'HoatDong', 'LOCAL', NULL, 1, NULL, NULL, '2026-10-16 15:04:53'),
(7, 'phat', 'thangphatnekea21333@gmail.com', '$2a$10$SCy7moQtkixA39J791LR3.EGHlLW.xjKMz/uSmCt20n1mWHOnYd0y', 'KHACHHANG', 'HoatDong', 'LOCAL', NULL, 1, NULL, NULL, '2026-10-17 10:05:28'),
(8, 'manager', 'manager@the4bookstore.vn', '$2a$10$SCy7moQtkixA39J791LR3.EGHlLW.xjKMz/uSmCt20n1mWHOnYd0y', 'MANAGER', 'HoatDong', 'LOCAL', NULL, 1, NULL, NULL, '2026-10-17 03:37:45');

-- 2. khach_hang
INSERT INTO `khach_hang` (`makh`, `ma_tai_khoan`, `ho_ten`, `email`, `so_dien_thoai`, `dia_chi`, `anh_dai_dien`, `ngay_dang_ky`) VALUES
(1, 5, 'Nguyễn Văn An', 'nguyenvanan@gmail.com', '0988111222', '123 Lê Lợi, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh', NULL, '2026-10-16 15:04:53'),
(2, 6, 'Lê Thị Bình', 'lethibinh@gmail.com', '0977333444', '456 Võ Văn Ngân, Phường Linh Chiểu, TP. Thủ Đức, TP. Hồ Chí Minh', NULL, '2026-10-16 15:04:53'),
(3, 7, 'Vo Thanh Phat', 'thangphatnekea21333@gmail.com', '0967238940', 'Dĩ An', NULL, '2026-10-17 10:05:28'),
(4, 3, 'vendor_kimdong', 'kimdong@gmail.com', NULL, NULL, NULL, '2026-10-17 10:09:24'),
(5, 1, 'admin', 'admin@the4bookstore.vn', NULL, NULL, NULL, '2026-10-17 10:22:00'),
(7, 8, 'manager', 'manager@the4bookstore.vn', NULL, NULL, NULL, '2026-10-17 11:01:50'),
(8, 4, 'vendor_nhanam', 'nhanam@gmail.com', NULL, NULL, NULL, '2026-10-17 11:56:01');

-- 3. dia_chi_giao_hang
INSERT INTO `dia_chi_giao_hang` (`ma_dia_chi`, `makh`, `ten_nguoi_nhan`, `so_dien_thoai`, `dia_chi_chi_tiet`, `phuong_xa`, `quan_huyen`, `tinh_thanh`, `la_mac_dinh`, `ngay_tao`) VALUES
(1, 1, 'Nguyễn Văn An', '0988111222', '123 Lê Lợi, P. Bến Nghé, Quận 1', NULL, NULL, 'TP. Hồ Chí Minh', 1, '2026-10-16 15:04:53'),
(2, 2, 'Lê Thị Bình', '0977333444', '456 Võ Văn Ngân, P. Linh Chiểu, TP. Thủ Đức', NULL, NULL, 'TP. Hồ Chí Minh', 1, '2026-10-16 15:04:53'),
(3, 3, 'Nguyễn Ngọc Thiện', '0934567822', '462', 'Linh trung', 'TP.thủ đức', 'Hồ Chí minh', 1, '2026-10-17 11:08:22');

-- 4. shop
INSERT INTO `shop` (`ma_shop`, `ma_tai_khoan`, `ten_shop`, `slug`, `logo`, `banner`, `mo_ta`, `dia_chi_shop`, `so_dien_thoai`, `email_shop`, `chiet_khau_phan_tram`, `trang_thai`, `ngay_tao`, `ngay_cap_nhat`) VALUES
(1, 3, 'Hiệu Sách Kim Đồng Official', 'hieu-sach-kim-dong', 'https://res.cloudinary.com/vkqtqiha/image/upload/v1792206732/the4bookstore/shops/ypnxy2kltgf1o9bo6pvr.png', 'https://res.cloudinary.com/vkqtqiha/image/upload/v1792206734/the4bookstore/shops/zwhzbwbgrnmcgot7koac.jpg', 'Gian hàng chính hãng truyện tranh, văn học thiếu nhi của NXB Kim Đồng', '55 Quang Trung, Hai Bà Trưng, Hà Nội', '02439434730', 'kimdong@gmail.com', 5.00, 'HoatDong', '2026-10-16 15:04:53', '2026-10-17 10:12:18'),
(2, 4, 'Nhã Nam Book Store', 'nha-nam-book-store', NULL, NULL, 'Nhã Nam - Nhận diện tác phẩm kinh điển và văn học tinh hoa thế giới', '59 Đỗ Quang, Cầu Giấy, Hà Nội', '02435146875', 'nhanam@gmail.com', 5.00, 'HoatDong', '2026-10-16 15:04:53', NULL);

-- 5. danh_muc
INSERT INTO `danh_muc` (`ma_danh_muc`, `ten_danh_muc`, `mo_ta`, `trang_thai`) VALUES
(1, 'Văn Học Trong Nước', 'Tiểu thuyết, truyện ngắn, phóng sự và tác phẩm văn học Việt Nam', 1),
(2, 'Văn Học Nước Ngoài', 'Tác phẩm dịch kinh điển, văn học thế giới đạt giải Nobel, Booker', 1),
(3, 'Kinh Tế - Khởi Nghiệp', 'Quản trị kinh doanh, đầu tư, tài chính và khởi nghiệp đổi mới', 1),
(4, 'Công Nghệ Thông Tin', 'Lập trình, kiến trúc phần mềm, khoa học dữ liệu và an toàn thông tin', 1),
(5, 'Thiếu Nhi & Truyện Tranh', 'Manga, comic, truyện thiếu nhi và văn học giáo dục sớm', 1),
(6, 'Tâm Lý & Kỹ Năng Sống', 'Sách phát triển bản thân, tư duy tích cực và thấu hiểu nội tâm', 1);

-- 6. tac_gia
INSERT INTO `tac_gia` (`ma_tac_gia`, `ten_tac_gia`, `mo_ta`) VALUES
(1, 'Nguyễn Nhật Ánh', 'Nhà văn chuyên viết cho thanh thiếu niên được mến mộ bậc nhất Việt Nam'),
(2, 'Tô Hoài', 'Nhà văn đại thụ của nền văn học thiếu nhi Việt Nam'),
(3, 'Dale Carnegie', 'Tác giả bậc thầy người Mỹ về nghệ thuật giao tiếp và đối nhân xử thế'),
(4, 'Haruki Murakami', 'Nhà văn đương đại lừng danh người Nhật Bản'),
(5, 'Robert C. Martin', 'Huyền thoại Uncle Bob - tác giả của Clean Code và kiến trúc phần mềm'),
(6, 'Antoine de Saint-Exupéry', 'Nhà văn và phi công quả cảm người Pháp, tác giả Hoàng Tử Bé'),
(7, 'Paulo Coelho', 'Nhà văn nổi tiếng thế giới người Brazil, tác giả Nhà Giả Kim'),
(8, 'Morgan Housel', 'Chuyên gia tài chính và tác giả bán chạy toàn cầu về tâm lý học tiền bạc');

-- 7. san_pham
INSERT INTO `san_pham` (`masp`, `ma_shop`, `ma_danh_muc`, `tensp`, `isbn`, `gia_ban`, `so_luong_ton`, `so_luong_da_ban`, `muc_ton_toi_thieu`, `loaisp`, `mo_ta`, `hinh_anh`, `trang_thai`, `trang_thai_khoa`, `ngay_tao`) VALUES
(1, 1, 1, 'Mắt Biếc', '9786041071234', 110000, 47, 18, 5, 'Sach', 'Câu chuyện tình yêu tuổi học trò sâu lắng và day dứt của Ngạn và Hà Lan nơi làng Đo Đo mộc mạc.', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=600', 'DangBan', 'BinhThuong', '2026-10-16 15:04:53'),
(2, 1, 1, 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', '9786041054321', 125000, 33, 22, 5, 'Sach', 'Tấm vé trở về tuổi thơ nơi miền quê nghèo êm đềm với những ký ức ngọt ngào và tình anh em ruột thịt.', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=600', 'DangBan', 'BinhThuong', '2026-10-16 15:04:53'),
(3, 1, 5, 'Dế Mèn Phiêu Lưu Ký', '9786042087654', 65000, 79, 43, 5, 'Sach', 'Tác phẩm thiếu nhi kinh điển của nhà văn Tô Hoài về bài học đường đời đầu tiên và tình bạn cao đẹp.', 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=600', 'DangBan', 'BinhThuong', '2026-10-16 15:04:53'),
(4, 2, 2, 'Rừng Na Uy', '9786045612345', 135000, 38, 30, 5, 'Sach', 'Kiệt tác nổi tiếng nhất của Haruki Murakami về nỗi cô đơn, tình yêu và tuổi trẻ đầy trăn trở tại Tokyo.', 'https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=600', 'DangBan', 'BinhThuong', '2026-10-16 15:04:53'),
(5, 2, 3, 'Đắc Nhân Tâm', '9786045876543', 95000, 100, 65, 5, 'Sach', 'Tác phẩm kinh điển vượt thời gian về nghệ thuật thu phục lòng người và kỹ năng giao tiếp chân thành.', 'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=600', 'DangBan', 'BinhThuong', '2026-10-16 15:04:53'),
(6, 2, 4, 'Clean Code - Mã Sạch', '9780132350884', 250000, 19, 13, 5, 'Sach', 'Cẩm nang nâng cao chất lượng mã nguồn dành cho mọi kỹ sư phần mềm chuyên nghiệp hướng đến sự tinh gọn.', 'https://images.unsplash.com/photo-1515879218367-8466d910aaa4?w=600', 'DangBan', 'BinhThuong', '2026-10-16 15:04:53'),
(7, 2, 2, 'Hoàng Tử Bé', '9786045634567', 75000, 60, 38, 5, 'Sach', 'Một câu chuyện ngụ ngôn tuyệt mỹ dành cho cả trẻ em lẫn người lớn về tình yêu thương và sự gắn kết.', 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600', 'DangBan', 'BinhThuong', '2026-10-16 16:31:25'),
(8, 2, 2, 'Nhà Giả Kim', '9786046987654', 85000, 90, 85, 5, 'Sach', 'Hành trình theo đuổi vận mệnh và giấc mơ cá nhân của chàng trai chăn cừu Santiago qua sa mạc mênh mông.', 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=600', 'DangBan', 'BinhThuong', '2026-10-16 16:31:25'),
(9, 2, 3, 'Tâm Lý Học Về Tiền', '9786045987123', 145000, 45, 52, 5, 'Sach', 'Những bí mật về cách con người suy nghĩ, cảm nhận và đưa ra quyết định với tiền bạc trong đời sống thực tế.', 'https://images.unsplash.com/photo-1553729459-efe14ef6055d?w=600', 'DangBan', 'BinhThuong', '2026-10-16 16:31:25'),
(10, 1, 1, 'Cho Tôi Xin Một Vé Đi Tuổi Thơ', '9786041088899', 95000, 68, 36, 5, 'Sach', 'Tác phẩm đưa người đọc quay trở lại thế giới ngây ngô, tươi vui của tuổi ấu thơ với đầy ắp tiếng cười.', 'https://images.unsplash.com/photo-1476275466078-4007374efbbe?w=600', 'DangBan', 'BinhThuong', '2026-10-16 16:31:25'),
(11, 1, 5, 'Doraemon Tuyển Tập Tranh Truyện Màu', '9786042123456', 55000, 120, 95, 5, 'Sach', 'Tập truyện tranh màu đặc sắc kể về chú mèo máy thông minh đến từ thế kỷ 22 và nhóm bạn Nobita.', 'https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600', 'DangBan', 'BinhThuong', '2026-10-16 16:31:25'),
(12, 2, 4, 'Kiến Trúc Phần Mềm Thực Chiến', '9786048033221', 280000, 30, 19, 5, 'Sach', 'Hướng dẫn chi tiết xây dựng hệ thống phân tán, Microservices và thiết kế CSDL chịu tải cao.', 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600', 'DangBan', 'BinhThuong', '2026-10-16 16:31:25'),
(13, 2, 4, 'Sổ Tay Da Cao Cấp Nhã Nam Edition', 'VPP-9001', 89000, 49, 23, 5, 'VanPhongPham', 'Sổ tay bìa da PU cao cấp, giấy chống lóa 100gsm chuyên dụng ghi chép sáng tạo.', 'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=600', 'DangBan', 'BinhThuong', '2026-10-16 16:31:25'),
(14, 1, 5, 'Đèn Đọc Sách Mini Kẹp Bìa Sách LED', 'QT-8001', 65000, 37, 18, 5, 'QuaTang', 'Đèn LED kẹp sách 3 mức sáng bảo vệ mắt ban đêm, pin sạc USB tiện dụng.', 'https://images.unsplash.com/photo-1513542789411-b6a5d4f31634?w=600', 'DangBan', 'BinhThuong', '2026-10-16 16:31:25');

-- 8. san_pham_tac_gia
INSERT INTO `san_pham_tac_gia` (`masp`, `ma_tac_gia`, `thu_tu_tac_gia`) VALUES
(1, 1, 1),
(2, 1, 1),
(3, 2, 1),
(4, 4, 1),
(5, 3, 1),
(6, 5, 1),
(7, 6, 1),
(8, 7, 1),
(9, 8, 1),
(10, 1, 1),
(11, 2, 1),
(12, 5, 1);

-- 9. phieu_kiem_ke
INSERT INTO `phieu_kiem_ke` (`ma_phieu_kiem_ke`, `ma_shop`, `ngay_kiem_ke`, `ngay_phe_duyet`, `trang_thai`, `ghi_chu`) VALUES
(1, 1, '2026-10-01 00:00:00', NULL, 'DangKiemKe', 'Kiểm kê định kỳ đầu tháng 10 của Shop Kim Đồng');

-- 10. chi_tiet_kiem_ke
INSERT INTO `chi_tiet_kiem_ke` (`ma_phieu_kiem_ke`, `masp`, `so_luong_he_thong`, `so_luong_thuc_te`, `chenh_lech`, `ly_do`) VALUES
(1, 1, 50, 48, -2, 'Rách bìa 2 cuốn phải thanh lý'),
(1, 2, 35, 35, 0, 'Khớp số lượng');

-- 11. nha_van_chuyen
INSERT INTO `nha_van_chuyen` (`ma_nvc`, `ten_nvc`, `phi_co_ban`, `thoi_gian_du_kien`, `trang_thai`) VALUES
(1, 'Giao Hàng Tiết Kiệm (GHTK)', 22000, '1 - 2 ngày làm việc', 'HoatDong'),
(2, 'Giao Hàng Nhanh (GHN)', 25000, '2 - 3 ngày làm việc', 'HoatDong'),
(3, 'Viettel Post', 28000, '2 - 4 ngày làm việc', 'HoatDong');

-- 12. khuyen_mai
INSERT INTO `khuyen_mai` (`makm`, `ma_code`, `tenkm`, `mo_ta`, `loai_giam`, `gia_tri_giam`, `giam_toi_da`, `don_toi_thieu`, `so_luong_toi_da`, `so_luong_da_dung`, `pham_vi`, `ma_shop`, `ngay_bat_dau`, `ngay_ket_thuc`, `trang_thai`, `hien_thi_cong_khai`, `gioi_han_moi_khach_hang`, `doi_tuong_khach_hang`, `loai_khuyen_mai`) VALUES
(1, 'TOANSAN10', 'Giảm 10% Toàn Sàn', 'Áp dụng cho mọi đơn hàng từ 200.000đ trở lên', 'PhanTram', 10, 50000, 200000, NULL, 0, 'TOAN_SAN', NULL, '2025-01-01 00:00:00', '2027-12-31 00:00:00', 'HoatDong', 1, 1, 'ALL', 'GIAM_GIA_SAN_PHAM'),
(2, 'FREESHIP', 'Miễn Phí Vận Chuyển', 'Giảm 25.000đ phí giao hàng cho đơn từ 150.000đ', 'TienCoDinh', 25000, 25000, 150000, NULL, 0, 'TOAN_SAN', NULL, '2025-01-01 00:00:00', '2027-12-31 00:00:00', 'HoatDong', 1, 1, 'ALL', 'GIAM_GIA_SAN_PHAM'),
(3, 'KIMDONG20K', 'Ưu Đãi Tri Ân Kim Đồng', 'Giảm ngay 20.000đ cho đơn hàng truyện tranh Kim Đồng từ 100.000đ', 'TienCoDinh', 20000, 20000, 100000, NULL, 0, 'SHOP', 1, '2025-01-01 00:00:00', '2027-12-31 00:00:00', 'HoatDong', 1, 1, 'ALL', 'GIAM_GIA_SAN_PHAM'),
(4, 'NHANAM15', 'Đặc Quyền Hội Sách Nhã Nam', 'Giảm 15% tối đa 40.000đ cho các đầu sách văn học Nhã Nam', 'PhanTram', 15, 60000, 250000, NULL, 0, 'SHOP', 2, '2025-01-01 00:00:00', '2027-12-31 00:00:00', 'HoatDong', 1, 1, 'ALL', 'GIAM_GIA_SAN_PHAM');

-- 13. voucher_da_luu
INSERT INTO `voucher_da_luu` (`id`, `makh`, `makm`, `trang_thai`, `ngay_luu`, `ngay_su_dung`) VALUES
(1, 3, 1, 'ChuaDung', '2026-10-17 11:07:01', NULL),
(2, 3, 2, 'ChuaDung', '2026-10-17 11:07:03', NULL);

-- 14. gio_hang
INSERT INTO `gio_hang` (`ma_gio_hang`, `makh`, `ngay_tao`, `ngay_cap_nhat`) VALUES
(1, 1, '2026-10-16 22:27:33', '2026-10-16 22:30:03'),
(2, 3, '2026-10-17 11:06:51', '2026-10-17 11:55:04');

-- 15. don_hang
INSERT INTO `don_hang` (`madh`, `makh`, `ma_shop`, `ma_nvc`, `makm`, `ma_km_freeship`, `dia_chi_giao`, `so_dien_thoai_giao`, `tong_tien`, `phi_van_chuyen`, `tien_giam`, `tien_giam_freeship`, `chiet_khau_app_phan_tram`, `tien_phi_san`, `tien_thuc_nhan_shop`, `phuong_thuc_thanh_toan`, `trang_thai_thanh_toan`, `trang_thai`, `ma_van_don`, `ly_do_huy`, `ly_do_tu_choi`, `ngay_dat`, `ngay_xac_nhan`, `ngay_hoan_thanh`, `ly_do_tra_hang`, `phan_quyet_tranh_chap`, `nguoi_xu_ly_tranh_chap`) VALUES
(1, 1, 1, 1, 3, NULL, '123 Lê Lợi, P. Bến Nghé, Q.1, TP.HCM', '0988111222', 155000, 22000, 20000, 0, 5.00, 7750, 147250, 'COD', 'ChuaThanhToan', 'DaGiao', 'GHTK8829102', NULL, NULL, '2026-10-16 15:04:53', NULL, '2026-10-16 16:00:00', NULL, NULL, NULL),
(2, 2, 2, 2, 4, NULL, '456 Võ Văn Ngân, TP. Thủ Đức, TP.HCM', '0977333444', 350000, 25000, 40000, 0, 5.00, 17500, 332500, 'VIETQR', 'DaThanhToan', 'DaHuy', NULL, 'Khach doi y doi sach khac', NULL, '2026-10-16 15:04:53', NULL, NULL, NULL, NULL, NULL),
(5, 1, 1, 1, NULL, NULL, '1 Vo Van Ngan Thu Duc', '0987654321', 220000, 22000, 0, 0, 5.00, 11000, 209000, 'COD', 'ChuaThanhToan', 'DangGiao', 'VNPOST_HN_005', NULL, NULL, '2026-10-16 22:30:03', '2026-10-16 15:31:18', NULL, NULL, NULL, NULL),
(6, 3, 1, 1, NULL, NULL, '462, Linh trung, TP.thủ đức, Hồ Chí minh', '0934567822', 195000, 22000, 0, 0, 5.00, 9750, 185250, 'COD', 'DaThanhToan', 'HoanTat', 'GHTK-6-62810', NULL, NULL, '2026-10-17 11:08:49', '2026-10-17 11:09:22', '2026-10-17 11:19:28', NULL, NULL, NULL),
(7, 1, 1, 1, NULL, NULL, '1 Võ Văn Ngân, TP. Thủ Đức', '0912345678', 480000, 20000, 0, 0, 5.00, 24000, 456000, 'COD', 'DaThanhToan', 'HoanTat', NULL, NULL, NULL, '2026-08-15 10:00:00', NULL, '2026-08-18 14:00:00', NULL, NULL, NULL),
(8, 2, 2, 1, NULL, NULL, 'Ký túc xá khu B ĐHQG, Dĩ An', '0987654321', 620000, 25000, 0, 0, 5.00, 31000, 589000, 'VNPAY', 'DaThanhToan', 'HoanTat', NULL, NULL, NULL, '2026-09-10 11:30:00', NULL, '2026-09-13 16:00:00', NULL, NULL, NULL),
(9, 3, 2, 1, NULL, NULL, '462, Linh trung, TP.thủ đức, Hồ Chí minh', '0934567822', 89000, 22000, 0, 0, 5.00, 4450, 84550, 'COD', 'DaThanhToan', 'DaGiao', 'GHTK-9-65979', NULL, NULL, '2026-10-17 11:55:04', '2026-10-17 11:56:05', NULL, NULL, NULL, NULL);

-- 16. chi_tiet_don_hang
INSERT INTO `chi_tiet_don_hang` (`madh`, `masp`, `don_gia`, `so_luong`) VALUES
(1, 1, 110000, 1),
(1, 3, 65000, 1),
(2, 4, 135000, 1),
(2, 6, 250000, 1),
(5, 1, 110000, 2),
(6, 14, 65000, 3),
(7, 2, 125000, 2),
(7, 10, 95000, 2),
(8, 4, 135000, 2),
(8, 6, 250000, 1),
(9, 13, 89000, 1);

-- 17. thanh_toan
INSERT INTO `thanh_toan` (`ma_thanh_toan`, `madh`, `ma_giao_dich`, `phuong_thuc`, `so_tien`, `noi_dung`, `trang_thai`, `ngay_thanh_toan`) VALUES
(2, 5, NULL, 'COD', 220000, 'Thanh toán đơn hàng #5', 'ChoThanhToan', NULL),
(3, 6, NULL, 'COD', 195000, 'Thanh toán đơn hàng #6', 'ChoThanhToan', NULL),
(4, 9, NULL, 'COD', 89000, 'Thanh toán đơn hàng #9', 'ChoThanhToan', NULL);

-- 18. danh_gia
INSERT INTO `danh_gia` (`ma_danh_gia`, `madh`, `makh`, `masp`, `so_sao`, `noi_dung`, `hinh_anh_danh_gia`, `trang_thai`, `ngay_danh_gia`) VALUES
(1, 6, 3, 14, 5, 'Tốt lắm Tốt lắm Tốt lắm Tốt lắm Tốt lắm Tốt lắm Tốt', NULL, 'HienThi', '2026-10-17 11:49:02'),
(2, 9, 3, 13, 5, 'Đánh giá sản phẩm chất lượng cao, đóng gói cẩn thận', NULL, 'HienThi', '2026-10-17 11:56:18');

-- 19. san_pham_yeu_thich
INSERT INTO `san_pham_yeu_thich` (`makh`, `masp`, `ngay_thich`) VALUES
(3, 13, '2026-10-17 10:07:52');

-- 20. san_pham_da_xem
INSERT INTO `san_pham_da_xem` (`makh`, `masp`, `thoi_gian_xem`) VALUES
(3, 13, '2026-10-17 12:04:51'),
(3, 14, '2026-10-17 12:04:33');

-- 21. password_reset_token
INSERT INTO `password_reset_token` (`id`, `otp_code`, `token`, `ma_tai_khoan`, `expiry_date`, `used`, `ngay_tao`) VALUES
(1, '994162', '994162', 7, '2026-10-17 10:10:37', 1, '2026-10-17 10:05:37');

-- 22. cau_hinh_he_thong
INSERT INTO `cau_hinh_he_thong` (`ma_cau_hinh`, `gia_tri`, `mo_ta`) VALUES
('MIN_REVIEW_LENGTH', '50', 'Số ký tự tối thiểu của bài đánh giá'),
('PHI_SAN_MAC_DINH', '5.0', 'Tỷ lệ hoa hồng sàn mặc định (%)');

-- 23. nhat_ky_bao_mat
INSERT INTO `nhat_ky_bao_mat` (`ma_nhat_ky`, `ten_bang`, `hanh_dong`, `ban_ghi_id`, `du_lieu_cu`, `du_lieu_moi`, `nguoi_thuc_hien`, `thoi_gian`) VALUES
(1, 'tai_khoan', 'UPDATE', 2, 'VaiTro: NHANVIEN, TrangThai: HoatDong', 'VaiTro: MANAGER, TrangThai: HoatDong', 'admin', '2026-10-17 03:37:19');

SET FOREIGN_KEY_CHECKS = 1;

-- ====================================================================
-- THIẾT LẬP CSDL QL_NHASACH CHO MYSQL HOÀN TẤT
-- ====================================================================
