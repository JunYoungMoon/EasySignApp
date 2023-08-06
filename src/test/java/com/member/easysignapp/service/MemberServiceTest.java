package com.member.easysignapp.service;

import com.member.easysignapp.domain.Member;
import com.member.easysignapp.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    public void testSignUp_Success() {
        // Given
        String username = "testuser";
        String email = "test@example.com";
        String password = "testpassword";

        List<String> roles = new ArrayList<>();
        roles.add("user");
        roles.add("admin");

        when(memberRepository.existsByUsername(username)).thenReturn(false);
        when(memberRepository.existsByEmail(email)).thenReturn(false);

        // When
        Member savedMember = memberService.signUp(username, email, password, roles);

        // Then
        assertNotNull(savedMember);
        assertNotNull(savedMember.getId());
        assertEquals(username, savedMember.getUsername());
        assertEquals(email, savedMember.getEmail());
        assertEquals(password, savedMember.getPassword());

        // Verify interactions
        verify(memberRepository, times(1)).existsByUsername(username);
        verify(memberRepository, times(1)).existsByEmail(email);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    // Other test methods...
}