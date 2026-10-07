package com.maan.eway.claimintimation.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.claimintimation.dto.DocumentInfoRequest;
import com.maan.eway.claimintimation.dto.DocumentInfoResponse;
import com.maan.eway.claimintimation.dto.FnolIntimationRequest;
import com.maan.eway.claimintimation.dto.FnolIntimationResponse;
import com.maan.eway.claimintimation.dto.GetIntimationReq;
import com.maan.eway.claimintimation.dto.ThirdPartyInfoRequest;
import com.maan.eway.claimintimation.dto.ThirdPartyInfoResponse;
import com.maan.eway.claimintimation.entity.DocumentInfo;
import com.maan.eway.claimintimation.entity.DocumentInfoRepository;
import com.maan.eway.claimintimation.entity.FnolIntimation;
import com.maan.eway.claimintimation.entity.FnolIntimationRepository;
import com.maan.eway.claimintimation.entity.ThirdPartyInfo;
import com.maan.eway.claimintimation.entity.ThirdPartyInfoRepository;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.SequenceGenerateRes;
import com.maan.eway.common.service.SequenceGenerateService;
import com.maan.eway.error.Error;

@Service
public class FnolIntimationServiceImpl implements FnolIntimationService {

	@Autowired
	private FnolIntimationRepository fnolRepository;
	@Autowired
	private ThirdPartyInfoRepository thirdPartyRepo;
	@Autowired
	private DocumentInfoRepository documentRepo;
	@Autowired
	private SequenceGenerateService seqGenNoService;
	Gson json = new Gson();
	private Logger log = LogManager.getLogger(FnolIntimationServiceImpl.class);

	@Override
	public List<Error> validateFnolIntimation(FnolIntimationRequest req) {
		List<Error> errorList = new ArrayList<>();

		try {
			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("1", "CompanyId", "CompanyId is required"));
			}
			if (StringUtils.isBlank(req.getProductId())) {
				errorList.add(new Error("1", "ProductId", "ProductId is required"));
			}
			if (StringUtils.isBlank(req.getRequestReferenceNo())) {
				errorList.add(new Error("1", "RequestReferenceNo", "RequestReferenceNo is required"));
			}
			if (StringUtils.isBlank(req.getClaimType())) {
				errorList.add(new Error("1", "ClaimType", "ClaimType is required"));
			}
			if (StringUtils.isBlank(req.getClaimCategory())) {
				errorList.add(new Error("1", "ClaimCategory", "ClaimCategory is required"));
			}
			if (StringUtils.isBlank(req.getPartyType())) {
				errorList.add(new Error("1", "PartyType", "PartyType is required"));
			}
			if (StringUtils.isBlank(req.getAtFault())) {
				errorList.add(new Error("1", "AtFault", "AtFault is required"));
			}
			if (req.getLossDate() == null) {
				errorList.add(new Error("1", "LossDate", "LossDate is required"));
			}
			if (StringUtils.isBlank(req.getLossLocation())) {
				errorList.add(new Error("1", "LossLocation", "LossLocation is required"));
			}
			if (StringUtils.isBlank(req.getLossDesc())) {
				errorList.add(new Error("1", "LossDesc", "LossDesc is required"));
			}
			if (StringUtils.isBlank(req.getCode())) {
				errorList.add(new Error("1", "Code", "Code is required"));
			}
			if (req.getContactPersonMobileNo() == null) {
				errorList.add(new Error("1", "ContactPersonMobileNo", "ContactPersonMobileNo is required"));
			}
			if (StringUtils.isBlank(req.getPoliceStation())) {
				errorList.add(new Error("1", "PoliceStation", "PoliceStation is required"));
			}
			if (StringUtils.isBlank(req.getPoliceReportNo())) {
				errorList.add(new Error("1", "PoliceReportNo", "PoliceReportNo is required"));
			}
			if (req.getAccidentNo() == null) {
				errorList.add(new Error("1", "AccidentNo", "AccidentNo is required"));
			}
			if (StringUtils.isBlank(req.getThirdPartyInvolved())) {
				errorList.add(new Error("1", "ThirdPartyInvolved", "ThirdPartyInvolved is required"));
			}

