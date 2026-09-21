package vn.bookstore.the4bookstore.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String tenDangNhap;
    private String email;
    private String matKhau;
    private String hoTen;
    private String soDienThoai;
    private String diaChi;
}
