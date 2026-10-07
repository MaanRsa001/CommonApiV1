package com.maan.eway.common.service.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
//import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import com.maan.eway.admin.req.PolicyTypeMasterGetReq;
import com.maan.eway.admin.service.RestTemplateApiService;
import com.maan.eway.bean.InsuranceTypeMaster;
import com.maan.eway.bean.SectionMaster;
import com.maan.eway.common.req.GetProductMasterReq;
import com.maan.eway.common.req.ProductStructureMasterReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DropdownCommonRes;
import com.maan.eway.common.res.ProductStructureMasterRes;
import com.maan.eway.common.res.ProductStructureMasterResponse;
import com.maan.eway.common.service.InsuranceTypeMasterService;
import com.maan.eway.error.Error;
//import com.maan.eway.master.service.PolicyTypeMasterService;
import com.maan.eway.repository.InsuranceTypeMasterRepository;
import com.maan.eway.repository.SectionMasterRepository;
import com.maan.eway.res.DropDownRes;
@Service
@Transactional
public class InsuranceTypeMasterServiceImpl  implements InsuranceTypeMasterService{

//	@Autowired
//	private PolicyTypeMasterService service;
	
	@Value("${api.policytype.url}")
	private String policyTypeUrl;
	
	@Autowired
	InsuranceTypeMasterRepository ProductStructureRepo;

	
	@Autowired
	RestTemplateApiService restTemplateApiService;
	
