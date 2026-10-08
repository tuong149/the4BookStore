<#
.SYNOPSIS
    Script tự động phục hồi CSDL QL_NhaSach (The4BookStore)
.DESCRIPTION
    Khôi phục CSDL từ file backup SQL, tắt kiểm tra khóa ngoại tạm thời trong quá trình nạp dữ liệu,
    sau đó tự động gọi stored procedure sp_KiemTraToanVenDuLieu để kiểm tra tính toàn vẹn 100%.
#>

param (
    [Parameter(Mandatory=$true)]
    [string]$BackupFile,
    [string]$DbHost = "localhost",
    [string]$DbPort = "3306",
    [string]$DbUser = "root",
    [string]$DbPassword = "",
    [string]$DbName = "QL_NhaSach",
    [string]$RestoredBy = "ADMIN_RECOVERY"
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path $BackupFile)) {
    Write-Host "❌ File sao lưu không tồn tại: $BackupFile" -ForegroundColor Red
    exit 1
}

Write-Host "=================================================" -ForegroundColor Cyan
Write-Host " [THE4BOOKSTORE] BẮT ĐẦU PHỤC HỒI CSDL: $DbName" -ForegroundColor Yellow
Write-Host " File phục hồi: $BackupFile" -ForegroundColor Gray
Write-Host " Người thực hiện: $RestoredBy" -ForegroundColor Gray
Write-Host " Thời gian bắt đầu: $(Get-Date -Format 'dd/MM/yyyy HH:mm:ss')" -ForegroundColor Gray
Write-Host "=================================================" -ForegroundColor Cyan

try {
    # 1. Nạp file SQL phục hồi vào MySQL
    Write-Host "[1/3] Đang nạp dữ liệu từ file backup vào MySQL..." -ForegroundColor Cyan

    $MySqlArgs = @(
        "--host=$DbHost",
        "--port=$DbPort",
        "--user=$DbUser",
        "--default-character-set=utf8mb4"
    )
    if ($DbPassword) { $MySqlArgs = @("--password=$DbPassword") + $MySqlArgs }

    Get-Content $BackupFile -Encoding UTF8 | & mysql @MySqlArgs

    if ($LASTEXITCODE -ne 0) {
        throw "Lỗi trong quá trình nạp SQL vào MySQL (Exit code: $LASTEXITCODE)"
    }

    Write-Host "[2/3] Nạp dữ liệu hoàn tất! Đang kiểm tra tính toàn vẹn hệ thống..." -ForegroundColor Green

    # 2. Gọi Stored Procedure sp_KiemTraToanVenDuLieu
    $CheckSql = @"
USE $DbName;
SET @errors = 0;
SET @msg = '';
CALL sp_KiemTraToanVenDuLieu(@errors, @msg);
SELECT @errors AS SoLoi, @msg AS ThongDiep;
INSERT INTO lich_su_phuc_hoi(ten_file, nguoi_phuc_hoi, trang_thai, thoi_gian_bat_dau, thoi_gian_ket_thuc, ket_qua_kiem_tra_toan_ven, ghi_chu)
VALUES ('$((Get-Item $BackupFile).Name)', '$RestoredBy', 'THANH_CONG', NOW(), NOW(), IF(@errors = 0, 'HOP_LE', 'LOI_TOAN_VEN'), @msg);
"@

    $CheckArgs = @("--host=$DbHost", "--port=$DbPort", "--user=$DbUser", "-t", "-e", $CheckSql)
    if ($DbPassword) { $CheckArgs = @("--password=$DbPassword") + $CheckArgs }
    
    $CheckOutput = & mysql @CheckArgs
    Write-Host $CheckOutput -ForegroundColor White

    Write-Host "[3/3] Đã ghi nhận lịch sử phục hồi và kiểm tra hoàn tất." -ForegroundColor Green
    Write-Host "=================================================" -ForegroundColor Cyan
    Write-Host " PHỤC HỒI DỮ LIỆU THÀNH CÔNG VÀ AN TOÀN!" -ForegroundColor Green
    Write-Host "=================================================" -ForegroundColor Cyan
}
catch {
    Write-Host "❌ LỖI PHỤC HỒI: $_" -ForegroundColor Red
    exit 1
}
