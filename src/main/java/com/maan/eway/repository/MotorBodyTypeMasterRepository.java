package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.maan.eway.bean.MotorBodyTypeMaster;
import com.maan.eway.bean.MotorBodyTypeMasterId;

public interface MotorBodyTypeMasterRepository extends JpaRepository<MotorBodyTypeMaster, MotorBodyTypeMasterId> , JpaSpecificationExecutor<MotorBodyTypeMaster>{

	List<MotorBodyTypeMaster> findByCoreAppCodeAndBranchCodeAndCompanyIdOrderByAmendIdDesc(String bodyType,
			String string, String companyId);

	@Query(nativeQuery=true,value="SELECT body_id FROM `eway_motor_bodytype_master` WHERE company_id='100002' AND body_name_en=?1 AND STATUS='y' AND  section_id=10 AND\r\n"
			+ "amend_id=(SELECT MAX(amend_id) FROM eway_motor_bodytype_master WHERE company_id='100002' AND body_name_en=?1 AND STATUS='y' AND  section_id=10)")
	List<Integer> findBodyIdWithMaxAmendId(String bodyNameEn);
	


}
