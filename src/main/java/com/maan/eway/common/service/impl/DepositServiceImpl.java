package com.maan.eway.common.service.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.DepositDetail;
import com.maan.eway.bean.DepositcbcMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.PaymentDeposit;
import com.maan.eway.bean.ProductMaster;
import com.maan.eway.common.req.GetDepositPaymentReq;
import com.maan.eway.common.req.SaveDepositeMasterReq;
import com.maan.eway.common.req.SavePaymentDepositReq;
import com.maan.eway.common.req.SavePremiumDepositReq;
import com.maan.eway.common.req.SavedepositDetailReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.GetDepositDetailRes;
import com.maan.eway.common.res.GetDepositMasterRes;
import com.maan.eway.common.res.GetDepositPaymentRes;
import com.maan.eway.common.res.SaveDepositeMasterRes;
import com.maan.eway.common.service.DepositService;
import com.maan.eway.error.Error;
import com.maan.eway.repository.DepositDetailRepository;
import com.maan.eway.repository.DepositcbcMasterRepository;
import com.maan.eway.repository.PaymentDepositRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;

@Service
public class DepositServiceImpl implements DepositService {

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private DepositcbcMasterRepository depositcbcRepo;
	
	@Autowired
	private DepositDetailRepository depositdetailRepo;
	
	@Autowired
	private EntityManagerFactory emfactory;
	
	@Autowired
	private PaymentDepositRepository paymentDepositRepo;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	private Logger log = LogManager.getLogger(DepositServiceImpl.class);
	String pattern ="#####0.0";
	DecimalFormat df = new DecimalFormat(pattern);
	@Override
	public CommonRes saveDepositeMaster(SaveDepositeMasterReq req) {
		CommonRes res = new CommonRes();
		SaveDepositeMasterRes response = new SaveDepositeMasterRes ();
		List<Error> error = saveDepositeMasterVali(req);
		int count=0;
		try {
			if(CollectionUtils.isEmpty(error)) {
			String cbcNo ="";
			DepositcbcMaster cbcMaster = new DepositcbcMaster();
			List<DepositcbcMaster> cbcList = depositcbcRepo.findByCbcNo(StringUtils.isBlank(req.getCbcNo())?"":req.getCbcNo());
			String brokerName = getBrokerNameById(req.getBrokerId());
			if(!CollectionUtils.isEmpty(cbcList)) {
				cbcMaster = cbcList.get(0);
			}else {
				 cbcNo = getCbcNumber();
				cbcMaster.setCbcNo("CBC"+cbcNo);  
				cbcMaster.setDepositUtilized(0.0);
				cbcMaster.setEntryDate(new Date());
				cbcMaster.setCompanyId(req.getCompanyId());
			}
			//if("C".equalsIgnoreCase(req.getPayableyn())) {
				cbcMaster.setDepositAmount(Double.valueOf(req.getDepositAmount()));
			//}else if("R".equalsIgnoreCase(req.getPayableyn())) {
			//	cbcMaster.setRefundAmount(Double.valueOf(req.getRefundAmount()));
			// }
			cbcMaster.setBrokerId(req.getBrokerId());
			cbcMaster.setBrokerName(brokerName);
			cbcMaster.setCustomerId(StringUtils.isBlank(req.getCustomerid())?"99999":req.getCustomerid());
			//cbcMaster.setProductId(StringUtils.isBlank(req.getProductId())?"":req.getProductId());  
			cbcMaster.setStatus("Y");  
			cbcMaster.setCompanyId(req.getCompanyId());
			
			DepositDetail depDetail = new DepositDetail();
			Optional<DepositDetail> optDetail = depositdetailRepo.findById(StringUtils.isBlank(req.getDepositNo())?0L:Long.valueOf(req.getDepositNo()));
			Long depositNo=null;
			if(optDetail.isPresent()) {
				depDetail = optDetail.get();
				depositNo=depDetail.getDepositNo();
			}else {
				depDetail.setStatus("D");
				depDetail.setEntryDate(new Date());  
				depDetail.setQuoteNo(req.getQuoteNo());
			}
				depDetail.setProductId("");
				depDetail.setQuoteNo("");
				depDetail.setBrokerId(Long.valueOf(req.getBrokerId()));
				depDetail.setCustomerId(StringUtils.isBlank(req.getCustomerid())?"99999":req.getCustomerid());
				depDetail.setPremiumAmount(Double.valueOf(req.getDepositAmount()));  
				//depDetail.setBalanceAmount(Double.valueOf(req.getBalanceAmount()));
				depDetail.setPremium(Double.valueOf(req.getDepositAmount()));
				//depDetail.setPolicyInsuranceFee(Double.valueOf(req.getPolicyInsuranceFee()));
				//depDetail.setVatAmount(Double.valueOf(req.getVatAmount()));
				//depDetail.setChargableType(req.getChargableType());
				depDetail.setChargableType(Double.parseDouble(req.getDepositAmount())<0?"R":"C");
				depDetail.setBrokerName(brokerName);
				depDetail.setDepositType("Deposit");
				
				
				if(depDetail.getDepositNo()==null) {
					depositNo=DepositMax();
					depDetail.setDepositNo(depositNo);
				}
				depDetail.setCbcNo(cbcMaster.getCbcNo());
				depositdetailRepo.save(depDetail);
				if(!optDetail.isPresent()) {
					depositcbcRepo.save(cbcMaster);
				}
				PaymentDeposit paymentdep = PaymentDeposit.builder()
					.cbcNo(cbcMaster.getCbcNo())
					.quoteNo(StringUtils.isBlank(req.getQuoteNo())?"":req.getQuoteNo())
					.depositNo(depositNo)
					.paymentType("1")
					.paymentTypeDesc(getPaymentTypeDesc("1",req.getCompanyId()))
					.premium(req.getDepositAmount()==""?0.0:Double.valueOf(req.getDepositAmount()))
					.entryDate(new Date())
					.build();
					paymentDepositRepo.save(paymentdep);
				count = 1;
				if(count == 1) {
					response.setStatus(true);
					response.setQuoteNo(req.getQuoteNo());
					res.setMessage("SUCCESS");
					res.setCommonResponse(response);
					res.setErrorMessage(null);
				}
			}else {
				res.setMessage("FAILED");
				res.setErrorMessage(error);
				res.setCommonResponse(null);
			}
		} catch (Exception e) {
			log.info("Error in saveDepositeMaster => "+ e.getMessage());
			e.printStackTrace();
		}
			return res;
	}

