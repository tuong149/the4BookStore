-- =========================================================================
-- THE4BOOKSTORE - TỔNG HỢP CÁC INDEX TỐI ƯU HIỆU NĂNG DATABASE
-- =========================================================================
-- Chú thích:
-- 1. Các cột PRIMARY KEY và UNIQUE (như ISBN, ten_dang_nhap, email) đã được MySQL 
--    tự động tạo B-Tree Index mặc định.
-- 2. Dưới đây là các INDEX bổ sung cho các trường hay dùng trong WHERE, ORDER BY, JOIN.
-- 3. Sử dụng lệnh CREATE INDEX IF NOT EXISTS (MySQL 8.0+) hoặc chạy trực tiếp.
-- =========================================================================

-- 1. BẢNG SAN_PHAM (Sản phẩm - Tần suất truy vấn và lọc cao nhất)
-- Tối ưu lọc theo loại sản phẩm và trạng thái bán (vd: Sách đang bán)
CREATE INDEX idx_sp_loaisp_trangthai ON san_pham(loaisp, trang_thai);

-- Tối ưu lọc theo danh mục cụ thể và trạng thái bán
CREATE INDEX idx_sp_danhmuc_trangthai ON san_pham(ma_danh_muc, trang_thai);

-- Tối ưu tìm kiếm theo tên sản phẩm
CREATE INDEX idx_sp_tensp ON san_pham(tensp);

-- Tối ưu sắp xếp sản phẩm mới nhất (ORDER BY ngay_tao DESC)
CREATE INDEX idx_sp_ngaytao ON san_pham(ngay_tao);

-- Tối ưu sắp xếp theo giá bán tăng/giảm (ORDER BY gia_ban)
CREATE INDEX idx_sp_giaban ON san_pham(gia_ban);


-- 2. BẢNG DANH_MUC (Danh mục sản phẩm)
-- Tối ưu lọc các danh mục đang hoạt động (WHERE trang_thai = true)
CREATE INDEX idx_dm_trangthai ON danh_muc(trang_thai);


-- 3. BẢNG DON_HANG (Đơn hàng)
-- Tối ưu lọc đơn hàng theo trạng thái (ChoXuLy, DangGiao, HoanThanh...)
CREATE INDEX idx_dh_trangthai ON don_hang(trang_thai);

-- Tối ưu truy vấn lịch sử đơn hàng theo ngày đặt và thống kê doanh thu theo tháng
CREATE INDEX idx_dh_ngaydat ON don_hang(ngay_dat);

-- Tối ưu tìm kiếm đơn hàng của từng khách hàng
CREATE INDEX idx_dh_makh ON don_hang(makh);


-- 4. BẢNG TAI_KHOAN (Tài khoản người dùng)
-- Tối ưu lọc tài khoản theo vai trò (ADMIN, QUANLY, KHACHHANG...) và trạng thái
CREATE INDEX idx_tk_vaitro_trangthai ON tai_khoan(vai_tro, trang_thai);


-- 5. BẢNG PHIEU_NHAP & PHIEU_KIEM_KE (Kho hàng)
-- Tối ưu lọc và sắp xếp phiếu nhập theo ngày nhập
CREATE INDEX idx_pn_ngaynhap ON phieu_nhap(ngay_nhap);
CREATE INDEX idx_pn_trangthai ON phieu_nhap(trang_thai);

-- Tối ưu lọc và sắp xếp phiếu kiểm kê theo ngày tạo
CREATE INDEX idx_pkk_ngaykiemke ON phieu_kiem_ke(ngay_kiem_ke);
CREATE INDEX idx_pkk_trangthai ON phieu_kiem_ke(trang_thai);


-- =========================================================================
-- LỆNH KIỂM TRA CÁC INDEX HIỆN CÓ TRONG BẢNG:
-- SHOW INDEX FROM san_pham;
-- SHOW INDEX FROM danh_muc;
-- SHOW INDEX FROM don_hang;
-- =========================================================================
