package com.example.ecommerce.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SellerRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id ;

    @Column(nullable = false)
   private String businessName;

   @Column(nullable = false)
   private String businessType;

   @Column(nullable = false)
   private String contactInfo;

   @Column(nullable = false)
   private String businessEmail;

   @Column(nullable = false)
   private String address;

   @Column(nullable = false)
   private String taxIdentification;

   @Column(nullable = false)
   private String bankAccount;

   @Column(nullable = false)
   private String productCategory;

   @Enumerated(EnumType.STRING)
   @Builder.Default
   private SellerRequestStatus sellerRequestStatus = SellerRequestStatus.DRAFT;

   @ManyToOne
   @JoinColumn(name = "user_id", nullable = false)
   private User user;

   @Column(nullable = true , length = 500)
   private String rejectionReason;


   private boolean requiresReview = false;

   @ElementCollection
   @Enumerated(EnumType.STRING)
   @Builder.Default
   private Set<ReviewReason> reviewReason = new HashSet<>();

}
