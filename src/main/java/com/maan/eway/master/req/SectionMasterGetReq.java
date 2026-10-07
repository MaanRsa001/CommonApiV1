package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SectionMasterGetReq {
	  @JsonProperty("SectionId")
      private Integer     sectionId     ;
    
	  @JsonFormat(pattern ="dd/MM/yyyy")
	  @JsonProperty("EffectiveDateStart")
	  private Date effectiveDateStart;
}
