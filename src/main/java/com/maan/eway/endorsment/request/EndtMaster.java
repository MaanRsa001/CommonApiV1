/**
 * 
 */
package com.maan.eway.endorsment.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EndtMaster  {

	@JsonProperty("EndorsementTypes") 
    List<EndorsementType> endorsementTypes;
 
}
