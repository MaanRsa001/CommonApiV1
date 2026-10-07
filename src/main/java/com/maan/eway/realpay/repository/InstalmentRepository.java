package com.maan.eway.realpay.repository;

import java.math.BigDecimal;
import java.util.Date;
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
import com.maan.eway.realpay.model.Instalment;

@Repository
public interface InstalmentRepository extends JpaRepository<Instalment, Long> {
    
    boolean existsByQuoteNoAndProductIdAndNoOfInstalmentAndInstalmentActionDateAndClientNumber(
        String quoteNo, 
        String productId, 
        Long noOfInstalment, 
        Date  instalmentActionDate, 
        String clientNumber
    );
    
    List<Instalment> findByClientAccount(ClientAccountDetails clientAccount);

    List<Instalment> findByClientAccountAndNoOfInstalmentOrderByNoOfInstalmentAsc(
        ClientAccountDetails clientAccount, 
        Long noOfInstalment
    );
    
    Optional<Instalment> findByContractAndContractSequence(Contract contract, Integer contractSequence);

    List<Instalment> findByQuoteNo(String quoteNo);
    
    Optional<Instalment> findByContractAndInstalmentReferenceNumber(Contract contract, String instalmentReferenceNumber);

    boolean existsByContractAndNoOfInstalment(Contract contract, Long noOfInstalment);

    List<Instalment> findByContract(Contract contract);
    
    List<Instalment> findByClientAccountAndQuoteNo(ClientAccountDetails clientAccount, String quoteNo);
    
    @Query("SELECT i FROM Instalment i WHERE i.contract = :contract ORDER BY i.noOfInstalment ASC")
    List<Instalment> findByContractOrderByNoOfInstalmentAsc(@Param("contract") Contract contract);
    
    void deleteByContract(Contract contract);
    
    @Query("SELECT i FROM Instalment i WHERE i.contract = :contract AND i.noOfInstalment > :minInstallment")
    List<Instalment> findByContractAndNoOfInstalmentGreaterThan(@Param("contract") Contract contract, 
                                                               @Param("minInstallment") Long minInstallment);
	
    Instalment findByInstalmentReferenceNumber(String instalmentReferenceNumber);
    
    List<Instalment> findByClientNumber(String clientNumber);
    
    List<Instalment> findByContractNumber(String contractNumber);
    
    @Query("SELECT i FROM Instalment i WHERE i.clientNumber = :clientNumber AND i.instalmentActionDate > :currentDate AND i.instalmentStatus NOT IN ('S', 'C')")
    List<Instalment> findFutureByClientNumber(@Param("clientNumber") String clientNumber, @Param("currentDate") Date currentDate);
    
    @Query("SELECT i FROM Instalment i WHERE i.clientNumber = :clientNumber ORDER BY i.instalmentActionDate DESC LIMIT :limit")
    List<Instalment> findRecentByClientNumberOrderByActionDate(@Param("clientNumber") String clientNumber, @Param("limit") int limit);
    
    @Query("SELECT i FROM Instalment i WHERE i.instalmentActionDate BETWEEN :startDate AND :endDate")
    List<Instalment> findByInstalmentActionDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    @Query("SELECT i FROM Instalment i WHERE i.clientNumber = :clientNumber AND i.instalmentActionDate > :afterDate AND i.instalmentStatus = 'C'")
    List<Instalment> findCancelledByClientNumberAfterDate(@Param("clientNumber") String clientNumber, @Param("afterDate") Date afterDate);
    
    List<Instalment> findByInstalmentStatus(String instalmentStatus);
    
    List<Instalment> findByResponseCode(String responseCode);
    
    @Query("SELECT COUNT(i) FROM Instalment i WHERE i.clientNumber = :clientNumber AND i.instalmentStatus = :status")
    long countByClientNumberAndStatus(@Param("clientNumber") String clientNumber, @Param("status") String status);

	List<Instalment> findByContractNumberAndInstalmentStatusIn(String contractNumber, List<String> asList); //neW
    
