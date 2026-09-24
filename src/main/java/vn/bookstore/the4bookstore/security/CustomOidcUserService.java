package vn.bookstore.the4bookstore.security;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.bookstore.the4bookstore.entity.TaiKhoan;

@Service
public class CustomOidcUserService extends OidcUserService {

    private final CustomOAuth2UserService customOAuth2UserService;

    public CustomOidcUserService(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String email = oidcUser.getEmail();
        String sub = oidcUser.getSubject();
        String name = oidcUser.getFullName();
        if (name == null || name.isBlank()) {
            Object n = oidcUser.getAttribute("name");
            if (n != null) {
                name = n.toString();
            }
        }
        if (name == null || name.isBlank()) {
            String givenName = oidcUser.getGivenName();
            String familyName = oidcUser.getFamilyName();
            if (givenName != null || familyName != null) {
                name = ((familyName != null ? familyName + " " : "") + (givenName != null ? givenName : "")).trim();
            }
        }
        String picture = oidcUser.getPicture();
        if (picture == null || picture.isBlank()) {
            Object pic = oidcUser.getAttribute("avatar_url");
            if (pic != null) {
                picture = pic.toString();
            }
        }

        try {
            TaiKhoan taiKhoan = customOAuth2UserService.processOAuthUser(email, sub, name, picture, registrationId);

            String nameAttributeKey = userRequest.getClientRegistration()
                    .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
            if (nameAttributeKey == null || !oidcUser.getAttributes().containsKey(nameAttributeKey)) {
                nameAttributeKey = IdTokenClaimNames.SUB;
            }

            return new CustomOidcUser(taiKhoan, oidcUser.getAuthorities(), oidcUser.getIdToken(), oidcUser.getUserInfo(), nameAttributeKey);
        } catch (OAuth2AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error processing Google OidcUser: " + e.getMessage());
            e.printStackTrace();
            throw new OAuth2AuthenticationException(new OAuth2Error("oidc_processing_failed", "Lỗi xử lý tài khoản Google OIDC: " + e.getMessage(), null), e);
        }
    }
}
