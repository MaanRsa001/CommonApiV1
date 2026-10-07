package com.maan.eway.realpay.repository;

import com.maan.eway.realpay.model.DisputeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface DisputeLogRepository extends JpaRepository<DisputeLog, Long> {
    
    List<DisputeLog> findByClientNumber(String clientNumber);
    
    List<DisputeLog> findByStatus(String status);
    
    List<DisputeLog> findByInstalmentReferenceNumber(String instalmentReferenceNumber);
    
    @Query("SELECT d FROM DisputeLog d WHERE d.mandateDeadline < :currentDate AND d.status = 'MANDATE_REQUIRED'")
    List<DisputeLog> findOverdueDisputesByMandateDeadline(@Param("currentDate") Date currentDate);
    
    List<DisputeLog> findByDisputeDateBetween(Date startDate, Date endDate);
}
