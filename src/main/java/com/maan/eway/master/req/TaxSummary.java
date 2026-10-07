package com.maan.eway.master.req;

import java.math.BigDecimal;

 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TaxSummary {
	
	 private Integer taxId;
	 private BigDecimal taxAmount;

}
