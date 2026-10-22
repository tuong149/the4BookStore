-- ====================================================================
-- THE4BOOKSTORE - PROJECT HỆ QUẢN TRỊ CƠ SỞ DỮ LIỆU (DBMS330284)
-- PHẦN 4: QUẢN LÝ KHO, THANH TOÁN, ĐÁNH GIÁ SẢN PHẨM & BÁO CÁO DASHBOARD
-- SINH VIÊN PHỤ TRÁCH: BÙI QUANG HÒA (MSSV: 24110213)
-- HỆ QUẢN TRỊ CSDL: MICROSOFT SQL SERVER 2022/2025 (T-SQL)
-- ====================================================================

USE [QL_NhaSach];
GO

-- ====================================================================
-- 1. BẢNG DỮ LIỆU & RÀNG BUỘC (TABLES & CONSTRAINTS)
-- ====================================================================

-- Bảng 20: thanh_toan (Giao dịch tài chính thanh toán đơn hàng)
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[thanh_toan](
	[ma_thanh_toan] [int] IDENTITY(1,1) NOT NULL,
	[madh] [int] NOT NULL,
	[ma_giao_dich] [varchar](100) COLLATE Vietnamese_CI_AS NULL,
	[phuong_thuc] [nvarchar](30) COLLATE Vietnamese_CI_AS NOT NULL,
	[so_tien] [int] NOT NULL,
	[noi_dung] [nvarchar](255) COLLATE Vietnamese_CI_AS NULL,
	[trang_thai] [nvarchar](30) COLLATE Vietnamese_CI_AS NOT NULL,
	[ngay_thanh_toan] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[ma_thanh_toan] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[thanh_toan] ADD CONSTRAINT [DF_thanhtoan_trangthai] DEFAULT (N'ChoThanhToan') FOR [trang_thai];
GO
ALTER TABLE [dbo].[thanh_toan] WITH CHECK ADD CONSTRAINT [FK_thanh_toan_don_hang] FOREIGN KEY([madh])
REFERENCES [dbo].[don_hang] ([madh])
ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[thanh_toan] CHECK CONSTRAINT [FK_thanh_toan_don_hang];
GO

-- Bảng 21: danh_gia (Đánh giá nhận xét sản phẩm sau khi đơn hoàn tất)
CREATE TABLE [dbo].[danh_gia](
	[ma_danh_gia] [int] IDENTITY(1,1) NOT NULL,
	[madh] [int] NOT NULL,
	[makh] [int] NOT NULL,
	[masp] [int] NOT NULL,
	[so_sao] [int] NOT NULL,
	[noi_dung] [nvarchar](1000) COLLATE Vietnamese_CI_AS NULL,
	[hinh_anh_danh_gia] [nvarchar](500) COLLATE Vietnamese_CI_AS NULL,
	[trang_thai] [nvarchar](30) COLLATE Vietnamese_CI_AS NOT NULL,
	[ngay_danh_gia] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ma_danh_gia] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_danhgia_makh_masp_madh] UNIQUE NONCLUSTERED 
