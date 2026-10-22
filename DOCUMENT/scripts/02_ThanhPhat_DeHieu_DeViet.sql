-- ====================================================================
-- THE4BOOKSTORE - PROJECT HỆ QUẢN TRỊ CƠ SỞ DỮ LIỆU (DBMS330284)
-- BẢN DỄ VIẾT - DỄ THUỘC KHI THI VẤN ĐÁP / VIẾT TAY TRÊN GIẤY
-- SINH VIÊN: VÕ THÀNH PHÁT (MSSV: 24110300)
-- MODULE: SẢN PHẨM, DANH MỤC, TÁC GIẢ, GIỎ HÀNG & CHỈ MỤC
-- ====================================================================

USE [QL_NhaSach];
GO

-- --------------------------------------------------------------------
-- 1. BẢNG DỮ LIỆU CỐT LÕI (8 BẢNG ĐẠT CHUẨN 3NF)
-- --------------------------------------------------------------------

-- 1.1. Danh mục sản phẩm
CREATE TABLE [dbo].[danh_muc](
    [ma_danh_muc] INT IDENTITY(1,1) PRIMARY KEY,
    [ten_danh_muc] NVARCHAR(100) NOT NULL UNIQUE,
    [mo_ta] NVARCHAR(500) NULL,
    [trang_thai] BIT NOT NULL DEFAULT 1,
    [loaisp] NVARCHAR(30) NOT NULL DEFAULT 'Sach'
);
GO

-- 1.2. Tác giả
CREATE TABLE [dbo].[tac_gia](
    [ma_tac_gia] INT IDENTITY(1,1) PRIMARY KEY,
    [ten_tac_gia] NVARCHAR(150) NOT NULL,
    [mo_ta] NVARCHAR(MAX) NULL
);
GO

-- 1.3. Sản phẩm (Sách, VPP, Quà tặng)
CREATE TABLE [dbo].[san_pham](
    [masp] INT IDENTITY(1,1) PRIMARY KEY,
    [ma_shop] INT NOT NULL,
    [ma_danh_muc] INT NOT NULL,
    [tensp] NVARCHAR(200) NOT NULL,
    [isbn] VARCHAR(20) NULL,
    [gia_ban] INT NOT NULL CHECK (gia_ban >= 0),
    [so_luong_ton] INT NOT NULL DEFAULT 0 CHECK (so_luong_ton >= 0),
    [so_luong_da_ban] INT NOT NULL DEFAULT 0,
    [muc_ton_toi_thieu] INT NOT NULL DEFAULT 5,
    [loaisp] NVARCHAR(50) NOT NULL DEFAULT N'SACH',
    [mo_ta] NVARCHAR(MAX) NULL,
    [hinh_anh] NVARCHAR(500) NULL,
    [trang_thai] NVARCHAR(30) NOT NULL DEFAULT N'DangBan',
    [trang_thai_khoa] NVARCHAR(30) NOT NULL DEFAULT N'BinhThuong',
    [ngay_tao] DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_sanpham_danhmuc FOREIGN KEY (ma_danh_muc) REFERENCES danh_muc(ma_danh_muc),
    CONSTRAINT FK_sanpham_shop FOREIGN KEY (ma_shop) REFERENCES shop(ma_shop)
);
GO

-- 1.4. Sản phẩm - Tác giả (Quan hệ nhiều - nhiều)
CREATE TABLE [dbo].[san_pham_tac_gia](
    [masp] INT NOT NULL,
    [ma_tac_gia] INT NOT NULL,
    [thu_tu_tac_gia] INT NULL,
    PRIMARY KEY (masp, ma_tac_gia),
    CONSTRAINT FK_sptg_sanpham FOREIGN KEY (masp) REFERENCES san_pham(masp) ON DELETE CASCADE,
    CONSTRAINT FK_sptg_tacgia FOREIGN KEY (ma_tac_gia) REFERENCES tac_gia(ma_tac_gia)
);
GO

-- 1.5. Sản phẩm yêu thích (Wishlist)
CREATE TABLE [dbo].[san_pham_yeu_thich](
    [makh] INT NOT NULL,
    [masp] INT NOT NULL,
    [ngay_thich] DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    PRIMARY KEY (makh, masp),
    CONSTRAINT FK_spyt_khachhang FOREIGN KEY (makh) REFERENCES khach_hang(makh) ON DELETE CASCADE,
    CONSTRAINT FK_spyt_sanpham FOREIGN KEY (masp) REFERENCES san_pham(masp) ON DELETE CASCADE
);
GO

