package com.maan.eway.master.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.WarrantyMaster;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.NonSelectedClausesGetAllReq;
import com.maan.eway.master.req.WarrantyChangeStatusReq;
import com.maan.eway.master.req.WarrantyMasterDropdownReq;
import com.maan.eway.master.req.WarrantyMasterGetReq;
import com.maan.eway.master.req.WarrantyMasterGetallReq;
import com.maan.eway.master.req.WarrantyMasterReq;
import com.maan.eway.master.req.WarrantyMasterSaveReq;
import com.maan.eway.master.res.WarrantyMasterRes;
import com.maan.eway.master.service.WarrantyMasterService;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.WarrantyMasterRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.thread.GetFileFromPath;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
@Service
public class WarrantyMasterServiceImpl implements WarrantyMasterService {

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private WarrantyMasterRepository repo;

	@Autowired
	private ListItemValueRepository listrepo;
	
	@Value("${file.directoryPath}")
	private String directoryPath;

	Gson json = new Gson();
	
	private Logger log = LogManager.getLogger(WarrantyMasterServiceImpl.class);
	
	@Override
	public List<String> validateWarranty(WarrantyMasterSaveReq req) {
		List<String> errorList = new ArrayList<String>();

		try {
			if (StringUtils.isBlank(req.getWarrantyDescription())) {
			//	errorList.add(new Error("02", "WarrantyDescription", "Please Select WarrantyDescription"));
				errorList.add("1331");
				
			}else if (req.getWarrantyDescription().length() > 5000){
			//	errorList.add(new Error("02","WarrantyDescription", "Please Enter WarrantyDescription 5000 Characters")); 
				errorList.add("1332");
			}else if (StringUtils.isBlank(req.getWarrantyId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getBranchCode())&& StringUtils.isNotBlank(req.getProductId())&& StringUtils.isNotBlank(req.getSectionId())) {
				List<WarrantyMaster> WarrantyList = getWarrantyDescriptionExistDetails(req.getWarrantyDescription() , req.getCompanyId() , req.getBranchCode(),req.getProductId(),req.getSectionId());
				if (WarrantyList.size()>0 ) {
				//	errorList.add(new Error("01", "WarrantyDescription", "This WarrantyDescription Already Exist "));
					errorList.add("1333");
				}
			}else if (StringUtils.isNotBlank(req.getWarrantyId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getBranchCode())&& StringUtils.isNotBlank(req.getProductId())&& StringUtils.isNotBlank(req.getSectionId())) {
				List<WarrantyMaster> WarrantyList = getWarrantyDescriptionExistDetails(req.getWarrantyDescription() , req.getCompanyId() , req.getBranchCode(),req.getProductId(),req.getSectionId());
				
				if (WarrantyList.size()>0 &&  (! req.getWarrantyId().equalsIgnoreCase(WarrantyList.get(0).getWarrantyId().toString())) ) {
				//	errorList.add(new Error("01", "WarrantyDescription", "This WarrantyDescription Already Exist "));
					errorList.add("1333");
				}
				
			}
			
			
			if (StringUtils.isBlank(req.getCompanyId())) {
				//errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
				errorList.add("1255");
			}
			
			if (StringUtils.isBlank(req.getBranchCode())) {
				//errorList.add(new Error("02", "BranchCode", "Please Select BranchCode"));
				errorList.add("1256");
			}
	/*		if (StringUtils.isBlank(req.getOccupationNameAr())) {
				errorList.add(new Error("03", "OccupationNameAr", "Please Select OccupationNameAr"));
			}else if (req.getOccupationNameAr().length() > 100){
				errorList.add(new Error("03","OccupationNameAr", "Please Enter OccupationNameAr 100 Characters")); 
			} */
			
/*			if (StringUtils.isBlank(req.getRemarks())) {
				//errorList.add(new Error("04", "Remarks", "Please Select Remarks "));
				errorList.add("1259");
			}else if (req.getRemarks().length() > 200){
			//	errorList.add(new Error("04","Remarks", "Please Enter Remarks within 200 Characters")); 
				errorList.add("1260");
			}*/
			
			// Date Validation 
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);;
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null || StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
			//	errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start"));
				errorList.add("1261");

			} else if (req.getEffectiveDateStart().before(today)) {
			//	errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
				errorList.add("1262");
			}
			//Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
				//errorList.add(new Error("05", "Status", "Please Select Status  "));
				errorList.add("1263");
			} else if (req.getStatus().length() > 1) {
				//errorList.add(new Error("05", "Status", "Please Select Valid Status - One Character Only Allwed"));
				errorList.add("1264");
			}else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
				//errorList.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
				errorList.add("1265");
			}

			if (StringUtils.isBlank(req.getCoreAppCode())) {
			//	errorList.add(new Error("07", "CoreAppCode", "Please Select CoreAppCode"));
				errorList.add("1266");
			}else if (req.getCoreAppCode().length() > 20){
			//	errorList.add(new Error("07","CoreAppCode", "Please Enter CoreAppCode within 20 Characters")); 
				errorList.add("1267");
			}
			if (StringUtils.isBlank(req.getRegulatoryCode())) {
				//errorList.add(new Error("08", "RegulatoryCode", "Please Select RegulatoryCode"));
				errorList.add("1268");
			}else if (req.getRegulatoryCode().length() > 20){
				//errorList.add(new Error("08","RegulatoryCode", "Please Enter RegulatoryCode within 20 Characters")); 
				errorList.add("1269");
			}
			if (StringUtils.isBlank(req.getCreatedBy())) {
			//	errorList.add(new Error("09", "CreatedBy", "Please Select CreatedBy"));
				errorList.add("1270");
			}else if (req.getCreatedBy().length() > 100){
			//	errorList.add(new Error("09","CreatedBy", "Please Enter CreatedBy within 100 Characters")); 
				errorList.add("1271");
			}		
			if (StringUtils.isBlank(req.getProductId())) {
				//errorList.add(new Error("10", "ProductId", "Please Enter ProductId"));
				errorList.add("1313");
			}
			if (StringUtils.isBlank(req.getSectionId())) {
				//errorList.add(new Error("11", "SectionId", "Please Enter SectionId"));
				errorList.add("1302");
			}
			if (StringUtils.isBlank(req.getTypeId())) {
			//	errorList.add(new Error("12", "TypeId", "Please Enter TypeId"));
				errorList.add("1314");
			}
