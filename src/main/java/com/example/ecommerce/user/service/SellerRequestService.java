package com.example.ecommerce.user.service;

import com.example.ecommerce.common.exception.DuplicateRequestException;
import com.example.ecommerce.common.exception.InvalidRequestException;
import com.example.ecommerce.common.exception.RequestDeniedException;
import com.example.ecommerce.common.exception.RequestNotFoundException;
import com.example.ecommerce.user.dto.AdminSellerRequestsResponseDto;
import com.example.ecommerce.user.dto.AdminUpdateSellerRequestDto;
import com.example.ecommerce.user.dto.SellerApprovedRequestResponseDto;
import com.example.ecommerce.user.dto.SellerRequestDto;
import com.example.ecommerce.user.dto.SellerRequestStatusResponseDto;
import com.example.ecommerce.user.dto.UpdateSellerRequest;
import com.example.ecommerce.user.entity.ReviewReason;
import com.example.ecommerce.user.entity.SellerRequest;
import com.example.ecommerce.user.entity.SellerRequestStatus;
import com.example.ecommerce.user.entity.User;
import com.example.ecommerce.user.repository.SellerRequestRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor

public class SellerRequestService {
    private final SellerRequestRepository sellerRequestRepository;



public List <SellerApprovedRequestResponseDto> getApprovedProfile(){

    var user = getCurrentUser();

  List<SellerRequest> requests = sellerRequestRepository.findAllByUserIdAndSellerRequestStatusIn(user.getId(),
          Set.of(
                  SellerRequestStatus.APPROVED));


    return requests.stream()
            .map(request -> buildApprovedRequestResponse(request)).toList();
}


