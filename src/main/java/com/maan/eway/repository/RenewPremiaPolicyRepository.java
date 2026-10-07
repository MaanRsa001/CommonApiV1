package com.maan.eway.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.RenewPremiaPolicy;
import com.maan.eway.bean.RenewPremiaPolicyId;

public interface RenewPremiaPolicyRepository
		extends JpaRepository<RenewPremiaPolicy, RenewPremiaPolicyId>, JpaSpecificationExecutor<RenewPremiaPolicy> {

	List<RenewPremiaPolicy> findByCurrentStatusAndTransactionId(String string, String tranId);

	List<RenewPremiaPolicy> findAllByInsuredMobile(String insuredMobile);

	List<RenewPremiaPolicy> findByPolSrcCode(String coreAppBrokerCode);

	List<RenewPremiaPolicy> findByPolicyNumber(String policyNumber);

	@Modifying
	@Transactional
	@Query("UPDATE RenewPremiaPolicy r SET r.currentStatus = 'RR' WHERE r.expiryDate < :now")
	int updateExpiredPolicies(@Param("now") Timestamp oneMonthAgo); 

}
