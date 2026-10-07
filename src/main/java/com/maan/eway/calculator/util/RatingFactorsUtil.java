package com.maan.eway.calculator.util;



import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.BrokerCommissionDetails;
import com.maan.eway.bean.CompanyProrataMaster;
import com.maan.eway.bean.ConstantTableDetails;
import com.maan.eway.bean.CurrencyMaster;
import com.maan.eway.bean.DropdownTableDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EwayFactorDetails;
import com.maan.eway.bean.EwayFactorResultDetail;
import com.maan.eway.bean.EwayVehicleMakemodelMasterDetail;
import com.maan.eway.bean.FactorRateMaster;
import com.maan.eway.bean.FactorTypeDetails;
import com.maan.eway.bean.LifePolicytermsMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.MotorDriverDetails;
import com.maan.eway.bean.OneTimeTableDetails;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.ProductTaxSetup;
import com.maan.eway.bean.RatingFieldMaster;
import com.maan.eway.bean.SectionCoverMaster;
import com.maan.eway.bean.TaxExemptionSetup;
import com.maan.eway.bean.TinyurlMaster;
import com.maan.eway.bean.TinyurlRequestDetail;
import com.maan.eway.chartaccount.ChartAccountRequest;
import com.maan.eway.chartaccount.ChartAccountService;
import com.maan.eway.common.req.DashBoardGetReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.notification.bean.NotifTransactionDetails;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EwayFactorDetailsRepository;
import com.maan.eway.repository.EwayFactorResultDetailRepository;
import com.maan.eway.repository.EwayVehicleMakemodelMasterDetailRepository;
import com.maan.eway.repository.MotorDriverDetailsRepository;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.req.referal.ReferralRequest;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.RatingInfo;
import com.maan.eway.upgrade.criteria.CriteriaService;
import com.maan.eway.upgrade.criteria.SpecCriteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

@Component
@Scope("prototype")
public class RatingFactorsUtil {
	@Autowired
	protected CriteriaService crservice;
	
	@Autowired
	private EServiceMotorDetailsRepository eserviceMotorRepo;
	
