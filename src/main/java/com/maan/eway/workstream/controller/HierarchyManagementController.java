/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DropdownCommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.workstream.request.HierarchyLevelDropdownReq;
import com.maan.eway.workstream.request.HierarchyManagementGetReq;
import com.maan.eway.workstream.request.HierarchyManagementSaveReq;
import com.maan.eway.workstream.response.HierarchyRes;
import com.maan.eway.workstream.serviceimpl.HierarchyManagementServiceImpl;

@RestController
@RequestMapping("/hierarchy")
public class HierarchyManagementController {	
	private HierarchyManagementServiceImpl hierarchyService;

	@Autowired
	public HierarchyManagementController(HierarchyManagementServiceImpl hierarchyService) {
		this.hierarchyService = hierarchyService;
	}

	
	/**
	 * Handles the POST request to save all hierarchy management data.
	 *
	 * @param req the request body containing hierarchy management details. 
	 *            Includes the parameters necessary for validation and saving the hierarchy data.
	 * @return ResponseEntity containing:
	 *         <ul>
	 *           <li>A success response with a status message if the hierarchy data is saved successfully.</li>
	 *           <li>A failure response with validation errors if input parameters are invalid.</li>
	 *           <li>A conflict response if the hierarchy management already exists.</li>
	 *           <li>A bad request response if saving fails due to any other issue.</li>
	 *         </ul>
	 */
	@PostMapping("/saveall")
	public ResponseEntity<?> saveAllHierarchyManagement(@RequestBody HierarchyManagementSaveReq req){
		CommonRes response = new CommonRes();
		List<Error> validation = hierarchyService.validateParametersForHierarchySaveReq(req);
		if(!validation.isEmpty()) {
			response.setMessage("failed");
			response.setIsError(true);
			response.setErrorMessage(validation);
			return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		
		SuccessRes savedResponse = hierarchyService.saveAllHierarchyManagement(req);
		if(savedResponse == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
		response.setMessage("success");
		response.setIsError(false);
		response.setCommonResponse(savedResponse);
		return new ResponseEntity<>(response, HttpStatus.OK);
					
	}
	
	
	/**
	 * Handles the POST request to retrieve all hierarchy level information.
	 *
	 * @param req the request body containing the details for retrieving hierarchy levels.
	 *            Includes company ID and product ID for identifying the hierarchy data.
	 * @return ResponseEntity containing:
	 *         <ul>
	 *           <li>A success response with the list of hierarchy levels if validation passes and data is found.</li>
	 *           <li>A failure response with validation errors if input parameters are invalid.</li>
	 *           <li>A success response with an empty list if no hierarchy levels are found.</li>
	 *         </ul>
	 */
	@PostMapping("/getall")
	public ResponseEntity<?> getAllHierarchyLevelInfo(@RequestBody HierarchyManagementGetReq req){
		CommonRes response = new CommonRes();
		
		List<Error> validation = hierarchyService.validateParametersForHierarchyGetReq(req);		
		if(!validation.isEmpty()) {
			response.setMessage("failed");
			response.setIsError(true);
			response.setErrorMessage(validation);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
		
		List<HierarchyRes> hierarchyLevels = hierarchyService.getAllHierarchyManagement(req.getCompanyId(), req.getProductId());
		response.setMessage("success");
		response.setIsError(false);
		response.setCommonResponse(hierarchyLevels);
		
		return new ResponseEntity<>(response, HttpStatus.OK);		
	}
	
	@PostMapping("/dropdown")
	public ResponseEntity<DropdownCommonRes> hierarchyLevelDropdown(@RequestBody HierarchyLevelDropdownReq req){
		DropdownCommonRes data = new DropdownCommonRes();
		
		List<DropDownRes> res = hierarchyService.getHierarchyLevelDropdown(req.getCompanyId(),req.getItemType());
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<DropdownCommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

}
