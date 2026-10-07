package com.maan.eway.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PtMotorDetailsId  implements Serializable{

	private static final long serialVersionUID=1L;
	
    private Integer id;
    private String     companyId ;
    private String     productId ;
    private String    sectionId ;
    private String     branchCode ;

   
}
