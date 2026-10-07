
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
public class MotorMakeModelMasterId implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private Integer makeId;
	private Integer modelId;
	private Integer bodyId;
	private String companyId;
	private Integer amendId;
	private String branchCode;

}