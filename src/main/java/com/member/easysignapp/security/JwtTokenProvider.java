package com.member.easysignapp.security;

import com.member.easysignapp.domain.Member;
import com.member.easysignapp.domain.RefreshToken;
import com.member.easysignapp.domain.TokenInfo;
import com.member.easysignapp.repository.MemberRepository;
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
import java.util.*;
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

    private final MemberRepository memberRepository;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, RefreshTokenService refreshTokenService, MemberRepository memberRepository) {
        this.refreshTokenService = refreshTokenService;
        this.memberRepository = memberRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenInfo generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String id = userDetails.getUsername();

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + accessExpiration);
        String accessToken = Jwts.builder()
                .setSubject(id)
                .claim("tokenType", "access")
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setSubject(id)
                .claim("tokenType", "refresh")
                .setExpiration(new Date(now + refreshExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token DB 저장
        refreshTokenService.saveRefreshToken(id, refreshToken, new Date(now + refreshExpiration).toInstant());

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

        // tokenType 클레임 값 가져오기
        String tokenType = claims.get("tokenType", String.class);

        //TODO refresh일때 유저 메일로 권한 정보 가져오기
        if ("refresh".equals(tokenType)) {
            // 클레임에서 이메일 정보 가져오기
            String id = claims.getSubject();

            // 이메일을 기반으로 Member 테이블 row 찾기
            Optional<Member> member = memberRepository.findById(id);

            if (member.isPresent()) {
                Member memberEntity = member.get();

                // member 객체에서 권한 정보 가져오기
                Collection<? extends GrantedAuthority> authorities = memberEntity.getAuthorities();

                // UserDetails 객체를 만들어서 Authentication 리턴
                UserDetails principal = new User(claims.getSubject(), "", authorities);
                return new UsernamePasswordAuthenticationToken(principal, "", authorities);

            } else {
                throw new RuntimeException("해당 이메일을 가진 사용자가 없습니다.");
            }
        } else {
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

    public boolean isRefreshTokenValid(String token) {
        Optional<RefreshToken> byToken = refreshTokenService.findByToken(token);

        return byToken.isPresent();
    }

    public void deleteRefreshToken(String token){
        refreshTokenService.deleteRefreshToken(token);
    }
}
