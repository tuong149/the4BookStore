<h1 align="center">The4BookStore</h1>

<p align="center">
  <strong>Nền tảng Sàn Thương Mại Điện Tử Sách & Ấn Phẩm Đa Gian Hàng (Multi-Vendor Bookstore E-Commerce Platform)</strong>
</p>

<p align="center">
  <a href="https://the4bookstore-phor.onrender.com/" target="_blank">
    <img src="https://img.shields.io/badge/Live_Demo-the4bookstore--phor.onrender.com-success?style=for-the-badge&logo=render&logoColor=white" alt="Live Demo">
  </a>
</p>

<p align="center">
  🌐 <strong>Trực tiếp trên Render:</strong> <a href="https://the4bookstore-phor.onrender.com/"><strong>https://the4bookstore-phor.onrender.com/</strong></a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&amp;logo=openjdk&amp;logoColor=white" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring_Boot-4.1.1-6DB33F?style=flat-square&amp;logo=springboot&amp;logoColor=white" alt="Spring Boot 4.1.1">
  <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&amp;logo=springsecurity&amp;logoColor=white" alt="Spring Security">
  <img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=flat-square&amp;logo=thymeleaf&amp;logoColor=white" alt="Thymeleaf">
  <img src="https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=flat-square&amp;logo=tailwind-css&amp;logoColor=white" alt="Tailwind CSS">
  <img src="https://img.shields.io/badge/Bootstrap-5-7952B3?style=flat-square&amp;logo=bootstrap&amp;logoColor=white" alt="Bootstrap 5">
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat-square&amp;logo=mysql&amp;logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/Aiven_Cloud-FF3554?style=flat-square&amp;logo=aiven&amp;logoColor=white" alt="Aiven Cloud">
  <img src="https://img.shields.io/badge/Render-46E3B7?style=flat-square&amp;logo=render&amp;logoColor=black" alt="Render">
  <img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&amp;logo=docker&amp;logoColor=white" alt="Docker">
  <img src="https://img.shields.io/badge/Maven-C71A36?style=flat-square&amp;logo=apachemaven&amp;logoColor=white" alt="Maven">
</p>

---

## Giới Thiệu

**The4BookStore** là nền tảng thương mại điện tử chuyên biệt về sách, truyện tranh, văn phòng phẩm và quà lưu niệm theo mô hình **Đa gian hàng (Multi-vendor Marketplace)**. Hệ thống kết nối độc giả với các nhà xuất bản, nhà sách lớn (Kim Đồng, Nhã Nam, Fahasa...) và tối ưu quy trình từ duyệt sách, áp mã giảm giá kép, đặt hàng, thanh toán QR động đến quản trị kho hàng và đối soát doanh thu.

Dự án đã được container hóa hoàn chỉnh bằng **Docker**, triển khai trực tiếp lên **Render Cloud** với cơ sở dữ liệu lưu trữ trên **Aiven Cloud MySQL**, đồng thời hỗ trợ vận hành nội bộ (Localhost) nhanh chóng bằng **MySQL Workbench**.

Hệ thống được thiết kế theo 4 phân quyền vai trò: **Customer (Khách hàng), Vendor (Chủ gian hàng), Manager (Quản lý) và Administrator (Quản trị viên toàn sàn)**.

---

## Công Nghệ

| Thành phần | Công nghệ / Dịch vụ sử dụng |
| --- | --- |
| **Back-end** | Java 17, Spring Boot 4.1.1, Spring Data JPA, Spring Web MVC |
| **Front-end** | Thymeleaf, Tailwind CSS, Bootstrap 5, JavaScript, Google Material Symbols |
| **Database** | MySQL 8.0, Aiven Managed Cloud MySQL, Stored Procedures, Views, Triggers, Indexes |
| **Bảo mật & Auth** | Spring Security, JWT (JSON Web Token trong HttpOnly Cookie), Google OAuth2, BCrypt |
| **Cloud & DevOps** | Render Web Service, Multi-stage Dockerfile (Alpine JRE 17), HikariCP Pool Optimization |
| **Thanh toán** | VietQR (Tạo mã QR thanh toán động và tự động xác thực Webhook), COD |
| **Lưu trữ đám mây** | Cloudinary Cloud Storage (Quản lý ảnh sách, banner và logo gian hàng) |
| **Email & Realtime**| Brevo API & Gmail SMTP (Gửi mã OTP khôi phục mật khẩu), WebSocket STOMP (Thông báo đơn hàng thời gian thực) |

---

## Chức Năng Chính

