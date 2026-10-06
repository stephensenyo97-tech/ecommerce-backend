package com.example.ecommerce.user.dto;

import com.example.ecommerce.user.entity.SellerRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class SellerApprovedRequestResponseDto {

private Long sellerRequestId;

private SellerRequestStatus status;

private String businessName;

private String businessType;

private String businessEmail;

private String contactInfo;

private String businessAddress;

private String taxIdentification;

private String bankAccount;

private String productCategory;
}
