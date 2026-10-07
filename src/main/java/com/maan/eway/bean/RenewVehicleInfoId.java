package com.maan.eway.bean;

import java.io.Serializable;

import jakarta.persistence.Column;
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
public class RenewVehicleInfoId implements Serializable {
	
	 private static final long serialVersionUID = 1L;
	
    @Column(name = "POLICY_NO", nullable = false, length = 100)
    private String policyNo;

    @Column(name = "RISK_ID", length = 20)
    private String riskId;

}
