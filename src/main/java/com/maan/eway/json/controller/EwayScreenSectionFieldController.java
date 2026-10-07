package com.maan.eway.json.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.json.dto.AdminControllersRes;
import com.maan.eway.json.dto.EwayFieldRequest;
import com.maan.eway.json.dto.EwayScreenSection;
import com.maan.eway.json.dto.EwayScreenSectionFieldResponse;
import com.maan.eway.json.servicee.EwayScreenSectionFieldService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/eway/fields")
public class EwayScreenSectionFieldController {

	@Autowired
    private EwayScreenSectionFieldService service;

    @PostMapping("/create")
    public ResponseEntity<CommonRes> create(@Valid @RequestBody EwayFieldRequest request) {
    	 CommonRes res = new CommonRes();
           try {
             EwayScreenSectionFieldResponse response = service.create(request);
             res.setCommonResponse(response);
             res.setIsError(false);
             res.setErrorMessage(Collections.emptyList());
             res.setMessage("Created Successfully");
             return new ResponseEntity<>(res, HttpStatus.CREATED);
         } catch (Exception e) {
             res.setCommonResponse(null);
             res.setIsError(true);
             res.setMessage("Failed to create");
             return new ResponseEntity<>(res, HttpStatus.OK);
         }
     }

    @PostMapping("/update/{sno}/{screenId}")
    public ResponseEntity<CommonRes> update(
            @PathVariable Integer sno,
            @PathVariable Integer screenId,
            @Valid @RequestBody EwayFieldRequest request) {

    	 CommonRes res = new CommonRes();
    	 try {
             EwayScreenSectionFieldResponse response = service.update(sno, screenId, request);
             res.setCommonResponse(response);
             res.setIsError(false);
             res.setErrorMessage(Collections.emptyList());
             res.setMessage("Updated Successfully");
             return new ResponseEntity<>(res, HttpStatus.OK);
         } catch (Exception e) {
             res.setCommonResponse(null);
             res.setIsError(true);
             res.setMessage("Update Failed");
             return new ResponseEntity<>(res, HttpStatus.OK);
         }
     }

    @PostMapping("/get/{sno}")
    public ResponseEntity<CommonRes> getById(@PathVariable Integer sno,@RequestBody EwayScreenSection req) {
    	 CommonRes res = new CommonRes();
    	  try {
              EwayScreenSectionFieldResponse data = service.getById(sno, req);
              res.setCommonResponse(data);
              res.setIsError(false);
              res.setErrorMessage(Collections.emptyList());
              res.setMessage("Success");
              return ResponseEntity.ok(res);
          } catch (Exception e) {
        	  res.setCommonResponse(null);
              res.setIsError(true);
              res.setMessage("Failed");
              return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
          }
     }

    @PostMapping("/fetch")
    public ResponseEntity<CommonRes> getAll(@RequestBody EwayScreenSection req) {
    	  CommonRes res = new CommonRes();
    	  try {
              List<EwayScreenSectionFieldResponse> list = service.getAll(req);
              res.setCommonResponse(list);
              res.setIsError(false);
              res.setErrorMessage(Collections.emptyList());
              res.setMessage("Success");
              return ResponseEntity.ok(res);
          } catch (Exception e) {
        	  res.setCommonResponse(null);
              res.setIsError(true);
              res.setMessage("Failed");
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
          }
      }

    @DeleteMapping("/delete/{sno}/{screenId}")
    public ResponseEntity<CommonRes> delete(@PathVariable Integer sno, @PathVariable Integer screenId) {
    	CommonRes res = new CommonRes();
    	 try {
             service.delete(sno, screenId);
             res.setCommonResponse("Deleted successfully.");
             res.setIsError(false);
             res.setErrorMessage(Collections.emptyList());
             res.setMessage("Success");
             return ResponseEntity.ok(res);
         } catch (Exception e) {
        	 res.setCommonResponse(null);
             res.setIsError(true);
             res.setMessage("Failed");
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
         }
    }
    @GetMapping("/getAllControllers")
    public AdminControllersRes getAllControllers(){
    	return service.getAllControllers();
	}
}
