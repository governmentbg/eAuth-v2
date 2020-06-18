package bg.bulsi.egov.eauth.service.totp;

import bg.bulsi.egov.eauth.Eauth2faTotpApplication;
import bg.bulsi.egov.eauth.model.Preferred2FA;
import bg.bulsi.egov.eauth.model.User;
import bg.bulsi.egov.eauth.model.repository.UserRepository;
import bg.bulsi.egov.eauth.tfa.totp.service.totp.TotpCredentialRepositoryService;
import bg.bulsi.egov.eauth.tfa.totp.service.totp.TotpGeneratedData;
import bg.bulsi.egov.eauth.tfa.totp.service.totp.TotpPasswordGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import java.util.*;
import static org.mockito.Mockito.when;



@SpringBootTest(classes = {Eauth2faTotpApplication.class})
@RunWith(MockitoJUnitRunner.class)
@Transactional
public class TotpCredentialRepositoryServiceTest {




/*
    final String USER_ID = "h2XDY78SARxxB+pjmnW/TQ==";

    private User user;

    @Autowired
    UserRepository userRepository;


    @Autowired
    TotpCredentialRepositoryService totpCredentialRepositoryService;

    @BeforeEach
    public void setUp() {

        user = new User();
        user.setPersonID(USER_ID);
        user.setName("username_test");
        user.setEmail("test@test.com");
        user.setPreferred(Preferred2FA.TOTP);
        user.setPhoneNumber("777777777");
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put(User.TOTP_SECRET,"GYRKDVPOETUD5AHM");
        user.setAttributes(attributes);
        userRepository.save(user);
    }
    */

    /*
    @Test()
    public void getSecretKeyTest() {


       Optional<User> u =  userRepository.findByPersonID(user.getPersonID());
        String secretKey =null;
        if( u.isPresent() ) {
            secretKey = this.totpCredentialRepositoryService.getSecretKey(u.get().getPersonID());
       }

       Assert.assertNotNull(secretKey);

    }


    @Test
    public void saveUserCredentialsTest() {

        String username = user.getPersonID();
        String secretKey = "3ZBDTZFJZ4F4EGPW";
        //not using in method so it do not matter
        int validationCode = 0;
        //not using in method
        List<Integer> scratchCodes = new ArrayList<>();


        totpCredentialRepositoryService.saveUserCredentials(username, secretKey,validationCode, scratchCodes);

    }
    */
}
