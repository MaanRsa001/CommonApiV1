package com.maan.eway.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.maan.eway.bean.ProductSequenceMaster;
import com.maan.eway.bean.ProductSequenceMasterId;

import jakarta.persistence.LockModeType;

@Repository
public interface ProductSequenceMasterRepository extends JpaRepository<ProductSequenceMaster, ProductSequenceMasterId>,
		JpaSpecificationExecutor<ProductSequenceMaster> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			    SELECT p FROM ProductSequenceMaster p
			    WHERE p.status = 'Y'
			      AND p.companyId = :companyId
			      AND p.branchCode = '99999'
			      AND p.type = :type
			      AND (p.productId = :productId OR p.productId = 99999)
			      AND p.effectiveDateStart = (
			          SELECT MAX(p2.effectiveDateStart)
			          FROM ProductSequenceMaster p2
			          WHERE p2.sequenceId = p.sequenceId
			            AND p2.companyId = p.companyId
			            AND p2.branchCode = p.branchCode
			            AND p2.productId = p.productId
			            AND p2.effectiveDateStart <= :today
			      )
			      AND p.effectiveDateEnd = (
			          SELECT MAX(p3.effectiveDateEnd)
			          FROM ProductSequenceMaster p3
			          WHERE p3.sequenceId = p.sequenceId
			            AND p3.companyId = p.companyId
			            AND p3.branchCode = p.branchCode
			            AND p3.productId = p.productId
			            AND p3.effectiveDateEnd >= :todayEnd
			      )
			    ORDER BY p.productId ASC
			""")
	List<ProductSequenceMaster> findForUpdate(@Param("companyId") String companyId, @Param("type") String type,
			@Param("productId") String productId, @Param("today") Date today, @Param("todayEnd") Date todayEnd);
}
