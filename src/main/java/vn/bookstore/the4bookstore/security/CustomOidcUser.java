package vn.bookstore.the4bookstore.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import vn.bookstore.the4bookstore.entity.TaiKhoan;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CustomOidcUser extends DefaultOidcUser {

    private final TaiKhoan taiKhoan;

    public CustomOidcUser(TaiKhoan taiKhoan, Collection<? extends GrantedAuthority> authorities,
                          OidcIdToken idToken, OidcUserInfo userInfo, String nameAttributeKey) {
        super(combineAuthorities(taiKhoan, authorities),
              idToken, userInfo,
              (nameAttributeKey != null && idToken != null && idToken.getClaims().containsKey(nameAttributeKey)) ? nameAttributeKey
                      : (userInfo != null && nameAttributeKey != null && userInfo.getClaims().containsKey(nameAttributeKey) ? nameAttributeKey : IdTokenClaimNames.SUB));
        this.taiKhoan = taiKhoan;
    }

    private static Collection<? extends GrantedAuthority> combineAuthorities(TaiKhoan taiKhoan, Collection<? extends GrantedAuthority> authorities) {
        Set<GrantedAuthority> combined = new HashSet<>();
        if (authorities != null) {
            combined.addAll(authorities);
        }
        String role = (taiKhoan != null && taiKhoan.getVaiTro() != null && !taiKhoan.getVaiTro().isBlank())
                ? taiKhoan.getVaiTro().toUpperCase()
                : "KHACHHANG";
        combined.add(new SimpleGrantedAuthority("ROLE_" + role));
        return combined;
    }

    @Override
    public String getName() {
        Object name = getAttributes().get("name");
        if (name != null && !name.toString().isBlank() && !name.toString().matches("\\d+")) {
            return name.toString();
        }
        Object given = getAttributes().get("given_name");
        Object family = getAttributes().get("family_name");
        if (given != null || family != null) {
            String full = ((family != null ? family.toString() + " " : "") + (given != null ? given.toString() : "")).trim();
            if (!full.isBlank() && !full.matches("\\d+")) {
                return full;
            }
        }
        if (taiKhoan != null && taiKhoan.getTenDangNhap() != null && !taiKhoan.getTenDangNhap().matches("\\d+")) {
            return taiKhoan.getTenDangNhap();
        }
        return getEmail();
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    @Override
    public String getPicture() {
        Object pic = getAttributes().get("picture");
        if (pic != null && !pic.toString().isBlank()) {
            return pic.toString();
        }
        Object avt = getAttributes().get("avatar_url");
        if (avt != null && !avt.toString().isBlank()) {
            return avt.toString();
        }
        return super.getPicture();
    }
}
