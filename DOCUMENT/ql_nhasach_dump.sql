-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: ql_nhasach
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `chi_tiet_don_hang`
--

DROP TABLE IF EXISTS `chi_tiet_don_hang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chi_tiet_don_hang` (
  `madh` int NOT NULL,
  `masp` int NOT NULL,
  `don_gia` int NOT NULL,
  `so_luong` int NOT NULL,
  PRIMARY KEY (`madh`,`masp`),
  KEY `FK98u87ds1aj1339w293i4er4fx` (`masp`),
  CONSTRAINT `FK98u87ds1aj1339w293i4er4fx` FOREIGN KEY (`masp`) REFERENCES `san_pham` (`masp`),
  CONSTRAINT `FKodihdwuetirdvsli7a1oobwnq` FOREIGN KEY (`madh`) REFERENCES `don_hang` (`madh`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chi_tiet_don_hang`
--

LOCK TABLES `chi_tiet_don_hang` WRITE;
/*!40000 ALTER TABLE `chi_tiet_don_hang` DISABLE KEYS */;
/*!40000 ALTER TABLE `chi_tiet_don_hang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chi_tiet_gio_hang`
--

DROP TABLE IF EXISTS `chi_tiet_gio_hang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chi_tiet_gio_hang` (
  `ma_gio_hang` int NOT NULL,
  `masp` int NOT NULL,
  `so_luong` int NOT NULL,
  PRIMARY KEY (`ma_gio_hang`,`masp`),
  KEY `FKkmq7k0m84x6j0g74rxs4fw0wg` (`masp`),
  CONSTRAINT `FKdqjc7vkyfiup4ydgmhx8sdwii` FOREIGN KEY (`ma_gio_hang`) REFERENCES `gio_hang` (`ma_gio_hang`),
  CONSTRAINT `FKkmq7k0m84x6j0g74rxs4fw0wg` FOREIGN KEY (`masp`) REFERENCES `san_pham` (`masp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chi_tiet_gio_hang`
--

LOCK TABLES `chi_tiet_gio_hang` WRITE;
/*!40000 ALTER TABLE `chi_tiet_gio_hang` DISABLE KEYS */;
/*!40000 ALTER TABLE `chi_tiet_gio_hang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chi_tiet_kiem_ke`
--

DROP TABLE IF EXISTS `chi_tiet_kiem_ke`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chi_tiet_kiem_ke` (
  `ma_phieu_kiem_ke` int NOT NULL,
  `masp` int NOT NULL,
  `ly_do` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `so_luong_he_thong` int NOT NULL,
  `so_luong_thuc_te` int NOT NULL,
  PRIMARY KEY (`ma_phieu_kiem_ke`,`masp`),
  KEY `FKghnffbkpvpnp3ahq0g43y7e0g` (`masp`),
  CONSTRAINT `FKghnffbkpvpnp3ahq0g43y7e0g` FOREIGN KEY (`masp`) REFERENCES `san_pham` (`masp`),
  CONSTRAINT `FKkc6e2jvl6hvhqtm42mr6xfebi` FOREIGN KEY (`ma_phieu_kiem_ke`) REFERENCES `phieu_kiem_ke` (`ma_phieu_kiem_ke`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chi_tiet_kiem_ke`
--

LOCK TABLES `chi_tiet_kiem_ke` WRITE;
/*!40000 ALTER TABLE `chi_tiet_kiem_ke` DISABLE KEYS */;
/*!40000 ALTER TABLE `chi_tiet_kiem_ke` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chi_tiet_phieu_nhap`
--

DROP TABLE IF EXISTS `chi_tiet_phieu_nhap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chi_tiet_phieu_nhap` (
  `mapn` int NOT NULL,
  `masp` int NOT NULL,
  `don_gia_nhap` int NOT NULL,
  `so_luong` int NOT NULL,
  PRIMARY KEY (`mapn`,`masp`),
  KEY `FKdjxtx10fxow826rs8ju4rlcvq` (`masp`),
  CONSTRAINT `FKdjxtx10fxow826rs8ju4rlcvq` FOREIGN KEY (`masp`) REFERENCES `san_pham` (`masp`),
  CONSTRAINT `FKkl3e6ypo8mcm8st8axnq3yj4g` FOREIGN KEY (`mapn`) REFERENCES `phieu_nhap` (`mapn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chi_tiet_phieu_nhap`
--

LOCK TABLES `chi_tiet_phieu_nhap` WRITE;
/*!40000 ALTER TABLE `chi_tiet_phieu_nhap` DISABLE KEYS */;
/*!40000 ALTER TABLE `chi_tiet_phieu_nhap` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chi_tiet_tra_hang`
--

DROP TABLE IF EXISTS `chi_tiet_tra_hang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chi_tiet_tra_hang` (
  `ma_phieu_tra` int NOT NULL,
  `masp` int NOT NULL,
  `don_gia` int NOT NULL,
  `so_luong` int NOT NULL,
  PRIMARY KEY (`ma_phieu_tra`,`masp`),
  KEY `FK78org0kyuit4uqye0lwy15bu` (`masp`),
  CONSTRAINT `FK3dy6w1ltkf3jmrrqk34axdsxt` FOREIGN KEY (`ma_phieu_tra`) REFERENCES `phieu_tra_hang` (`ma_phieu_tra`),
  CONSTRAINT `FK78org0kyuit4uqye0lwy15bu` FOREIGN KEY (`masp`) REFERENCES `san_pham` (`masp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chi_tiet_tra_hang`
--

LOCK TABLES `chi_tiet_tra_hang` WRITE;
/*!40000 ALTER TABLE `chi_tiet_tra_hang` DISABLE KEYS */;
/*!40000 ALTER TABLE `chi_tiet_tra_hang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `danh_gia`
--

DROP TABLE IF EXISTS `danh_gia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `danh_gia` (
  `ma_danh_gia` int NOT NULL AUTO_INCREMENT,
  `ngay_danh_gia` datetime(6) NOT NULL,
  `noi_dung` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `so_sao` int NOT NULL,
  `trang_thai` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `madh` int NOT NULL,
  `makh` int NOT NULL,
  `masp` int NOT NULL,
  PRIMARY KEY (`ma_danh_gia`),
  UNIQUE KEY `UKqc20lbrx414frax35ygtqldwh` (`makh`,`masp`,`madh`),
  KEY `FK2x7pqo1qrotk9d8umuemqa3gi` (`madh`),
  KEY `FK6ngu0jah0wdv2nk6bsxcwygh9` (`masp`),
  CONSTRAINT `FK1s2u0uipc62t77xq81xf9lbed` FOREIGN KEY (`makh`) REFERENCES `khach_hang` (`makh`),
  CONSTRAINT `FK2x7pqo1qrotk9d8umuemqa3gi` FOREIGN KEY (`madh`) REFERENCES `don_hang` (`madh`),
  CONSTRAINT `FK6ngu0jah0wdv2nk6bsxcwygh9` FOREIGN KEY (`masp`) REFERENCES `san_pham` (`masp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `danh_gia`
--

LOCK TABLES `danh_gia` WRITE;
/*!40000 ALTER TABLE `danh_gia` DISABLE KEYS */;
/*!40000 ALTER TABLE `danh_gia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `danh_muc`
--

DROP TABLE IF EXISTS `danh_muc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `danh_muc` (
  `ma_danh_muc` int NOT NULL AUTO_INCREMENT,
  `mo_ta` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ten_danh_muc` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `trang_thai` bit(1) NOT NULL,
  PRIMARY KEY (`ma_danh_muc`),
  UNIQUE KEY `UK5cmp5hxtq0cc1p5u3ofiser6v` (`ten_danh_muc`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `danh_muc`
--

LOCK TABLES `danh_muc` WRITE;
/*!40000 ALTER TABLE `danh_muc` DISABLE KEYS */;
INSERT INTO `danh_muc` VALUES (1,'Tác phẩm tiểu thuyết trong và ngoài nước','Tiểu Thuyết & Văn Học',_binary ''),(2,'Kỹ năng sống, tư duy và phát triển cá nhân','Phát Triển Bản Thân',_binary ''),(3,'Kinh doanh, đầu tư và tâm lý tài chính','Kinh Tế & Tài Chính',_binary ''),(4,'Lịch sử nhân loại, vũ trụ và khoa học thường thức','Khoa Học & Lịch Sử',_binary ''),(5,'Tiểu thuyết viễn tưởng kinh điển và hiện đại','Khoa Học Viễn Tưởng',_binary '');
/*!40000 ALTER TABLE `danh_muc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `don_hang`
--

DROP TABLE IF EXISTS `don_hang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `don_hang` (
  `madh` int NOT NULL AUTO_INCREMENT,
  `dia_chi_giao` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ly_do_huy` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ly_do_tu_choi` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ngay_dat` datetime(6) NOT NULL,
  `ngay_hoan_thanh` datetime(6) DEFAULT NULL,
  `ngay_xac_nhan` datetime(6) DEFAULT NULL,
  `so_dien_thoai_giao` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tien_giam` int NOT NULL,
  `tong_tien` int NOT NULL,
  `trang_thai` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `makh` int NOT NULL,
  `makm` int DEFAULT NULL,
  PRIMARY KEY (`madh`),
  KEY `FKiy9wbkgc3iv3ome6new025n9o` (`makh`),
  KEY `FKmailjslygm19yf3a0vxln8tx6` (`makm`),
  CONSTRAINT `FKiy9wbkgc3iv3ome6new025n9o` FOREIGN KEY (`makh`) REFERENCES `khach_hang` (`makh`),
  CONSTRAINT `FKmailjslygm19yf3a0vxln8tx6` FOREIGN KEY (`makm`) REFERENCES `khuyen_mai` (`makm`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `don_hang`
--

LOCK TABLES `don_hang` WRITE;
/*!40000 ALTER TABLE `don_hang` DISABLE KEYS */;
/*!40000 ALTER TABLE `don_hang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gio_hang`
--

DROP TABLE IF EXISTS `gio_hang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gio_hang` (
  `ma_gio_hang` int NOT NULL AUTO_INCREMENT,
  `ngay_cap_nhat` datetime(6) NOT NULL,
  `ngay_tao` datetime(6) NOT NULL,
  `makh` int NOT NULL,
  PRIMARY KEY (`ma_gio_hang`),
  KEY `FKr918hcrsa82ly9duc4jsec5n8` (`makh`),
  CONSTRAINT `FKr918hcrsa82ly9duc4jsec5n8` FOREIGN KEY (`makh`) REFERENCES `khach_hang` (`makh`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gio_hang`
--

LOCK TABLES `gio_hang` WRITE;
/*!40000 ALTER TABLE `gio_hang` DISABLE KEYS */;
/*!40000 ALTER TABLE `gio_hang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hoa_don`
--

DROP TABLE IF EXISTS `hoa_don`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hoa_don` (
  `mahd` int NOT NULL AUTO_INCREMENT,
  `ngay_lap` datetime(6) NOT NULL,
  `tong_tien_thanh_toan` int NOT NULL,
  `trang_thai` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `madh` int NOT NULL,
  `manv` int NOT NULL,
  PRIMARY KEY (`mahd`),
  UNIQUE KEY `UKi5dsqpkn4sb6xnm62vxche09x` (`madh`),
  KEY `FKr67k5gttxaonk5trdfwvcgk80` (`manv`),
  CONSTRAINT `FK83d5kek5uoh8e1kralry52seo` FOREIGN KEY (`madh`) REFERENCES `don_hang` (`madh`),
  CONSTRAINT `FKr67k5gttxaonk5trdfwvcgk80` FOREIGN KEY (`manv`) REFERENCES `nhan_vien` (`manv`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hoa_don`
--

LOCK TABLES `hoa_don` WRITE;
/*!40000 ALTER TABLE `hoa_don` DISABLE KEYS */;
/*!40000 ALTER TABLE `hoa_don` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `khach_hang`
--

DROP TABLE IF EXISTS `khach_hang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `khach_hang` (
  `makh` int NOT NULL AUTO_INCREMENT,
  `dia_chi` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ho_ten` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ngay_dang_ky` datetime(6) NOT NULL,
  `so_dien_thoai` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ma_tai_khoan` int DEFAULT NULL,
  PRIMARY KEY (`makh`),
  UNIQUE KEY `UK6j1oks4nrqpqnl0b6cnp85vrd` (`so_dien_thoai`),
  UNIQUE KEY `UKiv6nhi0meph4iaotgx5h0yg63` (`ma_tai_khoan`),
  CONSTRAINT `FKchhnalcpr9cvc1leppvfftoh5` FOREIGN KEY (`ma_tai_khoan`) REFERENCES `tai_khoan` (`ma_tai_khoan`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `khach_hang`
--

LOCK TABLES `khach_hang` WRITE;
/*!40000 ALTER TABLE `khach_hang` DISABLE KEYS */;
INSERT INTO `khach_hang` VALUES (1,'Ở đây','caotuong14@gmail.com','Nguyễn Duy','2026-09-21 18:03:58.136689','09999999999',6);
/*!40000 ALTER TABLE `khach_hang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `khuyen_mai`
--

DROP TABLE IF EXISTS `khuyen_mai`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `khuyen_mai` (
  `makm` int NOT NULL AUTO_INCREMENT,
  `don_toi_thieu` int DEFAULT NULL,
  `gia_tri_giam` int NOT NULL,
  `giam_toi_da` int DEFAULT NULL,
  `loai_giam` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ma_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ngay_bat_dau` datetime(6) NOT NULL,
  `ngay_ket_thuc` datetime(6) NOT NULL,
  `so_luong_da_dung` int NOT NULL,
  `so_luong_toi_da` int DEFAULT NULL,
  `tenkm` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `trang_thai` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`makm`),
  UNIQUE KEY `UKq8resr8h2u6wfhtgxq4itxssf` (`ma_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `khuyen_mai`
--

LOCK TABLES `khuyen_mai` WRITE;
/*!40000 ALTER TABLE `khuyen_mai` DISABLE KEYS */;
/*!40000 ALTER TABLE `khuyen_mai` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nha_cung_cap`
--

DROP TABLE IF EXISTS `nha_cung_cap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nha_cung_cap` (
  `mancc` int NOT NULL AUTO_INCREMENT,
  `dia_chi` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ma_so_thue` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `so_dien_thoai` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tenncc` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `trang_thai` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`mancc`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nha_cung_cap`
--

LOCK TABLES `nha_cung_cap` WRITE;
/*!40000 ALTER TABLE `nha_cung_cap` DISABLE KEYS */;
INSERT INTO `nha_cung_cap` VALUES (1,'TP.HCM','contact@phuongnam.com',NULL,'0283833333','Công ty Sách Phương Nam','HoatDong');
/*!40000 ALTER TABLE `nha_cung_cap` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nha_xuat_ban`
--

DROP TABLE IF EXISTS `nha_xuat_ban`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nha_xuat_ban` (
  `manxb` int NOT NULL AUTO_INCREMENT,
  `dia_chi` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `so_dien_thoai` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tennxb` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`manxb`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nha_xuat_ban`
--

LOCK TABLES `nha_xuat_ban` WRITE;
/*!40000 ALTER TABLE `nha_xuat_ban` DISABLE KEYS */;
INSERT INTO `nha_xuat_ban` VALUES (1,'Hà Nội','nxbhnv@gmail.com','0243822222','Nhà Xuất Bản Hội Nhà Văn');
/*!40000 ALTER TABLE `nha_xuat_ban` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nhan_vien`
--

DROP TABLE IF EXISTS `nhan_vien`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nhan_vien` (
  `manv` int NOT NULL AUTO_INCREMENT,
  `chuc_vu` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `dia_chi` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ho_ten` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ngay_vao_lam` date DEFAULT NULL,
  `so_dien_thoai` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `trang_thai` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ma_tai_khoan` int DEFAULT NULL,
  PRIMARY KEY (`manv`),
  UNIQUE KEY `UKetdcxme7ynbys36hi28u9tk4e` (`so_dien_thoai`),
  UNIQUE KEY `UKs931jur9i7px0jt36iev3osli` (`ma_tai_khoan`),
  CONSTRAINT `FKdpk3u6xuawsiksnkklx1pfeyw` FOREIGN KEY (`ma_tai_khoan`) REFERENCES `tai_khoan` (`ma_tai_khoan`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nhan_vien`
--

LOCK TABLES `nhan_vien` WRITE;
/*!40000 ALTER TABLE `nhan_vien` DISABLE KEYS */;
/*!40000 ALTER TABLE `nhan_vien` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phieu_kiem_ke`
--

DROP TABLE IF EXISTS `phieu_kiem_ke`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `phieu_kiem_ke` (
  `ma_phieu_kiem_ke` int NOT NULL AUTO_INCREMENT,
  `ghi_chu` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ngay_kiem_ke` datetime(6) NOT NULL,
  `ngay_phe_duyet` datetime(6) DEFAULT NULL,
  `trang_thai` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `manv` int NOT NULL,
  `manvphe_duyet` int DEFAULT NULL,
  PRIMARY KEY (`ma_phieu_kiem_ke`),
  KEY `FKivhhrnwvje1osre9o5o97ihxj` (`manv`),
  KEY `FKmaqnbjq6sj13w1q9ttu5pby52` (`manvphe_duyet`),
  CONSTRAINT `FKivhhrnwvje1osre9o5o97ihxj` FOREIGN KEY (`manv`) REFERENCES `nhan_vien` (`manv`),
  CONSTRAINT `FKmaqnbjq6sj13w1q9ttu5pby52` FOREIGN KEY (`manvphe_duyet`) REFERENCES `nhan_vien` (`manv`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phieu_kiem_ke`
--

LOCK TABLES `phieu_kiem_ke` WRITE;
/*!40000 ALTER TABLE `phieu_kiem_ke` DISABLE KEYS */;
/*!40000 ALTER TABLE `phieu_kiem_ke` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phieu_nhap`
--

DROP TABLE IF EXISTS `phieu_nhap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `phieu_nhap` (
  `mapn` int NOT NULL AUTO_INCREMENT,
  `ghi_chu` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ngay_nhap` datetime(6) NOT NULL,
  `tong_tien` int NOT NULL,
  `trang_thai` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mancc` int NOT NULL,
  `manv` int NOT NULL,
  PRIMARY KEY (`mapn`),
  KEY `FKbw2u0ber2va865kg948efhlm5` (`mancc`),
  KEY `FKlavt7ihi8dug436gh1vq69w1y` (`manv`),
  CONSTRAINT `FKbw2u0ber2va865kg948efhlm5` FOREIGN KEY (`mancc`) REFERENCES `nha_cung_cap` (`mancc`),
  CONSTRAINT `FKlavt7ihi8dug436gh1vq69w1y` FOREIGN KEY (`manv`) REFERENCES `nhan_vien` (`manv`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phieu_nhap`
--

LOCK TABLES `phieu_nhap` WRITE;
/*!40000 ALTER TABLE `phieu_nhap` DISABLE KEYS */;
/*!40000 ALTER TABLE `phieu_nhap` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phieu_tra_hang`
--

DROP TABLE IF EXISTS `phieu_tra_hang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `phieu_tra_hang` (
  `ma_phieu_tra` int NOT NULL AUTO_INCREMENT,
  `ghi_chu` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ly_do` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ngay_tra` datetime(6) NOT NULL,
  `trang_thai` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mancc` int NOT NULL,
  `manv` int NOT NULL,
  PRIMARY KEY (`ma_phieu_tra`),
  KEY `FK4l2i3xyg39ixq625uf8pght9o` (`mancc`),
  KEY `FKjat51cayratbgicvq12d7r800` (`manv`),
  CONSTRAINT `FK4l2i3xyg39ixq625uf8pght9o` FOREIGN KEY (`mancc`) REFERENCES `nha_cung_cap` (`mancc`),
  CONSTRAINT `FKjat51cayratbgicvq12d7r800` FOREIGN KEY (`manv`) REFERENCES `nhan_vien` (`manv`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phieu_tra_hang`
--

LOCK TABLES `phieu_tra_hang` WRITE;
/*!40000 ALTER TABLE `phieu_tra_hang` DISABLE KEYS */;
/*!40000 ALTER TABLE `phieu_tra_hang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `san_pham`
--

DROP TABLE IF EXISTS `san_pham`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `san_pham` (
  `masp` int NOT NULL AUTO_INCREMENT,
  `isbn` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gia_ban` int NOT NULL,
  `loaisp` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mo_ta` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `muc_ton_toi_thieu` int NOT NULL,
  `ngay_tao` datetime(6) NOT NULL,
  `so_luong_ton` int NOT NULL,
  `tensp` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `trang_thai` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ma_danh_muc` int NOT NULL,
  `mancc` int DEFAULT NULL,
  `manxb` int DEFAULT NULL,
  PRIMARY KEY (`masp`),
  UNIQUE KEY `UK400ysr2y7n5m2ehwyqnxb741v` (`isbn`),
  KEY `FKqss6n6gtx6lhb7flcka9un18t` (`ma_danh_muc`),
  KEY `FKhp2k7qqhwp3hb66f900uc5gg1` (`mancc`),
  KEY `FKa0fvb13fnkejnd3vtmyicq5x7` (`manxb`),
  CONSTRAINT `FKa0fvb13fnkejnd3vtmyicq5x7` FOREIGN KEY (`manxb`) REFERENCES `nha_xuat_ban` (`manxb`),
  CONSTRAINT `FKhp2k7qqhwp3hb66f900uc5gg1` FOREIGN KEY (`mancc`) REFERENCES `nha_cung_cap` (`mancc`),
  CONSTRAINT `FKqss6n6gtx6lhb7flcka9un18t` FOREIGN KEY (`ma_danh_muc`) REFERENCES `danh_muc` (`ma_danh_muc`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `san_pham`
--

LOCK TABLES `san_pham` WRITE;
/*!40000 ALTER TABLE `san_pham` DISABLE KEYS */;
INSERT INTO `san_pham` VALUES (1,'978-0525559474',189000,'Sach','https://lh3.googleusercontent.com/aida-public/AB6AXuAJxhxEUoGYZXYaOldMaT7d3dcCRrYvCD-3AWG8q4nOBMKeBIhtPi1dKb9NiDZHTXWMxMULBn3rsm5dU8_IVOjwK8Bnkg8xf-IGsBbsQoLtc5D6EWqcU1KfQ1LzI9jMW8JHZBBy9sHF_7t1onS8pagaubUOfOqYifnPkQMRgKeMgWgWQ71XMqmHdC9fJzuMiB3aqbLZt8bE5qj4HVtt62KzVUKztkjut685dS6dOCMbTJ_WPnz-9N7y',5,'2026-09-22 08:25:29.489818',25,'The Midnight Library','DangBan',1,1,1),(2,'978-0735211292',220000,'Sach','https://lh3.googleusercontent.com/aida-public/AB6AXuC0ZepDd_foKXu3i6ER1m8WWEdur0sxuw_PzY9MNfI4REnQ3rf6R_2Cm9EnfFAhXCPxQTTtVl4s2CrHSmtbVz08lHsAHDSiErKboS8Y4p5NQiHrun0Ek56kgIt1soK3hBbriVWEntfW_SHfXzkp7nmY2OyfVyfhAuJ50SpGyqZseN_277pw-RUdZgMOpRkN3ksmBAnBDCRnpGWTd3UWhZiP2GhZTpYd0mZ4A0F2WZ20z_Y2aCKwxl9w',5,'2026-09-22 08:25:29.500201',0,'Atomic Habits - Thay Đổi Tí Hon','HetHang',2,1,1),(3,'978-0857197689',150000,'Sach','https://lh3.googleusercontent.com/aida-public/AB6AXuAUSZ5dqoRMUTssXIap1lFtbGqNj76EO4_JpeLICxgK6i8RQ-b6FBevsmTpBNYLqOGOhEJmM72f7BeRd_P6wYVJ0WrTJJCFwjmrX7y4IYbT9cGZhUYpfZ41NtYpK8wDRUwUU5JolPlF4JYof0nf-Mnjr2hBZFwxagtPhNGSVfaR65hlzsL-oDdP7Q53Ww7C4vzv_B90IFfI-_FjyHy7V4wP5GHZfTpmShIiBp4oEL19xwu0tRaTGEgW',5,'2026-09-22 08:25:29.507098',3,'Tâm Lý Học Về Tiền','DangBan',3,1,1),(4,'978-0062316097',265000,'Sach','https://lh3.googleusercontent.com/aida-public/AB6AXuCG70bn_Qqg9AKCp8J9XwZoQ0H8otME2a1VxaIQ9mbLAQk60DgJssT5k7D_fsGf6ttO0j-TROb1NzE4QR1_sbRLD856RV8UIFgynUd7SujhVFdhLBJkAPHxPynAmPFKFoKuLSY2tyJX9T_1SDen13Mk4MN2urDBWhtknhDsTK8-O2oLxA8yjhXNZy0YUWK2U5gHXvRhAIj2d-YivtCOD_hFsb8Qu8Z00zBpmbfcRC4at7Dd35UbsRB0',5,'2026-09-22 08:25:29.512098',38,'Sapiens: Lược Sử Loài Người','DangBan',4,1,1),(5,'978-6047781234',108000,'Sach','https://lh3.googleusercontent.com/aida-public/AB6AXuAJxhxEUoGYZXYaOldMaT7d3dcCRrYvCD-3AWG8q4nOBMKeBIhtPi1dKb9NiDZHTXWMxMULBn3rsm5dU8_IVOjwK8Bnkg8xf-IGsBbsQoLtc5D6EWqcU1KfQ1LzI9jMW8JHZBBy9sHF_7t1onS8pagaubUOfOqYifnPkQMRgKeMgWgWQ71XMqmHdC9fJzuMiB3aqbLZt8bE5qj4HVtt62KzVUKztkjut685dS6dOCMbTJ_WPnz-9N7y',5,'2026-09-22 08:25:29.516615',45,'Cây Cam Ngọt Của Tôi','DangBan',1,1,1),(6,'978-0441013593',245000,'Sach','https://lh3.googleusercontent.com/aida-public/AB6AXuCG70bn_Qqg9AKCp8J9XwZoQ0H8otME2a1VxaIQ9mbLAQk60DgJssT5k7D_fsGf6ttO0j-TROb1NzE4QR1_sbRLD856RV8UIFgynUd7SujhVFdhLBJkAPHxPynAmPFKFoKuLSY2tyJX9T_1SDen13Mk4MN2urDBWhtknhDsTK8-O2oLxA8yjhXNZy0YUWK2U5gHXvRhAIj2d-YivtCOD_hFsb8Qu8Z00zBpmbfcRC4at7Dd35UbsRB0',5,'2026-09-22 08:25:29.521417',18,'Dune - Xứ Cát','DangBan',5,1,1);
/*!40000 ALTER TABLE `san_pham` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `san_pham_tac_gia`
--

DROP TABLE IF EXISTS `san_pham_tac_gia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `san_pham_tac_gia` (
  `masp` int NOT NULL,
  `ma_tac_gia` int NOT NULL,
  `thu_tu_tac_gia` int DEFAULT NULL,
  PRIMARY KEY (`masp`,`ma_tac_gia`),
  KEY `FKd0vf47jmbbc5xd458vmmt4ly4` (`ma_tac_gia`),
  CONSTRAINT `FKd0vf47jmbbc5xd458vmmt4ly4` FOREIGN KEY (`ma_tac_gia`) REFERENCES `tac_gia` (`ma_tac_gia`),
  CONSTRAINT `FKrn0c9qwvmpesdr9s0akt6lame` FOREIGN KEY (`masp`) REFERENCES `san_pham` (`masp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `san_pham_tac_gia`
--

LOCK TABLES `san_pham_tac_gia` WRITE;
/*!40000 ALTER TABLE `san_pham_tac_gia` DISABLE KEYS */;
/*!40000 ALTER TABLE `san_pham_tac_gia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tac_gia`
--

DROP TABLE IF EXISTS `tac_gia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tac_gia` (
  `ma_tac_gia` int NOT NULL AUTO_INCREMENT,
  `mo_ta` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ten_tac_gia` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`ma_tac_gia`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tac_gia`
--

LOCK TABLES `tac_gia` WRITE;
/*!40000 ALTER TABLE `tac_gia` DISABLE KEYS */;
/*!40000 ALTER TABLE `tac_gia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tai_khoan`
--

DROP TABLE IF EXISTS `tai_khoan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tai_khoan` (
  `ma_tai_khoan` int NOT NULL AUTO_INCREMENT,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mat_khau_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ngay_tao` datetime(6) NOT NULL,
  `ten_dang_nhap` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `trang_thai` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `vai_tro` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`ma_tai_khoan`),
  UNIQUE KEY `UKd0golrlr34gkql6so1i4gbuw5` (`email`),
  UNIQUE KEY `UKgkh4qh51gkiu8ccu1ybn1q7h7` (`ten_dang_nhap`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tai_khoan`
--

LOCK TABLES `tai_khoan` WRITE;
/*!40000 ALTER TABLE `tai_khoan` DISABLE KEYS */;
INSERT INTO `tai_khoan` VALUES (1,'khachhang@gmail.com','$2a$10$9gVsiLuhFceUREbeomrU1Of7g3mnarX0UrleT0/IFClBAiZBT8VIG','2026-09-21 17:59:56.441901','khachhang','HoatDong','KHACHHANG'),(2,'banhang@gmail.com','$2a$10$9gVsiLuhFceUREbeomrU1Of7g3mnarX0UrleT0/IFClBAiZBT8VIG','2026-09-21 17:59:56.492296','banhang','HoatDong','NHANVIENBANHANG'),(3,'thukho@gmail.com','$2a$10$9gVsiLuhFceUREbeomrU1Of7g3mnarX0UrleT0/IFClBAiZBT8VIG','2026-09-21 17:59:56.496732','thukho','HoatDong','NHANVIENKHO'),(4,'admin@gmail.com','$2a$10$9gVsiLuhFceUREbeomrU1Of7g3mnarX0UrleT0/IFClBAiZBT8VIG','2026-09-21 17:59:56.500059','admin','HoatDong','ADMIN'),(5,'quanly@gmail.com','$2a$10$9gVsiLuhFceUREbeomrU1Of7g3mnarX0UrleT0/IFClBAiZBT8VIG','2026-09-21 17:59:56.503747','quanly','HoatDong','QUANLY'),(6,'caotuong14@gmail.com','$2a$10$W86IHrxo0gXlDNDJOm/YjOET38VRwVk2FMv.a0WvL7RjYo9Rr5HFq','2026-09-21 18:03:58.023998','caotuong','HoatDong','KHACHHANG');
/*!40000 ALTER TABLE `tai_khoan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `thanh_toan`
--

DROP TABLE IF EXISTS `thanh_toan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `thanh_toan` (
  `ma_thanh_toan` int NOT NULL AUTO_INCREMENT,
  `ma_giao_dich` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ngay_thanh_toan` datetime(6) DEFAULT NULL,
  `noi_dung` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phuong_thuc` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `so_tien` int NOT NULL,
  `trang_thai` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `madh` int NOT NULL,
  PRIMARY KEY (`ma_thanh_toan`),
  KEY `FKfo8a50ev7l24cccqm3v0hfwbv` (`madh`),
  CONSTRAINT `FKfo8a50ev7l24cccqm3v0hfwbv` FOREIGN KEY (`madh`) REFERENCES `don_hang` (`madh`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `thanh_toan`
--

LOCK TABLES `thanh_toan` WRITE;
/*!40000 ALTER TABLE `thanh_toan` DISABLE KEYS */;
/*!40000 ALTER TABLE `thanh_toan` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-22 23:26:25

-- Kho
CREATE TABLE IF NOT EXISTS khos (
    ma_kho BIGINT PRIMARY KEY AUTO_INCREMENT,
    ten_kho VARCHAR(100) NOT NULL,
    dia_chi VARCHAR(255),
    email VARCHAR(100),
    sdt VARCHAR(20),
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Kho Hang (Tồn kho)
CREATE TABLE IF NOT EXISTS khohang (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sach_id INT NOT NULL,
    kho_id BIGINT NOT NULL,
    so_luong_ton INT DEFAULT 0,
    muc_toi_thieu INT DEFAULT 10,
    muc_toi_da INT DEFAULT 1000,
    ngay_cap_nhat DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_sach_kho (sach_id, kho_id),
    FOREIGN KEY (sach_id) REFERENCES san_phams(ma_sp) ON DELETE CASCADE,
    FOREIGN KEY (kho_id) REFERENCES khos(ma_kho) ON DELETE CASCADE,
    INDEX idx_khohang_kho (kho_id),
    INDEX idx_khohang_ton (so_luong_ton)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Phieu Nhap
CREATE TABLE IF NOT EXISTS phieunhap (
    ma_phieu_nhap BIGINT PRIMARY KEY AUTO_INCREMENT,
    kho_id BIGINT NOT NULL,
    nha_cung_cap_id INT NOT NULL,
    ngay_nhap DATETIME DEFAULT CURRENT_TIMESTAMP,
    trang_thai VARCHAR(20) DEFAULT 'DRAFT',
    ghi_chu VARCHAR(500),
    FOREIGN KEY (kho_id) REFERENCES khos(ma_kho) ON DELETE CASCADE,
    FOREIGN KEY (nha_cung_cap_id) REFERENCES nha_cung_caps(ma_ncc) ON DELETE RESTRICT,
    INDEX idx_phieunhap_kho (kho_id),
    INDEX idx_phieunhap_status (trang_thai)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Chi tiet Phieu Nhap
CREATE TABLE IF NOT EXISTS phieunhapct (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phieu_nhap_id BIGINT NOT NULL,
    sach_id INT NOT NULL,
    so_luong INT NOT NULL,
    gia_nhap DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (phieu_nhap_id) REFERENCES phieunhap(ma_phieu_nhap) ON DELETE CASCADE,
    FOREIGN KEY (sach_id) REFERENCES san_phams(ma_sp) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Phieu Ke
CREATE TABLE IF NOT EXISTS phieuke (
    ma_phieu_ke BIGINT PRIMARY KEY AUTO_INCREMENT,
    kho_id BIGINT NOT NULL,
    ngay_ke DATETIME DEFAULT CURRENT_TIMESTAMP,
    trang_thai VARCHAR(20) DEFAULT 'DRAFT',
    ghi_chu VARCHAR(500),
    FOREIGN KEY (kho_id) REFERENCES khos(ma_kho) ON DELETE CASCADE,
    INDEX idx_phieuke_kho (kho_id),
    INDEX idx_phieuke_status (trang_thai)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Chi tiet Phieu Ke
CREATE TABLE IF NOT EXISTS phieukect (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phieu_ke_id BIGINT NOT NULL,
    sach_id INT NOT NULL,
    so_luong_ly_thuyet INT NOT NULL,
    so_luong_thuc_te INT NOT NULL,
    chenh_lech INT GENERATED ALWAYS AS (so_luong_ly_thuyet - so_luong_thuc_te) STORED,
    FOREIGN KEY (phieu_ke_id) REFERENCES phieuke(ma_phieu_ke) ON DELETE CASCADE,
    FOREIGN KEY (sach_id) REFERENCES san_phams(ma_sp) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
