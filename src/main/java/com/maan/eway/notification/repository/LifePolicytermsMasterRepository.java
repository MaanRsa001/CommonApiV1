package com.maan.eway.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.LifePolicytermsMaster;
import com.maan.eway.bean.LifePolicytermsMasterId;

public interface LifePolicytermsMasterRepository  extends JpaRepository<LifePolicytermsMaster,LifePolicytermsMasterId > , JpaSpecificationExecutor<LifePolicytermsMaster> {

	List<LifePolicytermsMaster> findByPolicyTermsAndProductIdAndSectionIdAndCompanyId(Integer valueOf, Integer valueOf2,
			Integer valueOf3, String companyId);

}
