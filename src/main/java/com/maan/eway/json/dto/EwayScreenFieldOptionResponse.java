package com.maan.eway.json.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EwayScreenFieldOptionResponse {
    private Long code;
    private String codeDesc;
}
