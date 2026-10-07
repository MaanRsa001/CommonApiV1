package com.maan.eway.salesLead.bean;

import java.io.Serializable;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class EnquiryDetailsId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String enquiryId;
	
	private String leadId;
	
	private Integer amendId;

}
