package vn.bookstore.the4bookstore.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public CustomAuthenticationFailureHandler() {
        super("/login?error=true");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof LockedException || exception instanceof DisabledException
                || (exception.getCause() instanceof LockedException)
                || (exception.getCause() instanceof DisabledException)
                || (exception.getMessage() != null && (exception.getMessage().contains("locked") || exception.getMessage().contains("disabled") || exception.getMessage().contains("khóa")))) {
            getRedirectStrategy().sendRedirect(request, response, "/login?locked=true");
        } else {
            getRedirectStrategy().sendRedirect(request, response, "/login?error=true");
        }
    }
}
