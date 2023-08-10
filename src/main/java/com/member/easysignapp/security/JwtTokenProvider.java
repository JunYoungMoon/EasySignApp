package com.member.easysignapp.security;

import com.member.easysignapp.domain.RefreshToken;
import com.member.easysignapp.domain.TokenInfo;
import com.member.easysignapp.service.RefreshTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    @Value("${jwt.expiration.access}")
    private long accessExpiration;
    @Value("${jwt.expiration.refresh}")
    private long refreshExpiration;
    private final Key key;
    private final RefreshTokenService refreshTokenService;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenInfo generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + accessExpiration);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("tokenType", "access")
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("tokenType", "refresh")
                .setExpiration(new Date(now + refreshExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token DB 저장
        refreshTokenService.saveRefreshToken(authentication.getName(), refreshToken, new Date(now + refreshExpiration).toInstant());

        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 정보를 검증하는 메서드
    public void validateToken(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, IllegalArgumentException, SecurityException {
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public void findByRefreshToken(String token){
        Optional<RefreshToken> byToken = refreshTokenService.findByToken(token);


    }
}
