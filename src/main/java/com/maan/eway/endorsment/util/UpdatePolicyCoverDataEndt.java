package com.maan.eway.endorsment.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.repository.PolicyCoverDataEndtRepository;

@Service
public class UpdatePolicyCoverDataEndt {
//	
//	@Autowired
//	CopyPolicyCoverData copyService;
//	
//	@Autowired
//	private PolicyCoverDataEndtRepository policyCoverEndtRepo;
//	
//	 @Transactional
//	 public void deleteRemovedRecords(String policyNo, List<PolicyCoverData> pcd) {
//		 try {
//			 Map<String, List<PolicyCoverData>> groupedRecords = copyService.groupRecordsByMultipleColumns(pcd);
//			 for (Entry<String, List<PolicyCoverData>> policyCoverData : groupedRecords.entrySet()) {
//					List<PolicyCoverData> data = policyCoverData.getValue();
//					 if(!data.isEmpty()) {
//						 PolicyCoverData cover = data.get(0);
//						 policyCoverEndtRepo.deleteAllByPolicyNoAndVehicleIdAndSectionIdAndCompanyIdAndProductIdAndCoverId(policyNo, cover.getVehicleId(), cover.getSectionId(), cover.getCompanyId(), cover.getProductId(), cover.getCoverId()); 
//					 }
//				}
//		 }catch(Exception e) {
//			 System.out.println(e.getMessage());
//			 e.printStackTrace();
//			
//		 }
//	 }

}
