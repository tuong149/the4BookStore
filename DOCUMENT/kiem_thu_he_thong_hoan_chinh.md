# CHƯƠNG 6. KIỂM THỬ VÀ ĐÁNH GIÁ HỆ THỐNG

## 6.2. Kiểm thử hệ thống (Test Cases)

### 6.2.1. Mục tiêu, phạm vi và phương pháp kiểm thử
Mục tiêu của giai đoạn kiểm thử là xác nhận các chức năng đã cài đặt trong hệ thống The4BookStore (sàn thương mại điện tử sách đa gian hàng) hoạt động đúng theo đặc tả yêu cầu, bao gồm cả luồng nghiệp vụ thành công lẫn các trường hợp ngoại lệ.

Phạm vi kiểm thử bao gồm bốn nhóm chức năng chính:
- **(A) Xác thực và phân quyền:** Đăng ký tài khoản mới, kiểm tra ràng buộc duy nhất (tên đăng nhập, email, số điện thoại), đăng nhập JWT (lưu cookie `the4book_jwt`), phân quyền truy cập theo vai trò (RBAC), quy trình quên và đặt lại mật khẩu với mã OTP gửi qua SMTP Gmail (hiệu lực 5 phút).
- **(B) Mua sắm của khách hàng:** Tìm kiếm theo từ khóa đa tiêu chí, lọc danh mục và sắp xếp theo giá, quản lý giỏ hàng và ràng buộc tồn kho an toàn, áp dụng voucher giảm giá (theo % có trần mức tối đa), đặt hàng COD và thanh toán mã động VietQR, hủy đơn hàng và tự động hoàn trả tồn kho.
- **(C) Phân hệ Người bán (Vendor):** Đăng ký mở gian hàng, nộp hồ sơ chờ duyệt, đăng bán sách và tải ảnh bìa lên Cloudinary, lập phiếu nhập hàng tăng tồn kho, lập phiếu kiểm kê cân bằng kho, tiếp nhận và xử lý đơn hàng đa trạng thái (Escrow).
- **(D) Quản lý sàn và Quản trị viên:** Duyệt gian hàng mới, kiểm duyệt và khóa/mở khóa sản phẩm vi phạm, quản lý người dùng với ràng buộc phân quyền (chặn Manager khóa Admin, chặn tự khóa), giải quyết tranh chấp trả hàng - hoàn tiền, cấu hình tỷ lệ chiết khấu sàn.

**Phương pháp áp dụng:** Kiểm thử hộp đen (black-box testing) trên giao diện web, kết hợp kiểm tra dữ liệu trực tiếp trong CSDL MySQL 8.0 để xác minh kết quả. Các kỹ thuật áp dụng gồm: phân vùng tương đương (dữ liệu hợp lệ / không hợp lệ), phân tích giá trị biên (mã OTP hết hạn sau 5 phút, ngưỡng miễn phí vận chuyển 350.000đ, tồn kho bằng 0) và kiểm thử phân quyền theo vai trò. Ngoài ra, hệ thống tích hợp bộ kiểm thử tự động JUnit 5 / Spring MockMvc với 71 phương thức kiểm thử (mục 6.2.5).

---

### 6.2.2. Môi trường và dữ liệu kiểm thử

#### Bảng 6-2. Môi trường kiểm thử
| Hạng mục | Thông tin chi tiết |
| :--- | :--- |
| **Ứng dụng** | The4BookStore – Spring Boot 4.1.1, Thymeleaf, Spring Security (JWT trong cookie `the4book_jwt`) |
| **Địa chỉ truy cập** | `http://localhost:8080` |
| **Cơ sở dữ liệu** | MySQL 8.0 – schema `QL_NhaSach` (nạp từ `DOCUMENT/ql_nhasach_dump.sql`); công cụ quản lý: MySQL Workbench |
| **Dịch vụ tích hợp** | Cloudinary (lưu trữ ảnh bìa sách), SMTP Gmail (gửi OTP đặt lại mật khẩu), VietQR API (`img.vietqr.io`), Google OAuth2 |
| **Trình duyệt** | Google Chrome (phiên bản mới nhất) |
| **Hệ điều hành** | Windows 10/11 64-bit |
| **Kiểm thử tự động** | JUnit 5, Spring MockMvc, Mockito – thực thi bằng lệnh `.\mvnw.cmd test` |

#### Bảng 6-3. Danh sách tài khoản kiểm thử
| Tên đăng nhập | Mật khẩu | Vai trò | Shop liên kết | Màn hình sau đăng nhập |
| :--- | :--- | :--- | :--- | :--- |
| `admin` | `123456` | ADMIN | Shop 1 – The4BookStore Official | `/admin/dashboard` |
| `manager` | `123456` | MANAGER | – | `/admin/dashboard` |
| `user` | `123456` | Khách hàng (USER) | – | `/` (Trang chủ) |
| `vendor_demo` | `123456` | VENDOR | Shop 2 – Nhà Sách Trí Tuệ | `/vendor/dashboard` |
| `vendor_fahasa` | `123456` | VENDOR | Shop 3 – Fahasa | `/vendor/dashboard` |
| `khach_test01` | `123456` (`654321` sau TC-06) | KHACHHANG → VENDOR (sau TC-15) | Shop Nhà Sách Tri Thức Trẻ | `/` → `/vendor/dashboard` |

*Ghi chú: Các tài khoản `admin`, `manager`, `user`, `vendor_*` do Component `DataSeeder` (`vn.bookstore.the4bookstore.component.DataSeeder`) tự động khởi tạo vào CSDL; tài khoản `khach_test01` được tạo trong quá trình thực hiện kiểm thử (TC-01).*

---

### 6.2.3. Danh sách test case

#### Bảng 6-4. Tổng hợp các test case kiểm thử hệ thống
| Mã TC | Chức năng kiểm thử | Tài khoản thực hiện | Bảng chi tiết | Kết quả |
| :--- | :--- | :--- | :--- | :--- |
| **TC-01** | Đăng ký tài khoản khách hàng mới | Khách vãng lai | Bảng 6-5 | ĐẠT |
| **TC-02** | Đăng ký với thông tin đã tồn tại (trùng lặp) | Khách vãng lai | Bảng 6-6 | ĐẠT |
| **TC-03** | Đăng nhập thành công và điều hướng theo vai trò (JWT) | `user`, `vendor_demo`, `manager`, `admin` | Bảng 6-7 | ĐẠT |
| **TC-04** | Đăng nhập với thông tin sai | Khách vãng lai | Bảng 6-8 | ĐẠT |
| **TC-05** | Kiểm tra phân quyền truy cập (RBAC) | `user`, Khách vãng lai | Bảng 6-9 | ĐẠT |
| **TC-06** | Quên mật khẩu và đặt lại bằng mã OTP qua Email | Khách vãng lai | Bảng 6-10 | ĐẠT |
| **TC-07** | Quên mật khẩu – các trường hợp ngoại lệ | Khách vãng lai | Bảng 6-11 | ĐẠT |
| **TC-08** | Tìm kiếm sản phẩm theo từ khóa | Khách vãng lai | Bảng 6-12 | ĐẠT |
| **TC-09** | Lọc theo danh mục và sắp xếp theo giá | Khách vãng lai | Bảng 6-13 | ĐẠT |
| **TC-10** | Quản lý giỏ hàng (thêm, giới hạn tồn kho, hết hàng) | `user` | Bảng 6-14 | ĐẠT |
| **TC-11** | Áp dụng mã giảm giá (voucher) | `user` | Bảng 6-15 | ĐẠT |
| **TC-12** | Đặt hàng thanh toán khi nhận hàng (COD) | `user` | Bảng 6-16 | ĐẠT |
| **TC-13** | Đặt hàng thanh toán VietQR và xác nhận nhận tiền | `user`, `admin` | Bảng 6-17 | ĐẠT |
| **TC-14** | Hủy đơn hàng mới và hoàn lại tồn kho | `user` | Bảng 6-18 | ĐẠT |
| **TC-15** | Đăng ký mở gian hàng và quản lý sàn phê duyệt | `khach_test01`, `manager` | Bảng 6-19 | ĐẠT |
| **TC-16** | Vendor đăng bán sách (upload ảnh Cloudinary) và Admin duyệt | `vendor_demo`, `manager`, Khách | Bảng 6-20 | ĐẠT |
| **TC-17** | Lập phiếu nhập hàng và tự động cộng dồn tồn kho | `vendor_fahasa` | Bảng 6-21 | ĐẠT |
| **TC-18** | Lập phiếu kiểm kê và cân bằng tồn kho | `vendor_fahasa` | Bảng 6-22 | ĐẠT |
| **TC-19** | Xử lý đơn hàng của Shop đến khi khách xác nhận hoàn tất | `vendor_fahasa`, `user` | Bảng 6-23 | ĐẠT |
| **TC-20** | Khóa và mở khóa sản phẩm vi phạm | `manager`, Khách | Bảng 6-24 | ĐẠT |
| **TC-21** | Khóa tài khoản người dùng và ràng buộc quyền hạn | `admin`, `manager`, `khach_test01` | Bảng 6-25 | ĐẠT |
| **TC-22** | Quản lý phân xử tranh chấp trả hàng - hoàn tiền | `user`, `vendor_fahasa`, `manager` | Bảng 6-26 | ĐẠT |
| **TC-23** | Cấu hình chiết khấu sàn (mặc định và theo từng shop) | `admin`, `user` | Bảng 6-27 | ĐẠT |

---

### 6.2.4. Chi tiết các test case

#### A. Nhóm chức năng Xác thực và Phân quyền

##### Bảng 6-5. Test Case TC-01: Đăng ký tài khoản khách hàng mới
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-01 |
| **Chức năng** | Đăng ký tài khoản (`auth/register.html` – URL `/register`) |
| **Mục tiêu** | Kiểm tra hệ thống nhận đủ thông tin, lưu mật khẩu mã hóa BCrypt vào bảng `tai_khoan` và tạo hồ sơ khách hàng liên kết trong bảng `khach_hang`. |
| **Tài khoản thực hiện** | Chưa đăng nhập (khách vãng lai) |
| **Tiền điều kiện** | Trình duyệt mở tại `http://localhost:8080/register`. Tên đăng nhập `khach_test01`, email và số điện thoại `0912345678` chưa tồn tại trong CSDL. |
| **Dữ liệu đầu vào** | Tên đăng nhập: `khach_test01`<br>Email: `khachtest01@gmail.com` (sử dụng email thật để thử nghiệm TC-06)<br>Mật khẩu: `123456`<br>Họ và tên: `Nguyễn Văn Test`<br>Số điện thoại: `0912345678`<br>Địa chỉ: `12 Võ Văn Ngân, TP. Thủ Đức, TP.HCM` |
| **Các bước thực hiện** | 1. Nhập đủ 6 trường trên form TẠO TÀI KHOẢN.<br>2. Bấm nút "Đăng ký tài khoản".<br>3. Quan sát trang đích.<br>4. Mở MySQL Workbench, truy vấn bảng `tai_khoan` và `khach_hang`. |
| **Kết quả mong đợi** | Chuyển hướng về `/login?registered=true`, hiển thị thông báo "Đăng ký thành công! Vui lòng đăng nhập."<br>Bảng `tai_khoan` có bản ghi mới: `vai_tro = 'KHACHHANG'`, `trang_thai = 'HoatDong'`, `auth_provider = 'LOCAL'`, `mat_khau_hash` bắt đầu bằng `$2a$10$` (BCrypt).<br>Bảng `khach_hang` có bản ghi liên kết với tài khoản vừa tạo. |
| **Kết quả thực tế** | Đăng ký thành công, hiển thị đúng thông báo; mật khẩu được lưu dạng hash BCrypt, hồ sơ khách hàng được tạo chính xác. |
| **Ảnh minh chứng** | Hình 6-3, Hình 6-4, Hình 6-5 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-3] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** Chưa đăng nhập |
| **Màn hình / URL:** `/register` |
| **Cần thấy trong ảnh:** Toàn bộ form đã điền đủ 6 trường, trước khi bấm Đăng ký. |
| **Tên tệp gợi ý:** `tc01_form.png` |
*Hình 6-3. TC-01 – Toàn bộ form đã điền đủ 6 trường, trước khi bấm Đăng ký*

