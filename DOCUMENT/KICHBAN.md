**KỊCH BẢN DEMO TÍNH NĂNG HỆ THỐNG THE4BOOKSTORE**

---

**PHÂN HỆ 1: KHÁCH HÀNG (CUSTOMER & GUEST)**

**1\. Duyệt & Khám phá Sách (Trang chủ)**

* **Màn hình**: SCR-CUS-01 (/)

* **Thao tác**: 

  1. Mở trang chủ (`/`), xem biểu ngữ Hero Banner & ấn phẩm nổi bật tháng (*Book of the Month*).

  2. Lướt qua các khối nội dung: cụm sách **"Sách Bán Chạy Nhất"**, khối **"Săn Voucher Khuyến Mãi"**, khối **"Gợi Ý Riêng Cho Bạn"** (khi đã đăng nhập), khối **"Sản Phẩm Bạn Đã Xem Gần Đây"** và khối **"Danh Mục Nổi Bật"**.

* **Kết quả**: Hiển thị đầy đủ bìa sách, tên sách, tác giả, giá bán, nhãn giảm giá, tình trạng tồn kho và điểm đánh giá sao trung bình.

---

**2\. Tìm kiếm Sách thông minh & Gian hàng phù hợp**

* **Màn hình**: SCR-CUS-02 (/san-pham?q=...)

* **Thao tác**: 

  1. Nhập từ khóa trên thanh tìm kiếm header (ví dụ: *Cỏ Xanh*, *Murakami*, *Clean Code*).

  2. Nhập tên của một gian hàng sách (ví dụ: *Kim Đồng*, *Nhã Nam*).

  3. Thử nhập một từ khóa không tồn tại (ví dụ: *xyz999*).

* **Kết quả**: 
  * Hệ thống trả về đúng các cuốn sách khớp tên, tác giả hoặc ISBN.
  * Nếu từ khóa trùng hoặc khớp tên gian hàng đối tác, hệ thống tự động hiển thị thêm khối **"Gian Hàng Phù Hợp"** dẫn trực tiếp tới storefront của shop.
  * Với từ khóa không có kết quả, hiển thị thông báo rỗng cùng nút **"Xem tất cả sản phẩm"** để quay lại danh mục đầy đủ.

---

**3\. Bộ lọc & Sắp xếp đa tiêu chí**

* **Màn hình**: SCR-CUS-02 (/san-pham, /san-pham/sach, /san-pham/van-phong-pham, /san-pham/qua-tang)

* **Thao tác**: 

  1. Chọn nhóm ngành hàng (Cấp 1): Chọn qua dropdown *Ngành hàng* (*Sách*, *Văn phòng phẩm*, *Quà tặng & Trang trí*) hoặc bấm trực tiếp các tab điều hướng chuyên biệt (`/san-pham/sach`, `/san-pham/van-phong-pham`, `/san-pham/qua-tang`).

  2. Chọn lọc theo Danh mục chi tiết (Cấp 2): Chọn danh mục con tương ứng qua dropdown *Loại sản phẩm* hoặc bấm nhanh các thẻ danh mục (Category Pills) ở thanh cuộn ngang phía trên.

  3. Chọn lọc theo Tác giả (chỉ hiển thị khi lọc nhóm Sách): Chọn tác giả yêu thích qua dropdown *Tác giả*.

  4. Chọn sắp xếp theo: *Mới nhất* (`newest`), *Giá tăng dần* (`price_asc`), *Giá giảm dần* (`price_desc`), *Tên A-Z* (`name_asc`).

  5. Bấm nút **"Lọc"** để áp dụng, hoặc nút **"Đặt lại"** để xóa nhanh các tiêu chí lọc về mặc định.

* **Kết quả**: Danh sách sản phẩm được cập nhật chính xác theo đúng các tiêu chí kết hợp và thứ tự sắp xếp đã chọn.

---

**4\. Xem chi tiết Sản phẩm Sách**

* **Màn hình**: SCR-CUS-03 (/san-pham/{id})

* **Thao tác**: 

  1. Bấm vào một cuốn sách bất kỳ từ trang chủ hoặc trang tìm kiếm.

  2. Xem các thông tin chi tiết: Bìa sách độ nét cao, Tên tác phẩm, Tác giả liên kết, Mã ISBN, Giá niêm yết, Số lượng tồn kho theo thời gian thực, Tên gian hàng bán sách (liên kết sang storefront của shop), Mô tả chi tiết tác phẩm.

  3. Bấm nút **"Yêu thích"** (Wishlist) để lưu sách vào danh sách quan tâm.

  4. Cuộn xuống khu vực đánh giá: Điểm sao trung bình, số lượt đánh giá và các bài nhận xét kèm hình ảnh từ độc giả đã mua hàng.

* **Kết quả**: Toàn bộ thông số sản phẩm, nguồn gốc gian hàng và đánh giá thực tế được hiển thị đầy đủ, minh bạch.

---

**5\. Xem Gian hàng Người bán (Storefront)**

* **Màn hình**: SCR-CUS-04 (/shop/{slug})

* **Thao tác**: 

  1. Tại trang chi tiết sách, bấm vào tên hoặc huy hiệu gian hàng phân phối (ví dụ: *Hiệu Sách Kim Đồng Official*).

  2. Xem trang hồ sơ gian hàng: Ảnh bìa (Banner), Logo, Hotline, Email, Địa chỉ kho sách, Đánh giá trung bình của shop và danh mục các sản phẩm do chính shop này cung cấp.

* **Kết quả**: Giao diện gian hàng hiển thị chuyên nghiệp, tách biệt dữ liệu sản phẩm theo từng người bán.

---

**6\. Đăng ký Tài khoản Người dùng**

* **Màn hình**: SCR-CUS-09 (/register)

