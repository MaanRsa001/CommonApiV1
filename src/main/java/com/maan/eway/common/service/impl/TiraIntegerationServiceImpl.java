package com.maan.eway.common.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.common.req.TiraFrameReqCall;
import com.maan.eway.integration.req.PremiaRequest;
import com.maan.eway.integration.service.PhoenixIntegrationService;
import com.maan.eway.master.service.impl.ClausesMasterServiceImpl;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.res.SuccessRes;

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
public class TiraIntegerationServiceImpl {
 
	private Logger log = LogManager.getLogger(ClausesMasterServiceImpl.class);
	
	@Value(value = "${TiraIntegReqFrameLink}")
	private String tiraIntegReqFrameLink;
	
	@Value(value = "${TiraIntegPushLink}")
	private String tiraIntegPushLink;
	
	@Value(value = "${NonMotorTiraIntegPushLink}")
	private String nonMotorTiraLink;
	
	@Value(value="${collectDataFromTiraPost}")
	private String collectDataFromTiraPost;
	
	@Value(value = "${PremiaPushLink}")
	private String premiaPushLink;
	
	@Value(value = "${TiraIntegPushLinkFleet}")
	private String tiraIntegPushLinkFleet;
	@Value(value = "${TiraIntegReqFrameLinkFleet}")
	private String tiraIntegReqFrameLinkFleet;
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private HomePositionMasterRepository homerepo ;
	
	@Autowired
	private SectionDataDetailsRepository sectionDataRepo;
	
	@Autowired
	private PhoenixIntegrationService service;
	
