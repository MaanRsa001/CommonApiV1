package com.maan.eway.admin.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.admin.service.AdminDropDownService;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.res.DropDownRes;

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
public class AdminDropDownServiceImpl  implements AdminDropDownService{


	private Logger log = LogManager.getLogger(AdminDropDownServiceImpl.class);

	
	@Autowired
	private ListItemValueRepository listRepo;
	
	@Autowired
	private LoginMasterRepository loginRepo ;
	
	@PersistenceContext
	private EntityManager em;
	
	// Gender
		@Override
		public List<DropDownRes> getgender(LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
				// List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("GENDER", "Y");
				String itemType = "GENDER" ;
				List<ListItemValue> list  = getListItem(req , itemType,req.getInsuranceId()  );
				
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					resList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;
		}
		
		private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
		    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
		}

	

		@Override
		public List<DropDownRes> getConstMaterial(LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
			//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("CONST_MATERIAL", "Y");
				String itemType = "CONSTRUCT_TYPE";
				List<ListItemValue> list  = getListItem(req , itemType, req.getInsuranceId());
				
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					resList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;
		}

		@Override
		public List<DropDownRes> getOutbuildingConst(LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
			//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("OUTBUILDING_CONST", "Y");
				String itemType = "OUTBUILDING_CONST";
				List<ListItemValue> list  = getListItem(req , itemType, req.getInsuranceId());
				
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					resList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;	
			
		}

		@Override
		public List<DropDownRes> getAboutBuilding(LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
			//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("ABOUT_BUILDING", "Y");
				String itemType = "ABOUT_BUILDING";
				List<ListItemValue> list  = getListItem(req , itemType, req.getInsuranceId());
				
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					resList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;
		}

		@Override
		public List<DropDownRes> getStateExtent(LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
			//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("STATE_EXTENT", "Y");
				String itemType = "STATE_EXTENT";
				List<ListItemValue> list  = getListItem(req , itemType, req.getInsuranceId());
				
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					resList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;
		}
		
		@Override
		public List<DropDownRes> getContentName(LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
				//List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("CONTENT_NAME", "Y");
				String itemType = "CONTENT_NAME";
				List<ListItemValue> list  = getListItem(req , itemType, req.getInsuranceId());
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					resList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;
		}
		
		@Override
		public List<DropDownRes> getPropertyName(LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
			//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("PROPERTY_NAME", "Y");
				String itemType = "PROPERTY_NAME";
				List<ListItemValue> list  = getListItem(req , itemType, req.getInsuranceId());
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					resList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;
		}
	

		@Override
		public List<DropDownRes> getMobileCodes( LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
			//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("MOBILE_CODE", "Y");
				String itemType = "MOBILE_CODE";
				List<ListItemValue> list  = getListItem(req , itemType, req.getInsuranceId());		
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					res.setCodeDescLocal(data.getItemValueLocal());
					resList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;
		}
		@Override
		public List<DropDownRes> getBusinessType(LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
			//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("BUSINESS_TYPE", "Y");
				String itemType = "BUSINESS_TYPE";
				List<ListItemValue> list  = getListItem(req , itemType, req.getInsuranceId()!=null?req.getInsuranceId():"99999");
				if(list==null || list.size()==0) {
					
					list = getListItem(req, itemType,"99999");
				}
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					res.setCodeDescLocal(data.getItemValueLocal());
					resList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;
		}
		@Override
		public List<DropDownRes> getSourceType(LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
			//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByParam2Asc(req.getUserType(), "Y");
				String itemType = "Broker" ;
				LovDropDownReq req2 = new LovDropDownReq();
				req2.setBranchCode(req.getBranchCode());
				req2.setInsuranceId(req.getInsuranceId());
				List<ListItemValue> list  = getListItem(req2 , itemType, "99999");
			
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					resList.add(res);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;
		}

		
		public synchronized List<ListItemValue> getListItem(LovDropDownReq req , String itemType, String companyId) {
			List<ListItemValue> list = new ArrayList<ListItemValue>();
			try {
				Date today = new Date();
				Calendar cal = new GregorianCalendar();
				cal.setTime(today);
				today = cal.getTime();
				Date todayEnd = cal.getTime();
				
				// Criteria
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ListItemValue> query=  cb.createQuery(ListItemValue.class);
				// Find All
				Root<ListItemValue> c = query.from(ListItemValue.class);
				
				//Select
				query.select(c);
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(c.get("branchCode")));
				
				
				// Effective Date Start Max Filter
				Subquery<Date> effectiveDate = query.subquery(Date.class);
				Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
				effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
				Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
				Predicate x3 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
				Predicate x4 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				effectiveDate.where(a1,a2,x3,x4);
				// Effective Date End Max Filter
				Subquery<Date> effectiveDate2 = query.subquery(Date.class);
				Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
				effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
				Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
				Predicate x1 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
				Predicate x2 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
				Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
				effectiveDate2.where(a3,a4,x1,x2);
							
				// Where
				Predicate n1 = cb.equal(c.get("status"),"Y");
				Predicate n11 = cb.equal(c.get("status"),"R");
				Predicate n12 = cb.or(n1,n11);
				Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
				Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
				Predicate n4 = cb.equal(c.get("companyId"), companyId);
			//	Predicate n5 = cb.equal(c.get("companyId"), "99999");
				Predicate n6 = cb.equal(c.get("branchCode"), req.getBranchCode());
				Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			//	Predicate n8 = cb.or(n4,n5);
				Predicate n9 = cb.or(n6,n7);
				Predicate n10 = cb.equal(c.get("itemType"),itemType);
				
				
				//Not company based 99999 setup
//				if(itemType.equalsIgnoreCase("TAX_FOR_DESC") ||itemType.equalsIgnoreCase("TERMS_TYPE")
//						||itemType.equalsIgnoreCase("POLICY_HOLDER_TYPE") ||
//						itemType.equalsIgnoreCase("CALCULATION_TYPE") || itemType.equalsIgnoreCase("COVERAGE_TYPE") || 
//						itemType.equalsIgnoreCase("PRODUCT_CATEGORY") || 
//						itemType.equalsIgnoreCase("USER_TYPE") || itemType.equalsIgnoreCase("Broker") || 
//						itemType.equalsIgnoreCase("USER") || itemType.equalsIgnoreCase("ISSUER") || 
//						itemType.equalsIgnoreCase("PRODUCT_ICONS") || 
//						itemType.equalsIgnoreCase("DOCUMENT_APPLICABLE") || itemType.equalsIgnoreCase("INDUSTRY_CATEGORY") || 
//						itemType.equalsIgnoreCase("RANGE") || itemType.equalsIgnoreCase("IS_TAX_EXEMPTED") || 
//						itemType.equalsIgnoreCase("DISCRETE") || itemType.equalsIgnoreCase("POLICY_HOLDER_TYPE") || 
//						itemType.equalsIgnoreCase("POLICY_HOLDER_ID_TYPE") || 
//						itemType.equalsIgnoreCase("PRODUCT_SHORT_CODE") || 
//						itemType.equalsIgnoreCase("PAYMENT_TYPES") || itemType.equalsIgnoreCase("NOTIFICATION_TYPE") || 
//						itemType.equalsIgnoreCase("TERMS_AND_CONDITION") || itemType.equalsIgnoreCase("TITLE") || 
//						itemType.equalsIgnoreCase("COPY_QUOTE_BY_BUILDING") || itemType.equalsIgnoreCase("BUSINESS_TYPE") || 
//						itemType.equalsIgnoreCase("COPY_QUOTE_BY_COMMON") || itemType.equalsIgnoreCase("SOURCE_TYPE") || 
//						itemType.equalsIgnoreCase("PROMOCODE_TYPE") || itemType.equalsIgnoreCase("COPY_QUOTE_BY_MOTOR") ||
//						
//						itemType.equalsIgnoreCase("COPY_QUOTE_BY_TRAVEL") ||
//						itemType.equalsIgnoreCase("COPY_QUOTE_BY_BUILDING") || itemType.equalsIgnoreCase("COPY_QUOTE_BY_COMMON") || 
//						itemType.equalsIgnoreCase("TERMS_TYPE") || itemType.equalsIgnoreCase("PROMOCODE_TYPE") || 
//						itemType.equalsIgnoreCase("TRACKING_STATUS") || itemType.equalsIgnoreCase("ADMIN_SEARCH_MOTOR") || 
//						itemType.equalsIgnoreCase("ADMIN_SEARCH_BUILDING") || itemType.equalsIgnoreCase("ADMIN_SEARCH_TRAVEL") || 
//						itemType.equalsIgnoreCase("ADMIN_SEARCH_COMMON") || itemType.equalsIgnoreCase("COPY_QUOTE_BY_MOTOR") || 
//						itemType.equalsIgnoreCase("COPY_QUOTE_BY_TRAVEL") || itemType.equalsIgnoreCase("COPY_QUOTE_BY_BUILDING") || 
//						itemType.equalsIgnoreCase("COPY_QUOTE_BY_COMMON") || itemType.equalsIgnoreCase("DOC_ID_TYPE") || 
//						itemType.equalsIgnoreCase("TAX_FOR") || itemType.equalsIgnoreCase("PAYMENT") || 
//						itemType.equalsIgnoreCase("PORTFOLIO_TYPES") || itemType.equalsIgnoreCase("TAX_FOR_DESC") || 
//						itemType.equalsIgnoreCase("MONTHS") ) {
					
//					query.where(n12,n2,n3,n8,n9,n10).orderBy(orderList);
//				}else {
//					
					query.where(n12,n2,n3,n4,n9,n10).orderBy(orderList);
				//}
				
				// Get Result
				TypedQuery<ListItemValue> result = em.createQuery(query);
				list = result.getResultList();
				
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getItemCode()))).collect(Collectors.toList());
				list.sort(Comparator.comparing(ListItemValue :: getItemValue));
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return list ;
		}

		@Override
		public List<DropDownRes> getCommissionType(LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
			//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("BUSINESS_TYPE", "Y");
				String itemType = "COMMISSION_TYPE";
				List<ListItemValue> list  = getListItem(req , itemType, req.getInsuranceId());
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					resList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;
		}

		@Override
		public List<DropDownRes> getProRataType(LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
			//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("BUSINESS_TYPE", "Y");
				String itemType = "PRO_RATA_TYPE";
				List<ListItemValue> list  = getListItem(req , itemType, "99999");
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					resList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;
		}

	

}