### 1. Customer (Khách mua hàng)
- **Khám phá & Tìm kiếm**: Tìm kiếm thông minh theo tiêu đề sách, tác giả, ISBN; bộ lọc động theo danh mục cha/con và **thanh trượt lọc khoảng giá** theo thời gian thực.
- **Xem chi tiết sản phẩm**: Hiển thị trạng thái kho hàng chính xác (Còn hàng, **Sắp hết hàng < 10**, Hết hàng); xem đánh giá sao trung bình và nhận xét từ độc giả.
- **Giỏ hàng & Khuyến mãi kép**: Quản lý giỏ hàng phân loại theo từng Shop; hỗ trợ nhập cùng lúc Voucher toàn sàn + Voucher riêng của Shop + Voucher Miễn phí vận chuyển.
- **Thanh toán thông minh**: Tùy chọn Thanh toán khi nhận hàng (COD) hoặc **Quét mã VietQR động** tự động điền số tiền và nội dung chuyển khoản, hệ thống tự động đối soát qua Webhook.
- **Tài khoản & Cá nhân hóa**: Đăng nhập qua tài khoản hoặc **Google OAuth2** một chạm; quản lý sổ địa chỉ giao hàng; xem lại sản phẩm yêu thích và lịch sử mua sắm.
- **Đánh giá & Phản hồi**: Đánh giá từ 1 đến 5 sao và viết nhận xét cho từng đầu sách sau khi đơn hàng giao thành công.

### 2. Vendor (Chủ gian hàng)
- **Hồ sơ Shop**: Tùy chỉnh thông tin cửa hàng, đổi logo, banner quảng bá qua Cloudinary.
- **Quản lý sản phẩm**: Thêm mới, chỉnh sửa, dừng bán các đầu sách, văn phòng phẩm, quà tặng; tùy chỉnh **Ngưỡng cảnh báo tồn kho tối thiểu (`mucTonToiThieu`, mặc định 10)**.
- **Quản lý tồn kho**: Bảng theo dõi và cảnh báo tự động khi số lượng tồn kho chạm ngưỡng (Cảnh báo "Sắp hết" từ 1 - 9 sản phẩm và "Hết hàng").
- **Xử lý đơn hàng & Realtime**: Nhận thông báo đơn hàng mới tức thì qua **WebSocket**; theo dõi và cập nhật trạng thái đơn (Chờ xác nhận, Đang chuẩn bị, Đang giao, Đã giao, Hủy đơn).
- **Mã giảm giá Shop**: Tự tạo và phát hành voucher riêng áp dụng cho khách hàng mua sắm tại gian hàng của mình (% giảm giá hoặc số tiền cố định).
- **Thống kê & Doanh thu**: Báo cáo tổng doanh thu bán hàng, phí chiết khấu sàn và doanh số theo thời gian thực.

### 3. Administrator & Manager (Quản trị & Quản lý sàn)
- **Dashboard phân tích tổng quan**: Biểu đồ doanh thu toàn sàn, số lượng đơn hàng, số người dùng đăng ký mới và gian hàng đang hoạt động.
- **Báo cáo doanh thu nâng cao**: Lọc báo cáo tài chính theo khoảng thời gian tùy chọn thông qua Stored Procedure tối ưu (`sp_BaoCaoDoanhThuTheoKhoangNgay`).
- **Quản trị gian hàng & Sản phẩm**: Duyệt/khóa các gian hàng hoặc sản phẩm vi phạm quy định; quản trị danh mục thể loại lớn và tác giả toàn hệ thống.
- **Kiểm soát kho toàn sàn**: Theo dõi tồn kho trên toàn bộ các shop, cảnh báo sách cạn hàng.
- **Quản lý Voucher toàn sàn**: Tạo mã ưu đãi sự kiện, voucher freeship áp dụng cho toàn bộ người dùng.
- **Bảo mật & Người dùng**: Quản lý tài khoản, phân quyền vai trò (Admin, Manager, Vendor, Khách hàng), tra cứu nhật ký bảo mật (Security Audit Log).

---

## Hướng Dẫn Chạy Dự Án

### Yêu Cầu Môi Trường
- **Java**: Phiên bản 17 (khuyến nghị OpenJDK 17 hoặc Eclipse Temurin 17).
- **Maven**: Phiên bản 3.8+ (hoặc dùng sẵn `./mvnw` đi kèm dự án).
- **Cơ sở dữ liệu**: MySQL 8.0+ (Localhost) hoặc Aiven Cloud MySQL.

---

### Cách 1: Chạy Localhost (Dùng MySQL Workbench)

#### Bước 1: Khởi tạo Cơ Sở Dữ Liệu trên MySQL Workbench
1. Mở **MySQL Workbench**, kết nối vào MySQL Server cục bộ của bạn (`localhost:3306`).
2. Mở file script SQL hoàn chỉnh tại đường dẫn:
   ```text
   DOCUMENT/scripts/QL_NhaSach_Full_Setup_MySQL.sql
   ```
