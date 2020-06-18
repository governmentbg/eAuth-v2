package bg.bulsi.egov.eauth.tfa.totp.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder;
import com.warrenstrange.googleauth.GoogleAuthenticatorException;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.ICredentialRepository;
import com.warrenstrange.googleauth.IGoogleAuthenticator;

@Component
public class TotpAuthenticator implements IGoogleAuthenticator {

	@Value("${egov.eauth.sys.tfa.totp.window.size}")
	private int winSize;
	
    private final GoogleAuthenticator gAuth = new GoogleAuthenticator(buildConfig(winSize > 0 ? winSize : 2));
    
    private GoogleAuthenticatorConfig buildConfig(int windowSize) {
    	GoogleAuthenticatorConfigBuilder builder = new GoogleAuthenticatorConfigBuilder();
    	return builder.setWindowSize(windowSize).build();
    }

    @SuppressWarnings("unused")
    public TotpAuthenticator() {
        this(null);
    }

    public TotpAuthenticator(ICredentialRepository repository) {
        this.setCredentialRepository(repository);
    }

    @Override
    public GoogleAuthenticatorKey createCredentials() {
        return gAuth.createCredentials();
    }

    @Override
    public GoogleAuthenticatorKey createCredentials(String userName) {
        return gAuth.createCredentials(userName);
    }

    @Override
    public int getTotpPassword(String secret) {
        return gAuth.getTotpPassword(secret);
    }

    @Override
    public int getTotpPassword(String secret, long time) {
        return gAuth.getTotpPassword(secret, time);
    }

    @Override
    public int getTotpPasswordOfUser(String userName) {
        return gAuth.getTotpPasswordOfUser(userName);
    }

    @Override
    public int getTotpPasswordOfUser(String userName, long time) {
        return gAuth.getTotpPasswordOfUser(userName, time);
    }

    @Override
    public boolean authorize(String secret, int verificationCode) throws GoogleAuthenticatorException {
        return gAuth.authorize(secret, verificationCode);
    }

    @Override
    public boolean authorize(String secret, int verificationCode, long time) throws GoogleAuthenticatorException {
        return gAuth.authorize(secret, verificationCode, time);
    }

    @Override
    public boolean authorizeUser(String userName, int verificationCode) throws GoogleAuthenticatorException {
        return gAuth.authorizeUser(userName, verificationCode);
    }

    @Override
    public boolean authorizeUser(String userName, int verificationCode, long time) throws GoogleAuthenticatorException {
        return gAuth.authorizeUser(userName, verificationCode, time);
    }

    @Override
    public ICredentialRepository getCredentialRepository() {
        return gAuth.getCredentialRepository();
    }

    @Override
    public void setCredentialRepository(ICredentialRepository repository) {
        gAuth.setCredentialRepository(repository);
    }
}