| [CHÈN ẢNH – Hình 6-4] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** Chưa đăng nhập |
| **Màn hình / URL:** `/login?registered=true` |
| **Cần thấy trong ảnh:** Trang đăng nhập có banner "Đăng ký thành công! Vui lòng đăng nhập." |
| **Tên tệp gợi ý:** `tc01_thanhcong.png` |
*Hình 6-4. TC-01 – Trang đăng nhập có banner "Đăng ký thành công! Vui lòng đăng nhập."*

| [CHÈN ẢNH – Hình 6-5] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** (MySQL Workbench) |
| **Màn hình / URL:** `SELECT * FROM tai_khoan WHERE ten_dang_nhap='khach_test01';` |
| **Cần thấy trong ảnh:** Hàng dữ liệu cho thấy `vai_tro`, `trang_thai`, `auth_provider` và cột `mat_khau_hash` bắt đầu bằng `$2a$10$`. |
| **Tên tệp gợi ý:** `tc01_db.png` |
*Hình 6-5. TC-01 – Hàng dữ liệu cho thấy vai_tro, trang_thai, auth_provider và cột mat_khau_hash bắt đầu bằng $2a$10$*

---

##### Bảng 6-6. Test Case TC-02: Đăng ký với thông tin đã tồn tại
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-02 |
| **Chức năng** | Đăng ký tài khoản – kiểm tra trùng lặp (`AuthController.processRegister`) |
| **Mục tiêu** | Đảm bảo hệ thống từ chối đăng ký khi tên đăng nhập, email hoặc số điện thoại đã được sử dụng, không phát sinh bản ghi mới. |
| **Tài khoản thực hiện** | Chưa đăng nhập (khách vãng lai) |
| **Tiền điều kiện** | Đã hoàn thành TC-01 (tồn tại `khach_test01`, SĐT `0912345678`). Tài khoản `user` (email `user@the4bookstore.vn`) có sẵn trong CSDL. |
| **Dữ liệu đầu vào** | Lần 1: Tên đăng nhập = `user`; các trường khác hợp lệ, email và SĐT chưa dùng.<br>Lần 2: Tên đăng nhập mới; Email = `user@the4bookstore.vn`.<br>Lần 3: Tên đăng nhập và email mới; Số điện thoại = `0912345678`. |
| **Các bước thực hiện** | 1. Mở `/register`, nhập dữ liệu Lần 1, bấm "Đăng ký tài khoản".<br>2. Lặp lại với dữ liệu Lần 2 và Lần 3.<br>3. Kiểm tra số bản ghi trong bảng `tai_khoan` không thay đổi. |
| **Kết quả mong đợi** | Lần 1: ở lại trang `/register`, báo lỗi "Tên đăng nhập đã tồn tại!"<br>Lần 2: báo lỗi "Email đã được sử dụng!"<br>Lần 3: báo lỗi "Số điện thoại đã được sử dụng!"<br>Không phát sinh bản ghi mới trong CSDL. |
| **Kết quả thực tế** | Cả 3 trường hợp đều bị từ chối với đúng thông báo lỗi; không có dữ liệu mới nào bị lưu. |
| **Ảnh minh chứng** | Hình 6-6, Hình 6-7 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-6] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** Chưa đăng nhập |
| **Màn hình / URL:** `/register` (Lần 1) |
| **Cần thấy trong ảnh:** Form đăng ký còn dữ liệu và khung báo lỗi đỏ "Tên đăng nhập đã tồn tại!" |
| **Tên tệp gợi ý:** `tc02_username.png` |
*Hình 6-6. TC-02 – Form đăng ký còn dữ liệu và khung báo lỗi đỏ "Tên đăng nhập đã tồn tại!"*

| [CHÈN ẢNH – Hình 6-7] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** Chưa đăng nhập |
| **Màn hình / URL:** `/register` (Lần 3) |
| **Cần thấy trong ảnh:** Khung báo lỗi "Số điện thoại đã được sử dụng!" |
| **Tên tệp gợi ý:** `tc02_sdt.png` |
*Hình 6-7. TC-02 – Khung báo lỗi "Số điện thoại đã được sử dụng!"*

---

##### Bảng 6-7. Test Case TC-03: Đăng nhập thành công và điều hướng theo vai trò
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-03 |
| **Chức năng** | Đăng nhập hệ thống (`auth/login.html` – POST `/process-login`, `OAuth2LoginSuccessHandler`) |
| **Mục tiêu** | Kiểm tra xác thực thành công, cấp JWT (cookie `the4book_jwt`) và điều hướng đúng màn hình theo vai trò người dùng. |
| **Tài khoản thực hiện** | Lần lượt: `user`, `vendor_demo`, `manager`, `admin` (mật khẩu `123456`) |
| **Tiền điều kiện** | Hệ thống đang chạy; 4 tài khoản trên ở trạng thái `HoatDong`. Mỗi lần thử phải đăng xuất (hoặc dùng cửa sổ ẩn danh) trước khi đăng nhập tài khoản tiếp theo. |
| **Dữ liệu đầu vào** | `user / 123456` → Khách hàng<br>`vendor_demo / 123456` → Người bán (Shop 2: Nhà Sách Trí Tuệ)<br>`manager / 123456` → Quản lý sàn<br>`admin / 123456` → Quản trị viên |
| **Các bước thực hiện** | 1. Mở `/login`.<br>2. Nhập tên đăng nhập, mật khẩu và bấm "Đăng nhập".<br>3. Ghi nhận trang đích.<br>4. Mở DevTools (F12) → Application → Cookies, kiểm tra cookie `the4book_jwt`.<br>5. Lặp lại cho 3 tài khoản còn lại. |
| **Kết quả mong đợi** | `user` → về trang chủ `/`.<br>`vendor_demo` → `/vendor/dashboard`.<br>`manager` và `admin` → `/admin/dashboard`.<br>Trình duyệt nhận cookie `the4book_jwt` có cờ `HttpOnly`. |
| **Kết quả thực tế** | Cả 4 tài khoản đăng nhập thành công và được điều hướng đúng màn hình theo vai trò; cookie JWT được cấp đầy đủ. |
| **Ảnh minh chứng** | Hình 6-8, Hình 6-9, Hình 6-10, Hình 6-11, Hình 6-12 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-8] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `user` |
| **Màn hình / URL:** `/` (trang chủ sau đăng nhập) |
| **Cần thấy trong ảnh:** Thanh điều hướng hiển thị trạng thái đã đăng nhập của user. |
| **Tên tệp gợi ý:** `tc03_user.png` |
*Hình 6-8. TC-03 – Thanh điều hướng hiển thị trạng thái đã đăng nhập của user*

| [CHÈN ẢNH – Hình 6-9] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `vendor_demo` |
| **Màn hình / URL:** `/vendor/dashboard` |
| **Cần thấy trong ảnh:** Màn hình Kênh người bán (Seller Center). |
| **Tên tệp gợi ý:** `tc03_vendor.png` |
*Hình 6-9. TC-03 – Màn hình Kênh người bán (Seller Center)*

| [CHÈN ẢNH – Hình 6-10] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `manager` |
| **Màn hình / URL:** `/admin/dashboard` |
| **Cần thấy trong ảnh:** Bento KPI Dashboard của quản lý sàn. |
| **Tên tệp gợi ý:** `tc03_manager.png` |
*Hình 6-10. TC-03 – Bento KPI Dashboard của quản lý sàn*

| [CHÈN ẢNH – Hình 6-11] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `admin` |
| **Màn hình / URL:** `/admin/dashboard` |
| **Cần thấy trong ảnh:** Bento KPI Dashboard của quản trị viên. |
| **Tên tệp gợi ý:** `tc03_admin.png` |
*Hình 6-11. TC-03 – Bento KPI Dashboard của quản trị viên*

| [CHÈN ẢNH – Hình 6-12] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `admin` |
| **Màn hình / URL:** DevTools → Application → Cookies |
| **Cần thấy trong ảnh:** Cookie `the4book_jwt` với cột `HttpOnly` được đánh dấu tick. |
| **Tên tệp gợi ý:** `tc03_cookie.png` |
*Hình 6-12. TC-03 – Cookie the4book_jwt với cột HttpOnly được đánh dấu*

---

##### Bảng 6-8. Test Case TC-04: Đăng nhập với thông tin sai
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-04 |
| **Chức năng** | Đăng nhập hệ thống (`CustomAuthenticationFailureHandler`) |
| **Mục tiêu** | Đảm bảo hệ thống từ chối đăng nhập khi sai mật khẩu hoặc tên đăng nhập không tồn tại và không tạo phiên đăng nhập. |
| **Tài khoản thực hiện** | Chưa đăng nhập (khách vãng lai) |
| **Tiền điều kiện** | Tài khoản `user` tồn tại và đang hoạt động. |
| **Dữ liệu đầu vào** | Lần 1: `user / sai_mat_khau`<br>Lần 2: `khong_ton_tai / 123456` |
| **Các bước thực hiện** | 1. Mở `/login`, nhập dữ liệu Lần 1, bấm "Đăng nhập".<br>2. Lặp lại với Lần 2.<br>3. Kiểm tra không có cookie `the4book_jwt`. |
| **Kết quả mong đợi** | Chuyển về `/login?error=true`, hiển thị khung đỏ "Sai tên đăng nhập hoặc mật khẩu!"<br>Hệ thống không phân biệt sai mật khẩu hay sai tên đăng nhập (tránh lộ thông tin tài khoản).<br>Không cấp cookie `the4book_jwt`. |
| **Kết quả thực tế** | Hệ thống chặn đăng nhập ở cả 2 trường hợp và hiển thị đúng một thông báo chung an toàn. |
| **Ảnh minh chứng** | Hình 6-13 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-13] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** Chưa đăng nhập |
| **Màn hình / URL:** `/login?error=true` |
| **Cần thấy trong ảnh:** Khung báo lỗi "Sai tên đăng nhập hoặc mật khẩu!" phía trên form. |
| **Tên tệp gợi ý:** `tc04_error.png` |
*Hình 6-13. TC-04 – Khung báo lỗi "Sai tên đăng nhập hoặc mật khẩu!" phía trên form*

---

##### Bảng 6-9. Test Case TC-05: Kiểm tra phân quyền truy cập (RBAC)
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-05 |
| **Chức năng** | Phân quyền Spring Security (`SecurityConfig` – `requestMatchers` theo vai trò) |
| **Mục tiêu** | Đảm bảo người dùng không có quyền không thể truy cập các phân hệ Admin, Vendor hoặc trang yêu cầu đăng nhập. |
| **Tài khoản thực hiện** | `user` (khách hàng) và khách vãng lai |
| **Tiền điều kiện** | Hệ thống đang chạy bình thường. |
| **Dữ liệu đầu vào** | Ca 1: đăng nhập `user`, truy cập `/admin/dashboard`.<br>Ca 2: đăng nhập `user`, truy cập `/vendor/dashboard`.<br>Ca 3: chưa đăng nhập, truy cập `/don-hang`. |
| **Các bước thực hiện** | 1. Đăng nhập `user`, gõ trực tiếp URL ở Ca 1 trên thanh địa chỉ.<br>2. Gõ URL ở Ca 2.<br>3. Đăng xuất, gõ URL ở Ca 3. |
| **Kết quả mong đợi** | Ca 1 và Ca 2: bị chuyển về `/?accessDenied=true`, không thấy nội dung quản trị.<br>Ca 3: bị chuyển về trang `/login`. |
| **Kết quả thực tế** | Mọi truy cập trái quyền đều bị chặn và điều hướng chuẩn xác như kỳ vọng. |
| **Ảnh minh chứng** | Hình 6-14, Hình 6-15 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-14] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `user` |
| **Màn hình / URL:** `/?accessDenied=true` (sau khi gõ `/admin/dashboard`) |
| **Cần thấy trong ảnh:** Thanh địa chỉ hiển thị `/?accessDenied=true` và hiển thị trang chủ. |
| **Tên tệp gợi ý:** `tc05_denied.png` |
*Hình 6-14. TC-05 – Thanh địa chỉ hiển thị /?accessDenied=true và trang chủ*