    @Query("SELECT MAX(i.noOfInstalment) FROM Instalment i WHERE i.contractNumber = :contractNumber")
    Optional<Integer> findMaxInstalmentNumberByContractNumber(@Param("contractNumber") String contractNumber);
    
    @Query("SELECT i FROM Instalment i WHERE i.contractNumber LIKE '%ARR%' OR i.instalmentReferenceNumber LIKE '%ARR%'")
    List<Instalment> findArrearInstallments();
    
    @Query("SELECT i FROM Instalment i WHERE i.message LIKE '%ARREAR_RECOVERY%' OR i.message LIKE '%NEW_MANDATE%' OR i.message LIKE '%DISPUTED_RECOVERY%'")
    List<Instalment> findInstallmentsByArrearMessage();
    
    @Query("SELECT i FROM Instalment i JOIN i.contract c WHERE c.contractType = 'ARREAR'")
    List<Instalment> findInstallmentsByArrearContract();
    
    @Query("SELECT i FROM Instalment i JOIN i.contract c WHERE c.contractType = 'ORIGINAL' OR c.contractType IS NULL")
    List<Instalment> findInstallmentsByOriginalContract();
    
    List<Instalment> findByContractNumberAndInstalmentStatusOrderByNoOfInstalmentAsc(String contractNumber, String instalmentStatus);
    
    List<Instalment> findByContractNumberAndInstalmentStatus(String contractNumber, String instalmentStatus);

    @Query("SELECT i FROM Instalment i WHERE (i.contract.originalContractNumber = :originalContractNumber OR i.contractNumber = :originalContractNumber) AND i.instalmentStatus in ('EFT_PENDING','DOUBLE_DEBIT_PENDING','F') ORDER BY i.noOfInstalment ASC")
    List<Instalment> findFailedInstallmentsByOriginalContractAndDirect(@Param("originalContractNumber") String originalContractNumber);

    @Query("SELECT i FROM Instalment i WHERE i.instalmentReferenceNumber = :refNumber AND i.webhookUpdated = 'Y'")
    Optional<Instalment> findWebhookProcessedInstalment(@Param("refNumber") String refNumber);
    
    @Query("SELECT i FROM Instalment i WHERE i.contract.id = :contractId AND i.noOfInstalment = :noOfInstalment")
    Optional<Instalment> findByContractIdAndNoOfInstalment(@Param("contractId") Long contractId, @Param("noOfInstalment") Long noOfInstalment);

    Instalment findByQuoteNoAndNoOfInstalmentAndContractSequenceIsNotNull(String quoteNo, Integer noOfInstalment);

    @Modifying
    @Transactional
    @Query("UPDATE Instalment i " +
            "SET i.quoteNo = :newQuoteNo, " +
            "i.prevQuoteNo = :prevQuoteNo, " +
            "i.endorsementReason = :endorsementReason, " +
            "i.prevPolicyNo = :prevPolicyNo, " +
            "i.instalmentAmount = :amount " +
            "WHERE i.quoteNo = :oldQuoteNo " +
            "AND (i.noOfInstalment = :noOfInstalment OR i.arrearMonth = :noOfInstalment)")
    int updateInstalmentForEndorsement(
            @Param("oldQuoteNo") String oldQuoteNo,
            @Param("newQuoteNo") String newQuoteNo,
            @Param("prevQuoteNo") String prevQuoteNo,
            @Param("endorsementReason") String endorsementReason,
            @Param("prevPolicyNo") String prevPolicyNo,
            @Param("amount") BigDecimal amount,
            @Param("noOfInstalment") Long noOfInstalment
    );

    List<Instalment> findByQuoteNoAndInstalmentStatus(String quoteNo, String instalmentStatus);

    Instalment findByQuoteNoAndNoOfInstalmentAndContractNumber(
            String quoteNo,
            Long noOfInstalment,
            String contract
    );

    Instalment findByQuoteNoAndArrearMonth(String quoteNo,Long arrearMonth);
}

