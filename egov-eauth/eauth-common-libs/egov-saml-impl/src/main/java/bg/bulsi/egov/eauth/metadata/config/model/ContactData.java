package bg.bulsi.egov.eauth.metadata.config.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class ContactData implements Serializable {

	private static final long serialVersionUID = -8327458792336659441L;
	
	private String email;
    private String company;
    private String givenName;
    private String surName;
    private String phone;

}
