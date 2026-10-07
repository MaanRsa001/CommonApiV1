package com.maan.eway.salesLead.service.impl;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.maan.eway.bean.EserviceLeadDetails;
import com.maan.eway.bean.IplcmsListItemValue;
import com.maan.eway.bean.RegionMaster;
import com.maan.eway.bean.StateMaster;
import com.maan.eway.common.req.GetCustomerDetailsReq;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.service.impl.EserviceCustomerDetailsServiceImpl;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.jasper.res.JasperDocumentRes;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.master.req.RegionMasterDropDownReq;
import com.maan.eway.master.req.StateMasterDropDownReq;
import com.maan.eway.master.service.impl.CountryMasterServiceImpl;
import com.maan.eway.master.service.impl.RegionMasterServiceImpl;
import com.maan.eway.master.service.impl.StateMasterServiceImpl;
import com.maan.eway.repository.EserviceLeadDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.RegionMasterRepository;
import com.maan.eway.repository.StateMasterRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.salesLead.Repository.EnquiryDetailsRepository;
import com.maan.eway.salesLead.Repository.IpclmsFileUploadDetailsRepository;
import com.maan.eway.salesLead.Repository.IplcmsListItemValueRepository;
import com.maan.eway.salesLead.Repository.LeadContactPersonRepository;
import com.maan.eway.salesLead.Repository.LeadInformationRepository;
import com.maan.eway.salesLead.Repository.QuoteInformationRepository;
import com.maan.eway.salesLead.bean.EnquiryDetails;
import com.maan.eway.salesLead.bean.IpclmsFileUploadDetails;
import com.maan.eway.salesLead.bean.LeadContactPerson;
import com.maan.eway.salesLead.bean.LeadInformation;
import com.maan.eway.salesLead.custom.SalesLeadCustomRepositry;
import com.maan.eway.salesLead.req.EnquiryDetailsDTO;
import com.maan.eway.salesLead.req.EserviceLeadSaveReq;
import com.maan.eway.salesLead.req.GetEnquiryDetailsReq;
import com.maan.eway.salesLead.req.GetUploadDocumentListReq;
import com.maan.eway.salesLead.req.InsertSalesReq;
import com.maan.eway.salesLead.req.LeadContactPersonReq;
import com.maan.eway.salesLead.req.SaveUploadDocumentsReq;
import com.maan.eway.salesLead.res.GetLeadDetailsRes;
import com.maan.eway.salesLead.res.GetSalesLeadRes;
import com.maan.eway.salesLead.res.GetUploadDocumentListRes;
import com.maan.eway.salesLead.service.SalesLeadService;
import com.maan.eway.thread.GetFileFromPath;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;

@Service
public class SalesLeadServiceImpl implements SalesLeadService {
	
	private Logger log = LogManager.getLogger(SalesLeadServiceImpl.class);
	
	@Autowired
	SalesLeadCustomRepositry salesLeadCustomRepo;
	
	@Autowired
	private LeadInformationRepository leadInfoRepo;
	
	@Autowired
	private LeadContactPersonRepository leadContactRepo;
	
	@Autowired
	private EnquiryDetailsRepository enquiryDetailsRepo;
	
	@Autowired
	private IplcmsListItemValueRepository iplcmsListItemValueRepo;
	
	@Autowired
	private GenerateSeqNoServiceImpl genSeqNoService ;
	
	@Autowired
	private EserviceCustomerDetailsServiceImpl custService;
	
	@Autowired
	private EserviceLeadDetailsRepository leadrepo;
	
	@Autowired
	private ListItemValueRepository listRepo;
	
	@Autowired
	private RegionMasterRepository regionMasterRepo;
	
	@Autowired
	private StateMasterRepository stateMasterRepo;
	
	@Autowired
	private HomePositionMasterRepository homePosistionRepo;
	
	@Autowired
	private PersonalInfoRepository personalInforepo;
	
	@Autowired
	private CountryMasterServiceImpl countryMasterServiceImpl;
	
	@Autowired
	private RegionMasterServiceImpl regionMasterServiceImpl;
	
	@Autowired
	private StateMasterServiceImpl stateMasterServiceImpl;
	
	@Autowired
	private LoginMasterRepository loginRepo;
	
	@Autowired
    private QuoteInformationRepository quoteInformationRepo;
	
	@Autowired
	private IpclmsFileUploadDetailsRepository ipclmsFileUploadDetailsRepo;
	
	@PersistenceContext
	private EntityManager em;
	
	private Gson gson = new Gson();
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	private final static Logger logger = LogManager.getLogger(SalesLeadServiceImpl.class);
	
