package com.maan.eway.coinsurance.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CoInsuranceDetailsId implements Serializable {

    private static final long serialVersionUID = 1L;
	
	 private String quoteNo;
	 
	 private Integer slNo;

}
