package vn.bookstore.the4bookstore.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vn.bookstore.the4bookstore.security.CustomOAuth2User;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("userAvatar")
    public String getUserAvatar(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomOAuth2User oAuth2User) {
            String picture = oAuth2User.getPicture();
            if (picture != null && !picture.isBlank()) {
                return picture;
            }
        }
        return "/images/manager-avatar.png";
    }
}
