package com.maan.eway.master.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.SurvivalBenefitMaster;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.GetallSurrenderDetailsReq;
import com.maan.eway.master.req.GetoneSurvivalDetailsReq;
import com.maan.eway.master.req.InsertSurvivalReq;
import com.maan.eway.master.req.SurvivalReq;
import com.maan.eway.master.res.GetoneSurvivalDetailsRes;
import com.maan.eway.master.service.SurvivalBenefitMasterService;
import com.maan.eway.repository.SurvivalBenefitMasterRepository;
import com.maan.eway.res.SuccessRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
@Transactional
public class SurvivalBenefitMasterServiceImple implements SurvivalBenefitMasterService{

	private Logger log=LogManager.getLogger(BankMasterServiceImpl.class);
	Gson json = new Gson();
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	DozerBeanMapper mapper = new DozerBeanMapper();
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private SurvivalBenefitMasterRepository repo;

	@Override
	public List<Error> validateSurvival(InsertSurvivalReq req) {
		List<Error> errorList = new ArrayList<Error>();

		try {
			
			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
			}
			if (StringUtils.isBlank(req.getProductId())) {
				errorList.add(new Error("02", "ProductId", "Please Enter ProductId"));
			}
			if (StringUtils.isBlank(req.getSectionId())) {
				errorList.add(new Error("02", "SectionId", "Please Enter SectionId"));
			}
			if (StringUtils.isBlank(req.getCreatedBy())) {
				errorList.add(new Error("09", "CreatedBy", "Please Enter CreatedBy"));
			}else if (req.getCreatedBy().length() > 100){
				errorList.add(new Error("09","CreatedBy", "Please Enter CreatedBy within 100 Characters")); 
			}		
			
			// Date Validation 
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null || StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
				errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start"));

			} else if (req.getEffectiveDateStart().before(today)) {
				errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
			}
			
			if (req.getPolicyTerm()==null ) {
				errorList.add(new Error("07", "PolicyTerm", "Please Enter Policy Term"));
			}else if (req.getPolicyTerm() <= 0){
				errorList.add(new Error("07","PolicyTerm", "Please Enter Policy Term Greater Than Zero")); 
			}
			else if (req.getPolicyTerm() > 100){
				errorList.add(new Error("07","PolicyTerm", "Please Enter Policy Term within 100")); 
				
			} 
			 Set<Integer> uniqueElements = new HashSet<>();
			if(req.getSurvivalReq().isEmpty() ) {
				errorList.add(new Error("07","Surrender", "Please Enter Survival Details")); 
			}else if(req.getSurvivalReq().size() > req.getPolicyTerm()){
				errorList.add(new Error("07","Surrender", "Survival Details List Should Be Maximun" + req.getPolicyTerm() + " Rows")); 
			}
			