-- 1.6. Sản phẩm đã xem (Lịch sử xem)
CREATE TABLE [dbo].[san_pham_da_xem](
    [makh] INT NOT NULL,
    [masp] INT NOT NULL,
    [thoi_gian_xem] DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    PRIMARY KEY (makh, masp),
    CONSTRAINT FK_spdx_khachhang FOREIGN KEY (makh) REFERENCES khach_hang(makh) ON DELETE CASCADE,
    CONSTRAINT FK_spdx_sanpham FOREIGN KEY (masp) REFERENCES san_pham(masp) ON DELETE CASCADE
);
GO

-- 1.7. Giỏ hàng
CREATE TABLE [dbo].[gio_hang](
    [ma_gio_hang] INT IDENTITY(1,1) PRIMARY KEY,
    [makh] INT NOT NULL UNIQUE,
    [ngay_tao] DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    [ngay_cap_nhat] DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_giohang_khachhang FOREIGN KEY (makh) REFERENCES khach_hang(makh) ON DELETE CASCADE
);
GO

-- 1.8. Chi tiết giỏ hàng
CREATE TABLE [dbo].[chi_tiet_gio_hang](
    [ma_gio_hang] INT NOT NULL,
    [masp] INT NOT NULL,
    [so_luong] INT NOT NULL DEFAULT 1 CHECK (so_luong > 0),
    PRIMARY KEY (ma_gio_hang, masp),
    CONSTRAINT FK_ctgh_giohang FOREIGN KEY (ma_gio_hang) REFERENCES gio_hang(ma_gio_hang) ON DELETE CASCADE,
    CONSTRAINT FK_ctgh_sanpham FOREIGN KEY (masp) REFERENCES san_pham(masp) ON DELETE CASCADE
);
GO

-- --------------------------------------------------------------------
-- 2. CHỈ MỤC TĂNG TỐC TRUY VẤN (INDEXES)
-- --------------------------------------------------------------------
CREATE NONCLUSTERED INDEX idx_sanpham_danhmuc ON san_pham(ma_danh_muc);
CREATE NONCLUSTERED INDEX idx_sanpham_giaban ON san_pham(gia_ban);
CREATE NONCLUSTERED INDEX idx_sanpham_ngaytao ON san_pham(ngay_tao DESC);
CREATE NONCLUSTERED INDEX idx_sanpham_shop ON san_pham(ma_shop, trang_thai);
GO

-- --------------------------------------------------------------------
-- 3. HÀM TÍNH TOÁN (USER-DEFINED FUNCTION)
-- --------------------------------------------------------------------
-- Hàm đếm số lượng sách đang mở bán của 1 shop
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

-- --------------------------------------------------------------------
-- 4. THỦ TỤC LƯU TRỮ (STORED PROCEDURES CÓ TRY...CATCH & TRANSACTION)
-- --------------------------------------------------------------------

-- Thủ tục 1: Tìm kiếm sách theo từ khóa, lọc danh mục, giá & phân trang
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
        sp.masp, sp.tensp, sp.gia_ban, sp.so_luong_ton, sp.so_luong_da_ban,
        sp.hinh_anh, sp.loaisp, dm.ten_danh_muc, s.ten_shop
    FROM san_pham sp
    JOIN danh_muc dm ON sp.ma_danh_muc = dm.ma_danh_muc
    JOIN shop s ON sp.ma_shop = s.ma_shop
    WHERE sp.trang_thai = N'DangBan' AND sp.trang_thai_khoa = N'BinhThuong'
      AND (@TuKhoa IS NULL OR sp.tensp LIKE N'%' + @TuKhoa + N'%' OR sp.isbn LIKE '%' + @TuKhoa + '%')
      AND (@MaDanhMuc IS NULL OR sp.ma_danh_muc = @MaDanhMuc)
      AND (@GiaTu IS NULL OR sp.gia_ban >= @GiaTu)
      AND (@GiaDen IS NULL OR sp.gia_ban <= @GiaDen)
    ORDER BY sp.so_luong_da_ban DESC, sp.masp DESC
    OFFSET @BoQua ROWS FETCH NEXT @KichThuocTrang ROWS ONLY;
END;
GO

