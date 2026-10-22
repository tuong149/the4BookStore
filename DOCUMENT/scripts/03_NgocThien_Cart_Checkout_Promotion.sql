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
