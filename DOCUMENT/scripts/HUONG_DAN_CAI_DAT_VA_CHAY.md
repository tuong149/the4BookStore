# HƯỚNG DẪN THIẾT LẬP CƠ SỞ DỮ LIỆU & CHẠY ỨNG DỤNG THE4BOOKSTORE

Dự án cuối kỳ môn **Hệ Quản Trị Cơ Sở Dữ Liệu (DBMS330284)** - Trường Đại học Sư Phạm Kỹ Thuật TP.HCM (HCMUTE).  
Hệ quản trị CSDL: **Microsoft SQL Server 2022 / 2025** (T-SQL).

---

## 1. THÔNG TIN THÀNH VIÊN VÀ PHÂN BỔ TRÁCH NHIỆM

| STT | Họ và Tên | MSSV | Vai trò phụ trách | File script đảm nhiệm | Số dòng (Thực tế file) |
| :---: | :--- | :---: | :--- | :--- | :---: |
| 1 | **Nguyễn Đặng Cao Tường** | 24110375 | Xác thực, Tài khoản, Gian hàng Shop, Sổ địa chỉ, Vận chuyển & RBAC | `01_CaoTuong_Auth_Security.sql` | **621 dòng** |
| 2 | **Võ Thành Phát** | 24110300 | Danh mục, Tác giả, Sản phẩm, Giỏ hàng & Tối ưu hóa Chỉ mục (Indexes) | `02_ThanhPhat_Product_Catalog.sql` | **574 dòng** |
| 3 | **Nguyễn Ngọc Thiện** | 24110335 | Mã giảm giá Freeship, Giao dịch Đặt hàng Checkout & Hủy đơn | `03_NgocThien_Cart_Checkout_Promotion.sql` | **596 dòng** |
| 4 | **Bùi Quang Hòa** | 24110213 | Kiểm kê kho, Đánh giá, Thanh toán, Phí sàn & Báo cáo thống kê | `04_QuangHoa_Inventory_Reports_Dashboard.sql` | **578 dòng** |

---

## 2. HƯỚNG DẪN CÀI ĐẶT CSDL SQL SERVER

Có 3 cách để thiết lập CSDL `QL_NhaSach`:

### Cách 1: Chạy file điều phối Master Runner (Khuyên dùng)
- **Trong SQL Server Management Studio (SSMS)**:
  1. Mở file `00_Run_All_In_Order.sql`.
  2. Bật chế độ SQLCMD: Vào menu **Query** -> Click chọn **SQLCMD Mode**.
  3. Bấm **Execute** (hoặc phím **F5**).
- **Qua dòng lệnh PowerShell / Terminal**:
  ```powershell
  cd "DOCUMENT\scripts"
  # Thêm cờ -f 65001 để nạp chuẩn mã UTF-8 tiếng Việt:
  sqlcmd -f 65001 -S localhost -E -i "00_Run_All_In_Order.sql"
  # Hoặc chạy file gộp nguyên khối:
  sqlcmd -f 65001 -S localhost -E -i "QL_NhaSach_Full_Setup.sql"
  ```

### Cách 2: Chạy trực tiếp file gộp toàn bộ
- Mở file `QL_NhaSach_Full_Setup.sql` trong SSMS hoặc Azure Data Studio và bấm **Execute (F5)** (chạy bình thường không cần SQLCMD Mode).

### Cách 3: Phục hồi từ file sao lưu Backup (.bak)
- Đặt file `QL_NhaSach.bak` vào thư mục Backup của SQL Server hoặc chạy lệnh T-SQL sau:
  ```sql
  RESTORE DATABASE [QL_NhaSach] 
  FROM DISK = 'd:\HCMUTE\N3_HK1_Dot 1\He Quan tri CSDL\THE4_DBMS\DOCUMENT\scripts\QL_NhaSach.bak' 
  WITH REPLACE;
  ```

---

## 3. THÔNG TIN KẾT NỐI & TÀI KHOẢN ĐĂNG NHẬP

### 3.1. Kết nối ứng dụng Spring Boot
Kịch bản đã tự động khởi tạo User và Phân quyền:
- **Database Server**: `localhost:1433`
- **Database Name**: `QL_NhaSach`
- **Login / User**: `the4book_user`
- **Password**: `ChangeMe@2026`

### 3.2. Tài khoản thử nghiệm (Mật khẩu mặc định: `123456`)
- **Quản trị viên (ADMIN)**: `admin` (`admin@the4bookstore.vn`)
- **Quản lý báo cáo (MANAGER)**: `manager` (`manager@the4bookstore.vn`)
- **Chủ cửa hàng (VENDOR)**:
  - `vendor_kimdong` (`kimdong@gmail.com`)
  - `vendor_nhanam` (`nhanam@gmail.com`)
- **Khách mua hàng (KHACHHANG)**:
  - `phat` (`thangphatnekea21333@gmail.com`)
  - `khachhang_an` (`nguyenvanan@gmail.com`)

---

## 4. HƯỚNG DẪN KHỞI CHẠY ỨNG DỤNG SPRING BOOT

1. Mở terminal tại thư mục gốc dự án `THE4_DBMS`.
2. Khởi động server bằng Maven Wrapper:
   ```powershell
   ./mvnw spring-boot:run
   ```
3. Truy cập trình duyệt web tại địa chỉ:
   ```
   http://localhost:8080
   ```