	protected SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy")  ;
	
	
	@Cacheable(cacheNames = {"LoadConstant"},keyGenerator  = "loadConstantKeyGen",value = "LoadConstant" )
	public List<ReferralRequest> LoadConstant(CalcEngine engine) {
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:{Y,R};"+todayInString+"~effectiveDateStart&effectiveDateEnd;branchCode:{99999,"+engine.getBranchCode()+"};";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(ConstantTableDetails.class, search, "itemId"); 
			result=crservice.getResult(criteria, 0, 50);
			List<ReferralRequest> refreqs=null;
			if(result!=null && result.size()>0) {
				
				refreqs=new ArrayList<ReferralRequest>();
				
				
				
				for(Tuple r :result) {
					String itemId=r.get("itemId")==null?"":r.get("itemId").toString();
					
					ReferralRequest req=ReferralRequest.builder().apiLink(r.get("apiUrl")==null?"":r.get("apiUrl").toString())
							.primaryTable(r.get("keyTable")==null?"":r.get("keyTable").toString())
							.primaryKey(r.get("keyName")==null?"":r.get("keyName").toString())
							.build();
					
					
					List<Tuple> loadDropdown = loadDropdown(engine, itemId);
					List<Map<String,String>> mps=null;
					if(loadDropdown!=null && loadDropdown.size()>0) {
						mps=new ArrayList<Map<String,String>>();	
						for(Tuple l :loadDropdown) {
							Map<String,String> mp=new HashMap<String,String>();
							mp.put("JsonKey", l.get("requestJsonKey")==null?"":l.get("requestJsonKey").toString());
							mp.put("JsonColum", l.get("requestColumn")==null?"":l.get("requestColumn").toString());
							mp.put("JsonTable", l.get("requestTable")==null?"":l.get("requestTable").toString());
							mps.add(mp);
						}
						req.setMp(mps);
					}
					refreqs.add(req);
				}
				
			}
			
			return refreqs;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	public List<Tuple> loadDropdown(CalcEngine engine,String itemId){
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:{Y,R};"+todayInString+"~effectiveDateStart&effectiveDateEnd;branchCode:{99999,"+engine.getBranchCode()+"};itemId:"+itemId+";";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(DropdownTableDetails.class, search, "requestId"); 
			result=crservice.getResult(criteria, 0, 50);

			return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	protected List<Tuple> loopfactorrates(CalcEngine engine,Map<String, List<String>> vloop, String coverId, String subCoverId) {

		for (Entry<String, List<String>> entry : vloop.entrySet()) {
			List<String> condtions = entry.getValue();
			String condtion=StringUtils.join(condtions,';');

			List<Tuple> loadfactorOnlyquery = loadfactorOnlyquery(engine,condtion, coverId,subCoverId);
			
			return loadfactorOnlyquery;

		} 
		return null;
	}
	
	private Map<Integer,String>  commonQueries(CalcEngine engine,String condtion,String coverId, String subCoverId) {
		String todayInString = DD_MM_YYYY.format(new Date());
	/*	String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+
				engine.getSectionId()+";status:{Y,R};coverId:"+coverId+";subCoverId:"+subCoverId+";"
				+todayInString+"~effectiveDateStart&effectiveDateEnd;agencyCode:"+engine.getAgencyCode()
				+";branchCode:"+engine.getBranchCode()+";"+condtion;*/


		String search2="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+
				engine.getSectionId()+";status:{Y,R};coverId:"+coverId+";subCoverId:"+subCoverId+";"
				+todayInString+"~effectiveDateStart&effectiveDateEnd;agencyCode:"+engine.getAgencyCode()
				+";branchCode:99999;"+condtion;
/*
		String search3="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+
				engine.getSectionId()+";status:{Y,R};coverId:"+coverId+";subCoverId:"+subCoverId+";"
				+todayInString+"~effectiveDateStart&effectiveDateEnd;agencyCode:99999"
				+";branchCode:"+engine.getBranchCode()+";"+condtion;
*/
		String search4="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+
				engine.getSectionId()+";status:{Y,R};coverId:"+coverId+";subCoverId:"+subCoverId+";"
				+todayInString+"~effectiveDateStart&effectiveDateEnd;agencyCode:99999"
				+";branchCode:99999;"+condtion;

		Map<Integer,String> hsmap=new TreeMap<Integer,String>();
	//	hsmap.put(1, search);
		hsmap.put(1, search2);
	//	hsmap.put(3, search3);
		hsmap.put(2, search4);
		return hsmap;
	}
	@Cacheable(cacheNames= {"loadfactorOnlyquery"},keyGenerator  = "loadfactorOnlyqueryKeyGen",value = "loadfactorOnlyquery")
	public List<Tuple> loadfactorOnlyquery(CalcEngine engine,String condtion,String coverId, String subCoverId) {
		try{
		///	Map<Integer, String> hsmap = commonQueries(engine, condtion, coverId, subCoverId);
			//1.Priorty both s pecifi agencycode & branchcode
			//2.priorty both specifi agencycode
			//3.priorty both specifi branchcode
			//4.priorty both common
			SpecCriteria criteria = null;

			/*for(int i=1;i<=hsmap.size();i++) {
				String dataquery = hsmap.get(i);


				criteria = crservice.createCriteria(FactorRateMaster.class, dataquery, "factorTypeId"); 

				List<Long> count = crservice.getCount(criteria, 0, 50);
				if(!count.isEmpty()) { 
					Long countrec = count.get(0);				
					if(countrec>0) 
						break;
				}

			}*/
			
			criteria = crservice.createCriteria(FactorRateMaster.class, condtion, "factorTypeId"); 

			if(criteria!=null) {
				List<Tuple> result=null;
				result=crservice.getResult(criteria, 0, 50);
				if( result.size()>0) {					
					return result;
					
				}else {
					System.out.println("Cover Id"+coverId+" EXCEPTION ::: NODATA");
					return null;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Cacheable(cacheNames= {"countfactorOnlyquery"},keyGenerator  = "countfactorOnlyqueryKeyGen",value = "countfactorOnlyquery")
	public Long countfactorOnlyquery(CalcEngine engine,String condtion,String coverId, String subCoverId) {
		String dataquery = null;
		try{
			Map<Integer, String> hsmap = commonQueries(engine, condtion, coverId, subCoverId);
			//1.Priorty both s pecifi agencycode & branchcode
			//2.priorty both specifi agencycode
			//3.priorty both specifi branchcode
			//4.priorty both common
			Long countrec=0L;
			SpecCriteria criteria = null;
			List<Long> count=null;
			for(int i=1;i<=hsmap.size();i++) {
				dataquery = hsmap.get(i);


				criteria = crservice.createCriteria(FactorRateMaster.class, dataquery, "factorTypeId"); 

				  count = crservice.getCount(criteria, 0, 50);
				if(!count.isEmpty()) { 
					countrec = count.get(0);				
					if(countrec>0) 
						return countrec;
				}

			}

			 
			return countrec;
		}catch (Exception e) {
			System.out.println("Factor Id"+dataquery);
			e.printStackTrace();
			
		}
		return 0L;
	}
	
	
	public List<RatingInfo> LoadRatingField(CalcEngine engine,List<RatingInfo> infos){
		
		List<String> infoStr = infos.stream().map(RatingInfo::getRatingFieldId)/*.sorted(new 
				Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				// TODO Auto-generated method stub
				return (Integer.parseInt(o1)>Integer.parseInt(o2))?0:1;
			} 
			})*/.toList();
				//collect(Collectors.toList());
		List<Tuple> result=this.getCachedRatingFields(engine,infoStr);
		if(result!=null && result.size()>0) {
			for(int i=0;i<result.size();i++) {
				Tuple t = result.get(i);
				String ratingId=t.get("ratingId")==null?"":t.get("ratingId").toString();
				Optional<RatingInfo> findFirst = infos.stream().filter(jk->jk.getRatingFieldId().equals(ratingId)).findFirst();
				RatingInfo info = findFirst.get();
				info.setRatingFieldId(ratingId);
				info.setRatingField(t.get("ratingField")==null?"":t.get("ratingField").toString());
				info.setInputTableName(t.get("inputTable")==null?"":t.get("inputTable").toString());
				info.setInputColumName(t.get("inputColumnName")==null?"":t.get("inputColumnName").toString());
			}
		}
		
		/*for (RatingInfo info : infos) {
			try {
				
				List<Tuple> result=this.getCachedRatingFields(engine,info);
							
			}catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		
		return infos;
	}
	@Cacheable(cacheNames = {"getCachedRatingFields"},keyGenerator  = "getCachedRatingFieldsKeyGen",value = "getCachedRatingFields" )
	public List<Tuple> getCachedRatingFields(CalcEngine engine,List<String> info){
			//RatingInfo info){
		try {
			String ratingFieldIds="{"+StringUtils.join(info,",")+"}";
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="productId:"+engine.getProductId()+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;ratingId:"+ratingFieldIds+";";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(RatingFieldMaster.class, search, "ratingId"); 
			result=crservice.getResult(criteria, 0, 50);
			return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<RatingInfo> LoadRatingType(CalcEngine engine,String factorTypeId){
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;factorTypeId:"+factorTypeId+";";
			List<Tuple>  result=this.getRatingType(engine, factorTypeId, search);
			if(result!=null && result.size()>0) {
				
				RatingTypeUtil rate=new RatingTypeUtil();
				List<RatingInfo> collect = result.stream().map(rate).filter(d->d!=null).collect(Collectors.toList());
				collect.sort(new Comparator<RatingInfo>() {

					@Override
					public int compare(RatingInfo o1, RatingInfo o2) {
						// TODO Auto-generated method stub
						return  o1.getRatingFieldId().compareTo(o2.getRatingFieldId());
					}
					
				});
				this.LoadRatingField(engine, collect);
				 return collect;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Cacheable(cacheNames = {"RatingType"},keyGenerator  = "ratingTypeKeyGen",value = "RatingType" )
	public List<Tuple> getRatingType(CalcEngine engine,String factorTypeId,String search) {
		System.out.println(">>> getRatingType CACHE MISS - DB HIT");
		try {
			
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(FactorTypeDetails.class, search, "ratingFieldId"); 
			result=crservice.getResult(criteria, 0, 50);
			return result;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	
	public  List<Tuple> collectProductType(CalcEngine engine) {
		try{
			String todayInString = DD_MM_YYYY.format(new Date());

			if (engine.getProductId().equals("0")) {
				String prodSearch = "companyId:" + engine.getInsuranceId() + ";status:Y;" + todayInString
						+ "~effectiveDateStart&effectiveDateEnd;";
				SpecCriteria criteria = crservice.createCriteria(ProductSectionMaster.class, prodSearch, "companyId");
				List<Tuple> product = crservice.getResult(criteria, 0, 1);
				return product;
			}
			
			if(StringUtils.isBlank(engine.getSectionId())|| "99999".equals(engine.getSectionId())) {
				String prodSearch="companyId:"+engine.getInsuranceId()+";productId:"+engine.getProductId()+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;";				
				SpecCriteria	criteria = crservice.createCriteria(ProductSectionMaster.class, prodSearch, "companyId");			  
				List<Tuple> product = crservice.getResult(criteria, 0, 1);
				return product;					
			}
			else {
				String prodSearch="companyId:"+engine.getInsuranceId()+";productId:"+engine.getProductId()+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;sectionId:"+engine.getSectionId()+";";				
				SpecCriteria	criteria = crservice.createCriteria(ProductSectionMaster.class, prodSearch, "companyId");			  
				List<Tuple> product = crservice.getResult(criteria, 0, 1);
				return product;
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	
	
	@Cacheable(cacheNames = {"loadTax"},keyGenerator  = "loadTaxKeyGen",value = "loadTax" )
	public List<Tuple> LoadTax(CalcEngine engine,List<String> taxFor) {
		System.out.println(">>> LoadTax CACHE MISS - DB HIT for: " 
		        + engine.getInsuranceId() + "_" + engine.getProductId());
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			//String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:Y;branchCode:{99999,"+engine.getBranchCode()+"};"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
			String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:Y;branchCode:"+engine.getBranchCode()+";"+todayInString+"~effectiveDateStart&effectiveDateEnd;taxFor:{"+StringUtils.join(taxFor,',')+"};";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(ProductTaxSetup.class, search, "taxId"); 
			
			result=crservice.getResult(criteria, 0, 50);
			if(result.isEmpty()) {
				search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:Y;branchCode:99999;"+todayInString+"~effectiveDateStart&effectiveDateEnd;taxFor:{"+StringUtils.join(taxFor,',')+"};";
				criteria = crservice.createCriteria(ProductTaxSetup.class, search, "taxId"); 
				result=crservice.getResult(criteria, 0, 50);
				return result.size()>0?result:new ArrayList<Tuple>();
			}
			return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Cacheable(cacheNames = {"loadProRata"},keyGenerator  = "loadProRataKeyGen",value = "loadProRata" )
	public List<Tuple> loadProRataData(CalcEngine engine,String periodOfInsurance,String policyTypeId){
		System.out.println(">>> loadProRataData CACHE MISS - DB HIT");
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="insuranceid:"+engine.getInsuranceId()+";productid:"+engine.getProductId()+";status:Y;"+periodOfInsurance+"~startfrom&endto;"+todayInString+"~effectiveDateStart&effectiveDateEnd;policyTypeId:"+policyTypeId+";";
			SpecCriteria criteria = crservice.createCriteria(CompanyProrataMaster.class, search, "sno");
			List<Tuple> prorata = crservice.getResult(criteria, 0, 50);
			if(prorata!=null && prorata.size()>0)
					return prorata;
			else {
				 search="insuranceid:"+engine.getInsuranceId()+";productid:"+engine.getProductId()+";status:Y;"+periodOfInsurance+"~startfrom&endto;"+todayInString+"~effectiveDateStart&effectiveDateEnd;policyTypeId:99999;";
				 criteria = crservice.createCriteria(CompanyProrataMaster.class, search, "sno");
				 prorata = crservice.getResult(criteria, 0, 50);				 
				 return prorata.size()>0?prorata:null;
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	public List<Tuple> loadNotificationPending() {
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="notifPushedStatus:P;"+todayInString+"~notifcationPushDate&notifcationEndDate";
			SpecCriteria criteria = crservice.createCriteria(NotifTransactionDetails.class, search, "notifPriority");
			List<Tuple> prorata = crservice.getResult(criteria, 0, 50);
			return prorata;
		}catch (Exception e) {
			e.printStackTrace();	
		}
		return null;
		
	}
	
	@Cacheable(cacheNames = {"ProductToRawtable"},keyGenerator  = "getProductIdBasedRawTable",value = "ProductToRawtable" )
	public  String getProductIdBasedRawTable(CalcEngine engine) {
		try{
			
			String prodSearch="itemType:ESERVICE_TABLE;status:Y;productId:"+engine.getProductId()+";itemValue:"+engine.getSectionId()+";companyId:"+engine.getInsuranceId()+";";
			SpecCriteria	criteria = crservice.createCriteria(OneTimeTableDetails.class, prodSearch, "parentId");
			List<Tuple> product =null;
			List<Long> count = crservice.getCount(criteria, 0, 1);
			if(!count.isEmpty()) { 
				Long countrec = count.get(0);
				if(countrec<=0) {				
					 prodSearch="itemType:ESERVICE_TABLE;status:Y;productId:"+engine.getProductId()+";companyId:"+engine.getInsuranceId()+";";
					 criteria = crservice.createCriteria(OneTimeTableDetails.class, prodSearch, "parentId");					
				}
				product= crservice.getResult(criteria, 0, 1);
			}	
			
			String rawTable=product.get(0).get("itemCode")==null?"0":product.get(0).get("itemCode").toString();
			return "com.maan.eway.bean."+rawTable;

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	@Cacheable(cacheNames= {"EndtMasterData"},keyGenerator  = "getEndtMasterData",value = "EndtMasterData")
	public EndtTypeMaster getEndtMasterData(String insuranceId, String productId, String endtTypeId) {
		 try {

				String todayInString = DD_MM_YYYY.format(new Date());
				String search="companyId:"+insuranceId+";productId:"+productId+";status=Y;endtTypeId:"+endtTypeId+";"+todayInString+"effectiveDateStart&effectiveDateEnd;";
				SpecCriteria criteria = crservice.createCriteria(EndtTypeMaster.class, search, "endtTypeId");
				List<Tuple> prorata = crservice.getResult(criteria, 0, 50);
				 if(prorata!=null && prorata.size()>0) {
					 Tuple t = prorata.get(0);
					 EndtTypeMaster e=EndtTypeMaster.builder()
							 			.amendId(t.get("amendId")==null?0:Integer.parseInt(t.get("amendId").toString()))
							 			.calcTypeId(t.get("calcTypeId")==null?"P":t.get("calcTypeId").toString())
							 			.companyId(t.get("companyId")==null?"":t.get("companyId").toString())
							 			.coreAppCode(t.get("coreAppCode")==null?"":t.get("coreAppCode").toString())
							 			.createdBy(t.get("createdBy")==null?"":t.get("createdBy").toString())
							 			.effectiveDateEnd(new Date())
							 			.effectiveDateStart(new Date())
							 			.endtDependantFields(t.get("endtDependantFields")==null?"":t.get("endtDependantFields").toString())
							 			.endtDependantIds(t.get("endtDependantIds")==null?"":t.get("endtDependantIds").toString())
							 			.endtFeePercent(t.get("endtFeePercent")==null?"":t.get("endtFeePercent").toString())
							 			.endtFeeYn(t.get("endtFeeYn")==null?"":t.get("endtFeeYn").toString())
							 			.endtType(t.get("endtType")==null?"":t.get("endtType").toString())
							 			.endtTypeCategory(t.get("endtTypeCategory")==null?"":t.get("endtTypeCategory").toString())
							 			.endtTypeDesc(t.get("endtTypeDesc")==null?"":t.get("endtTypeDesc").toString())
							 			.endtTypeId(t.get("endtTypeId")==null?0:Integer.parseInt(t.get("endtTypeId").toString()))
							 			.entryDate(new Date())
							 			.priority(t.get("priority")==null?0:Integer.parseInt(t.get("priority").toString()))
							 			.productId(t.get("productId")==null?0:Integer.parseInt(t.get("productId").toString()))
							 			.regulatoryCode(t.get("regulatoryCode")==null?"":t.get("regulatoryCode").toString())
							 			.remarks(t.get("remarks")==null?"":t.get("remarks").toString())
							 			.status(t.get("status")==null?"":t.get("status").toString())
							 			.updatedBy(t.get("updatedBy")==null?"":t.get("updatedBy").toString())
							 			.endtTypeCategoryId(t.get("endtTypeCategoryId")==null?0:Integer.parseInt(t.get("endtTypeCategoryId").toString()))
							 			.isCoverendt(t.get("isCoverendt")==null?"N":t.get("isCoverendt").toString())
							 			.updatedDate(null).build();
					 return e;
				 }
			
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
		return null;
	}
	@Cacheable(cacheNames= {"currencyDecimalFormat"},keyGenerator  = "currencyDecimalFormatKeyGen",value = "currencyDecimalFormat")
	public String currencyDecimalFormat(String insuranceId, String currencyId) {

		String decimalFormat = "0" ;
		try {
			
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+insuranceId+";currencyId:"+currencyId+";status=Y;"+todayInString+"effectiveDateStart&effectiveDateEnd;";
			SpecCriteria criteria = crservice.createCriteria(CurrencyMaster.class, search, "amendId");
			List<Tuple> currencies = crservice.getResult(criteria, 0, 50);
			if(currencies!=null && currencies.size()>0) {
				decimalFormat = currencies.get(0).get("decimalDigit")==null?"0":currencies.get(0).get("decimalDigit").toString();
			}		 		
			
		} catch (Exception e) {
			e.printStackTrace(); 			
		}
		return decimalFormat;
	
	}

	public  List<Tuple> loadTinyUrl(String companyid, Integer productid, String notifTemplatename) {
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			//String search="companyId:"+ companyid +";productId:"+productid+";status:{Y,R};"+todayInString+"~effectiveDateStart&effectiveDateEnd;branchCode:{99999};notifYn:Y;type:"+notifTemplatename.toUpperCase().trim()+";";
			String search="type:"+notifTemplatename.toUpperCase().trim()+";"+"companyId:"+ companyid +";productId:"+productid+";status:{Y,R};notifYn:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;branchCode:99999";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(TinyurlMaster.class, search, "sno"); 
			result=crservice.getResult(criteria, 0, 50);
			return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public  List<Tuple> loadTinyUrl(String companyid, Integer productid, String notifTemplatename,String notifYn) {
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			//String search="companyId:"+ companyid +";productId:"+productid+";status:{Y,R};"+todayInString+"~effectiveDateStart&effectiveDateEnd;branchCode:{99999};notifYn:Y;type:"+notifTemplatename.toUpperCase().trim()+";";
			String search="type:"+notifTemplatename.toUpperCase().trim()+";"+"companyId:"+ companyid +";productId:"+productid+";status:{Y,R};notifYn:"+notifYn+";"+todayInString+"~effectiveDateStart&effectiveDateEnd;branchCode:99999";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(TinyurlMaster.class, search, "sno"); 
			result=crservice.getResult(criteria, 0, 50);
			return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public List<Tuple> loadTinyUrlRequest(String companyid, Integer productid, String notifTemplatename,String itemId){
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+ companyid +";productId:"+productid+";status:{Y,R};"+todayInString+"~effectiveDateStart&effectiveDateEnd;branchCode:99999;tinyId:"+itemId+";";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(TinyurlRequestDetail.class, search, "itemId"); 
			result=crservice.getResult(criteria, 0, 50);

			return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<EndtTypeMaster> getEndtMasterDatas(String companyId, String productId) {
		try {

			List<EndtTypeMaster> result=new ArrayList<EndtTypeMaster>();
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+companyId+";productId:"+productId+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
			SpecCriteria criteria = crservice.createCriteria(EndtTypeMaster.class, search, "endtTypeId");
			List<Tuple> prorata = crservice.getResult(criteria, 0, 50);
			 if(prorata!=null && prorata.size()>0) {
				 for(int i=0;i<prorata.size();i++) {
				 Tuple t = prorata.get(i);
				 EndtTypeMaster e=EndtTypeMaster.builder()
						 			.amendId(t.get("amendId")==null?0:Integer.parseInt(t.get("amendId").toString()))
						 			.calcTypeId(t.get("calcTypeId")==null?"P":t.get("calcTypeId").toString())
						 			.companyId(t.get("companyId")==null?"":t.get("companyId").toString())
						 			.coreAppCode(t.get("coreAppCode")==null?"":t.get("coreAppCode").toString())
						 			.createdBy(t.get("createdBy")==null?"":t.get("createdBy").toString())
						 			.effectiveDateEnd(new Date())
						 			.effectiveDateStart(new Date())
						 			.endtDependantFields(t.get("endtDependantFields")==null?"":t.get("endtDependantFields").toString())
						 			.endtDependantIds(t.get("endtDependantIds")==null?"":t.get("endtDependantIds").toString())
						 			.endtFeePercent(t.get("endtFeePercent")==null?"":t.get("endtFeePercent").toString())
						 			.endtFeeYn(t.get("endtFeeYn")==null?"":t.get("endtFeeYn").toString())
						 			.endtType(t.get("endtType")==null?"":t.get("endtType").toString())
						 			.endtTypeCategory(t.get("endtTypeCategory")==null?"":t.get("endtTypeCategory").toString())
						 			.endtTypeDesc(t.get("endtTypeDesc")==null?"":t.get("endtTypeDesc").toString())
						 			.endtTypeId(t.get("endtTypeId")==null?0:Integer.parseInt(t.get("endtTypeId").toString()))
						 			.entryDate(new Date())
						 			.priority(t.get("priority")==null?0:Integer.parseInt(t.get("priority").toString()))
						 			.productId(t.get("productId")==null?0:Integer.parseInt(t.get("productId").toString()))
						 			.regulatoryCode(t.get("regulatoryCode")==null?"":t.get("regulatoryCode").toString())
						 			.remarks(t.get("remarks")==null?"":t.get("remarks").toString())
						 			.status(t.get("status")==null?"":t.get("status").toString())
						 			.updatedBy(t.get("updatedBy")==null?"":t.get("updatedBy").toString())
						 			.endtTypeCategoryId(t.get("endtTypeCategoryId")==null?0:Integer.parseInt(t.get("endtTypeCategoryId").toString()))
						 			.sectionModificationYn(t.get("sectionModificationYn")==null ? "" : t.get("sectionModificationYn").toString())
						 			.sectionModificationType(t.get("sectionModificationType")==null ? "" : t.get("sectionModificationType").toString())
						 			.isCoverendt(t.get("isCoverendt")==null?"N":t.get("isCoverendt").toString())
						 			.endtShortCode(t.get("endtShortCode")==null?"99999":t.get("endtShortCode").toString())
						 			.endtShortDesc(t.get("endtShortDesc")==null?"Others":t.get("endtShortDesc").toString())
						 			.updatedDate(null).build();
				 result.add(e);
				 }
 			 }
		
			 return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Cacheable(cacheNames= {"collectProductsFromLoginId"},keyGenerator  = "collectProductsFromLoginIdKeyGen",value = "collectProductsFromLoginId")
	public Tuple collectProductsFromLoginId(String loginId) {
		try {			
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="loginId:"+loginId+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
			SpecCriteria criteria = crservice.createCriteria(LoginProductMaster.class, search, "loginId");
			List<Tuple> data = crservice.getResult(criteria, 0, 50);
			if(data.size()>0)
				return data.get(0);
		}catch (Exception e) {
			e.printStackTrace();	
		}
		return null;
		
	}
	@Cacheable(cacheNames= {"fetchAlipaRating"},keyGenerator  = "fetchAlipaRatingKeyGen",value = "fetchAlipaRating")
	public List<Tuple> fetchAlipaRating(String search) {
		try {
			SpecCriteria criteria = crservice.createCriteria(SectionCoverMaster.class, search, "coverId");
			List<Tuple> data = crservice.getResult(criteria, 0, 50);
			return data;
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	@Cacheable(cacheNames= {"collectCommissionDetails"},keyGenerator  = "collectCommissionDetailsKeyGen",value = "collectCommissionDetails")
	public Map<String, Object> collectCommissionDetails(String loginId) {
		 try {
			 String todayInString = DD_MM_YYYY.format(new Date());
			 String search="loginId:"+loginId+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;policyType:99999;";
			 SpecCriteria criteria = crservice.createCriteria(BrokerCommissionDetails.class, search, "amendId");
			 List<Tuple> data = crservice.getResult(criteria, 0, 50);
			 if(data.size()>0) {
				 Map<String, Object> r=new HashMap<String, Object>(); 
				 r.put("COMMISSION_PERCENTAGE", data.get(0).get("commissionPercentage"));
				 r.put("COMMISSION_VAT_YN", data.get(0).get("commissionVatYn"));
				 r.put("COMMISSION_VAT_PERCENT", data.get(0).get("commissionVatPercent"));
				 
				 CalcEngine e=new CalcEngine();
				 e.setAgencyCode("99999");
				 e.setBranchCode("99999");
				 e.setInsuranceId(data.get(0).get("companyId").toString());
				 e.setProductId(data.get(0).get("productId").toString());
				 e.setSectionId("99999");
				  List<Tuple> loadTax = LoadTax(e, Arrays.asList("NB"));
				  Double totalTax=loadTax==null?0D:loadTax.stream().mapToDouble(t->t.get("value")==null?0D:Double.parseDouble(t.get("value").toString())).sum();				  
				  r.put("TOTALTAX", totalTax);
				  
				 return r;
			 }
		 }catch (Exception e) {
			 e.printStackTrace();
		}
		return null;
	}
	
	@PersistenceContext
	private EntityManager em;

	public List<PolicyCoverData> findDataForTravel(String endtPrevQuoteNo, Integer vehicleId, String insuranceId,
			Integer productId, Integer sectionId, String status) {

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PolicyCoverData> createQuery = cb.createQuery(PolicyCoverData.class);
			Root<PolicyCoverData> from = createQuery.from(PolicyCoverData.class);
			Predicate n1 = cb.equal(from.get("quoteNo"),endtPrevQuoteNo);
			Predicate n2 = cb.equal(from.get("vehicleId"),vehicleId);
			Predicate n3 = cb.equal(from.get("companyId"),insuranceId);			
			Predicate n4 = cb.equal(from.get("productId"),productId);
			Predicate n5 = cb.equal(from.get("sectionId"),sectionId);
			Predicate n6 = cb.equal(from.get("status"),status);
			Field[] declaredFields = PolicyCoverData.class.getDeclaredFields();
			//declaredFields
			List<Selection<?>> colselct=new ArrayList<Selection<?>>();
			
			for(Field  field:declaredFields) {
				try {
					if(!"serialVersionUID".equals(field.getName()))
					{
						Selection<Object> alias = from.get(field.getName()).alias(field.getName());
						colselct.add(alias);
					}
				}catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
			
			
			createQuery.where(n1,n2,n3,n4,n5,n6).groupBy(from.get(""));
			createQuery.multiselect(colselct);
		}catch (Exception e) {
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}
	@Cacheable(cacheNames= {"collectSectionMaster"},keyGenerator  = "collectSectionMasterKeyGen",value = "collectSectionMaster")
	public List<ProductSectionMaster> collectSectionMaster(String companyId, String productId, String sectionId) {
		try {

			List<ProductSectionMaster> result=new ArrayList<ProductSectionMaster>();
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+companyId+";productId:"+productId+";sectionId:"+sectionId+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
			SpecCriteria criteria = crservice.createCriteria(ProductSectionMaster.class, search, "sectionId");
			List<Tuple> prorata = crservice.getResult(criteria, 0, 50);
			 if(prorata!=null && prorata.size()>0) {
				 for(int i=0;i<prorata.size();i++) {
					 Tuple t = prorata.get(i);
					 ProductSectionMaster section= new ProductSectionMaster();
					 section.setCoreAppCode(t.get("coreAppCode")==null?"":t.get("coreAppCode").toString());
					 section.setSectionName(t.get("sectionName")==null?"":t.get("sectionName").toString());
					 section.setSectionId(t.get("sectionId")==null?0:Integer.parseInt(t.get("sectionId").toString()));
					 result.add(section);
					 
				 }
			 }
			 return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Cacheable(cacheNames= {"collectBranchMaster"},keyGenerator  = "collectBranchMasterKeyGen",value = "collectBranchMaster")
	public List<BranchMaster> collectBranchMaster(String companyId, String branchCode) {

		try {

			List<BranchMaster> result=new ArrayList<BranchMaster>();
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+companyId+";branchCode:"+branchCode+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
			SpecCriteria criteria = crservice.createCriteria(BranchMaster.class, search, "branchCode");
			List<Tuple> prorata = crservice.getResult(criteria, 0, 50);
			 if(prorata!=null && prorata.size()>0) {
				 for(int i=0;i<prorata.size();i++) {
					 Tuple t = prorata.get(i);
					 BranchMaster b=new BranchMaster();
					 b.setCoreAppCode(t.get("coreAppCode")==null?"":t.get("coreAppCode").toString());
					 
					 result.add(b);
					 
				 }
			 }
			 return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	
	}

	 
	public List<DropDownRes> getLifePolicyTerms(String companyId,String productId) {
		try {
		String todayInString = DD_MM_YYYY.format(new Date());
		String search="companyId:"+companyId+";productId:"+productId+";sectionId:99999;status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
		SpecCriteria criteria = crservice.createCriteria(LifePolicytermsMaster.class, search, "policyTerms");
		List<Tuple> prorata = crservice.getResult(criteria, 0, 50);
		 List<DropDownRes> result=new LinkedList<DropDownRes>();
		 if(prorata!=null && prorata.size()>0) {
			 for(int i=0;i<prorata.size();i++) {
				 Tuple t = prorata.get(i);
				 DropDownRes b=new DropDownRes();
				 b.setCode(t.get("policyTerms")==null?"":t.get("policyTerms").toString());
				 b.setCodeDesc(t.get("policyTermsDesc")==null?"":t.get("policyTermsDesc").toString());
				 
				 result.add(b);
				 
			 }
		 }
		 return result;
	}catch (Exception e) {
		e.printStackTrace();
	}
		return null;
	}
	@Cacheable(cacheNames = {"excludedTax"},keyGenerator  = "excludedTaxKeyGen",value = "excludedTax" )
	public List<Tuple> LoadExcludedTax(CalcEngine engine, List<String> taxFor) {
		System.out.println(">>> LoadExcludedTax CACHE MISS - DB HIT");
		try {
			String todayInString = DD_MM_YYYY.format(new Date()); 
			String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()+";status:Y;branchCode:{99999,"+engine.getBranchCode()+"};"+todayInString+"~effectiveDateStart&effectiveDateEnd;taxFor:{"+StringUtils.join(taxFor,',')+"};";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(TaxExemptionSetup.class, search, "taxId");			
			result=crservice.getResult(criteria, 0, 50);			
			return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Long getCountFromRating(String dataquery) {
		try {
			SpecCriteria criteria = crservice.createCriteria(FactorRateMaster.class, dataquery, "factorTypeId"); 

			List<Long> count = crservice.getCount(criteria, 0, 500);
			if(!count.isEmpty()) { 
				Long countrec = count.get(0);				
				return countrec;
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return Long.valueOf("0");
	}

	public List<Tuple> getResult(String dataquery) {
		try {
			
			SpecCriteria criteria = crservice.createCriteria(FactorRateMaster.class, dataquery, "factorTypeId"); 
			List<Tuple> result = crservice.getResult(criteria, 0, 500);
			return result;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	@Autowired
	private EwayFactorDetailsRepository fdRepo;
	@Autowired
	private EwayFactorResultDetailRepository fdResultRepo;
	public List<EwayFactorDetails> saveFactorDetails(Map<String, List<Tuple>> queriesResult, CalcEngine engine, List<Tuple> result, List<Tuple> vehicles,
			List<Tuple> customers, Cover t, Map<String, List<Tuple>> minRateLoadingResult) {
		try {
			int count= fdRepo.deleteByRequestReferenceNoAndVehicleId(engine.getRequestReferenceNo(),Integer.parseInt(engine.getVehicleId()));
			List<EwayFactorDetails> fds=new ArrayList<EwayFactorDetails>();
			int sno=2;
			String pattern =  "#####0.#####" ;
			DecimalFormat decimalFormat = new DecimalFormat(pattern);
//			List<Double> minPremiumRates=new ArrayList<Double>();
//			for(Entry<String, List<Tuple>> entrySet :minRateLoadingResult.entrySet()) {
//						
//				List<Tuple> value = entrySet.getValue();
//				Double rate=value.isEmpty()?0D:(value.get(0).get("minPremium")==null?0D:Double.valueOf(decimalFormat.format( Double.parseDouble(value.get(0).get("minPremium").toString()))));
//				System.out.println(entrySet.getKey()+"--"+rate);
//				minPremiumRates.add(rate);
//			}
			
			Map<String, Double> minPremiumLoadingMap = new LinkedHashMap<>();
			int noLoadingFactorCount = 0;

			for (Entry<String, List<Tuple>> entrySet : minRateLoadingResult.entrySet()) {
				System.out.println("Key: " + entrySet.getKey() + 
	                       " | Value NULL? " + (entrySet.getValue() == null) +
	                       " | Size: " + (entrySet.getValue() == null ? "NULL" : entrySet.getValue().size()));


			    String factorName = entrySet.getKey();
			    List<Tuple> value = entrySet.getValue();
			    Double loading = 1D;

			    if (value != null && !value.isEmpty()
			            && value.get(0) != null
			            && value.get(0).get("minPremium") != null) {

			        loading = Double.valueOf(
			                decimalFormat.format(
			                        Double.parseDouble(value.get(0).get("minPremium").toString())
			                )
			        );
			    }

			    boolean isNoLoading = Double.compare(loading, 1D) == 0;

			    if (isNoLoading) {
			        noLoadingFactorCount++;
			    }
			    
			    System.out.println(
			            "Minimum Premium Loading -> Factor: " + factorName +
			            " | Rate Used: " + loading +
			            (isNoLoading ? " (No Loading Applied)" : "")
			        );

			    minPremiumLoadingMap.put(factorName, loading);
			}


				
			for(Entry<String, List<Tuple>> entrySet :queriesResult.entrySet()) {
				String key=entrySet.getKey();
				
				List<Tuple> value = entrySet.getValue();
				EwayFactorDetails fd=null;
				if("Base".equalsIgnoreCase(key)) {
					 fd=createBaseFactor(value,engine,vehicles,t);
					 System.out.println(
							    "BASE FACTOR => calcType=" + value.get(0).get("calcType")
							  + ", rate=" + value.get(0).get("rate")
							  + ", SI=" + vehicles.get(0).get("sumInsured")
							);

				}else {
				//	if (minRateLoadingResult.get(key)!=null) break;
					 fd=EwayFactorDetails.builder()
							.amendId(0)
							.cdRefno(engine.getCdRefNo())
							.companyId(engine.getInsuranceId())
							.factorId(sno++)
							.factorName(key)
							.coverId(Integer.parseInt(t.getCoverId()))
							.coverName(t.getCoverName())
							.createdBy(engine.getCreatedBy())
							.entryDate(new Date())
							.fire(value.get(0).get("param19")==null?0D:Double.valueOf(decimalFormat.format( Double.parseDouble(value.get(0).get("param19").toString()))))
							.thirdParty(value.get(0).get("param20")==null?0D:Double.valueOf(decimalFormat.format(Double.parseDouble(value.get(0).get("param20").toString()))))
							.theft(value.get(0).get("param18")==null?0D:Double.valueOf(decimalFormat.format(Double.parseDouble(value.get(0).get("param18").toString()))))
							.windscreen(value.get(0).get("param17")==null?0D:Double.valueOf(decimalFormat.format(Double.parseDouble(value.get(0).get("param17").toString()))))
							.ownDamage(value.get(0).get("param16")==null?0D:Double.valueOf(decimalFormat.format(Double.parseDouble(value.get(0).get("param16").toString()))))
							.msRefno(engine.getMsrefno())
							.productId(Integer.parseInt(engine.getProductId()))
							.requestReferenceNo(engine.getRequestReferenceNo())
							.sectionId(Integer.parseInt(engine.getSectionId()))
							.status("Y")
							.subCoverYn("N")
							.subCoverId(0)
							.subCoverName("")
							.vdRefno(engine.getVdRefNo())
							.vehicleId(Integer.parseInt(engine.getVehicleId()))						
							.build();
				}
				fds.add(fd);
			}
			
			//Annual Peril Risk Premium Amount
			EwayFactorDetails fd=EwayFactorDetails.builder()
					.amendId(0)
					.cdRefno(engine.getCdRefNo())
					.companyId(engine.getInsuranceId())
					.factorId(sno++)
					.factorName("Annual Peril Risk Premium Amount")
					.coverId(Integer.parseInt(t.getCoverId()))
					.coverName(t.getCoverName())
					.createdBy(engine.getCreatedBy())
					.entryDate(new Date())
					.fire(Double.valueOf(decimalFormat.format( fds.stream().mapToDouble(EwayFactorDetails::getFire).reduce((a,b)->a*b).getAsDouble())))
					.thirdParty(Double.valueOf(decimalFormat.format(fds.stream().mapToDouble(EwayFactorDetails::getThirdParty).reduce((a,b)->a*b).getAsDouble())))
					.theft(Double.valueOf(decimalFormat.format(fds.stream().mapToDouble(EwayFactorDetails::getTheft).reduce((a,b)->a*b).getAsDouble())))
					.windscreen(Double.valueOf(decimalFormat.format(fds.stream().mapToDouble(EwayFactorDetails::getWindscreen).reduce((a,b)->a*b).getAsDouble())))
					.ownDamage(Double.valueOf(decimalFormat.format(fds.stream().mapToDouble(EwayFactorDetails::getOwnDamage).reduce((a,b)->a*b).getAsDouble())))
					.msRefno(engine.getMsrefno())
					.productId(Integer.parseInt(engine.getProductId()))
					.requestReferenceNo(engine.getRequestReferenceNo())
					.sectionId(Integer.parseInt(engine.getSectionId()))
					.status("Y")
					.subCoverYn("N")
					.subCoverId(0)
					.subCoverName("")
					.vdRefno(engine.getVdRefNo())
					.vehicleId(Integer.parseInt(engine.getVehicleId()))						
					.build();
			
			//Annual Peril Risk Premium Rate
			Double sumInsured=Double.parseDouble(vehicles.get(0).get("sumInsured").toString());
			EwayFactorDetails fdr=EwayFactorDetails.builder()
					.amendId(0)
					.cdRefno(engine.getCdRefNo())
					.companyId(engine.getInsuranceId())
					.factorId(sno++)
					.factorName("Annual Peril Risk Premium Rate")
					.coverId(Integer.parseInt(t.getCoverId()))
					.coverName(t.getCoverName())
					.createdBy(engine.getCreatedBy())
					.entryDate(new Date())
					.fire(Double.valueOf(decimalFormat.format( (fd.getFire()/(sumInsured.doubleValue()>0?sumInsured:1D)))))
					.thirdParty(Double.valueOf(decimalFormat.format((fd.getThirdParty()/(sumInsured.doubleValue()>0?sumInsured:1D)))))
					.theft(Double.valueOf(decimalFormat.format( (fd.getTheft()/(sumInsured.doubleValue()>0?sumInsured:1D)))))
					.windscreen(Double.valueOf(decimalFormat.format( (fd.getWindscreen()/(sumInsured.doubleValue()>0?sumInsured:1D)))))
					.ownDamage(Double.valueOf(decimalFormat.format( (fd.getOwnDamage()/(sumInsured.doubleValue()>0?sumInsured:1D)))))
					.msRefno(engine.getMsrefno())
					.productId(Integer.parseInt(engine.getProductId()))
					.requestReferenceNo(engine.getRequestReferenceNo())
					.sectionId(Integer.parseInt(engine.getSectionId()))
					.status("Y")
					.subCoverYn("N")
					.subCoverId(0)
					.subCoverName("")
					.vdRefno(engine.getVdRefNo())
					.vehicleId(Integer.parseInt(engine.getVehicleId()))						
					.build();
			
			fds.add(fd);
			fds.add(fdr);
			
			String todayInString = DD_MM_YYYY.format(new Date());
			String minRateQuery="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+115+";param10:"+vehicles.get(0).get("vehicleClass").toString()
			+";param11:"+vehicles.get(0).get("insuranceClass").toString()+";"
			+(vehicles.get(0).get("sumInsured")==null?0:new BigDecimal(vehicles.get(0).get("sumInsured").toString()).toPlainString())+"~param1&param2;";
			;
			
			
			pattern =  "#####0.#####" ;
			decimalFormat = new DecimalFormat(pattern);
			
			List<Tuple> queryResult = getResult(minRateQuery);
			Double minPremium=0D;
			Double minRate=0D;
			
			Double minPremiumOrg=Double.parseDouble(queryResult.get(0).get("minPremium").toString());
			Double minRateOrg=Double.parseDouble(queryResult.get(0).get("rate").toString());
			System.out.println("DEBUG minPremiumOrg=" + minPremiumOrg);
			System.out.println("DEBUG minRateOrg=" + minRateOrg);
			
			Double minRateLoading = minPremiumLoadingMap
			        .values()
			        .stream()
			        .reduce(1D, (a, b) -> a * b);
			System.out.println("Total Minimum Premium Loading Multiplier: " + minRateLoading);
			ObjectMapper mapper = new ObjectMapper();
			String loadingJson = mapper.writeValueAsString(minPremiumLoadingMap);


			String insuranceClass = vehicles.get(0).get("insuranceClass").toString();

			if ("3".equals(insuranceClass)) {

			    minPremium = decimalFormat.parse(decimalFormat.format(minPremiumOrg)).doubleValue();
			    minRate    = decimalFormat.parse(decimalFormat.format(minRateOrg)).doubleValue();

			} else {

			    minPremium = decimalFormat.parse(decimalFormat.format(minPremiumOrg * minRateLoading)).doubleValue();
			    minRate    = decimalFormat.parse(decimalFormat.format(minRateOrg * minRateLoading)).doubleValue();
			}
			
			System.out.println("DEBUG minRate after loading=" + minRate);
			System.out.println("DEBUG minPremium after loading=" + minPremium);

			final double RATE_DISCOUNT_PERCENT = 10D;
			String appliedDiscountType = null;
			Double appliedDiscountRate = 0D;


			if (!"3".equals(insuranceClass) && noLoadingFactorCount >= 4) {

			    double discountMultiplier = (100 - RATE_DISCOUNT_PERCENT) / 100;

			    minRate = Double.valueOf(
			            decimalFormat.format(minRate * discountMultiplier)
			    );
			    appliedDiscountType = "MIN_RATE";
			    appliedDiscountRate = RATE_DISCOUNT_PERCENT;
			}
			

			Double totalLossRatio= (Double) 60d/100;
			Double riskPremiumAmt=(Double) (fd.getOwnDamage()+fd.getFire()+fd.getTheft()+fd.getThirdParty()+fd.getWindscreen());
			if("2".equals(vehicles.get(0).get("insuranceClass").toString())) {
				riskPremiumAmt=(Double) (fd.getOwnDamage()+fd.getFire()+fd.getTheft()+fd.getThirdParty()+fd.getWindscreen());
			}else if("3".equals(vehicles.get(0).get("insuranceClass").toString())) {
				riskPremiumAmt=(Double) (fd.getThirdParty());
			}
			
			String appliedRule = "BASE";
			Double premium=(Double) riskPremiumAmt/totalLossRatio;
			Double calcMinRate=sumInsured*(Double) (minRate/100);
			
			
			if (sumInsured > 3000000 &&
				    ("1".equals(insuranceClass) || "2".equals(insuranceClass))
				) {
				    premium = calcMinRate;
				    appliedRule = "MIN_RATE";
				} else {
					if (premium <= minPremium) {
				        premium = minPremium;
				        appliedRule = "MIN_PREMIUM";
				        t.setMinimumRateYn("Y");
				    }

				   
				    if (premium <= calcMinRate) {
				        premium = calcMinRate;
				        appliedRule = "MIN_RATE";
				    }
				    
				    if (premium > calcMinRate) {
					    double derivedMinRate = (premium / sumInsured) * 100;
				        minRate = derivedMinRate;
				    }
			}
			
			if("2".equals(vehicles.get(0).get("insuranceClass").toString())) {
				premium =premium *0.60;
			}

			
			List<EserviceMotorDetails> instList = eserviceMotorRepo
		            .findByRequestReferenceNo(engine.getRequestReferenceNo());

		    Integer installmentPeriod = 0;
		    if (instList != null && !instList.isEmpty() && instList.get(0).getNoOfClaims() != null) {
		        installmentPeriod = instList.get(0).getNoOfClaims();
		    }

		    if (installmentPeriod >= 4 &&
		            instList != null &&
		            !instList.isEmpty() &&
		            "1".equalsIgnoreCase(instList.get(0).getInsuranceClass())) {

		        premium = premium + (premium * 0.10); 
		    }

		    
		    double sumInsuredForCalc = (sumInsured == 0 ? 1 : sumInsured);

		   
		    double premiumRate;

		    if ("MIN_PREMIUM".equals(appliedRule)) {
		        premiumRate = minRate;
		    } else if ("MIN_RATE".equals(appliedRule)) {
		        premiumRate = minRate;
		    } else {
		        premiumRate = (premium / sumInsuredForCalc) * 100;
		    }
		    
		    

		    premium = Double.valueOf(decimalFormat.format(premium));
		    premiumRate = Double.valueOf(decimalFormat.format(premiumRate));
		    
		    			
			
			EwayFactorResultDetail efResult=EwayFactorResultDetail.builder()
					.cdRefno(engine.getCdRefNo())
					.companyId(engine.getInsuranceId())
					.coverId(Integer.parseInt(t.getCoverId()))
					.coverName(t.getCoverName())
					.createdBy(engine.getCreatedBy())
					.entryDate(new Date()) 
					.msRefno(engine.getMsrefno())
					.productId(Integer.parseInt(engine.getProductId()))					
					.requestReferenceNo(engine.getRequestReferenceNo())
					.sectionId(Integer.parseInt(engine.getSectionId()))
					.status("Y")
					.targetLossRatio(60D)
					.vdRefno(engine.getVdRefNo())
					.vehicleId(Integer.parseInt(engine.getVehicleId()))
					.minPremium(minPremiumOrg)
					.minRate(minRateOrg)
					.minPremiumLoad(minPremium)
					.minRateLoad(minRate)
					.appliedDiscountType(appliedDiscountType)
			        .appliedDiscountRate(appliedDiscountRate)
			        .minLoadingBreakdown(loadingJson)
					.finalPremiumAmtExclTax(premium)
					.finalPremiumRateExclTax(Double.valueOf(decimalFormat.format((Double) (premium/(sumInsured==0?1:sumInsured)))))
					//.proRataPremiumAmtExclTax(sumInsured)					
					.riskPremiumAmt(riskPremiumAmt)
					.riskPremiumRate(Double.valueOf(decimalFormat.format( (Double) (riskPremiumAmt)/(sumInsured.doubleValue()>0?sumInsured:1D))))
					.build();
			
			fdResultRepo.save(efResult);			
			fdRepo.saveAll(fds);
			try {

			 t.setPremiumBeforeDiscount(new BigDecimal(premium));
			 t.setMinimumPremium(new BigDecimal(minPremium));	
			 if ("MIN_PREMIUM".equals(appliedRule)) {
			        t.setMinimumPremiumYn("Y");
			    } 
			 
			 t.setRate(premiumRate);

				} catch (Exception e) {
				    e.printStackTrace();
				}
			 
			 t.setCalcType("P");
			 t.setRegulatoryCode("NA");
			 /// Referal
			 t.setIsReferral((premium<1)?"Y":"N");
			 if("Y".equals(t.getIsReferral())){
				 t.setReferalDescription(t.getCoverDesc() +" Referral" );
				
			 }
			 
			return fds;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	private EwayFactorDetails createBaseFactor(List<Tuple> value, CalcEngine engine, List<Tuple> vehicles,Cover t) {
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			String SumInsured =
				    "companyId:" + engine.getInsuranceId()
				  + ";productId:" + engine.getProductId()
				  + ";sectionId:" + engine.getSectionId()
				  + ";status:{Y,R};subCoverId:0;"
				  + todayInString + "~effectiveDateStart&effectiveDateEnd;"
				  + "coverId:101;"
				  + new BigDecimal(vehicles.get(0).get("sumInsured").toString()).toPlainString()
				  + "~param1&param2;";
			List<Tuple> result = getResult(SumInsured);
			if(result.size()==1) {
				SumInsured="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+101+";sNo:1;";
				List<Tuple> result2 = getResult(SumInsured);
				result.addAll(result2);
			}
			/*
				step 1 : take prev values
				step 2: take upper level si
				step 3:take lower level si
				step 4 : subtract step 2 and step 3
				step 5 multiply step 4 and step 1
				step 6 :sum of step 5
				step 7 : take rate
				step 8: subract sumInsured - lower level si
				step 9 :multiply step 8 * step 7
				step 10 : step 6 and step 9
			 */

			List<BigDecimal> ownDamages=new ArrayList<BigDecimal>();
			List<BigDecimal> windscreens=new ArrayList<BigDecimal>();
			List<BigDecimal> thefts=new ArrayList<BigDecimal>();
			List<BigDecimal> thirdPartys=new ArrayList<BigDecimal>();
			List<BigDecimal> fires=new ArrayList<BigDecimal>();

			for (Tuple tuple : result) {
				String upperSi=tuple.get("param2").toString();
				String lowerSi=tuple.get("param1").toString();

				BigDecimal subtract = new BigDecimal(upperSi).subtract(new BigDecimal(lowerSi));
				/*
				 * 	.fire(value.get(0).get("param19")==null?0D:Double.parseDouble(value.get(0).get("param19").toString()))
							.thirdParty(value.get(0).get("param20")==null?0D:Double.parseDouble(value.get(0).get("param20").toString()))
							.theft(value.get(0).get("param18")==null?0D:Double.parseDouble(value.get(0).get("param18").toString()))
							.windscreen(value.get(0).get("param17")==null?0D:Double.parseDouble(value.get(0).get("param17").toString()))
							.ownDamage(value.get(0).get("param16")==null?0D:Double.parseDouble(value.get(0).get("param16").toString()))
				 */
				BigDecimal ownDamage = new BigDecimal(tuple.get("param16").toString()).multiply(subtract,MathContext.DECIMAL32);
				BigDecimal windscreen = new BigDecimal(tuple.get("param17").toString()).multiply(subtract,MathContext.DECIMAL32);
				BigDecimal theft = new BigDecimal(tuple.get("param18").toString()).multiply(subtract,MathContext.DECIMAL32);
				BigDecimal thirdParty = new BigDecimal(tuple.get("param20").toString()).multiply(subtract,MathContext.DECIMAL32);
				BigDecimal fire = new BigDecimal(tuple.get("param19").toString()).multiply(subtract,MathContext.DECIMAL32);
				ownDamages.add(ownDamage);
				windscreens.add(windscreen);
				thefts.add(theft);
				thirdPartys.add(thirdParty);
				fires.add(fire);

			}
			BigDecimal sumOwnDamage = ownDamages.stream().reduce(BigDecimal.ZERO,BigDecimal::add);
			BigDecimal sumwindscreen = windscreens.stream().reduce(BigDecimal.ZERO,BigDecimal::add);
			BigDecimal sumtheft = thefts.stream().reduce(BigDecimal.ZERO,BigDecimal::add);
			BigDecimal sumthirdParty = thirdPartys.stream().reduce(BigDecimal.ZERO,BigDecimal::add);
			BigDecimal sumfire = fires.stream().reduce(BigDecimal.ZERO,BigDecimal::add);

			BigDecimal sumInsured=new BigDecimal(vehicles.get(0).get("sumInsured").toString());
			BigDecimal lowerSI=new BigDecimal(value.get(0).get("param1").toString());
			BigDecimal subtract = sumInsured.subtract(lowerSI,MathContext.DECIMAL32);

			BigDecimal ownDamage = new BigDecimal(value.get(0).get("param16").toString()).multiply(subtract,MathContext.DECIMAL32);
			BigDecimal windscreen = new BigDecimal(value.get(0).get("param17").toString()).multiply(subtract,MathContext.DECIMAL32);
			BigDecimal theft = new BigDecimal(value.get(0).get("param18").toString()).multiply(subtract,MathContext.DECIMAL32);
			BigDecimal thirdParty = new BigDecimal(value.get(0).get("param20").toString()).multiply(subtract,MathContext.DECIMAL32);
			BigDecimal fire = new BigDecimal(value.get(0).get("param19").toString()).multiply(subtract,MathContext.DECIMAL32);

			sumOwnDamage=sumOwnDamage.add(ownDamage);
			sumwindscreen=sumwindscreen.add(windscreen);
			sumtheft=sumtheft.add(theft);
			sumthirdParty=sumthirdParty.add(thirdParty);
			sumfire=sumfire.add(fire);
			String pattern =  "#####0.###" ;
			DecimalFormat decimalFormat = new DecimalFormat(pattern);
			EwayFactorDetails fd=EwayFactorDetails.builder()
					.amendId(0)
					.cdRefno(engine.getCdRefNo())
					.companyId(engine.getInsuranceId())
					.factorId(1)
					.factorName("Base")
					.coverId(Integer.parseInt(t.getCoverId()))
					.coverName(t.getCoverName())
					.createdBy(engine.getCreatedBy())
					.entryDate(new Date())
					.fire(Double.valueOf(decimalFormat.format(sumfire.doubleValue())))
					.thirdParty(Double.valueOf(decimalFormat.format(sumthirdParty.doubleValue())))
					.theft(Double.valueOf(decimalFormat.format(sumtheft.doubleValue())))
					.windscreen(Double.valueOf(decimalFormat.format(sumwindscreen.doubleValue())))
					.ownDamage(Double.valueOf(decimalFormat.format(sumOwnDamage.doubleValue())))
					.msRefno(engine.getMsrefno())
					.productId(Integer.parseInt(engine.getProductId()))
					.requestReferenceNo(engine.getRequestReferenceNo())
					.sectionId(Integer.parseInt(engine.getSectionId()))
					.status("Y")
					.subCoverYn("N")
					.subCoverId(0)
					.subCoverName("")
					.vdRefno(engine.getVdRefNo())
					.vehicleId(Integer.parseInt(engine.getVehicleId()))						
					.build();
			return fd;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Autowired
	private EwayVehicleMakemodelMasterDetailRepository makemodel;
	public EwayVehicleMakemodelMasterDetail collectMakeModelDetails(CalcEngine engine, List<Tuple> vehicles) {
		try {
			List<EwayVehicleMakemodelMasterDetail> models=makemodel.findByModelId(Integer.parseInt(vehicles.get(0).get("vehicleModelId").toString()));
			if(models!=null && models.size()>0)
				return models.get(0);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Autowired
	private MotorDriverDetailsRepository mddRepo;
	public MotorDriverDetails collectDriver(CalcEngine engine) {
		try {
			List<MotorDriverDetails> mddes=mddRepo.findByRequestReferenceNoAndRiskId(engine.getRequestReferenceNo(),Integer.parseInt(engine.getVehicleId()));
			return mddes.get(0);
		}catch (Exception e) {
			e.printStackTrace();
			
		}
		return null;
	}
	
	@Autowired
	private ChartAccountService chartService;


	public CommonRes drcrEntry(ChartAccountRequest r) {
	   return chartService.drcrEntry(r);		
	}

	public List<Map<String, Object>> executeCorporatePlusQuery(DashBoardGetReq req, List<String> loginIds, Date startDate, Date endDate) {
		try {
			List<Map<String, Object>> list = mddRepo.findByCorporatePlus(req.getInsuranceId(),req.getProductId(),loginIds,req.getUserType(),startDate,endDate);
			return list;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Map<String, Object>> executeChartCorporatePlusQuery(DashBoardGetReq req, List<String> loginIds,
			Date startDate, Date endDate) {
		try {
			List<Map<String, Object>> list = mddRepo.findByCorporatePlusChart(req.getInsuranceId(),req.getProductId(),loginIds,req.getUserType(),startDate,endDate);
			return list;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void saveFds(List<EwayFactorDetails> fds, CalcEngine engine) {
		try {
			int count= fdRepo.deleteByRequestReferenceNoAndVehicleId(engine.getRequestReferenceNo(),Integer.parseInt(engine.getVehicleId()));
		fdRepo.saveAll(fds);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void saveFactorResult(EwayFactorResultDetail efResult) {
		try {
			fdResultRepo.save(efResult);		
			}catch(Exception e) {
				e.printStackTrace();
			}
	}

	public List<Tuple> loopfactorrates(CalcEngine engine, Map<String, List<String>> vloop, String coverId,	String subCoverId, List<RatingInfo> rateInfos) {
		try {
			List<String> condtions=new ArrayList<String>();
			List<String> groupBy=new ArrayList<String>();
			groupBy.add("agencyCode");
			String todayInString = DD_MM_YYYY.format(new Date());
			condtions.add("companyId:"+ engine.getInsuranceId() );
			condtions.add("productId:"+ engine.getProductId() );
			condtions.add("sectionId:"+ engine.getSectionId() );
			condtions.add("status:{Y,R}");
			condtions.add("coverId:"+ coverId);
			condtions.add("subCoverId:"+subCoverId);
			condtions.add(todayInString+"~effectiveDateStart&effectiveDateEnd");
			if(rateInfos.size()>0)
				condtions.add("factorTypeId:"+rateInfos.get(0).getFactortypeId());
					
			
			for (RatingInfo r : rateInfos) {
				
				if("Y".equals(r.getFactorRangeYn())) {
					String inputValue = StringUtils.isBlank(r.getInputColumValue())
					        ? "99999"
					        : r.getInputColumValue();

					String condtion = inputValue + "~"
					        + r.getRangeFromCol() + "&" + r.getRangeToCol();					
					condtions.add(condtion);
				}else {
					groupBy.add(r.getDiscretCol());
				}
				
			}
			System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
			        + " <---- createCriteriaForGroupBy Block start :---->");
			SpecCriteria specCriteria = crservice.createCriteriaForGroupBy(FactorRateMaster.class, StringUtils.join(condtions,';'), "agencyCode",groupBy);
			System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
			        + " <---- createCriteriaForGroupBy Block end :---->");
			
			List<Tuple> result = crservice.getResultGroupBy(specCriteria, 0, 1000);
			if(result.size()>0) {
				for (RatingInfo r : rateInfos) {
					if(!"Y".equals(r.getFactorRangeYn())) {
						/*Optional<Tuple> findFirst = result.stream().filter(i -> i.get(r.getDiscretCol()).equals(r.getInputColumValue())
								).findFirst();*/
						System.out.println("Checking discrete column: " + r.getDiscretCol());
						System.out.println("Input value: " + r.getInputColumValue());
						System.out.println("Group results: " + result);
						List<Tuple> collect = result.stream()
						        .filter(i -> String.valueOf(i.get(r.getDiscretCol()))
						        .equals(String.valueOf(r.getInputColumValue())))
						        .collect(Collectors.toList());						
						if(collect.isEmpty())
							condtions.add(r.getDiscretCol()+":99999");
						else {
							condtions.add(r.getDiscretCol()+":"+r.getInputColumValue());
							result=collect;
						}
					}
					else {
	                    if (result.isEmpty()) {
	                        condtions.add(r.getInputColumValue() + ":99999");
	                    }
	                }
				}
				
//				Optional<Tuple> findFirst = result.stream().filter(i -> i.get("agencyCode").equals(engine.getAgencyCode())
//						).findFirst();
				
				Optional<Tuple> findFirst = result.stream()
				        .filter(i -> i != null
				                && i.get("agencyCode") != null
				                && engine.getAgencyCode() != null
				                && String.valueOf(i.get("agencyCode"))
				                        .equals(String.valueOf(engine.getAgencyCode())))
				        .findFirst();

				
				
				if(findFirst.isEmpty())
					condtions.add("agencyCode:99999");
				else
					condtions.add("agencyCode"+":"+engine.getAgencyCode());
				
			}
			
			System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
			        + " <---- loadfactorOnlyquery Block start :---->");
			List<Tuple> loadfactorOnlyquery = loadfactorOnlyquery(engine,StringUtils.join(condtions,';'), coverId,subCoverId);
			System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
			        + " <---- loadfactorOnlyquery Block nd :---->");
			return loadfactorOnlyquery;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private final Map<String, List<Tuple>> listItemCache = new ConcurrentHashMap<>();

	public List<Tuple> customerTaxList(CalcEngine engine) {
	    try {

	        String search = "companyId:" + engine.getInsuranceId() + ";status:Y;";

	        List<Tuple> result = listItemCache.get(search);
	        if (result != null) {
	            return result;
	        }

	        SpecCriteria criteria = crservice.createCriteria(ListItemValue.class, search, "itemId");
	        result = crservice.getResult(criteria, 0, 50);

	        if (result.isEmpty()) {
	            search = "companyId:99999;status:Y;";

	            result = listItemCache.get(search);
	            if (result != null) {
	                return result;
	            }

	            criteria = crservice.createCriteria(ListItemValue.class, search, "itemId");
	            result = crservice.getResult(criteria, 0, 50);
	        }


	        listItemCache.put(search, result);

	        return result.size() > 0 ? result : new ArrayList<>();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	public long calculatePolicyDays(CalcEngine engine) {
	    try {
	        LocalDate startDate = toLocalDate(engine.getEffectiveDate());
	        LocalDate endDate   = toLocalDate(engine.getPolicyEndDate());

	        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
	        System.out.println(">>> Policy days calculated: " + days
	            + " [" + startDate + " -> " + endDate + "]");
	        return days;

	    } catch (Exception e) {
	        System.out.println(">>> WARNING: Could not parse policy dates, defaulting to 0 days");
	        e.printStackTrace();
	        return 0L;
	    }
	}

	private LocalDate toLocalDate(Object dateValue) {
	    if (dateValue == null) return LocalDate.now();

	    if (dateValue instanceof java.util.Date) {
	        return ((java.util.Date) dateValue).toInstant()
	                .atZone(ZoneId.systemDefault())
	                .toLocalDate();
	    }

	    if (dateValue instanceof String) {
	        String dateStr = (String) dateValue;
	        
	        try {
	            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	        } catch (Exception e1) {
	            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	        }
	    }

	    throw new IllegalArgumentException("Unsupported date type: " + dateValue.getClass());
	}
	
	/**
	 * From the full tax list returned by LoadTax (may contain multiple rows per taxId
	 * when day-range bands are configured), resolves to ONE row per taxId.
	 *
	 * Rules:
	 *  - If a row has NULL startDays AND NULL endDays → legacy row, always applicable
	 *  - If a row has startDays=0 AND endDays=0 (explicit zeros) → also treated as legacy/normal tax, always applicable
	 *  - Otherwise, if day-range rows exist → pick the row where startDays <= policyDays <= endDays
	 *  - If no range matches → fallback to ZERO tax on first row for that taxId (safety net)
	 */
	public List<Tuple> resolveTaxByPolicyDays(List<Tuple> allTaxes, long policyDays) {

	    if (allTaxes == null || allTaxes.isEmpty()) {
	        System.out.println(">>> resolveTaxByPolicyDays : allTaxes is null or empty");
	        return Collections.emptyList();
	    }

	    // KEY FIX: convert to String to avoid Integer vs Long type mismatch
	    Map<String, List<Tuple>> groupedByTaxId = new LinkedHashMap<>();
	    for (Tuple row : allTaxes) {
	        String taxId = row.get("taxId") == null ? "null" : row.get("taxId").toString();
	        groupedByTaxId.computeIfAbsent(taxId, k -> new ArrayList<>()).add(row);
	    }

	    List<Tuple> resolved = new ArrayList<>();

	    for (Map.Entry<String, List<Tuple>> entry : groupedByTaxId.entrySet()) {
	        List<Tuple> rowsForTaxId = entry.getValue();

	        System.out.println(">>> Processing taxId=" + entry.getKey()
	            + " rowCount=" + rowsForTaxId.size());

	        // Find a legacy row: null/null OR explicit 0/0 → always applicable ("normal tax")
	        Tuple legacyRow = rowsForTaxId.stream()
	            .filter(row -> {
	                Object s = row.get("startDays");
	                Object e = row.get("enddays");
	                if (s == null && e == null) return true; // null/null
	                long sd = s != null ? toLong(s) : 0L;
	                long ed = e != null ? toLong(e) : 0L;
	                return sd == 0L && ed == 0L; // explicit 0/0
	            })
	            .findFirst()
	            .orElse(null);

	        if (legacyRow != null) {
	            System.out.println(">>> TaxId=" + entry.getKey()
	                + " legacy (null/null or 0/0 → normal tax, always applicable) → value="
	                + legacyRow.get("value"));
	            resolved.add(legacyRow);
	            continue;
	        }

	        // From here on, only genuine day-range rows remain for this taxId
	        Tuple matched = null;
	        for (Tuple row : rowsForTaxId) {
	            Object startDaysObj = row.get("startDays");
	            Object endDaysObj   = row.get("enddays");

	            long startDays = startDaysObj != null ? toLong(startDaysObj) : 0L;
	            long endDays   = endDaysObj   != null ? toLong(endDaysObj)   : Long.MAX_VALUE;

	            System.out.println(">>> taxId=" + entry.getKey()
	                + " checking range [" + startDays + " - " + endDays + "]"
	                + " vs policyDays=" + policyDays);

	            if (policyDays >= startDays && policyDays <= endDays) {
	                matched = row;
	                System.out.println(">>> taxId=" + entry.getKey()
	                    + " MATCHED → value=" + row.get("value"));
	                break;
	            }
	        }

	        if (matched != null) {
	            resolved.add(matched);
	        } else {
	            // No range matched → zero tax
	            System.out.println(">>> taxId=" + entry.getKey()
	                + " NO range matched for policyDays=" + policyDays
	                + " → ZERO tax applied");
	            resolved.add(new ZeroValueTupleWrapper(rowsForTaxId.get(0)));
	        }
	    }

	    System.out.println(">>> resolveTaxByPolicyDays output size=" + resolved.size());
	    resolved.forEach(t -> System.out.println("    RESOLVED taxId=" + t.get("taxId")
	        + " value=" + t.get("value")));

	    return resolved;
	}

	private long toLong(Object obj) {
	    try {
	        return Long.parseLong(obj.toString().trim());
	    } catch (Exception e) {
	        return 0L;
	    }
	}
	
	public class ZeroValueTupleWrapper implements Tuple {

	    private final Tuple delegate;

	    public ZeroValueTupleWrapper(Tuple delegate) {
	        this.delegate = delegate;
	    }

	    @Override
	    public <X> X get(TupleElement<X> tupleElement) {
	        return delegate.get(tupleElement);
	    }

	    @Override
	    public <X> X get(String alias, Class<X> type) {
	        if ("value".equals(alias)) {
	            return type.cast(BigDecimal.ZERO);
	        }
	        return delegate.get(alias, type);
	    }

	    @Override
	    public Object get(String alias) {
	        if ("value".equals(alias)) {
	            return BigDecimal.ZERO; 
	        }
	        return delegate.get(alias);
	    }

	    @Override
	    public <X> X get(int i, Class<X> type) { return delegate.get(i, type); }

	    @Override
	    public Object get(int i) { return delegate.get(i); }

	    @Override
	    public Object[] toArray() { return delegate.toArray(); }

	    @Override
	    public List<TupleElement<?>> getElements() { return delegate.getElements(); }
	}

	
}

