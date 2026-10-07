package com.maan.eway.ui.service.impl;

import com.google.gson.Gson;
import com.maan.eway.ui.request.Field;

import jakarta.persistence.AttributeConverter;

public class FieldConverter implements AttributeConverter<Field, String> {

	 

	@Override
	public Field convertToEntityAttribute(String dbData) {
		try {
			if(dbData!=null && dbData.length()>0){
				Gson g=new Gson();
				Field f = g.fromJson(dbData, Field.class);
				return f;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String convertToDatabaseColumn(Field f) {
		try {
			if(f!=null) {
				Gson g=new Gson();
				return g.toJson(f);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
