package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.RenewPremiaPolicyRaw;
import com.maan.eway.bean.RenewPremiaPolicyRawId;

 
 
public interface RenewPremiaPolicyRawRepository  extends JpaRepository<RenewPremiaPolicyRaw,RenewPremiaPolicyRawId > , JpaSpecificationExecutor<RenewPremiaPolicyRaw> {



	


	

}
