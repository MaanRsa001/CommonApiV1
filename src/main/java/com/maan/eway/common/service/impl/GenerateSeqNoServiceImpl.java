package com.maan.eway.common.service.impl;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.SeqCreditno;
import com.maan.eway.bean.SeqDebitnote;
import com.maan.eway.bean.SeqErrorCode;
import com.maan.eway.bean.SeqPolicyno;
import com.maan.eway.bean.SeqPolicynoMadison;
import com.maan.eway.bean.SeqPolicynoUganda;
import com.maan.eway.bean.SeqProductbenefit;
import com.maan.eway.bean.SeqRefno;
import com.maan.eway.bean.SeqTinyrefno;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.SequenceGenerateRes;
import com.maan.eway.repository.SeqCreditnoRepository;
import com.maan.eway.repository.SeqDebitnoteRepository;
import com.maan.eway.repository.SeqErrorCodeRepository;
import com.maan.eway.repository.SeqPolicynoMadisonRepository;
import com.maan.eway.repository.SeqPolicynoRepository;
import com.maan.eway.repository.SeqPolicynoUgandaRepository;
import com.maan.eway.repository.SeqProductbenefitRepository;
import com.maan.eway.repository.SeqRefnoRepository;
import com.maan.eway.repository.SeqTinyrefnoRepository;

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
public class GenerateSeqNoServiceImpl {
	
	private Logger log = LogManager.getLogger(GenerateSeqNoServiceImpl.class);
	
	@Autowired
	private SeqRefnoRepository refNoRepo ;
	
	@Autowired
	private SeqPolicynoRepository polNoRepo;
	
	@Autowired
	private SeqPolicynoUgandaRepository polNoUgandaRepo;
	
	@Autowired
	private SeqPolicynoMadisonRepository polNoMadisonRepo;
	
	@Autowired
	private SeqDebitnoteRepository debitRepo;
	
	@Autowired
	private SeqCreditnoRepository creditRepo;
	
	@Autowired
	private SeqTinyrefnoRepository tinyRefRepo;
	
	@Autowired
	private SeqProductbenefitRepository benefitRepo ;
	
	@Autowired
	private SeqErrorCodeRepository errorCodeRepo ;
	
	@PersistenceContext
	private EntityManager em;
	
	@Value(value = "${EwayBasicAuthPass}")
	private String EwayBasicAuthPass;
	
	@Value(value = "${EwayBasicAuthName}")
	private String EwayBasicAuthName;
	
	@Value(value = "${SequenceGenerateUrl}")
	private String SequenceGenerateUrl;

