package com.maan.eway.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentMasterId implements Serializable {

	private static final long serialVersionUId = 1L;
	private Integer paymentMasterId;
	private String branchCode;
	private String companyId;
	private Integer amendId;
	private Integer productId;
	private String userType;
	private String subUserType;
	private String agencyCode;
}
