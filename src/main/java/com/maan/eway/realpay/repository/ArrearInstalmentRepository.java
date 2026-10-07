package com.maan.eway.realpay.repository;

import com.maan.eway.realpay.model.ArrearInstalment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ArrearInstalmentRepository extends JpaRepository<ArrearInstalment, Long> {
    
    List<ArrearInstalment> findByClientNumber(String clientNumber);
    
    List<ArrearInstalment> findByClientNumberAndStatus(String clientNumber, String status);
    
    List<ArrearInstalment> findByOriginalInstalmentRef(String originalInstalmentRef);
    
    List<ArrearInstalment> findByContractNumber(String contractNumber);
    
    @Query("SELECT SUM(a.arrearAmount) FROM ArrearInstalment a WHERE a.clientNumber = :clientNumber AND a.status = :status")
    BigDecimal getTotalArrearAmountByClientAndStatus(@Param("clientNumber") String clientNumber, @Param("status") String status);
    
    @Query("SELECT a FROM ArrearInstalment a WHERE a.clientNumber = :clientNumber AND a.status IN :statuses")
    List<ArrearInstalment> findByClientNumberAndStatusIn(@Param("clientNumber") String clientNumber, @Param("statuses") List<String> statuses);
}
