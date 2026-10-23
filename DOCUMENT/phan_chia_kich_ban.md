# BẢNG PHÂN CHIA KỊCH BẢN DEMO HỆ THỐNG THE4BOOKSTORE CHO 4 THÀNH VIÊN

> **Căn cứ phân công:** Dựa trên phân nhiệm cụ thể tại `bang_phan_cong_nhiem_vu.md` và 45 hạng mục kiểm thử trong `KICHBAN.md`.

---

## I. BẢNG TỔNG QUAN PHÂN CÔNG

| STT | Thành viên | Vai trò & Trách nhiệm chính | Các mục kịch bản đảm nhiệm | Tổng số mục |
| :---: | :--- | :--- | :--- | :---: |
| **1** | **Nguyễn Đặng Cao Tường**<br>(MSSV: 24110375) | **Trưởng nhóm**<br>- Kiến trúc phân tầng Marketplace<br>- Phân hệ Xác thực & Bảo mật (Spring Security, JWT, OAuth2, OTP)<br>- Quản trị người dùng & Phân quyền RBAC, Session Invalidation & Audit Log | **Mở đầu/Giới thiệu**<br>+ **Mục 6, 7, 8** (Xác thực người dùng)<br>+ **Mục 39, 40, 41** (Quản trị bảo mật Admin)<br>+ **Tổng kết** | **6 mục**<br>*(+ Khai mạc & Bế mạc)* |
| **2** | **Nguyễn Ngọc Thiện**<br>(MSSV: 24110335) | **Buyer Experience & Promotion Lead**<br>- Giao diện Trang chủ, Chi tiết SP, Tìm kiếm & Lọc đa tiêu chí<br>- Hệ thống Khuyến mãi & Ví voucher (Sàn + Shop + Batch)<br>- Giỏ hàng, Freeship Progress Bar & Checkout COD | **Mục 1, 2, 3, 4** (Khám phá & Tìm kiếm)<br>**Mục 9, 10** (Profile, Sổ địa chỉ, Wishlist)<br>**Mục 11, 12, 13, 14** (Voucher ví, Giỏ hàng, Freeship, COD)<br>**Mục 30, 43, 44** (Voucher Shop, Voucher Sàn & Batch) | **13 mục** |
| **3** | **Bùi Quang Hòa**<br>(MSSV: 24110213) | **Seller Operations & Transactions Lead**<br>- Kênh Người Bán (Vendor Center) & Vendor KPI Dashboard<br>- Quản lý tồn kho Shop, Nhập kho CSV, Phiếu kiểm kê & Cân bằng kho<br>- Đơn hàng, VietQR động NAPAS & Quyết toán doanh thu<br>- Bento KPI Dashboard Sàn, Phí chiết khấu Sàn & Vận chuyển | **Mục 15, 16, 17, 18** (Thanh toán VietQR, Vòng đời đơn hàng, Quyết toán)<br>**Mục 21, 22** (Đăng ký Shop & Vendor Dashboard)<br>**Mục 26, 27, 28** (Nhập kho CSV, Kiểm kê kho, Xử lý đơn Shop)<br>**Mục 31, 32, 38, 42** (Bento Sàn, Phê duyệt Shop, Vận chuyển, Chiết khấu) | **13 mục** |
| **4** | **Võ Thành Phát**<br>(MSSV: 24110300) | **Catalog, Media CDN & Dispute Lead**<br>- Catalog 3 ngành hàng (Sách, VPP, Quà tặng) & Cloudinary CDN<br>- Trang Gian hàng Shop (Storefront) & Cài đặt Shop<br>- Đánh giá sản phẩm (Rating/Review)<br>- Phân hệ Phân xử Tranh chấp (Manager Dispute Center)<br>- Master Data (Danh mục, Tác giả) & Data Export Center (CSV UTF-8 BOM) | **Mục 5** (Shop Storefront)<br>**Mục 19, 20** (Đánh giá, Khiếu nại trả hàng)<br>**Mục 23, 24, 25** (Trang trí shop, Đăng bán CDN, Quản lý sách shop)<br>**Mục 29, 33, 34, 35** (Xử lý khiếu nại shop, Duyệt sách, Khóa SP, Tranh chấp)<br>**Mục 36, 37, 45** (Danh mục ngành hàng, Tác giả, Xuất nhập báo cáo) | **13 mục** |

