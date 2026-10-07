package com.maan.eway.endorsment.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.EmiTransactionDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.EndorsementEmiDetailsRes;
import com.maan.eway.endorsment.request.EndorsementEmiReq;
import com.maan.eway.endorsment.request.EndorsementEmiRes;
import com.maan.eway.endorsment.service.EndorsementEmiService;
import com.maan.eway.master.res.EmiDisplayListRes;
import com.maan.eway.repository.EmiTransactionDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PaymentDetailRepository;
import com.maan.eway.repository.PaymentInfoRepository;

@Service
public class CopyEmiInfos implements EndorsementEmiService {

	@Autowired
	private HomePositionMasterRepository homeRepo;

	@Autowired
	private EmiTransactionDetailsRepository emiTranRepo;

	@Autowired
	private PaymentInfoRepository paymentInfoRepo;

	@Autowired
	private PaymentDetailRepository paymentDetailRepo;

	@Override
	public CommonRes copyEmiDetails(EndorsementEmiReq req) {
		DozerBeanMapper mapper = new DozerBeanMapper();
		EndorsementEmiDetailsRes endorseEmiDetailsRes = new EndorsementEmiDetailsRes();
		CommonRes res = new CommonRes();
		try {
			HomePositionMaster homeData = homeRepo.findByQuoteNo(req.getQuoteNo());
			String prevQuoteNo = homeData.getEndtPrevQuoteNo();
			String endtType = homeData.getEndtTypeId();
			Date expiryDate = homeData.getExpiryDate();
			HomePositionMaster prevHomeData = homeRepo.findByQuoteNo(prevQuoteNo);
			Date prevExpiryDate = prevHomeData.getExpiryDate();
			BigDecimal prevPremiumWithTax = prevHomeData.getOverallPremiumLc();
			BigDecimal endtTotPremium = homeData.getEndtPremium().add(homeData.getEndtPremiumTax());
			BigDecimal premiumWithTax = prevPremiumWithTax.add(endtTotPremium);
			//copyPaymentInfo(req.getQuoteNo(), prevQuoteNo, req.getCompanyId(), req.getProductId());
			//copyPaymentDetail(req.getQuoteNo(), prevQuoteNo, req.getCompanyId(), req.getProductId());
			EndorsementEmiDetailsRes emiRes = copyEmiTransactionDetails(req.getQuoteNo(), prevQuoteNo, req.getCompanyId(), req.getProductId(),
					premiumWithTax, endtType, expiryDate, prevExpiryDate, endtTotPremium, homeData);
			
			//update home position master
			homeData.setEmiYn(prevHomeData.getEmiYn());
			homeData.setInstallmentPeriod(prevHomeData.getInstallmentPeriod());
			homeData.setEmiPremium(prevHomeData.getEmiPremium());
			homeRepo.saveAndFlush(homeData);
			
			// set response
			res.setCommonResponse(emiRes);
			res.setIsError(false);
			res.setMessage("Success");
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void copyPaymentInfo(String quoteNo, String prevQuoteNo, String comapanyId, String productId) {
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<PaymentInfo> copyList = new ArrayList<PaymentInfo>();
		try {
			List<PaymentInfo> paymentInfoList = paymentInfoRepo.findByQuoteNo(quoteNo);
			if (!paymentInfoList.isEmpty()) {
				paymentInfoRepo.deleteAll(paymentInfoList);
			}
			List<PaymentInfo> prevPaymentInfoList = paymentInfoRepo.findByQuoteNo(prevQuoteNo);
			if (!prevPaymentInfoList.isEmpty()) {
				for (PaymentInfo paymentInfo : prevPaymentInfoList) {
					PaymentInfo copy = mapper.map(paymentInfo, PaymentInfo.class);
					copy.setQuoteNo(quoteNo);
					copyList.add(copy);
				}
				paymentInfoRepo.saveAllAndFlush(copyList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void copyPaymentDetail(String quoteNo, String prevQuoteNo, String comapanyId, String productId) {
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<PaymentDetail> copyList = new ArrayList<PaymentDetail>();
		try {
			List<PaymentDetail> paymentDetailList = paymentDetailRepo.findByQuoteNo(quoteNo);
			if (!paymentDetailList.isEmpty()) {
				paymentDetailRepo.deleteAll(paymentDetailList);
			}
			List<PaymentDetail> prevPaymentDetailList = paymentDetailRepo.findByQuoteNo(prevQuoteNo);
			if (!prevPaymentDetailList.isEmpty()) {
				for (PaymentDetail paymentDetail : prevPaymentDetailList) {
					PaymentDetail copy = mapper.map(paymentDetail, PaymentDetail.class);
					copy.setQuoteNo(quoteNo);
					copyList.add(copy);
				}
				paymentDetailRepo.saveAllAndFlush(copyList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EndorsementEmiDetailsRes copyEmiTransactionDetails(String quoteNo, String prevQuoteNo, String comapanyId,
			String productId, BigDecimal premiumWithTax, String endtTypeId, Date policyEndDate, Date prevPolicyEndDate,
			BigDecimal endtTotPremium, HomePositionMaster homeData) {
		DozerBeanMapper mapper = new DozerBeanMapper();
		EndorsementEmiRes res = new EndorsementEmiRes();
		List<EmiDisplayListRes> emidisplay = new ArrayList<EmiDisplayListRes>();
		EndorsementEmiDetailsRes endorseEmiDetailsRes = new EndorsementEmiDetailsRes();
		List<EmiTransactionDetails> copyList = new ArrayList<EmiTransactionDetails>();

		try {
			String emiStatus = "";
			Boolean isPolicyPeriod = false;
			if (policyEndDate.compareTo(prevPolicyEndDate) != 0) {
				isPolicyPeriod = true;
			}
			List<EmiTransactionDetails> emiTranDetailsList = emiTranRepo.findByQuoteNoAndCompanyIdAndProductId(quoteNo,
					comapanyId, productId);
			if (homeData.getEndtTypeId().equalsIgnoreCase("842") && emiTranDetailsList != null && !emiTranDetailsList.isEmpty()) {
				double paidAmount = emiTranDetailsList.stream().filter(e -> "paid".equalsIgnoreCase(e.getPaymentStatus()))
						.mapToDouble(e -> e.getDueAmount() != null ? e.getDueAmount() : 0).sum();
				if (endtTotPremium.compareTo(BigDecimal.ZERO) == 0) {
					BigDecimal needToPay =BigDecimal.valueOf( paidAmount).abs().negate();

				} else if (premiumWithTax.compareTo(BigDecimal.ZERO) > 0) {
					if (BigDecimal.valueOf(paidAmount).compareTo(endtTotPremium) > 0) {
						BigDecimal needToPay = BigDecimal.valueOf(paidAmount).subtract(premiumWithTax).abs().negate();
						
						
					} else if (BigDecimal.valueOf(paidAmount).compareTo(endtTotPremium) < 0) {
						BigDecimal needToPay = BigDecimal.valueOf(paidAmount).subtract(premiumWithTax).abs();

					} 
				}
				}
			
			if (premiumWithTax.compareTo(BigDecimal.ZERO) == 0) {
				res = updateEndorsementEmiTransactionDetail2(emiTranDetailsList, premiumWithTax, endtTotPremium);
//				if (!emiTranDetailsList.isEmpty()) {
//					List<EmiTransactionDetails> paidList = emiTranDetailsList.stream()
//							.filter(e -> "paid".equalsIgnoreCase(e.getPaymentStatus())).collect(Collectors.toList());
//					emiTranRepo.saveAllAndFlush(paidList);
//				}
			} else if (premiumWithTax.compareTo(BigDecimal.ZERO) > 0) {
				res = updateEndorsementEmiTransactionDetail2(emiTranDetailsList, premiumWithTax, endtTotPremium);
			}
			List<EmiTransactionDetails> pendingList = emiTranDetailsList.stream()
					.filter(e -> !"paid".equalsIgnoreCase(e.getPaymentStatus())).collect(Collectors.toList());
			for(EmiTransactionDetails emi : pendingList) {
				EmiDisplayListRes emiRes = new EmiDisplayListRes();
				//emiRes.setDueDate(prevPolicyEndDate);
				emiRes.setInstallment(emi.getDueAmount() != null ? emi.getDueAmount().toString() : null);
				emiRes.setInstallmentDesc(emi.getInstallmentTypeDesc());
				emiRes.setNoOfInstallment(emi.getInstalment());
				emidisplay.add(emiRes);
			}
			endorseEmiDetailsRes.setEmiPremiumRes(emidisplay);
			endorseEmiDetailsRes.setEmiStatus(emiStatus);
			endorseEmiDetailsRes.setEndtEmiRes(res);
			return endorseEmiDetailsRes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private List<EmiDisplayListRes> createEmiDisplayListRes(List<EmiTransactionDetails> emiDetails) {
		List<EmiDisplayListRes> emiDisplayRes = new ArrayList<EmiDisplayListRes>();
		try {
			for(EmiTransactionDetails emi:emiDetails) {
				EmiDisplayListRes e = new EmiDisplayListRes();
				e.setDueDate(emi.getDueDate());
				e.setInstallment(emi.getDueAmount().toString());
				e.setInstallmentDesc(emi.getInstallmentDesc());
				e.setNoOfInstallment(emi.getInstalment());
				emiDisplayRes.add(e);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return emiDisplayRes;
	}

	public List<EmiTransactionDetails> updateEndorsementEmiTransactionDetail(List<EmiTransactionDetails> etdList,
			BigDecimal premiumWithTax) {
		List<EmiTransactionDetails> updatedList = new ArrayList<EmiTransactionDetails>();
		try {
			List<EmiTransactionDetails> pendingList = etdList.stream()
					.filter(e -> "pending".equalsIgnoreCase(e.getPaymentStatus())).collect(Collectors.toList());
			List<EmiTransactionDetails> paidList = etdList.stream()
					.filter(e -> "paid".equalsIgnoreCase(e.getPaymentStatus())).collect(Collectors.toList());
			Integer unpaidEmis = pendingList.size();
			Integer paidEmis = paidList.size();
			BigDecimal paidAmount = paidList.stream().map(e -> e.getDueAmount()).filter(Objects::nonNull)
					.map(BigDecimal::new).map(e -> e.setScale(2, RoundingMode.HALF_UP)).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal pendingAmount = premiumWithTax.subtract(paidAmount);
			BigDecimal balanceAmount = pendingAmount;
			BigDecimal trackBalanceAmount = pendingAmount;
			BigDecimal installmentAmount = pendingAmount.divide(new BigDecimal(unpaidEmis)).setScale(2,
					RoundingMode.HALF_UP);
			Integer start = paidEmis + 1;
			Integer end = paidEmis + unpaidEmis;
			// update unpaid emi's for endorsement
			for (int i = start; i <= end; i++) {
				BigDecimal ia = installmentAmount;
				if ((installmentAmount.compareTo(balanceAmount) > 0 && i == end)
						|| (installmentAmount.compareTo(balanceAmount) < 0 && i == end)) {
					ia = balanceAmount;
				}
				balanceAmount = balanceAmount.subtract(ia);
				Integer updateEmi = i;
				EmiTransactionDetails etd = pendingList.stream()
						.filter(e -> Objects.equals(Integer.parseInt(e.getInstalment()), updateEmi)).findFirst()
						.orElse(null);
				if (etd != null) {
					etd.setPremiumWithTax(premiumWithTax.doubleValue());
					etd.setTotalLoanAmount(premiumWithTax.doubleValue());
					etd.setBalanceAmount(balanceAmount.doubleValue());
					etd.setDueAmount(ia.doubleValue());
				}
			}
			updatedList.addAll(pendingList);
			// Update paid emi's for endorsement
			for (int i = paidEmis; i > 0; i--) {
				Integer updateEmi = i;
				EmiTransactionDetails etd = paidList.stream()
						.filter(e -> Objects.equals(Integer.parseInt(e.getInstalment()), updateEmi)).findFirst()
						.orElse(null);
				if (etd != null) {
					etd.setPremiumWithTax(premiumWithTax.doubleValue());
					etd.setTotalLoanAmount(premiumWithTax.doubleValue());
					etd.setBalanceAmount(trackBalanceAmount.doubleValue());
					BigDecimal da = BigDecimal.valueOf(etd.getDueAmount());
					trackBalanceAmount = trackBalanceAmount.add(da);
				}
			}
			updatedList.addAll(paidList);
			updatedList = updatedList.stream().sorted((e1, e2) -> Integer.compare(Integer.parseInt(e1.getInstalment()),
					Integer.parseInt(e2.getInstalment()))).collect(Collectors.toList());
			return updatedList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public EndorsementEmiRes updateEndorsementEmiTransactionDetail2(List<EmiTransactionDetails> etdList,
			BigDecimal premiumWithTax, BigDecimal endtTotPremium) {
		EndorsementEmiRes res = new EndorsementEmiRes();
		try {
			double paidAmount = etdList.stream().filter(e -> "paid".equalsIgnoreCase(e.getPaymentStatus()))
					.mapToDouble(e -> e.getDueAmount() != null ? e.getDueAmount() : 0).sum();
			if (endtTotPremium.compareTo(BigDecimal.ZERO) == 0) {
				BigDecimal needToPay =BigDecimal.valueOf( paidAmount).abs().negate();
				res.setIsChargRefund("REFUND");
				res.setIsFinaceYn("Y");
				res.setPendingPremiumWithTax(String.valueOf(needToPay));
				return res;
			} else if (premiumWithTax.compareTo(BigDecimal.ZERO) > 0) {
				if (BigDecimal.valueOf(paidAmount).compareTo(endtTotPremium) > 0) {
					BigDecimal needToPay = BigDecimal.valueOf(paidAmount).subtract(premiumWithTax).abs().negate();
					res.setIsChargRefund("REFUND");
					res.setIsFinaceYn("Y");
					res.setPendingPremiumWithTax(String.valueOf(endtTotPremium));
					return res;
				} else if (BigDecimal.valueOf(paidAmount).compareTo(endtTotPremium) < 0) {
					BigDecimal needToPay = BigDecimal.valueOf(paidAmount).subtract(premiumWithTax).abs();
					res.setIsChargRefund("CHARGE");
					res.setIsFinaceYn("Y");
					res.setPendingPremiumWithTax(String.valueOf(endtTotPremium));
					return res;
				} else {
					res.setIsChargRefund("N");
					res.setIsFinaceYn("Y");
					res.setPendingPremiumWithTax("0");
					return res;
				}

			}
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void updateEndorsementEmiTransactionDetail2(Integer n, BigDecimal premiumWithTax) {

		try {

			Integer unpaidEmis = n;
			BigDecimal balanceAmount = premiumWithTax;
			BigDecimal installmentAmount = premiumWithTax.divide(new BigDecimal(unpaidEmis)).setScale(2,
					RoundingMode.HALF_UP);
			for (int i = 0; i < unpaidEmis; i++) {
				BigDecimal ia = installmentAmount;
				if ((installmentAmount.compareTo(balanceAmount) > 0 && i == unpaidEmis - 1)
						|| (installmentAmount.compareTo(balanceAmount) < 0 && i == unpaidEmis - 1)) {
					ia = balanceAmount;
				}
				balanceAmount = balanceAmount.subtract(ia);
				System.out.println("loop: " + i);
				System.out.println("installment amount is: " + ia);
				System.out.println("balance amount is: " + balanceAmount);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<EmiTransactionDetails> updateEmiForModificationOfpolicyPeriod(List<EmiTransactionDetails> etdList,
			BigDecimal premiumWithTax, Date policyEndDate) {

		List<EmiTransactionDetails> updatedList = new ArrayList<EmiTransactionDetails>();
		try {
			List<EmiTransactionDetails> pendingList = etdList.stream()
					.filter(e -> "pending".equalsIgnoreCase(e.getPaymentStatus())).collect(Collectors.toList());
			List<EmiTransactionDetails> paidList = etdList.stream()
					.filter(e -> "paid".equalsIgnoreCase(e.getPaymentStatus())).collect(Collectors.toList());
			Date firstPaidDate = paidList.stream().map(e -> e.getDueDate()).filter(Objects::nonNull).min(Date::compareTo).orElse(null);
			
			Calendar c = Calendar.getInstance();
			c.setTime(firstPaidDate);
			int desiredDay = c.get(Calendar.DAY_OF_MONTH);  // get first time paid date 
			
			BigDecimal paidAmount = paidList.stream().map(e -> e.getDueAmount()).filter(Objects::nonNull)
					.map(BigDecimal::new).map(e -> e.setScale(2, RoundingMode.HALF_UP)).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal pendingAmount = premiumWithTax.subtract(paidAmount);
			BigDecimal balanceAmount = pendingAmount;
			BigDecimal trackBalanceAmount = pendingAmount;
			Integer unpaidEmis = pendingList.size();
			Integer paidEmis = paidList.size();

			List<Date> dueDateList = pendingList.stream().map(e -> e.getDueDate()).filter(Objects::nonNull).sorted()
					.collect(Collectors.toList());
			Date nextEmiDate = dueDateList.get(0);
			List<Date> dueDateWithinPolicyEndDate = dueDateList.stream().filter(d -> d.compareTo(policyEndDate) < 0)
					.collect(Collectors.toList());
			if (dueDateWithinPolicyEndDate.size() == 1) {
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(policyEndDate);
				cal.add(Calendar.MONTH, -1);
//				int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//				cal.set(Calendar.DAY_OF_MONTH, Math.min(desiredDay, maxDay) );
				Date foundDate = cal.getTime();
					EmiTransactionDetails nextEmiEtd = pendingList.get(0);
					if (nextEmiDate.after(foundDate) && (foundDate.before(new Date()) || foundDate.compareTo(new Date()) == 0)) {
						nextEmiEtd.setDueAmount(balanceAmount.doubleValue());
						nextEmiEtd.setBalanceAmount(0d);
						nextEmiEtd.setDueDate(new Date());
						nextEmiEtd.setPremiumWithTax(premiumWithTax.doubleValue());
						nextEmiEtd.setTotalLoanAmount(premiumWithTax.doubleValue());
					} else if (foundDate.after(nextEmiDate) && nextEmiDate.compareTo(new Date()) >=0) {
							nextEmiEtd.setDueAmount(balanceAmount.doubleValue());
							nextEmiEtd.setBalanceAmount(0d);
							nextEmiEtd.setDueDate(nextEmiDate);
							nextEmiEtd.setPremiumWithTax(premiumWithTax.doubleValue());
							nextEmiEtd.setTotalLoanAmount(premiumWithTax.doubleValue());	
					} /*else {
						nextEmiEtd.setDueAmount(balanceAmount.doubleValue());
						nextEmiEtd.setBalanceAmount(0d);
						nextEmiEtd.setDueDate(nextEmiDate);
						nextEmiEtd.setPremiumWithTax(premiumWithTax.doubleValue());
						nextEmiEtd.setTotalLoanAmount(premiumWithTax.doubleValue());
					}*/
					updatedList.add(nextEmiEtd);
					// Update paid emi's for endorsement
					for (int i = paidEmis; i > 0; i--) {
						Integer updateEmi = i;
						EmiTransactionDetails etd = paidList.stream()
								.filter(e -> Objects.equals(Integer.parseInt(e.getInstalment()), updateEmi)).findFirst()
								.orElse(null);
						if (etd != null) {
							etd.setPremiumWithTax(premiumWithTax.doubleValue());
							etd.setTotalLoanAmount(premiumWithTax.doubleValue());
							etd.setBalanceAmount(trackBalanceAmount.doubleValue());
							BigDecimal da = BigDecimal.valueOf(etd.getDueAmount());
							trackBalanceAmount = trackBalanceAmount.add(da);
						}
					}
					updatedList.addAll(paidList);
					updatedList = updatedList.stream().sorted((e1, e2) -> Integer
							.compare(Integer.parseInt(e1.getInstalment()), Integer.parseInt(e2.getInstalment())))
							.collect(Collectors.toList());

			} else if (dueDateWithinPolicyEndDate.size() > 1) {
				pendingList.removeIf(e -> e.getDueDate().compareTo(policyEndDate) > 0);
				unpaidEmis = pendingList.size();
				List<EmiTransactionDetails> orderedEmiList = pendingList.stream()
						.sorted((e1, e2) -> e1.getDueDate().compareTo(e2.getDueDate()))
						.collect(Collectors.toList());
				EmiTransactionDetails lastEmi = orderedEmiList.get(orderedEmiList.size() - 1);
				Date lastEmiDate = lastEmi.getDueDate();
				long diffInMillies = policyEndDate.getTime() - lastEmiDate.getTime();
				long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                System.out.println("diffInDays "+ diffInDays);	
                
                Calendar cal = Calendar.getInstance();
				cal.setTime(policyEndDate);
				cal.add(Calendar.MONTH, -1);
//				int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//				cal.set(Calendar.DAY_OF_MONTH, Math.min(desiredDay, maxDay) );
				Date foundDate = cal.getTime();
				
				if ( foundDate.before(lastEmiDate)) { 
					lastEmiDate = orderedEmiList.get(orderedEmiList.size() - 2).getDueDate();
					orderedEmiList.remove(lastEmi);
					unpaidEmis = orderedEmiList.size();
					
					BigDecimal installmentAmount = pendingAmount.divide(new BigDecimal(unpaidEmis)).setScale(2,
							RoundingMode.HALF_UP);
					Integer start = paidEmis + 1;
					Integer end = paidEmis + unpaidEmis;
					// update unpaid emi's for endorsement
					for (int i = start; i <= end; i++) {
						BigDecimal ia = installmentAmount;
						if ((installmentAmount.compareTo(balanceAmount) > 0 && i == end)
								|| (installmentAmount.compareTo(balanceAmount) < 0 && i == end)) {
							ia = balanceAmount;
						}
						balanceAmount = balanceAmount.subtract(ia);
						Integer updateEmi = i;
						EmiTransactionDetails etd = orderedEmiList.stream()
								.filter(e -> Objects.equals(Integer.parseInt(e.getInstalment()), updateEmi)).findFirst()
								.orElse(null);
						if (etd != null) {
							etd.setPremiumWithTax(premiumWithTax.doubleValue());
							etd.setTotalLoanAmount(premiumWithTax.doubleValue());
							etd.setBalanceAmount(balanceAmount.doubleValue());
							etd.setDueAmount(ia.doubleValue());
						}
					}

					updatedList.addAll(orderedEmiList);
					// Update paid emi's for endorsement
					for (int i = paidEmis; i > 0; i--) {
						Integer updateEmi = i;
						EmiTransactionDetails etd = paidList.stream()
								.filter(e -> Objects.equals(Integer.parseInt(e.getInstalment()), updateEmi)).findFirst()
								.orElse(null);
						if (etd != null) {
							etd.setPremiumWithTax(premiumWithTax.doubleValue());
							etd.setTotalLoanAmount(premiumWithTax.doubleValue());
							etd.setBalanceAmount(trackBalanceAmount.doubleValue());
							BigDecimal da = BigDecimal.valueOf(etd.getDueAmount());
							trackBalanceAmount = trackBalanceAmount.add(da);
						}
					}
					updatedList.addAll(paidList);
					updatedList = updatedList.stream().sorted((e1, e2) -> Integer
							.compare(Integer.parseInt(e1.getInstalment()), Integer.parseInt(e2.getInstalment())))
							.collect(Collectors.toList());
				
					
					
				} else {

					BigDecimal installmentAmount = pendingAmount.divide(new BigDecimal(unpaidEmis)).setScale(2,
							RoundingMode.HALF_UP);
					Integer start = paidEmis + 1;
					Integer end = paidEmis + unpaidEmis;
					// update unpaid emi's for endorsement
					for (int i = start; i <= end; i++) {
						BigDecimal ia = installmentAmount;
						if ((installmentAmount.compareTo(balanceAmount) > 0 && i == end)
								|| (installmentAmount.compareTo(balanceAmount) < 0 && i == end)) {
							ia = balanceAmount;
						}
						balanceAmount = balanceAmount.subtract(ia);
						Integer updateEmi = i;
						EmiTransactionDetails etd = pendingList.stream()
								.filter(e -> Objects.equals(Integer.parseInt(e.getInstalment()), updateEmi)).findFirst()
								.orElse(null);
						if (etd != null) {
							etd.setPremiumWithTax(premiumWithTax.doubleValue());
							etd.setTotalLoanAmount(premiumWithTax.doubleValue());
							etd.setBalanceAmount(balanceAmount.doubleValue());
							etd.setDueAmount(ia.doubleValue());
						}
					}

					updatedList.addAll(pendingList);
					// Update paid emi's for endorsement
					for (int i = paidEmis; i > 0; i--) {
						Integer updateEmi = i;
						EmiTransactionDetails etd = paidList.stream()
								.filter(e -> Objects.equals(Integer.parseInt(e.getInstalment()), updateEmi)).findFirst()
								.orElse(null);
						if (etd != null) {
							etd.setPremiumWithTax(premiumWithTax.doubleValue());
							etd.setTotalLoanAmount(premiumWithTax.doubleValue());
							etd.setBalanceAmount(trackBalanceAmount.doubleValue());
							BigDecimal da = BigDecimal.valueOf(etd.getDueAmount());
							trackBalanceAmount = trackBalanceAmount.add(da);
						}
					}
					updatedList.addAll(paidList);
					updatedList = updatedList.stream().sorted((e1, e2) -> Integer
							.compare(Integer.parseInt(e1.getInstalment()), Integer.parseInt(e2.getInstalment())))
							.collect(Collectors.toList());
				}

			} else { //  if pending charge is there when cancel the policy
				EmiTransactionDetails nextEmiEtd = pendingList.get(0);
				nextEmiEtd.setDueAmount(balanceAmount.doubleValue());
				nextEmiEtd.setBalanceAmount(0d);
				nextEmiEtd.setDueDate(new Date());
				nextEmiEtd.setPremiumWithTax(premiumWithTax.doubleValue());
				nextEmiEtd.setTotalLoanAmount(premiumWithTax.doubleValue());
			}
			return updatedList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
