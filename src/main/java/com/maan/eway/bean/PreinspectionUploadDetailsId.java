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
public class PreinspectionUploadDetailsId implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
private Long sNo;
	
	//private String quoteNo;
	
	private Long companyId;
	
	private String registrationNo;
	
	private String chassisNo;

}
