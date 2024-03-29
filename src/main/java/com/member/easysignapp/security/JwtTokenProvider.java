package com.member.easysignapp.security;

import com.member.easysignapp.entity.Member;
import com.member.easysignapp.dto.TokenInfo;
import com.member.easysignapp.repository.slave.SlaveMemberRepository;
import com.member.easysignapp.service.RedisService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Key;
import java.time.Duration;
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
    private final SlaveMemberRepository slaveMemberRepository;
    private final RedisService redisService;
    private final MessageSourceAccessor messageSourceAccessor;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, SlaveMemberRepository slaveMemberRepository, RedisService redisService, MessageSourceAccessor messageSourceAccessor) {
        this.slaveMemberRepository = slaveMemberRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.redisService = redisService;
        this.messageSourceAccessor = messageSourceAccessor;
    }

    public TokenInfo generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String uuid = userDetails.getUsername();

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + accessExpiration);
        String accessToken = Jwts.builder()
                .setSubject(uuid)
                .claim("tokenType", "access")
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setSubject(uuid)
                .claim("tokenType", "refresh")
                .setExpiration(new Date(now + refreshExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token Redis 저장
        redisService.setValues(uuid, refreshToken, Duration.ofMillis(refreshExpiration));

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
            String uuid = claims.getSubject();

            // uuid 기반으로 User 테이블 row 찾기
            Optional<Member> user = slaveMemberRepository.findByUuid(uuid);

            if (user.isPresent()) {
                SecurityMember securityMember = new SecurityMember(user.get());

                // member 객체에서 권한 정보 가져오기
                Collection<? extends GrantedAuthority> authorities = securityMember.getAuthorities();

                // UserDetails 객체를 만들어서 Authentication 리턴
                UserDetails principal = new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities);
                return new UsernamePasswordAuthenticationToken(principal, "", authorities);
            } else {
                String noUserMessage = messageSourceAccessor.getMessage("jwt.tokenProvider.noUser.message");

                throw new RuntimeException(noUserMessage);
            }
        } else {
            if (claims.get("auth") == null) {
                String noPermissionMessage = messageSourceAccessor.getMessage("jwt.tokenProvider.noPermission.message");

                throw new RuntimeException(noPermissionMessage);
            }

            // 클레임에서 권한 정보 가져오기
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(claims.get("auth").toString().split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            // UserDetails 객체를 만들어서 Authentication 리턴
            UserDetails principal = new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities);
            return new UsernamePasswordAuthenticationToken(principal, "", authorities);
        }
    }

    // 토큰 정보를 검증하는 메서드
    public void validateToken(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, IllegalArgumentException, SecurityException {
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public boolean isRefreshTokenValid(String token, String uuid) {
        String refreshToken = redisService.getValues(uuid);

        if (!redisService.checkExistsValue(refreshToken)) {
            return false;
        }

        return refreshToken.equals(token);
    }
}
