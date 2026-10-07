package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.maan.eway.bean.MotorMakeMaster;
import com.maan.eway.bean.MotorMakeMasterId;

public interface MotorMakeMasterRepository extends JpaRepository<MotorMakeMaster, MotorMakeMasterId> , JpaSpecificationExecutor<MotorMakeMaster>{

	List<MotorMakeMaster> findByCompanyIdAndCoreAppCodeOrderByAmendIdDesc(String companyId, String makeId);

	@Query(nativeQuery=true,value="SELECT make_id FROM `eway_motor_make_master` WHERE company_id='100002' AND make_name_en=?1 AND STATUS='y' AND \r\n"
			+ "amend_id=(SELECT MAX(amend_id) FROM eway_motor_make_master WHERE company_id='100002' AND make_name_en=?1 AND STATUS='y' )")
	List<Integer> findMakeIdWithMaxAmendId(String makeNameEn);




}
