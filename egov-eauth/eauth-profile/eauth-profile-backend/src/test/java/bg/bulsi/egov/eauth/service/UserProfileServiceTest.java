package bg.bulsi.egov.eauth.service;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
//import static bg.bulsi.egov.eauth.IntegrationTestUtil.loadJsonData;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import bg.bulsi.egov.eauth.model.repository.UserRepository;
import bg.bulsi.egov.eauth.profile.rest.api.ApiException;
import bg.bulsi.egov.eauth.services.profile.UserProfileService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public class UserProfileServiceTest {

    Logger log = LoggerFactory.getLogger(UserProfileServiceTest.class);

    @BeforeTest
    public void setup() {
        System.out.println("@BeforeMethod UserProfileServiceTest");
        MockitoAnnotations.initMocks(this);
    }

    private final String personEgnHashed = "y2XDY78SARxxB+pjmnW/TQ==";

    @Mock
    public UserRepository userRepository;

    @Mock
    public ModelMapper modelMapper;


    @InjectMocks
    public UserProfileService mockUserProfileService;


    private final String JSESSIONID = "DC078948A94F88E785692AE2D335E36B";

    @BeforeTest
    /**
     * Get user from database
     */
    public void beforeTest() {
        log.debug("BeforeTest method is run...");


        Authentication authentication = Mockito.mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


       ((Jws<Claims>) securityContext.getAuthentication().getPrincipal()).getBody().setSubject(personEgnHashed);

    }

    @Test
    @WithMockUser(personEgnHashed)
    public void testcreateProfile() throws ApiException {

     /*   try {
            *//*bg.bulsi.egov.eauth.profile.rest.api.dto.User userToCreateJson = loadJsonData("user.json", bg.bulsi.egov.eauth.profile.rest.api.dto.User.class);
            Object o = mockUserProfileService.createProfile(userToCreateJson);
            o.toString();*//*
        } catch (IOException e) {
            log.error("Error loading user.json file");
            e.printStackTrace();
        }
*/
    }
}
