package com.maan.eway.mtpintegration.mtppayment.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.maan.eway.mtpintegration.mtppayment.entity.MtpPaymentTracking;

public interface MtpPaymentTrackingRepository extends JpaRepository<MtpPaymentTracking, Long> {
    
	Optional<MtpPaymentTracking> findByQuoteNoAndStatusIn(String quoteNo, List<String> statuses);

    @Query("SELECT t FROM MtpPaymentTracking t WHERE t.status = 'PENDING' AND t.nextAttemptDue <= :now")
    List<MtpPaymentTracking> findDueForProcessing(@Param("now") Date now);

    List<MtpPaymentTracking> findByCompanyIdAndStatusInOrderByEntryDateDesc(
        String companyId, List<String> statuses);
}
