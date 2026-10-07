package com.maan.eway.bean;

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
public class InsuranceTypeMasterId implements Serializable{

	 private String     companyId ;

	 private Integer     productId ;
	 
	 private Integer     sectionId ;

	 private String     indsutryTypeId ;

}
