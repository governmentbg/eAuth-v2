package bg.bulsi.egov.idp.client;


import static org.testng.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import bg.bulsi.egov.eauth.eid.dto.InquiryResult;
import bg.bulsi.egov.idp.IdpApplication;
import bg.bulsi.egov.idp.client.config.model.EidProvidersConfiguration.EidProviderConfig;
import bg.bulsi.egov.idp.client.services.IdpClient;
import bg.bulsi.egov.idp.dto.AuthenticationMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Test(groups = {"pause"})
@SpringBootTest(classes = IdpApplication.class)
public class ClientTest extends AbstractTestNGSpringContextTests {

	@Autowired
	IdpClient client;
	
	EidProviderConfig providerConf;
	
	@BeforeGroups(groups = {"clientTest"})
	private void init() {
		this.providerConf = client.getIdentityProviderConfig("test");
	}
	
	

	@Test(groups = {"clientTest"})
	private void testConfig() {
		assertEquals(this.providerConf.getName().get("bg"), "Тест 1 LOW");
	}
	

	@Test(groups = {"clientTest", "brokenTest"})
	private void testInquiry() {
		
		Map<? extends String, ? extends String> m = new HashMap<>();
		AuthenticationMap authMap = new AuthenticationMap(m);

		InquiryResult inquiryResult = client.makeAuthInquiry(this.providerConf, authMap);
		
		String rpExpected = generateRelyingParty(this.providerConf.getProviderId()); 
		
		assertEquals(inquiryResult.getRelyingPartyRequestID(), rpExpected);
		
	}
	
//	@Test(groups = {"clientTest"})
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
