@echo off
chcp 65001 >nul
echo [THE4BOOKSTORE] Dang chay tien trinh sao luu CSDL...
powershell -ExecutionPolicy Bypass -File "%~dp0backup_database.ps1"
if %ERRORLEVEL% equ 0 (
    echo Sao luu thanh cong!
) else (
    echo Co loi xay ra trong qua trinh sao luu!
)
pause
