package vn.bookstore.the4bookstore.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.bookstore.the4bookstore.entity.TaiKhoan;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final TaiKhoan taiKhoan;

    public CustomUserDetails(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + taiKhoan.getVaiTro().toUpperCase()));
    }

    @Override
    public String getPassword() {
        return taiKhoan.getMatKhauHash();
    }

    @Override
    public String getUsername() {
        return taiKhoan.getTenDangNhap();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return "HoatDong".equals(taiKhoan.getTrangThai());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "HoatDong".equals(taiKhoan.getTrangThai());
    }
    
    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public String getPicture() {
        return null;
    }
}