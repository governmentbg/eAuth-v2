package bg.bulsi.egov.eauth.tfa.totp.service.totp;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.warrenstrange.googleauth.ICredentialRepository;

import bg.bulsi.egov.eauth.model.User;
import bg.bulsi.egov.eauth.model.repository.UserRepository;

@Service
public class TotpCredentialRepositoryService implements ICredentialRepository {

    private final UserRepository userRepository;

    public TotpCredentialRepositoryService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String getSecretKey(String userName) {
        return getSecretByPersonId(userName);
    }

    @Override
    public void saveUserCredentials(String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {
        persistsUserSecret(userName, secretKey);
    }

    @Transactional
    protected String getSecretByPersonId(String personId) {

        User user = getUser(personId);
        if (!user.getAttributes().containsKey(User.TOTP_SECRET))
            throw new EntityNotFoundException("Missing " + User.TOTP_SECRET + " for user with a personId: " + personId);

        return user.getAttributes().get(User.TOTP_SECRET);

    }

    @Transactional
    protected void persistsUserSecret(String personId, String secretKey) {

        User user = getUser(personId);

        user.getAttributes().put(User.TOTP_SECRET, secretKey);

        this.userRepository.saveAndFlush(user);
    }

    private User getUser(String personId) {
        return userRepository.findByPersonID(personId).orElseThrow(() -> new EntityNotFoundException("PersonId: " + personId));
    }
}
