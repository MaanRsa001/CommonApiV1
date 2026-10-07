package com.maan.eway.realpay.repository;

import com.maan.eway.realpay.model.CustomerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface CustomerTransactionRepository extends JpaRepository<CustomerTransaction, Long> {
    
    List<CustomerTransaction> findByClientNumber(String clientNumber);
    
    List<CustomerTransaction> findByTransactionType(String transactionType);
    
    List<CustomerTransaction> findByClientNumberAndTransactionType(String clientNumber, String transactionType);
    
    @Query("SELECT SUM(c.amount) FROM CustomerTransaction c WHERE c.clientNumber = :clientNumber AND c.transactionType = :type")
    BigDecimal getTotalAmountByClientAndType(@Param("clientNumber") String clientNumber, @Param("type") String transactionType);
}
