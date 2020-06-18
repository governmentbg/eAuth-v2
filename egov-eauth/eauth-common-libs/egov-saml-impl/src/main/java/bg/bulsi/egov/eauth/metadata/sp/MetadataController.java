package bg.bulsi.egov.eauth.metadata.sp;
import org.joda.time.DateTime;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.*;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.Signature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.Element;

import bg.bulsi.egov.eauth.common.xml.MetadataBuilderFactoryUtil;
import bg.bulsi.egov.eauth.saml.OpenSamlImplementation;
import bg.bulsi.egov.eauth.saml.SAMLBuilder;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.List;
import org.opensaml.core.xml.*;


public class MetadataController {

// https://medium.com/@sagarag/reloading-saml-why-do-you-need-metadata-3fbeb43320c3
	//private final OpenSamlImplementation saml = OpenSamlImplementation.getInstance();

	@Value("$server.port")
	String localPort;
	/**
     *
     * IDPSSODescriptor
     * трябва задължително да има :
     *
     * 1  the public key(s) used by the IdP for authentication and encryption
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
     */

    @RequestMapping(method = RequestMethod.GET, value = "/metadata", produces = "application/xml")
    public String metadata() throws SecurityException , ParserConfigurationException, TransformerException, MarshallingException {

        //Entity Descriptor ако искаме да си генерираме метадата за ИП
        EntityDescriptor entityDescriptor = MetadataBuilderFactoryUtil.buildXmlSAMLObject(EntityDescriptor.class);
        String entityId = "34534545";
        entityDescriptor.setEntityID(entityId);
        entityDescriptor.setID(SAMLBuilder.randomSAMLId());
        entityDescriptor.setValidUntil(new DateTime().plusMillis(86400000));


        Signature signature = MetadataBuilderFactoryUtil.buildXmlSAMLObject(Signature.class);

        IDPSSODescriptor idpssoDescriptor = MetadataBuilderFactoryUtil.buildXmlSAMLObject(IDPSSODescriptor.class);


        NameIDFormat nameIDFormat = MetadataBuilderFactoryUtil.buildXmlSAMLObject(NameIDFormat.class);
        nameIDFormat.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");
        idpssoDescriptor.getNameIDFormats().add(nameIDFormat);

        idpssoDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);




        SingleSignOnService singleSignOnService = MetadataBuilderFactoryUtil.buildXmlSAMLObject(SingleSignOnService.class);
        singleSignOnService.setLocation("http://localhost:" + localPort + "/SingleSignOnService");
        singleSignOnService.setBinding(SAMLConstants.SAML2_POST_BINDING_URI);
        idpssoDescriptor.getSingleSignOnServices().add(singleSignOnService);
        //TODO Use Builder Options
        entityDescriptor.getAdditionalMetadataLocations();
        X509KeyInfoGeneratorFactory keyInfoGeneratorFactory = new X509KeyInfoGeneratorFactory();
        keyInfoGeneratorFactory.setEmitEntityCertificate(true);
        KeyInfoGenerator keyInfoGenerator = keyInfoGeneratorFactory.newInstance();

        KeyDescriptor encKeyDescriptor = MetadataBuilderFactoryUtil.buildXmlSAMLObject(KeyDescriptor.class);
        encKeyDescriptor.setUse(UsageType.SIGNING);

        idpssoDescriptor.getKeyDescriptors().add(encKeyDescriptor);

        List<Endpoint> allEndponts = idpssoDescriptor.getEndpoints();


        entityDescriptor.getRoleDescriptors().add(idpssoDescriptor);


        return writeEntityDescriptor(entityDescriptor);

    }

    private String writeEntityDescriptor(EntityDescriptor entityDescriptor) throws ParserConfigurationException, TransformerException, MarshallingException {

        MarshallerFactory marshallerFactory = new MarshallerFactory();
        Marshaller marshaller =  marshallerFactory.getMarshaller(entityDescriptor);
        Element el =   marshaller.marshall(entityDescriptor);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(el);
        StreamResult result = new StreamResult(new StringWriter());



        transformer.transform(source, result);

        String strObject = result.getWriter().toString();

        return result.getWriter().toString();
    }



}
