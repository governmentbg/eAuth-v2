package bg.bulsi.egov.eauth.tfa.email.services;

import bg.bulsi.egov.eauth.tfa.email.utils.TokenGenerator;
import bg.bulsi.egov.eauth.tfa.email.utils.TwoFAToken;
import bg.bulsi.egov.hazelcast.config.HazelcastConfiguration;
import bg.bulsi.egov.hazelcast.service.HazelcastService;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

@Slf4j
public class TokenService {

    private final IMap<String, TwoFAToken> tokenStore;

    private final HazelcastService hazelcastService;

    public TokenService(final HazelcastInstance hazelcastInstance, final HazelcastService hazelcastService) {
        this.tokenStore = hazelcastInstance.getMap(HazelcastConfiguration.EMAIL_MAP);
        this.hazelcastService = hazelcastService;
    }

    synchronized TwoFAToken generate(String eMail) {

    	int count = Integer.parseInt(hazelcastService.get("egov.eauth.sys.tfa.email.otp.code_length"));
        TwoFAToken token = TokenGenerator.generateToken(count);

        persistsToken(eMail, token);

        return token;
    }

    private void persistsToken(String eMail, TwoFAToken token) {
        token.setEMail(eMail);
        tokenStore.put(token.getTransactionId(), token);
    }

    synchronized TwoFAToken removeToken(final String transactionId){
        if(isTokenNotExpired(transactionId)) return tokenStore.get(transactionId);
        return tokenStore.remove(transactionId);
    }

    synchronized boolean validateToken(String transactionId, String code){

        // check if transaction exists
        if(!tokenStore.containsKey(transactionId)) return false;
        return validateTokenInternal(removeToken(transactionId), code);
    }

    private boolean isTokenNotExpired(String transactionId){

        TwoFAToken token =  tokenStore.get(transactionId);
        int validationIntervalInSeconds = Integer.parseInt(hazelcastService.get("egov.eauth.dyn.tfa.email.otp.expiration"));
        if(token != null) {
            return ((token.getTimeStamp() + validationIntervalInSeconds * 1000) > new Date().getTime());
        }

        return false;
    }

    private boolean validateTokenInternal(TwoFAToken token, String code){
    	int validationIntervalInSeconds = Integer.parseInt(hazelcastService.get("egov.eauth.dyn.tfa.email.otp.expiration"));
        // check for token expiration
        if(  (token.getTimeStamp() + validationIntervalInSeconds * 1000) > new Date().getTime()) {
            // check code validity
            return StringUtils.isNotBlank(code) &&
                    StringUtils.equalsIgnoreCase(token.getCode().trim(), code.trim());
        }
        return  false;
    }
}
