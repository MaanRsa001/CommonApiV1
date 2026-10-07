/**
 * @author : Ashok Kumar S 
 * @since  : 09-01-2025
 */
package com.maan.eway.workstream.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class WorkflowFactorRateRequestDetailPK {
	
    private String requestReferenceNo ;

    private Integer vehicleId ;
    
    private Integer locationId ;	    

    private String companyId ;

    private Integer productId ;

    private Integer sectionId ;
    
    private Integer coverId ;

    private String subCoverYn ;

    private Integer subCoverId ;

    private Integer discLoadId ;

    private Integer taxId ;

    private Integer discountCoverId;
     
    private BigDecimal endtCount ;
    
    private Long proposalId;
    
    private Long workflowId;

}
