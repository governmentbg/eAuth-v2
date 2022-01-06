package bg.bulsi.egov.eauth.eid;

import static org.testng.Assert.assertEquals;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import bg.bulsi.egov.eauth.eid.dto.AssertionAttributeType;
import bg.bulsi.egov.eauth.eid.dto.AuthProcessingType;
import bg.bulsi.egov.eauth.eid.dto.AuthenticationRequest;
import bg.bulsi.egov.eauth.eid.dto.AuthenticationResponse;
import bg.bulsi.egov.eauth.eid.dto.InquiryResult;
import bg.bulsi.egov.eauth.eid.dto.LevelOfAssurance;
import bg.bulsi.egov.eauth.eid.dto.ProcessingData;
import bg.bulsi.egov.eauth.eid.dto.UserAuthData;
import bg.bulsi.egov.eauth.eid.provider.MainApplication;
import bg.bulsi.egov.eauth.eid.provider.model.Identity;
import bg.bulsi.egov.eauth.eid.provider.service.EidService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Test(groups = "")
@SpringBootTest(classes = MainApplication.class)
public class EidServiceTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private EidService eidService;

	private Identity entity1;

	private String relyingPartyRequestID;
	
	
	@BeforeGroups(groups = { "issuerTest" })
	private void initMaps() {

		log.info("----------------- IssuerTest TEST --------");

		entity1 = new Identity();
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

	}


	@Test(groups = { "issuerTest"})
	public void testInquiry() {

		AuthenticationRequest body = new AuthenticationRequest();
		body.setRelayState("testState");

		ProcessingData processing = new ProcessingData();
		processing.setPtype(AuthProcessingType.POLLING);
		ZoneId zoneId = ZoneOffset.systemDefault();
		LocalDateTime localDateTime = LocalDateTime.now().plusHours(4);
		processing.setResponceTimeout(OffsetDateTime.ofInstant(localDateTime.toDate().toInstant(), zoneId));
		processing.setCallbackUrl("");
		body.setProcessing(processing);

		body.setLevelOfAssurance(LevelOfAssurance.LOW);

		UserAuthData userAuthData = new UserAuthData();
		userAuthData.setIdentityString("1010101010");
		userAuthData.setAuthenticationString("$2a$10$KzZ7T6NnymJ8CzSRW.QsLe852FZ5TsghxX/T6XC7GqpT/XnWTouL2");

		body.setUser(userAuthData);

		List<AssertionAttributeType> attributeList = new ArrayList<>();
		attributeList.add(AssertionAttributeType.PERSONNAME);
		attributeList.add(AssertionAttributeType.EMAIL);
		attributeList.add(AssertionAttributeType.PHONE);
		body.setRequestedAddAuthAttributes(attributeList);

		ResponseEntity<?> resInq = eidService.identityInquiry(body);

		int code = resInq.getStatusCode().value();
		
		relyingPartyRequestID = ((InquiryResult) resInq.getBody()).getRelyingPartyRequestID();
		
		log.info("###Responce: " + code);
		log.info("###RP-ID: " + relyingPartyRequestID);

		assertEquals(code, 200, "ACCEPTED_200");
		
	}

	
	@Test(groups = { "issuerTest"},	dependsOnMethods = {"testInquiry"})
	public void testAuth() {

		try {
			log.info("Thread sleep for 5s ...");
			log.info("... wait pooling processing request... ");
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			log.error(e.getLocalizedMessage(),e);
			Thread.currentThread().interrupt();
		}
		log.info("... Thread continues after pooling process.");
		
		
		ResponseEntity<?> resAuth = eidService.getAuthentication(relyingPartyRequestID);
		
		int codeAuth = resAuth.getStatusCode().value();
		
		log.info("###Responce auth: " + codeAuth);
		log.info("###AUTH Email: " + ((AuthenticationResponse) resAuth.getBody()).getSubjectAssertions().stream()
				.filter(t -> t.getAttribute() == AssertionAttributeType.EMAIL)
				.findFirst().get().toString()
				);

		assertEquals(codeAuth, 200, "ACCEPTED_200");
		
	}

}
