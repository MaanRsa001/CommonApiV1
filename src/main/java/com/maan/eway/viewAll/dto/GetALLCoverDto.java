package com.maan.eway.viewAll.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GetALLCoverDto implements Serializable {

    private static final long serialVersionUID = 1L;
	
	private Integer companyId;
    private Integer productId;
    private Integer sectionId;
    private Integer coverId;

}
