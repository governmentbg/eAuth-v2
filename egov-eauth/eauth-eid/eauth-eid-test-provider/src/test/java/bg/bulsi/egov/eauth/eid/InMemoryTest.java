package bg.bulsi.egov.eauth.eid;

import static org.testng.Assert.assertEquals;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import bg.bulsi.egov.eauth.eid.provider.MainApplication;
import bg.bulsi.egov.eauth.eid.provider.model.Identity;
import bg.bulsi.egov.eauth.eid.provider.model.repository.IdentityRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Test(groups = {"pause"})
@SpringBootTest(classes = MainApplication.class)
public class InMemoryTest extends AbstractTestNGSpringContextTests {

	@Autowired
	IdentityRepository identityRepository;

	@BeforeGroups(groups = {"inMemoryTest"})
	private void init() {

		log.info("--------------- IN-MEMORY TEST -------------------");
		
		Identity entity = new Identity();
		entity.setId(1L);
		entity.setNid("1010101010");
		identityRepository.save(entity);
	}
	
	@Test(groups = {"inMemoryTest"})
	public void memTest() {
		Optional<Identity> identityOpt = identityRepository.findById(1L);
		
		log.info("--> " + identityOpt.get().getNid());
		assertEquals(identityOpt.isPresent(), true);
	}
	
}
