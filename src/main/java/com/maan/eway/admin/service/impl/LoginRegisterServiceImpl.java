package com.maan.eway.admin.service.impl;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.maan.eway.admin.req.AdditionalInfoReq;
import com.maan.eway.admin.req.AttachBrokerBranchReq;
import com.maan.eway.admin.req.AttachCompnayProductRequest;
import com.maan.eway.admin.req.BrokerActiveGridReq;
import com.maan.eway.admin.req.BrokerCreationReq;
import com.maan.eway.admin.req.BrokerDetailsGetReq;
import com.maan.eway.admin.req.BrokerLoginGridReq;
import com.maan.eway.admin.req.CommonLoginCreationReq;
import com.maan.eway.admin.req.CommonLoginInformationReq;
import com.maan.eway.admin.req.CommonPersonalInforReq;
import com.maan.eway.admin.req.GetBrokerListDropDownReq;
import com.maan.eway.admin.req.InsertUserLoginReq;
import com.maan.eway.admin.req.IssuerActiveGridReq;
import com.maan.eway.admin.req.IssuerCraeationReq;
import com.maan.eway.admin.req.IssuerDetailsGetReq;
import com.maan.eway.admin.req.IssuerLoginGridReq;
import com.maan.eway.admin.req.IssuerLoginReq;
import com.maan.eway.admin.req.IssuerPersonalInfoReq;
import com.maan.eway.admin.req.MenuIdGetReq;
import com.maan.eway.admin.req.MenuIdSaveReq;
import com.maan.eway.admin.req.MenuListReq;
import com.maan.eway.admin.req.Role;
import com.maan.eway.admin.req.UserActiveGridReq;
import com.maan.eway.admin.req.UserCreationReq;
import com.maan.eway.admin.req.UserDetailsGetReq;
import com.maan.eway.admin.req.UserLoginGridReq;
import com.maan.eway.admin.req.UserSolvedgeRequest;
import com.maan.eway.admin.res.BrokerDatailsGetRes;
import com.maan.eway.admin.res.BrokerDepositCbcDetailsGetRes;
import com.maan.eway.admin.res.BrokerLoginDetailsGetRes;
import com.maan.eway.admin.res.BrokerPersonalDetailsGetRes;
import com.maan.eway.admin.res.GetBrokerListDropDownRes;
import com.maan.eway.admin.res.IssuerDatailsGetRes;
import com.maan.eway.admin.res.IssuerLoginGetRes;
import com.maan.eway.admin.res.IssuerPersonalInfoGetRes;
import com.maan.eway.admin.res.LoginBrokerDetailsGetRes;
import com.maan.eway.admin.res.LoginBrokerDetailsGridRes;
import com.maan.eway.admin.res.LoginCreationRes;
import com.maan.eway.admin.res.LoginDetailsCriteriaRes;
import com.maan.eway.admin.res.LoginDropdownCriteria;
import com.maan.eway.admin.res.LoginIssuerGridRes;
import com.maan.eway.admin.res.LoginUserGridRes;
import com.maan.eway.admin.res.MenuIdGetRes;
import com.maan.eway.admin.res.UserDetailsGetRes;
import com.maan.eway.admin.res.UserLoginGetRes;
import com.maan.eway.admin.res.UserPersonalInfoGetRes;
import com.maan.eway.admin.service.LoginBranchService;
import com.maan.eway.admin.service.LoginProductService;
import com.maan.eway.auth.dto.Menu;
import com.maan.eway.auth.token.passwordEnc;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.BrokerCommissionDetails;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.DepositcbcMaster;
import com.maan.eway.bean.EwayUserMapping;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginBranchMasterArch;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginMasterArch;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.LoginUserInfoArch;
import com.maan.eway.bean.MenuMaster;
import com.maan.eway.bean.PolicyTypeMaster;
import com.maan.eway.bean.RegionMaster;
import com.maan.eway.bean.SeqAgencycode;
import com.maan.eway.bean.StateMaster;
import com.maan.eway.claim.PortalCreateBranchRequest;
import com.maan.eway.claim.PortalCreateUserRequest;
import com.maan.eway.common.req.SaveDepositeMasterReq;
import com.maan.eway.common.service.DepositService;
import com.maan.eway.jasper.res.JasperDocumentRes;
import com.maan.eway.master.req.BrokerCompanyListProductReq;
import com.maan.eway.master.req.BrokerDropdownReq;
import com.maan.eway.master.req.BrokerProductReq;
import com.maan.eway.repository.BranchMasterRepository;
import com.maan.eway.repository.BrokerCommissionDetailsRepository;
import com.maan.eway.repository.DepositcbcMasterRepository;
import com.maan.eway.repository.EwayUserMappingRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.LoginBranchMasterArchRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginMasterArchRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.LoginProductMasterRepository;
import com.maan.eway.repository.LoginUserInfoArchRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.RegionMasterRepository;
import com.maan.eway.repository.SeqAgencycodeRepository;
import com.maan.eway.repository.StateMasterRepository;
import com.maan.eway.res.BrokerDropDownRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class LoginRegisterServiceImpl {

	@Autowired
	private RegionMasterRepository regrepo;
	@Autowired
	private StateMasterRepository staterepo;

	@Autowired
	private LoginMasterRepository loginRepo;
	
	@Autowired
	private DepositService depoService;
	
	@Autowired
	private LoginMasterArchRepository loginArchRepo;
	
	@Autowired
	private LoginUserInfoRepository loginUserRepo ;
	
	@Autowired
	private LoginUserInfoArchRepository loginUserArchRepo ;
	
	@Autowired
	private LoginProductService loginProductService ;
	
	@Autowired
	private LoginBranchService loginBranchService ;
	
	@Autowired
	private LoginBranchMasterRepository loginBranchRepo ;
	
	@Autowired
	private LoginBranchMasterArchRepository loginBrokerArchRepo;
	
	@Autowired
	private ListItemValueRepository listRepo;
	
	@Autowired
	private SeqAgencycodeRepository seqAgencyRepo;
	
	@Autowired
	private DepositcbcMasterRepository depositcbcRepo;
	@PersistenceContext
	
	private EntityManager em;

	Gson json = new Gson();

	@Autowired
	private InsuranceCompanyMasterRepository insuranceRepo ;
	
	
	@Autowired
	private MarineLoginApi marineapi;
	
	@Autowired
	private EwayUserMappingRepository ewayUserMappingRepo;
	
	@Value(value = "${crm.createSales}")
	private String createSales;

	@Value(value = "${crm.createIssueAndBoth}")
	private String createIssueAndBoth;
	
	@Value(value = "${crm.updateSales}")
	private String updateSales;
	
	@Value(value = "${crm.updateIssueAndBoth}")
	private String updateIssueAndBoth;
	
	@Value(value = "${bidderCreation}")
	private String bidderCreation;
	
	@Value(value = "${solvegeLogin}")
	private String solvegeLogin;
	
	@Value(value = "${bidderUserUpdate}")
	private String bidderUserUpdate;
	
	@Value(value = "${claimUserSaveLogin}")
	private String claimUserSaveLogin;
	
	@Value(value = "${claimUserSaveBranch}")
	private String claimUserSaveBranch;
	
	@Autowired
	private LoginUserInfoRepository userinfoRepo;
	
	@Autowired
	private InsuranceCompanyMasterRepository companyMasterRepo;
	
	@Autowired
	private BranchMasterRepository branchRepo;
	
	@Autowired
	private LoginBranchMasterRepository loginBrokerRepo;
	
	@Autowired
	private BrokerCommissionDetailsRepository commissionRepo ;
	
	@Autowired
	private LoginProductMasterRepository loginProductRepo ;
	
	
private Logger log=LogManager.getLogger(LoginDetailsServiceImpl.class);
/*
public LoginMasterServiceImpl(LoginMasterRepository repo) {
this.repository = repo;
}

  */
 	
    public LoginMaster create(LoginMaster d) {

       LoginMaster entity;

        try {
            entity = loginRepo.save(d);

        } catch (Exception ex) {
			log.error(ex);
            return null;
        }
        return entity;
    }

    
    
    public LoginMaster update(LoginMaster d) {
        LoginMaster c;

        try {
            c = loginRepo.saveAndFlush(d);

        } catch (Exception ex) {
			log.error(ex);
            return null;
        }
        return c;
    }

/*
    
    public LoginMaster getOne(long id) {
        LoginMaster t;

        try {
            t = repository.findById(id).orElse(null);

        } catch (Exception ex) {
			log.error(ex);
            return null;
        }
        return t;
    }

*/
    
    public List<LoginMaster> getAll() {
        List<LoginMaster> lst;

        try {
            lst = loginRepo.findAll();

        } catch (Exception ex) {
			log.error(ex);
            return Collections.emptyList();
        }
        return lst;
    }


    
    public long getTotal() {
        long total;

        try {
            total = loginRepo.count();
        } catch (Exception ex) {
            log.error(ex);
			return 0;
        }
        return total;
    }

 //*************************************** Login Creation Apis Methods **********************************************************//
  
    @Transactional
	
	public LoginCreationRes createRegister(BrokerCreationReq req, MultipartFile brokerLogo, String token) {
		LoginCreationRes res = new LoginCreationRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {

			CommonLoginCreationReq commonReq = dozerMapper.map(req, CommonLoginCreationReq.class);
			LoginMaster loginData = loginRepo.findByLoginId(req.getLoginInformation().getLoginId());

			if (brokerLogo != null) {
				commonReq.setBrokerLogo(req.getLoginInformation().getLoginId() + "_logo."
						+ FilenameUtils.getExtension(brokerLogo.getOriginalFilename()));
				Path path = Paths.get(this.getClass().getClassLoader().getResource("report/images/").toURI());
				Files.copy(brokerLogo.getInputStream(), path.resolve(commonReq.getBrokerLogo()));
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			String saveRes = "";
			if (loginData == null) {
				// Save
				saveRes = newLoginInsert(commonReq);
				if (StringUtils.isNotBlank(saveRes)) {
					res.setAgencyCode(saveRes);
					res.setResponse("Saved Successfully");
					
					if (req.getLoginInformation().getSubUserType().equalsIgnoreCase("Sales")
							|| req.getLoginInformation().getSubUserType().equalsIgnoreCase("Bidder")) {

						EwayUserMapping ewayUserMapping = new EwayUserMapping();
						ewayUserMapping.setActive(req.getLoginInformation().getStatus());
						ewayUserMapping.setCompanyId(req.getLoginInformation().getCompanyId());
						ewayUserMapping.setLoginId(req.getLoginInformation().getLoginId());
						ewayUserMapping.setEffectiveDateStart(new Date());
						ewayUserMapping.setApproverId(req.getPersonalInformation().getApprovalId());
						ewayUserMapping.setRole(req.getLoginInformation().getSubUserType());
						ewayUserMapping.setBranchCode(req.getLoginInformation().getBranchCode());
						String end = "31/12/2050";
						Date endDate = sdf.parse(end);
						ewayUserMapping.setEffectiveDateEnd(endDate);
						System.out.println(ewayUserMapping);
						EwayUserMapping save = ewayUserMappingRepo.save(ewayUserMapping);
						log.info("Saved mapping: " + save.getMappingUserId());
						if (req.getLoginInformation().getSubUserType().equalsIgnoreCase("Sales")) {
							String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
							actualToken = actualToken.split(",")[0];
							String rcmApi = createSales;
							RestTemplate restTemplate = new RestTemplate();
							HttpHeaders headers = new HttpHeaders();
							headers.setContentType(MediaType.APPLICATION_JSON);
							headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
							headers.set("X-AUTH-TOKEN", actualToken);
							headers.set("Authorization", "");
							HttpEntity<BrokerCreationReq> requestEntity = new HttpEntity<>(req, headers);

							ResponseEntity<String> response = restTemplate.exchange(rcmApi, HttpMethod.POST,
									requestEntity, String.class);
							System.out.println("Response: " + response.getBody());
						}

					}

				} else {
					res.setAgencyCode("");
					res.setResponse("Error In Save");
				}

			} else {
				// Update
				saveRes = updateLoginDetails(commonReq);
				if (StringUtils.isNotBlank(saveRes)) {
					res.setAgencyCode(saveRes);
					res.setResponse("Updated Successfully");
					if (req.getLoginInformation().getSubUserType().equalsIgnoreCase("Sales")) {

						Optional<EwayUserMapping> mappingUser = ewayUserMappingRepo
								.findByApproverId(req.getLoginInformation().getLoginId());
						if (mappingUser.isPresent()) {
							EwayUserMapping ewayUserMapping = mappingUser.get();
							ewayUserMapping.setActive(req.getLoginInformation().getStatus());
							ewayUserMapping.setLoginId(req.getLoginInformation().getCreatedBy());
							ewayUserMapping.setApproverId(req.getPersonalInformation().getApprovalId());
							ewayUserMapping.setRole(req.getLoginInformation().getSubUserType());
							ewayUserMappingRepo.save(ewayUserMapping);
						}
					}
				} else {
					res.setAgencyCode("");
					res.setResponse("Error In Update");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	
	@Transactional
	
	public LoginCreationRes createIssuerLogin(IssuerCraeationReq req,String token) {
		LoginCreationRes res = new LoginCreationRes();
		 DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		
		try {	
			
			CommonLoginCreationReq commonReq = dozerMapper.map(req, CommonLoginCreationReq.class );
			LoginMaster loginData  = loginRepo.findByLoginId(req.getLoginInformation().getLoginId()  );
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
			actualToken = actualToken.split(",")[0];
			String saveRes = "";
			if (loginData ==null) {
				// Save
				saveRes =   newLoginInsert(commonReq) ;
				if( StringUtils.isNotBlank(saveRes)) {
					res.setAgencyCode(saveRes);
					res.setResponse("Saved Successfully");

					if(req.getLoginInformation().getSubUserTypeDesc().equalsIgnoreCase("Bidder")) {

						RestTemplate restTemplate = new RestTemplate();
						ObjectMapper objectMapper = new ObjectMapper();

						String loginPayload = "{\"email\":\"admin@admin.com\",\"password\":\"123456\"}";

						HttpHeaders loginHeaders = new HttpHeaders();
						loginHeaders.setContentType(MediaType.APPLICATION_JSON);
						loginHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

						HttpEntity<String> loginRequest = new HttpEntity<>(loginPayload, loginHeaders);
						ResponseEntity<String> loginResponse = restTemplate.postForEntity(solvegeLogin,
								loginRequest, String.class);
						String loginBody = loginResponse.getBody();
						System.out.println("Login Response: " + loginBody);

						JsonNode root = objectMapper.readTree(loginBody);
						JsonNode tokenNode = root.path("Result").path("token");
						String tokenSolvedge = tokenNode.asText();
						HttpHeaders creationHeaders = new HttpHeaders();
						creationHeaders.setContentType(MediaType.APPLICATION_JSON);
						creationHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
						creationHeaders.set("Authorization", "Bearer " + tokenSolvedge);
						UserSolvedgeRequest userRequest = draftUserSolvedgeDetail(req, commonReq);
						HttpEntity<UserSolvedgeRequest> creationRequest = new HttpEntity<>(userRequest,
								creationHeaders);
						ResponseEntity<String> creationResponse = restTemplate.exchange(bidderCreation, HttpMethod.POST,
								creationRequest, String.class);
						System.out.println("Bidder Creation Response: " + creationResponse.getBody());

					}
					if (req.getLoginInformation().getSubUserType().equalsIgnoreCase("both")||
							req.getLoginInformation().getSubUserType().equalsIgnoreCase("low")) {
					EwayUserMapping ewayUserMapping = new EwayUserMapping();
					ewayUserMapping.setActive(req.getLoginInformation().getStatus());
					ewayUserMapping.setCompanyId(req.getLoginInformation().getCompanyId());
					ewayUserMapping.setLoginId(req.getLoginInformation().getLoginId());
					ewayUserMapping.setEffectiveDateStart(new Date());	
					ewayUserMapping.setBranchCode(req.getLoginInformation().getBranchCode());
					String subUserType = req.getLoginInformation().getSubUserType();
					String role = subUserType.equalsIgnoreCase("both") ? "Manager" :
					              subUserType.equalsIgnoreCase("low") ? "Underwriter" : null;
					ewayUserMapping.setRole(role);

					String end = "31/12/2050";
					Date endDate = sdf.parse(end);
					ewayUserMapping.setEffectiveDateEnd(endDate);
					System.out.println(req.getLoginInformation().getBranchCode());
					 EwayUserMapping save = ewayUserMappingRepo.save(ewayUserMapping);
					 log.info("Saved mapping: " + save.getMappingUserId());

					System.out.println(save);
					
					
					if (req.getLoginInformation().getCompanyId().equalsIgnoreCase("100046")
							|| req.getLoginInformation().getCompanyId().equalsIgnoreCase("100047")
							|| req.getLoginInformation().getCompanyId().equalsIgnoreCase("100048")
							|| req.getLoginInformation().getCompanyId().equalsIgnoreCase("100049")
							|| req.getLoginInformation().getCompanyId().equalsIgnoreCase("100050")) {
						String rcmApi = createIssueAndBoth;

						RestTemplate restTemplate = new RestTemplate();

						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.APPLICATION_JSON);
						headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
						headers.set("X-AUTH-TOKEN", actualToken);
						headers.set("Authorization", "");
						HttpEntity<IssuerCraeationReq> requestEntity = new HttpEntity<>(req, headers);

						ResponseEntity<String> response = restTemplate.exchange(rcmApi, HttpMethod.POST, requestEntity,
								String.class);
						System.out.println("Response: " + response.getBody());
					}
				}
					saveClaimUser(req, actualToken, saveRes);
				} else {
					res.setAgencyCode("");
					res.setResponse("Error In Save");
				}
				
			} else {
				// Update
				saveRes = updateLoginDetails(commonReq);
				if (StringUtils.isNotBlank(saveRes)) {
					res.setAgencyCode(saveRes);
					res.setResponse("Updated Successfully");
					if (req.getLoginInformation().getSubUserType().equalsIgnoreCase("both")
							|| req.getLoginInformation().getSubUserType().equalsIgnoreCase("low")) {
						Optional<EwayUserMapping> mappingUser = ewayUserMappingRepo
								.findByApproverId(req.getLoginInformation().getLoginId());
						if (mappingUser.isPresent()) {
							EwayUserMapping ewayUserMapping = mappingUser.get();
							ewayUserMapping.setActive(req.getLoginInformation().getStatus());
							ewayUserMapping.setLoginId(req.getLoginInformation().getCreatedBy());
							ewayUserMapping.setApproverId("Approver");
							String subUserType = req.getLoginInformation().getSubUserTypeDesc();
							String role = subUserType.equalsIgnoreCase("both") ? "Manager"
									: subUserType.equalsIgnoreCase("low") ? "Underwriter" : null;
							ewayUserMapping.setRole(role);
							ewayUserMappingRepo.save(ewayUserMapping);
						}
						if (req.getLoginInformation().getCompanyId().equalsIgnoreCase("100046")
								|| req.getLoginInformation().getCompanyId().equalsIgnoreCase("100047")
								|| req.getLoginInformation().getCompanyId().equalsIgnoreCase("100048")
								|| req.getLoginInformation().getCompanyId().equalsIgnoreCase("100049")
								|| req.getLoginInformation().getCompanyId().equalsIgnoreCase("100050")) {
							String rcmApi = updateIssueAndBoth;
							RestTemplate restTemplate = new RestTemplate();
							HttpHeaders headers = new HttpHeaders();
							headers.setContentType(MediaType.APPLICATION_JSON);
							headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
							headers.set("X-AUTH-TOKEN", actualToken);
							headers.set("Authorization", "");
							HttpEntity<IssuerCraeationReq> requestEntity = new HttpEntity<>(req, headers);

							ResponseEntity<String> response = restTemplate.exchange(rcmApi, HttpMethod.PUT,
									requestEntity, String.class);
							System.out.println("Response: " + response.getBody());
						}
					}
					saveClaimUser(req, actualToken, saveRes);
				} else {
					res.setAgencyCode("");
					res.setResponse("Error In Update");
				}

			}
			
			// Product Insert 
			if(StringUtils.isNotBlank(req.getLoginInformation().getSubUserType() ) && "SuperAdmin".equalsIgnoreCase(req.getLoginInformation().getSubUserType() ) ) {
				AttachCompnayProductRequest productReq = new AttachCompnayProductRequest();
				productReq.setCreatedBy(req.getLoginInformation().getCreatedBy());
				productReq.setInsuranceId(req.getLoginInformation().getCompanyId());
				productReq.setLoginId(req.getLoginInformation().getLoginId());
				productReq.setProductIds(req.getLoginInformation().getProductIds());
				productReq.setReferralIds(req.getLoginInformation().getReferralIds());
				
				if((req.getLoginInformation().getSubUserType().equalsIgnoreCase("low")) || (req.getLoginInformation().getSubUserType().equalsIgnoreCase("both"))  ) {
					
					LoginCreationRes productRes = loginProductService.saveIssuerProductDetails(productReq) ;
				}
				
				//Remove unmatched branches
				if(loginData!=null) {
					List<LoginBranchMaster> findBranches = loginBranchRepo.findByLoginIdAndCompanyId(loginData.getLoginId() ,  loginData.getCompanyId());
					List<LoginBranchMaster> filtermatch = new ArrayList<LoginBranchMaster>();
					if(findBranches.size()>0) {
						for (String branh :   req.getLoginInformation().getAttachedBranches() ) {
							List<LoginBranchMaster> filter = findBranches.stream().filter(o -> o.getBranchCode().equalsIgnoreCase(branh) ).collect(Collectors.toList());		
							if(filter.size()>0)
								filtermatch.add(filter.get(0));
						}
						findBranches.removeAll(filtermatch);
						loginBranchRepo.deleteAll(findBranches);
					}
				}
				
				
				
				// Branch Insert 
				for (String branch :   req.getLoginInformation().getAttachedBranches() ) {
					AttachBrokerBranchReq branchReq = new AttachBrokerBranchReq();
					branchReq.setAddress1(req.getPersonalInformation().getAddress1());
					branchReq.setAddress2(req.getPersonalInformation().getAddress2());
					branchReq.setBranchCode(branch);
					branchReq.setCreatedBy(req.getLoginInformation().getCreatedBy());
					branchReq.setCompanyId(req.getLoginInformation().getCompanyId());
					branchReq.setEmail(req.getPersonalInformation().getUserMail());
					branchReq.setMobile(req.getPersonalInformation().getUserMobile() );
					branchReq.setRemarks(req.getPersonalInformation().getRemarks());
					branchReq.setStatus(req.getLoginInformation().getStatus());
					branchReq.setLoginId(req.getLoginInformation().getLoginId());
					
					LoginCreationRes branchRes  = loginBranchService.attachBrokerCompanyBranch(branchReq, token );
				}
				
			}
		
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null; 
		}
		return res ;
	}


	private void saveClaimUser(IssuerCraeationReq req, String actualToken, String saveRes) {
		if (req.getLoginInformation().getCompanyId().equalsIgnoreCase("100046")
				|| req.getLoginInformation().getCompanyId().equalsIgnoreCase("100047")
				|| req.getLoginInformation().getCompanyId().equalsIgnoreCase("100048")
				|| req.getLoginInformation().getCompanyId().equalsIgnoreCase("100049")
				|| req.getLoginInformation().getCompanyId().equalsIgnoreCase("100050")) {
			
			

			IssuerLoginReq loginInformation = req.getLoginInformation();
			IssuerPersonalInfoReq personalInformation = req.getPersonalInformation();
			PortalCreateUserRequest createReq = new PortalCreateUserRequest();
			LoginMaster findLogin = loginRepo.findByLoginId(loginInformation.getLoginId());
			createReq.setCompanyId(loginInformation.getCompanyId());
			createReq.setCreatedBy(loginInformation.getCreatedBy());
			createReq.setEmail(personalInformation.getUserMail());
			createReq.setFirstName(personalInformation.getUserName());
			createReq.setLastName("");
			createReq.setLoginId(loginInformation.getLoginId());
			createReq.setPassword(loginInformation.getPassword());
			createReq.setPhoneNo(personalInformation.getUserMobile());
			createReq.setSubUserType(findLogin.getSubUserType());
			createReq.setUserType(loginInformation.getUserType());
			createReq.setUserTypeId(findLogin.getSubUserTypeId());
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//			headers.set("X-AUTH-TOKEN", actualToken);
//			headers.setBearerAuth(actualToken);
			System.out.println("UrL : " + claimUserSaveLogin);
			System.out.println("Token : " + actualToken);
			HttpEntity<PortalCreateUserRequest> requestEntity = new HttpEntity<>(createReq, headers);
			ResponseEntity<String> response = restTemplate.exchange(claimUserSaveLogin, HttpMethod.POST, requestEntity,
					String.class);
			System.out.println("Response: " + response.getBody());
			
			List<LoginBranchMaster> loginBranchMasterList = loginBranchRepo.findByLoginIdAndCompanyId(loginInformation.getLoginId(),
					req.getLoginInformation().getCompanyId());
			
			if (loginBranchMasterList != null && !loginBranchMasterList.isEmpty()) {
				List<PortalCreateBranchRequest> requestList = new ArrayList<>();
				for(LoginBranchMaster loginB : loginBranchMasterList) {
				PortalCreateBranchRequest branchReq = new PortalCreateBranchRequest();
				branchReq.setBranchCode(loginB.getBranchCode());
				branchReq.setBranchName(loginB.getBranchName());
				branchReq.setCompanyId(loginB.getCompanyId());
				branchReq.setCreatedBy(loginB.getCreatedBy());
				branchReq.setLoginId(loginB.getLoginId());
				branchReq.setStatus(loginB.getStatus());
				requestList.add(branchReq);
				}

				RestTemplate restTemplate1 = new RestTemplate();
				HttpHeaders headers1 = new HttpHeaders();
				headers1.setContentType(MediaType.APPLICATION_JSON);
				headers1.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//				headers1.set("X-AUTH-TOKEN", actualToken);
//				headers1.setBearerAuth(actualToken);
				HttpEntity<List<PortalCreateBranchRequest>> requestEntity1 = new HttpEntity<>(requestList, headers1);
				ResponseEntity<String> response1 = restTemplate1.exchange(claimUserSaveBranch, HttpMethod.POST, requestEntity1,
						String.class);
				System.out.println("Response: " + response1.getBody());
			}
		}
	}


	@Transactional
	
	public LoginCreationRes createUserLogin(UserCreationReq req) {
		LoginCreationRes res = new LoginCreationRes();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		try {		
			
			CommonLoginCreationReq commonReq = dozerMapper.map(req, CommonLoginCreationReq.class );	
			LoginMaster loginData  = loginRepo.findByLoginId(req.getLoginInformation().getLoginId()  );
			
			String saveRes = "";
			if (loginData ==null) {
				// Save
				saveRes =   newLoginInsert(commonReq) ;
				if( StringUtils.isNotBlank(saveRes)) {
					res.setAgencyCode(saveRes);
					res.setResponse("Saved Successfully");
						
				} else {
					res.setAgencyCode("");
					res.setResponse("Error In Save");
				}
				
			} else {
				// Update
				saveRes = updateLoginDetails(commonReq);
				if( StringUtils.isNotBlank(saveRes)) {
					res.setAgencyCode(saveRes);
					res.setResponse("Updated Successfully");
						
				} else {
					res.setAgencyCode("");
					res.setResponse("Error In Update");
				}
				
			}
					
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null; 
		}
		return res ;
	}
	
	private String generateCustomerCode(List<LoginUserInfo> loginData) {

	    Set<Long> existingCodes = new HashSet<>();
	    long maxValue = 0;

	    for (LoginUserInfo data : loginData) {

	        String code = data.getCustomerCode();

	        if (StringUtils.isNotBlank(code)) {

	            long value = Long.parseLong(code);

	            existingCodes.add(value);
	            maxValue = Math.max(maxValue, value);
	        }
	    }

	    long nextValue = maxValue + 1;

	    while (existingCodes.contains(nextValue)) {
	        nextValue++;
	    }

	    return String.valueOf(nextValue);
	}
	
	// Login Save Method
	@Transactional
	public String newLoginInsert(CommonLoginCreationReq req ) {
		String res = "" ;  
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		try {
			CommonLoginInformationReq loginReq = req.getLoginInformation() ;
			
			Integer oaCode = StringUtils.isBlank(loginReq.getOaCode()) ? 0 : Integer.valueOf(loginReq.getOaCode());
			LoginMaster findBroker = loginRepo.findByAgencyCodeAndOaCodeAndCompanyId(loginReq.getOaCode() ,oaCode,loginReq.getCompanyId());
			String companies = loginReq.getAttachedCompanies()==null || loginReq.getAttachedCompanies().size()==0 ?"" : String.join(",", loginReq.getAttachedCompanies());
			String regions   = loginReq.getAttachedRegions()==null   || loginReq.getAttachedRegions().size()==0 ?"" : String.join(",", loginReq.getAttachedRegions());
			String customerCode =null;
			List<InsuranceCompanyMaster> insuranceList = companyMasterRepo
					.findTopByCompanyIdOrderByAmendIdDesc(loginReq.getCompanyId());
			if (insuranceList != null && !insuranceList.isEmpty()) {
				String companyName = insuranceList.get(0).getCompanyName();

			    List<LoginUserInfo> loginData =
			        userinfoRepo.findByCompanyName(companyName);
			    
			    customerCode = generateCustomerCode(loginData);
			    
			}
			
			String menuId = loginReq.getMenuId()==null || loginReq.getMenuId().size()==0 ?"" : String.join(",", loginReq.getMenuId());
			
			passwordEnc passEnc = new passwordEnc();
			String newpass =  passEnc.crypt(loginReq.getPassword().trim());
			Instant now = Instant.now();
			Instant after = now.plus(Duration.ofDays(45));
			Date dateAfter = Date.from(after);
			Long countId = Long.valueOf(generateAgencyCode()) ; 
			
			Instant date = now.minus(Duration.ofDays(1));	
			Date firstlogindate = Date.from(date);
			
			// Login Master Insert
			LoginMaster saveLogin = new LoginMaster();
			dozerMapper.map(loginReq, saveLogin);
			saveLogin.setPassword(newpass);
			saveLogin.setLoginId(loginReq.getLoginId());
			if (req.getLoginInformation().getUserType().equalsIgnoreCase("Broker")
					|| req.getLoginInformation().getUserType().equalsIgnoreCase("Issuer")
					|| req.getLoginInformation().getUserType().equalsIgnoreCase("Sales")) {
				saveLogin.setOaCode(Integer.valueOf(countId.toString()));
				saveLogin.setAgencyCode(countId.toString());
				
			} else if(req.getLoginInformation().getUserType().equalsIgnoreCase("User") ) {
				saveLogin.setOaCode(Integer.valueOf(loginReq.getOaCode()));
				saveLogin.setAgencyCode(countId.toString());
			}
			saveLogin.setSubUserType(loginReq.getSubUserTypeDesc()!=null?loginReq.getSubUserTypeDesc():loginReq.getSubUserType());

			if ( req.getLoginInformation().getUserType().equalsIgnoreCase("Issuer") ){
				saveLogin.setSubUserTypeId(loginReq.getSubUserType());
				subUserType(loginReq, saveLogin);
			}

			saveLogin.setEntryDate(new Date());
			saveLogin.setUpdatedDate(new Date());
			saveLogin.setUpdatedBy(loginReq.getCreatedBy());
			saveLogin.setPassword(newpass);
			saveLogin.setPwdCount("0");
			saveLogin.setStatus("D");		//	saveLogin.setLpassDate(dateAfter);
			saveLogin.setLpassDate(firstlogindate);
			saveLogin.setCompanyId(StringUtils.isNotBlank(req.getLoginInformation().getCompanyId() ) ? req.getLoginInformation().getCompanyId() : loginReq.getAttachedCompanies().get(0)  );
			
			List<ListItemValue> mobileCodes = listRepo.findByItemTypeAndStatusAndCompanyIdOrderByItemCodeDesc("MOBILE_CODE" , "Y",saveLogin.getCompanyId() );
			// Login User Details Insert
			CommonPersonalInforReq personalReq = req.getPersonalInformation() ;
			personalReq.setCustomerCode(customerCode);
			LoginUserInfo userInfo = new LoginUserInfo(); 
			dozerMapper.map(personalReq, userInfo);
			userInfo.setLoginId(loginReq.getLoginId());
			userInfo.setAgencyCode(saveLogin.getAgencyCode());
			userInfo.setEntryDate(new Date());
			userInfo.setUpdatedDate(new Date());
			userInfo.setUpdatedBy(loginReq.getCreatedBy());
			userInfo.setStatus("D");
			userInfo.setCountryCode(personalReq.getCountryCode());
//			userInfo.setCityCode(StringUtils.isNotBlank(personalReq.getCityName()) && personalReq.getCityName().matches("[0-9]+") ? Integer.valueOf(personalReq.getCityName()) :null );
			userInfo.setStateCode(StringUtils.isBlank(personalReq.getStateCode())?null:personalReq.getStateCode());
			userInfo.setTaxExemptedYn(StringUtils.isBlank(personalReq.getTaxExemptedYn())?"N":personalReq.getTaxExemptedYn() );
			userInfo.setTaxExemptedCode(req.getPersonalInformation().getTaxExemptedCode());
			userInfo.setCreditLimit(StringUtils.isBlank(personalReq.getCreditLimit())?BigDecimal.ZERO : new BigDecimal(personalReq.getCreditLimit()) );
			userInfo.setAddress1(StringUtils.isBlank(personalReq.getAddress1())?null:personalReq.getAddress1());
			userInfo.setAddress2(StringUtils.isBlank(personalReq.getAddress2())?null:personalReq.getAddress2());
			userInfo.setAddress3(StringUtils.isBlank(personalReq.getAddress3())?null:personalReq.getAddress3());
			userInfo.setCityCode(null != personalReq && personalReq.getCityCode() != null && !personalReq.getCityCode().isEmpty()
					&& personalReq.getCityCode().matches("[0-9]+") ? Integer.valueOf(personalReq.getCityCode()) : 0);
			userInfo.setCityName(personalReq.getCityName());
			userInfo.setMobileCodeDesc(StringUtils.isBlank(personalReq.getMobileCode()) ? "" : personalReq.getMobileCode()); // mobileCodes.stream().filter(o -> o.getItemCode().equalsIgnoreCase(personalReq.getMobileCode()) ).collect(Collectors.toList()).get(0).getItemValue() );
			userInfo.setWhatsappCodeDesc(StringUtils.isBlank(personalReq.getWhatsappCode()) ? "" : personalReq.getWhatsappCode());// mobileCodes.stream().filter(o -> o.getItemCode().equalsIgnoreCase(personalReq.getWhatsappCode()) ).collect(Collectors.toList()).get(0).getItemValue() );
			userInfo.setWhatsappNo(StringUtils.isBlank(personalReq.getWhatsappNo()) ? "" :personalReq.getWhatsappNo());
			userInfo.setContactPersonName(StringUtils.isBlank(personalReq.getContactPersonName()) ? "" :personalReq.getContactPersonName());
			userInfo.setRemarks(StringUtils.isBlank(personalReq.getRemarks()) ? "" :personalReq.getRemarks());
			userInfo.setUserMail(StringUtils.isBlank(personalReq.getUserMail()) ? "" :personalReq.getUserMail());
			userInfo.setBrokerLogo(req.getBrokerLogo());
			if(req.getLoginInformation().getUserType().equalsIgnoreCase("Broker")  || req.getLoginInformation().getUserType().equalsIgnoreCase("Issuer") ) {
				userInfo.setOaCode(saveLogin.getOaCode().toString());
				userInfo.setAgencyCode(saveLogin.getAgencyCode());
				userInfo.setCustomerCode(req.getLoginInformation().getUserType().equalsIgnoreCase("Broker") ?  personalReq.getCustomerCode():"");
				userInfo.setCustomerName(req.getLoginInformation().getUserType().equalsIgnoreCase("Broker") ? personalReq.getUserName() : "");
				
			} else if(req.getLoginInformation().getUserType().equalsIgnoreCase("User") ) {
				userInfo.setOaCode(saveLogin.getOaCode().toString());
				userInfo.setAgencyCode(saveLogin.getAgencyCode());
				userInfo.setCustomerCode(personalReq.getCustomerCode());
				userInfo.setCustomerName(StringUtils.isNotBlank(personalReq.getCustomerName()) ? personalReq.getCustomerName() : personalReq.getUserName());
			}
			
			if((req.getLoginInformation().getUserType().equalsIgnoreCase("Broker")  || req.getLoginInformation().getUserType().equalsIgnoreCase("User")) && StringUtils.isNotBlank(personalReq.getStateCode()) ) {
				List<Tuple> stateCityNames = null;
				List<Tuple> cityNames = null;
				List<RegionMaster> city=null;
				List<StateMaster> state=null;
				if(StringUtils.isNotBlank(personalReq.getCountryCode()) && StringUtils.isNotBlank(personalReq.getCityName()) && personalReq.getCityName().matches("[0-9]+") ) {
					stateCityNames = 	getStateAndCityName(personalReq.getCountryCode() ,personalReq.getCityName());
					state=staterepo.findByStateId(Integer.valueOf(personalReq.getCityName()));
				}
				if(StringUtils.isNotBlank(personalReq.getCountryCode()) && StringUtils.isNotBlank(personalReq.getStateCode())) {
					cityNames = 	getRegionName(personalReq.getCountryCode() ,personalReq.getStateCode());
					city=regrepo.findByRegionCode(personalReq.getStateCode());
				} 
//				userInfo.setCityName(cityNames.get(0).get("regionName") == null ? "" :  cityNames.get(0).get("regionName").toString());
				userInfo.setCityName(city!=null && city.size() > 0 ? ( city.get(0).getRegionName() == null ? "" :  city.get(0).getRegionName()) : personalReq.getCityName() != null ? personalReq.getCityName() : "" );
				userInfo.setStateName(state!=null && state.size() > 0 ? ( state.get(0).getStateName() == null ? "" : state.get(0).getStateName()) :"" );
//				userInfo.setStateName(stateCityNames.get(0).get("stateName") == null ? "" :  stateCityNames.get(0).get("stateName").toString());
				//userInfo.setCountryName(stateCityNames.get(0).get("countryName") == null ? "" :  stateCityNames.get(0).get("countryName").toString());
			}
			 
			
			List<InsuranceCompanyMaster> companyname = insuranceRepo
					.findByCompanyIdOrderByAmendIdDesc(saveLogin.getCompanyId());
			userInfo.setCompanyName(companyname.get(0).getCompanyName());

			String idTypeDesc = getIdTypeDesc(loginReq.getCompanyId(), personalReq.getIdType());
			userInfo.setIdType(personalReq.getIdType());
			userInfo.setIdNumber(personalReq.getIdNumber());
			userInfo.setIdTypeDesc(idTypeDesc);
			userInfo.setLoginAccess(loginReq.getLoginAccess() != null ? loginReq.getLoginAccess() : "");
			userInfo.setReferralAccess(loginReq.getReferralAccess() != null ? loginReq.getReferralAccess() : "");
			userInfo.setReportAccess(loginReq.getReportAccess() != null ? loginReq.getReferralAccess() : "");
			userInfo.setClaimCustomerAccess(loginReq.getClaimCustomerAccess()!=null?loginReq.getClaimCustomerAccess():"");
			userInfo.setCrNo(personalReq.getCRNo());
			userInfo.setCrExpiryDate(personalReq.getCrexpiryDate());
			userInfo.setVatRegNo(personalReq.getVatRegNo());
			userInfo.setVatExpiryDate(personalReq.getVatExpiryDate());
			userInfo.setTaxCardNo(personalReq.getTaxCardNo());
			userInfo.setTaxCardExpiryDate(personalReq.getTaxExpiryDate());
			userInfo.setLicenseCertNo(personalReq.getActivityLicenseno());   
			userInfo.setExpiryDateLicense(personalReq.getActivityExpiryDate() );
			
			loginUserRepo.saveAndFlush(userInfo);
			
		      
		   // Branch Setup
				//String branches  = loginReq.getAttachedBranches()==null  || loginReq.getAttachedBranches().size()==0 ?loginReq.getSubUserType().equalsIgnoreCase("SuperAdmin")?
			String branches = (loginReq.getAttachedBranches() == null
					|| loginReq.getAttachedBranches().size() == 0)
					? ("SuperAdmin".equalsIgnoreCase(loginReq.getSubUserTypeDesc())
					? GetAttachedBranchs(loginReq, countId)
					: "")
					: String.join(",", loginReq.getAttachedBranches());
				saveLogin.setAttachedBranches(branches);
				saveLogin.setAttachedRegions(regions);
				saveLogin.setAttachedCompanies(companies);
				saveLogin.setMenuIds(menuId);
				
				saveLogin.setBrokerCompanyYn(findBroker!=null ? findBroker.getBrokerCompanyYn() : loginReq.getBrokerCompanyYn());

			if (!"bank".equalsIgnoreCase(loginReq.getSubUserTypeDesc())) {
				saveLogin.setBankCode("");
			}
				List<String> moduleIds = loginReq.getModuleIds();
				String moduleIdsStr = (moduleIds == null || moduleIds.isEmpty())
				        ? null
				        : moduleIds.stream()
				                   .map(String::trim)
				                   .collect(Collectors.joining(","));
				saveLogin.setModuleIds(moduleIdsStr);				
				loginRepo.saveAndFlush(saveLogin);


				    AttachBrokerBranchReq branchReq = new AttachBrokerBranchReq();

				    branchReq.setAddress1(null);
				    branchReq.setAddress2(null);
				    branchReq.setBranchCode("234");
				    branchReq.setAttachedCompany(null);
				    branchReq.setBrokerBranchCode(null);
				    branchReq.setBranchType("Main");
				    branchReq.setBrokerBranchName(null);

				    // Insured name
				    branchReq.setCreatedBy(personalReq.getCompanyName());

				    branchReq.setEffectiveDateStart(new Date());
				    branchReq.setCompanyId("100055");
				    branchReq.setLoginId(loginReq.getLoginId());

				    branchReq.setMobile(null);
				    branchReq.setRemarks(null);
				    branchReq.setStatus("Y");
				    branchReq.setSalePointCode("99999");

				    attachBrokerCompanyBranch(branchReq, null);

				List<BrokerCompanyListProductReq> productReqList = new ArrayList<>();

				BrokerCompanyListProductReq productReq =
				        new BrokerCompanyListProductReq();

				productReq.setProductId(126);
				productReq.setProductDesc("Recruitment Agency");

				productReq.setPolicyTypeId("99999");
				productReq.setPolicyTypeDesc("All");
				productReq.setCommissionPercent("0.0");

				productReq.setSumInsuredStart("1");
				productReq.setSumInsuredEnd("50000000");

				productReq.setBackDays("0");
				productReq.setCreditYn("N");
				productReq.setCheckerYn("N");

				productReq.setEffectiveDateStart(new Date());
				productReq.setStatus("Y");

				productReq.setCompanyId("100055");
				productReq.setLoginId(loginReq.getLoginId());

				productReq.setRemarks("none");
				productReq.setCreatedBy(loginReq.getCreatedBy());

				productReqList.add(productReq);

				brokerListCompanyProducts(productReqList);
				
				
		     
			res = saveLogin.getAgencyCode() ;
			
			// Framing Request Save Deposit Cbc Master
			if (StringUtils.isBlank(loginReq.getCbcNo()) || loginReq.getCbcNo() == null || loginReq.getCbcNo() == "") {
				String cbcNo = null;
//			if(StringUtils.isBlank(cbcNo)||"".equalsIgnoreCase(cbcNo)) {
//				cbcNo=loginReq.getCbcNo();
//			}else {
//				cbcNo=null;
//			}
				SaveDepositeMasterReq depoReq = new SaveDepositeMasterReq();
				depoReq.setBrokerId(countId.toString());
				depoReq.setCbcNo(cbcNo);
				depoReq.setDepositAmount(personalReq.getCreditLimit());
				depoReq.setCustomerid(personalReq.getCustomerCode());
				depoReq.setCompanyId(loginReq.getCompanyId());
				depoReq.setLoginId(loginReq.getLoginId());
				depoService.saveDepositeMaster(depoReq);
			}
			
		
			// Branch Id
		} catch (Exception e) {
			e.printStackTrace();
			log.info("" + e.getMessage());
			return null;
		}
		return res;
	}


	private void subUserType(CommonLoginInformationReq loginReq, LoginMaster saveLogin) {
		switch (loginReq.getSubUserType()) {
		case "9":
			saveLogin.setSubUserType("high");
			saveLogin.setSubUserTypeId("9");
			break;
		case "8":
			saveLogin.setSubUserType("both");
			saveLogin.setSubUserTypeId("8");
			break;
		case "7":
			saveLogin.setSubUserType("garage");
			saveLogin.setSubUserTypeId("7");
			break;
		case "6":
			saveLogin.setSubUserType("surveyor");
			saveLogin.setSubUserTypeId("6");
			break;
		case "5":
			saveLogin.setSubUserType("claimofficer");
			saveLogin.setSubUserTypeId("5");
			break;
		case "4":
			saveLogin.setSubUserType("both");
			saveLogin.setSubUserTypeId("4");
			break;
			
		case "3":
			saveLogin.setSubUserType("high");
			saveLogin.setSubUserTypeId("3");
			break;
		case "2":
			saveLogin.setSubUserType("low");
			saveLogin.setSubUserTypeId("2");
			break;
		case "1":
			saveLogin.setSubUserType("SuperAdmin");
			saveLogin.setSubUserTypeId("1");
			break;
		case "10":
			saveLogin.setSubUserType("bidder");
			saveLogin.setSubUserTypeId("10");
			break;
		default:
			saveLogin.setSubUserType(loginReq.getSubUserTypeDesc()!=null?loginReq.getSubUserTypeDesc():loginReq.getSubUserType());
			saveLogin.setSubUserTypeId(loginReq.getSubUserTypeDesc());
			break;
		}
	}
	
	 private String GetAttachedBranchs(CommonLoginInformationReq loginReq, Long oacode) {
		 log.info("Enter into GetAttachedBranchs || "+loginReq.toString());
		 try {
			List<String> result = new ArrayList<>();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhssmmss");
			LoginUserInfo loginUserInfo = loginUserRepo.findByLoginId(loginReq.getLoginId());
			loginReq.getAttachedCompanies().forEach(k -> {
				try {
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
					Root<BranchMaster> bmRoot = cq.from(BranchMaster.class);
					
					Subquery<Integer> bmAmd = cq.subquery(Integer.class);
					Root<BranchMaster> bmAmdRoot = bmAmd.from(BranchMaster.class);
					bmAmd.select(cb.max(bmAmdRoot.get("amendId"))).where(cb.equal(bmAmdRoot.get("companyId"), bmRoot.get("companyId")),
							cb.equal(bmAmdRoot.get("status"), bmRoot.get("status")),cb.equal(bmAmdRoot.get("branchCode"), bmRoot.get("branchCode")));
					
					cq.multiselect(bmRoot.get("branchCode").alias("branchCode"),bmRoot.get("branchName").alias("branchName"))
					.where(cb.equal(bmRoot.get("companyId"), k),cb.equal(bmRoot.get("status"), "Y"),
							cb.equal(bmRoot.get("amendId"), bmAmd));
					
					List<Tuple> queryList = em.createQuery(cq).getResultList();
					List<String> branch_code_list = queryList.stream().filter(f -> f.get("branchCode")!=null).map(m -> m.get("branchCode").toString()).collect(Collectors.toList());
					result.addAll(branch_code_list);
					
					queryList.forEach(q -> {
						LoginBranchMaster findBranch = loginBranchRepo.findByBranchCodeAndLoginIdAndCompanyId(
								(q.get("branchCode")==null?"":q.get("branchCode").toString()), loginReq.getLoginId(),  loginReq.getCompanyId());

						LoginBranchMaster save = dozerMapper.map(loginReq, LoginBranchMaster.class);
						if (findBranch != null) {
							// Delete Old Record
							loginBranchRepo.delete(findBranch);
							// Save in Arch tables
							String archId = "AI-" + idf.format(new Date());
							LoginBranchMasterArch loginArch = dozerMapper.map(findBranch, LoginBranchMasterArch.class);
							loginArch.setArchId(archId);
							loginBrokerArchRepo.saveAndFlush(loginArch);

							save.setEntryDate(findBranch.getEntryDate());
							save.setCreatedBy(findBranch.getCreatedBy());
							save.setUpdatedBy(loginReq.getCreatedBy());
							save.setUpdatedDate(new Date());
						} else {
							save.setEntryDate(new Date());
							save.setCreatedBy(loginReq.getCreatedBy());
							save.setUpdatedBy(loginReq.getCreatedBy());
							save.setUpdatedDate(new Date());
						}

						save.setOaCode(oacode.intValue());
						save.setAgencyCode(oacode.intValue());
						save.setAttachedBranch(result.stream().distinct().collect(Collectors.joining(",")));
						save.setAttachedCompany(String.join(",", loginReq.getAttachedCompanies()));
						save.setUserType(loginReq.getUserType());
						save.setSubUserType(loginReq.getSubUserType());
						save.setBranchCode(q.get("branchCode")==null?"":q.get("branchCode").toString());
						save.setBrokerBranchCode(q.get("branchCode")==null?"":q.get("branchCode").toString());
						save.setBranchName(q.get("branchName")==null?"":q.get("branchName").toString());
						save.setBrokerBranchName(q.get("branchName")==null?"":q.get("branchName").toString());
						save.setCustomerCode(loginUserInfo.getCustomerCode());
						save.setCustomerName(loginUserInfo.getCustomerName());
						save.setCompanyId(loginReq.getAttachedCompanies().get(0));

						loginBranchRepo.save(save);
					});
					
					
				}catch(Exception e) {
					log.info("Exception is --->" + k+" "+e.getMessage());
					e.printStackTrace();
				}
			});
			
			return result.stream().distinct().collect(Collectors.joining(",")); 
			 
		}catch(Exception e) {
			log.info("Exception in GetAttachedBranchs --->" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}


	public String generateAgencyCode() {
	       try {
	        	SeqAgencycode entity;
	            entity = seqAgencyRepo.save(new SeqAgencycode());          
	            return String.format("%05d",entity.getAgencyCode()) ;
	        } catch (Exception ex) {
				log.error(ex);
	            return null;
	        }
	       
	    }
	 
	 public List<Tuple> getRegionName( String countryId , String cityCode  ) {
			List<Tuple> list = new ArrayList<Tuple>();
			try {
				Date today = new Date();
				Calendar cal = new GregorianCalendar();
				cal.setTime(today);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 1);
				today = cal.getTime();
				cal.set(Calendar.HOUR_OF_DAY, 1);
				cal.set(Calendar.MINUTE, 1);
				Date todayEnd = cal.getTime();

				// Criteria
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				// Find All
				Root<RegionMaster> c = query.from(RegionMaster.class);

				// Select
				query.multiselect( c.get("regionName").alias("regionName")  );

				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(c.get("regionName")));

				// AmendId Max Filter
				Subquery<Long> amendId = query.subquery(Long.class);
				Root<RegionMaster> ocpm1 = amendId.from(RegionMaster.class);
				amendId.select(cb.max(ocpm1.get("amendId")));
				Predicate a1 = cb.equal(c.get("regionCode"),ocpm1.get("regionCode") );
				Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				amendId.where(a1,a3);
			
				
			    // Where	
			
				Predicate n1 = cb.equal(c.get("amendId"), amendId);		
				Predicate n2 = cb.equal(c.get("regionCode"),cityCode) ;
				Predicate n3 = cb.equal(c.get("countryId"),countryId) ;
				query.where(n1 ,n2,n3).orderBy(orderList);
				// Get Result
				TypedQuery<Tuple> result = em.createQuery(query);
				list = result.getResultList();

			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return list;
		}

	
	 public List<Tuple> getStateAndCityName(String countryId , String stateId  ) {
			List<Tuple> list = new ArrayList<Tuple>();
			try {
				Date today = new Date();
				// Find Latest Record
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
				Root<StateMaster> s = query.from(StateMaster.class);
				
				Subquery<Long> amendId2 = query.subquery(Long.class);
				Root<StateMaster> ocpm2 = amendId2.from(StateMaster.class);
				amendId2.select(cb.max(ocpm2.get("amendId")));
				Predicate seff1 = cb.equal(ocpm2.get("stateId"), s.get("stateId"));
				Predicate seff2 = cb.equal(ocpm2.get("countryId"), s.get("countryId"));
				Predicate seff3 = cb.equal(ocpm2.get("status"),s.get("status"));
				amendId2.where(seff1,seff2,seff3);
				
				// Select
				query.multiselect( s.get("stateName").alias("stateName")  );
				Predicate s1 = cb.equal(s.get("regionCode"), stateId);
				Predicate s2 = cb.equal(s.get("countryId"),countryId);
				Predicate s4 = cb.equal(s.get("amendId"), amendId2);
				
				query.where(s1,s2,s4);
				// Get Result
				TypedQuery<Tuple> result = em.createQuery(query);
				list = result.getResultList();
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info(e.getMessage());
				return null;
			}
			return list;
		}
	
	// Login Save Method
	
	@Transactional
	public String updateLoginDetails(CommonLoginCreationReq req ) {
		String res = "";
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhssmmss"); 
		try {
			// Find Data 
			String loginId = req.getLoginInformation().getLoginId() ;
			LoginMaster findLogin = loginRepo.findByLoginId(loginId);
			String status = findLogin.getStatus();
			if(StringUtils.isNotBlank(status) && "T".equalsIgnoreCase(status) && "Y".equalsIgnoreCase(req.getLoginInformation().getStatus()))
			{
				findLogin.setStatus("Y");
				findLogin.setPwdCount("0");
			}
			LoginUserInfo findUserInfo = loginUserRepo.findByLoginId(loginId);
			
			// Delete Old Records
			LoginMaster  updateLogin = new  LoginMaster ();
			dozerMapper.map(findLogin, updateLogin);
			loginRepo.delete(findLogin);
			LoginUserInfo updateUser = findUserInfo;
			if(findUserInfo!=null)
			loginUserRepo.delete(findUserInfo);
			
			// Save in Arch tables
			String archId = "AI-" + idf.format(new Date());
			LoginMasterArch  loginArch = dozerMapper.map(findLogin, LoginMasterArch.class )  ;
			loginArch.setArchId(archId);
			loginArchRepo.saveAndFlush(loginArch);
			LoginUserInfoArch userArch = dozerMapper.map(findUserInfo, LoginUserInfoArch.class )  ;  
			userArch.setArchId(archId);
			loginUserArchRepo.saveAndFlush(userArch);
			
			// Update Login Master
			CommonLoginInformationReq loginReq = req.getLoginInformation() ;
			String oaCode = loginReq.getOaCode();
			Integer oaCodeInt = (oaCode == null || oaCode.trim().isEmpty()) ? 0 : Integer.parseInt(oaCode);

			LoginMaster findBroker = loginRepo.findByAgencyCodeAndOaCodeAndCompanyId(
			        oaCode,
			        oaCodeInt,
			        loginReq.getCompanyId()
			);
			
			// Branch Setup
			String branches  = loginReq.getAttachedBranches()==null  || loginReq.getAttachedBranches().size()==0 ?"" : String.join(",", loginReq.getAttachedBranches());
			String regions   = loginReq.getAttachedRegions()==null   || loginReq.getAttachedRegions().size()==0 ?"" : String.join(",", loginReq.getAttachedRegions());
			String companies = loginReq.getAttachedCompanies()==null || loginReq.getAttachedCompanies().size()==0 ?"" : String.join(",", loginReq.getAttachedCompanies());
			String menuId = loginReq.getMenuId()==null || loginReq.getMenuId().size()==0 ?"" : String.join(",", loginReq.getMenuId());
			dozerMapper.map(loginReq, updateLogin);
			
			
			
			updateLogin.setCreatedBy(findLogin.getCreatedBy() );
			if (req.getLoginInformation().getUserType().equalsIgnoreCase("Broker")
					|| req.getLoginInformation().getUserType().equalsIgnoreCase("Issuer")
					|| req.getLoginInformation().getUserType().equalsIgnoreCase("Sales")) {
				updateLogin.setOaCode(Integer.valueOf(loginReq.getOaCode()));
				updateLogin.setAgencyCode(loginReq.getOaCode());
				
			} else if(req.getLoginInformation().getUserType().equalsIgnoreCase("User") ) {
				updateLogin.setOaCode(Integer.valueOf(loginReq.getOaCode()));
				updateLogin.setAgencyCode(loginReq.getAgencyCode());
			}
//			Instant now = Instant.now();
//			Instant date = now.minus(Duration.ofDays(1));	
//			Date firstlogindate = Date.from(date);
			
			if(StringUtils.isNotBlank(loginReq.getPassword()) ) {
				passwordEnc passEnc = new passwordEnc();
				String newpass =  passEnc.crypt(loginReq.getPassword().trim());
				updateLogin.setPassword(newpass );
			} else {
				updateLogin.setPassword(StringUtils.isNotBlank(loginReq.getPassword()) ? loginReq.getPassword() : findLogin.getPassword()  );
			}
			
//			updateLogin.setSubUserType(loginReq.getSubUserTypeDesc() == null ||  
//					loginReq.getSubUserTypeDesc().isBlank() ?  null :  loginReq.getSubUserType() );
			
			if(loginReq.getUserType().equalsIgnoreCase("Issuer")) {
				updateLogin.setSubUserType(loginReq.getSubUserTypeDesc());
				updateLogin.setSubUserTypeId(loginReq.getSubUserType());
				subUserType(loginReq, updateLogin);
			}
			else {
				updateLogin.setSubUserType(loginReq.getSubUserType());

			}
			updateLogin.setPwdCount(findLogin.getPwdCount() );	
			updateLogin.setLpass1(findLogin.getLpass1());
			updateLogin.setLpass2(findLogin.getLpass2());
			updateLogin.setLpass3(findLogin.getLpass3());
			updateLogin.setLpass4(findLogin.getLpass4());
			updateLogin.setLpass5(findLogin.getLpass5());
			updateLogin.setLpassDate(updateLogin.getLpassDate());
			updateLogin.setUpdatedDate(new Date());
			updateLogin.setUpdatedBy(loginReq.getCreatedBy());
			updateLogin.setLoginId(loginReq.getLoginId());
			updateLogin.setAttachedBranches(branches);
			updateLogin.setAttachedRegions(regions);
			updateLogin.setAttachedCompanies(companies);
			updateLogin.setMenuIds(findLogin.getMenuIds());
			updateLogin.setCompanyId(StringUtils.isNotBlank(req.getLoginInformation().getCompanyId() ) ? req.getLoginInformation().getCompanyId() : loginReq.getAttachedCompanies().get(0)  );
		
			updateLogin.setBrokerCompanyYn(
					findBroker != null ? findBroker.getBrokerCompanyYn() : loginReq.getBrokerCompanyYn());

			updateUser.setLoginAccess(
					req.getLoginInformation().getLoginAccess() != null ? req.getLoginInformation().getLoginAccess()
							: "");
			updateUser.setReferralAccess(req.getLoginInformation().getReferralAccess() != null
					? req.getLoginInformation().getReferralAccess()
					: "");
			updateUser.setReportAccess(req.getLoginInformation().getReportAccess() != null
					? req.getLoginInformation().getReportAccess()
					: "");
			updateUser.setClaimCustomerAccess(req.getLoginInformation().getClaimCustomerAccess()!=null?req.getLoginInformation().getClaimCustomerAccess():"");
			if (!loginReq.getSubUserType().equalsIgnoreCase("bank")) {
				updateLogin.setBankCode("");
			}
			List<String> moduleIds = loginReq.getModuleIds();
			String moduleIdsStr = (moduleIds == null || moduleIds.isEmpty()) ? null : moduleIds.stream()
			                   .map(String::trim)
			                   .collect(Collectors.joining(","));
			updateLogin.setModuleIds(moduleIdsStr);	
			loginRepo.saveAndFlush(updateLogin);
			
			log.info( "Login Master Updated Details ---> " + json.toJson(updateLogin) );
			
			// Update User Info 
			CommonPersonalInforReq personalReq = req.getPersonalInformation() ;
			dozerMapper.map(personalReq , updateUser);
			updateLogin.setCreatedBy(findLogin.getCreatedBy() );
			updateUser.setLoginId(loginReq.getLoginId());
			updateUser.setBrokerLogo(req.getBrokerLogo());
			updateUser.setOaCode(updateLogin.getOaCode().toString());
			updateUser.setAgencyCode(updateLogin.getAgencyCode());
			updateUser.setUpdatedDate(new Date());
			updateUser.setUpdatedBy(loginReq.getCreatedBy());
			updateUser.setStatus(updateLogin.getStatus());
			List<ListItemValue> mobileCodes = listRepo.findByItemTypeAndStatusAndCompanyIdOrderByItemCodeDesc("MOBILE_CODE" , "Y", findLogin.getCompanyId());
			updateUser.setMobileCodeDesc(StringUtils.isBlank(personalReq.getMobileCode()) ? "" : mobileCodes.stream().filter(o -> o.getItemCode().equalsIgnoreCase(personalReq.getMobileCode()) ).collect(Collectors.toList()).get(0).getItemValue() );
			updateUser.setWhatsappCodeDesc(StringUtils.isBlank(personalReq.getWhatsappCode()) ? "" : mobileCodes.stream().filter(o -> o.getItemCode().equalsIgnoreCase(personalReq.getWhatsappCode()) ).collect(Collectors.toList()).get(0).getItemValue() );
			updateUser.setTaxExemptedYn(StringUtils.isBlank(personalReq.getTaxExemptedYn())?"N":personalReq.getTaxExemptedYn() );
			updateUser.setTaxExemptedCode(personalReq.getTaxExemptedCode());
			updateUser.setCreditLimit(StringUtils.isBlank(personalReq.getCreditLimit())?BigDecimal.ZERO : new BigDecimal(personalReq.getCreditLimit()) );
			
			if(req.getLoginInformation().getUserType().equalsIgnoreCase("Broker")  || req.getLoginInformation().getUserType().equalsIgnoreCase("Issuer") ) {
				updateUser.setOaCode(loginReq.getOaCode());
				updateUser.setAgencyCode(loginReq.getOaCode());
				updateUser.setCustomerCode(req.getLoginInformation().getUserType().equalsIgnoreCase("Broker") ?  personalReq.getCustomerCode():"");
				updateUser.setCustomerName(req.getLoginInformation().getUserType().equalsIgnoreCase("Broker") ? personalReq.getUserName() : "");
			} else if(req.getLoginInformation().getUserType().equalsIgnoreCase("User") ) {
				updateUser.setOaCode(loginReq.getOaCode());
				updateUser.setAgencyCode(loginReq.getAgencyCode());
				updateUser.setCustomerCode( personalReq.getCustomerCode());
				updateUser.setCustomerName(StringUtils.isNotBlank(personalReq.getCustomerName()) ? personalReq.getCustomerName() : personalReq.getUserName());
				
			}
			
			if(req.getLoginInformation().getUserType().equalsIgnoreCase("Broker")  || req.getLoginInformation().getUserType().equalsIgnoreCase("User")  ) {
				List<Tuple> stateCityNames = 	getStateAndCityName(personalReq.getCountryCode()  , req.getPersonalInformation().getStateCode());
				
			//	updateUser.setCityName(stateCityNames.get(0).get("cityName") == null ? "" :  stateCityNames.get(0).get("cityName").toString());
				if(stateCityNames!=null && stateCityNames.size()>0) {
					updateUser.setStateName(stateCityNames.get(0).get("stateName") == null ? "" :  stateCityNames.get(0).get("stateName").toString());
				}
			//	updateUser.setCountryName(stateCityNames.get(0).get("countryName") == null ? "" :  stateCityNames.get(0).get("countryName").toString());;
			}
			
			List<InsuranceCompanyMaster> companyname=	insuranceRepo.findByCompanyIdOrderByAmendIdDesc(updateLogin.getCompanyId());			
			updateUser.setCompanyName(companyname.get(0).getCompanyName());
			
			String idTypeDesc = getIdTypeDesc(loginReq.getCompanyId(),personalReq.getIdType());
			updateUser.setIdType(personalReq.getIdType());
			updateUser.setIdNumber(personalReq.getIdNumber());
			updateUser.setIdTypeDesc(idTypeDesc);  
			updateUser.setCrNo(personalReq.getCRNo());
			updateUser.setCrExpiryDate(personalReq.getCrexpiryDate());
			updateUser.setVatRegNo(personalReq.getVatRegNo());
			updateUser.setVatExpiryDate(personalReq.getVatExpiryDate());
			updateUser.setTaxCardNo(personalReq.getTaxCardNo());
			updateUser.setTaxCardExpiryDate(personalReq.getTaxExpiryDate());
			updateUser.setLicenseCertNo(personalReq.getActivityLicenseno());   
			updateUser.setExpiryDateLicense(personalReq.getActivityExpiryDate());
			
			loginUserRepo.saveAndFlush(updateUser);
			log.info( "Login User Info Updated Details ---> " + json.toJson(updateUser) );
			
			/*if(loginReq.getUserType().equalsIgnoreCase("Broker") ) {
		    	  
		    	  marineapi.createMarineBroker(updateUser,"edit", req,updateLogin); //insert
		    	  
		      }else  if(loginReq.getUserType().equalsIgnoreCase("User") ) {
		    	  
		    	  marineapi.createMarineUser(updateUser, updateLogin, req, "edit"); //user
		    	  
		      }else  if(loginReq.getUserType().equalsIgnoreCase("Issuer") && loginReq.getSubUserType().equalsIgnoreCase("SuperAdmin")  ) {
		    	  
		    	  marineapi.createMarineAdmin(updateUser, updateLogin, "edit", req); //Admin
		    	  
		      }else  if(loginReq.getUserType().equalsIgnoreCase("Issuer") && !loginReq.getSubUserType().equalsIgnoreCase("SuperAdmin") ) {
		    	  
		    	  marineapi.createMarineIssuer(updateUser, updateLogin, req,  "edit");
		      }*/
  
			res = updateLogin.getAgencyCode() ;
			
			/*// Branch Setup	
			String branches  =  String.join(",", loginReq.getAttachedBranches());
			String regions   =  String.join(",", loginReq.getAttachedRegions());
			String companies =  String.join(",", loginReq.getAttachedCompanies());
			updateLogin.setAttachedBranches(branches);
			updateLogin.setAttachedRegions(regions);
			updateLogin.setAttachedCompanies(companies);
			 
			 */
			
			// Framing Request Update Deposit Cbc Master
			if (StringUtils.isBlank(loginReq.getCbcNo()) || loginReq.getCbcNo() == null || loginReq.getCbcNo() == "") {
				String cbcNo = null;

				SaveDepositeMasterReq depoReq = new SaveDepositeMasterReq();
				depoReq.setBrokerId(loginReq.getAgencyCode());
				depoReq.setCbcNo(cbcNo);
				depoReq.setDepositAmount(personalReq.getCreditLimit());
				depoReq.setCustomerid(personalReq.getCustomerCode());
				depoReq.setCompanyId(loginReq.getCompanyId());
				depoReq.setLoginId(loginReq.getLoginId());
				depoService.saveDepositeMaster(depoReq);
			}
		
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}
/*
    
    public boolean delete(long id) {
        try {
            repository.deleteById(id);
            return true;

        } catch (Exception ex) {
			log.error(ex);
            return false;
        }
    }

 */

	
	public List<LoginBrokerDetailsGetRes> getBrokerLoginDetailsSubUserType(BrokerLoginGridReq req) {
		List<LoginBrokerDetailsGetRes> resList = new ArrayList<LoginBrokerDetailsGetRes>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
		try { 
			// Limit Offset
			//int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			//int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
			List<Tuple> list = new ArrayList<Tuple>();
			
			// Find All
			Root<LoginMaster> l = query.from(LoginMaster.class);

			
			// Select
			query.multiselect( l.get("loginId").alias("LoginId") , l.get("attachedBranches").alias("AttachedBranches") ,
					l.get("attachedCompanies").alias("AttachedCompanies") ,
					l.get("oaCode").alias("OaCode"));
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(l.get("entryDate")));
			
			// Where
			Predicate n1 = cb.equal(l.get("userType"), "Broker");
			Predicate n2 = cb.equal(l.get("status"), "Y");
			
			if(StringUtils.isNotBlank( req.getSubUserType())  ) {
				if("b2b".equals(req.getSubUserType())||"b2c".equals(req.getSubUserType())) {
				Predicate n3 = cb.equal(l.get("subUserType"), req.getSubUserType());
				query.where(n1,n2,n3).orderBy(orderList);
				}
				
			} else if("All".equals(req.getSubUserType())) {
				query.where(n1,n2).orderBy(orderList);
			}
			
			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			//result.setFirstResult(limit * offset);
			//result.setMaxResults(offset);
			list = result.getResultList();
			
			for (Tuple data :  list) {
				LoginBrokerDetailsGetRes res = new LoginBrokerDetailsGetRes();
				res.setLoginId(data.get("LoginId") ==null?"" :data.get("LoginId").toString());	
				res.setOaCode(data.get("OaCode") ==null?"" :data.get("OaCode").toString());
				res.setCompanyId(data.get("AttachedCompanies") ==null?"" :data.get("AttachedCompanies").toString());
				res.setBranchCodes(data.get("AttachedBranches") ==null?"" :data.get("AttachedBranches").toString());
				resList.add(res);				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return resList;
	}

	
	public List<LoginBrokerDetailsGridRes> getBrokerLoginDetailsByUserType(BrokerLoginGridReq req) {
		List<LoginBrokerDetailsGridRes> resList = new ArrayList<LoginBrokerDetailsGridRes>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
		ModelMapper mapper = new ModelMapper(); 
		try { 
			// Limit Offset
			//int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			//int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginDetailsCriteriaRes> query = cb.createQuery(LoginDetailsCriteriaRes.class);
			List<LoginDetailsCriteriaRes> list = new ArrayList<LoginDetailsCriteriaRes>();
			
			// Find All
			Root<LoginMaster> l = query.from(LoginMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
			
			// Select
			query.multiselect( l.get("loginId").alias("loginId") , l.get("createdBy").alias("createdBy") , 
					l.get("entryDate").alias("entryDate") , l.get("updatedDate").alias("updatedDate") ,
					l.get("updatedBy").alias("updatedBy") , l.get("attachedBranches").alias("attachedBranches") ,
					l.get("status").alias("status") ,  l.get("oaCode").alias("oaCode"),l.get("agencyCode").alias("agencyCode") ,l.get("subUserType").alias("subUserType"), 
					 l.get("bankCode").alias("bankCode") ,
					//cb.selectCase().when(l.get("bankCode").isNotNull(), l.get("bankCode") ).otherwise("No Bank").alias("BankCode") ,
					u.get("userName").alias("userName")  ,
					u.get("userMobile").alias("userMobile") , u.get("userMail").alias("userMail")  ,  l.get("companyId").alias("companyId"),u.get("regulatoryCode").alias("regulatoryCode")  );
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(l.get("entryDate")));
			
			// Where
			Predicate n1 = cb.equal(l.get("loginId"), u.get("loginId"));
			Predicate n2 = cb.equal(l.get("userType"), req.getUserType());
			Predicate n4 = cb.equal(l.get("companyId"), req.getCompanyId());
			
			if(StringUtils.isNotBlank( req.getSubUserType())  ) {
				Predicate n3 = cb.equal(l.get("subUserType"), req.getSubUserType());
				query.where(n1,n2,n3,n4).orderBy(orderList);
				
			} else {
				query.where(n1,n2,n4).orderBy(orderList);
			}
			
			// Get Result
			TypedQuery<LoginDetailsCriteriaRes> result = em.createQuery(query);
			//result.setFirstResult(limit * offset);
			//result.setMaxResults(offset);
			list = result.getResultList();
			
			Type listType = new TypeToken<List<LoginBrokerDetailsGridRes>>(){}.getType();
			resList = mapper.map(list,listType);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return resList;
	}
	
	
	public List<LoginUserGridRes> getUserLoginDetailsByUserType(UserLoginGridReq req) {
		List<LoginUserGridRes> resList = new ArrayList<LoginUserGridRes>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
		ModelMapper mapper = new ModelMapper(); 
		try { 
			// Limit Offset
			//int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			//int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginDetailsCriteriaRes> query = cb.createQuery(LoginDetailsCriteriaRes.class);
			List<LoginDetailsCriteriaRes> list = new ArrayList<LoginDetailsCriteriaRes>();
			
			// Find All
			Root<LoginMaster> l = query.from(LoginMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
			
			// Select
			query.multiselect( l.get("loginId").alias("loginId") , l.get("createdBy").alias("createdBy") , 
					l.get("entryDate").alias("entryDate") , l.get("updatedDate").alias("updatedDate") ,
					l.get("updatedBy").alias("updatedBy") , l.get("attachedBranches").alias("attachedBranches") ,
					l.get("status").alias("status") ,  l.get("oaCode").alias("oaCode"),l.get("agencyCode").alias("agencyCode") ,l.get("subUserType").alias("subUserType"), 
					 l.get("bankCode").alias("bankCode") ,
					//cb.selectCase().when(l.get("bankCode").isNotNull(), l.get("bankCode") ).otherwise("No Bank").alias("BankCode") ,
					u.get("userName").alias("userName")  ,
					u.get("userMobile").alias("userMobile") , u.get("userMail").alias("userMail") ,  l.get("companyId").alias("companyId") ,u.get("regulatoryCode").alias("regulatoryCode")     );
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(l.get("entryDate")));
			
			// Where
			Predicate n1 = cb.equal(l.get("loginId"), u.get("loginId"));
			Predicate n2 = cb.equal(l.get("userType"), req.getUserType());
			Predicate n4 = cb.equal(l.get("oaCode"), req.getOaCode());
			Predicate n5 = cb.equal(l.get("companyId"), req.getCompanyId());
			if(StringUtils.isNotBlank( req.getSubUserType())  ) {
				Predicate n3 = cb.equal(l.get("subUserType"), req.getSubUserType());
				query.where(n1,n2,n3,n4,n5).orderBy(orderList);
				
			} else {
				query.where(n1,n2,n4,n5).orderBy(orderList);
			}
			
			// Get Result
			TypedQuery<LoginDetailsCriteriaRes> result = em.createQuery(query);
			//result.setFirstResult(limit * offset);
			//result.setMaxResults(offset);
			list = result.getResultList();
			

			Type listType = new TypeToken<List<LoginUserGridRes>>(){}.getType();
			resList = mapper.map(list,listType);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return resList;
	}


	
	public List<LoginIssuerGridRes> getIssuerLoginDetailsByUserType(IssuerLoginGridReq req) {
		List<LoginIssuerGridRes> resList = new ArrayList<LoginIssuerGridRes>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
		ModelMapper mapper = new ModelMapper(); 
		try { 
			// Limit Offset
			//int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			//int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginDetailsCriteriaRes> query = cb.createQuery(LoginDetailsCriteriaRes.class);
			List<LoginDetailsCriteriaRes> list = new ArrayList<LoginDetailsCriteriaRes>();
			
			// Find All
			Root<LoginMaster> l = query.from(LoginMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
			

			// Select
			query.multiselect( l.get("loginId").alias("loginId") , l.get("createdBy").alias("createdBy") , 
					l.get("entryDate").alias("entryDate") , l.get("updatedDate").alias("updatedDate") ,
					l.get("updatedBy").alias("updatedBy") , l.get("attachedBranches").alias("attachedBranches") ,
					l.get("status").alias("status") ,  l.get("oaCode").alias("oaCode"),l.get("agencyCode").alias("agencyCode") ,l.get("subUserType").alias("subUserType"), 
					 l.get("bankCode").alias("bankCode") ,
					//cb.selectCase().when(l.get("bankCode").isNotNull(), l.get("bankCode") ).otherwise("No Bank").alias("BankCode") ,
					u.get("userName").alias("userName")  , 
					u.get("userMobile").alias("userMobile") , u.get("userMail").alias("userMail")  ,  l.get("companyId").alias("companyId") ,u.get("regulatoryCode").alias("regulatoryCode")   
					,l.get("subUserTypeId").alias("subUserTypeId"));
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(l.get("entryDate")));
			
			// Where
			Predicate n1 = cb.equal(l.get("loginId"), u.get("loginId"));
			Predicate n2 = cb.equal(l.get("userType"), req.getUserType());
			Predicate n4 = cb.equal(l.get("companyId"), req.getCompanyId());
			Predicate n5 = cb.like(l.get("attachedCompanies"), "%"+req.getCompanyId()+"%");
			Predicate n6 = cb.or(n4,n5);
			if(StringUtils.isNotBlank( req.getSubUserType())  ) {
				Predicate n3 = cb.equal(l.get("subUserType"), req.getSubUserType());
				query.where(n1,n2,n3,n6).orderBy(orderList);
				
			} else {
				query.where(n1,n2,n6).orderBy(orderList);
			}
			
			// Get Result
			TypedQuery<LoginDetailsCriteriaRes> result = em.createQuery(query);
			//result.setFirstResult(limit * offset);
			//result.setMaxResults(offset);
			list = result.getResultList();
			
			Type listType = new TypeToken<List<LoginIssuerGridRes>>(){}.getType();
			resList = mapper.map(list,listType);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return resList;
	}


	
	public List<LoginBrokerDetailsGridRes> getBrokerActiveDetailsByUserType(BrokerActiveGridReq req) {
		List<LoginBrokerDetailsGridRes> resList = new ArrayList<LoginBrokerDetailsGridRes>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
		ModelMapper mapper = new ModelMapper(); 
		try { 
			// Limit Offset
			//int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			//int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginDetailsCriteriaRes> query = cb.createQuery(LoginDetailsCriteriaRes.class);
			List<LoginDetailsCriteriaRes> list = new ArrayList<LoginDetailsCriteriaRes>();
			
			// Find All
			Root<LoginMaster> l = query.from(LoginMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
			

			// Select
			query.multiselect( l.get("loginId").alias("loginId") , l.get("createdBy").alias("createdBy") , 
					l.get("entryDate").alias("entryDate") , l.get("updatedDate").alias("updatedDate") ,
					l.get("updatedBy").alias("updatedBy") , l.get("attachedBranches").alias("attachedBranches") ,
					l.get("status").alias("status") ,  l.get("oaCode").alias("oaCode"),l.get("agencyCode").alias("agencyCode") ,l.get("subUserType").alias("subUserType"), 
					 l.get("bankCode").alias("bankCode") ,
					//cb.selectCase().when(l.get("bankCode").isNotNull(), l.get("bankCode") ).otherwise("No Bank").alias("BankCode") ,
					u.get("userName").alias("userName")  ,
					u.get("userMobile").alias("userMobile") , u.get("userMail").alias("userMail")  ,  l.get("companyId").alias("companyId")   ,u.get("regulatoryCode").alias("regulatoryCode")  );
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(l.get("entryDate")));
			
			// Where
			Predicate n1 = cb.equal(l.get("loginId"), u.get("loginId"));
			Predicate n2 = cb.equal(l.get("userType"), req.getUserType());
			Predicate n3 = cb.equal(l.get("status"), "Y");
			
			if(StringUtils.isNotBlank( req.getSubUserType())  ) {
				Predicate n4 = cb.equal(l.get("subUserType"), req.getSubUserType());
				query.where(n1,n2,n3,n4).orderBy(orderList);
				
			} else {
				query.where(n1,n2,n3).orderBy(orderList);
			}
			
			// Get Result
			TypedQuery<LoginDetailsCriteriaRes> result = em.createQuery(query);
			//result.setFirstResult(limit * offset);
			//result.setMaxResults(offset);
			list = result.getResultList();
			
			Type listType = new TypeToken<List<LoginBrokerDetailsGridRes>>(){}.getType();
			resList = mapper.map(list,listType);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return resList;
	}


	
	public List<LoginUserGridRes> getUserActiveDetailsByUserType(UserActiveGridReq req) {
		List<LoginUserGridRes> resList = new ArrayList<LoginUserGridRes>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
		ModelMapper mapper = new ModelMapper(); 
		try { 
			// Limit Offset
			//	int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			//int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginDetailsCriteriaRes> query = cb.createQuery(LoginDetailsCriteriaRes.class);
			List<LoginDetailsCriteriaRes> list = new ArrayList<LoginDetailsCriteriaRes>();
			
			// Find All
			Root<LoginMaster> l = query.from(LoginMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
			

			// Select
			query.multiselect( l.get("loginId").alias("loginId") , l.get("createdBy").alias("createdBy") , 
					l.get("entryDate").alias("entryDate") , l.get("updatedDate").alias("updatedDate") ,
					l.get("updatedBy").alias("updatedBy") , l.get("attachedBranches").alias("attachedBranches") ,
					l.get("status").alias("status") ,  l.get("oaCode").alias("oaCode"),l.get("agencyCode").alias("agencyCode") ,l.get("subUserType").alias("subUserType"), 
					 l.get("bankCode").alias("bankCode") ,
					//cb.selectCase().when(l.get("bankCode").isNotNull(), l.get("bankCode") ).otherwise("No Bank").alias("BankCode") ,
					u.get("userName").alias("userName")  ,
					u.get("userMobile").alias("userMobile") , u.get("userMail").alias("userMail")  ,  l.get("companyId").alias("companyId")  ,u.get("regulatoryCode").alias("regulatoryCode")   );
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(l.get("entryDate")));
			
			// Where
			Predicate n1 = cb.equal(l.get("loginId"), u.get("loginId"));
			Predicate n2 = cb.equal(l.get("userType"), req.getUserType());
			Predicate n3 = cb.equal(l.get("status"), "Y");
			
			if(StringUtils.isNotBlank( req.getSubUserType())  ) {
				Predicate n4 = cb.equal(l.get("subUserType"), req.getSubUserType());
				query.where(n1,n2,n3,n4).orderBy(orderList);
				
			} else {
				query.where(n1,n2,n3).orderBy(orderList);
			}
			
			// Get Result
			TypedQuery<LoginDetailsCriteriaRes> result = em.createQuery(query);
			//result.setFirstResult(limit * offset);
			//result.setMaxResults(offset);
			list = result.getResultList();
			
			Type listType = new TypeToken<List<LoginUserGridRes>>(){}.getType();
			resList = mapper.map(list,listType);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return resList;
	}


	
	public List<LoginIssuerGridRes> getIssuerActiveDetailsByUserType(IssuerActiveGridReq req) {
		List<LoginIssuerGridRes> resList = new ArrayList<LoginIssuerGridRes>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
		ModelMapper mapper = new ModelMapper(); 
		try { 
			// Limit Offset
			//	int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			//int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginDetailsCriteriaRes> query = cb.createQuery(LoginDetailsCriteriaRes.class);
			List<LoginDetailsCriteriaRes> list = new ArrayList<LoginDetailsCriteriaRes>();
			
			// Find All
			Root<LoginMaster> l = query.from(LoginMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
			

			// Select
			query.multiselect( l.get("loginId").alias("loginId") , l.get("createdBy").alias("createdBy") , 
					l.get("entryDate").alias("entryDate") , l.get("updatedDate").alias("updatedDate") ,
					l.get("updatedBy").alias("updatedBy") , l.get("attachedBranches").alias("attachedBranches") ,
					l.get("status").alias("status") ,  l.get("oaCode").alias("oaCode"),l.get("agencyCode").alias("agencyCode") ,l.get("subUserType").alias("subUserType"), 
					 l.get("bankCode").alias("bankCode") ,
					//cb.selectCase().when(l.get("bankCode").isNotNull(), l.get("bankCode") ).otherwise("No Bank").alias("BankCode") ,
					u.get("userName").alias("userName")  ,
					u.get("userMobile").alias("userMobile") , u.get("userMail").alias("userMail") ,  l.get("companyId").alias("companyId")   ,u.get("regulatoryCode").alias("regulatoryCode")   );
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(l.get("entryDate")));
			
			// Where
			Predicate n1 = cb.equal(l.get("loginId"), u.get("loginId"));
			Predicate n2 = cb.equal(l.get("userType"), req.getUserType());
			Predicate n3 = cb.equal(l.get("status"), "Y");
			
			if(StringUtils.isNotBlank( req.getSubUserType())  ) {
				Predicate n4 = cb.equal(l.get("subUserType"), req.getSubUserType());
				query.where(n1,n2,n3,n4).orderBy(orderList);
				
			} else {
				query.where(n1,n2,n3).orderBy(orderList);
			}
			
			// Get Result
			TypedQuery<LoginDetailsCriteriaRes> result = em.createQuery(query);
			//	result.setFirstResult(limit * offset);
			//	result.setMaxResults(offset);
			list = result.getResultList();
			
			Type listType = new TypeToken<List<LoginIssuerGridRes>>(){}.getType();
			resList = mapper.map(list,listType);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return resList;
	}


	
	public BrokerDatailsGetRes getBrokerDetailsById(BrokerDetailsGetReq req) {
		BrokerDatailsGetRes res = new BrokerDatailsGetRes();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		try {
			String pattern = "#####0.00";
		 	DecimalFormat df = new DecimalFormat(pattern);
			// LoginData
			LoginMaster loginData  = loginRepo.findByLoginId(req.getLoginId());
			BrokerLoginDetailsGetRes loginInfo = new BrokerLoginDetailsGetRes();  
			loginInfo = dozerMapper.map(loginData, BrokerLoginDetailsGetRes.class);
			
			// UserData
			LoginUserInfo userData = loginUserRepo.findByLoginId(req.getLoginId());	
			BrokerPersonalDetailsGetRes personalInfo = new BrokerPersonalDetailsGetRes();
			personalInfo = dozerMapper.map(userData, BrokerPersonalDetailsGetRes.class);
			personalInfo.setCreditLimit(userData.getCreditLimit()!=null ?  df.format(userData.getCreditLimit()) : "");
			personalInfo.setCityCode(userData.getCityCode()==null?"":userData.getCityCode().toString());
			//Deposit Cbc Master
			List<DepositcbcMaster> depoCbcList= depositcbcRepo.findByBrokerId(loginData.getAgencyCode());
			List<BrokerDepositCbcDetailsGetRes> depoReslist =new ArrayList<BrokerDepositCbcDetailsGetRes>();
			BrokerDepositCbcDetailsGetRes depoCbcRes=new BrokerDepositCbcDetailsGetRes ();
			for(DepositcbcMaster data:depoCbcList) {
				depoCbcRes = dozerMapper.map(data, BrokerDepositCbcDetailsGetRes.class);
				depoReslist.add(depoCbcRes);
			}
			
			// Response
			res.setLoginInformation(loginInfo);
			res.setPersonalInformation(personalInfo);
			res.setDepositCbcInformation(depoReslist);
			res.setBrokerLogo(userData.getBrokerLogo());
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}


	
	public IssuerDatailsGetRes getIssuerDetailsById(IssuerDetailsGetReq req) {
		IssuerDatailsGetRes res = new IssuerDatailsGetRes();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		try {
			// LoginData
			LoginMaster loginData  = loginRepo.findByLoginId(req.getLoginId());
			IssuerLoginGetRes loginInfo = new IssuerLoginGetRes();  
			loginInfo = dozerMapper.map(loginData, IssuerLoginGetRes.class);
			String mi = loginData.getModuleIds();
			if(StringUtils.isNotBlank(mi)) {
				List<String> collect = Arrays.stream(mi.split("\\s*,\\s*"))
                .distinct()
                .collect(Collectors.toList());
				res.setModuleIds(collect);
			}

						
			// UserData
			LoginUserInfo userData = loginUserRepo.findByLoginId(req.getLoginId());	
			IssuerPersonalInfoGetRes personalInfo = new IssuerPersonalInfoGetRes();
			personalInfo = dozerMapper.map(userData, IssuerPersonalInfoGetRes.class);
			
			BrokerProductReq productReq = new BrokerProductReq();
			productReq.setInsuranceId(loginData.getCompanyId());
			productReq.setLoginId(loginData.getLoginId());
			
			List<DropDownRes> productRes = loginProductService.getBrokerProductDropdown(productReq) ;
			
			List<String> productIds = productRes.stream().map( o -> o.getCode()).collect(Collectors.toList());
			
			
			loginInfo.setProductIds(productIds );
			
			
			List<LoginBranchMaster> findBranches = loginBranchRepo.findByLoginIdAndCompanyIdOrderByBranchCodeAsc(loginData.getLoginId() ,  loginData.getCompanyId());
			
			List<String> branchIds = findBranches.stream().map( o -> o.getBranchCode()).collect(Collectors.toList());
			
			loginInfo.setGetBranches(branchIds);
			
			if(loginData!=null && StringUtils.isNotBlank(loginData.getAttachedCompanies()) ) {
				String[] array = loginData.getAttachedCompanies().split(",");
				List<String> comapanyIds = 	new ArrayList<String>(Arrays.asList(array)) ;
				comapanyIds = comapanyIds.stream().filter( o -> StringUtils.isNotBlank(o)  ).collect(Collectors.toList());			
				loginInfo.setGetCompanies(comapanyIds);
			}
			
			// Response
			res.setLoginInformation(loginInfo);
			res.setPersonalInformation(personalInfo);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}


	
	public UserDetailsGetRes getUserDetailsById(UserDetailsGetReq req) {
		UserDetailsGetRes res = new UserDetailsGetRes();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		try {
			// LoginData
			LoginMaster loginData  = loginRepo.findByLoginId(req.getLoginId());
			UserLoginGetRes loginInfo = new UserLoginGetRes();  
			loginInfo = dozerMapper.map(loginData, UserLoginGetRes.class);
			
			// UserData
			LoginUserInfo userData = loginUserRepo.findByLoginId(req.getLoginId());	
			UserPersonalInfoGetRes personalInfo = new UserPersonalInfoGetRes();
			personalInfo = dozerMapper.map(userData, UserPersonalInfoGetRes.class);
			
			// Response
			res.setLoginInformation(loginInfo);
			res.setPersonalInformation(personalInfo);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}


	
	public List<Menu> getMenuList( MenuListReq req){
		List<Menu> menusret=new ArrayList<Menu>();
		String company_id=StringUtils.isBlank(req.getInsuranceId()) ? "99999" : req.getInsuranceId() ;
		String product_id=StringUtils.isBlank(req.getProductId()) ? "99999" : req.getProductId() ;
		try {
			LoginMaster login =loginRepo.findByLoginId(req.getLoginId());
			// Menu Ids
			List<String> asList =  new ArrayList<String>();
			 List<MenuMaster> findBymenuList = new ArrayList<>();
			  if(StringUtils.isNotBlank(login.getMenuIds()) ) {
				  // String[] split = login.getMenuIds().split(",");
				  asList = Arrays.asList(login.getMenuIds().split(",") );
				 
			  }
			// Get Menus 	  
			if(req.getSubUserType().equalsIgnoreCase("both")  )	  {
				List<MenuMaster> adminmenuList = getMenuListCriteria(asList , "admin",company_id,product_id );
				findBymenuList.addAll(adminmenuList);
				List<MenuMaster> usermenuList = getMenuListCriteria(asList , req.getUserType(),company_id,product_id  );
				findBymenuList.addAll(usermenuList);
			} else if(req.getSubUserType().equalsIgnoreCase("high")  ) {
				List<MenuMaster> adminmenuList = getMenuListCriteria(asList , "admin",company_id,product_id );
				findBymenuList.addAll(adminmenuList);
			} else if(req.getSubUserType().equalsIgnoreCase("low")  ) {
				List<MenuMaster> usermenuList = getMenuListCriteria(asList , req.getUserType(),company_id,product_id  );
				findBymenuList.addAll(usermenuList);
			}  else if(req.getSubUserType().equalsIgnoreCase("SuperAdmin")  ) {
				List<MenuMaster> usermenuList = getMenuListCriteria(asList , "SuperAdmin",company_id,product_id );
				findBymenuList.addAll(usermenuList);
			} else  {
				List<MenuMaster> usermenuList = getMenuListCriteria(asList , req.getUserType() ,company_id,product_id );
				findBymenuList.addAll(usermenuList);
			}
			List<MenuMaster> unique = findBymenuList.stream()
                    .collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparingInt((MenuMaster::getMenuId)))),
                                               ArrayList::new));
/*			
			List<Employee> unique = employee.stream()
                    .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingInt(Employee::getId))),
                                               ArrayList::new));
*/
				List<Menu> menus=new ArrayList<Menu>();
				for (MenuMaster menuMaster : unique) {
					Menu m = Menu.builder().title(menuMaster.getMenuName()).titleLocal(menuMaster.getMenuNameLocal()).link(menuMaster.getMenuUrl()).id(menuMaster.getMenuId().toString()).parent(menuMaster.getParentMenu())
							.icon(menuMaster.getMenuLogo()).productId(menuMaster.getProductId().toString()).orderby(menuMaster.getDisplayOrder()==null?0:menuMaster.getDisplayOrder().longValue()).build();
					menus.add(m);
				}
				 List<Menu> collect = menus.stream().filter(i-> "99999".equals(i.getParent())).collect(Collectors.toList());
				log.info("collect"+collect);
				 for (Menu menu : collect) {
					 Menu m = menu;
					 m.setChildren(menus.stream().filter(i -> (!"99999".equals(i.getParent()) && menu.getId().equals(i.getParent()))).collect(Collectors.toList()));
					if(m.getChildren().size()>0) {
						menusret.add(m);	
					} else {
						m.setChildren(null);
						menusret.add(m);
					}
					 
				}
				 menusret.sort(Comparator.comparing(Menu :: getOrderby));
				 
				 // Tira Vehicle Search Menu
				 /*
					 * if(StringUtils.isBlank(req.getInsuranceId()) ||
					 * StringUtils.isBlank(req.getProductId()) ||
					 * "100002".equalsIgnoreCase(req.getInsuranceId())) { menusret.removeIf( o ->
					 * o.getId().equalsIgnoreCase("406")); } else
					 * if(StringUtils.isBlank(req.getInsuranceId()) ||
					 * StringUtils.isBlank(req.getProductId()) ||
					 * "100019".equalsIgnoreCase(req.getInsuranceId())) { menusret.removeIf( o ->
					 * o.getId().equalsIgnoreCase("407")); }
					 */
					/*
					 * else { menusret.removeIf(o -> o.getId().equalsIgnoreCase("406") ||
					 * o.getId().equalsIgnoreCase("407")); }
					 */
				
				 
			return menusret;
		}catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage() );
			return null;
		}
	} 
	
	
	public List<MenuMaster> getMenuListCriteria( List<String> menuids, String usertype ,String company_id ,String product_id ){
		List<MenuMaster> menuList = new ArrayList<MenuMaster>();
		try {
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MenuMaster> query = cb.createQuery(MenuMaster.class);
			Root<MenuMaster> m = query.from(MenuMaster.class);

			//In 
			Expression<String>e0=m.get("menuId");
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(m.get("menuId")));
			
			Predicate p1 = cb.equal(m.get("status"), "Y");
			
			Predicate p3 =  cb.like(m.get("usertype"), "%" + usertype + "%" );
			
			Predicate p5 = cb.or(
				    cb.equal(m.get("companyId"), company_id),
				    cb.equal(m.get("companyId"), "99999")
				);
			
			Predicate p6 = cb.or(
				    cb.equal(m.get("productId"), product_id),
				    cb.equal(m.get("productId"), "99999")
				);
			if(menuids.size()>0) {
				Predicate p2 = e0.in(menuids).not();
				if (usertype.equalsIgnoreCase("admin") ) {
					Predicate p4 =  cb.notEqual(m.get("usertype"), "SuperAdmin" );
					query.select(m ).where(p1,p2,p3,p4,p5,p6).orderBy(orderList) ;
				} else {
					query.select(m ).where(p1,p2,p3,p5,p6).orderBy(orderList) ;
				}
				
			} else {
				if (usertype.equalsIgnoreCase("admin") ) {
					Predicate p4 =  cb.notEqual(m.get("usertype"), "SuperAdmin" );
					query.select(m ).where(p1,p3,p4,p5,p6).orderBy(orderList) ;
				} else {
					query.select(m ).where(p1,p3,p5,p6).orderBy(orderList) ;
				}
			}

			TypedQuery<MenuMaster> result = em.createQuery(query);
			menuList = result.getResultList();
			
		}catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage() );
			return null ;
		}
		return menuList;
	}


	
	public LoginCreationRes insertUserLogin(InsertUserLoginReq req) {
		ModelMapper mapper = new  ModelMapper();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhssmmss"); 
		LoginCreationRes res = new LoginCreationRes();
		try {
			// Find Data 
			String loginId = req.getLoginId() ;
			LoginMaster findLogin = loginRepo.findByLoginId(loginId);
			
			// Delete Old Records
			LoginMaster  updateLogin = findLogin;
			loginRepo.delete(findLogin);
			// Save in Arch tables
						String archId = "AI-" + idf.format(new Date());
						LoginMasterArch  loginArch = mapper.map(findLogin, LoginMasterArch.class )  ;
						loginArch.setArchId(archId);
						loginArchRepo.saveAndFlush(loginArch);
						
						
			LoginMaster ent =  mapper.map(updateLogin, LoginMaster.class);
			ent.setLoginId(req.getLoginId());
			String key = "";
			if (req.getMenuId() != null) {
				List<String> keys = req.getMenuId();
				for (int i = 0; i < keys.size(); i++) {
					if (i == 0) {
						key = keys.get(i);
					} else {
						key = key + "," + keys.get(i);
					}
				}
			}
			ent.setMenuIds(key);
			loginRepo.save(ent);
			res.setResponse(req.getLoginId());
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}


	
	public List<BrokerDropDownRes> getBrokerIds() {
		List<BrokerDropDownRes> resList = new ArrayList<BrokerDropDownRes>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
		ModelMapper mapper = new ModelMapper(); 
		try { 
			// Limit Offset
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginDetailsCriteriaRes> query = cb.createQuery(LoginDetailsCriteriaRes.class);
			List<LoginDetailsCriteriaRes> list = new ArrayList<LoginDetailsCriteriaRes>();
			
			// Find All
			Root<LoginMaster> l = query.from(LoginMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
			
 
			// Select
			query.multiselect( l.get("loginId").alias("loginId") , l.get("createdBy").alias("createdBy") , 
					l.get("entryDate").alias("entryDate") , l.get("updatedDate").alias("updatedDate") ,
					l.get("updatedBy").alias("updatedBy") , l.get("attachedBranches").alias("attachedBranches") ,
					l.get("status").alias("status") ,  l.get("oaCode").alias("oaCode"),l.get("agencyCode").alias("agencyCode") ,l.get("subUserType").alias("subUserType"), 
					 l.get("bankCode").alias("bankCode") ,
					//cb.selectCase().when(l.get("bankCode").isNotNull(), l.get("bankCode") ).otherwise("No Bank").alias("BankCode") ,
					u.get("userName").alias("userName")  ,
					u.get("userMobile").alias("userMobile") , u.get("userMail").alias("userMail") ,  l.get("companyId").alias("companyId")  ,u.get("regulatoryCode").alias("regulatoryCode")  );
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(u.get("userName")));
			
			// Where
			Predicate n1 = cb.equal(l.get("userType"), "Broker");
			Predicate n2 = cb.equal(l.get("status"), "Y");
			Predicate n3 = cb.equal(l.get("loginId"), u.get("loginId"));
				query.where(n1,n2,n3).orderBy(orderList);

			// Get Result
			TypedQuery<LoginDetailsCriteriaRes> result = em.createQuery(query);
			list = result.getResultList();
			

			for( LoginDetailsCriteriaRes data :list ) {
				BrokerDropDownRes res = new BrokerDropDownRes();
				res.setBrokerId(data.getAgencyCode());
				res.setBrokerName(data.getUserName());
				res.setCompanyId(data.getCompanyId());
				res.setSubUserType(data.getSubUserType());
				resList.add(res);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return resList;
	}


	
	public SuccessRes savemenuids(MenuIdSaveReq req) {
	SuccessRes res = new SuccessRes();
	try {
		LoginMaster data = loginRepo.findByLoginId(req.getLoginId());
		String key = "";
		if(data!=null) {
			List<String> keys = req.getMenuIds()!=null && req.getMenuIds().size() >0 ?req.getMenuIds() : new ArrayList<String>() ;
			for (String menuId : keys ) {			
			key =StringUtils.isBlank(key) ? menuId  : key + "," + menuId;
			}
			}
		data.setMenuIds(key);
		loginRepo.save(data);
		res.setResponse("Menu Ids Added Successfully");
		res.setSuccessId(req.getLoginId());
		}
	catch(Exception e) {
		e.printStackTrace();
		log.info("Log Details"+e.getMessage());
		return null;
		}
		return res;
	}


	
	public MenuIdGetRes getmenuids(MenuIdGetReq req) {
		MenuIdGetRes res = new MenuIdGetRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		LoginMaster savedata = new LoginMaster();
		try {
			LoginMaster data = loginRepo.findByLoginId(req.getLoginId());
			String menuid = data.getMenuIds();
			List<String> menuids = menuid!=null ? new ArrayList<String>(Arrays.asList(menuid.split(","))) : new ArrayList<String>() ;
			menuids = menuids.stream().filter( o -> ! o.equals("")).collect(Collectors.toList());
			
			res.setMenuId(menuids);	
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
			}
			return res;
		}


	
	public List<BrokerDropDownRes> getBrokerIdsByCompany(BrokerDropdownReq req) {
		List<BrokerDropDownRes> resList = new ArrayList<BrokerDropDownRes>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
		ModelMapper mapper = new ModelMapper(); 
		try { 
			// Limit Offset
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginDropdownCriteria> query = cb.createQuery(LoginDropdownCriteria.class);
			List<LoginDropdownCriteria> list = new ArrayList<LoginDropdownCriteria>();
			
			// Find All
			Root<LoginMaster> l = query.from(LoginMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
			

			// Select
			query.multiselect( l.get("loginId").alias("loginId") , l.get("createdBy").alias("createdBy") , 
					l.get("entryDate").alias("entryDate") , l.get("updatedDate").alias("updatedDate") ,
					l.get("updatedBy").alias("updatedBy") , l.get("attachedBranches").alias("attachedBranches") ,
					l.get("status").alias("status") ,  l.get("oaCode").alias("oaCode"),l.get("agencyCode").alias("agencyCode") ,l.get("subUserType").alias("subUserType"), 
					 l.get("bankCode").alias("bankCode") ,
					//cb.selectCase().when(l.get("bankCode").isNotNull(), l.get("bankCode") ).otherwise("No Bank").alias("BankCode") ,
					u.get("userName").alias("userName")  ,
					u.get("userMobile").alias("userMobile") , u.get("userMail").alias("userMail") ,  l.get("companyId").alias("companyId") ,
					u.get("customerCode").alias("customerCode") , u.get("customerName").alias("customerName") );
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(u.get("userName")));
			
			// Where
			Predicate n1 = cb.equal(l.get("userType"), "Broker");
			Predicate n2 = cb.equal(l.get("status"), "Y");
			Predicate n3 = cb.equal(l.get("loginId"), u.get("loginId"));
			Predicate n4 = cb.equal(l.get("companyId"),req.getInsuranceId());
			Predicate n6 = cb.lessThanOrEqualTo(l.get("effectiveDateStart"),new Date());
			
			
			if(StringUtils.isNotBlank( req.getSubUserType())  ) {
				Predicate n5 = cb.equal(l.get("subUserType"), req.getSubUserType());
				query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
				
			} else {
				query.where(n1,n2,n3,n4,n6).orderBy(orderList);
			}
			
			

			// Get Result
			TypedQuery<LoginDropdownCriteria> result = em.createQuery(query);
			list = result.getResultList();
			

			for( LoginDropdownCriteria data :list ) {
				BrokerDropDownRes res = new BrokerDropDownRes();
				res.setBrokerId(data.getAgencyCode());
				res.setBrokerName(data.getUserName());
				res.setCompanyId(data.getCompanyId());
				res.setSubUserType(data.getSubUserType());
				res.setCustomerCode(data.getCustomerCode());
				res.setCustomerName(data.getCustomerName());
				resList.add(res);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return resList;
	}
	private String getIdTypeDesc(String companyId, String idType) {
		 String itemDesc = "" ;
		 List<String> list = new ArrayList<String>();
			try {			
				// Criteria
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<String> query=  cb.createQuery(String.class);
				Root<ListItemValue> c = query.from(ListItemValue.class);
				query.select(c.get("itemValue"));

				// AmendId Max Filter
				Subquery<Integer> amendId = query.subquery(Integer.class);
				Root<ListItemValue> sub = amendId.from(ListItemValue.class);
				amendId.select(cb.max(sub.get("amendId")));
				Predicate a3 = cb.equal(c.get("itemId"),sub.get("itemId"));
				Predicate a4 = cb.equal(c.get("itemType"),"POLICY_HOLDER_ID_TYPE");
				Predicate b3= cb.equal(c.get("companyId"),companyId);
				Predicate b4= cb.equal(c.get("branchCode"),"99999");
				amendId.where(a3,a4,b3,b4);
							
				// Where
				Predicate n1 = cb.equal(c.get("status"),"Y");
				Predicate n4 = cb.equal(c.get("companyId"), companyId);
				Predicate n7 = cb.equal(c.get("branchCode"), "99999");
				Predicate n10 = cb.equal(c.get("itemType"),"POLICY_HOLDER_ID_TYPE");
				Predicate n11 = cb.equal(c.get("itemCode"), idType);
				query.where(n1,n4,n10,n11,n7);
				// Get Result
				TypedQuery<String> result = em.createQuery(query);
				list = result.getResultList();
				
				itemDesc = list.size() > 0 ? list.get(0): "" ; 
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return itemDesc ;
		}


	
	public JasperDocumentRes getBrokerLogo(String loginId) {
		log.info("Enter into BrokerLogo");
		JasperDocumentRes res = new JasperDocumentRes();
		try {
			LoginUserInfo info = loginUserRepo.findByLoginId(loginId);
			Path path = Paths.get(this.getClass().getClassLoader().getResource("report/images/"+info.getBrokerLogo()).toURI());
			byte [] imageByte = Files.readAllBytes(path);
			String imgStr = Base64.getEncoder().encodeToString(imageByte);
			String mimeType = Files.probeContentType(path);
			String imgData ="data:"+mimeType+";base64,"+imgStr;
			res.setPdfoutfilepath(path.toString());
			res.setPdfoutfile(imgData);
			return res;
		}catch(Exception e) {
			log.info("Error in BrokerLogo ==> "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}


	
	public List<DropDownRes> getBrokerList(GetBrokerListDropDownReq req) {
		
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<GetBrokerListDropDownRes> query = cb.createQuery(GetBrokerListDropDownRes.class);
			List<GetBrokerListDropDownRes> list = new ArrayList<GetBrokerListDropDownRes>();
			
			// Find All
			Root<LoginMaster> c = query.from(LoginMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
			Root<LoginBranchMaster> l = query.from(LoginBranchMaster.class);
			
			// Select
			query.multiselect(c.get("loginId").alias("loginId"),c.get("status").alias("status"),c.get("oaCode").alias("oaCode"),
					c.get("agencyCode").alias("agencyCode"),u.get("userName").alias("userName"),u.get("customerCode").alias("customerCode") , 
					u.get("customerName").alias("customerName")) ;
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("loginId")));
			
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("userType"), req.getUserType());
			Predicate n3 = cb.equal(c.get("subUserType"), req.getSubUserType());
			Predicate n4 = cb.equal(c.get("companyId"), req.getCompanyId());
		/*	Predicate n5 = cb.or(
				    cb.like(c.get("attachedBranches"), req.getBranchCode()), // Exact match
				    cb.like(c.get("attachedBranches"), req.getBranchCode() + ",%"), // Starts with
				    cb.like(c.get("attachedBranches"), "%," + req.getBranchCode() + ",%"), // In the middle
				    cb.like(c.get("attachedBranches"), "%," + req.getBranchCode()) // Ends with
				);*/
			//Predicate n6 = cb.equal(c.get("agencyCode"), c.get("oaCode").toString());
			//Predicate n7 = cb.in(c.get("attachedBranches")).value("99999");
		//	Predicate n8 = cb.or(n5,n7);
			Predicate n9 = cb.equal(u.get("loginId"), c.get("loginId"));
			Predicate n10 = cb.equal(u.get("loginId"), l.get("loginId"));
			Predicate n11 = cb.equal(l.get("branchCode"),req.getBranchCode());					
			Predicate n12 = cb.equal(l.get("branchCode"),"99999");
			Predicate n13 = cb.or(n11,n12);
			//Predicate n10 = cb.equal(u.get("agencyCode"), u.get("oaCode"));
			// n11 = cb.equal(u.get("agencyCode"), c.get("agencyCode"));
			//Predicate n12 = cb.equal(u.get("oaCode"), c.get("oaCode").toString());
			
			
			query.where(n1,n2,n3,n4,n9,n10,n13).orderBy(orderList);
			
			
			// Get Result
			TypedQuery<GetBrokerListDropDownRes> result = em.createQuery(query);
			list = result.getResultList();
			
			for (GetBrokerListDropDownRes data : list) {
				// Response
				DropDownRes res = new DropDownRes();
				res.setCode(data.getLoginId());
				res.setCodeDesc(data.getCustomerName());
				res.setCodeDescLocal(data.getCustomerName());
				res.setStatus(data.getStatus());
				res.setOaCode(data.getOaCode());				
				resList.add(res);			
			}
		
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return resList;
	}


	private UserSolvedgeRequest draftUserSolvedgeDetail(IssuerCraeationReq req, CommonLoginCreationReq commonReq) {
		UserSolvedgeRequest userRequest = new UserSolvedgeRequest();
		userRequest.setAddress(commonReq.getPersonalInformation().getAddress1()
				+ commonReq.getPersonalInformation().getAddress2()
				+ commonReq.getPersonalInformation().getAddress3());
		userRequest.setEmail(commonReq.getPersonalInformation().getUserMail());
		userRequest.setFullName(req.getLoginInformation().getLoginId());
		userRequest.setPassword(commonReq.getLoginInformation().getPassword());
		userRequest.setPhone(commonReq.getPersonalInformation().getUserMobile());
		userRequest.setReasonForRegistration(commonReq.getPersonalInformation().getRemarks());
		String subUserTypeStr = req.getLoginInformation().getSubUserTypeDesc();
		Role userRole = Role.valueOf(subUserTypeStr.toUpperCase());
		userRequest.setRole(userRole);
		return userRequest;
	}
	
	public LoginCreationRes attachBrokerCompanyBranch(AttachBrokerBranchReq req, String token) {
		LoginCreationRes res = new LoginCreationRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhssmmss");
		try {
			// Login Data
			LoginMaster loginData = loginRepo.findByLoginId(req.getLoginId());
			LoginUserInfo loginUserInfo = loginUserRepo.findByLoginId(req.getLoginId());

			// Find Data
			String brokerBranchCode = "None";

			if (StringUtils.isBlank(req.getBrokerBranchCode()) && (loginData.getUserType().equalsIgnoreCase("Broker")
					|| loginData.getUserType().equalsIgnoreCase("User"))) {
				long count = loginBrokerRepo.countByLoginId(req.getLoginId());
				brokerBranchCode = String.valueOf(count + 1);
			} else if (loginData.getUserType().equalsIgnoreCase("Broker")
					|| loginData.getUserType().equalsIgnoreCase("User")) {
				brokerBranchCode = req.getBrokerBranchCode();
			}
			
			LoginBranchMaster findBranch = loginBrokerRepo.findByBranchCodeAndLoginIdAndCompanyId(
					req.getBranchCode(), req.getLoginId(),  req.getCompanyId());
			
			LoginBranchMaster save = dozerMapper.map(req, LoginBranchMaster.class);
			if (findBranch != null) {
				// Delete Old Record
				loginBrokerRepo.delete(findBranch);
				// Save in Arch tables
				String archId = "AI-" + idf.format(new Date());
				LoginBranchMasterArch loginArch = dozerMapper.map(findBranch, LoginBranchMasterArch.class);
				loginArch.setArchId(archId);
				loginBrokerArchRepo.saveAndFlush(loginArch);

				save.setEntryDate(findBranch.getEntryDate());
				save.setCreatedBy(findBranch.getCreatedBy());
				save.setUpdatedBy(req.getCreatedBy());
				save.setUpdatedDate(new Date());
			} else {
				save.setEntryDate(new Date());
				save.setCreatedBy(req.getCreatedBy());
				save.setUpdatedBy(req.getCreatedBy());
				save.setUpdatedDate(new Date());
			}

			save.setOaCode(Integer.valueOf(loginData.getOaCode()));
			save.setAgencyCode(Integer.valueOf(loginData.getAgencyCode()));
			save.setAttachedBranch(
					StringUtils.isBlank(req.getAttachedBranch()) ? req.getBranchCode() : req.getAttachedBranch());
			save.setAttachedCompany(
					StringUtils.isBlank(req.getAttachedCompany()) ? req.getCompanyId() : req.getAttachedCompany());
			save.setBrokerBranchCode(brokerBranchCode);
			
			save.setUserType(loginData.getUserType());
			save.setSubUserType(loginData.getSubUserType());
			List<BranchMaster> branchname = branchRepo.findTopByCompanyIdAndBranchCodeOrderByAmendIdDesc(req.getCompanyId(),req.getBranchCode());
			save.setBranchName(branchname.get(0).getBranchName());
			save.setBrokerBranchName(req.getBrokerBranchName());
			save.setCustomerCode(loginUserInfo.getCustomerCode());
			save.setCustomerName(loginUserInfo.getCustomerName());
		
			loginBrokerRepo.save(save);

			log.info("Login Master Updated Details ---> " + json.toJson(save));
			res.setResponse("Branch Added Successfully");

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}
	

	public SuccessRes brokerListCompanyProducts(List<BrokerCompanyListProductReq> reqList) {
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/YYYY");
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		try {
			
			List<Integer> findproductId =  reqList.stream().map( BrokerCompanyListProductReq :: getProductId ) .collect(Collectors.toList());					
			
			for (BrokerCompanyListProductReq req : reqList) {
				LoginMaster login = loginRepo.findByLoginId(req.getLoginId());
				
				// Save Broker Commission Details
				
				res = saveBrokerCommission1(req, login);
				
				LoginProductMaster saveData = new LoginProductMaster();
				String productName =   getCompanyProductMasterDropdown(req.getCompanyId() , req.getProductId().toString()); //productRepo.findByProductIdOrderByAmendIdDesc(Integer.valueOf(req.getProductId()));
				List<LoginProductMaster> list = new ArrayList<LoginProductMaster>();
				Integer amendId = 0;
				Date startDate = req.getEffectiveDateStart();
				String end = "31/12/2050";
				Date endDate = sdformat.parse(end);
				long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
				Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLIS_IN_A_DAY);
				String financeId = "";
				String nonFinanceId = "";
				String status = req.getStatus();

				String productId = "";
				Date entryDate = null;
				String createdBy = "";

				// Update
				// Get Less than Equal Today Record
				// Criteria
				productId = req.getProductId().toString();
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<LoginProductMaster> query = cb.createQuery(LoginProductMaster.class);

				// Find All
				Root<LoginProductMaster> b = query.from(LoginProductMaster.class);

				// Select
				query.select(b);

				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("amendId")));

				// Where
				// Predicate n1 = cb.equal(b.get("status"), "Y");
				Predicate n3 = cb.equal(b.get("productId"), req.getProductId());
				Predicate n4 = cb.equal(b.get("companyId"), req.getCompanyId());
				Predicate n5 = cb.equal(b.get("loginId"), req.getLoginId());

				query.where(n3, n4, n5).orderBy(orderList);

				// Get Result
				TypedQuery<LoginProductMaster> result = em.createQuery(query);
				int limit = 0, offset = 2;
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				list = result.getResultList();

				if (list.size() > 0) {
					Date beforeOneDay = new Date(new Date().getTime() - MILLIS_IN_A_DAY);
					financeId = list.get(0).getFinancialEndtIds();
					nonFinanceId = list.get(0).getNonFinancialEndtIds();

					if (list.get(0).getEffectiveDateStart().before(beforeOneDay)) {
						amendId = list.get(0).getAmendId() + 1;
						entryDate = new Date();
						createdBy = req.getCreatedBy();
						LoginProductMaster lastRecord = list.get(0);

						lastRecord.setEffectiveDateEnd(oldEndDate);
						loginProductRepo.saveAndFlush(lastRecord);

					} else {
						amendId = list.get(0).getAmendId();
						entryDate = list.get(0).getEntryDate();
						createdBy = list.get(0).getCreatedBy();
						saveData = list.get(0);
						if (list.size() > 1) {
							LoginProductMaster lastRecord = list.get(1);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							loginProductRepo.saveAndFlush(lastRecord);
						}

					}
				}

				res.setResponse("Updated Successfully ");
				res.setSuccessId(productId);
				
				saveData.setStatus(status);
				saveData.setProductId(Integer.valueOf(productId));
				saveData.setProductName(productName);
				saveData.setEffectiveDateStart(startDate);
				saveData.setEffectiveDateEnd(endDate);
				saveData.setCreatedBy(createdBy);
			
				saveData.setEntryDate(new Date());
				saveData.setCompanyId(req.getCompanyId());
				saveData.setEntryDate(entryDate);
				saveData.setAmendId(amendId);
				saveData.setAgencyCode(Integer.valueOf(login.getAgencyCode()));
				saveData.setOaCode(login.getOaCode());
				saveData.setUserType(login.getUserType());
				saveData.setSubUserType(login.getSubUserType());
				saveData.setCheckerYn(req.getCheckerYn());
				saveData.setMakerYn(req.getCheckerYn());
				saveData.setCreditYn(req.getCreditYn()==null?"N":req.getCreditYn());
				saveData.setBackDays(req.getBackDays()==null?0:Integer.valueOf(req.getBackDays()));
				saveData.setLoginId(login.getLoginId());
				saveData.setSumInsuredStart(new BigDecimal(req.getSumInsuredStart()));
				saveData.setSumInsuredEnd(new BigDecimal(req.getSumInsuredEnd()));
				
				
				if ("5".equalsIgnoreCase(req.getProductId().toString())) {
					saveData.setPolicyTypeId(req.getPolicyTypeId());
					saveData.setPolicyTypeDesc(req.getPolicyTypeDesc());
				} else {
					saveData.setPolicyTypeId("99999");
					saveData.setPolicyTypeDesc("All");
				}

				saveData.setFinancialEndtIds(financeId);
				saveData.setNonFinancialEndtIds(nonFinanceId);
				loginProductRepo.saveAndFlush(saveData);

				log.info("Saved Details is ---> " + json.toJson(saveData));

			
				


			}
			List<Integer> productIds =  reqList.stream().map( BrokerCompanyListProductReq :: getProductId ) .collect(Collectors.toList());
			List<String> pro=new ArrayList<String>();
			for(int i=0;i<productIds.size();i++) {
			pro.add(productIds.get(i).toString());
			}
			

			

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}
	
	public SuccessRes saveBrokerCommission1(BrokerCompanyListProductReq req ,LoginMaster login  ) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		BrokerCommissionDetails saveData = new BrokerCommissionDetails();
		List<BrokerCommissionDetails> list = new ArrayList<BrokerCommissionDetails>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			String productId="";
			Integer amendId = 0;
			Date StartDate = req.getEffectiveDateStart();
			String end = "31/12/2050";
			Date endDate = sdf.parse(end);
			long MILLS_IN_A_DAY = 1000 * 60 * 60 * 24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLS_IN_A_DAY);
			Date entryDate = null;
			String createdBy = "";
			Integer id = 1;

			id = StringUtils.isBlank(req.getPolicyTypeId()) ? 1 : Integer.valueOf(req.getPolicyTypeId());
			
			
			
//			List<BrokerCommissionDetails>   oldCommList = commissionRepo.findByProductIdNotAndLoginId(req.getProductId().toString(), req.getLoginId() ) ;
//
//			oldCommList.forEach ( o -> { 
//				Date startDate1=null;
//				Date date1 = new Date();
//				Calendar cal = new GregorianCalendar();
//				cal.setTime(date1);
//				cal.add(Calendar.DATE, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
//				startDate1 = cal.getTime();
//				o.setEffectiveDateEnd(startDate1);  }   );
//			commissionRepo.saveAll(oldCommList);
			
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);
			// Findall
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);
			// select
			query.select(b);
			// Orderby
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));
			// Where
			Predicate n1 = cb.equal(b.get("loginId"), req.getLoginId());
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(b.get("oaCode"), login.getOaCode());
			Predicate n5 = cb.equal(b.get("agencyCode"), login.getAgencyCode());
			Predicate n6 = cb.equal(b.get("policyType"),req.getPolicyTypeId());

			query.where(n1, n2, n3, n4, n5,n6).orderBy(orderList);

			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);
			int limit = 0, offset = 2;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			if (list.size() > 0) {
				Date beforeOneDay = new Date(new Date().getTime() - MILLS_IN_A_DAY);
				if (list.get(0).getEffectiveDateStart().before(beforeOneDay)) {
					amendId = list.get(0).getAmendId() + 1;
					entryDate = new Date();
					createdBy = req.getCreatedBy();
					BrokerCommissionDetails lastRecord = list.get(0);
					lastRecord.setEffectiveDateEnd(oldEndDate);
					commissionRepo.saveAndFlush(lastRecord);
				} else {
					amendId = list.get(0).getAmendId();
					entryDate = list.get(0).getEntryDate();
					createdBy = list.get(0).getCreatedBy();
					saveData = list.get(0);
					if (list.size() > 1) {
						BrokerCommissionDetails lastRecord = list.get(1);
						lastRecord.setEffectiveDateEnd(oldEndDate);
						commissionRepo.saveAndFlush(lastRecord);
					}
				}
			}
			res.setResponse("Updated Successfully");
			res.setSuccessId(req.getPolicyTypeId());

			String policytype = policyName(req.getCompanyId(), req.getProductId().toString(), req.getPolicyTypeId());

			dozerMapper.map(req, saveData);
		//	saveData.setSelectedYN("Y");
			saveData.setEffectiveDateStart(StartDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setEntryDate(entryDate);
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setAmendId(amendId);
			saveData.setSuminsuredStart(StringUtils.isBlank(req.getSumInsuredStart()) ? new BigDecimal("0")
					: new BigDecimal(req.getSumInsuredStart()));
			saveData.setSuminsuredEnd(StringUtils.isBlank(req.getSumInsuredEnd()) ? new BigDecimal("0")
					: new BigDecimal(req.getSumInsuredEnd()));
			saveData.setCommissionPercentage(
					StringUtils.isBlank(req.getCommissionPercent()) ? 0D : Double.valueOf(req.getCommissionPercent()));
			// saveData.setCommissionVatYn(req.getCommissionVatYn());
			// saveData.setCommissionVatPercent(StringUtils.isBlank(comm.getCommissionVatPercent()) ? 0D : Double.valueOf(comm.getCommissionVatPercent()));
			saveData.setBackDays(StringUtils.isBlank(req.getBackDays()) ? 0 : Integer.valueOf(req.getBackDays()));
			saveData.setAgencyCode(login.getAgencyCode());
			saveData.setCheckerYn(req.getCheckerYn());
			saveData.setCreditYn(req.getCreditYn());
			saveData.setCompanyId(req.getCompanyId());
			saveData.setFmvSiEnd("");
			saveData.setFmvSiStart("");
			saveData.setFmvStatus("");
			saveData.setLoginId(login.getLoginId());
			saveData.setOaCode(login.getOaCode().toString());
			saveData.setProductId(req.getProductId().toString());
			saveData.setRemarks(req.getRemarks());
			saveData.setStatus(StringUtils.isBlank(req.getStatus()) ? req.getStatus() : req.getStatus());
			saveData.setId(id);
		//	if ("5".equalsIgnoreCase(req.getProductId().toString())) {
				saveData.setPolicyType(req.getPolicyTypeId());
				saveData.setPolicyTypeDesc(req.getPolicyTypeDesc());
				saveData.setOpencoverno(req.getOpenCoverNo());
				saveData.setMinimumPremium(req.getMinimumPremium() == null ? null : req.getMinimumPremium());
				saveData.setLoadingofPremium(req.getLoadingofPremium() == null ? null : new BigDecimal(req.getLoadingofPremium()));
				saveData.setDiscountofPremium(req.getDiscountofPremium() == null ? null : new BigDecimal(req.getDiscountofPremium()));
				
		//	} else {
			//	saveData.setPolicyType("99999");
			//	saveData.setPolicyTypeDesc("All");
			//}
			// saveData.setPolicyTypeDesc(policytype);
			commissionRepo.save(saveData);

			res.setResponse("Saved Successfully");
			res.setSuccessId(login.getLoginId());
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}

		return res;

	}
	
	private String policyName(String companyId, String productId, String policyTypeId) {
		// TODO Auto-generated method stub
		String data="";
		try {
		List<PolicyTypeMaster> list = new ArrayList<PolicyTypeMaster>();
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PolicyTypeMaster> query = cb.createQuery(PolicyTypeMaster.class);
		//Find all
		Root<PolicyTypeMaster> b = query.from(PolicyTypeMaster.class);
		// Select
		query.select(b);
		// Effective Date Max Filter
		Subquery<Date> effectiveDate = query.subquery(Date.class);
		Root<PolicyTypeMaster> ocpm1 = effectiveDate.from(PolicyTypeMaster.class);
		effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
		Predicate a1 = cb.equal(ocpm1.get("policyTypeId"),b.get("policyTypeId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"),b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("productId"),b.get("productId"));

		effectiveDate.where(a1,a2,a3);
	
		//OrderBy
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.desc(b.get("amendId")));
		
		Predicate n1 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
		Predicate n2 = cb.equal(b.get("companyId"),companyId);
		Predicate n3 = cb.equal(b.get("productId"),productId);
		Predicate n4 = cb.equal(b.get("policyTypeId"),policyTypeId);
		
		query.where(n1,n2,n3,n4).orderBy(orderList);
		
		
		
		// Get Result
		TypedQuery<PolicyTypeMaster> result = em.createQuery(query);
		int limit = 0 , offset = 1 ;
		result.setFirstResult(limit * offset);
		result.setMaxResults(offset);
		list = result.getResultList();
		data = list.size() > 0 ? list.get(0).getPolicyTypeName() : "" ;
	}
	catch(Exception e) {
		e.printStackTrace();
		log.info(e.getMessage());
	}
	return data;
}
	
	public String getCompanyProductMasterDropdown(String companyId , String productId) {
		String productName = "";
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);;
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyProductMaster> query=  cb.createQuery(CompanyProductMaster.class);
			List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
			// Find All
			Root<CompanyProductMaster> c = query.from(CompanyProductMaster.class);
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productName")));
			
			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("productId"),ocpm1.get("productId"));
			Predicate a2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2,a3);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<CompanyProductMaster> ocpm2 = effectiveDate2.from(CompanyProductMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a4 = cb.equal(c.get("productId"),ocpm2.get("productId"));
			Predicate a5 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a4,a5,a6);
			
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"),companyId);
			Predicate n5 = cb.equal(c.get("productId"),productId);
			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			// Get Result
			TypedQuery<CompanyProductMaster> result = em.createQuery(query);
			list = result.getResultList();
			productName  = list.size()> 0 ? list.get(0).getProductName() : "";	
		}
			catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --->"+e.getMessage());
				return null;
				}
			return productName;
		}
	
	public List<String> validateRegisterCreation(BrokerCreationReq req) {
		List<String> errors = new ArrayList<String>();
		 DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		try {
			
			// Common Errors
			
			CommonLoginCreationReq commonReq = new CommonLoginCreationReq();
			dozerMapper.map(req, commonReq);
			String companyId = commonReq.getLoginInformation().getCompanyId();
			String loginId=commonReq.getLoginInformation().getLoginId();
			
{
				
				LoginMaster  loginData = loginRepo.findByLoginIdAndCompanyId(loginId, companyId);
				if(loginData!=null ) {
					if (StringUtils.isBlank( commonReq.getLoginInformation().getOaCode()) ) {
				//		errors.add(new Error("02", "Login Id", "Login Id Already Exist"));
						errors.add("1733");
					} else if(! commonReq.getLoginInformation().getOaCode().equalsIgnoreCase(loginData.getOaCode().toString() ) ) {
				//		errors.add(new Error("02", "Login Id", "Login Id Already Exist"));
						errors.add("1733");
					}
				} 
			}
			
			
			// Additional Broker Validataion
			AdditionalInfoReq  brokerReq = new AdditionalInfoReq();
			dozerMapper.map(req.getPersonalInformation() , brokerReq); 
			List<String>  brokerErrors = null;
			if("100055".equalsIgnoreCase(companyId))
			{
				 brokerErrors = commonBrokerPersonalValidationForOman(brokerReq) ;
			}

			
			if(brokerErrors.size()>0  ) {
				errors.addAll(brokerErrors);
			}
			
			if(StringUtils.isBlank(commonReq.getLoginInformation().getBrokerCompanyYn()))  {
			//	errors.add(new Error("01","BrokerCompanyYn","Please Select BrokerCompany Y or N"));
				errors.add("1761");
				
			} else if( ! (commonReq.getLoginInformation().getBrokerCompanyYn().equalsIgnoreCase("Y") || commonReq.getLoginInformation().getBrokerCompanyYn().equalsIgnoreCase("N")) )  {
			//	errors.add(new Error("01","BrokerCompanyYn","Please Select BrokerCompany Y or N"));
				errors.add("1761");
			}
			
			
			if(StringUtils.isNotBlank(brokerReq.getCreditLimit())  ) {
				if(! brokerReq.getCreditLimit().matches("[0-9.]+")  ) {
			//		errors.add(new Error("22", "CreditLimit", "Please Enter Valid Number in Credit Limit" ));
					errors.add("1762");
				}
			} 

			if(StringUtils.isBlank(brokerReq.getCustomerCode())  ) {
			//	errors.add(new Error("22", "CustomerCode", "Please Select Customer Code" ));
				errors.add("1763");
				
			} 
			if(StringUtils.isBlank(brokerReq.getRegulatoryCode())  ) {
			//	errors.add(new Error("22", "RegulatoryCode", "Please Enter Regulatory Code" ));
				errors.add("1764");
				
			} else if (brokerReq.getRegulatoryCode().length() > 20  ) {
			//	errors.add(new Error("22", "RegulatoryCode", "Regulatory Code Max 20 Characters only allowed" ));
				errors.add("1765");
				
			}
		  
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
		//	errors.add(new Error("07", "Common Error", e.getMessage() ));
			return errors;
		}
		return errors;
	}
	
	public List<String> commonBrokerPersonalValidationForOman(AdditionalInfoReq brokerReq) {

	    List<String> errorList = new ArrayList<>();

	    try {

	        // Insured Name (Company Name)
	        if (StringUtils.isBlank(brokerReq.getCompanyName())) {
	        	//Please enter the Insured name
	            errorList.add("5027");
	        } else if (brokerReq.getCompanyName().length() > 100) {
	        	//Insured name should be 100 characters
	            errorList.add("5028");
	        } else if (!brokerReq.getCompanyName().matches("^[A-Za-z0-9&.\\-'(), ]+$")) {
	        	//Special characters not allowed for insured name
	            errorList.add("5029");
	        }

	        // C.R No
	        if (StringUtils.isBlank(brokerReq.getCRNo())) {
	        	//Please enter the CRNO
	            errorList.add("5030");
	        } else if (!brokerReq.getCRNo().matches("^\\d{7}$")) {
	        	//CRNO must be numerical and length must be 7 digits
	            errorList.add("5031");
	        }

	        // C.R Expiry Date
	        if (brokerReq.getCrexpiryDate() == null) {
	        	//Please enter CR expiry date
	            errorList.add("5032");
	        } else if (!brokerReq.getCrexpiryDate().after(new Date())) {
	        	//CR expiry date must me future date
	            errorList.add("5033");
	        }
	        
	        if (StringUtils.isBlank(brokerReq.getVatRegNo())) {

	        	//Please enter the VAT Registration number
	            errorList.add("5036");

	        } 

	        if (brokerReq.getVatExpiryDate() == null) {
	        	//Please enter VAT expiry date
	            errorList.add("5033");
	        } else if (!brokerReq.getVatExpiryDate().after(new Date())) {
	        	//VAT expiry date must me future date
	            errorList.add("5035");
	        }

	        // Tax Card No - Optional

	        // Tax Card Expiry Date - Optional

	        // Mobile Number
	        if (StringUtils.isBlank(brokerReq.getUserMobile())) {
	        	//Please enter the mobile no
	            errorList.add("5039");
	        } else if (!brokerReq.getUserMobile().matches("^[79]\\d{7}$")) {
	        	//mobile no must start with 7,9
	            errorList.add("5040");
	        }

	        // P.O Box (Optional)
	        if (StringUtils.isNotBlank(brokerReq.getPobox())
	                && !brokerReq.getPobox().matches("^\\d{1,6}$")) {
	        	//PO BOX doesnt has Letters and Special characters
	            errorList.add("5041");
	        }

	        // Country
	        if (StringUtils.isBlank(brokerReq.getCountryCode())) {
	        	// please enter the country name
	            errorList.add("5042");
	        } else if (!"OMAN".equalsIgnoreCase(brokerReq.getCountryCode())
	                && !"OM".equalsIgnoreCase(brokerReq.getCountryCode())
	                && !"OMN".equalsIgnoreCase(brokerReq.getCountryCode())) {
	        	//Country name must be OMAN
	            errorList.add("5043");
	        }

	        // Email
	        if (StringUtils.isBlank(brokerReq.getUserMail())) {
	        	//Please enter the email id 
	            errorList.add("5044");
	        } else if (!isValidMail(brokerReq.getUserMail())) {
	        	//Please enter the valid mail id 
	            errorList.add("5045");
	        }

	        // Activity License No
	        if (StringUtils.isBlank(brokerReq.getActivityLicenseno())) {
	        	//Please enter Activity License No
	            errorList.add("5046");
	        }

	        // Activity License Expiry
	        if (brokerReq.getActivityExpiryDate() == null) {
	           //Please enter the Activity License no expiry date
	            errorList.add("5047");
	        } else if (!brokerReq.getActivityExpiryDate().after(new Date())) {
	        	//Activity License no expiry date must be future date
	            errorList.add("5048");
	        }
	        
	     // Tax Card No (Optional)
	        if (StringUtils.isNotBlank(brokerReq.getTaxCardNo())) {

	            if (!brokerReq.getTaxCardNo().matches("^\\d+$")) {
	            	errorList.add("5037"); // Tax Card No must be numeric
	            } else if (brokerReq.getTaxCardNo().length() > 20) {
	                errorList.add("5038"); // Tax card Maximum length is 20
	            }

	        }
 
	    } catch (Exception e) {
	        e.printStackTrace();
	        errorList.add("01");
	    }

	    return errorList;
	}
	
	public static boolean isValidMail(String mail) {
		String regex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(mail);
		return m.matches();

	}

}
