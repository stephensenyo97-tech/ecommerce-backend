package com.example.ecommerce.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSellerRequest {

    @Email(message = "invalid email")
    private String businessEmail;

    @Pattern(
            regexp = "^[0-9]{10,15}$",
            message = "Contact info should be a valid phone number"
    )
    private String contactInfo;


    private String businessAddress;

    private String productCategory;

    private String bankAccount;

    private String businessType;

    private String businessName;

}
