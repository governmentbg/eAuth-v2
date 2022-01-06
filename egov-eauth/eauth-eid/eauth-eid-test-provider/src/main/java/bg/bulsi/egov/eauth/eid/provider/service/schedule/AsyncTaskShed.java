package bg.bulsi.egov.eauth.eid.provider.service.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import bg.bulsi.egov.eauth.eid.provider.service.EidService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableAsync
public class AsyncTaskShed {

	@Autowired
	EidService eidService;


	// return type Future<InquiryResult> || CompletableFuture<T>
	@Async
	public void  asyncTask() { 
		log.debug("running Async task 'IdentityInquiry' in thread: " + Thread.currentThread().getName());

		eidService.poolInMemoryToIdentityInquiry();
		
		//return new AsyncResult<InquiryResult>(res.getBody());
	}
	
}
