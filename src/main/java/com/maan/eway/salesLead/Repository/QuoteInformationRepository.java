package com.maan.eway.salesLead.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.salesLead.bean.QuoteInformationId;
import com.maan.eway.salesLead.bean.QuoteInformationIpclms;

@Repository
public interface QuoteInformationRepository extends JpaRepository<QuoteInformationIpclms, QuoteInformationId> {
	 List<QuoteInformationIpclms> findByEnquiryIdAndQuoteNo(String enquiryId, String quoteNo);
	 List<QuoteInformationIpclms> findByEnquiryId(String enquiryId);
	List<QuoteInformationIpclms> findByEnquiryIdAndQuoteStatus(String enquiryId, String string);
}
