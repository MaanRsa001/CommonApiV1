package com.maan.eway.endorsment.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PolicyCoverDataEndt;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PolicyCoverDataEndtRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;

@Service
public class CopyPolicyCoverData {
	
	@Autowired
	private PolicyCoverDataRepository policyCoverRepo;

	@Autowired
	private HomePositionMasterRepository homeRepo;
	
	@Autowired
	private PolicyCoverDataEndtRepository policyCoverEndtRepo;
	
//	@Autowired
//	private UpdatePolicyCoverDataEndt updatePolicyCoverDataEndtService;

	
	public Map<String,Object> copy(String policyNo){
		try {
			HomePositionMaster hpmData = homeRepo.findByPolicyNoAndStatus(policyNo,"P");
			boolean isBasePolicy=false;
			String quoteNo=hpmData.getQuoteNo();
			if(StringUtils.isBlank(hpmData.getEndtTypeId())){
				isBasePolicy=true;
				quoteNo=hpmData.getQuoteNo();
			} 
			List<String> coverageTypes=new ArrayList<String>();			
			List<PolicyCoverData> datas=null;
			if(isBasePolicy) {
				coverageTypes.add("B");
				coverageTypes.add("O");				 
			}else {
				coverageTypes.add("B");
				coverageTypes.add("O");
				coverageTypes.add("E");
			}
//			List<String>status = Arrays.asList("Y","D");
			datas= policyCoverRepo.findByQuoteNoAndStatusAndCoverageTypeIn(quoteNo,"Y",coverageTypes);
//			datas=policyCoverRepo.findByQuoteNoAndStatusInAndCoverageTypeIn(quoteNo,status,coverageTypes);
			Map<String, List<PolicyCoverData>> groupedRecords = groupRecordsByMultipleColumns(datas);
			List<PolicyCoverData> newData=new ArrayList<PolicyCoverData>();
			/*groupedRecords.forEach((col, data) -> {
	            
	            List<PolicyCoverData> collect = data.stream().filter(i -> i.getCoverageType().equals("E")).collect(Collectors.toList());
	            if(collect.isEmpty()) {
	            	collect=data.stream().filter(i -> (i.getCoverageType().equals("B") || i.getCoverageType().equals("O") || i.getCoverageType().equals("A"))).collect(Collectors.toList());
	            
	            }
	            newData.addAll(collect);	            
	        });*/
			
	/*		for (Entry<String, List<PolicyCoverData>> policyCoverData : groupedRecords.entrySet()) {
				List<PolicyCoverData> data = policyCoverData.getValue();
				 List<PolicyCoverData> collect = data.stream().filter(i -> i.getCoverageType().equals("E")).collect(Collectors.toList());
				 if(!collect.isEmpty()) {
					 List<PolicyCoverData> removedCovers = collect.stream().filter(i -> "D".equalsIgnoreCase(i.getStatus())).collect(Collectors.toList());
					 if(!removedCovers.isEmpty()) {
						 for(PolicyCoverData r:removedCovers) {
							 r.setSumInsured(r.getSumInsured().negate());
						 }
					 }
				 }
				 if(collect.isEmpty()) {
		            	collect=data.stream().filter(i -> (i.getCoverageType().equals("B") || i.getCoverageType().equals("O") || i.getCoverageType().equals("A"))).collect(Collectors.toList());
		            
		            }
				  newData.addAll(collect);	
			}*/
			

			for (Entry<String, List<PolicyCoverData>> policyCoverData : groupedRecords.entrySet()) {
				List<PolicyCoverData> data = policyCoverData.getValue();
				 List<PolicyCoverData> collect = data.stream().filter(i -> i.getCoverageType().equals("E")).collect(Collectors.toList());
				 if(collect.isEmpty()) {
		            	collect=data.stream().filter(i -> (i.getCoverageType().equals("B") || i.getCoverageType().equals("O") || i.getCoverageType().equals("A"))).collect(Collectors.toList());
		            
		            }
				  newData.addAll(collect);	
			}

			
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			newData.removeIf(t -> t.getCoverId().compareTo(Integer.valueOf("945"))==0);
			List<PolicyCoverDataEndt> mappedData = newData.stream().map( m->{ 
				PolicyCoverDataEndt map = dozerMapper.map(m, PolicyCoverDataEndt.class);
				map.setLocationId(m.getLocationId());
				map.setPolicyNo(StringUtils.isNotBlank(hpmData.getOriginalPolicyNo())?hpmData.getOriginalPolicyNo():hpmData.getPolicyNo());
				return map;
					
			}  ) .collect(Collectors.toList());
			
			policyCoverEndtRepo.saveAll(mappedData);
			
			if(!isBasePolicy) {
				String polNo = StringUtils.isNotBlank(hpmData.getOriginalPolicyNo())?hpmData.getOriginalPolicyNo():hpmData.getPolicyNo();
				List<PolicyCoverData> removedCovers=null;
				removedCovers= policyCoverRepo.findByQuoteNoAndStatusAndCoverageTypeIn(quoteNo,"D",coverageTypes);
				if(removedCovers!=null && !removedCovers.isEmpty()) {
					deleteRemovedRecords(polNo, removedCovers);
				}
			}
			
		}catch (Exception e) {
			
			e.printStackTrace();
		}
		return null;
		
	}
	 public  Map<String, List<PolicyCoverData>> groupRecordsByMultipleColumns(List<PolicyCoverData> records) {
	        return records.stream()
	            .collect(Collectors.groupingBy(record -> record.getCompanyId() 
	            		+ "-" + record.getProductId()
	            		+"-"+ record.getVehicleId()
	            		+"-"+ record.getSectionId()
	            		+"-"+ record.getCoverId() ));
	    }

		
		 @Transactional
		 public void deleteRemovedRecords(String policyNo, List<PolicyCoverData> pcd) {
			 try {
				 Map<String, List<PolicyCoverData>> groupedRecords = groupRecordsByMultipleColumns(pcd);
				 for (Entry<String, List<PolicyCoverData>> policyCoverData : groupedRecords.entrySet()) {
						List<PolicyCoverData> data = policyCoverData.getValue();
						 if(!data.isEmpty()) {
							 PolicyCoverData cover = data.get(0);
							 policyCoverEndtRepo.deleteAllByPolicyNoAndVehicleIdAndSectionIdAndCompanyIdAndProductIdAndCoverId(policyNo, cover.getVehicleId(), cover.getSectionId(), cover.getCompanyId(), cover.getProductId(), cover.getCoverId()); 
						 }
					}
			 }catch(Exception e) {
				 System.out.println(e.getMessage());
				 e.printStackTrace();
				
			 }
		 }
	 
	
}
