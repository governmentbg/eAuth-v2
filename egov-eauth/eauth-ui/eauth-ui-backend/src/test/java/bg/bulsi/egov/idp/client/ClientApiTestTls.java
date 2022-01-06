package bg.bulsi.egov.idp.client;


import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import bg.bulsi.egov.eauth.eid.dto.AssertionAttributeType;
import bg.bulsi.egov.eauth.eid.dto.InquiryResult;
import bg.bulsi.egov.idp.IdpApplication;
import bg.bulsi.egov.idp.client.config.model.EidProviderConfig;
import bg.bulsi.egov.idp.dto.AuthenticationMap;
import bg.bulsi.egov.idp.dto.LoginResponse;
import bg.bulsi.egov.idp.services.IEidProviderClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Test(groups = {""})
@SpringBootTest(classes = IdpApplication.class)
@PropertySource("application.yaml")
public class ClientApiTestTls extends AbstractTestNGSpringContextTests {

	@Autowired
	@Qualifier("idpApiClientTls")
	IEidProviderClient client;
	
	EidProviderConfig providerConf;
	String relyingPartyRequestID;
	
	@BeforeGroups(groups = {"clientApiTest"})
	private void init() {
//		this.providerConf = client.getIdentityProviderConfig("Borica Cloud QES");
		this.providerConf = client.getIdentityProviderConfig("test");
	}
	
	

	@Test(groups = {"clientApiTest"}, priority = 1)
	private void testConfig() {
		String name = this.providerConf.getName().get("bg");
		log.info("# CLIENT Name: {}", name);
		assertEquals(name, "Тест ОблаченКЕП Borica");
	}
	

	@Test(groups = {"clientApiTest"}, priority = 2)
	private void testInquiry() {
		
		log.info("Callback URL: {}", providerConf.getEidCallbackUrl());

		List<AssertionAttributeType> authList = new ArrayList<>();
		authList.add(AssertionAttributeType.EMAIL);
		AuthenticationMap authMap = new AuthenticationMap(new HashMap<String, String>());
//		authMap.put("borica_EGN", "7408065638");
		authMap.put("test_USERNAME", "user2");
		authMap.put("test_PASSWORD", "user2");
		
		InquiryResult inquiryResult = client.makeAuthInquiry(this.providerConf, authList, authMap,"Редактиране на профил двуфакторна автентикация","Портал ЕПДАЕУ");

		String rpExpected = generateRelyingParty(this.providerConf.getProviderId());

		relyingPartyRequestID = inquiryResult.getRelyingPartyRequestID();
		log.info("#TLS#relyingPartyRequestID: {}", relyingPartyRequestID);
		assertTrue(inquiryResult.getRelyingPartyRequestID() != null);
		
	}
	
	@Test(groups = {"clientApiTest"}, dependsOnMethods = "testInquiry")
	private void testAuth() {
		LocalDateTime date = LocalDateTime.now();
		ZoneId zone = ZoneId.systemDefault();
		ZoneOffset zoneOffSet = zone.getRules().getOffset(date);
		OffsetDateTime timeoutValidity = date.atOffset(zoneOffSet).plusSeconds(60);
		
		LoginResponse providerConf = client.getAuthInquiryResponse(this.providerConf, relyingPartyRequestID,timeoutValidity);

		String providerId = providerConf.getProviderId();
		System.out.println("#TLS#PRId_A: " + providerId);
		assertTrue(providerId != null);
		
	}
	
	
	@Deprecated
	private String generateRelyingParty(String nid) {
		String relyingPartyRequestID = null;
		String timedNid = nid + String.valueOf(System.currentTimeMillis());
		byte[] bytes = null;
		try {
			bytes = timedNid.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("UTF-8 encode", e.getLocalizedMessage());
		}
		UUID uuid = UUID.nameUUIDFromBytes(bytes);
		relyingPartyRequestID = uuid.toString();
		return relyingPartyRequestID;
	}
	
}