| [CHÈN ẢNH – Hình 6-15] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** Chưa đăng nhập |
| **Màn hình / URL:** `/login` (sau khi gõ `/don-hang`) |
| **Cần thấy trong ảnh:** Thanh địa chỉ hiển thị `/login`. |
| **Tên tệp gợi ý:** `tc05_login.png` |
*Hình 6-15. TC-05 – Thanh địa chỉ hiển thị /login*

---

##### Bảng 6-10. Test Case TC-06: Quên mật khẩu và đặt lại bằng mã OTP qua Email
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-06 |
| **Chức năng** | Quên mật khẩu (`auth/forgot-password.html`, `auth/reset-password.html` – `PasswordResetService`) |
| **Mục tiêu** | Kiểm tra sinh OTP 6 chữ số, gửi email qua SMTP Gmail, xác thực OTP (hiệu lực 5 phút) và cập nhật mật khẩu mới dạng mã hóa BCrypt. |
| **Tài khoản thực hiện** | Chưa đăng nhập (đặt lại cho tài khoản `khach_test01`) |
| **Tiền điều kiện** | Đã có `khach_test01` với email thực nhận (TC-01). Cấu hình `MAIL_USERNAME`/`MAIL_PASSWORD` hợp lệ trong file `.env`. |
| **Dữ liệu đầu vào** | Email: `khachtest01@gmail.com`<br>Mật khẩu mới: `654321`; Xác nhận: `654321` |
| **Các bước thực hiện** | 1. Tại `/login` bấm "Quên mật khẩu?" để vào `/forgot-password`.<br>2. Nhập email, bấm "Gửi mã xác thực OTP".<br>3. Mở hộp thư, lấy mã OTP 6 số.<br>4. Tại trang XÁC THỰC MÃ OTP nhập OTP, mật khẩu mới, xác nhận.<br>5. Bấm "Xác nhận & Đổi mật khẩu".<br>6. Đăng nhập lại bằng mật khẩu `654321`. |
| **Kết quả mong đợi** | Chuyển sang `/reset-password?email=...`, hiển thị "Mã xác thực OTP đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư (hoặc mục Spam)."<br>Email chứa OTP gồm 6 chữ số được gửi tới hộp thư.<br>Sau khi đổi: chuyển `/login?resetSuccess=true` với thông báo "Đặt lại mật khẩu thành công! Vui lòng đăng nhập bằng mật khẩu mới."<br>Đăng nhập được bằng mật khẩu mới; OTP chuyển sang trạng thái đã sử dụng. |
| **Kết quả thực tế** | OTP được gửi qua email, đổi mật khẩu thành công và đăng nhập được bằng mật khẩu mới. |
| **Ảnh minh chứng** | Hình 6-16, Hình 6-17, Hình 6-18, Hình 6-19 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-16] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** Chưa đăng nhập |
| **Màn hình / URL:** `/forgot-password` |
| **Cần thấy trong ảnh:** Form QUÊN MẬT KHẨU đã nhập email. |
| **Tên tệp gợi ý:** `tc06_forgot.png` |
*Hình 6-16. TC-06 – Form QUÊN MẬT KHẨU đã nhập email*

| [CHÈN ẢNH – Hình 6-17] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** Chưa đăng nhập |
| **Màn hình / URL:** `/reset-password?email=...` |
| **Cần thấy trong ảnh:** Form XÁC THỰC MÃ OTP kèm thông báo gửi OTP thành công. |
| **Tên tệp gợi ý:** `tc06_reset.png` |
*Hình 6-17. TC-06 – Form XÁC THỰC MÃ OTP kèm thông báo gửi OTP thành công*

| [CHÈN ẢNH – Hình 6-18] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** (Gmail của bạn) |
| **Màn hình / URL:** Hộp thư đến |
| **Cần thấy trong ảnh:** Email chứa mã OTP 6 chữ số (che bớt thông tin nhạy cảm nếu cần). |
| **Tên tệp gợi ý:** `tc06_mail.png` |
*Hình 6-18. TC-06 – Email chứa mã OTP 6 chữ số*

| [CHÈN ẢNH – Hình 6-19] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** Chưa đăng nhập |
| **Màn hình / URL:** `/login?resetSuccess=true` |
| **Cần thấy trong ảnh:** Banner "Đặt lại mật khẩu thành công! Vui lòng đăng nhập bằng mật khẩu mới." |
| **Tên tệp gợi ý:** `tc06_success.png` |
*Hình 6-19. TC-06 – Banner Đặt lại mật khẩu thành công*

---

##### Bảng 6-11. Test Case TC-07: Quên mật khẩu – các trường hợp ngoại lệ
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-07 |
| **Chức năng** | Quên mật khẩu / Xác thực OTP (`PasswordResetService`) |
| **Mục tiêu** | Kiểm tra hệ thống xử lý đúng khi email không tồn tại, tài khoản đăng nhập bằng Google, OTP sai hoặc OTP hết hạn (quá 5 phút). |
| **Tài khoản thực hiện** | Chưa đăng nhập (khách vãng lai) |
| **Tiền điều kiện** | Tài khoản `user` liên kết Google (`auth_provider = 'GOOGLE'`). Đã yêu cầu OTP cho một email hợp lệ để thử trường hợp OTP sai/hết hạn. |
| **Dữ liệu đầu vào** | Ca 1: email `abc@khongtontai.com`<br>Ca 2: email `user@the4bookstore.vn` (tài khoản Google)<br>Ca 3: OTP nhập `000000` (sai)<br>Ca 4: OTP đúng nhưng nhập sau hơn 5 phút |
| **Các bước thực hiện** | 1. Ca 1, 2: nhập email tại `/forgot-password`, bấm "Gửi mã xác thực OTP".<br>2. Ca 3, 4: tại trang xác thực nhập OTP tương ứng cùng mật khẩu mới hợp lệ, bấm "Xác nhận & Đổi mật khẩu". |
| **Kết quả mong đợi** | Ca 1: "Không tìm thấy tài khoản liên kết với email này!"<br>Ca 2: "Tài khoản của bạn được liên kết và đăng nhập bằng Google. Vui lòng sử dụng tính năng 'Đăng nhập bằng Google' để tiếp tục."<br>Ca 3: "Mã OTP không chính xác hoặc đã được sử dụng!"<br>Ca 4: "Mã OTP đã hết hạn (chỉ có hiệu lực trong 5 phút). Vui lòng bấm 'Gửi lại mã'!"<br>Mật khẩu không bị thay đổi ở mọi ca. |
| **Kết quả thực tế** | Cả 4 ca đều bị từ chối với đúng thông báo, mật khẩu được giữ nguyên vẹn. |
| **Ảnh minh chứng** | Hình 6-20, Hình 6-21 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-20] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** Chưa đăng nhập |
| **Màn hình / URL:** `/forgot-password` (Ca 2) |
| **Cần thấy trong ảnh:** Thông báo yêu cầu dùng Đăng nhập bằng Google. |
| **Tên tệp gợi ý:** `tc07_google.png` |
*Hình 6-20. TC-07 – Thông báo yêu cầu dùng Đăng nhập bằng Google*

| [CHÈN ẢNH – Hình 6-21] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** Chưa đăng nhập |
| **Màn hình / URL:** `/reset-password` (Ca 3) |
| **Cần thấy trong ảnh:** Khung lỗi "Mã OTP không chính xác hoặc đã được sử dụng!" |
| **Tên tệp gợi ý:** `tc07_otp_sai.png` |
*Hình 6-21. TC-07 – Khung lỗi Mã OTP không chính xác hoặc đã được sử dụng*

---

#### B. Nhóm chức năng Mua sắm của Khách hàng

##### Bảng 6-12. Test Case TC-08: Tìm kiếm sản phẩm theo từ khóa
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-08 |
| **Chức năng** | Tìm kiếm sản phẩm (`products/list.html` – `/san-pham?q=...`) |
| **Mục tiêu** | Kiểm tra tìm kiếm theo tên, tác giả và trường hợp không có kết quả. |
| **Tài khoản thực hiện** | Chưa đăng nhập (khách vãng lai) |
| **Tiền điều kiện** | CSDL có sách "Dune - Xứ Cát" (245.000đ) và "Sapiens: Lược Sử Loài Người" (tác giả Yuval Noah Harari). |
| **Dữ liệu đầu vào** | Ca 1: từ khóa `Dune`<br>Ca 2: từ khóa `Harari`<br>Ca 3: từ khóa `xyzabc123` |
| **Các bước thực hiện** | 1. Mở trang chủ, nhập từ khóa vào ô tìm kiếm "Tìm theo tên sản phẩm, tác giả, NXB, ISBN..." và bấm tìm.<br>2. Quan sát danh sách và dòng "Hiển thị N sản phẩm".<br>3. Lặp lại cho các ca còn lại. |
| **Kết quả mong đợi** | Ca 1: hiển thị "Dune - Xứ Cát" với giá 245.000đ.<br>Ca 2: hiển thị "Sapiens: Lược Sử Loài Người".<br>Ca 3: hiển thị "Không tìm thấy sản phẩm" kèm gợi ý "Thử từ khóa khác hoặc điều chỉnh lại bộ lọc" và nút "Xem tất cả sản phẩm". |
| **Kết quả thực tế** | Kết quả tìm kiếm chính xác theo từng ca; ca không có kết quả hiển thị đúng trạng thái rỗng. |
| **Ảnh minh chứng** | Hình 6-22, Hình 6-23 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-22] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** Chưa đăng nhập |
| **Màn hình / URL:** `/san-pham?q=Dune` |
| **Cần thấy trong ảnh:** Danh sách kết quả có thẻ sách Dune - Xứ Cát và ô tìm kiếm chứa từ khóa. |
| **Tên tệp gợi ý:** `tc08_dune.png` |
*Hình 6-22. TC-08 – Danh sách kết quả có thẻ sách Dune - Xứ Cát và ô tìm kiếm chứa từ khóa*

| [CHÈN ẢNH – Hình 6-23] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** Chưa đăng nhập |
| **Màn hình / URL:** `/san-pham?q=xyzabc123` |
| **Cần thấy trong ảnh:** Màn hình "Không tìm thấy sản phẩm". |
| **Tên tệp gợi ý:** `tc08_rong.png` |
*Hình 6-23. TC-08 – Màn hình Không tìm thấy sản phẩm*

---

