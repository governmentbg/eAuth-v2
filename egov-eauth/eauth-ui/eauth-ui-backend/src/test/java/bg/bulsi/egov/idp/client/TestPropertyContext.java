package bg.bulsi.egov.idp.client;


import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import bg.bulsi.egov.eauth.eid.dto.LevelOfAssurance;
import bg.bulsi.egov.eauth.metadata.config.model.SpProvidersConfiguration;
import bg.bulsi.egov.eauth.metadata.config.model.SpProvidersConfiguration.SpProviderConfig;
import bg.bulsi.egov.idp.IdpApplication;
import bg.bulsi.egov.idp.client.config.model.Eid;
import bg.bulsi.egov.idp.client.config.model.EidProviderConfig;
import bg.bulsi.egov.idp.client.config.model.EidProviderConnection;
import bg.bulsi.egov.idp.client.config.model.EidProvidersConfiguration;
import bg.bulsi.egov.idp.client.config.model.ProviderAuthAttribute;
import bg.bulsi.egov.idp.dto.Label;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Test(groups = {""})
@SpringBootTest(classes = IdpApplication.class)
public class TestPropertyContext extends AbstractTestNGSpringContextTests {

//	@Autowired
//	IdpConfigurationProperties idpConfigurationProperties;
	@Autowired
	EidProvidersConfiguration identitiesParamsObj;
//	@Autowired
//	SpProvidersConfiguration serviceParamsObj;
	
	@Test(groups = {"propertiesContextTest","pause"})
	private void testIdpObj() {
		
		String keyProvider = "test";
		EidProviderConfig identityParam = identitiesParamsObj.getProviders().get(keyProvider);
		
		String id = identityParam.getProviderId();
		log.info("#TEST Provider ID: {}", id);
		
		String attributeKey = id + "_" + ProviderIdSuffix.USERNAME.name();
		ProviderAuthAttribute providerAuthAttribute = identityParam.getAttributes().get(attributeKey);
		
		Eid attEId = providerAuthAttribute.getEId();
		log.info("#TEST Provider attEId: {}", attEId);
		
		boolean attMan = providerAuthAttribute.isMandatory();
		log.info("#TEST Provider attMandatory: {}", attMan);
		
		Label attLab = providerAuthAttribute.getLabel();
		log.info("#TEST Provider BG attLabel: {}", attLab.get("bg"));
		log.info("#TEST Provider EN attLAbel: {}", attLab.get("en"));
		
		LevelOfAssurance attLoa =  identityParam.getLoa();
		log.info("#TEST Provider attLoa: {}", attLoa);
		
		EidProviderConnection cc = identityParam.getEidProviderConnection();
		log.info("", cc.getEndpoint());
		log.info("", cc.getTlsVersion());
		log.info("", cc.getClientKeyStore().getPass());
		
		assertEquals(id, keyProvider);
		assertEquals(attEId, Eid.IDENTITY);
		assertEquals(attMan, true);
		assertEquals(attLoa, LevelOfAssurance.LOW);
		assertEquals(attLab.get("bg"), "Потребител");
		assertEquals(attLab.get("en"), "User");
		assertEquals(cc.getTlsVersion(), "1.3");

	}
	

	@Test(groups = {"propertiesContextTest", "pause"})
	private void testFilter() {
		int i;
		i = identitiesParamsObj.getListByLoa(LevelOfAssurance.LOW).size();
		assertEquals(i, 4);
		i = identitiesParamsObj.getListByLoa(LevelOfAssurance.SUBSTANTIAL).size();
		assertEquals(i, 3);
		i = identitiesParamsObj.getListByLoa(LevelOfAssurance.HIGH).size();
		assertEquals(i, 2);
	}
	

	@Test(groups = {"propertiesContextTest", "pause"})
	private void testSpObj() {
		
//		String keyProvider = "test";
//		SpProviderConfig spParam = serviceParamsObj.getProviders().get(keyProvider);
//		
//		String id = spParam.getEntityId();
//		log.info("#TEST EntityID: {}", id);
//		
//		Map<String, String> name = spParam.getName();
//		log.info("#TEST Provider BG attLabel: {}", name.get("bg"));
//		log.info("#TEST Provider EN attLAbel: {}", name.get("en"));
//		
//		String url =  spParam.getBindingUrl();
//		log.info("#TEST Provider attLoa: {}", url);
//		
//		assertEquals(id, "testsp");
//		assertEquals(name.get("bg"), "СП1");
//		assertEquals(name.get("en"), "SP1");
//		assertEquals(url,"https://xxx.com");

	}
	
	@Test(groups = {"propertiesContextTest","pause"})
	private void testIdpConfObj() {
		
		
//		String id = idpConfigurationProperties.getEntityId();
//		log.info("#TEST EntityID: {}", id);
//
//		String urlPk =  idpConfigurationProperties.getPrivateKey();
//		log.info("#TEST PK: {}", urlPk);
//
//		String urlCrt =  idpConfigurationProperties.getCertificate();
//		log.info("#TEST Cert: {}", urlCrt);
//
//		String urlClams =  idpConfigurationProperties.getClaims();
//		log.info("#TEST Clams: {}", urlClams);
//		
//		assertEquals(id, "http://mock-idp");
//		assertEquals(urlPk, "certificates/private.key");
//		assertEquals(urlCrt, "certificates/certificate.crt");
//		assertEquals(urlClams,"/eauth-saml-extensions/src/main/resources/schema/bg-egov-eauthentication-2.0.xsd");

	}
	
	@Test(groups = {"propertiesContextTest","pause"})
	private void testIdpConfExt() {
		
//		String id = idpConfigurationProperties.getEntityId();
//		log.info("#TEST EntityID: {}", id);
//
//		Set<String> digSet =  idpConfigurationProperties.getDigestMethods();
//		Iterator<String> it = digSet.iterator();
//		String digSet1 = it.next();
//		log.info("#TEST Digest: {}", digSet1);
//
////		List<FederatedUserAuthenticationToken> listFUAT =  idpConfigurationProperties.getUsers();
////		FederatedUserAuthenticationToken listFUAT1 = listFUAT.get(0);
////		Map<String, List<String>> listFUAT1Map = listFUAT.get(0).getAttributes();
////		List<String> listFUAT1MapK1 = listFUAT1Map.get("keyX1");
//		
//		assertEquals(id, "http://mock-idp");
//		assertEquals(digSet1, "method1");
////		assertEquals(listFUAT1MapK1.get(0), "list1");

	}
	
	@SuppressWarnings("restriction")
	@Test(groups = {"propertiesContextTest","brokenTest"})
	private void testIdpReadXsd() {
		
//		String urlClams =  idpConfigurationProperties.getClaims();
//		log.info("#TEST Clams: {}", urlClams);
//
//		Schema sch = idpConfigurationProperties.readSchemaFromXsd(urlClams);
//		
////		String urlClamsExpected = "/home/gdimitrov/workspaces201909/eAuth2/egov-eauth/eauth-common-libs/eauth-saml-extensions/src/main/resources/schema/bg-egov-eauthentication-2.0.xsd";
////		assertEquals(urlClams,urlClamsExpected);

	}

	
}
