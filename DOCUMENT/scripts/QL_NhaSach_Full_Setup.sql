-- ====================================================================
-- THE4BOOKSTORE - PROJECT HỆ QUẢN TRỊ CƠ SỞ DỮ LIỆU (DBMS330284)
-- PHẦN 1: XÁC THỰC, TÀI KHOẢN, HỒ SƠ, GIAN HÀNG, VẬN CHUYỂN & BẢO MẬT RBAC
-- SINH VIÊN PHỤ TRÁCH: NGUYỄN ĐẶNG CAO TƯỜNG (MSSV: 24110375)
-- HỆ QUẢN TRỊ CSDL: MICROSOFT SQL SERVER 2022/2025 (T-SQL)
-- MẬT KHẨU MẶC ĐỊNH TẤT CẢ TÀI KHOẢN SEED DATA: 123456
-- ====================================================================

USE master;
GO

IF EXISTS (SELECT name FROM sys.databases WHERE name = N'QL_NhaSach')
BEGIN
    ALTER DATABASE [QL_NhaSach] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE [QL_NhaSach];
END
GO

CREATE DATABASE [QL_NhaSach] COLLATE Vietnamese_CI_AS;
GO

USE [QL_NhaSach];
GO

-- ====================================================================
-- 1. BẢNG DỮ LIỆU & RÀNG BUỘC (TABLES & CONSTRAINTS)
-- ====================================================================

-- Bảng 1: tai_khoan (Tài khoản người dùng, phân quyền đăng nhập hệ thống)
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[tai_khoan](
	[ma_tai_khoan] [int] IDENTITY(1,1) NOT NULL,
	[ten_dang_nhap] [varchar](50) COLLATE Vietnamese_CI_AS NOT NULL,
	[email] [varchar](100) COLLATE Vietnamese_CI_AS NOT NULL,
	[mat_khau_hash] [varchar](255) COLLATE Vietnamese_CI_AS NULL,
	[vai_tro] [varchar](30) COLLATE Vietnamese_CI_AS NOT NULL,
	[trang_thai] [nvarchar](30) COLLATE Vietnamese_CI_AS NOT NULL,
	[auth_provider] [varchar](20) COLLATE Vietnamese_CI_AS NOT NULL,
	[provider_id] [varchar](100) COLLATE Vietnamese_CI_AS NULL,
	[token_version] [int] NOT NULL,
	[otp_code] [varchar](10) COLLATE Vietnamese_CI_AS NULL,
	[otp_expiry] [datetime2](7) NULL,
	[ngay_tao] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ma_tai_khoan] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_tai_khoan_email] UNIQUE NONCLUSTERED 
(
	[email] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_tai_khoan_username] UNIQUE NONCLUSTERED 
