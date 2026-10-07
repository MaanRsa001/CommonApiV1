package com.maan.eway.salesLead.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.salesLead.bean.LeadInformation;


public interface LeadInformationRepository extends JpaRepository<LeadInformation, String>{

}
