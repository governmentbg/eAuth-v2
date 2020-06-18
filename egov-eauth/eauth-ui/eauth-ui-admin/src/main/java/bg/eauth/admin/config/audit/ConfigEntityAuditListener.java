package bg.eauth.admin.config.audit;

import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import bg.eauth.admin.domain.Config;

public class ConfigEntityAuditListener {
	
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
	
    	
    public void postPersist(Config target) { // Persistence logic 
    	
    	
    }

    @PostUpdate
    public void postUpdate(Config target) { //Updation logic 
    }

    @PostRemove
    public void postRemove(Config target) { //Removal logic 
    	
    }
}