* **Thao tác**: 

  1. Nhập thông tin đăng ký: Tên đăng nhập, Email liên hệ, Mật khẩu, Số điện thoại, Họ tên đầy đủ, Địa chỉ giao nhận hàng.

  2. Thử nhập trùng tên đăng nhập hoặc email đã có trong hệ thống \-\> Quan sát thông báo lỗi đỏ.

  3. Nhập thông tin hợp lệ \-\> Bấm **Đăng ký tài khoản**.

* **Kết quả**: Hệ thống chặn thông tin trùng lặp. Khi đăng ký thành công, tự động băm mật khẩu bằng thuật toán **BCrypt**, tạo hồ sơ khách hàng liên kết và chuyển về màn hình đăng nhập kèm thông báo thành công.

---

**7\. Đăng nhập Hệ thống & Phân quyền RBAC**

* **Màn hình**: SCR-CUS-08 (/login)

* **Thao tác**: 

  1. Thử nhập sai mật khẩu \-\> Hệ thống chặn và hiển thị thông báo lỗi bảo mật chung.

  2. Đăng nhập bằng tài khoản Local (Tên đăng nhập hoặc Email kèm Mật khẩu).

  3. Hoặc bấm nút **"Tiếp tục với tài khoản Google"** (đăng nhập nhanh qua chuẩn OAuth2).

* **Kết quả**: Hệ thống xác thực thành công, cấp JWT Cookie (`the4book_jwt`), lưu trạng thái đăng nhập và tự động điều hướng đúng trang đích theo vai trò: Khách hàng về Trang chủ (`/`), Vendor về Vendor Dashboard (`/vendor/dashboard`), Admin/Manager về Admin Dashboard (`/admin/dashboard`).

---

**8\. Quên mật khẩu & Đặt lại mật khẩu qua OTP Email**

* **Màn hình**: SCR-CUS-10, SCR-CUS-11 (/forgot-password, /reset-password)

* **Thao tác**: 

  1. Tại trang đăng nhập, bấm **Quên mật khẩu?**.

  2. Nhập địa chỉ email đăng ký \-\> Bấm **Gửi mã OTP**.

  3. Kiểm tra hộp thư Gmail nhận mã OTP 6 chữ số (hiệu lực trong 5 phút).

  4. Hệ thống chuyển sang màn hình đặt lại mật khẩu (`/reset-password?email=...`): Nhập mã OTP 6 chữ số và thiết lập mật khẩu mới (tối thiểu 6 ký tự).

* **Kết quả**: Hệ thống xác thực mã OTP, cập nhật mật khẩu mới đã mã hóa BCrypt và cho phép đăng nhập bằng mật khẩu mới ngay lập tức.

---

**9\. Quản lý Hồ sơ Cá nhân & Sổ Địa chỉ Giao hàng**

* **Màn hình**: SCR-CUS-12, SCR-CUS-13 (/profile, /profile/addresses)

* **Thao tác**: 

  1. Tại `/profile`: Cập nhật thông tin cá nhân, tải lên ảnh đại diện cá nhân (hoặc quay về avatar mặc định), đổi mật khẩu tài khoản.

  2. Bấm vào menu **"Xem địa chỉ"** để chuyển sang trang Sổ địa chỉ (`/profile/addresses`).

  3. Thêm mới một địa chỉ nhận hàng (Họ tên người nhận, Số điện thoại, Địa chỉ giao hàng chi tiết).

  4. Thiết lập địa chỉ vừa tạo làm **Địa chỉ mặc định** hoặc xóa địa chỉ cũ.

* **Kết quả**: Thông tin hồ sơ được lưu ngay lập tức; địa chỉ mặc định sẽ tự động được chọn sẵn tại bước thanh toán giỏ hàng.

---

**10\. Sách Yêu thích (Wishlist) & Lịch sử Sản phẩm Đã xem**

* **Màn hình**: SCR-CUS-14, SCR-CUS-15 (/profile/favorites & Khối đã xem tại /)

* **Thao tác**: 

  1. Tại trang chi tiết sản phẩm, bấm nút **"Yêu thích"** (biểu tượng trái tim) để thêm/xóa sách khỏi Wishlist thông qua API `/api/favorites/toggle/{maSP}`.

  2. Truy cập trang **Sản phẩm yêu thích** (`/profile/favorites`) từ menu tài khoản để xem toàn bộ danh sách các đầu sách đã lưu.

  3. Lướt xem một số đầu sách khác, sau đó quay lại **Trang chủ (`/`)**: Cuộn xuống xem khối **"Sản Phẩm Bạn Đã Xem Gần Đây"**.

* **Kết quả**: Danh mục yêu thích hiển thị chính xác các sách đã thả tim; trang chủ tự động ghi nhớ và hiển thị danh sách sản phẩm độc giả vừa duyệt qua.

---

**11\. Săn Voucher & Lưu vào Ví Khuyến Mãi**

* **Màn hình**: SCR-CUS-05 (/san-voucher)

* **Thao tác**: 

  1. Truy cập mục **Săn Voucher** trên thanh điều hướng header hoặc từ banner trang chủ.

  2. Xem danh sách các voucher toàn sàn (ví dụ: mã Freeship, mã giảm giá %) và voucher độc quyền của từng gian hàng.

  3. Bấm nút **Lưu mã** vào ví cá nhân.

* **Kết quả**: Voucher chuyển sang trạng thái "Đã lưu", người dùng có thể xem lại trong tab *Ví Voucher & Ưu đãi* tại trang cá nhân (`/profile#voucher-section`) và sẵn sàng áp dụng khi thanh toán giỏ hàng.

---

**12\. Quản lý Giỏ hàng & Tiến trình Freeship**

* **Màn hình**: SCR-CUS-06 (/gio-hang)

