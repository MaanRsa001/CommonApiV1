package com.maan.eway.excess.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.ExcessMaster;
import com.maan.eway.bean.ExcessTransactionDetails;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.excess.req.ExcessDetailsReq;
import com.maan.eway.excess.req.ExcessInsertDetailsReq;
import com.maan.eway.excess.req.ExcessInsertReq;
import com.maan.eway.excess.req.ExcessTransactionReq;
import com.maan.eway.excess.res.ExcessTransactionRes;
import com.maan.eway.excess.service.ExcessTransactionDetailsService;
import com.maan.eway.repository.ExcessMasterRepository;
import com.maan.eway.repository.ExcessTransactionDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;

import groovy.transform.EqualsAndHashCode;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.Data;

@Service
public class ExcessTransactionDetailsServiceImpl implements ExcessTransactionDetailsService {

	@Autowired
	private ExcessTransactionDetailsRepository excesstranRepo;

	@Autowired
	private ExcessMasterRepository excessRepo;

	@Autowired
	private FactorRateRequestDetailsRepository factorRepo;

	@Autowired
	private EntityManager em;

	@Data
	@EqualsAndHashCode
	public class EnquiryDetails {
		private String locationId;
		private String sectionId;
		private String coverId;
		private String riskId;
	}

	// Insert Excess Transaction Details
	@Override
	public CommonRes insertExessTransactionDetails(ExcessTransactionReq req) {
		CommonRes res = new CommonRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<ExcessTransactionDetails> excessTranDetailsList = new ArrayList<ExcessTransactionDetails>();
		List<ExcessTransactionDetails> toDelete = new ArrayList<>();
		try {
			List<FactorRateRequestDetails> factorDatas = factorRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
			List<FactorRateRequestDetails> optedDatas = factorDatas.stream().filter(f -> !StringUtils.isBlank(f.getUserOpt()) && f.getUserOpt().equalsIgnoreCase("Y")).toList();
			// Create list of EnquiryDetails (with no duplicates)
			List<EnquiryDetails> enquiryDetailsList = optedDatas.stream().distinct().map(data -> {
				EnquiryDetails enquiry = new EnquiryDetails();
				enquiry.setLocationId(String.valueOf(data.getLocationId()));
				enquiry.setSectionId(String.valueOf(data.getSectionId()));
				enquiry.setCoverId(String.valueOf(data.getCoverId()));
				enquiry.setRiskId(String.valueOf(data.getVehicleId()));
				return enquiry;
			}).distinct().collect(Collectors.toList());

			String companyId = factorDatas.get(0).getCompanyId();
			String productId = String.valueOf(factorDatas.get(0).getProductId());
			List<ExcessTransactionDetails> existTranDatas = excesstranRepo
					.findByRequestReferenceNo(req.getRequestReferenceNo());
			if (existTranDatas.isEmpty()) {
				List<ExcessMaster> excessMasterDatas = excessRepo
						.findAllByCompanyIdAndProductIdOrderByExcessId(companyId, productId);
				if (!excessMasterDatas.isEmpty()) {

					for (EnquiryDetails enquiry : enquiryDetailsList) {
						List<ExcessMaster> em = excessMasterDatas.parallelStream()
								.filter(f -> f.getSectionId().equalsIgnoreCase(enquiry.getSectionId())
										&& f.getCoverId().equals(enquiry.getCoverId()))
								.toList();
						if (!em.isEmpty()) {
							ExcessMaster mas = em.get(0);
							ExcessTransactionDetails map = mapper.map(mas, ExcessTransactionDetails.class);
							map.setRiskId(Integer.parseInt(enquiry.getRiskId()));
							map.setLocationId(enquiry.getLocationId());
							map.setRequestReferenceNo(req.getRequestReferenceNo());
							excessTranDetailsList.add(map);
						}
					}

				}
			} else {
				List<ExcessTransactionDetails> copyList= existTranDatas;
				List<ExcessDetailsReq> excessDetails = req.getExcessDetails();
				for (EnquiryDetails enquiry : enquiryDetailsList) {
					List<ExcessTransactionDetails> exist = existTranDatas.stream()
							.filter(f -> f.getSectionId().equalsIgnoreCase(enquiry.getSectionId())
									&& f.getCoverId().equals(enquiry.getCoverId())
									&& f.getLocationId().equalsIgnoreCase(enquiry.getLocationId())
									&& String.valueOf(f.getRiskId()).equals(enquiry.getRiskId()))
							.toList();

					if (exist.isEmpty()) {
						List<ExcessMaster> excessMasterDatas = excessRepo
								.findAllByCompanyIdAndProductIdOrderByExcessId(companyId, productId);
						if (!excessMasterDatas.isEmpty()) {
							List<ExcessMaster> em = excessMasterDatas.parallelStream()
									.filter(f -> f.getSectionId().equalsIgnoreCase(enquiry.getSectionId())
											&& f.getCoverId().equals(enquiry.getCoverId()))
									.toList();
							if (!em.isEmpty()) {
								ExcessMaster mas = em.get(0);
								ExcessTransactionDetails map = mapper.map(mas, ExcessTransactionDetails.class);
								map.setRiskId(Integer.parseInt(enquiry.getRiskId()));
								map.setLocationId(enquiry.getLocationId());
								map.setRequestReferenceNo(req.getRequestReferenceNo());
								excessTranDetailsList.add(map);
							}
						}
					} else {
						ExcessTransactionDetails tran = exist.get(0);
						if (!excessDetails.isEmpty()) {
							List<ExcessDetailsReq> extractDatasFromReq = excessDetails.stream()
									.filter(f -> f.getSectionId().equalsIgnoreCase(enquiry.getSectionId())
											&& f.getCoverId().equals(enquiry.getCoverId())
											&& f.getLocationId().equalsIgnoreCase(enquiry.getLocationId())
											&& String.valueOf(f.getRiskId()).equals(enquiry.getRiskId()))
									.toList();
							if (!extractDatasFromReq.isEmpty()) {
								ExcessDetailsReq extractexcess = extractDatasFromReq.get(0);
								tran.setExcessAmount(
										StringUtils.isBlank(extractexcess.getExcessAmount()) ? tran.getExcessAmount()
												: Double.valueOf(extractexcess.getExcessAmount()));
								tran.setExcessPercentage(StringUtils.isBlank(extractexcess.getExcessPercentage())
										? tran.getExcessPercentage()
										: Integer.parseInt(extractexcess.getExcessPercentage()));
								tran.setExcessDescription(StringUtils.isBlank(extractexcess.getExcessDescription())
										? tran.getExcessDescription()
										: extractexcess.getExcessDescription());
							}

						}
						copyList.remove(tran);
					}
                      
				}
				
				if(!copyList.isEmpty())
					toDelete.addAll(copyList);
			}
			if (!CollectionUtils.isEmpty(toDelete))
				excesstranRepo.deleteAll(toDelete);

			excesstranRepo.saveAllAndFlush(excessTranDetailsList);

			res.setMessage("Success");
			res.setIsError(false);
			res.setCommonResponse("success");

		} catch (Exception e) {
			e.printStackTrace();
			res.setMessage("Failed");
			res.setIsError(true);
		}
		return res;
	}