---

## II. CHI TIẾT KỊCH BẢN DEMO CỦA TỪNG THÀNH VIÊN

### 1. NGUYỄN ĐẶNG CAO TƯỜNG (MSSV: 24110375)
*Phân hệ: Xác thực, Bảo mật Phân quyền RBAC, Quản trị Hệ thống & Kiến trúc phần mềm*

* **Phần mở đầu (2 phút):**
  - Giới thiệu đề tài sàn thương mại điện tử đa người bán The4BookStore.
  - Trình bày kiến trúc phân tầng (Controller - Service - Repository - Entity) và cơ chế phân quyền 4 vai trò (Guest/KhachHang, Vendor, Manager, Admin).
* **Các mục demo trực tiếp:**
  1. **Mục 6 - Đăng ký Tài khoản Người dùng (`SCR-CUS-09` | `/register`):** Thử nhập trùng username/email để xem kiểm tra lỗi validation; tạo tài khoản mới thành công và chứng minh mật khẩu được băm an toàn bằng thuật toán **BCrypt**.
  2. **Mục 7 - Đăng nhập Hệ thống & Phân quyền RBAC (`SCR-CUS-08` | `/login`):** Demo đăng nhập tài khoản Local, thử nhập sai mật khẩu để kiểm tra thông báo bảo mật; demo tính năng **Đăng nhập nhanh với Google OAuth2**; kiểm tra việc cấp JWT Cookie (`the4book_jwt`) và điều hướng người dùng đúng theo từng Role.
  3. **Mục 8 - Quên mật khẩu & Đặt lại mật khẩu qua OTP Email (`SCR-CUS-10, 11` | `/forgot-password`, `/reset-password`):** Nhập email nhận mã OTP 6 số qua Gmail thật (hiệu lực 5 phút), nhập OTP và mật khẩu mới để phục hồi tài khoản ngay lập tức.
  4. **Mục 39 - Quản trị Người dùng & Phân quyền RBAC (`SCR-ADM-05` | `/admin/users`):** Đăng nhập Admin, xem danh sách tài khoản, thay đổi quyền hạn người dùng (`USER` $\rightarrow$ `MANAGER`), kiểm tra quy tắc bảo vệ (Admin không tự hạ quyền mình, Manager không sửa được Admin).
  5. **Mục 40 - Khóa Tài khoản Vi phạm & Thu hồi Phiên tức thì (`SCR-ADM-05` | `/admin/users`):** Khóa một tài khoản đang hoạt động, chứng minh cơ chế **Session Invalidation** lập tức đá người dùng ra khỏi phiên làm việc (nhờ kiểm tra `tokenVersion` / `SessionRegistry`).
  6. **Mục 41 - Theo dõi Nhật ký Bảo mật & Audit Log (`CSDL nhat_ky_bao_mat` / `SecurityAuditService`):** Mở bảng kiểm toán chứng minh mọi thao tác khóa tài khoản, đổi quyền đều được ghi vết thời gian, actor và dữ liệu chi tiết.
* **Chuyển giao:** Bàn giao lượt trình bày cho **Nguyễn Ngọc Thiện** trình diễn luồng trải nghiệm khách hàng mua sách.

---

### 2. NGUYỄN NGỌC THIỆN (MSSV: 24110335)
*Phân hệ: Giao diện Khách hàng, Tìm kiếm & Lọc sản phẩm, Hệ thống Khuyến mãi & Giỏ hàng*