-- Thủ tục 2: Thêm sản phẩm vào giỏ hàng (kiểm tra tồn kho & cập nhật số lượng)
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
        SELECT @ton_kho = so_luong_ton FROM san_pham WITH (UPDLOCK) 
        WHERE masp = @masp AND trang_thai = N'DangBan' AND trang_thai_khoa = N'BinhThuong';

        IF @ton_kho IS NULL
        BEGIN
            RAISERROR (N'Sản phẩm không tồn tại hoặc đã ngừng kinh doanh!', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        -- Lấy hoặc tạo giỏ hàng
        DECLARE @ma_gio_hang INT;
        SELECT @ma_gio_hang = ma_gio_hang FROM gio_hang WHERE makh = @makh;

        IF @ma_gio_hang IS NULL
        BEGIN
            INSERT INTO gio_hang (makh) VALUES (@makh);
            SET @ma_gio_hang = SCOPE_IDENTITY();
        END

        -- Kiểm tra số lượng tồn
        DECLARE @so_luong_cu INT = 0;
        SELECT @so_luong_cu = so_luong FROM chi_tiet_gio_hang WHERE ma_gio_hang = @ma_gio_hang AND masp = @masp;

        IF (@so_luong_cu + @so_luong) > @ton_kho
        BEGIN
            RAISERROR (N'Số lượng yêu cầu vượt quá tồn kho hiện có!', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        IF @so_luong_cu > 0
            UPDATE chi_tiet_gio_hang SET so_luong = so_luong + @so_luong WHERE ma_gio_hang = @ma_gio_hang AND masp = @masp;
        ELSE
            INSERT INTO chi_tiet_gio_hang (ma_gio_hang, masp, so_luong) VALUES (@ma_gio_hang, @masp, @so_luong);

        UPDATE gio_hang SET ngay_cap_nhat = SYSUTCDATETIME() WHERE ma_gio_hang = @ma_gio_hang;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        DECLARE @ErrMsg NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR (@ErrMsg, 16, 1);
    END CATCH
END;
GO

-- Thủ tục 3: Xóa lịch sử đã xem quá hạn
CREATE OR ALTER PROCEDURE sp_DonDepSanPhamDaXem
    @SoGioToiDa INT = 1
AS
BEGIN
    SET NOCOUNT ON;
    DELETE FROM san_pham_da_xem WHERE thoi_gian_xem < DATEADD(HOUR, -@SoGioToiDa, GETDATE());
    SELECT @@ROWCOUNT AS SoBanGhiDaDonDep;
END;
GO

-- --------------------------------------------------------------------
-- 5. KHUNG NHÌN (VIEWS)
-- --------------------------------------------------------------------
-- View 1: Top sách bán chạy nhất
CREATE OR ALTER VIEW vw_TopSachBanChayToanSan AS
SELECT TOP 20
    sp.masp, sp.tensp, sp.gia_ban, sp.so_luong_da_ban, sp.so_luong_ton, sp.hinh_anh, dm.ten_danh_muc, s.ten_shop
FROM san_pham sp
JOIN danh_muc dm ON sp.ma_danh_muc = dm.ma_danh_muc
JOIN shop s ON sp.ma_shop = s.ma_shop
WHERE sp.trang_thai = N'DangBan' AND sp.trang_thai_khoa = N'BinhThuong'
ORDER BY sp.so_luong_da_ban DESC;
GO

-- View 2: Chi tiết giỏ hàng kèm tính thành tiền
CREATE OR ALTER VIEW vw_ChiTietGioHangKhachHang AS
SELECT 
    gh.makh, gh.ma_gio_hang, ct.masp, sp.tensp, sp.gia_ban, ct.so_luong,
    (sp.gia_ban * ct.so_luong) AS thanh_tien, sp.hinh_anh, sp.so_luong_ton, s.ten_shop
FROM gio_hang gh
JOIN chi_tiet_gio_hang ct ON gh.ma_gio_hang = ct.ma_gio_hang
JOIN san_pham sp ON ct.masp = sp.masp
JOIN shop s ON sp.ma_shop = s.ma_shop;
GO

-- --------------------------------------------------------------------
-- 6. PHÂN QUYỀN (RBAC)
-- --------------------------------------------------------------------
GRANT SELECT ON san_pham TO Role_KhachHang;
GRANT SELECT ON danh_muc TO Role_KhachHang;
GRANT SELECT ON tac_gia TO Role_KhachHang;
GRANT SELECT, INSERT, UPDATE, DELETE ON gio_hang TO Role_KhachHang;
GRANT SELECT, INSERT, UPDATE, DELETE ON chi_tiet_gio_hang TO Role_KhachHang;
GRANT EXECUTE ON sp_TimKiemSachPhanTrang TO Role_KhachHang;
GRANT EXECUTE ON sp_ThemSanPhamVaoGioHang TO Role_KhachHang;
DENY INSERT, UPDATE, DELETE ON san_pham TO Role_KhachHang;
GO
