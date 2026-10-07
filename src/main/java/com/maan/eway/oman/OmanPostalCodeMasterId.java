package com.maan.eway.oman;

import java.io.Serializable;

import com.maan.eway.bean.BankMasterId;

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
public class OmanPostalCodeMasterId implements Serializable{
	
	private static final long serialVersionUID = 1L;

    //--- ENTITY KEY ATTRIBUTES 
	private String     countryId ;
	private Long     governorateId ;
	private Long     wilayatId ;
	private Long     locationId ;
	private Long     companyId ;

}
