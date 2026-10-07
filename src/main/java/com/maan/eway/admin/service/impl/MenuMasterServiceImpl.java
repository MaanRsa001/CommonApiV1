package com.maan.eway.admin.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.admin.req.GetAllMenuReq;
import com.maan.eway.admin.req.GetMenuTypeReq;
import com.maan.eway.admin.req.MenuDetails;
import com.maan.eway.admin.req.MenuServiceReq;
import com.maan.eway.admin.req.UserTypeReq;
import com.maan.eway.admin.res.GetMenuTypeRes;
import com.maan.eway.admin.res.GetmenuDetailsRes2;
import com.maan.eway.admin.res.MenuDetailsRes;
import com.maan.eway.admin.res.MenuServiceRes;
import com.maan.eway.admin.service.MenuMasterService;
import com.maan.eway.auth.dto.Menu;
import com.maan.eway.bean.MenuMaster;
import com.maan.eway.repository.MenuMasterRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
@Transactional
public class MenuMasterServiceImpl implements MenuMasterService {

	@Autowired
	private MenuMasterRepository menurepo;
	
	

	@PersistenceContext
	private EntityManager em;

	private Logger log = LogManager.getLogger(MenuMasterService.class);

	@Override
	public MenuServiceRes menudisplay(MenuServiceReq req) {
		MenuServiceRes response = new MenuServiceRes();
		ModelMapper mapper = new ModelMapper();
		try {
			List<Menu> menus = new ArrayList<Menu>();

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MenuMaster> criteriaQuery = cb.createQuery(MenuMaster.class);
			// Find All
			Root<MenuMaster> m = criteriaQuery.from(MenuMaster.class);
			// Select
			criteriaQuery.select(m);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(m.get("menuId")));

			Predicate p1 = cb.equal(m.get("displayYn"), "Y");
			Predicate p2 = cb.equal(m.get("status"), "Y");
			Predicate p3 = cb.like(m.get("usertype"), "%" + req.getUserType() + "%");
             
			TypedQuery<MenuMaster> query = em.createQuery(criteriaQuery);
			List<MenuMaster> adminMenulist = new ArrayList<MenuMaster>();
			List<MenuMaster> otherMenulist = new ArrayList<MenuMaster>();

			if (req.getSubUserType().equalsIgnoreCase("both")) {
				criteriaQuery.where(p1, p2, p3).orderBy(orderList);
				query = em.createQuery(criteriaQuery);
				otherMenulist = query.getResultList();

				p3 = cb.like(m.get("usertype"), "admin" + "%");
				criteriaQuery.where(p1, p2, p3).orderBy(orderList);
				query = em.createQuery(criteriaQuery);
				adminMenulist = query.getResultList();

			} else if (req.getSubUserType().equalsIgnoreCase("low")) { // broker
				criteriaQuery.where(p1, p2, p3).orderBy(orderList);
				query = em.createQuery(criteriaQuery);
				otherMenulist = query.getResultList();

			} else if (req.getSubUserType().equalsIgnoreCase("high")) { // approver issuer
				p3 = cb.like(m.get("usertype"), "admin" + "%");
				criteriaQuery.where(p1, p2, p3).orderBy(orderList);
				query = em.createQuery(criteriaQuery);
				adminMenulist = query.getResultList();
			} else if (req.getSubUserType().equalsIgnoreCase("SuperAdmin")) {
				p3 = cb.like(m.get("usertype"), "%" + "SuperAdmin" + "%");
				criteriaQuery.where(p1, p2, p3).orderBy(orderList);
				query = em.createQuery(criteriaQuery);
				adminMenulist = query.getResultList();
			}

			else {
				criteriaQuery.where(p1, p2, p3).orderBy(orderList);
				query = em.createQuery(criteriaQuery);
				otherMenulist = query.getResultList();
			}

			List<Menu> userResList = new ArrayList<Menu>();
			List<Menu> userMenus = new ArrayList<Menu>();
			List<Menu> adminReslist = new ArrayList<Menu>();
			List<Menu> adminMenus = new ArrayList<Menu>();

			// User Menu List
			for (MenuMaster menuMaster : otherMenulist) {
				if(menuMaster.getCompanyId().equalsIgnoreCase(req.getInsuranceId())) {
					Menu menu = Menu.builder().title(menuMaster.getMenuName()).link(menuMaster.getMenuUrl())
							.id(menuMaster.getMenuId().toString()).parent(menuMaster.getParentMenu())
							.icon(menuMaster.getMenuLogo()).isdesti(false)
							.orderby(menuMaster.getDisplayOrder() == null ? 0 : menuMaster.getDisplayOrder().longValue())
							.build();
					userMenus.add(menu);
				}
			}
			if(userMenus.isEmpty()) {
				for (MenuMaster menuMaster : otherMenulist) {
					if(menuMaster.getCompanyId().equalsIgnoreCase("99999")) {
						Menu menu = Menu.builder().title(menuMaster.getMenuName()).link(menuMaster.getMenuUrl())
								.id(menuMaster.getMenuId().toString()).parent(menuMaster.getParentMenu())
								.icon(menuMaster.getMenuLogo()).isdesti(false)
								.orderby(menuMaster.getDisplayOrder() == null ? 0 : menuMaster.getDisplayOrder().longValue())
								.build();
						userMenus.add(menu);
					}
				}
			}
			if(userMenus.isEmpty()) {
				for (MenuMaster menuMaster : otherMenulist) {
					if(menuMaster.getCompanyId().equalsIgnoreCase("99999")) {
						Menu menu = Menu.builder().title(menuMaster.getMenuName()).link(menuMaster.getMenuUrl())
								.id(menuMaster.getMenuId().toString()).parent(menuMaster.getParentMenu())
								.icon(menuMaster.getMenuLogo()).isdesti(false)
								.orderby(menuMaster.getDisplayOrder() == null ? 0 : menuMaster.getDisplayOrder().longValue())
								.build();
						userMenus.add(menu);
					}
				}
			}
			List<Menu> collect = userMenus.stream().filter(i -> "99999".equals(i.getParent()))
					.collect(Collectors.toList());
			log.info("collect" + collect);
			for (Menu men : collect) {
				Menu menu = men;
				menu.setChildren(userMenus.stream()
						.filter(i -> (!"99999".equals(i.getParent()) && menu.getId().equals(i.getParent())))
						.collect(Collectors.toList()));
				userResList.add(menu);
			}

			// Admin Menu List
			for (MenuMaster menuMaster : adminMenulist) {
				if (menuMaster.getCompanyId().equalsIgnoreCase(req.getInsuranceId())) {
					Menu menu = Menu.builder().title(menuMaster.getMenuName()).link(menuMaster.getMenuUrl())
							.id(menuMaster.getMenuId().toString()).parent(menuMaster.getParentMenu())
							.icon(menuMaster.getMenuLogo()).isdesti(false)
							.orderby(menuMaster.getDisplayOrder() == null ? 0 : menuMaster.getDisplayOrder().longValue())
							.build();
					adminMenus.add(menu);
				}
			}
			
			if(adminMenus.isEmpty()) {
				for (MenuMaster menuMaster : adminMenulist) {
					if (menuMaster.getCompanyId().equalsIgnoreCase("99999")) {
						Menu menu = Menu.builder().title(menuMaster.getMenuName()).link(menuMaster.getMenuUrl())
								.id(menuMaster.getMenuId().toString()).parent(menuMaster.getParentMenu())
								.icon(menuMaster.getMenuLogo()).isdesti(false)
								.orderby(menuMaster.getDisplayOrder() == null ? 0 : menuMaster.getDisplayOrder().longValue())
								.build();
						adminMenus.add(menu);
					}
				}
			}
			
			List<Menu> collect2 = adminMenus.stream().filter(i -> "99999".equals(i.getParent()))
					.collect(Collectors.toList());
			log.info("collect" + collect2);
			for (Menu men : collect2) {
				Menu menu = men;
				menu.setChildren(adminMenus.stream()
						.filter(i -> (!"99999".equals(i.getParent()) && menu.getId().equals(i.getParent())))
						.collect(Collectors.toList()));
				adminReslist.add(menu);
			}
			List<Menu> menuReslist = new ArrayList<Menu>();
			if("both".equalsIgnoreCase(req.getSubUserType().toLowerCase())) {
				menuReslist.addAll(userResList);
				menuReslist.addAll(adminReslist);
			}else if("high".equalsIgnoreCase(req.getSubUserType().toLowerCase()) || "superadmin".equalsIgnoreCase(req.getSubUserType().toLowerCase())) {
				menuReslist.addAll(adminReslist);
			}else {
				menuReslist.addAll(userResList);
			}
			response.setMenuList(menuReslist);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return response;
	}

