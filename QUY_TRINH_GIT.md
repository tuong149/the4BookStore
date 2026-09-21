# QUY TRÌNH QUẢN LÝ MÃ NGUỒN (GIT WORKFLOW)

Để đảm bảo dự án hoạt động trơn tru và không bị xung đột code (conflict) giữa các thành viên, toàn bộ team thống nhất tuân thủ quy trình Git Flow dưới đây.

## 1. Cấu Trúc Nhánh (Branches)
- **`main`**: Nhánh chứa code hoàn chỉnh, ổn định nhất. (Tuyệt đối **KHÔNG** code trực tiếp trên nhánh này).
- **`develop`**: Nhánh tích hợp code để mọi người cùng test. Các tính năng sau khi làm xong sẽ được gom vào đây.
- **`feature/tên-chức-năng`**: Nhánh để làm chức năng mới (VD: `feature/trang-dang-nhap`, `feature/gio-hang`).
- **`fix/tên-lỗi`**: Nhánh để sửa một lỗi (bug) cụ thể (VD: `fix/loi-nut-mua-hang`).

---

## 2. Quy Trình Làm Việc Tiêu Chuẩn (Dành Cho Mọi Thành Viên)

### Bước 1: Lấy code mới nhất về trước khi làm việc
Luôn bắt đầu từ nhánh `develop` và cập nhật code mới nhất từ mọi người:
```bash
git checkout develop
git pull origin develop
```

### Bước 2: Tạo nhánh riêng để bắt đầu code
Tạo nhánh mới từ `develop` (Tên nhánh viết thường, không dấu, cách nhau bằng dấu gạch ngang):
```bash
# Nếu làm tính năng mới:
git checkout -b feature/ten-chuc-nang

# Nếu sửa lỗi:
git checkout -b fix/ten-loi
```

### Bước 3: Code và Commit
Thực hiện code trên nhánh của bạn. Sau khi hoàn thành hoặc đến cuối ngày, hãy commit:
```bash
git add .
git commit -m "feat: [Mô tả ngắn gọn bạn vừa làm gì]"
```
*(Lưu ý: Dùng `feat:` cho tính năng, `fix:` cho sửa lỗi, `docs:` cho tài liệu)*

### Bước 4: Đẩy nhánh của bạn lên GitHub
```bash
git push -u origin tên-nhánh-của-bạn
```
*(VD: `git push -u origin feature/gio-hang`)*

### Bước 5: Gộp code vào Develop (Trên GitHub)
1. Lên trang GitHub của nhóm.
2. Bấm nút **"Compare & pull request"**.
3. Chọn gộp từ nhánh của bạn (`feature/...`) **VÀO** nhánh `develop` (Tuyệt đối không gộp thẳng vào `main`).
4. Báo cho một thành viên khác vào Review (Xem lại) và bấm nút Merge.

---

## 3. Quy Trình Test Chung (Dành Cho Cả Nhóm)

Sau khi có người đưa code mới vào nhánh `develop`, mọi người cần kéo về máy để test:

```bash
git checkout develop
git pull origin develop
```
- Hãy chạy thử dự án và kiểm tra xem tính năng mới có hoạt động không, có làm hỏng tính năng cũ không.

---

## 4. Đưa Sản Phẩm Ra Mắt (Merge vào Main)

Khi toàn bộ nhóm đã test kỹ trên nhánh `develop` và xác nhận mọi thứ hoạt động hoàn hảo 100%, Trưởng nhóm (hoặc người được giao quyền) sẽ thực hiện đưa code vào `main`:

1. Lên GitHub, tạo Pull Request từ `develop` **VÀO** `main`.
2. Bấm Merge.
3. Cập nhật nhánh `main` dưới máy cá nhân:
```bash
git checkout main
git pull origin main
```

🎉 **Chúc team code ít bug, merge không conflict!** 🎉