##### Bảng 6-13. Test Case TC-09: Lọc theo danh mục và sắp xếp theo giá
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-09 |
| **Chức năng** | Danh mục sách và sắp xếp (`/san-pham/sach`, `/san-pham/van-phong-pham`) |
| **Mục tiêu** | Kiểm tra lọc theo nhóm sản phẩm, danh mục con và sắp xếp Giá tăng dần / Giá giảm dần. |
| **Tài khoản thực hiện** | Chưa đăng nhập (khách vãng lai) |
| **Tiền điều kiện** | Danh mục "Tiểu Thuyết & Văn Học" có các sách: Cho tôi xin một vé đi tuổi thơ (80.000đ), Sách Mắt Biếc (87.000đ), Cây Cam Ngọt Của Tôi (108.000đ), Sách Test Ảnh Cloudinary Thật (185.000đ), The Midnight Library (189.000đ). |
| **Dữ liệu đầu vào** | Nhóm: Sách; Danh mục: Tiểu Thuyết & Văn Học<br>Sắp xếp: Giá tăng dần, sau đó Giá giảm dần<br>Nhóm: Văn phòng phẩm |
| **Các bước thực hiện** | 1. Mở `/san-pham`, chọn tab "Sách" rồi chọn danh mục "Tiểu Thuyết & Văn Học".<br>2. Chọn sắp xếp "Giá tăng dần", ghi nhận thứ tự.<br>3. Chọn "Giá giảm dần", ghi nhận thứ tự.<br>4. Chọn tab "Văn phòng phẩm". |
| **Kết quả mong đợi** | Chỉ hiển thị sách thuộc danh mục đã chọn.<br>Giá tăng dần: 80.000 → 87.000 → 108.000 → 185.000 → 189.000 (đ); Giá giảm dần thì ngược lại.<br>Tab Văn phòng phẩm hiển thị Sổ Tay, Bút Máy, Bookmark, Đèn Kẹp... không lẫn sách. |
| **Kết quả thực tế** | Danh sách lọc và sắp xếp đúng thứ tự giá theo từng lựa chọn. |
| **Ảnh minh chứng** | Hình 6-24, Hình 6-25, Hình 6-26 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-24] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** Chưa đăng nhập |
| **Màn hình / URL:** `/san-pham/sach?danhMuc=1&sort=price_asc` |
| **Cần thấy trong ảnh:** Danh sách sách theo giá tăng dần, ô chọn sắp xếp hiển thị "Giá tăng dần". |
| **Tên tệp gợi ý:** `tc09_tang.png` |
*Hình 6-24. TC-09 – Danh sách sách theo giá tăng dần, ô chọn sắp xếp hiển thị "Giá tăng dần"*

| [CHÈN ẢNH – Hình 6-25] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** Chưa đăng nhập |
| **Màn hình / URL:** `/san-pham/sach?danhMuc=1&sort=price_desc` |
| **Cần thấy trong ảnh:** Danh sách theo giá giảm dần. |
| **Tên tệp gợi ý:** `tc09_giam.png` |
*Hình 6-25. TC-09 – Danh sách theo giá giảm dần*

| [CHÈN ẢNH – Hình 6-26] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** Chưa đăng nhập |
| **Màn hình / URL:** `/san-pham/van-phong-pham` |
| **Cần thấy trong ảnh:** Danh sách văn phòng phẩm. |
| **Tên tệp gợi ý:** `tc09_vpp.png` |
*Hình 6-26. TC-09 – Danh sách văn phòng phẩm*

---

##### Bảng 6-14. Test Case TC-10: Quản lý giỏ hàng (thêm, giới hạn tồn kho, hết hàng)
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-10 |
| **Chức năng** | Giỏ hàng (`cart/cart.html` – `/gio-hang`, `GioHangService`) |
| **Mục tiêu** | Kiểm tra thêm sản phẩm, tự giới hạn số lượng theo tồn kho, chặn sản phẩm hết hàng và hiển thị tiến trình Freeship. |
| **Tài khoản thực hiện** | `user` |
| **Tiền điều kiện** | Đăng nhập `user`, giỏ hàng trống. Tồn kho: Dune = 17, Tâm Lý Học Về Tiền = 2, Atomic Habits = 0 (`HetHang`). |
| **Dữ liệu đầu vào** | Ca 1: thêm Dune - Xứ Cát, số lượng 2<br>Ca 2: thêm Tâm Lý Học Về Tiền, số lượng 5<br>Ca 3: thêm Atomic Habits - Thay Đổi Tí Hon<br>Ca 4: giảm số lượng Dune xuống 1, sau đó xóa Tâm Lý Học Về Tiền |
| **Các bước thực hiện** | 1. Mở chi tiết từng sách và thêm vào giỏ.<br>2. Mở `/gio-hang`, quan sát số lượng, thành tiền, thanh Freeship.<br>3. Dùng nút + / - và biểu tượng xóa. |
| **Kết quả mong đợi** | Ca 1: "Đã thêm sản phẩm vào giỏ hàng!"; thành tiền 490.000đ; thanh Freeship báo đủ điều kiện (mức 350.000đ).<br>Ca 2: số lượng trong giỏ bị giới hạn bằng tồn kho = 2.<br>Ca 3: không thêm được, báo "Sản phẩm đã hết hàng: Atomic Habits - Thay Đổi Tí Hon" (hoặc nút thêm bị vô hiệu hóa).<br>Ca 4: cập nhật số lượng và xóa thành công, tổng tiền tính lại đúng. |
| **Kết quả thực tế** | Giỏ hàng xử lý đúng từng ca, tổng tiền và thanh Freeship cập nhật chính xác. |
| **Ảnh minh chứng** | Hình 6-27, Hình 6-28 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-27] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `user` |
| **Màn hình / URL:** `/gio-hang` |
| **Cần thấy trong ảnh:** Giỏ có Dune x2 (490.000đ), Tâm Lý Học Về Tiền x2 và thanh Freeship. |
| **Tên tệp gợi ý:** `tc10_gio.png` |
*Hình 6-27. TC-10 – Giỏ có Dune x2 (490.000đ), Tâm Lý Học Về Tiền x2 và thanh Freeship*

| [CHÈN ẢNH – Hình 6-28] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `user` |
| **Màn hình / URL:** `/san-pham/{mã Atomic Habits}` |
| **Cần thấy trong ảnh:** Thông báo hết hàng khi thêm Atomic Habits. |
| **Tên tệp gợi ý:** `tc10_hethang.png` |
*Hình 6-28. TC-10 – Thông báo hết hàng khi thêm Atomic Habits*

---

##### Bảng 6-15. Test Case TC-11: Áp dụng mã giảm giá (voucher)
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-11 |
| **Chức năng** | Mã giảm giá trong giỏ hàng (API `/api/gio-hang/kiem-tra-voucher`) |
| **Mục tiêu** | Kiểm tra tính giảm giá theo phần trăm có trần mức tối đa, từ chối mã hết hạn và mã không tồn tại. |
| **Tài khoản thực hiện** | `user` |
| **Tiền điều kiện** | Giỏ hàng có Dune x1 (245.000đ) và The Midnight Library x1 (189.000đ): tạm tính 434.000đ.<br>Mã `KM-UGW9SZ2G` (Khai Trương): giảm 20%, tối đa 50.000đ, hiệu lực đến 02/11/2026.<br>Mã `THEFOUR` đã hết hạn ngày 13/10/2026 (xem tại `/admin/promotions`). |
| **Dữ liệu đầu vào** | Ca 1: `KM-UGW9SZ2G`<br>Ca 2: `THEFOUR`<br>Ca 3: `ABC123` |
| **Các bước thực hiện** | 1. Tại khung "Mã Giảm Giá & Khuyến Mãi" nhập mã, bấm áp dụng.<br>2. Ghi nhận thông báo và dòng "Giảm giá voucher", "Tổng Thanh Toán". |
| **Kết quả mong đợi** | Ca 1: "Áp dụng thành công mã [KM-UGW9SZ2G] - Khai Trương"; giảm 50.000đ (20% = 86.800đ nhưng bị giới hạn mức trần 50.000đ); tổng thanh toán 384.000đ.<br>Ca 2: "Mã giảm giá đã hết hạn sử dụng".<br>Ca 3: "Mã giảm giá không tồn tại hoặc không hợp lệ!" |
| **Kết quả thực tế** | Voucher hợp lệ được áp đúng mức trần, hai mã không hợp lệ bị từ chối đúng thông báo. |
| **Ảnh minh chứng** | Hình 6-29, Hình 6-30 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-29] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `user` |
| **Màn hình / URL:** `/gio-hang` |
| **Cần thấy trong ảnh:** Khung voucher báo áp dụng thành công, dòng giảm giá -50.000đ và tổng 384.000đ. |
| **Tên tệp gợi ý:** `tc11_ok.png` |
*Hình 6-29. TC-11 – Khung voucher báo áp dụng thành công, dòng giảm giá -50.000đ và tổng 384.000đ*

| [CHÈN ẢNH – Hình 6-30] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `user` |
| **Màn hình / URL:** `/gio-hang` |
| **Cần thấy trong ảnh:** Thông báo lỗi khi nhập mã THEFOUR (hết hạn). |
| **Tên tệp gợi ý:** `tc11_hethan.png` |
*Hình 6-30. TC-11 – Thông báo lỗi khi nhập mã THEFOUR (hết hạn)*

---

##### Bảng 6-16. Test Case TC-12: Đặt hàng thanh toán khi nhận hàng (COD)
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-12 |
| **Chức năng** | Đặt hàng (`CartController.placeOrder`, `DonHangService.createOrder`) |
| **Mục tiêu** | Kiểm tra tạo đơn hàng, trừ tồn kho an toàn, tính phí sàn 5% và thực nhận của shop, xóa sản phẩm đã đặt khỏi giỏ. |
| **Tài khoản thực hiện** | `user` |
| **Tiền điều kiện** | Giỏ hàng chỉ có Cây Cam Ngọt Của Tôi (Shop Fahasa) x4 = 432.000đ (≥ 350.000đ: miễn phí vận chuyển). Tồn kho sản phẩm trước khi đặt: 44 cuốn. Shop Fahasa có chiết khấu 5%. |
| **Dữ liệu đầu vào** | Nhà vận chuyển: Giao Hàng Nhanh (GHN)<br>Phương thức: Tiền mặt (COD)<br>Địa chỉ giao: chọn địa chỉ mặc định / nhập mới |
| **Các bước thực hiện** | 1. Tại `/gio-hang` tích chọn sản phẩm, bấm "Tiến Hành Thanh Toán".<br>2. Trong popup "Thông Tin Thanh Toán & Đặt Hàng": chọn địa chỉ, GHN, "Tiền mặt (COD)".<br>3. Bấm "Xác Nhận Đặt Hàng".<br>4. Vào `/don-hang` xem đơn vừa tạo.<br>5. Truy vấn bảng `don_hang`, `san_pham`. |
| **Kết quả mong đợi** | Hiển thị "Đặt Hàng Thành Công!" kèm mã đơn dạng `#TB-{id}` (hoặc mã đơn `{id}`).<br>`don_hang`: `trang_thai = 'ChoXuLy'`, `trang_thai_thanh_toan = 'ChuaThanhToan'`, `ma_shop = 3`, `tong_tien = 432000`, `tien_phi_san = 21600`, `tien_thuc_nhan_shop = 410400`.<br>`san_pham`: `so_luong_ton` giảm từ 44 xuống 40; `so_luong_da_ban` tăng 4.<br>Giỏ hàng không còn sản phẩm đã đặt; bảng `thanh_toan` có bản ghi mới trạng thái `ChoThanhToan`. |
| **Kết quả thực tế** | Đơn hàng được tạo đúng trạng thái, số tiền phí sàn và thực nhận chính xác, tồn kho trừ đúng 4 cuốn. |
| **Ảnh minh chứng** | Hình 6-31, Hình 6-32, Hình 6-33 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-31] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `user` |
| **Màn hình / URL:** `/gio-hang` (popup Thông Tin Thanh Toán & Đặt Hàng) |
| **Cần thấy trong ảnh:** Địa chỉ, GHN, COD đã chọn, tổng thanh toán 432.000đ. |
| **Tên tệp gợi ý:** `tc12_modal.png` |
*Hình 6-31. TC-12 – Địa chỉ, GHN, COD đã chọn, tổng thanh toán 432.000đ*

| [CHÈN ẢNH – Hình 6-32] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `user` |
| **Màn hình / URL:** `/gio-hang?datHangThanhCong=true` |
| **Cần thấy trong ảnh:** Khung "Đặt Hàng Thành Công!" và mã đơn #TB-... |
| **Tên tệp gợi ý:** `tc12_thanhcong.png` |
*Hình 6-32. TC-12 – Khung "Đặt Hàng Thành Công!" và mã đơn #TB-...*

| [CHÈN ẢNH – Hình 6-33] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** (MySQL Workbench) |
| **Màn hình / URL:** `SELECT madh, trang_thai, trang_thai_thanh_toan, tong_tien, tien_phi_san, tien_thuc_nhan_shop FROM don_hang ORDER BY madh DESC LIMIT 1;` |
| **Cần thấy trong ảnh:** Các cột `trang_thai`, `trang_thai_thanh_toan`, `tong_tien`, `tien_phi_san`, `tien_thuc_nhan_shop`. |
| **Tên tệp gợi ý:** `tc12_db.png` |
*Hình 6-33. TC-12 – Các cột trang_thai, tong_tien, tien_phi_san, tien_thuc_nhan_shop*

