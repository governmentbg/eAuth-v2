package bg.bulsi.egov.eauth.services.profile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.xml.sax.SAXException;

import bg.bulsi.egov.eauth.audit.model.DataKeys;
import bg.bulsi.egov.eauth.audit.model.EventTypes;
import bg.bulsi.egov.eauth.audit.util.EventBuilder;
import bg.bulsi.egov.eauth.audit.util.HttpReqRespUtils;
import bg.bulsi.egov.eauth.model.repository.UserRepository;
import bg.bulsi.egov.eauth.profile.rest.api.ApiException;
import bg.bulsi.egov.eauth.profile.rest.api.UserApiDelegate;
import bg.bulsi.egov.eauth.profile.rest.api.dto.FullProfile;
import bg.bulsi.egov.eauth.profile.rest.api.dto.User;
import bg.bulsi.egov.eauth.services.Saml2Service;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@Service
public class UserProfileService implements UserApiDelegate {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final Saml2Service service;
	private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public UserProfileService(UserRepository userRepository, ModelMapper modelMapper, Saml2Service service, ApplicationEventPublisher applicationEventPublisher) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.service = service;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    private String getEncryptedPersonId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Saml2Authentication saml2Auth = (Saml2Authentication) authentication;
		Response saml2Response = null;
		try {
			saml2Response = service.getSaml2Response(saml2Auth.getSaml2Response());
		} catch (ParserConfigurationException | SAXException | IOException | UnmarshallingException e) {
			log.error(e.getMessage());
		}
		String nid = service.getNidFromSaml2Response(saml2Response);
        log.info("nid: [{}]", nid);
        
        String encryptedNid = service.encrypted(nid);
        log.info("encryptedNid: [{}]", encryptedNid);
        
        return encryptedNid;
        // return ((Jws<Claims>) authentication.getPrincipal()).getBody().getSubject();
    }

    @Transactional
    @Override
    public ResponseEntity<Void> createProfile(User user) throws ApiException {		
    	
    	ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    	HttpServletRequest request = attr.getRequest();

        if (user.getId() == null) {

            bg.bulsi.egov.eauth.model.User userToCreate = modelMapper.map(user, bg.bulsi.egov.eauth.model.User.class);
            String personIdEncrypted = getEncryptedPersonId();

            userToCreate.setPersonID(personIdEncrypted);

            bg.bulsi.egov.eauth.model.User newUser = userRepository.save(userToCreate);
            
			/*
			 * AuditEvent
			 */  
			AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
					.type(EventTypes.CREATE_2FA_PROFILE)
					.data(DataKeys.SOURCE, this.getClass().getName())
		    		.data(DataKeys.DB_MODEL, newUser.getId()) //personIdEncrypted
					.build();
    		applicationEventPublisher.publishEvent(auditApplicationEvent);

            return ResponseEntity.status(201).build();
        }

        Optional<bg.bulsi.egov.eauth.model.User> userExist = userRepository.findById(user.getId());

        if (userExist.isPresent()) {
            modelMapper.map(user, userExist.get());
            bg.bulsi.egov.eauth.model.User updatedUser = userRepository.save(userExist.get());
            
    		/*
    		 * AuditEvent
    		 */
			AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
					.type(EventTypes.UPDATE_2FA_PROFILE)
					.data(DataKeys.SOURCE, this.getClass().getName())
		    		.data(DataKeys.DB_MODEL, updatedUser.getId()) //personIdEncrypted
					.build();
    		applicationEventPublisher.publishEvent(auditApplicationEvent);

            return ResponseEntity.status(200).build();

        } else {
             String message = "Error creating profile";
             throw new ApiException(1, message);
        }
    }

    @Transactional
    @Override
    public ResponseEntity<FullProfile> getProfile() throws ApiException {

        String personIdEncrypted = getEncryptedPersonId();

        Optional<bg.bulsi.egov.eauth.model.User> userEntity = userRepository.findByPersonID(personIdEncrypted);
        if (userEntity.isPresent()) {

            FullProfile userRes = modelMapper.map(userEntity.get(), bg.bulsi.egov.eauth.profile.rest.api.dto.FullProfile.class);

            return ResponseEntity.ok().body(userRes);
        }

        return ResponseEntity.notFound().build();
    }

}