			// Validate ThirdPartyList if not empty
			if (req.getThirdPartyList() != null && !req.getThirdPartyList().isEmpty()) {
				int index = 0;
				for (ThirdPartyInfoRequest third : req.getThirdPartyList()) {
					if (StringUtils.isBlank(third.getDriverName())) {
						errorList.add(
								new Error("1", "ThirdPartyList[" + index + "].DriverName", "DriverName is required"));
					}
					if (StringUtils.isBlank(third.getLicenseNo())) {
						errorList.add(
								new Error("1", "ThirdPartyList[" + index + "].LicenseNo", "LicenseNo is required"));
					}
					if (StringUtils.isBlank(third.getNationality())) {
						errorList.add(
								new Error("1", "ThirdPartyList[" + index + "].Nationality", "Nationality is required"));
					}
					if (third.getMobileNo() == null) {
						errorList.add(new Error("1", "ThirdPartyList[" + index + "].MobileNo", "MobileNo is required"));
					}
					if (StringUtils.isBlank(third.getDriverLiability())) {
						errorList.add(new Error("1", "ThirdPartyList[" + index + "].DriverLiability",
								"DriverLiability is required"));
					}
					if (StringUtils.isBlank(third.getVehicleNumber())) {
						errorList.add(new Error("1", "ThirdPartyList[" + index + "].VehicleNumber",
								"VehicleNumber is required"));
					}
					if (StringUtils.isBlank(third.getMake())) {
						errorList.add(new Error("1", "ThirdPartyList[" + index + "].Make", "Make is required"));
					}
					if (StringUtils.isBlank(third.getModel())) {
						errorList.add(new Error("1", "ThirdPartyList[" + index + "].Model", "Model is required"));
					}
					if (third.getPlateNo() == null) {
						errorList.add(new Error("1", "ThirdPartyList[" + index + "].PlateNo", "PlateNo is required"));
					}
					if (StringUtils.isBlank(third.getThirdPartyReference())) {
						errorList.add(new Error("1", "ThirdPartyList[" + index + "].ThirdPartyReference",
								"ThirdPartyReference is required"));
					}
					if (StringUtils.isBlank(third.getThirdPartyType())) {
						errorList.add(new Error("1", "ThirdPartyList[" + index + "].ThirdPartyType",
								"ThirdPartyType is required"));
					}
					index++;
				}
			}

