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

import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.PremiaConfigDataMaster;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.PremiaConfigDataMasterChangeStatusReq;
import com.maan.eway.master.req.PremiaConfigDataMasterGetReq;
import com.maan.eway.master.req.PremiaConfigDataMasterGetallReq;
import com.maan.eway.master.req.PremiaConfigDataMasterSaveReq;
import com.maan.eway.master.res.PremiaConfigDataMasterGetRes;
import com.maan.eway.master.res.PremiaConfigDataMasterGetallRes;
import com.maan.eway.master.res.PremiaConfigDataMasterListRes;
import com.maan.eway.master.service.PremiaConfigDataMasterService;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.PremiaConfigDataMasterRepository;
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
public class PremiaConfigDataMasterServiceImpl implements PremiaConfigDataMasterService {

	@PersistenceContext
	private EntityManager em;

	private Logger log = LogManager.getLogger(PremiaConfigDataMasterServiceImpl.class);

	@Autowired
	private PremiaConfigDataMasterRepository repo;

	@Autowired
	private ListItemValueRepository listrepo;

	@Override
	public List<Error> validatePremiaConfigData(PremiaConfigDataMasterSaveReq req) {
		List<Error> errorList = new ArrayList<Error>();

		try {
			
			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
			}
			
			if (StringUtils.isBlank(req.getBranchCode())) {
				errorList.add(new Error("02", "BranchCode", "Please Select BranchCode"));
			}
			if (StringUtils.isBlank(req.getProductId())) {
				errorList.add(new Error("03", "ProductId", "Please Select ProductId"));
			}
			if (StringUtils.isBlank(req.getSectionId())) {
				errorList.add(new Error("04", "SectionId", "Please Select SectionId"));
			}
			if (StringUtils.isBlank(req.getColumnId())) {
				errorList.add(new Error("14", "ColumnId", "Please Select Column"));
			}

			if (StringUtils.isBlank(req.getRemarks())) {
				errorList.add(new Error("15", "Remarks", "Please Enter Remarks"));
			}
			else if ((StringUtils.isNotBlank(req.getRemarks())) && req.getRemarks().length()>100) {
				errorList.add(new Error("15", "Remarks", "Please Enter Remarks within 100 Characters"));
			}
			if (StringUtils.isBlank(req.getDataTypeId())) {
				errorList.add(new Error("10", "DataType", "Please Select DataType"));				
			}
			else if ( StringUtils.isNotBlank(req.getDataTypeId())&& req.getDataTypeId().equalsIgnoreCase("3") &&  StringUtils.isBlank(req.getDateFormatType())) {
				errorList.add(new Error("11", "Date Format Type", "Please Enter Date Format Type"));				
			}
			// Date Validation 
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);;
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null || StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
				errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start"));

			} else if (req.getEffectiveDateStart().before(today)) {
				errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
			}
			//Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
				errorList.add(new Error("05", "Status", "Please Select Status  "));
			} else if (req.getStatus().length() > 1) {
				errorList.add(new Error("05", "Status", "Please Select Valid Status - One Character Only Allwed"));
			}else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
				errorList.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
			}
			if((StringUtils.isNotBlank(req.getDefaultYn())) && (req.getDefaultYn().equalsIgnoreCase("N") )){	
				if(StringUtils.isBlank(req.getInputColumn()) ) {
					errorList.add(new Error("14", "InputColumn", "Please Enter Input Column Name"));
				}
				}
			if (StringUtils.isBlank(req.getCreatedBy())) {
				errorList.add(new Error("07", "CreatedBy", "Please Select CreatedBy"));
			}else if (req.getCreatedBy().length() > 100){
				errorList.add(new Error("07","CreatedBy", "Please Enter CreatedBy within 100 Characters")); 
			}		
		
			if (StringUtils.isBlank(req.getDefaultYn())) {
				errorList.add(new Error("08", "DefaultYn", "Please Select DefaultYn"));
			}

			if(StringUtils.isNotBlank(req.getDefaultYn()) && req.getDefaultYn().equalsIgnoreCase("Y")) {
				if (StringUtils.isBlank(req.getDefaultValue())) {
				errorList.add(new Error("09", "DefaultValue", "Please Select DefaultValue"));				
			}
			
				if (StringUtils.isBlank(req.getDataTypeId())) {
					errorList.add(new Error("10", "DataType", "Please Select DataType"));				
				}
				else if ( StringUtils.isNotBlank(req.getDataTypeId())&& req.getDataTypeId().equalsIgnoreCase("3") &&  StringUtils.isBlank(req.getDateFormatType())) {
					errorList.add(new Error("11", "Date Format Type", "Please Enter Date Format Type"));				
				}
				
			}
			
			if (StringUtils.isBlank(req.getCaseConditionYn())) {
				errorList.add(new Error("12", "CaseConditionYn", "Please Select CaseConditionYn"));
			}

			if(StringUtils.isNotBlank(req.getCaseConditionYn()) && req.getCaseConditionYn().equalsIgnoreCase("Y")) {
				if (StringUtils.isBlank(req.getCaseCondition())) {
				errorList.add(new Error("13", "CaseCondition", "Please Enter CaseCondition"));				
			}
				
			
				
						}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}



	@Override
	public SuccessRes insertPremiaConfigData(PremiaConfigDataMasterSaveReq req) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		PremiaConfigDataMaster saveData = new PremiaConfigDataMaster();
		List<PremiaConfigDataMaster> list = new ArrayList<PremiaConfigDataMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Integer amendId = 0;
			Date StartDate = req.getEffectiveDateStart();
			String end = "31/12/2050";
			Date endDate = sdf.parse(end);
			long MILLS_IN_A_DAY = 1000 * 60 * 60 * 24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLS_IN_A_DAY);
			Date entryDate = null;
			String createdBy = "";
			String columnId = "";

			if (StringUtils.isBlank(req.getColumnId())) {
				Integer totalCount = getMasterTableCount(req.getPremiaId(), req.getCompanyId(), req.getBranchCode(),
						req.getProductId(), req.getSectionId());
				Integer column = totalCount + 1;
				columnId = column.toString();
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully");
				res.setSuccessId(column.toString());
			} else {
				columnId = req.getColumnId();
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<PremiaConfigDataMaster> query = cb.createQuery(PremiaConfigDataMaster.class);
				// Findall
				Root<PremiaConfigDataMaster> b = query.from(PremiaConfigDataMaster.class);
				// select
				query.select(b);
				// Orderby
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("effectiveDateStart")));
				// Where
				Predicate n1 = cb.equal(b.get("premiaId"), req.getPremiaId());
				Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
				Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
				Predicate n4 = cb.equal(b.get("productId"), req.getProductId());
				Predicate n5 = cb.equal(b.get("sectionId"), req.getSectionId());
				Predicate n6 = cb.equal(b.get("columnId"), req.getColumnId());

				query.where(n1, n2, n3, n4, n5, n6).orderBy(orderList);

				// Get Result
				TypedQuery<PremiaConfigDataMaster> result = em.createQuery(query);
				int limit = 0, offset = 2;
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				list = result.getResultList();
				if (list.size() > 0) {
					Date beforeOneDay = new Date(new Date().getTime() - MILLS_IN_A_DAY);
					if (list.get(0).getEffectiveDateStart().before(beforeOneDay)) {
						amendId = list.get(0).getAmendId() + 1;
						entryDate = new Date();
						createdBy = req.getCreatedBy();
						PremiaConfigDataMaster lastRecord = list.get(0);
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					} else {
						amendId = list.get(0).getAmendId();
						entryDate = list.get(0).getEntryDate();
						createdBy = list.get(0).getCreatedBy();
						saveData = list.get(0);
						if (list.size() > 1) {
							PremiaConfigDataMaster lastRecord = list.get(1);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}
					}
				}
				res.setResponse("Updated Successfully");
				res.setSuccessId(columnId.toString());
			}
			
			if(StringUtils.isNotBlank(req.getDataTypeId())) {
			ListItemValue datatype = listrepo.findByItemTypeAndItemCodeAndCompanyId("DATA_TYPE", req.getDataTypeId(),req.getCompanyId() );
			}
			dozerMapper.map(req, saveData);

			saveData.setPremiaId(Integer.valueOf(req.getPremiaId()));
			saveData.setEffectiveDateStart(StartDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setEntryDate(entryDate);
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setAmendId(amendId);
			saveData.setBranchCode(req.getBranchCode());
			saveData.setProductId(req.getProductId());
			saveData.setSectionId(req.getSectionId()==null?"99999":req.getSectionId());
			saveData.setColumnName(req.getColumnName());
			saveData.setDefaultYn(req.getDefaultYn());
			saveData.setDefaultValue(req.getDefaultValue());
			saveData.setInputColumn(req.getInputColumn());
			saveData.setDataFormatType(req.getDateFormatType()==null?null:req.getDateFormatType());
			saveData.setCaseConditionYn(req.getCaseConditionYn());
			saveData.setCaseCondition(req.getCaseCondition()==null?null:req.getCaseCondition());
			saveData.setColumnId(Integer.valueOf(columnId));
			saveData.setDataTypeId(req.getDataTypeId()==null?null:req.getDataTypeId());
			if(StringUtils.isNotBlank(req.getDataTypeId())) {
				ListItemValue datatype = listrepo.findByItemTypeAndItemCodeAndCompanyId("DATA_TYPE", req.getDataTypeId(),req.getCompanyId() );
				saveData.setDataTypeDesc(datatype.getItemValue()==null?null:datatype.getItemValue());
				
			}
		
			repo.saveAndFlush(saveData);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
	}

	public Integer getMasterTableCount(String premiaId, String companyId, String branchCode, String productId,
			String sectionId) {

		Integer data = 0;
		try {
			List<PremiaConfigDataMaster> list = new ArrayList<PremiaConfigDataMaster>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PremiaConfigDataMaster> query = cb.createQuery(PremiaConfigDataMaster.class);
			// Find all
			Root<PremiaConfigDataMaster> b = query.from(PremiaConfigDataMaster.class);
			// Select
			query.select(b);
			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<PremiaConfigDataMaster> ocpm1 = effectiveDate.from(PremiaConfigDataMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(ocpm1.get("premiaId"), b.get("premiaId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a5 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
			Predicate a6 = cb.equal(ocpm1.get("columnId"), b.get("columnId"));

			effectiveDate.where(a1, a2, a3, a4, a5, a6);

			// OrderBy
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("columnId")));

			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"), companyId);
			Predicate n3 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n4 = cb.equal(b.get("productId"), productId);
			Predicate n5 = cb.equal(b.get("sectionId"), sectionId);
			Predicate n6 = cb.equal(b.get("premiaId"), premiaId);
			Predicate n7 = cb.equal(b.get("sectionId"), sectionId);
			Predicate n8 = cb.equal(b.get("sectionId"),"99999");
			Predicate n9 = cb.or(n7,n8);
			query.where(n1, n2, n3, n4, n5, n6,n9).orderBy(orderList);

			// Get Result
			TypedQuery<PremiaConfigDataMaster> result = em.createQuery(query);
			int limit = 0, offset = 1;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			data = list.size() > 0 ? Integer.valueOf(list.get(0).getColumnId()) : 0;
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return data;
	}



	@Override
	public PremiaConfigDataMasterGetRes getPremiaConfigData(PremiaConfigDataMasterGetReq req) {
		PremiaConfigDataMasterGetRes res = new PremiaConfigDataMasterGetRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<PremiaConfigDataMaster> list = new ArrayList<PremiaConfigDataMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PremiaConfigDataMaster> query = cb.createQuery(PremiaConfigDataMaster.class);

			// Find All
			Root<PremiaConfigDataMaster> b = query.from(PremiaConfigDataMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<PremiaConfigDataMaster> ocpm1 = amendId.from(PremiaConfigDataMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("premiaId"), b.get("premiaId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
			Predicate a5 = cb.equal(ocpm1.get("sectionId"),b.get("sectionId"));
			Predicate a6 = cb.equal(ocpm1.get("columnId"),b.get("columnId"));

			amendId.where(a1, a2,a3,a4,a5,a6);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("columnId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("premiaId"), req.getPremiaId());
			Predicate n5 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n6 = cb.equal(b.get("sectionId"), req.getSectionId());
			Predicate n7 = cb.equal(b.get("columnId"), req.getColumnId());
			Predicate n8 = cb.equal(b.get("sectionId"),"99999");
			Predicate n9 = cb.or(n6,n8);
						
			query.where(n1,n2,n3,n4,n5,n9,n7).orderBy(orderList);
			
			// Get Result
			TypedQuery<PremiaConfigDataMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getColumnId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(PremiaConfigDataMaster :: getColumnId ));
			if(list.size()>0 && list!=null) {
			res.setPremiaId(list.get(0).getPremiaId().toString());
			res.setEntryDate(list.get(0).getEntryDate());
			res.setEffectiveDateStart(list.get(0).getEffectiveDateStart());
			res.setEffectiveDateEnd(list.get(0).getEffectiveDateEnd());
			res.setCompanyId(list.get(0).getCompanyId());
			res.setBranchCode(list.get(0).getBranchCode());
			res.setProductId(list.get(0).getProductId());
			res.setSectionId(list.get(0).getSectionId()==null?"":list.get(0).getSectionId());
			res.setStatus(list.get(0).getStatus());		
			res.setAmendId(list.get(0).getAmendId().toString());
			res.setCreatedBy(list.get(0).getCreatedBy());
			res.setUpdatedBy(list.get(0).getUpdatedBy());
			res.setUpdatedDate(list.get(0).getUpdatedDate());
			res.setRemarks(list.get(0).getRemarks());;
			res.setColumnId(list.get(0).getColumnId().toString());
			res.setColumnName(list.get(0).getColumnName());
			res.setDefaultYn(list.get(0).getDefaultYn()==null?"":list.get(0).getDefaultYn());
			res.setDefaultValue(list.get(0).getDefaultValue()==null?"":list.get(0).getDefaultValue());
			res.setCaseConditionYn(list.get(0).getCaseConditionYn()==null?"":list.get(0).getCaseConditionYn());
			res.setCaseCondition(list.get(0).getCaseCondition()==null?"":list.get(0).getCaseCondition());
			res.setInputColumn(	list.get(0).getInputColumn()==null?"":list.get(0).getInputColumn());
			
			res.setDataTypeId(list.get(0).getDataTypeId()==null?"":list.get(0).getDataTypeId());
			res.setDataTypeDesc(list.get(0).getDataTypeDesc()==null?"":list.get(0).getDataTypeDesc());
			res.setDateFormatType(list.get(0).getDataFormatType()==null?"":list.get(0).getDataFormatType());
			
			}
		}catch (Exception e) {
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
	public PremiaConfigDataMasterGetallRes getallPremiaConfigData(PremiaConfigDataMasterGetallReq req) {
		PremiaConfigDataMasterGetallRes res1 = new PremiaConfigDataMasterGetallRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<PremiaConfigDataMaster> list = new ArrayList<PremiaConfigDataMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PremiaConfigDataMaster> query = cb.createQuery(PremiaConfigDataMaster.class);

			// Find All
			Root<PremiaConfigDataMaster> b = query.from(PremiaConfigDataMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<PremiaConfigDataMaster> ocpm1 = amendId.from(PremiaConfigDataMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("premiaId"), b.get("premiaId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
			Predicate a5 = cb.equal(ocpm1.get("sectionId"),b.get("sectionId"));
			Predicate a6 = cb.equal(ocpm1.get("columnId"),b.get("columnId"));

			amendId.where(a1, a2,a3,a4,a5,a6);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("columnId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("premiaId"), req.getPremiaId());
			Predicate n5 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n6 = cb.equal(b.get("sectionId"), req.getSectionId());
			Predicate n7 = cb.equal(b.get("sectionId"),"99999");
			Predicate n8 = cb.or(n6,n7);
								
			query.where(n1,n2,n3,n4,n5,n8).orderBy(orderList);
			
			// Get Result
			TypedQuery<PremiaConfigDataMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getColumnId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(PremiaConfigDataMaster :: getColumnId ));
			if(list.size()>0 && list!=null) {
			res1.setPremiaId(list.get(0).getPremiaId().toString());
			res1.setCompanyId(list.get(0).getCompanyId());
			res1.setBranchCode(list.get(0).getBranchCode());
			res1.setProductId(list.get(0).getProductId());
			res1.setSectionId(list.get(0).getSectionId()==null?"": list.get(0).getSectionId());
			res1.setCreatedBy(list.get(0).getCreatedBy());
			
			List<PremiaConfigDataMasterListRes> resList = new ArrayList<PremiaConfigDataMasterListRes>();	
			for(PremiaConfigDataMaster data : list) {	
				PremiaConfigDataMasterListRes res = new PremiaConfigDataMasterListRes();
			res.setEntryDate(data.getEntryDate());
			res.setEffectiveDateStart(data.getEffectiveDateStart());
			res.setEffectiveDateEnd(data.getEffectiveDateEnd());
			res.setStatus(data.getStatus());		
			res.setAmendId(data.getAmendId().toString());
			res.setUpdatedBy(data.getUpdatedBy());
			res.setUpdatedDate(data.getUpdatedDate());
			res.setRemarks(data.getRemarks());;
			res.setColumnId(data.getColumnId().toString());
			res.setColumnName(data.getColumnName());
			res.setDefaultYn(data.getDefaultYn()==null?"":data.getDefaultYn());
			res.setDefaultValue(data.getDefaultValue()==null?"":data.getDefaultValue());
			res.setCaseConditionYn(data.getCaseConditionYn()==null?"":data.getCaseConditionYn());
			res.setCaseCondition(data.getCaseCondition()==null?"":data.getCaseCondition());
			res.setInputColumn(data.getInputColumn()==null?"":data.getInputColumn());
			res.setDataTypeId(data.getDataTypeId()==null?"":data.getDataTypeId());
			res.setDataTypeDesc(data.getDataTypeDesc()==null?"":data.getDataTypeDesc());
			res.setDateFormatType(data.getDataFormatType()==null?"":data.getDataFormatType());
			resList.add(res);
			
			}
			res1.setColumnList(resList);;
			
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res1;
	}



	@Override
	public PremiaConfigDataMasterGetallRes getactivePremiaConfigData(PremiaConfigDataMasterGetallReq req) {
		PremiaConfigDataMasterGetallRes res1 = new PremiaConfigDataMasterGetallRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<PremiaConfigDataMaster> list = new ArrayList<PremiaConfigDataMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PremiaConfigDataMaster> query = cb.createQuery(PremiaConfigDataMaster.class);

			// Find All
			Root<PremiaConfigDataMaster> b = query.from(PremiaConfigDataMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<PremiaConfigDataMaster> ocpm1 = amendId.from(PremiaConfigDataMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("premiaId"), b.get("premiaId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
			Predicate a5 = cb.equal(ocpm1.get("sectionId"),b.get("sectionId"));

			amendId.where(a1, a2,a3,a4,a5);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("premiaId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("premiaId"), req.getPremiaId());
			Predicate n5 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n6 = cb.equal(b.get("sectionId"), req.getSectionId());
			Predicate n7 = cb.equal(b.get("status"), "Y");
			Predicate n8 = cb.equal(b.get("sectionId"),"99999");
			Predicate n9 = cb.or(n6,n8);
		
			query.where(n1,n2,n3,n4,n5,n9,n7).orderBy(orderList);
			
			// Get Result
			TypedQuery<PremiaConfigDataMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getColumnId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(PremiaConfigDataMaster :: getColumnId ));
			if(list.size()>0 && list!=null) {
			res1.setPremiaId(list.get(0).getPremiaId().toString());
			res1.setCompanyId(list.get(0).getCompanyId());
			res1.setBranchCode(list.get(0).getBranchCode());
			res1.setProductId(list.get(0).getProductId());
			res1.setSectionId(list.get(0).getSectionId()==null?"":list.get(0).getSectionId());
			res1.setCreatedBy(list.get(0).getCreatedBy());
			
			List<PremiaConfigDataMasterListRes> resList = new ArrayList<PremiaConfigDataMasterListRes>();	
			for(PremiaConfigDataMaster data : list) {	
				PremiaConfigDataMasterListRes res = new PremiaConfigDataMasterListRes();
			res.setEntryDate(data.getEntryDate());
			res.setEffectiveDateStart(data.getEffectiveDateStart());
			res.setEffectiveDateEnd(data.getEffectiveDateEnd());
			res.setStatus(data.getStatus());		
			res.setAmendId(data.getAmendId().toString());
			res.setUpdatedBy(data.getUpdatedBy());
			res.setUpdatedDate(data.getUpdatedDate());
			res.setRemarks(data.getRemarks());;
			res.setColumnId(data.getColumnId().toString());
			res.setColumnName(data.getColumnName());
			res.setDefaultYn(data.getDefaultYn()==null?"":data.getDefaultYn());
			res.setDefaultValue(data.getDefaultValue()==null?"":data.getDefaultValue());
			res.setCaseConditionYn(data.getCaseConditionYn()==null?"":data.getCaseConditionYn());
			res.setCaseCondition(data.getCaseCondition()==null?"":data.getCaseCondition());
			res.setInputColumn(data.getInputColumn()==null?"":data.getInputColumn());
			res.setDataTypeId(data.getDataTypeId()==null?"":data.getDataTypeId());
			res.setDataTypeDesc(data.getDataTypeDesc()==null?"":data.getDataTypeDesc());
			res.setDateFormatType(data.getDataFormatType()==null?"":data.getDataFormatType());
			resList.add(res);
			
			}
			res1.setColumnList(resList);;
			
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res1;
	}



	@Override
	public List<DropDownRes> getPremiaConfigDataMasterDropdown(PremiaConfigDataMasterGetallReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PremiaConfigDataMaster> query=  cb.createQuery(PremiaConfigDataMaster.class);
			List<PremiaConfigDataMaster> list = new ArrayList<PremiaConfigDataMaster>();
			// Find All
			Root<PremiaConfigDataMaster> b = query.from(PremiaConfigDataMaster.class);
			//Select
			query.select(b);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("premiaId")));
			
			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<PremiaConfigDataMaster> ocpm1 = effectiveDate.from(PremiaConfigDataMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(b.get("premiaId"),ocpm1.get("premiaId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a4 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			Predicate a5 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a6 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));

			effectiveDate.where(a1,a2,a3,a4,a5,a6);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<PremiaConfigDataMaster> ocpm2 = effectiveDate2.from(PremiaConfigDataMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a7 = cb.equal(b.get("premiaId"),ocpm2.get("premiaId"));
			Predicate a8 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a9 = cb.equal(ocpm2.get("companyId"), b.get("companyId"));
			Predicate a10 = cb.equal(ocpm2.get("branchCode"),b.get("branchCode"));
			Predicate a11 = cb.equal(ocpm2.get("productId"), b.get("productId"));
			Predicate a12 = cb.equal(ocpm2.get("sectionId"), b.get("sectionId"));

			effectiveDate2.where(a7,a8,a9,a10,a11,a12);
			// Where
			Predicate n1 = cb.equal(b.get("status"),"Y");
			Predicate n11 = cb.equal(b.get("status"),"R");
			Predicate n12 = cb.or(n1,n11);
			Predicate n2 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(b.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(b.get("companyId"),req.getCompanyId());
			Predicate n5 = cb.equal(b.get("branchCode"),req.getBranchCode());
			Predicate n6 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n7 = cb.equal(b.get("sectionId"),req.getSectionId());
			Predicate n8 = cb.equal(b.get("sectionId"),"99999");
			Predicate n9 = cb.or(n7,n8);
		
			query.where(n12,n2,n3,n4,n5,n6,n9).orderBy(orderList);
			// Get Result
			TypedQuery<PremiaConfigDataMaster> result = em.createQuery(query);
			list = result.getResultList();
			for (PremiaConfigDataMaster data : list) {
				// Response 
				DropDownRes res = new DropDownRes();
				res.setCode(data.getColumnId().toString());
				res.setCodeDesc(data.getColumnName());
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



	@Override
	public SuccessRes changeStatusPremiaConfigData(PremiaConfigDataMasterChangeStatusReq req) {
		SuccessRes res = new SuccessRes();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		List<PremiaConfigDataMaster> list = new ArrayList<PremiaConfigDataMaster>(); 
		PremiaConfigDataMaster saveData = new PremiaConfigDataMaster(); 
		try {
			Integer amendId = 0;
			Date StartDate = req.getEffectiveDateStart();
			String end = "31/12/2050";
			Date endDate = sdf.parse(end);
			long MILLS_IN_A_DAY = 1000*60*60*24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime()- MILLS_IN_A_DAY);
			Date entryDate = null;
			String createdBy ="";
			String columnId = "";
			
			columnId = req.getColumnId();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PremiaConfigDataMaster> query = cb.createQuery(PremiaConfigDataMaster.class);
			//Findall
			Root<PremiaConfigDataMaster> b = query.from(PremiaConfigDataMaster.class);
			//select
			query.select(b);
			//Orderby
			Subquery<Long> amendId2 = query.subquery(Long.class);
			Root<PremiaConfigDataMaster> ocpm1 = amendId2.from(PremiaConfigDataMaster.class);
			amendId2.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("columnId"), b.get("columnId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
			Predicate a5 = cb.equal(ocpm1.get("sectionId"),b.get("sectionId"));
			Predicate a6 = cb.equal(ocpm1.get("premiaId"),b.get("premiaId"));

			amendId2.where(a1, a2,a3,a4,a5,a6);
			//Orderby
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("columnId")));
			//Where
			Predicate n1 = cb.equal(b.get("columnId"),req.getColumnId());
			Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"),req.getBranchCode());
			Predicate n4 = cb.equal(b.get("amendId"),amendId2);
			Predicate n5 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n6 = cb.equal(b.get("sectionId"),req.getSectionId());
			Predicate n7 = cb.equal(b.get("premiaId"),req.getPremiaId());
			Predicate n8 = cb.equal(b.get("sectionId"),"99999");
			Predicate n9 = cb.or(n6,n8);
		
			query.where(n1,n2,n3,n4,n5,n9,n7).orderBy(orderList);
			
			// Get Result
			TypedQuery<PremiaConfigDataMaster> result = em.createQuery(query);
			int limit=0, offset=2;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			if (list.size() > 0) {

				Date beforeOneDay = new Date(new Date().getTime()- MILLS_IN_A_DAY);
				if(list.get(0).getEffectiveDateStart().before(beforeOneDay)) {
					amendId = list.get(0).getAmendId()+1;
					entryDate = new Date();
					createdBy = req.getCreatedBy();
					PremiaConfigDataMaster lastRecord = list.get(0);
					lastRecord.setEffectiveDateEnd(oldEndDate);
					repo.saveAndFlush(lastRecord);
				}
				else  {
					amendId = list.get(0).getAmendId();
					entryDate = list.get(0).getEntryDate();
					createdBy = list.get(0).getCreatedBy();
					saveData = list.get(0);
					if (list.size()>1 ) {
					PremiaConfigDataMaster lastRecord = list.get(1);	
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					}
				}
			}
			res.setResponse("Updated Successfully");
			res.setSuccessId(columnId.toString());
				
			dozerMapper.map(list.get(0), saveData);
			saveData.setEffectiveDateStart(StartDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setEntryDate(entryDate);
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setAmendId(amendId);
			saveData.setStatus(req.getStatus());
			saveData.setBranchCode(req.getBranchCode());
			repo.saveAndFlush(saveData);	
			// Perform Update
			res.setResponse("Status Changed");
			res.setSuccessId(req.getColumnId());
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
			}
		return res;
	}


}
