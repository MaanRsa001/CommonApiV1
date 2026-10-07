package com.maan.eway.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.ProductEmployeeDetails;
import com.maan.eway.bean.ProductEmployeeDetailsId;

import jakarta.transaction.Transactional;

public interface ProductEmployeesDetailsRepository extends JpaRepository<ProductEmployeeDetails, ProductEmployeeDetailsId>, JpaSpecificationExecutor<ProductEmployeeDetails> {

	Long countByQuoteNoAndRiskId(String quoteNo, Integer valueOf);

	@Transactional
	void deleteByQuoteNoAndRiskId(String quoteNo, Integer valueOf);

	List<ProductEmployeeDetails> findByQuoteNo(String quoteNo);

	List<ProductEmployeeDetails> findByQuoteNoAndRiskIdOrderByEmployeeIdAsc(String quoteNo, Integer valueOf);

	Long countByQuoteNoAndRiskIdAndEmployeeId(String quoteNo, Integer valueOf, Long valueOf2);

	@Transactional
	void deleteByQuoteNoAndRiskIdAndEmployeeId(String quoteNo, Integer valueOf, Long valueOf2);

	Long countByQuoteNo(String newQuoteNo);

	List<ProductEmployeeDetails> findByRequestReferenceNo(String requestReferenceNo);

	List<ProductEmployeeDetails> findByQuoteNoAndSectionIdAndProductIdOrderByRiskIdAsc(String quoteNo, String string,
			Integer productId);

	List<ProductEmployeeDetails> findByQuoteNoAndSectionIdAndStatusNot(String quoteNo, String sectionId, String string);

	Long countByQuoteNoNotAndEndtCountAndOriginalPolicyNo(String quoteNo, BigDecimal bigDecimal,
			String originalPolicyNo);

	@Transactional
	void deleteByQuoteNoNotAndEndtCountAndOriginalPolicyNo(String quoteNo, BigDecimal bigDecimal,
			String originalPolicyNo);

	List<ProductEmployeeDetails> findByQuoteNoAndSectionId(String string, String sectionId);

	List<ProductEmployeeDetails> findByRequestReferenceNoAndSectionIdAndLocationId(String string, String sectionId,
			Integer locationId);

	long deleteByRequestReferenceNo(String refNo);


}