	@Autowired
	SectionMasterRepository sectionrepo;
	@Override
	public List<Error> validationInsuranceTypeMaster(ProductStructureMasterReq req) {
		// TODO Auto-generated method stub
		List<Error> errors = new ArrayList<Error>();
		try {
		if (StringUtils.isBlank(req.getCompanyid())) {
			 errors.add(new Error("01", "Company Id" , "Please Enter CompanyId"));
		 }
		if (StringUtils.isBlank(req.getIndustryTypeId())) {
			 errors.add(new Error("02", "Insurance Type Id" , "Please select  the insurance type"));
		 }
//		if(StringUtils.isBlank(req.getSectionId()))
//		{
//			 errors.add(new Error("03", "Section Id" , "Please select  the section"));	
//		}
		if(StringUtils.isBlank(req.getSectionName()))
		{
			 errors.add(new Error("04", "Section Name" , "Please enter the section"));	
		}
		if(StringUtils.isBlank(req.getRemarks()))
		{
			 errors.add(new Error("05", "Remark" , "Please enter the remark"));	
		}
		
		if(StringUtils.isBlank(req.getDisplayOrder()))
		{
			 errors.add(new Error("06", "Display Order" , "Please enter the display order"));	
		}
		else if(!req.getDisplayOrder().matches("[0-9]+"))
		{
			errors.add(new Error("11", "Display Order" , "Please enter the display order in number only"));	
		}
		
		if(StringUtils.isBlank(req.getProductId()))
		{
			 errors.add(new Error("06", "Product Id" , "Please enter the product id"));	
		}
		
		if (req.getBodyTypeIds() == null) {
			 errors.add(new Error("07", "Body Type" , "Please select the body type"));
		 }
		else if(req.getBodyTypeIds().size() < 1)
		{
			errors.add(new Error("07", "Body Type" , "Please select the body type"));
		}
		
		if(req.getEntryDate() == null)
		{
			errors.add(new Error("08", "Effective Date" , "Please select the effective date"));
		}
		if(StringUtils.isBlank(req.getStatus()))
		{
			 errors.add(new Error("09", "Status " , "Please choose the status"));	
		}
		if(StringUtils.isBlank(req.getCoreAppCode()))
		{
			 errors.add(new Error("10", "Core App Code " , "Please Enter the core app code"));	
		}
		
		}catch(Exception cc)
		{
			System.out.println("The Exception Occured in ValidationnsuranceTypeMaster ");
			cc.printStackTrace();
		}
		return errors;
	}
	
	
	@Override
	public CommonRes saveproductMaster(ProductStructureMasterReq req) {
		// TODO Auto-generated method stub
		CommonRes res = new CommonRes();
		int AmendId=0;
		try {
		
			//delete Block
			{
				InsuranceTypeMaster Exsisting =ProductStructureRepo.findByIndsutryTypeIdAndSectionId(req.getIndustryTypeId(),Integer.valueOf(req.getSectionId()));	
			if(Exsisting!=null) {
				AmendId=Exsisting.getAmendId()+1;
				ProductStructureRepo.delete(Exsisting);}
			}
			//insert block
		   
			Date entryDate = req.getEntryDate();
			 LocalDate nextYearDate = entryDate.toLocalDate().plusYears(1);
			  Date enddate = java.sql.Date.valueOf(nextYearDate);
			InsuranceTypeMaster newRecords = new InsuranceTypeMaster();
			newRecords.setCompanyId(StringUtils.isBlank(req.getCompanyid())?null:req.getCompanyid());
			newRecords.setProductId(StringUtils.isBlank(req.getProductId())?null:Integer.valueOf(req.getProductId()));
			newRecords.setIndsutryTypeId(StringUtils.isBlank(req.getIndustryTypeId())? "0":req.getIndustryTypeId());
			newRecords.setIndsutryTypeDesc(StringUtils.isBlank(req.getIndustryTypeDesc())?null:req.getIndustryTypeDesc());
			newRecords.setSectionId(StringUtils.isBlank(req.getSectionId())?0:Integer.valueOf(req.getSectionId()));
			newRecords.setSectionName(StringUtils.isBlank(req.getSectionName())?null:req.getSectionName());
			newRecords.setStatus(StringUtils.isBlank(req.getStatus())?null:req.getStatus());
			newRecords.setCreatedBy(StringUtils.isBlank(req.getCreatedBy())?null:req.getCreatedBy());
			newRecords.setDisplayOrder(StringUtils.isBlank(req.getDisplayOrder())?null:Integer.valueOf(req.getDisplayOrder()));
			newRecords.setRemarks(StringUtils.isBlank(req.getRemarks())?null:req.getRemarks());
			newRecords.setEntryDate(entryDate);
			newRecords.setEffectiveDateStart(entryDate);
			newRecords.setEffectiveDateEnd(entryDate);
			newRecords.setEffectiveDateEnd(enddate);
			newRecords.setAmendId(AmendId);
			newRecords.setDisplayOrder(StringUtils.isBlank(req.getDisplayOrder())?null:Integer.valueOf(req.getDisplayOrder()));
			newRecords.setIndsutryTypeLocalDesc(StringUtils.isBlank(req.getIndustryTypeLocalDesc()) ? null : req.getIndustryTypeLocalDesc());
			newRecords.setCoreAppCode(StringUtils.isBlank(req.getCoreAppCode()) ? null : req.getCoreAppCode());
			
			List<String> bodyTypeIds = req.getBodyTypeIds();
			StringBuilder concatenatedBodyTypeIds = new StringBuilder();

			for (int i = 0; i < bodyTypeIds.size(); i++) {
			    concatenatedBodyTypeIds.append(bodyTypeIds.get(i));
			    if (i < bodyTypeIds.size() - 1) {
			        concatenatedBodyTypeIds.append(",");
			    }
			}
			newRecords.setBodyTypeIds(concatenatedBodyTypeIds.toString());
	
			ProductStructureRepo.saveAndFlush(newRecords);
		
			res.setCommonResponse("Inserted Successfully......");
			res.setIsError(false);
			res.setMessage("Success");
		}catch(Exception in)
		{
		System.out.println("**********The Exception Occured in   Insurance Type MasterSave****************");
		in.printStackTrace();
		
		res.setCommonResponse("Oops Something Went Wrong......");
		res.setIsError(true);
		res.setMessage("Failed");
		}
		return res;
	}
 
