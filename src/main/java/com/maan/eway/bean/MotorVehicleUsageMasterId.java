//StateMasterId
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
public class MotorVehicleUsageMasterId implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private Integer vehicleUsageId;
	
	private String sectionId;
	
	private Integer amendId;

	private String companyId;

	private String branchCode;


}