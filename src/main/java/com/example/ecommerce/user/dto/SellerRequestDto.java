package com.example.ecommerce.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SellerRequestDto {

    @NotBlank
    @NotNull
    private String businessName;

    @NotBlank
    @NotNull
    private String businessType;


    @Email
    private String businessEmail;

    @NotBlank
    @NotNull
    private String businessAddress;

    @NotBlank
    @NotNull
    @Pattern(
             regexp = "^[0-9]{10,15}$",
            message = "Contact info should be a valid phone number"
    )
    private String contactInfo;

    @NotBlank
    @NotNull
    private String taxIdentification;

    @NotBlank
    @NotNull
    private String bankAccount;

    @NotBlank
    @NotNull
    private String productCategory;



}