	public MenuDetailsRes savemenu(MenuDetails req) {

		MenuDetailsRes res = new MenuDetailsRes();
		List<MenuMaster> menuData = menurepo.findAll();
       List<UserTypeReq> usertypelist= req.getUsertypelist();
		Integer menuid = null;
		int count=0;
		
		String usertype="";
		try {
		
			
	    if (StringUtils.isBlank(req.getMenuId()) && req.getMenuId() == null) {
				menuid = generateMenuId();
				System.out.print("menu id------------" + menuid);
				
				
			} else {
				menuid = Integer.parseInt(req.getMenuId());
			}
		if(req.getInserttype().equalsIgnoreCase("Insert")) {
			MenuMaster savemenu = new MenuMaster();
			for(UserTypeReq usertypes : usertypelist)  //admin 
			{
			usertype=req.getUsertypelist().get(count).getUserType();
			savemenu.setMenuId(menuid);
            savemenu.setMenuName(StringUtils.isEmpty(req.getMenuName()) ? "null" : req.getMenuName());
			savemenu.setMenuUrl(StringUtils.isEmpty(req.getMenuUrl()) ? "null" : req.getMenuUrl());
			if (req.getMenuType().equalsIgnoreCase("parent")) {
				savemenu.setParentMenu("99999");
			} else {
				savemenu.setParentMenu(StringUtils.isBlank(req.getParentMenu()) ? null : req.getParentMenu());
			}
			savemenu.setProductId(StringUtils.isBlank(req.getProductId()) ? Integer.valueOf(99999): Integer.valueOf(req.getProductId()));
			savemenu.setStatus(StringUtils.isBlank(req.getStatus()) ? null : req.getStatus());
			savemenu.setUsertype(StringUtils.isBlank(usertype) ? null : usertype);
			savemenu.setDisplayOrder(StringUtils.isBlank(req.getDisplayOrder()) ? null : Integer.valueOf(req.getDisplayOrder()));
			savemenu.setEntryDate(req.getEntryDate()==null ? new Date():req.getEntryDate());
			savemenu.setCreatedBy(req.getCreatedBy());
			savemenu.setMenuLogo(StringUtils.isBlank(req.getMenulogo()) ? null : req.getMenulogo());
			savemenu.setDisplayYn(StringUtils.isBlank(req.getStatus()) ? null : req.getStatus());
			savemenu.setCompanyId(req.getCompanyId());
			savemenu.setMenuNameLocal(req.getCodeDescLocal());
			menurepo.saveAndFlush(savemenu);
			count++;
			}
			res.setResponse("Saved Successfully ");
			res.setSuccessId(menuid);
		   
		}
		
		  else if(req.getInserttype().equalsIgnoreCase("update")) {
			 List<MenuMaster> menumaster = menurepo.findByMenuIdAndCompanyId(menuid,req.getCompanyId());	
			  if(!menumaster.isEmpty())
			  {
			   //delete the row 
				  menurepo.deleteAll(menumaster);
				  System.out.println("Deleted succesfully...");
			  }
			   MenuMaster savemenu = new MenuMaster();
				for(UserTypeReq usertypes1 : usertypelist)  //admin 
				{
				usertype=usertypes1.getUserType();
				savemenu.setMenuId(menuid);
	            savemenu.setMenuName(StringUtils.isEmpty(req.getMenuName()) ? "null" : req.getMenuName());
				savemenu.setMenuUrl(StringUtils.isEmpty(req.getMenuUrl()) ? "null" : req.getMenuUrl());
				if (req.getMenuType().equalsIgnoreCase("parent")) {
					savemenu.setParentMenu("99999");
				} else {
					savemenu.setParentMenu(StringUtils.isBlank(req.getParentMenu()) ? null : req.getParentMenu());
				}
				savemenu.setProductId(StringUtils.isBlank(req.getProductId()) ? Integer.valueOf(99999): Integer.valueOf(req.getProductId()));
				savemenu.setStatus(StringUtils.isBlank(req.getStatus()) ? null : req.getStatus());
				savemenu.setUsertype(StringUtils.isBlank(usertype) ? null : usertype);
				savemenu.setDisplayOrder(StringUtils.isBlank(req.getDisplayOrder()) ? null : Integer.valueOf(req.getDisplayOrder()));
				savemenu.setEntryDate(req.getEntryDate()==null ? new Date():req.getEntryDate());
				savemenu.setCreatedBy(req.getCreatedBy());
				savemenu.setMenuLogo(StringUtils.isBlank(req.getMenulogo()) ? null : req.getMenulogo());
				savemenu.setDisplayYn(StringUtils.isBlank(req.getStatus()) ? null : req.getStatus());
				savemenu.setCompanyId(req.getCompanyId());
				savemenu.setMenuNameLocal(req.getCodeDescLocal());
				menurepo.saveAndFlush(savemenu);
				count++;
				}
			  
				  res.setResponse("Updated Successfully ");
					res.setSuccessId(menuid);
			  
		  }
		
		
		} catch (Exception ss) {
			System.out.println(" *********Exception********");
			ss.printStackTrace();
			log.info("Log Details" + ss.getMessage());
			return null;
			
		}
		return res;

	}
	
	
	public List<String> validateMenuDetails(MenuDetails req) {
		List<String> errorList = new ArrayList<>();
		try {
		       if(StringUtils.isBlank(req.getMenuName()))
		       {
		    	   errorList.add("2213");
		       }
		       
		       if(StringUtils.isBlank(req.getMenuUrl()))
		       {
		    	   errorList.add("2214");  
		       }
		     
	           if(StringUtils.isBlank(req.getMenuType()))
	           {
	        	   errorList.add("2215");  
	           }
	           if(!StringUtils.isBlank(req.getMenuType()))
	           {
	        	 if(req.getParentMenu()==null || req.getParentMenu().isBlank() )
	        	 {
	        		 errorList.add("2216");   
	        	 }
	           }
	           if( StringUtils.isBlank(req.getStatus()))
	           {
	        	   errorList.add("2217");  
	           }
	           
	           if( StringUtils.isBlank(req.getCompanyId()))
		       {
		    	   errorList.add("2220");
		       }
				
			   if(req.getUsertypelist() == null || req.getUsertypelist().isEmpty()) {
				   errorList.add("2218"); 
			   }else {
				   for(UserTypeReq user : req.getUsertypelist()) {
					   if(StringUtils.isBlank(user.getUserType())) {
						   errorList.add("2218"); 
					   }
				   }
			   }
				 
			
		}catch(Exception Problem)
		{
			System.out.println("*****Exception in validating the menu master details :) *****");
			Problem.printStackTrace();
			 errorList.add("2219");  
			log.info("Exception Details----->"+Problem.getMessage());
			return null;
		}
		return errorList;
	}
	public Set<GetmenuDetailsRes2> getAllMenuList(GetAllMenuReq req)
	{
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();

        Set<GetmenuDetailsRes2> resList = new HashSet<GetmenuDetailsRes2>(); 
		//List<MenuDetails> resList = new ArrayList<MenuDetails>();
		try {
			
			boolean a=true;
			if(req.getMenuId()==null || req.getMenuId().isBlank() ||req.getGetType()==null || req.getGetType().isBlank())
			{
			a=false;	
			}
			if(req.getGetType().equalsIgnoreCase("getById") && a)
			{
			Integer menuid=Integer.valueOf(req.getMenuId());	
		     List<MenuMaster> menumaster = menurepo.findByMenuIdAndCompanyId(menuid,req.getCompanyId());
		    if(!menumaster.isEmpty()) {
		     List<UserTypeReq> u1= new ArrayList<>();
		     for(MenuMaster mm: menumaster)
		     {
		    	 UserTypeReq usertypelist= new UserTypeReq(); 
		    	 if(mm.getMenuId().equals(menuid)) {
		    	 usertypelist.setUserType(mm.getUsertype());
		    	 u1.add(usertypelist);
		    	 }
		     }
		     
		     GetmenuDetailsRes2 res = dozerMapper.map(menumaster.get(0), GetmenuDetailsRes2.class);
		     res.setUsertypelist(u1);
		     res.setMenuType(menumaster.get(0).getParentMenu().equalsIgnoreCase("99999")?"Parent": "Child");
		     res.setCodeDescLocal(menumaster.get(0).getMenuNameLocal());
		    
		     resList.add(res);
		    } 
			}
			else {
		    List<MenuMaster> menumaster = menurepo.findAll();
	        Integer start=req.getLimit();
	         for(MenuMaster mm: menumaster)
		     {
	        	Date date=mm.getEntryDate()==null? null :mm.getEntryDate(); // Your date string from mm.getEntryDate()
	        	String entrydate=String.valueOf(date);
	        	 String parentmenu=mm.getParentMenu()==null ? "99999" :mm.getParentMenu();
	        	 String menutype="";
		    	 UserTypeReq usertypelist= new UserTypeReq();
			     List<UserTypeReq> u1= new ArrayList<>();
			     GetmenuDetailsRes2 res = new GetmenuDetailsRes2();
		    	
		    	 if(parentmenu.equalsIgnoreCase("99999"))
		    	 {
		    		 menutype="Parent";
		    	 }
		    	 else{
		    		 menutype="Child"; 
		    	 }
		    	 if(start>req.getOffset()){break;}
		    	 usertypelist.setUserType(mm.getUsertype());
		    	 u1.add(usertypelist);
		    	 res = dozerMapper.map(mm, GetmenuDetailsRes2.class);
		         res.setUsertypelist(u1);
		         res.setMenuType(menutype);
		         res.setEntryDate(entrydate.isBlank()?null:entrydate);
		         res.setCodeDescLocal(mm.getMenuNameLocal());
		         resList.add(res);
		         start++;
		     }
		   
			}
		}catch(Exception ss)
		{
			System.out.println(" *********Exception********");
			ss.printStackTrace();
			log.info("Log Details" + ss.getMessage());
			return null;
		}
		return resList;

	}