			else {
				int row = 0;
				int toalAmount = 0;
				
				for(SurvivalReq data : req.getSurvivalReq()) {
					row = row + 1;
					
					//EndOfYear Validation
					if (data.getEndOfYear()==null ) {
						errorList.add(new Error("07", "EndOfYear", "Please Enter End Of Year in Row "+ row));
					}else if (data.getEndOfYear() <= 0){
						errorList.add(new Error("07","EndOfYear", "Please Enter End Of Year Greater Than Zero in Row "+ row)); 
					}
					else if (data.getEndOfYear() > req.getPolicyTerm()){
						errorList.add(new Error("07","EndOfYear", "Please Enter End Of Year Within " + req.getPolicyTerm() +" in Row "+row )); 
						
					} 
					//EndOfYear Duplication
					
					if (data.getEndOfYear()!=null ) {
						if(! uniqueElements.add(data.getEndOfYear()) )
							errorList.add(new Error("07","Duplicate EndOfYear","End Of Year "+  data.getEndOfYear()+ " Already Exists "  +"in Row "+row ));
					}
					
					if(req.getSaveType().equalsIgnoreCase("Submit")){
					
						//Status Validation
						if (StringUtils.isBlank(data.getStatus())) {
							errorList.add(new Error("05", "Status", "Please Select Status in Row " + row));
						} else if (data.getStatus().length() > 1) {
							errorList.add(new Error("05", "Status", "Please Select Valid Status - One Character Only Allwed in Row " + row));
						}else if(!("Y".equalsIgnoreCase(data.getStatus())||"N".equalsIgnoreCase(data.getStatus())||"R".equalsIgnoreCase(data.getStatus())|| "P".equalsIgnoreCase(data.getStatus()))) {
							errorList.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral in Row "+ row));
						} 
						
						//Amount %
						if (data.getAmount()==null ) { 
							errorList.add(new Error("07", "SurvivalBenefit Percentage", "Please Enter Survival Benefit Percentage in Row "+ row));
						}else if (data.getAmount() < 0){
							errorList.add(new Error("07","SurvivalBenefit Percentage", "Please Enter Non-Negative Survival Benefit Percentage in Row "+ row)); 
						}
						else if (data.getAmount() > 100){
							errorList.add(new Error("07","SurvivalBenefit Percentage", "Please Enter Survival Benefit Percentage Within 100"+" in Row "+row )); 
							
						} 
						if (data.getAmount()!=null )
							toalAmount = toalAmount + data.getAmount();
						
						
						if(StringUtils.isNotBlank(data.getStatus()) && ! (data.getStatus().equalsIgnoreCase("P")) ) {
							if (StringUtils.isBlank(data.getCoreAppCode())) {
								errorList.add(new Error("07", "CoreAppCode", "Please Enter CoreAppCode in Row "+ row));
							}else if (data.getCoreAppCode().length() > 20){
								errorList.add(new Error("07","CoreAppCode", "Please Enter CoreAppCode within 20 Characters in Row "+ row)); 
							}
							if (StringUtils.isBlank(data.getRegulatoryCode())) {
								errorList.add(new Error("08", "RegulatoryCode", "Please Enter RegulatoryCode in Row "+ row));
							}else if (data.getRegulatoryCode().length() > 20){
								errorList.add(new Error("08","RegulatoryCode", "Please Enter RegulatoryCode within 20 Characters in Row "+ row)); 
							}
							if (StringUtils.isNotBlank(data.getRemarks()) && data.getRemarks().length() > 100){
								errorList.add(new Error("03","Remark", "Please Enter Remark within 100 Characters in Row " + row)); 
							}
							if (StringUtils.isBlank(data.getCalcType())) {
								errorList.add(new Error("07", "Calc Type", "Please Select Calc Type in Row "+ row));
							}
							
						}
						
					}
				}
				if(req.getSaveType().equalsIgnoreCase("Submit")){
					if(toalAmount!=100)
						errorList.add(new Error("07","SurvivalBenefit Total Percentage", "Survival Benefit Total Percentage Should Be 100"));
				
					//EndOfYear 
//					Set<Integer> endyearfilter = uniqueElements.stream().filter(element -> element ==req.getPolicyTerm() ).collect(Collectors.toSet());
//					
//					if(endyearfilter.isEmpty())
//						errorList.add(new Error("07","EndOfYear", "Add End Of Year Upto "+ req.getPolicyTerm()));
					}
			}
		
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}

	@Override
	public SuccessRes insertSurvival(InsertSurvivalReq req) {
		SuccessRes res = new SuccessRes();
		SurvivalBenefitMaster saveData = new SurvivalBenefitMaster();
		List<SurvivalBenefitMaster> list  = new ArrayList<SurvivalBenefitMaster>();
		
		try {
			Integer amendId = 0;
		
			String end = "31/12/2050";
			Date endDate = sdf.parse(end);
			long MILLS_IN_A_DAY = 1000*60*60*24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime()- MILLS_IN_A_DAY);
			Date entryDate = null;
			String createdBy ="";
			
			List<SurvivalBenefitMaster> old = repo.findByPolicyTermsAndProductIdAndSectionIdAndCompanyId(
					Integer.valueOf(req.getPolicyTerm()), Integer.valueOf(req.getProductId()),Integer.valueOf(req.getSectionId()),
					req.getCompanyId());
			
			if(old.size()<=0 ) {   //Insert
				
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully");
				
			}
			else {  //update
				
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<SurvivalBenefitMaster> query = cb.createQuery(SurvivalBenefitMaster.class);
			
				Root<SurvivalBenefitMaster> b = query.from(SurvivalBenefitMaster.class);
				
				query.select(b);
				
				Subquery<Long> maxAmendId = query.subquery(Long.class);
				Root<SurvivalBenefitMaster> ocpm1 = maxAmendId.from(SurvivalBenefitMaster.class);
				maxAmendId.select(cb.max(ocpm1.get("amendId")));
				Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
				Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
				Predicate a3 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
				Predicate a4 = cb.equal(ocpm1.get("policyTerms"), b.get("policyTerms"));
				maxAmendId.where(a1,a2,a3,a4);
			
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(b.get("endOfYear")));
				
				Predicate n1 = cb.equal(b.get("policyTerms"),req.getPolicyTerm());
				Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
				Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
				Predicate n4 = cb.equal(b.get("sectionId"),req.getSectionId());
				Predicate n5 = cb.equal(b.get("amendId"),maxAmendId);
				
			
				query.where(n1,n2,n3,n4,n5).orderBy(orderList);
				
			
				TypedQuery<SurvivalBenefitMaster> result = em.createQuery(query);
				list = result.getResultList();
				
				
				if(list.size()>0) {
					Date beforeOneDay = new Date(new Date().getTime() - MILLS_IN_A_DAY);
				
					if ( list.get(0).getEffectiveDateStart().before(beforeOneDay)  ) { // if old start is past,
					
						amendId = list.get(0).getAmendId() + 1 ;
						entryDate = new Date() ;
						createdBy = req.getCreatedBy();
						
						if(req.getSaveType().equalsIgnoreCase("Submit")) { //old record update
							//UPDATE old data
							CriteriaBuilder cb2 = em.getCriteriaBuilder();
						
							CriteriaUpdate<SurvivalBenefitMaster> update = cb2.createCriteriaUpdate(SurvivalBenefitMaster.class);
						
							Root<SurvivalBenefitMaster> m = update.from(SurvivalBenefitMaster.class);
						
							update.set("updatedBy", req.getCreatedBy());
							update.set("updatedDate", entryDate);
							update.set("effectiveDateEnd", oldEndDate);
							
							List<Predicate> predics = new ArrayList<Predicate>();
							predics.add(cb2.equal(m.get("policyTerms"), req.getPolicyTerm()));
							predics.add(cb2.equal(m.get("companyId"), req.getCompanyId()));
							predics.add(cb2.equal(m.get("amendId"), list.get(0).getAmendId() ));
							predics.add(cb2.equal(m.get("productId"),req.getProductId()));
							predics.add(cb.equal(b.get("sectionId"),req.getSectionId()));
							
							update.where(predics.toArray(new Predicate[0]) );
					
							em.createQuery(update).executeUpdate();
						}
						
					} else { //future or today
						amendId = list.get(0).getAmendId() ;
						entryDate = list.get(0).getEntryDate() ;
						createdBy = list.get(0).getCreatedBy();
						
						repo.deleteAll(list);
					
				    }
				}		
				res.setResponse("Updated Successfully");
				
			}
			int sno = 0; 
			List<SurvivalBenefitMaster> list1  = new ArrayList<SurvivalBenefitMaster>();
			
			for ( SurvivalReq data :  req.getSurvivalReq() ) {
				sno = sno + 1;
				// Save New Records
				saveData = mapper.map(data, SurvivalBenefitMaster.class ); 
				
				if(req.getSaveType().equalsIgnoreCase("Save"))
					saveData.setStatus("P");
				
				saveData.setSno(sno);
				saveData.setAmendId(amendId);
				saveData.setCreatedBy(createdBy);
				saveData.setEntryDate(entryDate);
				saveData.setPolicyTerms(req.getPolicyTerm());
				saveData.setEffectiveDateEnd(endDate);
				saveData.setUpdatedBy(req.getCreatedBy());
				saveData.setUpdatedDate(new Date());
				saveData.setCompanyId(req.getCompanyId());
				saveData.setProductId(Integer.valueOf(req.getProductId()));
				saveData.setSectionId(Integer.valueOf(req.getSectionId()));
				saveData.setEffectiveDateStart(req.getEffectiveDateStart());
				
				list1.add(saveData);
			}
				repo.saveAllAndFlush(list1);
			
				res.setSuccessId(req.getPolicyTerm().toString());
				log.info("Saved Details is --> " + json.toJson(saveData));	
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public InsertSurvivalReq getallSurvivalDetails(GetallSurrenderDetailsReq req) {
		InsertSurvivalReq resp = new InsertSurvivalReq();
		List<SurvivalReq> resList = new ArrayList<SurvivalReq>();
		List<SurvivalBenefitMaster> list = new ArrayList<SurvivalBenefitMaster>();
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<SurvivalBenefitMaster> query = cb.createQuery(SurvivalBenefitMaster.class);
		
			Root<SurvivalBenefitMaster> b = query.from(SurvivalBenefitMaster.class);

			query.select(b);
			
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<SurvivalBenefitMaster> ocpm1 = amendId.from(SurvivalBenefitMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("policyTerms"), b.get("policyTerms"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));

			amendId.where(a1,a2,a3,a4);
			
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("endOfYear")));

			List<Predicate> predics = new ArrayList<Predicate>();
			predics.add(cb.equal(b.get("amendId"), amendId));
			predics.add(cb.equal(b.get("companyId"), req.getCompanyId()));
			predics.add(cb.equal(b.get("productId"), req.getProductId()));
			predics.add(cb.equal(b.get("sectionId"), req.getSectionId()));
			predics.add(cb.equal(b.get("policyTerms"), req.getPolicyTerm()));
			
			query.where(predics.toArray(new Predicate[0])).orderBy(orderList);
			
			TypedQuery<SurvivalBenefitMaster> result = em.createQuery(query);
			list = result.getResultList();
			
			if(list.size()>0) {
				
				for (SurvivalBenefitMaster data : list) {
					resp = mapper.map(data, InsertSurvivalReq.class);
					resp.setPolicyTerm(data.getPolicyTerms() );
					SurvivalReq res = new SurvivalReq();
					res = mapper.map(data, SurvivalReq.class);
					resList.add(res); 
				}
				resp.setSurvivalReq(resList);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
		}
		return resp;
	}

	@Override
	public GetoneSurvivalDetailsRes getoneSurvivalDetails(GetoneSurvivalDetailsReq req) {
		GetoneSurvivalDetailsRes res = new GetoneSurvivalDetailsRes();
		List<SurvivalBenefitMaster> list = new ArrayList<SurvivalBenefitMaster>();
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<SurvivalBenefitMaster> query = cb.createQuery(SurvivalBenefitMaster.class);
		
			Root<SurvivalBenefitMaster> b = query.from(SurvivalBenefitMaster.class);

			query.select(b);
			
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<SurvivalBenefitMaster> ocpm1 = amendId.from(SurvivalBenefitMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("policyTerms"), b.get("policyTerms"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
			Predicate a5 = cb.equal(ocpm1.get("endOfYear"), b.get("endOfYear"));

			amendId.where(a1,a2,a3,a4,a5);
			
			List<Predicate> predics = new ArrayList<Predicate>();
			predics.add(cb.equal(b.get("amendId"), amendId));
			predics.add(cb.equal(b.get("companyId"), req.getCompanyId()));
			predics.add(cb.equal(b.get("productId"), req.getProductId()));
			predics.add(cb.equal(b.get("sectionId"), req.getSectionId()));
			predics.add(cb.equal(b.get("policyTerms"), req.getPolicyTerm()));
			predics.add(cb.equal(b.get("endOfYear"), req.getEndOfYear()));
			
			query.where(predics.toArray(new Predicate[0]));
			
			TypedQuery<SurvivalBenefitMaster> result = em.createQuery(query);
			list = result.getResultList();
			
			if(list.size()>0) {
				
				res = mapper.map(list.get(0), GetoneSurvivalDetailsRes.class );
				
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
		}
		return res;
	} 
	
}
