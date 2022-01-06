package bg.bulsi.egov.eauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import bg.bulsi.egov.eauth.model.Address;
import bg.bulsi.egov.eauth.model.Preferred2FA;
import bg.bulsi.egov.eauth.model.User;
import bg.bulsi.egov.eauth.model.ekkate.Ekatte;
import bg.bulsi.egov.eauth.model.repository.AddressRepository;
import bg.bulsi.egov.eauth.model.repository.EkatteRepository;
import bg.bulsi.egov.eauth.model.repository.UserRepository;
import bg.bulsi.egov.security.utils.PersonalIdUtils;

@Test
@SpringBootTest(classes = EauthProfileApplication.class)
public class UserTestNg extends AbstractTestNGSpringContextTests {

	@Autowired
	UserRepository userRepository;

	@Autowired
	AddressRepository addressRepository;

	@Autowired
	EkatteRepository ekatteRepository;

	@Test(priority = 1, enabled = false)
	public void userTestPersist() {
		User user = new User();
		Address address = new Address();
		Ekatte ekatte = new Ekatte();

		if (true) {
			ekatte = ekatteRepository.getOne("00007");

			address.setAddressDescription("Ulica 2");
			address.setPostalCode("1000");
			address.setEkatte(ekatte);

		}

		user.setName("name6ng");
		user.setAddress(address);
		user.setEmail("email@mail.com");
		user.setPersonID("80007286274");
		user.setPreferred(Preferred2FA.SMS);
		user.setPhoneNumber("777777777");

		User savedUser = userRepository.save(user);

//		assertThat(savedUser.getAddress().getId().toString()).isNotEmpty();
		assertThat(savedUser.getId().toString()).isNotEmpty();

	}
	@Test(priority = 3)
	public void userTestEncription() {
		User user = new User();
		Address address = new Address();
		Ekatte ekatte = new Ekatte();
		if (true) {
			ekatte = ekatteRepository.getOne("00007");
			address.setAddressDescription("Ulica 2");
			address.setPostalCode("1000");
			address.setEkatte(ekatte);
		}
		user.setName("name6ng");
		user.setAddress(address);
		user.setEmail("email@mail.com");
		user.setPersonID("80007286274");
		user.setPreferred(Preferred2FA.SMS);
		user.setPhoneNumber("777777777");
		System.out.println("["+user.getPersonID()+"]->["+PersonalIdUtils.encrypt(user.getPersonID(),"NWKNjXLRdRNjhjgTcRssMmMskbxFrRJWvjWMw7wbVCcHJxKtvf74KzrJRg4hfFhj")+"]");

	}

	@Test(priority = 2, enabled = false)
	public void userTestRead() {
		Optional<User> userOpt = userRepository.findByPersonID("2010101015");

		User user = null;

		if (userOpt.isPresent()) {
			user = userOpt.get();
		}

		assertThat(user.getId().toString()).isNotEmpty();
	}


	@Test(priority = 3, enabled = false)
	public void userTestDelete() {

		Optional<User> userOpt = userRepository.findByPersonID("2010101015");

		User user = null;

		if (userOpt.isPresent()) {
			user = userOpt.get();
			userRepository.delete(user);
		}

		userOpt = null;
		userOpt = userRepository.findByPersonID("2010101015");

		assertFalse(userOpt.isPresent());

	}

}
