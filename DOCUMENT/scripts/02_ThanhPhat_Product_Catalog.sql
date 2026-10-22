-- ====================================================================
-- THE4BOOKSTORE - PROJECT HỆ QUẢN TRỊ CƠ SỞ DỮ LIỆU (DBMS330284)
-- PHẦN 2: SẢN PHẨM, DANH MỤC, TÁC GIẢ, GIỎ HÀNG & TỐI ƯU CHỈ MỤC
-- SINH VIÊN PHỤ TRÁCH: VÕ THÀNH PHÁT (MSSV: 24110300)
-- HỆ QUẢN TRỊ CSDL: MICROSOFT SQL SERVER 2022/2025 (T-SQL)
-- ====================================================================

USE [QL_NhaSach];
GO

-- ====================================================================
-- 1. BẢNG DỮ LIỆU & RÀNG BUỘC (TABLES & CONSTRAINTS)
-- ====================================================================

-- Bảng 7: danh_muc (Phân loại danh mục sách và văn phòng phẩm)
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[danh_muc](
	[ma_danh_muc] [int] IDENTITY(1,1) NOT NULL,
	[ten_danh_muc] [nvarchar](100) COLLATE Vietnamese_CI_AS NOT NULL,
	[mo_ta] [nvarchar](500) COLLATE Vietnamese_CI_AS NULL,
	[trang_thai] [bit] NOT NULL,
	[loaisp] [nvarchar](30) COLLATE Vietnamese_CI_AS NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ma_danh_muc] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_danh_muc_ten] UNIQUE NONCLUSTERED 
