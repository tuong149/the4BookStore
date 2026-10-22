-- ====================================================================
-- THE4BOOKSTORE - PROJECT HỆ QUẢN TRỊ CƠ SỞ DỮ LIỆU (DBMS330284)
-- PHẦN 1: XÁC THỰC, TÀI KHOẢN, HỒ SƠ, GIAN HÀNG, VẬN CHUYỂN & BẢO MẬT RBAC
-- SINH VIÊN PHỤ TRÁCH: NGUYỄN ĐẶNG CAO TƯỜNG (MSSV: 24110375)
-- HỆ QUẢN TRỊ CSDL: MICROSOFT SQL SERVER 2022/2025 (T-SQL)
-- MẬT KHẨU MẶC ĐỊNH TẤT CẢ TÀI KHOẢN SEED DATA: Abc@123
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
