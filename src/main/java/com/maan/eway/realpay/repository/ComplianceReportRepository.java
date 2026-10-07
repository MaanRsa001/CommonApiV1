package com.maan.eway.realpay.repository;

import com.maan.eway.realpay.model.ComplianceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface ComplianceReportRepository extends JpaRepository<ComplianceReport, Long> {
    
    List<ComplianceReport> findByReportMonth(Date reportMonth);
    
    List<ComplianceReport> findByOverallCompliant(Boolean overallCompliant);
    
    @Query("SELECT c FROM ComplianceReport c WHERE c.reportMonth BETWEEN :startDate AND :endDate ORDER BY c.reportMonth DESC")
    List<ComplianceReport> findByReportMonthBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    @Query("SELECT c FROM ComplianceReport c WHERE c.overallCompliant = false ORDER BY c.reportMonth DESC")
    List<ComplianceReport> findNonCompliantReports();
    
    @Query("SELECT c FROM ComplianceReport c ORDER BY c.reportMonth DESC")
    List<ComplianceReport> findAllOrderByReportMonthDesc();
}
