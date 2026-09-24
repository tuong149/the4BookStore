package vn.bookstore.the4bookstore.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vn.bookstore.the4bookstore.entity.KhachHang;
import vn.bookstore.the4bookstore.entity.TaiKhoan;
import vn.bookstore.the4bookstore.repository.KhachHangRepository;
import vn.bookstore.the4bookstore.repository.TaiKhoanRepository;
import vn.bookstore.the4bookstore.security.CustomOAuth2User;
import vn.bookstore.the4bookstore.security.CustomOidcUser;
import vn.bookstore.the4bookstore.security.CustomUserDetails;

import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final KhachHangRepository khachHangRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    public GlobalControllerAdvice(KhachHangRepository khachHangRepository,
                                  TaiKhoanRepository taiKhoanRepository) {
        this.khachHangRepository = khachHangRepository;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    private String getCustomAvatar(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomOAuth2User oAuth2User) {
            String picture = oAuth2User.getPicture();
            if (picture != null && !picture.isBlank() && !picture.contains("manager-avatar.png")) {
                return picture;
            }
            if (oAuth2User.getTaiKhoan() != null) {
                Optional<KhachHang> khOpt = khachHangRepository.findByTaiKhoan(oAuth2User.getTaiKhoan());
                if (khOpt.isPresent() && khOpt.get().getAnhDaiDien() != null && !khOpt.get().getAnhDaiDien().isBlank() && !khOpt.get().getAnhDaiDien().contains("manager-avatar.png")) {
                    return khOpt.get().getAnhDaiDien();
                }
            }
        }

        if (principal instanceof CustomOidcUser oidcUser) {
            String picture = oidcUser.getPicture();
            if (picture != null && !picture.isBlank() && !picture.contains("manager-avatar.png")) {
                return picture;
            }
            if (oidcUser.getTaiKhoan() != null) {
                Optional<KhachHang> khOpt = khachHangRepository.findByTaiKhoan(oidcUser.getTaiKhoan());
                if (khOpt.isPresent() && khOpt.get().getAnhDaiDien() != null && !khOpt.get().getAnhDaiDien().isBlank() && !khOpt.get().getAnhDaiDien().contains("manager-avatar.png")) {
                    return khOpt.get().getAnhDaiDien();
                }
            }
        }

        if (principal instanceof OAuth2User oauth2User) {
            Object pic = oauth2User.getAttribute("picture");
            if (pic == null) {
                pic = oauth2User.getAttribute("avatar_url");
            }
            if (pic != null && !pic.toString().isBlank() && !pic.toString().contains("manager-avatar.png")) {
                return pic.toString();
            }
            Object emailObj = oauth2User.getAttribute("email");
            if (emailObj != null) {
                Optional<TaiKhoan> tkOpt = taiKhoanRepository.findByEmail(emailObj.toString());
                if (tkOpt.isPresent()) {
                    Optional<KhachHang> khOpt = khachHangRepository.findByTaiKhoan(tkOpt.get());
                    if (khOpt.isPresent() && khOpt.get().getAnhDaiDien() != null && !khOpt.get().getAnhDaiDien().isBlank() && !khOpt.get().getAnhDaiDien().contains("manager-avatar.png")) {
                        return khOpt.get().getAnhDaiDien();
                    }
                }
            }
        }

        if (principal instanceof CustomUserDetails userDetails) {
            Optional<KhachHang> khOpt = khachHangRepository.findByTaiKhoan(userDetails.getTaiKhoan());
            if (khOpt.isPresent() && khOpt.get().getAnhDaiDien() != null && !khOpt.get().getAnhDaiDien().isBlank() && !khOpt.get().getAnhDaiDien().contains("manager-avatar.png")) {
                return khOpt.get().getAnhDaiDien();
            }
        }

        return null;
    }

    @ModelAttribute("userAvatar")
    public String getUserAvatar(Authentication authentication) {
        String customAvatar = getCustomAvatar(authentication);
        if (customAvatar != null) {
            return customAvatar;
        }
        String displayName = getUserDisplayName(authentication);
        return vn.bookstore.the4bookstore.util.AvatarUtils.generateInitialAvatarSvg(displayName);
    }

    @ModelAttribute("userInitial")
    public String getUserInitial(Authentication authentication) {
        String displayName = getUserDisplayName(authentication);
        return vn.bookstore.the4bookstore.util.AvatarUtils.extractInitial(displayName);
    }

    @ModelAttribute("userHasCustomAvatar")
    public boolean getUserHasCustomAvatar(Authentication authentication) {
        String customAvatar = getCustomAvatar(authentication);
        return vn.bookstore.the4bookstore.util.AvatarUtils.hasCustomAvatar(customAvatar);
    }

    @ModelAttribute("userDisplayName")
    public String getUserDisplayName(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return "User";
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomOAuth2User oAuth2User) {
            if (oAuth2User.getTaiKhoan() != null) {
                Optional<KhachHang> khOpt = khachHangRepository.findByTaiKhoan(oAuth2User.getTaiKhoan());
                if (khOpt.isPresent() && khOpt.get().getHoTen() != null && !khOpt.get().getHoTen().isBlank() && !khOpt.get().getHoTen().matches("\\d+")) {
                    return khOpt.get().getHoTen();
                }
            }
            String name = oAuth2User.getName();
            if (name != null && !name.isBlank() && !name.matches("\\d+")) {
                return name;
            }
        }

        if (principal instanceof CustomOidcUser oidcUser) {
            if (oidcUser.getTaiKhoan() != null) {
                Optional<KhachHang> khOpt = khachHangRepository.findByTaiKhoan(oidcUser.getTaiKhoan());
                if (khOpt.isPresent() && khOpt.get().getHoTen() != null && !khOpt.get().getHoTen().isBlank() && !khOpt.get().getHoTen().matches("\\d+")) {
                    return khOpt.get().getHoTen();
                }
            }
            String name = oidcUser.getName();
            if (name != null && !name.isBlank() && !name.matches("\\d+")) {
                return name;
            }
        }

        if (principal instanceof OAuth2User oauth2User) {
            Object name = oauth2User.getAttribute("name");
            if (name != null && !name.toString().isBlank() && !name.toString().matches("\\d+")) {
                return name.toString();
            }
            Object given = oauth2User.getAttribute("given_name");
            Object family = oauth2User.getAttribute("family_name");
            if (given != null || family != null) {
                String full = ((family != null ? family.toString() + " " : "") + (given != null ? given.toString() : "")).trim();
                if (!full.isBlank() && !full.matches("\\d+")) {
                    return full;
                }
            }
            Object emailObj = oauth2User.getAttribute("email");
            if (emailObj != null) {
                Optional<TaiKhoan> tkOpt = taiKhoanRepository.findByEmail(emailObj.toString());
                if (tkOpt.isPresent()) {
                    Optional<KhachHang> khOpt = khachHangRepository.findByTaiKhoan(tkOpt.get());
                    if (khOpt.isPresent() && khOpt.get().getHoTen() != null && !khOpt.get().getHoTen().isBlank() && !khOpt.get().getHoTen().matches("\\d+")) {
                        return khOpt.get().getHoTen();
                    }
                }
                return emailObj.toString().split("@")[0];
            }
        }

        if (principal instanceof CustomUserDetails userDetails) {
            Optional<KhachHang> khOpt = khachHangRepository.findByTaiKhoan(userDetails.getTaiKhoan());
            if (khOpt.isPresent() && khOpt.get().getHoTen() != null && !khOpt.get().getHoTen().isBlank() && !khOpt.get().getHoTen().matches("\\d+")) {
                return khOpt.get().getHoTen();
            }
            if (userDetails.getUsername() != null && !userDetails.getUsername().matches("\\d+")) {
                return userDetails.getUsername();
            }
            if (userDetails.getTaiKhoan() != null && userDetails.getTaiKhoan().getEmail() != null) {
                return userDetails.getTaiKhoan().getEmail().split("@")[0];
            }
        }

        String authName = authentication.getName();
        return (authName != null && !authName.matches("\\d+") && !authName.isBlank()) ? authName : "User";
    }
}
