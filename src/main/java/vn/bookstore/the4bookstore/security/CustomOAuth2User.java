package vn.bookstore.the4bookstore.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import vn.bookstore.the4bookstore.entity.TaiKhoan;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final TaiKhoan taiKhoan;
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;

    public CustomOAuth2User(TaiKhoan taiKhoan, Map<String, Object> attributes, String nameAttributeKey) {
        this.taiKhoan = taiKhoan;
        this.attributes = attributes;
        this.nameAttributeKey = (nameAttributeKey != null && attributes != null && attributes.containsKey(nameAttributeKey)) ? nameAttributeKey : "sub";
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = (taiKhoan != null && taiKhoan.getVaiTro() != null) ? taiKhoan.getVaiTro().toUpperCase() : "KHACHHANG";
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getName() {
        if (attributes != null) {
            Object name = attributes.get("name");
            if (name != null && !name.toString().isBlank() && !name.toString().matches("\\d+")) {
                return name.toString();
            }
            Object givenName = attributes.get("given_name");
            Object familyName = attributes.get("family_name");
            if (givenName != null || familyName != null) {
                String full = ((familyName != null ? familyName + " " : "") + (givenName != null ? givenName : "")).trim();
                if (!full.isBlank() && !full.matches("\\d+")) {
                    return full;
                }
            }
        }
        if (taiKhoan != null && taiKhoan.getTenDangNhap() != null && !taiKhoan.getTenDangNhap().matches("\\d+")) {
            return taiKhoan.getTenDangNhap();
        }
        return getEmail();
    }

    public String getEmail() {
        if (attributes != null && attributes.containsKey("email") && attributes.get("email") != null) {
            return attributes.get("email").toString();
        }
        return taiKhoan != null ? taiKhoan.getEmail() : "";
    }

    public String getPicture() {
        if (attributes != null) {
            if (attributes.get("picture") != null) {
                String pic = attributes.get("picture").toString();
                if (!pic.isBlank()) {
                    return pic;
                }
            }
            if (attributes.get("avatar_url") != null) {
                String pic = attributes.get("avatar_url").toString();
                if (!pic.isBlank()) {
                    return pic;
                }
            }
        }
        return null;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }
}
