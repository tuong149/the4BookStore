package vn.bookstore.the4bookstore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;
    private final OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtLogoutSuccessHandler jwtLogoutSuccessHandler;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                          CustomOidcUserService customOidcUserService,
                          OAuth2LoginSuccessHandler oauth2LoginSuccessHandler,
                          CustomAuthenticationFailureHandler customAuthenticationFailureHandler,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtLogoutSuccessHandler jwtLogoutSuccessHandler) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customOidcUserService = customOidcUserService;
        this.oauth2LoginSuccessHandler = oauth2LoginSuccessHandler;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtLogoutSuccessHandler = jwtLogoutSuccessHandler;
    }

    @Bean
    public org.springframework.security.core.session.SessionRegistry sessionRegistry() {
        return new org.springframework.security.core.session.SessionRegistryImpl();
    }

    @Bean
    public org.springframework.security.web.session.HttpSessionEventPublisher httpSessionEventPublisher() {
        return new org.springframework.security.web.session.HttpSessionEventPublisher();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/vendor", "/vendor/**").authenticated()
                .requestMatchers("/admin/platform-fees", "/admin/platform-fees/**").hasAnyRole("ADMIN", "QUANLY", "MANAGER")
                .requestMatchers("/admin/system/**", "/admin/managers/**").hasRole("ADMIN")
                .requestMatchers("/admin/users", "/admin/users/**").hasAnyRole("ADMIN", "QUANLY", "MANAGER")
                .requestMatchers("/admin/kho", "/admin/kho/**", "/kho/**").hasAnyRole("ADMIN", "QUANLY", "MANAGER", "NHANVIENKHO")
                .requestMatchers("/admin/orders", "/admin/orders/**").hasAnyRole("ADMIN", "QUANLY", "MANAGER", "NHANVIENBANHANG")
                .requestMatchers("/admin", "/admin/**", "/manager", "/manager/**").hasAnyRole("ADMIN", "QUANLY", "MANAGER")
                .requestMatchers("/ban-hang/**").hasAnyRole("ADMIN", "QUANLY", "MANAGER", "NHANVIENBANHANG")
                .requestMatchers("/khach-hang/**").hasAnyRole("KHACHHANG", "USER", "ADMIN", "QUANLY", "MANAGER")
                .requestMatchers("/profile", "/profile/**", "/dia-chi/**", "/api/dia-chi/**", "/api/favorites/**", "/api/danh-gia/**").authenticated()
                .requestMatchers("/don-hang", "/don-hang/**", "/gio-hang/dat-hang", "/thanh-toan/**").authenticated()
                .requestMatchers("/api/gio-hang/**").permitAll()
                .requestMatchers("/gio-hang", "/gio-hang/**").permitAll()
                .requestMatchers("/shop", "/shop/**").permitAll()
                .requestMatchers("/forgot-password", "/forgot-password/**", "/reset-password", "/reset-password/**", "/resend-otp").permitAll()
                .anyRequest().permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    if (request.getRequestURI().startsWith("/admin") || request.getRequestURI().startsWith("/kho")) {
                        response.sendRedirect(request.getContextPath() + "/?accessDenied=true");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/");
                    }
                })
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/process-login")
                .successHandler(oauth2LoginSuccessHandler)
                .failureHandler(customAuthenticationFailureHandler)
                .permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                    .oidcUserService(customOidcUserService)
                )
                .successHandler(oauth2LoginSuccessHandler)
                .failureHandler((request, response, exception) -> {
                    if (exception != null && exception.getMessage() != null && (exception.getMessage().contains("account_locked") || exception.getMessage().contains("khóa"))) {
                        response.sendRedirect(request.getContextPath() + "/login?locked=true");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/login?error=true");
                    }
                })
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(jwtLogoutSuccessHandler)
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .maximumSessions(-1)
                .sessionRegistry(sessionRegistry())
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}