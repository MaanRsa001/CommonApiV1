package com.maan.eway.whatsapp;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.MotorColorMaster;

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
public class WhatsappColorServiceImpl implements WhatsappColorService {

	private Logger log = LogManager.getLogger(WhatsappColorServiceImpl.class);

	@PersistenceContext
	private EntityManager em;

	@Override
	public WhatsappColorDropdownCommonRes dropdownTop100(WhatsappColorDropdownReq req) {
		WhatsappColorDropdownCommonRes response = baseResponse();

		if (req == null || StringUtils.isBlank(req.getInsuranceId()) || StringUtils.isBlank(req.getBranchCode())) {
			response.setMessage("InsuranceId and BranchCode are required");
			response.setIsError(true);
			return response;
		}

		try {
			List<MotorColorMaster> list = fetchActiveColorMasterRecords(req.getInsuranceId(), req.getBranchCode());
			list = distinctColors(list);
			list.sort(Comparator.comparing(MotorColorMaster::getColorCode, Comparator.nullsLast(String::compareToIgnoreCase)));

			int max = Math.min(list.size(), 100);
			List<WhatsappColorDropdownItemRes> result = new ArrayList<>();
			for (int i = 0; i < max; i++) {
				MotorColorMaster data = list.get(i);
				result.add(new WhatsappColorDropdownItemRes(data.getColorId().toString(), data.getColorCode()));
			}

			response.setMessage("Success");
			response.setResult(result);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			response.setMessage("Failed");
			response.setIsError(true);
			return response;
		}
	}

	@Override
	public WhatsappColorDropdownCommonRes search(WhatsappColorDropdownReq req) {
		WhatsappColorDropdownCommonRes response = baseResponse();

		if (req == null || StringUtils.isBlank(req.getInsuranceId()) || StringUtils.isBlank(req.getBranchCode())
				|| StringUtils.isBlank(req.getSearchText())) {
			response.setMessage("InsuranceId, BranchCode and SearchText are required");
			response.setIsError(true);
			return response;
		}

		String search = req.getSearchText().trim().toUpperCase();
		try {
			List<MotorColorMaster> list = fetchActiveColorMasterRecords(req.getInsuranceId(), req.getBranchCode());
			list = distinctColors(list);

			List<MotorColorMaster> filtered = list.stream()
					.filter(d -> {
						String code = d.getColorCode();
						String desc = d.getColorDesc();
						return (code != null && code.toUpperCase().contains(search))
								|| (desc != null && desc.toUpperCase().contains(search));
					})
					.sorted(Comparator.comparing(MotorColorMaster::getColorCode, Comparator.nullsLast(String::compareToIgnoreCase)))
					.collect(Collectors.toList());

			List<WhatsappColorDropdownItemRes> result = filtered.stream()
					.map(d -> new WhatsappColorDropdownItemRes(d.getColorId().toString(), d.getColorCode()))
					.collect(Collectors.toList());

			response.setMessage("Success");
			response.setResult(result);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			response.setMessage("Failed");
			response.setIsError(true);
			return response;
		}
	}

	private WhatsappColorDropdownCommonRes baseResponse() {
		WhatsappColorDropdownCommonRes response = new WhatsappColorDropdownCommonRes();
		response.setIsError(false);
		response.setErrorMessage(new ArrayList<>());
		response.setErroCode(0);
		response.setResult(new ArrayList<>());
		return response;
	}

	private List<MotorColorMaster> distinctColors(List<MotorColorMaster> list) {
		return list.stream().filter(distinctByKey(o -> Arrays.asList(o.getColorId()))).collect(Collectors.toList());
	}

	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> map = new ConcurrentHashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	private List<MotorColorMaster> fetchActiveColorMasterRecords(String insuranceId, String branchCode) {
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 1);
		Date todayEnd = cal.getTime();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MotorColorMaster> query = cb.createQuery(MotorColorMaster.class);
		Root<MotorColorMaster> c = query.from(MotorColorMaster.class);
		query.select(c);

		List<Order> orderList = new ArrayList<>();
		orderList.add(cb.asc(c.get("colorDesc")));

		Subquery<Date> effectiveDate = query.subquery(Date.class);
		Root<MotorColorMaster> ocpm1 = effectiveDate.from(MotorColorMaster.class);
		effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
		Predicate a1 = cb.equal(c.get("colorId"), ocpm1.get("colorId"));
		Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
		Predicate a5 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
		Predicate a6 = cb.equal(c.get("branchCode"), ocpm1.get("branchCode"));
		effectiveDate.where(a1, a2, a5, a6);

		Subquery<Date> effectiveDate2 = query.subquery(Date.class);
		Root<MotorColorMaster> ocpm2 = effectiveDate2.from(MotorColorMaster.class);
		effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
		Predicate a3 = cb.equal(c.get("colorId"), ocpm2.get("colorId"));
		Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
		Predicate a7 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
		Predicate a8 = cb.equal(c.get("branchCode"), ocpm2.get("branchCode"));
		effectiveDate2.where(a3, a4, a7, a8);

		Predicate n1 = cb.equal(c.get("status"), "Y");
		Predicate n11 = cb.equal(c.get("status"), "R");
		Predicate n12 = cb.or(n1, n11);
		Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
		Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
		Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
		Predicate n5 = cb.equal(c.get("branchCode"), branchCode);
		Predicate n6 = cb.equal(c.get("branchCode"), "99999");
		Predicate n7 = cb.or(n5, n6);
		query.where(n12, n2, n3, n4, n7).orderBy(orderList);

		TypedQuery<MotorColorMaster> result = em.createQuery(query);
		return result.getResultList();
	}
}

