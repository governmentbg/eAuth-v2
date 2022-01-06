package bg.bulsi.egov.eauth.tfa.sms.data.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import bg.bulsi.egov.eauth.tfa.sms.service.SmsService;
import bg.bulsi.egov.eauth.tfa.util.SmsUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ScheduledTasks {

	@Autowired
	private SmsService service;
	
	@Value("${egov.eauth.sys.tfa.sms.cron.enabled:false}")
	private boolean enabled;
	
	@Value("${egov.eauth.sys.tfa.sms.otp.delete.period.days:30}")
	private int days;
	
	
	/**
	 * The pattern is a list of six single space-separated fields: representing
	 * second, minute, hour, day, month, weekday. Month and weekday names can be
	 * given as the first three letters of the English names.
	 * Meanings: (*) - match any; (*\/X) - means "every X"; (?) - no specific value
	 * current: delete on first day of every month
	 */
	@Scheduled(cron = "${egov.eauth.sys.tfa.sms.cron.expression:0 0 0 1 * ?}")
	public void deleteSms() {
		if (enabled) {
			log.info("deleting from Sms table entries older than {} days...", days);
			service.deleteAllSmsCreatedBefore(SmsUtils.getDateDaysAgoFromToday(days));
		}
	}
}
