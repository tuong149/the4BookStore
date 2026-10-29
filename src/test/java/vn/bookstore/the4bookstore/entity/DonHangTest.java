package vn.bookstore.the4bookstore.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DonHangTest {

    @Test
    @DisplayName("isKhachHangBiKhoa returns false when khachHang is null")
    void testKhachHangNull() {
        DonHang dh = new DonHang();
        assertFalse(dh.isKhachHangBiKhoa());
    }

    @Test
    @DisplayName("isKhachHangBiKhoa returns false when taiKhoan is null")
    void testTaiKhoanNull() {
        DonHang dh = new DonHang();
        KhachHang kh = new KhachHang();
        dh.setKhachHang(kh);
        assertFalse(dh.isKhachHangBiKhoa());
    }

    @Test
    @DisplayName("isKhachHangBiKhoa returns false when account is active (HoatDong)")
    void testTaiKhoanActive() {
        DonHang dh = new DonHang();
        KhachHang kh = new KhachHang();
        TaiKhoan tk = new TaiKhoan();
        tk.setTrangThai("HoatDong");
        kh.setTaiKhoan(tk);
        dh.setKhachHang(kh);

        assertFalse(dh.isKhachHangBiKhoa());
    }

    @Test
    @DisplayName("isKhachHangBiKhoa returns true when account is locked (BiKhoa)")
    void testTaiKhoanBiKhoa() {
        DonHang dh = new DonHang();
        KhachHang kh = new KhachHang();
        TaiKhoan tk = new TaiKhoan();
        tk.setTenDangNhap("bad_user");
        tk.setTrangThai("BiKhoa");
        kh.setTaiKhoan(tk);
        dh.setKhachHang(kh);

        assertTrue(dh.isKhachHangBiKhoa());
        assertEquals("bad_user", dh.getTenDangNhapKhachHang());
    }

    @Test
    @DisplayName("isKhachHangBiKhoa is case-insensitive")
    void testTaiKhoanBiKhoaCaseInsensitive() {
        DonHang dh = new DonHang();
        KhachHang kh = new KhachHang();
        TaiKhoan tk = new TaiKhoan();
        tk.setTrangThai("bikhoa");
        kh.setTaiKhoan(tk);
        dh.setKhachHang(kh);

        assertTrue(dh.isKhachHangBiKhoa());
    }

    @Test
    @DisplayName("isConHanTraHang returns true for DaGiao orders")
    void testConHanTraHangDaGiao() {
        DonHang dh = new DonHang();
        dh.setTrangThai("DaGiao");
        dh.setNgayGiaoHang(LocalDateTime.now().minusHours(10));
        assertTrue(dh.isConHanTraHang());
        assertNotNull(dh.getHanTraHang());
    }

    @Test
    @DisplayName("isConHanTraHang returns true for HoanTat orders within 48 hours")
    void testConHanTraHangHoanTatWithin48h() {
        DonHang dh = new DonHang();
        dh.setTrangThai("HoanTat");
        dh.setNgayHoanThanh(LocalDateTime.now().minusHours(24));
        assertTrue(dh.isConHanTraHang());
        assertNotNull(dh.getHanTraHang());
    }

    @Test
    @DisplayName("isConHanTraHang returns false for HoanTat orders past 48 hours")
    void testConHanTraHangHoanTatPast48h() {
        DonHang dh = new DonHang();
        dh.setTrangThai("HoanTat");
        dh.setNgayHoanThanh(LocalDateTime.now().minusHours(50));
        assertFalse(dh.isConHanTraHang());
        assertNotNull(dh.getHanTraHang());
    }

    @Test
    @DisplayName("isConHanTraHang returns false for other statuses like ChoXuLy, DaHuy")
    void testConHanTraHangOtherStatuses() {
        DonHang dh = new DonHang();
        dh.setTrangThai("ChoXuLy");
        assertFalse(dh.isConHanTraHang());

        dh.setTrangThai("DaHuy");
        assertFalse(dh.isConHanTraHang());
    }
}