* **Thao tác**: 

  1. Bấm **Thêm vào giỏ hàng** từ trang chi tiết hoặc thẻ sản phẩm.

  2. Tại trang Giỏ hàng: Tăng/giảm số lượng từng món, xóa từng món hoặc xóa tất cả sản phẩm.

  3. Thử tăng số lượng vượt quá số tồn kho của sách \-\> Hệ thống tự động giới hạn ở mức tồn tối đa.

  4. Quan sát thanh **Freeship Progress Bar**: Khi tổng tiền các sản phẩm được tích chọn đạt mốc **350.000đ**, thanh tiến trình tự động đạt 100% và kích hoạt thông báo miễn phí vận chuyển toàn sàn.

* **Kết quả**: Giỏ hàng tính toán chuẩn xác thành tiền theo từng shop, tự động chặn mua quá tồn kho thực tế.

---

**13\. Áp dụng Mã Giảm Giá tại Giỏ hàng**

* **Màn hình**: SCR-CUS-06 (/gio-hang)

* **Thao tác**: 

  1. Nhập mã voucher không hợp lệ hoặc không đủ điều kiện \-\> Quan sát thông báo lỗi trực tiếp.

  2. Chọn voucher có sẵn trong ví hoặc nhập mã hợp lệ (ví dụ: voucher giảm giá sản phẩm hoặc voucher giảm phí vận chuyển).

  3. Bấm **Áp dụng**.

* **Kết quả**: Hệ thống kiểm tra điều kiện giá trị đơn hàng tối thiểu và tự động trừ số tiền giảm đúng theo thuật toán trần tối đa, hiển thị rõ dòng chiết khấu sản phẩm và giảm phí ship.

---

**14\. Đặt hàng & Thanh toán COD (Tiền mặt)**

* **Màn hình**: SCR-CUS-07 (Modal Thanh toán tại /gio-hang)

* **Thao tác**: 

  1. Tích chọn các sản phẩm cần mua trong giỏ \-\> Bấm **Tiến Hành Thanh Toán**.

  2. Chọn địa chỉ nhận hàng từ sổ địa chỉ có sẵn (hoặc nhập địa chỉ mới ngay tại form).

  3. Chọn đối tác vận chuyển liên kết sàn (GHN, Viettel Post, GHTK...).

  4. Chọn phương thức: **Tiền mặt (COD)**.

  5. Bấm **Xác Nhận Đặt Hàng**.

* **Kết quả**: Đơn hàng được tạo thành công với mã `#TB-xxx`, trạng thái `ChoXuLy` (Chờ xử lý), trạng thái thanh toán `ChuaThanhToan`. Số lượng tồn kho sản phẩm tự động giảm trừ ngay lập tức. Nếu giỏ hàng gồm sản phẩm từ nhiều shop khác nhau, hệ thống tự động phân tách thành các kiện hàng độc lập theo từng Shop.

---

**15\. Đặt hàng & Thanh toán VietQR Động**

* **Màn hình**: SCR-CUS-07, SCR-CUS-17 (/don-hang/{id})

* **Thao tác**: 

  1. Tích chọn sản phẩm trong giỏ \-\> Bấm **Tiến Hành Thanh Toán**.

  2. Chọn phương thức: **Chuyển khoản VietQR**.

  3. Bấm **Xác Nhận Đặt Hàng** \-\> Hệ thống điều hướng ngay tới trang **Chi tiết đơn hàng** (`/don-hang/{id}`).

  4. Quan sát Card VietQR hiển thị mã QR động được sinh tự động với đúng số tiền đơn hàng, số tài khoản thụ hưởng và nội dung chuyển khoản chuẩn cú pháp `#TB-{id}`.

* **Kết quả**: Khách hàng chỉ cần quét mã QR bằng ứng dụng ngân hàng bất kỳ để chuyển khoản chính xác mà không cần nhập tay thông tin.

---

**16\. Theo dõi Lịch sử Đơn hàng & Tìm kiếm Đơn**

* **Màn hình**: SCR-CUS-16, SCR-CUS-17 (/don-hang, /don-hang/{id})

* **Thao tác**: 

  1. Vào mục **Đơn hàng của tôi** (`/don-hang`).

  2. Nhập từ khóa tìm kiếm trên ô tìm kiếm realtime: Tìm theo mã đơn hoặc tên sản phẩm trong đơn.

  3. Lọc đơn theo các tab trạng thái: *Tất cả*, *Đơn mới* (`ChoXuLy`), *Đã xác nhận* (`DaXacNhan`), *Đang giao* (`DangGiao`), *Đã giao* (`DaGiao`), *Hoàn tất* (`HoanTat`), *Đã hủy* (`DaHuy`), *Trả hàng / Hoàn tiền* (`TraHangHoanTien`).

  4. Bấm **Xem chi tiết** một đơn hàng để xem dòng thời gian (Timeline) trạng thái đơn, địa chỉ giao nhận và bảng kê chi phí chi tiết.

* **Kết quả**: Khách hàng nắm bắt rõ ràng lộ trình và tình trạng giao vận của kiện hàng.

---

**17\. Hủy Đơn Hàng & Tự Động Hoàn Tồn Kho**

* **Màn hình**: SCR-CUS-17 (/don-hang/{id})

* **Thao tác**: 

  1. Mở một đơn hàng đang ở trạng thái `ChoXuLy` (Chờ xử lý).

  2. Bấm nút **"Hủy đơn hàng này"**, chọn/nhập lý do hủy đơn \-\> Xác nhận hủy.

  3. Thử với đơn hàng đã ở trạng thái `DaXacNhan`: Bấm nút **"Yêu cầu hủy đơn"** để gửi yêu cầu đến Shop phê duyệt.

* **Kết quả**: Đơn ở trạng thái chờ xử lý lập tức chuyển sang `DaHuy`, hệ thống tự động hoàn trả số lượng sách vào tồn kho gian hàng. Đơn đã xác nhận sẽ được chuyển yêu cầu sang Kênh người bán để chờ shop xác nhận hủy.

