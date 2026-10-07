
package com.maan.eway.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MotorBodyTypeMasterId implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private Integer bodyId;
	private Integer sectionId;
	private Integer amendId;
	private String companyId;
	private String branchCode;


}