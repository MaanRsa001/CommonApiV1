package com.maan.eway.jasper.res;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CommonSectionRes {

    private String sectionId;
    private String sectionName;
    
    private String status;     
    
    private Double premium;  
    private BigDecimal sumInsured; 
}
