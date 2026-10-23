package vn.bookstore.the4bookstore.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
}
