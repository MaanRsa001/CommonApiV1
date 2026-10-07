package com.maan.eway.oman;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;


import com.maan.eway.master.req.StateMasterDropDownReq;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class OmanPostalCodeMasterServiceImpl implements OmanPostalCodeMasterService{
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public List<OmanDropDownRes> getOmanPostalCodeMasterDropdown(StateMasterDropDownReq req) {
		List<OmanDropDownRes> resList = new ArrayList<OmanDropDownRes>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();

			String countryId=null;
		
			if (StringUtils.isBlank(req.getCountryId())) {
				countryId="TZA";
			}else {
				countryId=req.getCountryId();
			}
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<OmanPostalCodeMaster> query = cb.createQuery(OmanPostalCodeMaster.class);
			List<OmanPostalCodeMaster> list = new ArrayList<OmanPostalCodeMaster>();

			// Find All
			Root<OmanPostalCodeMaster> c = query.from(OmanPostalCodeMaster.class);

			// Select
			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("location")));

			
			
			
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n11 = cb.equal(c.get("status"),"R");
			Predicate n12 = cb.or(n1,n11);
			jakarta.persistence.criteria.Predicate n4 = cb.equal(c.get("countryId"), countryId);
		//	if(req.getRegionCode()!=null) {
		//	jakarta.persistence.criteria.Predicate n5 = cb.equal(c.get("regionCode"), req.getRegionCode());
	
		//	query.where(n12, n2,n3,n4,n5).orderBy(orderList);
		//	}
		//	else {
				query.where(n12,n4).orderBy(orderList);	
		//	}
			// Get Result
			TypedQuery<OmanPostalCodeMaster> result = em.createQuery(query);
			list = result.getResultList();

			for (OmanPostalCodeMaster data : list) {
				// Response
				OmanDropDownRes res = new OmanDropDownRes();
				res.setCode(data.getLocationId().toString());
				res.setCodeDesc(data.getLocation());
				res.setStatus(data.getStatus());
				res.setGovernorateId(data.getGovernorateId().toString());
				res.setGovernorateDesc(data.getGovernorate());
				res.setWilayatId(data.getWilayatId().toString());
				res.setWilayatDesc(data.getWilayat());
				res.setPostalCode(data.getPostalCode().toString());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

}
