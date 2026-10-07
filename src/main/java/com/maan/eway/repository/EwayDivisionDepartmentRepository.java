package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.maan.eway.bean.EwayDivisionDepartment;
import com.maan.eway.bean.EwayDivisionDepartmentId;

import java.util.List;

public interface EwayDivisionDepartmentRepository
		extends JpaRepository<EwayDivisionDepartment, EwayDivisionDepartmentId>,
		JpaSpecificationExecutor<EwayDivisionDepartment> {

	@Query(value = "SELECT * FROM eway_division_department e " + "WHERE e.company_id = :companyId "
			+ "AND FIND_IN_SET(:productId, e.product_id)", nativeQuery = true)
	EwayDivisionDepartment findByCompnayIdAndProductId(@Param("companyId") String companyId,
			@Param("productId") String productId);

    List<EwayDivisionDepartment> findDistinctByCompnayId(String companyId);

}
