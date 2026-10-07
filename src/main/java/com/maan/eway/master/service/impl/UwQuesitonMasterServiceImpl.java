package com.maan.eway.master.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.UWQuestionsMaster;
import com.maan.eway.bean.UwQuestionsOptionsMaster;
import com.maan.eway.master.req.OptionsReq;
import com.maan.eway.master.req.UwQuestionChangeStatusReq;
import com.maan.eway.master.req.UwQuestionMasterGetReq;
import com.maan.eway.master.req.UwQuestionMasterSaveReq;
import com.maan.eway.master.req.UwQuestionsMasterGetAllReq;
import com.maan.eway.master.res.OptionsRes;
import com.maan.eway.master.res.UwQuestionMasterRes;
import com.maan.eway.master.service.UwQuestionMasterService;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.UwQuestionMasterRepository;
import com.maan.eway.repository.UwQuestionsOptionsMasterRepository;
import com.maan.eway.res.DropDownRes;
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
@Transactional
public class UwQuesitonMasterServiceImpl implements UwQuestionMasterService {

	@Autowired
	private UwQuestionMasterRepository repo;

	@PersistenceContext
	private EntityManager em;

	Gson json = new Gson();
	
	@Autowired
	private LoginBranchMasterRepository loginBranchRepo ;
	

	
	@Autowired
	private LoginMasterRepository loginRepo ;
	
	@Autowired
	private UwQuestionsOptionsMasterRepository optionsRepo;

	private Logger log = LogManager.getLogger(UwQuesitonMasterServiceImpl.class);

	@Override
	public List<String> validateUwQuestions(UwQuestionMasterSaveReq req) {
		List<String> errorList = new ArrayList<String>();

		try {
			if (StringUtils.isBlank(req.getQuestionCategory())) {
			//	errorList.add(new Error("02", "QuestionCategory", "Please Select Question Category"));
				errorList.add("1632");
			}
			
			if (StringUtils.isBlank(req.getUwQuestionDesc())) {
			//	errorList.add(new Error("02", "QuestionCategory Desc", "Please Select Question Category Desc"));
				errorList.add("1633");
			}
			
		
			if (StringUtils.isBlank(req.getUwQuestionDesc())) {
		//		errorList.add(new Error("02", "UwQuestionDesc", "Please Select UwQuestionDesc"));
				errorList.add("1634");
			}else if (req.getUwQuestionDesc().length() > 500){
			//	errorList.add(new Error("02","UwQuestionDesc", "Please Enter UwQuestionDesc 500 Characters")); 
				errorList.add("1635");
			}
			
			
			if (StringUtils.isBlank(req.getCompanyId())) {
			//	errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
				errorList.add("1255");
			}
			/*
			 * if (StringUtils.isBlank(req.getSectionId())) { // errorList.add(new
			 * Error("02", "CompanyId", "Please Enter CompanyId")); errorList.add("1255"); }
			 */
			
			if (StringUtils.isBlank(req.getBranchCode())) {
		//		errorList.add(new Error("02", "BranchCode", "Please Select BranchCode"));
				errorList.add("1636");
			}
			if (StringUtils.isBlank(req.getQuestionType())) {
		//		errorList.add(new Error("03", "QuestionType", "Please Select QuestionType"));
				errorList.add("1637");
			}else if (req.getQuestionType().length() > 100){
			//	errorList.add(new Error("03","QuestionType", "Please Enter QuestionType 100 Characters")); 
				errorList.add("1638");
			} 
			if(req.getQuestionType().equalsIgnoreCase("02")){
				if (StringUtils.isBlank(req.getDataType())) {
			//		errorList.add(new Error("03", "DataType", "Please Select DataType"));
					errorList.add("1639");
				}	
			}
			
			if (StringUtils.isBlank(req.getRemarks())) {
		//		errorList.add(new Error("04", "Remarks", "Please Select Remarks "));
				errorList.add("1259");
			}else if (req.getRemarks().length() > 100){
		//		errorList.add(new Error("04","Remarks", "Please Enter Remarks within 100 Characters")); 
				errorList.add("1260");
			}
			
			// Date Validation 
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);;
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null || StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
	//			errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start"));
				errorList.add("1261");

			} else if (req.getEffectiveDateStart().before(today)) {
		//		errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
				errorList.add("1262");
			}
			//Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
	//			errorList.add(new Error("05", "Status", "Please Select Status  "));
				errorList.add("1263");
			} else if (req.getStatus().length() > 1) {
		//		errorList.add(new Error("05", "Status", "Please Select Valid Status - One Character Only Allwed"));
				errorList.add("1264");
			}else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
		//		errorList.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
				errorList.add("1265");
			}

