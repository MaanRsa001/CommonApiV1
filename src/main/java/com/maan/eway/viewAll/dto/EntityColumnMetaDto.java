package com.maan.eway.viewAll.dto;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EntityColumnMetaDto {

    private String datatype;

    private String code;

    private String codeDes;
}