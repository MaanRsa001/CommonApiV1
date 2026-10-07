package com.maan.eway.workflow.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.maan.eway.bean.ApiIntegMaster;
import com.maan.eway.bean.FieldQueryTablequery;
import com.maan.eway.bean.FlowFieldDetails;
import com.maan.eway.bean.PremiaApiDropdownMaster;
import com.maan.eway.bean.PremiaTransactionLog;
import com.maan.eway.repository.PremiaTransactionLogRepository;
import com.maan.eway.upgrade.criteria.CriteriaService;
import com.maan.eway.upgrade.criteria.SpecCriteria;
import com.maan.eway.viewAll.entity.RiskFieldFlow;
import com.maan.eway.viewAll.service.impl.RiskFieldFromTuple;
import com.maan.eway.workflow.dto.JsonField;
import com.maan.eway.workflow.dto.WorkEngine;

import jakarta.persistence.Tuple;
@Component
public class WorkFlowFactorUtil {
	@Autowired
	protected CriteriaService crservice;
	@Autowired
	private PremiaTransactionLogRepository transRepo;
	
	protected SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy")  ;

	@Cacheable(cacheNames = {"FlowFieldData"},keyGenerator  = "FlowFieldDataKeyGen",value = "FlowFieldData" )
	public List<JsonField> getFlowFieldData(WorkEngine engine) {
		try {
			String search="companyId:"+engine.getCompanyId()+";productId:"+engine.getProductId()+";status:{Y,R};integType:"+engine.getIntegType()+";";
			SpecCriteria criteria = crservice.createCriteria(FlowFieldDetails.class, search, "keyId");
			List<Tuple> result = crservice.getResult(criteria, 0, 50);
			FieldFromTuple t=new FieldFromTuple();			
			List<JsonField> data = result.parallelStream().map(t).filter(d-> d!=null).collect(Collectors.toList());
			return data;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

	@Cacheable(cacheNames = {"DistinctQueryId"},keyGenerator  = "DistinctQueryIdKeyGen",value = "DistinctQueryId" )
	public String getDistinctQueryId(BigDecimal id) {
		try {
			String search="queryId:"+id.toPlainString()+";";
			SpecCriteria criteria = crservice.createCriteria(FieldQueryTablequery.class, search, "queryId");
			List<Tuple> result = crservice.getResult(criteria, 0, 50);	
			return result.get(0).get("sqlQuery").toString();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Cacheable(cacheNames = {"AzentoURL"},keyGenerator  = "AzentoURLKeyGen",value = "AzentoURL" )
	public String getAzentoURL(WorkEngine engine) {
		try {
			String search4 = "companyId:" + engine.getCompanyId() + ";productId:" + engine.getProductId()+";status:{Y,R};apiType:AUTH";
			SpecCriteria commonCriteria = crservice.createCriteria(ApiIntegMaster.class, search4, "productId");
			List<Tuple> commonResult = crservice.getResult(commonCriteria, 0, 50);				
			String url=commonResult.get(0).get("apiUrl").toString();
			return url;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Cacheable(cacheNames = {"AzentoToken"},keyGenerator  = "AzentoTokenKeyGen",value = "AzentoToken" )
	public String getAzentoToken(WorkEngine engine) {
		String azentoToken="";
		try {
			
			String url = this.getAzentoURL(engine);
			
			TrustManager[] trustAllCerts = new TrustManager[]{
					new X509TrustManager() {
						public java.security.cert.X509Certificate[] getAcceptedIssuers() {return null;}
						public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType){}
						public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType){}
					}
			};

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

				@Override
				public boolean verify(String hostname, SSLSession session) {
					// TODO Auto-generated method stub
					return true;
				}
			});

			PremiaTransactionLog log=new PremiaTransactionLog();
			try {
				Gson gson = new GsonBuilder() .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()) .create();

				log.setEntryDate(new Date());
				log.setQuoteNo(StringUtils.isBlank(engine.getQuoteNo())?engine.getRequestReferenceNo():engine.getQuoteNo());
				log.setRequestTime(LocalDateTime.now());
				log.setGenerateReq(engine.toString());
				log.setEndpoint(url);

				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
				headers.setContentType(MediaType.APPLICATION_JSON);
				//headers.set("Authorization",authHeader);
				Map<String,Object> req=new HashMap<String,Object>();
				req.put("username", "azentio");
				req.put("password", "azentio");
				log.setRequest(gson.toJson(req));		
				HttpEntity<Object> entityReq = new HttpEntity<Object>(req, headers);
				ResponseEntity<Map> response = restTemplate.postForEntity(url, entityReq, Map.class);
				log.setResponse(gson.toJson(response.getBody()));
				log.setResponseTime(LocalDateTime.now());
				log.setStatus(response.getBody()!=null ?"Y":"F");
				azentoToken=(String)response.getBody().get("jwt");
			}catch (Exception e) {
				log.setResponseTime(LocalDateTime.now());
				log.setStatus("F");
				log.setErrorMessage(e.getLocalizedMessage());
				e.printStackTrace();
			}finally {
				transRepo.save(log);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return azentoToken;
	}

	public List<Tuple> getPremiaApiDropdownMaster(String companyId, String string) {
		try {
			String search4 = "companyId:" + companyId + ";itemType:"+string;
			SpecCriteria commonCriteria = crservice.createCriteria(PremiaApiDropdownMaster.class, search4, "itemId");			
			List<Tuple> commonResult = crservice.getResult(commonCriteria, 0, 50);				
						return commonResult;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<JsonField> getFlowFieldDataPdf(WorkEngine engine) {
		try {
//			String search=  "companyId:" + engine.getCompanyId() +
//	                ";status:{Y,R}" +
//	                ";integType:" + engine.getIntegType() +
//	                ";coverId:{88888," + engine.getCoverId() + "}" +
//	                ";productId:{88888," + engine.getProductId() + "}" +
//	                ";sectionId:{88888," + Integer.valueOf(engine.getSectionId()) + "};"
//	                ;
			String search = "companyId:{88888," + engine.getCompanyId() + "}" +
	                ";status:{Y,R}" +
	                ";integType:" + engine.getIntegType() +
	                ";coverId:{88888," + engine.getCoverId() + "}" +
	                ";productId:{88888," + engine.getProductId() + "}" +
	                ";sectionId:{88888," + Integer.valueOf(engine.getSectionId()) + "};";
			SpecCriteria criteria = crservice.createCriteria(RiskFieldFlow.class, search, "keyId");
			List<Tuple> result = crservice.getResult(criteria, 0, 50);
			RiskFieldFromTuple t=new RiskFieldFromTuple();			
			List<JsonField> data = result.parallelStream().map(t).filter(d-> d!=null).collect(Collectors.toList());
			return data;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public List<JsonField> getFlowFieldDataPdfCover(WorkEngine engine) {
		try {
			String search=  "companyId:" + engine.getCompanyId() +
	                ";status:{Y,R}" +
	                ";integType:" + engine.getIntegType() +
	                ";coverId:{99999," + engine.getCoverId() + "}" +
	                ";productId:{99999," + engine.getProductId() + "}" +
	                ";sectionId:{99999," + Integer.valueOf(engine.getSectionId()) + "};"
	                ;
//			String search = "companyId:" + engine.getCompanyId()
//	        + ";status:{Y,R}"
//	        + ";integType:" + engine.getIntegType()
//	        + ";coverId:" + engine.getCoverId()
//	        + ";productId:" + engine.getProductId()
//	        + ";sectionId:" + Integer.valueOf(engine.getSectionId())
//	        + ";";
			SpecCriteria criteria = crservice.createCriteria(RiskFieldFlow.class, search, "keyId");
			List<Tuple> result = crservice.getResult(criteria, 0, 50);
			RiskFieldFromTuple t=new RiskFieldFromTuple();			
			List<JsonField> data = result.parallelStream().map(t).filter(d-> d!=null).collect(Collectors.toList());
			return data;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

}