(
	[madh] ASC,
	[makh] ASC,
	[masp] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[danh_gia] ADD CONSTRAINT [DF_danhgia_trangthai] DEFAULT (N'HienThi') FOR [trang_thai];
GO
ALTER TABLE [dbo].[danh_gia] ADD CONSTRAINT [DF_danhgia_ngay] DEFAULT (sysutcdatetime()) FOR [ngay_danh_gia];
GO
ALTER TABLE [dbo].[danh_gia] WITH CHECK ADD CONSTRAINT [FK_danhgia_donhang] FOREIGN KEY([madh])
REFERENCES [dbo].[don_hang] ([madh]);
GO
ALTER TABLE [dbo].[danh_gia] CHECK CONSTRAINT [FK_danhgia_donhang];
GO
ALTER TABLE [dbo].[danh_gia] WITH CHECK ADD CONSTRAINT [FK_danhgia_khachhang] FOREIGN KEY([makh])
REFERENCES [dbo].[khach_hang] ([makh]);
GO
ALTER TABLE [dbo].[danh_gia] CHECK CONSTRAINT [FK_danhgia_khachhang];
GO
ALTER TABLE [dbo].[danh_gia] WITH CHECK ADD CONSTRAINT [FK_danhgia_sanpham] FOREIGN KEY([masp])
REFERENCES [dbo].[san_pham] ([masp]);
GO
ALTER TABLE [dbo].[danh_gia] CHECK CONSTRAINT [FK_danhgia_sanpham];
GO
ALTER TABLE [dbo].[danh_gia] WITH CHECK ADD CONSTRAINT [CK_danhgia_sosao] CHECK (([so_sao]>=(1) AND [so_sao]<=(5)));
GO
ALTER TABLE [dbo].[danh_gia] CHECK CONSTRAINT [CK_danhgia_sosao];
GO

-- Bảng 22: danh_gia_media (Hình ảnh/video đính kèm bài đánh giá)
CREATE TABLE [dbo].[danh_gia_media](
	[ma_media] [int] IDENTITY(1,1) NOT NULL,
	[ma_danh_gia] [int] NOT NULL,
	[url] [nvarchar](500) COLLATE Vietnamese_CI_AS NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ma_media] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[danh_gia_media] WITH CHECK ADD CONSTRAINT [FK_dgm_danh_gia] FOREIGN KEY([ma_danh_gia])
REFERENCES [dbo].[danh_gia] ([ma_danh_gia])
ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[danh_gia_media] CHECK CONSTRAINT [FK_dgm_danh_gia];
GO

-- Bảng 23: phieu_kiem_ke (Phiếu kiểm kê kho định kỳ của từng gian hàng Shop)
CREATE TABLE [dbo].[phieu_kiem_ke](
	[ma_phieu_kiem_ke] [int] IDENTITY(1,1) NOT NULL,
	[ma_shop] [int] NOT NULL,
	[ngay_kiem_ke] [datetime2](7) NOT NULL,
	[ngay_phe_duyet] [datetime2](7) NULL,
	[trang_thai] [nvarchar](30) COLLATE Vietnamese_CI_AS NOT NULL,
	[ghi_chu] [nvarchar](500) COLLATE Vietnamese_CI_AS NULL,
PRIMARY KEY CLUSTERED 
(
	[ma_phieu_kiem_ke] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[phieu_kiem_ke] ADD CONSTRAINT [DF_pkk_ngaykiemke] DEFAULT (sysutcdatetime()) FOR [ngay_kiem_ke];
GO
ALTER TABLE [dbo].[phieu_kiem_ke] ADD CONSTRAINT [DF_pkk_trangthai] DEFAULT (N'DangKiemKe') FOR [trang_thai];
GO
ALTER TABLE [dbo].[phieu_kiem_ke] WITH CHECK ADD CONSTRAINT [FK_pkk_shop] FOREIGN KEY([ma_shop])
REFERENCES [dbo].[shop] ([ma_shop]);
GO
ALTER TABLE [dbo].[phieu_kiem_ke] CHECK CONSTRAINT [FK_pkk_shop];
GO

-- Bảng 24: chi_tiet_kiem_ke (Chi tiết số lượng thực tế và chênh lệch từng đầu sách)
CREATE TABLE [dbo].[chi_tiet_kiem_ke](
	[ma_phieu_kiem_ke] [int] NOT NULL,
	[masp] [int] NOT NULL,
	[so_luong_he_thong] [int] NOT NULL,
	[so_luong_thuc_te] [int] NOT NULL,
	[chenh_lech] [int] NOT NULL,
	[ly_do] [nvarchar](255) COLLATE Vietnamese_CI_AS NULL,
PRIMARY KEY CLUSTERED 
(
	[ma_phieu_kiem_ke] ASC,
	[masp] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[chi_tiet_kiem_ke] WITH CHECK ADD CONSTRAINT [FK_ctkk_phieu] FOREIGN KEY([ma_phieu_kiem_ke])
REFERENCES [dbo].[phieu_kiem_ke] ([ma_phieu_kiem_ke])
ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[chi_tiet_kiem_ke] CHECK CONSTRAINT [FK_ctkk_phieu];
GO
ALTER TABLE [dbo].[chi_tiet_kiem_ke] WITH CHECK ADD CONSTRAINT [FK_ctkk_san_pham] FOREIGN KEY([masp])
REFERENCES [dbo].[san_pham] ([masp]);
GO
ALTER TABLE [dbo].[chi_tiet_kiem_ke] CHECK CONSTRAINT [FK_ctkk_san_pham];
GO
ALTER TABLE [dbo].[chi_tiet_kiem_ke] WITH CHECK ADD CONSTRAINT [CK_ctkk_soluong] CHECK (([so_luong_he_thong]>=(0) AND [so_luong_thuc_te]>=(0)));
GO
ALTER TABLE [dbo].[chi_tiet_kiem_ke] CHECK CONSTRAINT [CK_ctkk_soluong];
GO

-- Bảng 25: cau_hinh_he_thong (Cấu hình tỷ lệ hoa hồng sàn & tham số vận hành)
CREATE TABLE [dbo].[cau_hinh_he_thong](
	[ma_cau_hinh] [varchar](50) COLLATE Vietnamese_CI_AS NOT NULL,
	[gia_tri] [nvarchar](255) COLLATE Vietnamese_CI_AS NOT NULL,
	[mo_ta] [nvarchar](255) COLLATE Vietnamese_CI_AS NULL,
PRIMARY KEY CLUSTERED 
(
	[ma_cau_hinh] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

-- ====================================================================
-- 2. TỐI ƯU CHỈ MỤC & TRIGGERS KHO HÀNG / KINH TẾ
-- ====================================================================

CREATE NONCLUSTERED INDEX [idx_danhgia_masp] ON [dbo].[danh_gia]([masp] ASC, [so_sao] DESC);
GO

-- Trigger 1: Tự động cân bằng số lượng tồn kho theo số thực tế khi duyệt kiểm kê
CREATE OR ALTER TRIGGER trg_PhieuKiemKe_CanBangTonKho
ON phieu_kiem_ke
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    IF UPDATE(trang_thai)
    BEGIN
        IF EXISTS (
            SELECT 1 
            FROM inserted i 
            JOIN deleted d ON i.ma_phieu_kiem_ke = d.ma_phieu_kiem_ke 
            WHERE i.trang_thai = N'DaKiemKe' AND d.trang_thai <> N'DaKiemKe'
        )
        BEGIN
            UPDATE sp
            SET sp.so_luong_ton = ctkk.so_luong_thuc_te
            FROM san_pham sp
            JOIN chi_tiet_kiem_ke ctkk ON sp.masp = ctkk.masp
            JOIN inserted i ON ctkk.ma_phieu_kiem_ke = i.ma_phieu_kiem_ke
            JOIN deleted d ON i.ma_phieu_kiem_ke = d.ma_phieu_kiem_ke
            WHERE i.trang_thai = N'DaKiemKe' AND d.trang_thai <> N'DaKiemKe';
        END
    END
END;
GO

-- Trigger 2: Tự động tính tiền phí sàn và số tiền shop thực nhận khi chèn/cập nhật đơn hàng
CREATE OR ALTER TRIGGER trg_DonHang_TinhTienPhiSanVaShopNhan
ON don_hang
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    IF TRIGGER_NESTLEVEL() > 1 RETURN;

    UPDATE dh
    SET 
        dh.tien_phi_san = CAST(ROUND((dh.tong_tien * dh.chiet_khau_app_phan_tram) / 100.0, 0) AS INT),
        dh.tien_thuc_nhan_shop = dh.tong_tien - CAST(ROUND((dh.tong_tien * dh.chiet_khau_app_phan_tram) / 100.0, 0) AS INT)
    FROM don_hang dh
    JOIN inserted i ON dh.madh = i.madh;
END;
GO

-- ====================================================================
-- 3. HÀM TÍNH TOÁN KINH TẾ & BÁO CÁO (USER-DEFINED FUNCTIONS)
-- ====================================================================

-- Hàm 1: Tính điểm đánh giá trung bình của sản phẩm
CREATE OR ALTER FUNCTION dbo.fn_TinhDiemDanhGiaTrungBinh (@maSP INT)
RETURNS FLOAT
AS
BEGIN
    DECLARE @diemTB FLOAT;
    SELECT @diemTB = ROUND(AVG(CAST(so_sao AS FLOAT)), 1)
    FROM danh_gia
    WHERE masp = @maSP AND trang_thai = N'HienThi';
    RETURN ISNULL(@diemTB, 5.0);
END;
GO

-- Hàm 2: Đếm tổng số cuốn sách đã bán trên toàn hệ thống
CREATE OR ALTER FUNCTION dbo.fn_TongSachDaBan()
RETURNS BIGINT
AS
BEGIN
    DECLARE @v_tong BIGINT;
    SELECT @v_tong = COALESCE(SUM(CAST(ct.so_luong AS BIGINT)), 0)
    FROM chi_tiet_don_hang ct
    JOIN don_hang d ON ct.madh = d.madh
    WHERE d.trang_thai IN ('DaGiao', 'HoanTat');
    RETURN @v_tong;
END;
GO

-- Hàm 3: Tính tổng doanh thu toàn sàn theo khoảng thời gian
CREATE OR ALTER FUNCTION dbo.fn_TinhDoanhThu(
    @p_start_date DATETIME2,
    @p_end_date DATETIME2
)
RETURNS BIGINT
AS
BEGIN
    DECLARE @v_tong BIGINT;
    SELECT @v_tong = COALESCE(SUM(CAST(tong_tien AS BIGINT)), 0)
    FROM don_hang
    WHERE ngay_dat >= @p_start_date AND ngay_dat < @p_end_date
      AND trang_thai NOT IN ('DaHuy', 'TuChoi');
    RETURN @v_tong;
END;
GO

-- Hàm 4: Đếm tổng số đơn hàng hợp lệ theo khoảng thời gian
CREATE OR ALTER FUNCTION dbo.fn_DemDonHang(
    @p_start_date DATETIME2,
    @p_end_date DATETIME2
)
RETURNS BIGINT
AS
BEGIN
    DECLARE @v_so_luong BIGINT;
    SELECT @v_so_luong = COUNT(*)
    FROM don_hang
    WHERE ngay_dat >= @p_start_date AND ngay_dat < @p_end_date
      AND trang_thai NOT IN ('DaHuy', 'TuChoi');
    RETURN @v_so_luong;
END;
GO

-- Hàm 5: Tính phí sàn thu từ đơn hàng của gian hàng
CREATE OR ALTER FUNCTION dbo.fn_TinhPhiSan (@tongTien INT, @maShop INT)
RETURNS INT
AS
BEGIN
    DECLARE @chietKhau DECIMAL(5,2);
    SELECT @chietKhau = chiet_khau_phan_tram FROM shop WHERE ma_shop = @maShop;
    IF @chietKhau IS NULL SET @chietKhau = 5.00;
    RETURN CAST(ROUND((@tongTien * @chietKhau) / 100.0, 0) AS INT);
END;
GO

-- Hàm 6: Tính tổng số tiền đã giảm theo mã khuyến mãi
CREATE OR ALTER FUNCTION dbo.fn_TinhTienGiamKhuyenMai (@makm INT)
RETURNS BIGINT
AS
BEGIN
    DECLARE @tongTienGiam BIGINT;
    SELECT @tongTienGiam = COALESCE(SUM(CAST(tien_giam AS BIGINT)), 0)
    FROM don_hang
    WHERE makm = @makm AND trang_thai IN ('ChoXuLy', 'ChoXacNhan', 'DangGiao', 'DaGiao', 'HoanTat');
    RETURN @tongTienGiam;
END;
GO

-- Hàm 7: Tính tổng doanh thu đơn hàng áp dụng mã khuyến mãi
CREATE OR ALTER FUNCTION dbo.fn_TinhDoanhThuKhuyenMai (@makm INT)
RETURNS BIGINT
AS
BEGIN
    DECLARE @doanhThu BIGINT;
    SELECT @doanhThu = COALESCE(SUM(CAST(tong_tien AS BIGINT)), 0)
    FROM don_hang
    WHERE makm = @makm AND trang_thai IN ('ChoXuLy', 'ChoXacNhan', 'DangGiao', 'DaGiao', 'HoanTat');
    RETURN @doanhThu;
END;
GO

-- Hàm 8 (Inline Table-Valued): Lấy danh sách sách của một Shop kèm danh mục & điểm đánh giá
CREATE OR ALTER FUNCTION dbo.fn_LayDanhSachSachCuaShop (@ma_shop INT)
RETURNS TABLE
AS
RETURN (
    SELECT 
        sp.masp,
        sp.tensp,
        dm.ten_danh_muc,
        sp.gia_ban,
        sp.so_luong_ton,
        sp.so_luong_da_ban,
        sp.trang_thai,
        dbo.fn_TinhDiemDanhGiaTrungBinh(sp.masp) AS diem_danh_gia
    FROM san_pham sp
    JOIN danh_muc dm ON sp.ma_danh_muc = dm.ma_danh_muc
    WHERE sp.ma_shop = @ma_shop
);
GO

-- Hàm 9 (Multi-Statement Table-Valued): Báo cáo doanh số chi tiết theo khoảng thời gian
CREATE OR ALTER FUNCTION dbo.fn_BaoCaoDoanhThuTheoKhoangNgay (
    @tu_ngay DATETIME2,
    @den_ngay DATETIME2
)
RETURNS @KetQua TABLE (
    ma_shop INT,
    ten_shop NVARCHAR(150),
    so_don INT,
    tong_tien_ban INT,
    phi_san INT,
    shop_nhan INT
)
AS
BEGIN
    INSERT INTO @KetQua
    SELECT 
        s.ma_shop,
        s.ten_shop,
        COUNT(dh.madh) AS so_don,
        ISNULL(SUM(dh.tong_tien), 0) AS tong_tien_ban,
        ISNULL(SUM(dh.tien_phi_san), 0) AS phi_san,
        ISNULL(SUM(dh.tien_thuc_nhan_shop), 0) AS shop_nhan
    FROM shop s
    LEFT JOIN don_hang dh ON s.ma_shop = dh.ma_shop 
        AND dh.ngay_dat >= @tu_ngay 
        AND dh.ngay_dat <= @den_ngay 
        AND dh.trang_thai NOT IN (N'DaHuy', N'TuChoi')
    GROUP BY s.ma_shop, s.ten_shop;
    
    RETURN;
END;
GO

-- ====================================================================
-- 4. KHUNG NHÌN TỔNG HỢP (VIEWS BÁO CÁO & GIÁM SÁT)
-- ====================================================================

-- View 1: Thống kê đánh giá sản phẩm (lượt đánh giá, điểm sao TB, lượt 5 sao)
CREATE OR ALTER VIEW vw_ThongKeDanhGiaSanPham AS
SELECT 
    sp.masp,
    sp.tensp,
    s.ten_shop,
    COUNT(dg.ma_danh_gia) AS tong_luot_danh_gia,
    ROUND(AVG(CAST(dg.so_sao AS FLOAT)), 1) AS diem_sao_trung_binh,
    SUM(CASE WHEN dg.so_sao = 5 THEN 1 ELSE 0 END) AS so_luot_5_sao
FROM san_pham sp
JOIN shop s ON sp.ma_shop = s.ma_shop
LEFT JOIN danh_gia dg ON sp.masp = dg.masp AND dg.trang_thai = N'HienThi'
GROUP BY sp.masp, sp.tensp, s.ten_shop;
GO

-- View 2: Cảnh báo sách sắp hết hàng (tồn kho < 10) của từng Shop
CREATE OR ALTER VIEW vw_CanhBaoTonKhoShop AS
SELECT 
    s.ma_shop,
    s.ten_shop,
    sp.masp,
    sp.tensp,
    sp.so_luong_ton,
    sp.so_luong_da_ban,
    sp.gia_ban,
    CASE 
        WHEN sp.so_luong_ton = 0 THEN N'HẾT HÀNG'
        WHEN sp.so_luong_ton <= 5 THEN N'CỰC KỲ NGUY CẤP'
        ELSE N'SẮP HẾT HÀNG'
    END AS muc_do_canh_bao
FROM san_pham sp
JOIN shop s ON sp.ma_shop = s.ma_shop
WHERE sp.so_luong_ton < 10 AND sp.trang_thai = N'DangBan';
GO

-- View 3: Báo cáo tổng hợp số đơn, doanh thu và phí sàn theo từng tháng/năm
CREATE OR ALTER VIEW vw_BaoCaoDoanhThuTheoThang AS
SELECT 
    YEAR(ngay_dat) AS nam,
    MONTH(ngay_dat) AS thang,
    COUNT(*) AS tong_so_don,
    SUM(CAST(tong_tien AS BIGINT)) AS tong_doanh_thu,
    SUM(CAST(tien_phi_san AS BIGINT)) AS tong_phi_san_thu_ve,
    SUM(CAST(tien_thuc_nhan_shop AS BIGINT)) AS tong_tien_tra_shop
FROM don_hang
WHERE trang_thai IN ('DaGiao', 'HoanTat')
GROUP BY YEAR(ngay_dat), MONTH(ngay_dat);
GO

-- View 4: Báo cáo kết quả kinh doanh và hoa hồng của từng gian hàng Shop
CREATE OR ALTER VIEW vw_DoanhThuTungShop AS
SELECT 
    s.ma_shop,
    s.ten_shop,
    s.so_dien_thoai,
    s.chiet_khau_phan_tram,
    COUNT(dh.madh) AS tong_don_hang_thanh_cong,
    COALESCE(SUM(CAST(dh.tong_tien AS BIGINT)), 0) AS tong_doanh_thu_shop,
    COALESCE(SUM(CAST(dh.tien_phi_san AS BIGINT)), 0) AS hoa_hong_san_da_thu,
    COALESCE(SUM(CAST(dh.tien_thuc_nhan_shop AS BIGINT)), 0) AS tien_thuc_nhan_cua_shop
FROM shop s
LEFT JOIN don_hang dh ON s.ma_shop = dh.ma_shop AND dh.trang_thai IN ('DaGiao', 'HoanTat')
GROUP BY s.ma_shop, s.ten_shop, s.so_dien_thoai, s.chiet_khau_phan_tram;
GO

-- ====================================================================
-- 5. THỦ TỤC LƯU TRỮ (STORED PROCEDURES)
-- ====================================================================

-- Thủ tục 1: Gian hàng xác nhận hoàn tất phiếu kiểm kê (Transaction + Cập nhật tồn kho)
CREATE OR ALTER PROCEDURE sp_ShopXacNhanKiemKe
    @maPhieuKiemKe INT,
    @maShop INT
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;

        IF NOT EXISTS (
            SELECT 1 FROM phieu_kiem_ke 
            WHERE ma_phieu_kiem_ke = @maPhieuKiemKe AND ma_shop = @maShop AND trang_thai = N'DangKiemKe'
        )
        BEGIN
            RAISERROR (N'Phiếu kiểm kê không hợp lệ hoặc đã được xử lý trước đó!', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        UPDATE phieu_kiem_ke
        SET trang_thai = N'DaKiemKe', ngay_phe_duyet = SYSUTCDATETIME()
        WHERE ma_phieu_kiem_ke = @maPhieuKiemKe;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        DECLARE @ErrMsg NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR (@ErrMsg, 16, 1);
    END CATCH
END;
GO

-- Thủ tục 2: Thống kê doanh thu theo 12 tháng gần nhất toàn sàn
CREATE OR ALTER PROCEDURE dbo.sp_DoanhThuTheoThang
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        YEAR(ngay_dat) AS nam,
        MONTH(ngay_dat) AS thang,
        SUM(CAST(tong_tien AS BIGINT)) AS tongDoanhThu
    FROM don_hang
    WHERE trang_thai IN ('DaGiao', 'HoanTat')
    GROUP BY YEAR(ngay_dat), MONTH(ngay_dat)
    ORDER BY nam DESC, thang DESC;
END;
GO

-- Thủ tục 3: Thống kê top N sản phẩm bán chạy nhất hệ thống
CREATE OR ALTER PROCEDURE dbo.sp_TopSachBanChay
    @top_n INT = 10
AS
BEGIN
    SET NOCOUNT ON;

    SELECT TOP (@top_n)
        sp.masp,
        sp.tensp,
        SUM(ct.so_luong) AS soLuongBan,
        sp.gia_ban AS giaBan,
        SUM(CAST(ct.so_luong AS BIGINT) * ct.don_gia) AS doanhThuDongGop
    FROM chi_tiet_don_hang ct
    JOIN don_hang d ON ct.madh = d.madh
    JOIN san_pham sp ON ct.masp = sp.masp
    WHERE d.trang_thai IN ('DaGiao', 'HoanTat')
    GROUP BY sp.masp, sp.tensp, sp.gia_ban
    ORDER BY soLuongBan DESC;
END;
GO

-- Thủ tục 4: Doanh thu theo tháng của riêng từng gian hàng Shop
CREATE OR ALTER PROCEDURE dbo.sp_DoanhThuShopTheoThang
    @p_ma_shop INT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        YEAR(ngay_dat) AS nam,
        MONTH(ngay_dat) AS thang,
        COUNT(*) AS tongDonHang,
        SUM(CAST(tien_thuc_nhan_shop AS BIGINT)) AS tongDoanhThu
    FROM don_hang
    WHERE ma_shop = @p_ma_shop
      AND trang_thai IN ('DaGiao', 'HoanTat')
    GROUP BY YEAR(ngay_dat), MONTH(ngay_dat)
    ORDER BY nam DESC, thang DESC;
END;
GO

-- ====================================================================
-- 6. PHÂN QUYỀN TOÀN DIỆN TRÊN HỆ THỐNG (GRANT/DENY)
-- ====================================================================

GRANT SELECT, INSERT ON danh_gia TO Role_KhachHang;
GRANT SELECT, INSERT ON danh_gia_media TO Role_KhachHang;
GRANT SELECT ON vw_ThongKeDanhGiaSanPham TO Role_KhachHang;
GRANT EXECUTE ON fn_TinhDiemDanhGiaTrungBinh TO Role_KhachHang;

GRANT SELECT, INSERT, UPDATE, DELETE ON phieu_kiem_ke TO Role_Vendor;
GRANT SELECT, INSERT, UPDATE, DELETE ON chi_tiet_kiem_ke TO Role_Vendor;
GRANT SELECT ON vw_CanhBaoTonKhoShop TO Role_Vendor;
GRANT SELECT ON fn_LayDanhSachSachCuaShop TO Role_Vendor;
GRANT EXECUTE ON sp_ShopXacNhanKiemKe TO Role_Vendor;
GRANT EXECUTE ON sp_DoanhThuShopTheoThang TO Role_Vendor;

GRANT SELECT ON vw_BaoCaoDoanhThuTheoThang TO Role_Manager;
GRANT SELECT ON vw_DoanhThuTungShop TO Role_Manager;
GRANT SELECT ON vw_ThongKeDanhGiaSanPham TO Role_Manager;
GRANT SELECT ON cau_hinh_he_thong TO Role_Manager;
GRANT EXECUTE ON sp_DoanhThuTheoThang TO Role_Manager;
GRANT EXECUTE ON sp_TopSachBanChay TO Role_Manager;
GRANT EXECUTE ON fn_TongSachDaBan TO Role_Manager;
GRANT EXECUTE ON fn_TinhDoanhThu TO Role_Manager;
GRANT EXECUTE ON fn_DemDonHang TO Role_Manager;
GRANT EXECUTE ON fn_TinhPhiSan TO Role_Manager;
GRANT EXECUTE ON fn_TinhTienGiamKhuyenMai TO Role_Manager;
GRANT EXECUTE ON fn_TinhDoanhThuKhuyenMai TO Role_Manager;
GRANT SELECT ON fn_BaoCaoDoanhThuTheoKhoangNgay TO Role_Manager;

GRANT SELECT ON cau_hinh_he_thong TO Role_KhachHang;
GRANT SELECT ON cau_hinh_he_thong TO Role_Vendor;
GRANT SELECT, UPDATE ON cau_hinh_he_thong TO Role_Admin;
GO

-- ====================================================================
-- 7. DỮ LIỆU MẪU (SEED DATA: PAYMENTS, REVIEWS, INVENTORY & CONFIG)
-- ====================================================================

SET IDENTITY_INSERT [dbo].[thanh_toan] ON;
INSERT [dbo].[thanh_toan] ([ma_thanh_toan], [madh], [ma_giao_dich], [phuong_thuc], [so_tien], [noi_dung], [trang_thai], [ngay_thanh_toan]) 
VALUES 
(1, 1, N'COD_GHTK8829102', N'COD', 155000, N'Thanh toán khi nhận hàng đơn #1', N'DaThanhToan', CAST(N'2026-10-16T16:00:00' AS DateTime2)),
(2, 5, NULL, N'COD', 220000, N'Thanh toán đơn hàng #5', N'ChoThanhToan', NULL),
(3, 6, NULL, N'COD', 195000, N'Thanh toán đơn hàng #6', N'ChoThanhToan', NULL),
(4, 9, NULL, N'COD', 89000, N'Thanh toán đơn hàng #9', N'ChoThanhToan', NULL);
SET IDENTITY_INSERT [dbo].[thanh_toan] OFF;
GO

SET IDENTITY_INSERT [dbo].[danh_gia] ON;
INSERT [dbo].[danh_gia] ([ma_danh_gia], [madh], [makh], [masp], [so_sao], [noi_dung], [hinh_anh_danh_gia], [trang_thai], [ngay_danh_gia]) 
VALUES 
(1, 6, 3, 14, 5, N'Sách đẹp, đóng gói cẩn thận, giao hàng siêu nhanh!', NULL, N'HienThi', CAST(N'2026-10-17T11:49:02' AS DateTime2)),
(2, 9, 3, 13, 5, N'Sổ tay da Nhã Nam chất lượng rất tốt, bìa dày dặn!', NULL, N'HienThi', CAST(N'2026-10-17T11:56:18' AS DateTime2));
SET IDENTITY_INSERT [dbo].[danh_gia] OFF;
GO

INSERT [dbo].[cau_hinh_he_thong] ([ma_cau_hinh], [gia_tri], [mo_ta]) 
VALUES 
(N'MIN_REVIEW_LENGTH', N'50', N'Số ký tự tối thiểu của bài đánh giá'),
(N'PHI_SAN_MAC_DINH', N'5.0', N'Tỷ lệ hoa hồng sàn mặc định (%)');
GO

SET IDENTITY_INSERT [dbo].[phieu_kiem_ke] ON;
INSERT [dbo].[phieu_kiem_ke] ([ma_phieu_kiem_ke], [ma_shop], [ngay_kiem_ke], [ngay_phe_duyet], [trang_thai], [ghi_chu]) 
VALUES 
(1, 1, CAST(N'2026-10-10T08:00:00' AS DateTime2), CAST(N'2026-10-10T11:30:00' AS DateTime2), N'DaKiemKe', N'Kiểm kê định kỳ đầu tháng 10/2026 gian hàng Kim Đồng'),
(2, 2, CAST(N'2026-10-12T09:00:00' AS DateTime2), NULL, N'DangKiemKe', N'Kiểm kê đột xuất đầu sách văn học Nhã Nam');
SET IDENTITY_INSERT [dbo].[phieu_kiem_ke] OFF;
GO

INSERT [dbo].[chi_tiet_kiem_ke] ([ma_phieu_kiem_ke], [masp], [so_luong_he_thong], [so_luong_thuc_te], [chenh_lech], [ly_do]) 
VALUES 
(1, 1, 50, 50, 0, N'Khớp số lượng tồn hệ thống'),
(1, 2, 60, 60, 0, N'Khớp số lượng tồn hệ thống'),
(2, 4, 45, 43, -2, N'Rách hỏng trong quá trình lưu kho, chờ thanh lý'),
(2, 6, 30, 30, 0, N'Khớp số lượng tồn hệ thống');
GO

PRINT N'>>> HOÀN THÀNH PHẦN 4: QUẢN LÝ KHO, THANH TOÁN & BÁO CÁO THỐNG KÊ (BÙI QUANG HÒA)!';
GO
