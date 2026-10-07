package com.maan.eway.mtpintegration.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.mtpintegration.entity.MtpPaymentTransaction;

public interface MtpPaymentTransactionRepository extends JpaRepository<MtpPaymentTransaction, Long> {
    /*Optional<MtpPaymentTransaction> findTopByPaymentRequestIdOrderByIdDesc(String paymentRequestId);
    
    
    Optional<MtpPaymentTransaction> findByNumberPlateAndAssessmentTypeAndMsisdnAndPartnerIdentifier(
            String numberPlate,
            String assessmentType,
            String msisdn,
            String partnerIdentifier);

}*/
	Optional<MtpPaymentTransaction> findTopByPaymentRequestIdOrderByIdDesc(String paymentRequestId);

    Optional<MtpPaymentTransaction> findTopByStickerReferenceAndNumberPlateOrderByIdDesc(String stickerReference, String numberPlate);

    Optional<MtpPaymentTransaction> findByNumberPlateAndAssessmentTypeAndMsisdnAndPartnerIdentifier(
            String numberPlate,
            String assessmentType,
            String msisdn,
            String partnerIdentifier);
}
