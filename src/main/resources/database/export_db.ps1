$dumpExe = 'C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqldump.exe'
$rawDump = 'C:\Users\Phat\the4BookStore\raw_dump.sql'

& $dumpExe -u root -p12345 --default-character-set=utf8mb4 --databases ql_nhasach --routines --triggers --single-transaction --hex-blob --result-file=$rawDump

if (Test-Path $rawDump) {
    Write-Host "Raw dump created."
    $content = [System.IO.File]::ReadAllText($rawDump, [System.Text.Encoding]::UTF8)

    # 1. Standardize Database Name to QL_NhaSach
    $content = [System.Text.RegularExpressions.Regex]::Replace($content, 'CREATE DATABASE /\*!32312 IF NOT EXISTS\*/ `ql_nhasach`[^;]*;', 'CREATE DATABASE /*!32312 IF NOT EXISTS*/ `QL_NhaSach` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;')
    $content = $content.Replace('USE `ql_nhasach`;', 'USE `QL_NhaSach`;')
    $content = $content.Replace('Current Database: `ql_nhasach`', 'Current Database: `QL_NhaSach`')
    $content = $content.Replace("Dumping routines for database 'ql_nhasach'", "Dumping routines for database 'QL_NhaSach'")

    # 2. Remove DEFINER=... completely
    # Pattern matches "DEFINER=`...`@`...` "
    $definerPattern = 'DEFINER\s*=\s*`[^`]+`@`[^`]+`\s*'
    $content = [System.Text.RegularExpressions.Regex]::Replace($content, $definerPattern, '')

    # 3. Add header comments and log_bin_trust_function_creators
    $header = @"
-- ====================================================================
-- THE4BOOKSTORE - FULL DATABASE DUMP (SCHEMA + DATA + ROUTINES)
-- Database: QL_NhaSach
-- Encoding: UTF-8 (utf8mb4)
-- Date: 2026-10-06
-- Compatible with MySQL 8.0+
-- ====================================================================

/*!50003 SET @OLD_LOG_BIN_TRUST_FUNCTION_CREATORS=@@log_bin_trust_function_creators */;
/*!50003 SET GLOBAL log_bin_trust_function_creators=1 */;

"@
    $content = $header + $content

    [System.IO.File]::WriteAllText('C:\Users\Phat\the4BookStore\src\main\resources\database\ql_nhasach_full_dump.sql', $content, [System.Text.Encoding]::UTF8)
    [System.IO.File]::WriteAllText('C:\Users\Phat\the4BookStore\DOCUMENT\ql_nhasach_dump.sql', $content, [System.Text.Encoding]::UTF8)

    Remove-Item $rawDump -Force
    Write-Host "Successfully generated clean dumps for both locations."
} else {
    Write-Host "Dump execution failed."
}
