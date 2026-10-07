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
import com.maan.eway.realpay.model.Contract;


@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    
    
    Optional<Contract> findByContractNumber(String contractNumber);
    
    
    Optional<Contract> findByClientNumberAndContractNumber(String clientNumber, String contractNumber);
    
    
    List<Contract> findByClientAccount(ClientAccountDetails clientAccount);
    
    
    @Query("SELECT c FROM Contract c WHERE c.clientAccount = :clientAccount AND c.quoteNo = :quoteNo")
    Optional<Contract> findByClientAccountAndQuoteNo(
            @Param("clientAccount") ClientAccountDetails clientAccount, 
            @Param("quoteNo") String quoteNo);
    
    
    List<Contract> findByClientNumber(String clientNumber);
    
    
    List<Contract> findByQuoteNo(String quoteNo);

    @Query(value = "SELECT * FROM contracts WHERE quote_no IN (:quoteNos)", nativeQuery = true)
    List<Contract> findByQuoteNos(@Param("quoteNos") List<String> quoteNos);

    
    void deleteByClientAccount(ClientAccountDetails clientAccount);
    
    
    List<Contract> findByContractTypeAndOriginalContractNumber(
            String contractType, 
            String originalContractNumber);
    
    
    List<Contract> findByOriginalContractNumber(String originalContractNumber);
    
    
    @Query("SELECT c FROM Contract c WHERE c.originalContractNumber = :originalContractNumber "
            + "AND c.contractType = :contractType")
    List<Contract> findByOriginalContractNumberAndContractType(
            @Param("originalContractNumber") String originalContractNumber,
            @Param("contractType") String contractType);
    
    
    @Query("SELECT c FROM Contract c WHERE c.originalContractNumber = :originalContractNumber "
            + "AND c.contractType = 'ARREAR'")
    List<Contract> findArrearContractsByOriginalContractNumber(
            @Param("originalContractNumber") String originalContractNumber);
    
    
    @Query("SELECT c FROM Contract c WHERE c.contractType = 'ORIGINAL' OR c.contractType IS NULL")
    List<Contract> findOriginalContracts();
    
    
    @Query("SELECT c FROM Contract c WHERE c.contractType = 'ARREAR'")
    List<Contract> findArrearContracts();
    
    
    @Query("SELECT c FROM Contract c WHERE c.contractNumber LIKE '%ARR%'")
    List<Contract> findContractsByArrearPattern();

    @Modifying
    @Transactional
    @Query("UPDATE Contract c " +
            "SET c.quoteNo = :newQuoteNo, " +
            "c.prevQuoteNo = :prevQuoteNo, " +
            "c.endorsementReason = :endorsementReason, " +
            "c.prevPolicyNo = :prevPolicyNo " +
            "WHERE c.quoteNo = :oldQuoteNo")
    int updateContractsByQuoteNo(
            @Param("oldQuoteNo") String oldQuoteNo,
            @Param("newQuoteNo") String newQuoteNo,
            @Param("prevQuoteNo") String prevQuoteNo,
            @Param("endorsementReason") String endorsementReason,
            @Param("prevPolicyNo") String prevPolicyNo
    );

    @Query("SELECT c FROM Contract c WHERE c.quoteNo = :quoteNo AND c.contractType = 'ORIGINAL'")
    Contract findOriginalContract(@Param("quoteNo") String quoteNo);
}
