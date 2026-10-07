package com.maan.eway.integration.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.ValuationCompanyMaster;
import com.maan.eway.bean.ValuationIntegration;
import com.maan.eway.integration.req.ValuationDetailsReq;
import com.maan.eway.integration.req.ValuationReq;
import com.maan.eway.integration.req.ValuationStatusReq;
import com.maan.eway.integration.res.PremiaResponse;
import com.maan.eway.integration.res.ValuationCreateRes;
import com.maan.eway.integration.res.ValuationQuoteDetailsRes;
import com.maan.eway.integration.res.ValuationStatusDetailsRes;
import com.maan.eway.integration.res.ValuationTokenRes;
import com.maan.eway.notification.bean.NotifTransactionDetails;
import com.maan.eway.notification.repository.NotifTransactionDetailsRepository;
import com.maan.eway.notification.service.NotificationService;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.ValuationIntegrationRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class SolvitValuation  {
	@PersistenceContext
	private EntityManager em;
//	@Autowired
//	private ValuationServiceImpl valuationImpl;
	@Autowired
	private ValuationIntegrationRepository valuationIntegrationRepository;
	
	@Autowired
	private NotificationService notificationService;
	@Autowired 
	private NotifTransactionDetailsRepository notifTrans;
	
	@Autowired 
	private InsuranceCompanyMasterRepository companyRepo;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	public PremiaResponse pushValuation(ValuationReq req) {
		PremiaResponse resp=new PremiaResponse();
		try {
		List<ValuationQuoteDetailsRes>list=getQuoteDetails(req.getQuoteNo());
		if(!CollectionUtils.isEmpty(list)) {
			for (ValuationQuoteDetailsRes quote : list) {
				ValuationIntegration data=new ValuationIntegration();
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				dozerMapper.map(quote, data);
				data.setType("2");
				data.setSitetype("2");
				data.setPaymenttype("1");
				data.setAutoAssign("0");
				data.setEntryDate(new Date());
				data.setStatus("Pending");
				data.setLastName("");
				data.setLongitude("");
				data.setLatitude("");
				data.setValCompanyId("1");
				data.setValCompanyName("Solvit");
				valuationIntegrationRepository.saveAndFlush(data);
			}
			
		}
		List<ValuationIntegration>vlist=valuationIntegrationRepository.findByQuoteNoOrderByVehicleId(req.getQuoteNo());
		if(!CollectionUtils.isEmpty(vlist)) {
			List<ValuationCompanyMaster>llist=getValuationCompanyDetails("1", req.getBranchCode(), req.getCompanyId());
			if(!CollectionUtils.isEmpty(llist)) {
			String token=getAccessTokern(llist.get(0));
			for (ValuationIntegration vdata : vlist) {
				
				Map<String ,Object> request=new HashMap<String, Object>();
				request.put("customerMobile", vdata.getCustomerMobile());
				request.put("vehicleRegNo", vdata.getVehicleRegNo());
				request.put("firstName", vdata.getFirstName());
				request.put("lastName", vdata.getLastName());
				request.put("email", StringUtils.isBlank(vdata.getEmail())?"":vdata.getEmail());
				request.put("type", vdata.getType());
				request.put("siteType", vdata.getSitetype());
				request.put("insuranceCompanyRequestId", vdata.getCompanyId());
				request.put("paymentType", vdata.getPaymenttype());
				request.put("autoAssign", vdata.getAutoAssign());
				request.put("latitude", "");
				request.put("longitude", "");
				ResponseEntity<ValuationCreateRes> response=null;
				String res="",recordId="";
				try {
					sslverification();
					RestTemplate restTemplate = new RestTemplate();
					HttpHeaders headers = new HttpHeaders();
					headers.set("Authorization",token);
					headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
					headers.setContentType(MediaType.APPLICATION_JSON);
					HttpEntity<Object> entityReq = new HttpEntity<>(request, headers);
					System.out.println(entityReq.getBody());
					response = restTemplate.postForEntity(llist.get(0).getCreateApi(), entityReq, ValuationCreateRes.class);
					System.out.println(response.getBody());
					res=response.getBody().toString();
					recordId=response.getBody().getRequestId();
				}catch (HttpClientErrorException e) {
		            	System.out.println("Resource is locked. Please try again later.");
		            	 Map<String, String> headers = e.getResponseHeaders().toSingleValueMap();
		                 System.out.println("Response Headers:");
		                 for (Map.Entry<String, String> entry : headers.entrySet()) {
		                     System.out.println(entry.getKey() + ": " + entry.getValue());
		                 }
		                 res=headers.get("Message");
		        } catch (Exception e) {
					e.printStackTrace();
					res=e.getLocalizedMessage();
					resp.setResponse("Valuation Request Not Created Successfully");
				}
				
				vdata.setCreateRequest(request.toString());
				vdata.setCreateResponse(res);
				vdata.setRecordId(recordId);
				valuationIntegrationRepository.saveAndFlush(vdata);
				resp.setResponse("Valuation Request Created Successfully");
				sendSMSMail(vdata,"VALUATION_NOTIFICATION");
			}
			
		}
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return resp;
	}


	

	public String getAccessTokern(ValuationCompanyMaster list) {
		String token="";
		try {
			if(list!=null) {
			Map<String ,Object> request=new HashMap<String, Object>();
			request.put("email", list.getAuthUserName());
			request.put("password", list.getAuthPassword());
			sslverification();
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> entityReq = new HttpEntity<>(request, headers);
			System.out.println(entityReq.getBody());
			ResponseEntity<ValuationTokenRes> response = restTemplate.postForEntity(list.getAuthApi(), entityReq, ValuationTokenRes.class);
			System.out.println(response.getBody());
			token=response.getBody().getToken();
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return token;
		
	}
	
	public PremiaResponse getStatus(ValuationStatusReq req) {
		PremiaResponse resp=new PremiaResponse();
		try {
			List<ValuationCompanyMaster>list=getValuationCompanyDetails(req.getValCompanyId(), req.getBranchCode(), req.getCompanyId());
			if(!CollectionUtils.isEmpty(list)) {
				List<ValuationIntegration>vlist=valuationIntegrationRepository.findByVehicleRegNoOrderByVehicleId(req.getVehicleRegNo());
				if(!CollectionUtils.isEmpty(vlist)) {
					String token=getAccessTokern(list.get(0));
					for (ValuationIntegration vdata : vlist) {
						Map<String ,Object> request=new HashMap<String, Object>();
						request.put("vehicleRegNo", req.getVehicleRegNo());
						ResponseEntity<List<ValuationStatusDetailsRes>> response=null;
						String res="",status="Pending",masterRecordId="";
						try {
							sslverification();
							RestTemplate restTemplate = new RestTemplate();
							HttpHeaders headers = new HttpHeaders();
							headers.set("Authorization",token);
							headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
							headers.setContentType(MediaType.APPLICATION_JSON);
							HttpEntity<Object> entityReq = new HttpEntity<>(request, headers);
							System.out.println(entityReq.getBody());
							response = restTemplate.exchange(list.get(0).getStatusApi(),  HttpMethod.POST,entityReq,new ParameterizedTypeReference<List<ValuationStatusDetailsRes>>() {});
							System.out.println(response.getBody());
							res=response.getBody().toString();
							status=response.getBody().get(0).getStatus();
							masterRecordId=response.getBody().get(0).getRequestMasterId();
						}catch (HttpClientErrorException e) {
			            	System.out.println("Resource is locked. Please try again later.");
			            	 Map<String, String> headers = e.getResponseHeaders().toSingleValueMap();
			                 System.out.println("Response Headers:");
			                 for (Map.Entry<String, String> entry : headers.entrySet()) {
			                     System.out.println(entry.getKey() + ": " + entry.getValue());
			                 }
			                 res=headers.get("Message");
						}catch (Exception e) {
							e.printStackTrace();
							res=e.getLocalizedMessage();
							resp.setResponse("Failed");
						}
						
						vdata.setMasterrecordId(masterRecordId);
						vdata.setStatusrequest(request.toString());
						vdata.setStatusresponse(res);
						vdata.setStatus(status);
						valuationIntegrationRepository.saveAndFlush(vdata);
						resp.setResponse(status);
						if("Processed".equalsIgnoreCase(status)) {
							ValuationDetailsReq dreq=new ValuationDetailsReq();
							dreq.setBranchCode(req.getBranchCode());
							dreq.setCompanyId(req.getCompanyId());
							dreq.setRecordId(masterRecordId);
							dreq.setValCompanyId(req.getValCompanyId());
							getDetails(dreq);
						}
					}
					}
			}
			}catch (Exception e) {
				e.printStackTrace();
			}
		return resp;
	}

	
	public PremiaResponse getDetails(ValuationDetailsReq req) {
		PremiaResponse resp=new PremiaResponse();
		try {
			List<ValuationCompanyMaster>list=getValuationCompanyDetails(req.getValCompanyId(), req.getBranchCode(), req.getCompanyId());
			if(!CollectionUtils.isEmpty(list)) {
				List<ValuationIntegration>vlist=valuationIntegrationRepository.findByMasterrecordIdOrderByVehicleId(req.getRecordId());
				if(!CollectionUtils.isEmpty(vlist)) {
					String token=getAccessTokern(list.get(0));
					for (ValuationIntegration vdata : vlist) {
						Map<String ,Object> request=new HashMap<String, Object>();
						request.put("id", req.getRecordId());
						ResponseEntity<Map> response=null;
						String res="";
						try {
							sslverification();
							RestTemplate restTemplate = new RestTemplate();
							HttpHeaders headers = new HttpHeaders();
							headers.set("Authorization",token);
							headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
							headers.setContentType(MediaType.APPLICATION_JSON);
							HttpEntity<Object> entityReq = new HttpEntity<>(request, headers);
							System.out.println(entityReq.getBody());
							response = restTemplate.exchange(list.get(0).getGetDetailApi(),  HttpMethod.POST,entityReq,Map.class);
							System.out.println(response.getBody());
							res=new Gson().toJson(response.getBody());
						}catch (HttpClientErrorException e) {
			            	System.out.println("Resource is locked. Please try again later.");
			            	 Map<String, String> headers = e.getResponseHeaders().toSingleValueMap();
			                 System.out.println("Response Headers:");
			                 for (Map.Entry<String, String> entry : headers.entrySet()) {
			                     System.out.println(entry.getKey() + ": " + entry.getValue());
			                 }
			                 res=headers.get("Message");
						}catch (Exception e) {
							e.printStackTrace();
							res=e.getLocalizedMessage();
							resp.setResponse("Failed");
						}
						
							vdata.setIdrequest(request.toString());
							vdata.setIdresponse(res);
							if(response.getBody()!=null) {
							Map<String,Object> map = objectMapper.readValue(res, new TypeReference<Map<String,Object>>(){});
							String valsuminsured=map.get("force_sales_value")==null?"":map.get("force_sales_value").toString();
							double diffsuminsured=vdata.getSumInsured()-Double.parseDouble(valsuminsured.replaceAll(",", "")) ;
							vdata.setExceptionSumInsured(Double.parseDouble(valsuminsured));
							if(Math.abs(diffsuminsured)>1000) {
								vdata.setExceptionStatus("E");
								vdata.setExceptionRemarks("SumInsured Difference is High");
								sendSMSMail(vdata,"VALUATION_STATUS");
							}
							}
							valuationIntegrationRepository.saveAndFlush(vdata);
							resp.setResponse(res);
					}
				}
			}
			}catch (Exception e) {
				e.printStackTrace();
			}
		return resp;
	}
	public List<ValuationCompanyMaster> getValuationCompanyDetails(String Id,String branchCode,String companyId) {
		try {

			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			//Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ValuationCompanyMaster> query = cb.createQuery(ValuationCompanyMaster.class);
			List<ValuationCompanyMaster> list = new ArrayList<ValuationCompanyMaster>();

			//Find All
			Root<ValuationCompanyMaster> b = query.from(ValuationCompanyMaster.class);

			//select
			query.select(b);

			//Order by
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("valCompanyCode")));

			//Effective Date start max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ValuationCompanyMaster> ocpm1 = effectiveDate.from(ValuationCompanyMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(b.get("valCompanyCode"), ocpm1.get("valCompanyCode"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a4 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));

			effectiveDate.where(a1,a2,a3,a4);

			//Effective Date end max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ValuationCompanyMaster> ocpm2 = effectiveDate2.from(ValuationCompanyMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate c1 = cb.equal(b.get("valCompanyCode"), ocpm2.get("valCompanyCode"));
			Predicate c2 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate c3 = cb.equal(ocpm2.get("companyId"), b.get("companyId"));
			Predicate c4 = cb.equal(ocpm2.get("branchCode"), b.get("branchCode"));

			effectiveDate2.where(c1,c2,c3,c4);

			//where
			Predicate n1 = cb.equal(b.get("status"), "Y");
			Predicate n2 = cb.equal(b.get("status"), "R");
			Predicate n3 = cb.or(n1,n2);
			Predicate n4 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n5 = cb.equal(b.get("effectiveDateEnd"), effectiveDate2);
			Predicate n6 = cb.equal(b.get("companyId"), companyId);
			Predicate n7 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n8 = cb.equal(b.get("branchCode"), "99999");
			Predicate n9 = cb.or(n7,n8);
			Predicate n10 = cb.equal(b.get("valCompanyCode"), Id);
			query.where(n3,n4,n5,n6,n9,n10).orderBy(orderList);

			//GetResult
			TypedQuery<ValuationCompanyMaster> result = em.createQuery(query);
			list = result.getResultList();

			 return list;
			}
			catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
public void sslverification() {
	try {
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
	}catch (Exception e) {
		e.printStackTrace();
	}
}

public List<ValuationQuoteDetailsRes> getQuoteDetails(String quoteNo) {
	List<ValuationQuoteDetailsRes>list=null;
	try {
	// Criteria
	CriteriaBuilder cb = em.getCriteriaBuilder();
	CriteriaQuery<ValuationQuoteDetailsRes> query = cb.createQuery(ValuationQuoteDetailsRes.class);

	// Find All
	Root<MotorDataDetails> a = query.from(MotorDataDetails.class);
	Root<PersonalInfo> b = query.from(PersonalInfo.class);
	Root<HomePositionMaster> c = query.from(HomePositionMaster.class);
	Root<LoginUserInfo> d = query.from(LoginUserInfo.class);
	// Select
	query.multiselect(a.get("quoteNo").alias("quoteNo"),a.get("companyId").alias("companyId"),a.get("productId").alias("productId"),
			a.get("vehicleId").alias("vehicleId"),a.get("registrationNumber").alias("vehicleRegNo"),b.get("clientName").alias("firstName"),
			b.get("email1").alias("email"),b.get("mobileNo1").alias("customerMobile"),c.get("policyNo").alias("policyNo"),c.get("branchCode").alias("branchCode"),
			a.get("sumInsured").alias("sumInsured"),d.get("userName").alias("brokerName"),d.get("userMail").alias("brokerMail"),d.get("userMobile").alias("brokerMobileNo"));

	// Order By
	List<Order> orderList = new ArrayList<Order>();
	orderList.add(cb.asc(a.get("vehicleId")));

	
	// Where
	Predicate n1 = cb.equal(a.get("quoteNo"), quoteNo);
	Predicate n2 = cb.equal(a.get("policyType"), "1");
	Predicate n3 = cb.equal(a.get("quoteNo"), c.get("quoteNo"));
	Predicate n4 = cb.equal(b.get("customerId"), c.get("customerId"));
	Predicate n5 = cb.equal(c.get("loginId"), d.get("loginId"));
	query.where(n1, n2, n3, n4,n5).orderBy(orderList);

	// Get Result
	TypedQuery<ValuationQuoteDetailsRes> result = em.createQuery(query);
	list = result.getResultList();
	}catch (Exception e) {
		e.printStackTrace();
	}
	return list;
}

public void sendSMSMail(ValuationIntegration vdata,String type) {
	boolean sms=true,mail=true;
	String brokerMail="",brokerName="",brokerMobileNo="";
	try {
		List<ValuationQuoteDetailsRes>list=getQuoteDetails(vdata.getQuoteNo());
		if(!CollectionUtils.isEmpty(list)) {
			brokerMail=list.get(0).getBrokerMail();
			brokerName=list.get(0).getBrokerName();
			brokerMobileNo=list.get(0).getBrokerMobileNo();
		}
		 String mobileNo=vdata.getCustomerMobile();
		 String email=vdata.getEmail();
		 if(StringUtils.isBlank(email)) {
			 mail=false;
		 }
		 if(StringUtils.isBlank(mobileNo)) {
			 sms=false;
		 }
		if(sms || mail) {
			List<InsuranceCompanyMaster>cm= companyRepo.findTopByCompanyIdOrderByAmendIdDesc(vdata.getCompanyId());
			if(!CollectionUtils.isEmpty(cm)) {
			Calendar calend = Calendar.getInstance();
			calend.setTime(new Date()); 
			calend.add(Calendar.DATE, 1); 
			NotifTransactionDetails nt = NotifTransactionDetails.builder()
					.brokerCompanyName(brokerName)
					.brokerMailId(brokerMail)
					.brokerPhoneCode(Integer.parseInt("254"))
					.brokerPhoneNo(StringUtils.isBlank(brokerMobileNo)?null:new BigDecimal(brokerMobileNo))
					.companyName("First Insurance")
					.customerPhoneCode(Integer.parseInt("254"))
					.customerPhoneNo(vdata.getCustomerMobile()==null?null:new BigDecimal(vdata.getCustomerMobile()))
					.customerMailid(vdata.getEmail())					
					.customerName(vdata.getFirstName())
					.entryDate(new Date())
					.notifcationPushDate(new Date())
					.notifcationEndDate(calend.getTime())
					.regNo(vdata.getVehicleRegNo())
					//.notifDescription(tempPassword)
					.notifNo(Instant.now().toEpochMilli())
					//.notifNo(null)
					.notifPriority(1)
					.notifPushedStatus("P")
					.notifTemplatename("VALUATION_NOTIFICATION")											
					.productName("Common")					
					//.tinyUrl(n.getTinyUrl())
					.companyid(vdata.getCompanyId())
					.productid(99999)
					.companyLogo(cm.get(0).getCompanyLogo())
					.companyAddress(cm.get(0).getCompanyAddress())											
					.tinyUrlActive("N").pushedBy(vdata.getValCompanyName())
					.insuranceClass(vdata.getSumInsured()==null?"":vdata.getSumInsured().toString())
					.premiumAmount(vdata.getExceptionSumInsured()==null?"":vdata.getExceptionSumInsured().toString())
					//.tinyGroupId(tinyGroupId)
					.build();
			NotifTransactionDetails sv = notifTrans.save(nt);
			List<NotifTransactionDetails> text=new LinkedList<NotifTransactionDetails>();
			text.add(sv);
			notificationService.jobProcess(text);
			}
		}
		 
	}catch (Exception e) {
		e.printStackTrace();
	}
	
}
}