* **Các mục demo trực tiếp:**
  1. **Mục 1 - Duyệt & Khám phá Sách (`SCR-CUS-01` | `/`):** Trình diễn Hero Banner, Book of the Month, các cụm "Sách Bán Chạy", "Săn Voucher Khuyến Mãi", "Gợi Ý Riêng Cho Bạn", "Sản Phẩm Đã Xem Gần Đây".
  2. **Mục 2 - Tìm kiếm Sách thông minh & Gian hàng phù hợp (`SCR-CUS-02` | `/san-pham?q=...`):** Tìm kiếm theo tên tác phẩm, tác giả, ISBN; thử tìm tên gian hàng (ví dụ: *Kim Đồng*) để hiển thị card **"Gian Hàng Phù Hợp"** dẫn sang Storefront; kiểm tra giao diện kết quả rỗng.
  3. **Mục 3 - Bộ lọc & Sắp xếp đa tiêu chí (`SCR-CUS-02` | `/san-pham`, `/san-pham/sach`...):** Lọc theo 3 ngành hàng (Sách, VPP, Quà tặng), chọn Category Pills cấp 2, lọc theo Tác giả, sắp xếp theo giá tăng/giảm, tên A-Z, mới nhất.
  4. **Mục 4 - Xem chi tiết Sản phẩm Sách (`SCR-CUS-03` | `/san-pham/{id}`):** Xem ảnh nét cao, thông số ISBN, tồn kho thời gian thực, liên kết sang Shop bán, mô tả tác phẩm, nút Wishlist.
  5. **Mục 9 - Quản lý Hồ sơ Cá nhân & Sổ Địa chỉ Giao hàng (`SCR-CUS-12, 13` | `/profile`, `/profile/addresses`):** Đổi avatar, thêm địa chỉ nhận hàng mới trong Sổ địa chỉ, chọn Địa chỉ mặc định.
  6. **Mục 10 - Sách Yêu thích (Wishlist) & Lịch sử Sản phẩm Đã xem (`SCR-CUS-14, 15` | `/profile/favorites`):** Thả tim sản phẩm qua API `/api/favorites/toggle/{maSP}`, mở danh sách yêu thích, quay lại Trang chủ kiểm tra khối "Sản Phẩm Bạn Đã Xem Gần Đây".
  7. **Mục 11 - Săn Voucher & Lưu vào Ví Khuyến Mãi (`SCR-CUS-05` | `/san-voucher`):** Duyệt voucher toàn sàn và voucher shop, bấm "Lưu mã", kiểm tra mã đã vào Ví voucher tại `/profile#voucher-section`.
  8. **Mục 12 - Quản lý Giỏ hàng & Tiến trình Freeship (`SCR-CUS-06` | `/gio-hang`):** Thêm sách vào giỏ, tăng/giảm số lượng, chặn mua quá tồn kho thực tế; quan sát **Freeship Progress Bar** tự động đạt 100% khi đủ mốc 350.000đ.
  9. **Mục 13 - Áp dụng Mã Giảm Giá tại Giỏ hàng (`SCR-CUS-06` | `/gio-hang`):** Nhập voucher sai kiểm tra bắt lỗi; áp dụng voucher từ ví, chứng minh hệ thống tính chuẩn xác số tiền giảm và trần chiết khấu tối đa.
  10. **Mục 14 - Đặt hàng & Thanh toán COD (`SCR-CUS-07` | Modal tại `/gio-hang`):** Chọn sản phẩm từ giỏ, chọn địa chỉ mặc định, chọn phương thức Tiền mặt (COD), xác nhận đơn, chứng minh đơn hàng tự tách theo từng Shop và trừ tồn kho ngay lập tức.
  11. **Mục 30 - Tạo & Quản lý Voucher Riêng của Shop (`SCR-VEN-11` | `/vendor/promotions`):** Đăng nhập quyền Vendor, tạo mã giảm giá riêng của shop (mã code, giảm cố định hoặc %, giới hạn số lượng và đơn tối thiểu).
  12. **Mục 43 - Quản lý & Tạo Mã Giảm Giá Toàn Sàn (`SCR-ADM-10, 11` | `/admin/promotions`):** Đăng nhập Admin, xem Bento thống kê voucher sàn, tạo voucher sự kiện phạm vi toàn sàn `TOAN_SAN`, thao tác nhân bản mã và bật/tắt mã.
  13. **Mục 44 - Tạo Mã Giảm Giá Hàng Loạt (`SCR-ADM-12` | `/admin/promotions`):** Bấm "Sinh mã hàng loạt", nhập Prefix (ví dụ: `BOOKFEST2026_`), sinh 50 mã ngẫu nhiên duy nhất và xuất file CSV voucher.
* **Chuyển giao:** Bàn giao lượt trình bày cho **Bùi Quang Hòa** demo cổng thanh toán VietQR và Kênh vận hành của Shop/Sàn.

---

### 3. BÙI QUANG HÒA (MSSV: 24110213)
*Phân hệ: Thanh toán Trực tuyến, Vòng đời Đơn hàng, Quản lý Kho hàng Shop, Kênh Người Bán & Quản trị Sàn*

