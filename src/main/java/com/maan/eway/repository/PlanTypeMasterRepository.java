package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.maan.eway.bean.PlanTypeMaster;
import com.maan.eway.bean.PlanTypeMasterId;

@Repository

public interface PlanTypeMasterRepository extends JpaRepository<PlanTypeMaster, PlanTypeMasterId> {

	List<PlanTypeMaster> findByCompanyIdAndProductId(String companyId, String productId);

	@Query(value = """

			SELECT p.*

			FROM plan_type_master p

			WHERE (p.company_id, p.product_id, p.plan_id, p.amend_id) IN (

			    SELECT company_id, product_id, plan_id, MAX(amend_id)

			    FROM plan_type_master

			    WHERE company_id = :companyId

			      AND product_id = :productId

			    GROUP BY company_id, product_id, plan_id

			)

			""", nativeQuery = true)

	List<PlanTypeMaster> findLatestPlansByCompanyAndProduct(String companyId, String productId);

	List<PlanTypeMaster> findByPlanTypeIdAndCompanyIdAndProductId(Integer planTypeId, String companyId,
			String productId);

}
