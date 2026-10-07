package com.maan.eway.viewAll.entity;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiskFieldFlowRepo extends JpaRepository<RiskFieldFlow, RiskFieldFlowId>{

	List<RiskFieldFlow> findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByKeyIdDesc(Integer companyId,
			BigDecimal productId, Integer sectionId, Integer coverId);

	List<RiskFieldFlow> findByCompanyIdAndProductIdAndSectionIdAndCoverIdInOrderByKeyIdDesc(Integer companyId,
			BigDecimal productId, Integer sectionId, List<Integer> coverId);

	

	List<RiskFieldFlow> findByCompanyIdAndProductIdAndSectionIdAndCoverIdInOrderByKeyIdDesc(Integer companyId,
			Integer productId, Integer sectionId, List<Integer> coverId);

	List<RiskFieldFlow> findByCompanyIdAndProductIdInAndSectionIdInAndCoverIdInOrderByKeyIdDesc(Integer companyId,
			List<Integer> product, List<Integer> sectionId, List<Integer> coverId);

	List<RiskFieldFlow> findByCompanyIdAndProductIdAndSectionIdOrderByKeyIdDesc(Integer companyId,
			Integer productId, Integer sectionId);
}
