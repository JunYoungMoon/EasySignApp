package com.member.easysignapp.service;

import com.member.easysignapp.entity.Member;
import com.member.easysignapp.dto.TokenInfo;
import com.member.easysignapp.dto.MemberRequest;
import com.member.easysignapp.repository.MemberRepository;
import com.member.easysignapp.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public Member signUp(MemberRequest request) {
        // 이메일 중복 체크
        if (memberRepository.existsById(request.getId())) {
            throw new RuntimeException("이미 사용중인 이메일입니다.");
        }

        // 비밀번호를 Spring Security를 이용하여 해싱하여 저장
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        //jwt토큰에 보여질 UUID 생성
        UUID randomUUID = UUID.randomUUID();

        Member member = Member.builder()
                .id(request.getId())
                .uuid(randomUUID.toString())
                .password(hashedPassword)
                .email(request.getEmail())
                .name(request.getName())
                .roles(request.getRoles())
                .build();

        return memberRepository.save(member);
    }

    public TokenInfo login(MemberRequest request) {
        //사용자의 인증을 위해 이 객체를 사용하여 사용자가 제공한 아이디와 비밀번호를 저장
        //이 토큰은 사용자 인증을 위해 사용되며, 인증 매니저를 통해 실제 인증이 수행
        //TODO ID 값으로 UUID를 찾아와서 넣어야 한다.
        Optional<Member> user = memberRepository.findById(request.getId());

        if (user.isPresent()) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.get().getUuid(), request.getPassword());

            //authenticationManagerBuilder는 스프링 시큐리티 설정에서 정의한 AuthenticationManager를 생성하는 빌더 클래스이고
            //getObject() 메서드를 사용하여 실제 AuthenticationManager 객체를 가져온다.
            //AuthenticationManager의 authenticate 메서드에 authenticationToken을 전달하여 사용자를 인증하는데,
            //이때 인증은 CustomUserDetailsService에서 UserDetailsService 인터페이스의 구현 메소드인 loadUserByUsername를 통해 인증을 진행한다.
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            return jwtTokenProvider.generateToken(authentication);
        } else {
            throw new RuntimeException("Member not found for ID: " + request.getId());
        }
    }
}

