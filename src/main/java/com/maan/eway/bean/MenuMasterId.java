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
public class MenuMasterId  implements Serializable{
    private static final long serialVersionUID = 1L;
 //--- ENTITY KEY ATTRIBUTES 
	
  private Integer  menuId ;
	
  private String   usertype ;
  
  private String   companyId ;
	
}
