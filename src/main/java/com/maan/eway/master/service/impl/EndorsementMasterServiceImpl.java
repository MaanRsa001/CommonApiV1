package com.maan.eway.master.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.maan.eway.bean.EndtDependantFieldMaster;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.master.req.EndorsementChangeStatusReq;
import com.maan.eway.master.req.EndorsementMasterDropdownReq;
import com.maan.eway.master.req.EndorsementMasterGetReq;
import com.maan.eway.master.req.EndorsementMasterGetallReq;
import com.maan.eway.master.req.EndorsementMasterSaveReq;
import com.maan.eway.master.res.EndorsementMasterGetallRes;
import com.maan.eway.master.res.EndorsementMasterListRes;
import com.maan.eway.master.res.EndorsementMasterRes;
import com.maan.eway.master.res.GetallEndorsementRes;
import com.maan.eway.master.service.EndorsementMasterService;
import com.maan.eway.repository.EndtDependantFieldsMasterRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.LoginProductMasterRepository;
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
public class EndorsementMasterServiceImpl implements EndorsementMasterService {

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private EndtTypeMasterRepository repo;

	@Autowired
	private EndtDependantFieldsMasterRepository dependantrepo;

	
	@Autowired
	private ListItemValueRepository listrepo;

	@Autowired
	private LoginProductMasterRepository loginRepo;
	
	@Autowired
	private LoginMasterRepository loginmasterrepo;
	
	Gson json = new Gson();
	
	private Logger log = LogManager.getLogger(EndorsementMasterServiceImpl.class);

	@Override
	public List<String> validateEndorsement(EndorsementMasterSaveReq req) {
		// TODO Auto-generated method stub
		List<String> errorList = new ArrayList<String>();

		try {
			if (StringUtils.isBlank(req.getProductId())) {
				
//				errorList.add(new Error("01", "ProductId", "Please Enter ProductId"));
				errorList.add("1313");
				
			}
			
			if (StringUtils.isBlank(req.getCompanyId())) {
//				errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
				errorList.add("1255");
			}
						
		/*	if (StringUtils.isBlank(req.getEndtTypeId())) {
				errorList.add(new Error("03", "EndtTypeId", "Please Enter EndtTypeId"));
			}
			*/
			if (StringUtils.isBlank(req.getRemarks())) {
//				errorList.add(new Error("04", "Remarks", "Please Enter Remarks "));
				errorList.add("1259");
			}else if (req.getRemarks().length() > 100){
//				errorList.add(new Error("04","Remarks", "Please Enter Remarks within 100 Characters")); 
				errorList.add("1260");
			}
			
			if (StringUtils.isNotBlank(req.getSectionModificationYn())) {
				if (req.getSectionModificationYn().equalsIgnoreCase("Y") && StringUtils.isBlank(req.getSectionModificationType()) )
//				errorList.add(new Error("04", "SectionModificationType", "Please Select Section Modition Type "));
				errorList.add("1953");
			}
			
			if (StringUtils.isNotBlank(req.getSectionModificationYn())) {
				if (req.getSectionModificationYn().equalsIgnoreCase("Y") && StringUtils.isBlank(req.getSectionModificationType()) )
//				errorList.add(new Error("04", "SectionModificationType", "Please Select Section Modition Type "));
				errorList.add("1953");
			}
			// Date Validation 
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);;
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null || StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
//				errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start"));
				errorList.add("1261");

			} else if (req.getEffectiveDateStart().before(today)) {
//				errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
				errorList.add("1262");
			}
			if (StringUtils.isBlank(req.getEndtShortCode())) {
//				errorList.add(new Error("05", "Endorsement Short Code", "Please Select Endorsement Short Code"));
				errorList.add("2148");
			} else if(StringUtils.isNotBlank(req.getEndtTypeCategoryId()) && "1".equalsIgnoreCase(req.getEndtTypeCategoryId()) && !"99999".equalsIgnoreCase(req.getEndtShortCode()) ) {
				//errorList.add(new Error("05", "ShortCode Description", "ShortCode Description - Others Only Allowed in Non Financial"));
				errorList.add("2150");
			} else if(  StringUtils.isNotBlank(req.getCompanyId()) &&  StringUtils.isNotBlank(req.getProductId()) 
					&& StringUtils.isNotBlank(req.getEndtTypeCategoryId()) && "842".equalsIgnoreCase(req.getEndtShortCode()) ) {
				
				 List<EndtTypeMaster> cancelTypes =  checkDuplicateCount(req.getCompanyId() ,req.getProductId() ,req.getEndtTypeCategoryId() );
				 if(StringUtils.isBlank(req.getEndtType() )  && cancelTypes.size() > 0   ) {
					//errorList.add(new Error("05", "ShortCode Description", "ShortCode Description - Cancellation Already Exist"));
					errorList.add("2151");
				 } else if (StringUtils.isNotBlank(req.getEndtType() )  && !"842".equalsIgnoreCase(req.getEndtTypeId()) && cancelTypes.size() > 0 ) {
					//errorList.add(new Error("05", "ShortCode Description", "ShortCode Description - Cancellation Already Exist"));
					errorList.add("2151"); 
				 }
				 
				
			}
				
			//Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
//				errorList.add(new Error("05", "Status", "Please Select Status"));
				errorList.add("1263");
			} else if (req.getStatus().length() > 1) {
//				errorList.add(new Error("05", "Status", "Please Select Valid Status - One Character Only Allwed"));
				errorList.add("1264");
			}else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
//				errorList.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
				errorList.add("1265");
			}
			
