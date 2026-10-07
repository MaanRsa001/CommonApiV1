package com.maan.eway.bean;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class IntegrationMappingDetailsMasterId {
	
	private Integer companyId;
	
	private Integer sectionId;
	
	private Integer productId;
	
	private Integer policyTypeId;
	
	private Integer amendId;
	
	private Long integrationId;
}
