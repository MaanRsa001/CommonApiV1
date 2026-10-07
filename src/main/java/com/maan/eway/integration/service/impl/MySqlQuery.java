package com.maan.eway.integration.service.impl;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON) // change to prototype
@PropertySource("classpath:oracle.properties")
public class MySqlQuery {
	
	@PersistenceContext
	private EntityManager em;
	
	private static Properties properties ;
	 
	private void setProperties() {
		if(properties==null) {
			try {
	        	InputStream  inputStream = getClass().getClassLoader().getResourceAsStream("oracle.properties");
	        	if (inputStream != null) {
	        		properties=new Properties();
	        		properties.load(inputStream);
				}
	        	
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
			
		 
		}
	}
	
	public String getQuery(String key) {
		setProperties();
		Object query=properties.get(key);
		if(query!=null)  return query.toString();
		else return null;
	}
	
	public List<Map<String,Object>> getListFromQuery(String key,List<String> params){
		String query = getQuery(key);
		if(query!=null) {
			 Query nativequery = em.createNativeQuery(query);		
			for(int i=0;i<params.size();i++) {
				nativequery.setParameter(i+1, params.get(i));
			}			
		 
			nativequery.unwrap(NativeQueryImpl.class).setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			List<Map<String,Object>> list = nativequery.getResultList();
			return list;
		}
		return null;
	}
	
	@Transactional
	public boolean insert(String query) {
		try {
			em.createNativeQuery(query).executeUpdate(); 
			
			return true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<Map<String, Object>> getListFromQueryWithoutKey(String query, List<String> params) {
		if(query!=null) {
			 Query nativequery = em.createNativeQuery(query);	
				if (params != null && params.size() >= 2) {
						  nativequery.setParameter("quoteNo", params.get(0));
				            nativequery.setParameter("sectionId", params.get(1));
					
				} else {
					for (int i = 0; i < params.size(); i++) {
						nativequery.setParameter(i + 1, params.get(i));
					}
				}

			nativequery.unwrap(NativeQueryImpl.class).setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			List<Map<String,Object>> list = nativequery.getResultList();
			return list;
		}
		return null;
	}
}