package com.maan.eway.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;

import com.maan.eway.bean.WntCustDtl;

import jakarta.transaction.Transactional;

public interface WntCustDtlRepository extends JpaRepository<WntCustDtl, Long> {

	Optional<WntCustDtl> findByCode(String code);

	@Modifying
	@Transactional
	@Query("UPDATE WntCustDtl w SET " + "w.corporate = :corporate, " + "w.credit = :credit, "
			+ "w.gridGlLedgerStatus = :gridGlLedgerStatus, " + "w.subGlobalLedgerStatus = :subGlobalLedgerStatus, "
			+ "w.groupLimit = :groupLimit, " + "w.groupId = :groupId, " + "w.nameAr = :nameAr, "
			+ "w.nameEn = :nameEn, " + "w.partyType = :partyType, " + "w.glGroupLabel = :glGroupLabel, "
			+ "w.glGroupValue = :glGroupValue, " + "w.glLabel = :glLabel, " + "w.glValue = :glValue, "
			+ "w.parentPartyLabel = :parentPartyLabel, " + "w.parentPartyValue = :parentPartyValue, "
			+ "w.glCompLabel = :glCompLabel, " + "w.glCompValue = :glCompValue, " + "w.glCurrLabel = :glCurrLabel, "
			+ "w.glCurrValue = :glCurrValue, " + "w.custReqRespStatus = :custReqRespStatus, "
			+ "w.custReqSentDt = :custReqSentDt, " + "w.custReqMessage = :custReqMessage, "
			+ "w.custResRecdDt = :custResRecdDt, " + "w.custRespMessage = :custRespMessage, "
			+ "w.custFinIntgStatus = :custFinIntgStatus, " + "w.custFinIntgRefNo = :custFinIntgRefNo, "
			+ "w.ledgerType = :ledgerType " + "WHERE w.code = :code")
	void updateWntCustDtl(@Param("code") String code, @Param("corporate") Boolean corporate,
			@Param("credit") Boolean credit, @Param("gridGlLedgerStatus") Boolean gridGlLedgerStatus,
			@Param("subGlobalLedgerStatus") Boolean subGlobalLedgerStatus, @Param("groupLimit") Double groupLimit,
			@Param("groupId") Long groupId, @Param("nameAr") String nameAr, @Param("nameEn") String nameEn,
			@Param("partyType") String partyType, @Param("glGroupLabel") String glGroupLabel,
			@Param("glGroupValue") String glGroupValue, @Param("glLabel") String glLabel,
			@Param("glValue") String glValue, @Param("parentPartyLabel") String parentPartyLabel,
			@Param("parentPartyValue") String parentPartyValue, @Param("glCompLabel") String glCompLabel,
			@Param("glCompValue") String glCompValue, @Param("glCurrLabel") String glCurrLabel,
			@Param("glCurrValue") String glCurrValue, @Param("custReqRespStatus") Long custReqRespStatus,
			@Param("custReqSentDt") Date custReqSentDt, @Param("custReqMessage") String custReqMessage,
			@Param("custResRecdDt") Date custResRecdDt, @Param("custRespMessage") String custRespMessage,
			@Param("custFinIntgStatus") String custFinIntgStatus, @Param("custFinIntgRefNo") String custFinIntgRefNo,
			@Param("ledgerType") String ledgerType);

	Optional<WntCustDtl> findByCodeAndCompanyId(String polCustCode, String companyId);
}
