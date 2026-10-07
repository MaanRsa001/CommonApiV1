package com.maan.eway.bean;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Builder
public class ClaimIntimationId implements Serializable {
	
private static final long serialVersionUID = 1L;
	
    private String companyId;
    private String claimRefNo;
    private String policyNo;

}
