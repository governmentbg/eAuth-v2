package bg.bulsi.egov.eauth.tfa.email.utils;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;

public class TokenGenerator {

    private static final int DEFAULT_TOKEN_LENGTH = 6;

    public static TwoFAToken generateToken(){
        return generateToken(DEFAULT_TOKEN_LENGTH);
    }

    public static TwoFAToken generateToken(final int count){

        //long unixTime = Instant.now().getEpochSecond()
        long unixTime = new Date().getTime();

        return new TwoFAToken(
                UUID.randomUUID().toString().replace("-", ""),
                "",
                generate(count),
                unixTime
        );
    }
    private static String generate(final int count){
        return RandomStringUtils.randomNumeric(count);
    }
}
