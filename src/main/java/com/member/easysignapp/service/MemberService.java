package com.member.easysignapp.service;

import com.member.easysignapp.domain.Member;
import com.member.easysignapp.repository.MemberRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.access}")
    private long jwtExpirationAccessMs;

    @Value("${jwt.expiration.refresh}")
    private long jwtExpirationRefreshMs;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    public Member signUp(String username, String email, String password) {
        // 아이디 중복 체크
        if (memberRepository.existsByUsername(username)) {
            throw new RuntimeException("이미 사용중인 아이디입니다.");
        }

        // 이메일 중복 체크
        if (memberRepository.existsByEmail(email)) {
            throw new RuntimeException("이미 사용중인 이메일입니다.");
        }

        Member member = new Member();
        member.setUsername(username);
        member.setEmail(email);

        // 비밀번호를 Spring Security를 이용하여 해싱하여 저장
        String hashedPassword = passwordEncoder.encode(password);
        member.setPassword(hashedPassword);

        return memberRepository.save(member);
    }

    public Member login(String email, String password) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);



        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new RuntimeException("잘못된 사용자 이름 또는 비밀번호");
        }

        return member;
    }

    public String generateJwtToken(Member member) {
        Date expirationDate = new Date(System.currentTimeMillis() + jwtExpirationAccessMs);

        Key signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", member.getEmail());
        claims.put("username", member.getUsername());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }
}

