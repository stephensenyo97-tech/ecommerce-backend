package com.example.ecommerce.user.service;

import com.example.ecommerce.common.exception.DuplicateUsernameException;
import com.example.ecommerce.common.exception.SamePasswordException;
import com.example.ecommerce.common.exception.UserNotFoundException;
import com.example.ecommerce.user.dto.UpdateUserRequestDto;
import com.example.ecommerce.user.dto.UserResponseDto;
import com.example.ecommerce.user.entity.Role;
import com.example.ecommerce.user.entity.User;
import com.example.ecommerce.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void getUsers() {

        User admin = new User();
        admin.setId(1L);
        admin.setEmail("admin@gmail.com");
        admin.setUsername("admin");
        admin.setRole(new HashSet<>(Set.of(Role.ROLE_ADMIN)));

        when(userRepository.findAll()).thenReturn(List.of(admin));

        List<UserResponseDto> response = userService.getUsers();

        assertEquals(1, response.size());
        assertEquals("admin", response.get(0).getUsername());
        assertEquals("admin@gmail.com", response.get(0).getEmail());
        assertTrue(response.get(0).getRoles().contains(Role.ROLE_ADMIN));

        verify(userRepository).findAll();
    }


    @Test
    void getUserById() {

        User user = new User();
        user.setId(1L);
        user.setRole(new HashSet<>(Set.of(Role.ROLE_CUSTOMER)));
        user.setEmail("stephen@gmail.com");
        user.setUsername("steve");
        user.setPassword("stevenhello");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserResponseDto response = userService.getUserById(1L);

        assertEquals("steve", response.getUsername());
        assertEquals("stephen@gmail.com", response.getEmail());
        assertTrue(response.getRoles().contains(Role.ROLE_CUSTOMER));

        verify(userRepository).findById(1L);
    }


    @Test
    void getUserByIdNotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(1L)
        );

        verify(userRepository).findById(1L);
    }


    @Test
    void updateUser() {

        User user = new User();
        user.setId(1L);
        user.setUsername("steve");
        user.setEmail("steve@gmail.com");
        user.setPassword("OldPassword");

        setAuthenticatedUser(user);

        UpdateUserRequestDto request = new UpdateUserRequestDto();
        request.setUsername("senyo");
        request.setPassword("newPassword");

        when(userRepository.findByUsername("senyo"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.matches("newPassword", "OldPassword"))
                .thenReturn(false);

        when(passwordEncoder.encode("newPassword"))
                .thenReturn("EncodedPassword");

        UserResponseDto response = userService.updateUser(request);

        assertEquals("senyo", response.getUsername());

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();

        assertEquals("senyo", capturedUser.getActualUsername());
        assertEquals("EncodedPassword", capturedUser.getPassword());

        verify(passwordEncoder).matches(
                "newPassword",
                "OldPassword"
        );

        verify(passwordEncoder).encode("newPassword");
    }


    @Test
    void updateUserWithSamePassword() {

        User user = new User();
        user.setId(1L);
        user.setUsername("steve");
        user.setEmail("steve@gmail.com");
        user.setPassword("OldPassword");

        setAuthenticatedUser(user);

        UpdateUserRequestDto request = new UpdateUserRequestDto();
        request.setPassword("OldPassword");

        when(passwordEncoder.matches("OldPassword", "OldPassword"))
                .thenReturn(true);

        assertThrows(
                SamePasswordException.class,
                () -> userService.updateUser(request)
        );

        verify(passwordEncoder)
                .matches("OldPassword", "OldPassword");

        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void updateUserWithExistingUsername() {

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("steve");
        currentUser.setEmail("steve@gmail.com");
        currentUser.setPassword("OldPassword");

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("senyo");
        anotherUser.setEmail("senyo@gmail.com");

        setAuthenticatedUser(currentUser);

        UpdateUserRequestDto request = new UpdateUserRequestDto();
        request.setUsername("senyo");

        when(userRepository.findByUsername("senyo"))
                .thenReturn(Optional.of(anotherUser));

        assertThrows(
                DuplicateUsernameException.class,
                () -> userService.updateUser(request)
        );

        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void updateUserWithSameUsername() {

        User user = new User();
        user.setId(1L);
        user.setUsername("steve");
        user.setEmail("steve@gmail.com");
        user.setPassword("OldPassword");

        setAuthenticatedUser(user);

        UpdateUserRequestDto request = new UpdateUserRequestDto();
        request.setUsername("steve");

        assertThrows(
                DuplicateUsernameException.class,
                () -> userService.updateUser(request)
        );

        verify(userRepository, never()).findByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void updateUserPassword() {

        User user = new User();
        user.setId(1L);
        user.setUsername("steve");
        user.setEmail("steve@gmail.com");
        user.setPassword("oldPassword");

        setAuthenticatedUser(user);

        UpdateUserRequestDto request = new UpdateUserRequestDto();
        request.setPassword("newPassword");

        when(passwordEncoder.matches("newPassword", "oldPassword"))
                .thenReturn(false);

        when(passwordEncoder.encode("newPassword"))
                .thenReturn("EncodedPassword");

        userService.updateUser(request);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();

        assertEquals(
                "EncodedPassword",
                capturedUser.getPassword()
        );
    }


    @Test
    void deleteUser() {

        User user = new User();
        user.setId(1L);
        user.setUsername("steve");
        user.setEmail("steve@gmail.com");

        setAuthenticatedUser(user);

        userService.deleteUser();

        verify(userRepository).delete(user);
    }


    private void setAuthenticatedUser(User user) {

        var authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
    }
}