---

##### Bảng 6-17. Test Case TC-13: Đặt hàng thanh toán VietQR và xác nhận đã nhận tiền
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-13 |
| **Chức năng** | Thanh toán VietQR (`order/detail.html` – `/don-hang/{id}`, `VietQRService`, `OrderController`) |
| **Mục tiêu** | Kiểm tra sinh mã VietQR động đúng số tiền và nội dung chuyển khoản, luồng báo đã chuyển tiền và xác nhận của Admin/Shop. |
| **Tài khoản thực hiện** | `user` (đặt hàng và báo chuyển tiền); `admin` (xác nhận nhận tiền) |
| **Tiền điều kiện** | Giỏ hàng có Sapiens x1 (265.000đ) và Cho tôi xin một vé đi tuổi thơ x2 (160.000đ) = 425.000đ, cùng shop The4BookStore Official. Cấu hình `vietqr.*` hợp lệ (Timo, STK: 9021308251034, THE4BOOKSTORE). |
| **Dữ liệu đầu vào** | Phương thức thanh toán: Chuyển khoản VietQR<br>Nhà vận chuyển: GHN |
| **Các bước thực hiện** | 1. `user` chọn "Chuyển khoản VietQR" và bấm "Xác Nhận Đặt Hàng".<br>2. Quan sát trang chi tiết đơn với khối "Quét Mã VietQR Để Thanh Toán Đơn Hàng".<br>3. Bấm "Tôi Đã Chuyển Tiền Thành Công".<br>4. Đăng xuất, đăng nhập `admin`, mở `/don-hang/{mã đơn}`.<br>5. Bấm "[Admin/Shop] Xác Nhận Đã Nhận Tiền" và đồng ý popup xác nhận. |
| **Kết quả mong đợi** | Chuyển tới `/don-hang/{id}`, thông báo "Đặt hàng thành công! Vui lòng quét mã VietQR bên dưới để thanh toán đơn hàng #TB-{id}".<br>Mã QR hiển thị cùng ngân hàng, số tài khoản, tên chủ tài khoản, số tiền 425.000đ, nội dung chuyển khoản `DH{id}`; `don_hang.trang_thai = 'ChoThanhToan'`.<br>Sau khi `user` báo chuyển tiền: "Hệ thống đã ghi nhận thông báo chuyển tiền của bạn. Nhân viên / Shop sẽ đối soát và xử lý đơn ngay!"; `trang_thai_thanh_toan = 'ChoDuyetThanhToan'`.<br>Sau khi `admin` xác nhận: "Đã xác nhận thanh toán thành công cho đơn hàng #TB-{id}!"; `trang_thai = 'ChoXuLy'`, `trang_thai_thanh_toan = 'DaThanhToan'`. |
| **Kết quả thực tế** | Mã VietQR hiển thị đúng số tiền và nội dung; luồng báo chuyển tiền và xác nhận của admin cập nhật đúng trạng thái. |
| **Ảnh minh chứng** | Hình 6-34, Hình 6-35, Hình 6-36 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-34] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `user` |
| **Màn hình / URL:** `/don-hang/{mã đơn}` |
| **Cần thấy trong ảnh:** Khối VietQR: mã QR, ngân hàng, số tài khoản, số tiền 425.000đ, nội dung DH{mã đơn}. |
| **Tên tệp gợi ý:** `tc13_qr.png` |
*Hình 6-34. TC-13 – Khối VietQR: mã QR, ngân hàng, số tài khoản, số tiền 425.000đ, nội dung DH{mã đơn}*

| [CHÈN ẢNH – Hình 6-35] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `user` |
| **Màn hình / URL:** `/don-hang/{mã đơn}` |
| **Cần thấy trong ảnh:** Thông báo đã ghi nhận chuyển tiền / dòng "Đang chờ đối soát chuyển tiền từ ngân hàng..." |
| **Tên tệp gợi ý:** `tc13_baochuyen.png` |
*Hình 6-35. TC-13 – Thông báo đã ghi nhận chuyển tiền / dòng Đang chờ đối soát chuyển tiền từ ngân hàng...*

| [CHÈN ẢNH – Hình 6-36] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `admin` |
| **Màn hình / URL:** `/don-hang/{mã đơn}` |
| **Cần thấy trong ảnh:** Nút "[Admin/Shop] Xác Nhận Đã Nhận Tiền" và thông báo xác nhận thành công, trạng thái thanh toán "Đã thanh toán". |
| **Tên tệp gợi ý:** `tc13_admin.png` |
*Hình 6-36. TC-13 – Nút [Admin/Shop] Xác Nhận Đã Nhận Tiền và thông báo xác nhận thành công*

---

##### Bảng 6-18. Test Case TC-14: Hủy đơn hàng mới và hoàn lại tồn kho
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-14 |
| **Chức năng** | Hủy đơn (`OrderController.cancelOrder`, `DonHangService.cancelOrder`) |
| **Mục tiêu** | Kiểm tra khách hủy được đơn ở trạng thái Đơn mới, tồn kho và số lượng đã bán được hoàn lại, thanh toán chuyển sang hoàn tiền. |
| **Tài khoản thực hiện** | `user` |
| **Tiền điều kiện** | Đơn của TC-13 đang ở trạng thái `ChoXuLy` và đã thanh toán (`DaThanhToan`). Ghi lại tồn kho hiện tại của Sapiens và Cho tôi xin một vé đi tuổi thơ. |
| **Dữ liệu đầu vào** | Lý do hủy: `Đặt nhầm sách` |
| **Các bước thực hiện** | 1. Mở `/don-hang/{mã đơn}`.<br>2. Bấm nút hủy đơn hàng, nhập lý do và xác nhận.<br>3. Kiểm tra trạng thái đơn và tồn kho trong bảng `san_pham`. |
| **Kết quả mong đợi** | Hiển thị "Đã hủy đơn hàng #TB-{id} thành công!"; trạng thái "Đã hủy".<br>`don_hang`: `trang_thai = 'DaHuy'`, `trang_thai_thanh_toan = 'DaHoanTien'`, `ly_do_huy = 'Đặt nhầm sách'`.<br>Tồn kho Sapiens +1, Cho tôi xin một vé đi tuổi thơ +2; `so_luong_da_ban` giảm tương ứng. |
| **Kết quả thực tế** | Đơn được hủy, tồn kho và số lượng đã bán được hoàn lại chính xác. |
| **Ảnh minh chứng** | Hình 6-37, Hình 6-38 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-37] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `user` |
| **Màn hình / URL:** `/don-hang/{mã đơn}` |
| **Cần thấy trong ảnh:** Thông báo hủy thành công và trạng thái "Đã hủy", lý do hủy. |
| **Tên tệp gợi ý:** `tc14_huy.png` |
*Hình 6-37. TC-14 – Thông báo hủy thành công và trạng thái "Đã hủy", lý do hủy*

| [CHÈN ẢNH – Hình 6-38] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `user` |
| **Màn hình / URL:** `/don-hang` |
| **Cần thấy trong ảnh:** Danh sách đơn, tab "Đã hủy" có đơn vừa hủy. |
| **Tên tệp gợi ý:** `tc14_ds.png` |
*Hình 6-38. TC-14 – Danh sách đơn, tab Đã hủy có đơn vừa hủy*

---

#### C. Nhóm chức năng của Người bán (Vendor)

##### Bảng 6-19. Test Case TC-15: Đăng ký mở gian hàng và quản lý sàn phê duyệt
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-15 |
| **Chức năng** | Đăng ký shop (`vendor/register.html` – `/vendor/register`; `admin/shops.html` – `/admin/shops`) |
| **Mục tiêu** | Kiểm tra chu trình: nộp hồ sơ → trạng thái chờ duyệt → quản lý sàn duyệt → tài khoản được nâng thành VENDOR. |
| **Tài khoản thực hiện** | `khach_test01` (nộp hồ sơ); `manager` (phê duyệt) |
| **Tiền điều kiện** | Tài khoản `khach_test01` chưa có shop (mật khẩu hiện tại: `654321` nếu đã làm TC-06, ngược lại `123456`). Chưa có shop tên "Nhà Sách Tri Thức Trẻ". |
| **Dữ liệu đầu vào** | Tên gian hàng: `Nhà Sách Tri Thức Trẻ`<br>Mô tả: `Chuyên sách kỹ năng và công nghệ thông tin`<br>Số điện thoại: `0909123456`<br>Email gian hàng: `trithuctre@gmail.com`<br>Địa chỉ: `123 Nguyễn Văn Cừ, Q5, TP.HCM` |
| **Các bước thực hiện** | 1. Đăng nhập `khach_test01`, mở `/vendor/register`, điền form, bấm "Gửi Hồ Sơ Đăng Ký Gian Hàng".<br>2. Quan sát trang "Hồ Sơ Đang Chờ Phê Duyệt".<br>3. Đăng nhập `manager`, mở `/admin/shops`, tìm shop mới (trạng thái Chờ duyệt) và bấm "Duyệt".<br>4. Đăng xuất, đăng nhập lại `khach_test01`. |
| **Kết quả mong đợi** | Chuyển `/vendor/pending`, thông báo "Đăng ký mở gian hàng thành công! Hồ sơ của bạn đã được gửi và đang chờ Ban Quản Trị phê duyệt."; shop có `trang_thai = 'ChoDuyet'`, `chiet_khau_phan_tram = 5.00`.<br>Sau khi duyệt: "Đã duyệt và kích hoạt gian hàng #{id}"; shop `trang_thai = 'HoatDong'`; `tai_khoan.vai_tro` của `khach_test01 = 'VENDOR'`.<br>Đăng nhập lại `khach_test01` được chuyển thẳng tới `/vendor/dashboard`. |
| **Kết quả thực tế** | Hồ sơ được tạo ở trạng thái chờ duyệt; sau khi duyệt, tài khoản trở thành VENDOR và vào được Kênh người bán. |
| **Ảnh minh chứng** | Hình 6-39, Hình 6-40, Hình 6-41, Hình 6-42 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-39] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `khach_test01` |
| **Màn hình / URL:** `/vendor/register` |
| **Cần thấy trong ảnh:** Form đăng ký gian hàng đã điền đủ thông tin. |
| **Tên tệp gợi ý:** `tc15_form.png` |
*Hình 6-39. TC-15 – Form đăng ký gian hàng đã điền đủ thông tin*

| [CHÈN ẢNH – Hình 6-40] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `khach_test01` |
| **Màn hình / URL:** `/vendor/pending` |
| **Cần thấy trong ảnh:** Trang "Hồ Sơ Đang Chờ Phê Duyệt". |
| **Tên tệp gợi ý:** `tc15_pending.png` |
*Hình 6-40. TC-15 – Trang Hồ Sơ Đang Chờ Phê Duyệt*

| [CHÈN ẢNH – Hình 6-41] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `manager` |
| **Màn hình / URL:** `/admin/shops` |
| **Cần thấy trong ảnh:** Dòng shop "Nhà Sách Tri Thức Trẻ" trạng thái Chờ duyệt và nút "Duyệt". |
| **Tên tệp gợi ý:** `tc15_duyet.png` |
*Hình 6-41. TC-15 – Dòng shop "Nhà Sách Tri Thức Trẻ" trạng thái Chờ duyệt và nút Duyệt*

| [CHÈN ẢNH – Hình 6-42] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `khach_test01` (đăng nhập lại) |
| **Màn hình / URL:** `/vendor/dashboard` |
| **Cần thấy trong ảnh:** Kênh người bán sau khi được duyệt. |
| **Tên tệp gợi ý:** `tc15_dashboard.png` |
*Hình 6-42. TC-15 – Kênh người bán sau khi được duyệt*

---