//			if (StringUtils.isBlank(req.getPolicyType())) {
//				errorList.add(new Error("12", "PolicyType", "Please Enter PolicyType"));
//			}
				
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}
	public List<WarrantyMaster> getWarrantyDescriptionExistDetails(String WarrantyDescription , String InsuranceId , String branchCode, String productId, String sectionId) {
		List<WarrantyMaster> list = new ArrayList<WarrantyMaster>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<WarrantyMaster> query = cb.createQuery(WarrantyMaster.class);

			// Find All
			Root<WarrantyMaster> b = query.from(WarrantyMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<WarrantyMaster> ocpm1 = amendId.from(WarrantyMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("warrantyId"), b.get("warrantyId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a6 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a7 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
//			Predicate a8 = cb.equal(ocpm1.get("policyType"), b.get("policyType"));
			
			amendId.where(a1,a2,a3,a6,a7);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(cb.lower( b.get("warrantyDescription")), WarrantyDescription.toLowerCase());
			Predicate n3 = cb.equal(b.get("companyId"),InsuranceId);
			Predicate n4 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(b.get("productId"), productId);
			Predicate n10 = cb.equal(b.get("sectionId"),sectionId);
			
			query.where(n1,n2,n3,n4,n7,n10);
			
			// Get Result
			TypedQuery<WarrantyMaster> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return list;
	}
	@Override
	public SuccessRes saveWarranty(WarrantyMasterSaveReq req, MultipartFile documentFile) {
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	SuccessRes res = new SuccessRes();
	WarrantyMaster saveData = new WarrantyMaster();
	List<WarrantyMaster> list  = new ArrayList<WarrantyMaster>();
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	String newfilename = "";
	try {
		if(documentFile != null) {
			// Copy File
			Random random = new Random();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			// OrginalFile
			Path destination = Paths.get(directoryPath);
			newfilename = random.nextInt(100)
					+ timestamp.toString().replace(":", "T").replace(" ", "S").replace("-", "H").replace(".", "D") + "."
					+ FilenameUtils.getExtension(documentFile.getOriginalFilename());
			Files.copy(documentFile.getInputStream(), destination.resolve(newfilename));
		}
		Integer amendId = 0;
		Date StartDate = req.getEffectiveDateStart();
		String end = "31/12/2050";
		Date endDate = sdf.parse(end);
		long MILLS_IN_A_DAY = 1000*60*60*24;
		Date oldEndDate = new Date(req.getEffectiveDateStart().getTime()- MILLS_IN_A_DAY);
		Date entryDate = null;
		String createdBy ="";
		Integer warrantyId = 0;
		
		ListItemValue data = listrepo.findByItemTypeAndItemCode("TERMS_TYPE",req.getTypeId());
		if(StringUtils.isBlank(req.getWarrantyId())) {
			Integer totalCount = getMasterTableCount(req.getCompanyId(),req.getProductId(),req.getSectionId());
			warrantyId = totalCount+1;
			entryDate = new Date();
			createdBy = req.getCreatedBy();
			res.setResponse("Saved Successfully");
			res.setSuccessId(warrantyId.toString());
		}
		else {
			warrantyId = Integer.valueOf(req.getWarrantyId());
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<WarrantyMaster> query = cb.createQuery(WarrantyMaster.class);
			//Findall
			Root<WarrantyMaster> b = query.from(WarrantyMaster.class);
			//select
			query.select(b);
			//Orderby
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));
			//Where
			Predicate n1 = cb.equal(b.get("warrantyId"),req.getWarrantyId());
			Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"),req.getBranchCode());
			Predicate n4 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n5 = cb.equal(b.get("sectionId"),req.getSectionId());
//			Predicate n6 = cb.equal(b.get("policyType"),req.getPolicyType());
			
			if(StringUtils.isNotBlank(req.getCoverId()))
			{
				Predicate n6 = cb.equal(b.get("coverId"),Integer.valueOf(req.getCoverId()) );	
				query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
			}
			else
			{
				query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			}
//				
			
			
			// Get Result
			TypedQuery<WarrantyMaster> result = em.createQuery(query);
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
					WarrantyMaster lastRecord = list.get(0);
					lastRecord.setEffectiveDateEnd(oldEndDate);
					repo.saveAndFlush(lastRecord);
				}
				else {
					amendId = list.get(0).getAmendId();
					entryDate = list.get(0).getEntryDate();
					createdBy = list.get(0).getCreatedBy();
					saveData = list.get(0);
					if(list.size()>1) {
						WarrantyMaster lastRecord = list.get(1);	
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					}
				}
			}
			res.setResponse("Updated Successfully");
			res.setSuccessId(warrantyId.toString());
		}
		dozerMapper.map(req, saveData);
		saveData.setWarrantyId(warrantyId);
		saveData.setEffectiveDateStart(StartDate);
		saveData.setEffectiveDateEnd(endDate);
		saveData.setCreatedBy(createdBy);
		saveData.setEntryDate(entryDate);
		saveData.setUpdatedBy(req.getCreatedBy());
		saveData.setUpdatedDate(new Date());
		saveData.setAmendId(amendId);
		saveData.setProductId(req.getProductId()==null? "99999":req.getProductId());
		saveData.setSectionId(req.getSectionId()==null? "99999" : req.getSectionId());
		saveData.setCoverId(req.getCoverId()==null? 99999 : Integer.valueOf(req.getCoverId()));
//		saveData.setPolicyType(req.getPolicyType()==null?"" : "99999");
		saveData.setTypeDesc(data.getItemValue());
		saveData.setTypeId(req.getTypeId());
		saveData.setWarrantyDescriptionLocal(req.getCodeDescLocal());
		saveData.setCategoryDescription(req.getCategoryDescription()==null?"":req.getCategoryDescription());
		saveData.setPdfLocation(StringUtils.isNotBlank(newfilename)?directoryPath + newfilename:null);
		saveData.setPdfName(newfilename);
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
	
	
public Integer getMasterTableCount(String companyId, String productId, String sectionId)	{

	Integer data =0;
	try {
		List<WarrantyMaster> list = new ArrayList<WarrantyMaster>();
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WarrantyMaster> query = cb.createQuery(WarrantyMaster.class);
		//Find all
		Root<WarrantyMaster> b = query.from(WarrantyMaster.class);
		// Select
		query.select(b);
		// Effective Date Max Filter
		Subquery<Date> effectiveDate = query.subquery(Date.class);
		Root<WarrantyMaster> ocpm1 = effectiveDate.from(WarrantyMaster.class);
		effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
		Predicate a1 = cb.equal(ocpm1.get("warrantyId"),b.get("warrantyId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"),b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
		Predicate a5 = cb.equal(ocpm1.get("sectionId"),b.get("sectionId"));
//		Predicate a6 = cb.equal(ocpm1.get("policyType"),b.get("policyType"));
//		
		
		effectiveDate.where(a1,a2,a3,a4,a5);
	
		//OrderBy
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.desc(b.get("warrantyId")));
		
		Predicate n1 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
		Predicate n2 = cb.equal(b.get("companyId"),companyId);
		Predicate n6 = cb.equal(b.get("productId"),productId);
		Predicate n9 = cb.equal(b.get("sectionId"),sectionId);
		
		
		
		query.where(n1,n2,n6,n9).orderBy(orderList);
				
		
		// Get Result
		TypedQuery<WarrantyMaster> result = em.createQuery(query);
		int limit = 0 , offset = 1 ;
		result.setFirstResult(limit * offset);
		result.setMaxResults(offset);
		list = result.getResultList();
		data = list.size() > 0 ? list.get(0).getWarrantyId() : 0 ;
	}
	catch(Exception e) {
		e.printStackTrace();
		log.info(e.getMessage());
	}
	return data;
}

@Override
public List<WarrantyMasterRes> getallWarranty(WarrantyMasterGetallReq req) {
	List<WarrantyMasterRes> resList = new ArrayList<WarrantyMasterRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		List<WarrantyMaster> list = new ArrayList<WarrantyMaster>();
	
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WarrantyMaster> query = cb.createQuery(WarrantyMaster.class);

		// Find All
		Root<WarrantyMaster> b = query.from(WarrantyMaster.class);

		// Select
		query.select(b);

		// Amend ID Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<WarrantyMaster> ocpm1 = amendId.from(WarrantyMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("warrantyId"), b.get("warrantyId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
		Predicate a5 = cb.equal(ocpm1.get("sectionId"),b.get("sectionId"));
//		Predicate a6 = cb.equal(ocpm1.get("policyType"),b.get("policyType"));

		amendId.where(a1, a2,a3,a4,a5);

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(b.get("sectionId")));

		// Where
		Predicate n1 = cb.equal(b.get("amendId"), amendId);
		Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
		Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
	//	Predicate n4 = cb.equal(b.get("branchCode"),"99999");
	//	Predicate n5 = cb.or(n3,n4);
		Predicate n6 = cb.equal(b.get("productId"), req.getProductId());
		Predicate n9 = cb.equal(b.get("sectionId"), req.getSectionId());
	//	Predicate n10 = cb.equal(b.get("sectionId"),"99999");
	//	Predicate n11 = cb.or(n9,n10);
	//	
		query.where(n1,n2,n3,n6,n9).orderBy(orderList);
		
		// Get Result
		TypedQuery<WarrantyMaster> result = em.createQuery(query);
		list = result.getResultList();
		list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getWarrantyId()))).collect(Collectors.toList());
		list.sort(Comparator.comparing(WarrantyMaster :: getWarrantyDescription ));
		
		// Map
		for (WarrantyMaster data : list) {
			WarrantyMasterRes res = new WarrantyMasterRes();

			res = mapper.map(data, WarrantyMasterRes.class);
			res.setCoreAppCode(data.getCoreAppCode());
			res.setCodeDescLocal(data.getWarrantyDescriptionLocal());
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
public List<WarrantyMasterRes> getActiveWarranty(WarrantyMasterGetallReq req) {
	List<WarrantyMasterRes> resList = new ArrayList<WarrantyMasterRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		List<WarrantyMaster> list = new ArrayList<WarrantyMaster>();
	
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WarrantyMaster> query = cb.createQuery(WarrantyMaster.class);

		// Find All
		Root<WarrantyMaster> b = query.from(WarrantyMaster.class);

		// Select
		query.select(b);

		// Amend ID Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<WarrantyMaster> ocpm1 = amendId.from(WarrantyMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("warrantyId"), b.get("warrantyId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
		Predicate a5 = cb.equal(ocpm1.get("sectionId"),b.get("sectionId"));
		
		
		amendId.where(a1, a2,a3,a4,a5);

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(b.get("branchCode")));

		// Where
		Predicate n1 = cb.equal(b.get("amendId"), amendId);
		Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
		Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
		Predicate n4 = cb.equal(b.get("status"), "Y");
		Predicate n7 = cb.equal(b.get("productId"), req.getProductId());
		Predicate n10 = cb.equal(b.get("sectionId"), req.getSectionId());
		Predicate n11 = cb.equal(b.get("branchCode"),"99999");
		Predicate n12 = cb.or(n3,n11);
		Predicate n13 = cb.equal(b.get("sectionId"),"99999");
		Predicate n14 = cb.or(n10,n13);
		
		query.where(n1,n2,n4,n12,n7,n14).orderBy(orderList);
		
		// Get Result
		TypedQuery<WarrantyMaster> result = em.createQuery(query);
		list = result.getResultList();
		list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getWarrantyId()))).collect(Collectors.toList());
		list.sort(Comparator.comparing(WarrantyMaster :: getWarrantyDescription ));
		
		// Map
		for (WarrantyMaster data : list) {
			WarrantyMasterRes res = new WarrantyMasterRes();

			res = mapper.map(data, WarrantyMasterRes.class);
			res.setCoreAppCode(data.getCoreAppCode());

			resList.add(res);
		}

	} catch (Exception e) {
		e.printStackTrace();
		log.info(e.getMessage());
		return null;

	}
	return resList;
}