3. Nhấn nút **Execute (biểu tượng tia sét)** hoặc bấm tổ hợp phím `Ctrl + Shift + Enter` để chạy toàn bộ file. Script sẽ tự động:
   - Tạo mới database `QL_NhaSach`.
   - Tạo toàn bộ cấu trúc bảng, khóa ngoại, chỉ mục tối ưu hóa (Indexes).
   - Tạo các View thống kê (`vw_CanhBaoTonKhoShop`, `vw_ThongKeDanhGiaSanPham`...).
   - Tạo các Stored Procedures & Triggers kiểm soát luồng giao dịch.
   - Nạp sẵn toàn bộ dữ liệu mẫu (Seed Data) chuẩn UTF-8.

#### Bước 2: Thiết lập file cấu hình môi trường `.env`
Sao chép file `.env.example` thành `.env` tại thư mục gốc của dự án:
```sh
# Trên Windows PowerShell:
Copy-Item .env.example .env

# Trên Linux / macOS:
cp .env.example .env
```

Mở file `.env` vừa tạo và chỉnh sửa thông tin kết nối MySQL:
```dotenv
# Cấu hình kết nối MySQL Localhost
DB_URL=jdbc:mysql://localhost:3306/QL_NhaSach?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=mat_khau_mysql_cua_ban

# Dịch vụ ngoài (Nếu chưa có thể để nguyên giá trị mặc định)
MAIL_USERNAME=nhasachthe4bookstore@gmail.com
MAIL_PASSWORD=your-gmail-app-password
JWT_SECRET=the4BookStore_SuperSecret_Jwt_Signing_Key_2026_DBMS330284_HCMUTE_Key
```

#### Bước 3: Khởi chạy ứng dụng
Mở terminal tại thư mục gốc dự án và thực hiện lệnh:
```sh
# Tải dependencies và biên dịch:
./mvnw clean install -DskipTests

# Khởi động Spring Boot:
./mvnw spring-boot:run
```

Truy cập hệ thống tại: **`http://localhost:8080`**. Dừng ứng dụng bằng tổ hợp phím `Ctrl + C`.

---

### Cách 2: Triển Khai Lên Render & Kết Nối Aiven Cloud MySQL

