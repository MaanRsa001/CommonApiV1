package com.maan.eway.integration.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.CreditLimitDetail;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotCommDiscountDetail;
import com.maan.eway.bean.MotDriverDetail;
import com.maan.eway.bean.MotorDriverDetails;
import com.maan.eway.bean.PgithPolRiskAddlInfo;
import com.maan.eway.bean.PositionMaster;
import com.maan.eway.bean.PremiaConfigDataMaster;
import com.maan.eway.bean.PremiaConfigMaster;
import com.maan.eway.bean.PtIntgFlexTran;
import com.maan.eway.bean.PushIntegrationStatus;
import com.maan.eway.bean.ReInsuranceRiskDetails;
import com.maan.eway.bean.ReinsuranceCoverDetails;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.SeqPiftTranId;
import com.maan.eway.bean.YiChargeDetail;
import com.maan.eway.bean.YiCoverDetail;
import com.maan.eway.bean.YiDeductibleDetail;
import com.maan.eway.bean.YiInstallmentDetail;
import com.maan.eway.bean.YiPolicyApproval;
import com.maan.eway.bean.YiPolicyDetail;
import com.maan.eway.bean.YiPremCal;
import com.maan.eway.bean.YiSectionDetail;
import com.maan.eway.bean.YiVatDetail;
import com.maan.eway.integration.req.PremiaListRequest;
import com.maan.eway.integration.req.PremiaRequest;
import com.maan.eway.integration.req.ValuationReq;
import com.maan.eway.integration.res.IntegrationSaveRes;
import com.maan.eway.integration.res.PremiaResponse;
import com.maan.eway.integration.service.FrameReqService;
import com.maan.eway.integration.service.PhoenixIntegrationService;
import com.maan.eway.reinsurance.req.ReInsuranceQuoteReq;
import com.maan.eway.reinsurance.service.ReinsuranceService;
import com.maan.eway.repository.BranchMasterRepository;
import com.maan.eway.repository.ClausesMasterRepository;
import com.maan.eway.repository.CompanyProductMasterRepository;
import com.maan.eway.repository.CreditLimitDetailRepository;
import com.maan.eway.repository.EwayDeductibleMasterRepository;
import com.maan.eway.repository.ExclusionMasterRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotDriverDetailRepository;
import com.maan.eway.repository.MotcommDiscountDetailRepository;
import com.maan.eway.repository.MotorDriverDetailsRepository;
import com.maan.eway.repository.PgitPolRiskAddlInfoRepository;
import com.maan.eway.repository.PositionMasterRepository;
import com.maan.eway.repository.PremiaConfigDataMasterRepository;
import com.maan.eway.repository.PremiaConfigMasterRepository;
import com.maan.eway.repository.ProductSectionMasterRepository;
import com.maan.eway.repository.PtintgFlexTransRepository;
import com.maan.eway.repository.PushIntegrationStatusRepository;
import com.maan.eway.repository.ReInsuranceRiskDetailsRepositiory;
import com.maan.eway.repository.ReinsuranceCoverDetailsRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.repository.SeqPiftTranIdRepository;
import com.maan.eway.repository.TermsAndConditionRepository;
import com.maan.eway.repository.WarrantyMasterRepository;
import com.maan.eway.repository.YiChargeDetailRepository;
import com.maan.eway.repository.YiConditionDetailRepository;
import com.maan.eway.repository.YiCoverDetailRepository;
import com.maan.eway.repository.YiDeductibleDetailRepository;
import com.maan.eway.repository.YiInstallmentDetailRepsoitory;
import com.maan.eway.repository.YiPolicyApprovalRepository;
import com.maan.eway.repository.YiPolicyDetailRepository;
import com.maan.eway.repository.YiPremCalRepository;
import com.maan.eway.repository.YiSectionDetailRepository;
import com.maan.eway.repository.YiVatDetailRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class PhoenixIntegrationServiceImpl implements PhoenixIntegrationService {

	@Autowired
	private SectionDataDetailsRepository sectionDataRepo;
//	@Autowired
//	private PhoenixIntegrationService intSer;
	@Autowired
	private HomePositionMasterRepository homeRepo;
	@Autowired
	private MotorDriverDetailsRepository motDriDetails;

	@Autowired
	private YiCoverDetailRepository yiCoverDetailRepo;
	@Autowired
	private PgitPolRiskAddlInfoRepository pgitPolRiskRepo;
	@Autowired
	private YiChargeDetailRepository yiChargeDetailRepo;
	@Autowired
	private MotDriverDetailRepository motDrivDetailsRepo;
	@Autowired
	private MotcommDiscountDetailRepository motComRepo;
	@Autowired
	private YiPolicyDetailRepository yiPolicyReo;
	@Autowired
	private CreditLimitDetailRepository creditRepo;

	@Autowired
	private YiPolicyApprovalRepository yipolicyRepo;

	@Autowired
	private YiPremCalRepository yipremRepo;

	@Autowired
	private YiSectionDetailRepository yisecRepo;

	@Autowired
	private YiVatDetailRepository yivatRepo;

	@Autowired
	private SeqPiftTranIdRepository seqPiftTranIdRepo;

	@Autowired
	private PtintgFlexTransRepository ptTransRepo;

	@Autowired
	private FrameReqService frameReqService;
	@Autowired
	private MySqlQuery oracle;
	@Autowired
	private ValuationServiceImpl valuationServiceImpl;
	
	@Autowired
	private PositionMasterRepository pmRepo;
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private YiDeductibleDetailRepository yideductRepo;
	
	@Autowired
	private ReInsuranceRiskDetailsRepositiory reInsuranceRiskDetailsRepos;
	
	@Autowired
	private ReinsuranceCoverDetailsRepository reinsuranceCoverDetailsRepos;
	
	
	@Autowired
	private YiConditionDetailRepository yicondRepo;

	@Autowired
	private YiDeductibleDetailRepository deductibleDetailRepo;

	@Autowired
	private EwayDeductibleMasterRepository ewayDeductibleMasterRepo;

	@Autowired
	private HomePositionMasterRepository homePositionMasterRepo;

	@Autowired
	private ClausesMasterRepository clausesMasterRepo;

	@Autowired
	private WarrantyMasterRepository warrantyMasterRepo;

	@Autowired
	private ExclusionMasterRepository exclusionMasterRepo;
	@Autowired
	private BranchMasterRepository branchMasterRepo;
	@Autowired
	private ProductSectionMasterRepository productSectionMasterRepo;
	@Autowired
	private TermsAndConditionRepository termsAndConditionRepo;
	
	@Autowired
	private YiInstallmentDetailRepsoitory yiinstallmentDetailRepo;

	@Autowired
	private PushIntegrationStatusRepository pushIntegrationStatusRepo;
	
	@Autowired
	private CompanyProductMasterRepository companyProductMasterRepo;
	
	@Autowired
	private ReinsuranceService reinsuranceService;
	
	Gson json = new Gson();
	private Logger log = LogManager.getLogger(PhoenixIntegrationServiceImpl.class);

	/*
	 * 
	 * 
	 *
	 * get Master table : premia_config_master get Data Maste TAble :
	 * premia_config_data get Frame Insert query
	 */

	public boolean push(PremiaConfigMaster configMas, List<String> params, String quoteNo) {
		try {

			PremiaConfigMaster masterop = configMas;
			if (masterop != null) {
				PremiaConfigMaster masterdata = masterop; // col names
				List<CompanyProductMaster> companyProductList = companyProductMasterRepo
						.findByCompanyIdAndProductIdOrderByAmendIdDesc(configMas.getCompanyId(),
								Integer.valueOf(masterdata.getProductId()));

				boolean isPackage = !companyProductList.isEmpty()
						&& "Y".equalsIgnoreCase(companyProductList.get(0).getPackageYn());
				List<PremiaConfigDataMaster> configData = getPremiaConfigData(configMas.getCompanyId(),
						configMas.getProductId(), configMas.getPremiaId(),configMas.getSectionId(),isPackage);
				List<Map<String, Object>> listFromQuery = new ArrayList<Map<String, Object>>();

				if (StringUtils.isNotBlank(masterdata.getQueryKey())) {
					String query = oracle.getQuery(masterdata.getQueryKey());
					System.out.println("Premia Id :" + masterdata.getPremiaId());
					System.out.println("Premia Table Name :" + masterdata.getPremiaTableName());
					System.out.println("Premia SECTION_ID :" + masterdata.getSectionId());
					System.out.println("Premia SECTION_NAME :" + masterdata.getEntityName());
					System.out.println("**********************************************************");
					System.out.println("QueryKey : " + masterdata.getQueryKey());
					System.out.println("Oracle Main Quey : " + query);
					System.out.println("**********************************************************");
					List<String> asList = fromQuerytoList(query);

					Map<String, String> maps = fromListToMaps(asList);

					Map<String, String> avoidd = new HashMap<String, String>();

					if (configData != null && !configData.isEmpty()) {
						for (PremiaConfigDataMaster data : configData) {

							if (!"Y".equals(data.getDefaultYn())) {
								Map<String, String> filterdmap = maps.entrySet().stream()
										.filter(m -> data.getInputColumn().equalsIgnoreCase(m.getKey()))
										.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
								// Map<String, String> filered = filterdmap.get(0);
								String queryvalue = filterdmap.get(data.getInputColumn());

								if ("Date".equals(data.getDataTypeDesc())
										&& !avoidd.containsKey(data.getInputColumn())) {
									// queryvalue=(data.getDataFormatType()==null || StringUtils.isBlank(queryvalue)
									// ) ?queryvalue:data.getDataFormatType().replaceAll("<>",queryvalue );
									// avoidd.put(data.getInputColumn(), queryvalue);
									maps.put(data.getInputColumn(), queryvalue);
								}
								if ("N".equals(data.getDefaultYn()) && "Y".equals(data.getCaseConditionYn())) {
									maps.put(data.getInputColumn(), data.getCaseCondition());
								} else
									maps.put(data.getInputColumn(), queryvalue);

							} else if ("N".equals(data.getDefaultYn()) && "Y".equals(data.getCaseConditionYn())) {
								maps.put(data.getInputColumn(), data.getCaseCondition());
							}
						}
					}
					// Stream combined = Stream.concat(maps.entrySet().stream(),
					// map2.entrySet().stream());

					String framedselecquery = frameselectfromMap(maps);
					log.info("framedselecquery :: " + framedselecquery);
					query = "SELECT " + framedselecquery + " "
							+ query.substring(query.indexOf(" FROM"), query.length());

					log.info("framedselecquery with Select :: " + query);
					/*
					 * maps.get(0);
					 ***********/
					System.out.println("**********************************************************");
					System.out.println("Select Qury with case condition");
					
		            List<String> queryParams = new ArrayList<>(params);

		            if (String.valueOf(configMas.getPremiaId()).equals("3") && isPackage && !configMas.getSectionId().equals("99999")) {
		                queryParams.add(1, configMas.getSectionId().toString());
		            }
					listFromQuery = oracle.getListFromQueryWithoutKey(query, queryParams);
					System.out.println("**********************************************************");
//				if(listFromQuery!=null && listFromQuery.size()>0) {
//					 qdata = listFromQuery.get(0);
//				}
				}
				//Boolean result = delete(quoteNo, masterdata.getPremiaTableName());

				String companyId = masterdata.getCompanyId();

				boolean result = false;

				HomePositionMaster home = homeRepo.findByQuoteNo(quoteNo);

				if (home != null && home.getPolicyNo() != null) {

				   // boolean isSpecialCompany = companyId.matches("10004[6-9]|100050");
				    boolean isPremiaId3 = "3".equals(String.valueOf(masterdata.getPremiaId()));

					if (isPremiaId3 && isPackage && !configMas.getSectionId().equals("99999")) {
						result = pgitPolRiskRepo.deleteByQuotationPolicyNoAndSectionId(home.getPolicyNo(),
								masterdata.getSectionId()) > 0;
					} else {
						result = delete(quoteNo, masterdata.getPremiaTableName());
					}
				}

				for (Map<String, Object> qdata : listFromQuery) {
					System.out.println("Framing Insert Query");
					if (configData != null && !configData.isEmpty() && qdata != null) {
						Map<String, String> jmap = new HashMap<String, String>();
						List<String> colums = new ArrayList<String>();
						List<String> values = new ArrayList<String>();

						for (PremiaConfigDataMaster data : configData) {

							String value = "";
							if ("Y".equals(data.getDefaultYn())) {
								value = StringUtils.isBlank(data.getDefaultValue()) ? "" : data.getDefaultValue();

								if ("Date".equals(data.getDataTypeDesc())) {
									// String
									// dateformatt=StringUtils.isNotEmpty(data.getDataFormatType())?data.getDataFormatType().toUpperCase().replace("TO_CHAR",
									// "TO_DATE"):null;
									String dateformatt = StringUtils.isNotEmpty(data.getDataFormatType())
											? data.getDataFormatType().toUpperCase()
											: "yyyy-MM-dd hh:mm:ss";

									if (value.equalsIgnoreCase("SYSDATE")) {
										SimpleDateFormat dbF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
										value = dbF.format(new Date());
									} else {
										// value = " STR_TO_DATE(" +"'"+ value + ","+","+ "'"+ dateformatt+") " ;
										value = "'" + value + "'";

									}

								}
								value = (("String".equals(data.getDataTypeDesc())
										|| "Date".equals(data.getDataTypeDesc())) ? "'" + value + "'" : value);

							} /*
								 * else if("N".equals(data.getDefaultYn()) && "Y".equals(data.getCasecondYn() )
								 * ){ Object aliazval=qdata.get(data.getQueryAliaz())==null?"":qdata.get(data.
								 * getQueryAliaz()); value=aliazval; }
								 */else {
								Object aliazval = qdata.get(data.getInputColumn()) == null ? ""
										: qdata.get(data.getInputColumn());

								value = String.valueOf(aliazval);
//							System.out.println("Data Values :"+value);

								value = (("String".equals(data.getDataTypeDesc())
										|| "Date".equals(data.getDataTypeDesc())) ? value.replace("'", "") : value);
//							System.out.println("Data Values :"+value);
								if ("Date".equals(data.getDataTypeDesc())) {
									// String
									// dateformatt=StringUtils.isNotEmpty(data.getDataFormatType())?data.getDataFormatType().toUpperCase().replace("TO_CHAR",
									// "TO_DATE"):null;
									String dateformatt = StringUtils.isNotEmpty(data.getDataFormatType())
											? data.getDataFormatType().toUpperCase()
											: "yyyy-MM-dd hh:mm:ss";
									if (dateformatt != null)
										// value = " STR_TO_DATE(" +"'"+ value + "'"+","+ "'"+dateformatt+"'"+") " ;
										// value=dateformatt.replaceAll("<>","'"+aliazval.toString()+"'" );
										value = "'" + value + "'";

								}

//							value=(("String".equals(data.getDataTypeDesc()) )?"'"+String.valueOf(aliazval)+"'":value);
								value = (("Number".equals(data.getDataTypeDesc()))
										? "'" + String.valueOf(aliazval) + "'"
										: value);
								value = (("String".equals(data.getDataTypeDesc())) ? "'" + String.valueOf(value) + "'"
										: value);

							}

							jmap.put(data.getColumnName(), value);
//						System.out.println("Column Id :"+data.getColumnId());
							colums.add(data.getColumnName());
//						System.out.println("Column Name :"+data.getColumnName());
							values.add(value);
//						System.out.println("Data Values :"+value);
						}

						if (result = true) {
							if (!jmap.isEmpty()) {
								// Madison
//						Boolean result1=deleteTable(quoteNo,masterdata.getPremiaTableName(),jmap);
								System.out.println("***************************************************************");
								System.out.println("MySQL Inset Query");
//						String insertQuery="INSERT INTO "+masterdata.getPremiaTableName()+" ("+StringUtils.join(colums,",")
//						+") VALUES ("+StringUtils.join(values,",")+")";
//						log.info("Insert Query::"+insertQuery);
//						oracle.insert(insertQuery);
								String resultColumn = colums.stream().map(String::toLowerCase)
										.collect(Collectors.joining(","));

								String insertQuery = "INSERT INTO " + masterdata.getPremiaTableName().toLowerCase()
										+ " (" + resultColumn + ") VALUES (" + StringUtils.join(values, ",") + ")";
								log.info("Insert Query::" + insertQuery);
								oracle.insert(insertQuery);
								System.out.println("****************************************************************");
							}
						}

					}
				}
//			// Framing External Api
//			String policyNo = "";
//			String reqRefNo = "";
//			String companyId="";
//			String productId="";
//			HomePositionMaster home = homeRepo.findByQuoteNo(quoteNo);
//			if (home != null) {
//				policyNo = home.getPolicyNo();
//				reqRefNo = home.getRequestReferenceNo();
//				companyId= home.getCompanyId();
//				productId= home.getProductId().toString();
//			}
//			CompanyProductMaster product =  getCompanyProductMasterDropdown(companyId , productId);
//		
////			if ("100002".equalsIgnoreCase(companyId)) {
//				ewayMotorPremiaPush(policyNo, reqRefNo, configMas);
////			}
			}

			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}

	private String updateIntegrationStatus(String quoteNo, HomePositionMaster home, String reqRefNo) {
		boolean status = false, status1 = false, status2 = false, status3 = false, status4 = false, status5 = false,
				status6 = false, status7 = false, status8 = false, status9 = false, status10 = false;
		String policyNo = home.getPolicyNo();
		String result = "";
		try {
			List<String> errorList = new ArrayList<>();
			List<YiPolicyDetail> list = yiPolicyReo.findByQuotationPolicyNo(policyNo);
			if (list.size() > 0 && list != null) {
				status = true;
				System.out.println("Saved in My sql YiPolicyDetail");
			} else {
				status = false;
				errorList.add("YiPolicyDetail");
				System.out.println("Not Saved in My sql YiPolicyDetail");
			}

			List<YiSectionDetail> list1 = yisecRepo.findByQuotationPolicyNo(policyNo);
			if (list1.size() > 0 && list1 != null) {
				status1 = true;
				System.out.println("Saved in My sql YiSectionDetail");
			} else {
				status1 = false;
				errorList.add("YiSectionDetail");
				System.out.println("Not Saved in My sql YiSectionDetail");
			}

			List<PgithPolRiskAddlInfo> list2 = pgitPolRiskRepo.findByQuotationPolicyNo(policyNo);
			if (list2.size() > 0 && list2 != null) {
				status2 = true;
				System.out.println("Saved in My sql PgithPolRiskAddlInfo");
			} else {
				status2 = false;
				errorList.add("PgithPolRiskAddlInfo");
				System.out.println("Not Saved in My sql PgithPolRiskAddlInfo");
			}
			if (home.getProductId().toString().equals("5")) {
				List<MotDriverDetail> list3 = motDrivDetailsRepo.findByQuotationPolicyNo(policyNo);
				if (list3.size() > 0 && list3 != null) {
					status3 = true;
				} else {
					List<MotorDriverDetails> motDriverData = motDriDetails.findByQuoteNo(quoteNo);
					if (motDriverData.isEmpty()) {
						status3 = true;
						System.out.println("Saved in My sql MotDriverDetail");
					} else {
						status3 = false;
						errorList.add("MotDriverDetail");
						System.out.println("Not Saved in My sql MotDriverDetail");
					}
				}
			}else {
				status3 = true;
			}
			List<YiCoverDetail> list4 = yiCoverDetailRepo.findByQuotationPolicyNo(policyNo);
			if (list4.size() > 0 && list4 != null) {
				status4 = true;
				System.out.println("Saved in My sql YiCoverDetail");
			} else {
				status4 = false;
				errorList.add("YiCoverDetail");
				System.out.println("Not Saved in My sql YiCoverDetail");
			}

			List<MotCommDiscountDetail> list5 = motComRepo.findByQuotationPolicyNo(policyNo);
			if (list5.size() > 0 && list5 != null) {
				status5 = true;
				System.out.println("Saved in My sql MotCommDiscountDetail");
			} else {
				status5 = true;
			}
			if (!home.getCompanyId().equals("100046") && !home.getCompanyId().equals("100047")) {
			List<YiChargeDetail> list6 = yiChargeDetailRepo.findByQuotationPolicyNo(policyNo);
			if (list6.size() > 0 && list6 != null) {
				status6 = true;
				System.out.println("Saved in My sql YiChargeDetail");
			} else {
				status6 = false;
				errorList.add("YiChargeDetail");
				System.out.println("Not Saved in My sql YiChargeDetail");
			}}else {
				status6 = true;
			}

			List<YiVatDetail> list7 = yivatRepo.findByQuotationPolicyNo(policyNo);
			if (list7.size() > 0 && list7 != null) {
				status7 = true;
				System.out.println("Saved in My sql YiVatDetail");
			} else {
				status7 = false;
				errorList.add("YiVatDetail");
				System.out.println("Not Saved in My sql YiVatDetail");
			}
			List<YiPremCal> list8 = yipremRepo.findByQuotationPolicyNo(policyNo);
			if (list8.size() > 0 && list8 != null) {
				status8 = true;
				System.out.println("Saved in My sql YiPremCal");
			} else {
				status8 = false;
				errorList.add("YiPremCal");
				System.out.println("Not Saved in My sql YiPremCal");
			}

			List<YiPolicyApproval> list9 = yipolicyRepo.findByQuotationPolicyNo(policyNo);
			if (list9.size() > 0 && list9 != null) {
				status9 = true;
				System.out.println("Saved in My sql YiPolicyApproval");
			} else {
				status9 = false;
				errorList.add("YiPolicyApproval");
				System.out.println("Not Saved in My sql YiPolicyApproval");
			}
			List<CreditLimitDetail> list10 = creditRepo.findByRequestreferenceno(reqRefNo);
			if (list10.size() > 0 && list10 != null) {
				status10 = true;
				System.out.println("Saved in My sql CreditLimitDetail");
			} else {
				status10 = false;
				errorList.add("YiPolicyApproval");
				System.out.println("Not Saved in My sql CreditLimitDetail");
			}
			if (status && status1 && status2 && status3 && status4 && status5 && status6 && status7 && status8
					&& status9 && status10) {
				home.setCoreIntgStatus("Data Successfully saved in  My Sql");
//			home.setIntegrationStatus("S");
				home.setIntegrationError("");
				result = "S";
			} else {
				home.setCoreIntgStatus("Data Failed to saved in  My Sql");
//			home.setIntegrationStatus("F");
				home.setIntegrationError("Data Failed to saved in  My Sql " + errorList);
				result = "F";
			}
			homeRepo.save(home);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			home.setCoreIntgStatus("F");
		}
		homeRepo.saveAndFlush(home);
		return result;
	}

	public IntegrationSaveRes ewayMotorPremiaPush(String policyNo, String reqRefNo, PremiaConfigMaster configMas,
			HomePositionMaster home) {
		IntegrationSaveRes res1 = new IntegrationSaveRes();
		String companyId = configMas.getCompanyId();

		boolean check = false;
		try {
			System.out.println("*********EXTERNAL API CALL STARTS*********");
			System.out.println("*********PolicyNo " + policyNo);

			if (configMas.getPremiaId() == 1) {
				System.out.println("*********1.YiPolicyDetail: ");
				Object list = frameReqService.pushYiPolicyDetail(policyNo,companyId);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;

				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}

			} else if (configMas.getPremiaId() == 2) {
				System.out.println("*********2.YiSectionDetail:");
				Object list = frameReqService.pushYiSectionDetail(policyNo,companyId);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;
				res1.getResponse();
				res1.getErrorMessage();
				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}
			} else if (configMas.getPremiaId() == 3) {
				System.out.println("*********3.PgitPolRiskAddlInfo:");
				Object list = frameReqService.pushPgitPolRiskAddlInfo(policyNo,companyId);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;
				res1.getResponse();
				res1.getErrorMessage();
				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}
			} else if (configMas.getPremiaId() == 4) {
				System.out.println("*********4.MotDriverDetail: ");
				Object list = frameReqService.pushMotDriverDetail(policyNo,companyId);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;
				res1.getResponse();
				res1.getErrorMessage();
				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}
			} else if (configMas.getPremiaId() == 5) {
				System.out.println("*********5.YiCoverDetail: ");
				Object list = frameReqService.pushYiCoverDetail(policyNo,companyId);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;
				res1.getResponse();
				res1.getErrorMessage();
				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}
			} else if (configMas.getPremiaId() == 6) {
				System.out.println("*********6.MotCommDiscountDetail:");
				Object list = frameReqService.pushMotCommDiscountDetail(policyNo,companyId);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;
				res1.getResponse();
				res1.getErrorMessage();
				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}
			} else if (configMas.getPremiaId() == 7) {
				System.out.println("*********7.YiChargeDetail: ");
				Object list = frameReqService.pushYiChargeDetail(policyNo,companyId);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;
				res1.getResponse();
				res1.getErrorMessage();
				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}
			} else if (configMas.getPremiaId() == 8) {
				System.out.println("*********8.YiVatDetail:");
				Object list = frameReqService.pushYiVatDetail(policyNo,companyId);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;
				res1.getResponse();
				res1.getErrorMessage();
				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}
			} else if (configMas.getPremiaId() == 9) {
				System.out.println("*********9.YiPremCal:");
				Object list = frameReqService.pushYiPremCal(policyNo,companyId);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;
				res1.getResponse();
				res1.getErrorMessage();
				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}
			} else if (configMas.getPremiaId() == 10) {
				System.out.println("*********10.YiPolicyApproval:");
				Object list = frameReqService.pushYiPolicyApproval(policyNo,companyId);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;
				res1.getResponse();
				res1.getErrorMessage();
				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}
			} else if (configMas.getPremiaId() == 11) {
				System.out.println("*********11.CreditLimitDetail:");
				Object list = frameReqService.pushCreditLimitDetail(reqRefNo,companyId);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;
				res1.getResponse();
				res1.getErrorMessage();
				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}
			}
			if (check = true) {
				home.setCoreIntgStatus("Data Inserted saved in  Oracle DB");
//			home.setIntegrationStatus("S");
				home.setIntegrationError("");
				homeRepo.save(home);
			} else {
				home.setCoreIntgStatus("Data Failed saved in  Oracle DB");
//			home.setIntegrationStatus("F");
				home.setIntegrationError(res1.getErrorMessage());
				homeRepo.save(home);
			}
			System.out.println("Response from oracle " + json.toJson(res1));

			return res1;
		} catch (Exception e) {
			e.printStackTrace();
			return res1;
		}
	}

	public void madisonMotorPremiaPush(String policyNo, String reqRefNo) {
		try {

			System.out.println("*********EXTERNAL API CALL STARTS*********");
			System.out.println("*********PolicyNo " + policyNo);
			System.out.println("*********PtIntgFlexTran:");
			Object list = frameReqService.pushPtIntgFlexTran(policyNo);
			System.out.println("List " + json.toJson(list));
			System.out.println("_____________________________________________ ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean delete(String quoteNo, String tableName) {
		Boolean result = true;
		try {
			String policyNo = "";
			String reqRefNo = "";
			String companyId = "";
			String productId = "";
			HomePositionMaster home = homeRepo.findByQuoteNo(quoteNo);
			if (home != null) {
				policyNo = home.getPolicyNo();
				reqRefNo = home.getRequestReferenceNo();
				companyId = home.getCompanyId();
				productId = home.getProductId().toString();
			}
			if ("Yi_Policy_Detail".equalsIgnoreCase(tableName)) {
				List<YiPolicyDetail> list = yiPolicyReo.findByRequestreferenceno(reqRefNo);
				if (list.size() > 0 && list != null) {
					yiPolicyReo.deleteAll(list);
					result = true;
				}
			} else if ("Yi_Section_Detail".equalsIgnoreCase(tableName)) {
				List<YiSectionDetail> list = yisecRepo.findByRequestreferenceno(reqRefNo);
				if (list.size() > 0 && list != null) {
					yisecRepo.deleteAll(list);
					result = true;
				}
			} else if ("PGIT_POL_RISK_ADDL_INFO_01".equalsIgnoreCase(tableName)) {
				// List<PgithPolRiskAddlInfo>
				// list=pgitPolRiskRepo.findByQuotationPolicyNo(policyNo);
				List<PgithPolRiskAddlInfo> list = pgitPolRiskRepo.findByRequestReferenceNo(reqRefNo);
				if (list.size() > 0 && list != null) {
					pgitPolRiskRepo.deleteAll(list);
					result = true;
				}
			} else if ("Mot_Driver_Detail".equalsIgnoreCase(tableName)) {
				// List<MotDriverDetail>
				// list=motDrivDetailsRepo.findByQuotationPolicyNo(policyNo);
				List<MotDriverDetail> list = motDrivDetailsRepo.findByRequestreferenceno(reqRefNo);
				if (list.size() > 0 && list != null) {
					motDrivDetailsRepo.deleteAll(list);
					result = true;
				}
			} else if ("Yi_Cover_Detail".equalsIgnoreCase(tableName)) {
				// List<YiCoverDetail> list=yiCoverDetailRepo.findByQuotationPolicyNo(policyNo);
				List<YiCoverDetail> list = yiCoverDetailRepo.findByRequestreferenceno(reqRefNo);
				if (list.size() > 0 && list != null) {
					yiCoverDetailRepo.deleteAll(list);
					result = true;
				}
			} else if ("Mot_Comm_Discount_Detail".equalsIgnoreCase(tableName)) {
				// List<MotCommDiscountDetail>
				// list=motComRepo.findByQuotationPolicyNo(policyNo);
				List<MotCommDiscountDetail> list = motComRepo.findByRequestreferenceno(reqRefNo);
				if (list.size() > 0 && list != null) {
					motComRepo.deleteAll(list);
					result = true;
				}
			} else if ("Yi_Charge_Detail".equalsIgnoreCase(tableName)) {
				// List<YiChargeDetail>
				// list=yiChargeDetailRepo.findByQuotationPolicyNo(policyNo);
				List<YiChargeDetail> list = yiChargeDetailRepo.findByRequestreferenceno(reqRefNo);
				if (list.size() > 0 && list != null) {
					yiChargeDetailRepo.deleteAll(list);
					result = true;
				}
			} else if ("Yi_Vat_Detail".equalsIgnoreCase(tableName)) {
				// List<YiVatDetail> list=yivatRepo.findByQuotationPolicyNo(policyNo);
				List<YiVatDetail> list = yivatRepo.findByRequestreferenceno(reqRefNo);
				if (list.size() > 0 && list != null) {
					yivatRepo.deleteAll(list);
					result = true;
				}
			} else if ("Yi_Prem_Cal".equalsIgnoreCase(tableName)) {
				// List<YiPremCal> list=yipremRepo.findByQuotationPolicyNo(policyNo);
				List<YiPremCal> list = yipremRepo.findByRequestreferenceno(reqRefNo);
				if (list.size() > 0 && list != null) {
					yipremRepo.deleteAll(list);
					result = true;
				}
			} else if ("Yi_Policy_Approval".equalsIgnoreCase(tableName)) {
				// List<YiPolicyApproval> list=yipolicyRepo.findByQuotationPolicyNo(policyNo);
				List<YiPolicyApproval> list = yipolicyRepo.findByRequestreferenceno(reqRefNo);
				if (list.size() > 0 && list != null) {
					yipolicyRepo.deleteAll(list);
					result = true;
				}
			} else if ("Credit_Limit_Detail".equalsIgnoreCase(tableName)) {
				List<CreditLimitDetail> list = creditRepo.findByRequestreferenceno(reqRefNo);
				if (list.size() > 0 && list != null) {
					creditRepo.deleteAll(list);
					result = true;
				}
			} else if ("Yi_Deductible_Detail".equalsIgnoreCase(tableName)) {
				List<YiDeductibleDetail> list = yideductRepo.findByQuotationPolicyNo(policyNo);
				if (list.size() > 0 && list != null) {
					yideductRepo.deleteAll(list);
					result = true;
				}
			}
			else if ("yi_installment_detail".equalsIgnoreCase(tableName)) {
				List<YiInstallmentDetail> list = yiinstallmentDetailRepo.findByQuotationPolicyNo(policyNo);
				if (list.size() > 0 && list != null) {
					yiinstallmentDetailRepo.deleteAll(list);
					System.out.println("yi_installment_detail is deleted successfully");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Boolean deleteTable(String quoteNo, String tableName, Map<String, String> jmap) {
		Boolean result = false;
		try {
			String companyId = "";
			String productId = "";
			String policyNo = "";
			HomePositionMaster home = homeRepo.findByQuoteNo(quoteNo);
			if (home != null) {
				policyNo = home.getPolicyNo();
				companyId = home.getCompanyId();
				productId = home.getProductId().toString();
			}
			if ("100004".equalsIgnoreCase(companyId) && "5".equalsIgnoreCase(productId)) {
				// using for-each loop for iteration over Map.entrySet()
				for (Map.Entry<String, String> entry : jmap.entrySet()) {
					System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
					String key = entry.getKey();
					String value = "";
					if ("PIFT_LEVEL".equalsIgnoreCase(key)) {
						value = entry.getValue();
						if ("Pt_intg_flex_tran".equalsIgnoreCase(tableName)) {
							List<PtIntgFlexTran> list = ptTransRepo.findByPiftPolicyNoAndPiftLevel(policyNo,
									value.replaceAll("\'", ""));
							if (list.size() > 0 && list != null) {
								ptTransRepo.deleteAll(list);
								System.out.println("Record Deteted :: " + key + " " + value);
								result = true;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private String frameselectfromMap(Map<String, String> maps) {
		String result = maps.entrySet().stream().map(map -> (map.getValue() + " " + map.getKey()))
				.collect(Collectors.joining(","));

		return result;
	}

	private List<String> fromQuerytoList(String selectquery) {
		if (selectquery.indexOf(",") != -1) {
			selectquery = selectquery.substring(selectquery.indexOf("SELECT") , selectquery.indexOf(" FROM"));
			List<String> arrays = new ArrayList<String>();
			String[] col_aliz = selectquery.split(","); // column names
			for (int i = 0; i < col_aliz.length; i++) {
				arrays.add(col_aliz[i]);
			}
			return arrays;
		}
		return null;
	}

	private Map<String, String> fromListToMaps(List<String> arrays) {
		Map<String, String> listmaps = new HashMap<String, String>();
		for (String val : arrays) {
			if (val.trim().indexOf(" ") != 1) {
				val = val.trim();
				String[] split = val.split(" ");
				// Map<String,String> hmap=new HashMap<String,String>();
				listmaps.put(split[split.length - 1], split[0]);
				// listmaps.add(hmap);
			}
		}
		return listmaps;
	}

	@Override
	public PremiaResponse pushPremiaIntegration(PremiaRequest request) {
		PremiaResponse response = new PremiaResponse();
		try {
			LocalDateTime currentDateTime = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String formattedDateTime = currentDateTime.format(formatter);
			String quoteNo = "";
			String policyNo = "";
			String reqRefNo = "";
			String companyId = "";
			String productId = "";
			HomePositionMaster home = null;
			String status = "";
			// HomePositionMaster home = homeRepo.findByQuoteNo(request.getPolicyNo());
			if (StringUtils.isBlank(request.getQuoteNo())) {
				home = homeRepo.findByPolicyNo(request.getPolicyNo());
			} else {
				home = homeRepo.findByQuoteNo(request.getQuoteNo());
			}
			if (home != null) {
				policyNo = home.getPolicyNo();
				quoteNo = home.getQuoteNo();
				reqRefNo = home.getRequestReferenceNo();
				companyId = home.getCompanyId();
				productId = home.getProductId().toString();
				status = home.getStatus();
			}
			CompanyProductMaster product = getCompanyProductMasterDropdown(companyId, productId);
			if ("P".equalsIgnoreCase(status)|| "D".equalsIgnoreCase(status) || "E".equalsIgnoreCase(status)) {
				List<PremiaConfigMaster> configMasterList = getPremiaConfigMaster(home.getCompanyId(),
						home.getProductId(), request.getPremiaIds());
				if (configMasterList.size() > 0 && configMasterList != null && !configMasterList.isEmpty()) {
					List<String> param = new ArrayList<String>();
					param.add(quoteNo);
					List<String> successList = new ArrayList<>();
					Set<String> stickerNoList = new HashSet<>();
					List<String> successOracleList = new ArrayList<>();
					List<String> failureOracleList = new ArrayList<>();

					// Insert Data MySql
					System.out.println("*********Inserting Data In MySql Started " + quoteNo + " " + formattedDateTime);
					List<SectionDataDetails> risks = sectionDataRepo.findByQuoteNo(request.getQuoteNo());
					if (risks != null) {
						stickerNoList = risks.stream().map(SectionDataDetails::getStickerNumber)
								.filter(Objects::nonNull).collect(Collectors.toSet());
					}

					// TIRA233
					if ("100002".equalsIgnoreCase(companyId)) {
						if ((!stickerNoList.isEmpty() && stickerNoList.size() == home.getNoOfVehicles())) {
							for (PremiaConfigMaster configMas : configMasterList) {
								boolean push = push(configMas, param, quoteNo);
								if (push == true) {
									response.setResponse("Success");
									successList.add(configMas.getPremiaTableName());
								} else {
									response.setResponse("Failed");
								}

							}
						} else if (!"5".equalsIgnoreCase(productId) || !"46".equalsIgnoreCase(productId)) {
							for (PremiaConfigMaster configMas : configMasterList) {
								boolean push = push(configMas, param, quoteNo);
								if (push == true) {
									response.setResponse("Success");
									successList.add(configMas.getPremiaTableName());
								} else {
									response.setResponse("Failed");
								}

							}
						}
//			else if (!stickerNoList.isEmpty() && (stickerNoList.size() != home.getNoOfVehicles())) {
//				List<SectionDataDetails> risks1 = risks.stream()
//						.filter(risk -> (!risk.getResponseStatusCode().equalsIgnoreCase("TIRA233"))
//								&& risk.getStickerNumber() == null)
//						.collect(Collectors.toList());
//				if (risks1.isEmpty()) {
//
//					for (PremiaConfigMaster configMas : configMasterList) {
//						boolean push = push(configMas, param, quoteNo);
//						if (push == true) {
//							response.setResponse("Success");
//							successList.add(configMas.getPremiaTableName());
//						} else {
//							response.setResponse("Failed");
//						}
//
//					}
//				}else {
//					response.setResponse("Premia Failed Sticker Number is Null");
//					System.out.println("Premia Failed Sticker Number is Null");
//					failureOracleList.add("Premia Failed Sticker Number is Null");
//					home.setCoreIntgStatus("Premia Failed Sticker Number is Null");
//					home.setIntegrationStatus("F");
//					home.setIntegrationError("Premia Failed Sticker Number is Null");
//					homeRepo.save(home);
//				}
//			}
						else {
							response.setResponse("Premia Failed Sticker Number is Null");
							System.out.println("Premia Failed Sticker Number is Null");
							failureOracleList.add("Premia Failed Sticker Number is Null");
							home.setCoreIntgStatus("Premia Failed Sticker Number is Null");
//				home.setIntegrationStatus("F");
							home.setIntegrationError("Premia Failed Sticker Number is Null");
							homeRepo.save(home);
						}

					} else if ((!"100002".equalsIgnoreCase(companyId))) {
						for (PremiaConfigMaster configMas : configMasterList) {
							boolean push = push(configMas, param, quoteNo);
							if (push == true) {
								response.setResponse("Success");
								successList.add(configMas.getPremiaTableName());
							} else {
								response.setResponse("Failed");
							}

						}
					} else {
						response.setResponse("Premia Failed Sticker Number is Null");
						System.out.println("Premia Failed Sticker Number is Null");
						failureOracleList.add("Premia Failed Sticker Number is Null");
						home.setCoreIntgStatus("Premia Failed Sticker Number is Null");
//			home.setIntegrationStatus("F");
						home.setIntegrationError("Premia Failed Sticker Number is Null");
						homeRepo.save(home);
					}

					// Status of My Sql Data
					String mySqlTable = "F";
					if ("100002".equalsIgnoreCase(companyId)) {

						if ((!stickerNoList.isEmpty() && stickerNoList.size() == home.getNoOfVehicles())) {
							mySqlTable = updateIntegrationStatus(quoteNo, home, reqRefNo);
							if (mySqlTable.equalsIgnoreCase("S")) {
								response.setResponse("Success");
							} else {
								response.setResponse("Failed to Save in My Sql");
							}
						} else if (!"5".equalsIgnoreCase(productId) || !"46".equalsIgnoreCase(productId)) {
							mySqlTable = updateNonMotorIntegrationStatus(quoteNo, home, reqRefNo);
							if (mySqlTable.equalsIgnoreCase("S")) {
								response.setResponse("Success");
							} else {
								response.setResponse("Failed to Save in My Sql");
							}
						}
//			else if (!stickerNoList.isEmpty() && (stickerNoList.size() != home.getNoOfVehicles())) {
//				List<SectionDataDetails> risks1 = risks.stream()
//						.filter(risk -> (!risk.getResponseStatusCode().equalsIgnoreCase("TIRA233"))
//								&& risk.getStickerNumber() == null)
//						.collect(Collectors.toList());
//				if (risks1.isEmpty()) {
//
//					mySqlTable = updateIntegrationStatus(quoteNo, home, reqRefNo);
//					if (mySqlTable.equalsIgnoreCase("S")) {
//						response.setResponse("Success");
//					} else {
//						response.setResponse("Failed to Save in My Sql");
//					}
//				}else {
//					response.setResponse("Premia Failed Sticker Number is Null");
//					System.out.println("Premia Failed Sticker Number is Null");
//					home.setCoreIntgStatus("Premia Failed Sticker Number is Null");
//				//	home.setIntegrationStatus("F");
//					home.setIntegrationError("Premia Failed Sticker Number is Null");
//					homeRepo.save(home);
//				}
//			}
						else {
							response.setResponse("Premia Failed Sticker Number is Null");
							System.out.println("Premia Failed Sticker Number is Null");
							home.setCoreIntgStatus("Premia Failed Sticker Number is Null");
//				home.setIntegrationStatus("F");
							home.setIntegrationError("Premia Failed Sticker Number is Null");
							homeRepo.save(home);
						}

					} else if (!"100002".equalsIgnoreCase(companyId)) {
						mySqlTable = updateIntegrationStatus(quoteNo, home, reqRefNo);
						if (mySqlTable.equalsIgnoreCase("S")) {
							response.setResponse("Success");
							System.out.println("Success");
						} else {
							response.setResponse("Failed to Save in My Sql");
							System.out.println("Failed to Save in My Sql");
						}
					}
					System.out.println("*********MySql Block Ended " + quoteNo + " " + formattedDateTime);
					// Framing External Api

					System.out.println("Response from mySqlTable :" + mySqlTable);
					System.out.println("*********Oracle Block started " + quoteNo + " " + formattedDateTime);
					if (mySqlTable.equalsIgnoreCase("S")) {
						savePushIntegrationStatus(policyNo);

						/*
						  draftOrcalInsert(response, policyNo, reqRefNo, home, configMasterList,
						  successOracleList, failureOracleList);
						 */

					}
					System.out.println("*********Oracle Block Ended " + quoteNo + " " + formattedDateTime);
					// Premia Posting Calling procedural call
					System.out.println("*********Procedure Block Started " + quoteNo + " " + formattedDateTime);
					if (failureOracleList.isEmpty()) {/**
						 * draftPremiaCall(response, formattedDateTime, quoteNo, policyNo, companyId,
						 * home);
						 **/}
				} else {
					response.setResponse("Premia Not Available");
				}
			}
			if ("100004".equalsIgnoreCase(companyId)) {
				SeqPiftTranId entity = new SeqPiftTranId();
				List<SeqPiftTranId> data = seqPiftTranIdRepo.findAllByOrderByTranIdDesc();
				Long id = data.get(0).getTranId() + 1;
				entity.setTranId(id);
				seqPiftTranIdRepo.saveAndFlush(entity);
				System.out.println(entity);
			}
			if ("100004".equalsIgnoreCase(companyId)) {
				if (product.getMotorYn().equalsIgnoreCase("M")) {
					madisonMotorPremiaPush(policyNo, reqRefNo);
				}

			}
			if ("100020".equalsIgnoreCase(companyId)) {
				ValuationReq vreq = new ValuationReq();
				vreq.setBranchCode(home.getBranchCode());
				vreq.setCompanyId(companyId);
				vreq.setQuoteNo(quoteNo);
				valuationServiceImpl.pushValuation(vreq);
			}
			
			ReInsuranceQuoteReq reInsuranceQuoteReq =new ReInsuranceQuoteReq();
			reInsuranceQuoteReq.setPolicyNo(policyNo);
			reInsuranceQuoteReq.setQuoteNo(request.getQuoteNo());
			reInsuranceQuoteReq.setCreatedBy(null);
			reinsuranceService.updatePolicyNo(reInsuranceQuoteReq);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return response;
	}


	public synchronized List<PremiaConfigMaster> getPremiaConfigMaster(String insuraceId, Integer productId,
			List<String> premiaIds) {
		List<PremiaConfigMaster> list = new ArrayList<PremiaConfigMaster>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PremiaConfigMaster> query = cb.createQuery(PremiaConfigMaster.class);
			// Find All
			Root<PremiaConfigMaster> c = query.from(PremiaConfigMaster.class);

			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("premiaId")));

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<PremiaConfigMaster> ocpm1 = effectiveDate.from(PremiaConfigMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("premiaId"), ocpm1.get("premiaId"));
			Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a3 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, a3, a4);

			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<PremiaConfigMaster> ocpm2 = effectiveDate2.from(PremiaConfigMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a5 = cb.equal(c.get("premiaId"), ocpm2.get("premiaId"));
			Predicate a6 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a7 = cb.equal(c.get("productId"), ocpm2.get("productId"));
			Predicate a8 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a5, a6, a7, a8);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), insuraceId);
			Predicate n5 = cb.equal(c.get("productId"), productId);
			Predicate n10 = cb.equal(c.get("status"), "Y");
//			Predicate n7 = cb.equal(c.get("productId"), "99999");
//			Predicate n8 = cb.or(n5,n7);
			// In
//			Expression<String>e0= c.get("premiaId");
//			Predicate n6 = e0.in(premiaIds);
			query.where(n1, n2, n3, n4, n5, n10).orderBy(orderList);

			// Get Result
			TypedQuery<PremiaConfigMaster> result = em.createQuery(query);
			list = result.getResultList();
			if (list.size() > 0) {
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getPremiaId(),o.getSectionId())))
						.collect(Collectors.toList());
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}

	private static <T> java.util.function.Predicate<T> distinctByKey(
			java.util.function.Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	public synchronized List<PremiaConfigDataMaster> getPremiaConfigData(String insuraceId, String productId,
			Integer premiaId,String sectionId,boolean isPackage) {
		List<PremiaConfigDataMaster> list = new ArrayList<PremiaConfigDataMaster>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PremiaConfigDataMaster> query = cb.createQuery(PremiaConfigDataMaster.class);
			// Find All
			Root<PremiaConfigDataMaster> c = query.from(PremiaConfigDataMaster.class);

			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("premiaId")));

			/*
			 * // Effective Date Start Max Filter Subquery<Long> effectiveDate =
			 * query.subquery(Long.class); Root<PremiaConfigDataMaster> ocpm1 =
			 * effectiveDate.from(PremiaConfigDataMaster.class);
			 * effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.
			 * class))); Predicate a1 = cb.equal(c.get("premiaId"),ocpm1.get("premiaId"));
			 * Predicate a2 = cb.equal(c.get("companyId"),ocpm1.get("companyId")); Predicate
			 * a3 = cb.equal(c.get("productId"),ocpm1.get("productId")); Predicate a4 =
			 * cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			 * effectiveDate.where(a1,a2,a3,a4);
			 * 
			 * // Effective Date End Max Filter Subquery<Long> effectiveDate2 =
			 * query.subquery(Long.class); Root<PremiaConfigDataMaster> ocpm2 =
			 * effectiveDate2.from(PremiaConfigDataMaster.class);
			 * effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class
			 * ))); Predicate a5 = cb.equal(c.get("premiaId"),ocpm2.get("premiaId"));
			 * Predicate a6 = cb.equal(c.get("companyId"),ocpm2.get("companyId")); Predicate
			 * a7 = cb.equal(c.get("productId"),ocpm2.get("productId")); Predicate a8 =
			 * cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			 * effectiveDate2.where(a5,a6,a7,a8);
			 * 
			 * // Where Predicate n1 = cb.equal(c.get("status"),"Y"); Predicate n2 =
			 * cb.equal(c.get("effectiveDateStart"),effectiveDate); Predicate n3 =
			 * cb.equal(c.get("effectiveDateEnd"),effectiveDate2); Predicate n4 =
			 * cb.equal(c.get("companyId"), insuraceId); Predicate n5 =
			 * cb.equal(c.get("productId"), productId); Predicate n6 =
			 * cb.equal(c.get("premiaId"), premiaId);
			 * query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
			 */

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<PremiaConfigDataMaster> ocpm1 = amendId.from(PremiaConfigDataMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("premiaId"), c.get("premiaId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), c.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), c.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("columnId"), c.get("columnId"));

			amendId.where(a1, a2, a3, a4);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("amendId"), amendId);
			Predicate n3 = cb.equal(c.get("companyId"), insuraceId);
			Predicate n4 = cb.equal(c.get("productId"), productId);
			Predicate n5 = cb.equal(c.get("productId"), "99999");
			Predicate n6 = cb.or(n4, n5);
			Predicate n7 = cb.equal(c.get("premiaId"), premiaId);
			Predicate n8 = cb.equal(c.get("sectionId"), sectionId);
			if (premiaId==3 && isPackage){
				query.where(n1, n2, n3, n6, n7,n8).orderBy(orderList);

			}else {
				query.where(n1, n2, n3, n6, n7).orderBy(orderList);
			}
			// Get Result
			TypedQuery<PremiaConfigDataMaster> result = em.createQuery(query);
			list = result.getResultList();

			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getPremiaId(), o.getColumnId())))
					.collect(Collectors.toList());

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}

	public synchronized CompanyProductMaster getCompanyProductMasterDropdown(String companyId, String productId) {
		CompanyProductMaster product = new CompanyProductMaster();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			;
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyProductMaster> query = cb.createQuery(CompanyProductMaster.class);
			List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
			// Find All
			Root<CompanyProductMaster> c = query.from(CompanyProductMaster.class);
			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productName")));

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, a3);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<CompanyProductMaster> ocpm2 = effectiveDate2.from(CompanyProductMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a4 = cb.equal(c.get("productId"), ocpm2.get("productId"));
			Predicate a5 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a4, a5, a6);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), companyId);
			Predicate n5 = cb.equal(c.get("productId"), productId);
			query.where(n1, n2, n3, n4, n5).orderBy(orderList);
			// Get Result
			TypedQuery<CompanyProductMaster> result = em.createQuery(query);
			list = result.getResultList();
			product = list.size() > 0 ? list.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return product;
	}

	@Override
	public PremiaResponse hitByQuoteNo(PremiaListRequest req) {
		PremiaResponse response = new PremiaResponse();

		try {
			List<HomePositionMaster> data = this.homeRepo.findAllByOrderByEntryDateDesc();
			List<String> quoteNo = new ArrayList<>();
			List<String> premiaIds = new ArrayList<>();
			if (data.size() > 0 && data != null && !data.isEmpty() && req.getQuoteNo() == null) {
				quoteNo = data.stream().map(HomePositionMaster::getQuoteNo).collect(Collectors.toList());
				premiaIds.add("1");
				premiaIds.add("2");
				premiaIds.add("3");
				premiaIds.add("4");
				premiaIds.add("5");
				premiaIds.add("6");
				premiaIds.add("7");
				premiaIds.add("8");
				premiaIds.add("9");
				premiaIds.add("10");
				premiaIds.add("11");
				premiaIds.add("12");
			} else if (req.getQuoteNo() != null) {
				quoteNo = req.getQuoteNo();
				premiaIds = req.getPremiaIds();
			}

			PhoenixPushIntegrationThread hit = new PhoenixPushIntegrationThread(this, quoteNo, premiaIds);
			Thread push = new Thread(hit);
			push.start();
			response.setResponse("Processing.....");
			return response;
		} catch (Exception var8) {
			var8.printStackTrace();
			return null;
		}
	}

	@Override
	public PremiaResponse pushPremiaMarineIntegeration(PremiaRequest req) {
		PremiaResponse response = new PremiaResponse();
		List<String> failureOracleList = new ArrayList<>();
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedDateTime = currentDateTime.format(formatter);
		System.out.println("*********Oracle Block started " + req.getQuoteNo() + " " + formattedDateTime);
		try {
		List<String> premaiId = new ArrayList<>();
		premaiId.add("1");premaiId.add("2");premaiId.add("3");premaiId.add("4");premaiId.add("5");premaiId.add("6");premaiId.add("7");premaiId.add("8");premaiId.add("9");premaiId.add("10");
		
		List<PositionMaster>list=pmRepo.findByQuoteno(Long.parseLong(req.getQuoteNo()));
		if(!CollectionUtils.isEmpty(list)) {
			PositionMaster pm=list.get(0);
			for (String id : premaiId) {
				IntegrationSaveRes oraclpush = ewayMarinePremiaPush(req.getPolicyNo(),pm,id,req.getCompanyId());
				System.out.println("Response after oracle push " + oraclpush);
				if ("Connection refused".equalsIgnoreCase(oraclpush.getResponse())) {

					pm.setCoreintgstatus("Data Failed saved in  Oracle DB Connection refused");
					pm.setIntegrationError(oraclpush.getErrorMessage());
					pmRepo.save(pm);
					System.out.println("-----Connection refused to save in oracle");
					System.out.println("Data Failed saved in  Oracle DB Connection refused");
					response.setResponse("Data Failed saved in  Oracle DB Connection refused");
					failureOracleList.add("Data Failed saved in  Oracle DB Connection refused");
					break;
				} else if ("Success".equalsIgnoreCase(oraclpush.getResponse())) {
					response.setResponse("Success");
					pm.setCoreintgstatus("Data saved in  Oracle DB");
					pm.setIntegrationError("");
					pmRepo.save(pm);
					System.out.println("--------Saved in Oracle");

				} else {
					response.setResponse("Data Failed saved in  Oracle DB");
					pm.setCoreintgstatus("Data Failed saved in  Oracle DB");
					pm.setIntegrationError(oraclpush.getErrorMessage());
					pmRepo.save(pm);
					failureOracleList.add("Data Failed saved in  Oracle DB");
				}
			}
			if (failureOracleList.isEmpty()) {
				System.out.println("*********Premia Integration Wecore Api Call:");
				System.out.println("Policy No :" + req.getPolicyNo() + " Company Id :" + req.getCompanyId());
				IntegrationSaveRes list1 = frameReqService.premiaExternalCall(req.getPolicyNo(), req.getCompanyId());
				System.out.println("List " + json.toJson(list1));
				if (list1.getResponse().equalsIgnoreCase("Failed")) {
	
					pm.setCoreintgstatus(
							StringUtils.isBlank(list1.getPWsResponseType()) ? "Data not Integrated"
									: list1.getPWsResponseType());
					pm.setIntegrationError(StringUtils.isBlank(list1.getPWsError()) ? list1.getErrorMessage()
							: list1.getPWsError());
					response.setResponse("Failed");
				} else {
					pm.setCoreintgstatus(StringUtils.isBlank(list1.getPWsResponseType()) ? "Data Integrated"
							: list1.getPWsResponseType());
					pm.setIntegrationError(StringUtils.isBlank(list1.getPWsError()) ? "" : list1.getPWsError());
					response.setResponse(StringUtils.isBlank(list1.getPWsResponseType()) ? "Data Integrated"
							: list1.getPWsResponseType());
				}
				response.setPremiaPolicyNo(list1.getPremiaPolicyNo());
				pmRepo.save(pm);
				System.out.println("response " + json.toJson(response));
				System.out.println("________________________________________________ ");
				}
			}
		}catch (Exception e) {
			e.printStackTrace();;
		}
		System.out.println("*********Oracle Block Ended " + req.getQuoteNo()+ " " + formattedDateTime);
		return response;
	}

	
	private IntegrationSaveRes ewayMarinePremiaPush(String policyNo, PositionMaster pm, String premaiId,String companyId) {

		IntegrationSaveRes res1 = new IntegrationSaveRes();
		boolean check = false;
		try {
			System.out.println("*********EXTERNAL API CALL STARTS*********");
			System.out.println("*********PolicyNo " + policyNo);

			if ("1".equals(premaiId)) {
				System.out.println("*********1.YiPolicyDetail: ");
				Object list = frameReqService.pushYiPolicyDetail(policyNo,companyId);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;

				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}

			} else if ("2".equals(premaiId)) {
				System.out.println("*********2.YiSectionDetail:");
				Object list = frameReqService.pushYiSectionDetail(policyNo,companyId);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;
				res1.getResponse();
				res1.getErrorMessage();
				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}
			} else if ("3".equals(premaiId)) {
				System.out.println("*********3.PgitPolRiskAddlInfo:");
				Object list = frameReqService.pushPgitPolRiskAddlInfo(policyNo,companyId);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;
				res1.getResponse();
				res1.getErrorMessage();
				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}
			} else if ("4".equals(premaiId)) {
				System.out.println("*********4.YiCoverDetail: ");
				Object list = frameReqService.pushYiCoverDetail(policyNo,companyId);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;
				res1.getResponse();
				res1.getErrorMessage();
				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}
			}  else if ("5".equals(premaiId)) {
				System.out.println("*********5.YiChargeDetail: ");
				Object list = frameReqService.pushYiChargeDetail(policyNo,companyId);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;
				res1.getResponse();
				res1.getErrorMessage();
				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}
			} else if ("6".equals(premaiId)) {
				System.out.println("*********6.YiVatDetail:");
				Object list = frameReqService.pushYiVatDetail(policyNo,companyId);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;
				res1.getResponse();
				res1.getErrorMessage();
				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}
			} else if ("7".equals(premaiId)) {
				System.out.println("*********7.YiPremCal:");
				Object list = frameReqService.pushYiPremCal(policyNo,companyId);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;
				res1.getResponse();
				res1.getErrorMessage();
				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}
			}else if ("8".equals(premaiId)) {
				System.out.println("*********8.YiConditions:");
				Object list = frameReqService.pushYiConditionCal(policyNo);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;
				res1.getResponse();
				res1.getErrorMessage();
				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}
			} else if ("9".equals(premaiId)) {
				System.out.println("*********9.YiDeductable:");
				Object list = frameReqService.pushYiDeductableCal(policyNo);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;
				res1.getResponse();
				res1.getErrorMessage();
				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}
			} 
			else if ("10".equals(premaiId)) {
				System.out.println("*********10.YiPolicyApproval:");
				Object list = frameReqService.pushYiPolicyApproval(policyNo,companyId);
				System.out.println("List " + json.toJson(list));
				System.out.println("_____________________________________________ ");
				res1 = (IntegrationSaveRes) list;
				res1.getResponse();
				res1.getErrorMessage();
				if ("Success".equalsIgnoreCase(res1.getResponse())) {
					check = true;
				} else {
					check = false;
				}
			} 
			if (check = true) {
				pm.setCoreintgstatus("Data Inserted saved in  Oracle DB");
				pm.setIntegrationError("");
				pmRepo.save(pm);
			} else {
				pm.setCoreintgstatus("Data Failed saved in  Oracle DB");
				pm.setIntegrationError(res1.getErrorMessage());
				pmRepo.save(pm);
			}
			System.out.println("Response from oracle " + json.toJson(res1));

			return res1;
		} catch (Exception e) {
			e.printStackTrace();
			return res1;
		}
	
	}
	
	private String updateNonMotorIntegrationStatus(String quoteNo, HomePositionMaster home, String reqRefNo) {
		boolean status = false, status1 = false, status2 = false,  status4 = false,
				status6 = false, status7 = false, status8 = false, status9 = false, status10 = false;
		String policyNo = home.getPolicyNo();
		String result = "";
		try {
			List<String> errorList = new ArrayList<>();
			List<YiPolicyDetail> list = yiPolicyReo.findByQuotationPolicyNo(policyNo);
			if (list.size() > 0 && list != null) {
				status = true;
				System.out.println("Saved in My sql YiPolicyDetail");
			} else {
				status = false;
				errorList.add("YiPolicyDetail");
				System.out.println("Not Saved in My sql YiPolicyDetail");
			}
 
			List<YiSectionDetail> list1 = yisecRepo.findByQuotationPolicyNo(policyNo);
			if (list1.size() > 0 && list1 != null) {
				status1 = true;
				System.out.println("Saved in My sql YiSectionDetail");
			} else {
				status1 = false;
				errorList.add("YiSectionDetail");
				System.out.println("Not Saved in My sql YiSectionDetail");
			}
 
			List<PgithPolRiskAddlInfo> list2 = pgitPolRiskRepo.findByQuotationPolicyNo(policyNo);
			if (list2.size() > 0 && list2 != null) {
				status2 = true;
				System.out.println("Saved in My sql PgithPolRiskAddlInfo");
			} else {
				status2 = false;
				errorList.add("PgithPolRiskAddlInfo");
				System.out.println("Not Saved in My sql PgithPolRiskAddlInfo");
			}
 
		
			List<YiCoverDetail> list4 = yiCoverDetailRepo.findByQuotationPolicyNo(policyNo);
			if (list4.size() > 0 && list4 != null) {
				status4 = true;
				System.out.println("Saved in My sql YiCoverDetail");
			} else {
				status4 = false;
				errorList.add("YiCoverDetail");
				System.out.println("Not Saved in My sql YiCoverDetail");
			}
			if (!home.getCompanyId().equals("100046") && !home.getCompanyId().equals("100047")) {
				List<YiChargeDetail> list6 = yiChargeDetailRepo.findByQuotationPolicyNo(policyNo);
				if (list6.size() > 0 && list6 != null) {
					status6 = true;
					System.out.println("Saved in My sql YiChargeDetail");
				} else {
					status6 = false;
					errorList.add("YiChargeDetail");
					System.out.println("Not Saved in My sql YiChargeDetail");
				}

				List<YiVatDetail> list7 = yivatRepo.findByQuotationPolicyNo(policyNo);
				if (list7.size() > 0 && list7 != null) {
					status7 = true;
					System.out.println("Saved in My sql YiVatDetail");
				} else {
					status7 = false;
					errorList.add("YiVatDetail");
					System.out.println("Not Saved in My sql YiVatDetail");
				}
			}else {
				status7 = true;
			}
			List<YiPremCal> list8 = yipremRepo.findByQuotationPolicyNo(policyNo);
			if (list8.size() > 0 && list8 != null) {
				status8 = true;
				System.out.println("Saved in My sql YiPremCal");
			} else {
				status8 = false;
				errorList.add("YiPremCal");
				System.out.println("Not Saved in My sql YiPremCal");
			}
 
			List<YiPolicyApproval> list9 = yipolicyRepo.findByQuotationPolicyNo(policyNo);
			if (list9.size() > 0 && list9 != null) {
				status9 = true;
				System.out.println("Saved in My sql YiPolicyApproval");
			} else {
				status9 = false;
				errorList.add("YiPolicyApproval");
				System.out.println("Not Saved in My sql YiPolicyApproval");
			}
			List<CreditLimitDetail> list10 = creditRepo.findByRequestreferenceno(reqRefNo);
			if (list10.size() > 0 && list10 != null) {
				status10 = true;
				System.out.println("Saved in My sql CreditLimitDetail");
			} else {
				status10 = false;
				errorList.add("CreditLimitDetail");
				System.out.println("Not Saved in My sql CreditLimitDetail");
			}
			if (status && status1 && status2  && status4 &&  status6 && status7 && status8
					&& status9 && status10) {
				home.setCoreIntgStatus("Data Successfully saved in  My Sql");
//			home.setIntegrationStatus("S");
				home.setIntegrationError("");
				result = "S";
			} else {
				home.setCoreIntgStatus("Data Failed to saved in  My Sql");
//			home.setIntegrationStatus("F");
				home.setIntegrationError("Data Failed to saved in  My Sql " + errorList);
				result = "F";
			}
			homeRepo.save(home);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			home.setCoreIntgStatus("F");
		}
		homeRepo.saveAndFlush(home);
		return result;
	}
	
	public void reinsurancepremiaIntegration(String quoteNo) {
		try {
			HomePositionMaster home = homeRepo.findByQuoteNo(quoteNo);
			if(home != null) {
				
			}
			List<ReinsuranceCoverDetails> reinsuranceCoverDetailsList = reinsuranceCoverDetailsRepos
					.findByQuoteno(quoteNo);
			if (reinsuranceCoverDetailsList != null && !reinsuranceCoverDetailsList.isEmpty()) {

			}
			List<ReInsuranceRiskDetails> reInsuranceRiskDetailsList = reInsuranceRiskDetailsRepos
					.findByQuoteno(quoteNo);
			if (reInsuranceRiskDetailsList != null && !reInsuranceRiskDetailsList.isEmpty()) {

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void savePushIntegrationStatus(String policyNo) {
		try {
			Optional<PushIntegrationStatus> byQuotationPolicyNo = pushIntegrationStatusRepo
					.findByquotationPolicyNo(policyNo);
			byQuotationPolicyNo.ifPresent(pushIntegrationStatusRepo::delete);

			PushIntegrationStatus pushIntgeration = new PushIntegrationStatus();
			pushIntgeration.setQuotationPolicyNo(policyNo);
			pushIntgeration.setStatus("pending");
			pushIntgeration.setErrorMessage("");
			pushIntegrationStatusRepo.saveAndFlush(pushIntgeration);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void draftOrcalInsert(PremiaResponse response, String policyNo, String reqRefNo, HomePositionMaster home,
			List<PremiaConfigMaster> configMasterList, List<String> successOracleList, List<String> failureOracleList) {
		for (PremiaConfigMaster configMas : configMasterList) {
			IntegrationSaveRes oraclpush = ewayMotorPremiaPush(policyNo, reqRefNo, configMas, home);
			System.out.println("Response after oracle push " + oraclpush);
			if ("Connection refused".equalsIgnoreCase(oraclpush.getResponse())) {

				home.setCoreIntgStatus("Data Failed saved in  Oracle DB Connection refused");
//					home.setIntegrationStatus("F");
				home.setIntegrationError(oraclpush.getErrorMessage());
				homeRepo.save(home);
				System.out.println("-----Connection refused to save in oracle");
				System.out.println("Data Failed saved in  Oracle DB Connection refused");
				response.setResponse("Data Failed saved in  Oracle DB Connection refused");
				failureOracleList.add("Data Failed saved in  Oracle DB Connection refused");
				break;
			} else if ("Success".equalsIgnoreCase(oraclpush.getResponse())) {
				response.setResponse("Success");
				successOracleList.add(configMas.getPremiaTableName());
				home.setCoreIntgStatus("Data saved in  Oracle DB");
//					home.setIntegrationStatus("S");
				home.setIntegrationError("");
				homeRepo.save(home);
				System.out.println("--------Saved in Oracle");

			} else {
				response.setResponse("Data Failed saved in  Oracle DB");
				if (configMas.getPremiaId() == 1 || configMas.getPremiaId() == 2 || configMas.getPremiaId() == 3
						|| configMas.getPremiaId() == 5 || configMas.getPremiaId() == 7 || configMas.getPremiaId() == 8
						|| configMas.getPremiaId() == 9 || configMas.getPremiaId() == 10
						|| configMas.getPremiaId() == 11) {
					failureOracleList.add(configMas.getPremiaTableName());
					home.setCoreIntgStatus("Data Failed saved in  Oracle DB");
//						home.setIntegrationStatus("F");
					home.setIntegrationError(oraclpush.getErrorMessage());
					homeRepo.save(home);
					System.out.println("-------Not Saved in Oracle " + failureOracleList);

				}
			}
		}
	}
	
	private void draftPremiaCall(PremiaResponse response, String formattedDateTime, String quoteNo, String policyNo,
			String companyId, HomePositionMaster home) {
		if ("100002".equalsIgnoreCase(companyId) || "100019".equalsIgnoreCase(companyId)
				|| "100046".equalsIgnoreCase(companyId) || "100047".equalsIgnoreCase(companyId)
				|| "100048".equalsIgnoreCase(companyId) || "100049".equalsIgnoreCase(companyId)
				|| "100050".equalsIgnoreCase(companyId)) {
			System.out.println("*********Premia Integration Wecore Api Call:");
			System.out.println("Policy No :" + policyNo + " Company Id :" + companyId);
			IntegrationSaveRes list = frameReqService.premiaExternalCall(policyNo, companyId);
			System.out.println("List " + json.toJson(list));
//				IntegrationSaveRes status = frameReqService.updatePremiaExternalCallStatus(policyNo,companyId);
			if (list.getResponse().equalsIgnoreCase("Failed")) {

				home.setCoreIntgStatus(StringUtils.isBlank(list.getPWsResponseType()) ? "Data not Integrated"
						: list.getPWsResponseType());
//					home.setIntegrationStatus("F");
				home.setIntegrationError(
						StringUtils.isBlank(list.getPWsError()) ? list.getErrorMessage() : list.getPWsError());
				homeRepo.save(home);
				response.setResponse("Failed");
			} else {
				home.setCoreIntgStatus(
						StringUtils.isBlank(list.getPWsResponseType()) ? "Data Integrated" : list.getPWsResponseType());
//					home.setIntegrationStatus("S");
				home.setIntegrationError(StringUtils.isBlank(list.getPWsError()) ? "" : list.getPWsError());
				homeRepo.save(home);
				response.setResponse(
						StringUtils.isBlank(list.getPWsResponseType()) ? "Data Integrated" : list.getPWsResponseType());
			}

			System.out.println("List " + json.toJson(list));
			System.out.println("_____________________________________________ ");
		}
		System.out.println("*********Procedure Block Ended " + quoteNo + " " + formattedDateTime);
	}
	
}