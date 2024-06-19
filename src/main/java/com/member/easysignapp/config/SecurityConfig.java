package com.member.easysignapp.config;

import com.member.easysignapp.exception.CustomAuthenticationEntryPoint;
import com.member.easysignapp.handler.OAuth2LoginFailureHandler;
import com.member.easysignapp.handler.OAuth2LoginSuccessHandler;
import com.member.easysignapp.security.JwtAuthenticationFilter;
import com.member.easysignapp.security.JwtTokenProvider;
import com.member.easysignapp.service.CustomOAuth2UserService;
import com.member.easysignapp.util.CommonUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.*;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.function.Supplier;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final MessageSourceAccessor messageSourceAccessor;
    private final CorsConfigurationSource corsConfigurationSource;

    String[] csrfPatterns = new String[]{
            "/api/getcsrf",
            "/login/**",
            "/oauth2/**",
            "/profile/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**"
    };

    String[] authPatterns = new String[]{
            "/api/registerUser",
            "/api/send-email-code",
            "/api/email-verification",
            "/api/login",
            "/api/user-info",
            "/api/getcsrf",
            "/api/test",
            "/login/**",
            "/oauth2/**",
            "/api/check-auth",
            "/profile/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**"
    };

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }

    @Bean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //CSP로 XSS 공격을 방지 및 csrf 검증 모바일일때 제외
        http
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("script-src 'self'")
                        )
                )
//                .headers(headers ->
//                        headers.contentSecurityPolicy("script-src 'self'"))
//                .csrf()
                .csrf((csrf) -> csrf
                        .requireCsrfProtectionMatcher(request -> !CommonUtil.isMobile(request))
                        .ignoringRequestMatchers(csrfPatterns)
                        .csrfTokenRepository(csrfTokenRepository())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
//                .requireCsrfProtectionMatcher(request -> !CommonUtil.isMobile(request))
//                .ignoringAntMatchers(csrfPatterns)
//                .csrfTokenRepository(csrfTokenRepository())
//                .and()
//                .cors()
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //jwt filter 설정
        http
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, messageSourceAccessor), UsernamePasswordAuthenticationFilter.class);
        //요청에 대한 권한 설정
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(authPatterns).permitAll()
                        .anyRequest().authenticated()
                );
//                .authorizeRequests()
//                .antMatchers(authPatterns).permitAll()
//                .anyRequest().authenticated(); // 그 외의 URL은 인증된 사용자만 접근 가능
        //인증 실패시 예외 처리 재정의
        http
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .authenticationEntryPoint(customAuthenticationEntryPoint()));
//                .exceptionHandling()
//                .authenticationEntryPoint(customAuthenticationEntryPoint());
        //OAuth 2.0 로그인 설정 시작
        http
                .oauth2Login((oauth2) -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                );
//                .oauth2Login()
//                .successHandler(oAuth2LoginSuccessHandler)
//                .failureHandler(oAuth2LoginFailureHandler)
//                .userInfoEndpoint()
//                .userService(customOAuth2UserService);

        //csrf filter 검증 이후 새로운 토큰 발급 필터 설정
        //csrf는 XSRF-TOKEN의 쿠키값과 전달받은 파라미터 _csrf 혹은 헤더 X-XSRF-TOKEN 값과 비교한다.
//        http
//                .addFilterAfter(new CsrfTokenRenewalFilter(csrfTokenRepository()), CsrfFilter.class);

        return http.build();
    }
}

// Custom CSRF Token Request Handler
final class SpaCsrfTokenRequestHandler extends CsrfTokenRequestAttributeHandler {
    private final CsrfTokenRequestHandler delegate = new XorCsrfTokenRequestAttributeHandler();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
        // 항상 BREACH 보호 제공
        this.delegate.handle(request, response, csrfToken);
    }

    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
        if (request.getHeader(csrfToken.getHeaderName()) != null) {
            return request.getHeader(csrfToken.getHeaderName());
        }
        return this.delegate.resolveCsrfTokenValue(request, csrfToken);
    }
}

// Filter to ensure CSRF token is loaded
final class CsrfCookieFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        // 쿠키에 토큰 값 저장
        csrfToken.getToken();
        filterChain.doFilter(request, response);
    }
}