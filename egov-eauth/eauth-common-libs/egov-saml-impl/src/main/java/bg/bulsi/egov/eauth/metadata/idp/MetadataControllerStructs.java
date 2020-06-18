//package bg.bulsi.egov.eauth.metadata.idp;
//import javax.net.ssl.KeyManager;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.env.Environment;
//
//import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
//
//public class MetadataControllerStructs {
//	
//	  @Autowired
//	  private KeyManager keyManager;
//
//	  @Autowired
//	  private IdpConfigurationProperties idpConfiguration;
//
//	  @Autowired
//	  Environment environment;

	/*
	 * <a href="https://medium.com/@sagarag/reloading-saml-why-do-you-need-metadata-3fbeb43320c3">info</a>
	 */

	/**
	 *
	 * IDPSSODescriptor
	 * трябва задължително да има :
	 *
	 * 1 the public key(s) used by the IdP for authentication and encryption
	 * 2 endpoints of various types for communicating with it
	 * 3 explicitly supported identifier formats, if any
	 * 4 explicitly supported attributes, if any
	 *
	 *
	 * @return
	 * @throws SecurityException
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws MarshallingException
	 * @throws org.opensaml.security.SecurityException 
	 * @throws org.opensaml.xml.security.SecurityException 
	 */

////	@RequestMapping(method = RequestMethod.GET, value = "/metadata2", produces = "application/xml")
//	public String metadata() throws SecurityException, ParserConfigurationException, TransformerException, MarshallingException, org.opensaml.security.SecurityException, org.opensaml.xml.security.SecurityException {
//
//		EntitiesDescriptor entitiesDescriptor = SAMLBuilder.buildSAMLObject(EntitiesDescriptor.class, EntitiesDescriptor.DEFAULT_ELEMENT_NAME);
//
//		/*
//		 * IDP Descriptor
//		 */
//		EntityDescriptor entityDescriptorIdp = SAMLBuilder.buildSAMLObject(EntityDescriptor.class, EntityDescriptor.DEFAULT_ELEMENT_NAME);
//		{
//			entityDescriptorIdp.setEntityID("3rdPartyIdentifier");
//			entityDescriptorIdp.setID(SAMLBuilder.randomSAMLId());
//			entityDescriptorIdp.setValidUntil(new DateTime().plusSeconds(86400));
//			entityDescriptorIdp.setCacheDuration(new DateTime().plusSeconds(86400).getMillis());
//
//			Signature signature = SAMLBuilder.buildSAMLObject(Signature.class, Signature.DEFAULT_ELEMENT_NAME);
//			{
//
//			}
//
//			Extensions extensions;
//			{
//
//			}
//
//			RoleDescriptor roleDescriptor;
//			AffiliationDescriptor affiliationDescriptor;
//			{
//				SSODescriptor ssoDescriptor;
//				{
//					SPSSODescriptor spssoDescriptor  = SAMLBuilder.buildSAMLObject(SPSSODescriptor.class, SPSSODescriptor.DEFAULT_ELEMENT_NAME);
//					{
//						SingleLogoutService singleLogoutService;
//						{
//
//						}
//						AttributeConsumingService attributeConsumingService;
//						{
//
//						}
//						AssertionConsumerService assertionConsumerService;
//						{
//
//						}
//					}
//					IDPSSODescriptor idpssoDescriptor = SAMLBuilder.buildSAMLObject(IDPSSODescriptor.class, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
//					{
//						idpssoDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);
//						KeyDescriptor encKeyDescriptor = SAMLBuilder.buildSAMLObject(KeyDescriptor.class, KeyDescriptor.DEFAULT_ELEMENT_NAME);
//						{					
//							encKeyDescriptor.setUse(UsageType.SIGNING);
//							
//							X509KeyInfoGeneratorFactory keyInfoGeneratorFactory = new X509KeyInfoGeneratorFactory();
//							keyInfoGeneratorFactory.setEmitEntityCertificate(true);
//							KeyInfoGenerator keyInfoGenerator = keyInfoGeneratorFactory.newInstance();
//						   
//							Credential credential = null; //keyManager.resolveSingle(new CriteriaSet(new EntityIDCriteria(idpConfiguration.getEntityId())));
//						    signature.setSigningCredential(credential);
//						    signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
//						    signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
//							
//							org.opensaml.xml.signature.KeyInfo keyInfo = keyInfoGenerator.generate(credential);
//							encKeyDescriptor.setKeyInfo(keyInfo);
//							idpssoDescriptor.getKeyDescriptors().add(encKeyDescriptor);
//							
//						}
//						NameIDFormat nameIDFormat = SAMLBuilder.buildSAMLObject(NameIDFormat.class, NameIDFormat.DEFAULT_ELEMENT_NAME);
//						{							
//							nameIDFormat.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");
//							idpssoDescriptor.getNameIDFormats().add(nameIDFormat);
//						}
//
//						SingleSignOnService singleSignOnService = SAMLBuilder.buildSAMLObject(SingleSignOnService.class, SingleSignOnService.DEFAULT_ELEMENT_NAME);
//						{
//							String localPort = "8080";
//							singleSignOnService.setBinding(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
//							singleSignOnService.setLocation("http://localhost:" + localPort + "/SingleSignOnService");
//							singleSignOnService.setResponseLocation("http://localhost:" + localPort + "/SingleSignOnService");
//							
//							ArtifactResolutionService artifactResolutionService;
//							{
//								
//							}
//						}
//					}
//					
//					entityDescriptorIdp.getRoleDescriptors().add(idpssoDescriptor);
//					
//					List<Endpoint> allEndponts = idpssoDescriptor.getEndpoints();
//					allEndponts.addAll(spssoDescriptor.getEndpoints());
//				}
//				AuthnAuthorityDescriptor authnAuthorityDescriptor;
//				{
//
//				}
//				AttributeAuthorityDescriptor attributeAuthorityDescriptor;
//				{
//
//				}
//				PDPDescriptor pdpDescriptor;
//				{
//
//				}
//			}
//
//			Organization organization;
//			{
//				OrganizationName organizationName;
//				OrganizationDisplayName organizationDisplayName;
//				OrganizationURL organizationURL;
//			}
//			// ContactType
//
//			ContactPerson contactPerson;
//			{
//				GivenName givenName;
//				SurName surName;
//				EmailAddress emailAddress;
//			}
//
//			AdditionalMetadataLocation additionalMetadataLocation;
//			{
//
//			}
//
//		}
//		
//		/*
//		 * SP Descriptor
//		 */
//		EntityDescriptor entityDescriptorSp = SAMLBuilder.buildSAMLObject(EntityDescriptor.class, EntityDescriptor.DEFAULT_ELEMENT_NAME);
//		{
//			//...
//		}
//
//		entitiesDescriptor.getEntityDescriptors().add(entityDescriptorIdp);
//		entitiesDescriptor.getEntityDescriptors().add(entityDescriptorSp);
//
//		return writeEntityDescriptor(entityDescriptorIdp); // entitiesDescriptor
//
//	}
//
//
//	private String writeEntityDescriptor(EntityDescriptor entityDescriptor) throws ParserConfigurationException, TransformerException, MarshallingException {
//
//		MarshallerFactory marshallerFactory = new MarshallerFactory();
//		Marshaller marshaller = marshallerFactory.getMarshaller(entityDescriptor);
//		Element el = marshaller.marshall(entityDescriptor);
//
//		TransformerFactory transformerFactory = TransformerFactory.newInstance();
//		Transformer transformer = transformerFactory.newTransformer();
//		DOMSource source = new DOMSource(el);
//		StreamResult result = new StreamResult(new StringWriter());
//
//		transformer.transform(source, result);
//
//		String strObject = result.getWriter().toString();
//
//		return result.getWriter().toString();
//	}
//
//}
