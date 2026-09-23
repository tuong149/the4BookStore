package vn.bookstore.the4bookstore.service;

import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import vn.bookstore.the4bookstore.config.MailConfig;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SendEmailIntegrationTest {

    @Test
    void testSendRealPasswordResetOtpEmail() throws Exception {
        Properties envProps = new Properties();
        try (FileInputStream fis = new FileInputStream(".env")) {
            envProps.load(fis);
        } catch (Exception ignored) {}

        String mailUsername = envProps.getProperty("MAIL_USERNAME", System.getenv("MAIL_USERNAME"));
        String mailPassword = envProps.getProperty("MAIL_PASSWORD", System.getenv("MAIL_PASSWORD"));

        if (mailUsername == null || mailUsername.isBlank()) {
            mailUsername = "nhasachthe4bookstore@gmail.com";
        }
        if (mailPassword == null || mailPassword.isBlank()) {
            mailPassword = "dcyk byxv webx cbus";
        }

        System.out.println("========== BẮT ĐẦU TEST GỬI EMAIL MÃ OTP GMAIL ==========");
        System.out.println("Tài khoản gửi: " + mailUsername);

        MailConfig mailConfig = new MailConfig();
        setField(mailConfig, "host", "smtp.gmail.com");
        setField(mailConfig, "port", 587);
        setField(mailConfig, "username", mailUsername);
        setField(mailConfig, "password", mailPassword);

        JavaMailSender mailSender = mailConfig.javaMailSender();
        EmailService emailService = new EmailService(mailSender);
        setField(emailService, "mailUsername", mailUsername);

        String testOtp = "482915";
        boolean result = emailService.sendOtpEmail(mailUsername.trim(), testOtp);

        System.out.println("Kết quả gửi mã OTP: " + (result ? "THÀNH CÔNG (ĐÃ GỬI TỚI HÒM THƯ " + mailUsername + ")" : "THẤT BÀI"));
        System.out.println("=========================================================");

        assertTrue(result, "Gửi email OTP qua Gmail SMTP phải trả về true khi cấu hình hợp lệ!");
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