##### Bảng 6-20. Test Case TC-16: Vendor đăng bán sách (upload ảnh Cloudinary) và Admin duyệt sản phẩm
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-16 |
| **Chức năng** | Đăng bán sách (`vendor/product_form.html` – `/vendor/products/add`; `admin/shop_products.html`) |
| **Mục tiêu** | Kiểm tra upload ảnh bìa lên Cloudinary, lưu URL HTTPS, sách mới ở trạng thái chờ duyệt và chỉ hiển thị cho khách sau khi quản lý sàn duyệt. |
| **Tài khoản thực hiện** | `vendor_demo` (đăng bán); `manager` (duyệt); khách vãng lai (kiểm tra hiển thị) |
| **Tiền điều kiện** | Cloudinary đã cấu hình trong `.env`. Chuẩn bị tệp `bia_sach_java.jpg` (khoảng 1.2MB, định dạng JPG). |
| **Dữ liệu đầu vào** | Tên sách: `Lập Trình Java Nâng Cao`<br>ISBN: để trống (hệ thống tự sinh)<br>Tác giả: `Nguyễn Văn A`; Nhà xuất bản: `NXB Thanh Niên`<br>Giá bán: `120000`; Danh mục: `Khoa Học & Lịch Sử`<br>Ảnh bìa: `bia_sach_java.jpg`; Trạng thái: `Đang mở bán` |
| **Các bước thực hiện** | 1. Đăng nhập `vendor_demo`, mở `/vendor/products/add`, điền form, chọn tệp ảnh.<br>2. Bấm "Lưu Cuốn Sách Này".<br>3. Khách vãng lai tìm "Java Nâng Cao" tại `/san-pham`.<br>4. Đăng nhập `manager`, mở `/admin/shops/2/products`, tab "Chờ kiểm duyệt", bấm "Duyệt SP".<br>5. Khách tìm lại sách. |
| **Kết quả mong đợi** | Hiển thị "Lưu sản phẩm thành công!" và chuyển về `/vendor/products`.<br>`san_pham`: `hinh_anh` dạng `https://res.cloudinary.com/.../the4bookstore/books/...`; `isbn` tự sinh `BK-xxxxxxxx`; `ma_shop = 2`; `trang_thai_khoa = 'ChoDuyet'`; `so_luong_ton = 0`.<br>Trước khi duyệt: khách không tìm thấy sách.<br>Sau khi duyệt: "Đã DUYỆT thành công sản phẩm #{id}, sản phẩm đã được phép bày bán trên sàn!"; khách tìm thấy sách, ảnh bìa hiển thị sắc nét. |
| **Kết quả thực tế** | Ảnh tải lên Cloudinary thành công, sách ở trạng thái chờ duyệt và hiển thị cho khách sau khi được duyệt. |
| **Ảnh minh chứng** | Hình 6-43, Hình 6-44, Hình 6-45, Hình 6-46 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-43] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `vendor_demo` |
| **Màn hình / URL:** `/vendor/products/add` |
| **Cần thấy trong ảnh:** Form đăng bán sách đã điền, mục Ảnh Bìa Sách hiển thị tên tệp đã chọn. |
| **Tên tệp gợi ý:** `tc16_form.png` |
*Hình 6-43. TC-16 – Form đăng bán sách đã điền, mục Ảnh Bìa Sách hiển thị tên tệp đã chọn*

| [CHÈN ẢNH – Hình 6-44] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** (MySQL Workbench) |
| **Màn hình / URL:** `SELECT masp, ten_sp, isbn, hinh_anh, trang_thai_khoa, ma_shop FROM san_pham ORDER BY masp DESC LIMIT 1;` |
| **Cần thấy trong ảnh:** Cột `hinh_anh` có URL `res.cloudinary.com`, `trang_thai_khoa = 'ChoDuyet'`. |
| **Tên tệp gợi ý:** `tc16_db.png` |
*Hình 6-44. TC-16 – Cột hinh_anh có URL res.cloudinary.com, trang_thai_khoa = ChoDuyet*

| [CHÈN ẢNH – Hình 6-45] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `manager` |
| **Màn hình / URL:** `/admin/shops/2/products` |
| **Cần thấy trong ảnh:** Sản phẩm ở tab "Chờ kiểm duyệt" và nút "Duyệt SP"; thông báo duyệt thành công. |
| **Tên tệp gợi ý:** `tc16_duyet.png` |
*Hình 6-45. TC-16 – Sản phẩm ở tab Chờ kiểm duyệt và nút Duyệt SP*

| [CHÈN ẢNH – Hình 6-46] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** Chưa đăng nhập |
| **Màn hình / URL:** `/san-pham?q=Java` |
| **Cần thấy trong ảnh:** Trang kết quả hiển thị sách cùng ảnh bìa từ Cloudinary. |
| **Tên tệp gợi ý:** `tc16_hienthi.png` |
*Hình 6-46. TC-16 – Trang kết quả hiển thị sách cùng ảnh bìa từ Cloudinary*

---

##### Bảng 6-21. Test Case TC-17: Lập phiếu nhập hàng và tự động cộng dồn tồn kho
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-17 |
| **Chức năng** | Nhập hàng (`vendor/import_form.html` – `/vendor/imports/create`, `VendorImportController`) |
| **Mục tiêu** | Kiểm tra tính tổng tiền vốn của phiếu nhập và cộng dồn số lượng tồn kho. |
| **Tài khoản thực hiện** | `vendor_fahasa` |
| **Tiền điều kiện** | Sách "Sách Mắt Biếc (Tái Bản 2019)" của Fahasa đang có tồn 38. |
| **Dữ liệu đầu vào** | Nguồn hàng: `NXB Trẻ`<br>Ghi chú: `Nhập bổ sung cuối tháng`<br>Sách: `Sách Mắt Biếc (Tái Bản 2019)`; số lượng nhập: `20`; đơn giá nhập: `50000` |
| **Các bước thực hiện** | 1. Đăng nhập `vendor_fahasa`, mở `/vendor/imports/create`.<br>2. Điền nguồn hàng, ghi chú, số lượng và đơn giá cho sách.<br>3. Kiểm tra "Tổng tiền vốn" trên form.<br>4. Bấm "Xác Nhận & Nhập Hàng Vào Kho".<br>5. Mở `/vendor/imports` xem phiếu và kiểm tra bảng `san_pham`. |
| **Kết quả mong đợi** | Tổng tiền vốn = 1.000.000đ.<br>Thông báo "Nhập hàng thành công! Đã tự động cộng dồn tồn kho cho 1 sản phẩm."<br>`phieu_nhap`: `trang_thai = 'DaNhap'`, `tong_tien = 1000000`.<br>`san_pham`: `so_luong_ton` tăng từ 38 lên 58. |
| **Kết quả thực tế** | Phiếu nhập được lưu đúng tổng tiền, tồn kho tăng chính xác 20 cuốn. |
| **Ảnh minh chứng** | Hình 6-47, Hình 6-48 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-47] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `vendor_fahasa` |
| **Màn hình / URL:** `/vendor/imports/create` |
| **Cần thấy trong ảnh:** Form phiếu nhập với dòng sách, số lượng 20, đơn giá 50.000, tổng tiền vốn 1.000.000đ. |
| **Tên tệp gợi ý:** `tc17_form.png` |
*Hình 6-47. TC-17 – Form phiếu nhập với dòng sách, số lượng 20, đơn giá 50.000, tổng tiền vốn 1.000.000đ*

| [CHÈN ẢNH – Hình 6-48] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `vendor_fahasa` |
| **Màn hình / URL:** `/vendor/imports` |
| **Cần thấy trong ảnh:** Danh sách phiếu nhập có phiếu vừa tạo. |
| **Tên tệp gợi ý:** `tc17_ds.png` |
*Hình 6-48. TC-17 – Danh sách phiếu nhập có phiếu vừa tạo*

---

##### Bảng 6-22. Test Case TC-18: Lập phiếu kiểm kê và cân bằng tồn kho
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-18 |
| **Chức năng** | Kiểm kê (`vendor/inventory_form.html` – `/vendor/inventory/create`) |
| **Mục tiêu** | Kiểm tra lưu chênh lệch giữa tồn hệ thống và thực tế, đồng bộ lại số lượng tồn của shop. |
| **Tài khoản thực hiện** | `vendor_fahasa` |
| **Tiền điều kiện** | Sách "Cây Cam Ngọt Của Tôi" có tồn hệ thống là N cuốn (ví dụ 40 sau TC-12). |
| **Dữ liệu đầu vào** | Ghi chú: `Kiểm kê định kỳ tháng 10`<br>Cây Cam Ngọt Của Tôi: số lượng thực tế = `N - 2`<br>Lý do: `Hư hỏng khi vận chuyển` |
| **Các bước thực hiện** | 1. Mở `/vendor/inventory/create`.<br>2. Nhập ghi chú, số lượng thực tế và lý do ở dòng sách trên; cột "Chênh lệch" hiển thị -2.<br>3. Bấm "Xác Nhận & Cân Bằng Kho".<br>4. Mở chi tiết biên bản và kiểm tra bảng `san_pham`. |
| **Kết quả mong đợi** | Thông báo "Lập phiếu kiểm kê #{id} và cân bằng ... sản phẩm thành công!"<br>`phieu_kiem_ke`: `trang_thai = 'DaCanHang'`; `chi_tiet_kiem_ke` lưu `so_luong_he_thong`, `so_luong_thuc_te` và `ly_do`.<br>`san_pham.so_luong_ton` của Cây Cam Ngọt Của Tôi bằng số lượng thực tế (`N - 2`). |
| **Kết quả thực tế** | Biên bản kiểm kê được lưu, tồn kho đồng bộ đúng số lượng thực tế. |
| **Ảnh minh chứng** | Hình 6-49, Hình 6-50 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-49] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `vendor_fahasa` |
| **Màn hình / URL:** `/vendor/inventory/create` |
| **Cần thấy trong ảnh:** Form kiểm kê với cột Chênh lệch -2 và lý do điều chỉnh. |
| **Tên tệp gợi ý:** `tc18_form.png` |
*Hình 6-49. TC-18 – Form kiểm kê với cột Chênh lệch -2 và lý do điều chỉnh*

| [CHÈN ẢNH – Hình 6-50] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `vendor_fahasa` |
| **Màn hình / URL:** `/vendor/inventory` |
| **Cần thấy trong ảnh:** Thông báo lập phiếu kiểm kê thành công. |
| **Tên tệp gợi ý:** `tc18_ok.png` |
*Hình 6-50. TC-18 – Thông báo lập phiếu kiểm kê thành công*

---

