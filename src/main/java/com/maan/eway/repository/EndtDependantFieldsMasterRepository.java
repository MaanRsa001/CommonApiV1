package com.maan.eway.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.EndtDependantFieldMaster;
import com.maan.eway.bean.EndtDependantFieldsMasterId;

public interface EndtDependantFieldsMasterRepository extends JpaRepository<EndtDependantFieldMaster,EndtDependantFieldsMasterId >, JpaSpecificationExecutor<EndtDependantFieldMaster>  {

	List<EndtDependantFieldMaster> findByCompanyIdAndProductIdAndStatusAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndDependantFieldIdOrderByAmendIdDesc(
			String companyId, String string, String string2, Date date, Date date2, Integer valueOf);

	
}
