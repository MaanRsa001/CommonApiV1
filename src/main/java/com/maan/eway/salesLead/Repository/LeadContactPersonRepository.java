package com.maan.eway.salesLead.Repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.salesLead.bean.LeadContactPerson;

import jakarta.transaction.Transactional;

@Transactional
public interface LeadContactPersonRepository extends JpaRepository<LeadContactPerson, BigDecimal>{

	List<LeadContactPerson> findByLeadId(String leadId);
	
	void deleteByLeadId(String leadId);

}
