# CẨM NANG ÔN TẬP & THI VẤN ĐÁP - VÕ THÀNH PHÁT (MSSV: 24110300)
> **Môn học**: Hệ Quản Trị Cơ Sở Dữ Liệu (DBMS330284) – HCMUTE  
> **Phân hệ**: SẢN PHẨM, DANH MỤC, TÁC GIẢ, GIỎ HÀNG & CHỈ MỤC CATALOG  
> **File code học tập**: [`02_ThanhPhat_DeHieu_DeViet.sql`](file:///d:/HCMUTE/N3_HK1_Dot%201/He%20Quan%20tri%20CSDL/THE4_DBMS/DOCUMENT/scripts/02_ThanhPhat_DeHieu_DeViet.sql)  
> **File code nộp bài chính thức**: [`02_ThanhPhat_Product_Catalog.sql`](file:///d:/HCMUTE/N3_HK1_Dot%201/He%20Quan%20tri%20CSDL/THE4_DBMS/DOCUMENT/scripts/02_ThanhPhat_Product_Catalog.sql)

---

## 1. BẢN ĐỒ TƯ DUY 8 BẢNG (THỨ TỰ PHỤ THUỘC CHA -> CON)

Để không bao giờ bị lỗi khóa ngoại khi tạo bảng hoặc khi vấn đáp, hãy nhớ thứ tự tạo 8 bảng như sau:

```
[Bảng Độc Lập - Tạo trước]
  ├── 1. danh_muc (ma_danh_muc, ten_danh_muc, ...)
  └── 2. tac_gia (ma_tac_gia, ten_tac_gia, ...)

[Bảng Sản Phẩm - Phụ thuộc shop & danh_muc]
  └── 3. san_pham (masp, ma_shop -> shop, ma_danh_muc -> danh_muc, gia_ban, so_luong_ton, ...)

[Bảng Trung Gian N - N & Tương Tác - Phụ thuộc san_pham]
  ├── 4. san_pham_tac_gia (masp, ma_tac_gia)
  ├── 5. san_pham_yeu_thich (makh, masp)
  └── 6. san_pham_da_xem (makh, masp, ngay_xem)

[Phân Hệ Giỏ Hàng Trực Tuyến]
  ├── 7. gio_hang (ma_gio_hang, makh -> khach_hang)
  └── 8. chi_tiet_gio_hang (ma_gio_hang -> gio_hang, masp -> san_pham, so_luong)
```

---

## 2. CÔNG THỨC VIẾT NHANH 8 BẢNG TRÊN GIẤY / BẢNG TRẮNG

> **Mẹo vàng**: Viết ràng buộc trực tiếp (Inline Constraints) ngay sau tên kiểu dữ liệu, không cần dùng `ALTER TABLE ADD CONSTRAINT`.

```sql
-- 1. Danh mục
CREATE TABLE danh_muc (
    ma_danh_muc INT IDENTITY(1,1) PRIMARY KEY,
    ten_danh_muc NVARCHAR(100) NOT NULL UNIQUE
);

-- 2. Tác giả
CREATE TABLE tac_gia (
    ma_tac_gia INT IDENTITY(1,1) PRIMARY KEY,
    ten_tac_gia NVARCHAR(100) NOT NULL
);

-- 3. Sản phẩm sách (Lưu ý 2 ràng buộc CHECK: giá bán >= 0 và tồn kho >= 0)
CREATE TABLE san_pham (
    masp INT IDENTITY(1,1) PRIMARY KEY,
    ma_shop INT NOT NULL REFERENCES shop(ma_shop),
    ma_danh_muc INT NOT NULL REFERENCES danh_muc(ma_danh_muc),
    tensp NVARCHAR(255) NOT NULL,
    gia_ban INT NOT NULL CHECK (gia_ban >= 0),
    so_luong_ton INT NOT NULL DEFAULT 0 CHECK (so_luong_ton >= 0),
    so_luong_da_ban INT NOT NULL DEFAULT 0,
    trang_thai NVARCHAR(30) NOT NULL DEFAULT N'DangBan'
);

-- 4. Bảng N-N Sách & Tác giả (Khóa chính kép)
CREATE TABLE san_pham_tac_gia (
    masp INT NOT NULL REFERENCES san_pham(masp) ON DELETE CASCADE,
    ma_tac_gia INT NOT NULL REFERENCES tac_gia(ma_tac_gia) ON DELETE CASCADE,
    PRIMARY KEY (masp, ma_tac_gia)
);

-- 5 & 6. Yêu thích & Đã xem (Khóa chính kép + ON DELETE CASCADE)
CREATE TABLE san_pham_yeu_thich (
    makh INT NOT NULL REFERENCES khach_hang(makh) ON DELETE CASCADE,
    masp INT NOT NULL REFERENCES san_pham(masp) ON DELETE CASCADE,
    PRIMARY KEY (makh, masp)
);

CREATE TABLE san_pham_da_xem (
    makh INT NOT NULL REFERENCES khach_hang(makh) ON DELETE CASCADE,
    masp INT NOT NULL REFERENCES san_pham(masp) ON DELETE CASCADE,
    ngay_xem DATETIME2 DEFAULT SYSUTCDATETIME(),
    PRIMARY KEY (makh, masp)
);

-- 7 & 8. Giỏ hàng & Chi tiết giỏ
CREATE TABLE gio_hang (
    ma_gio_hang INT IDENTITY(1,1) PRIMARY KEY,
    makh INT NOT NULL UNIQUE REFERENCES khach_hang(makh)
);

CREATE TABLE chi_tiet_gio_hang (
    ma_gio_hang INT NOT NULL REFERENCES gio_hang(ma_gio_hang) ON DELETE CASCADE,
    masp INT NOT NULL REFERENCES san_pham(masp),
    so_luong INT NOT NULL DEFAULT 1 CHECK (so_luong > 0),
    PRIMARY KEY (ma_gio_hang, masp)
);
```

---

## 3. CÔNG THỨC VIẾT 3 STORED PROCEDURES KHI THI

### SP 1: Thêm sản phẩm vào giỏ hàng (`sp_ThemSanPhamVaoGioHang`)
**Thuật toán 4 bước**:
1. `IF NOT EXISTS (SELECT 1 FROM san_pham WHERE masp = @masp AND trang_thai = 'DangBan')` $\rightarrow$ Rollback & báo lỗi.
2. Tìm `ma_gio_hang` của khách, nếu chưa có thì `INSERT INTO gio_hang`.
3. Kiểm tra: `(Số lượng trong giỏ hiện tại + @so_luong_them) > Tồn kho` $\rightarrow$ Rollback & báo lỗi.
4. **UPSERT**: Nếu đã có trong giỏ thì `UPDATE so_luong = so_luong + @so_luong`, chưa có thì `INSERT INTO chi_tiet_gio_hang`.

```sql
CREATE OR ALTER PROCEDURE sp_ThemSanPhamVaoGioHang
    @makh INT, @masp INT, @so_luong INT = 1
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;

        -- Bước 1: Kiểm tra sản phẩm
        IF NOT EXISTS (SELECT 1 FROM san_pham WHERE masp = @masp AND trang_thai = N'DangBan')
        BEGIN
            RAISERROR(N'Sản phẩm không tồn tại hoặc đã ngừng kinh doanh!', 16, 1);
            ROLLBACK TRANSACTION; RETURN;
        END

        -- Bước 2: Lấy hoặc tạo giỏ
        DECLARE @ma_gio INT;
        SELECT @ma_gio = ma_gio_hang FROM gio_hang WHERE makh = @makh;
        IF @ma_gio IS NULL
        BEGIN
            INSERT INTO gio_hang (makh) VALUES (@makh);
            SET @ma_gio = SCOPE_IDENTITY();
        END

        -- Bước 3: Kiểm tra tồn kho
        DECLARE @ton_kho INT = (SELECT so_luong_ton FROM san_pham WHERE masp = @masp);
        DECLARE @sl_cu INT = ISNULL((SELECT so_luong FROM chi_tiet_gio_hang WHERE ma_gio_hang = @ma_gio AND masp = @masp), 0);
        IF (@sl_cu + @so_luong) > @ton_kho
        BEGIN
            RAISERROR(N'Vượt quá tồn kho!', 16, 1);
            ROLLBACK TRANSACTION; RETURN;
        END

        -- Bước 4: UPSERT
        IF @sl_cu > 0
            UPDATE chi_tiet_gio_hang SET so_luong = so_luong + @so_luong WHERE ma_gio_hang = @ma_gio AND masp = @masp;
        ELSE
            INSERT INTO chi_tiet_gio_hang (ma_gio_hang, masp, so_luong) VALUES (@ma_gio, @masp, @so_luong);

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        RAISERROR(ERROR_MESSAGE(), 16, 1);
    END CATCH
END;
```

### SP 2: Tìm kiếm phân trang (`sp_TimKiemSachPhanTrang`)
**Mẹo nhớ**: Dùng `OFFSET (@page_number - 1) * @page_size ROWS FETCH NEXT @page_size ROWS ONLY`.
```sql
CREATE OR ALTER PROCEDURE sp_TimKiemSachPhanTrang
    @tu_khoa NVARCHAR(255) = NULL,
    @page_number INT = 1,
    @page_size INT = 12,
    @tong_so_luong INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    -- 1. Đếm tổng
    SELECT @tong_so_luong = COUNT(*) FROM san_pham
    WHERE trang_thai = N'DangBan' AND (@tu_khoa IS NULL OR tensp LIKE N'%' + @tu_khoa + '%');

    -- 2. Phân trang
    SELECT sp.*, s.ten_shop, dm.ten_danh_muc
    FROM san_pham sp
    JOIN shop s ON sp.ma_shop = s.ma_shop
    JOIN danh_muc dm ON sp.ma_danh_muc = dm.ma_danh_muc
    WHERE sp.trang_thai = N'DangBan' AND (@tu_khoa IS NULL OR tensp LIKE N'%' + @tu_khoa + '%')
    ORDER BY sp.so_luong_da_ban DESC, sp.masp DESC
    OFFSET (@page_number - 1) * @page_size ROWS FETCH NEXT @page_size ROWS ONLY;
END;
```

### SP 3: Dọn dẹp lịch sử xem cũ (`sp_DonDepSanPhamDaXem`)
```sql
CREATE OR ALTER PROCEDURE sp_DonDepSanPhamDaXem @so_ngay INT = 30
AS
BEGIN
    DELETE FROM san_pham_da_xem
    WHERE ngay_xem < DATEADD(DAY, -@so_ngay, SYSUTCDATETIME());
    SELECT @@ROWCOUNT AS so_dong_da_xoa;
END;
```

---

## 4. HÀM UDF & KHUNG NHÌN VIEW

### Hàm UDF đếm sách của shop
```sql
CREATE OR ALTER FUNCTION dbo.fn_DemSachCuaShop (@ma_shop INT)
RETURNS INT
AS
BEGIN
    RETURN (SELECT COUNT(*) FROM san_pham WHERE ma_shop = @ma_shop AND trang_thai <> N'DaXoa');
END;
```

### View xem chi tiết giỏ hàng
```sql
CREATE OR ALTER VIEW dbo.vw_ChiTietGioHangKhachHang AS
SELECT 
    gh.ma_gio_hang, gh.makh, kh.ho_ten,
    sp.masp, sp.tensp, sp.gia_ban, ct.so_luong,
    (sp.gia_ban * ct.so_luong) AS thanh_tien,
    s.ten_shop
FROM gio_hang gh
JOIN khach_hang kh ON gh.makh = kh.makh
JOIN chi_tiet_gio_hang ct ON gh.ma_gio_hang = ct.ma_gio_hang
JOIN san_pham sp ON ct.masp = sp.masp
JOIN shop s ON sp.ma_shop = s.ma_shop;
```

---

## 5. GIẢI THÍCH 4 CHỈ MỤC (ĐIỂM CỘNG 10 ĐIỂM)

Khi thầy cô hỏi: *"Tại sao em tạo chỉ mục này và nó tăng tốc truy vấn nào?"*
1. **`idx_sp_shop_trangthai` (ma_shop, trang_thai)**: Tăng tốc lọc sản phẩm trong trang quản lý của từng Shop. Dùng `INCLUDE (tensp, gia_ban)` để thành **Covering Index** (không cần quay lại bảng gốc tìm dữ liệu).
2. **`idx_sp_danhmuc_trangthai` (ma_danh_muc, trang_thai)**: Tăng tốc hiển thị sách khi khách click vào từng thể loại trên Menu trang chủ.
3. **`idx_sp_giaban` (gia_ban) WHERE trang_thai = 'DangBan'**: Đây là **Filtered Index (Chỉ mục có điều kiện)**. Khách chỉ tìm sách đang bán, nên chỉ đánh chỉ mục trên sách `DangBan`, giúp chỉ mục nhỏ hơn, nhẹ RAM hơn rất nhiều so với đánh toàn bộ bảng.
4. **`idx_sp_ngaytao` (ngay_tao DESC)**: Tăng tốc khối "Sách mới phát hành" trên trang chủ.
