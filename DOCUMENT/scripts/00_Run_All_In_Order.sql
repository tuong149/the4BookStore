-- ====================================================================
-- THE4BOOKSTORE - PROJECT HỆ QUẢN TRỊ CƠ SỞ DỮ LIỆU (DBMS330284)
-- SCRIPT ĐIỀU PHỐI CHẠY TUẦN TỰ 4 PHẦN CSDL (MASTER RUNNER SCRIPT)
-- HƯỚNG DẪN: 
-- 1. Trong SSMS: Bật menu Query -> Chọn "SQLCMD Mode" -> Bấm F5 (Execute)
-- 2. Trong PowerShell / Terminal: Chạy với cờ -f 65001 để giữ nguyên vẹn font Tiếng Việt Unicode:
--    sqlcmd -f 65001 -S localhost -i "00_Run_All_In_Order.sql"
-- 3. Hoặc mở từng file theo đúng thứ tự từ 01 -> 02 -> 03 -> 04 để chạy
-- ====================================================================

PRINT N'====================================================================';
PRINT N'BẮT ĐẦU QUY TRÌNH THIẾT LẬP CƠ SỞ DỮ LIỆU SÀN SÁCH THE4BOOKSTORE';
PRINT N'====================================================================';
GO

PRINT N'[1/4] Đang thực thi Phần 1: Xác thực, Gian hàng, Hồ sơ & Bảo mật RBAC (Cao Tường)...';
:r 01_CaoTuong_Auth_Security.sql
GO

PRINT N'[2/4] Đang thực thi Phần 2: Sản phẩm, Danh mục, Giỏ hàng & Chỉ mục Catalog (Thành Phát)...';
:r 02_ThanhPhat_Product_Catalog.sql
GO

PRINT N'[3/4] Đang thực thi Phần 3: Đặt hàng, Khuyến mãi Freeship & Giao dịch Checkout (Ngọc Thiện)...';
:r 03_NgocThien_Cart_Checkout_Promotion.sql
GO

PRINT N'[4/4] Đang thực thi Phần 4: Quản lý Kho, Thanh toán & Báo cáo Dashboard (Quang Hòa)...';
:r 04_QuangHoa_Inventory_Reports_Dashboard.sql
GO

PRINT N'====================================================================';
PRINT N'CHÚC MỪNG! TOÀN BỘ CSDL QL_NHASACH ĐÃ ĐƯỢC THIẾT LẬP HOÀN TẤT!';
PRINT N'====================================================================';
GO