	// Get All Transaction Details Based on the RequestReferenceNumber

	@Override
	public CommonRes getExcessTransactionDetails(ExcessTransactionReq req) {
		CommonRes commonRes = new CommonRes();
		List<ExcessTransactionRes> res = new ArrayList<ExcessTransactionRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {

			List<ExcessTransactionDetails> excessTranDetails = excesstranRepo
					.findByRequestReferenceNo(req.getRequestReferenceNo());
			if (!CollectionUtils.isEmpty(excessTranDetails)) {
				for(ExcessTransactionDetails e:excessTranDetails) {
					ExcessTransactionRes et = mapper.map(e, ExcessTransactionRes.class);
					res.add(et);
				}
				commonRes.setCommonResponse(res);
			} else {
				List<FactorRateRequestDetails> factorDatas = factorRepo
						.findByRequestReferenceNo(req.getRequestReferenceNo());
				if (!factorDatas.isEmpty()) {
					String companyId = factorDatas.get(0).getCompanyId();
					String productId = String.valueOf(factorDatas.get(0).getProductId());
					List<ExcessMaster> master = excessRepo.findAllByCompanyIdAndProductIdOrderByExcessId(companyId,
							productId);
					List<EnquiryDetails> enquiryDetailsList = factorDatas.stream().distinct().map(data -> {
						EnquiryDetails enquiry = new EnquiryDetails();
						enquiry.setLocationId(String.valueOf(data.getLocationId()));
						enquiry.setSectionId(String.valueOf(data.getSectionId()));
						enquiry.setCoverId(String.valueOf(data.getCoverId()));
						enquiry.setRiskId(String.valueOf(data.getVehicleId()));
						return enquiry;
					}).distinct().collect(Collectors.toList());
					
					for(EnquiryDetails enquiry:enquiryDetailsList) {
						List<ExcessMaster> matched = master.stream().filter(m-> m.getSectionId().equalsIgnoreCase(enquiry.getSectionId()) && m.getCoverId().equalsIgnoreCase(enquiry.getCoverId())).toList();
						if (!matched.isEmpty()) {
							for (ExcessMaster mas : matched) {
								// ExcessMaster mas = matched.get(0);
								ExcessTransactionRes singleRes = mapper.map(mas, ExcessTransactionRes.class);
								res.add(singleRes);
							}
						}
					}
					
				}
				commonRes.setCommonResponse(res);
			}
			if (CollectionUtils.isNotEmpty(res)) {
				commonRes.setMessage("Success");
				commonRes.setIsError(false);
			} else {
				commonRes.setMessage("No data");
				commonRes.setIsError(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			commonRes.setMessage("Failed");
			commonRes.setIsError(true);
		}
				return commonRes;
	}

	
	@Transactional
	@Override
	public CommonRes insertExess(ExcessInsertDetailsReq req) {
		CommonRes commonRes = new CommonRes();
		try {
			
			List<ExcessInsertReq> excessReqList = req.getExcessList();
			String requestRefNo = excessReqList.get(0).getRequestReferenceNo();
			String sectionId = excessReqList.get(0).getSectionId();
			if(StringUtils.isBlank(requestRefNo))
			{
				commonRes.setMessage("Please Provide RequsetReference NO.");
	            commonRes.setIsError(true);
	            return commonRes;
			}
			if (excessReqList == null || excessReqList.isEmpty()) {
	            commonRes.setMessage("Please Provide Excess Data");
	            commonRes.setIsError(true);
	            return commonRes;
	        }
			boolean inconsistentRef = excessReqList.stream()
		            .anyMatch(r -> !requestRefNo.equals(r.getRequestReferenceNo()));
		        if (inconsistentRef) {
		            commonRes.setMessage("All records must have the same requestReferenceNo");
		            commonRes.setIsError(true);
		            return commonRes;
		        }
			 
			List<ExcessTransactionDetails> existing  = excesstranRepo.findByRequestReferenceNoAndSectionId(requestRefNo,sectionId);
			 if (existing != null && !existing.isEmpty()) {
			excesstranRepo.deleteAll(existing);
		     }
			     List<ExcessTransactionDetails> collect = excessReqList.stream().map(reqItem-> {
				 ExcessTransactionDetails entity = new ExcessTransactionDetails();
				 entity.setRequestReferenceNo(reqItem.getRequestReferenceNo());
				 entity.setExcessId(reqItem.getExcessId()==null ? 0 : reqItem.getExcessId());
				 entity.setProductId(StringUtils.isBlank(reqItem.getProductId())? "":reqItem.getProductId());
				 entity.setSectionId(StringUtils.isBlank(reqItem.getSectionId())? "":reqItem.getSectionId());
				 entity.setCoverId(StringUtils.isBlank(reqItem.getCoverId())? "":reqItem.getCoverId());
				 entity.setExcessPercentage(reqItem.getExcessPercentage()==null ? 0 : reqItem.getExcessPercentage());
				 entity.setExcessAmount(reqItem.getExcessAmount()==null ? 0.0 : reqItem.getExcessAmount());
				 entity.setExcessDescription(StringUtils.isBlank(reqItem.getExcessDescription())? "":reqItem.getExcessDescription());
				 entity.setCurrency(StringUtils.isBlank(reqItem.getCurrency())? "":reqItem.getCurrency());
				 entity.setEntryDate(reqItem.getEntryDate()==null ? null : reqItem.getEntryDate());
				 entity.setCreatedBy(StringUtils.isBlank(reqItem.getCreatedBy())? "":reqItem.getCreatedBy());
				 entity.setRegulatoryCode(StringUtils.isBlank(reqItem.getRegulatoryCode())? "":reqItem.getRegulatoryCode());
				 entity.setCoreAppCode(StringUtils.isBlank(reqItem.getCoreAppCode())? "":reqItem.getCoreAppCode());
				 entity.setBranchCode(StringUtils.isBlank(reqItem.getBranchCode())? "":reqItem.getBranchCode());
				 entity.setStatus(StringUtils.isBlank(reqItem.getStatus())? "":reqItem.getStatus());
				 entity.setCoverName(StringUtils.isBlank(reqItem.getCoverName())? "":reqItem.getCoverName());
				 entity.setLocationId(StringUtils.isBlank(reqItem.getLocationId())? "":reqItem.getLocationId());
				 entity.setRiskId(reqItem.getRiskId()==null? 0:reqItem.getRiskId());
				 return entity;
			 }).collect(Collectors.toList());
			     excesstranRepo.saveAllAndFlush(collect);
			     commonRes.setMessage("Excess records processed successfully");
			     commonRes.setIsError(false);
			     commonRes.setCommonResponse("success");
		}catch (Exception e) {
			e.printStackTrace();
			commonRes.setMessage("Failed");
			commonRes.setIsError(true);
		}
		
		return commonRes;
	}

}