##### Bảng 6-23. Test Case TC-19: Xử lý đơn hàng của Shop đến khi khách xác nhận hoàn tất
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-19 |
| **Chức năng** | Quản lý đơn Shop (`vendor/orders.html` – `/vendor/orders`; `order/detail.html`) |
| **Mục tiêu** | Kiểm tra toàn bộ vòng đời đơn: xác nhận → bàn giao shipper → giao hàng → giao thành công (Escrow) → khách xác nhận → quyết toán phí sàn. |
| **Tài khoản thực hiện** | `vendor_fahasa` (xử lý đơn); `user` (xác nhận đã nhận hàng) |
| **Tiền điều kiện** | Đơn COD của TC-12 (432.000đ, Shop Fahasa) đang ở trạng thái Đơn mới (`ChoXuLy`). |
| **Dữ liệu đầu vào** | Đơn hàng: `#TB-{mã đơn TC-12}` |
| **Các bước thực hiện** | 1. `vendor_fahasa` mở `/vendor/orders`, tab "Đơn Mới", bấm "Xác Nhận Đơn (Tạo Mã VĐ)".<br>2. Lần lượt bấm "Bàn Giao Shipper (Đã Lấy Hàng)" → "Bắt Đầu Giao Hàng" → "Giao Thành Công (Vào Escrow)".<br>3. Đăng nhập `user`, mở `/don-hang/{mã đơn}`, bấm "Đã nhận được hàng".<br>4. Kiểm tra bảng `don_hang`. |
| **Kết quả mong đợi** | Lần lượt hiển thị: "Đã xác nhận đơn hàng #{id}"; "Đã bàn giao đơn hàng #{id} cho đơn vị vận chuyển."; "Đơn hàng #{id} đang trên đường giao."; "Đơn hàng #{id} đã giao thành công! Kích hoạt thời gian chờ khiếu nại (tiền trong Escrow)."<br>Đơn có mã vận đơn; phía `user` thấy trạng thái "Đã giao hàng (Chờ xác nhận)".<br>Sau khi `user` xác nhận: "Cảm ơn bạn đã xác nhận nhận hàng! Đơn hàng đã hoàn tất thành công..."; `trang_thai = 'HoanTat'`, `trang_thai_thanh_toan = 'DaThanhToan'`, `ngay_hoan_thanh` có giá trị.<br>`tien_phi_san = 21600` (5%), `tien_thuc_nhan_shop = 410400`. |
| **Kết quả thực tế** | Đơn chuyển trạng thái đúng thứ tự, quyết toán phí sàn và thực nhận của shop chính xác. |
| **Ảnh minh chứng** | Hình 6-51, Hình 6-52, Hình 6-53, Hình 6-54 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-51] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `vendor_fahasa` |
| **Màn hình / URL:** `/vendor/orders?status=...` |
| **Cần thấy trong ảnh:** Thẻ đơn hàng ở trạng thái Đơn Mới với nút "Xác Nhận Đơn (Tạo Mã VĐ)". |
| **Tên tệp gợi ý:** `tc19_moi.png` |
*Hình 6-51. TC-19 – Thẻ đơn hàng ở trạng thái Đơn Mới với nút Xác Nhận Đơn (Tạo Mã VĐ)*

| [CHÈN ẢNH – Hình 6-52] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `vendor_fahasa` |
| **Màn hình / URL:** `/vendor/orders` |
| **Cần thấy trong ảnh:** Đơn ở trạng thái "Đã Giao (Chờ Escrow)" và thông báo giao thành công. |
| **Tên tệp gợi ý:** `tc19_dagiao.png` |
*Hình 6-52. TC-19 – Đơn ở trạng thái Đã Giao (Chờ Escrow) và thông báo giao thành công*

| [CHÈN ẢNH – Hình 6-53] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `user` |
| **Màn hình / URL:** `/don-hang/{mã đơn}` |
| **Cần thấy trong ảnh:** Khối "Kiện hàng đã được giao thành công" và nút "Đã nhận được hàng". |
| **Tên tệp gợi ý:** `tc19_user.png` |
*Hình 6-53. TC-19 – Khối Kiện hàng đã được giao thành công và nút Đã nhận được hàng*

| [CHÈN ẢNH – Hình 6-54] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `user` |
| **Màn hình / URL:** `/don-hang/{mã đơn}` |
| **Cần thấy trong ảnh:** Trạng thái "Hoàn tất thành công" cùng thông báo cảm ơn. |
| **Tên tệp gợi ý:** `tc19_hoantat.png` |
*Hình 6-54. TC-19 – Trạng thái Hoàn tất thành công cùng thông báo cảm ơn*

---

#### D. Nhóm chức năng của Quản lý sàn và Quản trị viên

##### Bảng 6-24. Test Case TC-20: Khóa và mở khóa sản phẩm vi phạm
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-20 |
| **Chức năng** | Kiểm duyệt sản phẩm (`admin/shop_products.html` – `ManagerDisputeController`) |
| **Mục tiêu** | Kiểm tra sản phẩm bị khóa không còn truy cập được từ phía khách hàng và có thể mở khóa lại. |
| **Tài khoản thực hiện** | `manager` (khóa/mở khóa); khách vãng lai (kiểm tra) |
| **Tiền điều kiện** | Sách "Lập Trình Java Nâng Cao" (Shop 2) đã được duyệt ở TC-16. |
| **Dữ liệu đầu vào** | Sản phẩm: `Lập Trình Java Nâng Cao` |
| **Các bước thực hiện** | 1. `manager` mở `/admin/shops/2/products`, bấm "Khóa" ở sản phẩm.<br>2. Khách truy cập `/san-pham/{mã sách}` và tìm kiếm từ khóa "Java".<br>3. `manager` mở khóa lại sản phẩm và kiểm tra lại phía khách. |
| **Kết quả mong đợi** | Hiển thị "Đã khóa sản phẩm vi phạm #{id}"; `trang_thai_khoa = 'BiKhoaBoiAdmin'`.<br>Khách bị chuyển về `/san-pham` kèm thông báo "Sản phẩm này hiện đang tạm ngừng kinh doanh hoặc gian hàng đang bị tạm khóa!"; sách không còn trong kết quả tìm kiếm.<br>Sau khi mở khóa: "Đã mở khóa sản phẩm #{id}"; sách hiển thị lại bình thường. |
| **Kết quả thực tế** | Sản phẩm bị khóa được ẩn khỏi khách hàng và hiển thị lại sau khi mở khóa. |
| **Ảnh minh chứng** | Hình 6-55, Hình 6-56 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-55] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `manager` |
| **Màn hình / URL:** `/admin/shops/2/products` |
| **Cần thấy trong ảnh:** Sản phẩm với nhãn "Bị Khóa" và thông báo khóa thành công. |
| **Tên tệp gợi ý:** `tc20_khoa.png` |
*Hình 6-55. TC-20 – Sản phẩm với nhãn Bị Khóa và thông báo khóa thành công*

| [CHÈN ẢNH – Hình 6-56] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** Chưa đăng nhập |
| **Màn hình / URL:** `/san-pham` |
| **Cần thấy trong ảnh:** Thông báo "Sản phẩm này hiện đang tạm ngừng kinh doanh..." sau khi truy cập chi tiết sách bị khóa. |
| **Tên tệp gợi ý:** `tc20_khach.png` |
*Hình 6-56. TC-20 – Thông báo Sản phẩm này hiện đang tạm ngừng kinh doanh... sau khi truy cập chi tiết sách bị khóa*

---

##### Bảng 6-25. Test Case TC-21: Khóa tài khoản người dùng và ràng buộc quyền hạn
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-21 |
| **Chức năng** | Quản lý người dùng (`admin/users.html` – `/admin/users`, `AdminUserController`) |
| **Mục tiêu** | Kiểm tra tài khoản bị khóa không đăng nhập được và các ràng buộc phân quyền khi khóa tài khoản. |
| **Tài khoản thực hiện** | `admin`, `manager`, `khach_test01` |
| **Tiền điều kiện** | Tồn tại tài khoản `khach_test01` đang hoạt động. |
| **Dữ liệu đầu vào** | Ca 1: `admin` khóa `khach_test01`<br>Ca 2: `manager` khóa tài khoản `admin`<br>Ca 3: `admin` tự khóa chính mình |
| **Các bước thực hiện** | 1. `admin` mở `/admin/users`, tìm `khach_test01`, bấm "Khóa".<br>2. Đăng nhập `khach_test01`.<br>3. Thực hiện Ca 2 và Ca 3.<br>4. `admin` mở khóa lại `khach_test01`. |
| **Kết quả mong đợi** | Ca 1: khóa thành công; đăng nhập `khach_test01` bị chuyển `/login?locked=true` với thông báo "Tài khoản của bạn đã bị khóa!" và email liên hệ `nhasachthe4bookstore@gmail.com`.<br>Ca 2: "Quản lý (Manager) không có quyền khóa hoặc can thiệp tài khoản Quản trị viên cấp cao (Admin)!"<br>Ca 3: "Bạn không thể tự khóa tài khoản đang đăng nhập của chính mình!"<br>Sau khi mở khóa, `khach_test01` đăng nhập lại được bình thường. |
| **Kết quả thực tế** | Tài khoản bị khóa không đăng nhập được; các ca vượt quyền bị từ chối đúng thông báo. |
| **Ảnh minh chứng** | Hình 6-57, Hình 6-58, Hình 6-59 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-57] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `admin` |
| **Màn hình / URL:** `/admin/users` |
| **Cần thấy trong ảnh:** Danh sách người dùng với tài khoản `khach_test01` ở trạng thái bị khóa. |
| **Tên tệp gợi ý:** `tc21_khoa.png` |
*Hình 6-57. TC-21 – Danh sách người dùng với tài khoản khach_test01 ở trạng thái bị khóa*

| [CHÈN ẢNH – Hình 6-58] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `khach_test01` |
| **Màn hình / URL:** `/login?locked=true` |
| **Cần thấy trong ảnh:** Khung "Tài khoản của bạn đã bị khóa!" |
| **Tên tệp gợi ý:** `tc21_locked.png` |
*Hình 6-58. TC-21 – Khung Tài khoản của bạn đã bị khóa!*

| [CHÈN ẢNH – Hình 6-59] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `manager` |
| **Màn hình / URL:** `/admin/users` |
| **Cần thấy trong ảnh:** Thông báo lỗi khi manager khóa tài khoản admin. |
| **Tên tệp gợi ý:** `tc21_manager.png` |
*Hình 6-59. TC-21 – Thông báo lỗi khi manager khóa tài khoản admin*

---

##### Bảng 6-26. Test Case TC-22: Quản lý phân xử tranh chấp trả hàng - hoàn tiền
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-22 |
| **Chức năng** | Phân xử tranh chấp (`admin/disputes.html` – `/admin/disputes`, `ManagerDisputeService`) |
| **Mục tiêu** | Kiểm tra luồng từ yêu cầu trả hàng, shop từ chối, chuyển tranh chấp đến phán quyết hoàn tiền của quản lý sàn. |
| **Tài khoản thực hiện** | `user`, `vendor_fahasa`, `manager` |
| **Tiền điều kiện** | Tạo đơn COD mới Cây Cam Ngọt Của Tôi x4 (432.000đ), `vendor_fahasa` xử lý tới trạng thái "Đã giao" (như TC-19). Ghi lại tồn kho của sản phẩm sau khi đặt. |
| **Dữ liệu đầu vào** | Lý do trả hàng: `Sách bị rách gáy khi vận chuyển`<br>Lý do shop từ chối: `Sách còn nguyên vẹn khi bàn giao`<br>Phán quyết: `Chấp thuận khiếu nại của khách hàng, hoàn tiền 100%` |
| **Các bước thực hiện** | 1. `user` mở đơn, gửi "Yêu cầu Trả hàng - Hoàn tiền" với lý do.<br>2. `vendor_fahasa` mở tab "Trả Hàng", nhập lý do và bấm "Từ Chối (Khiếu Nại Lên Sàn)".<br>3. `manager` mở `/admin/disputes`, nhập ghi chú phán quyết.<br>4. Bấm "Phán Quyết: Hoàn Tiền Cho Khách".<br>5. Kiểm tra bảng `don_hang` và tồn kho. |
| **Kết quả mong đợi** | Bước 1: "Yêu cầu Trả hàng - Hoàn tiền đã gửi đến hệ thống xử lý thành công!"; `trang_thai = 'TraHangHoanTien'`.<br>Bước 2: "Đã từ chối trả hàng cho đơn #{id}. Đơn đã chuyển sang trạng thái Tranh Chấp để Quản Lý xem xét."<br>Bước 4: "Đã phán quyết tranh chấp đơn #{id}: Chấp nhận hoàn tiền cho khách hàng".<br>`don_hang`: `trang_thai = 'DaHoan'`, `trang_thai_thanh_toan = 'DaHoanTien'`, `tien_thuc_nhan_shop = 0`, `tien_phi_san = 0`, có `nguoi_xu_ly_tranh_chap` và `phan_quyet_tranh_chap`.<br>Tồn kho sản phẩm được hoàn lại 4 cuốn. |
| **Kết quả thực tế** | Đơn chuyển đúng các trạng thái, phán quyết được lưu kèm người xử lý, tiền thực nhận của shop về 0 và tồn kho được hoàn lại. |
| **Ảnh minh chứng** | Hình 6-60, Hình 6-61, Hình 6-62, Hình 6-63 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-60] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `user` |
| **Màn hình / URL:** `/don-hang/{mã đơn}` |
| **Cần thấy trong ảnh:** Biểu mẫu yêu cầu trả hàng và thông báo gửi thành công. |
| **Tên tệp gợi ý:** `tc22_yeucau.png` |
*Hình 6-60. TC-22 – Biểu mẫu yêu cầu trả hàng và thông báo gửi thành công*

