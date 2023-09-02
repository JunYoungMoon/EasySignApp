package com.member.easysignapp.config;

import com.member.easysignapp.handler.OAuth2LoginFailureHandler;
import com.member.easysignapp.handler.OAuth2LoginSuccessHandler;
import com.member.easysignapp.security.JwtAuthenticationFilter;
import com.member.easysignapp.security.JwtTokenProvider;
import com.member.easysignapp.service.CustomOAuth2UserService;
import com.member.easysignapp.util.WebUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    String[] patterns = new String[] {
            "/",
            "/assets/**",
            "/signup",
            "/getcsrf",
            "/login/**",
            "/oauth2/**"
    };

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //CSP로 XSS 공격을 방지 및 csrf 검증 모바일일때 제외
        http
                .headers(headers ->
                        headers.contentSecurityPolicy("script-src 'self'"))
                .csrf()
                .requireCsrfProtectionMatcher(request -> !WebUtil.isMobile(request))
                .ignoringAntMatchers(patterns)
                .csrfTokenRepository(csrfTokenRepository())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //요청에 대한 권한 설정
        http
                .authorizeRequests()
                .antMatchers(patterns).permitAll()
                .anyRequest().authenticated(); // 그 외의 URL은 인증된 사용자만 접근 가능

        //OAuth 2.0 로그인 설정 시작
        http
                .oauth2Login()
                .successHandler(oAuth2LoginSuccessHandler)
                .failureHandler(oAuth2LoginFailureHandler)
                .userInfoEndpoint()
                .userService(customOAuth2UserService);

        //jwt filter 설정
        http
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}