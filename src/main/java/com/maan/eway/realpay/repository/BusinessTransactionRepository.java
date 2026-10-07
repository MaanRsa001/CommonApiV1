package com.maan.eway.realpay.repository;

import com.maan.eway.realpay.model.BusinessTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface BusinessTransactionRepository extends JpaRepository<BusinessTransaction, Long> {
    
    List<BusinessTransaction> findByTransactionType(String transactionType);
    
    List<BusinessTransaction> findByRelatedInstalmentRef(String relatedInstalmentRef);
    
    List<BusinessTransaction> findByTransactionDateBetween(Date startDate, Date endDate);
    
    @Query("SELECT SUM(b.amount) FROM BusinessTransaction b WHERE b.transactionType = :type AND b.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalAmountByTypeAndDateRange(
        @Param("type") String transactionType,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );
}
