package com.member.easysignapp.service;

import com.member.easysignapp.dto.MemberResponse;
import com.member.easysignapp.entity.Member;
import com.member.easysignapp.dto.MemberRequest;
import com.member.easysignapp.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signUp_ValidInput_Success() {
        // Arrange
        MemberRequest request = new MemberRequest();
        request.setPassword("password");
        request.setEmail("test@example.com");
        request.setName("Test User");
        // Add roles to the request if needed

        // Mock behavior for repository and passwordEncoder
        when(memberRepository.existsByEmail(request.getEmail())).thenReturn(false); // Email doesn't exist
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");

        // Act
        MemberResponse response = memberService.signUp(request);

        // Assert
        // Verify that the memberRepository.save method was called once with the correct arguments
        verify(memberRepository, times(1)).save(any(Member.class));

        // Add more assertions as needed to verify the response object
        assertNotNull(response);
        assertEquals(request.getId(), response.getEmail());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getName(), response.getName());
        // Add assertions for roles if needed
    }

}