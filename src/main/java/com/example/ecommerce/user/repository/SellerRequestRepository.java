package com.example.ecommerce.user.repository;

import com.example.ecommerce.user.entity.SellerRequest;
import com.example.ecommerce.user.entity.SellerRequestStatus;
import com.example.ecommerce.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SellerRequestRepository extends JpaRepository<SellerRequest,Long> {



    boolean existsByBankAccountAndUserIdNot(String bankAccount, Long userId);

    boolean existsByTaxIdentificationAndUserIdNot(String taxIdentification, Long userId);


    boolean existsByUserIdAndSellerRequestStatusIn(Long userId,
                                                 Set<SellerRequestStatus> status
                      );

    Optional<SellerRequest> findFirstByUserIdOrderByIdDesc(Long userID);




    List<SellerRequest> findAllByUserIdAndSellerRequestStatusIn(Long userId, Set<SellerRequestStatus> statuses);

}
