# QUY TRÌNH VÀ CHIẾN LƯỢC SAO LƯU & PHỤC HỒI CSDL (BACKUP & RECOVERY STRATEGY)
**Hệ thống:** The4BookStore E-Commerce & Marketplace  
**Cơ sở dữ liệu:** `QL_NhaSach` (MySQL 8.0+)

---

## 1. Mục tiêu và Chỉ số An toàn Dữ liệu (RTO & RPO)
- **RPO (Recovery Point Objective):** $\le 1$ giờ đối với dữ liệu giao dịch đơn hàng và thanh toán.
- **RTO (Recovery Time Objective):** $\le 15$ phút để khôi phục toàn bộ hệ thống khi có sự cố.

---

## 2. Cấu trúc Bảng Quản lý Sao lưu & Phục hồi trong CSDL
1. **`lich_su_sao_luu`**:
   - Lưu trữ lịch sử toàn bộ các lần sao lưu (Full, Differential, Schema).
   - Ghi nhận `ten_file`, `duong_dan`, `dung_luong_bytes`, `ma_bam_checksum` (SHA-256), `thoi_gian_bat_dau`, `thoi_gian_ket_thuc`, `trang_thai`.
2. **`lich_su_phuc_hoi`**:
   - Ghi nhận nhật ký mỗi lần restore CSDL, người thực hiện, thời gian và kết quả kiểm tra tính toàn vẹn (`ket_qua_kiem_tra_toan_ven`).
3. **`nhat_ky_thay_doi_du_lieu`**:
   - Ghi nhận thay đổi quan trọng (Change Data Capture / Audit Trail) phục vụ rà soát và khắc phục thảm họa (Disaster Recovery).

---

## 3. Các Stored Procedure Hỗ trợ Sao lưu & Phục hồi
| Stored Procedure | Mục đích |
| :--- | :--- |
| `sp_TaoLichSuSaoLuu` | Khởi tạo phiên sao lưu mới và ghi nhận vào bảng quản lý |
| `sp_HoanTatSaoLuu` | Cập nhật kết quả sao lưu, dung lượng và mã băm SHA-256 |
| `sp_GhiNhanPhucHoi` | Ghi nhận sự kiện phục hồi CSDL |
| `sp_KiemTraToanVenDuLieu` | **Healthcheck tự động:** Kiểm tra 100% các khóa ngoại, đơn mồ côi, giỏ hàng mồ côi, tồn kho âm sau khi phục hồi |
| `sp_DonDepLichSuSaoLuu` | Tự động dọn dẹp các bản ghi log sao lưu cũ hơn $N$ ngày |

---

## 4. Hướng dẫn Thực hiện Sao lưu (Backup)
### Cách 1: Chạy tự động bằng script PowerShell
```powershell
.\DOCUMENT\scripts\backup_database.ps1
```
*(Hoặc nhấp đúp chuột vào file `DOCUMENT\scripts\backup_database.bat`)*

### Cách 2: Lệnh mysqldump tiêu chuẩn
```bash
mysqldump --host=localhost --port=3306 --user=root -p \
  --routines --triggers --events --single-transaction --quick --hex-blob \
  --default-character-set=utf8mb4 --set-gtid-purged=OFF \
  --databases QL_NhaSach > "DOCUMENT/backups/QL_NhaSach_backup_full.sql"
```

---

## 5. Hướng dẫn Phục hồi (Recovery / Restore)
### Cách 1: Chạy bằng script phục hồi có kiểm tra toàn vẹn
```powershell
.\DOCUMENT\scripts\restore_database.ps1 -BackupFile "D:\Documents\Final\the4BookStore\DOCUMENT\backups\QL_NhaSach_backup_xxxx.sql"
```
*(Hoặc kéo thả file `.sql` vào `DOCUMENT\scripts\restore_database.bat`)*

### Cách 2: Phục hồi thủ công và kiểm tra
```bash
mysql --host=localhost --port=3306 --user=root -p < "DOCUMENT/backups/QL_NhaSach_backup_xxxx.sql"
```
Sau khi nạp xong, chạy câu lệnh kiểm tra toàn vẹn:
```sql
USE QL_NhaSach;
SET @err = 0; SET @msg = '';
CALL sp_KiemTraToanVenDuLieu(@err, @msg);
SELECT @err AS SoLoi, @msg AS ThongDiep;
```

---

## 6. Lên lịch Sao lưu Tự động (Windows Task Scheduler)
Để hệ thống tự động sao lưu hàng ngày lúc **00:00 AM**:
```cmd
schtasks /create /tn "The4BookStore_Daily_DB_Backup" /tr "D:\Documents\Final\the4BookStore\DOCUMENT\scripts\backup_database.bat" /sc daily /st 00:00
```