			// Validate DocumentList if not empty
			if (req.getDocumentList() != null && !req.getDocumentList().isEmpty()) {
				int index = 0;
				for (DocumentInfoRequest doc : req.getDocumentList()) {
					if (StringUtils.isBlank(doc.getDocument())) {
						errorList.add(new Error("1", "DocumentList[" + index + "].Document", "Document is required"));
					}
					if (StringUtils.isBlank(doc.getFormat())) {
						errorList.add(new Error("1", "DocumentList[" + index + "].Format", "Format is required"));
					}
					if (StringUtils.isBlank(doc.getName())) {
						errorList.add(new Error("1", "DocumentList[" + index + "].Name", "Name is required"));
					}
					if (StringUtils.isBlank(doc.getDocumentType())) {
						errorList.add(
								new Error("1", "DocumentList[" + index + "].DocumentType", "DocumentType is required"));
					}
					index++;
				}
			}

		} catch (Exception e) {
			log.error("Validation Exception", e);
			errorList.add(new Error("EX", "Exception", "Unexpected validation error occurred"));
		}

		return errorList;
	}

	@Override
	@Transactional
	public CommonRes saveFnolIntimation(FnolIntimationRequest req) {
		CommonRes res = new CommonRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<ThirdPartyInfoRequest> thirdPartyList = req.getThirdPartyList();
			List<DocumentInfoRequest> documentList = req.getDocumentList();
			String intimationNo = "";
			if (StringUtils.isBlank(req.getIntimationNo())) {
				intimationNo = generateIntimationNo(req.getCompanyId(), req.getProductId());
			} else {
				intimationNo = req.getIntimationNo();
				fnolRepository.deleteAllByIntimationNo(intimationNo);
			}

			Date entryDate = new Date();
			FnolIntimation fnol = mapper.map(req, FnolIntimation.class);
			fnol.setEntryDate(entryDate);
			fnol.setLossDate(req.getLossDate());
			fnol.setIntimationNo(intimationNo);
			fnolRepository.saveAndFlush(fnol);

			if (!thirdPartyList.isEmpty()) {
				List<ThirdPartyInfo> tpiList = new ArrayList<ThirdPartyInfo>();
				for (ThirdPartyInfoRequest t : thirdPartyList) {
					ThirdPartyInfo tpi = mapper.map(t, ThirdPartyInfo.class);
					tpi.setEntryDate(entryDate);
					tpi.setIntimationNo(intimationNo);
					tpiList.add(tpi);
				}
				thirdPartyRepo.saveAllAndFlush(tpiList);
			}

			if (!documentList.isEmpty()) {
				List<DocumentInfo> diList = new ArrayList<DocumentInfo>();
				for (DocumentInfoRequest d : documentList) {
					DocumentInfo di = mapper.map(d, DocumentInfo.class);
					di.setEntryDate(entryDate);
					di.setIntimationNo(intimationNo);
					diList.add(di);
				}
				documentRepo.saveAllAndFlush(diList);
			}

			res.setIsError(false);
			res.setMessage("Successfully Saved");
			res.setCommonResponse(Map.of("IntimationNo", intimationNo));
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			res.setIsError(true);
			res.setMessage("Exception occurs");
			res.setCommonResponse(Collections.emptyList());
			return res;
		}
	}

	public String generateIntimationNo(String companyId, String productId) {
		String intimationNo = null;
		try {
			SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
			generateSeqReq.setInsuranceId(companyId);
			generateSeqReq.setProductId(productId);
			generateSeqReq.setType("11");
			generateSeqReq.setTypeDesc("INTIMATION_NO");
			SequenceGenerateRes sequenceRes = seqGenNoService.generateSequence(generateSeqReq);
			intimationNo = sequenceRes.getGeneratedValue();
			System.out.println("Generated Sequence --> " + intimationNo);
			return intimationNo;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public CommonRes getByIntimationNo(GetIntimationReq req) {
		CommonRes res = new CommonRes();
		FnolIntimationResponse fnolIntimation = new FnolIntimationResponse();
		List<ThirdPartyInfoResponse> thirdPartyInfos = new ArrayList<ThirdPartyInfoResponse>();
		List<DocumentInfoResponse> documentInfos = new ArrayList<DocumentInfoResponse>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			FnolIntimation fnol = fnolRepository.findByIntimationNo(req.getIntimationNo());
			if (fnol != null) {
				List<ThirdPartyInfo> tpiList = fnol.getThirdParties();
				List<DocumentInfo> diList = fnol.getDocuments();
				fnolIntimation = mapper.map(fnol, FnolIntimationResponse.class);
				if (!tpiList.isEmpty()) {
					thirdPartyInfos = tpiList.stream().map(m -> mapper.map(m, ThirdPartyInfoResponse.class))
							.collect(Collectors.toList());
				}
				if (!diList.isEmpty()) {
					documentInfos = diList.stream().map(m -> mapper.map(m, DocumentInfoResponse.class))
							.collect(Collectors.toList());
				}
				fnolIntimation.setThirdPartyList(thirdPartyInfos);
				fnolIntimation.setDocumentList(documentInfos);
				res.setMessage("Success");
				res.setCommonResponse(fnolIntimation);
			} else {
				res.setMessage("No data for you given Intimation_no");
				res.setCommonResponse(fnol);
			}
			res.setIsError(false);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			res.setIsError(true);
			res.setMessage("Exception occurs");
			res.setCommonResponse(Collections.emptyList());
			return res;
		}
	}

	// Get all
	@Override
	public CommonRes getIntimationDetailsByPolicyNo(String policyNo) {
		CommonRes res = new CommonRes();
		List<FnolIntimationResponse> fnolIntimationList = new ArrayList<FnolIntimationResponse>();
		List<ThirdPartyInfoResponse> thirdPartyInfos = new ArrayList<ThirdPartyInfoResponse>();
		List<DocumentInfoResponse> documentInfos = new ArrayList<DocumentInfoResponse>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<FnolIntimation> getAll = fnolRepository.findByPolicyNo(policyNo);
			if (!getAll.isEmpty()) {
				for (FnolIntimation fnol : getAll) {
					if (fnol != null) {
						List<ThirdPartyInfo> tpiList = fnol.getThirdParties();
						List<DocumentInfo> diList = fnol.getDocuments();
						FnolIntimationResponse fnolIntimation = mapper.map(fnol, FnolIntimationResponse.class);
						if (!tpiList.isEmpty()) {
							thirdPartyInfos = tpiList.stream().map(m -> mapper.map(m, ThirdPartyInfoResponse.class))
									.collect(Collectors.toList());
						}
						if (!diList.isEmpty()) {
							documentInfos = diList.stream().map(m -> mapper.map(m, DocumentInfoResponse.class))
									.collect(Collectors.toList());
						}
						fnolIntimation.setThirdPartyList(thirdPartyInfos);
						fnolIntimation.setDocumentList(documentInfos);
						fnolIntimationList.add(fnolIntimation);
					}
				}
				res.setMessage("Success");
				res.setCommonResponse(fnolIntimationList);
			} else {
				res.setMessage("No data for you given Intimation_no");
				res.setCommonResponse(Collections.emptyList());
			}
			res.setIsError(false);
			res.setCommonResponse(getAll);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			res.setIsError(true);
			res.setMessage("Exception occurs");
			res.setCommonResponse(Collections.emptyList());
			return res;
		}

	}
}