* **Link ứng dụng trực tuyến:** [https://the4bookstore-phor.onrender.com/](https://the4bookstore-phor.onrender.com/)

Dự án đã tích hợp sẵn tệp cấu hình **`Dockerfile`** (được tối ưu hóa tài nguyên cho gói Free 512MB RAM) và **`render.yaml`**.

#### 1. Chuẩn bị Cơ sở dữ liệu Aiven Cloud MySQL
1. Đăng ký tài khoản miễn phí tại [Aiven.io](https://aiven.io/) và tạo một dịch vụ **MySQL**.
2. Lấy thông tin kết nối từ Aiven Dashboard:
   - **Host**, **Port**, **User** (`avnadmin`), **Password**, **Database Name** (`defaultdb` hoặc tạo `QL_NhaSach`).
3. Mở **MySQL Workbench** trên máy tính, tạo kết nối mới tới Aiven MySQL bằng Host/Port/SSL được cấp.
4. Mở và thực thi file `DOCUMENT/scripts/QL_NhaSach_Full_Setup_MySQL.sql` để thiết lập bảng và dữ liệu mẫu lên Aiven Cloud.

#### 2. Cấu hình biến môi trường trên Render
Khi tạo dịch vụ **Web Service** trên Render (chọn môi trường Runtime là **Docker**), thiết lập các biến môi trường (Environment Variables) sau:

| Tên biến (Key) | Giá trị mẫu (Value) | Mô tả |
| --- | --- | --- |
| `DB_URL` | `jdbc:mysql://<host-aiven>:<port>/<dbname>?useSSL=true&requireSSL=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8` | URL kết nối Aiven Cloud MySQL |
| `DB_USERNAME` | `avnadmin` | Tài khoản đăng nhập Aiven |
| `DB_PASSWORD` | `<mat-khau-aiven>` | Mật khẩu Aiven Cloud |
| `JWT_SECRET` | `the4BookStore_SuperSecret_Jwt_Signing_Key_2026_DBMS330284_HCMUTE_Key` | Khóa ký JWT Token |
| `JWT_COOKIE_SECURE` | `true` | Bật cờ Secure cho cookie HTTPS trên Render |
| `BREVO_API_KEY` | `xkeysib-...` | API Key gửi mail qua Brevo (không bị chặn cổng SMTP trên Cloud) |
| `CLOUDINARY_CLOUD_NAME` | `<ten-cloud>` | Cloud Name lưu trữ ảnh |
| `CLOUDINARY_API_KEY` | `<api-key>` | API Key Cloudinary |
| `CLOUDINARY_API_SECRET` | `<api-secret>` | API Secret Cloudinary |

> **Lưu ý tối ưu bộ nhớ Render**: Trong `Dockerfile`, ứng dụng đã được cấu hình các cờ JVM nghiêm ngặt (`-Xmx180m -Xms80m -XX:MaxMetaspaceSize=192m -XX:+UseSerialGC`) cùng cấu hình giới hạn kết nối HikariCP (`maximum-pool-size=12`) để đảm bảo không vượt quá giới hạn 512MB RAM của gói Render Free.

---

## Tài Khoản Thử Nghiệm Mặc Định

Tất cả các tài khoản mẫu dưới đây đã được khởi tạo sẵn trong file SQL với mật khẩu chung là **`Abc@123`**:

| Vai trò | Tên đăng nhập | Mật khẩu | Email | Trang truy cập |
| --- | --- | --- | --- | --- |
| **Quản trị viên (Admin)** | `admin` | `Abc@123` | `admin@the4bookstore.vn` | `/admin` |
| **Quản lý (Manager)** | `manager` | `Abc@123` | `manager@the4bookstore.vn` | `/admin` |
| **Chủ Shop Kim Đồng** | `vendor_kimdong` | `Abc@123` | `kimdong@gmail.com` | `/vendor` |
| **Chủ Shop Nhã Nam** | `vendor_nhanam` | `Abc@123` | `nhanam@gmail.com` | `/vendor` |
| **Khách hàng** | `phat` | `Abc@123` | `thangphatnekea21333@gmail.com` | `/` |
| **Khách hàng** | `khachhang_an` | `Abc@123` | `nguyenvanan@gmail.com` | `/` |

*(Nếu kích hoạt cờ `app.seeder.enabled=true`, mật khẩu mặc định của các tài khoản khởi tạo tự động cũng là `Abc@123`)*

---

## Cấu Trúc Dự Án

```text
the4BookStore/
├── DOCUMENT/
│   └── scripts/
│       ├── QL_NhaSach_Full_Setup_MySQL.sql   # Toàn bộ DDL, Views, Procedures, Triggers & DML Seed Data
│       └── HUONG_DAN_CAI_DAT_VA_CHAY.md      # Hướng dẫn chi tiết thiết lập đồ án môn học
├── src/
│   ├── main/
│   │   ├── java/vn/bookstore/the4bookstore/
│   │   │   ├── component/                    # Bộ Seeder nạp dữ liệu mẫu tự động
│   │   │   ├── config/                       # Cấu hình Security, WebMvc, Cloudinary, WebSocket
│   │   │   ├── controller/                   # Các Controller xử lý (Admin, Vendor, Cart, Order, Auth...)
│   │   │   ├── dto/                          # Các lớp Data Transfer Object truyền tải dữ liệu
│   │   │   ├── entity/                       # Các thực thể ánh xạ JPA Database Entities
│   │   │   ├── repository/                   # Tầng giao tiếp dữ liệu Spring Data JPA
│   │   │   ├── security/                     # Xử lý JWT Token, Custom UserDetails, Security Filters
│   │   │   └── service/                      # Tầng nghiệp vụ xử lý nghiệp vụ đơn hàng, sản phẩm, báo cáo
│   │   └── resources/
│   │       ├── static/                       # Hình ảnh tĩnh, Logo, Icon, CSS tùy biến
│   │       ├── templates/                    # Giao diện Thymeleaf HTML (Khách hàng, Vendor, Admin)
│   │       └── application.properties        # Cấu hình kết nối DataSource, HikariCP, Port & Mail
│   └── test/
│       └── java/vn/bookstore/the4bookstore/  # Bộ kiểm thử đơn vị & kiểm thử tích hợp (32 tests)
├── Dockerfile                                # Multi-stage Docker build tối ưu cho Render
├── render.yaml                               # File khai báo hạ tầng triển khai Blueprint trên Render
├── pom.xml                                   # Quản lý phiên bản dependencies Maven
├── .env.example                              # File mẫu cấu hình biến môi trường
└── README.md                                 # Tài liệu tổng quan dự án
```

---

<p align="center">
  <sub>Đồ án kết thúc học phần môn Hệ Quản Trị Cơ Sở Dữ Liệu (DBMS330284) - Trường Đại học Sư Phạm Kỹ Thuật TP.HCM (HCMUTE).</sub>
</p>
