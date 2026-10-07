package com.maan.eway.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.maan.eway.bean.RenewVehicleInfo;

@Repository
public interface RenewVehicleInfoRepo extends JpaRepository<RenewVehicleInfo, String> ,JpaSpecificationExecutor<RenewVehicleInfo>{

	 RenewVehicleInfo findByPolicyNoAndRiskId(String policyNo, String riskId);

	 RenewVehicleInfo findByPolicyNo(String policyNo);
}
