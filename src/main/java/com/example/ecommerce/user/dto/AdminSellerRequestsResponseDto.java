package com.example.ecommerce.user.dto;

import com.example.ecommerce.user.entity.ReviewReason;
import com.example.ecommerce.user.entity.SellerRequestStatus;
import lombok.Builder;

import java.util.Set;
@Builder
public record AdminSellerRequestsResponseDto(
        Long userId,

        Long sellerRequestId,

        String businessName,

        String businessType,

        String contactInfo,

        String businessEmail,

        String businessAddress,

        String taxIdentification,

        String bankAccount,

        String productCategory,

       SellerRequestStatus sellerRequestStatus,

        String rejectionReason,

        boolean requiresReview,

        Set<ReviewReason>reviewReason

) {
}
