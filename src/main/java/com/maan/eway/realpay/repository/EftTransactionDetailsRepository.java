package com.maan.eway.realpay.repository;

import com.maan.eway.realpay.model.EftTransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EftTransactionDetailsRepository extends JpaRepository<EftTransactionDetails, Long> {
    
    Optional<EftTransactionDetails> findByPaymentReference(String paymentReference);
    
    // Additional query methods can be added here if needed
}
