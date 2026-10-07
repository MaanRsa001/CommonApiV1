package com.maan.eway.finanaceIntegration.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NarrationResponse {

    private String narrationDesc;
    private Map<String, String> replaceText;
}
