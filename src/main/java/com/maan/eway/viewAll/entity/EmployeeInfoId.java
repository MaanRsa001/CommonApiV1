package com.maan.eway.viewAll.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeInfoId implements Serializable {
	 private static final long serialVersionUID = 1L;
//	private String requestReferenceNo;
    private String passportNo;
    private String customerId;
    private String customerReferenceNo;
    private String loginId;
    private Long empId;
}
