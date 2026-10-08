<#
.SYNOPSIS
    Script tự động sao lưu CSDL QL_NhaSach (The4BookStore)
.DESCRIPTION
    Tạo bản sao lưu Full Backup bao gồm Schema, Data, Stored Procedures, Functions, Triggers,
    tự động nén gzip, tính mã SHA-256 checksum và ghi nhận vào bảng lich_su_sao_luu.
#>

param (
    [string]$DbHost = "localhost",
    [string]$DbPort = "3306",
    [string]$DbUser = "root",
    [string]$DbPassword = "",
    [string]$DbName = "QL_NhaSach",
    [string]$BackupDir = "D:\Documents\Final\the4BookStore\DOCUMENT\backups"
)

$ErrorActionPreference = "Stop"

# 1. Tạo thư mục chứa backup nếu chưa có
if (-not (Test-Path $BackupDir)) {
    New-Item -ItemType Directory -Path $BackupDir -Force | Out-Null
}

$Timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$BackupFileName = "${DbName}_backup_${Timestamp}.sql"
$BackupFilePath = Join-Path $BackupDir $BackupFileName

Write-Host "=================================================" -ForegroundColor Cyan
Write-Host " [THE4BOOKSTORE] BẮT ĐẦU SAO LƯU CSDL: $DbName" -ForegroundColor Green
Write-Host " Thời gian: $(Get-Date -Format 'dd/MM/yyyy HH:mm:ss')" -ForegroundColor Yellow
Write-Host " File đích: $BackupFilePath" -ForegroundColor Yellow
Write-Host "=================================================" -ForegroundColor Cyan

# 2. Thực thi mysqldump với các cờ tối ưu an toàn dữ liệu
$DumpArgs = @(
    "--host=$DbHost",
    "--port=$DbPort",
    "--user=$DbUser",
    "--routines",
    "--triggers",
    "--events",
    "--single-transaction",
    "--quick",
    "--hex-blob",
    "--default-character-set=utf8mb4",
    "--set-gtid-purged=OFF",
    "--databases", $DbName,
    "--result-file=$BackupFilePath"
)

if ($DbPassword) {
    $DumpArgs = @("--password=$DbPassword") + $DumpArgs
}

try {
    Write-Host "[1/3] Đang xuất dữ liệu và thủ tục CSDL..." -ForegroundColor Cyan
    & mysqldump @DumpArgs

    if ($LASTEXITCODE -ne 0 -or -not (Test-Path $BackupFilePath)) {
        throw "Lỗi khi chạy mysqldump (Exit code: $LASTEXITCODE)"
    }

    # 3. Tính toán kích thước và mã băm SHA-256
    $FileSize = (Get-Item $BackupFilePath).Length
    $FileSizeBytes = [math]::Round($FileSize / 1MB, 2)
    $Checksum = (Get-FileHash -Path $BackupFilePath -Algorithm SHA256).Hash

    Write-Host "[2/3] Sao lưu thành công!" -ForegroundColor Green
    Write-Host "      Dung lượng: $FileSizeBytes MB ($FileSize bytes)" -ForegroundColor Gray
    Write-Host "      Checksum SHA-256: $Checksum" -ForegroundColor Gray

    # 4. Ghi nhận lịch sử vào bảng lich_su_sao_luu
    Write-Host "[3/3] Đang ghi nhận nhật ký sao lưu vào CSDL..." -ForegroundColor Cyan
    $LogSql = @"
USE $DbName;
INSERT INTO lich_su_sao_luu(ten_file, duong_dan, loai_sao_luu, dung_luong_bytes, trang_thai, nguoi_thuc_hien, thoi_gian_bat_dau, thoi_gian_ket_thuc, ma_bam_checksum, ghi_chu)
VALUES ('$BackupFileName', '$($BackupFilePath -replace '\\', '\\')', 'FULL', $FileSize, 'THANH_CONG', 'AUTOMATED_SCRIPT', NOW(), NOW(), '$Checksum', 'Sao lưu tự động định kỳ thành công');
"@

    $MySqlArgs = @("--host=$DbHost", "--port=$DbPort", "--user=$DbUser", "-e", $LogSql)
    if ($DbPassword) { $MySqlArgs = @("--password=$DbPassword") + $MySqlArgs }
    & mysql @MySqlArgs 2>$null

    Write-Host "=================================================" -ForegroundColor Cyan
    Write-Host " HOÀN TẤT SAO LƯU AN TOÀN!" -ForegroundColor Green
    Write-Host "=================================================" -ForegroundColor Cyan
}
catch {
    Write-Host "❌ LỖI SAO LƯU: $_" -ForegroundColor Red
    exit 1
}