* **Các mục demo trực tiếp:**
  1. **Mục 15 - Đặt hàng & Thanh toán VietQR Động (`SCR-CUS-07, 17` | `/gio-hang`, `/don-hang/{id}`):** Tiến hành thanh toán chọn hình thức "Chuyển khoản VietQR", mở trang chi tiết đơn hàng quan sát **Card VietQR động chuẩn NAPAS 24/7** sinh đúng số tiền, số tài khoản thụ hưởng và đúng cú pháp `#TB-{id}`.
  2. **Mục 16 - Theo dõi Lịch sử Đơn hàng & Tìm kiếm Đơn (`SCR-CUS-16, 17` | `/don-hang`, `/don-hang/{id}`):** Tìm kiếm đơn realtime, lọc theo 7 tab trạng thái, xem timeline trạng thái và bảng kê chi phí đơn hàng.
  3. **Mục 17 - Hủy Đơn Hàng & Tự Động Hoàn Tồn Kho (`SCR-CUS-17` | `/don-hang/{id}`):** Hủy đơn đang ở trạng thái `ChoXuLy`, chứng minh hệ thống tự động hoàn lại số lượng sách vào kho; với đơn `DaXacNhan` thì gửi yêu cầu hủy sang Shop phê duyệt.
  4. **Mục 18 - Xác nhận Đã Nhận Hàng & Quyết toán Doanh thu (`SCR-CUS-17` | `/don-hang/{id}`):** Khách bấm "Đã nhận được hàng", đơn hàng chuyển `HoanTat`, giải phóng tiền từ tài khoản ký quỹ sàn về ví doanh thu thực nhận của Shop sau khi trừ 5% phí sàn.
  5. **Mục 21 - Đăng ký Mở Gian Hàng (`SCR-VEN-01, 02` | `/vendor/register`, `/vendor/pending`):** Khách hàng nộp hồ sơ mở shop (tên shop, hotline, kho hàng), hệ thống ghi nhận trạng thái `ChoDuyet` và chuyển tới màn hình chờ duyệt.
  6. **Mục 32 - Thẩm định & Phê duyệt Gian Hàng mới (`SCR-ADM-04` | `/admin/shops`):** Phía Quản lý/Admin mở danh sách gian hàng, duyệt hồ sơ shop, tài khoản tự động được nâng cấp lên vai trò `VENDOR`.
  7. **Mục 22 - Bảng điều khiển Vendor KPI Dashboard (`SCR-VEN-03` | `/vendor/dashboard`):** Đăng nhập tài khoản Vendor, quan sát các thẻ KPI: Doanh thu thực nhận sau phí, tổng đơn, cảnh báo sách sắp hết hàng (Low stock), biểu đồ biến động doanh thu.
  8. **Mục 26 - Quản lý Tồn kho: Nhập Hàng Loạt Bằng Tệp CSV (`SCR-VEN-08` | `/vendor/products`):** Tải file CSV mẫu, thực hiện nhập danh sách sách qua file CSV, chứng minh cơ chế tự cộng dồn tồn kho nếu trùng ISBN hoặc tự tạo sản phẩm mới.
  9. **Mục 27 - Quản lý Tồn kho: Kiểm Kê & Cân Bằng Kho (`SCR-VEN-09, 10` | `/vendor/inventory/create`):** Tạo phiếu kiểm kê kho, nhập số lượng đếm thực tế lệch so với phần mềm, bấm xác nhận để thủ tục `sp_ShopXacNhanKiemKe` tự động cân bằng tồn kho.
  10. **Mục 28 - Xử lý Đơn hàng & Bàn giao Đơn vị Vận chuyển (`SCR-VEN-07` | `/vendor/orders`):** Thao tác toàn bộ vòng đời đơn hàng phía Shop: Xác nhận đơn (`DaXacNhan`) $\rightarrow$ Bàn giao vận chuyển (`DaLayHang`) $\rightarrow$ Đang giao (`DangGiao`) $\rightarrow$ Giao thành công (`DaGiao`).
  11. **Mục 31 - Bento Dashboard Vận hành Sàn (`SCR-ADM-01` | `/admin/dashboard`):** Đăng nhập quyền Quản lý/Admin, xem Dashboard tổng thể toàn sàn: Doanh thu GMV, số đơn, hoa hồng thu về, số gian hàng, ấn phẩm mới và đơn hàng gần nhất.
  12. **Mục 38 - Quản lý Đối tác Nhà Vận Chuyển (`SCR-ADM-13` | `/admin/shipping`):** Xem danh sách nhà vận chuyển tích hợp (GHN, GHTK, Viettel Post), cập nhật cước phí cơ bản và thời gian giao dự kiến, bật/tắt đối tác.
  13. **Mục 42 - Cấu hình Tỷ lệ Chiết khấu Sàn (`SCR-ADM-14`, `SCR-ADM-04`):** Cấu hình tỷ lệ hoa hồng mặc định của sàn (ví dụ 5% lên 6%); thiết lập tỷ lệ chiết khấu riêng ưu đãi cho từng Shop đối tác chiến lược.
