package com.maan.eway.payment.process.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.PaymentProcessDetail;
import com.maan.eway.bean.PaymentProcessDetailId;

public interface PaymentProcessDetailRepository extends JpaRepository<PaymentProcessDetail, PaymentProcessDetailId>{

	Optional<PaymentProcessDetail> findByQuoteNoAndPaymentId(String quoteNo, String paymentId);

}