	 public synchronized String generateRefNo() {
	       try {
	    	   SeqRefno entity;
	            entity = refNoRepo.save(new SeqRefno());          
	            return String.format("%05d",entity.getRequestReferenceNo()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 }
	 
	
	 

	 public synchronized String generatePolicyNo(String productCode,String branchcode, String companyId, String vehUsageCoreappcode, String productId, String itemvalue ) { //madison 100004
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy"); 
		 String policyNo = "";
	       try {
	    	   SeqPolicynoMadison entity;
	            entity = polNoMadisonRepo.save(new SeqPolicynoMadison());    
	            Date currentDate = Calendar.getInstance().getTime();
	            String year =  sdf.format(new Date()) ;
	       
	            	 //---> P/01/4013/001377/2023/B,  P/01/4013/001377/2023/E
		            //----> 'P'||'/'||LvBranchCd||'/'||LvVeh_Usage||'/'||Lpad(MOTOR_POLICY_NO.Nextval,'6','0')||'/'||TO_CHAR(SYSDATE,'YYYY') //others productid
	            	//end local, uat-->/T, live-->/B
	            	
	            	if(productId.equalsIgnoreCase("5"))
	            		policyNo = "P/" + branchcode + "/"  + vehUsageCoreappcode + "/" + String.format("%06d",entity.getPolicyno()) +  "/" + year + itemvalue;
	            	else
	            		policyNo = "P/" + branchcode + "/"  + productCode + "/" + String.format("%06d",entity.getPolicyno()) +  "/" + year + itemvalue;
	            	
	    
	            
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       return policyNo;
	 
	 }
	 
	 public synchronized String generatePolicyNo(String productCode,String branchcode) {
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy"); 
		 String policyNo = "";
	       try {
	    	    SeqPolicyno entity;
	            entity = polNoRepo.save(new SeqPolicyno());    
	            Date currentDate = Calendar.getInstance().getTime();
	            String year =  sdf.format(new Date()) ;
	        
	            	policyNo =  "P11/"+year+"/"+branchcode+"/"+productCode+"/10/"+String.format("%07d",entity.getPolicyno()) ;
	        
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       return policyNo;
	 
	 }
	 
	 public synchronized String generateUgandaPolicyNo(String productCode,String branchcode) {
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy"); 
		 String policyNo = "";
	       try {
	    	   SeqPolicynoUganda entity;
	            entity = polNoUgandaRepo.save(new SeqPolicynoUganda());    
	            Date currentDate = Calendar.getInstance().getTime();
	            String year =  sdf.format(new Date()) ;
	        
	            	policyNo =  "P11/"+year+"/"+branchcode+"/"+productCode+"/10/"+String.format("%07d",entity.getPolicyno()) ;
	        
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       return policyNo;
	 
	 }
	 
	 
	 public synchronized String generateDebitNo(String branchCode) {
	       try {
	    	    SeqDebitnote entity;
	            entity = debitRepo.save(new SeqDebitnote());          
	            return "DNP"+branchCode+"-"+String.format("%09d",entity.getDebitnote()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 
	 }
	 
	 public synchronized String generateCreditNo(String branchCode) {
	       try {
	    	    SeqCreditno entity;
	            entity = creditRepo.save(new SeqCreditno());          
	            return "CNP"+branchCode+"-"+String.format("%09d",entity.getCreditnote()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 
	 }
	 
	 public synchronized String generateTinyRefNo() {
	       try {
	    	    SeqTinyrefno entity;
	            entity = tinyRefRepo.save(new SeqTinyrefno());          
	            return String.format("%05d",entity.getTinyUrlRefNo()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 
	 }
	 
	 public synchronized String generateBenefitId() {
	       try {
	    	    SeqProductbenefit entity;
	            entity = benefitRepo.save(new SeqProductbenefit());          
	            return String.format("%05d",entity.getBenefitId()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 
	 }
	 
	 public synchronized String generateErrorCode() {
	       try {
	    	   SeqErrorCode entity;
	            entity = errorCodeRepo.save(new SeqErrorCode());          
	            return String.format("%04d",entity.getErrorCode()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 }
	 
	 
	 public synchronized String generateSeqCall(SequenceGenerateReq req ) {
	       try {
	    	String url = SequenceGenerateUrl;
	   		String auth = EwayBasicAuthName +":"+ EwayBasicAuthPass;
	         byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charset.forName("US-ASCII")) );
	         String authHeader = "Basic " + new String( encodedAuth );
	      
	   		RestTemplate restTemplate = new RestTemplate();
	   		HttpHeaders headers = new HttpHeaders();
	   		headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
	   		headers.setContentType(MediaType.APPLICATION_JSON);
	   		 headers.set("Authorization",authHeader);
	   		HttpEntity<SequenceGenerateReq> entityReq = new HttpEntity<SequenceGenerateReq>(req, headers);

	   		ResponseEntity<CommonRes> response = restTemplate.postForEntity(url, entityReq, CommonRes.class);
	   		ObjectMapper mapper = new ObjectMapper();
	   		SequenceGenerateRes res = mapper.convertValue(response.getBody().getCommonResponse() ,new TypeReference<SequenceGenerateRes>(){});
	   		String seq = res.getGeneratedValue();
	   		System.out.println("Generated Sequence --> " + seq );
	   		
	    	 return seq ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	 }

	 public synchronized List<ListItemValue> getSourceTypeDropdown(String insuranceId , String branchCode, String itemType) {
			List<ListItemValue> list = new ArrayList<ListItemValue>();
			try {
				Date today = new Date();
				Calendar cal = new GregorianCalendar();
				cal.setTime(today);
				today = cal.getTime();
				Date todayEnd = cal.getTime();
				
				// Criteria
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ListItemValue> query=  cb.createQuery(ListItemValue.class);
				// Find All
				Root<ListItemValue> c = query.from(ListItemValue.class);
				
				//Select
				query.select(c);
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(c.get("branchCode")));
				
				
				// Effective Date Start Max Filter
				Subquery<Date> effectiveDate = query.subquery(Date.class);
				Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
				effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
				Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
				Predicate b3 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
				Predicate b4 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				effectiveDate.where(a1,a2,b3,b4);
				// Effective Date End Max Filter
				Subquery<Date> effectiveDate2 = query.subquery(Date.class);
				Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
				effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
				Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
				Predicate b1 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
				Predicate b2 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
				Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
				effectiveDate2.where(a3,a4,b1,b2);
							
				// Where
				Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
				Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
				Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
				Predicate n5 = cb.equal(c.get("companyId"), "99999");
				Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
				Predicate n7 = cb.equal(c.get("branchCode"), "99999");
				Predicate n8 = cb.or(n4,n5);
				Predicate n9 = cb.or(n6,n7);
				Predicate n10 = cb.equal(c.get("itemType"),itemType );
				query.where(n2,n3,n4,n8,n9,n10).orderBy(orderList);
				
			
				// Get Result
				TypedQuery<ListItemValue> result = em.createQuery(query);
				list = result.getResultList();
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return list ;
		}
}
