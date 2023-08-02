package com.member.easysignapp.config;

import com.member.easysignapp.security.CustomCsrfFilter;
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
public class SecurityConfig {
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
        http
                .addFilterBefore(new CustomCsrfFilter(csrfTokenRepository()), CustomCsrfFilter.class) // 모바일일때 CSRF 검증 스킵
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 세션을 생성하지 않고, 요청마다 인증을 수행 JWT 방식
                .and()
                .authorizeRequests()
                    .antMatchers("/signup").permitAll() // 로그인 없이 접근 가능한 URL
                    .antMatchers("/getcsrf").permitAll()
                    .antMatchers("/login").permitAll()
                    .anyRequest().authenticated() // 그 외의 URL은 인증된 사용자만 접근 가능
                .and();

        return http.build();
    }
}



