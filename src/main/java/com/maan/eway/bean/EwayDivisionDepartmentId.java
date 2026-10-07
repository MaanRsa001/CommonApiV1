package com.maan.eway.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class EwayDivisionDepartmentId implements Serializable {

    private static final long serialVersionUID = 1L;
	
	private String compnayId;
	
	private String departmentId;
	
	private String departmentCode;

}