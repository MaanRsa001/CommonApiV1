package com.maan.eway.finanaceIntegration.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectMappingRes {
    private String objectName;
    private List<InputFieldRes> fields;
}

