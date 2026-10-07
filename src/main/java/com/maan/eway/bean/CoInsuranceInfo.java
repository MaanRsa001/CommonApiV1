package com.maan.eway.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@Builder
@Entity
@NoArgsConstructor
@IdClass(CoInsuranceInfoId.class)
@Table(name="co_insurance_info")
public class CoInsuranceInfo implements Serializable   {
	
	@Id
	@Column(name="SNO", nullable=false)
	private int sno ;
	
	

	@Column(name="QUOTE_NO ", nullable=false)
	private String quoteno ;
	
	
	@Column(name="INSURANCE_COMPANY_ID ", nullable=false)
	private int insurancecompanyid ;


	@Column(name="INSURANCE_COMPANY_NAME ", nullable=false)
	private String insurancecompanyname ;

	@Column(name="SHARED_PERCENTAGE ", nullable=false)
	private BigDecimal sharedpercentage ;
	
	
	@Column(name="LEADER_PARTICIPANT ", nullable=false)
	private String leaderparticipant ;
	
	@Id
	@Column(name="AMEND_ID ", nullable=false)
	private int amendid ;
	
	
	@Id
	@Column(name="PRODUCT_ID ", nullable=false)
	private int productid ;
	
	
	@Id
	@Column(name="REQUEST_REFERENCE_NO ", nullable=false)
	private String requestreferenceno ;
	
	@Column(name="EFFECTIVE_DATE_START", nullable=false)
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date effectivedatestart ;
	
	
	
	@Column(name="EFFECTIVE_DATE_END", nullable=false)
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date effectivedateend ;

	@Column(name="STATUS ", nullable=false)
	private String status ;
	
}