    public SellerRequestStatusResponseDto getStatus(){
        var user = getCurrentUser();

        var request =sellerRequestRepository.findFirstByUserIdOrderByIdDesc(user.getId()).orElseThrow(
                ()-> new RequestNotFoundException("no request found")
        );




        return buildRequestResponse(request);
    }



@Transactional
    public SellerRequestStatusResponseDto requestToBeASeller(SellerRequestDto request){

        var user = getCurrentUser();

     var checkDuplicateRequests =  sellerRequestRepository.existsByUserIdAndSellerRequestStatusIn(user.getId(),
               Set.of(
                       SellerRequestStatus.SUBMITTED,
                       SellerRequestStatus.DRAFT,
                       SellerRequestStatus.UNDER_REVIEW,
                       SellerRequestStatus.NEEDS_MORE_INFO
               )
               );

     if (checkDuplicateRequests){
         throw new DuplicateRequestException("You already have a request");
     }

        if(request.getBusinessEmail() == null || request.getBusinessEmail().isBlank()){

            request.setBusinessEmail(user.getEmail());
        }

        SellerRequestDto normalizedRequest = normalizedRequest(request);


        var request1 = buildSellerRequest(normalizedRequest);


boolean dupBankAccount = sellerRequestRepository.existsByBankAccountAndUserIdNot(request1.getBankAccount(), user.getId());

boolean dupTaxId = sellerRequestRepository.existsByTaxIdentificationAndUserIdNot(request1.getTaxIdentification(),user.getId());


if(dupBankAccount || dupTaxId){

    request1.setRequiresReview(true);

    Set <ReviewReason> reviewReasons = new HashSet<>();

    if(dupBankAccount){

        reviewReasons.add(ReviewReason.DUPLICATE_BANK_ACCOUNT);
    }

    if(dupTaxId){

        reviewReasons.add(ReviewReason.DUPLICATE_TAX_ID);
    }
    request1.setReviewReason(reviewReasons);
}



            request1.setUser(user);
            request1.setSellerRequestStatus(SellerRequestStatus.SUBMITTED);

            sellerRequestRepository.save(request1);


        return buildRequestResponse(request1);
    }

@Transactional
    public SellerRequestStatusResponseDto updateRequest(Long id,UpdateSellerRequest request ){

var user = getCurrentUser();




        var profile = sellerRequestRepository.findById(id).orElseThrow(()-> new RequestNotFoundException("no request found"));

if(!user.getId().equals(profile.getUser().getId())){
    throw new RequestNotFoundException("request not found");
}

Set<SellerRequestStatus> allowedStatuses = Set.of(SellerRequestStatus.APPROVED,SellerRequestStatus.NEEDS_MORE_INFO);


if(!allowedStatuses.contains(profile.getSellerRequestStatus())){

   throw new RequestDeniedException("can't update request at this stage");
}



    if(request.getBankAccount() != null && !request.getBankAccount().isBlank()) {


        String bankAccount = request.getBankAccount().trim();

        if (!bankAccount.equalsIgnoreCase(profile.getBankAccount())) {

            profile.setBankAccount(bankAccount

            );


            if (sellerRequestRepository.existsByBankAccountAndUserIdNot(bankAccount,user.getId())) {

                profile.setRequiresReview(true);

                profile.getReviewReason().add(ReviewReason.DUPLICATE_BANK_ACCOUNT);

            }
        }


        }


        if (request.getBusinessAddress() != null && !request.getBusinessAddress().isBlank()) {

            String address = request.getBusinessAddress().trim();

            if (!address.equals(profile.getAddress())) {

                profile.setAddress(address);
            }
        }



    if(request.getBusinessEmail() != null && !request.getBusinessEmail().isBlank()){

        String email = request.getBusinessEmail().trim().toLowerCase();

        if(!email.equalsIgnoreCase(profile.getBusinessEmail())){

            profile.setBusinessEmail(email);
        }
    }

    if(request.getProductCategory() != null && !request.getProductCategory().isBlank()){

        String productCategory = request.getProductCategory().trim().toLowerCase();

        if(!productCategory.equalsIgnoreCase(profile.getProductCategory())){

            profile.setProductCategory(productCategory);
        }
    }

    if(request.getContactInfo() != null && !request.getContactInfo().isBlank()){

        String contactInfo = request.getContactInfo().trim().toLowerCase();

        if(!contactInfo.equalsIgnoreCase(profile.getContactInfo())){

            profile.setContactInfo(contactInfo);
        }
    }

    if(request.getBusinessName() != null && !request.getBusinessName().isBlank()){

        String businessName = request.getBusinessName().trim();

        if(!businessName.equalsIgnoreCase(profile.getBusinessName())){

            profile.setBusinessName(businessName);
        }
    }


    if(request.getBusinessType() != null && !request.getBusinessType().isBlank()){

        String businessType = request.getBusinessType().trim().toLowerCase();

        if(!businessType.equalsIgnoreCase(profile.getBusinessType())){

            profile.setBusinessType(businessType);
        }
    }

    if(profile.getSellerRequestStatus() == SellerRequestStatus.NEEDS_MORE_INFO)
           {
        profile.setSellerRequestStatus(SellerRequestStatus.SUBMITTED);
        profile.setRejectionReason(null);
    }


    sellerRequestRepository.save(profile);

    return buildRequestResponse(profile);
    }




    public AdminSellerRequestsResponseDto getSellerRequestById(Long id){


    var request = sellerRequestRepository.findById(id).orElseThrow(()-> new RequestNotFoundException("request not found"));

    return mapSellerRequestToAdminResponse(request);
    }


    @Transactional
    public AdminSellerRequestsResponseDto updateRequestStatuses (Long id, AdminUpdateSellerRequestDto request){



    Set <SellerRequestStatus> needsAMessage = EnumSet.of(SellerRequestStatus.REJECTED,SellerRequestStatus.NEEDS_MORE_INFO);

    if(needsAMessage.contains(request.status()) && (request.rejectionReason() == null || request.rejectionReason().isBlank())){

            throw  new InvalidRequestException("must provide a reason for rejecting or asking for more information ");
    }


    var activeRequest = sellerRequestRepository.findById(id).orElseThrow(()-> new RequestNotFoundException("request with id: " + id + " not found"));





    if(request.status() != null  ){


        SellerRequestStatus status = request.status();

    activeRequest.setSellerRequestStatus(status);


    }




        if(!needsAMessage.contains(activeRequest.getSellerRequestStatus())){
            activeRequest.setRejectionReason(null);
        }




    else if(request.rejectionReason() != null && !request.rejectionReason().isBlank()){

        String rejectionReason = request.rejectionReason().trim();

        if(!rejectionReason.equals(activeRequest.getRejectionReason())){

            activeRequest.setRejectionReason(rejectionReason);
        }

    }
    sellerRequestRepository.save(activeRequest);


    return mapSellerRequestToAdminResponse(activeRequest);
    }