@Override
public SuccessRes changeStatusOfWarranty(WarrantyChangeStatusReq req) {
	SuccessRes res = new SuccessRes();
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	try {
		List<WarrantyMaster> list = new ArrayList<WarrantyMaster>();
		
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WarrantyMaster> query = cb.createQuery(WarrantyMaster.class);
		// Find all
		Root<WarrantyMaster> b = query.from(WarrantyMaster.class);
		//Select
		query.select(b);

		// Amend ID Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<WarrantyMaster> ocpm1 = amendId.from(WarrantyMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("warrantyId"), b.get("warrantyId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
		Predicate a5 = cb.equal(ocpm1.get("sectionId"),b.get("sectionId"));
//		Predicate a6 = cb.equal(ocpm1.get("policyType"),b.get("policyType"));

		amendId.where(a1, a2,a3,a4,a5);

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(b.get("branchCode")));

		// Where
		Predicate n1 = cb.equal(b.get("amendId"), amendId);
		Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
		Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
		Predicate n4 = cb.equal(b.get("warrantyId"), req.getWarrantyId());
		Predicate n5 = cb.equal(b.get("branchCode"), "99999");
		Predicate n6 = cb.or(n3,n5);
		Predicate n7 = cb.equal(b.get("productId"), req.getProductId());
		Predicate n8 = cb.equal(b.get("productId"), "99999");
		Predicate n9 = cb.or(n7,n8);
		Predicate n10 = cb.equal(b.get("sectionId"), req.getSectionId());
		Predicate n11 = cb.equal(b.get("sectionId"), "99999");
		Predicate n12 = cb.or(n10,n11);
//		Predicate n13 = cb.equal(b.get("policyType"), req.getPolicyType());
//		Predicate n14 = cb.equal(b.get("policyType"), "99999");
//		Predicate n15 = cb.or(n13,n14);

		query.where(n1,n2,n4,n6,n9,n12).orderBy(orderList);
		
		// Get Result 
		TypedQuery<WarrantyMaster> result = em.createQuery(query);
		list = result.getResultList();
		WarrantyMaster updateRecord = list.get(0);
		if(  req.getBranchCode().equalsIgnoreCase(updateRecord.getBranchCode())) {
			updateRecord.setStatus(req.getStatus());
			repo.save(updateRecord);
		} else {
			WarrantyMaster saveNew = new WarrantyMaster();
			dozerMapper.map(updateRecord,saveNew);
			saveNew.setBranchCode(req.getBranchCode());
			saveNew.setStatus(req.getStatus());
			repo.save(saveNew);
		}
	
		// Perform Update
		res.setResponse("Status Changed");
		res.setSuccessId(req.getWarrantyId());
	}
	catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
		}
	return res;
}
@Override
public List<DropDownRes> getWarrantyMasterDropdown(WarrantyMasterDropdownReq req) {
	List<DropDownRes> resList = new ArrayList<DropDownRes>();
	try {
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		today = cal.getTime();
		Date todayEnd = cal.getTime();
		
		// Criteria
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WarrantyMaster> query=  cb.createQuery(WarrantyMaster.class);
		List<WarrantyMaster> list = new ArrayList<WarrantyMaster>();
		// Find All
		Root<WarrantyMaster> c = query.from(WarrantyMaster.class);
		//Select
		query.select(c);
		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(c.get("warrantyDescription")));
		
		// Effective Date Start Max Filter
		Subquery<Date> effectiveDate = query.subquery(Date.class);
		Root<WarrantyMaster> ocpm1 = effectiveDate.from(WarrantyMaster.class);
		effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
		Predicate a1 = cb.equal(c.get("warrantyId"),ocpm1.get("warrantyId"));
		Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
		Predicate a5 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
		Predicate a6 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
		Predicate a7 = cb.equal(c.get("productId"),ocpm1.get("productId"));
		Predicate a8 = cb.equal(c.get("sectionId"),ocpm1.get("sectionId"));
//		Predicate a9 = cb.equal(c.get("policyType"),ocpm1.get("policyType"));

		effectiveDate.where(a1,a2,a5,a6,a7,a8);
		// Effective Date End Max Filter
		Subquery<Date> effectiveDate2 = query.subquery(Date.class);
		Root<WarrantyMaster> ocpm2 = effectiveDate2.from(WarrantyMaster.class);
		effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
		Predicate a3 = cb.equal(c.get("warrantyId"),ocpm2.get("warrantyId"));
		Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
		Predicate a10 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
		Predicate a11 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
		Predicate a12 = cb.equal(c.get("productId"),ocpm2.get("productId"));
		Predicate a13 = cb.equal(c.get("sectionId"),ocpm2.get("sectionId"));
//		Predicate a14 = cb.equal(c.get("policyType"),ocpm1.get("policyType"));

		effectiveDate2.where(a3,a4,a10,a11,a12,a13);
		// Where
		Predicate n1 = cb.equal(c.get("status"),"Y");
		Predicate n12 = cb.equal(c.get("status"),"R");
		Predicate n13 = cb.or(n1,n12);
		Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
		Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
		Predicate n4 = cb.equal(c.get("companyId"),req.getCompanyId());
		Predicate n5 = cb.equal(c.get("branchCode"),req.getBranchCode());
		Predicate n6 = cb.equal(c.get("branchCode"),"99999");
		Predicate n7 = cb.or(n5,n6);
		Predicate n8 = cb.equal(c.get("productId"), req.getProductId());
		Predicate n11 = cb.equal(c.get("sectionId"), req.getSectionId());
		
		query.where(n13,n2,n3,n4,n7,n8,n11).orderBy(orderList);
		// Get Result
		TypedQuery<WarrantyMaster> result = em.createQuery(query);
		list = result.getResultList();
		for (WarrantyMaster data : list) {
			// Response 
			DropDownRes res = new DropDownRes();
			res.setCode(data.getWarrantyId().toString());
			res.setCodeDesc(data.getWarrantyDescription());
			res.setCodeDescLocal(data.getWarrantyDescriptionLocal());
			res.setStatus(data.getStatus());
			resList.add(res);
		}
	}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is --->"+e.getMessage());
			return null;
			}
		return resList;
}
//@Override
//public List<Error> validateWarranty(WarrantyMasterListSaveReq req) {
//	List<Error> errorList = new ArrayList<Error>();
//
//	try {
//		
//		if (StringUtils.isBlank(req.getWarrantyDescription())) {
//			errorList.add(new Error("02", "WarrantyDescription", "Please Select WarrantyDescription"));
//		}else if (req.getWarrantyDescription().length() > 100){
//			errorList.add(new Error("02","WarrantyDescription", "Please Enter WarrantyDescription 100 Characters")); 
//		}else if (StringUtils.isBlank(req.getWarrantyId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getBranchCode())&& StringUtils.isNotBlank(req.getProductId())&& StringUtils.isNotBlank(req.getSectionId())) {
//			List<WarrantyMaster> WarrantyList = getWarrantyDescriptionExistDetails(req.getWarrantyDescription() , req.getCompanyId() , req.getBranchCode(),req.getProductId(),req.getSectionId());
//			if (WarrantyList.size()>0 ) {
//				errorList.add(new Error("01", "WarrantyDescription", "This WarrantyDescription Already Exist "));
//			}
//		}else if (StringUtils.isNotBlank(req.getWarrantyId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getBranchCode())&& StringUtils.isNotBlank(req.getProductId())&& StringUtils.isNotBlank(req.getSectionId())) {
//			List<WarrantyMaster> WarrantyList = getWarrantyDescriptionExistDetails(req.getWarrantyDescription() , req.getCompanyId() , req.getBranchCode(),req.getProductId(),req.getSectionId());
//			
//			if (WarrantyList.size()>0 &&  (! req.getWarrantyId().equalsIgnoreCase(WarrantyList.get(0).getWarrantyId().toString())) ) {
//				errorList.add(new Error("01", "WarrantyDescription", "This WarrantyDescription Already Exist "));
//			}
//			
//		}
//		
//		
//		if (StringUtils.isBlank(req.getCompanyId())) {
//			errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
//		}
//		
//		if (StringUtils.isBlank(req.getBranchCode())) {
//			errorList.add(new Error("02", "BranchCode", "Please Select BranchCode"));
//		}
//		if (StringUtils.isBlank(req.getProductId())) {
//			errorList.add(new Error("10", "ProductId", "Please Enter ProductId"));
//		}
//		if (StringUtils.isBlank(req.getSectionId())) {
//			errorList.add(new Error("11", "SectionId", "Please Enter SectionId"));
//		}
//		}
//		
///*		if (StringUtils.isBlank(req.getOccupationNameAr())) {
//			errorList.add(new Error("03", "OccupationNameAr", "Please Select OccupationNameAr"));
//		}else if (req.getOccupationNameAr().length() > 100){
//			errorList.add(new Error("03","OccupationNameAr", "Please Enter OccupationNameAr 100 Characters")); 
//		} */
//		
//		if (StringUtils.isBlank(reqList.getRemarks())) {
//			errorList.add(new Error("04", "Remarks", "Please Select Remarks "));
//		}else if (reqList.getRemarks().length() > 100){
//			errorList.add(new Error("04","Remarks", "Please Enter Remarks within 100 Characters")); 
//		}
//		
//		// Date Validation 
//		Calendar cal = new GregorianCalendar();
//		Date today = new Date();
//		cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);;
//		today = cal.getTime();
//		if (reqList.getEffectiveDateStart() == null || StringUtils.isBlank(reqList.getEffectiveDateStart().toString())) {
//			errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start"));
//
//		} else if (reqList.getEffectiveDateStart().before(today)) {
//			errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
//		}
//		//Status Validation
//		if (StringUtils.isBlank(reqList.getStatus())) {
//			errorList.add(new Error("06", "Status", "Please Enter Status"));
//		} else if (reqList.getStatus().length() > 1) {
//			errorList.add(new Error("06", "Status", "Enter Status in 1 Character Only"));
//		}else if(!("Y".equals(reqList.getStatus())||"N".equals(reqList.getStatus()) || "R".equals(reqList.getStatus()))) {
//			errorList.add(new Error("06", "Status", "Enter Status in Y or N or R Only"));
//		}
//
//		if (StringUtils.isBlank(reqList.getCoreAppCode())) {
//			errorList.add(new Error("07", "CoreAppCode", "Please Select CoreAppCode"));
//		}else if (reqList.getCoreAppCode().length() > 20){
//			errorList.add(new Error("07","CoreAppCode", "Please Enter CoreAppCode within 20 Characters")); 
//		}
//		if (StringUtils.isBlank(reqList.getRegulatoryCode())) {
//			errorList.add(new Error("08", "RegulatoryCode", "Please Select RegulatoryCode"));
//		}else if (reqList.getRegulatoryCode().length() > 20){
//			errorList.add(new Error("08","RegulatoryCode", "Please Enter RegulatoryCode within 20 Characters")); 
//		}
//		if (StringUtils.isBlank(reqList.getCreatedBy())) {
//			errorList.add(new Error("09", "CreatedBy", "Please Select CreatedBy"));
//		}else if (reqList.getCreatedBy().length() > 100){
//			errorList.add(new Error("09","CreatedBy", "Please Enter CreatedBy within 100 Characters")); 
//		}		
////		if (StringUtils.isBlank(req.getPolicyType())) {
////			errorList.add(new Error("12", "PolicyType", "Please Enter PolicyType"));
////		}
//		
//		if (StringUtils.isBlank(reqList.getDocRefNo())) {
//			errorList.add(new Error("13", "DocRefNo", "Please Enter DocRefNo"));
//		}else if (reqList.getDocRefNo().length() > 50){
//			errorList.add(new Error("13","DocRefNo", "Please Enter DocRefNo within 50 Characters")); 
//		}		
//		
//		
//	} catch (Exception e) {
//		log.error(e);
//		e.printStackTrace();
//	}
//	return errorList;
//}
//@Override
//public SuccessRes saveWarranty(WarrantyMasterListSaveReq req1) {
//	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//	SuccessRes res = new SuccessRes();
//	WarrantyMaster saveData = new WarrantyMaster();
//	List<WarrantyMaster> list  = new ArrayList<WarrantyMaster>();
//	DozerBeanMapper dozerMapper = new DozerBeanMapper();
//	try {
//		Integer amendId = 0;
//		Date StartDate = req1.getEffectiveDateStart();
//		String end = "31/12/2050";
//		Date endDate = sdf.parse(end);
//		long MILLS_IN_A_DAY = 1000*60*60*24;
//		Date oldEndDate = new Date(req1.getEffectiveDateStart().getTime()- MILLS_IN_A_DAY);
//		Date entryDate = null;
//		String createdBy ="";
//		createdBy = req1.getCreatedBy();
//		
//		for(WarrantyMasterReq req : req1.getWarrantyReq()) {
//		Integer warrantyId = 0;
//		if(StringUtils.isBlank(req.getWarrantyId())) {
//			Integer totalCount = getMasterTableCount(req.getCompanyId(),req.getBranchCode(),req.getProductId(),req.getSectionId());
//			warrantyId = totalCount+1;
//			entryDate = new Date();
//			res.setResponse("Saved Successfully");
//			res.setSuccessId(warrantyId.toString());
//		}
//		else {
//			warrantyId = Integer.valueOf(req.getWarrantyId());
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<WarrantyMaster> query = cb.createQuery(WarrantyMaster.class);
//			//Findall
//			Root<WarrantyMaster> b = query.from(WarrantyMaster.class);
//			//select
//			query.select(b);
//			//Orderby
//			List<Order> orderList = new ArrayList<Order>();
//			orderList.add(cb.desc(b.get("effectiveDateStart")));
//			//Where
//			Predicate n1 = cb.equal(b.get("warrantyId"),req.getWarrantyId());
//			Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
//			Predicate n3 = cb.equal(b.get("branchCode"),req.getBranchCode());
//			Predicate n4 = cb.equal(b.get("productId"),req.getProductId());
//			Predicate n5 = cb.equal(b.get("sectionId"),req.getSectionId());
////			Predicate n6 = cb.equal(b.get("policyType"),req.getPolicyType());
////				
//			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
//			
//			// Get Result
//			TypedQuery<WarrantyMaster> result = em.createQuery(query);
//			int limit=0, offset=2;
//			result.setFirstResult(limit * offset);
//			result.setMaxResults(offset);
//			list = result.getResultList();
//			if(list.size()>0) {
//				Date beforeOneDay = new Date(new Date().getTime()- MILLS_IN_A_DAY);
//				if(list.get(0).getEffectiveDateStart().before(beforeOneDay)) {
//					amendId = list.get(0).getAmendId()+1;
//					entryDate = new Date();
//					WarrantyMaster lastRecord = list.get(0);
//					lastRecord.setEffectiveDateEnd(oldEndDate);
//					repo.saveAndFlush(lastRecord);
//				}
//				else {
//					amendId = list.get(0).getAmendId();
//					entryDate = list.get(0).getEntryDate();
//					createdBy = list.get(0).getCreatedBy();
//					saveData = list.get(0);
//					if(list.size()>1) {
//						WarrantyMaster lastRecord = list.get(1);	
//						lastRecord.setEffectiveDateEnd(oldEndDate);
//						repo.saveAndFlush(lastRecord);
//					}
//				}
//			}
//			res.setResponse("Updated Successfully");
//			res.setSuccessId(warrantyId.toString());
//		}
//		dozerMapper.map(req, saveData);
//		saveData.setWarrantyId(warrantyId);
//		saveData.setEffectiveDateStart(StartDate);
//		saveData.setEffectiveDateEnd(endDate);
//		saveData.setCreatedBy(createdBy);
//		saveData.setEntryDate(entryDate);
//		saveData.setUpdatedBy(req1.getCreatedBy());
//		saveData.setUpdatedDate(new Date());
//		saveData.setAmendId(amendId);
//		saveData.setProductId(req.getProductId()==null? "99999":req.getProductId());
//		saveData.setSectionId(req.getSectionId()==null? "99999" : req.getSectionId());
////		saveData.setPolicyType(req.getPolicyType()==null?"" : "99999");
//		saveData.setCoreAppCode(req1.getCoreAppCode());
//		saveData.setRegulatoryCode(req1.getRegulatoryCode());
//		saveData.setStatus(req1.getStatus());
//		saveData.setRemarks(req1.getRemarks());
//		saveData.setDocRefNo(req1.getDocRefNo());
//		repo.saveAndFlush(saveData);	
//		log.info("Saved Details is --> " + json.toJson(saveData));	
//	}
//	}
//	catch(Exception e) {
//		e.printStackTrace();
//		log.info("Exception is --> " + e.getMessage());
//		return null;
//	}
//	return res;
//	}
@Override
public List<WarrantyMasterRes> getallNonSelectedWarranty(NonSelectedClausesGetAllReq req) {
	List<WarrantyMasterRes> resList = new ArrayList<WarrantyMasterRes>();
	DozerBeanMapper dozerMapper = new  DozerBeanMapper();
	try {
		Date today  = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 1);
		Date todayEnd = cal.getTime();
		
		List<WarrantyMaster> list = new ArrayList<WarrantyMaster>();
	
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WarrantyMaster> query = cb.createQuery(WarrantyMaster.class);

		// Find All
		Root<WarrantyMaster> b = query.from(WarrantyMaster.class);

		// Select
		query.select(b);

		// Effective Date Max Filter
		Subquery<Date> effectiveDate = query.subquery(Date.class);
		Root<WarrantyMaster> ocpm1 = effectiveDate.from(WarrantyMaster.class);
		effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
		Predicate a1 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a2 = cb.equal(ocpm1.get("productId"), b.get("productId"));
		Predicate a3 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
		Predicate a4 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
		Predicate a5 = cb.lessThanOrEqualTo(b.get("effectiveDateStart"),today);
		effectiveDate.where(a1,a2,a3,a4,a5);

		// Effective Date End
		Subquery<Date> effectiveDate2 = query.subquery(Date.class);
		Root<WarrantyMaster> ocpm2 = effectiveDate2.from(WarrantyMaster.class);
		effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
		Predicate a6 = cb.equal(ocpm2.get("companyId"), b.get("companyId"));
		Predicate a7 = cb.equal(ocpm2.get("productId"), b.get("productId"));
		Predicate a8 = cb.equal(ocpm2.get("sectionId"), b.get("sectionId"));
		Predicate a9 = cb.equal(ocpm2.get("branchCode"), b.get("branchCode"));
		Predicate a10 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
		effectiveDate2.where(a6,a7,a8,a9,a10);
		
		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(b.get("warrantyDescription")));
		
		// Company Product Effective Date Max Filter
		Subquery<Long> clause = query.subquery(Long.class);
		Root<WarrantyMaster> cs = clause.from(WarrantyMaster.class);
		Subquery<Date> effectiveDate3 = query.subquery(Date.class);
		Root<WarrantyMaster> ocpm3 = effectiveDate3.from(WarrantyMaster.class);
		effectiveDate3.select(cb.greatest(ocpm3.get("effectiveDateStart").as(Date.class)));
		Predicate eff1 = cb.equal(ocpm3.get("companyId"), cs.get("companyId"));
		Predicate eff2 = cb.equal(ocpm3.get("productId"), cs.get("productId"));
		Predicate eff3 = cb.equal(ocpm3.get("sectionId"), cs.get("sectionId"));
		Predicate eff4 = cb.equal(ocpm3.get("branchCode"), cs.get("branchCode"));
		Predicate eff5 = cb.lessThanOrEqualTo(ocpm3.get("effectiveDateStart"),today);
		effectiveDate3.where(eff1,eff2,eff3,eff4,eff5);
		
		Subquery<Date> effectiveDate4 = query.subquery(Date.class);
		Root<WarrantyMaster> ocpm4 = effectiveDate4.from(WarrantyMaster.class);
		effectiveDate4.select(cb.greatest(ocpm4.get("effectiveDateEnd").as(Date.class)));
		Predicate eff6 = cb.equal(ocpm4.get("companyId"), cs.get("companyId"));
		Predicate eff7 = cb.equal(ocpm4.get("productId"), cs.get("productId"));
		Predicate eff8 = cb.equal(ocpm4.get("sectionId"), cs.get("sectionId"));
		Predicate eff9 = cb.equal(ocpm4.get("branchCode"), cs.get("branchCode"));
		Predicate eff10 = cb.lessThanOrEqualTo(ocpm4.get("effectiveDateEnd"),todayEnd);
		effectiveDate4.where(eff6,eff7,eff8,eff9,eff10);
		
		// Product Section Filter
		clause.select(cs.get("warrantyId"));
		Predicate cs1 = cb.equal(cs.get("companyId"), req.getCompanyId());
		Predicate cs2 = cb.equal(cs.get("productId"), req.getProductId());
		Predicate cs3 = cb.equal(cs.get("sectionId"),req.getSectionId());
		Predicate cs4 = cb.equal(cs.get("effectiveDateStart"),effectiveDate3);
		Predicate cs5 = cb.equal(cs.get("effectiveDateEnd"),effectiveDate4);
		Predicate cs6 = cb.equal(cs.get("branchCode"), req.getBranchCode());
		clause.where(cs1,cs2,cs3,cs4,cs5,cs6);
		
		// Where
		Expression<String>e0= b.get("warrantyId");
		Predicate n1 = cb.equal(b.get("companyId"), req.getCompanyId());
		Predicate n2 = cb.equal(b.get("productId"), req.getProductId());
		Predicate n3 = cb.equal(b.get("sectionId"),"0");
		Predicate n6 = cb.equal(b.get("branchCode"), req.getBranchCode());
		Predicate n9 = e0.in(clause).not();
	//	Predicate n10 = cb.equal(cs.get("status"), "Y");
		query.where(n1,n2,n6,n3,n9).orderBy(orderList);

		// Get Result
		TypedQuery<WarrantyMaster> result = em.createQuery(query);
		list = result.getResultList();
		
