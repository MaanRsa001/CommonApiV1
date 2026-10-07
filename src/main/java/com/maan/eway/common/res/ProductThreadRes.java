package com.maan.eway.common.res;

import java.util.List;
import java.util.concurrent.Callable;

import lombok.Data;

@Data
public class ProductThreadRes {

	private List<Callable<Object>> productQueue ;
	private List<Callable<Object>> coverQueue ;
	private Integer threadCount ;
}
