package com.maan.eway.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AviationInfoId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer productId;
    private Integer locationId;
    private Integer sectionId;
    private Integer riskId;
    private String requestReferenceNo;
}