	public SuccessRes callTiraIntegeration(TiraFrameReqCall tiraReq, String token) {
		SuccessRes res = new SuccessRes();
		try {
			HomePositionMaster data = homerepo.findByQuoteNo(tiraReq.getQuoteNo());
			CompanyProductMaster product =  getCompanyProductMasterDropdown(data.getCompanyId() , data.getProductId().toString());
			
			String url=nonMotorTiraLink; 
			if  (  product.getMotorYn().equalsIgnoreCase("M")  && data.getCompanyId().equalsIgnoreCase("100002") && data.getNoOfVehicles()<50 ) {
				url=tiraIntegPushLink;
				
			//	 em.flush();
			}else if(  product.getMotorYn().equalsIgnoreCase("M")  && data.getCompanyId().equalsIgnoreCase("100002") && data.getNoOfVehicles()>49 ) {
				url=tiraIntegPushLinkFleet;
			} 
				
				
				
				// Tira Request Frame
			if( data.getProductId().equals(4) ) {
				
				res.setResponse("Success");
				
			} else if(  data.getCompanyId().equalsIgnoreCase("100002") && data.getNoOfVehicles()<50   ) {
				List<SectionDataDetails> secDatas = sectionDataRepo.findByQuoteNo(tiraReq.getQuoteNo());
				secDatas=secDatas.stream().filter(o-> StringUtils.isBlank(o.getCoverNoteReferenceNo())).toList();
				//List<Integer> risks = secDatas.stream().map(SectionDataDetails::getRiskId).distinct().collect(Collectors.toList());
				List<SectionDataDetails> risks = secDatas.stream()
			            .collect(Collectors.toMap(
			                item -> Arrays.asList(item.getRiskId(), item.getQuoteNo(), item.getProductId(), item.getSectionId()),
			                item -> item,
			                (existing, replacement) -> existing
			            ))
			            .values()
			            .stream()
			            .toList();
				for(SectionDataDetails risk:risks) {
						TiraFrameReqCall request=new TiraFrameReqCall();
						request.setQuoteNo(tiraReq.getQuoteNo());
						request.setRiskId(risk.getRiskId()+"");
						request.setSectionId(risk.getSectionId());
						request.setLocationId(risk.getLocationId().toString());
						
						Object tiraFramedReq = TiraReqFrame(request, token);
						res.setResponse("Success");
						// Tira Integ Push
						if(tiraFramedReq!=null) {
							JSONObject tiraIntegPushRes = TiraIntegPush(tiraFramedReq , token,url);
							log.info("Tira Response --->"+tiraIntegPushRes);
						}else {
							res.setResponse("Failed");
							log.info("Tira Framed Req --->"+tiraFramedReq);	
						}
					
				}
			}else if(  data.getCompanyId().equalsIgnoreCase("100002") && data.getNoOfVehicles()>49   ) {
				Object tiraFramedReq = TiraReqFrameFleet(tiraReq, token);
				res.setResponse("Success");
				// Tira Integ Push
				if(tiraFramedReq!=null) {
					JSONObject tiraIntegPushRes = TiraIntegPush(tiraFramedReq , token,url);


					log.info("Tira Response --->"+tiraIntegPushRes);
				}else {
					res.setResponse("Failed");
					log.info("Tira Framed Req --->"+tiraFramedReq);	
				}
			}
			/*
			Set<String> stickerNoList = new HashSet<>();
			List<SectionDataDetails> risks = sectionDataRepo.findByQuoteNo(tiraReq.getQuoteNo());
			if (risks != null) {
				stickerNoList = risks.stream()
			                        .map(SectionDataDetails::getStickerNumber)
			                        .filter(Objects::nonNull) 
			                        .collect(Collectors.toSet());
			}
			if (!stickerNoList.isEmpty()) {
				if (stickerNoList.size() > 0 && stickerNoList.size() == data.getNoOfVehicles()) {
					// Background Call
					ExecutorService service2 = Executors.newFixedThreadPool(1);

					service2.submit(new Runnable() {
						public void run() {
							try {
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
								System.out.println("Premia Integration Started... Quote No ---> " + tiraReq.getQuoteNo()
										+ " . Time : " + sdf.format(new Date()));

								// Call Integeration
								if (product.getMotorYn().equalsIgnoreCase("M")) {
									PremiaRequest premiaReq = new PremiaRequest();
									premiaReq.setQuoteNo(tiraReq.getQuoteNo());
									List<String> premiaIds = new ArrayList<String>();
									premiaIds.add("1");
									premiaIds.add("2");
									premiaIds.add("3");
									premiaIds.add("4");
									premiaIds.add("5");
									premiaIds.add("6");
									premiaIds.add("7");
									premiaIds.add("8");
									premiaIds.add("9");
									premiaIds.add("10");
									premiaIds.add("11");
									premiaIds.add("12");
									premiaReq.setPremiaIds(premiaIds);

									// service.pushPremiaIntegration(premiaReq);
									pushPremiaIntegration(premiaReq, token);
								} else {
									PremiaRequest premiaReq = new PremiaRequest();
									premiaReq.setQuoteNo(tiraReq.getQuoteNo());
									List<String> premiaIds = new ArrayList<String>();
									premiaIds.add("1");
									premiaIds.add("2");
									premiaIds.add("3");
									premiaIds.add("4");
									premiaIds.add("5");
									premiaIds.add("6");
									premiaIds.add("7");
									premiaIds.add("8");
									premiaIds.add("9");
									premiaIds.add("10");
									premiaIds.add("11");
									premiaIds.add("12");
									premiaReq.setPremiaIds(premiaIds);

									// service.pushPremiaIntegration(premiaReq);
									pushPremiaIntegration(premiaReq, token);
								}
								System.out.println("Premia Integration Ended... Quote No ---> " + tiraReq.getQuoteNo()
										+ " . Time : " + sdf.format(new Date()));

							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally {
								// Shutdown the ExecutorService
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
								System.out.println("Premia Integration FINALY... Quote No ---> " + tiraReq.getQuoteNo()
										+ " . Time : " + sdf.format(new Date()));
								service2.shutdown();
								try {
									if (!service2.awaitTermination(30, TimeUnit.SECONDS)) {
										service2.shutdownNow();
									}
								} catch (InterruptedException ex) {
									service2.shutdownNow();
									Thread.currentThread().interrupt();
								}
							}

						}
					});
				}
			} else {
			System.out.println("*****STICKER NUMBER DOES NOT EXIST FOR THIS QUOTE NO : " + data.getQuoteNo()
					+ " STICKER NUMBER LIST " + stickerNoList);
		}*/
			
			
			
	//}
			
			
			res.setSuccessId("");
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}
	
	 private Object TiraReqFrameFleet(TiraFrameReqCall tiraReq, String token) {
		 try {

				// Frame Tira Req

				RestTemplate temp = new RestTemplate();
				HttpHeaders header = new HttpHeaders();
				header.setContentType(MediaType.APPLICATION_JSON);
				// header.setCharset("UTF-8");
				header.setBearerAuth(token);
				String url = tiraIntegReqFrameLinkFleet;
				HttpEntity<?> requestent = new HttpEntity<>(tiraReq, header);

				System.out.println(new Date() + " Start " + url);
				ResponseEntity<Object> postEntity = temp.exchange(url, HttpMethod.POST, requestent,new ParameterizedTypeReference<Object>() {}) ;
				Object TiraFramedReq = null;
				//if(postEntity.getStatusCode()==HttpStatus.ACCEPTED) {
					TiraFramedReq = postEntity.getBody() ;
				//}		
					System.out.println("FLEET"+TiraFramedReq);
				System.out.println(new Date() + " End " + url);

			return TiraFramedReq;
		 }catch (Exception e) {
			 e.printStackTrace();
		}
		return null;
	}

	public Object pushPremiaIntegration(PremiaRequest premiaReq , String token ) {
		 	Object PremiaRes = null;
		try {
			// Frame Tira Req
			System.out.println("Calling : /push/integration/quote");
			RestTemplate temp = new RestTemplate();
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			// header.setCharset("UTF-8");
			header.setBearerAuth(token);
			String url = premiaPushLink ;
			HttpEntity<?> requestent = new HttpEntity<>(premiaReq, header);

			System.out.println(new Date() + " Start " + url);
			ResponseEntity<Object> postEntity = temp.exchange(url, HttpMethod.POST, requestent,new ParameterizedTypeReference<Object>() {}) ;
			
			//if(PremiaRes.getStatusCode()==HttpStatus.ACCEPTED) {
			PremiaRes = postEntity.getBody() ;
			//}		
				System.out.println("Premia Response --> "+PremiaRes);
			System.out.println(new Date() + " End " + url);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return PremiaRes;
	}
	
	
	 public Object TiraReqFrame (TiraFrameReqCall tiraReq , String token ) {
		 	Object TiraFramedReq = null;
		try {
			// Frame Tira Req

			RestTemplate temp = new RestTemplate();
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			// header.setCharset("UTF-8");
			header.setBearerAuth(token);
			String url = tiraIntegReqFrameLink;
			HttpEntity<?> requestent = new HttpEntity<>(tiraReq, header);
			System.out.println("Tira Request" + tiraReq);
			System.out.println(new Date() + " Start " + url);
			ResponseEntity<Object> postEntity = temp.exchange(url, HttpMethod.POST, requestent,new ParameterizedTypeReference<Object>() {}) ;
			
			//if(postEntity.getStatusCode()==HttpStatus.ACCEPTED) {
				TiraFramedReq = postEntity.getBody() ;
			//}		
				System.out.println("HHE"+TiraFramedReq);
			System.out.println(new Date() + " End " + url);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return TiraFramedReq;
	}
	 
		 
	 public JSONObject TiraIntegPush(Object pushReq , String token, String url  ) {
		 JSONObject res = null;
		try {
			// Frame Tira Req

			RestTemplate temp = new RestTemplate();
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_XML);
			// header.setCharset("UTF-8");
			header.setBearerAuth(token);
			
			HttpEntity<?> requestent = new HttpEntity<>(pushReq , header);

			System.out.println(new Date() + " Start " + url);
			ResponseEntity<JSONObject> postForEntity = temp.exchange(url, HttpMethod.POST, requestent,new ParameterizedTypeReference<JSONObject>() {}) ;
			res = postForEntity.getBody() ;
			System.out.println(new Date() + " End " + url);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}
		 
		public synchronized CompanyProductMaster getCompanyProductMasterDropdown(String companyId, String productId) {
			CompanyProductMaster product = new CompanyProductMaster();
			try {
				Date today = new Date();
				Calendar cal = new GregorianCalendar();
				cal.setTime(today);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				;
				cal.set(Calendar.MINUTE, 1);
				today = cal.getTime();
				cal.set(Calendar.HOUR_OF_DAY, 1);
				cal.set(Calendar.MINUTE, 1);
				Date todayEnd = cal.getTime();

				// Criteria
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<CompanyProductMaster> query = cb.createQuery(CompanyProductMaster.class);
				List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
				// Find All
				Root<CompanyProductMaster> c = query.from(CompanyProductMaster.class);
				// Select
				query.select(c);
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(c.get("productName")));

				// Effective Date Start Max Filter
				Subquery<Date> effectiveDate = query.subquery(Date.class);
				Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
				effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
				Predicate a1 = cb.equal(c.get("productId"), ocpm1.get("productId"));
				Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
				Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				effectiveDate.where(a1, a2, a3);
				// Effective Date End Max Filter
				Subquery<Date> effectiveDate2 = query.subquery(Date.class);
				Root<CompanyProductMaster> ocpm2 = effectiveDate2.from(CompanyProductMaster.class);
				effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
				Predicate a4 = cb.equal(c.get("productId"), ocpm2.get("productId"));
				Predicate a5 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
				Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
				effectiveDate2.where(a4, a5, a6);

				// Where
				Predicate n1 = cb.equal(c.get("status"), "Y");
				Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
				Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
				Predicate n4 = cb.equal(c.get("companyId"), companyId);
				Predicate n5 = cb.equal(c.get("productId"), productId);
				query.where(n1, n2, n3, n4, n5).orderBy(orderList);
				// Get Result
				TypedQuery<CompanyProductMaster> result = em.createQuery(query);
				list = result.getResultList();
				product = list.size() > 0 ? list.get(0) :null;
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is --->" + e.getMessage());
				return null;
			}
			return product;
		}


		public JSONObject resultOfTiraIntegration(String quoteNo, String token) {
			 JSONObject res = null;
				try {
					// Frame Tira Req

					RestTemplate temp = new RestTemplate();
					HttpHeaders header = new HttpHeaders();
					header.setContentType(MediaType.APPLICATION_XML);
					// header.setCharset("UTF-8");
					header.setBearerAuth(token);
					
					HttpEntity<?> requestent = new HttpEntity<>(header);
					String url=collectDataFromTiraPost.replaceAll("<QuoteNo>", quoteNo);
					System.out.println(new Date() + " Start " + url);
					ResponseEntity<JSONObject> postForEntity = temp.exchange(url,
												HttpMethod.GET,
												requestent,new ParameterizedTypeReference<JSONObject>() {}) ;
					res = postForEntity.getBody() ;
					System.out.println(new Date() + " End " + url);

				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is ---> " + e.getMessage());
					return null;
				}
				return res;
		} 
			
}
