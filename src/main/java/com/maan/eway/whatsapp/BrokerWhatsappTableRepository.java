package com.maan.eway.whatsapp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BrokerWhatsappTableRepository extends JpaRepository<BrokerWhatsappTable, BrokerWhatsappTableId>{

	BrokerWhatsappTable findByWhatsappNoAndStatus(String whatsappNo,String status);

	List<BrokerWhatsappTable> findByLoginId(String loginId);

	BrokerWhatsappTable findByMobileNo(String whatsappNo);

	BrokerWhatsappTable findBySnoAndLoginId(Long sNo, String loginId);

}