//			if (StringUtils.isBlank(req.getCoreAppCode())) {
//				errorList.add(new Error("07", "CoreAppCode", "Please Enter CoreAppCode"));
//			}else if (req.getCoreAppCode().length() > 20){
//				errorList.add(new Error("07","CoreAppCode", "Please Enter CoreAppCode within 20 Characters")); 
//			}
			
			if (StringUtils.isBlank(req.getCreatedBy())) {
//				errorList.add(new Error("09", "CreatedBy", "Please Enter CreatedBy"));
				errorList.add("1270");
			}else if (req.getCreatedBy().length() > 100){
//				errorList.add(new Error("09","CreatedBy", "Please Enter CreatedBy within 100 Characters")); 
				errorList.add("1271");
			}

			if (StringUtils.isBlank(req.getEndtType())) {
//				errorList.add(new Error("10", "EndtType", "Please Enter EndtType"));
				errorList.add("1954");
			}else if (req.getEndtType().length() > 300){
//				errorList.add(new Error("10","EndtType", "Please Enter EndtType within 300 Characters")); 
				errorList.add("1955");
			}

			if (StringUtils.isBlank(req.getEndtTypeDesc())) {
//				errorList.add(new Error("11", "EndtTypeDesc", "Please Enter EndtTypeDesc"));
				errorList.add("1956");
			}else if (req.getEndtTypeDesc().length() > 300){
//				errorList.add(new Error("11","EndtTypeDesc", "Please Enter EndtTypeDesc within 300 Characters")); 
				errorList.add("1957");
			}

			if (StringUtils.isBlank(req.getEndtTypeCategoryId())) {
//				errorList.add(new Error("12", "EndtTypeCategoryId", "Please Enter EndtTypeCategoryId"));
				errorList.add("1958");
			}
			
			if (StringUtils.isBlank(req.getPriority())) {
//				errorList.add(new Error("13", "Priority", "Please Enter Priority"));
				errorList.add("1959");
			}
			 else if (StringUtils.isNotBlank(req.getPriority())&& !req.getPriority().matches("[0-9]+")){
//					errorList.add(new Error("13", "Priority", "Please Enter Priority only in numbers"));
					errorList.add("1960");
				}
				
			for(String dependantid : req.getEndtDependantIds()) {
			if (StringUtils.isBlank(dependantid)) {
//				errorList.add(new Error("14", "EndtDependantId", "Please Enter EndtDependantId"));
				errorList.add("1961");
			}
			}
			
			if (req.getEndtDependantIds().isEmpty()){
//				errorList.add(new Error("14", "EndtDependantId", "Please Select EndtDependantId"));
				errorList.add("1962");
				}
			if (StringUtils.isNotBlank(req.getEndtFeeYn()) && req.getEndtFeeYn().equalsIgnoreCase("Y")){		
			if (StringUtils.isBlank(req.getCalcTypeId())) {
//				errorList.add(new Error("15", "CalcTypeId", "Please Enter CalcTypeId"));
				errorList.add("1963");
			}
			}
			if((StringUtils.isNotBlank(req.getCalcTypeId())) && req.getCalcTypeId().equalsIgnoreCase("A")) {
				if (StringUtils.isBlank(req.getEndtFeePercent())) {				
//					errorList.add(new Error("16", "EndtFeePercent", "Please Enter EndtFeePercent"));
					errorList.add("1964");
					}
				if ((StringUtils.isNotBlank(req.getEndtFeePercent()))
						&& !req.getEndtFeePercent().matches("[0-9]+")){
//					errorList.add(new Error("19","EndtFeePercent", "Please Enter EndtFeePercent in correct format")); 
					errorList.add("1965");
					
				}
			}
			if((StringUtils.isNotBlank(req.getCalcTypeId())) && req.getCalcTypeId().equalsIgnoreCase("P")) {
				if (StringUtils.isBlank(req.getEndtFeePercent())) {				
//				errorList.add(new Error("17", "EndtFeePercent", "Please Enter EndtFeePercent"));
				errorList.add("1966");
				}
				Double a =Double.valueOf(req.getEndtFeePercent());
				if((StringUtils.isNotBlank(req.getEndtFeePercent()))&& a>100) {
//					errorList.add(new Error("17", "EndtFeePercent", "Please Enter EndtFeePercent below 100"));	
					errorList.add("1967");
				}
				if ((StringUtils.isNotBlank(req.getEndtFeePercent()))
						&& !req.getEndtFeePercent().matches("[0-9]+")){
//					errorList.add(new Error("19","EndtFeePercent", "Please Enter EndtFeePercent in correct format")); 
					errorList.add("1968");
					
				}
			}
			if (StringUtils.isBlank(req.getEndtFeeYn())) {
//				errorList.add(new Error("16", "EndtFeeYn", "Please Enter EndtFeeYn"));
				errorList.add("1969");
			}
			