	@Override
	public boolean insertLeadDetails(List<InsertSalesReq> reqList) {
		logger.info("Enter into insertSales.\n Argument ==> "+gson.toJson(reqList));
		boolean result = false;
		try {
			if(reqList!=null && reqList.size()>0) {
				reqList.forEach(req -> {
					String leadId = null,createdBy=null,updatedBy=null;
					Date updatedDate=null,entryDate=null;
					Optional<LeadInformation> exitingData = leadInfoRepo.findById(StringUtils.isBlank(req.getLeadId())?"":req.getLeadId());
					if(exitingData.isPresent()) {
						LeadInformation ed = exitingData.get();
						leadId = ed.getLeadId();
						createdBy = ed.getCreatedBy();
						updatedBy = req.getLoginId();
						entryDate = ed.getEntryDate();
						updatedDate = new Date();
					}else {
						SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
			 		 	generateSeqReq.setInsuranceId(req.getCompanyId());  
			 		 	generateSeqReq.setProductId(req.getProductId());
			 		 	generateSeqReq.setType("8");
			 		 	generateSeqReq.setTypeDesc("LEAD_REFERENCE_NO");
			 		 	leadId =  genSeqNoService.generateSeqCall(generateSeqReq);
						createdBy = req.getLoginId();
						entryDate = new Date();
					}
					Map<String,String> title = custService.getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"NAME_TITLE",req.getTitle());//listRepo.findByItemTypeAndItemCode("NAME_TITLE", req.getTitle());
					Map<String,String> gender = custService.getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"GENDER",req.getGender());// listRepo.findByItemTypeAndItemCode("GENDER", saveData.getGender());
					Map<String,String> policyHolderType = custService.getListItemLocal ("99999" , req.getBranchCode() ,"POLICY_HOLDER_TYPE",req.getPolicyHolderTypeid());//listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_TYPE",	req.getPolicyHolderType());
					Map<String,String> occupation = custService.getByOccupationIdDesc(req.getOccupation(), req.getCompanyId(),req.getProductId() , req.getBranchCode());
					Map<String,String> policyHolderTypeId = custService.getListItemLocal (req.getCompanyId(), req.getBranchCode() ,"POLICY_HOLDER_ID_TYPE",req.getPolicyHolderTypeid());// listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_ID_TYPE", req.getPolicyHolderTypeid());
					
					
					String genderDesc = Optional.ofNullable(gender).map(map -> map.get("itemDesc")).orElse("");
					String titleDesc = Optional.ofNullable(title).map(map -> map.get("itemDesc")).orElse("");
					String policyHolderTypeDesc = Optional.ofNullable(policyHolderType).map(map -> map.get("itemDesc")).orElse("");
					String occupationDesc = Optional.ofNullable(occupation).map(map -> map.get("occupationName")).orElse("");
					String policyHolderTypeIdDesc = Optional.ofNullable(policyHolderTypeId).map(map -> map.get("itemDesc")).orElse("");
					LovDropDownReq req1 = new LovDropDownReq();
					req1.setInsuranceId(req.getCompanyId());
					String countryCodeDesc = countryMasterServiceImpl.getCountryMasterDropdown(req1).stream().filter(f -> f.getCode().equalsIgnoreCase(req.getCountryCode()))
							.map(q -> q.getCodeDesc()).findFirst().orElse("");
					RegionMasterDropDownReq req2 = new RegionMasterDropDownReq();
					req2.setCountryId(req.getCountryCode());
					String regionDesc = regionMasterServiceImpl.getRegionMasterDropdown(req2).stream().filter(f -> f.getCode().equalsIgnoreCase(req.getRegionCode()))
							.map(q -> q.getCodeDesc()).findFirst().orElse("");
					StateMasterDropDownReq req3 = new StateMasterDropDownReq();
					req3.setCountryId(req.getCountryCode());
					req3.setRegionCode(req.getRegionCode());
					String regionStateDesc = stateMasterServiceImpl.getRegionStateMasterDropdown(req3).stream().filter(f -> f.getCode().equalsIgnoreCase(req.getStateCode()))
							.map(q -> q.getCodeDesc()).findFirst().orElse("");
					
					String channelName="",sectionTypeName="",propobabilityOfSuccessName="",typeOfBussinessName="",currentInsurerName="";
					if (StringUtils.isNotBlank(req.getChannelId())) {
						List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType("CUSTOMER_TYPE");
						channelName = getList.stream()
							    .filter(k -> req.getChannelId().equalsIgnoreCase(String.valueOf(k.getId())))
							    .map(k -> String.valueOf(k.getItemValue()))
							    .findFirst()
							    .orElse(null);
					}
					
					if (StringUtils.isNotBlank(req.getSectionTypeId())) {
						List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType("SECTION_TYPE");
						sectionTypeName = getList.stream()
							    .filter(k -> req.getSectionTypeId().equalsIgnoreCase(String.valueOf(k.getId())))
							    .map(k -> String.valueOf(k.getItemValue()))
							    .findFirst()
							    .orElse(null);
					}
					
					if (StringUtils.isNotBlank(req.getPropobabilityOfSuccessId())) {
						List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType("POS");
						propobabilityOfSuccessName = getList.stream()
							    .filter(k -> req.getPropobabilityOfSuccessId().equalsIgnoreCase(String.valueOf(k.getId())))
							    .map(k -> String.valueOf(k.getItemValue()))
							    .findFirst()
							    .orElse(null);
					}
					
					if (StringUtils.isNotBlank(req.getTypeOfBusinessId())) {
						List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType("TYPE_OF_BUSINESS");
						typeOfBussinessName = getList.stream()
							    .filter(k -> req.getTypeOfBusinessId().equalsIgnoreCase(String.valueOf(k.getId())))
							    .map(k -> String.valueOf(k.getItemValue()))
							    .findFirst()
							    .orElse(null);
					}
					
					if (StringUtils.isNotBlank(req.getCurrentInsurer())) {
						List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType("INS_COMP");
						currentInsurerName = getList.stream()
							    .filter(k -> req.getCurrentInsurer().equalsIgnoreCase(String.valueOf(k.getId())))
							    .map(k -> String.valueOf(k.getItemValue()))
							    .findFirst()
							    .orElse(null);
					}
					
					
					
					try {
						LeadInformation m = LeadInformation.builder()
								.leadId(leadId)
								.policyHolderType(req.getPolicyHolderTypeid())
								.policyHolderTypeDesc(policyHolderTypeDesc)
								.title(req.getTitle())
								.titleDesc(titleDesc)
								.clientName(req.getClientName())
								.gender(req.getGender())
								.genderDesc(genderDesc)
								.occupation(req.getOccupation())
								.occupationDesc(occupationDesc)
								.email(req.getEmail())
								.mobileCode(req.getMobileCode())
								.mobileNumber(req.getMobileNumber())
								.identityType(req.getIdType())
								.identityTypeDesc(policyHolderTypeIdDesc)
								.idNumber(req.getIdNumber())
								.gstIdentificationNo(req.getGstIdentificationNo())
								.preferredNotification(req.getPreferredNotification())
								.taxExcepted(req.getIsTaxExempted())
								.status(req.getStatus())
								.statusDesc(req.getStatus().equalsIgnoreCase("Y")?"Active":req.getStatus().equalsIgnoreCase("N")?"De-Active":"Pending")
								.street(req.getStreet())
								.country(req.getCountryCode())
								.countryDesc(countryCodeDesc)
								.region(req.getRegionCode())
								.regionDesc(regionDesc)
								.district(req.getStateCode())
								.districtDesc(regionStateDesc)
								.pobox(req.getPoBox())
								.intermeCode(req.getIntermediateId())
								.intermeName(req.getIntermediateName())
								.leadCreatedDate(sdf.parse(req.getLeadCreatedOn()))
								.channelId(req.getChannelId())
								.channelDesc(channelName)
								.sectionTypeId(req.getSectionTypeId())
								.sectionTypeDesc(sectionTypeName)
								.propobabilityOfSuccessId(req.getPropobabilityOfSuccessId())
								.propobabilityOfSuccessDesc(propobabilityOfSuccessName)
								.typeOfBusinessId(req.getTypeOfBusinessId())
								.typeOfBusinessDesc(typeOfBussinessName)
								.currentInsurer(req.getCurrentInsurer())
								.currentInsurerDesc(currentInsurerName)
								.entryDate(entryDate)
								.createdBy(createdBy)
								.updatedBy(updatedBy)
								.updatedDate(updatedDate)
								.companyId(req.getCompanyId())
								.productId(req.getProductId())
								.branchCode(req.getBranchCode())
								.build();
							leadInfoRepo.save(m);
							
							if(req.getLeadContactPersonReq()!=null && req.getLeadContactPersonReq().size()>0) {
								List<LeadContactPerson> cps = new ArrayList<LeadContactPerson>();
								String ioe = leadId;
								leadContactRepo.deleteByLeadId(leadId);
								AtomicInteger autoIndex = new AtomicInteger(GetLeadContactMaxSno());
								req.getLeadContactPersonReq().forEach(a -> {
									LeadContactPerson p = LeadContactPerson.builder()
											.sno(new BigDecimal(autoIndex.getAndIncrement()))
											.leadId(ioe)
											.contactType(a.getContactType())
											.contactPersonName(a.getContactPersonName())
											.emailAddress(a.getEmailAddress())
											.mobile(a.getMobileNo())
											.phone(a.getPhoneNo())
											.designation(a.getDesignation())
											.Remarks(a.getRemarks())
											.build();
									cps.add(p);
								});
								leadContactRepo.saveAll(cps);
							}
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				result = true;
			}
		logger.info("Exist into insertSales");
		}catch(Exception e) {
			logger.info("Error in insertSales ==> "+e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	private int GetLeadContactMaxSno() {
		Integer value = null;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BigDecimal> cq = cb.createQuery(BigDecimal.class);
			Root<LeadContactPerson> lpRoot = cq.from(LeadContactPerson.class);
			cq.select(cb.coalesce(cb.sum(cb.max(lpRoot.get("sno")),BigDecimal.ONE), BigDecimal.ONE));
			BigDecimal o = em.createQuery(cq).getSingleResult();
			value = o.intValue();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	@Override
	public CommonRes getSalesLead(String leadId) {
		logger.info("Enter into getAllSales.");
		CommonRes res = new CommonRes();
		List<GetSalesLeadRes> resList = new ArrayList<GetSalesLeadRes>();
		try {
			List<LeadInformation> salesList = new ArrayList<LeadInformation>();
			if(StringUtils.isBlank(leadId)) {
				salesList = leadInfoRepo.findAll();
			}else {
				LeadInformation salesById = leadInfoRepo.findById(leadId).get();
				salesList.add(salesById);
			}
			
			if(!salesList.isEmpty()) {
				salesList.forEach(k -> {
					GetSalesLeadRes m = GetSalesLeadRes.builder()
							.leadId(k.getLeadId()==null?"":k.getLeadId())
							.policyHolderTypeid(k.getPolicyHolderType()==null?"":k.getPolicyHolderType())
							.policyHolderTypeDesc(k.getPolicyHolderTypeDesc()==null?"":k.getPolicyHolderTypeDesc())
							.title(k.getTitle()==null?"":k.getTitle())
							.titleDesc(k.getTitleDesc()==null?"":k.getTitleDesc())
							.clientName(k.getClientName()==null?"":k.getClientName())
							.gender(k.getGender()==null?"":k.getGender())
							.genderDesc(k.getGenderDesc()==null?"":k.getGenderDesc())
							.occupation(k.getOccupation()==null?"":k.getOccupation())
							.occupationDesc(k.getOccupationDesc()==null?"":k.getOccupationDesc())
							.email(k.getEmail()==null?"":k.getEmail())
							.mobileCode(k.getMobileCode()==null?"":k.getMobileCode())
							.mobileNumber(k.getMobileNumber()==null?"":k.getMobileNumber())
							.idType(k.getIdentityType()==null?"":k.getIdentityType())
							.idTypeDesc(k.getIdentityTypeDesc()==null?"":k.getIdentityTypeDesc())
							.idNumber(k.getIdNumber()==null?"":k.getIdNumber())
							.gstIdentificationNo(k.getGstIdentificationNo()==null?"":k.getGstIdentificationNo())
							.preferredNotification(k.getPreferredNotification()==null?"":k.getPreferredNotification())
							.isTaxExempted(k.getTaxExcepted()==null?"":k.getTaxExcepted())
							.status(k.getStatus()==null?"":k.getStatus())
							.statusDesc(k.getStatusDesc()==null?"":k.getStatusDesc())
							.street(k.getStreet()==null?"":k.getStreet())
							.countryCode(k.getCountry()==null?"":k.getCountry())
							.countryCodeDesc(k.getCountryDesc()==null?"":k.getCountryDesc())
							.regionCode(k.getRegion()==null?"":k.getRegion())
							.regionCodeDesc(k.getRegionDesc()==null?"":k.getRegionDesc())
							.stateCode(k.getDistrict()==null?"":k.getDistrict())
							.stateCodeDesc(k.getDistrictDesc()==null?"":k.getDistrictDesc())
							.poBox(k.getPobox()==null?"":k.getPobox())
							.intermediateId(k.getIntermeCode()==null?"":k.getIntermeCode())
							.intermediateName(k.getIntermeName()==null?"":k.getIntermeName())
							.leadCreatedOn(k.getLeadCreatedDate()==null?"":sdf.format(k.getLeadCreatedDate()))
							.channelId(k.getChannelId()==null?"":k.getChannelId())
							.channelDesc(k.getChannelDesc()==null?"":k.getChannelDesc())
							.sectionTypeId(k.getSectionTypeId()==null?"":k.getSectionTypeId())
							.sectionTypeDesc(k.getSectionTypeDesc()==null?"":k.getSectionTypeDesc())
							.propobabilityOfSuccessId(k.getPropobabilityOfSuccessId()==null?"":k.getPropobabilityOfSuccessId())
							.propobabilityOfSuccessDesc(k.getPropobabilityOfSuccessDesc()==null?"":k.getPropobabilityOfSuccessDesc())
							.typeOfBusinessId(k.getTypeOfBusinessId()==null?"":k.getTypeOfBusinessId())
							.typeOfBusinessDesc(k.getTypeOfBusinessDesc()==null?"":k.getTypeOfBusinessDesc())
							.currentInsurer(k.getCurrentInsurer()==null?"":k.getCurrentInsurer())
							.currentInsurerDesc(k.getCurrentInsurerDesc()==null?"":k.getCurrentInsurerDesc())
							.entryDate(k.getEntryDate()==null?"":sdf.format(k.getEntryDate()))
							.createdBy(k.getCreatedBy()==null?"":k.getCreatedBy())
							.updatedBy(k.getUpdatedBy()==null?"":k.getUpdatedBy())
							.updatedDate(k.getUpdatedDate()==null?"":sdf.format(k.getUpdatedDate()))
							.companyId(k.getCompanyId()==null?"":k.getCompanyId())
							.productId(k.getProductId()==null?"":k.getProductId())
							.branchCode(k.getBranchCode()==null?"":k.getBranchCode())
							.leadContactPersonReq(GetLeadContactPerson(k.getLeadId()))
							.enquiryCount(String.valueOf(getEnquiryCountByLeadId(k.getLeadId())))
							.build();
					resList.add(m);
				});
				res.setMessage("SUCCESS");
				res.setCommonResponse(resList);
				res.setIsError(false);
				res.setErrorMessage(Collections.emptyList());
			}
			logger.info("Exit into getAllSales.");
			return res;
		}catch(Exception e) {
			logger.info("Error in getAllSales ==> "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	private int getEnquiryCountByLeadId(String leadId) {
		int result = 0;
		try {
			List<EnquiryDetails> enquiryList = new ArrayList<EnquiryDetails>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EnquiryDetails> cq = cb.createQuery(EnquiryDetails.class);
			Root<EnquiryDetails> edRoot = cq.from(EnquiryDetails.class);
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			cq.select(edRoot);

			Subquery<Integer> amdMax = cq.subquery(Integer.class);
			Root<EnquiryDetails> amdRoot = amdMax.from(EnquiryDetails.class);

			amdMax.select(cb.max(amdRoot.get("amendId")))
			.where(cb.equal(amdRoot.get("enquiryId"), edRoot.get("enquiryId")),
					cb.equal(amdRoot.get("leadId"), edRoot.get("leadId")));
			
			predicates.add(cb.equal(edRoot.get("leadId"), leadId));
			predicates.add(cb.equal(edRoot.get("amendId"), amdMax));
			Predicate [] predicateArray = new Predicate[predicates.size()];
			predicates.toArray(predicateArray);
			cq.where(predicateArray);
			enquiryList = em.createQuery(cq).getResultList();				
			if(enquiryList!=null && enquiryList.size()>0) {
				result = enquiryList.size();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private List<LeadContactPersonReq> GetLeadContactPerson(String leadId) {
		List<LeadContactPersonReq> result = new ArrayList<LeadContactPersonReq>();
		try {
			List<LeadContactPerson> list = leadContactRepo.findByLeadId(leadId);
			if(list!=null && list.size()>0) {
				list.forEach(k -> {
					LeadContactPersonReq m = LeadContactPersonReq.builder()
							.contactType(k.getContactType()==null?"":k.getContactType())
							.contactPersonName(k.getContactPersonName()==null?"":k.getContactPersonName())
							.emailAddress(k.getEmailAddress()==null?"":k.getEmailAddress())
							.mobileNo(k.getMobile()==null?"":k.getMobile())
							.phoneNo(k.getPhone()==null?"":k.getPhone())
							.designation(k.getDesignation()==null?"":k.getDesignation())
							.remarks(k.getRemarks()==null?"":k.getRemarks())
							.build();
					result.add(m);
				});
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public CommonRes insertEnquiry(EnquiryDetailsDTO req) {
		logger.info("Enter into insertEnquiry.\n Argument ==> "+gson.toJson(req));
		CommonRes res = new CommonRes();
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EnquiryDetails> cq = cb.createQuery(EnquiryDetails.class);
			Root<EnquiryDetails> edRoot = cq.from(EnquiryDetails.class);
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			cq.select(edRoot);				

			Subquery<Integer> amdMax = cq.subquery(Integer.class);
			Root<EnquiryDetails> amdRoot = amdMax.from(EnquiryDetails.class);
			
			amdMax.select(cb.max(amdRoot.get("amendId")))
				.where(cb.equal(amdRoot.get("enquiryId"), edRoot.get("enquiryId")),
						cb.equal(amdRoot.get("leadId"), edRoot.get("leadId")));
			
			predicates.add(cb.equal(edRoot.get("enquiryId"), req.getEnquiryId()));
			predicates.add(cb.equal(edRoot.get("amendId"), amdMax));
			Predicate [] predicateArray = new Predicate[predicates.size()];
			predicates.toArray(predicateArray);
			cq.where(predicateArray);
			
			List<EnquiryDetails> enquiryData = em.createQuery(cq).getResultList();
			
			EnquiryDetails existingList=null;
			if(!enquiryData.isEmpty()) {
				existingList = enquiryData.get(0);
			}
			
			String enquiryId = !enquiryData.isEmpty()?existingList.getEnquiryId():salesLeadCustomRepo.getMaxEnquiryId();
			
			String statusDesc = null;
			if (StringUtils.isNotBlank(req.getStatus())) {
				List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType("SU_STATUS");
				statusDesc = getList.stream()
					    .filter(k -> req.getStatus().equalsIgnoreCase(k.getItemCode()))
					    .map(k -> String.valueOf(k.getItemValue()))
					    .findFirst()
					    .orElse(null);
			}
			
			EnquiryDetails e = EnquiryDetails.builder()
					.enquiryId(enquiryId)
					.leadId(req.getLeadId())
					.enquiryDescription(req.getEnquiryDescription())
	                .lobId(req.getLobId())
	                .productId(req.getProductId())
	                .sumInsured(req.getSumInsured())
	                .suggestPremium(req.getSuggestPremium())
	                .amendId(MaxEnquiryAmendId(enquiryId,req.getLeadId()))
	                .entryDate(new Date())
	                .createdBy(req.getCreatedBy())
	                .salesRemarks(req.getSalesRemarks())
	                .uwRemarks(req.getUwRemarks())
	                .businessType(req.getBusniessType())
	                .rejectedDate(req.getRejectedDate())
	                .rejectedReason(req.getRejectedReason())
	                .status(req.getStatus())
	                .statusDesc(statusDesc)
	                .receiptOfenquiry(req.getReceiptOfenquiry())
	                .exceptedDateCommBussiness(req.getExceptedDateCommBussiness()==null?null:sdf.parse(req.getExceptedDateCommBussiness()))
	                .underwritters(req.getUnderWritters())
					.build();
			enquiryDetailsRepo.save(e);
			res.setCommonResponse(e);
			res.setMessage("SUCCESS");
			res.setIsError(false);
			res.setErrorMessage(Collections.emptyList());
		logger.info("Exist into insertEnquiry");
		return res;
		}catch(Exception e) {
			logger.info("Error in insertEnquiry ==> "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	private Integer MaxEnquiryAmendId(String enquiryId, String leadId) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
			Root<EnquiryDetails> edRoot = cq.from(EnquiryDetails.class);
			
			cq.select(cb.coalesce(cb.sum(cb.max(edRoot.get("amendId")),1), 0))
			.where(cb.equal(edRoot.get("enquiryId"), enquiryId),
					cb.equal(edRoot.get("leadId"), leadId));
			
			return em.createQuery(cq).getSingleResult();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public CommonRes getEnquirys(GetEnquiryDetailsReq req) {
		logger.info("Enter into getAllEnquiry.");
		CommonRes res = new CommonRes();
		List<EnquiryDetailsDTO> resList = new ArrayList<EnquiryDetailsDTO>();
		try {
			List<EnquiryDetails> enquiryList = new ArrayList<EnquiryDetails>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EnquiryDetails> cq = cb.createQuery(EnquiryDetails.class);
			Root<EnquiryDetails> edRoot = cq.from(EnquiryDetails.class);
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			cq.select(edRoot);
			
			if(StringUtils.isNotBlank(req.getEnquiryId())) {
				Subquery<Integer> amdMax = cq.subquery(Integer.class);
				Root<EnquiryDetails> amdRoot = amdMax.from(EnquiryDetails.class);
				
				amdMax.select(cb.max(amdRoot.get("amendId")))
					.where(cb.equal(amdRoot.get("enquiryId"), edRoot.get("enquiryId")),
							cb.equal(amdRoot.get("leadId"), edRoot.get("leadId")));
				
				predicates.add(cb.equal(edRoot.get("enquiryId"), req.getEnquiryId()));
				predicates.add(cb.equal(edRoot.get("amendId"), amdMax));
				Predicate [] predicateArray = new Predicate[predicates.size()];
				predicates.toArray(predicateArray);
				cq.where(predicateArray);
				enquiryList.add(em.createQuery(cq).getSingleResult());
			}else if(StringUtils.isNotBlank(req.getLeadId())) {
				Subquery<Integer> amdMax = cq.subquery(Integer.class);
				Root<EnquiryDetails> amdRoot = amdMax.from(EnquiryDetails.class);

				amdMax.select(cb.max(amdRoot.get("amendId")))
				.where(cb.equal(amdRoot.get("enquiryId"), edRoot.get("enquiryId")),
						cb.equal(amdRoot.get("leadId"), edRoot.get("leadId")));
				
				predicates.add(cb.equal(edRoot.get("leadId"), req.getLeadId()));
				predicates.add(cb.equal(edRoot.get("amendId"), amdMax));
				Predicate [] predicateArray = new Predicate[predicates.size()];
				predicates.toArray(predicateArray);
				cq.where(predicateArray);
				enquiryList = em.createQuery(cq).getResultList();				
			}else if(StringUtils.isNotBlank(req.getStatus()) && (StringUtils.isNotBlank(req.getLoginId()) || StringUtils.isNotBlank(req.getUwCode()))) {
				Subquery<Integer> amdMax = cq.subquery(Integer.class);
				Root<EnquiryDetails> amdRoot = amdMax.from(EnquiryDetails.class);
				
				amdMax.select(cb.max(amdRoot.get("amendId")))
					.where(cb.equal(amdRoot.get("enquiryId"), edRoot.get("enquiryId")),
							cb.equal(amdRoot.get("leadId"), edRoot.get("leadId")));
				
				predicates.add(cb.equal(edRoot.get("status"), req.getStatus()));
				if(StringUtils.isNotBlank(req.getUwCode()))
					predicates.add(cb.equal(edRoot.get("underwritters"), req.getUwCode()));
				else
					predicates.add(cb.equal(edRoot.get("createdBy"), req.getLoginId()));
				predicates.add(cb.equal(edRoot.get("amendId"), amdMax));
				Predicate [] predicateArray = new Predicate[predicates.size()];
				predicates.toArray(predicateArray);
				cq.where(predicateArray);
				enquiryList = em.createQuery(cq).getResultList();
			}else {
				Subquery<Integer> amdMax = cq.subquery(Integer.class);
				Root<EnquiryDetails> amdRoot = amdMax.from(EnquiryDetails.class);
				
				amdMax.select(cb.max(amdRoot.get("amendId")))
					.where(cb.equal(amdRoot.get("enquiryId"), edRoot.get("enquiryId")),
							cb.equal(amdRoot.get("leadId"), edRoot.get("leadId")));
				
				predicates.add(cb.equal(edRoot.get("amendId"), amdMax));
				Predicate [] predicateArray = new Predicate[predicates.size()];
				predicates.toArray(predicateArray);
				cq.where(predicateArray);
				enquiryList = em.createQuery(cq).getResultList();
			}
			
			List<LeadInformation> leadDetails = leadInfoRepo.findAll();
			
			if(!enquiryList.isEmpty()) {
				enquiryList.forEach(k -> {
					EnquiryDetailsDTO e = EnquiryDetailsDTO.builder()
				            .enquiryId(k.getEnquiryId()==null?"":k.getEnquiryId())
							.leadId(k.getLeadId()==null?"":k.getLeadId())
							.enquiryDescription(k.getEnquiryDescription() == null ? "" : k.getEnquiryDescription())
	                        .lobId(k.getLobId() == null ? "" : k.getLobId())
	                        .lobDesc(k.getLobId()==null?"":iplcmsListItemValueRepo.findByItemType("LINE_OF_BUSINESS")
	                        		.stream().filter(f -> k.getLobId().equalsIgnoreCase(f.getId().toString())
	                        				&& f.getStatus().equalsIgnoreCase("Y")).map(m -> m.getItemValue()).findFirst().get())
	                        .clientName(leadDetails.stream().filter(f -> f.getLeadId().equalsIgnoreCase(k.getLeadId()))
	                        		.map(m -> m.getClientName()).findFirst().get())
	                        .ClientCodeDesc(leadDetails.stream().filter(f -> f.getLeadId().equalsIgnoreCase(k.getLeadId()))
	                        		.map(m -> m.getClientName()).findFirst().get())
	                        .productId(k.getProductId() == null ? "" : k.getProductId())
	                        .sumInsured(k.getSumInsured())
	                        .suggestPremium(k.getSuggestPremium())
	                        .entryDate(k.getEntryDate())
	                        .createdBy(k.getCreatedBy() == null ? "" : k.getCreatedBy())
	                        .salesRemarks(k.getSalesRemarks()==null?"":k.getSalesRemarks())
	                        .uwRemarks(k.getUwRemarks()==null?"":k.getUwRemarks())
	                        .busniessType(k.getBusinessType()==null?"":k.getBusinessType())
	                        .rejectedDate(k.getRejectedDate())
	                        .rejectedReason(k.getRejectedReason() == null ? "" : k.getRejectedReason())
	                        .status(k.getStatus() == null ? "" : k.getStatus())
	                        .statusDesc(k.getStatusDesc()==null?"":k.getStatusDesc())
	                        .receiptOfenquiry(k.getReceiptOfenquiry()==null?"":k.getReceiptOfenquiry())
	                        .exceptedDateCommBussiness(k.getExceptedDateCommBussiness()==null?"":sdf.format(k.getExceptedDateCommBussiness()))
	                        .underWritters(k.getUnderwritters()==null?"":k.getUnderwritters())
	                        .quotesCount(String.valueOf(quoteInformationRepo.findByEnquiryIdAndQuoteStatus(k.getEnquiryId(),"Y").size()))
							.build();
					resList.add(e);
				});
				res.setMessage("SUCCESS");
				res.setCommonResponse(resList);
				res.setIsError(false);
				res.setErrorMessage(Collections.emptyList());
			}
			logger.info("Exit into getAllEnquiry.");
			return res;
		}catch(Exception e) {
			logger.info("Error in getAllEnquiry ==> "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<DropDownRes> contactType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String itemType= "CONTACT_TYPE" ;

			List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType(itemType);
			for (IplcmsListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getId().toString());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
//				if(!data.getParam1().isEmpty()) {
//				res.setTitletype(data.getParam1());
//				}
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> customerType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String itemType= "CUSTOMER_TYPE" ;

			List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType(itemType);
			for (IplcmsListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getId().toString());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
//				if(!data.getParam1().isEmpty()) {
//				res.setTitletype(data.getParam1());
//				}
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> sectionType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String itemType= "SECTION_TYPE" ;

			List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType(itemType);
			for (IplcmsListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getId().toString());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
//				if(!data.getParam1().isEmpty()) {
//				res.setTitletype(data.getParam1());
//				}
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> typeOfBusiness(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String itemType= "TYPE_OF_BUSINESS" ;

			List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType(itemType);
			for (IplcmsListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getId().toString());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
//				if(!data.getParam1().isEmpty()) {
//				res.setTitletype(data.getParam1());
//				}
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> currentInsurer(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String itemType= "INS_COMP" ;

			List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType(itemType);
			for (IplcmsListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getId().toString());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
//				if(!data.getParam1().isEmpty()) {
//				res.setTitletype(data.getParam1());
//				}
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> lineOfBusiness(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String itemType= "LINE_OF_BUSINESS" ;

			List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType(itemType);
			for (IplcmsListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getId().toString());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
//				if(!data.getParam1().isEmpty()) {
//				res.setTitletype(data.getParam1());
//				}
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> product(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String itemType= "PRODUCT" ;

			List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemTypeAndParam1(itemType,req.getProductId());
			for (IplcmsListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getId().toString());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
//				if(!data.getParam1().isEmpty()) {
//				res.setTitletype(data.getParam1());
//				}
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> businessType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String itemType= "BUSINESS_TYPE" ;

			List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType(itemType);
			for (IplcmsListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getId().toString());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
//				if(!data.getParam1().isEmpty()) {
//				res.setTitletype(data.getParam1());
//				}
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> probabilityOfSuccess(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String itemType= "POS" ;

			List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType(itemType);
			for (IplcmsListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getId().toString());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
//				if(!data.getParam1().isEmpty()) {
//				res.setTitletype(data.getParam1());
//				}
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

	@Transactional
	@Override
	public SuccessRes saveLeadDetails(EserviceLeadSaveReq req) {
			SuccessRes res = new SuccessRes();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
		//	SimpleDateFormat sdf = new SimpleDateFormat("yyMMddmmssSSS");
			try {
				EserviceLeadDetails saveData = new EserviceLeadDetails();
				Date entryDate = null;	
				String createdBy = "";
				String custRefNo = "";
				Integer productId;
	        if (StringUtils.isBlank(req.getCustomerReferenceNo())) {
					// Save
					entryDate = new Date();
					createdBy = req.getCreatedBy();
				//	Random rand = new Random();
				//	int random = rand.nextInt(90) + 10;
					productId=Integer.valueOf(req.getProductId());
				//	custRefNo = "Cust-" +   generateCustRefNo() ; // idf.format(new Date()) + random ;
					// Generate Seq
		 			SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
		 		 	generateSeqReq.setInsuranceId(req.getCompanyId());  
		 		 	generateSeqReq.setProductId(req.getProductId());
		 		 	generateSeqReq.setType("8");
		 		 	generateSeqReq.setTypeDesc("LEAD_REFERENCE_NO");
		 		 	custRefNo =  genSeqNoService.generateSeqCall(generateSeqReq);
					res.setResponse("Saved Successfully");
					res.setSuccessId(custRefNo);
				} else {
					// Update
					custRefNo = req.getCustomerReferenceNo();
					EserviceLeadDetails findData = leadrepo.findByCustomerReferenceNo(req.getCustomerReferenceNo());
					entryDate = findData.getEntryDate();
					createdBy = findData.getCreatedBy();
					productId=findData.getProductId();
					res.setResponse("Updated Successfully");
					res.setSuccessId(custRefNo);
				}
	        
	        	// Dob Condition
		        if(req.getDobOrRegDate() ==null  ) {
					Date   dobOrReg = new Date() ;
					if( req.getPolicyHolderType().equalsIgnoreCase("1") ) {
						// Dob
						Calendar cal = new GregorianCalendar();
						cal.setTime(dobOrReg);
						cal.add(Calendar.YEAR, -18);
						dobOrReg = cal.getTime();
					}
					req.setDobOrRegDate(dobOrReg);
					
				}
	        
				dozerMapper.map(req, saveData);
				saveData.setProductId(productId);
				saveData.setEntryDate(entryDate);
				saveData.setCreatedBy(createdBy);
				saveData.setUpdatedDate(new Date());
				saveData.setUpdatedBy(req.getCreatedBy());
				saveData.setCustomerReferenceNo(custRefNo);
				saveData.setStatus(req.getStatus());
				
				saveData.setZone(StringUtils.isBlank(req.getZone())? 0:Integer.valueOf(req.getZone()));
				saveData.setClientStatusDesc(req.getClientStatus().equalsIgnoreCase("N") ? "DeActive" : "Active");
				saveData.setGender(StringUtils.isBlank(req.getGender()) ? "M" : req.getGender());
				saveData.setOccupation(StringUtils.isBlank(req.getOccupation()) ? "2" : req.getOccupation());
				saveData.setBrokerBranchCode(req.getBrokerBranchCode());
				
				// Age Calculation
				int age = 0 ;
				Date dob = null;
				if (req.getDobOrRegDate() !=null) {
					dob = req.getDobOrRegDate();
					Date today = new Date();
					age = today.getYear() - dob.getYear();
				}

				// From List Item Value
				
				Map<String,String> title = custService.getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"NAME_TITLE",req.getTitle());//listRepo.findByItemTypeAndItemCode("NAME_TITLE", req.getTitle());
				Map<String,String> gender = custService.getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"GENDER",req.getGender());// listRepo.findByItemTypeAndItemCode("GENDER", saveData.getGender());
				Map<String,String> policyHolderType = custService.getListItemLocal ("99999" , req.getBranchCode() ,"POLICY_HOLDER_TYPE",req.getPolicyHolderType());//listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_TYPE",	req.getPolicyHolderType());
				Map<String,String> policyHolderTypeId = custService.getListItemLocal (req.getCompanyId(), req.getBranchCode() ,"POLICY_HOLDER_ID_TYPE",req.getPolicyHolderTypeid());// listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_ID_TYPE", req.getPolicyHolderTypeid());
				
				String genderDesc = Optional.ofNullable(gender).map(map -> map.get("itemDesc")).orElse("");
				String titleDesc = Optional.ofNullable(title).map(map -> map.get("itemDesc")).orElse("");
				String policyHolderTypeDesc = Optional.ofNullable(policyHolderType).map(map -> map.get("itemDesc")).orElse("");
				String policyHolderTypeIdDesc = Optional.ofNullable(policyHolderTypeId).map(map -> map.get("itemDesc")).orElse("");
				
				// From List Item Value (Local)
				String genderLocal = Optional.ofNullable(gender).map(map -> map.get("itemDescLocal")).orElse("");
				String titleLocal = Optional.ofNullable(title).map(map -> map.get("itemDescLocal")).orElse("");
				String PolicyHolderTypeLocal = Optional.ofNullable(policyHolderType).map(map -> map.get("itemDescLocal")).orElse("");
				String policyHolderTypeIdLocal = Optional.ofNullable(policyHolderTypeId).map(map -> map.get("itemDescLocal")).orElse("");
				
				// From Region_mater for state name local
				String stateNameLocal = "";
				List<RegionMaster> rgMaster = regionMasterRepo.findByCountryIdAndRegionCode(req.getCountry(),req.getStateCode());
				if(rgMaster!= null  && rgMaster.size()>0) {
					stateNameLocal = rgMaster.get(0).getRegionNameLocal();
				}
				// From State_master for city name local
				String cityNameLocal = "";
				List<StateMaster> stMaster = stateMasterRepo.findByStateIdAndCountryIdAndRegionCode(Integer.valueOf(req.getCityCode()),req.getCountry(),req.getStateCode());
				if(stMaster!= null && stMaster.size()>0) {
					cityNameLocal = stMaster.get(0).getStateNameLocal();
				}
				
				if(StringUtils.isNotBlank(req.getMobileCode1())){		        
					Map<String,String> mobileCode1Desc = custService.getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getMobileCode1());
					String mobileCode1 = Optional.ofNullable(mobileCode1Desc).map(map -> map.get("itemDesc")).orElse("");
				saveData.setMobileCodeDesc1(mobileCode1);

				}
		       
		        if(StringUtils.isNotBlank(req.getWhatsappCode())){		        
		        	Map<String,String> whatsappCodeDesc = custService.getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getWhatsappCode());
		        	String whatsappCode = Optional.ofNullable(whatsappCodeDesc).map(map -> map.get("itemDesc")).orElse("");
				saveData.setWhatsappCodeDesc(whatsappCode);

		        }			
				if (StringUtils.isNotBlank(req.getBusinessType())) {
					Map<String,String> businessTypeDesc =  custService.getListItemLocal ("99999" , req.getBranchCode() ,"BUSINESS_TYPE",req.getBusinessType());//listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE", req.getBusinessType());
					String businessType = Optional.ofNullable(businessTypeDesc).map(map -> map.get("itemDesc")).orElse("");
					saveData.setBusinessTypeDesc(businessType);
				}
				
				if (StringUtils.isNotBlank(req.getChannelId())) {
					List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType("CUSTOMER_TYPE");
					String channelName = getList.stream()
						    .filter(k -> req.getChannelId().equalsIgnoreCase(String.valueOf(k.getId())))
						    .map(k -> String.valueOf(k.getItemValue()))
						    .findFirst()
						    .orElse(null);
					saveData.setChannelName(channelName);
				}
				
				if (StringUtils.isNotBlank(req.getSectionTypeId())) {
					List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType("SECTION_TYPE");
					String sectionTypeName = getList.stream()
						    .filter(k -> req.getSectionTypeId().equalsIgnoreCase(String.valueOf(k.getId())))
						    .map(k -> String.valueOf(k.getItemValue()))
						    .findFirst()
						    .orElse(null);
					saveData.setSectionTypeDesc(sectionTypeName);
				}
				
				if (StringUtils.isNotBlank(req.getPropobabilityOfSuccessId())) {
					List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType("POS");
					String propobabilityOfSuccessName = getList.stream()
						    .filter(k -> req.getPropobabilityOfSuccessId().equalsIgnoreCase(String.valueOf(k.getId())))
						    .map(k -> String.valueOf(k.getItemValue()))
						    .findFirst()
						    .orElse(null);
					saveData.setPropobabilityOfSuccessDesc(propobabilityOfSuccessName);
				}
				
				if (StringUtils.isNotBlank(req.getTypeOfBusinessId())) {
					List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType("POS");
					String typeOfBussinessName = getList.stream()
						    .filter(k -> req.getTypeOfBusinessId().equalsIgnoreCase(String.valueOf(k.getId())))
						    .map(k -> String.valueOf(k.getItemValue()))
						    .findFirst()
						    .orElse(null);
					saveData.setTypeOfBussinessDesc(typeOfBussinessName);
				}
				
				
				
	 			Map<String,String> occupation = custService.getByOccupationIdDesc(req.getOccupation(), req.getCompanyId(),req.getProductId() , req.getBranchCode());
				String occupationDesc = Optional.ofNullable(occupation).map(map -> map.get("occupationName")).orElse("");
				String occupationDescLocal = Optional.ofNullable(occupation).map(map -> map.get("occupationNameLocal")).orElse("");
				saveData.setTitleDesc(titleDesc);
				saveData.setPreferredNotification(req.getPreferredNotification());
				saveData.setIsTaxExempted(req.getIsTaxExempted());
				saveData.setRegionCode(req.getRegionCode());
				saveData.setStatus(req.getStatus());
				saveData.setBusinessType(req.getBusinessType());
				saveData.setVrTinNo(req.getVrTinNo());
				saveData.setVrnGst(req.getVrTinNo());
				saveData.setMobileCode1(req.getMobileCode1());
				saveData.setGenderDesc(genderDesc);
				saveData.setOccupationDesc(occupationDesc);
				saveData.setOtherOccupation(req.getOtherOccupation());
				saveData.setPolicyHolderTypeDesc(policyHolderTypeDesc);
				saveData.setPolicyHolderTypeIdDesc(policyHolderTypeIdDesc);
				saveData.setIdType(req.getPolicyHolderTypeid());
				saveData.setIdTypeDesc(policyHolderTypeIdDesc);
				saveData.setVrTinNo(req.getVrTinNo());
				saveData.setVrnGst(req.getVrTinNo());
				saveData.setAge(age);
				saveData.setMobileCode1(req.getMobileCode1());
				saveData.setWhatsappCode(req.getWhatsappCode());
				saveData.setRegionCode(req.getStateCode());
				saveData.setStateCode(StringUtils.isBlank(req.getStateCode()) ?null :Integer.valueOf(req.getStateCode()));
				saveData.setStateName(req.getStateName());
				saveData.setCityCode(StringUtils.isBlank(req.getCityCode())?null :Integer.valueOf(req.getCityCode()));
				saveData.setCityName(req.getCityName());
				saveData.setRegionCode(req.getRegionCode());
				
				//local desc feilds
				saveData.setGenderDescLocal(genderLocal);
				saveData.setTitleDescLocal(titleLocal);
				saveData.setPolicyHolderTypeDescLocal(PolicyHolderTypeLocal);
				saveData.setPolicyHolderTypeIdDescLocal(policyHolderTypeIdLocal);
				saveData.setOccupationDescLocal(occupationDescLocal);
				saveData.setStateNameLocal(stateNameLocal);
				saveData.setCityNameLocal(cityNameLocal);
				saveData.setMobileCodeDesc1Local(req.getMobileCode1());
				saveData.setWhatsappCodeDescLocal(req.getWhatsappCode());
				saveData.setIdTypeDescLocal(policyHolderTypeIdLocal);
				saveData.setLicenseIssuedDate(new Date());
				saveData.setLicenseDuration(20);
				saveData.setGstIdentificationNo(req.getGstIdentificationNo());
				saveData.setLeadCreatedDate(sdf.parse(req.getLeadCreatedOn()));
				saveData.setIntermediateId(req.getIntermediateId());
				saveData.setIntermediateName(req.getIntermediateName());
				saveData.setChannelId(req.getChannelId());
				saveData.setSectionTypeId(req.getSectionTypeId());
				saveData.setCompanyId(req.getCompanyId());
				saveData.setProductId(Integer.parseInt(req.getProductId()));
				saveData.setPropobabilityOfSuccessId(req.getPropobabilityOfSuccessId());
				saveData.setTypeOfBussinessId(req.getTypeOfBusinessId());
				saveData.setCurrentInsurer(req.getCurrentInsurer());
				leadrepo.save(saveData);
				
				if(req.getLeadContactPersonReq()!=null && req.getLeadContactPersonReq().size()>0) {
					List<LeadContactPerson> cps = new ArrayList<LeadContactPerson>();
					String ioe = custRefNo;
					leadContactRepo.deleteByLeadId(custRefNo);
					AtomicInteger autoIndex = new AtomicInteger(GetLeadContactMaxSno());
					req.getLeadContactPersonReq().forEach(a -> {
						LeadContactPerson p = LeadContactPerson.builder()
								.sno(new BigDecimal(autoIndex.getAndIncrement()))
								.leadId(ioe)
								.contactType(a.getContactType())
								.contactPersonName(a.getContactPersonName())
								.emailAddress(a.getEmailAddress())
								.mobile(a.getMobileNo())
								.phone(a.getPhoneNo())
								.designation(a.getDesignation())
								.Remarks(a.getRemarks())
								.build();
						cps.add(p);
					});
					leadContactRepo.saveAll(cps);
				}

			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;

		}
	
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	@Override
	public List<GetLeadDetailsRes> getLeadDetails(GetCustomerDetailsReq req) {
		List<GetLeadDetailsRes> resList = new ArrayList<GetLeadDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			List<EserviceLeadDetails> salesList = new ArrayList<EserviceLeadDetails>();
			if(StringUtils.isBlank(req.getCustomerReferenceNo())) {
				salesList = leadrepo.findAll();
			}else {
				EserviceLeadDetails salesById = leadrepo.findByCustomerReferenceNoOrderByEntryDateDesc(req.getCustomerReferenceNo()).get(0);
				salesList.add(salesById);
			}
			
			salesList.forEach(k -> {
				GetLeadDetailsRes u = dozerMapper.map(k, GetLeadDetailsRes.class);
				u.setLeadContactPersonReq(GetLeadContactPerson(k.getCustomerReferenceNo()));
				resList.add(u);
			});
					
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> getStatusByUserType() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String itemType= "SU_STATUS" ;

			List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType(itemType);
			for (IplcmsListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

	@Override
	public boolean saveUploadDocuments(SaveUploadDocumentsReq req, List<MultipartFile> fileReq) {
		try {
			if(fileReq!=null && fileReq.size()>0) {
				Path path = Paths.get(this.getClass().getClassLoader().getResource("report/IPCLMS_Documents").toURI());
				if(!Files.exists(path)) {
					Files.createDirectories(path);
				}
				for(MultipartFile file :fileReq) {
					String fileName = req.getLoginId()+"_"+System.currentTimeMillis()+"_"+file.getOriginalFilename();
					if(file!=null) {
						Files.copy(file.getInputStream(), path.resolve(fileName));
						IpclmsFileUploadDetails m = IpclmsFileUploadDetails.builder()
								.fileId(getMaxFileId())
								.enquiryId(req.getEnquiryId()==null?null:req.getEnquiryId())
								.quoteNo(req.getQuoteNo()==null?null:req.getQuoteNo())
								.fileName(fileName)
								.filePath(path.resolve(fileName).toString())
								.loginId(req.getLoginId()==null?null:req.getLoginId())
								.status(req.getStatus()==null?null:req.getStatus())
								.build();
						ipclmsFileUploadDetailsRepo.saveAndFlush(m);
					}
				}
				return true;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private BigDecimal getMaxFileId() {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BigDecimal> cq = cb.createQuery(BigDecimal.class);
			Root<IpclmsFileUploadDetails> ifuRoot = cq.from(IpclmsFileUploadDetails.class);
			
			cq.select(cb.coalesce(cb.sum(cb.max(ifuRoot.get("fileId")),BigDecimal.ONE), new BigDecimal("5000")));
			
			return em.createQuery(cq).getSingleResult();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JasperDocumentRes downloadUploadDocuments(String id) {
		JasperDocumentRes res = new JasperDocumentRes();
		try {
			Optional<IpclmsFileUploadDetails> optdata =  ipclmsFileUploadDetailsRepo.findById(new BigDecimal(id));
			if(optdata.isPresent()) {
				IpclmsFileUploadDetails data = optdata.get();
				GetFileFromPath filePath = new GetFileFromPath(data.getFilePath());
				res.setPdfoutfile(filePath.call().getImgUrl());
				res.setPdfoutfilepath(data.getFilePath());
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public CommonRes getUploadDocumentList(GetUploadDocumentListReq req) {
		CommonRes res = new CommonRes();
		List<GetUploadDocumentListRes> reslist = new ArrayList<GetUploadDocumentListRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<IpclmsFileUploadDetails> cq = cb.createQuery(IpclmsFileUploadDetails.class);
			Root<IpclmsFileUploadDetails> ifuRoot = cq.from(IpclmsFileUploadDetails.class);
			
			cq.multiselect(ifuRoot.get("fileId").alias("fileId"),ifuRoot.get("fileName").alias("fileName"),
					ifuRoot.get("loginId").alias("loginId"),ifuRoot.get("status").alias("status"))
			.where(cb.equal(ifuRoot.get("enquiryId"), req.getEnquiryId()),
					cb.equal(ifuRoot.get("status"), req.getStatus()),
					req.getStatus().equalsIgnoreCase("Q")?cb.equal(ifuRoot.get("quoteNo"), req.getQuoteNo()):cb.conjunction());
			
			List<IpclmsFileUploadDetails> list = em.createQuery(cq).getResultList();
			if(list!=null && list.size()>0) {
				list.forEach(k -> {
					GetUploadDocumentListRes m = GetUploadDocumentListRes.builder()
							.fileId(k.getFileId()==null?"":k.getFileId().toString())
							.fileName(k.getFileName()==null?"":k.getFileName())
							.loginId(k.getLoginId()==null?"":k.getLoginId())
							.status(k.getStatus()==null?"":k.getStatus())
							.build();
					reslist.add(m);
				});
				if(reslist != null && reslist.size()>0) {
					res.setCommonResponse(reslist);
					res.setIsError(false);
					res.setMessage("SUCCESS");
				}else {
					res.setCommonResponse(Collections.emptyList());
					res.setIsError(true);
					res.setMessage("FAILED");
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return res;
	}


}
