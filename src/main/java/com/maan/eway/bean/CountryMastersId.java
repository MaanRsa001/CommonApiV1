package com.maan.eway.bean;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class CountryMastersId implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name="COUNTRY_ID")
	private Long countryid;
	@Column(name="AMEND_ID")
	private Long amendid;
	@Column(name="BRANCH_CODE")
	private String branchCode;

}
