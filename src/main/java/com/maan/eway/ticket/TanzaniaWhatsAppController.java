package com.maan.eway.ticket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/whatsapp")
public class TanzaniaWhatsAppController {
	
	@Autowired
	private TanzaniaWhastAppService whatsappService;
	
	@PostMapping("/stickerno/checkstatus")
	public Object checkStickerNoStatus(@RequestBody Object req){
		return whatsappService.checkStickerNoStatus(req);
	}

}