	private String getBrokerNameById(String brokerId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<LoginMaster> lmRoot = cq.from(LoginMaster.class);
		cq.select(lmRoot.get("loginId").as(String.class)).where(cb.equal(lmRoot.get("agencyCode"), brokerId));
		return em.createQuery(cq).getSingleResult();
	}

	private Long DepositMax() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<DepositDetail> dRoot = cq.from(DepositDetail.class);
		cq.select(cb.coalesce(cb.sum(cb.max(dRoot.get("depositNo")),1L), 100124L));
		return em.createQuery(cq).getSingleResult();
	}

	private String getCbcNumber() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<DepositcbcMaster> dmRoot = cq.from(DepositcbcMaster.class);
		String result = "",cbc = "";
		CriteriaQuery<String> cbcNo = null;
		try {
			cbcNo = cq.multiselect(cb.max(dmRoot.get("cbcNo")).as(String.class));
			cbc = em.createQuery(cbcNo).getSingleResult();
		} catch (Exception e) {
			log.info("No Entity Found");
		}
		if(StringUtils.isNotBlank(cbc)) {
			String parts [] = cbc.split("[^\\d]");
			if(parts!=null) {
				String cbcCurrenctNo = parts[3];
				int sumCbc = Integer.valueOf(cbcCurrenctNo)+1;
				result = String.valueOf(sumCbc);
			}
		}else {
			cq.multiselect(cb.coalesce(cb.sum(cb.max(dmRoot.get("cbcNo")),1), 100076).as(String.class));
			result = em.createQuery(cq).getSingleResult();
		}
		
		return result;
	}
	
	private List<Error> saveDepositeMasterVali(SaveDepositeMasterReq req) {
		List<Error> error = new ArrayList<>();
//		if(StringUtils.isBlank(req.getBalanceAmount())) {
//			error.add(new Error("500","BalanceAmount","Please Enter BalanceAmount"));
//		}
//		if(StringUtils.isBlank(req.getPayableyn())) {
//			error.add(new Error("500","Payableyn","Please Enter Payableyn"));
//		}
		if(StringUtils.isBlank(req.getDepositAmount())) {
			error.add(new Error("500","DepositAmount","Please Enter DepositAmount"));
		}
//		if(StringUtils.isBlank(req.getRefundAmount())) {
//			error.add(new Error("500","RefundAmount","Please Enter RefundAmount"));
//		}
//		if(StringUtils.isBlank(req.getQuoteNo())) {
//			error.add(new Error("500","QuoteNo","Please Enter QuoteNo"));
//		}
//		if(StringUtils.isBlank(req.getProductId())) {
//			error.add(new Error("500","ProductId","Please Enter ProductId"));
//		}
		if(StringUtils.isBlank(req.getBrokerId())) {
			error.add(new Error("500","BrokerId","Please Enter BrokerId"));
		}
		if(StringUtils.isBlank(req.getCustomerid())) {
			error.add(new Error("500","Customerid","Please Enter CustomerId"));
		}
//		if(StringUtils.isBlank(req.getPremiumAmount())) {
//			error.add(new Error("500","PremiumAmount","Please Enter PremiumAmount"));
//		}
//		if(StringUtils.isBlank(req.getPremium())) {
//			error.add(new Error("500","Premium","Please Enter Premium"));
//		}
//		if(StringUtils.isBlank(req.getPolicyInsuranceFee())) {
//			error.add(new Error("500","PolicyInsuranceFee","Please Enter PolicyInsuranceFee"));
//		}
//		if(StringUtils.isBlank(req.getVatAmount())) {
//			error.add(new Error("500","VatAmount","Please Enter VatAmount"));
//		}
//		if(StringUtils.isBlank(req.getChargableType())) {
//			error.add(new Error("500","ChargableType","Please Enter ChargableType"));
//		}
		return error;
	}

	@Override
	@Transactional
	public CommonRes savePremiumDeposit(SavePremiumDepositReq req) {
		CommonRes res = new CommonRes();
		List<Error> error = savePremiumDepositVali(req);
		String result = "";
		try {
			if(CollectionUtils.isEmpty(error)) {
				if(Double.parseDouble(req.getPremium())<0) {
					result = getCancelDepositDetails(req);
				}else {
					result = getDepositDetails(req,"update");
				}
				res.setCommonResponse(result);
				res.setMessage("SUCCESS");
			}else {
				res.setCommonResponse(null);
				res.setMessage("FAILED");
				res.setErrorMessage(error);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	
	private List<Error> savePremiumDepositVali(SavePremiumDepositReq req) {
		List<Error> error = new ArrayList<>();
		String creditLimit="";
		if(StringUtils.isBlank(req.getBrokerId())) {
			
			error.add(new Error("500","BrokerId","Please Enter BrokerId"));
		}
		
		if(StringUtils.isBlank(req.getPremium())) {
			error.add(new Error("500","Premium","Please Enter Premium"));
		}
		if(StringUtils.isBlank(req.getProductId())) {
			error.add(new Error("500","ProductId","Please Enter ProductId"));
		}
		if(StringUtils.isBlank(req.getCompanyId())) {
			error.add(new Error("500","Company","Please Enter Company Id"));
		}
//		if(StringUtils.isBlank(req.getPolicyTypeId())) {
//			error.add(new Error("500","PolicyType","Please Enter Policy Type"));
//		}
		if(StringUtils.isBlank(req.getQuoteNo())) {
			error.add(new Error("500","QuoteNo","Please Enter QuoteNo"));
		}
//		if(StringUtils.isBlank(req.getPolicyfee())) {
//			error.add(new Error("500","Policyfee","Please Enter Policyfee"));
//		}
//		if(StringUtils.isBlank(req.getVattaxamt())) {
//			error.add(new Error("500","Vattaxamt","Please Enter Vattaxamt"));
//		}
		if(StringUtils.isBlank(req.getCustomerId())) {
			error.add(new Error("500","CustomerId","Please Enter CustomerId"));
		}
		return error;
	}

	
	public String getDepositDetails(SavePremiumDepositReq req, String type) {
		String result="",customerOption="",cbcNo="",totalAmount="";
		List<DepositcbcMaster>list=null;
		Long depNo = null;
		try {
			int cunt=depositcbcRepo.countByBrokerId(req.getBrokerId());
			int count=depositdetailRepo.countByQuoteNoAndPremiumAmountAndStatus(req.getQuoteNo(),Double.valueOf(req.getPremium()),"Y");
			if(count==0) {
				if(cunt==0) {
					result="DEPOSIT IS NOT AVAILABLE FOR THIS BROKER";
				}
				list=depositcbcRepo.findByBrokerId(req.getBrokerId());
				/*
				 * if("3".equals(req.getProductId())) {
				 * list=depositcbcRepo.findByBrokerIdAndProductIdLike(req.getBrokerId(),req.
				 * getProductId()); }else {
				 * 
				 * }
				 */
				if(list!=null && list.size()>0) {
					cbcNo=list.get(0).getCbcNo()==null?"0":list.get(0).getCbcNo().toString();
					customerOption="NONE";
					String depositAMount=list.get(0).getDepositAmount()==null?"0":(df.format(list.get(0).getDepositAmount())).toString();
					String utilizedAmt=list.get(0).getDepositUtilized()==null?"0":(df.format(list.get(0).getDepositUtilized())).toString();
					String refundAmt=list.get(0).getRefundAmount()==null?"0":(df.format(list.get(0).getRefundAmount())).toString();
					totalAmount=String.valueOf(Double.valueOf(depositAMount)-Double.valueOf(utilizedAmt)+Double.valueOf(refundAmt));
				}else {
					result="DEPOSIT IS NOT AVAILABLE FOR THIS BROKER";
				}
				if("NONE".equals(customerOption)) {
					list = getDepositbalanceCheck(req.getBrokerId(),req.getCustomerId(),req.getProductId(),req.getPremium());	
					if(list!=null && list.size()>0) {
						//cbcNo=list.get(0).getCbcNo()==null?"0":list.get(0).getCbcNo().toString();
						//String depositAMount=list.get(0).getDepositAmount()==null?"0":list.get(0).getDepositAmount().toString();
						//String utilizedAmt=list.get(0).getDepositUtilized()==null?"0":list.get(0).getDepositUtilized().toString();
						//String refundAmt=list.get(0).getRefundAmount()==null?"0":list.get(0).getRefundAmount().toString();
						//totalAmount=String.valueOf(Double.valueOf(depositAMount)-Double.valueOf(utilizedAmt)+Double.valueOf(refundAmt));
					}else {
						result="DEPOSIT AMOUNT IS LOW POLICY CANNOT BE GENERATED  AND  AVAILABLE BALANCE AMOUNT IS "+totalAmount;
					}
					
					if(Double.parseDouble(totalAmount)>=Double.parseDouble(req.getPremium())) {
						if("update".equals(type)) {
							
						updateDepositCBCUtilized(req.getPremium(),cbcNo,req.getProductId(),req.getBrokerId());
						String depositbal=String.valueOf(Double.parseDouble(totalAmount)-Double.parseDouble(req.getPremium()));
								//getDepositBalance(req.getPremium(),cbcNo,req.getProductId(),req.getBrokerId());
						if(StringUtils.isNotBlank(depositbal)) {
							DepositDetail des=new DepositDetail();
							des.setDepositNo(depNo = DepositMax());
							des.setCbcNo(cbcNo);
							des.setProductId(req.getProductId());
							des.setQuoteNo(req.getQuoteNo()); 
							des.setPremiumAmount(Double.parseDouble(req.getPremium()));
							des.setEntryDate(new Date());
							des.setStatus("Y");
							des.setBalanceAmount(Double.parseDouble(depositbal));
							des.setBrokerId(Long.parseLong(req.getBrokerId()));
							des.setPremium(Double.valueOf(req.getPremium()));
							des.setPolicyInsuranceFee(req.getPolicyfee()==null?0d:Double.valueOf(req.getPolicyfee()));
							des.setVatAmount(req.getVattaxamt()==null?0d:Double.valueOf(req.getVattaxamt()));
							des.setChargableType(Double.parseDouble(req.getPremium())<0?"R":"C");
							depositdetailRepo.save(des);
							result="Y";
							
							PaymentDeposit paymentdep = PaymentDeposit.builder()
								.cbcNo(cbcNo)
								.quoteNo(StringUtils.isBlank(req.getQuoteNo())?"":req.getQuoteNo())
								.depositNo(depNo)
								.paymentType("1")
								.paymentTypeDesc(getPaymentTypeDesc("1",req.getCompanyId()))
								.premium(req.getPremium()==""?0.0:Double.valueOf(req.getPremium()))
								.payeeName(req.getPayeeName())
								.entryDate(new Date())
								.build();
								paymentDepositRepo.save(paymentdep);
							
						}else {
							result="COMBINATION NOT AVAILABLE";
						}
						}else {
							result="Y";
						}
					}else {
						result="DEPOSIT AMOUNT IS LOW POLICY CANNOT BE GENERATED  AND  AVAILABLE BALANCE AMOUNT IS "+totalAmount;
					}
				}
			}else {
				result="Y";
			}
		}catch (Exception e) {
			log.info(e);
		}
		return result;
	}

	@Transactional
	private void updateDepositCBCUtilized(String premium, String cbcNo, String productId, String brokerId) {
			try {
			EntityManager em1 = emfactory.createEntityManager();
//			String qutext = "Update eway_deposit_cbc_master Set Deposit_Utilised=NVL(Deposit_Utilised,0)+TO_NUMBER(NVL(round(?,2),0)) Where product_Id =? and Cbc_no=? and broker_id=? and Status='Y'";
			String qutext = "Update eway_deposit_cbc_master Set Deposit_Utilised=COALESCE(Deposit_Utilised,0)+(COALESCE(round(?,2),0)) Where  Cbc_no=? and broker_id=? and Status='Y'";
			Query query = em1.createNativeQuery(qutext);
			query.setParameter(1, premium);
			//query.setParameter(2, productId);
			query.setParameter(2, cbcNo);
			query.setParameter(3, brokerId);
			em1.getTransaction().begin();
			int rowsUpdated = query.executeUpdate();
			em1.getTransaction().commit();
			em1.clear();
			em1.close();
			
			log.info("entities Updated: " + rowsUpdated);
		} catch (Exception e) {
			log.info(e);
		}
	}

	public List<DepositcbcMaster> getDepositbalanceCheck(String brokerId, String customerId, String productId, String premium) {
		double totalAmount = 0.00;
		List<DepositcbcMaster> list = null;
		try {
			list = depositcbcRepo.findByStatusAndBrokerId("Y",brokerId);
			if (!CollectionUtils.isEmpty(list)) {
				String depositAMount = list.get(0).getDepositAmount() == null ? "0": list.get(0).getDepositAmount().toString();
				String utilizedAmt = list.get(0).getDepositUtilized() == null ? "0": list.get(0).getDepositUtilized().toString();
				String refundamount = list.get(0).getRefundAmount() == null ? "0": list.get(0).getRefundAmount().toString();
				totalAmount = Double.valueOf(depositAMount) - Double.valueOf(utilizedAmt)+Double.parseDouble(refundamount);
			}
			if (totalAmount >= 0) {

			} else {
				list = null;
			}
		} catch (Exception e) {
			log.info(e);
		}
		return list;
	}

	public String getCancelDepositDetails(SavePremiumDepositReq req) {
		String result="",cbcNo="",totalAmount="";
		List<DepositcbcMaster>list=null;
		try {
		int cunt=depositcbcRepo.countByBrokerId(req.getBrokerId());
		if(cunt!=0) {
			
			list=depositcbcRepo.findByBrokerId(req.getBrokerId());
			if(list!=null && list.size()>0) {
				cbcNo=list.get(0).getCbcNo()==null?"0":list.get(0).getCbcNo().toString();
				String depositAMount=list.get(0).getDepositAmount()==null?"0":(df.format(list.get(0).getDepositAmount())).toString();
				String utilizedAmt=list.get(0).getDepositUtilized()==null?"0":(df.format(list.get(0).getDepositUtilized())).toString();
				String refundAmt=list.get(0).getRefundAmount()==null?"0":(df.format(list.get(0).getRefundAmount())).toString();
				totalAmount=String.valueOf(Double.valueOf(depositAMount)-Double.valueOf(utilizedAmt)+Double.valueOf(refundAmt));
			}
			updateCancelDepositCBCUtilized(req.getPremium(),cbcNo,req.getBrokerId());
			String depositbal=String.valueOf(Double.parseDouble(totalAmount)+Double.parseDouble(req.getPremium()));
					//getDepositBalance(req.getPremium(),cbcNo,req.getProductId(),req.getBrokerId());
			if(StringUtils.isNotBlank(depositbal)) {
				DepositDetail des=new DepositDetail();
				des.setDepositNo(DepositMax());
				des.setCbcNo(cbcNo);
				des.setProductId(req.getProductId());
				des.setQuoteNo(req.getQuoteNo()); 
				des.setPremiumAmount(Double.parseDouble(req.getPremium()));
				des.setEntryDate(new Date());
				des.setStatus("Y");
				des.setBalanceAmount(Double.parseDouble(depositbal));
				des.setBrokerId(Long.parseLong(req.getBrokerId()));
				des.setBrokerName(getBrokerNameById(req.getBrokerId()));
				des.setPremium(Double.valueOf(req.getPremium()));
				des.setPolicyInsuranceFee(req.getPolicyfee()==null?0d:Double.valueOf(req.getPolicyfee()));
				des.setVatAmount(req.getVattaxamt()==null?0d:Double.valueOf(req.getVattaxamt()));
				des.setChargableType(Double.parseDouble(req.getPremium())<0?"R":"C");
				depositdetailRepo.save(des);
				result="Y";
			}
		}else {
			result="Y";
		}
	}catch (Exception e) {
		log.info(e);
	}
	return result;
	}
	@Modifying(clearAutomatically = true)
	private String getDepositBalance(String premium, String cbcNo, String productId, String brokerId) {
		String totalAmount = "";
		try {
		
			List<DepositcbcMaster> list=depositcbcRepo.findByCbcNoAndBrokerIdAndStatus(cbcNo,brokerId, "Y");
			if (!CollectionUtils.isEmpty(list)) {
				String depositAMount = list.get(0).getDepositAmount() == null ? "0": list.get(0).getDepositAmount().toString();
				String utilizedAmt = list.get(0).getDepositUtilized() == null ? "0": list.get(0).getDepositUtilized().toString();
				String refundAmt = list.get(0).getRefundAmount() == null ? "0": list.get(0).getRefundAmount().toString();
				totalAmount = String.valueOf(Double.valueOf(depositAMount) - Double.valueOf(utilizedAmt) + Double.valueOf(refundAmt));
			}
		} catch (Exception e) {
			log.info(e);
		}
		return totalAmount;
	}

	private void updateCancelDepositCBCUtilized(String premium, String cbcNo, String brokerId) {
		try {
			List<DepositcbcMaster> list = depositcbcRepo.findByCbcNoAndBrokerId(cbcNo, brokerId);
			if (!CollectionUtils.isEmpty(list)) {
				DepositcbcMaster deposit = list.get(0);
				double depositutil = deposit.getDepositUtilized() == null ? 0d : deposit.getDepositUtilized();
				double policyrefund = deposit.getPolicyrefundamount() == null ? 0d : deposit.getPolicyrefundamount();
				deposit.setDepositUtilized(depositutil + Double.valueOf(premium));
				deposit.setPolicyrefundamount(policyrefund + Double.valueOf(premium));
				depositcbcRepo.save(deposit);
			}
		} catch (Exception e) {
			log.info(e);
		}
	}

	@Override
	public CommonRes savePaymentDeposit(SavePaymentDepositReq req) {
		CommonRes res = new CommonRes();
		List<Error> error = savePaymentDepositVali(req);
		Long depNo = null;
		try {
			if(CollectionUtils.isEmpty(error)) {
				String brokerId = getbrokerIdByLoginId(req.getLoginId());
				if(StringUtils.isBlank(req.getDepositNo()))
					depNo = DepositMax();
				/*String cbcNo = "";
				if(StringUtils.isBlank(req.getCbcNo())) {
					cbcNo = "CBC"+getCbcNumber();
					DepositcbcMaster cbcMaster = DepositcbcMaster.builder()
						.cbcNo(cbcNo)
						.brokerId(brokerId)
						.brokerName(getBrokerNameById(brokerId))
						.productId(req.getProductId())
						.productName(getProductNameById(req.getProductId()))
						.status(req.getStatus())
						.entryDate(new Date())
						.depositUtilized(0.0)
						.updatedBy(req.getLoginId())
						.build();
					depositcbcRepo.save(cbcMaster);
				}else {
					cbcNo = req.getCbcNo();
				}*/
				Date entryDate=null;
				if("R".equalsIgnoreCase(req.getDepositType())) {
					entryDate=req.getRefundDate();
				}else {
					entryDate=new Date();
				}
					PaymentDeposit paymentdep = PaymentDeposit.builder()
						.cbcNo(req.getCbcNo())
						.quoteNo(StringUtils.isBlank(req.getQuoteNo())?"":req.getQuoteNo())
						.depositNo(StringUtils.isBlank(req.getDepositNo())?depNo:Long.valueOf(req.getDepositNo()))
						.paymentType(req.getPaymentType()==null?"":req.getPaymentType())
						.paymentTypeDesc(req.getPaymentType()==null?"":getPaymentTypeDesc(req.getPaymentType(),req.getCompanyId()))
						.createdBy(req.getLoginId())
						.premium(req.getPremium()==""?0.0:Double.valueOf(req.getPremium()))
						.chequeNo(req.getChequeNo())
						.chequeDate(StringUtils.isBlank(req.getChequeDate())?null:sdf.parse(req.getChequeDate()))
						.AccountNo(req.getAccountNo())
						.ibanNumber(req.getIbanNumber()==null?null:req.getIbanNumber())
						.micrno(req.getMicrNo())
						.payeeName(req.getPayeeName())
						.referenceNo(req.getReferenceNo())
						.entryDate(entryDate)
						.build();
					paymentDepositRepo.save(paymentdep);
				
				Optional<DepositcbcMaster> depositcbc = depositcbcRepo.findById(req.getCbcNo());
				DepositcbcMaster NewcbcMaster = new DepositcbcMaster();
				if(depositcbc.isPresent()) {
					NewcbcMaster = depositcbc.get();
				}
				NewcbcMaster.setCbcNo(req.getCbcNo());
				if("C".equalsIgnoreCase(req.getDepositType())) {
					//NewcbcMaster.setDepositAmount(Double.valueOf(req.getDepositAmount()));
					NewcbcMaster.setDepositAmount(NewcbcMaster.getDepositAmount() + Double.valueOf(req.getPremium()));
				}else if("R".equalsIgnoreCase(req.getDepositType())) {
					NewcbcMaster.setDepositAmount(NewcbcMaster.getDepositAmount() - Double.valueOf(req.getPremium()));
					//NewcbcMaster.setPolicyrefundamount(NewcbcMaster.getPolicyrefundamount()==null?0.0:NewcbcMaster.getPolicyrefundamount() + Double.parseDouble(req.getPremium()));
				}
				depositcbcRepo.save(NewcbcMaster);

				Optional<DepositDetail> detailMaster = depositdetailRepo.findById(StringUtils.isBlank(req.getDepositNo())?0L:Long.valueOf(req.getDepositNo()));
				DepositDetail k = new DepositDetail();
				if(detailMaster.isPresent()) {
					k = detailMaster.get();
				}else {
				DepositDetail depDetail = DepositDetail.builder()
					.quoteNo(StringUtils.isBlank(req.getQuoteNo())?"":req.getQuoteNo())
					.productId(req.getProductId())
					// .productName(getProductNameById(req.getProductId()))
				//	.premiumAmount(Double.valueOf(req.getPremium()))
					.premiumAmount(Double.valueOf(req.getPremium()))
					.entryDate(entryDate)
					.status("D")
					.cbcNo(req.getCbcNo())
					.depositNo(k.getDepositNo()==null?depNo:k.getDepositNo())
					.balanceAmount(StringUtils.isBlank(req.getBalanceAmount())?null:Double.parseDouble(req.getBalanceAmount()))
					.receiptNo(StringUtils.isBlank(req.getReceiptNo())?"":req.getReceiptNo())
					.brokerId(Long.valueOf(brokerId))
					.brokerName(getBrokerNameById(brokerId))
					.premium(Double.parseDouble(req.getPremium()))
					.policyInsuranceFee(StringUtils.isBlank(req.getPolicyInsuranceFee())?null:Double.parseDouble(req.getPolicyInsuranceFee()))
					.vatAmount(StringUtils.isBlank(req.getVatAmount())?null:Double.valueOf(req.getVatAmount()))
					.depositType("C".equalsIgnoreCase(req.getDepositType())?"Deposit":"Refund")
					.build();
				depositdetailRepo.save(depDetail);
				}
				res.setCommonResponse("Insert/Update SuccessFully");
				res.setIsError(false);
				res.setMessage("SUCCESS");
			}else {
				res.setCommonResponse(null);
				res.setIsError(true);
				res.setErrorMessage(error);
				res.setMessage("FAILED");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	private String getProductNameById(String productId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ProductMaster> pRoot = cq.from(ProductMaster.class);
		Subquery<Integer> amendId = cq.subquery(Integer.class);
		Root<ProductMaster> paRoot = amendId.from(ProductMaster.class);
		amendId.select(cb.max(paRoot.get("amendId"))).where(cb.equal(pRoot.get("productId"), paRoot.get("productId")));
		cq.select(pRoot.get("productName"))
			.where(cb.equal(pRoot.get("productId"), Integer.valueOf(productId)),
					cb.equal(pRoot.get("amendId"), amendId));
		return em.createQuery(cq).getSingleResult();
	}

	private String getPaymentTypeDesc(String paymentType,String companyId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ListItemValue> lRoot = cq.from(ListItemValue.class);
		cq.select(lRoot.get("itemValue").as(String.class))
			.where(cb.equal(lRoot.get("itemType"), "PAYMENT_MODE"),cb.equal(lRoot.get("itemCode"), paymentType),cb.equal(lRoot.get("companyId"),companyId));
		return em.createQuery(cq).getSingleResult();
	}

	private List<Error> savePaymentDepositVali(SavePaymentDepositReq req) {
		List<Error> error = new ArrayList<>();
//		if(StringUtils.isBlank(req.getCbcNo())) {
//			if(StringUtils.isBlank(req.getLoginId())) {
//				error.add(new Error("500","LoginId","Please Enter LoginId"));
//			}
//			if(StringUtils.isBlank(req.getProductId())) {
//				error.add(new Error("500","ProductId","Please Enter ProductId"));
//			}
//		}
	
		if("R".equalsIgnoreCase(req.getDepositType())) {
			if(StringUtils.isNotBlank(req.getPremium())) {
			Double totalAmount=0.0;
			Optional<DepositcbcMaster> depositcbc = depositcbcRepo.findById(req.getCbcNo());
			if (depositcbc.isPresent()) {
				String depositAMount = depositcbc.get().getDepositAmount() == null ? "0": depositcbc.get().getDepositAmount().toString();
				String utilizedAmt = depositcbc.get().getDepositUtilized() == null ? "0": depositcbc.get().getDepositUtilized().toString();
				String refundAmt = depositcbc.get().getRefundAmount() == null ? "0": depositcbc.get().getRefundAmount().toString();
				totalAmount = Double.valueOf(depositAMount) - Double.valueOf(utilizedAmt) + Double.valueOf(refundAmt);
			}
			if(Double.parseDouble(req.getPremium())>totalAmount) {
				error.add(new Error("500","Premium","Refund Amount not greater than Balance Amount"));
			}
			}
			
			if (req.getRefundDate() == null) {
				error.add(new Error("500", "RefundDate", "Please Enter Refund Date "));

			}
			if(StringUtils.isBlank(req.getPremium())) {
				error.add(new Error("500","Amount","Please Enter Amount"));
			}else if(Double.valueOf(req.getPremium())<0.0){
				error.add(new Error("500","Amount","Please Enter Valid Amount"));
			}else if (!req.getPremium().matches("[0-9.]+")) {
				error.add(new Error("500", "Amount", "Please Enter Valid Amount"));
			}
			
		}else if("C".equalsIgnoreCase(req.getDepositType())) {
			if(StringUtils.isBlank(req.getPaymentType())) {
				error.add(new Error("500","PaymentType","Please Enter PaymentType"));
			}
		
		}
//		}
		
//		if(StringUtils.isBlank(req.getPayeeName())) {
//			error.add(new Error("500","PayeeName","Please Enter PayeeName"));
//		}
//		if(StringUtils.isBlank(req.getPremiumAmount())) {
//			error.add(new Error("500","PremiumAmount","Please Enter PremiumAmount"));
//		}
//		if(StringUtils.isBlank(req.getStatus())) {
//			error.add(new Error("500","Status","Please Enter Status"));
//		}
//		if(StringUtils.isBlank(req.getBalanceAmount())) {
//			error.add(new Error("500","BalanceAmount","Please Enter BalanceAmount"));
//		}
//		if(StringUtils.isBlank(req.getPolicyInsuranceFee())) {
//			error.add(new Error("500","PolicyInsuranceFee","Please Enter PolicyInsuranceFee"));
//		}
//		if(StringUtils.isBlank(req.getVatAmount())) {
//			error.add(new Error("500","VatAmount","Please Enter VatAmount"));
//		}
		if(StringUtils.isBlank(req.getDepositType())) {
			error.add(new Error("500","DepositType","Please Enter DepositType"));
		}
//		else if("C".equalsIgnoreCase(req.getDepositType())) {
//			if(StringUtils.isBlank(req.getDepositAmount())) {
//				error.add(new Error("500","DepositAmount","Please Enter DepositAmount"));
//			}
//		}
		if("2".equalsIgnoreCase(req.getPaymentType())) {
			if(StringUtils.isBlank(req.getChequeNo())) {
				error.add(new Error("500","ChequeNo","Please Enter ChequeNo"));
			}else if(req.getChequeNo().length()<6 || req.getChequeNo().length()>8) {
				error.add(new Error("500","ChequeNo","Please Enter valid ChequeNo"));
			}else if(Long.valueOf(req.getChequeNo())<0) {
				error.add(new Error("500","ChequeNo","Please Enter Valid ChequeNo"));
			}	else if (!req.getChequeNo().matches("[0-9]+")) {
				error.add(new Error("500", "ChequeNo", "Please Enter Valid ChequeNo"));
			}
			if(StringUtils.isBlank(req.getChequeDate())) {
				error.add(new Error("500","ChequeDate","Please Enter ChequeDate"));
			}
//			if(StringUtils.isBlank(req.getAccountNo())) {
//				error.add(new Error("500","AccountNo","Please Enter AccountNo"));
//			}
//			if(StringUtils.isBlank(req.getIbanNumber())) {
//				error.add(new Error("500","IbanNumber","Please Enter IbanNumber"));
//			}
			if(StringUtils.isBlank(req.getMicrNo())) {
				error.add(new Error("500","MicrNo","Please Enter MicrNo"));
			}
			if(StringUtils.isBlank(req.getReferenceNo())) {
				error.add(new Error("500","RecepitNo","Please Enter RecepitNo"));
			}
			if(StringUtils.isBlank(req.getPremium())) {
				error.add(new Error("500","Amount","Please Enter Amount"));
			}else if(Double.valueOf(req.getPremium())<0.0){
				error.add(new Error("500","Amount","Please Enter Valid Amount"));
			}else if (!req.getPremium().matches("[0-9.]+")) {
				error.add(new Error("500", "Amount", "Please Enter Valid Amount"));
			} 
		}else if("1".equalsIgnoreCase(req.getPaymentType())) {
			if(StringUtils.isBlank(req.getPayeeName())) {
				error.add(new Error("500","PayeeName","Please Enter PayeeName"));
			}
			if(StringUtils.isBlank(req.getPremium())) {
				error.add(new Error("500","Amount","Please Enter Amount"));
			}else if(Double.valueOf(req.getPremium())<0.0){
				error.add(new Error("500","Amount","Please Enter Valid Amount"));
			}else if (!req.getPremium().matches("[0-9.]+")) {
				error.add(new Error("500", "Amount", "Please Enter Valid Amount"));
			} 
		}
		
		return error;
	}

	@Override
	public CommonRes CbcbyBrokerId(String LoginId) {
		String pattern ="#####0.0";
		DecimalFormat df = new DecimalFormat(pattern);
		CommonRes res = new CommonRes();
		List<GetDepositMasterRes> response = new ArrayList<>();
		String brokerId = "";
		if(StringUtils.isNotBlank(LoginId)) {
			brokerId = getbrokerIdByLoginId(LoginId);
		}
		List<DepositcbcMaster> list = depositcbcRepo.findByStatusAndBrokerId("Y",brokerId);
		if(!CollectionUtils.isEmpty(list)) {
			list.forEach( k -> {
				GetDepositMasterRes m = GetDepositMasterRes.builder()
					.CbcNo(k.getCbcNo())
					.brokerId(k.getBrokerId())
					.productId(k.getProductId())
					.status(k.getStatus())
					.depositAmount(k.getDepositAmount()==null?"0":(df.format(k.getDepositAmount())).toString())
					.depositUtilised(k.getDepositUtilized()==null?"0":(df.format(k.getDepositUtilized())).toString())
					.refundAmt(k.getRefundAmount()==null?"0":k.getRefundAmount().toString())
					.policyRefundAmt(k.getPolicyrefundamount()==null?"0":(df.format(k.getPolicyrefundamount())).toString())
					.brokerName(k.getBrokerName())
					.updatedBy(k.getUpdatedBy())
					.build();
				response.add(m);
			});
			res.setCommonResponse(response);
			res.setMessage("SUCCESS");
			res.setIsError(false);
		}else {
			res.setCommonResponse("No Data Found");
			res.setMessage("FAILED");
		}
		return res;
	}

	private String getbrokerIdByLoginId(String LoginId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<LoginMaster> lmRoot = cq.from(LoginMaster.class);
		cq.select(lmRoot.get("agencyCode")).where(cb.equal(lmRoot.get("loginId"), LoginId));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public CommonRes DepositMasterById(String cbcNo) {
		CommonRes res = new CommonRes();
		List<GetDepositMasterRes> response = new ArrayList<>();
		List<DepositcbcMaster> list = depositcbcRepo.findByCbcNo(cbcNo);
		if(!CollectionUtils.isEmpty(list)) {
			list.forEach(k -> {
				GetDepositMasterRes m = GetDepositMasterRes.builder()
					.CbcNo(cbcNo)
					.brokerId(k.getBrokerId())
					.productId(k.getProductId())
					.status(k.getStatus())
					.depositAmount(k.getDepositAmount()==null?"":k.getDepositAmount().toString())
					.depositUtilised(k.getDepositUtilized()==null?"":k.getDepositUtilized().toString())
					.refundAmt(k.getRefundAmount()==null?"":k.getRefundAmount().toString())
					.policyRefundAmt(k.getPolicyrefundamount()==null?"":k.getPolicyrefundamount().toString())
					.brokerName(k.getBrokerName())
					.updatedBy(k.getUpdatedBy())
					.build();
				response.add(m);
			});
			res.setCommonResponse(response);
			res.setMessage("SUCCESS");
			res.setIsError(false);
		}else {
			res.setCommonResponse("No Data Found");
			res.setMessage("FAILED");
		}
		return res;
	}

	@Override
	public CommonRes GetDepositDetail() {
		CommonRes res = new CommonRes();
		List<GetDepositDetailRes> response = new ArrayList<>();
		List<DepositDetail> list = depositdetailRepo.findByStatus("Y");
		if(!CollectionUtils.isEmpty(list)) {
			list.forEach(k -> {
				GetDepositDetailRes m = GetDepositDetailRes.builder()
					.quoteNo(k.getQuoteNo())
					.productId(k.getProductId())
					.premiumAmt(k.getPremiumAmount()==null?"":k.getPremiumAmount().toString())
					.entryDate(k.getEntryDate()==null?"":sdf.format(k.getEntryDate()))
					.status(k.getStatus())
					.cbcNo(k.getCbcNo())
					.depositNo(k.getDepositNo()==null?"":k.getDepositNo().toString())
					.balanceAmt(k.getBalanceAmount()==null?"":k.getBalanceAmount().toString())
					.receiptNo(k.getReceiptNo())
					.brokerId(k.getBrokerId()==null?"":k.getBrokerId().toString())
					.premium(k.getPremium()==null?"":k.getPremium().toString())
					.policyInsuranceFee(k.getPolicyInsuranceFee()==null?"":k.getPolicyInsuranceFee().toString())
					.vatAmount(k.getVatAmount()==null?"":k.getVatAmount().toString())
					.chargableType(k.getChargableType())
					.brokerName(k.getBrokerName())
					.build();
				response.add(m);
			});
			res.setCommonResponse(response);
			res.setMessage("SUCCESS");
			res.setIsError(false);
		}else {
			res.setCommonResponse("No Data Found");
			res.setMessage("FAILED");
		}
		return res;
	}

	@Override
	public CommonRes GetDepositDetailById(String cbcNo) {
		CommonRes res = new CommonRes();
		List<GetDepositDetailRes> response = new ArrayList<>();
//		List<DepositDetail> list = depositdetailRepo.findByCbcNoAndStatus(cbcNo,"Y");
		List<DepositDetail> list = depositdetailRepo.findByCbcNoAndStatusOrderByEntryDateAsc(cbcNo,"Y");
		if(!CollectionUtils.isEmpty(list)) {
			list.forEach(k -> {
				GetDepositDetailRes m = GetDepositDetailRes.builder()
						.quoteNo(k.getQuoteNo())
						.productId(k.getProductId())
						.premiumAmt(k.getPremiumAmount()==null?"":df.format(k.getPremiumAmount()).toString())
						.entryDate(k.getEntryDate()==null?"":sdf.format(k.getEntryDate()))
						.status(k.getStatus())
						.cbcNo(k.getCbcNo())
						.depositNo(k.getDepositNo()==null?"":k.getDepositNo().toString())
						.balanceAmt(k.getBalanceAmount()==null?"":k.getBalanceAmount().toString())
						.receiptNo(k.getReceiptNo())
						.brokerId(k.getBrokerId()==null?"":k.getBrokerId().toString())
						.premium(k.getPremium()==null?"":df.format(k.getPremium()).toString())
						.policyInsuranceFee(k.getPolicyInsuranceFee()==null?"":k.getPolicyInsuranceFee().toString())
						.vatAmount(k.getVatAmount()==null?"":k.getVatAmount().toString())
						.chargableType(k.getChargableType())
						.brokerName(k.getBrokerName())
						.depositType(k.getDepositType())
						.build();
					response.add(m);
				});
				res.setCommonResponse(response);
				res.setMessage("SUCCESS");
				res.setIsError(false);
			}else {
				res.setCommonResponse("No Data Found");
				res.setMessage("FAILED");
			}
			return res;
		}

	@Override
	public CommonRes GetDepositPayment(GetDepositPaymentReq req) {
		String pattern ="#####0.0";
		DecimalFormat df = new DecimalFormat(pattern);
		CommonRes res = new CommonRes();
		List<GetDepositPaymentRes> response = new ArrayList<>();
		if(StringUtils.isNotBlank(req.getCbcNo())) {
			List<DepositDetail> depolist=depositdetailRepo.findByCbcNoAndStatus(req.getCbcNo(), "D");
			List<Long> depositNos=depolist.stream().map(DepositDetail :: getDepositNo ).collect(Collectors.toList())  ;
			List<PaymentDeposit> list = paymentDepositRepo.findByDepositNoInOrderByEntryDateAsc(depositNos);
			if(!CollectionUtils.isEmpty(list)) {
				list.forEach(k -> {
					DepositDetail depo=depositdetailRepo.findByDepositNo(k.getDepositNo());
					String depositType=depo.getDepositType();
					GetDepositPaymentRes m = GetDepositPaymentRes.builder()
						.cbcNo(k.getCbcNo())
						.quoteNo(k.getQuoteNo())
						.paymentType(k.getPaymentType())
						.paymentTypeDesc(k.getPaymentTypeDesc())
						.premium(k.getPremium()==null?"":df.format(k.getPremium()).toString())
						.entryDate(k.getEntryDate()==null?"":sdf.format(k.getEntryDate()))
						.createdBy(k.getCreatedBy())
						.chequeNo(k.getChequeNo())
						.chequeDate(k.getChequeDate()==null?"":sdf.format(k.getChequeDate()))
						.accountNumber(k.getAccountNo())
						.ibanNumber(k.getIbanNumber())
						.micrNo(k.getMicrno())
						.payeeName(k.getPayeeName())
						.depositNo(k.getDepositNo()==null?"":k.getDepositNo().toString())
						.referenceNo(k.getReferenceNo())
						.depositType(depositType==null?"":depositType)
						.build();
					
					response.add(m);
				});
				res.setCommonResponse(response);
				res.setMessage("SUCCESS");
				res.setIsError(false);
			}else {
				res.setCommonResponse("No Data Found");
				res.setMessage("FAILED");
			}
		}
		else {
			PaymentDeposit PaymentByDep = paymentDepositRepo.findById(StringUtils.isBlank(req.getDepositNo())?0L:Long.valueOf(req.getDepositNo())).get();
			if(PaymentByDep.getDepositNo()!=null) {
					GetDepositPaymentRes m = GetDepositPaymentRes.builder()
						.cbcNo(PaymentByDep.getCbcNo())
						.quoteNo(PaymentByDep.getQuoteNo())
						.paymentType(PaymentByDep.getPaymentType())
						.paymentTypeDesc(PaymentByDep.getPaymentTypeDesc())
						.premium(PaymentByDep.getPremium()==null?"":df.format(PaymentByDep.getPremium()).toString())
						.entryDate(PaymentByDep.getEntryDate()==null?"":sdf.format(PaymentByDep.getEntryDate()))
						.createdBy(PaymentByDep.getCreatedBy())
						.chequeNo(PaymentByDep.getChequeNo())
						.chequeDate(PaymentByDep.getChequeDate()==null?"":sdf.format(PaymentByDep.getChequeDate()))
						.accountNumber(PaymentByDep.getAccountNo())
						.ibanNumber(PaymentByDep.getIbanNumber())
						.micrNo(PaymentByDep.getMicrno())
						.payeeName(PaymentByDep.getPayeeName())
						.depositNo(PaymentByDep.getDepositNo()==null?"":PaymentByDep.getDepositNo().toString())
						.referenceNo(PaymentByDep.getReferenceNo())
						.build();
					response.add(m);
				};
				res.setCommonResponse(response);
				res.setMessage("SUCCESS");
				res.setIsError(false);
			}
		return res;
		}
	
	@Override
	public CommonRes savedepositDetail(SavedepositDetailReq req) {
		CommonRes res = new CommonRes();
		List<Error> error = savedepositDetailVali(req);
		if(CollectionUtils.isEmpty(error)) {
			String brokerId = getbrokerIdByLoginId(req.getLoginId());
			DepositDetail depositDet = DepositDetail.builder()
				.quoteNo(req.getQuoteNo())
				.productId(req.getProductId())
				.premiumAmount(Double.parseDouble(req.getPremiumAmount()))
				.entryDate(new Date())
				.status(req.getStatus())
				.cbcNo(req.getCbcNo())
				.depositNo(StringUtils.isBlank(req.getDepositNo())?DepositMax():Long.valueOf(req.getDepositNo()))
				.balanceAmount(Double.parseDouble(req.getBalanceAmount()))
				.receiptNo(StringUtils.isBlank(req.getReceiptNo())?"":req.getReceiptNo())
				.brokerId(Long.valueOf(brokerId))
				.brokerName(getBrokerNameById(brokerId))
				.premium(Double.valueOf(req.getPremium()))
				.policyInsuranceFee(Double.valueOf(req.getPolicyInsuranceFee()))
				.vatAmount(Double.valueOf(req.getVatAmount()))
				.chargableType(StringUtils.isBlank(req.getChargableType())?"":req.getChargableType())
				.productName(getProductNameById(req.getProductId()))
				.build();
			depositdetailRepo.save(depositDet);
				res.setCommonResponse("Insert/Update SuccessFully");
				res.setMessage("SUCCESS");
				res.setIsError(false);;
		}else {
			res.setCommonResponse("Insert/Update Failed");
			res.setMessage("FAILED");
			res.setIsError(true);
			res.setErrorMessage(error);
		}
		return res;
	}

	private List<Error> savedepositDetailVali(SavedepositDetailReq req) {
		List<Error> error = new ArrayList<>();
		if(StringUtils.isBlank(req.getQuoteNo())) {
			error.add(new Error("500","QuoteNo","Please Enter QuoteNo"));
		}
		if(StringUtils.isBlank(req.getProductId())) {
			error.add(new Error("500","ProductId","Please Enter ProductId"));
		}
		if(StringUtils.isBlank(req.getPremiumAmount())) {
			error.add(new Error("500","PremiumAmount","Please Enter PremiumAmount"));
		}
		if(StringUtils.isBlank(req.getStatus())) {
			error.add(new Error("500","Status","Please Enter Status"));
		}
		if(StringUtils.isBlank(req.getCbcNo())) {
			error.add(new Error("500","CbcNo","Please Enter CbcNo"));
		}
		if(StringUtils.isBlank(req.getBalanceAmount())) {
			error.add(new Error("500","BalanceAmount","Please Enter BalanceAmount"));
		}
		if(StringUtils.isBlank(req.getLoginId())) {
			error.add(new Error("500","LoginId","Please Enter LoginId"));
		}
		if(StringUtils.isBlank(req.getPremium())) {
			error.add(new Error("500","Premium","Please Enter Premium"));
		}
		if(StringUtils.isBlank(req.getPolicyInsuranceFee())) {
			error.add(new Error("500","PolicyInsuranceFee","Please Enter PolicyInsuranceFee"));
		}
		if(StringUtils.isBlank(req.getVatAmount())) {
			error.add(new Error("500","VatAmount","Please Enter VatAmount"));
		}
		return error;
	}
	

}
