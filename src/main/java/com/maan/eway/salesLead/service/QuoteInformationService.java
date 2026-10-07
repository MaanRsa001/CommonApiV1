package com.maan.eway.salesLead.service;

import java.util.List;

import com.maan.eway.salesLead.req.GetQuotationDetailsReq;
import com.maan.eway.salesLead.req.QuoteInformationDTO;

public interface QuoteInformationService {

	public QuoteInformationDTO saveOrUpdate(QuoteInformationDTO dto);

	public List<QuoteInformationDTO> getQuotationDetails(GetQuotationDetailsReq req);

	public void delete(String enquiryId, String quoteNo);

	

}