* **Chuyển giao:** Bàn giao lượt trình bày cho **Võ Thành Phát** demo Phân hệ Sản phẩm, Storefront, Đánh giá, Tranh chấp và Trung tâm Xuất Nhập Dữ liệu CSDL.

---

### 4. VÕ THÀNH PHÁT (MSSV: 24110300)
*Phân hệ: Sản phẩm & Danh mục 3 ngành hàng, Cloudinary CDN, Shop Storefront, Phân xử Tranh chấp & Quản trị CSDL*

* **Các mục demo trực tiếp:**
  1. **Mục 5 - Xem Gian hàng Người bán (`SCR-CUS-04` | `/shop/{slug}`):** Xem giao diện Storefront công khai của một gian hàng: Banner, Logo, Hotline, Email, Đánh giá trung bình và danh mục sản phẩm riêng biệt của shop đó.
  2. **Mục 23 - Cài đặt Gian hàng & Trang trí Shop (`SCR-VEN-04` | `/vendor/profile`):** Phía Vendor cập nhật thông tin shop, tải lên Banner trang trí và Logo mới, kiểm tra kết quả đồng bộ ngay lập tức ra ngoài Storefront khách xem.
  3. **Mục 24 - Đăng bán Sách mới tích hợp Cloudinary CDN (`SCR-VEN-05, 06` | `/vendor/products/add`):** Tạo sản phẩm mới thuộc 1 trong 3 ngành hàng (*Sách*, *Văn phòng phẩm*, *Quà tặng*), tải ảnh bìa lên đám mây **Cloudinary qua CDN**, sản phẩm được tạo ở trạng thái `ChoDuyet`.
  4. **Mục 25 - Quản lý Danh mục Sách của Shop (`SCR-VEN-05, 06` | `/vendor/products`):** Xem danh sách sách của shop, tìm kiếm/lọc, chỉnh sửa giá bán/mô tả và thử xóa sản phẩm ngừng kinh doanh.
  5. **Mục 33 - Kiểm duyệt Sách Đăng bán của Gian Hàng (`SCR-ADM-06` | `/admin/shops/{id}/products`):** Đăng nhập quyền Manager, duyệt sản phẩm đang ở trạng thái `ChoDuyet` $\rightarrow$ chuyển sang `BinhThuong` để xuất hiện ngoài sàn.
  6. **Mục 34 - Khóa & Mở khóa Sản phẩm Vi phạm (`SCR-ADM-06` | `/admin/shops/{id}/products`):** Khóa sản phẩm vi phạm bản quyền, kiểm tra trang khách hàng thấy sản phẩm lập tức bị chặn/ngưng bán; sau đó mở khóa lại.
  7. **Mục 36 - Quản lý Danh Mục Lớn & Danh Mục Chi Tiết (`SCR-ADM-02` | `/admin/categories`):** Thêm mới ngành hàng lớn qua Drawer Modal, thêm danh mục con cấp 2, kiểm tra ràng buộc toàn vẹn ngăn chặn xóa danh mục đang có sản phẩm.
  8. **Mục 37 - Quản lý Danh mục Tác giả (`SCR-ADM-03` | `/admin/authors`):** Thêm mới tác giả, xem danh sách sách theo tác giả, kiểm tra thông báo chặn xóa tác giả đang có sách trong hệ thống.
  9. **Mục 19 - Đánh giá & Nhận xét Sản phẩm (`SCR-CUS-18` | `/san-pham/{id}#reviews-section`):** Vào đơn hàng đã giao, đánh giá 5 sao, bắt lỗi nhận xét dưới 10 ký tự, đính kèm ảnh chụp thực tế và kiểm tra điểm sao trung bình được tính toán lại.
  10. **Mục 20 - Yêu cầu Trả hàng / Hoàn tiền (`SCR-CUS-17` | `/don-hang/{id}`):** Khách nhận sách lỗi, gửi yêu cầu Trả hàng / Hoàn tiền kèm mô tả và ảnh bằng chứng lỗi in ấn.
  11. **Mục 29 - Xử lý Yêu cầu Trả hàng & Khiếu nại từ Khách (`SCR-VEN-07` | `/vendor/orders`):** Shop kiểm tra khiếu nại, bấm **Từ chối trả hàng** và gửi kèm lý do giải trình $\rightarrow$ Đơn hàng tự động chuyển sang trạng thái `TranhChap`.
  12. **Mục 35 - Phân xử Tranh chấp Trả hàng / Hoàn tiền (`SCR-ADM-09` | `/admin/disputes`):** Đăng nhập Manager mở **Dispute Center**, xem đối chất và bằng chứng của 2 bên, ban hành phán quyết trọng tài: Chấp nhận hoàn tiền cho khách (`DaHoan`) hoặc Bác bỏ bảo vệ shop (`HoanTat`).
  13. **Mục 45 - Trung tâm Xuất & Nhập Dữ liệu Báo cáo (`SCR-ADM-15` | `/admin/import-export`):** Xuất các file CSV chuẩn **UTF-8 BOM** (mở trực tiếp bằng Excel không lỗi font tiếng Việt): Tải CSV Sản phẩm, CSV Đơn hàng, CSV Tài khoản, CSV Gian hàng; và tính năng nhập sản phẩm mẫu.
