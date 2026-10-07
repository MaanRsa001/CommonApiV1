package com.maan.eway.salesLead.Repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.salesLead.bean.IpclmsFileUploadDetails;

public interface IpclmsFileUploadDetailsRepository extends JpaRepository<IpclmsFileUploadDetails, BigDecimal>{

}
