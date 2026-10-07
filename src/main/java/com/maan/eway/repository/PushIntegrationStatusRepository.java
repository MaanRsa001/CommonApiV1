package com.maan.eway.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.PushIntegrationStatus;

public interface PushIntegrationStatusRepository extends JpaRepository<PushIntegrationStatus, Long> {
	List<PushIntegrationStatus> findByStatus(String status);

	Optional<PushIntegrationStatus> findByquotationPolicyNo(String policyNo);

}