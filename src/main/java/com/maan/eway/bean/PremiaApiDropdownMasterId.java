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
public class PremiaApiDropdownMasterId implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer companyId;

	private Integer sectionId;

	private Integer productId;

	private Integer amendId;

	private String itemType;

	private Integer itemId;

}
