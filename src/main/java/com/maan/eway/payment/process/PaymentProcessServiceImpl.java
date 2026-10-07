package com.maan.eway.payment.process;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.PaymentProcessDetail;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.payment.process.Repository.PaymentProcessDetailRepository;
import com.maan.eway.payment.process.req.SavePaymentProcessReq;
import com.maan.eway.payment.process.req.StatusListReq;
import com.maan.eway.payment.process.res.StatusListRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class PaymentProcessServiceImpl implements PaymentProcessService {
	
	public static final Logger LOGGER = LogManager.getLogger(PaymentProcessServiceImpl.class);
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	@Autowired
	private PaymentProcessDetailRepository detailRepository;
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public CommonRes savePaymentProcess(SavePaymentProcessReq req) {
		LOGGER.info("Enter into savePaymentProcess || "+req.toString());
		CommonRes res = new CommonRes();
		try {
			Timestamp today = Timestamp.from(Instant.now());
			Optional<PaymentProcessDetail> processDetail = detailRepository.findByQuoteNoAndPaymentId(req.getQuoteNo(),StringUtils.isBlank(req.getPaymentId())?"":req.getPaymentId());
			if(processDetail.isPresent()) {
				PaymentProcessDetail p = processDetail.get();
				if("creditcontroller".equalsIgnoreCase(req.getType())) {
					if("Y".equalsIgnoreCase(req.getStatus())) {
						p.setCreditControllerStatus("CCA");
						p.setCcUpdatedDate(today);
						p.setSurveyorStatus("SP");
						p.setSurveyorCode(req.getAgencyCode());
						p.setCcRemarks(req.getRemarks());
						p.setType("surveyor");
					}else if("P".equalsIgnoreCase(req.getStatus())){
						p.setCreditControllerStatus("CCP");
						p.setCcUpdatedDate(today);
						p.setCcRemarks(req.getRemarks());
					}else {
						p.setCreditControllerStatus("CCR");
						p.setCcUpdatedDate(today);
						p.setCcRemarks(req.getRemarks());
					}
				}else if("surveyor".equalsIgnoreCase(req.getType())) {
					if("Y".equalsIgnoreCase(req.getStatus())) {
						p.setSurveyorStatus("SA");
						p.setSurveyorUpdatedDate(today); // here call policy generation block
						p.setUnderWritterStatus("UWP");
						p.setUnderWritterCode(req.getAgencyCode());
						p.setSsRemarks(req.getRemarks());
						p.setType("underwritter");
					}else if("P".equalsIgnoreCase(req.getStatus())) {
						p.setSurveyorStatus("SP");
						p.setSurveyorUpdatedDate(today);
						p.setSsRemarks(req.getRemarks());
					}else if("R".equalsIgnoreCase(req.getStatus())) {
						p.setSurveyorStatus("SR");
						p.setSurveyorUpdatedDate(today);
						p.setCreditControllerStatus("CCP");
						p.setSsRemarks(req.getRemarks());
						p.setType("creditcontroller");
					}
				}else if("underwritter".equalsIgnoreCase(req.getType())) {
					if("Y".equalsIgnoreCase(req.getStatus())) {
						p.setUnderWritterStatus("UWA");
						p.setUnderwritterUpdatedDate(today);// update status on home position master
						p.setUwRemarks(req.getRemarks());
						p.setType("completed");
					}else if("P".equalsIgnoreCase(req.getStatus())) {
						p.setUnderWritterStatus("UWP");
						p.setUnderwritterUpdatedDate(today);
						p.setUwRemarks(req.getRemarks());
					}else if("R".equalsIgnoreCase(req.getStatus())) {
						p.setUnderWritterStatus("UWR");
						p.setUnderwritterUpdatedDate(today);
						p.setSurveyorStatus("SP");
						p.setUwRemarks(req.getRemarks());
						p.setType("surveyor");
					}
				}
				detailRepository.save(p);
				res.setCommonResponse(p);
				res.setMessage("SUCCESS");
				res.setIsError(false);
				LOGGER.info("Exit into savePaymentProcess");
				return res;
			}else {
				boolean paymentCondition = Arrays.asList("101","102").contains(req.getPaymentId());
				PaymentProcessDetail p = PaymentProcessDetail.builder()
						.quoteNo(req.getQuoteNo())
						.paymentId(req.getPaymentId())
						.paymentType(getPaymentDesc(req.getPaymentId(),req.getInsuranceId(),req.getBranchCode()))
						.entryDate(today)
						.creditControllerStatus(paymentCondition?"CCA":"CCP")
						.ccUpdatedDate(paymentCondition?today:null)
						.surveyorStatus(paymentCondition?"SP":null)
						.companyId(req.getInsuranceId())
						.type(paymentCondition?"surveyor":"creditcontroller")
						.build();
				detailRepository.save(p);
				res.setCommonResponse(p);
				res.setMessage("SUCCESS");
				res.setIsError(false);
				LOGGER.info("Exit into savePaymentProcess");
				return res;
			}
			
		}catch(Exception e) {
			LOGGER.info("Error in savePaymentProcess  || "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	private String getPaymentDesc(String paymentId,String insuranceId,String branchCode) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ListItemValue> lRoot = cq.from(ListItemValue.class);
		
		Subquery<Integer> lAmd = cq.subquery(Integer.class);
		Root<ListItemValue> lAmdRoot = lAmd.from(ListItemValue.class);
		lAmd.select(cb.max(lAmdRoot.get("amendId"))).where(cb.equal(lAmdRoot.get("companyId"),lRoot.get("companyId")),
				cb.equal(lAmdRoot.get("status"),lRoot.get("status")),cb.equal(lAmdRoot.get("itemType"),lRoot.get("itemType")),
				cb.equal(lAmdRoot.get("branchCode"),lRoot.get("branchCode")));
		
		cq.select(lRoot.get("itemValue")).where(cb.equal(lRoot.get("companyId"), insuranceId),
				cb.equal(lRoot.get("status"), "Y"),cb.equal(lRoot.get("itemType"), "PAYMENT_MODE"),
				cb.or(cb.equal(lRoot.get("branchCode"), branchCode),cb.equal(lRoot.get("branchCode"), "99999")),
				cb.equal(lRoot.get("amendId"), lAmd),cb.equal(lRoot.get("itemCode"), paymentId));
			
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public CommonRes getStatusList(String type,String status,StatusListReq req) {
		LOGGER.info("Enter into StatusList || "+type,status);
		CommonRes response = new CommonRes();
		List<StatusListRes> res = new ArrayList<StatusListRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
			Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
			Root<PaymentProcessDetail> ppdRoot = cq.from(PaymentProcessDetail.class);
			
			Predicate n1 = null;
			if("creditcontroller".equalsIgnoreCase(type)) {
				n1 = cb.equal(ppdRoot.get("creditControllerStatus"), status);
			}else if("surveyor".equalsIgnoreCase(type)) {
				n1 = cb.equal(ppdRoot.get("surveyorStatus"), status);
			}else if("underwritter".equalsIgnoreCase(type)) {
				n1 = cb.equal(ppdRoot.get("underWritterStatus"), status);
			}
			
			cq.multiselect(hpmRoot.get("quoteNo").alias("quoteNo"),cb.concat(piRoot.get("titleDesc"), cb.concat(".", piRoot.get("clientName"))).alias("clientName"),piRoot.get("mobileNo1").alias("mobileNo1"),
					hpmRoot.get("inceptionDate").alias("inceptionDate"),hpmRoot.get("expiryDate").alias("expiryDate"),hpmRoot.get("overallPremiumFc").alias("overallPremiumFc"),
					ppdRoot.get("paymentType").alias("paymentType"),ppdRoot.get("paymentId").alias("paymentId"),ppdRoot.get("type").alias("type"))
			.where(cb.equal(hpmRoot.get("quoteNo"), ppdRoot.get("quoteNo")),cb.equal(hpmRoot.get("customerId"), piRoot.get("customerId")),n1 == null?cb.conjunction():n1,
					cb.equal(hpmRoot.get("productId"), req.getProductId()));
			
			List<Tuple> list = em.createQuery(cq).getResultList();
			if(!list.isEmpty()) {
				list.forEach(k -> {
					StatusListRes m = StatusListRes.builder()
							.quoteNo(k.get("quoteNo")==null?"":k.get("quoteNo").toString())
							.customerName(k.get("clientName")==null?"":k.get("clientName").toString())
							.inceptionDate(k.get("inceptionDate")==null?"":sdf.format(k.get("inceptionDate")))
							.expiryDate(k.get("expiryDate")==null?"":sdf.format(k.get("expiryDate")))
							.premium(k.get("overallPremiumFc")==null?"":k.get("overallPremiumFc").toString())
							.paymentType(k.get("paymentType")==null?"":k.get("paymentType").toString())
							.paymentId(k.get("paymentId")==null?"":k.get("paymentId").toString())
							.type(k.get("type")==null?"":k.get("type").toString())
							.mobileNo(k.get("mobileNo1")==null?"":k.get("mobileNo1").toString())
							.build();
					res.add(m);
				});
				response.setCommonResponse(res);
				response.setMessage("SUCCESS");
				response.setIsError(false);
				response.setErrorMessage(null);
			}else {
				response.setCommonResponse(null);
				response.setMessage("FAILED");
				response.setIsError(true);
			}
			return response;
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
