package com.example.ecommerce.user.dto;

import com.example.ecommerce.user.entity.SellerRequestStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminUpdateSellerRequestDto(
@NotNull
        SellerRequestStatus status,
@Size(min = 5, max = 500, message = "should be between 5 and 500 characters")
        String rejectionReason
) {
}
