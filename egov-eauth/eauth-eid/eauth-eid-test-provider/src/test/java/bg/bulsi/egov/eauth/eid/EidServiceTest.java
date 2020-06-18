package bg.bulsi.egov.eauth.eid;

import static org.testng.Assert.assertEquals;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import bg.bulsi.egov.eauth.eid.dto.AttributeMap;
import bg.bulsi.egov.eauth.eid.dto.AuthProcessingType;
import bg.bulsi.egov.eauth.eid.dto.AuthenticationRequest;
import bg.bulsi.egov.eauth.eid.dto.LevelOfAssurance;
import bg.bulsi.egov.eauth.eid.dto.ProcessingData;
import bg.bulsi.egov.eauth.eid.dto.UserAuthData;
import bg.bulsi.egov.eauth.eid.dto.UserData;
import bg.bulsi.egov.eauth.eid.provider.MainApplication;
import bg.bulsi.egov.eauth.eid.provider.model.Identity;
import bg.bulsi.egov.eauth.eid.provider.model.repository.IdentityRepository;
import bg.bulsi.egov.eauth.eid.provider.service.EidService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Test(groups = "pause")
@SpringBootTest(classes = MainApplication.class)
public class EidServiceTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private EidService eidService;
	
	@Autowired
	private IdentityRepository identityRepository;
	
	@BeforeGroups(groups = {"issuerTest"})
	private void initMaps() {

		log.info("----------------- IssuerTest TEST --------");
		
		
		Identity entity = new Identity();
		entity.setId(2L);
		entity.setNid("1110101010");
		identityRepository.save(entity);
		
		Identity entity1 = new Identity();
		entity1.setId(1L);
		entity1.setNid("1010101010");
		entity1.setActive(true);
		entity1.setCreateDate(new Date());
		entity1.setEditDate(null);
		entity1.setEmail("admin@bul-si.bg");
		entity1.setNames("Админ Админ");
		entity1.setPassword("$2a$10$KzZ7T6NnymJ8CzSRW.QsLe852FZ5TsghxX/T6XC7GqpT/XnWTouL2");
		entity1.setUsername("admin");
		entity1.setPhone("+359888888888");
		identityRepository.save(entity1);
		
	}

	@Test(groups = {"issuerTest"})
	public void testPool() {
		
		AuthenticationRequest body = new AuthenticationRequest();
		body.setLevelOfAssurance(LevelOfAssurance.LOW);
		body.setRelayState("someState");
		
		ProcessingData processing = new ProcessingData();
		processing.setPtype(AuthProcessingType.POLLING);
		ZoneId zoneId = ZoneOffset.systemDefault();
		LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(60);
		processing.setResponceTimeout(OffsetDateTime.ofInstant(localDateTime.toDate().toInstant(), zoneId));
		
		processing.setCallbackUrl("");
		body.setProcessing(processing);
		
		UserAuthData userAuthData = new UserAuthData();
		userAuthData.setAuthenticationString("$2a$10$KzZ7T6NnymJ8CzSRW.QsLe852FZ5TsghxX/T6XC7GqpT/XnWTouL2");
		
		UserData identify = new UserData();
		identify.setIdentificationNumber("1010101010");
		
		userAuthData.setIdentityString(identify.getIdentificationNumber());
		
		body.setUser(userAuthData);
		//body.setVendorId("vendorID");
		
		AttributeMap attributeMap = new AttributeMap();
		attributeMap.put("EXPIRATIONPERIOD", "1400");
		body.setIdentificationAttributes(attributeMap);
		
		ResponseEntity<?> res = eidService.identityInquiry(body);

		int code = res.getStatusCode().value();
		
		log.debug("###Responce: " + code);

		assertEquals(code, 200, "ACCEPTED_200");

	}

}
