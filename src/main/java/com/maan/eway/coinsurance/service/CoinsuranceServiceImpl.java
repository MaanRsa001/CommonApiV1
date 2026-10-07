package com.maan.eway.coinsurance.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.coinsurance.Repo.CoInsuranceDetailsRepo;
import com.maan.eway.coinsurance.Repo.CoinsuranceHeaderRepo;
import com.maan.eway.coinsurance.bean.CoInsuranceDetails;
import com.maan.eway.coinsurance.bean.CoInsuranceHeader;
import com.maan.eway.coinsurance.req.DetailsReq;
import com.maan.eway.coinsurance.req.QuoteNoReq;
import com.maan.eway.coinsurance.res.CoInsuranceRes;
import com.maan.eway.coinsurance.res.CoinsuranceListRes;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class CoinsuranceServiceImpl implements CoinsuranceService {
	
	@Autowired
	CoinsuranceHeaderRepo coInRepo;
	@Autowired
	CoInsuranceDetailsRepo coDetlsRepo;
	@Autowired
	PolicyCoverDataRepository policyRepo;
	@Autowired
	HomePositionMasterRepository homerepo;
	
	 @PersistenceContext
	    private EntityManager em;
	 
		@Override
		public Map<Boolean, String> validationRole(DetailsReq req) {
			try {
				String currentRole = req.getCoInsuranceRole();
				if (currentRole == null || currentRole.isBlank()) {
					return Map.of(false, "CoInsuranceRole is empty");
				}

				String normalizedRole = currentRole.trim().toUpperCase();
				if (!normalizedRole.equals("LEADER") && !normalizedRole.equals("FOLLOW")) {
					return Map.of(false, "Invalid CoInsuranceRole: must be either 'LEADER' or 'FOLLOW'");
				}

				List<CoInsuranceDetails> existingList = coDetlsRepo.findByQuoteNo(req.getQuoteNo());

				// If updating
				if (StringUtils.isNotBlank(req.getSlNo())) {
					CoInsuranceDetails existingRecord = coDetlsRepo.findByQuoteNoAndSlNo(req.getQuoteNo(),
							Integer.valueOf(req.getSlNo()));
					if (existingRecord != null) {
						String oldRole = existingRecord.getCoInsurerRole();

						// If existing role was already LEADER, and you're not changing it → allow
						if ("LEADER".equalsIgnoreCase(oldRole) && "LEADER".equalsIgnoreCase(normalizedRole)) {
							return Map.of(true, "Role validation passed for update");
						}

						// If trying to change role to LEADER from FOLLOW → validate
						if ("FOLLOW".equalsIgnoreCase(oldRole) && "LEADER".equalsIgnoreCase(normalizedRole)) {
							boolean leaderExists = existingList.stream()
									.anyMatch(detail -> "LEADER".equalsIgnoreCase(detail.getCoInsurerRole())
											&& !detail.getSlNo().equals(existingRecord.getSlNo())); // exclude self

							if (leaderExists) {
								return Map.of(false,
										"Only one LEADER is allowed per QuoteNo. A LEADER already exists.");
							}
							return Map.of(true, "Role change from FOLLOW to LEADER is valid");
						}

						// If changing to FOLLOW → always allowed
						return Map.of(true, "Role validation passed for update");
					} else {
						return Map.of(false, "No existing record found for update");
					}
				}

				// Insert case (no slNo)
				if ("LEADER".equalsIgnoreCase(normalizedRole)) {
					boolean leaderExists = existingList.stream()
							.anyMatch(detail -> "LEADER".equalsIgnoreCase(detail.getCoInsurerRole()));
					if (leaderExists) {
						return Map.of(false, "Only one LEADER is allowed per QuoteNo. A LEADER already exists.");
					}
				}

				return Map.of(true, "Role validation passed");

			} catch (Exception e) {
				e.printStackTrace();
				return Map.of(false, "Exception during role validation: " + e.getMessage());
			}
		}


	 
	 @Override
	 public Map<Boolean, String> validationShare(DetailsReq req) {
	     try {
	         List<CoInsuranceDetails> existingList = coDetlsRepo.findByQuoteNo(req.getQuoteNo());

	         if (existingList == null) {
	             return Map.of(false, "No existing records found for QuoteNo: " + req.getQuoteNo());
	         }

	         // Parse new share value
	         double newShare;
	         try {
	             newShare = Double.parseDouble(req.getSharePrecentage());
	         } catch (NumberFormatException e) {
	             return Map.of(false, "Invalid SharePrecentage in request: " + req.getSharePrecentage());
	         }

	         double existingSum = existingList.stream()
	                 .map(CoInsuranceDetails::getSharePercentage)
	                 .filter(Objects::nonNull)
	                 .mapToDouble(BigDecimal::doubleValue)
	                 .sum();

	         // If this is an update, subtract old value
	         if (StringUtils.isNotBlank(req.getSlNo())) {
	             CoInsuranceDetails existingRecord = coDetlsRepo.findByQuoteNoAndSlNo(req.getQuoteNo(), Integer.valueOf(req.getSlNo()));
	             if (existingRecord != null && existingRecord.getSharePercentage() != null) {
	                 existingSum -= existingRecord.getSharePercentage().doubleValue();
	             }
	         }

	         double totalAfterUpdate = existingSum + newShare;

	         if (totalAfterUpdate > 100.0) {
	             return Map.of(false, "Total SharePrecentage will be " + totalAfterUpdate + "%. It must not exceed 100%");
	         }

	         return Map.of(true, "SharePrecentage is valid. Total after update: " + totalAfterUpdate + "%");

	     } catch (Exception e) {
	         e.printStackTrace();
	         return Map.of(false, "Error in validation logic: " + e.getMessage());
	     }
	 }

	 private BigDecimal sumBigDecimalList(List<BigDecimal> values) {
		    if (values == null || values.isEmpty()) {
		        return BigDecimal.ZERO;
		    }
		    return values.stream()
		        .filter(Objects::nonNull)
		        .reduce(BigDecimal.ZERO, BigDecimal::add);
		}

		

	@Override
	public CommonRes insertCo(QuoteNoReq req) {
		CoInsuranceHeader saveData = new CoInsuranceHeader();
		CommonRes res = new CommonRes();
		CoInsuranceRes response = new CoInsuranceRes();
		try
		{
			List<PolicyCoverData> policyData = policyRepo.findByQuoteNo(req.getQuoteNo());
			List<BigDecimal> sum = new ArrayList<>();
			List<BigDecimal> preLc = new ArrayList<>();
			List<BigDecimal> preFc = new ArrayList<>();
			List<BigDecimal> tax = new ArrayList<>();
			for (PolicyCoverData policy : policyData) {
				if ("T".equalsIgnoreCase(policy.getCoverageType())) {
					sum.add(policy.getSumInsured());
					tax.add(policy.getTaxAmountLc());
					
				} else {
					preLc.add(policy.getPremiumIncludedTaxLc());
					preFc.add(policy.getPremiumIncludedTaxFc());
				}
			}
			
			HomePositionMaster homedata = homerepo.findByQuoteNo(req.getQuoteNo());
			saveData.setQuoteNo(req.getQuoteNo());
			saveData.setCompanyId(Integer.valueOf(homedata.getCompanyId()));
			saveData.setProductId(Integer.valueOf(homedata.getProductId()));
			saveData.setProductDesc(homedata.getProductName());
			saveData.setCustomerName(homedata.getCustomerName());
			saveData.setPolicyStartDate(homedata.getInceptionDate());
			saveData.setPolicyEndDate(homedata.getExpiryDate());
			saveData.setSumInsuredLc(sumBigDecimalList(sum));
			saveData.setTotalPremiumLc(sumBigDecimalList(preLc));
			saveData.setTaxAmountLc(sumBigDecimalList(tax));
			saveData.setSumInsuredFc(sumBigDecimalList(sum).setScale(5, RoundingMode.HALF_UP));
			saveData.setTotalPremiumFc(sumBigDecimalList(preFc).setScale(5, RoundingMode.HALF_UP));
			saveData.setTaxAmountFc(sumBigDecimalList(tax).setScale(5, RoundingMode.HALF_UP));
			saveData.setCurrencyId(homedata.getCurrency());
			saveData.setExchangeRate(homedata.getExchangeRate());
			saveData.setEntryDate(new Date());
			
			saveData.setStatus("Y");
			coInRepo.saveAndFlush(saveData);
			ModelMapper mapper = new ModelMapper();
			response = mapper.map(saveData, CoInsuranceRes.class);
			response.setQuoteNo(req.getQuoteNo());
		   
		    response.setCurrencyId(homedata.getCurrency());
		    response.setCustomerName(homedata.getCustomerName());
		    response.setProductDesc(homedata.getProductName());
			res.setMessage("Saved Successfully");
			res.setCommonResponse(response);
			res.setIsError(false);
			res.setErroCode(0);

		}catch(Exception e)
		{
			  res.setMessage("not working" +e);
		        res.setCommonResponse(saveData);
		        res.setIsError(false);
		        res.setErroCode(0);
			e.printStackTrace();
		}
		return res;
	}
	
	@Override
	public CommonRes insertCoDetails(DetailsReq req) {
		CommonRes res = new CommonRes();
		CoInsuranceDetails save = new CoInsuranceDetails();
		List<CoInsuranceDetails> existingList = new ArrayList<CoInsuranceDetails>();
		try {
			if(StringUtils.isBlank(req.getSlNo()))
			{
				existingList = coDetlsRepo.findByQuoteNo(req.getQuoteNo());
			if (existingList == null || existingList.isEmpty()) {
				save.setSlNo(1);
			   }
			else
			{
				int maxSNo = existingList.stream().mapToInt(CoInsuranceDetails::getSlNo).max().orElse(0);
				save.setSlNo(maxSNo + 1);
			}
			}else {
				save = coDetlsRepo.findByQuoteNoAndSlNo(req.getQuoteNo() , Integer.valueOf(req.getSlNo()));
			}
			    CoInsuranceHeader headerdata = coInRepo.findByQuoteNo(req.getQuoteNo());
				save.setCompanyId(Integer.valueOf(headerdata.getCompanyId()));
				save.setQuoteNo(req.getQuoteNo());
				save.setProductId(Integer.valueOf(headerdata.getProductId()));
				save.setProductDesc(headerdata.getProductDesc());
				save.setInsuranceCompanyId(req.getInsuranceCompanyId());
				save.setCoInsurerRole(req.getCoInsuranceRole());
				save.setPolicyStartDate(req.getPolicyStartDate());
				save.setPolicyEndDate(req.getPolicyEndDate());
				save.setInsuranceCompanyDesc(req.getInsuranceCompanyDesc());
				BigDecimal share = toBigDecimal(req.getSharePrecentage());
				BigDecimal comShare = toBigDecimal(req.getCommissionPre());
				save.setCommissionPercentage(comShare);
				save.setSharePercentage(share);
				CoInsuranceHeader header = coInRepo.findByQuoteNo(req.getQuoteNo());
				BigDecimal sumLc = calculateShareAmount(header.getSumInsuredLc(), share);
				save.setSumInsuredLc(sumLc);
				BigDecimal preLc = calculateShareAmount(header.getTotalPremiumLc(), share);
				save.setPremiumLc(preLc);
				BigDecimal taxLc = calculateShareAmount(header.getTaxAmountLc(), share);
				save.setTaxAmountLc(taxLc);
				BigDecimal sumFc = calculateFA(header.getSumInsuredFc(), share);
				save.setSumInsuredFc(sumFc);
				BigDecimal preFc = calculateFA(header.getTotalPremiumFc(), share);
				save.setPremiumFc(preFc);
				BigDecimal taxFc = calculateFA(header.getTaxAmountFc(), share);
				save.setTaxAmountFc(taxFc);
				BigDecimal commAmount = calculateShareAmount(sumLc, comShare);
				save.setCommissionAmount(commAmount);
				coDetlsRepo.save(save);
				ModelMapper mapper = new ModelMapper();
				CoinsuranceListRes enRes = mapper.map(save, CoinsuranceListRes.class);
				res.setMessage("Saved Successfully");
				res.setCommonResponse(enRes);
				res.setIsError(false);
				res.setErroCode(0);
			
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			res.setMessage("error " + e);
			res.setCommonResponse(null);
			res.setIsError(true);
			res.setErroCode(1);
			return res;
		}

	}

	public BigDecimal toBigDecimal(String input) {
		if (input == null || input.trim().isEmpty()) {
			return BigDecimal.ZERO;
		}

		try {
			return new BigDecimal(input.trim());
		} catch (NumberFormatException e) {
			System.out.println("Invalid BigDecimal format: " + input);
			return BigDecimal.ZERO;
		}
	}

	private BigDecimal calculateShareAmount(BigDecimal totalAmount, BigDecimal sharePercentage) {
		if (totalAmount == null && sharePercentage == null) {
			return BigDecimal.ZERO;
		}

		BigDecimal shareAmount = totalAmount.multiply(sharePercentage).divide(BigDecimal.valueOf(100));
		BigDecimal integerPart = shareAmount.setScale(0, RoundingMode.DOWN);
		BigDecimal decimalPart = shareAmount.subtract(integerPart);

		if (decimalPart.compareTo(new BigDecimal("0.5")) > 0) {
			return integerPart.add(BigDecimal.ONE);
		} else {
			return integerPart;
		}
	}

	private BigDecimal calculateFA(BigDecimal totalAmount, BigDecimal sharePercentage) {
		if (totalAmount == null || sharePercentage == null) {
			return BigDecimal.ZERO;
		}
		return totalAmount.multiply(sharePercentage).divide(BigDecimal.valueOf(100), 5, RoundingMode.HALF_UP).stripTrailingZeros();
	}

	

	@Override
	public CommonRes getDetails(QuoteNoReq req) {
		CommonRes res = new CommonRes();
		try {
			List<CoInsuranceDetails> existingList = coDetlsRepo.findByQuoteNo(req.getQuoteNo());

			if (existingList == null || existingList.isEmpty()) {
				res.setMessage("No records found for quote number: " + req.getQuoteNo());
				res.setCommonResponse(Collections.emptyList()); 
				res.setIsError(false);
				res.setErroCode(0);
				return res;
			}

			ModelMapper mapper = new ModelMapper();

			List<CoinsuranceListRes> responseList = existingList.stream().map(entity -> {
				CoinsuranceListRes re = mapper.map(entity, CoinsuranceListRes.class); 
				re.setSharePrecentage((entity.getSharePercentage() == null) ? ""
						: entity.getSharePercentage().stripTrailingZeros().toPlainString());
				re.setCommissionPercentage((entity.getCommissionPercentage() == null) ? ""
						: entity.getCommissionPercentage().stripTrailingZeros().toPlainString());
				re.setCommissionAmount((entity.getCommissionAmount() == null) ? ""
						: entity.getCommissionAmount().stripTrailingZeros().toPlainString());

				return re;
			}).collect(Collectors.toList());

			res.setMessage("Details retrieved successfully");
			res.setCommonResponse(responseList);
			res.setIsError(false);
			res.setErroCode(0);
			return res;

		} catch (Exception e) {
			e.printStackTrace();
			res.setMessage("Exception in getDetails API: " + e.getMessage());
			res.setCommonResponse(null);
			res.setIsError(true);
			res.setErroCode(1);
			return res;
		}
	}

}
