@echo off
chcp 65001 >nul
if "%~1"=="" (
    echo Vui long keo tha file backup .sql vao file .bat nay hoac truyen duong dan file!
    echo Vi du: restore_database.bat "D:\Documents\Final\the4BookStore\DOCUMENT\backups\QL_NhaSach_backup_20261008_190000.sql"
    pause
    exit /b 1
)
powershell -ExecutionPolicy Bypass -File "%~dp0restore_database.ps1" -BackupFile "%~1"
pause
