package com.maan.eway.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class EwayLobMasterId implements Serializable {

    private static final long serialVersionUID = 1L;
	
	private String compnayId;
	
	private String lobId;
	
	private String lobCode;

}
