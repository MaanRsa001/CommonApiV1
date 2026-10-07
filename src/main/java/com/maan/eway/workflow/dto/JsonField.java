package com.maan.eway.workflow.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
@Data
public class JsonField implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BigDecimal companyId ;

	private BigDecimal productId ;
	
	private Integer sectionId ;

	private Integer coverId ;

	private BigDecimal keyId ;

	private String     jsonKey ;

	private String     isHeader ;

	private BigDecimal     headerKeyid ;

	private String     isarray ;

	private String     datatype ;

	private String     pattern ;

	private String     defaultYn ;

	private String     defaultValue ;

	private String     status ;
	
	private BigDecimal queryId ;
	 
	private String queryCol ;
	private String queryAlias;
	private Integer orderBy; 
	private String amount; 
	private List<JsonField> childField;
	

	 
}
