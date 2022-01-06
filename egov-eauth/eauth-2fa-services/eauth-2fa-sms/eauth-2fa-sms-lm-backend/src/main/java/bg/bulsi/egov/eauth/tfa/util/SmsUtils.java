package bg.bulsi.egov.eauth.tfa.util;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;

public class SmsUtils {
	
	private SmsUtils() {
		throw new IllegalStateException("Utility class");
	}
	
	private static final int DEFAULT_TOKEN_LENGTH = 6;

	public static String generateCode() {
		return RandomStringUtils.randomNumeric(DEFAULT_TOKEN_LENGTH);
	}

	public static String generateTransactionId() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	public static Date getDateDaysAgoFromToday(int days) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -days);
		return cal.getTime();
	}
	
	public static boolean codeNotExpired(Date createDate, long expirationTimeInSeconds) {
		Date now = new Date();
		return now.before(new Date(createDate.getTime() + (expirationTimeInSeconds * 1000)));
	}
}
