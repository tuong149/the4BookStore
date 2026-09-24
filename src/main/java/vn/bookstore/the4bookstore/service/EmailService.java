package vn.bookstore.the4bookstore.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    public EmailService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Gửi email mã OTP đặt lại mật khẩu với giao diện HTML chuẩn nhận diện The4BookStore.
     * Hỗ trợ tự động fallback in ra Console nếu chưa cấu hình SMTP.
     */
    public boolean sendOtpEmail(String toEmail, String otpCode) {
        if (mailSender == null || mailUsername == null || mailUsername.trim().isEmpty()) {
            log.warn("[EMAIL SERVICE] Chưa cấu hình SMTP hoặc MAIL_USERNAME. Đang kích hoạt chế độ DEV FALLBACK.");
            printDevOtp(toEmail, otpCode);
            return false;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailUsername.trim(), "The4BookStore Support");
            helper.setTo(toEmail.trim());
            helper.setSubject("The4BookStore - Mã OTP đặt lại mật khẩu: " + otpCode);

            String htmlContent = buildOtpEmailHtml(otpCode);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("[EMAIL SERVICE] Đã gửi mã OTP đặt lại mật khẩu thành công tới: {}", toEmail);
            return true;
        } catch (Exception e) {
            log.error("[EMAIL SERVICE] Lỗi khi gửi email qua SMTP: {}", e.getMessage());
            if (e.getMessage() != null && (e.getMessage().contains("535") || e.getMessage().contains("AuthenticationFailedException") || e.getMessage().contains("Username and Password not accepted"))) {
                log.error("[EMAIL SERVICE HƯỚNG DẪN] Xác thực Gmail thất bại. Do Google đã chặn mật khẩu thông thường:");
                log.error("  1. Hãy chắc chắn tài khoản Google của bạn đã BẬT 'Xác minh 2 bước' (2-Step Verification).");
                log.error("  2. Vào https://myaccount.google.com/apppasswords tạo 'Mật khẩu ứng dụng' 16 chữ cái.");
                log.error("  3. Dán 16 chữ cái đó vào biến MAIL_PASSWORD trong file .env (không dùng mật khẩu đăng nhập cá nhân).");
            }
            printDevOtp(toEmail, otpCode);
            return false;
        }
    }

    // Tương thích ngược với các hàm gọi cũ nếu có
    public boolean sendPasswordResetEmail(String toEmail, String otpOrUrl) {
        return sendOtpEmail(toEmail, otpOrUrl);
    }

    private void printDevOtp(String toEmail, String otpCode) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println(" [DEV MODE / KHÔI PHỤC MẬT KHẨU - MÃ OTP]");
        System.out.println(" Email người nhận: " + toEmail);
        System.out.println(" Mã xác thực OTP (Có hiệu lực 05 phút):");
        System.out.println(" >>> [ " + otpCode + " ] <<<");
        System.out.println("=".repeat(80) + "\n");
    }

    private String buildOtpEmailHtml(String otpCode) {
        return """
            <!DOCTYPE html>
            <html lang="vi">
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Helvetica Neue', Arial, sans-serif; background-color: #f1f5f9; margin: 0; padding: 24px; color: #1e293b; }
                    .container { max-width: 580px; margin: 0 auto; background-color: #ffffff; border-radius: 20px; overflow: hidden; box-shadow: 0 10px 25px rgba(0,0,0,0.08); border: 1px solid #e2e8f0; }
                    .header { background: linear-gradient(135deg, #004c22 0%, #166534 100%); padding: 32px 24px; text-align: center; color: #ffffff; }
                    .header h1 { margin: 0; font-size: 22px; font-weight: 800; letter-spacing: 1px; }
                    .header p { margin: 6px 0 0; font-size: 12px; text-transform: uppercase; letter-spacing: 2px; color: #93e0a2; }
                    .content { padding: 36px 32px; line-height: 1.6; }
                    .greeting { font-size: 18px; font-weight: 700; color: #0f172a; margin-bottom: 12px; }
                    .otp-box { background-color: #f8fafc; border: 2px dashed #004c22; border-radius: 16px; padding: 24px; text-align: center; margin: 28px 0; }
                    .otp-label { font-size: 12px; text-transform: uppercase; letter-spacing: 2px; color: #64748b; margin-bottom: 8px; font-weight: 700; }
                    .otp-code { font-size: 42px; font-weight: 900; letter-spacing: 14px; color: #004c22; font-family: 'Courier New', Courier, monospace; padding-left: 14px; }
                    .otp-expiry { font-size: 12px; color: #64748b; margin-top: 10px; font-weight: 500; }
                    .note { background-color: #fef2f2; border-left: 4px solid #ef4444; padding: 12px 16px; border-radius: 6px; font-size: 13px; color: #991b1b; margin: 20px 0; }
                    .footer { text-align: center; padding: 24px; font-size: 12px; color: #94a3b8; background-color: #f8fafc; border-top: 1px solid #e2e8f0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>THE4BOOKSTORE</h1>
                        <p>Mã Xác Thực Khôi Phục Mật Khẩu</p>
                    </div>
                    <div class="content">
                        <div class="greeting">Xin chào quý khách,</div>
                        <p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản The4BookStore của bạn. Dưới đây là mã xác thực OTP của bạn:</p>
                        
                        <div class="otp-box">
                            <div class="otp-label">MÃ XÁC THỰC OTP</div>
                            <div class="otp-code">{{OTP_CODE}}</div>
                            <div class="otp-expiry">Mã này có hiệu lực trong vòng <strong>05 phút</strong></div>
                        </div>

                        <div class="note">
                            <strong>Lưu ý bảo mật:</strong> Tuyệt đối không chia sẻ mã này cho bất kỳ ai. Nhân viên The4BookStore không bao giờ yêu cầu bạn cung cấp mã OTP này.
                        </div>

                        <p style="font-size: 13px; color: #64748b;">Nếu quý khách không thực hiện yêu cầu này, vui lòng bỏ qua email và mật khẩu tài khoản của quý khách vẫn được bảo mật an toàn.</p>
                    </div>
                    <div class="footer">
                        &copy; 2026 The4BookStore. Mọi quyền được bảo lưu.<br/>
                        Email này được gửi tự động từ hệ thống The4BookStore, vui lòng không phản hồi lại.
                    </div>
                </div>
            </body>
            </html>
            """.replace("{{OTP_CODE}}", otpCode);
    }
}
