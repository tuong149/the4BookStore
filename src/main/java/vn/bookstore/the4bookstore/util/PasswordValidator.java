package vn.bookstore.the4bookstore.util;

import java.util.regex.Pattern;

/**
 * Utility class phục vụ kiểm tra và thẩm định độ mạnh của mật khẩu người dùng.
 * Quy định mật khẩu mạnh chuẩn:
 * - Độ dài: từ 8 đến 64 ký tự.
 * - Chứa ít nhất 1 chữ cái in thường (a-z).
 * - Chứa ít nhất 1 chữ cái in hoa (A-Z).
 * - Chứa ít nhất 1 chữ số (0-9).
 * - Chứa ít nhất 1 ký tự đặc biệt (!@#$%^&*()_+-=[]{}|;:,.<>?...).
 * - Không chứa khoảng trắng.
 */
public class PasswordValidator {

    public static final int MIN_LENGTH = 6;
    public static final int MAX_LENGTH = 64;

    public static final String REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9\\s])\\S{6,64}$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    public static final String REQUIREMENT_MESSAGE = 
            "Mật khẩu phải từ 6 đến 64 ký tự, bao gồm ít nhất 1 chữ hoa, 1 chữ thường, 1 chữ số và 1 ký tự đặc biệt (không chứa khoảng trắng).";

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * Kiểm tra nhanh mật khẩu có đạt chuẩn hay không.
     */
    public static boolean isValid(String password) {
        if (password == null) {
            return false;
        }
        return PATTERN.matcher(password).matches();
    }

    /**
     * Thẩm định chi tiết mật khẩu và trả về lý do cụ thể nếu không hợp lệ.
     */
    public static ValidationResult validate(String password) {
        if (password == null || password.trim().isEmpty()) {
            return new ValidationResult(false, "Mật khẩu không được để trống!");
        }
        if (password.contains(" ")) {
            return new ValidationResult(false, "Mật khẩu không được chứa khoảng trắng!");
        }
        if (password.length() < MIN_LENGTH) {
            return new ValidationResult(false, "Mật khẩu phải có tối thiểu " + MIN_LENGTH + " ký tự!");
        }
        if (password.length() > MAX_LENGTH) {
            return new ValidationResult(false, "Mật khẩu không được vượt quá " + MAX_LENGTH + " ký tự!");
        }
        if (!Pattern.compile("[a-z]").matcher(password).find()) {
            return new ValidationResult(false, "Mật khẩu phải chứa ít nhất 1 chữ cái in thường (a-z)!");
        }
        if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            return new ValidationResult(false, "Mật khẩu phải chứa ít nhất 1 chữ cái in hoa (A-Z)!");
        }
        if (!Pattern.compile("[0-9]").matcher(password).find()) {
            return new ValidationResult(false, "Mật khẩu phải chứa ít nhất 1 chữ số (0-9)!");
        }
        if (!Pattern.compile("[^a-zA-Z0-9\\s]").matcher(password).find()) {
            return new ValidationResult(false, "Mật khẩu phải chứa ít nhất 1 ký tự đặc biệt (ví dụ: @, #, $, %, !...)!");
        }

        return new ValidationResult(true, null);
    }
}
