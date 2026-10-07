package com.maan.eway.realpay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EndorsementCheckDto {
    public Boolean endorsement;
    public String originalQuoteNo;

    public EndorsementCheckDto constructEndorsementCheck(Boolean endorsement, String originalQuoteNo){
        return EndorsementCheckDto.builder()
                .endorsement(endorsement).originalQuoteNo(originalQuoteNo).build();
    }
}
