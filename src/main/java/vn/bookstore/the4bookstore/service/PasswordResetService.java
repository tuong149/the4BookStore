package vn.bookstore.the4bookstore.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.bookstore.the4bookstore.entity.PasswordResetToken;
import vn.bookstore.the4bookstore.entity.TaiKhoan;
import vn.bookstore.the4bookstore.repository.PasswordResetTokenRepository;
import vn.bookstore.the4bookstore.repository.TaiKhoanRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    private static final SecureRandom secureRandom = new SecureRandom();

    private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public enum ResetStatus {
        SUCCESS,
        EMAIL_NOT_FOUND,
        GOOGLE_ACCOUNT,
        ACCOUNT_LOCKED,
        ERROR
    }

    public record ResetResult(ResetStatus status, String message, String otpCode) {}
    public record VerifyResult(boolean success, String message) {}

    public PasswordResetService(TaiKhoanRepository taiKhoanRepository,
                                PasswordResetTokenRepository tokenRepository,
                                PasswordEncoder passwordEncoder,
                                EmailService emailService) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    /**
     * Sinh và gửi mã OTP 6 chữ số đến email người dùng
     */
    @Transactional
    public ResetResult createPasswordResetOtp(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new ResetResult(ResetStatus.ERROR, "Email không được để trống!", null);
        }

        String cleanEmail = email.trim();
        Optional<TaiKhoan> optionalTaiKhoan = taiKhoanRepository.findByEmail(cleanEmail);

        if (optionalTaiKhoan.isEmpty()) {
            return new ResetResult(ResetStatus.EMAIL_NOT_FOUND, "Không tìm thấy tài khoản liên kết với email này!", null);
        }

        TaiKhoan taiKhoan = optionalTaiKhoan.get();

        // Kiểm tra trạng thái tài khoản
        if (!"HoatDong".equalsIgnoreCase(taiKhoan.getTrangThai())) {
            return new ResetResult(ResetStatus.ACCOUNT_LOCKED, "Tài khoản này hiện đang bị khóa hoặc ngừng hoạt động!", null);
        }

        // Kiểm tra tài khoản Google
        boolean isGoogleAccount = (taiKhoan.getAuthProvider() != null && "GOOGLE".equalsIgnoreCase(taiKhoan.getAuthProvider()))
                || (taiKhoan.getMatKhauHash() != null && taiKhoan.getMatKhauHash().startsWith("OAUTH2_"))
                || (taiKhoan.getProviderId() != null && !taiKhoan.getProviderId().isBlank());
        if (isGoogleAccount) {
            return new ResetResult(ResetStatus.GOOGLE_ACCOUNT, 
                    "Tài khoản của bạn được liên kết và đăng nhập bằng Google. Vui lòng sử dụng tính năng 'Đăng nhập bằng Google' để tiếp tục.", null);
        }

        // Xóa các OTP cũ chưa sử dụng
        List<PasswordResetToken> oldTokens = tokenRepository.findAllByTaiKhoan(taiKhoan);
        if (!oldTokens.isEmpty()) {
            tokenRepository.deleteAll(oldTokens);
        }

        // Sinh mã OTP 6 chữ số ngẫu nhiên an toàn
        String otpCode = String.format("%06d", secureRandom.nextInt(1000000));

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setOtpCode(otpCode);
        resetToken.setToken(otpCode); // Giữ tương thích cột token cũ
        resetToken.setTaiKhoan(taiKhoan);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(5)); // Hiệu lực 5 phút
        resetToken.setUsed(false);
        resetToken.setNgayTao(LocalDateTime.now());
        tokenRepository.save(resetToken);

        // Gửi email OTP
        emailService.sendOtpEmail(cleanEmail, otpCode);

        return new ResetResult(ResetStatus.SUCCESS, 
                "Mã xác thực OTP đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư (hoặc mục Spam).", 
                otpCode);
    }

    /**
     * Xác thực mã OTP và đặt mật khẩu mới
     */
    @Transactional
    public VerifyResult verifyOtpAndResetPassword(String email, String otpCode, String newPassword) {
        if (email == null || email.trim().isEmpty()) {
            return new VerifyResult(false, "Email không hợp lệ!");
        }
        if (otpCode == null || otpCode.trim().length() != 6) {
            return new VerifyResult(false, "Mã OTP phải gồm đúng 6 chữ số!");
        }
        if (newPassword == null || newPassword.length() < 6) {
            return new VerifyResult(false, "Mật khẩu mới phải có tối thiểu 6 ký tự!");
        }

        Optional<TaiKhoan> optionalTaiKhoan = taiKhoanRepository.findByEmail(email.trim());
        if (optionalTaiKhoan.isEmpty()) {
            return new VerifyResult(false, "Không tìm thấy tài khoản tương ứng với email!");
        }

        TaiKhoan taiKhoan = optionalTaiKhoan.get();
        String cleanOtp = otpCode.trim();

        Optional<PasswordResetToken> optToken = tokenRepository
                .findFirstByTaiKhoanAndOtpCodeAndUsedFalseOrderByNgayTaoDesc(taiKhoan, cleanOtp);

        if (optToken.isEmpty()) {
            return new VerifyResult(false, "Mã OTP không chính xác hoặc đã được sử dụng!");
        }

        PasswordResetToken resetToken = optToken.get();
        if (resetToken.isExpired()) {
            return new VerifyResult(false, "Mã OTP đã hết hạn (chỉ có hiệu lực trong 5 phút). Vui lòng bấm 'Gửi lại mã'!");
        }

        // Cập nhật mật khẩu mới
        taiKhoan.setMatKhauHash(passwordEncoder.encode(newPassword));
        if (taiKhoan.getAuthProvider() == null) {
            taiKhoan.setAuthProvider("LOCAL");
        }
        taiKhoanRepository.save(taiKhoan);

        // Vô hiệu hóa OTP
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("[PASSWORD RESET] Đã xác thực OTP thành công và cập nhật mật khẩu mới cho tài khoản: {}", taiKhoan.getTenDangNhap());
        return new VerifyResult(true, "Cập nhật mật khẩu thành công!");
    }

    @Transactional(readOnly = true)
    public boolean hasActiveOtp(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        Optional<TaiKhoan> opt = taiKhoanRepository.findByEmail(email.trim());
        if (opt.isEmpty()) return false;
        return tokenRepository.findFirstByTaiKhoanAndUsedFalseOrderByNgayTaoDesc(opt.get())
                .map(t -> !t.isExpired())
                .orElse(false);
    }
}