//		// Map
		for (WarrantyMaster data : list ) {
			WarrantyMasterRes res = new WarrantyMasterRes();

			res = dozerMapper.map(data, WarrantyMasterRes.class);
			res.setProductId(data.getProductId());
			resList.add(res);
		}

	} catch (Exception e) {
		e.printStackTrace();
		log.info(e.getMessage());
		return null;

	}
	return resList;
}
	


@Override
public WarrantyMasterRes getByWarrantyId(WarrantyMasterGetReq req) {
	WarrantyMasterRes res = new WarrantyMasterRes();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();

		List<WarrantyMaster> list = new ArrayList<WarrantyMaster>();
	
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WarrantyMaster> query = cb.createQuery(WarrantyMaster.class);

		// Find All
		Root<WarrantyMaster> b = query.from(WarrantyMaster.class);

		// Select
		query.select(b);

		// Amend ID Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<WarrantyMaster> ocpm1 = amendId.from(WarrantyMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("warrantyId"), b.get("warrantyId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
		Predicate a5 = cb.equal(ocpm1.get("sectionId"),b.get("sectionId"));
//		Predicate a6 = cb.equal(ocpm1.get("policyType"),b.get("policyType"));

		amendId.where(a1, a2,a3,a4,a5);

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(b.get("sectionId")));

		// Where
		Predicate n1 = cb.equal(b.get("amendId"), amendId);
		Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
		Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
		Predicate n4 = cb.equal(b.get("warrantyId"), req.getWarrantyId());
		Predicate n8 = cb.equal(b.get("productId"), req.getProductId());
		Predicate n11 = cb.equal(b.get("sectionId"), req.getSectionId());
