package bg.bulsi.egov.eauth.eid.provider.service.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SchedulerTask {

    @Autowired
    private AsyncTaskShed asyncTask;

    @Scheduled(initialDelay = 15000, fixedRate = 5000)
    public void shedTask() {
    	log.debug("Scheduled AsynTask ...");
        asyncTask.asyncTask();
    }
}