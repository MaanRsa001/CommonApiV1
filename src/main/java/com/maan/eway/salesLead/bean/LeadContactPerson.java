package com.maan.eway.salesLead.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity
@DynamicUpdate
@Data
@Table(name = "lead_contact_person")
public class LeadContactPerson  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "SNO", length = 20, nullable = false)
	private BigDecimal sno;
	
	@Column(name = "LEAD_ID", length = 20, nullable = false)
	private String leadId;
	
	@Column(name = "CONTACT_TYPE")
	private String contactType;
	
	@Column(name = "CONTACT_PERSON_NAME")
	private String contactPersonName;
	
	@Column(name = "EMAIL_ADDRESS")
	private String emailAddress;
	
	@Column(name = "MOBILE")
	private String mobile;
	
	@Column(name = "PHONE")
	private String phone;
	
	@Column(name = "DESIGNATION")
	private String designation;
	
	@Column(name = "REMARKS")
	private String Remarks;
	
}