//		Predicate n12 = cb.equal(b.get("sectionId"), "99999");
//		Predicate n13 = cb.or(n12,n11);
	//	Predicate n14 = cb.equal(b.get("branchCode"), "99999");
	//	Predicate n15 = cb.or(n3,n14);

		query.where(n1,n2,n4,n11,n8,n3).orderBy(orderList);
		
		// Get Result
		TypedQuery<WarrantyMaster> result = em.createQuery(query);

		list = result.getResultList();
		if(list.size()>0) {
		list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getWarrantyId()))).collect(Collectors.toList());
		list.sort(Comparator.comparing(WarrantyMaster :: getWarrantyDescription ));
		
		res = mapper.map(list.get(0), WarrantyMasterRes.class);
		res.setWarrantyId(list.get(0).getWarrantyId().toString());
		res.setEntryDate(list.get(0).getEntryDate());
		res.setEffectiveDateStart(list.get(0).getEffectiveDateStart());
		res.setEffectiveDateEnd(list.get(0).getEffectiveDateEnd());
		res.setCoreAppCode(list.get(0).getCoreAppCode());
		res.setCodeDescLocal(list.get(0).getWarrantyDescriptionLocal());
		if(list.get(0).getPdfLocation() != null) {
			File file = new File(list.get(0).getPdfLocation());
			if(file.exists()) {
				GetFileFromPath filePath = new GetFileFromPath(list.get(0).getPdfLocation());
				res.setPdfLocation(filePath.call().getImgUrl());
			}
		}
		} 
	}catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return res;
}
		@Override
		public List<Error> validateWarrantyMultiInsert(List<WarrantyMasterReq> reqList) {
			List<Error> errorList = new ArrayList<Error>();

			try {
				
				if (reqList == null || reqList.size()<=0) {
					errorList.add(new Error("02", "Warranty", "Please Select Atleast One Warranty"));
				}else {
					Integer rowNo = 0 ;
					for (WarrantyMasterReq data : reqList ) {
						rowNo = rowNo + 1 ;
						if (StringUtils.isBlank( data.getBranchCode()) ) {
							errorList.add(new Error("01", "BranchCode", "Please Enter BranchCode in Row no : " + rowNo));
						}
						if (StringUtils.isBlank( data.getCompanyId()) ) {
							errorList.add(new Error("01", "InsuranceId", "Please Enter InsuranceId in Row no : " + rowNo));
						}
						if (StringUtils.isBlank( data.getCreatedBy()))  {
							errorList.add(new Error("01", "CreatedBy", "Please Enter CreatedBy in Row no : " + rowNo));
						}
						if (StringUtils.isBlank( data.getProductId()) ) {
							errorList.add(new Error("01", "ProductId", "Please Enter ProductId in Row no : " + rowNo));
						}
						if (StringUtils.isBlank( data.getSectionId()) ) {
							errorList.add(new Error("01", "SectionId", "Please Enter SectionId in Row no : " + rowNo));
						}
						if (StringUtils.isBlank( data.getWarrantyId()) ) {
							errorList.add(new Error("01", "WarrantyId", "Please Enter WarrantyId in Row no : " + rowNo));
						}
					}
					
				 
				}	
				
			} catch (Exception e) {
				log.error(e);
				e.printStackTrace();
			}
			return errorList;
		}
		@Override
		public SuccessRes saveWarrantyMultiInsert(List<WarrantyMasterReq> reqList) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			SuccessRes res = new SuccessRes();
			List<WarrantyMaster> list  = new ArrayList<WarrantyMaster>();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				String end = "31/12/2050";
				List<String> warrantyIds = reqList.stream().map( WarrantyMasterReq :: getWarrantyId ).collect(Collectors.toList()); 
			
					String createdBy ="";
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<WarrantyMaster> query = cb.createQuery(WarrantyMaster.class);
					//Findall
					Root<WarrantyMaster> b = query.from(WarrantyMaster.class);
					//select
					query.select(b);
					//Orderby
					List<Order> orderList = new ArrayList<Order>();
					orderList.add(cb.desc(b.get("amendId")));
					
					// Amend ID Max Filter
					Subquery<Long> amendId = query.subquery(Long.class);
					Root<WarrantyMaster> ocpm1 = amendId.from(WarrantyMaster.class);
					amendId.select(cb.max(ocpm1.get("amendId")));
					Predicate a1 = cb.equal(ocpm1.get("warrantyId"), b.get("warrantyId"));
					Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
					Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
					Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
					Predicate a5 = cb.equal(ocpm1.get("sectionId"),b.get("sectionId"));