//			if (StringUtils.isBlank(req.getCoreAppCode())) {
//				errorList.add(new Error("07", "CoreAppCode", "Please Select CoreAppCode"));
//			}else if (req.getCoreAppCode().length() > 20){
//				errorList.add(new Error("07","CoreAppCode", "Please Enter CoreAppCode within 20 Characters")); 
//			}
//			if (StringUtils.isBlank(req.getRegulatoryCode())) {
//				errorList.add(new Error("08", "RegulatoryCode", "Please Select RegulatoryCode"));
//			}else if (req.getRegulatoryCode().length() > 20){
//				errorList.add(new Error("08","RegulatoryCode", "Please Enter RegulatoryCode within 20 Characters")); 
//			}
			if (StringUtils.isBlank(req.getCreatedBy())) {
		//		errorList.add(new Error("09", "CreatedBy", "Please Select CreatedBy"));
				errorList.add("1270");
			}else if (req.getCreatedBy().length() > 100){
		//		errorList.add(new Error("09","CreatedBy", "Please Enter CreatedBy within 100 Characters")); 
				errorList.add("1271");
			}
			
			
			
			List<String> opsId = new ArrayList<String>();
			List<String> opsDesc = new ArrayList<String>();
			
			//Options Validation
			if(req.getQuestionType().equalsIgnoreCase("01")) { //Radio Button
				
				if(req.getOptionsReq().size()<=0) {
			//		errorList.add(new Error("01","Options", "Please Add Atleast One Option Details")); 
					errorList.add("1640");
				}	else {
					int row = 0;
					for(OptionsReq ops : req.getOptionsReq()) {
						row = row + 1;
						

						if (StringUtils.isBlank(ops.getUwQuesOptionId())) {
				//			errorList.add(new Error("07", "Option Id", "Please Enter Value in Row "+ row));
							errorList.add("1641" + "," + row);
						}else {
							
							if(opsId.contains(ops.getUwQuesOptionId())) {
								errorList.add("1642" + "," + row);
					//			errorList.add(new Error("07","Option Id", "Duplicate Value Entered in Row " + row)); 
							}
							
							opsId.add(ops.getUwQuesOptionId());
							if ( ! isNumeric(ops.getUwQuesOptionId())  ) {
								errorList.add("1643" + "," + row);
						//		errorList.add(new Error("07","Option Id", "Please Enter Value in Numeric Only in Row " + row));
								}
						}
						
						if (StringUtils.isBlank(ops.getUwQuesOptionDesc())) {
					//		errorList.add(new Error("07", "Option Desc", "Please Enter Display Name in Row "+ row));
							errorList.add("1644" + "," + row);
						}else {
							
							if(opsDesc.contains(ops.getUwQuesOptionDesc())) {
						//		errorList.add(new Error("07","Option Desc", "Duplicate Display Name Entered in Row " + row)); 
								errorList.add("1645" + "," + row);
							}
							
							opsDesc.add(ops.getUwQuesOptionDesc());
							
							if ( ops.getUwQuesOptionDesc().length()>100  ) {
					//			errorList.add(new Error("07","Option Desc", "Please Enter Display Name within 100 Characters in Row " + row)); 
								errorList.add("1646" + "," + row);}
						}
						
						if (StringUtils.isBlank(ops.getStatus())) {
					//		errorList.add(new Error("05", "Status", "Please Select Status in Row "+ row));
							errorList.add("1483" + "," + row);
						} else if (ops.getStatus().length() > 1) {
					//		errorList.add(new Error("05", "Status", "Please Select Valid Status - One Character Only Allwed in row" + row));
							errorList.add("1484" + "," + row);
						}
						
						if (StringUtils.isBlank(ops.getLoadingPercent())) {
					//		errorList.add(new Error("07", "Loading", "Please Enter Loading in Row "+ row));
							errorList.add("1647" + "," + row);
						}else if (!ops.getLoadingPercent().matches("[-0-9.]+")  ){
					//		errorList.add(new Error("07","Loading", "Please Enter Loading in Numeric Only in Row " + row)); 
							errorList.add("1648" + "," + row);
						}
						
						//DependantYN
						if (StringUtils.isBlank(ops.getDependentYn())) {
					//		errorList.add(new Error("05", "DependentYn", "Please Select DependentYn in Row "+ row));
							errorList.add("1649" + "," + row);
						} else {
							
							if (ops.getDependentYn().length() > 1) {
							//	errorList.add(new Error("05", "DependentYn", "Please Select Valid DependentYn - One Character Only Allowed in row" + row));
							errorList.add("1650" + "," + row); }
						
							if(ops.getDependentYn().equalsIgnoreCase("Y")) {
							
								if (CollectionUtils.isEmpty(ops.getDependentUnderwriterId())) {
								//	errorList.add(new Error("07", "Dependent Question", "Please Select Dependent Question in Row "+ row));
									errorList.add("1651" + "," + row);
								}
								
//								if (StringUtils.isBlank(ops.getDependentUwAction())) {
//									errorList.add(new Error("07", "Dependent Question Action", "Please Enter Dependent Question Action in Row "+ row));
//								}else if ( ops.getDependentUwAction().length()>100  ){
//									errorList.add(new Error("07","Dependent Question Action", "Please Enter Dependent Question Action within 100 Characters in Row " + row)); 
//								}
						
							}
						}
						
						if (StringUtils.isBlank(ops.getReferralYn())) {
						//	errorList.add(new Error("05", "Referral", "Please Select Referral in Row "+ row));
							errorList.add("1654" + "," + row);
						} else if (ops.getReferralYn().length() > 1) {
						//	errorList.add(new Error("05", "Referral", "Please Select Valid Referral - One Character Only Allowed in row" + row));
							errorList.add("1655" + "," + row);
						}
				}
			
			}
			
			}
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}
	public List<UWQuestionsMaster> getUwQuestionDescExistDetails(String UWQuestionDesc , String InsuranceId , String branchCode, String productId, String Status) {
		List<UWQuestionsMaster> list = new ArrayList<UWQuestionsMaster>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<UWQuestionsMaster> query = cb.createQuery(UWQuestionsMaster.class);

			// Find All
			Root<UWQuestionsMaster> b = query.from(UWQuestionsMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<UWQuestionsMaster> ocpm1 = amendId.from(UWQuestionsMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("uwQuestionId"), b.get("uwQuestionId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a5 = cb.greaterThanOrEqualTo(ocpm1.get("effectiveDateEnd"), today);
			Predicate a6 = cb.equal(ocpm1.get("productId"), b.get("productId"));

			amendId.where(a1,a2,a3,a4,a5,a6);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(cb.lower( b.get("uwQuestionDesc")), UWQuestionDesc.toLowerCase());
			Predicate n3 = cb.equal(b.get("companyId"),InsuranceId);
			Predicate n4 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n4,n5);
			Predicate n7 = cb.equal(b.get("productId"),productId);
			
			query.where(n1,n2,n3,n6,n7);
			
			// Get Result
			TypedQuery<UWQuestionsMaster> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return list;
	}
	@Override
	public SuccessRes insertUwQuestions(UwQuestionMasterSaveReq req) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		UWQuestionsMaster saveData = new UWQuestionsMaster();
		List<UWQuestionsMaster> list  = new ArrayList<UWQuestionsMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		Integer uwQuestionId = 0;
		try {
			Integer amendId = 0;
			Date StartDate = req.getEffectiveDateStart();
			String end = "31/12/2050";
			Date endDate = sdf.parse(end);
			long MILLS_IN_A_DAY = 1000*60*60*24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime()- MILLS_IN_A_DAY);
			Date entryDate = null;
			String createdBy ="";
		
			if(req.getUwQuestionId()==null||StringUtils.isBlank(req.getUwQuestionId())) {
				Integer totalCount = getMasterTableCount(req.getCompanyId(),req.getBranchCode(),req.getProductId(),req.getSectionId());
				uwQuestionId = totalCount+1;
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully");
				res.setSuccessId(uwQuestionId.toString());
			}
			else {
				uwQuestionId = Integer.valueOf(req.getUwQuestionId());
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<UWQuestionsMaster> query = cb.createQuery(UWQuestionsMaster.class);
				//Findall
				Root<UWQuestionsMaster> b = query.from(UWQuestionsMaster.class);
				//select
				query.select(b);
				//Orderby
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("effectiveDateStart")));
				//Where
				Predicate n1 = cb.equal(b.get("uwQuestionId"),req.getUwQuestionId());
				Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
				Predicate n3 = cb.equal(b.get("branchCode"),req.getBranchCode());
				Predicate n4 = cb.equal(b.get("productId"),req.getProductId());
				Predicate n7 = cb.equal(b.get("sectionId"),req.getSectionId());
				Predicate n5 = cb.equal(b.get("branchCode"), "99999");
				
				Predicate n6 = cb.or(n3,n5);
			
				query.where(n1,n2,n6,n4,n7).orderBy(orderList);
				
				// Get Result
				TypedQuery<UWQuestionsMaster> result = em.createQuery(query);
				int limit=0, offset=2;
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				list = result.getResultList();
				if(list.size()>0) {
					Date beforeOneDay = new Date(new Date().getTime()- MILLS_IN_A_DAY);
					if(list.get(0).getEffectiveDateStart().before(beforeOneDay)) {
						amendId = list.get(0).getAmendId()+1;
						entryDate = new Date();
						createdBy = req.getCreatedBy();
						UWQuestionsMaster lastRecord = list.get(0);
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					}
					else {
						amendId = list.get(0).getAmendId();
						entryDate = list.get(0).getEntryDate();
						createdBy = list.get(0).getCreatedBy();
					//	saveData = list.get(0);
						if(list.size()>1) {
							UWQuestionsMaster lastRecord = list.get(1);	
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}
					}
				}
				res.setResponse("Updated Successfully");
				res.setSuccessId(uwQuestionId.toString());
			}
			dozerMapper.map(req, saveData);
			saveData.setUwQuestionId(uwQuestionId);
			saveData.setEffectiveDateStart(StartDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setEntryDate(entryDate);
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setAmendId(amendId);
			saveData.setBranchCode(req.getBranchCode());
			saveData.setSectionId(req.getSectionId());
			repo.saveAndFlush(saveData);	
			
			//Options
			List<UwQuestionsOptionsMaster> optionsList = new ArrayList<UwQuestionsOptionsMaster>();
					
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<UwQuestionsOptionsMaster> query1 = cb.createQuery(UwQuestionsOptionsMaster.class);
			
			Root<UwQuestionsOptionsMaster> b = query1.from(UwQuestionsOptionsMaster.class);
			
			query1.select(b);
			
			
			Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"),req.getBranchCode());
			Predicate n4 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
		    Predicate n7 = cb.equal(b.get("sectionId"), req.getSectionId());
			Predicate n6 = cb.or(n3,n5);
		
			query1.where(n2,n6,n4,n7);
			
			// Get Result
			TypedQuery<UwQuestionsOptionsMaster> result1 = em.createQuery(query1);
			optionsList = result1.getResultList();
			
			
			Integer quesId = uwQuestionId;
			//Options save
			if( req.getOptionsReq().size()>0) {
				
				if(optionsList.size()>0) {
					
					//old delete
					List<UwQuestionsOptionsMaster> optionsfilter =optionsList.stream().filter(o -> o.getDependentUwQuestionId().equals(quesId))
							.collect(Collectors.toList());
					
					optionsRepo.deleteAll(optionsfilter);
				}
				
				String listAsString = "";
				List<UwQuestionsOptionsMaster> opsList = new ArrayList<UwQuestionsOptionsMaster>();
				for(OptionsReq options : req.getOptionsReq()) {
					
					if(!CollectionUtils.isEmpty(options.getDependentUnderwriterId())){
//						listAsString = options.getDependentUnderwriterId().stream()
//					                .collect(Collectors.joining(" "));
						
						listAsString =  String.join(",", options.getDependentUnderwriterId()); ;
						listAsString = listAsString.replace("[", "").replace("]", "");
						
					}
					
				
					
					UwQuestionsOptionsMaster ops =  UwQuestionsOptionsMaster.builder()
							.amendId(amendId)
							.branchCode(req.getBranchCode())
							.companyId(req.getCompanyId())
							.productId(Integer.valueOf(req.getProductId()))
							.dependentUwQuestionId(Integer.valueOf(quesId))
							.effectiveDateEnd(endDate)
							.effectiveDateStart(StartDate)
							.entryDate(entryDate)
							
							.uwQuesOptionDesc(options.getUwQuesOptionDesc())  //DisplayName (i.e, Options)
							.uwQuesOptionId(StringUtils.isBlank(options.getUwQuesOptionId())?null:Integer.valueOf(options.getUwQuesOptionId())) //value
							.dependentYn(options.getDependentYn())

					//		.dependentUnderwriterId(StringUtils.isBlank(options.getDependentUnderwriterId())?null:options.getDependentUnderwriterId()) //dropdown 

							.dependentUnderwriterId(CollectionUtils.isEmpty(options.getDependentUnderwriterId())?null:listAsString) //dropdown 

							.dependentUwAction(options.getDependentUwAction()==null?"":options.getDependentUwAction())
							.loadingPercent(StringUtils.isBlank(options.getLoadingPercent())?null:new BigDecimal(options.getLoadingPercent()))
							.referralYn(options.getReferralYn()) 
							.status(options.getStatus())
							.sectionId(req.getSectionId())
							.build();
					opsList.add(ops);
				}
				optionsRepo.saveAllAndFlush(opsList);
			
			}
			
			
			log.info("Saved Details is --> " + json.toJson(saveData));	
			}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
		}
		
	public Integer getMasterTableCount(String companyId, String branchCode, String productId,String Sectionid)	{

		Integer data =0;
		try {
			List<UWQuestionsMaster> list = new ArrayList<UWQuestionsMaster>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<UWQuestionsMaster> query = cb.createQuery(UWQuestionsMaster.class);
			//Find all
			Root<UWQuestionsMaster> b = query.from(UWQuestionsMaster.class);
			// Select
			query.select(b);
			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<UWQuestionsMaster> ocpm1 = effectiveDate.from(UWQuestionsMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(ocpm1.get("uwQuestionId"),b.get("uwQuestionId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"),b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
			Predicate a5 = cb.equal(ocpm1.get("sectionId"),b.get("sectionId"));


			effectiveDate.where(a1,a2,a3,a4,a5);
		
			//OrderBy
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("uwQuestionId")));
			
			Predicate n1 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"),companyId);
			Predicate n3 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3,n4);
			Predicate n6 = cb.equal(b.get("productId"), productId);
		//	Predicate n7 = cb.equal(b.get("sectionId"),Sectionid);

			//query.where(n1,n2,n5,n6,n7).orderBy(orderList);
			
			
			query.where(n1,n2,n5,n6).orderBy(orderList);

			// Get Result
			TypedQuery<UWQuestionsMaster> result = em.createQuery(query);
			int limit = 0 , offset = 1 ;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			data = list.size() > 0 ? list.get(0).getUwQuestionId() : 0 ;
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return data;
	}

	@Override
	public List<UwQuestionMasterRes> getallUwQuestions(UwQuestionsMasterGetAllReq req) {
		List<UwQuestionMasterRes> resList = new ArrayList<UwQuestionMasterRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		if(req.getSectionId()==null)return null;
		try {
			List<UWQuestionsMaster> list = new ArrayList<UWQuestionsMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<UWQuestionsMaster> query = cb.createQuery(UWQuestionsMaster.class);

			// Find All
			Root<UWQuestionsMaster> b = query.from(UWQuestionsMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<UWQuestionsMaster> ocpm1 = amendId.from(UWQuestionsMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("uwQuestionId"), b.get("uwQuestionId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a5 = cb.equal(ocpm1.get("questionCategory"),req.getQuestionCategory());

			amendId.where(a1, a2,a3,a4,a5);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));
			orderList.add(cb.asc(b.get("uwQuestionId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3,n4);
			Predicate n6 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n7 = cb.equal(b.get("questionCategory"), req.getQuestionCategory());

			if(!req.getSectionId().equals("99999"))
			{
				Predicate n8 = cb.equal(b.get("sectionId"), req.getSectionId());
				query.where(n1,n2,n5,n6,n7,n8).orderBy(orderList);
			}else {
				query.where(n1,n2,n5,n6,n7).orderBy(orderList);
	
			}

			// Get Result
			TypedQuery<UWQuestionsMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getUwQuestionId()))).collect(Collectors.toList());
		//	list.sort(Comparator.comparing(UWQuestionsMaster :: getUwQuestionDesc ));
			Set<String> Sectionid=list.stream().filter(a->a.getSectionId().equals("99999")).map(a->a.getSectionId()).
					collect(Collectors.toSet());
		    Map<String,String> sectiondetails=getsectionid(req,Sectionid);

			// Map
			for (UWQuestionsMaster data : list) {
				UwQuestionMasterRes res = new UwQuestionMasterRes();

				res = mapper.map(data, UwQuestionMasterRes.class);
			//	res.setCoreAppCode(data.getCoreAppCode());
				res.setSectionName(sectiondetails.get(res.getSectionId()));
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;

		}
		return resList;
	}
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	@Override
	public List<UwQuestionMasterRes> getActiveUwQuestions(UwQuestionsMasterGetAllReq req) {
		List<UwQuestionMasterRes> resList = new ArrayList<UwQuestionMasterRes>();
		
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

	//		 LoginMaster loginData =  loginRepo.findByLoginId(req.getLoginId());
		
			List<UWQuestionsMaster> list = new ArrayList<UWQuestionsMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<UWQuestionsMaster> query = cb.createQuery(UWQuestionsMaster.class);

			// Find All 
			Root<UWQuestionsMaster> b = query.from(UWQuestionsMaster.class);
			

			// Select
			query.select(b);
			
			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<UWQuestionsMaster> ocpm1 = effectiveDate.from(UWQuestionsMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(b.get("uwQuestionId"),ocpm1.get("uwQuestionId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(b.get("companyId"),ocpm1.get("companyId"));
			Predicate a4 = cb.equal(b.get("branchCode"),ocpm1.get("branchCode"));
			Predicate a5 = cb.equal(b.get("productId"),ocpm1.get("productId"));
			Predicate a13 = cb.equal(b.get("sectionId"),ocpm1.get("sectionId"));

			Predicate a11 = cb.equal(b.get("questionCategory"),ocpm1.get("questionCategory"));

			effectiveDate.where(a1,a2,a3,a4,a5,a11,a13);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<UWQuestionsMaster> ocpm2 = effectiveDate2.from(UWQuestionsMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a6 = cb.equal(b.get("uwQuestionId"),ocpm2.get("uwQuestionId"));
			Predicate a7 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a8 = cb.equal(b.get("companyId"),ocpm2.get("companyId"));
			Predicate a9 = cb.equal(b.get("branchCode"),ocpm2.get("branchCode"));
			Predicate a10 = cb.equal(b.get("productId"),ocpm2.get("productId"));
			Predicate a12 = cb.equal(b.get("questionCategory"),ocpm2.get("questionCategory"));
			Predicate a14 = cb.equal(b.get("sectionId"),ocpm2.get("sectionId"));

			effectiveDate2.where(a6,a7,a8,a9,a10,a12,a14);
			
			//amendId

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));
			orderList.add(cb.asc(b.get("uwQuestionId")));

			// Where
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"),  req.getBranchCode() );
			Predicate n4 = cb.equal(b.get("status"), "Y");
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n3,n5);
			Predicate n7 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n8 = cb.equal(b.get("status"), "R");
			Predicate n9 = cb.or(n4,n8);
			Predicate n10 = cb.equal(b.get("effectiveDateEnd"), effectiveDate2);
			Predicate n11 = cb.equal(b.get("questionCategory"), req.getProductId().equalsIgnoreCase("45")?req.getQuestionCategory()
					: "99999"); //anticipated endowmwnt
			if(req.getSectionId().equals("99999"))
			{
				query.where(n1,n2,n9,n6,n7,n10,n11).orderBy(orderList);
	
			}else {
			Predicate n12 = cb.equal(b.get("sectionId"), req.getSectionId());

			query.where(n1,n2,n9,n6,n7,n10,n11,n12).orderBy(orderList);
			
			}
			// Get Result
			TypedQuery<UWQuestionsMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getUwQuestionId()))).collect(Collectors.toList());
		//	list.sort(Comparator.comparing(UWQuestionsMaster :: getUwQuestionDesc ));
			
			
			//Options
			List<UwQuestionsOptionsMaster> optionsList = new ArrayList<UwQuestionsOptionsMaster>();
					
			CriteriaBuilder cb1 = em.getCriteriaBuilder();
			CriteriaQuery<UwQuestionsOptionsMaster> query1 = cb1.createQuery(UwQuestionsOptionsMaster.class);
			
			Root<UwQuestionsOptionsMaster> opst = query1.from(UwQuestionsOptionsMaster.class);
			
			query1.select(opst);
			
			
			Predicate m2 = cb1.equal(opst.get("companyId"),req.getCompanyId());
			Predicate m3 = cb1.equal(opst.get("branchCode"),req.getBranchCode());
			Predicate m4 = cb1.equal(opst.get("productId"),req.getProductId());
			Predicate m5 = cb1.equal(opst.get("branchCode"), "99999");
			Predicate m6 = cb1.or(m3,m5);
              
			if(!req.getSectionId().equals("99999")) {
			Predicate m7 = cb1.equal(opst.get("sectionId"), req.getSectionId());
			query1.where(m2,m6,m4,m7);
			}else {
				query1.where(m2,m6,m4);
             }
			// Get Result
			TypedQuery<UwQuestionsOptionsMaster> result1 = em.createQuery(query1);
			optionsList = result1.getResultList();
		    Set<String> SectionId=optionsList.stream().filter(a->!a.equals("99999")).map(B->B.getSectionId())
		    		.collect(Collectors.toSet()); 
		    Map<String,String> sectiondetails=getsectionid(req,SectionId);

			// Map
			for (UWQuestionsMaster data : list) {
				UwQuestionMasterRes res = new UwQuestionMasterRes();
				res = mapper.map(data, UwQuestionMasterRes.class);
			  //	res.setCoreAppCode(data.getCoreAppCode());
				
				List<UwQuestionsOptionsMaster> optionsfilter =optionsList.stream().filter(o -> o.getDependentUwQuestionId().equals(data.getUwQuestionId()))
						.collect(Collectors.toList());
				if(optionsfilter.size()>0) {
					List<OptionsRes> optionsRes = new ArrayList<OptionsRes>();
					for(UwQuestionsOptionsMaster ops : optionsfilter ) {
						OptionsRes options = new OptionsRes();
						//options = mapper.map(ops, OptionsRes.class);
						
						options.setDependentUwAction(ops.getDependentUwAction()==null?"":ops.getDependentUwAction());
						options.setDependentYn(ops.getDependentYn()==null?"":ops.getDependentYn());
						options.setLoadingPercent(ops.getLoadingPercent()==null?"0":ops.getLoadingPercent().toString());
						options.setReferralYn(ops.getReferralYn()==null?"":ops.getReferralYn());
						options.setStatus(ops.getStatus()==null?"":ops.getStatus());
						options.setUwQuesOptionDesc(ops.getUwQuesOptionDesc()==null?"":ops.getUwQuesOptionDesc());
						options.setUwQuesOptionId(ops.getUwQuesOptionId()==null?"":ops.getUwQuesOptionId().toString());
						
						List<String> dependantIds = new ArrayList<String>(ops.getDependentUnderwriterId()==null?Collections.emptyList() : Arrays.asList(ops.getDependentUnderwriterId().split(",")));
						options.setDependentUnderwriterId(dependantIds);
						
						optionsRes.add(options);
					}
					res.setOptionsRes(optionsRes);
					//SectionName
					
					
					res.setSectionName(sectiondetails.get(data.getSectionId()));
				}
				
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;

		}
		return resList;
	}

	
	public Map<String,String> getsectionid(UwQuestionsMasterGetAllReq req,Set<String> SectionId)
	{
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
        Map<String,String> SectionDetails= new HashMap<String, String>();
		try {
			Date today = req.getEffectiveDateStart() != null ? req.getEffectiveDateStart() : new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProductSectionMaster> query = cb.createQuery(ProductSectionMaster.class);
			List<ProductSectionMaster> list = new ArrayList<ProductSectionMaster>();

			// Find All
			Root<ProductSectionMaster> c = query.from(ProductSectionMaster.class);

			query.select(c);
			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ProductSectionMaster> ocpm1 = amendId.from(ProductSectionMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			jakarta.persistence.criteria.Predicate a1 = cb.equal(c.get("sectionId"), ocpm1.get("sectionId"));
			jakarta.persistence.criteria.Predicate a2 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			jakarta.persistence.criteria.Predicate a3 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			// jakarta.persistence.criteria.Predicate a4 =
			// cb.lessThanOrEqualTo(c.get("effectiveDateStart"),today ) ;
			// Predicate a5 = cb.equal(ocpm1.get("branchCode"),c.get("branchCode"));
			amendId.where(a1, a2, a3);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(c.get("effectiveDateStart")));

			// Where

			Predicate n1 = cb.equal(c.get("amendId"), amendId);
			Predicate n2 = c.get("sectionId").in(SectionId);
			Predicate n3 = cb.equal(c.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(c.get("companyId"), req.getCompanyId());
			query.where(n1, n2, n3, n4).orderBy(orderList);

			// Get Result
			TypedQuery<ProductSectionMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getSectionId())))
					.collect(Collectors.toList());
			list.sort(Comparator.comparing(ProductSectionMaster::getSectionName));
			if(list!=null||!list.isEmpty()) {
				SectionDetails= list.stream().collect(Collectors.toMap(a->String.valueOf(a.getSectionId()), ProductSectionMaster::getSectionName));
			}
			return SectionDetails;
		}catch(Exception Ex)
		{
			Ex.printStackTrace();
			log.info("Exception is ---> " + Ex.getMessage());
			return null;
			
		}

	}
	@Override
	public UwQuestionMasterRes getByUwQuestionId(UwQuestionMasterGetReq req) {
		UwQuestionMasterRes res = new UwQuestionMasterRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<UWQuestionsMaster> list = new ArrayList<UWQuestionsMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<UWQuestionsMaster> query = cb.createQuery(UWQuestionsMaster.class);

			// Find All
			Root<UWQuestionsMaster> b = query.from(UWQuestionsMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<UWQuestionsMaster> ocpm1 = amendId.from(UWQuestionsMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("uwQuestionId"), b.get("uwQuestionId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("uwQuestionId"),b.get("uwQuestionId"));
			Predicate a5 = cb.equal(ocpm1.get("productId"),b.get("productId"));

			amendId.where(a1, a2,a3,a4,a5);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));
			orderList.add(cb.asc(b.get("uwQuestionId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("uwQuestionId"), req.getUwQuestionId());
			Predicate n6 = cb.equal(b.get("branchCode"), "99999");
			Predicate n8 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n9 = cb.equal(b.get("sectionId"), req.getSectionId());

			Predicate n7 = cb.or(n3,n6);
			query.where(n1,n2,n4,n7,n8,n9).orderBy(orderList);
			
			// Get Result
			TypedQuery<UWQuestionsMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getUwQuestionId()))).collect(Collectors.toList());
		//	list.sort(Comparator.comparing(UWQuestionsMaster :: getUwQuestionDesc ));
			
//			List<UwQuestionsOptionsMaster> optionsList = optionsRepo.findByCompanyIdAndBranchCodeOrBranchCodeAndProductId(req.getCompanyId(),req.getBranchCode(),
//					"99999",Integer.valueOf(req.getProductId()));
			
			//Options
			List<UwQuestionsOptionsMaster> optionsList = new ArrayList<UwQuestionsOptionsMaster>();
					
			CriteriaBuilder cb1 = em.getCriteriaBuilder();
			CriteriaQuery<UwQuestionsOptionsMaster> query1 = cb1.createQuery(UwQuestionsOptionsMaster.class);
			
			Root<UwQuestionsOptionsMaster> opst = query1.from(UwQuestionsOptionsMaster.class);
			
			query1.select(opst);
			
			
			Predicate m2 = cb1.equal(opst.get("companyId"),req.getCompanyId());
			Predicate m3 = cb1.equal(opst.get("branchCode"),req.getBranchCode());
			Predicate m4 = cb1.equal(opst.get("productId"),req.getProductId());
			Predicate m5 = cb1.equal(opst.get("branchCode"), "99999");
			Predicate m7 = cb1.equal(opst.get("sectionId"), req.getSectionId());

			Predicate m6 = cb1.or(m3,m5);
		
			query1.where(m2,m6,m4,m7);
			
			// Get Result
			TypedQuery<UwQuestionsOptionsMaster> result1 = em.createQuery(query1);
			optionsList = result1.getResultList();
			
			
			if(list!=null &&list.size()>0) {
				res = mapper.map(list.get(0), UwQuestionMasterRes.class);
				res.setUwQuestionId(list.get(0).getUwQuestionId().toString());
				res.setEntryDate(list.get(0).getEntryDate());
				res.setEffectiveDateStart(list.get(0).getEffectiveDateStart());
				res.setEffectiveDateEnd(list.get(0).getEffectiveDateEnd());
				//res.setCoreAppCode(list.get(0).getCoreAppCode());
				int quesId = list.get(0).getUwQuestionId();
				
				List<UwQuestionsOptionsMaster> optionsfilter =optionsList.stream().filter(o -> o.getDependentUwQuestionId().equals(quesId))
						.collect(Collectors.toList());
				
				if(optionsfilter.size()>0) {
					List<OptionsRes> optionsRes = new ArrayList<OptionsRes>();
					for(UwQuestionsOptionsMaster ops : optionsfilter ) {
						OptionsRes options = new OptionsRes();
						//options = mapper.map(ops, OptionsRes.class);
						
						options.setDependentUwAction(ops.getDependentUwAction()==null?"":ops.getDependentUwAction());
						options.setDependentYn(ops.getDependentYn()==null?"":ops.getDependentYn());
						options.setLoadingPercent(ops.getLoadingPercent()==null?"0":ops.getLoadingPercent().toString());
						options.setReferralYn(ops.getReferralYn()==null?"":ops.getReferralYn());
						options.setStatus(ops.getStatus()==null?"":ops.getStatus());
						options.setUwQuesOptionDesc(ops.getUwQuesOptionDesc()==null?"":ops.getUwQuesOptionDesc());
						options.setUwQuesOptionId(ops.getUwQuesOptionId()==null?"":ops.getUwQuesOptionId().toString());
						
						List<String> dependantIds = new ArrayList<String>(ops.getDependentUnderwriterId()==null?Collections.emptyList() : Arrays.asList(ops.getDependentUnderwriterId().split(",")));
						options.setDependentUnderwriterId(dependantIds);
						
						optionsRes.add(options);
					}
					res.setOptionsRes(optionsRes);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public SuccessRes changeStatusOfUwQuestion(UwQuestionChangeStatusReq req) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<UWQuestionsMaster> list = new ArrayList<UWQuestionsMaster>();
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<UWQuestionsMaster> query = cb.createQuery(UWQuestionsMaster.class);
			// Find all
			Root<UWQuestionsMaster> b = query.from(UWQuestionsMaster.class);
			//Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<UWQuestionsMaster> ocpm1 = amendId.from(UWQuestionsMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("uwQuestionId"), b.get("uwQuestionId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));

			amendId.where(a1, a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n3,n5);
			Predicate n7 = cb.equal(b.get("uwQuestionId"), req.getUwQuestionId());

			query.where(n1,n2,n4,n6,n7).orderBy(orderList);
			
			// Get Result 
			TypedQuery<UWQuestionsMaster> result = em.createQuery(query);
			list = result.getResultList();
			UWQuestionsMaster updateRecord = list.get(0);
			if(  req.getBranchCode().equalsIgnoreCase(updateRecord.getBranchCode())) {
				updateRecord.setStatus(req.getStatus());
				repo.save(updateRecord);
			} else {
				UWQuestionsMaster saveNew = new UWQuestionsMaster();
				dozerMapper.map(updateRecord,saveNew);
				saveNew.setBranchCode(req.getBranchCode());
				saveNew.setStatus(req.getStatus());
				repo.save(saveNew);
			}
		
			// Perform Update
			res.setResponse("Status Changed");
			res.setSuccessId(req.getUwQuestionId());
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
			}
		return res;
	}

	@Override
	public List<DropDownRes> getUwQuestionMasterDropdown(UwQuestionMasterGetReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
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
			CriteriaQuery<UWQuestionsMaster> query = cb.createQuery(UWQuestionsMaster.class);
			List<UWQuestionsMaster> list = new ArrayList<UWQuestionsMaster>();
			// Find All
			Root<UWQuestionsMaster> c = query.from(UWQuestionsMaster.class);
			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("uwQuestionDesc")));

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<UWQuestionsMaster> ocpm1 = effectiveDate.from(UWQuestionsMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("uwQuestionId"), ocpm1.get("uwQuestionId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			Predicate a9 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a10 = cb.equal(c.get("branchCode"), ocpm1.get("branchCode"));
			
			effectiveDate.where(a1, a2, a3,a9,a10);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<UWQuestionsMaster> ocpm2 = effectiveDate2.from(UWQuestionsMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a6 = cb.equal(c.get("uwQuestionId"), ocpm2.get("uwQuestionId"));
			Predicate a7 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a8 = cb.equal(c.get("productId"), ocpm2.get("productId"));
			Predicate a11 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a12 = cb.equal(c.get("branchCode"), ocpm2.get("branchCode"));
			
			effectiveDate2.where(a6, a7, a8,a11,a12);
			// Where
			// Where

			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n11 = cb.equal(c.get("status"),"R");
			Predicate n12 = cb.or(n1,n11);
			jakarta.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			jakarta.persistence.criteria.Predicate n3 = cb.equal(c.get("companyId"), req.getCompanyId());
			jakarta.persistence.criteria.Predicate n4 = cb.equal(c.get("productId"), req.getProductId());
			jakarta.persistence.criteria.Predicate n5 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			jakarta.persistence.criteria.Predicate n8 = cb.equal(c.get("branchCode"), req.getBranchCode());
			jakarta.persistence.criteria.Predicate n9 = cb.equal(c.get("branchCode"),"99999");

			Predicate n10 = cb.or(n8,n9);

			query.where(n12, n2, n3, n4, n5,n10).orderBy(orderList);

			// Get Result
			TypedQuery<UWQuestionsMaster> result = em.createQuery(query);
			list = result.getResultList();
			for (UWQuestionsMaster data : list) {
				// Response
				DropDownRes res = new DropDownRes();
				res.setCode(data.getUwQuestionId().toString());
				res.setCodeDesc(data.getUwQuestionDesc());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return resList;
	}
	
	  public static boolean isNumeric(String input) {
	        String numericPattern = "^[0-9]+$";
	        Pattern pattern = Pattern.compile(numericPattern);
	        Matcher matcher = pattern.matcher(input);
	        
	        return matcher.matches();
	    }
}
