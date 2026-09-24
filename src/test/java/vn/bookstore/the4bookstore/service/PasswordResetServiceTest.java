package vn.bookstore.the4bookstore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.bookstore.the4bookstore.entity.PasswordResetToken;
import vn.bookstore.the4bookstore.entity.TaiKhoan;
import vn.bookstore.the4bookstore.repository.PasswordResetTokenRepository;
import vn.bookstore.the4bookstore.repository.TaiKhoanRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private TaiKhoanRepository taiKhoanRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private TaiKhoan testTaiKhoan;

    @BeforeEach
    void setUp() {
        testTaiKhoan = new TaiKhoan();
        testTaiKhoan.setMaTaiKhoan(1);
        testTaiKhoan.setTenDangNhap("khachhang1");
        testTaiKhoan.setEmail("khachhang1@gmail.com");
        testTaiKhoan.setMatKhauHash("oldHashedPassword");
        testTaiKhoan.setTrangThai("HoatDong");
        testTaiKhoan.setAuthProvider("LOCAL");
    }

    @Test
    void testCreateOtp_EmailNotFound() {
        when(taiKhoanRepository.findByEmail("notfound@gmail.com")).thenReturn(Optional.empty());

        PasswordResetService.ResetResult result = passwordResetService.createPasswordResetOtp("notfound@gmail.com");

        assertEquals(PasswordResetService.ResetStatus.EMAIL_NOT_FOUND, result.status());
    }

    @Test
    void testCreateOtp_GoogleAccountNoPassword() {
        testTaiKhoan.setAuthProvider("GOOGLE");
        testTaiKhoan.setMatKhauHash(null);
        when(taiKhoanRepository.findByEmail(testTaiKhoan.getEmail())).thenReturn(Optional.of(testTaiKhoan));

        PasswordResetService.ResetResult result = passwordResetService.createPasswordResetOtp(testTaiKhoan.getEmail());

        assertEquals(PasswordResetService.ResetStatus.GOOGLE_ACCOUNT, result.status());
    }

    @Test
    void testCreateOtp_GoogleAccountWithOAuth2Hash() {
        testTaiKhoan.setAuthProvider("GOOGLE");
        testTaiKhoan.setMatKhauHash("OAUTH2_f81d4fae-7dec-11d0-a765-00a0c91e6bf6");
        when(taiKhoanRepository.findByEmail(testTaiKhoan.getEmail())).thenReturn(Optional.of(testTaiKhoan));

        PasswordResetService.ResetResult result = passwordResetService.createPasswordResetOtp(testTaiKhoan.getEmail());

        assertEquals(PasswordResetService.ResetStatus.GOOGLE_ACCOUNT, result.status());
    }

    @Test
    void testCreateOtp_GoogleAccountWithProviderId() {
        testTaiKhoan.setAuthProvider("LOCAL");
        testTaiKhoan.setProviderId("google-sub-123456789");
        when(taiKhoanRepository.findByEmail(testTaiKhoan.getEmail())).thenReturn(Optional.of(testTaiKhoan));

        PasswordResetService.ResetResult result = passwordResetService.createPasswordResetOtp(testTaiKhoan.getEmail());

        assertEquals(PasswordResetService.ResetStatus.GOOGLE_ACCOUNT, result.status());
    }

    @Test
    void testCreateOtp_Success() {
        when(taiKhoanRepository.findByEmail(testTaiKhoan.getEmail())).thenReturn(Optional.of(testTaiKhoan));
        when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PasswordResetService.ResetResult result = passwordResetService.createPasswordResetOtp(testTaiKhoan.getEmail());

        assertEquals(PasswordResetService.ResetStatus.SUCCESS, result.status());
        assertNotNull(result.otpCode());
        assertEquals(6, result.otpCode().length());
        assertTrue(result.otpCode().matches("\\d{6}"));
        verify(emailService, times(1)).sendOtpEmail(eq(testTaiKhoan.getEmail()), eq(result.otpCode()));
    }

    @Test
    void testVerifyOtp_Success() {
        PasswordResetToken token = new PasswordResetToken();
        token.setOtpCode("123456");
        token.setTaiKhoan(testTaiKhoan);
        token.setUsed(false);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(4));

        when(taiKhoanRepository.findByEmail(testTaiKhoan.getEmail())).thenReturn(Optional.of(testTaiKhoan));
        when(tokenRepository.findFirstByTaiKhoanAndOtpCodeAndUsedFalseOrderByNgayTaoDesc(testTaiKhoan, "123456"))
                .thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");

        PasswordResetService.VerifyResult result = passwordResetService
                .verifyOtpAndResetPassword(testTaiKhoan.getEmail(), "123456", "newPassword123");

        assertTrue(result.success());
        assertEquals("encodedNewPassword", testTaiKhoan.getMatKhauHash());
        assertTrue(token.isUsed());
        verify(taiKhoanRepository, times(1)).save(testTaiKhoan);
        verify(tokenRepository, times(1)).save(token);
    }

    @Test
    void testVerifyOtp_WrongOtp() {
        when(taiKhoanRepository.findByEmail(testTaiKhoan.getEmail())).thenReturn(Optional.of(testTaiKhoan));
        when(tokenRepository.findFirstByTaiKhoanAndOtpCodeAndUsedFalseOrderByNgayTaoDesc(testTaiKhoan, "999999"))
                .thenReturn(Optional.empty());

        PasswordResetService.VerifyResult result = passwordResetService
                .verifyOtpAndResetPassword(testTaiKhoan.getEmail(), "999999", "newPassword123");

        assertFalse(result.success());
        assertTrue(result.message().contains("không chính xác"));
    }

    @Test
    void testVerifyOtp_Expired() {
        PasswordResetToken token = new PasswordResetToken();
        token.setOtpCode("123456");
        token.setTaiKhoan(testTaiKhoan);
        token.setUsed(false);
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1)); // Đã hết hạn

        when(taiKhoanRepository.findByEmail(testTaiKhoan.getEmail())).thenReturn(Optional.of(testTaiKhoan));
        when(tokenRepository.findFirstByTaiKhoanAndOtpCodeAndUsedFalseOrderByNgayTaoDesc(testTaiKhoan, "123456"))
                .thenReturn(Optional.of(token));

        PasswordResetService.VerifyResult result = passwordResetService
                .verifyOtpAndResetPassword(testTaiKhoan.getEmail(), "123456", "newPassword123");

        assertFalse(result.success());
        assertTrue(result.message().contains("hết hạn"));
    }

    @Test
    void testVerifyOtp_InvalidLength() {
        PasswordResetService.VerifyResult result = passwordResetService
                .verifyOtpAndResetPassword(testTaiKhoan.getEmail(), "123", "newPassword123");

        assertFalse(result.success());
        assertTrue(result.message().contains("6 chữ số"));
    }

    @Test
    void testVerifyOtp_ShortPassword() {
        PasswordResetService.VerifyResult result = passwordResetService
                .verifyOtpAndResetPassword(testTaiKhoan.getEmail(), "123456", "123");

        assertFalse(result.success());
        assertTrue(result.message().contains("tối thiểu 6 ký tự"));
    }
}
