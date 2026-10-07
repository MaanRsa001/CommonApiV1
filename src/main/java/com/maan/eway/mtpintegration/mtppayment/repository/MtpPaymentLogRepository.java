package com.maan.eway.mtpintegration.mtppayment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.mtpintegration.mtppayment.entity.MtpPaymentLog;

public interface MtpPaymentLogRepository extends JpaRepository<MtpPaymentLog, Long> {
   
    Optional<MtpPaymentLog> findTopByTrackingIdOrderByEntryDateDesc(Long trackingId);

    List<MtpPaymentLog> findByTrackingIdOrderByEntryDateAsc(Long trackingId);
}