//			if (StringUtils.isBlank(req.getRemarks())) {
////				errorList.add(new Error("18", "Remarks", "Please Enter Remarks"));
//				errorList.add("1970");
//			}else if (req.getRemarks().length() > 100){
////				errorList.add(new Error("18","Remarks", "Please Enter Remarks within 100 Characters")); 
//				errorList.add("1971");
//			}

			
//			if (StringUtils.isBlank(req.getRegulatoryCode())) {
//				errorList.add(new Error("20", "RegulatoryCode", "Please Enter RegulatoryCode"));
//			}else if (req.getRegulatoryCode().length() > 10){
//				errorList.add(new Error("20","RegulatoryCode", "Please Enter RegulatoryCode within 10 Characters")); 
//			}
			if((StringUtils.isNotBlank(req.getCalcTypeId())) && req.getCalcTypeId().equalsIgnoreCase("M")) {
				if (StringUtils.isBlank(req.getEndtFeePercent())) {				
//					errorList.add(new Error("16", "EndtFeePercent", "Please Enter EndtFeePercent"));
					errorList.add("1972");
					}
				if ((StringUtils.isNotBlank(req.getEndtFeePercent()))
						&& !req.getEndtFeePercent().matches("[0-9]+")){
//					errorList.add(new Error("19","EndtFeePercent", "Please Enter EndtFeePercent in correct format")); 
					errorList.add("1973");
					
				}
			}
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}
			
	@Override
	public SuccessRes saveEndorsement(EndorsementMasterSaveReq req) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		EndtTypeMaster saveData = new EndtTypeMaster();
		List<EndtTypeMaster> list  = new ArrayList<EndtTypeMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Integer amendId = 0;
			Date StartDate = req.getEffectiveDateStart();
			String end = "31/12/2050";
			Date endDate = sdf.parse(end);
			long MILLS_IN_A_DAY = 1000*60*60*24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime()- MILLS_IN_A_DAY);
			Date entryDate = null;
			String createdBy ="";
			Integer endtTypeId = 0;
			
			ListItemValue data = getListItem("ENDORSEMENT_TYPE",req.getEndtTypeCategoryId() ,req.getCompanyId());
		//	ListItemValue calc = listrepo.findByItemTypeAndItemCode("CALCULATION_TYPE",req.getCalcTypeId());
			ListItemValue endtShortDesc = getListItem("ENDT_SHORTCODE",req.getEndtShortCode(),"99999"  );
			
			if(StringUtils.isBlank(req.getEndtTypeId())) {
				
				Integer totalCount = getMasterTableCount(req.getCompanyId(),req.getProductId(),req.getEndtTypeCategoryId());
				if("1".equalsIgnoreCase(req.getEndtTypeCategoryId())){//Non Financial
					endtTypeId = totalCount + 1 ;
				}else if("2".equalsIgnoreCase(req.getEndtTypeCategoryId())){//Finacial
					endtTypeId = req.getEndtShortCode().equalsIgnoreCase("842") ? 842 :   totalCount + 1  ;	
				}
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully");
				res.setSuccessId(endtTypeId.toString());
			}
			else {
				endtTypeId = Integer.valueOf(req.getEndtTypeId());
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<EndtTypeMaster> query = cb.createQuery(EndtTypeMaster.class);
				//Findall
				Root<EndtTypeMaster> b = query.from(EndtTypeMaster.class);
				//select
				query.select(b);
				//Orderby
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("effectiveDateStart")));
				//Where
				Predicate n1 = cb.equal(b.get("endtTypeId"),req.getEndtTypeId());
				Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
				Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
				Predicate n4 = cb.equal(b.get("endtTypeCategoryId"),req.getEndtTypeCategoryId());

				query.where(n1,n2,n3,n4).orderBy(orderList);
				
				// Get Result
				TypedQuery<EndtTypeMaster> result = em.createQuery(query);
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
						EndtTypeMaster lastRecord = list.get(0);
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					}
					else {
						amendId = list.get(0).getAmendId();
						entryDate = list.get(0).getEntryDate();
						createdBy = list.get(0).getCreatedBy();
						saveData = list.get(0);
						if(list.size()>1) {
							EndtTypeMaster lastRecord = list.get(1);	
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}
					}
				}
				res.setResponse("Updated Successfully");
				res.setSuccessId(endtTypeId.toString());
			}
		
			dozerMapper.map(req, saveData);
			saveData.setEndtTypeId(endtTypeId);
			saveData.setEffectiveDateStart(StartDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setEntryDate(new Date());
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setAmendId(amendId);
			saveData.setProductId(req.getProductId()==null?99999: Integer.valueOf(req.getProductId()));
			saveData.setEndtType(req.getEndtType());
			saveData.setEndtTypeDesc(req.getEndtTypeDesc());
			saveData.setEndtTypeCategoryId(Integer.valueOf(req.getEndtTypeCategoryId()));
			saveData.setEndtTypeCategory(data.getItemValue());
			saveData.setPriority(Integer.valueOf(req.getPriority()));
			saveData.setCalcTypeId(req.getCalcTypeId());
		//	saveData.setCalcType(calc.getItemValue());
			saveData.setEndtTypeId(endtTypeId);
		//	saveData.setRegulatoryCode(req.getRegulatoryCode());
			saveData.setSectionModificationYn(StringUtils.isBlank(req.getSectionModificationYn()) ? "N" : req.getSectionModificationYn()   );
			saveData.setSectionModificationType(req.getSectionModificationType());
			saveData.setIsCoverendt(StringUtils.isBlank(req.getIsCoverEndorsementYN()) ?"N" :req.getIsCoverEndorsementYN()  );
			saveData.setEndtShortCode(req.getEndtShortCode());
			saveData.setEndtShortDesc(endtShortDesc.getItemValue());
			/*
			String id = "";
			String desc = "";
			List<String> ids = req.getEndtDependantIds();
			for (int i = 0; i < ids.size(); i++) {
			ListItemValue data1 = listrepo.findByItemTypeAndItemCode("ENDT_DEPENDANT_FIELDS",ids.get(i));				
				id = id + "," + ids.get(i);
				desc=desc+"," +data1.getItemValue();
			}
			*/

			String id = "";
			String desc = "";
			List<String> ids = req.getEndtDependantIds();
			for (int i = 0; i < ids.size(); i++) {
			EndtDependantFieldMaster data1 = getDependantField(req.getCompanyId(),req.getProductId(),Integer.valueOf(ids.get(i)));				
				id = id + "," + ids.get(i);
				desc=desc+"," +data1.getDependantFieldName();
			}

			
			id=id.substring(1);
			desc=desc.substring(1);
			saveData.setEndtDependantIds(id);
			saveData.setEndtDependantFields(desc);
			
			repo.saveAndFlush(saveData);	

			log.info("Saved Details is --> " + json.toJson(saveData));	
			}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
		}
	
	public synchronized ListItemValue getListItem( String itemType,String itemCode , String companyId) {
		ListItemValue data = new ListItemValue();
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
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate b1= cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			effectiveDate.where(a1,a2,b1,b2);
			
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate b3= cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate b4= cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			effectiveDate2.where(a3,a4,b3,b4);
						
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n11 = cb.equal(c.get("status"),"R");
			Predicate n12 = cb.or(n1,n11);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n5 = cb.equal(c.get("companyId"), companyId);
			//Predicate n6 = cb.equal(c.get("branchCode"), "");
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
	//		Predicate n8 = cb.or(n4,n5);
		//	Predicate n9 = cb.or(n6,n7);
			Predicate n10 = cb.equal(c.get("itemType"),itemType); 
			Predicate n15 = cb.equal(c.get("itemCode"),itemCode); 
				
			query.where(n12,n2,n3,n5,n7,n10,n15).orderBy(orderList);
		
			TypedQuery<ListItemValue> result = em.createQuery(query);
			List<ListItemValue> list = result.getResultList();
			data = list.size() > 0 ? list.get(0) : null ;			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return data ;
	}
	
	
	private EndtDependantFieldMaster getDependantField(String companyId, String productId, Integer dependantFieldId) {
		// TODO Auto-generated method stub
		List<EndtDependantFieldMaster> list = new ArrayList<EndtDependantFieldMaster>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtDependantFieldMaster> query = cb.createQuery(EndtDependantFieldMaster.class);

			// Find All
			Root<EndtDependantFieldMaster> b = query.from(EndtDependantFieldMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<EndtDependantFieldMaster> ocpm1 = amendId.from(EndtDependantFieldMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("dependantFieldId"), b.get("dependantFieldId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			
			amendId.where(a1,a2,a3);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"),companyId);
			Predicate n3 = cb.equal(b.get("productId"),productId);
			Predicate n4 = cb.equal(b.get("dependantFieldId"),dependantFieldId);
				
			query.where(n1,n2,n3,n4);
			
			// Get Result
			TypedQuery<EndtDependantFieldMaster> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return list.get(0);
	}


	public Integer getMasterTableCount(String companyId,  String productId, String endtTypeCategoryId)	{

		Integer data =0;
		try {
			List<EndtTypeMaster> list = new ArrayList<EndtTypeMaster>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtTypeMaster> query = cb.createQuery(EndtTypeMaster.class);
			//Find all
			Root<EndtTypeMaster> b = query.from(EndtTypeMaster.class);
			// Select
			query.select(b);
			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<EndtTypeMaster> ocpm1 = effectiveDate.from(EndtTypeMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(ocpm1.get("endtTypeId"),b.get("endtTypeId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"),b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"),b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("endtTypeCategoryId"),b.get("endtTypeCategoryId"));

			effectiveDate.where(a1,a2,a3,a4);
		
			//OrderBy
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("endtTypeId")));
			
			Predicate n1 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"),companyId);
			Predicate n3 = cb.equal(b.get("productId"),productId);
			Predicate n4 = cb.equal(b.get("endtTypeCategoryId"),endtTypeCategoryId);
			query.where(n1,n2,n3,n4).orderBy(orderList);
			
			
			
			// Get Result
			TypedQuery<EndtTypeMaster> result = em.createQuery(query);
			int limit = 0 , offset = 1 ;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			data = list.size() > 0 ? list.get(0).getEndtTypeId() : endtTypeCategoryId.equalsIgnoreCase("1") ? 50 : 850 ;
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return data;
	}
	
	public List<EndtTypeMaster> checkDuplicateCount(String companyId,  String productId, String endtTypeCategoryId)	{

		List<EndtTypeMaster> list = new ArrayList<EndtTypeMaster>();
		try {
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtTypeMaster> query = cb.createQuery(EndtTypeMaster.class);
			//Find all
			Root<EndtTypeMaster> b = query.from(EndtTypeMaster.class);
			// Select
			query.select(b);
			//OrderBy
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("endtTypeId")));
			
			Predicate n1 = cb.equal(b.get("endtShortCode"),"842");
			Predicate n2 = cb.equal(b.get("companyId"),companyId);
			Predicate n3 = cb.equal(b.get("productId"),productId);
			Predicate n4 = cb.equal(b.get("endtTypeCategoryId"),endtTypeCategoryId);
			query.where(n1,n2,n3,n4).orderBy(orderList);
			
			
			
			// Get Result
			TypedQuery<EndtTypeMaster> result = em.createQuery(query);
			int limit = 0 , offset = 1 ;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return list;
	}
	
	@Override
	public List<EndorsementMasterGetallRes> getallEndorsement(EndorsementMasterGetallReq req) {
		// TODO Auto-generated method stub
		List<EndorsementMasterGetallRes> resList = new ArrayList<EndorsementMasterGetallRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			LoginProductMaster loginProduct =   getLoginProductDetails(req.getCompanyId() , req.getProductId() , req.getLoginId() );
			List<String> financeids = new ArrayList<String>();
			List<String> nonfinanceids = new ArrayList<String>();
			if ( loginProduct !=null ) {
				String financeid = loginProduct.getFinancialEndtIds()==null ? "": loginProduct.getFinancialEndtIds() ;
				String nonFinanceid = loginProduct.getNonFinancialEndtIds()==null ? "": loginProduct.getNonFinancialEndtIds();
				financeids = new ArrayList<String>(Arrays.asList(financeid.split(",")));
				nonfinanceids = new ArrayList<String>(Arrays.asList(nonFinanceid.split(",")));
			}
			
			
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<EndtTypeMaster> list = new ArrayList<EndtTypeMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtTypeMaster> query = cb.createQuery(EndtTypeMaster.class);

			// Find All
			Root<EndtTypeMaster> b = query.from(EndtTypeMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<EndtTypeMaster> ocpm1 = amendId.from(EndtTypeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a3 = cb.equal(ocpm1.get("endtTypeCategoryId"), b.get("endtTypeCategoryId"));
			Predicate a4 = cb.equal(ocpm1.get("endtTypeId"), b.get("endtTypeId"));
		//	Predicate a5 = cb.equal(ocpm1.get("sectionId"),b.get("sectionId"));
			amendId.where(a1, a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("endtTypeId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n4 = cb.equal(b.get("endtTypeCategoryId"),req.getEndtTypeCategoryId());
	//		Predicate n5 = cb.equal(b.get("sectionId"),req.getSectionId());
			
			query.where(n1,n2,n3,n4).orderBy(orderList);
			
			// Get Result
			TypedQuery<EndtTypeMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getEndtTypeId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(EndtTypeMaster :: getEndtType ));
			
			EndorsementMasterGetallRes res1 = new EndorsementMasterGetallRes();
			
			List<EndorsementMasterListRes> endtlist = new ArrayList<EndorsementMasterListRes>();
			for(EndtTypeMaster data : list) {
				EndorsementMasterListRes res = new EndorsementMasterListRes(); 	
			String dependentid = data.getEndtDependantIds();
			List<String> dependentids = new ArrayList<String>(Arrays.asList(dependentid.split(",")));
			res.setEndtDependantIds(dependentids);

			String dependentfield = data.getEndtDependantFields();
			List<String> dependentfields = new ArrayList<String>(Arrays.asList(dependentfield.split(",")));
			res.setEndtDependantFields(dependentfields);
			
			res.setAmendId(data.getAmendId().toString());
			res.setEntryDate(data.getEntryDate());
			res.setEffectiveDateStart(data.getEffectiveDateStart());
			res.setEffectiveDateEnd(data.getEffectiveDateEnd());
			res.setCoreAppCode(data.getCoreAppCode());
			res.setEndtTypeId(data.getEndtTypeId().toString());
			res1.setEndtTypeCategoryId(data.getEndtTypeCategoryId().toString());
			res.setPriority(data.getPriority().toString());
			res1.setProductId(data.getProductId().toString());
			res.setEndtFeePercent(data.getEndtFeePercent());
			res.setUpdatedDate(data.getUpdatedDate());
			res.setEndtType(data.getEndtType());
			res.setEndtTypeDesc(data.getEndtTypeDesc());
			res1.setEndtTypeCategory(data.getEndtTypeCategory());
			res.setStatus(data.getStatus());
			res1.setCompanyId(data.getCompanyId());
			res.setCalcTypeId(data.getCalcTypeId());			
		//	res.setCalcType(data.getCalcType());
			res.setEndtFeeYn(data.getEndtFeeYn());
			res.setRemarks(data.getRemarks());
			res.setCreatedBy(data.getCreatedBy());
			res.setUpdatedBy(data.getUpdatedBy());
			res.setRegulatoryCode(data.getRegulatoryCode());
			res.setSelectedYn("N");
			res.setIsCoverEndorsementYN(data.getIsCoverendt());
			res.setEndtShortCode(data.getEndtShortCode());
			res.setEndtShortDesc(data.getEndtShortDesc());
			if(data.getEndtTypeCategoryId().equals(1)) {
				List<String> filterTotalIds = nonfinanceids.stream().filter( o -> o.equalsIgnoreCase(data.getEndtTypeId().toString()) ).collect(Collectors.toList());
				if(filterTotalIds.size() > 0 ) {
					res.setSelectedYn("Y");
				}
			} else {
				List<String> filterTotalIds = financeids.stream().filter( o -> o.equalsIgnoreCase(data.getEndtTypeId().toString()) ).collect(Collectors.toList());
				if(filterTotalIds.size() > 0 ) {
					res.setSelectedYn("Y");
				}
			}
			endtlist.add(res);
			}
			res1.setEndorsementMasterListRes(endtlist);
			resList.add(res1);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	public LoginProductMaster getLoginProductDetails(String companyId  , String productId , String loginId ) {
		LoginProductMaster res = new LoginProductMaster();
		List<LoginProductMaster> list = new ArrayList<LoginProductMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			// Update
			// Get Less than Equal Today Record 
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginProductMaster> query = cb.createQuery(LoginProductMaster.class);

			// Find All
			Root<LoginProductMaster> b = query.from(LoginProductMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<LoginProductMaster> ocpm1 = effectiveDate.from(LoginProductMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			effectiveDate.where(a1,a2,a3);

			// Effective Date Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<LoginProductMaster> ocpm2 = effectiveDate2.from(LoginProductMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a4 = cb.equal(ocpm2.get("productId"), b.get("productId"));
			Predicate a5 = cb.equal(ocpm2.get("companyId"), b.get("companyId"));
			Predicate a6 = cb.equal(ocpm2.get("loginId"), b.get("loginId"));
			effectiveDate2.where(a4,a5,a6);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));
			
			// Where
			Predicate n1 = cb.lessThanOrEqualTo(b.get("effectiveDateEnd"), effectiveDate2);
			Predicate n2 = cb.greaterThanOrEqualTo(b.get("effectiveDateStart"), effectiveDate);
			Predicate n3 =  cb.equal(b.get("productId"), productId );
			Predicate n4 =  cb.equal(b.get("companyId"), companyId );
			Predicate n5 =  cb.equal(b.get("loginId"), loginId );

			query.where(n1, n2, n3,n4,n5);//.orderBy(orderList);

			// Get Result
			TypedQuery<LoginProductMaster> result = em.createQuery(query);
			list = result.getResultList();
			res = list.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}
	
	@Override
	public List<EndorsementMasterGetallRes> getActiveEndorsement(EndorsementMasterGetallReq req) {
		// TODO Auto-generated method stub
		List<EndorsementMasterGetallRes> resList = new ArrayList<EndorsementMasterGetallRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			LoginProductMaster loginProduct =   getLoginProductDetails(req.getCompanyId() , req.getProductId() , req.getLoginId() );
			List<String> financeids = new ArrayList<String>();
			List<String> nonfinanceids = new ArrayList<String>();
			if ( loginProduct !=null ) {
				String financeid = loginProduct.getFinancialEndtIds()==null ? "": loginProduct.getFinancialEndtIds() ;
				String nonFinanceid = loginProduct.getNonFinancialEndtIds()==null ? "": loginProduct.getNonFinancialEndtIds();
				financeids = new ArrayList<String>(Arrays.asList(financeid.split(",")));
				nonfinanceids = new ArrayList<String>(Arrays.asList(nonFinanceid.split(",")));
			}
			
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<EndtTypeMaster> list = new ArrayList<EndtTypeMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtTypeMaster> query = cb.createQuery(EndtTypeMaster.class);

			// Find All
			Root<EndtTypeMaster> b = query.from(EndtTypeMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<EndtTypeMaster> ocpm1 = amendId.from(EndtTypeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a3 = cb.equal(ocpm1.get("endtTypeCategoryId"), b.get("endtTypeCategoryId"));
			Predicate a4 = cb.equal(ocpm1.get("endtTypeId"), b.get("endtTypeId"));
			amendId.where(a1, a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("endtTypeId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n4 = cb.equal(b.get("endtTypeCategoryId"),req.getEndtTypeCategoryId());
			Predicate n5 = cb.equal(b.get("status"),"Y");
			
			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			
			// Get Result
			TypedQuery<EndtTypeMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getEndtTypeId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(EndtTypeMaster :: getEndtType ));
			
			EndorsementMasterGetallRes res1 = new EndorsementMasterGetallRes();
			
			List<EndorsementMasterListRes> endtlist = new ArrayList<EndorsementMasterListRes>();
			for(EndtTypeMaster data : list) {
				EndorsementMasterListRes res = new EndorsementMasterListRes(); 	
			String dependentid = data.getEndtDependantIds();
			List<String> dependentids = new ArrayList<String>(Arrays.asList(dependentid.split(",")));
			res.setEndtDependantIds(dependentids);

			String dependentfield = data.getEndtDependantFields();
			List<String> dependentfields = new ArrayList<String>(Arrays.asList(dependentfield.split(",")));
			res.setEndtDependantFields(dependentfields);
			
			res.setAmendId(data.getAmendId().toString());
			res.setEntryDate(data.getEntryDate());
			res.setEffectiveDateStart(data.getEffectiveDateStart());
			res.setEffectiveDateEnd(data.getEffectiveDateEnd());
			res.setCoreAppCode(data.getCoreAppCode());
			res.setEndtTypeId(data.getEndtTypeId().toString());
			res1.setEndtTypeCategoryId(data.getEndtTypeCategoryId().toString());
			res.setPriority(data.getPriority().toString());
			res1.setProductId(data.getProductId().toString());
			res.setEndtFeePercent(data.getEndtFeePercent());
			res.setUpdatedDate(data.getUpdatedDate());
			res.setEndtType(data.getEndtType());
			res.setEndtTypeDesc(data.getEndtTypeDesc());
			res1.setEndtTypeCategory(data.getEndtTypeCategory());
			res.setStatus(data.getStatus());
			res1.setCompanyId(data.getCompanyId());
			res.setCalcTypeId(data.getCalcTypeId());			
		//	res.setCalcType(data.getCalcType());
			res.setEndtFeeYn(data.getEndtFeeYn());
			res.setRemarks(data.getRemarks());
			res.setCreatedBy(data.getCreatedBy());
			res.setUpdatedBy(data.getUpdatedBy());
			res.setRegulatoryCode(data.getRegulatoryCode());
			res.setSelectedYn("N");
			res.setEndtShortCode(data.getEndtShortCode());
			res.setEndtShortDesc(data.getEndtShortDesc());
			res.setIsCoverEndorsementYN(data.getIsCoverendt());
			if(data.getEndtTypeCategoryId().equals(1) ) {
				List<String> filterTotalIds = nonfinanceids.stream().filter( o -> o.equalsIgnoreCase(data.getEndtTypeId().toString()) ).collect(Collectors.toList());
				if(filterTotalIds.size() > 0 ) {
					res.setSelectedYn("Y");
				}
			} else {
				List<String> filterTotalIds = financeids.stream().filter( o -> o.equalsIgnoreCase(data.getEndtTypeId().toString()) ).collect(Collectors.toList());
				if(filterTotalIds.size() > 0 ) {
					res.setSelectedYn("Y");
				}
			}
			endtlist.add(res);
			}
			res1.setEndorsementMasterListRes(endtlist);
			resList.add(res1);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public EndorsementMasterRes getByEndorsementId(EndorsementMasterGetReq req) {
		EndorsementMasterRes res = new EndorsementMasterRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<EndtTypeMaster> list = new ArrayList<EndtTypeMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtTypeMaster> query = cb.createQuery(EndtTypeMaster.class);

			// Find All
			Root<EndtTypeMaster> b = query.from(EndtTypeMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<EndtTypeMaster> ocpm1 = amendId.from(EndtTypeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("endtTypeId"), b.get("endtTypeId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("endtTypeCategoryId"), b.get("endtTypeCategoryId"));

			amendId.where(a1, a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("endtTypeId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n4 = cb.equal(b.get("endtTypeCategoryId"),req.getEndtTypeCategoryId());
			Predicate n5 = cb.equal(b.get("endtTypeId"),req.getEndtTypeId());
			
			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			
			// Get Result
			TypedQuery<EndtTypeMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getEndtTypeId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(EndtTypeMaster :: getEndtType ));
			
	//		res = mapper.map(list.get(0), EndorsementMasterRes.class);
			if(list!=null && list.size()>0) {
			String dependentid = list.get(0).getEndtDependantIds();
			List<String> dependentids = new ArrayList<String>(Arrays.asList(dependentid.split(",")));
			res.setEndtDependantIds(dependentids);

			String dependentfield = list.get(0).getEndtDependantFields();
			List<String> dependentfields = new ArrayList<String>(Arrays.asList(dependentfield.split(",")));
			res.setEndtDependantFields(dependentfields);
			
			res.setAmendId(list.get(0).getAmendId().toString());
			res.setEntryDate(list.get(0).getEntryDate());
			res.setEffectiveDateStart(list.get(0).getEffectiveDateStart());
			res.setEffectiveDateEnd(list.get(0).getEffectiveDateEnd());
			res.setCoreAppCode(list.get(0).getCoreAppCode());
			res.setEndtTypeId(list.get(0).getEndtTypeId().toString());
			res.setEndtTypeCategoryId(list.get(0).getEndtTypeCategoryId().toString());
			res.setPriority(list.get(0).getPriority().toString());
			res.setProductId(list.get(0).getProductId().toString());
			res.setEndtFeePercent(list.get(0).getEndtFeePercent());
			res.setUpdatedDate(list.get(0).getUpdatedDate());
			res.setEndtType(list.get(0).getEndtType());
			res.setEndtTypeDesc(list.get(0).getEndtTypeDesc());
			res.setEndtTypeCategory(list.get(0).getEndtTypeCategory());
			res.setStatus(list.get(0).getStatus());
			res.setCompanyId(list.get(0).getCompanyId());
			res.setCalcTypeId(list.get(0).getCalcTypeId());			
		//	res.setCalcType(list.get(0).getCalcType());
			res.setEndtFeeYn(list.get(0).getEndtFeeYn());
			res.setRemarks(list.get(0).getRemarks());
			res.setCreatedBy(list.get(0).getCreatedBy());
			res.setUpdatedBy(list.get(0).getUpdatedBy());	
			res.setRegulatoryCode(list.get(0).getRegulatoryCode());
			res.setIsCoverEndorsementYN(list.get(0).getIsCoverendt());
			res.setEndtShortCode(list.get(0).getEndtShortCode());
			res.setEndtShortDesc(list.get(0).getEndtShortDesc());
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	@Override
	public SuccessRes changeStatusOfEndorsement(EndorsementChangeStatusReq req) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		EndtTypeMaster saveData = new EndtTypeMaster();
		List<EndtTypeMaster> list  = new ArrayList<EndtTypeMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Integer amendId = 0;
			Date StartDate = req.getEffectiveDateStart();
			String end = "31/12/2050";
			Date endDate = sdf.parse(end);
			long MILLS_IN_A_DAY = 1000*60*60*24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime()- MILLS_IN_A_DAY);
			Date entryDate = null;
			String createdBy = "";
			Integer endtTypeId = 0;

			endtTypeId = Integer.valueOf(req.getEndtTypeId());
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtTypeMaster> query = cb.createQuery(EndtTypeMaster.class);
			// Findall
			Root<EndtTypeMaster> b = query.from(EndtTypeMaster.class);
			// select
			query.select(b);
			// Orderby
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));
			// Where
			Predicate n1 = cb.equal(b.get("endtTypeId"), req.getEndtTypeId());
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(b.get("endtTypeCategoryId"), req.getEndtTypeCategoryId());
			query.where(n1, n2, n3, n4).orderBy(orderList);

			// Get Result
			TypedQuery<EndtTypeMaster> result = em.createQuery(query);
			int limit = 0, offset = 2;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
				Date beforeOneDay = new Date(new Date().getTime() - MILLS_IN_A_DAY);
				if (list.get(0).getEffectiveDateStart().before(beforeOneDay)) {
					amendId = list.get(0).getAmendId() + 1;
					entryDate = new Date();
					createdBy = req.getCreatedBy();
					EndtTypeMaster lastRecord = list.get(0);
					lastRecord.setEffectiveDateEnd(oldEndDate);
					repo.saveAndFlush(lastRecord);
				} else {
					amendId = list.get(0).getAmendId();
					entryDate = list.get(0).getEntryDate();
					createdBy = list.get(0).getCreatedBy();
					saveData = list.get(0);
			//			EndtTypeMaster lastRecord = list.get(1);
			//			lastRecord.setEffectiveDateEnd(oldEndDate);
			//			repo.saveAndFlush(lastRecord);
					
				}
			
			res.setResponse("Updated Successfully");
			res.setSuccessId(endtTypeId.toString());

			dozerMapper.map(list.get(0), saveData);
			
			saveData.setEndtTypeId(endtTypeId);
			saveData.setEffectiveDateStart(StartDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setEntryDate(entryDate);
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setAmendId(amendId);
			saveData.setStatus(req.getStatus());
			saveData.setCompanyId(list.get(0).getCompanyId());
			saveData.setProductId(req.getProductId()==null?99999:Integer.valueOf(req.getProductId()));
			repo.saveAndFlush(saveData);	
			log.info("Saved Details is --> " + json.toJson(saveData));	
			res.setResponse("Status Changed");
			res.setSuccessId(req.getEndtTypeId());
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
			}
		return res;
	}

	@Override
	public List<DropDownRes> getEndorsementMasterDropdown(EndorsementMasterDropdownReq req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EndorsementMasterGetallRes> getallBrokerEndorsement(EndorsementMasterGetallReq req) {
		// TODO Auto-generated method stub
		List<EndorsementMasterGetallRes> resList = new ArrayList<EndorsementMasterGetallRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			LoginMaster login = loginmasterrepo.findByLoginId(req.getLoginId());
		//	List<LoginProductMaster> product = loginRepo.findByOaCodeAndAgencyCodeAndProductIdAndCompanyIdOrderByAmendIdDesc(login.getOaCode(),login.getAgencyCode(),Integer.valueOf(req.getProductId()),req.getCompanyId());	
			List<LoginProductMaster> product = loginRepo.findByOaCodeAndAgencyCodeAndProductIdAndCompanyIdOrderByAmendIdDesc(login.getOaCode(),Integer.valueOf(login.getAgencyCode()),Integer.valueOf(req.getProductId()),req.getCompanyId());
			String endtid = "";
			if(req.getEndtTypeCategoryId().equalsIgnoreCase("2")) {
			endtid = product.get(0).getFinancialEndtIds();
			}
			if(req.getEndtTypeCategoryId().equalsIgnoreCase("1")) {
			endtid = product.get(0).getNonFinancialEndtIds();
			}
			endtid = endtid.substring(0);
			List<String> endtids = new ArrayList<String>(Arrays.asList(endtid.split(",")));
			for(String id : endtids) {
				if(StringUtils.isNotBlank(id) ) {
					List<EndtTypeMaster> data1 = new ArrayList<EndtTypeMaster>();	
					data1 = repo.findByEndtTypeCategoryIdAndEndtTypeIdAndCompanyIdAndProductIdOrderByAmendIdDesc((Integer.valueOf(req.getEndtTypeCategoryId())), Integer.valueOf(id), req.getCompanyId(),Integer.valueOf(req.getProductId()));	
					if(data1.size()>0 && data1!=null) {
					EndorsementMasterGetallRes res1 = new EndorsementMasterGetallRes();
					
					List<EndorsementMasterListRes> endtlist = new ArrayList<EndorsementMasterListRes>();
					for(EndtTypeMaster data : data1) {
						EndorsementMasterListRes res = new EndorsementMasterListRes(); 	
					String dependentid = data.getEndtDependantIds();
					List<String> dependentids = new ArrayList<String>(Arrays.asList(dependentid.split(",")));
					res.setEndtDependantIds(dependentids);

					String dependentfield = data.getEndtDependantFields();
					List<String> dependentfields = new ArrayList<String>(Arrays.asList(dependentfield.split(",")));
					res.setEndtDependantFields(dependentfields);
					
					res.setAmendId(data.getAmendId().toString());
					res.setEntryDate(data.getEntryDate());
					res.setEffectiveDateStart(data.getEffectiveDateStart());
					res.setEffectiveDateEnd(data.getEffectiveDateEnd());
					res.setCoreAppCode(data.getCoreAppCode());
					res.setEndtTypeId(data.getEndtTypeId().toString());
					res1.setEndtTypeCategoryId(data.getEndtTypeCategoryId().toString());
					res.setPriority(data.getPriority().toString());
					res1.setProductId(data.getProductId().toString());
					res.setEndtFeePercent(data.getEndtFeePercent());
					res.setUpdatedDate(data.getUpdatedDate());
					res.setEndtType(data.getEndtType());
					res.setEndtTypeDesc(data.getEndtTypeDesc());
					res1.setEndtTypeCategory(data.getEndtTypeCategory());
					res.setStatus(data.getStatus());
					res1.setCompanyId(data.getCompanyId());
					res.setCalcTypeId(data.getCalcTypeId());			
				//	res.setCalcType(data.getCalcType());
					res.setEndtFeeYn(data.getEndtFeeYn());
					res.setRemarks(data.getRemarks());
					res.setCreatedBy(data.getCreatedBy());
					res.setUpdatedBy(data.getUpdatedBy());
					res.setRegulatoryCode(data.getRegulatoryCode());
					res.setEndtShortCode(data.getEndtShortCode());
					res.setEndtShortDesc(data.getEndtShortDesc());
					endtlist.add(res);
					}
					res1.setEndorsementMasterListRes(endtlist);
					resList.add(res1);
					}
				}
			}
				
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<GetallEndorsementRes> getallEndorsementGrid(EndorsementMasterGetallReq req) {
		List<GetallEndorsementRes> resList = new ArrayList<GetallEndorsementRes>();
		try {
			
			List<EndtTypeMaster> list = new ArrayList<EndtTypeMaster>();
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtTypeMaster> query = cb.createQuery(EndtTypeMaster.class);

			Root<EndtTypeMaster> b = query.from(EndtTypeMaster.class);

			query.select(b);

			Subquery<Long> amendId = query.subquery(Long.class);
			Root<EndtTypeMaster> ocpm1 = amendId.from(EndtTypeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a3 = cb.equal(ocpm1.get("endtTypeCategoryId"), b.get("endtTypeCategoryId"));
			Predicate a4 = cb.equal(ocpm1.get("endtTypeId"), b.get("endtTypeId"));
			

			amendId.where(a1, a2,a3,a4);

			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("endtType")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n4 = cb.equal(b.get("endtTypeCategoryId"),req.getEndtTypeCategoryId());
			Predicate n5 = cb.equal(b.get("status"), "Y");
			Predicate n6 = cb.lessThanOrEqualTo(b.get("effectiveDateStart"), new Date());
			Predicate n7 = cb.greaterThanOrEqualTo(b.get("effectiveDateEnd"), new Date());
			
			query.where(n1,n2,n3,n4,n5,n6,n7).orderBy(orderList);
			
			// Get Result
			TypedQuery<EndtTypeMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getEndtTypeId()))).collect(Collectors.toList());
			
			LoginMaster login = loginmasterrepo.findByLoginId(req.getLoginId());
			List<LoginProductMaster> product = loginRepo.findByOaCodeAndAgencyCodeAndProductIdAndCompanyIdOrderByAmendIdDesc(login.getOaCode(),Integer.valueOf(login.getAgencyCode()),Integer.valueOf(req.getProductId()),req.getCompanyId());
		
			String finan = product.get(0).getFinancialEndtIds();
			String[] splitf = finan.split(",");
			List<String> finanList = Arrays.asList(splitf);
		//	List<Integer> finanList1 = finanList.stream().map(Integer::parseInt) .collect(Collectors.toList());
			
			
			String nonfinan = product.get(0).getNonFinancialEndtIds();
			String[] splitn = nonfinan.split(",");
			List<String> nonfinanList = Arrays.asList(splitn);
		//	List<Integer> nonfinanList1 = nonfinanList.stream().map(Integer::parseInt) .collect(Collectors.toList());
		                
		               
			
			if(list.size()>0) {
				
				for(EndtTypeMaster  data : list) {
					
					GetallEndorsementRes res = new GetallEndorsementRes();
					if(data.getEndtTypeCategoryId()==1) {       // non financial
						
						if(nonfinanList.contains(data.getEndtTypeId().toString()))
							res.setStatus("Y");            			 //tick Yes	
						else
							res.setStatus("");
						
					}else if(data.getEndtTypeCategoryId()==2) { //financial
						
						if(finanList.contains(data.getEndtTypeId().toString()))
							res.setStatus("Y");    
						else
							res.setStatus("");
						
					}
					res.setEndtTypeId(data.getEndtTypeId()==null?"":data.getEndtTypeId().toString());
					
					resList.add(res);
				}
				
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	
}
