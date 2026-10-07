package com.maan.eway.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Composite Primary Key class for "WnmClaimCustomerDetails"
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClaimCustomerDetailsId implements Serializable{
    private static final long serialVersionUID = 1L;

    private String customerReferenceNo;

    private String companyId;

}
