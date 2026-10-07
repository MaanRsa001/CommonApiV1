package com.maan.eway.endorsment.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.gson.Gson;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.common.req.CoverIdsReq;
import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.common.req.EservieMotorDetailsViewRes;
import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.req.VehicleIdsReq;
import com.maan.eway.common.req.ViewQuoteReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.NewQuoteRes;
import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.common.service.QuoteService;
import com.maan.eway.common.service.impl.QuoteThreadServiceImpl;
import com.maan.eway.endorsment.request.NonMotEndtReq;
import com.maan.eway.endorsment.request.NonMotorComRes;
import com.maan.eway.endorsment.request.RemoveLocationReq;
import com.maan.eway.endorsment.service.RemoveLocationService;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.req.FactorRateDetailsGetReq;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.service.FactorRateRequestDetailsService;
import com.maan.eway.service.impl.CalculatorEngineService;
@Service
public class RemoveLocationServiceImpl implements RemoveLocationService {

	@Autowired
	private EServiceSectionDetailsRepository secRepo;

	private Logger log = LogManager.getLogger(RemoveLocationServiceImpl.class);
	
	Gson json = new Gson();
	
	@Autowired
	private CalculatorEngineService calcService;

	@Autowired
	private  FactorRateRequestDetailsService factorService;
	
	@Autowired
	private  QuoteService entityService;
	
	@Autowired
	private EserviceBuildingDetailsRepository eBuildingRepo;
	
	@Autowired
	private EserviceCommonDetailsRepository eCommonRepo;
	 
	@Autowired
	private QuoteThreadServiceImpl quoteThreadimpl;
	

	@Value(value = "${nonMotor.req}")
	private String nonMotor;
	
