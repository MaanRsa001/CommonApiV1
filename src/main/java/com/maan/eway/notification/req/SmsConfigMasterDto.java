package com.maan.eway.notification.req;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class SmsConfigMasterDto implements Serializable {

    private static final long serialVersionUID = 1L;

    //----------------------------------------------------------------------
    // ENTITY PRIMARY KEY 
    //----------------------------------------------------------------------
	@JsonProperty("Insid")
    private BigDecimal insId        ;

    //----------------------------------------------------------------------
    // ENTITY DATA FIELDS 
    //----------------------------------------------------------------------    
	@JsonProperty("Smspartyurl")
    private String     smsPartyUrl  ;
	@JsonProperty("Smsusername")
    private String     smsUserName  ;
	@JsonProperty("Smsuserpass")
    private String     smsUserPass  ;
	@JsonProperty("Status")
    private String     status       ;
	@JsonProperty("Secureyn")
    private String     secureYn     ;
	@JsonProperty("Senderid")
    private String     senderid     ;

      
	  
	  
}