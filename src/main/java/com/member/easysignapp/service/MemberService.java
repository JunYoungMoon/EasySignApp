package com.member.easysignapp.service;

import com.member.easysignapp.dto.MemberInfo;
import com.member.easysignapp.dto.MemberResponse;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public MemberResponse signUp(MemberRequest request) {
        // 이메일 중복 체크
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 사용중인 이메일입니다.");
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new RuntimeException("플랫폼 가입을 위해서는 비밀번호가 필요합니다.");
        }

        // 비밀번호를 Spring Security를 이용하여 해싱하여 저장
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // jwt토큰에 보여질 UUID 생성
        UUID randomUUID = UUID.randomUUID();

        List<String> roles = request.getRoles();

        // role이 null이거나 비어 있는지 확인하고 필요한 경우 기본 role을 할당
        if (roles == null || roles.isEmpty()) {
            roles = Collections.singletonList("user");
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .uuid(randomUUID.toString())
                .password(hashedPassword)
                .name(request.getName())
                .roles(roles)
                .build();

        memberRepository.save(member);

        return MemberResponse.builder()
                .email(member.getEmail())
                .name(member.getName())
                .build();
    }

    public TokenInfo login(MemberRequest request) {
        //아이디 값으로 사용자 정보를 가져와 uuid로 아이디값을 저장한다.
        //소셜 로그인일때 id 값을 jwt로 노출시키기에는 보안적인 부분을 우려.
        Optional<Member> user = memberRepository.findByEmail(request.getEmail());

        if (user.isPresent()) {
            //사용자의 인증을 위해 이 객체를 사용하여 사용자가 제공한 아이디와 비밀번호를 저장
            //이 토큰은 사용자 인증을 위해 사용되며, 인증 매니저를 통해 실제 인증이 수행
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.get().getUuid(), request.getPassword());

            //authenticationManagerBuilder는 스프링 시큐리티 설정에서 정의한 AuthenticationManager를 생성하는 빌더 클래스이고
            //getObject() 메서드를 사용하여 실제 AuthenticationManager 객체를 가져온다.
            //AuthenticationManager의 authenticate 메서드에 authenticationToken을 전달하여 사용자를 인증하는데,
            //이때 인증은 CustomUserDetailsService에서 UserDetailsService 인터페이스의 구현 메소드인 loadUserByUsername를 통해 인증을 진행한다.
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            return jwtTokenProvider.generateToken(authentication);
        } else {
            throw new RuntimeException(request.getEmail() + " 회원을 찾을 수 없습니다");
        }
    }

    public MemberInfo userInfo(String uuid){
        // uuid 기반으로 User 테이블 row 찾기
        Optional<Member> optionalMember = memberRepository.findByUuid(uuid);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();

            return MemberInfo.builder()
                    .profileImage(member.getProfileImage())
                    .email(member.getEmail())
                    .name(member.getName())
                    .nickName(member.getNickname())
                    .build();
        } else {
            throw new RuntimeException("해당 정보를 가진 사용자가 없습니다.");
        }
    }

    @Transactional
    public void updateMemberInfo(String uuid, String newNickname, String newProfileImagePath) {
        // uuid 기반으로 Member 테이블 row 찾기
        Optional<Member> optionalMember = memberRepository.findByUuid(uuid);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();

            if (newNickname != null) {
                member.setNickname(newNickname);
            }

            if (newProfileImagePath != null) {
                member.setProfileImage(newProfileImagePath);
            }

            memberRepository.save(member);
        } else {
            throw new RuntimeException("해당 정보를 가진 사용자가 없습니다.");
        }
    }
}

