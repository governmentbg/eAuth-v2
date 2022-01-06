package bg.bulsi.egov.idp.client;


import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import bg.bulsi.egov.eauth.eid.dto.AssertionAttributeType;
import bg.bulsi.egov.eauth.eid.dto.InquiryResult;
import bg.bulsi.egov.idp.IdpApplication;
import bg.bulsi.egov.idp.client.config.model.EidProviderConfig;
import bg.bulsi.egov.idp.dto.AuthenticationMap;
import bg.bulsi.egov.idp.services.IEidProviderClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Test(groups = {""})
@SpringBootTest(classes = IdpApplication.class)
public class ClientApiTest extends AbstractTestNGSpringContextTests {

	@Autowired
	@Qualifier("idpApiClient")
	IEidProviderClient client;
	
	EidProviderConfig providerConf;
	
	@BeforeGroups(groups = {"clientApiTest","pause"})
	private void init() {
		this.providerConf = client.getIdentityProviderConfig("test");
	}
	
	

	@Test(groups = {"clientApiTest","pause"}, priority = 1)
	private void testConfig() {
		assertEquals(this.providerConf.getName().get("bg"), "Тест 1 LOW");
	}
	

	@Test(groups = {"clientApiTest","pause"}, priority = 2)
	private void testInquiry() {
		
		log.info("Callback URL: {}", providerConf.getEidCallbackUrl());

		List<AssertionAttributeType> authList = new ArrayList<>();
		AuthenticationMap authMap = new AuthenticationMap(new HashMap<String, String>());
		InquiryResult inquiryResult = client.makeAuthInquiry(this.providerConf, authList, authMap, "Тестова услуга 1", "Тестови доставчик на услуги");

		String rpExpected = generateRelyingParty(this.providerConf.getProviderId());

		assertTrue(inquiryResult.getRelyingPartyRequestID() != null);
		
	}
	
//	@Test(groups = {"clientApiTest"}, dependsOnMethods = "testInquiry")
	private void testAuth() {
//		EidProviderConfig providerConf = client.getAuthInquiryResponse(this.providerConf, relyingPartyRequestID)
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