* **Chuyển giao:** Bàn giao lại cho Trưởng nhóm **Nguyễn Đặng Cao Tường** để tổng kết buổi báo cáo.

---

## III. KỊCH BẢN LUỒNG TRÌNH DIỄN LIÊN TỤC (DEMO FLOW GỢI Ý)

Để buổi báo cáo diễn ra mạch lạc, tự nhiên như một câu chuyện vận hành thực tế (End-to-End User Story), nhóm nên phối hợp theo thứ tự 5 giai đoạn sau:

```
[GIAI ĐOẠN 1: MỞ ĐẦU & BẢO MẬT]
TƯỜNG: Giới thiệu Kiến trúc -> Đăng ký (BCrypt) -> Đăng nhập (Google OAuth2/Local) -> Quên mật khẩu OTP
   │
   ▼
[GIAI ĐOẠN 2: TRẢI NGHIỆM MUA HÀNG & KHUYẾN MÃI]
THIỆN: Trang chủ -> Tìm kiếm/Lọc -> Chi tiết SP -> Wishlist & Đã xem -> Săn Voucher vào Ví -> Giỏ hàng Freeship 350k -> Áp Voucher -> Đặt hàng COD & Quản lý Voucher
   │
   ▼
[GIAI ĐOẠN 3: GIAO DỊCH VIETQR & VẬN HÀNH BÁN HÀNG]
HÒA: Thanh toán VietQR Động -> Lịch sử đơn & Hủy đơn -> Nhận hàng giải phóng ký quỹ -> Đăng ký Shop -> Vendor Dashboard -> Nhập kho CSV -> Kiểm kê kho -> Shop xử lý giao hàng -> Dashboard Sàn & Phí hoa hồng
   │
   ▼
[GIAI ĐOẠN 4: CATALOG ĐA NGÀNH HÀNG, TRANH CHẤP & XUẤT DỮ LIỆU]
PHÁT: Shop Storefront -> Đăng bán Cloudinary CDN -> Quản lý sách shop -> Quản lý Danh mục & Tác giả -> Duyệt sách/Khóa sách -> Đánh giá sao -> Khách khiếu nại -> Shop từ chối -> Manager phân xử Dispute Center -> Xuất file CSV UTF-8 BOM
   │
   ▼
[GIAI ĐOẠN 5: BẢO MẬT ADMIN & BẾ MẠC]
TƯỜNG: Đổi quyền RBAC -> Khóa tài khoản thu hồi phiên tức thì (Session Invalidation) -> Audit Log kiểm toán -> Tổng kết & Cảm ơn
```