(
	[ten_dang_nhap] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[tai_khoan] ADD CONSTRAINT [DF_taikhoan_vaitro] DEFAULT ('KHACHHANG') FOR [vai_tro];
GO
ALTER TABLE [dbo].[tai_khoan] ADD CONSTRAINT [DF_taikhoan_trangthai] DEFAULT (N'HoatDong') FOR [trang_thai];
GO
ALTER TABLE [dbo].[tai_khoan] ADD CONSTRAINT [DF_taikhoan_auth] DEFAULT ('LOCAL') FOR [auth_provider];
GO
ALTER TABLE [dbo].[tai_khoan] ADD CONSTRAINT [DF_taikhoan_token] DEFAULT ((1)) FOR [token_version];
GO
ALTER TABLE [dbo].[tai_khoan] ADD CONSTRAINT [DF_taikhoan_ngaytao] DEFAULT (sysutcdatetime()) FOR [ngay_tao];
GO
ALTER TABLE [dbo].[tai_khoan] WITH CHECK ADD CONSTRAINT [CK_taikhoan_vaitro] CHECK (([vai_tro]='ADMIN' OR [vai_tro]='MANAGER' OR [vai_tro]='VENDOR' OR [vai_tro]='KHACHHANG'));
GO
ALTER TABLE [dbo].[tai_khoan] CHECK CONSTRAINT [CK_taikhoan_vaitro];
GO

-- Bảng 2: khach_hang (Hồ sơ người dùng cá nhân liên kết tài khoản)
CREATE TABLE [dbo].[khach_hang](
	[makh] [int] IDENTITY(1,1) NOT NULL,
	[ma_tai_khoan] [int] NOT NULL,
	[ho_ten] [nvarchar](100) COLLATE Vietnamese_CI_AS NOT NULL,
	[email] [varchar](100) COLLATE Vietnamese_CI_AS NOT NULL,
	[so_dien_thoai] [varchar](20) COLLATE Vietnamese_CI_AS NULL,
	[dia_chi] [nvarchar](255) COLLATE Vietnamese_CI_AS NULL,
	[anh_dai_dien] [nvarchar](500) COLLATE Vietnamese_CI_AS NULL,
	[ngay_dang_ky] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[makh] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_khach_hang_email] UNIQUE NONCLUSTERED 
(
	[email] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_khach_hang_mataikhoan] UNIQUE NONCLUSTERED 
(
	[ma_tai_khoan] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[khach_hang] ADD CONSTRAINT [DF_khachhang_ngaydangky] DEFAULT (sysutcdatetime()) FOR [ngay_dang_ky];
GO
ALTER TABLE [dbo].[khach_hang] WITH CHECK ADD CONSTRAINT [FK_khach_hang_tai_khoan] FOREIGN KEY([ma_tai_khoan])
REFERENCES [dbo].[tai_khoan] ([ma_tai_khoan])
ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[khach_hang] CHECK CONSTRAINT [FK_khach_hang_tai_khoan];
GO

-- Bảng 3: dia_chi_giao_hang (Sổ địa chỉ nhận hàng của khách hàng)
CREATE TABLE [dbo].[dia_chi_giao_hang](
	[ma_dia_chi] [int] IDENTITY(1,1) NOT NULL,
	[makh] [int] NOT NULL,
	[ten_nguoi_nhan] [nvarchar](100) COLLATE Vietnamese_CI_AS NOT NULL,
	[so_dien_thoai] [varchar](20) COLLATE Vietnamese_CI_AS NOT NULL,
	[dia_chi_chi_tiet] [nvarchar](255) COLLATE Vietnamese_CI_AS NOT NULL,
	[phuong_xa] [nvarchar](100) COLLATE Vietnamese_CI_AS NOT NULL,
	[quan_huyen] [nvarchar](100) COLLATE Vietnamese_CI_AS NOT NULL,
	[tinh_thanh] [nvarchar](100) COLLATE Vietnamese_CI_AS NOT NULL,
	[la_mac_dinh] [bit] NOT NULL,
	[ngay_tao] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ma_dia_chi] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[dia_chi_giao_hang] ADD CONSTRAINT [DF_dcgh_lamacdinh] DEFAULT ((0)) FOR [la_mac_dinh];
GO
ALTER TABLE [dbo].[dia_chi_giao_hang] ADD CONSTRAINT [DF_dcgh_ngaytao] DEFAULT (sysutcdatetime()) FOR [ngay_tao];
GO
ALTER TABLE [dbo].[dia_chi_giao_hang] WITH CHECK ADD CONSTRAINT [FK_dia_chi_khach_hang] FOREIGN KEY([makh])
REFERENCES [dbo].[khach_hang] ([makh]);
GO
ALTER TABLE [dbo].[dia_chi_giao_hang] CHECK CONSTRAINT [FK_dia_chi_khach_hang];
GO

-- Bảng 4: shop (Hồ sơ gian hàng đối tác Vendor kinh doanh trên sàn)
CREATE TABLE [dbo].[shop](
	[ma_shop] [int] IDENTITY(1,1) NOT NULL,
	[ma_tai_khoan] [int] NOT NULL,
	[ten_shop] [nvarchar](150) COLLATE Vietnamese_CI_AS NOT NULL,
	[slug] [varchar](150) COLLATE Vietnamese_CI_AS NOT NULL,
	[logo] [nvarchar](500) COLLATE Vietnamese_CI_AS NULL,
	[banner] [nvarchar](500) COLLATE Vietnamese_CI_AS NULL,
	[mo_ta] [nvarchar](max) COLLATE Vietnamese_CI_AS NULL,
	[dia_chi_shop] [nvarchar](255) COLLATE Vietnamese_CI_AS NULL,
	[so_dien_thoai] [varchar](20) COLLATE Vietnamese_CI_AS NULL,
	[email_shop] [varchar](100) COLLATE Vietnamese_CI_AS NULL,
	[chiet_khau_phan_tram] [decimal](5, 2) NOT NULL,
	[trang_thai] [nvarchar](30) COLLATE Vietnamese_CI_AS NOT NULL,
	[ngay_tao] [datetime2](7) NOT NULL,
	[ngay_cap_nhat] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[ma_shop] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[ma_tai_khoan] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_shop_slug] UNIQUE NONCLUSTERED 
(
	[slug] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
ALTER TABLE [dbo].[shop] ADD CONSTRAINT [DF_shop_chietkhau] DEFAULT ((5.00)) FOR [chiet_khau_phan_tram];
GO
ALTER TABLE [dbo].[shop] ADD CONSTRAINT [DF_shop_trangthai] DEFAULT (N'ChoDuyet') FOR [trang_thai];
GO
ALTER TABLE [dbo].[shop] ADD CONSTRAINT [DF_shop_ngaytao] DEFAULT (sysutcdatetime()) FOR [ngay_tao];
GO
ALTER TABLE [dbo].[shop] WITH CHECK ADD CONSTRAINT [FK_shop_tai_khoan] FOREIGN KEY([ma_tai_khoan])
REFERENCES [dbo].[tai_khoan] ([ma_tai_khoan]);
GO
ALTER TABLE [dbo].[shop] CHECK CONSTRAINT [FK_shop_tai_khoan];
GO
ALTER TABLE [dbo].[shop] WITH CHECK ADD CONSTRAINT [CK_shop_chietkhau] CHECK (([chiet_khau_phan_tram]>=(0) AND [chiet_khau_phan_tram]<=(100)));
GO
ALTER TABLE [dbo].[shop] CHECK CONSTRAINT [CK_shop_chietkhau];
GO

-- Bảng 5: nha_van_chuyen (Đơn vị vận chuyển liên kết sàn - Cấu hình Logistics hệ thống)
CREATE TABLE [dbo].[nha_van_chuyen](
	[ma_nvc] [int] IDENTITY(1,1) NOT NULL,
	[ten_nvc] [nvarchar](100) COLLATE Vietnamese_CI_AS NOT NULL,
	[phi_co_ban] [int] NOT NULL,
	[thoi_gian_du_kien] [nvarchar](100) COLLATE Vietnamese_CI_AS NOT NULL,
	[trang_thai] [nvarchar](30) COLLATE Vietnamese_CI_AS NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ma_nvc] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[nha_van_chuyen] ADD CONSTRAINT [DF_nvc_trangthai] DEFAULT (N'HoatDong') FOR [trang_thai];
GO
ALTER TABLE [dbo].[nha_van_chuyen] WITH CHECK ADD CONSTRAINT [CK_nvc_phico_ban] CHECK (([phi_co_ban]>=(0)));
GO
ALTER TABLE [dbo].[nha_van_chuyen] CHECK CONSTRAINT [CK_nvc_phico_ban];
GO

-- Bảng 6: password_reset_token (Quản lý mã OTP và Token khôi phục mật khẩu)
CREATE TABLE [dbo].[password_reset_token](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[otp_code] [varchar](10) COLLATE Vietnamese_CI_AS NULL,
	[token] [varchar](255) COLLATE Vietnamese_CI_AS NOT NULL,
	[ma_tai_khoan] [int] NOT NULL,
	[expiry_date] [datetime2](7) NOT NULL,
	[used] [bit] NOT NULL,
	[ngay_tao] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[password_reset_token] ADD CONSTRAINT [DF_prt_used] DEFAULT ((0)) FOR [used];
GO
ALTER TABLE [dbo].[password_reset_token] ADD CONSTRAINT [DF_prt_ngaytao] DEFAULT (sysutcdatetime()) FOR [ngay_tao];
GO
ALTER TABLE [dbo].[password_reset_token] WITH CHECK ADD CONSTRAINT [FK_prt_tai_khoan] FOREIGN KEY([ma_tai_khoan])
REFERENCES [dbo].[tai_khoan] ([ma_tai_khoan])
ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[password_reset_token] CHECK CONSTRAINT [FK_prt_tai_khoan];
GO

-- Bảng 7: nhat_ky_bao_mat (Audit Log ghi vết các thao tác nhạy cảm trên hệ thống)
CREATE TABLE [dbo].[nhat_ky_bao_mat](
	[ma_nhat_ky] [int] IDENTITY(1,1) NOT NULL,
	[ten_bang] [varchar](50) COLLATE Vietnamese_CI_AS NOT NULL,
	[hanh_dong] [varchar](20) COLLATE Vietnamese_CI_AS NOT NULL,
	[ban_ghi_id] [int] NOT NULL,
	[du_lieu_cu] [nvarchar](max) COLLATE Vietnamese_CI_AS NULL,
	[du_lieu_moi] [nvarchar](max) COLLATE Vietnamese_CI_AS NULL,
	[nguoi_thuc_hien] [nvarchar](100) COLLATE Vietnamese_CI_AS NULL,
	[thoi_gian] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ma_nhat_ky] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
ALTER TABLE [dbo].[nhat_ky_bao_mat] ADD CONSTRAINT [DF_nhatky_thoigian] DEFAULT (sysutcdatetime()) FOR [thoi_gian];
GO

-- ====================================================================
-- 2. TỐI ƯU CHỈ MỤC & RÀNG BUỘC TOÀN VẸN
-- ====================================================================

-- Index tối ưu tra cứu gian hàng theo trạng thái
CREATE NONCLUSTERED INDEX [idx_shop_trangthai] ON [dbo].[shop]
(
	[trang_thai] ASC
)INCLUDE ([ten_shop], [logo]) ON [PRIMARY];
GO

-- ====================================================================
-- 3. TRIGGERS AN TOÀN & AUDIT LOG DỮ LIỆU
-- ====================================================================

-- Trigger 1: Ghi vết khi có sự thay đổi quyền hạn / trạng thái tài khoản
CREATE OR ALTER TRIGGER trg_AuditThayDoiVaiTroTaiKhoan
ON tai_khoan
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    IF UPDATE(vai_tro) OR UPDATE(trang_thai)
    BEGIN
        INSERT INTO nhat_ky_bao_mat (
            ten_bang, hanh_dong, ban_ghi_id, du_lieu_cu, du_lieu_moi, nguoi_thuc_hien, thoi_gian
        )
        SELECT 
            'tai_khoan',
            'UPDATE',
            i.ma_tai_khoan,
            CONCAT(N'VaiTro: ', d.vai_tro, N', TrangThai: ', d.trang_thai),
            CONCAT(N'VaiTro: ', i.vai_tro, N', TrangThai: ', i.trang_thai),
            SYSTEM_USER,
            SYSUTCDATETIME()
        FROM inserted i
        JOIN deleted d ON i.ma_tai_khoan = d.ma_tai_khoan;
    END
END;
GO

-- Trigger 2: Ghi log audit khi khách hàng chỉnh sửa hoặc cập nhật địa chỉ giao hàng
CREATE OR ALTER TRIGGER trg_AuditThayDoiDiaChiGiaoHang
ON dia_chi_giao_hang
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    IF UPDATE(dia_chi_chi_tiet) OR UPDATE(so_dien_thoai) OR UPDATE(la_mac_dinh)
    BEGIN
        INSERT INTO nhat_ky_bao_mat (
            ten_bang, hanh_dong, ban_ghi_id, du_lieu_cu, du_lieu_moi, nguoi_thuc_hien, thoi_gian
        )
        SELECT 
            'dia_chi_giao_hang',
            'UPDATE',
            i.ma_dia_chi,
            CONCAT(N'DC: ', d.dia_chi_chi_tiet, N', SĐT: ', d.so_dien_thoai, N', MacDinh: ', d.la_mac_dinh),
            CONCAT(N'DC: ', i.dia_chi_chi_tiet, N', SĐT: ', i.so_dien_thoai, N', MacDinh: ', i.la_mac_dinh),
            SYSTEM_USER,
            SYSUTCDATETIME()
        FROM inserted i
        JOIN deleted d ON i.ma_dia_chi = d.ma_dia_chi;
    END
END;
GO

-- ====================================================================
-- 4. THỦ TỤC LƯU TRỮ (STORED PROCEDURES)
-- ====================================================================

-- Thủ tục 1: Thêm hoặc cập nhật địa chỉ giao hàng với cơ chế reset cờ mặc định
CREATE OR ALTER PROCEDURE sp_ThemCapNhatDiaChiGiaoHang
    @ma_dia_chi INT = NULL,
    @makh INT,
    @ten_nguoi_nhan NVARCHAR(100),
    @so_dien_thoai VARCHAR(20),
    @dia_chi_chi_tiet NVARCHAR(255),
    @phuong_xa NVARCHAR(100),
    @quan_huyen NVARCHAR(100),
    @tinh_thanh NVARCHAR(100),
    @la_mac_dinh BIT = 0
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;

        -- Nếu đặt làm địa chỉ mặc định, reset cờ các địa chỉ cũ của khách này
        IF @la_mac_dinh = 1
        BEGIN
            UPDATE dia_chi_giao_hang
            SET la_mac_dinh = 0
            WHERE makh = @makh;
        END

        IF @ma_dia_chi IS NULL OR @ma_dia_chi = 0
        BEGIN
            INSERT INTO dia_chi_giao_hang (
                makh, ten_nguoi_nhan, so_dien_thoai, dia_chi_chi_tiet, 
                phuong_xa, quan_huyen, tinh_thanh, la_mac_dinh, ngay_tao
            )
            VALUES (
                @makh, @ten_nguoi_nhan, @so_dien_thoai, @dia_chi_chi_tiet,
                @phuong_xa, @quan_huyen, @tinh_thanh, @la_mac_dinh, SYSUTCDATETIME()
            );
        END
        ELSE
        BEGIN
            UPDATE dia_chi_giao_hang
            SET ten_nguoi_nhan = @ten_nguoi_nhan,
                so_dien_thoai = @so_dien_thoai,
                dia_chi_chi_tiet = @dia_chi_chi_tiet,
                phuong_xa = @phuong_xa,
                quan_huyen = @quan_huyen,
                tinh_thanh = @tinh_thanh,
                la_mac_dinh = @la_mac_dinh
            WHERE ma_dia_chi = @ma_dia_chi AND makh = @makh;
        END

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        DECLARE @ErrMsg NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR (@ErrMsg, 16, 1);
    END CATCH
END;
GO

-- Thủ tục 2: Quản trị viên cập nhật cấu hình đơn vị vận chuyển
CREATE OR ALTER PROCEDURE sp_AdminCapNhatNhaVanChuyen
    @ma_nvc INT,
    @ten_nvc NVARCHAR(100),
    @phi_co_ban INT,
    @thoi_gian_du_kien NVARCHAR(100),
    @trang_thai NVARCHAR(30)
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;

        IF NOT EXISTS (SELECT 1 FROM nha_van_chuyen WHERE ma_nvc = @ma_nvc)
        BEGIN
            RAISERROR (N'Đơn vị vận chuyển không tồn tại!', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        UPDATE nha_van_chuyen
        SET ten_nvc = @ten_nvc,
            phi_co_ban = @phi_co_ban,
            thoi_gian_du_kien = @thoi_gian_du_kien,
            trang_thai = @trang_thai
        WHERE ma_nvc = @ma_nvc;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        DECLARE @Err NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR (@Err, 16, 1);
    END CATCH
END;
GO

-- Thủ tục 3: Đổi mật khẩu người dùng và tăng token_version để hủy phiên cũ
CREATE OR ALTER PROCEDURE sp_DoiMatKhauNguoiDung
    @ma_tai_khoan INT,
    @mat_khau_moi_hash VARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;

        IF NOT EXISTS (SELECT 1 FROM tai_khoan WHERE ma_tai_khoan = @ma_tai_khoan)
        BEGIN
            RAISERROR (N'Tài khoản không tồn tại!', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        UPDATE tai_khoan
        SET mat_khau_hash = @mat_khau_moi_hash,
            token_version = token_version + 1
        WHERE ma_tai_khoan = @ma_tai_khoan;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        DECLARE @ErrPW NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR (@ErrPW, 16, 1);
    END CATCH
END;
GO

-- ====================================================================
-- 5. HÀM NGƯỜI DÙNG ĐỊNH NGHĨA (USER-DEFINED FUNCTIONS)
-- ====================================================================

-- Hàm 1: Lấy địa chỉ giao hàng mặc định của khách hàng
CREATE OR ALTER FUNCTION dbo.fn_LayDiaChiMacDinhKhachHang (@makh INT)
RETURNS NVARCHAR(500)
AS
BEGIN
    DECLARE @dia_chi_day_du NVARCHAR(500);

    SELECT TOP 1 
        @dia_chi_day_du = CONCAT(dia_chi_chi_tiet, N', ', phuong_xa, N', ', quan_huyen, N', ', tinh_thanh)
    FROM dia_chi_giao_hang
    WHERE makh = @makh AND la_mac_dinh = 1;

    IF @dia_chi_day_du IS NULL
    BEGIN
        SELECT TOP 1 
            @dia_chi_day_du = CONCAT(dia_chi_chi_tiet, N', ', phuong_xa, N', ', quan_huyen, N', ', tinh_thanh)
        FROM dia_chi_giao_hang
        WHERE makh = @makh
        ORDER BY ma_dia_chi DESC;
    END

    RETURN @dia_chi_day_du;
END;
GO

-- Hàm 2: Kiểm tra định dạng số điện thoại Việt Nam hợp lệ (10 chữ số, đầu số 03, 05, 07, 08, 09)
CREATE OR ALTER FUNCTION dbo.fn_KiemTraSoDienThoaiHopLe (@sdt VARCHAR(20))
RETURNS BIT
AS
BEGIN
    DECLARE @hop_le BIT = 0;
    
    IF @sdt IS NOT NULL 
       AND LEN(@sdt) = 10 
       AND @sdt NOT LIKE '%[^0-9]%'
       AND (@sdt LIKE '03%' OR @sdt LIKE '05%' OR @sdt LIKE '07%' OR @sdt LIKE '08%' OR @sdt LIKE '09%')
    BEGIN
        SET @hop_le = 1;
    END

    RETURN @hop_le;
END;
GO

-- ====================================================================
-- 6. BẢO MẬT & PHÂN QUYỀN TRÊN MICROSOFT SQL SERVER (RBAC)
-- ====================================================================

IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'Role_Admin' AND type = 'R') 
    CREATE ROLE Role_Admin;
GO
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'Role_Vendor' AND type = 'R') 
    CREATE ROLE Role_Vendor;
GO
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'Role_Manager' AND type = 'R') 
    CREATE ROLE Role_Manager;
GO
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'Role_KhachHang' AND type = 'R') 
    CREATE ROLE Role_KhachHang;
GO

-- Phân quyền RBAC trên các đối tượng Module 1
GRANT CONTROL TO Role_Admin;
GRANT SELECT ON nhat_ky_bao_mat TO Role_Admin;
GRANT SELECT, INSERT, UPDATE, DELETE ON nha_van_chuyen TO Role_Admin;
GRANT EXECUTE ON sp_AdminCapNhatNhaVanChuyen TO Role_Admin;

GRANT SELECT ON shop TO Role_KhachHang;
GRANT SELECT, INSERT, UPDATE, DELETE ON dia_chi_giao_hang TO Role_KhachHang;
GRANT SELECT ON nha_van_chuyen TO Role_KhachHang;
GRANT EXECUTE ON sp_ThemCapNhatDiaChiGiaoHang TO Role_KhachHang;
GRANT EXECUTE ON sp_DoiMatKhauNguoiDung TO Role_KhachHang;
GRANT EXECUTE ON fn_LayDiaChiMacDinhKhachHang TO Role_KhachHang;
GRANT EXECUTE ON fn_KiemTraSoDienThoaiHopLe TO Role_KhachHang;

GRANT SELECT, UPDATE ON shop TO Role_Vendor;
GRANT SELECT ON nha_van_chuyen TO Role_Vendor;

GRANT SELECT, UPDATE ON shop TO Role_Manager;
GRANT SELECT ON nha_van_chuyen TO Role_Manager;

DENY INSERT, UPDATE, DELETE ON tai_khoan TO Role_Vendor;
DENY INSERT, UPDATE, DELETE ON tai_khoan TO Role_KhachHang;
DENY INSERT, UPDATE, DELETE ON nhat_ky_bao_mat TO Role_KhachHang;
DENY INSERT, UPDATE, DELETE ON nhat_ky_bao_mat TO Role_Vendor;
DENY DELETE ON shop TO Role_Manager;
GO

-- Tạo Login & Database User cho ứng dụng Spring Boot kết nối SQL Server
IF NOT EXISTS (SELECT * FROM sys.server_principals WHERE name = 'the4book_user')
BEGIN
    CREATE LOGIN [the4book_user] WITH PASSWORD = 'ChangeMe@2026', CHECK_POLICY = OFF;
END
GO
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'the4book_user')
BEGIN
    CREATE USER [the4book_user] FOR LOGIN [the4book_user];
END
GO
ALTER ROLE [db_owner] ADD MEMBER [the4book_user];
GO

-- ====================================================================
-- 7. DỮ LIỆU MẪU BAN ĐẦU (SEED DATA: ACCOUNTS, SHOPS & LOGISTICS)
-- ====================================================================

SET IDENTITY_INSERT [dbo].[tai_khoan] ON;
INSERT [dbo].[tai_khoan] ([ma_tai_khoan], [ten_dang_nhap], [email], [mat_khau_hash], [vai_tro], [trang_thai], [auth_provider], [provider_id], [token_version], [otp_code], [otp_expiry], [ngay_tao]) 
VALUES 
(1, N'admin', N'admin@the4bookstore.vn', N'$2a$10$SCy7moQtkixA39J791LR3.EGHlLW.xjKMz/uSmCt20n1mWHOnYd0y', N'ADMIN', N'HoatDong', N'LOCAL', NULL, 1, NULL, NULL, CAST(N'2026-10-16T15:04:53' AS DateTime2)),
(3, N'vendor_kimdong', N'kimdong@gmail.com', N'$2a$10$SCy7moQtkixA39J791LR3.EGHlLW.xjKMz/uSmCt20n1mWHOnYd0y', N'VENDOR', N'HoatDong', N'LOCAL', NULL, 1, NULL, NULL, CAST(N'2026-10-16T15:04:53' AS DateTime2)),
(4, N'vendor_nhanam', N'nhanam@gmail.com', N'$2a$10$SCy7moQtkixA39J791LR3.EGHlLW.xjKMz/uSmCt20n1mWHOnYd0y', N'VENDOR', N'HoatDong', N'LOCAL', NULL, 1, NULL, NULL, CAST(N'2026-10-16T15:04:53' AS DateTime2)),
(5, N'khachhang_an', N'nguyenvanan@gmail.com', N'$2a$10$SCy7moQtkixA39J791LR3.EGHlLW.xjKMz/uSmCt20n1mWHOnYd0y', N'KHACHHANG', N'HoatDong', N'LOCAL', NULL, 1, NULL, NULL, CAST(N'2026-10-16T15:04:53' AS DateTime2)),
(6, N'khachhang_binh', N'lethibinh@gmail.com', N'$2a$10$SCy7moQtkixA39J791LR3.EGHlLW.xjKMz/uSmCt20n1mWHOnYd0y', N'KHACHHANG', N'HoatDong', N'LOCAL', NULL, 1, NULL, NULL, CAST(N'2026-10-16T15:04:53' AS DateTime2)),
(7, N'phat', N'thangphatnekea21333@gmail.com', N'$2a$10$SCy7moQtkixA39J791LR3.EGHlLW.xjKMz/uSmCt20n1mWHOnYd0y', N'KHACHHANG', N'HoatDong', N'LOCAL', NULL, 1, NULL, NULL, CAST(N'2026-10-17T10:05:28' AS DateTime2)),
(8, N'manager', N'manager@the4bookstore.vn', N'$2a$10$SCy7moQtkixA39J791LR3.EGHlLW.xjKMz/uSmCt20n1mWHOnYd0y', N'MANAGER', N'HoatDong', N'LOCAL', NULL, 1, NULL, NULL, CAST(N'2026-10-17T03:37:45' AS DateTime2));
SET IDENTITY_INSERT [dbo].[tai_khoan] OFF;
GO

SET IDENTITY_INSERT [dbo].[khach_hang] ON;
INSERT [dbo].[khach_hang] ([makh], [ma_tai_khoan], [ho_ten], [email], [so_dien_thoai], [dia_chi], [anh_dai_dien], [ngay_dang_ky]) 
VALUES 
(1, 5, N'Nguyễn Văn An', N'nguyenvanan@gmail.com', N'0988111222', N'123 Lê Lợi, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh', NULL, CAST(N'2026-10-16T15:04:53' AS DateTime2)),
(2, 6, N'Lê Thị Bình', N'lethibinh@gmail.com', N'0977333444', N'456 Võ Văn Ngân, Phường Linh Chiểu, TP. Thủ Đức, TP. Hồ Chí Minh', NULL, CAST(N'2026-10-16T15:04:53' AS DateTime2)),
(3, 7, N'Võ Thành Phát', N'thangphatnekea21333@gmail.com', N'0967238940', N'Dĩ An, Bình Dương', NULL, CAST(N'2026-10-17T10:05:28' AS DateTime2)),
(4, 3, N'vendor_kimdong', N'kimdong@gmail.com', NULL, NULL, NULL, CAST(N'2026-10-17T10:09:24' AS DateTime2)),
(5, 1, N'admin', N'admin@the4bookstore.vn', NULL, NULL, NULL, CAST(N'2026-10-17T10:11:47' AS DateTime2)),
(6, 4, N'vendor_nhanam', N'nhanam@gmail.com', NULL, NULL, NULL, CAST(N'2026-10-17T10:14:13' AS DateTime2)),
(7, 8, N'manager', N'manager@the4bookstore.vn', NULL, NULL, NULL, CAST(N'2026-10-17T10:15:00' AS DateTime2));
SET IDENTITY_INSERT [dbo].[khach_hang] OFF;
GO

SET IDENTITY_INSERT [dbo].[dia_chi_giao_hang] ON;
INSERT [dbo].[dia_chi_giao_hang] ([ma_dia_chi], [makh], [ten_nguoi_nhan], [so_dien_thoai], [dia_chi_chi_tiet], [phuong_xa], [quan_huyen], [tinh_thanh], [la_mac_dinh], [ngay_tao]) 
VALUES 
(1, 1, N'Nguyễn Văn An', N'0988111222', N'123 Lê Lợi', N'Bến Nghé', N'Quận 1', N'TP. Hồ Chí Minh', 1, CAST(N'2026-10-16T15:04:53' AS DateTime2)),
(2, 2, N'Lê Thị Bình', N'0977333444', N'456 Võ Văn Ngân', N'Linh Chiểu', N'TP. Thủ Đức', N'TP. Hồ Chí Minh', 1, CAST(N'2026-10-16T15:04:53' AS DateTime2)),
(3, 3, N'Võ Thành Phát', N'0967238940', N'Ký túc xá khu B ĐHQG', N'Đông Hòa', N'Dĩ An', N'Bình Dương', 1, CAST(N'2026-10-17T10:06:00' AS DateTime2)),
(4, 3, N'Nguyễn Ngọc Thiện', N'0934567822', N'462 Hoàng Diệu 2', N'Linh Trung', N'TP. Thủ Đức', N'TP. Hồ Chí Minh', 0, CAST(N'2026-10-17T11:08:22' AS DateTime2));
SET IDENTITY_INSERT [dbo].[dia_chi_giao_hang] OFF;
GO

SET IDENTITY_INSERT [dbo].[shop] ON;
INSERT [dbo].[shop] ([ma_shop], [ma_tai_khoan], [ten_shop], [slug], [logo], [banner], [mo_ta], [dia_chi_shop], [so_dien_thoai], [email_shop], [chiet_khau_phan_tram], [trang_thai], [ngay_tao], [ngay_cap_nhat]) 
VALUES 
(1, 3, N'Hiệu Sách Kim Đồng Official', N'hieu-sach-kim-dong', N'https://res.cloudinary.com/vkqtqiha/image/upload/v1792206732/the4bookstore/shops/ypnxy2kltgf1o9bo6pvr.png', N'https://res.cloudinary.com/vkqtqiha/image/upload/v1792206734/the4bookstore/shops/zwhzbwbgrnmcgot7koac.jpg', N'Gian hàng chính hãng truyện tranh, văn học thiếu nhi của NXB Kim Đồng', N'55 Quang Trung, Hai Bà Trưng, Hà Nội', N'02439434730', N'kimdong@gmail.com', CAST(5.00 AS Decimal(5, 2)), N'HoatDong', CAST(N'2026-10-16T15:04:53.1424040' AS DateTime2), CAST(N'2026-10-17T10:12:18.7290932' AS DateTime2)),
(2, 4, N'Nhã Nam Book Store', N'nha-nam-book-store', NULL, NULL, N'Nhã Nam - Nhận diện tác phẩm kinh điển và văn học tinh hoa thế giới', N'59 Đỗ Quang, Cầu Giấy, Hà Nội', N'02435146875', N'nhanam@gmail.com', CAST(5.00 AS Decimal(5, 2)), N'HoatDong', CAST(N'2026-10-16T15:04:53.1424040' AS DateTime2), NULL);
SET IDENTITY_INSERT [dbo].[shop] OFF;
GO

SET IDENTITY_INSERT [dbo].[nha_van_chuyen] ON;
INSERT [dbo].[nha_van_chuyen] ([ma_nvc], [ten_nvc], [phi_co_ban], [thoi_gian_du_kien], [trang_thai]) 
VALUES 
(1, N'Giao Hàng Tiết Kiệm (GHTK)', 22000, N'1 - 2 ngày làm việc', N'HoatDong'),
(2, N'Giao Hàng Nhanh (GHN)', 25000, N'2 - 3 ngày làm việc', N'HoatDong'),
(3, N'Viettel Post', 28000, N'2 - 4 ngày làm việc', N'HoatDong');
SET IDENTITY_INSERT [dbo].[nha_van_chuyen] OFF;
GO

SET IDENTITY_INSERT [dbo].[password_reset_token] ON;
INSERT [dbo].[password_reset_token] ([id], [otp_code], [token], [ma_tai_khoan], [expiry_date], [used], [ngay_tao]) 
VALUES (1, N'994162', N'994162', 7, CAST(N'2026-10-17T10:10:37' AS DateTime2), 1, CAST(N'2026-10-17T10:05:37' AS DateTime2));
SET IDENTITY_INSERT [dbo].[password_reset_token] OFF;
GO

SET IDENTITY_INSERT [dbo].[nhat_ky_bao_mat] ON;
INSERT [dbo].[nhat_ky_bao_mat] ([ma_nhat_ky], [ten_bang], [hanh_dong], [ban_ghi_id], [du_lieu_cu], [du_lieu_moi], [nguoi_thuc_hien], [thoi_gian]) 
VALUES (1, N'tai_khoan', N'UPDATE', 8, N'VaiTro: NHANVIEN, TrangThai: HoatDong', N'VaiTro: MANAGER, TrangThai: HoatDong', N'WQE-Y7000\Phat', CAST(N'2026-10-17T03:37:19' AS DateTime2));
SET IDENTITY_INSERT [dbo].[nhat_ky_bao_mat] OFF;
GO

PRINT N'>>> HOÀN THÀNH PHẦN 1: XÁC THỰC, TÀI KHOẢN, SỔ ĐỊA CHỈ, GIAN HÀNG & VẬN CHUYỂN (NGUYỄN ĐẶNG CAO TƯỜNG)!';
GO


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


-- ====================================================================
-- THE4BOOKSTORE - PROJECT HỆ QUẢN TRỊ CƠ SỞ DỮ LIỆU (DBMS330284)
-- PHẦN 3: ĐẶT HÀNG, KHUYẾN MÃI FREESHIP & GIAO DỊCH CHECKOUT
-- SINH VIÊN PHỤ TRÁCH: NGUYỄN NGỌC THIỆN (MSSV: 24110335)
-- HỆ QUẢN TRỊ CSDL: MICROSOFT SQL SERVER 2022/2025 (T-SQL)
-- ====================================================================

USE [QL_NhaSach];
GO

-- ====================================================================
-- 1. BẢNG DỮ LIỆU & RÀNG BUỘC (TABLES & CONSTRAINTS)
-- ====================================================================

-- Bảng 15: khuyen_mai (Mã giảm giá: Đơn hàng, Freeship, Toàn sàn hoặc Gian hàng)
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[khuyen_mai](
	[makm] [int] IDENTITY(1,1) NOT NULL,
	[ma_code] [varchar](50) COLLATE Vietnamese_CI_AS NOT NULL,
	[tenkm] [nvarchar](150) COLLATE Vietnamese_CI_AS NOT NULL,
	[mo_ta] [nvarchar](500) COLLATE Vietnamese_CI_AS NULL,
	[loai_giam] [nvarchar](30) COLLATE Vietnamese_CI_AS NOT NULL,
	[gia_tri_giam] [int] NOT NULL,
	[giam_toi_da] [int] NULL,
	[don_toi_thieu] [int] NOT NULL,
	[so_luong_toi_da] [int] NULL,
	[so_luong_da_dung] [int] NOT NULL,
	[pham_vi] [nvarchar](30) COLLATE Vietnamese_CI_AS NOT NULL,
	[ma_shop] [int] NULL,
	[ngay_bat_dau] [datetime2](7) NOT NULL,
	[ngay_ket_thuc] [datetime2](7) NOT NULL,
	[trang_thai] [nvarchar](30) COLLATE Vietnamese_CI_AS NOT NULL,
	[hien_thi_cong_khai] [bit] NOT NULL,
	[gioi_han_moi_khach_hang] [int] NOT NULL,
	[doi_tuong_khach_hang] [nvarchar](30) COLLATE Vietnamese_CI_AS NOT NULL,
	[loai_khuyen_mai] [nvarchar](30) COLLATE Vietnamese_CI_AS NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[makm] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_khuyenmai_code] UNIQUE NONCLUSTERED 
(
	[ma_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[khuyen_mai] ADD CONSTRAINT [DF_km_dontoithieu] DEFAULT ((0)) FOR [don_toi_thieu];
GO
ALTER TABLE [dbo].[khuyen_mai] ADD CONSTRAINT [DF_km_soluongdadung] DEFAULT ((0)) FOR [so_luong_da_dung];
GO
ALTER TABLE [dbo].[khuyen_mai] ADD CONSTRAINT [DF_km_phamvi] DEFAULT (N'TOAN_SAN') FOR [pham_vi];
GO
ALTER TABLE [dbo].[khuyen_mai] ADD CONSTRAINT [DF_km_trangthai] DEFAULT (N'HoatDong') FOR [trang_thai];
GO
ALTER TABLE [dbo].[khuyen_mai] ADD CONSTRAINT [DF_km_hienthicongkhai] DEFAULT ((1)) FOR [hien_thi_cong_khai];
GO
ALTER TABLE [dbo].[khuyen_mai] ADD CONSTRAINT [DF_km_gioihan] DEFAULT ((1)) FOR [gioi_han_moi_khach_hang];
GO
ALTER TABLE [dbo].[khuyen_mai] ADD CONSTRAINT [DF_km_doituong] DEFAULT (N'ALL') FOR [doi_tuong_khach_hang];
GO
ALTER TABLE [dbo].[khuyen_mai] ADD CONSTRAINT [DF_km_loaikm] DEFAULT (N'GIAM_GIA_SAN_PHAM') FOR [loai_khuyen_mai];
GO
ALTER TABLE [dbo].[khuyen_mai] WITH CHECK ADD CONSTRAINT [FK_khuyen_mai_shop] FOREIGN KEY([ma_shop])
REFERENCES [dbo].[shop] ([ma_shop]);
GO
ALTER TABLE [dbo].[khuyen_mai] CHECK CONSTRAINT [FK_khuyen_mai_shop];
GO
ALTER TABLE [dbo].[khuyen_mai] WITH CHECK ADD CONSTRAINT [CK_km_giatri] CHECK (([gia_tri_giam]>(0)));
GO
ALTER TABLE [dbo].[khuyen_mai] CHECK CONSTRAINT [CK_km_giatri];
GO
ALTER TABLE [dbo].[khuyen_mai] WITH CHECK ADD CONSTRAINT [CK_km_ngay] CHECK (([ngay_ket_thuc]>=[ngay_bat_dau]));
GO
ALTER TABLE [dbo].[khuyen_mai] CHECK CONSTRAINT [CK_km_ngay];
GO

-- Bảng 16: voucher_da_luu (Ví voucher cá nhân của khách hàng)
CREATE TABLE [dbo].[voucher_da_luu](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[makh] [int] NOT NULL,
	[makm] [int] NOT NULL,
	[trang_thai] [nvarchar](30) COLLATE Vietnamese_CI_AS NOT NULL,
	[ngay_luu] [datetime2](7) NOT NULL,
	[ngay_su_dung] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_voucherdaluu_makh_makm] UNIQUE NONCLUSTERED 
(
	[makh] ASC,
	[makm] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[voucher_da_luu] ADD CONSTRAINT [DF_voucherdaluu_trangthai] DEFAULT (N'ChuaDung') FOR [trang_thai];
GO
ALTER TABLE [dbo].[voucher_da_luu] ADD CONSTRAINT [DF_voucherdaluu_ngayluu] DEFAULT (sysutcdatetime()) FOR [ngay_luu];
GO
ALTER TABLE [dbo].[voucher_da_luu] WITH CHECK ADD CONSTRAINT [FK_vdl_khach_hang] FOREIGN KEY([makh])
REFERENCES [dbo].[khach_hang] ([makh]);
GO
ALTER TABLE [dbo].[voucher_da_luu] CHECK CONSTRAINT [FK_vdl_khach_hang];
GO
ALTER TABLE [dbo].[voucher_da_luu] WITH CHECK ADD CONSTRAINT [FK_vdl_khuyen_mai] FOREIGN KEY([makm])
REFERENCES [dbo].[khuyen_mai] ([makm]);
GO
ALTER TABLE [dbo].[voucher_da_luu] CHECK CONSTRAINT [FK_vdl_khuyen_mai];
GO

-- Bảng 17: don_hang (Đơn đặt hàng, hỗ trợ phân tách Voucher Đơn hàng & Freeship)
CREATE TABLE [dbo].[don_hang](
	[madh] [int] IDENTITY(1,1) NOT NULL,
	[makh] [int] NOT NULL,
	[ma_shop] [int] NULL,
	[ma_nvc] [int] NULL,
	[makm] [int] NULL,
	[ma_km_freeship] [int] NULL,
	[dia_chi_giao] [nvarchar](255) COLLATE Vietnamese_CI_AS NOT NULL,
	[so_dien_thoai_giao] [varchar](20) COLLATE Vietnamese_CI_AS NOT NULL,
	[tong_tien] [int] NOT NULL,
	[phi_van_chuyen] [int] NOT NULL,
	[tien_giam] [int] NOT NULL,
	[tien_giam_freeship] [int] NOT NULL,
	[chiet_khau_app_phan_tram] [decimal](5, 2) NOT NULL,
	[tien_phi_san] [int] NOT NULL,
	[tien_thuc_nhan_shop] [int] NOT NULL,
	[phuong_thuc_thanh_toan] [nvarchar](30) COLLATE Vietnamese_CI_AS NOT NULL,
	[trang_thai_thanh_toan] [nvarchar](30) COLLATE Vietnamese_CI_AS NOT NULL,
	[trang_thai] [nvarchar](30) COLLATE Vietnamese_CI_AS NOT NULL,
	[ma_van_don] [varchar](50) COLLATE Vietnamese_CI_AS NULL,
	[ly_do_huy] [nvarchar](255) COLLATE Vietnamese_CI_AS NULL,
	[ly_do_tu_choi] [nvarchar](255) COLLATE Vietnamese_CI_AS NULL,
	[ly_do_tra_hang] [nvarchar](500) COLLATE Vietnamese_CI_AS NULL,
	[phan_quyet_tranh_chap] [nvarchar](500) COLLATE Vietnamese_CI_AS NULL,
	[nguoi_xu_ly_tranh_chap] [int] NULL,
	[ngay_dat] [datetime2](7) NOT NULL,
	[ngay_xac_nhan] [datetime2](7) NULL,
	[ngay_hoan_thanh] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[madh] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[don_hang] ADD CONSTRAINT [DF_donhang_phiship] DEFAULT ((0)) FOR [phi_van_chuyen];
GO
ALTER TABLE [dbo].[don_hang] ADD CONSTRAINT [DF_donhang_tiengiam] DEFAULT ((0)) FOR [tien_giam];
GO
ALTER TABLE [dbo].[don_hang] ADD CONSTRAINT [DF_donhang_tiengiamfreeship] DEFAULT ((0)) FOR [tien_giam_freeship];
GO
ALTER TABLE [dbo].[don_hang] ADD CONSTRAINT [DF_donhang_chietkhau] DEFAULT ((5.00)) FOR [chiet_khau_app_phan_tram];
GO
ALTER TABLE [dbo].[don_hang] ADD CONSTRAINT [DF_donhang_phisan] DEFAULT ((0)) FOR [tien_phi_san];
GO
ALTER TABLE [dbo].[don_hang] ADD CONSTRAINT [DF_donhang_thucnhanshop] DEFAULT ((0)) FOR [tien_thuc_nhan_shop];
GO
ALTER TABLE [dbo].[don_hang] ADD CONSTRAINT [DF_donhang_pttt] DEFAULT (N'COD') FOR [phuong_thuc_thanh_toan];
GO
ALTER TABLE [dbo].[don_hang] ADD CONSTRAINT [DF_donhang_tttt] DEFAULT (N'ChuaThanhToan') FOR [trang_thai_thanh_toan];
GO
ALTER TABLE [dbo].[don_hang] ADD CONSTRAINT [DF_donhang_trangthai] DEFAULT (N'ChoXuLy') FOR [trang_thai];
GO
ALTER TABLE [dbo].[don_hang] ADD CONSTRAINT [DF_donhang_ngaydat] DEFAULT (sysutcdatetime()) FOR [ngay_dat];
GO
ALTER TABLE [dbo].[don_hang] WITH CHECK ADD CONSTRAINT [FK_don_hang_khach_hang] FOREIGN KEY([makh])
REFERENCES [dbo].[khach_hang] ([makh]);
GO
ALTER TABLE [dbo].[don_hang] CHECK CONSTRAINT [FK_don_hang_khach_hang];
GO
ALTER TABLE [dbo].[don_hang] WITH CHECK ADD CONSTRAINT [FK_don_hang_khuyen_mai] FOREIGN KEY([makm])
REFERENCES [dbo].[khuyen_mai] ([makm]);
GO
ALTER TABLE [dbo].[don_hang] CHECK CONSTRAINT [FK_don_hang_khuyen_mai];
GO
ALTER TABLE [dbo].[don_hang] WITH CHECK ADD CONSTRAINT [FK_don_hang_km_freeship] FOREIGN KEY([ma_km_freeship])
REFERENCES [dbo].[khuyen_mai] ([makm]);
GO
ALTER TABLE [dbo].[don_hang] CHECK CONSTRAINT [FK_don_hang_km_freeship];
GO
ALTER TABLE [dbo].[don_hang] WITH CHECK ADD CONSTRAINT [FK_don_hang_nha_van_chuyen] FOREIGN KEY([ma_nvc])
REFERENCES [dbo].[nha_van_chuyen] ([ma_nvc]);
GO
ALTER TABLE [dbo].[don_hang] CHECK CONSTRAINT [FK_don_hang_nha_van_chuyen];
GO
ALTER TABLE [dbo].[don_hang] WITH CHECK ADD CONSTRAINT [FK_don_hang_shop] FOREIGN KEY([ma_shop])
REFERENCES [dbo].[shop] ([ma_shop]);
GO
ALTER TABLE [dbo].[don_hang] CHECK CONSTRAINT [FK_don_hang_shop];
GO
ALTER TABLE [dbo].[don_hang] WITH CHECK ADD CONSTRAINT [FK_don_hang_tai_khoan_tranh_chap] FOREIGN KEY([nguoi_xu_ly_tranh_chap])
REFERENCES [dbo].[tai_khoan] ([ma_tai_khoan]);
GO
ALTER TABLE [dbo].[don_hang] CHECK CONSTRAINT [FK_don_hang_tai_khoan_tranh_chap];
GO

-- Bảng 18: chi_tiet_don_hang (Sản phẩm trong từng đơn hàng)
CREATE TABLE [dbo].[chi_tiet_don_hang](
	[madh] [int] NOT NULL,
	[masp] [int] NOT NULL,
	[don_gia] [int] NOT NULL,
	[so_luong] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[madh] ASC,
	[masp] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[chi_tiet_don_hang] WITH CHECK ADD CONSTRAINT [FK_ctdh_don_hang] FOREIGN KEY([madh])
REFERENCES [dbo].[don_hang] ([madh])
ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[chi_tiet_don_hang] CHECK CONSTRAINT [FK_ctdh_don_hang];
GO
ALTER TABLE [dbo].[chi_tiet_don_hang] WITH CHECK ADD CONSTRAINT [FK_ctdh_san_pham] FOREIGN KEY([masp])
REFERENCES [dbo].[san_pham] ([masp]);
GO
ALTER TABLE [dbo].[chi_tiet_don_hang] CHECK CONSTRAINT [FK_ctdh_san_pham];
GO
ALTER TABLE [dbo].[chi_tiet_don_hang] WITH CHECK ADD CONSTRAINT [CK_ctdh_dongia] CHECK (([don_gia]>=(0)));
GO
ALTER TABLE [dbo].[chi_tiet_don_hang] CHECK CONSTRAINT [CK_ctdh_dongia];
GO
ALTER TABLE [dbo].[chi_tiet_don_hang] WITH CHECK ADD CONSTRAINT [CK_ctdh_soluong] CHECK (([so_luong]>(0)));
GO
ALTER TABLE [dbo].[chi_tiet_don_hang] CHECK CONSTRAINT [CK_ctdh_soluong];
GO

-- ====================================================================
-- 2. TỐI ƯU HIỆU NĂNG VỚI NONCLUSTERED INDEXES
-- ====================================================================

CREATE NONCLUSTERED INDEX [idx_khuyenmai_code] ON [dbo].[khuyen_mai]([ma_code] ASC);
GO
CREATE NONCLUSTERED INDEX [idx_donhang_makh_ngaydat] ON [dbo].[don_hang]([makh] ASC, [ngay_dat] DESC);
GO
CREATE NONCLUSTERED INDEX [idx_donhang_shop_trangthai] ON [dbo].[don_hang]([ma_shop] ASC, [trang_thai] ASC);
GO

-- ====================================================================
-- 3. HÀM TÍNH TOÁN NGHIỆP VỤ (USER-DEFINED FUNCTIONS)
-- ====================================================================

-- Hàm 1: Kiểm tra tính hợp lệ của mã Voucher
CREATE OR ALTER FUNCTION dbo.fn_KiemTraVoucherHopLe (
    @ma_code VARCHAR(50),
    @tong_tien_don INT,
    @ma_shop INT = NULL
)
RETURNS BIT
AS
BEGIN
    DECLARE @hop_le BIT = 0;

    IF EXISTS (
        SELECT 1 
        FROM khuyen_mai 
        WHERE ma_code = @ma_code 
          AND trang_thai = N'HoatDong'
          AND SYSUTCDATETIME() BETWEEN ngay_bat_dau AND ngay_ket_thuc
          AND @tong_tien_don >= don_toi_thieu
          AND (so_luong_toi_da IS NULL OR so_luong_da_dung < so_luong_toi_da)
          AND (pham_vi = N'TOAN_SAN' OR (pham_vi = N'SHOP' AND ma_shop = @ma_shop))
    )
    BEGIN
        SET @hop_le = 1;
    END

    RETURN @hop_le;
END;
GO

-- Hàm 2: Tính số tiền được giảm từ Voucher (Hỗ trợ PhanTram, TienCoDinh & Freeship)
CREATE OR ALTER FUNCTION dbo.fn_TinhTienGiamVoucher (
    @ma_code VARCHAR(50),
    @tong_tien_hang INT
)
RETURNS INT
AS
BEGIN
    DECLARE @tien_giam INT = 0;
    DECLARE @loai_giam NVARCHAR(30);
    DECLARE @gia_tri_giam INT;
    DECLARE @giam_toi_da INT;

    SELECT 
        @loai_giam = loai_giam,
        @gia_tri_giam = gia_tri_giam,
        @giam_toi_da = giam_toi_da
    FROM khuyen_mai
    WHERE ma_code = @ma_code AND trang_thai = N'HoatDong';

    IF @loai_giam = N'TienCoDinh' OR @loai_giam = N'Freeship'
    BEGIN
        SET @tien_giam = @gia_tri_giam;
    END
    ELSE IF @loai_giam = N'PhanTram'
    BEGIN
        SET @tien_giam = (@tong_tien_hang * @gia_tri_giam) / 100;
        IF @giam_toi_da IS NOT NULL AND @tien_giam > @giam_toi_da
            SET @tien_giam = @giam_toi_da;
    END

    IF @tien_giam > @tong_tien_hang
        SET @tien_giam = @tong_tien_hang;

    RETURN ISNULL(@tien_giam, 0);
END;
GO

-- ====================================================================
-- 4. TRIGGERS NGHIỆP VỤ BÁN HÀNG & TỒN KHO
-- ====================================================================

-- Trigger 1: Tự động kiểm tra và trừ tồn kho khi chi tiết đơn hàng được thêm vào
CREATE OR ALTER TRIGGER trg_ChiTietDonHang_TruTonKho
ON chi_tiet_don_hang
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (
        SELECT 1
        FROM san_pham sp
        JOIN inserted i ON sp.masp = i.masp
        WHERE sp.so_luong_ton < i.so_luong
    )
    BEGIN
        RAISERROR (N'Lỗi: Số lượng tồn kho không đủ để đáp ứng đơn hàng!', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END

    UPDATE sp
    SET 
        sp.so_luong_ton = sp.so_luong_ton - i.so_luong,
        sp.so_luong_da_ban = sp.so_luong_da_ban + i.so_luong
    FROM san_pham sp
    JOIN inserted i ON sp.masp = i.masp;
END;
GO

-- Trigger 2: Tự động hoàn lại số lượng tồn kho khi đơn hàng bị HỦY (DaHuy)
CREATE OR ALTER TRIGGER trg_DonHang_HoanTonKhoKhiHuy
ON don_hang
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    IF UPDATE(trang_thai)
    BEGIN
        IF EXISTS (
            SELECT 1 
            FROM inserted i 
            JOIN deleted d ON i.madh = d.madh 
            WHERE i.trang_thai = N'DaHuy' AND d.trang_thai <> N'DaHuy'
        )
        BEGIN
            UPDATE sp
            SET 
                sp.so_luong_ton = sp.so_luong_ton + ctdh.so_luong,
                sp.so_luong_da_ban = CASE 
                    WHEN sp.so_luong_da_ban >= ctdh.so_luong THEN sp.so_luong_da_ban - ctdh.so_luong 
                    ELSE 0 
                END
            FROM san_pham sp
            JOIN chi_tiet_don_hang ctdh ON sp.masp = ctdh.masp
            JOIN inserted i ON ctdh.madh = i.madh
            JOIN deleted d ON i.madh = d.madh
            WHERE i.trang_thai = N'DaHuy' AND d.trang_thai <> N'DaHuy';
        END
    END
END;
GO

-- ====================================================================
-- 5. THỦ TỤC LƯU TRỮ (STORED PROCEDURES CÓ TRANSACTION & TRY...CATCH)
-- ====================================================================

-- Thủ tục 1: Đặt hàng hoàn tất (Hỗ trợ phân tách Voucher đơn & Freeship + Giao dịch ACID)
CREATE OR ALTER PROCEDURE sp_DatHangVaTruTonKho
    @makh INT,
    @ma_shop INT,
    @ma_nvc INT,
    @makm INT = NULL,
    @ma_km_freeship INT = NULL,
    @dia_chi_giao NVARCHAR(255),
    @so_dien_thoai_giao VARCHAR(20),
    @tong_tien INT,
    @phi_van_chuyen INT = 0,
    @tien_giam INT = 0,
    @tien_giam_freeship INT = 0,
    @phuong_thuc NVARCHAR(30) = N'COD',
    @new_madh INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;

        DECLARE @chiet_khau DECIMAL(5, 2) = 5.00;
        SELECT @chiet_khau = chiet_khau_phan_tram FROM shop WHERE ma_shop = @ma_shop;
        IF @chiet_khau IS NULL SET @chiet_khau = 5.00;

        INSERT INTO don_hang (
            makh, ma_shop, ma_nvc, makm, ma_km_freeship, dia_chi_giao, so_dien_thoai_giao,
            tong_tien, phi_van_chuyen, tien_giam, tien_giam_freeship, chiet_khau_app_phan_tram,
            phuong_thuc_thanh_toan, trang_thai
        )
        VALUES (
            @makh, @ma_shop, @ma_nvc, @makm, @ma_km_freeship, @dia_chi_giao, @so_dien_thoai_giao,
            @tong_tien, @phi_van_chuyen, @tien_giam, @tien_giam_freeship, @chiet_khau,
            @phuong_thuc, N'ChoXacNhan'
        );

        SET @new_madh = SCOPE_IDENTITY();

        -- Cập nhật lượt dùng của voucher đơn hàng nếu có
        IF @makm IS NOT NULL
        BEGIN
            UPDATE khuyen_mai 
            SET so_luong_da_dung = so_luong_da_dung + 1 
            WHERE makm = @makm;
        END

        -- Cập nhật lượt dùng của voucher freeship nếu có
        IF @ma_km_freeship IS NOT NULL
        BEGIN
            UPDATE khuyen_mai 
            SET so_luong_da_dung = so_luong_da_dung + 1 
            WHERE makm = @ma_km_freeship;
        END

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        DECLARE @ErrMsg NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR (@ErrMsg, 16, 1);
    END CATCH
END;
GO

-- Thủ tục 2: Hủy đơn hàng và kích hoạt trigger hoàn trả tồn kho
CREATE OR ALTER PROCEDURE sp_HuyDonHang
    @madh INT,
    @lyDo NVARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;

        DECLARE @trang_thai_hien_tai NVARCHAR(30);
        SELECT @trang_thai_hien_tai = trang_thai FROM don_hang WHERE madh = @madh;

        IF @trang_thai_hien_tai IS NULL
        BEGIN
            RAISERROR (N'Đơn hàng không tồn tại!', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        IF @trang_thai_hien_tai IN (N'DangGiao', N'DaGiao', N'HoanTat')
        BEGIN
            RAISERROR (N'Đơn hàng đang giao hoặc đã hoàn tất, không thể hủy!', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        UPDATE don_hang
        SET trang_thai = N'DaHuy', ly_do_huy = @lyDo
        WHERE madh = @madh;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        DECLARE @ErrMsg NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR (@ErrMsg, 16, 1);
    END CATCH
END;
GO

-- Thủ tục 3: Gian hàng xác nhận đơn hàng và gắn mã vận đơn
CREATE OR ALTER PROCEDURE sp_ShopXacNhanDonHang 
    @madh INT, 
    @maShop INT, 
    @maVanDon VARCHAR(50) 
AS 
BEGIN 
    SET NOCOUNT ON; 
    BEGIN TRY 
        BEGIN TRANSACTION; 

        IF NOT EXISTS (SELECT 1 FROM don_hang WHERE madh = @madh AND ma_shop = @maShop AND trang_thai IN (N'ChoXacNhan', N'ChoXuLy')) 
        BEGIN 
            RAISERROR (N'Đơn hàng không hợp lệ hoặc không ở trạng thái Chờ xác nhận!', 16, 1); 
            ROLLBACK TRANSACTION; 
            RETURN; 
        END 

        UPDATE don_hang 
        SET trang_thai = N'DangGiao', ngay_xac_nhan = SYSUTCDATETIME(), ma_van_don = @maVanDon 
        WHERE madh = @madh; 

        COMMIT TRANSACTION; 
    END TRY 
    BEGIN CATCH 
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION; 
        DECLARE @ErrMsg NVARCHAR(4000) = ERROR_MESSAGE(); 
        RAISERROR (@ErrMsg, 16, 1); 
    END CATCH 
END;
GO

-- ====================================================================
-- 6. PHÂN QUYỀN TRÊN CÁC ĐỐI TƯỢNG MODULE 3
-- ====================================================================

GRANT SELECT, INSERT ON don_hang TO Role_KhachHang;
GRANT SELECT, INSERT ON chi_tiet_don_hang TO Role_KhachHang;
GRANT SELECT, INSERT, UPDATE ON voucher_da_luu TO Role_KhachHang;
GRANT SELECT ON khuyen_mai TO Role_KhachHang;
GRANT EXECUTE ON sp_DatHangVaTruTonKho TO Role_KhachHang;
GRANT EXECUTE ON sp_HuyDonHang TO Role_KhachHang;
GRANT EXECUTE ON fn_KiemTraVoucherHopLe TO Role_KhachHang;
GRANT EXECUTE ON fn_TinhTienGiamVoucher TO Role_KhachHang;

GRANT SELECT, UPDATE ON don_hang TO Role_Vendor;
GRANT SELECT ON chi_tiet_don_hang TO Role_Vendor;
GRANT SELECT, INSERT, UPDATE, DELETE ON khuyen_mai TO Role_Vendor;
GRANT EXECUTE ON sp_ShopXacNhanDonHang TO Role_Vendor;

GRANT SELECT, UPDATE ON don_hang TO Role_Manager;

DENY DELETE ON don_hang TO Role_Vendor;
GO

-- ====================================================================
-- 7. DỮ LIỆU MẪU (SEED DATA: PROMOTIONS & ORDERS)
-- ====================================================================

SET IDENTITY_INSERT [dbo].[khuyen_mai] ON;
INSERT [dbo].[khuyen_mai] ([makm], [ma_code], [tenkm], [mo_ta], [loai_giam], [gia_tri_giam], [giam_toi_da], [don_toi_thieu], [so_luong_toi_da], [so_luong_da_dung], [pham_vi], [ma_shop], [ngay_bat_dau], [ngay_ket_thuc], [trang_thai], [hien_thi_cong_khai], [gioi_han_moi_khach_hang], [doi_tuong_khach_hang], [loai_khuyen_mai]) 
VALUES 
(1, N'TOANSAN10', N'Giảm 10% Toàn Sàn', N'Áp dụng cho mọi đơn hàng từ 200.000đ trở lên', N'PhanTram', 10, 50000, 200000, NULL, 0, N'TOAN_SAN', NULL, CAST(N'2025-01-01T00:00:00' AS DateTime2), CAST(N'2027-12-31T00:00:00' AS DateTime2), N'HoatDong', 1, 1, N'ALL', N'GIAM_GIA_SAN_PHAM'),
(2, N'FREESHIP', N'Miễn Phí Vận Chuyển', N'Giảm 25.000đ phí giao hàng cho đơn từ 150.000đ', N'TienCoDinh', 25000, 25000, 150000, NULL, 0, N'TOAN_SAN', NULL, CAST(N'2025-01-01T00:00:00' AS DateTime2), CAST(N'2027-12-31T00:00:00' AS DateTime2), N'HoatDong', 1, 1, N'ALL', N'MIEN_PHI_VAN_CHUYEN'),
(3, N'KIMDONG20K', N'Ưu Đãi Tri Ân Kim Đồng', N'Giảm ngay 20.000đ cho đơn hàng truyện tranh Kim Đồng từ 100.000đ', N'TienCoDinh', 20000, 20000, 100000, NULL, 0, N'SHOP', 1, CAST(N'2025-01-01T00:00:00' AS DateTime2), CAST(N'2027-12-31T00:00:00' AS DateTime2), N'HoatDong', 1, 1, N'ALL', N'GIAM_GIA_SAN_PHAM'),
(4, N'NHANAM15', N'Đặc Quyền Hội Sách Nhã Nam', N'Giảm 15% tối đa 40.000đ cho các đầu sách văn học Nhã Nam', N'PhanTram', 15, 60000, 250000, NULL, 0, N'SHOP', 2, CAST(N'2025-01-01T00:00:00' AS DateTime2), CAST(N'2027-12-31T00:00:00' AS DateTime2), N'HoatDong', 1, 1, N'ALL', N'GIAM_GIA_SAN_PHAM');
SET IDENTITY_INSERT [dbo].[khuyen_mai] OFF;
GO

SET IDENTITY_INSERT [dbo].[voucher_da_luu] ON;
INSERT [dbo].[voucher_da_luu] ([id], [makh], [makm], [trang_thai], [ngay_luu], [ngay_su_dung]) 
VALUES 
(1, 3, 1, N'ChuaDung', CAST(N'2026-10-17T11:07:01' AS DateTime2), NULL),
(2, 3, 2, N'ChuaDung', CAST(N'2026-10-17T11:07:03' AS DateTime2), NULL);
SET IDENTITY_INSERT [dbo].[voucher_da_luu] OFF;
GO

SET IDENTITY_INSERT [dbo].[don_hang] ON;
INSERT [dbo].[don_hang] ([madh], [makh], [ma_shop], [ma_nvc], [makm], [ma_km_freeship], [dia_chi_giao], [so_dien_thoai_giao], [tong_tien], [phi_van_chuyen], [tien_giam], [tien_giam_freeship], [chiet_khau_app_phan_tram], [tien_phi_san], [tien_thuc_nhan_shop], [phuong_thuc_thanh_toan], [trang_thai_thanh_toan], [trang_thai], [ma_van_don], [ly_do_huy], [ly_do_tu_choi], [ly_do_tra_hang], [phan_quyet_tranh_chap], [nguoi_xu_ly_tranh_chap], [ngay_dat], [ngay_xac_nhan], [ngay_hoan_thanh]) 
VALUES 
(1, 1, 1, 1, 3, NULL, N'123 Lê Lợi, P. Bến Nghé, Q.1, TP.HCM', N'0988111222', 155000, 22000, 20000, 0, CAST(5.00 AS Decimal(5, 2)), 7750, 147250, N'COD', N'ChuaThanhToan', N'DaGiao', N'GHTK8829102', NULL, NULL, NULL, NULL, NULL, CAST(N'2026-10-16T15:04:53' AS DateTime2), NULL, CAST(N'2026-10-16T16:00:00' AS DateTime2)),
(2, 2, 2, 2, 4, NULL, N'456 Võ Văn Ngân, TP. Thủ Đức, TP.HCM', N'0977333444', 350000, 25000, 40000, 0, CAST(5.00 AS Decimal(5, 2)), 17500, 332500, N'VIETQR', N'DaThanhToan', N'DaHuy', NULL, N'Khách đổi ý sang sách khác', NULL, NULL, NULL, NULL, CAST(N'2026-10-16T15:04:53' AS DateTime2), NULL, NULL),
(5, 1, 1, 1, NULL, NULL, N'1 Võ Văn Ngân, TP. Thủ Đức', N'0987654321', 220000, 22000, 0, 0, CAST(5.00 AS Decimal(5, 2)), 11000, 209000, N'COD', N'ChuaThanhToan', N'DangGiao', N'VNPOST_HN_005', NULL, NULL, NULL, NULL, NULL, CAST(N'2026-10-16T22:30:03' AS DateTime2), CAST(N'2026-10-16T15:31:18' AS DateTime2), NULL),
(6, 3, 1, 1, NULL, NULL, N'462 Hoàng Diệu 2, TP. Thủ Đức', N'0934567822', 195000, 22000, 0, 0, CAST(5.00 AS Decimal(5, 2)), 9750, 185250, N'COD', N'DaThanhToan', N'HoanTat', N'GHTK-6-62810', NULL, NULL, NULL, NULL, NULL, CAST(N'2026-10-17T11:08:49' AS DateTime2), CAST(N'2026-10-17T11:09:22' AS DateTime2), CAST(N'2026-10-17T11:19:28' AS DateTime2)),
(7, 1, 1, 1, NULL, NULL, N'1 Võ Văn Ngân, TP. Thủ Đức', N'0912345678', 480000, 20000, 0, 0, CAST(5.00 AS Decimal(5, 2)), 24000, 456000, N'COD', N'DaThanhToan', N'HoanTat', NULL, NULL, NULL, NULL, NULL, NULL, CAST(N'2026-08-15T10:00:00' AS DateTime2), NULL, CAST(N'2026-08-18T14:00:00' AS DateTime2)),
(8, 2, 2, 2, NULL, NULL, N'Ký túc xá khu B ĐHQG, Dĩ An', N'0987654321', 620000, 25000, 0, 0, CAST(5.00 AS Decimal(5, 2)), 31000, 589000, N'VNPAY', N'DaThanhToan', N'HoanTat', NULL, NULL, NULL, NULL, NULL, NULL, CAST(N'2026-09-10T11:30:00' AS DateTime2), NULL, CAST(N'2026-09-13T16:00:00' AS DateTime2)),
(9, 3, 2, 1, NULL, NULL, N'462 Hoàng Diệu 2, TP. Thủ Đức', N'0934567822', 89000, 22000, 0, 0, CAST(5.00 AS Decimal(5, 2)), 4450, 84550, N'COD', N'DaThanhToan', N'DaGiao', N'GHTK-9-65979', NULL, NULL, NULL, NULL, NULL, CAST(N'2026-10-17T11:55:04' AS DateTime2), CAST(N'2026-10-17T11:56:05' AS DateTime2), NULL);
SET IDENTITY_INSERT [dbo].[don_hang] OFF;
GO

INSERT [dbo].[chi_tiet_don_hang] ([madh], [masp], [don_gia], [so_luong]) VALUES 
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
GO

PRINT N'>>> HOÀN THÀNH PHẦN 3: ĐẶT HÀNG & KHUYẾN MÃI (NGUYỄN NGỌC THIỆN)!';
GO


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
