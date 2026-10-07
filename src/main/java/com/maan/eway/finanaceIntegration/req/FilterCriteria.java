package com.maan.eway.finanaceIntegration.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterCriteria {
    private String key;
    private String operation;
    private Object value;
}