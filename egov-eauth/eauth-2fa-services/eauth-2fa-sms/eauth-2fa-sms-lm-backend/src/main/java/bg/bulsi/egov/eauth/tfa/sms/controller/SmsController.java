package bg.bulsi.egov.eauth.tfa.sms.controller;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import bg.bulsi.egov.eauth.tfa.sms.model.SmsBody;
import bg.bulsi.egov.eauth.tfa.sms.model.SmsLM;
import bg.bulsi.egov.eauth.tfa.sms.model.SmsResponse;
import bg.bulsi.egov.eauth.tfa.sms.model.SmsStatus;
import bg.bulsi.egov.eauth.tfa.sms.service.SmsService;
import bg.bulsi.egov.eauth.tfa.util.HashUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/sms")
public class SmsController {

	private static final String DLR_DELIVERED_STATUS = "delivered_mobile";
	
	private static final String API_URL = "https://api-test.msghub.cloud/bulknew";
	
	private static final String API_KEY = "$2y$10$mN8ywDmA2bVSUbiZ8hQHyOsj8x8DY1jm9zvobXwt/R2mWU/egu4He";
	private static final String API_SECRET = "$2y$10$Ny.07AJ5mJRxaY55BuT.KeQeGZoDaq3NJ0Pg.uSosj4zoaEGQvwAC";
	
	private static final String SMS_SC = "1917";
	private static final int SMS_SERVICE_ID = 394;
	private static final String SMS_PRIORITY = "high";
	
	@Autowired
	private SmsService service;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("/send")
	public String send() throws JsonProcessingException {
		
		SmsBody sms = load("+359883367967", "Тестово съобщение еАвт:");
		
		ObjectMapper mapper = new ObjectMapper();
		String smsAsJson = mapper.writeValueAsString(sms);
		log.info("smsAsJson: [{}]", smsAsJson);
		
		String hashedData = HashUtils.sha512Hash(API_SECRET, smsAsJson);
		log.info("hashedData: [{}]", hashedData);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("x-api-key", API_KEY);
		headers.set("x-api-sign", hashedData);
		headers.set("Expect", ""); // empty
		
		HttpEntity<String> request = new HttpEntity<>(smsAsJson, headers);
		
		ResponseEntity<SmsResponse> response = restTemplate.postForEntity(API_URL, request, SmsResponse.class);
		log.info("response: [{}]", response);
		
		printPrettyResponse(response);
		
		return hashedData;
	}
	
	private SmsBody load(String phone, String text) {
		SmsBody sms = SmsBody.builder()
				.msisdn(phone)
				.shortCode(SMS_SC)
				.text(text)
				.serviceId(SMS_SERVICE_ID)
				.priority(SMS_PRIORITY)
				.build();
		
		log.info("sms: [{}]", sms);
		
		return sms;
	}
	
	private void printPrettyResponse(ResponseEntity<SmsResponse> response) {
		JsonNode metaNode = response.getBody().getMeta();
		log.info("metaNode: ---------------------- [{}]", metaNode.toPrettyString());
		
		int code = metaNode.get("code").asInt();
		log.info("code: [{}] = 200: [{}]", code, code == 200);
		
		
		String text = metaNode.get("text").asText();
		log.info("text: [{}]", text);
		
		JsonNode dataNode = response.getBody().getData();
		log.info("dataNode: ----------------------  [{}]", dataNode.toPrettyString());
	}
	
	
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
				Optional<SmsLM> foundSms = service.findSmsById(Long.valueOf(id));
				if (foundSms.isPresent()) {
					SmsLM sms = foundSms.get();
					sms.setStatus(SmsStatus.DELIVERED.name());
					sms.setEditDate(new Date());
					service.update(sms);
				}
			}
		}
		
		return response;
	}
	

}
