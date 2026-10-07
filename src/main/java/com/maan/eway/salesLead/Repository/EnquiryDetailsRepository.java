package com.maan.eway.salesLead.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.salesLead.bean.EnquiryDetails;
import com.maan.eway.salesLead.bean.EnquiryDetailsId;

@Repository
public interface EnquiryDetailsRepository extends JpaRepository<EnquiryDetails, EnquiryDetailsId> {

	EnquiryDetails findByEnquiryId(String enquiryId);

	List<EnquiryDetails> findByLeadId(String leadId);
}
