package com.example.ecommerce.user.dto;

import com.example.ecommerce.user.entity.SellerRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data

public class SellerRequestStatusResponseDto {

    private Long id;

   private SellerRequestStatus status;

   private String rejectionReason;
}
