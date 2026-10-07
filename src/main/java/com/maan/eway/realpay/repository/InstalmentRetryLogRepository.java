package com.maan.eway.realpay.repository;

import com.maan.eway.realpay.model.InstalmentRetryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InstalmentRetryLogRepository extends JpaRepository<InstalmentRetryLog, Long> {
    
    List<InstalmentRetryLog> findByInstalmentReferenceNumber(String instalmentReferenceNumber);
    
    @Query("SELECT COUNT(r) FROM InstalmentRetryLog r WHERE r.instalmentReferenceNumber = :instalmentRef")
    int countByInstalmentReferenceNumber(@Param("instalmentRef") String instalmentReferenceNumber);
    
    List<InstalmentRetryLog> findByClientNumberAndStatus(String clientNumber, String status);
    
    @Query("SELECT r FROM InstalmentRetryLog r WHERE r.instalmentReferenceNumber = :instalmentRef ORDER BY r.createdDate DESC")
    List<InstalmentRetryLog> findLatestByInstalmentReference(@Param("instalmentRef") String instalmentReferenceNumber);
}
