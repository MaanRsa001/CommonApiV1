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
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.MotorMakeMaster;
import com.maan.eway.bean.MotorMakeModelMaster;
import com.maan.eway.master.req.MakeModelChangeStatusReq;
import com.maan.eway.master.req.MotorMakeModelGetAllReq;
import com.maan.eway.master.req.MotorMakeModelGetReq;
import com.maan.eway.master.req.MotorMakeModelSaveReq;
import com.maan.eway.master.res.MotorMakeModelGetRes;
import com.maan.eway.master.service.MotorMakeModelMasterService;
import com.maan.eway.repository.MotorMakeModelMasterRepository;
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
public class MotorMakeModelMasterServiceImpl implements MotorMakeModelMasterService {

	@Autowired
	private MotorMakeModelMasterRepository repo;

	@PersistenceContext
	private EntityManager em;

	Gson json = new Gson();

	private Logger log = LogManager.getLogger(MotorMakeModelMasterServiceImpl.class);

	@Override
	public List<String> validateMotorMakeModel(MotorMakeModelSaveReq req) {
		List<String> errorList = new ArrayList<String>();

		try {
			
			if (StringUtils.isBlank(req.getModelNameEn())) {
				//errorList.add(new Error("02", "ModelName", "Please Select ModelName"));
				errorList.add("1928");
			}else if (req.getModelNameEn().length() > 100){
				//errorList.add(new Error("02","ModelName", "Please Enter ModelName 100 Characters")); 
				errorList.add("1929");
			}else if (StringUtils.isBlank(req.getModelId()) && StringUtils.isNotBlank(req.getMakeId())  && StringUtils.isNotBlank(req.getBodyId()) 
				&& StringUtils.isNotBlank(req.getInsuranceId()) && StringUtils.isNotBlank(req.getBranchCode())) {
				List<MotorMakeModelMaster> ModelList = getModelNameExistDetails(req.getMakeId()  ,req.getBodyId() , req.getModelNameEn() ,  req.getInsuranceId() , req.getBranchCode());
				if (ModelList.size()>0 ) {
					//errorList.add(new Error("01", "ModelName", "This ModelName Already Exist "));
					errorList.add("1930");
				}
			}
//		
//			if (StringUtils.isBlank(req.getModelNameEn())) {
//				errorList.add(new Error("02", "ModelName", "Please Select ModelName"));
//			}else if (req.getModelNameEn().length() > 100){
//				errorList.add(new Error("02","ModelName", "Please Enter ModelName 100 Characters")); 
//			}else if (StringUtils.isBlank(req.getModelId()) && StringUtils.isNotBlank(req.getMakeId())  && StringUtils.isNotBlank(req.getBodyId()) 
//					&& StringUtils.isNotBlank(req.getInsuranceId()) && StringUtils.isNotBlank(req.getBranchCode())) {
//				List<MotorMakeModelMaster> ModelList = getModelNameExistDetails(req.getMakeId()  ,req.getBodyId() , req.getMakeNameEn() ,  req.getInsuranceId() , req.getBranchCode());
//				if (ModelList.size()>0 ) {
//					errorList.add(new Error("01", "ModelName", "This ModelName Already Exist "));
//				}
//			}else if (StringUtils.isNotBlank(req.getModelId()) && StringUtils.isNotBlank(req.getMakeId())  && StringUtils.isNotBlank(req.getBodyId()) 
//					&& StringUtils.isNotBlank(req.getInsuranceId()) && StringUtils.isNotBlank(req.getBranchCode()) ) {
//				List<MotorMakeModelMaster> ModelList = getModelNameExistDetails(req.getMakeId()  ,req.getBodyId() , req.getMakeNameEn() , req.getInsuranceId() , req.getBranchCode());
//				
//				if (ModelList.size()>0 &&  (! req.getModelId().equalsIgnoreCase(ModelList.get(0).getModelId().toString())) ) {
//					errorList.add(new Error("01", "ModelName", "This ModelName Already Exist "));
//				}
//				
//			}
			
//			
//			if (StringUtils.isBlank(req.getInsuranceId())) {
//				errorList.add(new Error("02", "InsuranceId", "Please Enter InsuranceId"));
//			}
			if (StringUtils.isBlank(req.getMakeId())) {
			//	errorList.add(new Error("02", "MakeId", "Please Enter MakeId"));
				errorList.add("1337");
			}
			
//			if(StringUtils.isBlank(req.getBaseRate())) {
//			//	errorList.add(new Error("12","BaseRate","Please Enter the base rate"));
//				errorList.add("1338");
//			}
//			if (StringUtils.isBlank(req.getMakeNameEn())) {
//				errorList.add(new Error("02", "MakeName", "Please Enter MakeName"));
//			}
//			
//			if (StringUtils.isBlank(req.getBodyId())) {
//				errorList.add(new Error("02", "BodyId", "Please Enter BodyId"));
//			}
//			if (StringUtils.isBlank(req.getBodyNameEn())) {
//			//	errorList.add(new Error("02", "BodyName", "Please Enter BodyName"));
//				errorList.add("1341");
//			}
			
//			if (StringUtils.isBlank(req.getInsuranceId())) {
//				errorList.add(new Error("02", "InsuranceId", "Please Enter InsuranceId"));
//			}
//			
			if (StringUtils.isBlank(req.getBranchCode())) {
			//	errorList.add(new Error("02", "BranchCode", "Please Select BranchCode"));
				errorList.add("1256");
			}
	/*		if (StringUtils.isBlank(req.getOccupationNameAr())) {
				errorList.add(new Error("03", "OccupationNameAr", "Please Select OccupationNameAr"));
			}else if (req.getOccupationNameAr().length() > 100){
				errorList.add(new Error("03","OccupationNameAr", "Please Enter OccupationNameAr 100 Characters")); 
			} */
			
			if (StringUtils.isBlank(req.getRemarks())) {
			//	errorList.add(new Error("04", "Remarks", "Please Select Remarks "));
				errorList.add("1259");
			}else if (req.getRemarks().length() > 100){
				//errorList.add(new Error("04","Remarks", "Please Enter Remarks within 100 Characters")); 
				errorList.add("1260");
			}
			
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
			//errorList.add(new Error("05", "Status", "Please Select Valid Status One Character Only Allwed"));
			errorList.add("1264");
			}else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
			//errorList.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
			errorList.add("1265");
			}

//			if (StringUtils.isBlank(req.getCoreAppCode())) {
//				errorList.add(new Error("07", "CoreAppCode", "Please Select CoreAppCode"));
//			}else if (req.getCoreAppCode().length() > 20){
//				errorList.add(new Error("07","CoreAppCode", "Please Enter CoreAppCode within 20 Characters")); 
//			}
//			
			
//			if (StringUtils.isBlank(req.getRegulatoryCode())) {
//				//errorList.add(new Error("08", "RegulatoryCode", "Please Select RegulatoryCode"));
//				errorList.add("1268");
//			}else if (req.getRegulatoryCode().length() > 20){
//			//	errorList.add(new Error("08","RegulatoryCode", "Please Enter RegulatoryCode within 20 Characters")); 
//				errorList.add("1269");
//			}
//			if (StringUtils.isBlank(req.getCreatedBy())) {
//				errorList.add(new Error("09", "CreatedBy", "Please Select CreatedBy"));
//			}else if (req.getCreatedBy().length() > 100){
//				errorList.add(new Error("09","CreatedBy", "Please Enter CreatedBy within 100 Characters")); 
//			}
//			if(StringUtils.isBlank(req.getNetRate())) {
//			//	errorList.add(new Error("10","NetRate","Please Enter the NetRate"));
//				errorList.add("1342");
//			}
			
//			if(StringUtils.isBlank(req.getRopBodyid())) {
//			//	errorList.add(new Error("11","RopBodyId","Please enter the RopBodyId"));
//				errorList.add("1343");
//			}
			
			
//			if(StringUtils.isBlank(req.getVehCc())){
//			//	errorList.add(new Error("13","VechCC","Please Enter the VechCC"));
//				errorList.add("1344");
//			}
//			if(StringUtils.isBlank(req.getTplRate())) {
//			//	errorList.add(new Error("14","TplRate","Please Enter the tpl rate"));
//				errorList.add("1345");
//			}
//			if(StringUtils.isBlank(req.getOtherBodyId1())) {
//			//	errorList.add(new Error("15","OtherBodyId1","Please Enter the OtherBodyId1"));
//				errorList.add("1346");
//			}
//			else if(req.getOtherBodyId1().length()>100) {
//				//errorList.add(new Error("15","OtherBodyId1","Please Enter the otherBodyId1 characters between 100"));
//				errorList.add("1347");
//				
//			}
//			if(StringUtils.isBlank(req.getOtherBodyId2())) {
//			//	errorList.add(new Error("16","OtherBodyId2","Please Enter the OtherBodyId2"));
//				errorList.add("1348");
//			}
//			else if(req.getOtherBodyId2().length()>100) {
//			//	errorList.add(new Error("16","OtherBodyId2","Please Enter the OtherBodyId2 characters between 100"));
//				errorList.add("1349");
//				
//			}
			
////			if(StringUtils.isBlank(req.getOtherMakeId1())) {
////			//	errorList.add(new Error("17","OtherMakeId1","Please Enter the OtherMakeId1"));
////				errorList.add("1350");
////			}
////			else if(req.getOtherMakeId1().length()>100) {
////				//errorList.add(new Error("17","OtherMakeId1","Please Enter the otherMakeId1 characters between 100"));
////				errorList.add("1351");
////				
////			}
////			
////			if(StringUtils.isBlank(req.getOtherMakeId2())) {
////			//	errorList.add(new Error("18","OtherMakeId2","Please Enter the OtherMakeId2"));
////				errorList.add("1352");
////			}
////			else if(req.getOtherMakeId2().length()>100) {
////			//	errorList.add(new Error("18","OtherMakeId2","Please Enter the otherMakeId2 characters between 100"));
////				errorList.add("1353");
////				
////			}
////			if(StringUtils.isBlank(req.getOtherModelId1())) {
////			//	errorList.add(new Error("19","OtherModelId1","Please Enter the OtherModelId1"));
////				errorList.add("1354");
////			}
////			else if(req.getOtherModelId1().length()>1000) {
////			//	errorList.add(new Error("19","OtherModelId1","Please Enter the otherModelId1 characters between 1000"));
////				errorList.add("1355");
////				
////			}
//			
//			if(StringUtils.isBlank(req.getOtherModelId2())) {
//				//errorList.add(new Error("20","OtherModelId2","Please Enter the OtherModelId2"));
//				errorList.add("1356");
//			}
//			else if(req.getOtherModelId2().length()>100) {
//				//errorList.add(new Error("20","OtherModelId2","Please Enter the otherModelId2 characters between 100"));
//				errorList.add("1357");
//				
//			}
//			
//			if(StringUtils.isBlank(req.getVehFueltype())) {
//			//	errorList.add(new Error("21","VehFueltype","Please Enter the VehFueltype"));
//				errorList.add("1358");
//			}
//			
//			if(StringUtils.isBlank(req.getVehClass())) {
//			//	errorList.add(new Error("22","VehClass","Please Enter the VehClass"));
//				errorList.add("1359");
//			}
//			
//			if(StringUtils.isBlank(req.getVehClassEn())) {
//			//	errorList.add(new Error("23","VehClassEn","Please Enter the VehClassEn"));
//				errorList.add("1360");
//			}
//			else if(req.getVehClassEn().length()>1000) {
//				//errorList.add(new Error("23","VehClassEn","Please Enter the VehclassEn characters between 1000"));
//				errorList.add("1361");
//			}
//			
//			if(StringUtils.isBlank(req.getVehManfCountry())) {
//			//	errorList.add(new Error("24","VehManfCountry","Please Enter the VehManfCounry"));
//				errorList.add("1362");
//			}
//			else if(req.getVehManfCountry().length()>20) {
//			//	errorList.add(new Error("24","VehManfCountry","Please Enter the VehManfCountry between characters 20"));
//				errorList.add("1363");
//			}
//			if(StringUtils.isBlank(req.getVehManfCountryEn())) {
//			//	errorList.add(new Error("25","VehManfCountryEn","Please Enter the VehManfCountryEn"));
//				errorList.add("1364");
//			}
//			else if(req.getVehManfCountryEn().length()>1000) {
//			//	errorList.add(new Error("25","VehManfCountryEn","Please Enter the VehManfCountryEn between characters 1000"));
//				errorList.add("1365");
//			}
//			
//			if(StringUtils.isBlank(req.getVehManfRegion())) {
//			//	errorList.add(new Error("26","VehManfRegion","Please Enter the VehManfRegion"));
//				errorList.add("1366");
//			}
//			
//			if(StringUtils.isBlank(req.getVehManfRegionEn())) {
//			//	errorList.add(new Error("27","VehManfRegionEn","Please Enter the ManfRegionEn"));
//				errorList.add("1367");
//			}
//			else if(req.getVehManfRegionEn().length()>1000) {
//			//	errorList.add(new Error("28","VehManfRegionEn","Please Enter the VehManfRegionEn characters between 1000"));
//				errorList.add("1368");
//			}
//			
//			if(StringUtils.isBlank(req.getCoreMakeId())) {
//			//	errorList.add(new Error("29","CoreMakeId","Please Enter the CoreMakeId"));
//				errorList.add("1369");
//			}
//			else if(req.getCoreMakeId().length()>100) {
//			//	errorList.add(new Error("29","CoreMakeId","Please Enter the CoreMakeId characters between 100"));
//				errorList.add("1370");
//			}
//						
//			if(StringUtils.isBlank(req.getCoreModelId())) {
//			//	errorList.add(new Error("30","CoreModelId","Please Enter the CoreModelId"));
//				errorList.add("1371");
//			}
//			else if(req.getCoreModelId().length()>100) {
//			//	errorList.add(new Error("30","CoreModelId","Please Enter the CoreModeld characters between 100"));
//				errorList.add("1372");
//			}
//			
////			if(StringUtils.isBlank(req.getCoreRefNo())) {
////				errorList.add(new Error("31","CoreRefNo","Please Enter the CoreRefno"));
////			}
////			else if(req.getCoreRefNo().length()>100) {
////				errorList.add(new Error("32","CoreRefNo","Please Enter the CoreRefNo characters between 100"));
////			}
//			
//			
////			if(StringUtils.isBlank(req.getCoreBodyId())) {
////				errorList.add(new Error("31","CoreBodyId","Please Enter the CoreBodyId"));
////			}
////			
////			else if(req.getCoreBodyId().length()>100) {
////				errorList.add(new Error("31","CoreBodyId","Please Enter the CoreBodyId characters between 100"));
////			}
//			
//			
//			if(StringUtils.isBlank(req.getRefNo())) {
//			//	errorList.add(new Error("31","Refno","Please Enter the Refno"));
//				errorList.add("1373");
//			}
//			
//			if(StringUtils.isBlank(req.getPrimaCode())) {
//			//	errorList.add(new Error("32","PrimaCode","Please Enter the Prima code"));
//				errorList.add("1374");
//			}
			

			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}

	public List<MotorMakeModelMaster> getModelNameExistDetails(String makeId , String bodyId ,  String modelName , String InsuranceId , String branchCode) {
		List<MotorMakeModelMaster> list = new ArrayList<MotorMakeModelMaster>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeModelMaster> query = cb.createQuery(MotorMakeModelMaster.class);

			// Find All
			Root<MotorMakeModelMaster> b = query.from(MotorMakeModelMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorMakeModelMaster> ocpm1 = amendId.from(MotorMakeModelMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("modelId"), b.get("modelId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a5 = cb.greaterThanOrEqualTo(ocpm1.get("effectiveDateEnd"), today);
			amendId.where(a1,a2,a3,a4,a5);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(cb.upper( b.get("modelNameEn")), modelName.trim().toUpperCase());
			Predicate n9 = cb.equal( b.get("makeId"), makeId);
			Predicate n10 = cb.equal( b.get("bodyId"), bodyId);
			Predicate n3 = cb.equal(b.get("companyId"),InsuranceId);
			Predicate n4 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n4,n5);
//			Predicate n7 = cb.equal(b.get("makeId"), b.get("makeId"));
//			Predicate n8 = cb.equal(b.get("bodyId"), b.get("bodyId"));
//			query.where(n1,n2,n3,n6,n7,n8,n9,n10);
			query.where(n1,n2,n3,n6,n9,n10);
			
			// Get Result
			TypedQuery<MotorMakeModelMaster> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return list;
	}
	
	@Override
	public SuccessRes saveMotorMakeModel(MotorMakeModelSaveReq req) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		MotorMakeModelMaster saveData = new MotorMakeModelMaster();
		List<MotorMakeModelMaster> list = new ArrayList<MotorMakeModelMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Integer amendId=0;
			Date startDate = req.getEffectiveDateStart() ;
			String end = "31/12/2050";
			Date endDate = sdf.parse(end);
			long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLIS_IN_A_DAY);
			Date entryDate = null ;
			String createdBy = "" ;
			
			Integer modelId = 0 ;
			if(StringUtils.isBlank(req.getModelId())) {
				// Save
				Integer totalCount = getMasterTableCount( req.getInsuranceId() , req.getBranchCode());
				modelId =  totalCount+ 1 ;
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully");
				res.setSuccessId(modelId.toString());
			}
			else {
				// Update
				modelId = Integer.valueOf(req.getModelId());
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<MotorMakeModelMaster> query = cb.createQuery(MotorMakeModelMaster.class);
				//Find all
				Root<MotorMakeModelMaster> b = query.from(MotorMakeModelMaster.class);
				//Select 
				query.select(b);
//				
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("effectiveDateStart")));
				
				// Where
			//	Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
				Predicate n2 = cb.equal(b.get("modelId"), req.getModelId());
				Predicate n3 = cb.equal(b.get("companyId"), req.getInsuranceId());
				Predicate n4 = cb.equal(b.get("branchCode"), req.getBranchCode());
				Predicate n5 = cb.equal(b.get("makeId"), req.getMakeId());
				Predicate n6 = cb.equal(b.get("bodyId"), req.getBodyId());
				
				query.where(n2,n3,n4,n5,n6).orderBy(orderList);
				
				// Get Result 
				TypedQuery<MotorMakeModelMaster> result = em.createQuery(query);
				int limit = 0 , offset = 2 ;
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				list = result.getResultList();
				
				if(list.size()>0) {
					Date beforeOneDay = new Date(new Date().getTime() - MILLIS_IN_A_DAY);
				
					if ( list.get(0).getEffectiveDateStart().before(beforeOneDay)  ) {
						amendId = list.get(0).getAmendId() + 1 ;
						entryDate = new Date() ;
						createdBy = req.getCreatedBy();
						MotorMakeModelMaster lastRecord = list.get(0);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						
					} else {
						amendId = list.get(0).getAmendId() ;
						entryDate = list.get(0).getEntryDate() ;
						createdBy = list.get(0).getCreatedBy();
						saveData = list.get(0) ;
						if (list.size()>1 ) {
							MotorMakeModelMaster lastRecord = list.get(1);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}
					
				    }
				}
				res.setResponse("Updated Successfully");
				res.setSuccessId(modelId.toString());
			}
			dozerMapper.map(req, saveData);
			saveData.setModelId(modelId);
			saveData.setEffectiveDateStart(startDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setStatus(req.getStatus());
			saveData.setCompanyId(req.getInsuranceId());
			saveData.setBranchCode(req.getBranchCode());
			saveData.setEntryDate(entryDate);
			saveData.setUpdatedDate(new Date());
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setAmendId(amendId);
			saveData.setCoreAppCode(req.getCoreAppCode());
			saveData.setVehiclemodelcode(req.getVehicleModelCode()==null?0:Integer.valueOf(req.getVehicleModelCode()));
	        saveData.setRegulatoryCode(req.getRegulatoryCode());
	        saveData.setBatchId(req.getBatchId()!=null && !req.getBatchId().isEmpty() ?Integer.valueOf(req.getBatchId()) : 0);
	        saveData.setNetrate(req.getNetRate()!=null && !req.getNetRate().isEmpty()?Integer.valueOf(req.getNetRate()): 0);
	        saveData.setOthrBodyId1(req.getOtherBodyId1());
	        saveData.setOthrBodyId2(req.getOtherBodyId2());
	        saveData.setOthrMakeId1(req.getOtherMakeId1());
	        saveData.setOthrMakeId2(req.getOtherMakeId2());
	        saveData.setOthrModelId1(req.getOtherModelId1());
	        saveData.setOthrModelId2(req.getOtherModelId2());
	        saveData.setPremiaCode(req.getPrimaCode()!=null && !req.getPrimaCode().isEmpty()?Integer.valueOf(req.getPrimaCode()) : 0);
	        saveData.setTplrate(req.getTplRate()!=null && !req.getTplRate().isEmpty()?Integer.valueOf(req.getTplRate()): 0);
	        saveData.setVehCc(req.getVehCc()!=null && !req.getVehCc().isEmpty()?Integer.valueOf(req.getVehCc()): 0);
	        saveData.setVehFueltype(req.getVehFueltype()!=null && !req.getVehFueltype().isEmpty() ?Integer.valueOf(req.getVehFueltype()): 0);
	        saveData.setRemarks(req.getRemarks());
	        saveData.setBaserate(req.getBaseRate()!=null && !req.getBaseRate().isEmpty()?Integer.valueOf(req.getBaseRate()): 0);
	        saveData.setCoreBOdyId(req.getCoreBodyId()!=null && !req.getCoreBodyId().isEmpty()?"":req.getCoreBodyId());
	        saveData.setMakeNameEn(getMakeName(req.getInsuranceId(), req.getBranchCode() , req.getMakeId() )); // Make Name Query
	        saveData.setModelNameLocal(req.getCodeDescLocal());
	        repo.saveAndFlush(saveData);
			log.info("Saved Details is --> " + json.toJson(saveData));
			
			}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> "+ e.getMessage());
			return null;
		}
		return res;
		}
	
	public String getMakeName(String companyId , String branchCode , String makeId ) {
		String makeName = "" ;
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);;
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeMaster> query=  cb.createQuery(MotorMakeMaster.class);
			// Find All
			Root<MotorMakeMaster> c = query.from(MotorMakeMaster.class);
			//Select
			query.select(c);
			
			List<MotorMakeMaster> list = new ArrayList<MotorMakeMaster>();
				
			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<MotorMakeMaster> ocpm1 = effectiveDate.from(MotorMakeMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("makeId"),ocpm1.get("makeId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a5 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a6 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			effectiveDate.where(a1,a2,a5,a6);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<MotorMakeMaster> ocpm2 = effectiveDate2.from(MotorMakeMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("makeId"),ocpm2.get("makeId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a7 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a8 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			effectiveDate2.where(a3,a4,a7,a8);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));
			
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n8 = cb.equal(c.get("status"),"R");
			Predicate n9 = cb.or(n1,n8);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), companyId);
			Predicate n5 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n6 = cb.equal(c.get("branchCode"), "99999");
			Predicate n7 = cb.or(n5,n6);
			Predicate n10 = cb.equal(c.get("makeId"),makeId);
			query.where(n9,n2,n3,n4,n7,n10).orderBy(orderList);
			
			// Get Result
			TypedQuery<MotorMakeMaster> result = em.createQuery(query);
			list = result.getResultList();
			makeName = list.size() > 0 ? list.get(0).getMakeNameEn() : ""; 
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --->" + e.getMessage());
		return null;
	}
	return makeName;
	}

	@Override
	public MotorMakeModelGetRes getMotorMakeModel(MotorMakeModelGetReq req) {
		MotorMakeModelGetRes res = new MotorMakeModelGetRes();

		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<MotorMakeModelMaster> list = new ArrayList<MotorMakeModelMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeModelMaster> query = cb.createQuery(MotorMakeModelMaster.class);

			// Find All
			Root<MotorMakeModelMaster> b = query.from(MotorMakeModelMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorMakeModelMaster> ocpm1 = amendId.from(MotorMakeModelMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("modelId"), b.get("modelId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("makeId"), b.get("makeId"));
			amendId.where(a1, a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("amendId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3,n4);
			Predicate n6 = cb.equal(b.get("makeId"), req.getMakeId());
			Predicate n7 = cb.equal(b.get("modelId"), req.getModelId());
			Predicate n8 = cb.equal(b.get("bodyId"), req.getBodyId());
			query.where(n1,n2,n5,n6,n7,n8).orderBy(orderList);
			
			// Get Result
			TypedQuery<MotorMakeModelMaster> result = em.createQuery(query);
			list = result.getResultList();
				// Response
			// Map
			res = mapper.map(list.get(0) , MotorMakeModelGetRes.class);
			res.setRegulatoryCode(list.get(0).getRegulatoryCode()==null?"":list.get(0).getRegulatoryCode());
			res.setBatchId(list.get(0).getBatchId()==null?"":list.get(0).getBatchId().toString());
			res.setOtherBodyId1(list.get(0).getOthrBodyId1()==null?"":list.get(0).getOthrBodyId1());
			res.setOtherBodyId2(list.get(0).getOthrBodyId2()==null?"":list.get(0).getOthrBodyId2());
			res.setOtherMakeId1(list.get(0).getOthrMakeId1()==null?"":list.get(0).getOthrMakeId1());
			res.setOtherMakeId2(list.get(0).getOthrMakeId2()==null?"":list.get(0).getOthrMakeId2());
			res.setOtherModelId1(list.get(0).getOthrModelId1()==null?"":list.get(0).getOthrModelId1());
			res.setOtherModelId2(list.get(0).getOthrModelId2()==null?"":list.get(0).getOthrModelId2());
			res.setPrimaCode(list.get(0).getPremiaCode()==null?"":list.get(0).getPremiaCode().toString());
			res.setRefNo(list.get(0).getRefNo()==null?"":list.get(0).getRefNo().toString());
			res.setTplRate(list.get(0).getTplrate()==null?"":list.get(0).getTplrate().toString());
			res.setVehCc(list.get(0).getVehCc()==null?"":list.get(0).getVehCc().toString());
			res.setVehFueltype(list.get(0).getVehFueltype()==null?"":list.get(0).getVehFueltype().toString());
			res.setBaseRate(list.get(0).getBaserate()==null?"":list.get(0).getBaserate().toString());
			res.setNetRate(list.get(0).getNetrate()==null?"":list.get(0).getNetrate().toString());
			res.setStatus(list.get(0).getStatus()==null?"":list.get(0).getStatus());
			res.setRemarks(list.get(0).getRemarks()==null?"":list.get(0).getRemarks());	
			res.setCoreBodyId(list.get(0).getCoreBOdyId()==null?"":list.get(0).getCoreBOdyId());	
			res.setCodeDescLocal(list.get(0).getModelNameLocal()==null?"":list.get(0).getModelNameLocal());
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;

		}
		return res;
	}

	@Override
	public List<MotorMakeModelGetRes> getallMotorMakeModel(MotorMakeModelGetAllReq req) {
		List<MotorMakeModelGetRes> resList = new ArrayList<MotorMakeModelGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<MotorMakeModelMaster> list = new ArrayList<MotorMakeModelMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeModelMaster> query = cb.createQuery(MotorMakeModelMaster.class);

			// Find All
			Root<MotorMakeModelMaster> b = query.from(MotorMakeModelMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorMakeModelMaster> ocpm1 = amendId.from(MotorMakeModelMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("modelId"), b.get("modelId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("makeId"), b.get("makeId"));
			amendId.where(a1, a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
		//	Predicate n4 = cb.equal(b.get("branchCode"), "99999");
		//	Predicate n5 = cb.or(n3,n4);
			Predicate n6 = cb.equal(b.get("makeId"), req.getMakeId());
			query.where(n1,n2,n3,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<MotorMakeModelMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getModelId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(MotorMakeModelMaster :: getModelNameEn ));
			// Map
			for (MotorMakeModelMaster data : list) {
				MotorMakeModelGetRes res = new MotorMakeModelGetRes();
				res = mapper.map(data, MotorMakeModelGetRes.class);
				res.setCodeDescLocal(data.getModelNameLocal());
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
	
	public Integer getMasterTableCount(String companyId , String branchCode) {
		Integer data =0;
		try {
			List<MotorMakeModelMaster> list = new ArrayList<MotorMakeModelMaster>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeModelMaster> query = cb.createQuery(MotorMakeModelMaster.class);
		// Find all
			Root<MotorMakeModelMaster> b = query.from(MotorMakeModelMaster.class);
			//Select 
			query.select(b);

			//Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<MotorMakeModelMaster> ocpm1 = effectiveDate.from(MotorMakeModelMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(ocpm1.get("modelId"), b.get("modelId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			effectiveDate.where(a1,a2,a3);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("modelId")));
			
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"), companyId);
			Predicate n3 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3,n4);
			query.where(n1,n2,n5).orderBy(orderList);
			
			
			
			// Get Result
			TypedQuery<MotorMakeModelMaster> result = em.createQuery(query);
			int limit = 0 , offset = 1 ;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			data = list.size() > 0 ? list.get(0).getModelId() : 0 ;
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return data;
	}
	
	@Override
	public List<MotorMakeModelGetRes> getactiveMakeModel(MotorMakeModelGetAllReq req) {
		List<MotorMakeModelGetRes> resList = new ArrayList<MotorMakeModelGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<MotorMakeModelMaster> list = new ArrayList<MotorMakeModelMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeModelMaster> query = cb.createQuery(MotorMakeModelMaster.class);

			// Find All
			Root<MotorMakeModelMaster> b = query.from(MotorMakeModelMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorMakeModelMaster> ocpm1 = amendId.from(MotorMakeModelMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("modelId"), b.get("modelId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("makeId"), b.get("makeId"));
			amendId.where(a1, a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3,n4);
			Predicate n6 = cb.equal(b.get("makeId"),req.getMakeId());
			Predicate n7 = cb.equal(b.get("status"), "Y");
			query.where(n1,n2,n5,n6,n7).orderBy(orderList);
			
			// Get Result
			TypedQuery<MotorMakeModelMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getModelId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(MotorMakeModelMaster :: getModelNameEn ));
			// Map
			for (MotorMakeModelMaster data : list) {
				MotorMakeModelGetRes res = new MotorMakeModelGetRes();
				res = mapper.map(data, MotorMakeModelGetRes.class);
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
	public List<DropDownRes> getMotorMakeModelDropdown(MotorMakeModelGetAllReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);;
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeModelMaster> query=  cb.createQuery(MotorMakeModelMaster.class);
			List<MotorMakeModelMaster> list = new ArrayList<MotorMakeModelMaster>();
			// Find All
			Root<MotorMakeModelMaster> c = query.from(MotorMakeModelMaster.class);
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("modelNameEn")));
			
			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<MotorMakeModelMaster> ocpm1 = effectiveDate.from(MotorMakeModelMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("modelId"),ocpm1.get("modelId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("makeId"),ocpm1.get("makeId"));
			//Predicate a11 = cb.equal(c.get("bodyId"),ocpm1.get("bodyId"));
			Predicate a7 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a8 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			effectiveDate.where(a1,a2,a3,a7,a8);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<MotorMakeModelMaster> ocpm2 = effectiveDate2.from(MotorMakeModelMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a4 = cb.equal(c.get("modelId"),ocpm2.get("modelId"));
			Predicate a5 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a6 = cb.equal(c.get("makeId"),ocpm2.get("makeId"));
			Predicate a9 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a10 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			//Predicate a12 = cb.equal(c.get("bodyId"),ocpm2.get("bodyId"));
			effectiveDate2.where(a4,a5,a6,a9,a10);
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n9 = cb.equal(c.get("status"),"R");
			Predicate n10 = cb.or(n1,n9);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"),req.getInsuranceId());
			Predicate n5 = cb.equal(c.get("branchCode"),req.getBranchCode());
			Predicate n6 = cb.equal(c.get("branchCode"),"99999");
			Predicate n7 = cb.or(n5,n6);
			Predicate n8 = cb.equal(c.get("makeId"),req.getMakeId());
			Predicate n14 = cb.equal(c.get("makeId"),"99999");
			Predicate n15 = cb.or(n8,n14);
			/*if(StringUtils.isNotBlank(req.getBodyId()) ) {
				Predicate n11 = cb.equal(c.get("bodyId"),req.getBodyId() );
				Predicate n12 = cb.equal(c.get("bodyId"),"99999");
				Predicate n13 = cb.or(n11,n12);
				
				query.where(n10,n4,n7,n13,n15,n2,n3).orderBy(orderList);
				
			} else {*/
				query.where(n10,n4,n7,n15,n2,n3).orderBy(orderList);
		//	}
			
			// Get Result
			TypedQuery<MotorMakeModelMaster> result = em.createQuery(query);
			list = result.getResultList();
			
			List<DropDownRes> totalList = new ArrayList<DropDownRes>();
			for (MotorMakeModelMaster data : list) {
				// Response 
				DropDownRes res = new DropDownRes();
				res.setCode(data.getModelId().toString());
				res.setCodeDesc(data.getModelNameEn());
				res.setCodeDescLocal(data.getModelNameLocal());
				res.setStatus(data.getStatus());
				totalList.add(res);
			}
			List<DropDownRes> induvidualList = totalList.stream().filter( o -> !"99999".equalsIgnoreCase(o.getCode())  ).collect(Collectors.toList());
			induvidualList.sort(Comparator.comparing( DropDownRes :: getCodeDesc));
			resList.addAll(induvidualList);
			
			// Commercial
			List<DropDownRes> otherList = totalList.stream().filter( o -> "99999".equalsIgnoreCase(o.getCode())  ).collect(Collectors.toList());
			resList.addAll(otherList);
		}
			catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --->"+e.getMessage());
				return null;
				}
			return resList;
		}

		@Override
		public SuccessRes changeStatusOfMakeModel(MakeModelChangeStatusReq req) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			SuccessRes res = new SuccessRes();
			MotorMakeModelMaster saveData = new MotorMakeModelMaster();
			List<MotorMakeModelMaster> list = new ArrayList<MotorMakeModelMaster>();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				Integer amendId = 0;
				Date startDate = req.getEffectiveDateStart();
				String end = "31/12/2050";
				Date endDate = sdf.parse(end);
				long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
				Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLIS_IN_A_DAY);
				Date entryDate = null;
				String createdBy = "";

				Integer modelId = 0;

				// Update
				modelId = Integer.valueOf(req.getModelId());
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<MotorMakeModelMaster> query = cb.createQuery(MotorMakeModelMaster.class);
				// Find all
				Root<MotorMakeModelMaster> b = query.from(MotorMakeModelMaster.class);
				// Select
				query.select(b);
				//Orderby
				Subquery<Long> amendId2 = query.subquery(Long.class);
				Root<MotorMakeModelMaster> ocpm1 = amendId2.from(MotorMakeModelMaster.class);
				amendId2.select(cb.max(ocpm1.get("amendId")));
				Predicate a1 = cb.equal(ocpm1.get("modelId"), b.get("modelId"));
				Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
				Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
				amendId2.where(a1, a2,a3);
				//Orderby
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(b.get("branchCode")));
				//Where
				Predicate n2 = cb.equal(b.get("modelId"), req.getModelId());
				Predicate n3 = cb.equal(b.get("companyId"), req.getInsuranceId());
				Predicate n4 = cb.equal(b.get("branchCode"), req.getBranchCode());
				Predicate n5 = cb.equal(b.get("makeId"), req.getMakeId());
				Predicate n6 = cb.equal(b.get("bodyId"), req.getBodyId());

				query.where(n2, n3, n4, n5, n6).orderBy(orderList);

				// Get Result
				TypedQuery<MotorMakeModelMaster> result = em.createQuery(query);
				int limit = 0, offset = 2;
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				list = result.getResultList();

				if(req.getBranchCode().equalsIgnoreCase(list.get(0).getBranchCode() ) &&  list.size()>0) {
					Date beforeOneDay = new Date(new Date().getTime() - MILLIS_IN_A_DAY);

					if (list.get(0).getEffectiveDateStart().before(beforeOneDay)) {
						amendId = list.get(0).getAmendId() + 1;
						entryDate = new Date();
						createdBy = req.getCreatedBy();
						MotorMakeModelMaster lastRecord = list.get(0);
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);

					} else {
						amendId = list.get(0).getAmendId();
						entryDate = list.get(0).getEntryDate();
						createdBy = list.get(0).getCreatedBy();
						saveData = list.get(0);
						if(req.getBranchCode().equalsIgnoreCase(list.get(0).getBranchCode() ) &&  list.size()>1) {
							MotorMakeModelMaster lastRecord = list.get(1);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}

					}
				}
				res.setResponse("Updated Successfully");
				res.setSuccessId(modelId.toString());

				dozerMapper.map(list.get(0), saveData);
				saveData.setModelId(modelId);
				saveData.setMakeId(Integer.valueOf(req.getModelId()));
				saveData.setBodyId(Integer.valueOf(req.getBodyId()));
				saveData.setEffectiveDateStart(startDate);
				saveData.setEffectiveDateEnd(endDate);
				saveData.setCreatedBy(createdBy);
				saveData.setStatus(req.getStatus());
				saveData.setCompanyId(req.getInsuranceId());
				saveData.setBranchCode(req.getBranchCode());
				saveData.setEntryDate(entryDate);
				saveData.setUpdatedDate(new Date());
				saveData.setUpdatedBy(req.getCreatedBy());
				saveData.setAmendId(amendId);
				repo.saveAndFlush(saveData);
				log.info("Saved Details is --> " + json.toJson(saveData));
				// Perform Update
				res.setResponse("Status Changed");
				res.setSuccessId(req.getModelId());
			} catch (

			Exception e) {
				e.printStackTrace();
				log.info("Exception is --> " + e.getMessage());
				return null;
			}
			return res;
		}


}
