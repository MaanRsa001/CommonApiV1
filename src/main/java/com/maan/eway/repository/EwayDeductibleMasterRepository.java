package com.maan.eway.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.bean.EwayDeductibleMaster;

@Repository
public interface EwayDeductibleMasterRepository extends JpaRepository<EwayDeductibleMaster, Integer> {

	List<EwayDeductibleMaster> findAllByCompanyIdAndBranchCodeAndProductIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatusAndSectionId(
			String companyId, String string, Integer productId, Date startOfDay, Date endOfDay, String string2,
		Integer sectionId);
}