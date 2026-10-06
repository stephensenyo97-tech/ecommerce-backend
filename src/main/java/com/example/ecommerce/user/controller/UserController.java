package com.example.ecommerce.user.controller;

import com.example.ecommerce.user.dto.AdminSellerRequestsResponseDto;
import com.example.ecommerce.user.dto.AdminUpdateSellerRequestDto;
import com.example.ecommerce.user.dto.SellerApprovedRequestResponseDto;
import com.example.ecommerce.user.dto.SellerRequestDto;
import com.example.ecommerce.user.dto.SellerRequestStatusResponseDto;
import com.example.ecommerce.user.dto.UpdateSellerRequest;
import com.example.ecommerce.user.dto.UpdateUserRequestDto;

import com.example.ecommerce.user.dto.UserResponseDto;
import com.example.ecommerce.user.service.SellerRequestService;
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
    private final SellerRequestService sellerRequestService;



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

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/seller-request")
    public ResponseEntity<SellerRequestStatusResponseDto> requestToBeASeller(
            @RequestBody @Valid SellerRequestDto request
    ){
    return ResponseEntity.ok(sellerRequestService.requestToBeASeller(request));
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/seller-request/status")
    public ResponseEntity<SellerRequestStatusResponseDto> getStatus(){

    return ResponseEntity.ok(sellerRequestService.getStatus());
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/seller-profile")
    public ResponseEntity<List<SellerApprovedRequestResponseDto>> getProfile (){

    return ResponseEntity.ok(sellerRequestService.getApprovedProfile());
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/seller-request/{id}")
    public ResponseEntity<SellerRequestStatusResponseDto> updateRequest(
            @PathVariable Long id,
          @RequestBody @Valid UpdateSellerRequest request
    ){

    return ResponseEntity.ok(sellerRequestService.updateRequest(id,request));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-all-requests")
    public  ResponseEntity<List<AdminSellerRequestsResponseDto>>  getAllSellerRequests (){

        return ResponseEntity.ok(sellerRequestService.getAllSellerRequest());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/seller-request/{id}")
    public ResponseEntity<AdminSellerRequestsResponseDto> getSellerRequestById (@PathVariable Long id){

        return ResponseEntity.ok(sellerRequestService.getSellerRequestById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update-seller-status/{id}")
    public ResponseEntity<AdminSellerRequestsResponseDto> updateSellerRequestStatus(
            @PathVariable Long id,
         @Valid   @RequestBody AdminUpdateSellerRequestDto request
            ){

        return ResponseEntity.ok(sellerRequestService.updateRequestStatuses(id,request));
    }

}

