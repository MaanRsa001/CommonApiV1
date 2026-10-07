package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.maan.eway.bean.PositionMaster;

import jakarta.transaction.Transactional;

@Transactional
@Repository
public interface PositionMasterRepository extends JpaRepository<PositionMaster,Long>{

	List<PositionMaster> findByQuoteno(Long quoteno);
	List<PositionMaster> findByPolicynoAndStatusIn(String policyno,List<String> status);
	List<PositionMaster> findByQuotenoAndStatusIn(Long quoteNo,List<String> status);
	List<PositionMaster> findByApplicationno(Long appNo);
	List<PositionMaster> findByPolicyno(String searchValue);
	List<PositionMaster> findBycustomerid(Long custId);
	int countByApplicationnoAndEndtstatus(Long applicationNo, String status);         
	
	List<PositionMaster> findByApplicationnoOrderByAmendidDesc(Long appNo);
	
	List<PositionMaster> findByOpencoverno(String opencoverno);
	List<PositionMaster> findByOpencovernoAndStatus(String opencoverno, String string);
	int countByApplicationno(Long applicationNo);
	List<PositionMaster> findByQuotenoAndAmendid(Long valueOf, Long valueOf2);
	int countByVerificationcode(Long valueOf);
	Long countByPolicynoAndQuotenoNot(String policyNo, Long valueOf);
	
	List<PositionMaster> findByLoginidInAndProductidAndPolicynoAndStatusAndOpencoverintstatusNotInAndOriginalPolicyNoIsNull(List<String> loginid,Long productid,String policyno,String status,List<String> opencoverintstatus);
	
	List<PositionMaster> findByQuotenoAndFreightStatusAndRemarksInAndApplicationno(Long quoteno,String freightStatus,List<String> remarks,Long applicationno);
	int countByQuotenoAndAmendid(Long valueOf, Long valueOf2);
	List<PositionMaster> findByQuotenoIn(List<Long> quoteno);
	List<PositionMaster> findByQuotenoInAndRemarks(List<Long> quoteno,String remarks);
	List<PositionMaster> findByQuotenoInAndQuotenoNot(List<Long> quoteno,Long Quoteno);
	List<PositionMaster> findAllByOrderByTranIdDesc();
	List<PositionMaster> findByQuotenoOrderByAmendidDesc(Long valueOf);
	List<PositionMaster> findByQuotenoAndBranchCode(Long valueOf, String branchCode);
	List<PositionMaster> findByOpencovernoAndStatusNot(String originalopencover, String string);

	List<PositionMaster> findByQuotenoAndPolicynoIsNotNull(Long valueOf); 

	int countByQuotenoAndStatus(Long valueOf, String string);
	List<PositionMaster> findByQuotenoAndStatus(Long long1, String string);
	@Modifying
	@Transactional
	void deleteByQuotenoAndStatusAndAmendid(Long valueOf, String string, Long valueOf2);
	List<PositionMaster> findByOriginalPolicyNo(String originalPolicyNo);
	int countByMissippiopencovernoAndStampDutyYNAndPolicynoIsNotNull(String openCoverPolicyNo, String string); 

}
  