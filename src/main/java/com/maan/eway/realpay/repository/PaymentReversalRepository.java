package com.maan.eway.realpay.repository;

import com.maan.eway.realpay.model.PaymentReversal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface PaymentReversalRepository extends JpaRepository<PaymentReversal, Long> {
    
    List<PaymentReversal> findByClientNumber(String clientNumber);
    
    List<PaymentReversal> findByOriginalInstalmentRef(String originalInstalmentRef);
    
    List<PaymentReversal> findByStatus(String status);
    
    @Query("SELECT SUM(p.reversalAmount) FROM PaymentReversal p WHERE p.clientNumber = :clientNumber AND p.reversalDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalReversalAmountByClientAndDateRange(
        @Param("clientNumber") String clientNumber,
        @Param("startDate") Date startDate, 
        @Param("endDate") Date endDate
    );
}
