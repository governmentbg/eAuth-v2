package bg.bulsi.egov.eauth.eid;

import static org.testng.Assert.assertEquals;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import bg.bulsi.egov.eauth.eid.dto.AttributeMap;
import bg.bulsi.egov.eauth.eid.dto.AuthenticationRequest;
import bg.bulsi.egov.eauth.eid.dto.LevelOfAssurance;
import bg.bulsi.egov.eauth.eid.dto.UserAuthData;
import bg.bulsi.egov.eauth.eid.dto.UserData;
import bg.bulsi.egov.eauth.eid.provider.MainApplication;
import bg.bulsi.egov.eauth.eid.provider.cash.Cache;
import bg.bulsi.egov.eauth.eid.provider.cash.InMemoryCache;
import bg.bulsi.egov.eauth.eid.provider.cash.Cache.ExpiredType;
import bg.bulsi.egov.eauth.eid.provider.model.UserStatusDataIn;
import bg.bulsi.egov.eauth.eid.provider.service.EidService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Test(groups = {"pause"})
@SpringBootTest(classes = MainApplication.class)
public class CacheTest extends AbstractTestNGSpringContextTests {

	@Autowired
	EidService eidService;

	@BeforeGroups(groups = {"cashTest"})
	private void initMaps() {

		log.info("----------------- CASH TEST --------");
		
		
		AuthenticationRequest body = new AuthenticationRequest();
		body.setLevelOfAssurance(LevelOfAssurance.LOW);
		body.setRelayState("someState");
		UserAuthData userAuthData = new UserAuthData();
		userAuthData.setAuthenticationString("$2a$10$KzZ7T6NnymJ8CzSRW.QsLe852FZ5TsghxX/T6XC7GqpT/XnWTouL2");
		UserData identify = new UserData();
		identify.setIdentificationNumber("1010101010");
		userAuthData.setIdentityString(identify.getIdentificationNumber());
		body.setUser(userAuthData);
	//	body.setVendorId("vendorID");
		AttributeMap attributeMap = new AttributeMap();
		attributeMap.put("EXPIRATIONPERIOD", "1400");
		body.setIdentificationAttributes(attributeMap);
		eidService.identityInquiry(body);
		

		AuthenticationRequest auth0 = new AuthenticationRequest();
		auth0.setLevelOfAssurance(LevelOfAssurance.LOW);
		auth0.setRelayState("someState");
		UserAuthData userAuthData0 = new UserAuthData();
		userAuthData0.setIdentityString("admin");
		userAuthData0.setAuthenticationString("d033e22ae348aeb5660fc2140aec35850c4da997");
		UserData identify0 = new UserData();
		identify0.setIdentificationNumber("1010101010");
		userAuthData0.setIdentityString(identify0.getIdentificationNumber());
		auth0.setUser(userAuthData0);
		//auth0.setVendorId("vendorID");
		AttributeMap attributeMap0 = new AttributeMap();
		attributeMap0.put("EXPIRATIONPERIOD", "1400");
		auth0.setIdentificationAttributes(attributeMap0);
		eidService.identityInquiry(auth0);
		

		AuthenticationRequest auth1 = new AuthenticationRequest();
		auth1.setLevelOfAssurance(LevelOfAssurance.LOW);
		auth1.setRelayState("someState");
		UserAuthData userAuthData1 = new UserAuthData();
		userAuthData1.setIdentityString("id2");
		userAuthData1.setAuthenticationString("pass2");
		UserData identify1 = new UserData();
		identify1.setIdentificationNumber("1110101010");
		userAuthData1.setIdentityString(identify1.getIdentificationNumber());
		auth1.setUser(userAuthData1);
		//auth1.setVendorId("vendorID");
		AttributeMap attributeMap1 = new AttributeMap();
		attributeMap1.put("EXPIRATIONPERIOD", "1400");
		auth1.setIdentificationAttributes(attributeMap1);
		eidService.identityInquiry(auth1);
		
	}
	
	@Test(enabled = true, groups = {"cashTest"}, priority = 1)
	public void testOldestKey() {
	
			Cache<UserStatusDataIn> identitiyRequiestCash = new InMemoryCache<>();
			UserStatusDataIn value = new UserStatusDataIn();
			identitiyRequiestCash.add("key1", value, 10000, ExpiredType.EXPIRED_PERIOD);
			identitiyRequiestCash.add("key2", value, 20000, ExpiredType.EXPIRED_PERIOD);
			
			Optional<String> keyOpt = identitiyRequiestCash.getOldestKey();
			
			String key = null;
			if (keyOpt.isPresent()) {
				key = keyOpt.get();
			}
		
			assertEquals(key, "key1");

	}

	@Test(enabled = true, groups = {"cashTest"}, priority = 2)
	public void testPool() {
		
		eidService.poolInMemoryToIdentityInquiry();
		
		
	}

}
