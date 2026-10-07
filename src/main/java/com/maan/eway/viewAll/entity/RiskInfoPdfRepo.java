package com.maan.eway.viewAll.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface RiskInfoPdfRepo extends JpaRepository<RiskInfoPdf, RiskInfoPdfId> {

	RiskInfoPdf findByQuoteNoAndCompanyidAndProductId(String quoteNo, String companyId, Integer productId);

	RiskInfoPdf findByQuoteNoAndCompanyidAndProductIdAndBrokerQuotationyn(String quoteNo, String companyId,
			Integer productId, String brokerYn);

	int deleteByQuoteNoAndBrokerQuotationyn(String quoteNo, String brokerQouotation);

	int deleteByQuoteNo(String quoteNo);

}
