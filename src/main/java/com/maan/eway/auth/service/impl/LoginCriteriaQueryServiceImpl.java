package com.maan.eway.auth.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.maan.eway.auth.dto.LoginRequest;
import com.maan.eway.auth.service.LoginCriteriaQueryService;
import com.maan.eway.auth.token.passwordEnc;
import com.maan.eway.bean.LoginMaster;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Component
public class LoginCriteriaQueryServiceImpl implements LoginCriteriaQueryService {

	private Logger log = LogManager.getLogger(LoginCriteriaQueryServiceImpl.class);
	
	@PersistenceContext
	private EntityManager em;

	public List<LoginMaster> isvalidUser(LoginRequest req) {
		List<LoginMaster> data = new ArrayList<LoginMaster>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginMaster> query = cb.createQuery(LoginMaster.class);

			Root<LoginMaster> login = query.from(LoginMaster.class);

			passwordEnc passEnc = new passwordEnc();
			String password = passEnc.crypt(req.getPassword().trim());

			Predicate p1 = cb.equal(login.get("loginId"), req.getLoginId());
			Predicate p3 = cb.or(cb.equal(login.get("password"), password),cb.equal(login.get("password"), req.getPassword().trim()));
			Predicate p2 = cb.equal(login.get("status"), "Y");
			query.select(login).where(p1, p2, p3);

			TypedQuery<LoginMaster> result = em.createQuery(query);
			data = result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

		
}