---

**18\. Xác nhận Đã Nhận Hàng & Quyết toán Doanh thu**

* **Màn hình**: SCR-CUS-17 (/don-hang/{id})

* **Thao tác**: 

  1. Với đơn hàng đã được shipper giao thành công (trạng thái `DaGiao`), khách hàng kiểm tra kiện hàng.

  2. Bấm nút **"Đã nhận được hàng"** (`POST /don-hang/{id}/da-nhan-hang`).

* **Kết quả**: Đơn hàng chuyển sang trạng thái `HoanTat`. Cơ chế ký quỹ giải phóng số tiền thanh toán: Hệ thống tự động trích tỷ lệ hoa hồng phí sàn và chuyển số tiền thực nhận còn lại vào doanh thu của gian hàng.

---

**19\. Đánh giá & Nhận xét Sản phẩm**

* **Màn hình**: SCR-CUS-18 (/san-pham/{id}#reviews-section)

* **Thao tác**: 

  1. Tại đơn hàng đã giao hoặc hoàn tất, bấm nút **"Đánh giá sản phẩm"** \-\> Hệ thống dẫn trực tiếp tới biểu mẫu đánh giá tại trang chi tiết cuốn sách (`/san-pham/{id}#reviews-section`).

  2. Chọn số sao đánh giá (từ 1 đến 5 sao).

  3. Thử nhập nhận xét dưới 10 ký tự \-\> Hệ thống báo lỗi yêu cầu tối thiểu 10 ký tự để đảm bảo chất lượng nhận xét.

  4. Nhập nhận xét đầy đủ chi tiết, đính kèm hình ảnh thực tế nếu muốn \-\> Bấm **"Gửi Đánh Giá"**.

* **Kết quả**: Đánh giá được lưu trữ và hiển thị ngay trên trang chi tiết cuốn sách, điểm đánh giá sao trung bình và số lượt đánh giá của sản phẩm được tự động tính toán lại.

---

**20\. Yêu cầu Trả hàng / Hoàn tiền**

* **Màn hình**: SCR-CUS-17 (/don-hang/{id})

* **Thao tác**: 

  1. Tại đơn hàng đã giao (`DaGiao`) nhận phải sách lỗi in ấn hoặc rách bìa, bấm nút **"Yêu cầu Trả hàng / Hoàn tiền"**.

  2. Chọn lý do khiếu nại, nhập mô tả chi tiết lỗi và tải lên hình ảnh bằng chứng.

  3. Bấm **"Gửi yêu cầu"**.

* **Kết quả**: Đơn hàng chuyển sang trạng thái `TraHangHoanTien`, hệ thống gửi thông báo sang Kênh người bán (Vendor Orders) để chờ xử lý.

---

**PHÂN HỆ 2: KÊNH NGƯỜI BÁN (VENDOR / SELLER CENTER)**

**21\. Đăng ký Mở Gian Hàng (Vendor Onboarding)**

* **Màn hình**: SCR-VEN-01, SCR-VEN-02 (/vendor/register, /vendor/pending)

* **Thao tác**: 

  1. Tài khoản khách hàng bấm **Đăng ký mở gian hàng** trên thanh menu người dùng.

  2. Điền thông tin gian hàng: Tên shop, Địa chỉ kho sách, Số điện thoại hotline, Email shop, Mô tả giới thiệu.

  3. Bấm **Gửi hồ sơ đăng ký**.

* **Kết quả**: Hồ sơ được lưu ở trạng thái `ChoDuyet`, người dùng được điều hướng tới màn hình chờ phê duyệt của Ban quản trị sàn (`/vendor/pending`).

---

**22\. Bảng điều khiển Vendor KPI Dashboard**

* **Màn hình**: SCR-VEN-03 (/vendor/dashboard)

* **Thao tác**: 

  1. Đăng nhập tài khoản Vendor (ví dụ: `vendor_kimdong`).

  2. Quan sát các thẻ chỉ số KPI: Tổng doanh thu thực nhận (đã trừ phí sàn), Tổng đơn hàng, Đơn chờ xử lý, Tổng số sách đang bán.

  3. Xem danh sách sách sắp hết hàng (Low Stock Warning) và biểu đồ biến động doanh thu.

* **Kết quả**: Chủ shop nắm bắt tình hình kinh doanh tổng thể trong ngày và theo tháng một cách trực quan.

---

**23\. Cài đặt Gian hàng & Trang trí Shop**

* **Màn hình**: SCR-VEN-04 (/vendor/profile)

* **Thao tác**: 

  1. Vào mục Cài đặt gian hàng (`/vendor/profile`).

  2. Thay đổi tên shop, cập nhật thông tin liên hệ, viết lại lời chào giới thiệu gian hàng.

  3. Tải lên tệp Banner trang trí và Logo đại diện mới.

  4. Bấm **Lưu thay đổi**.

* **Kết quả**: Giao diện gian hàng ngoài trang công khai của khách hàng (`/shop/{slug}`) lập tức được cập nhật thông tin và hình ảnh mới.

---

**24\. Đăng bán Sách mới (Tích hợp Cloudinary CDN)**

* **Màn hình**: SCR-VEN-05, SCR-VEN-06 (/vendor/products/add)

* **Thao tác**: 

  1. Bấm **Thêm sách mới**.

  2. Nhập thông tin: Tên tác phẩm, Mã ISBN, Giá bán niêm yết, Số lượng nhập kho ban đầu, Mức tồn cảnh báo tối thiểu, Phân loại ngành hàng (*Sách* / *Văn phòng phẩm* / *Quà tặng*).

  3. Chọn Danh mục thể loại và Tên tác giả liên quan.

  4. Chọn tệp ảnh bìa từ máy tính \-\> Bấm **Lưu & Đăng bán**.

* **Kết quả**: Ảnh bìa được tải trực tiếp lên kho lưu trữ đám mây **Cloudinary CDN**, sách được tạo ở trạng thái `ChoDuyet` chờ Quản lý sàn kiểm duyệt nội dung trước khi xuất hiện công khai trên sàn.

---

**25\. Quản lý Danh mục Sách của Shop**

* **Màn hình**: SCR-VEN-05, SCR-VEN-06 (/vendor/products)

* **Thao tác**: 

  1. Xem danh sách toàn bộ sách do gian hàng sở hữu.

  2. Tìm kiếm sách theo từ khóa, lọc theo nhóm ngành hàng, danh mục thể loại hoặc sắp xếp.

  3. Bấm **Chỉnh sửa** (`/vendor/products/edit/{id}`) để cập nhật lại giá bán, tồn kho hoặc mô tả.

  4. Bấm **Xóa sản phẩm** (`POST /vendor/products/delete/{id}`) đối với sản phẩm ngừng kinh doanh.

* **Kết quả**: Shop chủ động điều chỉnh danh mục hàng hóa linh hoạt theo nhu cầu kinh doanh.

---

**26\. Quản lý Tồn kho: Nhập Sản Phẩm Hàng Loạt Bằng Tệp CSV (Batch CSV Import)**

* **Màn hình**: SCR-VEN-08 (/vendor/products)

* **Thao tác**: 

  1. Vào danh sách sản phẩm của gian hàng (`/vendor/products`).

  2. Bấm nút **"Tải file CSV mẫu"** (`/vendor/products/sample-csv` tải về `mau_nhap_sach_shop.csv`) chứa các cột dữ liệu chuẩn: `ISBN,TenSP,GiaBan,SoLuongTon,LoaiSP,MaDanhMuc,MoTa`.

  3. Điền thông tin sách và tải tệp lên qua form **"Nhập sản phẩm từ file CSV"** (`POST /vendor/products/import-csv`).

  4. Bấm **Tải lên & Nhập kho**.

* **Kết quả**: Hệ thống tự động phân tích tệp CSV: Nếu sách đã có theo ISBN thì tự động cộng dồn số lượng tồn kho mới; nếu sách mới thì tự động khởi tạo và gán vào gian hàng của Vendor.

---

**27\. Quản lý Tồn kho: Kiểm Kê & Cân Bằng Kho**

* **Màn hình**: SCR-VEN-09, SCR-VEN-10 (/vendor/inventory/create, /vendor/inventory/{id})

* **Thao tác**: 

  1. Vào mục **Kiểm kê kho** \-\> Bấm **Tạo phiếu kiểm kê mới**.

  2. Chọn danh sách sách cần kiểm kê: Hệ thống hiển thị số lượng tồn trên sổ sách phần mềm.

  3. Nhập số lượng kiểm đếm thực tế tại kệ hàng (ví dụ: thực tế thiếu 2 cuốn so với hệ thống do thất lạc).

  4. Nhập lý do chênh lệch \-\> Bấm **Xác nhận & Cân bằng tồn kho**.

* **Kết quả**: Thủ tục lưu trữ `sp_ShopXacNhanKiemKe` được thực thi, tự động cân bằng tồn kho hệ thống về đúng con số thực tế và lưu vết biên bản kiểm kê minh bạch.

---

**28\. Xử lý Đơn hàng & Bàn giao Đơn vị Vận chuyển**

* **Màn hình**: SCR-VEN-07 (/vendor/orders)

* **Thao tác**: 

  1. Xem đơn hàng mới của khách tại danh sách đơn hàng của Shop (`/vendor/orders`).

  2. Bấm **Xác nhận đơn** (`POST /vendor/orders/{id}/confirm`) \-\> Trạng thái chuyển sang `DaXacNhan`.

  3. Sau khi đóng gói, bấm **Bàn giao vận chuyển** (`POST /vendor/orders/{id}/pickup`) \-\> Trạng thái chuyển sang `DaLayHang`.

  4. Bấm **Đang giao hàng** (`POST /vendor/orders/{id}/delivering`) \-\> Đơn hàng chuyển sang `DangGiao`.

  5. Cập nhật khi shipper giao đến khách \-\> Bấm **Giao thành công** (`POST /vendor/orders/{id}/delivered`), đơn hàng chuyển `DaGiao` (kích hoạt thời gian bảo vệ ký quỹ).

  6. Nếu khách hàng có yêu cầu hủy đơn (`YeuCauHuy`): Shop có thể bấm **Đồng ý hủy đơn** (hoàn tồn kho) hoặc **Từ chối hủy đơn**.

* **Kết quả**: Trạng thái đơn hàng được đồng bộ theo thời gian thực sang phía Khách hàng để theo dõi.

---

**29\. Xử lý Yêu cầu Trả hàng & Khiếu nại từ Khách**

* **Màn hình**: SCR-VEN-07 (/vendor/orders)

* **Thao tác**: 

  1. Mở đơn hàng khách đang có yêu cầu trả hàng (`TraHangHoanTien`).

  2. Xem hình ảnh bằng chứng và lý do khách gửi lên.

  3. Trường hợp đồng thuận: Bấm **Đồng ý trả hàng** (`POST /vendor/orders/{id}/accept-return`) để hoàn tiền và nhận lại hàng vào kho.

  4. Trường hợp không đồng thuận (hàng giao chuẩn, lỗi do người dùng): Bấm **Từ chối trả hàng** (`POST /vendor/orders/{id}/reject-return`) và nhập lý do giải trình.

* **Kết quả**: Nếu từ chối, đơn hàng tự động chuyển sang trạng thái `TranhChap` và được đưa lên **Trung tâm Tranh chấp của Ban Quản lý Sàn (Manager)** để mở phiên phân xử.

---

**30\. Tạo & Quản lý Voucher Riêng của Shop**

* **Màn hình**: SCR-VEN-11 (/vendor/promotions)

* **Thao tác**: 

  1. Vào mục Khuyến mãi của Shop (`/vendor/promotions`) \-\> Xem danh sách các voucher hiện có.

  2. Điền thông tin tạo voucher mới: Nhập mã code (ví dụ: `KIMDONG20K`), tên chương trình, ngày bắt đầu, ngày kết thúc.

  3. Chọn loại giảm: Giảm số tiền cố định (ví dụ: 20.000đ) hoặc Giảm theo phần trăm (ví dụ: 15% tối đa 40.000đ).

  4. Thiết lập giá trị đơn hàng tối thiểu áp dụng và số lượt sử dụng tối đa.

  5. Bấm **Kích hoạt voucher** (`POST /vendor/promotions/create`).

* **Kết quả**: Voucher của Shop được lưu trữ với phạm vi SHOP, chỉ hiển thị và áp dụng cho các sản phẩm do chính Shop bán.

---

**PHÂN HỆ 3: QUẢN LÝ VẬN HÀNH SÀN (MANAGER)**

**31\. Bento Dashboard Vận hành Sàn**

* **Màn hình**: SCR-ADM-01 (/admin/dashboard)

* **Thao tác**: 

  1. Đăng nhập tài khoản Manager hoặc Admin (ví dụ: `manager`).

  2. Quan sát các chỉ số vận hành toàn sàn: Doanh thu tháng này, Số đơn hàng tháng này, Số đơn hôm nay, Tổng số sách đã bán, Số sách sắp hết hàng / hết hàng, Số gian hàng đang hoạt động, Số đơn hàng chờ xử lý.

  3. Xem danh sách ấn phẩm mới đăng và danh sách đơn hàng phát sinh gần nhất.

* **Kết quả**: Ban quản lý nắm bắt chính xác sức khỏe kinh doanh và luồng vận hành của toàn bộ hệ thống.

---

**32\. Thẩm định & Phê duyệt Gian Hàng mới**

* **Màn hình**: SCR-ADM-04 (/admin/shops)

* **Thao tác**: 

  1. Vào danh sách gian hàng (`/admin/shops`).

  2. Tìm kiếm shop theo tiêu chí: Tên shop, Tên người bán, Số điện thoại, Email.

  3. Mở hồ sơ đăng ký gian hàng đang chờ xét duyệt: Kiểm tra hotline, email, địa chỉ kho sách.

  4. Bấm **Phê duyệt gian hàng** (`POST /admin/shops/{id}/approve`) (hoặc Từ chối có gửi kèm lý do).

  5. Đối với các gian hàng đang hoạt động: Quản lý có thể **Khóa gian hàng** (`lock`), **Mở khóa** (`unlock`), hoặc **Xóa gian hàng** (`delete`).

* **Kết quả**: Khi phê duyệt, trạng thái Shop chuyển sang `HoatDong`. Vai trò tài khoản người dùng được tự động nâng cấp thành `VENDOR`, kích hoạt quyền truy cập vào Kênh Người Bán.

---

**33\. Kiểm duyệt Sách Đăng bán của Gian Hàng**

* **Màn hình**: SCR-ADM-06 (/admin/shops/{id}/products)

* **Thao tác**: 

  1. Tại danh sách gian hàng (`/admin/shops`), bấm vào một gian hàng đối tác để mở danh mục sản phẩm của shop (`/admin/shops/{id}/products`).

  2. Lọc sản phẩm theo trạng thái kiểm duyệt: *Tất cả*, *Chờ duyệt* (`ChoDuyet`), *Bình thường* (`BinhThuong`), *Bị khóa bởi Admin* (`BiKhoaBoiAdmin`).

  3. Bấm **Phê duyệt sản phẩm** (`POST /admin/products/{id}/approve`).

* **Kết quả**: Sách chính thức chuyển trạng thái sang `BinhThuong`, được phép bày bán và xuất hiện công khai trên kết quả tìm kiếm và trang chủ của sàn.

---

**34\. Khóa & Mở khóa Sản phẩm Vi phạm**

* **Màn hình**: SCR-ADM-06 (/admin/shops/{id}/products)

* **Thao tác**: 

  1. Tìm một sản phẩm bị phản ánh vi phạm bản quyền hoặc nội dung không phù hợp trong danh mục của shop.

  2. Bấm nút **"Khóa vi phạm"** (`POST /admin/products/{id}/lock`) và xác nhận.

  3. Kiểm tra ngoài trang web khách hàng: Sản phẩm lập tức bị chặn hoặc thông báo ngưng bán khi truy cập trực tiếp bằng URL.

  4. Bấm **"Mở khóa"** (`POST /admin/products/{id}/unlock`) khi vấn đề vi phạm đã được khắc phục.

* **Kết quả**: Cơ chế kiểm duyệt nội dung hoạt động tức thì, bảo vệ bản quyền và uy tín của sàn.

---

**35\. Phân xử Tranh chấp Trả hàng / Hoàn tiền (Dispute Center)**

* **Màn hình**: SCR-ADM-09 (/admin/disputes)

* **Thao tác**: 

  1. Vào Trung tâm Tranh chấp (`/admin/disputes`) \-\> Chọn vụ việc khiếu nại giữa Khách hàng và Shop đang ở trạng thái `TranhChap`.

  2. Xem xét các luận điểm và hình ảnh bằng chứng đối chất của cả hai bên.

  3. Đưa ra phán quyết (`POST /admin/disputes/{id}/arbitrate`): 

     * Nếu lỗi do shop/vận chuyển: Tích chọn **Chấp nhận hoàn tiền cho Khách hàng** \-\> Đơn hàng chuyển sang `DaHoan`, hoàn tiền cho khách.

     * Nếu yêu cầu vô căn cứ: Không tích chọn \-\> Đưa ra phán quyết **Bác bỏ khiếu nại, giữ nguyên đơn hàng** \-\> Đơn hàng chuyển sang `HoanTat`, giải phóng tiền cho Shop.

* **Kết quả**: Hệ thống thực hiện phán quyết dứt điểm, bảo vệ tính công bằng cho cả người mua và người bán.

---

**36\. Quản lý Danh Mục Lớn (Ngành Hàng) & Danh Mục Chi Tiết (Master Data)**

* **Màn hình**: SCR-ADM-02 (/admin/categories)

* **Thao tác**: 

  1. Mở trang Quản lý danh mục (`/admin/categories`).

  2. Tại khối **Danh Mục Lớn (Ngành Hàng)**: Bấm **"+ Thêm Danh Mục Lớn"** để mở Drawer modal thêm mới ngành hàng (ví dụ: *Đồ Chơi Giáo Trí* với mã code `DoChoi`); hoặc bấm **Sửa / Tạm khóa / Xóa** ngành hàng. Hệ thống bảo vệ toàn vẹn: ngăn chặn xóa nếu ngành hàng đang có danh mục con hoặc sản phẩm liên kết.

  3. Tại khối **Danh Mục Chi Tiết (Danh Mục Con)**: Bấm **"+ Thêm Danh Mục Con"**, chọn ngành hàng trực thuộc (*Sách*, *Văn phòng phẩm*, *Quà tặng*...), nhập tên danh mục và mô tả. Có thể Sửa, Bật/Tắt trạng thái hoặc Xóa danh mục con (ngăn chặn xóa nếu đang có sản phẩm liên kết).

* **Kết quả**: Cấu trúc phân loại mới lập tức xuất hiện đồng bộ trong cây danh mục ngoài trang chủ và dropdown chọn phân loại khi các Shop đăng bán hàng.

---

**37\. Quản lý Danh mục Tác giả**

* **Màn hình**: SCR-ADM-03 (/admin/authors)

* **Thao tác**: 

  1. Vào mục Quản lý Tác giả (`/admin/authors`).

  2. Thêm mới tác giả sách (Tên tác giả, tiểu sử, mô tả) qua form lưu (`POST /admin/authors/save`).

  3. Chỉnh sửa thông tin hoặc bấm xem danh sách sách do tác giả sáng tác (`/admin/authors/{id}/books`).

  4. Thử xóa tác giả đang có sách trong hệ thống \-\> Quan sát thông báo chặn xóa để bảo toàn dữ liệu.

* **Kết quả**: Dữ liệu tác giả được chuẩn hóa, phục vụ việc liên kết tác giả với sách và bộ lọc tìm kiếm của độc giả.

---

**38\. Quản lý Đối tác Nhà Vận Chuyển**

* **Màn hình**: SCR-ADM-13 (/admin/shipping)

* **Thao tác**: 

  1. Xem danh sách các đối tác vận chuyển tích hợp (GHTK, GHN, Viettel Post...).

  2. Thêm mới hoặc chỉnh sửa cước phí cơ bản và thời gian giao hàng dự kiến (`POST /admin/shipping/save`).

  3. Bật/Tắt trạng thái hoạt động của từng đơn vị vận chuyển.

* **Kết quả**: Cước phí và danh sách hãng vận chuyển tại bước thanh toán của khách hàng tự động được cập nhật theo cấu hình mới nhất.

---

**PHÂN HỆ 4: QUẢN TRỊ VIÊN HỆ THỐNG (ADMIN)**

**39\. Quản trị Người dùng & Phân quyền RBAC**

* **Màn hình**: SCR-ADM-05 (/admin/users)

* **Thao tác**: 

  1. Đăng nhập tài khoản Super Admin (`admin`).

  2. Mở danh sách tài khoản toàn hệ thống: Lọc theo từ khóa, lọc theo vai trò `USER`, `VENDOR`, `MANAGER`, `ADMIN`.

  3. Thay đổi vai trò quyền hạn của một tài khoản (`POST /admin/users/{id}/change-role`, ví dụ: thăng cấp nhân viên lên `MANAGER`).

  4. Kiểm tra quy tắc bảo vệ: Admin không thể tự hạ quyền của chính mình; Manager không thể can thiệp hoặc sửa đổi tài khoản của Admin.

* **Kết quả**: Quyền hạn của tài khoản được cập nhật an toàn theo đúng ma trận phân quyền RBAC.

---

**40\. Khóa Tài khoản Vi phạm & Thu hồi Phiên tức thì (Session Invalidation)**

* **Màn hình**: SCR-ADM-05 (/admin/users)

* **Thao tác**: 

  1. Chọn một tài khoản vi phạm chính sách \-\> Bấm **Khóa tài khoản** (`POST /admin/users/{id}/toggle-lock`).

  2. Mở một tab/trình duyệt đang đăng nhập tài khoản vừa bị khóa, bấm F5 hoặc chuyển trang.

  3. Thử đăng nhập lại bằng tài khoản vừa bị khóa.

* **Kết quả**: Hệ thống lập tức thu hồi phiên làm việc thông qua cơ chế vô hiệu hóa token JWT (`tokenVersion`) và xóa phiên từ `SessionRegistry`, đẩy người dùng ra màn hình đăng nhập ngay lập tức. Khi cố gắng đăng nhập lại, hệ thống từ chối và báo tài khoản đã bị khóa.

---

**41\. Theo dõi Nhật ký Bảo mật & Audit Log**

* **Màn hình**: CSDL bảng nhat\_ky\_bao\_mat / Service SecurityAuditService

* **Thao tác**: 

  1. Thực hiện thao tác khóa tài khoản hoặc thay đổi vai trò người dùng ở bước trên.

  2. Kiểm tra bản ghi ghi nhận vết trong hệ thống kiểm toán bảo mật.

* **Kết quả**: Hệ thống tự động ghi lại bản ghi nhật ký: Tên tài khoản bị tác động, Hành động (`LOCK_ACCOUNT`, `UNLOCK_ACCOUNT`, `CHANGE_ROLE`), Dữ liệu vai trò cũ/mới, Người thực hiện (Actor) và Dấu thời gian chính xác.

---

**42\. Cấu hình Tỷ lệ Chiết khấu Sàn (Platform Commission)**

* **Màn hình**: SCR-ADM-14 (/admin/platform-fees, /admin/shops)

* **Thao tác**: 

  1. Vào mục Cấu hình chiết khấu sàn (`/admin/platform-fees`): Thay đổi tỷ lệ hoa hồng mặc định toàn sàn (ví dụ: từ 5.0% thành 6.0%) \-\> Bấm Lưu cấu hình.

  2. Vào danh sách gian hàng (`/admin/shops`): Tại cột Chiết khấu của từng Shop đối tác chiến lược (ví dụ: Shop Fahasa), điều chỉnh mức chiết khấu riêng (ví dụ: 3.5%) \-\> Bấm Lưu (`POST /admin/shops/{id}/commission`).

* **Kết quả**: Mọi đơn hàng phát sinh sau thời điểm lưu sẽ tự động tính toán tiền phí sàn và tiền thực nhận của shop theo đúng tỷ lệ chiết khấu đã cấu hình.

---

**43\. Quản lý & Tạo Mã Giảm Giá Toàn Sàn**

* **Màn hình**: SCR-ADM-10, SCR-ADM-11 (/admin/promotions, /admin/promotions/them)

* **Thao tác**: 

  1. Vào mục Khuyến mãi toàn sàn (`/admin/promotions`) \-\> Quan sát bảng thống kê Bento (Tổng số mã, Mã đang chạy, Lượt đã dùng, Tổng tiền giảm).

  2. Bấm **Tạo mã giảm giá mới** (`/admin/promotions/them`).

  3. Thiết lập thông số: Mã code, Tên sự kiện (ví dụ: *Đại Tiệc Sách Toàn Quốc*), Thời gian áp dụng, Mức giảm (% hoặc số tiền cố định), Mức giảm tối đa, Giá trị đơn hàng tối thiểu, Phạm vi toàn sàn (`TOAN_SAN`).

  4. Bấm **Lưu voucher**.

  5. Trải nghiệm các nút thao tác nhanh: **Nhân bản mã** (`duplicate`), **Bật / Tắt trạng thái** (`toggle`), **Xóa mã** (`xoa`).

* **Kết quả**: Mã giảm giá toàn sàn được kích hoạt và xuất hiện tại mục Săn Voucher cho toàn bộ khách hàng trên sàn sử dụng.

---

**44\. Tạo Mã Giảm Giá Hàng Loạt (Batch Voucher Generation)**

* **Màn hình**: SCR-ADM-12 (Modal Sinh mã hàng loạt tại /admin/promotions)

* **Thao tác**: 

  1. Tại trang Quản lý khuyến mãi (`/admin/promotions`), bấm nút **Sinh mã hàng loạt**.

  2. Nhập tiền tố mã (Prefix, ví dụ: `BOOKFEST2026_`), số lượng mã cần sinh ngẫu nhiên (ví dụ: 50 mã), mệnh giá giảm và hạn sử dụng.

  3. Bấm **Sinh mã hàng loạt** (`POST /admin/promotions/batch`).

  4. Bấm nút **"Xuất file CSV"** (`/admin/promotions/export-csv`) để tải về danh sách mã khuyến mãi.

* **Kết quả**: Hệ thống tự động tạo ra 50 mã voucher ngẫu nhiên duy nhất với cấu hình đồng nhất, sẵn sàng phục vụ các chiến dịch tặng quà hoặc mini-game tiếp thị.

---

**45\. Trung tâm Xuất & Nhập Dữ liệu Báo cáo (Data Export Center)**

* **Màn hình**: SCR-ADM-15 (/admin/import-export)

* **Thao tác**: 

  1. Truy cập Trung tâm xuất nhập dữ liệu (`/admin/import-export`).

  2. Quan sát các thẻ thống kê tổng quan: *Sản Phẩm Trong DB*, *Đơn Hàng Ghi Nhận*, *Tài Khoản Hệ Thống*, *Gian Hàng Đăng Ký*.

  3. Bấm các nút xuất dữ liệu một chạm: 

     * **Tải CSV Sản Phẩm** (`/admin/import-export/export/products`): Xuất mã SP, ISBN, tên, giá bán, tồn kho, đã bán, loại SP, danh mục, gian hàng sở hữu, trạng thái khóa.

     * **Tải CSV Đơn Hàng** (`/admin/import-export/export/orders`): Xuất mã ĐH, ngày đặt, khách hàng, SĐT, địa chỉ, gian hàng, đơn vị vận chuyển, phí ship, tiền giảm, tổng tiền, phương thức TT, trạng thái đơn.

     * **Tải CSV Tài Khoản** (`/admin/import-export/export/users`): Xuất mã TK, tên đăng nhập, email, vai trò, trạng thái, hình thức đăng nhập, ngày tạo.

     * **Tải CSV Gian Hàng** (`/admin/import-export/export/shops`): Xuất mã shop, tên shop, slug, chủ shop, SĐT, email, địa chỉ, tỷ lệ chiết khấu, trạng thái, ngày tạo.

  4. Tại khối Nhập sản phẩm: Bấm **"Tải File CSV Mẫu"** (`/admin/import-export/sample/products`) hoặc tải tệp CSV lên để nhập sản phẩm mới hàng loạt vào hệ thống.

* **Kết quả**: Trình duyệt tải về ngay các tệp tin định dạng chuẩn **CSV (UTF-8 BOM tương thích hoàn hảo với Microsoft Excel, không lỗi font tiếng Việt)** với cấu trúc dữ liệu đầy đủ, chuẩn hóa, phục vụ đối soát và lưu trữ.