//					Predicate a6 = cb.equal(ocpm1.get("policyType"),b.get("policyType"));
					amendId.where(a1,a2,a3,a4,a5);
					// Where
					Expression<String> e0 = b.get("warrantyId");
					
					
					//Where
					Predicate n1 = e0.in(warrantyIds);
					Predicate n2 = cb.equal(b.get("companyId"),reqList.get(0).getCompanyId());
					Predicate n3 = cb.equal(b.get("branchCode"),reqList.get(0).getBranchCode());
					Predicate n4 = cb.equal(b.get("productId"),reqList.get(0).getProductId() );
					Predicate n5 = cb.equal(b.get("sectionId"),"0");
					Predicate n6 = cb.equal(b.get("amendId"),amendId);
					query.where(n1,n2,n3,n4,n5,n5,n6).orderBy(orderList);
					TypedQuery<WarrantyMaster> result = em.createQuery(query);
					list = result.getResultList();
					
					for (WarrantyMaster data :  list) {
						WarrantyMaster save = new WarrantyMaster();
						dozerMapper.map(data, save);
						save.setCreatedBy(createdBy);
						save.setEntryDate(new Date());
						save.setUpdatedBy(createdBy);
						save.setUpdatedDate(new Date());
						save.setAmendId(0);
						save.setProductId(reqList.get(0).getProductId());
						save.setSectionId(reqList.get(0).getSectionId());
						repo.saveAndFlush(save);	
						log.info("Saved Details is --> " + json.toJson(save));	
					}
					res.setResponse("Added Succesfully");
					res.setSuccessId("");
			}
			catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --> " + e.getMessage());
				return null;
			}
			return res;
			}

	
	
}