(
	[ten_danh_muc] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[danh_muc] ADD CONSTRAINT [DF_danhmuc_trangthai] DEFAULT ((1)) FOR [trang_thai];
GO
ALTER TABLE [dbo].[danh_muc] ADD CONSTRAINT [DF_danhmuc_loaisp] DEFAULT (N'Sach') FOR [loaisp];
GO

-- Bảng 8: tac_gia (Thông tin tác giả viết sách)
CREATE TABLE [dbo].[tac_gia](
	[ma_tac_gia] [int] IDENTITY(1,1) NOT NULL,
	[ten_tac_gia] [nvarchar](150) COLLATE Vietnamese_CI_AS NOT NULL,
	[mo_ta] [nvarchar](max) COLLATE Vietnamese_CI_AS NULL,
PRIMARY KEY CLUSTERED 
(
	[ma_tac_gia] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

-- Bảng 9: san_pham (Sản phẩm: Sách, Văn phòng phẩm, Quà lưu niệm)
CREATE TABLE [dbo].[san_pham](
	[masp] [int] IDENTITY(1,1) NOT NULL,
	[ma_shop] [int] NOT NULL,
	[ma_danh_muc] [int] NOT NULL,
	[tensp] [nvarchar](200) COLLATE Vietnamese_CI_AS NOT NULL,
	[isbn] [varchar](20) COLLATE Vietnamese_CI_AS NULL,
	[gia_ban] [int] NOT NULL,
	[so_luong_ton] [int] NOT NULL,
	[so_luong_da_ban] [int] NOT NULL,
	[muc_ton_toi_thieu] [int] NOT NULL,
	[loaisp] [nvarchar](50) COLLATE Vietnamese_CI_AS NOT NULL,
	[mo_ta] [nvarchar](max) COLLATE Vietnamese_CI_AS NULL,
	[hinh_anh] [nvarchar](500) COLLATE Vietnamese_CI_AS NULL,
	[trang_thai] [nvarchar](30) COLLATE Vietnamese_CI_AS NOT NULL,
	[trang_thai_khoa] [nvarchar](30) COLLATE Vietnamese_CI_AS NOT NULL,
	[ngay_tao] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[masp] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
ALTER TABLE [dbo].[san_pham] ADD CONSTRAINT [DF_sanpham_tonkho] DEFAULT ((0)) FOR [so_luong_ton];
GO
ALTER TABLE [dbo].[san_pham] ADD CONSTRAINT [DF_sanpham_daban] DEFAULT ((0)) FOR [so_luong_da_ban];
GO
ALTER TABLE [dbo].[san_pham] ADD CONSTRAINT [DF_sanpham_tontoithieu] DEFAULT ((5)) FOR [muc_ton_toi_thieu];
GO
ALTER TABLE [dbo].[san_pham] ADD CONSTRAINT [DF_sanpham_loai] DEFAULT (N'SACH') FOR [loaisp];
GO
ALTER TABLE [dbo].[san_pham] ADD CONSTRAINT [DF_sanpham_trangthai] DEFAULT (N'DangBan') FOR [trang_thai];
GO
ALTER TABLE [dbo].[san_pham] ADD CONSTRAINT [DF_sanpham_khoa] DEFAULT (N'BinhThuong') FOR [trang_thai_khoa];
GO
ALTER TABLE [dbo].[san_pham] ADD CONSTRAINT [DF_sanpham_ngaytao] DEFAULT (sysutcdatetime()) FOR [ngay_tao];
GO
ALTER TABLE [dbo].[san_pham] WITH CHECK ADD CONSTRAINT [FK_sanpham_danhmuc] FOREIGN KEY([ma_danh_muc])
REFERENCES [dbo].[danh_muc] ([ma_danh_muc]);
GO
ALTER TABLE [dbo].[san_pham] CHECK CONSTRAINT [FK_sanpham_danhmuc];
GO
ALTER TABLE [dbo].[san_pham] WITH CHECK ADD CONSTRAINT [FK_sanpham_shop] FOREIGN KEY([ma_shop])
REFERENCES [dbo].[shop] ([ma_shop]);
GO
ALTER TABLE [dbo].[san_pham] CHECK CONSTRAINT [FK_sanpham_shop];
GO
ALTER TABLE [dbo].[san_pham] WITH CHECK ADD CONSTRAINT [CK_sanpham_giaban] CHECK (([gia_ban]>=(0)));
GO
ALTER TABLE [dbo].[san_pham] CHECK CONSTRAINT [CK_sanpham_giaban];
GO
ALTER TABLE [dbo].[san_pham] WITH CHECK ADD CONSTRAINT [CK_sanpham_tonkho] CHECK (([so_luong_ton]>=(0)));
GO
ALTER TABLE [dbo].[san_pham] CHECK CONSTRAINT [CK_sanpham_tonkho];
GO

-- Bảng 10: san_pham_tac_gia (Quan hệ Nhiều - Nhiều giữa Sách và Tác giả)
CREATE TABLE [dbo].[san_pham_tac_gia](
	[masp] [int] NOT NULL,
	[ma_tac_gia] [int] NOT NULL,
	[thu_tu_tac_gia] [int] NULL,
 CONSTRAINT [PK_san_pham_tac_gia] PRIMARY KEY CLUSTERED 
(
	[masp] ASC,
	[ma_tac_gia] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[san_pham_tac_gia] WITH CHECK ADD CONSTRAINT [FK_sptg_sanpham] FOREIGN KEY([masp])
REFERENCES [dbo].[san_pham] ([masp])
ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[san_pham_tac_gia] CHECK CONSTRAINT [FK_sptg_sanpham];
GO
ALTER TABLE [dbo].[san_pham_tac_gia] WITH CHECK ADD CONSTRAINT [FK_sptg_tacgia] FOREIGN KEY([ma_tac_gia])
REFERENCES [dbo].[tac_gia] ([ma_tac_gia]);
GO
ALTER TABLE [dbo].[san_pham_tac_gia] CHECK CONSTRAINT [FK_sptg_tacgia];
GO

-- Bảng 11: san_pham_yeu_thich (Danh sách wishlist của khách hàng)
CREATE TABLE [dbo].[san_pham_yeu_thich](
	[makh] [int] NOT NULL,
	[masp] [int] NOT NULL,
	[ngay_thich] [datetime2](7) NOT NULL,
 CONSTRAINT [PK_san_pham_yeu_thich] PRIMARY KEY CLUSTERED 
(
	[makh] ASC,
	[masp] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[san_pham_yeu_thich] ADD CONSTRAINT [DF_spyt_ngay] DEFAULT (sysutcdatetime()) FOR [ngay_thich];
GO
ALTER TABLE [dbo].[san_pham_yeu_thich] WITH CHECK ADD CONSTRAINT [FK_spyt_khachhang] FOREIGN KEY([makh])
REFERENCES [dbo].[khach_hang] ([makh])
ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[san_pham_yeu_thich] CHECK CONSTRAINT [FK_spyt_khachhang];
GO
ALTER TABLE [dbo].[san_pham_yeu_thich] WITH CHECK ADD CONSTRAINT [FK_spyt_sanpham] FOREIGN KEY([masp])
REFERENCES [dbo].[san_pham] ([masp])
ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[san_pham_yeu_thich] CHECK CONSTRAINT [FK_spyt_sanpham];
GO

-- Bảng 12: san_pham_da_xem (Lịch sử duyệt sản phẩm của khách hàng)
CREATE TABLE [dbo].[san_pham_da_xem](
	[makh] [int] NOT NULL,
	[masp] [int] NOT NULL,
	[thoi_gian_xem] [datetime2](7) NOT NULL,
 CONSTRAINT [PK_san_pham_da_xem] PRIMARY KEY CLUSTERED 
(
	[makh] ASC,
	[masp] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[san_pham_da_xem] ADD CONSTRAINT [DF_spdx_thoigian] DEFAULT (sysutcdatetime()) FOR [thoi_gian_xem];
GO
ALTER TABLE [dbo].[san_pham_da_xem] WITH CHECK ADD CONSTRAINT [FK_spdx_khachhang] FOREIGN KEY([makh])
REFERENCES [dbo].[khach_hang] ([makh])
ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[san_pham_da_xem] CHECK CONSTRAINT [FK_spdx_khachhang];
GO
ALTER TABLE [dbo].[san_pham_da_xem] WITH CHECK ADD CONSTRAINT [FK_spdx_sanpham] FOREIGN KEY([masp])
REFERENCES [dbo].[san_pham] ([masp])
ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[san_pham_da_xem] CHECK CONSTRAINT [FK_spdx_sanpham];
GO

-- Bảng 13: gio_hang (Giỏ hàng trực tuyến của mỗi khách hàng)
CREATE TABLE [dbo].[gio_hang](
	[ma_gio_hang] [int] IDENTITY(1,1) NOT NULL,
	[makh] [int] NOT NULL,
	[ngay_tao] [datetime2](7) NOT NULL,
	[ngay_cap_nhat] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ma_gio_hang] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_gio_hang_makh] UNIQUE NONCLUSTERED 
(
	[makh] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[gio_hang] ADD CONSTRAINT [DF_giohang_ngaytao] DEFAULT (sysutcdatetime()) FOR [ngay_tao];
GO
ALTER TABLE [dbo].[gio_hang] ADD CONSTRAINT [DF_giohang_ngaycapnhat] DEFAULT (sysutcdatetime()) FOR [ngay_cap_nhat];
GO
ALTER TABLE [dbo].[gio_hang] WITH CHECK ADD CONSTRAINT [FK_gio_hang_khach_hang] FOREIGN KEY([makh])
REFERENCES [dbo].[khach_hang] ([makh])
ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[gio_hang] CHECK CONSTRAINT [FK_gio_hang_khach_hang];
GO

-- Bảng 14: chi_tiet_gio_hang (Các mặt hàng trong giỏ)
CREATE TABLE [dbo].[chi_tiet_gio_hang](
	[ma_gio_hang] [int] NOT NULL,
	[masp] [int] NOT NULL,
	[so_luong] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ma_gio_hang] ASC,
	[masp] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[chi_tiet_gio_hang] ADD CONSTRAINT [DF_ctgh_soluong] DEFAULT ((1)) FOR [so_luong];
GO
ALTER TABLE [dbo].[chi_tiet_gio_hang] WITH CHECK ADD CONSTRAINT [FK_ctgh_giohang] FOREIGN KEY([ma_gio_hang])
REFERENCES [dbo].[gio_hang] ([ma_gio_hang])
ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[chi_tiet_gio_hang] CHECK CONSTRAINT [FK_ctgh_giohang];
GO
ALTER TABLE [dbo].[chi_tiet_gio_hang] WITH CHECK ADD CONSTRAINT [FK_ctgh_sanpham] FOREIGN KEY([masp])
REFERENCES [dbo].[san_pham] ([masp])
ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[chi_tiet_gio_hang] CHECK CONSTRAINT [FK_ctgh_sanpham];
GO
ALTER TABLE [dbo].[chi_tiet_gio_hang] WITH CHECK ADD CONSTRAINT [CK_ctgh_soluong] CHECK (([so_luong]>(0)));
GO
ALTER TABLE [dbo].[chi_tiet_gio_hang] CHECK CONSTRAINT [CK_ctgh_soluong];
GO

-- ====================================================================
-- 2. CHỈ MỤC TỐI ƯU HIỆU NĂNG (INDEXES)
-- ====================================================================

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_sanpham_danhmuc')
    CREATE NONCLUSTERED INDEX idx_sanpham_danhmuc ON san_pham(ma_danh_muc);
GO

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_sanpham_giaban')
    CREATE NONCLUSTERED INDEX idx_sanpham_giaban ON san_pham(gia_ban);
GO

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_sanpham_ngaytao')
    CREATE NONCLUSTERED INDEX idx_sanpham_ngaytao ON san_pham(ngay_tao DESC);
GO

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_sanpham_shop')
    CREATE NONCLUSTERED INDEX idx_sanpham_shop ON san_pham(ma_shop, trang_thai);
GO

-- ====================================================================
-- 3. HÀM NGƯỜI DÙNG ĐỊNH NGHĨA (USER-DEFINED FUNCTIONS)
-- ====================================================================

-- Hàm đếm tổng số đầu sách đang hoạt động của 1 Shop
CREATE OR ALTER FUNCTION dbo.fn_DemSachCuaShop(@p_ma_shop INT)
RETURNS INT
AS
BEGIN
    DECLARE @v_tong INT;
    SELECT @v_tong = COUNT(*)
    FROM san_pham
    WHERE ma_shop = @p_ma_shop AND trang_thai = N'DangBan' AND trang_thai_khoa = N'BinhThuong';
    RETURN ISNULL(@v_tong, 0);
END;
GO

-- ====================================================================
-- 4. THỦ TỤC LƯU TRỮ (STORED PROCEDURES - CÓ TRANSACTION & TRY...CATCH)
-- ====================================================================

-- Thủ tục 1: Tìm kiếm sách theo từ khóa, danh mục, khoảng giá và phân trang
CREATE OR ALTER PROCEDURE sp_TimKiemSachPhanTrang
    @TuKhoa NVARCHAR(200) = NULL,
    @MaDanhMuc INT = NULL,
    @GiaTu INT = NULL,
    @GiaDen INT = NULL,
    @TrangHienTai INT = 1,
    @KichThuocTrang INT = 12
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @BoQua INT = (@TrangHienTai - 1) * @KichThuocTrang;

    SELECT 
        sp.masp,
        sp.tensp,
        sp.gia_ban,
        sp.so_luong_ton,
        sp.so_luong_da_ban,
        sp.hinh_anh,
        sp.loaisp,
        dm.ten_danh_muc,
        s.ten_shop,
        ISNULL(AVG(CAST(dg.diem_danh_gia AS FLOAT)), 5.0) AS DiemDanhGiaTrungBinh,
        COUNT(dg.ma_danh_gia) AS TongSoDanhGia
    FROM san_pham sp
    JOIN danh_muc dm ON sp.ma_danh_muc = dm.ma_danh_muc
    JOIN shop s ON sp.ma_shop = s.ma_shop
    LEFT JOIN danh_gia dg ON sp.masp = dg.masp
    WHERE sp.trang_thai = N'DangBan'
      AND sp.trang_thai_khoa = N'BinhThuong'
      AND (@TuKhoa IS NULL OR sp.tensp LIKE N'%' + @TuKhoa + N'%' OR sp.isbn LIKE '%' + @TuKhoa + '%')
      AND (@MaDanhMuc IS NULL OR sp.ma_danh_muc = @MaDanhMuc)
      AND (@GiaTu IS NULL OR sp.gia_ban >= @GiaTu)
      AND (@GiaDen IS NULL OR sp.gia_ban <= @GiaDen)
    GROUP BY 
        sp.masp, sp.tensp, sp.gia_ban, sp.so_luong_ton, sp.so_luong_da_ban,
        sp.hinh_anh, sp.loaisp, dm.ten_danh_muc, s.ten_shop
    ORDER BY sp.so_luong_da_ban DESC, sp.masp DESC
    OFFSET @BoQua ROWS FETCH NEXT @KichThuocTrang ROWS ONLY;
END;
GO

-- Thủ tục 2: Thêm hoặc Cập nhật Sản phẩm vào Giỏ Hàng (Transaction ACID)
CREATE OR ALTER PROCEDURE sp_ThemSanPhamVaoGioHang
    @makh INT,
    @masp INT,
    @so_luong INT = 1
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;

        IF @so_luong <= 0
        BEGIN
            RAISERROR (N'Số lượng thêm vào giỏ hàng phải lớn hơn 0!', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        DECLARE @ton_kho INT;
        DECLARE @trang_thai NVARCHAR(30);
        DECLARE @khoa NVARCHAR(30);

        SELECT @ton_kho = so_luong_ton, @trang_thai = trang_thai, @khoa = trang_thai_khoa
        FROM san_pham WITH (UPDLOCK)
        WHERE masp = @masp;

        IF @ton_kho IS NULL OR @trang_thai <> N'DangBan' OR @khoa <> N'BinhThuong'
        BEGIN
            RAISERROR (N'Sản phẩm hiện không mở bán hoặc đã bị khóa!', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        DECLARE @ma_gio_hang INT;
        SELECT @ma_gio_hang = ma_gio_hang FROM gio_hang WHERE makh = @makh;

        IF @ma_gio_hang IS NULL
        BEGIN
            INSERT INTO gio_hang (makh, ngay_tao, ngay_cap_nhat)
            VALUES (@makh, SYSUTCDATETIME(), SYSUTCDATETIME());
            SET @ma_gio_hang = SCOPE_IDENTITY();
        END

        DECLARE @so_luong_hien_tai INT = 0;
        SELECT @so_luong_hien_tai = so_luong 
        FROM chi_tiet_gio_hang 
        WHERE ma_gio_hang = @ma_gio_hang AND masp = @masp;

        IF (@so_luong_hien_tai + @so_luong) > @ton_kho
        BEGIN
            RAISERROR (N'Số lượng yêu cầu vượt quá số lượng hàng tồn kho có sẵn!', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        IF @so_luong_hien_tai > 0
        BEGIN
            UPDATE chi_tiet_gio_hang
            SET so_luong = so_luong + @so_luong
            WHERE ma_gio_hang = @ma_gio_hang AND masp = @masp;
        END
        ELSE
        BEGIN
            INSERT INTO chi_tiet_gio_hang (ma_gio_hang, masp, so_luong)
            VALUES (@ma_gio_hang, @masp, @so_luong);
        END

        UPDATE gio_hang
        SET ngay_cap_nhat = SYSUTCDATETIME()
        WHERE ma_gio_hang = @ma_gio_hang;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        DECLARE @ErrMsg NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR (@ErrMsg, 16, 1);
    END CATCH
END;
GO

-- Thủ tục 3: Dọn dẹp nhật ký sản phẩm đã xem quá hạn
CREATE OR ALTER PROCEDURE sp_DonDepSanPhamDaXem
    @SoGioToiDa INT = 1
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @ThoiDiemHetHan DATETIME2 = DATEADD(HOUR, -@SoGioToiDa, GETDATE());
    DELETE FROM san_pham_da_xem WHERE thoi_gian_xem < @ThoiDiemHetHan;
    SELECT @@ROWCOUNT AS SoBanGhiDaDonDep;
END;
GO

-- ====================================================================
-- 5. KHUNG NHÌN (VIEWS)
-- ====================================================================

-- View 1: Top sách bán chạy nhất toàn sàn
CREATE OR ALTER VIEW vw_TopSachBanChayToanSan AS
SELECT TOP 20
    sp.masp,
    sp.tensp,
    sp.gia_ban,
    sp.so_luong_da_ban,
    sp.so_luong_ton,
    sp.hinh_anh,
    dm.ten_danh_muc,
    s.ten_shop
FROM san_pham sp
JOIN danh_muc dm ON sp.ma_danh_muc = dm.ma_danh_muc
JOIN shop s ON sp.ma_shop = s.ma_shop
WHERE sp.trang_thai = N'DangBan' AND sp.trang_thai_khoa = N'BinhThuong'
ORDER BY sp.so_luong_da_ban DESC;
GO

-- View 2: Xem chi tiết các mặt hàng trong giỏ của khách hàng
CREATE OR ALTER VIEW vw_ChiTietGioHangKhachHang AS
SELECT 
    gh.makh,
    gh.ma_gio_hang,
    ct.masp,
    sp.tensp,
    sp.gia_ban,
    ct.so_luong,
    (sp.gia_ban * ct.so_luong) AS thanh_tien,
    sp.hinh_anh,
    sp.so_luong_ton,
    sp.trang_thai,
    sp.ma_shop,
    s.ten_shop
FROM gio_hang gh
JOIN chi_tiet_gio_hang ct ON gh.ma_gio_hang = ct.ma_gio_hang
JOIN san_pham sp ON ct.masp = sp.masp
JOIN shop s ON sp.ma_shop = s.ma_shop;
GO

-- ====================================================================
-- 6. PHÂN QUYỀN TRUY CẬP (RBAC - ROLE-BASED ACCESS CONTROL)
-- ====================================================================

GRANT SELECT ON san_pham TO Role_KhachHang;
GRANT SELECT ON danh_muc TO Role_KhachHang;
GRANT SELECT ON tac_gia TO Role_KhachHang;
GRANT SELECT ON san_pham_tac_gia TO Role_KhachHang;
GRANT SELECT, INSERT, UPDATE, DELETE ON san_pham_yeu_thich TO Role_KhachHang;
GRANT SELECT, INSERT, UPDATE, DELETE ON san_pham_da_xem TO Role_KhachHang;
GRANT SELECT, INSERT, UPDATE, DELETE ON gio_hang TO Role_KhachHang;
GRANT SELECT, INSERT, UPDATE, DELETE ON chi_tiet_gio_hang TO Role_KhachHang;
GRANT SELECT ON vw_TopSachBanChayToanSan TO Role_KhachHang;
GRANT SELECT ON vw_ChiTietGioHangKhachHang TO Role_KhachHang;
GRANT EXECUTE ON sp_TimKiemSachPhanTrang TO Role_KhachHang;
GRANT EXECUTE ON sp_ThemSanPhamVaoGioHang TO Role_KhachHang;

GRANT SELECT, INSERT, UPDATE, DELETE ON san_pham TO Role_Vendor;
GRANT SELECT, INSERT, UPDATE, DELETE ON san_pham_tac_gia TO Role_Vendor;
GRANT SELECT ON danh_muc TO Role_Vendor;
GRANT SELECT, INSERT ON tac_gia TO Role_Vendor;
GRANT EXECUTE ON fn_DemSachCuaShop TO Role_Vendor;

GRANT SELECT, INSERT, UPDATE, DELETE ON danh_muc TO Role_Manager;
GRANT SELECT, INSERT, UPDATE, DELETE ON tac_gia TO Role_Manager;
GRANT SELECT ON vw_TopSachBanChayToanSan TO Role_Manager;

DENY INSERT, UPDATE, DELETE ON san_pham TO Role_KhachHang;
GO

-- ====================================================================
-- 7. DỮ LIỆU MẪU (SEED DATA: CATEGORIES, AUTHORS, 14 PRODUCTS & CARTS)
-- ====================================================================

SET IDENTITY_INSERT [dbo].[danh_muc] ON;
INSERT [dbo].[danh_muc] ([ma_danh_muc], [ten_danh_muc], [mo_ta], [trang_thai], [loaisp]) VALUES 
(1, N'Văn Học Trong Nước', N'Tiểu thuyết, truyện ngắn, phóng sự và tác phẩm văn học Việt Nam', 1, N'Sach'),
(2, N'Văn Học Nước Ngoài', N'Tác phẩm dịch kinh điển, văn học thế giới đạt giải Nobel, Booker', 1, N'Sach'),
(3, N'Kinh Tế - Khởi Nghiệp', N'Quản trị kinh doanh, đầu tư, tài chính và khởi nghiệp đổi mới', 1, N'Sach'),
(4, N'Công Nghệ Thông Tin', N'Lập trình, kiến trúc phần mềm, khoa học dữ liệu và an toàn thông tin', 1, N'Sach'),
(5, N'Thiếu Nhi & Truyện Tranh', N'Manga, comic, truyện thiếu nhi và văn học giáo dục sớm', 1, N'Sach'),
(6, N'Tâm Lý & Kỹ Năng Sống', N'Sách phát triển bản thân, tư duy tích cực và thấu hiểu nội tâm', 1, N'Sach'),
(7, N'Sổ Tay & Vở Viết', N'Sổ tay bìa da, sổ dot grid, sổ còng và vở ghi chép cao cấp', 1, N'VanPhongPham'),
(8, N'Bút & Dụng Cụ Viết', N'Bút máy, bút gel, bút ký và mực viết thư pháp chính hãng', 1, N'VanPhongPham'),
(9, N'Dụng Cụ Học Tập & Văn Phòng', N'Bookmark kim loại, thước, kẹp tài liệu và dụng cụ văn phòng', 1, N'VanPhongPham'),
(10, N'Đèn Đọc Sách & Phụ Kiện Sách', N'Đèn LED kẹp sách bảo vệ mắt, giá đỡ sách thông minh và phụ kiện', 1, N'QuaTang'),
(11, N'Mô Hình Book Nook & Trang Trí', N'Mô hình gỗ 3D Book Nook lắp ráp trang trí giá sách tinh tế', 1, N'QuaTang'),
(12, N'Túi Tote & Thời Trang Sách', N'Túi vải canvas thời trang, áo thun và phụ kiện phong cách mọt sách', 1, N'QuaTang'),
(13, N'Boardgame & Hộp Quà Tặng', N'Trò chơi boardgame trí tuệ, set quà tặng độc đáo cho người yêu sách', 1, N'QuaTang');
SET IDENTITY_INSERT [dbo].[danh_muc] OFF;
GO

SET IDENTITY_INSERT [dbo].[tac_gia] ON;
INSERT [dbo].[tac_gia] ([ma_tac_gia], [ten_tac_gia], [mo_ta]) VALUES 
(1, N'Nguyễn Nhật Ánh', N'Nhà văn chuyên viết cho thanh thiếu niên được mến mộ bậc nhất Việt Nam'),
(2, N'Tô Hoài', N'Nhà văn đại thụ của nền văn học thiếu nhi Việt Nam'),
(3, N'Dale Carnegie', N'Tác giả bậc thầy người Mỹ về nghệ thuật giao tiếp và đối nhân xử thế'),
(4, N'Haruki Murakami', N'Nhà văn đương đại lừng danh người Nhật Bản'),
(5, N'Robert C. Martin', N'Huyền thoại Uncle Bob - tác giả của Clean Code và kiến trúc phần mềm'),
(6, N'Antoine de Saint-Exupéry', N'Nhà văn và phi công quả cảm người Pháp, tác giả Hoàng Tử Bé'),
(7, N'Paulo Coelho', N'Nhà văn nổi tiếng thế giới người Brazil, tác giả Nhà Giả Kim'),
(8, N'Morgan Housel', N'Chuyên gia tài chính và tác giả bán chạy toàn cầu về tâm lý học tiền bạc');
SET IDENTITY_INSERT [dbo].[tac_gia] OFF;
GO

SET IDENTITY_INSERT [dbo].[san_pham] ON;
INSERT [dbo].[san_pham] ([masp], [ma_shop], [ma_danh_muc], [tensp], [isbn], [gia_ban], [so_luong_ton], [so_luong_da_ban], [muc_ton_toi_thieu], [loaisp], [mo_ta], [hinh_anh], [trang_thai], [trang_thai_khoa], [ngay_tao]) VALUES 
(1, 1, 1, N'Mắt Biếc', N'9786041071234', 110000, 47, 18, 5, N'Sach', N'Câu chuyện tình yêu tuổi học trò sâu lắng và day dứt của Ngạn và Hà Lan nơi làng Đo Đo mộc mạc.', N'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=600', N'DangBan', N'BinhThuong', CAST(N'2026-10-16T15:04:53.1483971' AS DateTime2)),
(2, 1, 1, N'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', N'9786041054321', 125000, 33, 22, 5, N'Sach', N'Tấm vé trở về tuổi thơ nơi miền quê nghèo êm đềm với những ký ức ngọt ngào và tình anh em ruột thịt.', N'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=600', N'DangBan', N'BinhThuong', CAST(N'2026-10-16T15:04:53.1483971' AS DateTime2)),
(3, 1, 5, N'Dế Mèn Phiêu Lưu Ký', N'9786042087654', 65000, 79, 43, 5, N'Sach', N'Tác phẩm thiếu nhi kinh điển của nhà văn Tô Hoài về bài học đường đời đầu tiên và tình bạn cao đẹp.', N'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=600', N'DangBan', N'BinhThuong', CAST(N'2026-10-16T15:04:53.1483971' AS DateTime2)),
(4, 2, 2, N'Rừng Na Uy', N'9786045612345', 135000, 38, 30, 5, N'Sach', N'Kiệt tác nổi tiếng nhất của Haruki Murakami về nỗi cô đơn, tình yêu và tuổi trẻ đầy trăn trở tại Tokyo.', N'https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=600', N'DangBan', N'BinhThuong', CAST(N'2026-10-16T15:04:53.1483971' AS DateTime2)),
(5, 2, 6, N'Đắc Nhân Tâm', N'9786045876543', 95000, 100, 65, 5, N'Sach', N'Tác phẩm kinh điển vượt thời gian về nghệ thuật thu phục lòng người và kỹ năng giao tiếp chân thành.', N'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=600', N'DangBan', N'BinhThuong', CAST(N'2026-10-16T15:04:53.1483971' AS DateTime2)),
(6, 2, 4, N'Clean Code - Mã Sạch', N'9780132350884', 250000, 19, 13, 5, N'Sach', N'Cẩm nang nâng cao chất lượng mã nguồn dành cho mọi kỹ sư phần mềm chuyên nghiệp hướng đến sự tinh gọn.', N'https://images.unsplash.com/photo-1515879218367-8466d910aaa4?w=600', N'DangBan', N'BinhThuong', CAST(N'2026-10-16T15:04:53.1483971' AS DateTime2)),
(7, 2, 2, N'Hoàng Tử Bé', N'9786045634567', 75000, 60, 38, 5, N'Sach', N'Một câu chuyện ngụ ngôn tuyệt mỹ dành cho cả trẻ em lẫn người lớn về tình yêu thương và sự gắn kết.', N'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600', N'DangBan', N'BinhThuong', CAST(N'2026-10-16T16:31:25.1719499' AS DateTime2)),
(8, 2, 2, N'Nhà Giả Kim', N'9786046987654', 85000, 90, 85, 5, N'Sach', N'Hành trình theo đuổi vận mệnh và giấc mơ cá nhân của chàng trai chăn cừu Santiago qua sa mạc mênh mông.', N'https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=600', N'DangBan', N'BinhThuong', CAST(N'2026-10-16T16:31:25.1739494' AS DateTime2)),
(9, 2, 3, N'Tâm Lý Học Về Tiền', N'9786045987123', 145000, 45, 52, 5, N'Sach', N'Những bí mật về cách con người suy nghĩ, cảm nhận và đưa ra quyết định với tiền bạc trong đời sống thực tế.', N'https://images.unsplash.com/photo-1553729459-efe14ef6055d?w=600', N'DangBan', N'BinhThuong', CAST(N'2026-10-16T16:31:25.1739494' AS DateTime2)),
(10, 1, 1, N'Cho Tôi Xin Một Vé Đi Tuổi Thơ', N'9786041088899', 95000, 68, 36, 5, N'Sach', N'Tác phẩm đưa người đọc quay trở lại thế giới ngây ngô, tươi vui của tuổi ấu thơ với đầy ắp tiếng cười.', N'https://images.unsplash.com/photo-1476275466078-4007374efbbe?w=600', N'DangBan', N'BinhThuong', CAST(N'2026-10-16T16:31:25.1749512' AS DateTime2)),
(11, 1, 5, N'Doraemon Tuyển Tập Tranh Truyện Màu', N'9786042123456', 55000, 120, 95, 5, N'Sach', N'Tập truyện tranh màu đặc sắc kể về chú mèo máy thông minh đến từ thế kỷ 22 và nhóm bạn Nobita.', N'https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600', N'DangBan', N'BinhThuong', CAST(N'2026-10-16T16:31:25.1749512' AS DateTime2)),
(12, 2, 4, N'Kiến Trúc Phần Mềm Thực Chiến', N'9786048033221', 280000, 30, 19, 5, N'Sach', N'Hướng dẫn chi tiết xây dựng hệ thống phân tán, Microservices và thiết kế CSDL chịu tải cao.', N'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600', N'DangBan', N'BinhThuong', CAST(N'2026-10-16T16:31:25.1759510' AS DateTime2)),
(13, 2, 7, N'Sổ Tay Da Cao Cấp Nhã Nam Edition', N'VPP-9001', 89000, 49, 23, 5, N'VanPhongPham', N'Sổ tay bìa da PU cao cấp, giấy chống lóa 100gsm chuyên dụng ghi chép sáng tạo.', N'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=600', N'DangBan', N'BinhThuong', CAST(N'2026-10-16T16:31:25.1769497' AS DateTime2)),
(14, 1, 10, N'Đèn Đọc Sách Mini Kẹp Bìa Sách LED', N'QT-8001', 65000, 37, 18, 5, N'QuaTang', N'Đèn LED kẹp sách 3 mức sáng bảo vệ mắt ban đêm, pin sạc USB tiện dụng.', N'https://images.unsplash.com/photo-1513542789411-b6a5d4f31634?w=600', N'DangBan', N'BinhThuong', CAST(N'2026-10-16T16:31:25.1769497' AS DateTime2));
SET IDENTITY_INSERT [dbo].[san_pham] OFF;
GO

INSERT [dbo].[san_pham_tac_gia] ([masp], [ma_tac_gia], [thu_tu_tac_gia]) VALUES 
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
GO

INSERT [dbo].[san_pham_yeu_thich] ([makh], [masp], [ngay_thich]) VALUES 
(1, 1, CAST(N'2026-10-16T15:04:53.1513994' AS DateTime2)),
(3, 4, CAST(N'2026-10-17T10:06:00.0000000' AS DateTime2));
GO

INSERT [dbo].[san_pham_da_xem] ([makh], [masp], [thoi_gian_xem]) VALUES 
(3, 1, CAST(N'2026-10-17T10:06:00.0000000' AS DateTime2)),
(3, 4, CAST(N'2026-10-17T10:07:00.0000000' AS DateTime2)),
(3, 13, CAST(N'2026-10-17T11:53:00.0000000' AS DateTime2));
GO

SET IDENTITY_INSERT [dbo].[gio_hang] ON;
INSERT [dbo].[gio_hang] ([ma_gio_hang], [makh], [ngay_tao], [ngay_cap_nhat]) VALUES 
(1, 1, CAST(N'2026-10-16T15:04:53.1523992' AS DateTime2), CAST(N'2026-10-16T15:04:53.1523992' AS DateTime2)),
(2, 2, CAST(N'2026-10-16T15:04:53.1523992' AS DateTime2), CAST(N'2026-10-16T15:04:53.1523992' AS DateTime2)),
(3, 3, CAST(N'2026-10-17T10:05:28.1884481' AS DateTime2), CAST(N'2026-10-17T11:00:00.0000000' AS DateTime2));
SET IDENTITY_INSERT [dbo].[gio_hang] OFF;
GO

INSERT [dbo].[chi_tiet_gio_hang] ([ma_gio_hang], [masp], [so_luong]) VALUES 
(1, 2, 1),
(2, 3, 2),
(3, 1, 1),
(3, 4, 1);
GO

PRINT N'>>> HOÀN THÀNH PHẦN 2: SẢN PHẨM, DANH MỤC & GIỎ HÀNG (VÕ THÀNH PHÁT)!';
GO
