package com.maan.eway.master.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.CountryMasters;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.MotorBodyTypeMaster;
import com.maan.eway.bean.MotorColorMaster;
import com.maan.eway.bean.MotorMakeMaster;
import com.maan.eway.bean.MotorMakeModelMaster;
import com.maan.eway.bean.MotorVehicleUsageMaster;
import com.maan.eway.bean.OccupationMaster;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.RegionMaster;
import com.maan.eway.bean.StateMaster;
import com.maan.eway.master.req.GetMasterTableIdsReq;
import com.maan.eway.master.req.OriginatingCountryDropdownReq;
import com.maan.eway.master.res.CountryMasterRes;
import com.maan.eway.master.service.MasterTablesDescAndIdService;
import com.maan.eway.res.SuccessRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class MasterTablesDescAndIdServiceImpl implements MasterTablesDescAndIdService{
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public SuccessRes getIdsfromMastersTable(GetMasterTableIdsReq req) {

		SuccessRes res = new SuccessRes();

		if(req.getMasterType().equalsIgnoreCase("CUSTOMER_TITLE")) {
			List<ListItemValue> list = new ArrayList<ListItemValue>();
			try {
			
				// Criteria
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
				// Find All
				Root<ListItemValue> c = query.from(ListItemValue.class);
				
				// Select
				query.select(c);

				// Where
				Predicate n1 = cb.equal(c.get("status"), "Y");
				Predicate n2 = cb.equal(c.get("companyId"), req.getCompanyId());
				Predicate n3 = cb.equal(c.get("itemValue"),req.getDesc());

				query.where(n1, n2, n3);

				TypedQuery<ListItemValue> result = em.createQuery(query);
				list = result.getResultList();
				res.setResponse(list.get(0).getItemCode());

			}catch(Exception e) {
				e.printStackTrace();
			}
			return res;
			
		}else if(req.getMasterType().equalsIgnoreCase("CUSTOMER_GENDER")) {
			List<ListItemValue> list = new ArrayList<ListItemValue>();
			try {
			
				// Criteria
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
				// Find All
				Root<ListItemValue> c = query.from(ListItemValue.class);
				
				// Select
				query.select(c);

				// Where
				Predicate n1 = cb.equal(c.get("status"), "Y");
				Predicate n2 = cb.equal(c.get("companyId"), req.getCompanyId());
				Predicate n3 = cb.equal(c.get("itemValue"),req.getDesc());

				query.where(n1, n2, n3);

				TypedQuery<ListItemValue> result = em.createQuery(query);
				list = result.getResultList();
				res.setResponse(list.get(0).getItemCode());

			}catch(Exception e) {
				e.printStackTrace();
			}
			return res;	
		}else if(req.getMasterType().equalsIgnoreCase("CUSTOMER_OCCUPATION")) {
			List<OccupationMaster> list = new ArrayList<>();
			try {
				
				// Criteria
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<OccupationMaster> query = cb.createQuery(OccupationMaster.class);
				// Find All
				Root<OccupationMaster> c = query.from(OccupationMaster.class);
				
				// Select
				query.select(c);

				// Where
				Predicate n1 = cb.equal(c.get("status"), "Y");
				Predicate n2 = cb.equal(c.get("companyId"), req.getCompanyId());
				Predicate n3 = cb.equal(c.get("occupationName"),req.getDesc());
				Predicate n4 = cb.equal(c.get("occupationType"), "I");

				query.where(n1, n2, n3, n4);

				TypedQuery<OccupationMaster> result = em.createQuery(query);
				list = result.getResultList();
				res.setResponse(list.get(0).getOccupationId().toString());

			}catch(Exception e) {
				e.printStackTrace();
			}
			return res;	
			
		}else if(req.getMasterType().equalsIgnoreCase("CUSTOMER_IDTYPE")) {
			List<ListItemValue> list = new ArrayList<ListItemValue>();
			try {
			
				// Criteria
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
				// Find All
				Root<ListItemValue> c = query.from(ListItemValue.class);
				
				// Select
				query.select(c);

				// Where
				Predicate n1 = cb.equal(c.get("status"), "Y");
				Predicate n2 = cb.equal(c.get("companyId"), req.getCompanyId());
				Predicate n3 = cb.equal(c.get("itemValue"),req.getDesc());
				Predicate n4 = cb.equal(c.get("itemType"),"POLICY_HOLDER_ID_TYPE");

				query.where(n1, n2, n3, n4);

				TypedQuery<ListItemValue> result = em.createQuery(query);
				list = result.getResultList();
				res.setResponse(list.get(0).getItemCode());

			}catch(Exception e) {
				e.printStackTrace();
			}
			return res;	
		}else if(req.getMasterType().equalsIgnoreCase("CUSTOMER_REGION")) {
			List<RegionMaster> list = new ArrayList<RegionMaster>();
			try {
				
				// Criteria
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<RegionMaster> query = cb.createQuery(RegionMaster.class);
				// Find All
				Root<RegionMaster> c = query.from(RegionMaster.class);
				
				// Select
				query.select(c);

				// Where
				Predicate n1 = cb.equal(c.get("status"), "Y");
				Predicate n2 = cb.equal(c.get("countryId"), req.getCountryCode());
				Predicate n3 = cb.equal(c.get("regionName"),req.getDesc());
				

				query.where(n1, n2, n3);

				TypedQuery<RegionMaster> result = em.createQuery(query);
				list = result.getResultList();
				res.setResponse(list.get(0).getRegionCode());

			}catch(Exception e) {
				e.printStackTrace();
			}
			return res;	
			
		}else if(req.getMasterType().equalsIgnoreCase("CUSTOMER_DISTRICT")) {
			List<StateMaster> list = new ArrayList<StateMaster>();
		try {
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<StateMaster> query = cb.createQuery(StateMaster.class);
			// Find All
			Root<StateMaster> c = query.from(StateMaster.class);
			
			// Select
			query.select(c);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("countryId"), req.getCountryCode());
			Predicate n3 = cb.equal(c.get("stateName"),req.getDesc());
			

			query.where(n1, n2, n3);

			TypedQuery<StateMaster> result = em.createQuery(query);
			list = result.getResultList();
			res.setResponse(list.get(0).getStateId().toString());

		}catch(Exception e) {
			e.printStackTrace();
		}
		return res;	
			
		}else if(req.getMasterType().equalsIgnoreCase("MOTOR_USAGE")) {
			List<MotorVehicleUsageMaster> list = new ArrayList<MotorVehicleUsageMaster>();
		try {
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorVehicleUsageMaster> query = cb.createQuery(MotorVehicleUsageMaster.class);
			// Find All
			Root<MotorVehicleUsageMaster> c = query.from(MotorVehicleUsageMaster.class);
			
			// Select
			query.select(c);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(c.get("vehicleUsageDesc"),req.getDesc());
			

			query.where(n1, n2, n3);

			TypedQuery<MotorVehicleUsageMaster> result = em.createQuery(query);
			list = result.getResultList();
			res.setResponse(list.get(0).getVehicleUsageId().toString());

		}catch(Exception e) {
			e.printStackTrace();
		}
		return res;	
			
		}else if(req.getMasterType().equalsIgnoreCase("BODY_TYPE")) {
			List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();
		try {
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);
			// Find All
			Root<MotorBodyTypeMaster> c = query.from(MotorBodyTypeMaster.class);
			
			// Select
			query.select(c);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(c.get("bodyNameEn"),req.getDesc());
			

			query.where(n1, n2, n3);

			TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
			list = result.getResultList();
			res.setResponse(list.get(0).getBodyId().toString());

		}catch(Exception e) {
			e.printStackTrace();
		}
		return res;	
			
		}else if(req.getMasterType().equalsIgnoreCase("VEHICLE_MAKE")) {
			List<MotorMakeMaster> list = new ArrayList<MotorMakeMaster>();
		try {
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeMaster> query = cb.createQuery(MotorMakeMaster.class);
			// Find All
			Root<MotorMakeMaster> c = query.from(MotorMakeMaster.class);
			
			// Select
			query.select(c);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(c.get("makeNameEn"),req.getDesc());
			

			query.where(n1, n2, n3);

			TypedQuery<MotorMakeMaster> result = em.createQuery(query);
			list = result.getResultList();
			res.setResponse(list.get(0).getMakeId().toString());

		}catch(Exception e) {
			e.printStackTrace();
		}
		return res;	
			
		}else if(req.getMasterType().equalsIgnoreCase("VEHICLE_MODEL")) {
			List<MotorMakeModelMaster> list = new ArrayList<MotorMakeModelMaster>();
		try {
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeModelMaster> query = cb.createQuery(MotorMakeModelMaster.class);
			// Find All
			Root<MotorMakeModelMaster> c = query.from(MotorMakeModelMaster.class);
			
			// Select
			query.select(c);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(c.get("modelNameEn"),req.getDesc());
			//Predicate n4 = cb.equal(c.get("makeId"), Integer.parseInt(req.getMakeId()));
			

			query.where(n1, n2, n3);

			TypedQuery<MotorMakeModelMaster> result = em.createQuery(query);
			list = result.getResultList();
			res.setResponse(list.get(0).getModelId().toString());

		}catch(Exception e) {
			e.printStackTrace();
		}
		return res;	
			
		}else if(req.getMasterType().equalsIgnoreCase("VEHICLE_COLOR")) {
			List<MotorColorMaster> list = new ArrayList<MotorColorMaster>();
		try {
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorColorMaster> query = cb.createQuery(MotorColorMaster.class);
			// Find All
			Root<MotorColorMaster> c = query.from(MotorColorMaster.class);
			
			// Select
			query.select(c);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(c.get("colorDesc"),req.getDesc());
			//Predicate n4 = cb.equal(c.get("makeId"), Integer.parseInt(req.getMakeId()));
			

			query.where(n1, n2, n3);

			TypedQuery<MotorColorMaster> result = em.createQuery(query);
			list = result.getResultList();
			res.setResponse(list.get(0).getColorId().toString());

		}catch(Exception e) {
			e.printStackTrace();
		}
		return res;	
		}else if(req.getMasterType().equalsIgnoreCase("INSURANCE_CLASS")) {
			List<ProductSectionMaster> list = new ArrayList<ProductSectionMaster>();
		try {
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProductSectionMaster> query = cb.createQuery(ProductSectionMaster.class);
			// Find All
			Root<ProductSectionMaster> c = query.from(ProductSectionMaster.class);
			
			// Select
			query.select(c);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(c.get("sectionName"),req.getDesc());
			Predicate n4 = cb.equal(c.get("productId"), Integer.parseInt("5"));
			

			query.where(n1, n2, n3, n4);

			TypedQuery<ProductSectionMaster> result = em.createQuery(query);
			list = result.getResultList();
			res.setResponse(list.get(0).getSectionId().toString());

		}catch(Exception e) {
			e.printStackTrace();
		}
		return res;	
		}
		return null;
	}

	@Override
	public List<CountryMasterRes> getOriginatingCountryDropdown(OriginatingCountryDropdownReq req) {
		List<CountryMasterRes> resList = new ArrayList<CountryMasterRes>();
		List<CountryMasters> list = new ArrayList<CountryMasters>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CountryMasters> query = cb.createQuery(CountryMasters.class);
			Root<CountryMasters> c = query.from(CountryMasters.class);
			query.select(c);
			
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("status"), "R");
			Predicate n3 = cb.equal(c.get("countrypk").get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.or(n1,n3);
			
			query.where(n3, n4);
			
			TypedQuery<CountryMasters> result = em.createQuery(query);
			list = result.getResultList();
			
			for (CountryMasters data : list) {
				CountryMasterRes res = new CountryMasterRes();

				res = dozerMapper.map(data, CountryMasterRes.class);
				res.setCountryId(data.getCountrypk().getCountryid().toString());
				res.setNationality(data.getCountryname()==null?"":data.getCountryname());
				resList.add(res);
			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return resList;
	}

}
