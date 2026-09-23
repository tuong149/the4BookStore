package vn.bookstore.the4bookstore.security;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.bookstore.the4bookstore.entity.KhachHang;
import vn.bookstore.the4bookstore.entity.TaiKhoan;
import vn.bookstore.the4bookstore.repository.KhachHangRepository;
import vn.bookstore.the4bookstore.repository.TaiKhoanRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final KhachHangRepository khachHangRepository;

    public CustomOAuth2UserService(TaiKhoanRepository taiKhoanRepository, KhachHangRepository khachHangRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.khachHangRepository = khachHangRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String email = oAuth2User.getAttribute("email");
        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("missing_email"),
                    "Không thể lấy được địa chỉ Email từ tài khoản Google."
            );
        }

        String sub = oAuth2User.getAttribute("sub");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        TaiKhoan taiKhoan = processOAuthUser(email, sub, name, picture, registrationId);

        return new CustomOAuth2User(taiKhoan, oAuth2User.getAttributes(), "sub");
    }

    private TaiKhoan processOAuthUser(String email, String sub, String name, String picture, String registrationId) {
        Optional<TaiKhoan> optionalTaiKhoan = taiKhoanRepository.findByEmail(email);
        TaiKhoan tk;

        if (optionalTaiKhoan.isPresent()) {
            tk = optionalTaiKhoan.get();
            // Cập nhật thông tin nhà cung cấp OAuth nếu chưa có
            if (tk.getProviderId() == null) {
                tk.setProviderId(sub);
            }
            if (tk.getAuthProvider() == null || "LOCAL".equalsIgnoreCase(tk.getAuthProvider())) {
                tk.setAuthProvider(registrationId.toUpperCase());
            }
            tk = taiKhoanRepository.save(tk);

            // Kiểm tra và đồng bộ thông tin khách hàng
            Optional<KhachHang> optionalKh = khachHangRepository.findByTaiKhoan(tk);
            if (optionalKh.isPresent()) {
                KhachHang kh = optionalKh.get();
                if (picture != null && (kh.getAnhDaiDien() == null || kh.getAnhDaiDien().isBlank())) {
                    kh.setAnhDaiDien(picture);
                    khachHangRepository.save(kh);
                }
            } else {
                KhachHang newKh = new KhachHang();
                newKh.setHoTen(name != null && !name.isBlank() ? name : email);
                newKh.setEmail(email);
                newKh.setAnhDaiDien(picture);
                newKh.setTaiKhoan(tk);
                newKh.setNgayDangKy(LocalDateTime.now());
                khachHangRepository.save(newKh);
            }
        } else {
            // Tạo tài khoản mới cho người dùng OAuth2
            tk = new TaiKhoan();
            
            // Đảm bảo tên đăng nhập là duy nhất
            String baseUsername = email;
            if (taiKhoanRepository.findByTenDangNhap(baseUsername).isPresent()) {
                baseUsername = email.split("@")[0] + "_" + UUID.randomUUID().toString().substring(0, 5);
            }
            tk.setTenDangNhap(baseUsername);
            tk.setEmail(email);
            tk.setMatKhauHash("OAUTH2_" + UUID.randomUUID());
            tk.setVaiTro("KHACHHANG");
            tk.setTrangThai("HoatDong");
            tk.setAuthProvider(registrationId.toUpperCase());
            tk.setProviderId(sub);
            tk.setNgayTao(LocalDateTime.now());
            tk = taiKhoanRepository.save(tk);

            // Tạo hồ sơ khách hàng tương ứng
            KhachHang newKh = new KhachHang();
            newKh.setHoTen(name != null && !name.isBlank() ? name : email);
            newKh.setEmail(email);
            newKh.setAnhDaiDien(picture);
            newKh.setTaiKhoan(tk);
            newKh.setNgayDangKy(LocalDateTime.now());
            khachHangRepository.save(newKh);
        }

        return tk;
    }
}
