package com.maan.eway.upgrade.criteria;

import lombok.Data;

@Data
public class JoinCriteria {
	
	private String columnName;
	
	private Class toTableName;
	
	private String toColumnName;
	
}
