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
public class RepoTest extends AbstractTestNGSpringContextTests  {
	
	@Autowired
	private IdentityRepository identityRepository;

	@BeforeGroups(groups = {"repoTest"})
	private void init() {

		log.info("----------------- REPO TEST --------");
		
		Identity entity = new Identity();
		entity.setId(1L);
		entity.setActive(true);
		entity.setEditDate(null);
		entity.setNames("Админ Админ");
		entity.setNid("1010101010");
		entity.setPassword("$2a$10$KzZ7T6NnymJ8CzSRW.QsLe852FZ5TsghxX/T6XC7GqpT/XnWTouL2");
		entity.setUsername("admin");
		entity.setPhone("+359888888888");
		entity.setEmail("admin@bul-si.bg");
		identityRepository.save(entity);
	}
	
	@Test(groups = {"repoTest"})
	public void testRepo() {
		
//	 Mockito.
//		when(identityRepository.findAll()).thenReturn(new ArrayList<Identity>());
		
		Optional<Identity> res = identityRepository.findByNid("1010101010");
		
		String mail = null;
		if (res.isPresent()) {			
			log.info("--> Result : " + res.get().getEmail());
			mail = res.get().getEmail();
		} else {
			assert(false);
		}
		
		assertEquals(mail, "admin@bul-si.bg");
		
	}
	
}
