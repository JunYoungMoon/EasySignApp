package com.member.easysignapp.service;

import com.member.easysignapp.domain.User;
import com.member.easysignapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signUp_ValidInput_Success() {
        // Given
        String id = "test";
        String email = "test@example.com";
        String password = "testpassword";
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");

        when(userRepository.existsById(id)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.signUp(id, email, password, roles);

        // Then
        verify(userRepository, times(1)).existsById(id);
        verify(passwordEncoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(any(User.class));

        // Additional assertions if necessary
         assertEquals(id, result.getEmail());
         assertEquals(roles, result.getRoles());
         assertEquals("hashedPassword", result.getPassword());
    }

    // Other test methods...
}