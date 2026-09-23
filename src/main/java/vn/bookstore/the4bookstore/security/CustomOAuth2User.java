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
        this.nameAttributeKey = nameAttributeKey != null ? nameAttributeKey : "sub";
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = (taiKhoan.getVaiTro() != null) ? taiKhoan.getVaiTro().toUpperCase() : "KHACHHANG";
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getName() {
        if (attributes.containsKey("name") && attributes.get("name") != null) {
            return (String) attributes.get("name");
        }
        return taiKhoan.getEmail();
    }

    public String getEmail() {
        if (attributes.containsKey("email") && attributes.get("email") != null) {
            return (String) attributes.get("email");
        }
        return taiKhoan.getEmail();
    }

    public String getPicture() {
        if (attributes.containsKey("picture") && attributes.get("picture") != null) {
            return (String) attributes.get("picture");
        }
        return null;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }
}