	@Override
	public List<GetMenuTypeRes> getByUserType(GetMenuTypeReq req0) {
		
		List<GetMenuTypeRes> resList = new ArrayList<GetMenuTypeRes>();
		List<MenuMaster> getbyusertype = new ArrayList<MenuMaster>();
		try {
			List<String> req1=req0.getUsertype();
			for(String req:req1)
			{
			String usertype=req;
			getbyusertype = menurepo.findByParentMenuAndUsertypeAndStatusAndDisplayYnAndCompanyId("99999",req,"Y","Y",req0.getCompanyId());		
			
			for (MenuMaster data : getbyusertype) {
				GetMenuTypeRes res = new GetMenuTypeRes();
				res.setMenuId(data.getMenuId().toString());
				res.setMenuName(data.getMenuName());
				res.setUsertype(data.getUsertype());
	            resList.add(res);
	        }
			}
			
		}catch(Exception prob)
		{
			prob.printStackTrace();
			return null;
		}
		// TODO Auto-generated method stub
		return resList;
	}
	public synchronized int generateMenuId() {
		try {
			int nextMenuId = 1; // Initialize the next menu ID
			boolean isUnique = false;

			while (!isUnique) {
				// Retrieve the last menu ID from the database
				Optional<MenuMaster> lastMenuOptional = menurepo.findFirstByOrderByMenuIdDesc();

				// If a record with menu ID exists, increment the menu ID
				if (lastMenuOptional.isPresent()) {
					MenuMaster lastMenu = lastMenuOptional.get();
					nextMenuId = lastMenu.getMenuId() + 1;
				}

				// Check if the generated menu ID already exists
				Optional<MenuMaster> existingMenuOptional = menurepo.findByMenuId(nextMenuId);
				if (!existingMenuOptional.isPresent()) {
					isUnique = true; // Exit the loop if the menu ID is unique
				}
			}

			// Return the unique menu ID
			return nextMenuId;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return -1; // Return -1 or any other default value in case of an exception
		}
	}



}
