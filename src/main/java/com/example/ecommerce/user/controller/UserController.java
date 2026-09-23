package com.example.ecommerce.user.controller;

import com.example.ecommerce.user.dto.UpdateUserRequestDto;

import com.example.ecommerce.user.dto.UserResponseDto;
import com.example.ecommerce.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;



@PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers(){
        return ResponseEntity.ok(userService.getUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/update")
    public ResponseEntity<UserResponseDto> updateUser( @Valid @RequestBody UpdateUserRequestDto request){
        return ResponseEntity.ok(userService.updateUser(request));
    }


@PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser() {
        userService.deleteUser();

        return ResponseEntity.noContent().build();
    }
}
