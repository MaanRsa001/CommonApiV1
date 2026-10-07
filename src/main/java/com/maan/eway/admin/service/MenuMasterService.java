package com.maan.eway.admin.service;

import java.util.List;
import java.util.Set;

import com.maan.eway.admin.req.GetAllMenuReq;
import com.maan.eway.admin.req.GetMenuTypeReq;
import com.maan.eway.admin.req.MenuDetails;
import com.maan.eway.admin.req.MenuServiceReq;
import com.maan.eway.admin.res.GetMenuTypeRes;
import com.maan.eway.admin.res.GetmenuDetailsRes2;
import com.maan.eway.admin.res.MenuDetailsRes;
import com.maan.eway.admin.res.MenuServiceRes;

public interface MenuMasterService {

	MenuServiceRes menudisplay(MenuServiceReq req);

	MenuDetailsRes savemenu(MenuDetails req);

	Set<GetmenuDetailsRes2> getAllMenuList(GetAllMenuReq req);

	List<GetMenuTypeRes> getByUserType(GetMenuTypeReq req);
	
	 List<String> validateMenuDetails(MenuDetails req);
	 
	

}
