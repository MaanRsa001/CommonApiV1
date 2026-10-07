package com.maan.eway.realpay.repository;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.maan.eway.realpay.model.ClientAccountDetails;

@Repository
public interface ClientAccountDetailsRepository extends JpaRepository<ClientAccountDetails, Long> {
    
    Optional<ClientAccountDetails> findByQuoteNo(String quoteNo);
    
    Optional<ClientAccountDetails> findByMerchantReference(String merchantReference);
    
    @Query(value = "SELECT * FROM client_account_details WHERE client_number = :clientNumber ORDER BY client_account_id DESC LIMIT 1", nativeQuery = true)
    Optional<ClientAccountDetails> findLatestByClientNumber(@Param("clientNumber") String clientNumber);
    
    List<ClientAccountDetails> findByStatus(String status);
    
    Optional<ClientAccountDetails> findByIdNumber(String idNumber);
    
    Optional<ClientAccountDetails> findByAccountNumberAndBankCode(Long accountNumber, Integer bankCode);
    
    @Query("SELECT c FROM ClientAccountDetails c WHERE c.clientNumber = :clientNumber")
    List<ClientAccountDetails> findByClientNumber(@Param("clientNumber") String clientNumber);
    
    boolean existsByClientNumber(String clientNumber);
    
    Optional<ClientAccountDetails> findByClientNumberAndQuoteNo(
            String clientNumber, String quoteNo);

    @Modifying
    @Transactional
    @Query("UPDATE ClientAccountDetails c " +
            "SET c.quoteNo = :newQuoteNo, " +
            "c.prevQuoteNo = :prevQuoteNo, " +
            "c.endorsementReason = :endorsementReason, " +
            "c.prevPolicyNo = :prevPolicyNo " +
            "WHERE c.clientNumber = :clientNumber " +
            "AND c.quoteNo = :oldQuoteNo")
    int updateClientAccountDetails(
            @Param("clientNumber") String clientNumber,
            @Param("oldQuoteNo") String oldQuoteNo,
            @Param("newQuoteNo") String newQuoteNo,
            @Param("prevQuoteNo") String prevQuoteNo,
            @Param("endorsementReason") String endorsementReason,
            @Param("prevPolicyNo") String prevPolicyNo
    );



}