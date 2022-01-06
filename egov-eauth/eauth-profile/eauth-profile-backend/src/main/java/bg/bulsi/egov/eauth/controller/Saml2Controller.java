package bg.bulsi.egov.eauth.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.impl.XSStringImpl;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import bg.bulsi.egov.eauth.services.Saml2Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class Saml2Controller {

	@Autowired
	private Saml2Service service;
	
	@GetMapping("/")
	public String home(Model model) throws TransformerException, SAXException, IOException,
			ParserConfigurationException, UnmarshallingException, DecryptionException {

		String username = null;
		String identifier = null;
		String idpName = null;

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		Saml2Authentication saml2Auth = (Saml2Authentication) auth;

		boolean authenticated = saml2Auth.isAuthenticated();
		log.info("authenticated: [{}]", authenticated);

		idpName = saml2Auth.getName();

		Collection<GrantedAuthority> authorities = saml2Auth.getAuthorities();
		log.info("authorities: [{}]", Arrays.toString(authorities.toArray()));

		String saml2ResponseXml = saml2Auth.getSaml2Response();
		log.info("saml2ResponseXml:");
		printXml(saml2ResponseXml);

		Response saml2Response = service.getSaml2Response(saml2ResponseXml);

		Issuer issuer = saml2Response.getIssuer();
		log.info("issuer: [{}]", issuer.getValue());
		Status status = saml2Response.getStatus();
		log.info("statusCode: [{}]", status.getStatusCode().getValue());
		
		List<Attribute> attritbutes = service.getAttributeStatement(saml2Response).getAttributes();
		for (Attribute attr : attritbutes) {
			if (attr.getName().contains("personName")) {
				username = ((XSStringImpl) attr.getAttributeValues().get(0)).getValue();
			} else if (attr.getName().contains("personIdentifier")) {
				String identValue = ((XSStringImpl) attr.getAttributeValues().get(0)).getValue();
				identifier = identValue.substring(identValue.indexOf('-') + 1);
			}
		}

		log.info("idpName: [{}]", idpName);
		model.addAttribute("idpName", idpName);
		log.info("username: [{}]", username);
		model.addAttribute("username", username);
		log.info("identifier: [{}]", identifier);
		model.addAttribute("identifier", identifier);

		return "index";
	}

	private void printXml(String xml) throws TransformerException, UnsupportedEncodingException, SAXException,
			IOException, ParserConfigurationException {
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse((new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8")))));

		// Remove whitespaces outside tags
		document.normalize();

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		// initialize StreamResult with File object to save to file
		StreamResult result = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(document);
		transformer.transform(source, result);
		String xmlString = result.getWriter().toString();
		System.out.println(xmlString);
	}

	
}
