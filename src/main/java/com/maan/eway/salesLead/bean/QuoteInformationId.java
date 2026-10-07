package com.maan.eway.salesLead.bean;

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
public class QuoteInformationId implements Serializable {
	
	 private static final long serialVersionUID = 1L;	
    private String enquiryId;
    private String quoteNo;
    private Integer amendId;

    // Constructors, equals, and hashCode methods
}
