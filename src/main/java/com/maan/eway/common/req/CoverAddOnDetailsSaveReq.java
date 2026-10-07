package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CoverAddOnDetailsSaveReq {

	@JsonProperty("AddonReference")
    private String     addonReference ;
	@JsonProperty("AddonDesc")
    private String     addonDesc    ;
	@JsonProperty("AddonAmount")
    private String     addonAmount  ;
	@JsonProperty("AddonPremiumRate")
    private String     addonPremiumRate ;
	
	@JsonProperty("AddonPremiumExcludingTax")
    private String     addonPremiumExcludingTax ;
	
	@JsonProperty("AddonPremiumExcludingTaxEquivalent")
    private String     addonPremiumExcludingTaxEquivalent ;
	
	@JsonProperty("AddonPremiumIncludingTax")
    private String     addonPremiumIncludingTax ;
	
}
