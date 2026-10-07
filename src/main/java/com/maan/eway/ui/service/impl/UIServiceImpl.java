package com.maan.eway.ui.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.CompanyUiBaseinfo;
import com.maan.eway.bean.CompanyUiFieldgroup;
import com.maan.eway.repository.CompanyUiBaseinfoRepository;
import com.maan.eway.repository.CompanyUiDatatypesRepository;
import com.maan.eway.repository.CompanyUiFieldgroupRepository;
import com.maan.eway.ui.request.Field;
import com.maan.eway.ui.request.GroupSetup;
import com.maan.eway.ui.request.LayoutSetup;
import com.maan.eway.ui.request.Props;
import com.maan.eway.ui.request.Ui;
import com.maan.eway.ui.service.UIService;
@Service
public class UIServiceImpl implements UIService {

	
	@Autowired
	private CompanyUiBaseinfoRepository uiBaseRepo;
	
	@Autowired
	private CompanyUiFieldgroupRepository uiFieldRepo;
	
	@Autowired
	private CompanyUiDatatypesRepository uiDataRepo;
	
	@Override
	public GroupSetup buildUIFromTable(Ui request) {
		
		try {
			
			CompanyUiBaseinfo uib=	uiBaseRepo.findByCompanyidAndProductid(request.getCompanyName(),request.getProductName());
			if(uib!=null) {
				
				String uiSplitType = uib.getUiSplitType();
				GroupSetup g=null;
				 if("Stepper".equalsIgnoreCase(uiSplitType)) {
					 g=GroupSetup.builder().type("stepper").build();
				 }else if("Tab".equalsIgnoreCase(uiSplitType)) {
					 g=GroupSetup.builder().type("tabs").build();
				 }else {
					 g=GroupSetup.builder().type(null).build();
				 }
				 
				 
				
				List<CompanyUiFieldgroup> uif=uiFieldRepo.findByUiId(uib.getUiId());
				if(!uif.isEmpty()) {
					
					List<CompanyUiFieldgroup> heads = uif.stream().filter(i -> i.getUiParent().doubleValue()==99999D).collect(Collectors.toList());
				List<LayoutSetup> ls=new ArrayList<LayoutSetup>();
				 for (CompanyUiFieldgroup hField : heads) {
					 
					 LayoutSetup l=null;
					 if("Layout".equalsIgnoreCase(uib.getUiDesigntype())) {
						 l=LayoutSetup.builder().fieldGroupClassName("row").build();
					 }else {
						 l=LayoutSetup.builder().fieldGroupClassName(null).build();
					 }
					 
					 List<CompanyUiFieldgroup> child = uif.stream().filter(i -> i.getUiParent().doubleValue()==hField.getUiGroupid().doubleValue()).collect(Collectors.toList());
					 l.setProps(Props.builder().label(hField.getUiLabel()).build());
					 
					 List<Field> fieldGroups=new ArrayList<Field>();					 
					 for(CompanyUiFieldgroup c :child) 
						 fieldGroups.add(c.getUiJson());
					 	
					 l.setFieldGroup(fieldGroups);
					  
					 ls.add(l);
				 }
				 
				 g.setFieldGroup(ls);
				}
				return g;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}

}
