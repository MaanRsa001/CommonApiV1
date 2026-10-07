package com.maan.eway.salesLead.Repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.bean.IplcmsListItemValue;

@Repository
public interface IplcmsListItemValueRepository extends JpaRepository<IplcmsListItemValue, Integer> {

	List<IplcmsListItemValue> findByItemType(String itemType);

	List<IplcmsListItemValue> findByItemTypeAndParam1(String itemType, String param1);
}
