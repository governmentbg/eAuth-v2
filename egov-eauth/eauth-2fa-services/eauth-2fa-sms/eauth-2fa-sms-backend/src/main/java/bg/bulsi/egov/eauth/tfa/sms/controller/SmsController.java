package bg.bulsi.egov.eauth.tfa.sms.controller;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bg.bulsi.egov.eauth.tfa.sms.model.Sms;
import bg.bulsi.egov.eauth.tfa.sms.model.SmsStatus;
import bg.bulsi.egov.eauth.tfa.sms.service.SmsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/sms")
public class SmsController {

	private static final String DLR_DELIVERED_STATUS = "delivered_mobile";
	
	@Autowired
	private SmsService service;
	
	
	@GetMapping("/health")
	public String health() {
		return "Server is up and running!";
	}
	
	/*
	 * SMS Delivery Report Status
	 * GET заявка от SMS доставчика на услуги
	 */
	@GetMapping("/dlr")
	public String delivered(@RequestParam Map<String,String> allParams) {
		log.info("allParams: {}", allParams.entrySet());
		String response = null;
		
		String id = allParams.get("id");
		String status = allParams.get("status");
		/*
		 * id int (11) ваш уникален идентификатор на заявката
		 * status string Таблица 2 (параметър: delivered_mobile => Финален=Да Доставен до абоната)
         * ts timestamp Дата и час на настъпване на събитието
         * attempt int (11) Опит за доставка на пакета до вас (1..N)
		 */
		if (StringUtils.isNotBlank(id)) {
			response = "OK " + id;
			if (StringUtils.isNotBlank(status) && status.equalsIgnoreCase(DLR_DELIVERED_STATUS)) {
				Optional<Sms> foundSms = service.findSmsById(Long.valueOf(id));
				if (foundSms.isPresent()) {
					Sms sms = foundSms.get();
					sms.setStatus(SmsStatus.DELIVERED.name());
					sms.setEditDate(new Date());
					service.update(sms);
				}
			}
		}
		
		return response;
	}
	

}
