package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.YiInstallmentDetail;

public interface YiInstallmentDetailRepsoitory extends JpaRepository<YiInstallmentDetail, Long> {

	List<YiInstallmentDetail> findByQuotationPolicyNo(String policyNo);

}
