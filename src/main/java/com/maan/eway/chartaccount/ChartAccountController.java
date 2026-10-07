package com.maan.eway.chartaccount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;

@RestController
@RequestMapping("/chartaccount")
public class ChartAccountController {
	
	
	@Autowired
	private ChartAccountService service;
	
	
	@PostMapping("/drcr/entry")
	public CommonRes drcrEntry(@RequestBody ChartAccountRequest req) {
		return service.drcrEntry(req);
	}

}
