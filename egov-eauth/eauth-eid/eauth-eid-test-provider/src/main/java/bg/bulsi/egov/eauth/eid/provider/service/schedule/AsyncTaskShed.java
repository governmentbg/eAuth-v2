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


	@Async
	public void  asyncMethod() { // reurn type Future<InquiryResult> || CompletableFuture<T>
		log.debug("running task in thread: " + Thread.currentThread().getName());

		//EidService eidService = new EidService();
		eidService.poolInMemoryToIdentityInquiry();
		
		//return new AsyncResult<InquiryResult>(res.getBody());

	}
}