    public List <AdminSellerRequestsResponseDto> getAllSellerRequest() {

    List<SellerRequest> requests = sellerRequestRepository.findAll();

    return
            requests.stream().map(request -> mapSellerRequestToAdminResponse(request)).toList();

    }



    private AdminSellerRequestsResponseDto mapSellerRequestToAdminResponse(SellerRequest request) {



        return new AdminSellerRequestsResponseDto(
                request.getUser().getId(),
                request.getId(),
                request.getBusinessName(),
                request.getBusinessType(),
                request.getContactInfo(),
                request.getBusinessEmail(),
                request.getAddress(),
                request.getTaxIdentification(),
                request.getBankAccount(),
                request.getProductCategory(),
                request.getSellerRequestStatus(),
                request.getRejectionReason(),
                request.isRequiresReview(),
                request.getReviewReason()
        );

    }
    private SellerRequestStatusResponseDto buildRequestResponse(SellerRequest request){
        return SellerRequestStatusResponseDto.builder()
                .id(request.getId())
                .status(request.getSellerRequestStatus())
                .rejectionReason(request.getRejectionReason())
                .build();

    }

    private SellerApprovedRequestResponseDto buildApprovedRequestResponse (SellerRequest request){

        return SellerApprovedRequestResponseDto.builder()
                .sellerRequestId(request.getId())
                .businessType(request.getBusinessType())
                .businessName(request.getBusinessName())
                .businessAddress(request.getAddress())
                .businessEmail(request.getBusinessEmail())
                .taxIdentification(request.getTaxIdentification())
                .status(request.getSellerRequestStatus())
                .contactInfo(request.getContactInfo())
                .productCategory(request.getProductCategory())
                .bankAccount(request.getBankAccount())
                .build();
    }

    private SellerRequest buildSellerRequest(SellerRequestDto request){

        return SellerRequest.builder()
                .contactInfo(request.getContactInfo())
                .businessName(request.getBusinessName())
                .address(request.getBusinessAddress())
                .businessEmail(request.getBusinessEmail())
                .bankAccount(request.getBankAccount())
                .businessType(request.getBusinessType())
                .productCategory(request.getProductCategory())
                .taxIdentification(request.getTaxIdentification())
                .build();
    }


    private SellerRequestDto normalizedRequest(SellerRequestDto request){

        String normalizedContactInfo = request.getContactInfo().trim().toLowerCase();

        String normalizedBusinessName = request.getBusinessName().trim();

        String normalizedBusinessType = request.getBusinessType().trim().toLowerCase();

        String normalizedBusinessEmail = request.getBusinessEmail().trim().toLowerCase();

        String normalizedBusinessAddress = request.getBusinessAddress().trim();

        String normalizedTaxIdentification = request.getTaxIdentification().trim().toLowerCase();

        String normalizedBankAccount = request.getBankAccount().trim();

        String normalizedProductCategory = request.getProductCategory().trim().toLowerCase();

        return SellerRequestDto
                .builder()

                .contactInfo(normalizedContactInfo)
                .businessName(normalizedBusinessName)
                .businessAddress(normalizedBusinessAddress)
                .businessEmail(normalizedBusinessEmail)
                .bankAccount(normalizedBankAccount)
                .businessType(normalizedBusinessType)
                .productCategory(normalizedProductCategory)
                .taxIdentification(normalizedTaxIdentification)
                .build();

    }

    private User getCurrentUser(){
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


}
