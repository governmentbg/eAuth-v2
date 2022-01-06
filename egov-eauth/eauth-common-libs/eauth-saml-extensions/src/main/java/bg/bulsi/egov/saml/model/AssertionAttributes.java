package bg.bulsi.egov.saml.model;

import lombok.Getter;

public enum AssertionAttributes {
	
	//required
	PERSON_IDENTIFIER("identificationNumber","urn:oasis:names:tc:saml2:2.0:attrname-format:uri","egov:UniqueIdentifierType","urn:egov:bg:eauth:2.0:attributes:personIdentifier",true), //"PersonIdentifier"
	PERSON_NAME("name","urn:oasis:names:tc:saml2:2.0:attrname-format:uri","egov:CurrentNameType","urn:egov:bg:eauth:2.0:attributes:personName",true), //"PersonName"
	//optional
	EMAIL("email","urn:oasis:names:tc:saml2:2.0:attrname-format:uri","egov:ContactEmailType","urn:egov:bg:eauth:2.0:attributes:email",false), //"Email"
	PHONE("phone","urn:oasis:names:tc:saml2:2.0:attrname-format:uri","egov:ContactPhoneType","urn:egov:bg:eauth:2.0:attributes:phone",false),//"Phone"
	LATIN_NAME("latinName","urn:oasis:names:tc:saml2:2.0:attrname-format:uri","egov:LatinCurrentNameType","urn:egov:bg:eauth:2.0:attributes:latinName",false),//"LatinName"
	BIRTH_NAME("birthName","urn:oasis:names:tc:saml2:2.0:attrname-format:uri","egov:BirthNameType","urn:egov:bg:eauth:2.0:attributes:birthName",false),//"BirthName"
	DATE_OF_BIRTH("dateOfBirth","urn:oasis:names:tc:saml2:2.0:attrname-format:uri","egov:DateOfBirthType","urn:egov:bg:eauth:2.0:attributes:dateOfBirth",false),//"DateOfBirth"
	GENDER("gender","urn:oasis:names:tc:saml2:2.0:attrname-format:uri","egov:GenderType","urn:egov:bg:eauth:2.0:attributes:gender",false),//"Gender"
	PLACE_OF_BIRTH("placeOfBirth","urn:oasis:names:tc:saml2:2.0:attrname-format:uri","egov:PlaceOfBirthType","urn:egov:bg:eauth:2.0:attributes:placeOfBirth",false),//"PlaceOfBirth"
	CERTIFICATE("X509","urn:oasis:names:tc:saml2:2.0:attrname-format:uri","egov:X509","urn:egov:bg:eauth:2.0:attributes:X509",false),//"Certificate X.509"
	CANNONICAL_RESIDENCE_ADDRESS("canonicalResidenceAddress","urn:oasis:names:tc:saml2:2.0:attrname-format:uri","egov:CanonicalResidenceAddressType","urn:egov:bg:eauth:2.0:attributes:canonicalResidenceAddress",false);//"CanonicalResidenceAddress"

	@Getter
	private String friendlyName;
	
	@Getter
	private String nameFormat;

	@Getter
	private String attrValue;

	@Getter
	private String eidUrn;

	@Getter
	private boolean required;
	
	
	private AssertionAttributes(String friendlyName, String nameFormat, String attrValue, String eidUrn, boolean required) {
		this.friendlyName = friendlyName;
		this.nameFormat = nameFormat;
		this.attrValue = attrValue;
		this.eidUrn = eidUrn;
		this.required = required;
	}
	
	public static AssertionAttributes fromUrn(String urn) {
        for (AssertionAttributes assertionAttribute: AssertionAttributes.values()) {
            if (assertionAttribute.eidUrn.equals(urn)) {
                return assertionAttribute;
            }
        }
        throw new IllegalArgumentException(urn);
    }
	
}
