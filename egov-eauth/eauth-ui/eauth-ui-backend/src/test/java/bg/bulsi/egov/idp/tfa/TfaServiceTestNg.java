package bg.bulsi.egov.idp.tfa;

import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import bg.bulsi.egov.eauth.model.Preferred2FA;
import bg.bulsi.egov.eauth.model.repository.UserRepository;
import bg.bulsi.egov.eauth.profile.rest.api.dto.User;
import bg.bulsi.egov.eauth.tfa.api.dto.AuthRequestDetails;
import bg.bulsi.egov.eauth.tfa.api.dto.OTPass;
import bg.bulsi.egov.eauth.tfa.api.dto.SecretMetadata;
import bg.bulsi.egov.eauth.tfa.api.dto.UserContactInfo;
import bg.bulsi.egov.idp.IdpApplication;
import bg.bulsi.egov.idp.controlers.TfaController;
import bg.bulsi.egov.idp.dto.CodeRequest;
import bg.bulsi.egov.idp.dto.OTPMethod;
import bg.bulsi.egov.idp.dto.OTPresponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Test(groups = {"brokenTest"})
//@Import(ProfileModelConfig.class)
@SpringBootTest(classes = IdpApplication.class)
public class TfaServiceTestNg extends AbstractTestNGSpringContextTests {

	@Mock
	UserRepository userRepository;
	@Mock
	RestTemplate restTemplate;
	
	@InjectMocks
	TfaController mockTfaService;

//	@BeforeMethod
//	public void setup() {
//		System.out.println("@BeforeMethod TestNGMockAnnotationExample");
//		MockitoAnnotations.initMocks(this);
//	}


	@Test(dataProvider = "re_sentcodedp", groups = {"tfaServiceTest"})
	public void resendCodeTest(CodeRequest body, Optional<String> nid) {
		
		/*
		 * mock getUser
		 */
	 
		User user  = new User();
	//	user.setEgn(nid.get());
		user.setEmail("email@mail.bg");
		user.setPhoneNumber("123456879");
		user.setPreferred2FA(bg.bulsi.egov.eauth.profile.rest.api.dto.OTPMethod.SMS);

		
		bg.bulsi.egov.eauth.model.User userModel = new bg.bulsi.egov.eauth.model.User();
		userModel.setAddress(null);
		//userModel.setPersonID(user.getEgn());
		userModel.setEmail(user.getEmail());
		userModel.setPhoneNumber(user.getPhoneNumber());
		userModel.setPreferred(Preferred2FA.valueOf(user.getPreferred2FA().name()));
					
		Optional<bg.bulsi.egov.eauth.model.User> userOpt = Optional.of(userModel);
		
		String nidStr = null;
		if (nid.isPresent()) {
			nidStr = nid.get();
		}
		
		Mockito
		.when(userRepository.findByPersonID(nidStr)).thenReturn(userOpt);
		
		/*
		 * mock sendSMS
		 */
		OTPresponse otp = new OTPresponse();	
		
		AuthRequestDetails authReq = null;
		{
			bg.bulsi.egov.eauth.profile.rest.api.dto.OTPMethod userPreferredTfa = user.getPreferred2FA();
			
			authReq = new AuthRequestDetails();
			authReq.setDescription(userPreferredTfa.toString());
			UserContactInfo contacts = new UserContactInfo();
			contacts.setPhone(user.getPhoneNumber());
			contacts.setEmail(user.getEmail());
			authReq.setContacts(contacts);
		}
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity<AuthRequestDetails> request = new HttpEntity<AuthRequestDetails>(authReq, headers);

	    
	    OTPass smsResp = new OTPass();	
		smsResp.setComplete(true);
		Date date = new Date();
		smsResp.setTimestamp(date.getTime());
		smsResp.setTransaction("");
	    
		Mockito
		.when(restTemplate.postForObject("http://localhost:8120/otp/auth", request, OTPass.class)).thenReturn(smsResp);

		/*
		 * mock sendTOTP
		 */
		
		
		{
			Optional<bg.bulsi.egov.eauth.model.User> userEntity = userRepository.findByPersonID(nid.get());
			ResponseEntity<SecretMetadata> secretResponse = null;
			bg.bulsi.egov.eauth.model.User user1 = userEntity.get();
			Map<String, String> attributes = user1.getAttributes();
			
			if (attributes == null || attributes.isEmpty() || !attributes.containsKey(bg.bulsi.egov.eauth.model.User.TOTP_SECRET)) {
				HttpHeaders headers1 = new HttpHeaders();
				headers1.setContentType(MediaType.APPLICATION_JSON);
				headers1.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
				headers1.set("userID", nid.get()); // add custom header
				
				HttpEntity<String> request1 = new HttpEntity<>(headers1);
				String applicationID = "eauth"; // hardcoded
				
				SecretMetadata secrResp = new SecretMetadata();
				secrResp.setSecretKey("xxx");
				secrResp.setQrImage(null);
				ResponseEntity<SecretMetadata> metaRest = ResponseEntity.ok(secrResp);

				Mockito
				.when(restTemplate.exchange("http://localhost:8130/totp/{applicationID}/generate-secret", HttpMethod.GET, request1, SecretMetadata.class, applicationID)).thenReturn(metaRest);

			}
			
		}
		

		/*
		 * test
		 */
		ResponseEntity<OTPresponse> otpResp = mockTfaService.resendCode(body);
		HttpStatus sc = otpResp.getStatusCode();

		assertTrue(sc == HttpStatus.OK);
	}
	


	@DataProvider(name = "re_sentcodedp")
	public Object[][] rscdp() {
		
		String nid = "2010101015";

		CodeRequest body1 = new CodeRequest();
		OTPMethod at1 = OTPMethod.SMS;
		body1.setNewCodeType(at1);
		body1.setCtId("");

		CodeRequest body2 = new CodeRequest();
		OTPMethod at2 = OTPMethod.TOTP;
		body2.setNewCodeType(at2);
		body2.setCtId("");

		return new Object[][] {
			new Object[] { body1, Optional.of(nid) },
			new Object[] { body2, Optional.of(nid) }
		};
	}



}