	public List<ProductStructureMasterResponse> getAllProductStructureMaster(GetProductMasterReq req)
	{
		List<ProductStructureMasterResponse> result=null;
		try {
			List<ProductStructureMasterResponse> result1 = new ArrayList<>();
			List<InsuranceTypeMaster> data =ProductStructureRepo.findByCompanyIdAndProductId(req.getCompanyId(),Integer.valueOf(req.getProductid()));
			for(InsuranceTypeMaster dd:data)
			{
				ProductStructureMasterResponse records=new ProductStructureMasterResponse();
//						DozerBeanMapper().map(dd,ProductStructureMasterReq.class);
				records.setIndustryTypeId(dd.getIndsutryTypeId());
				records.setIndustryTypeDesc(dd.getIndsutryTypeDesc());
				records.setIndustryTypeLocalDesc(dd.getIndsutryTypeLocalDesc());
				records.setCompanyid(dd.getCompanyId());
				
				records.setProductId(String.valueOf(dd.getProductId()));
				records.setSectionId(String.valueOf(dd.getSectionId()));
				records.setSectionName(dd.getSectionName());
//				records.setSectionNameLocal(dd.getSectionName());
				records.setStatus(dd.getStatus());
				records.setDisplayOrder(String.valueOf(dd.getDisplayOrder()));
				records.setEntryDate(dd.getEntryDate());
				records.setCreatedBy(dd.getCreatedBy());
				records.setAmendId(String.valueOf(dd.getAmendId()));
				records.setRemarks(dd.getRemarks()  );

			    if(dd.getBodyTypeIds() != null) {
			    	String bodyTypeIdsInString = dd.getBodyTypeIds();
				    List<String> bodyTypeIdsList = Arrays.asList(bodyTypeIdsInString.split(","));
				    records.setBodyTypeIds(bodyTypeIdsList);
			    }
			    else
			    {
			    	records.setBodyTypeIds(null);
			    }
			    
				records.setCoreAppCode(dd.getCoreAppCode());
				
				result1.add(records);
			}
			result=result1;
			
		}catch(Exception cc)
		{
		System.out.println("The Exception Occured in get All  get Insurance Type Master");	
		return null;
		}
		return result;
		
	}
	public CommonRes getInsuranceMaster(GetProductMasterReq req)
	{
		CommonRes res = new CommonRes();
		ProductStructureMasterResponse result = new ProductStructureMasterResponse();
		try {
	        
			List<SectionMaster> section =sectionrepo.findBySectionId(Integer.valueOf(req.getSectionId()));
		    InsuranceTypeMaster data =ProductStructureRepo.findByIndsutryTypeIdAndSectionId(req.getIndsutryTypeId(),Integer.valueOf(req.getSectionId()));
		    if(data!=null )
		    {
//		    result=new DozerBeanMapper().map(data, ProductStructureMasterReq.class);
		    result.setCompanyid(data.getCompanyId());
		    result.setIndustryTypeId(data.getIndsutryTypeId());
		    result.setIndustryTypeDesc(data.getIndsutryTypeDesc());
		    result.setIndustryTypeLocalDesc(data.getIndsutryTypeLocalDesc());
		    result.setSectionNameLocal(section.isEmpty()?null:section.get(0).getSectionNameLocal());
		        
		    result.setProductId(String.valueOf(data.getProductId()));
		    result.setSectionId(String.valueOf(data.getSectionId()));
		    result.setSectionName(data.getSectionName());
		    result.setStatus(data.getStatus());
		    result.setDisplayOrder(String.valueOf(data.getDisplayOrder()));
		    result.setEntryDate(data.getEntryDate());
		    result.setCreatedBy(data.getCreatedBy());
		    result.setAmendId(String.valueOf(data.getAmendId()));
		    result.setRemarks(data.getRemarks());
		    
		    if(data.getBodyTypeIds() != null) {
		    	String bodyTypeIdsInString = data.getBodyTypeIds();
			    List<String> bodyTypeIdsList = Arrays.asList(bodyTypeIdsInString.split(","));
			    result.setBodyTypeIds(bodyTypeIdsList);
		    }
		    else
		    {
		    	result.setBodyTypeIds(null);
		    }
		    

		    result.setCoreAppCode(data.getCoreAppCode());
		    
		    res.setCommonResponse(result);
		  
		    res.setIsError(false);
		    res.setMessage("Success");
		    }
		    else {
		    	  res.setCommonResponse(null);
				   res.setIsError(true);
				   res.setMessage("failed");
		    }
		}catch(Exception cc)
		{
		System.out.println("The Exception Occured in get Insurance Type Master");	
		cc.printStackTrace();
		return res;
		}
		return res;
		
	}
	public List<ProductStructureMasterRes> getByIndustryTypeId(GetProductMasterReq sneha, @RequestHeader("Authorization") String token)
	{
//		List<ProductStructureMasterRes> result=null;
		List<ProductStructureMasterRes> result1 = new ArrayList<>();
		try {
			List<InsuranceTypeMaster> getdata=	ProductStructureRepo.findByIndsutryTypeIdAndStatusAndCompanyIdAndProductId(sneha.getIndsutryTypeId(),"Y",sneha.getCompanyId(),Integer.valueOf(sneha.getProductid()));

			getdata.sort(Comparator.comparing(InsuranceTypeMaster::getDisplayOrder)); 	
			for(InsuranceTypeMaster dd:getdata)
			{
				List<SectionMaster> section =sectionrepo.findBySectionId(Integer.valueOf(dd.getSectionId()));
				ProductStructureMasterRes data = new ProductStructureMasterRes();
				data.setIndustryType(dd.getIndsutryTypeId());
				data.setSectionid(dd.getSectionId());
				data.setStatus(dd.getStatus());
				data.setSectionName(dd.getSectionName().trim());
				data.setLocalCodeDesc(section.isEmpty()?null:section.get(0).getSectionNameLocal());
			
				result1.add(data);
			}
			
			String removedBearer = token.replaceAll("Bearer ", "").split(",")[0];
			PolicyTypeMasterGetReq policyTypeMasterGetReq = new PolicyTypeMasterGetReq();
			policyTypeMasterGetReq.setInsuranceId(sneha.getCompanyId());
			policyTypeMasterGetReq.setProductId(sneha.getProductid());
			policyTypeMasterGetReq.setLoginId(sneha.getLoginId() );
				
//			String url = "http://192.168.1.42:8084/master/dropdown/policytype";
			DropdownCommonRes policyTypeCommonRes = restTemplateApiService.callSecondApi(policyTypeUrl, policyTypeMasterGetReq, removedBearer);

			List<DropDownRes> commonResponse = policyTypeCommonRes.getCommonResponse();
			if(commonResponse == null || commonResponse.isEmpty())
			{
				return result1; 
			}
			else if(commonResponse!=null)
			{
				for(int i=0; i<commonResponse.size(); i++)
				{
					if(commonResponse.get(i).getCodeDesc() == null)
					{
						 return Collections.emptyList(); 
					}
					else if(commonResponse.get(i).getCodeDesc().equalsIgnoreCase("ALL"))
					{
						 return result1; 
					}
				}
			}
		   
			 List<String> policyType = commonResponse.stream()
			            .map(D->D.getCodeDesc().trim())
			            .collect(Collectors.toList());
		
			List<ProductStructureMasterRes> getByIndustryType = result1.stream()
				            .filter(item -> policyType.contains(item.getSectionName().trim())) 
				            .collect(Collectors.toList());
				  
			 return getByIndustryType.isEmpty() ? Collections.emptyList() : getByIndustryType;
			
//			result=result1;
			
		}catch(Exception cc)
		{
		 System.out.println("***************Exception Occured in  get Insurance Type Master****************");
        cc.printStackTrace();
        return null;
		}
//		return result;
	}

	@Override
	public CommonRes DeleteproductStructureMaster(GetProductMasterReq req) {
		// TODO Auto-generated method stub
		CommonRes res=new CommonRes();
		String result="Not Data Found";
		try {
			
			
			InsuranceTypeMaster getdata=null;
			if(StringUtils.isNotBlank(req.getIndsutryTypeId()) && StringUtils.isNotBlank(req.getSectionId()))
			{
			 getdata=ProductStructureRepo.findByIndsutryTypeIdAndSectionId(req.getIndsutryTypeId(),Integer.valueOf(req.getSectionId()));
			}
			if(getdata!=null)
			{
				ProductStructureRepo.delete(getdata);
				result="Data Deleted Successfully";
			}
			res.setCommonResponse(result);
			res.setMessage("Success");
			res.setIsError(false);
			
		}catch(Exception dd)
		{
			System.out.println("**************The Exception Occured in Delete  Insurance Type Master*************");
			dd.printStackTrace();
			res.setCommonResponse(result);
			res.setMessage("Success");
			res.setIsError(false);
			
		}
		return res;
	}

	
}
