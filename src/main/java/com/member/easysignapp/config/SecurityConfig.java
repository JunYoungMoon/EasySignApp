package com.member.easysignapp.config;

import com.member.easysignapp.security.JwtTokenProvider;
import com.member.easysignapp.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //.csrf()가 없거나 .csrf()가 있으면 무조건 CsrfFilter.class를 실행하는데
        //.addFilterBefore(new CustomCsrfFilter(csrfTokenRepository()), CsrfFilter.class)
        //이코드를 적어서 내가 CsrfFilter를 커스텀해도 후에 CsrfFilter.class가 동작해버려서 커스텀이 의미 없어져버림
        //그래서 .csrf().disable()로 CsrfFilter.class를 막아버리고 커스텀한걸 돌리게 처리해야함
        http
                .csrf().disable()   //security에서 기본적으로 활성화 되는 CSRF 사용을 막음, 사용을 안막으면 아래의 필터의 동작을 막혀버림
//                .addFilterBefore(new CustomCsrfFilter(csrfTokenRepository()), CsrfFilter.class) // 모바일일때 CSRF 검증 스킵 및 웹일때 CSRF 검증
//                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class) // JWT 검증
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 세션을 생성하지 않고, 요청마다 인증을 수행 JWT 방식
                .and()
                .authorizeRequests()
//                    .antMatchers("/signup", "/getcsrf", "/login**").permitAll() // 로그인 없이 접근 가능한 URL
//                .anyRequest().authenticated() // 그 외의 URL은 인증된 사용자만 접근 가능
                .anyRequest().permitAll() // 그 외의 URL은 인증된 사용자만 접근 가능
                .and()
                .headers(headers ->
                        headers.contentSecurityPolicy("script-src 'self'")) // CSP로 XSS 공격을 방지
                .oauth2Login() // OAuth 2.0 로그인 설정 시작
//                .loginPage("/login") // 사용자를 내가 만든 로그인 페이지로 리다이렉트
//                .defaultSuccessUrl("/oauth2/success") // 로그인 성공 후 리다이렉트될 URL
//                .failureUrl("/oauth2/error") // 로그인 실패 시 리다이렉트될 URL
                .userInfoEndpoint()
                .userService(customOAuth2UserService);

        return http.build();
    }
}