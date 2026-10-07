package com.maan.eway.bean;

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
public class OneTimeTableDetailsId implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	 //--- ENTITY KEY ATTRIBUTES 
    private Integer    parentId ;
    
    private Integer    amendId ;
    
    private String     companyId ;

}