| [CHÈN ẢNH – Hình 6-61] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `vendor_fahasa` |
| **Màn hình / URL:** `/vendor/orders` (tab Trả Hàng) |
| **Cần thấy trong ảnh:** Ô nhập lý do và nút "Từ Chối (Khiếu Nại Lên Sàn)". |
| **Tên tệp gợi ý:** `tc22_tuchoi.png` |
*Hình 6-61. TC-22 – Ô nhập lý do và nút Từ Chối (Khiếu Nại Lên Sàn)*

| [CHÈN ẢNH – Hình 6-62] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `manager` |
| **Màn hình / URL:** `/admin/disputes` |
| **Cần thấy trong ảnh:** Thẻ tranh chấp với lý do khách, lý do shop và hai nút phán quyết. |
| **Tên tệp gợi ý:** `tc22_phanxu.png` |
*Hình 6-62. TC-22 – Thẻ tranh chấp với lý do khách, lý do shop và hai nút phán quyết*

| [CHÈN ẢNH – Hình 6-63] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** (MySQL Workbench) |
| **Màn hình / URL:** `SELECT madh, trang_thai, trang_thai_thanh_toan, tien_thuc_nhan_shop, nguoi_xu_ly_tranh_chap, phan_quyet_tranh_chap FROM don_hang WHERE madh={mã đơn};` |
| **Cần thấy trong ảnh:** Các cột `trang_thai`, `trang_thai_thanh_toan`, `tien_thuc_nhan_shop`, `nguoi_xu_ly_tranh_chap`, `phan_quyet_tranh_chap`. |
| **Tên tệp gợi ý:** `tc22_db.png` |
*Hình 6-63. TC-22 – Các cột trang_thai, trang_thai_thanh_toan, tien_thuc_nhan_shop, nguoi_xu_ly_tranh_chap, phan_quyet_tranh_chap*

---

##### Bảng 6-27. Test Case TC-23: Cấu hình chiết khấu sàn (mặc định và theo từng shop)
| Thuộc tính | Nội dung kiểm thử |
| :--- | :--- |
| **Mã kiểm thử** | TC-23 |
| **Chức năng** | Chiết khấu sàn (`admin/platform_fees.html` – `/admin/platform-fees`; `admin/shops.html` – `/admin/shops`) |
| **Mục tiêu** | Kiểm tra lưu tỷ lệ chiết khấu mặc định và tỷ lệ riêng của shop, đơn mới tính phí sàn theo tỷ lệ riêng của shop. |
| **Tài khoản thực hiện** | `admin` (cấu hình); `user` (đặt đơn kiểm tra) |
| **Tiền điều kiện** | Tỷ lệ mặc định hiện tại 5.0% (`cau_hinh_he_thong`, khóa `PHI_SAN_MAC_DINH`); Shop Fahasa 5%. Giỏ hàng của `user` có Cây Cam Ngọt Của Tôi x4 (432.000đ). |
| **Dữ liệu đầu vào** | Tỷ lệ mặc định mới: `8.0`<br>Tỷ lệ riêng của Shop Fahasa: `8` |
| **Các bước thực hiện** | 1. `admin` mở `/admin/platform-fees`, nhập 8.0, bấm "Lưu Cấu Hình Chiết Khấu".<br>2. Mở `/admin/shops`, nhập 8 vào ô chiết khấu của Fahasa và lưu.<br>3. `user` đặt đơn COD từ giỏ hàng trên.<br>4. Kiểm tra `don_hang`.<br>5. Khôi phục Fahasa về 5%. |
| **Kết quả mong đợi** | Hiển thị "Cập nhật tỷ lệ chiết khấu sàn mặc định thành 8.0%"; `cau_hinh_he_thong.PHI_SAN_MAC_DINH = '8.0'`.<br>Hiển thị "Đã cập nhật tỷ lệ chiết khấu cho shop #3 thành 8%"; cột "Chiết khấu sàn" của Fahasa hiển thị 8%.<br>Đơn mới: `chiet_khau_app_phan_tram = 8`, `tien_phi_san = 34560`, `tien_thuc_nhan_shop = 397440`; các đơn cũ không bị thay đổi. |
| **Kết quả thực tế** | Cấu hình lưu thành công; đơn mới tính phí sàn đúng theo tỷ lệ riêng của shop, đơn cũ giữ nguyên. |
| **Ảnh minh chứng** | Hình 6-64, Hình 6-65, Hình 6-66 |
| **Đánh giá** | **ĐẠT (PASS)** |

| [CHÈN ẢNH – Hình 6-64] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `admin` |
| **Màn hình / URL:** `/admin/platform-fees` |
| **Cần thấy trong ảnh:** Form cấu hình với giá trị 8.0 và thông báo lưu thành công. |
| **Tên tệp gợi ý:** `tc23_macdinh.png` |
*Hình 6-64. TC-23 – Form cấu hình với giá trị 8.0 và thông báo lưu thành công*

| [CHÈN ẢNH – Hình 6-65] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** `admin` |
| **Màn hình / URL:** `/admin/shops` |
| **Cần thấy trong ảnh:** Dòng shop Fahasa với chiết khấu sàn 8%. |
| **Tên tệp gợi ý:** `tc23_shop.png` |
*Hình 6-65. TC-23 – Dòng shop Fahasa với chiết khấu sàn 8%*

| [CHÈN ẢNH – Hình 6-66] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Đăng nhập bằng:** (MySQL Workbench) |
| **Màn hình / URL:** `SELECT madh, chiet_khau_app_phan_tram, tien_phi_san, tien_thuc_nhan_shop FROM don_hang ORDER BY madh DESC LIMIT 1;` |
| **Cần thấy trong ảnh:** Các cột `chiet_khau_app_phan_tram = 8`, `tien_phi_san = 34560`, `tien_thuc_nhan_shop = 397440`. |
| **Tên tệp gợi ý:** `tc23_db.png` |
*Hình 6-66. TC-23 – Các cột chiet_khau_app_phan_tram = 8, tien_phi_san = 34560, tien_thuc_nhan_shop = 397440*

---

### 6.2.5. Kiểm thử đơn vị tự động

Dự án The4BookStore đã được xây dựng sẵn 71 phương thức kiểm thử đơn vị (`@Test`) thuộc 13 lớp trong thư mục `src/test/java`, thực thi thông qua lệnh chuẩn Maven:
```powershell
.\mvnw.cmd test
```

#### Bảng 6-28. Tổng hợp bộ kiểm thử đơn vị tự động
| STT | Lớp kiểm thử | Số test | Nội dung bao phủ nghiệp vụ |
| :---: | :--- | :---: | :--- |
| 1 | `AdminControllerTest` | 11 | Phân quyền và chức năng Admin Dashboard |
| 2 | `PasswordResetServiceTest` | 10 | Sinh/xác thực OTP, hết hạn sau 5 phút, tài khoản Google |
| 3 | `VoucherSystemTest` | 7 | Kiểm tra ràng buộc và tính toán mã giảm giá |
| 4 | `AvatarUtilsTest` | 6 | Tiện ích sinh ảnh đại diện tự động |
| 5 | `MarketplaceFeaturesTest` | 6 | Nghiệp vụ sàn đa gian hàng (Multi-vendor) |
| 6 | `ProductControllerTest` | 6 | Danh sách, lọc, tìm kiếm và chi tiết sản phẩm |
| 7 | `ResetPasswordControllerTest` | 6 | Điều hướng và kiểm soát luồng đặt lại mật khẩu |
| 8 | `AdminUserAndImportExportTest` | 6 | Quản lý người dùng, nhập/xuất dữ liệu Excel/CSV |
| 9 | `VendorControllerTest` | 5 | Chức năng Kênh người bán (Seller Center) |
| 10 | `VendorServiceTest` | 4 | Xử lý logic nghiệp vụ người bán |
| 11 | `HomeControllerTest` | 2 | Trang chủ và các thành phần giao diện chính |
| 12 | `SendEmailIntegrationTest` | 1 | Tích hợp gửi email SMTP Gmail thực tế |
| 13 | `The4BookStoreApplicationTests` | 1 | Tải và khởi động Spring ApplicationContext |
| **Tổng** | **13 lớp kiểm thử** | **71** | **Bao phủ toàn bộ các dịch vụ và phân hệ cốt lõi** |

| [CHÈN ẢNH – Hình 6-67] (xóa khung này sau khi chèn ảnh) |
| :--- |
| **Thực hiện:** Mở terminal tại thư mục gốc dự án, chạy lệnh: `.\mvnw.cmd test` |
| **Cần thấy trong ảnh:** Dòng tổng kết cuối log: `Tests run: 71, Failures: 0, Errors: 0, Skipped: 0` và `BUILD SUCCESS`. |
| **Tên tệp gợi ý:** `unit_test_result.png` |
*Hình 6-67. Kết quả chạy bộ kiểm thử đơn vị tự động bằng Maven*

---

### 6.2.6. Tổng hợp kết quả và nhận xét

#### Bảng 6-29. Thống kê kết quả kiểm thử hệ thống
| Nhóm chức năng kiểm thử | Test case | Số lượng | Đạt (Pass) | Không đạt (Fail) | Tỷ lệ đạt |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **A. Xác thực và phân quyền** | TC-01 → TC-07 | 7 | 7 | 0 | 100% |
| **B. Mua sắm của khách hàng** | TC-08 → TC-14 | 7 | 7 | 0 | 100% |
| **C. Người bán (Vendor)** | TC-15 → TC-19 | 5 | 5 | 0 | 100% |
| **D. Quản lý sàn và Admin** | TC-20 → TC-23 | 4 | 4 | 0 | 100% |
| **Tổng cộng** | **TC-01 → TC-23** | **23** | **23** | **0** | **100%** |

#### Nhận xét đánh giá:
- Các chức năng cốt lõi của sàn The4BookStore (xác thực bảo mật với JWT trong cookie `the4book_jwt` có cờ `HttpOnly`, phân quyền theo vai trò RBAC chặt chẽ, giỏ hàng, đặt hàng COD và VietQR động với cơ chế trừ tồn kho an toàn, mô hình ký quỹ Escrow bảo vệ người mua, quyết toán tự động tỷ lệ phí sàn theo shop, kiểm duyệt sản phẩm và cơ chế phân xử tranh chấp) đều hoạt động ổn định và chính xác theo thiết kế.
- Hệ thống xử lý ngoại lệ tốt, hiển thị thông báo phản hồi thân thiện và rõ ràng bằng tiếng Việt cho người dùng ở mọi tình huống lỗi.

#### Hạn chế và hướng phát triển:
1. **Phí vận chuyển:** Hiện tại phí vận chuyển đang được tạm tính động trên giao diện giỏ hàng và chưa tích hợp API tính phí tự động theo địa chỉ thực tế từ đơn vị vận chuyển (GHN/GHTK) vào tổng tiền đơn hàng lưu ở máy chủ.
2. **Kiểm tra độ phức tạp mật khẩu:** Phía máy chủ khi đăng ký tài khoản cần bổ sung thêm ràng buộc Regex kiểm tra độ dài tối thiểu (ví dụ: từ 8 ký tự, có chữ và số) bên cạnh việc kiểm tra phía Client.
3. **Áp dụng tỷ lệ sàn tự động:** Cần hoàn thiện cơ chế tự động lấy cấu hình `PHI_SAN_MAC_DINH` gán cho các shop mới được tạo thay vì giá trị mặc định cố định 5%.
4. **Tự động hóa kiểm thử nâng cao:** Trong tương lai cần bổ sung kiểm thử tải chịu tải (Load Testing với Apache JMeter) và kiểm thử giao diện tự động đầu cuối (End-to-End Testing với Selenium/Playwright).
