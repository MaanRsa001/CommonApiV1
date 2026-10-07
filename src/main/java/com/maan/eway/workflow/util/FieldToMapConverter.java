package com.maan.eway.workflow.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.maan.eway.workflow.dto.JsonField;

public class FieldToMapConverter implements Function<JsonField,Map<String,Object>> {

	private Map<String, List<Map<String, Object>>> dynamicQuery=null;
	
	private int index;
	public FieldToMapConverter(Map<String, List<Map<String, Object>>> dynamicQuery) {
		super();
		this.dynamicQuery = dynamicQuery;
		index=0;
	}


	public FieldToMapConverter(Map<String, List<Map<String, Object>>> dynamicQuery, int index) {
		super();
		this.dynamicQuery = dynamicQuery;
		this.index=index;
	}


	@Override
	public Map<String, Object> apply(JsonField t) {
		try {
			
			if(t.getChildField()!=null && t.getChildField().size()>0) {
				System.out.println(t.getJsonKey()+":");
				Map<String, Object> collect=null;
				if("Yes".equals(t.getIsarray())) {
					
					if(dynamicQuery !=null && !dynamicQuery.isEmpty()) {
						
						List<Map<String, Object>> list = dynamicQuery.get(t.getQueryId().toPlainString());
						if(list.size()>0) {
							Map<String, Object> element=new HashMap<String, Object>(list.size());
							for (int i = 0; i < list.size(); i++) {
								FieldToMapConverter convt=new FieldToMapConverter(dynamicQuery,i);
								collect = t.getChildField().stream().map(convt).filter(d -> d != null).collect(Collectors.toMap(
										map -> map.keySet().iterator().next(), // Key mapper
										map -> map.values().iterator().next()  // Value mapper
										));


								if("Yes".equals(t.getIsarray())) {
									List<Map<String, Object>> objects=null;
									if(element.get(t.getJsonKey())==null) {  
										objects=new ArrayList<Map<String, Object>>(1);	
										element.put(t.getJsonKey(), objects);
									}else
										objects=(List<Map<String, Object>>) element.get(t.getJsonKey());
									objects.add(collect);					
								}else
									element.put(t.getJsonKey(), collect);

							}
							return element;
						}
					}
					
				}else {				
					collect = t.getChildField().stream().map(this).filter(d -> d != null)
							.filter(Objects::nonNull)
							.collect(Collectors.toMap(
	                    map -> map.keySet()==null?"":map.keySet().iterator().next(), // Key mapper
	                    map -> map.values()==null?"":map.values().iterator().next()  // Value mapper
	                ));
					
					Map<String, Object> element=new HashMap<String, Object>(1);
					if("Yes".equals(t.getIsarray())) {
						List<Map<String, Object>> objects=null;
						if(element.get(t.getJsonKey())==null) {  
							 objects=new ArrayList<Map<String, Object>>(1);	
							 element.put(t.getJsonKey(), objects);
						}else
							objects=(List<Map<String, Object>>) element.get(t.getJsonKey());
						objects.add(collect);					
					}else
						element.put(t.getJsonKey(), collect);
					return element;
				}
				//.collect(Collectors.toMap(obj -> obj.getKey(), Function.identity()));
				
			}else {
				Map<String, Object> element=new HashMap<String, Object>(1);
				
				Object value="";
				if(dynamicQuery !=null && !dynamicQuery.isEmpty()) {
					List<Map<String, Object>> list = dynamicQuery.get(t.getQueryId().toPlainString());
					if(list!=null && !list.isEmpty()) {
						Map<String, Object> map=list.get(this.index);


						if("Y".equals(t.getDefaultYn())) {
							value=t.getDefaultValue().toString();
						}else if("Y".equals(t.getDefaultYn()) && "Boolean".equals(t.getDatatype())) {
							value= Boolean.valueOf(t.getDefaultValue().toString());
						}else if(!"Y".equals(t.getDefaultYn()) && "Boolean".equals(t.getDatatype())) {
							value= Boolean.valueOf(map.get(t.getQueryAlias())==null?"":map.get(t.getQueryAlias()).toString());
						} else   if("Date".equals(t.getDatatype())  ) {
							value=map.get(t.getQueryAlias())==null?"":map.get(t.getQueryAlias()).toString();
							/*if(!"".equals(value)) {
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
							LocalDateTime dateTime = LocalDateTime.parse(value, formatter); 
							DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(t.getPattern());
							value = dateTime.format(outputFormatter);
						}*/
						}else if("Integer".equals(t.getDatatype()) ){
							try {
								value=map.get(t.getQueryAlias())==null?BigDecimal.ZERO:new BigDecimal(map.get(t.getQueryAlias()).toString());
							}catch (Exception e) {
								value=BigDecimal.ZERO;	
							}
						}else 
							value=map.get(t.getQueryAlias())==null?"":map.get(t.getQueryAlias()).toString();

					}
				}
				
				if("Yes".equals(t.getIsarray())) {
					element.put(t.getJsonKey(),new ArrayList<>());
				}else			
					element.put(t.getJsonKey(),value);
				System.out.println("\t"+t.getJsonKey());
				return element;
				
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}

/*	@Override
	public void accept(JsonField t) {
		try {
			
			if(t.getChildField()!=null && t.getChildField().size()>0) {
				t.getChildField().forEach(this);
				System.out.println("Parent :"+t.getJsonKey());
			}else {
				
				System.out.println(t.getJsonKey());
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}		
	}*/

}
