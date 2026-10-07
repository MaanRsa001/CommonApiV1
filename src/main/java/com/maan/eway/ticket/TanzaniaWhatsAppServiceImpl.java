package com.maan.eway.ticket;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class TanzaniaWhatsAppServiceImpl  implements TanzaniaWhastAppService{
	
	Logger log = LogManager.getLogger(getClass());
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private MotorDataDetailsRepository motRepo;
	
	@Autowired
	private SectionDataDetailsRepository secRepo;
	
	@Autowired
	private HomePositionMasterRepository homeRepo;
	
	@Autowired
	private PersonalInfoRepository personalInfoRepo;

	@Override
	public Object checkStickerNoStatus(Object req) {
		
		log.info("STICKER NUMBER CHECKING Block Start : "+new Date());
		try {
			
            Map<String,Object> requestMap = new HashMap<>();
            
            ObjectMapper mapper = new ObjectMapper();
			
            Map<String,Object> request = mapper.convertValue(req, Map.class);
            
            String regNo = request.get("RegistrationNo")== null ? "" : request.get("RegistrationNo").toString();
            String companyId = request.get("CompanyId")== null ? "" : request.get("CompanyId").toString();
            String productId = request.get("ProductId")== null ? "" : request.get("ProductId").toString();
            
            List<MotorDataDetails> mot = motRepo.findByRegistrationNumberAndCompanyIdAndStatus(regNo,companyId,"P");
            System.out.println("MOTOR DATA DETAILS COUNT:"+mot.size());
            
            Date currentDate = new Date();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.DAY_OF_MONTH, 30);

            Date within30Days = calendar.getTime();
            if(!mot.isEmpty()) {
            	List<MotorDataDetails> mot1 = mot.stream().filter(mo -> (!mo.getPolicyStartDate().before(currentDate)) 
            			|| (!mo.getPolicyStartDate().after(within30Days)) && 
            			(mo.getPolicyEndDate().after(new Date()))).toList();
            	
            	System.out.println("MOTOR DATA DETAILS COUNT(DATE FILTER):"+mot1.size());
            	
            	if(!mot1.isEmpty()) {
            		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            		List<MotorDataDetails> mot3 = mot1.stream().sorted(Comparator.comparing(MotorDataDetails::getPolicyStartDate).reversed())
            			    .toList();
            		MotorDataDetails mot2 = mot3.get(0);
            		SectionDataDetails sec = secRepo.findByQuoteNoAndSectionIdAndRiskId(mot2.getQuoteNo(),String.valueOf(mot2.getSectionId()),
            				Integer.valueOf(mot2.getVehicleId())); 
            		
            		HomePositionMaster home = homeRepo.findByPolicyNoAndStatusAndCompanyId(mot2.getPolicyNo(), "P", companyId);
            		
            		PersonalInfo per = personalInfoRepo.findByCustomerId(home.getCustomerId());
            		
            		 Map<String, Object> resultMap = new HashMap<>();
            		 resultMap.put("Message", "Successfully Fetched");
            		 resultMap.put("IsError", false);
            		 resultMap.put("ErrorMessage", null);
            		 
            		Map<String,Object> resMap = new HashMap<>();
            		
            		resMap.put("PolicyNo", home.getPolicyNo());
            		resMap.put("InceptionDate", sdf.format(home.getInceptionDate()));
            		resMap.put("ExpiryDate", sdf.format(home.getExpiryDate()));
            		resMap.put("CLientName", per.getClientName());
            		resMap.put("Title", per.getTitleDesc());
            		resMap.put("StickerNo", sec.getStickerNumber() == null ? null : sec.getStickerNumber());
            		resMap.put("CoverNoteRefNo", sec.getCoverNoteReferenceNo() == null ? null : sec.getCoverNoteReferenceNo());
            		resMap.put("TiraCode", sec.getResponseStatusCode() == null ? null : sec.getResponseStatusCode());
            		resMap.put("TiraDesc", sec.getResponseStatusDesc() == null ? null : sec.getResponseStatusDesc());
            		
            		resultMap.put("Result", resMap);
            		
            		System.out.println("API RESPONSE: "+resultMap);
            		
            		log.info("STICKER NUMBER CHECKING BLOCK END : "+new Date());
            		
            		return resultMap;
            		
            	}
            	Map<String, Object> resultMap = new HashMap<>();
       		    resultMap.put("Message", "No Active Policy for this Registration No");
       		    resultMap.put("IsError", false);
       		    resultMap.put("ErrorMessage", null);
       		    resultMap.put("Result", null);
       		    
       		 System.out.println("API RESPONSE: "+resultMap);
       		 
       		log.info("STICKER NUMBER CHECKING BLOCK END : "+new Date());
     		
     		    return resultMap;
            	
            }
            Map<String, Object> resultMap = new HashMap<>();
   		    resultMap.put("Message", "No Record Found for this Registraion No");
   		    resultMap.put("IsError", false);
   		    resultMap.put("ErrorMessage", null);
   		    resultMap.put("Result", null);
   		    
   		   System.out.println("API RESPONSE: "+resultMap);
   		   
   		log.info("STICKER NUMBER CHECKING BLOCK END : "+new Date());
 		
 		    return resultMap;
           
            
            /*
            CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
			
			// Find All
		    Root<HomePositionMaster> home = query.from(HomePositionMaster.class);
		    Root<MotorDataDetails> mot = query.from(MotorDataDetails.class);
		    Root<SectionDataDetails> sec = query.from(SectionDataDetails.class);
		    Root<PersonalInfo> pi = query.from(PersonalInfo.class);
		    
		    query.multiselect(home.get("policyNo").alias("policyNo"),home.get("inceptionDate").alias("inceptionDate"),
		    		home.get("expiryDate").alias("expiryDate"),sec.get("responseStatusCode").alias("responseStatusCode"),
		    		sec.get("responseStatusDesc").alias("responseStatusDesc"),sec.get("stickerNumber").alias("stickerNumber"),
		    		sec.get("coverNoteReferenceNo").alias("coverNoteReferenceNo"),pi.get("clientName").alias("clientName"),
		    		pi.get("titleDesc").alias("titleDesc"));
		    
		    Predicate n1 = cb.equal(mot.get("registrationNumber"), regNo);
		    Predicate n2 = cb.equal(mot.get("companyId"), companyId);
		    Predicate n3 = cb.equal(mot.get("productId"), productId);
		    Predicate n4 = cb.equal(mot.get("status"), "P");
		    Predicate n5 = cb.equal(sec.get("productId"), productId);
		    Predicate n6 = cb.equal(sec.get("companyId"), companyId);
		    Predicate n7 = cb.equal(mot.get("vehicleId"), sec.get("riskId").as(String.class));
		    Predicate n8 = cb.isNotNull(mot.get("policyNo"));
		    Predicate n9 = cb.isNotNull(sec.get("policyNo"));
		    Predicate n10 = cb.equal(sec.get("status"), "P");
		    Predicate n11 = cb.equal(mot.get("policyNo"),sec.get("policyNo"));
		    
		    Predicate n7 = cb.equal(home.get("productId"), productId);
		    
		    Predicate n11 = cb.isNotNull(home.get("policyNo"));
		    
		    
		    Predicate n14 = cb.equal(home.get("status"), "P");
		    
		    Predicate n16 = cb.equal(mot.get("policyNo"),home.get("policyNo"));
		   
		    Predicate n18 = cb.equal(sec.get("policyNo"),home.get("policyNo"));
		    Predicate n19 = cb.equal(mot.get("quoteNo"),home.get("quoteNo"));
		    Predicate n20 = cb.equal(mot.get("quoteNo"),sec.get("quoteNo"));
		    Predicate n21 = cb.equal(sec.get("quoteNo"),home.get("quoteNo"));
		    Predicate n5 = cb.equal(home.get("companyId"), companyId);
		    
		    query.where(n1,n2,n3,n4,n5,n6,n7,n8,n9,n10,n11);
		    
		    TypedQuery<Tuple> result = em.createQuery(query);
			List<Tuple> tub= result.getResultList();
			
			System.out.println("List: " + tub);
		    */
		    
		    
			
		}catch(Exception e) {
			e.printStackTrace();
			Map<String, Object> resultMap = new HashMap<>();
   		    resultMap.put("Message", "Failed to fatch record");
   		    resultMap.put("IsError", true);
   		    resultMap.put("ErrorMessage", e.getMessage());
   		    resultMap.put("Result", null);
 		
   		 log.info("STICKER NUMBER CHECKING BLOCK ERROR : "+new Date());
 		    return resultMap;
		}
	}

}