	@Override
	public CommonRes cancelPolicyLocation(RemoveLocationReq req, String tokens) {
		try {
			NonMotorComRes nonreq=new NonMotorComRes();
			nonreq.setRequestReferenceNo(req.getRequestReferenceNo());
			nonreq.setNonMotEndtReq(req.getNonMotEndtReq());
			doNonmotorProcess(nonreq,tokens);
			List<EserviceSectionDetails> secList = secRepo.findByRequestReferenceNoAndCompanyId(req.getRequestReferenceNo(), req.getCompanyid());
			if (secList != null && !secList.isEmpty()) {
				CalcEngine calcreq = new CalcEngine();
				calcreq.setRequestReferenceNo(req.getRequestReferenceNo());
				calcreq.setEffectiveDate(req.getEffectiveDate());
				calcreq.setCoverModification("Y");
				Integer n=Integer.valueOf(req.getLocationId());
				List<EserviceSectionDetails> sectionsave = secList.stream()
			    .filter(t -> t.getLocationId().equals(n))
			    .collect(Collectors.toList());
				sectionsave.forEach(t -> t.setStatus("D"));
				secRepo.saveAll(sectionsave);
				if("A".equalsIgnoreCase(sectionsave.get(0).getProductType())) {
					List<EserviceBuildingDetails> ebuilding = eBuildingRepo.findByRequestReferenceNoAndLocationId(req.getRequestReferenceNo(), n);
					ebuilding.forEach(t -> t.setStatus("D"));
					eBuildingRepo.saveAll(ebuilding);
				}
				else {
					List<EserviceCommonDetails> ecom = eCommonRepo.findByRequestReferenceNoAndLocationId(req.getRequestReferenceNo(), n);
					ecom.forEach(t -> t.setStatus("D"));
					eCommonRepo.saveAll(ecom);
				}
				
				List<EserviceMotorDetailsSaveRes> calc = calcService.getCalc(calcreq, tokens);
				if (calc != null && !calc.isEmpty()) {
					NewQuoteReq buyreq = new NewQuoteReq();
					buyreq.setRequestReferenceNo(req.getRequestReferenceNo());
					buyreq.setProductId(secList.get(0).getProductId());
					buyreq.setCreatedBy(secList.get(0).getCreatedBy());
					buyreq.setManualReferralYn("N");
					List<VehicleIdsReq> vehicles = new ArrayList<VehicleIdsReq>();
					List<EservieMotorDetailsViewRes> viewCalc = new ArrayList<>();
					FactorRateDetailsGetReq viewCalcReq = new FactorRateDetailsGetReq();
					viewCalcReq.setProductId(req.getProductid());
					viewCalcReq.setRequestReferenceNo(req.getRequestReferenceNo());
					viewCalcReq.setInsuranceId(req.getCompanyid());
					Map<List<EservieMotorDetailsViewRes>, List<Cover>> res = factorService
							.getFactorRateRequestDetails(viewCalcReq, "");
					if (res != null && !res.isEmpty()) {
						Entry<List<EservieMotorDetailsViewRes>, List<com.maan.eway.res.calc.Cover>> entry = res
								.entrySet().iterator().next();
						viewCalc = entry.getKey();
					}
					Set<String> findlocationid = viewCalc.stream().map(EservieMotorDetailsViewRes::getLocationId).distinct().collect(Collectors.toSet());
					findlocationid.remove(req.getLocationId());
					for (String data : findlocationid) {
						String LocationId = data;
						List<EservieMotorDetailsViewRes> locFilter = viewCalc.stream().filter(o -> o.getLocationId().equals(data)).collect(Collectors.toList());
						Set<String> findVehicleid = locFilter.stream().map(EservieMotorDetailsViewRes::getVehicleId)
								.distinct().collect(Collectors.toSet());
						for (String vehicle : findVehicleid) {
							List<EservieMotorDetailsViewRes> vehicleFilter = locFilter.stream()
									.filter(o -> o.getVehicleId().equals(vehicle)).collect(Collectors.toList());

							for (EservieMotorDetailsViewRes motors : vehicleFilter) {
								List<CoverIdsReq> covers = new ArrayList<CoverIdsReq>();

								List<Cover> coverList = motors.getCoverList();
								List<Cover> distinctSections = coverList.stream().filter(distinctByKey(c -> c.getSectionId())).collect(Collectors.toList());

								for (Cover ds : distinctSections) {

									VehicleIdsReq v = new VehicleIdsReq();

									v.setVehicleId(Integer.parseInt(vehicle));
									v.setLocationId(Integer.parseInt(LocationId));
									v.setSectionId(ds.getSectionId());

									for (Cover cover : coverList) {

										if ("Y".equals(cover.getUserOpt())
												&& ds.getSectionId().equals(cover.getSectionId())) {
											String isSubCover = cover.getIsSubCover();

											if ("Y".equals(isSubCover)) {
												List<Cover> subcovers = cover.getSubcovers().stream()
														.filter(f -> "Y".equals(f.getUserOpt()))
														.collect(Collectors.toList());
												for (Cover c : subcovers) {
													CoverIdsReq r = new CoverIdsReq();
													r.setSubCoverYn(isSubCover);
													r.setCoverId(Integer.parseInt(c.getCoverId()));
													r.setSubCoverId(c.getSubCoverId());
													covers.add(r);
												}
											} else {
												CoverIdsReq r = new CoverIdsReq();

												r.setSubCoverYn(isSubCover);
												r.setCoverId(Integer.parseInt(cover.getCoverId()));
												r.setSubCoverId(null);
												covers.add(r);
											}

										}
									}
									v.setCoverIdList(covers);
									vehicles.add(v);
								}

							}

						}
					}
					buyreq.setVehicleIdsList(vehicles);
					buyreq.setSectionId(null);
					buyreq.setReferralRemarks("");
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
					System.out.println("Buy Policy Request -->" + objectMapper.writeValueAsString(buyreq));
					CommonRes generateNewQuote = entityService.generateNewQuote(buyreq);		    
					if(!generateNewQuote.getIsError()) {
						NewQuoteRes view=(NewQuoteRes) generateNewQuote.getCommonResponse();
						ViewQuoteReq requestView=new ViewQuoteReq();
						requestView.setQuoteNo(view.getQuoteNo());
						ViewQuoteRes viewQuoteDetails = entityService.viewQuoteDetails(requestView);
						quoteThreadimpl.QuoteGenerateUwsysIdAndPolIdxID(buyreq, view.getQuoteNo());
						generateNewQuote.setCommonResponse(viewQuoteDetails);
					}
					return generateNewQuote;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void doNonmotorProcess(NonMotorComRes req, String tokens) {
	    try {
	    	String url=nonMotor;
	    	
	//        String url = "http://localhost:8085/api/slide/setNonMotor";

	        RestTemplate restTemplate = new RestTemplate();

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.set("Authorization", "Bearer " + tokens);

	        NonMotEndtReq endtReq = req.getNonMotEndtReq();

	        // 🔹 Inner map from object
	        Map<String, Object> endorsementDetails = new HashMap<>();
	        endorsementDetails.put("EndorsementDate", formatDate(endtReq.getEndorsementDate()));
	        endorsementDetails.put("EndorsementRemarks", endtReq.getEndorsementRemarks());
	        endorsementDetails.put("EndorsementEffectiveDate", formatDate(endtReq.getEndorsementEffdate()));
	        endorsementDetails.put("OrginalPolicyNo", endtReq.getOriginalPolicyNo());
	        endorsementDetails.put("EndtPrevPolicyNo", endtReq.getEndtPrevPolicyNo());
	        endorsementDetails.put("EndtPrevQuoteNo", endtReq.getEndtPrevQuoteNo());
	        endorsementDetails.put("EndtCount", endtReq.getEndtCount());
	        endorsementDetails.put("EndtStatus", endtReq.getEndtStatus());
	        endorsementDetails.put("IsFinanceEndt", endtReq.getIsFinaceYn());
	        endorsementDetails.put("EndtCategoryDesc", endtReq.getEndtCategDesc());
	        endorsementDetails.put("EndorsementType", "891");
	        endorsementDetails.put("EndorsementTypeDesc", endtReq.getEndorsementTypeDesc());
	        endorsementDetails.put("CoverModification", endtReq.getCoverModification());
	        endorsementDetails.put("PolicyNo", endtReq.getPolicyNo());

	        // 🔹 Main map
	        Map<String, Object> request = new HashMap<>();
	        request.put("RequestReferenceNo", req.getRequestReferenceNo());
	        request.put("EndorsementDetails", endorsementDetails);

	        System.out.println("Request ---> " + request);
	        
	        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

	        ResponseEntity<String> response =
	                restTemplate.postForEntity(url, entity, String.class);

	        System.out.println("Response ---> " + response.getBody());

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	public  <T> java.util.function.Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> uniqueMap = new ConcurrentHashMap<>();
		return t -> uniqueMap.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	private String formatDate(Date date) {
	    if (date == null) return null;
	    return new SimpleDateFormat("dd/MM/yyyy").format(date);
	}
}
