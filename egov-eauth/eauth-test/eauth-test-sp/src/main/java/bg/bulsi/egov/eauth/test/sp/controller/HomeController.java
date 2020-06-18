package bg.bulsi.egov.eauth.test.sp.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.impl.XSStringImpl;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class HomeController {
	
	private static final String SUCCESS_STATUS_CODE = "Success";
	private static final String RESPONDER_STATUS_CODE = "Responder";

	@GetMapping("/")
	public String home(HttpServletRequest request, Model model)
			throws TransformerException, SAXException, IOException, ParserConfigurationException,
			UnmarshallingException {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Saml2Authentication saml2Auth = (Saml2Authentication) auth;

		boolean authenticated = saml2Auth.isAuthenticated();
		log.info("authenticated: [{}]", authenticated);

		String idpName = saml2Auth.getName();
		log.info("idpName: [{}]", idpName);

		Collection<GrantedAuthority> authorities = saml2Auth.getAuthorities();
		log.info("authorities: [{}]", Arrays.toString(authorities.toArray()));

		String saml2ResponseXml = saml2Auth.getSaml2Response();
		log.info("saml2ResponseXml:");
		printXml(saml2ResponseXml);

		Response saml2Response = getSaml2Response(saml2ResponseXml);

		Issuer issuer = saml2Response.getIssuer();
		log.info("issuer: [{}]", issuer.getValue());
		
		Status status = saml2Response.getStatus();
		String statusCode = status.getStatusCode().getValue();
		log.info("statusCode: [{}]", statusCode);
		
		boolean loggedIn = statusCode.contains(SUCCESS_STATUS_CODE);
		log.info("loggedIn: [{}]", loggedIn);
		
		String message = null;
		StatusMessage statusMessage = status.getStatusMessage();
		if (statusMessage != null) {
			message = statusMessage.getMessage();
		}
		log.info("message: [{}]", message);

		String username = null;
		String identifier = null;

		AttributeStatement attritbuteStatement = saml2Response.getAssertions().get(0).getAttributeStatements().get(0);
		List<Attribute> attritbutes = attritbuteStatement.getAttributes();
		for (Attribute attr : attritbutes) {
			if (attr.getName().contains("personName")) {
				username = ((XSStringImpl) attr.getAttributeValues().get(0)).getValue();
			} else if (attr.getName().contains("personIdentifier")) {
				String identValue = ((XSStringImpl) attr.getAttributeValues().get(0)).getValue();
				identifier = identValue.substring(identValue.indexOf('-') + 1);
			}
		}

		log.info("username: [{}]", username);
		log.info("identifier: [{}]", identifier);
		
		model.addAttribute("loggedIn", loggedIn);
		model.addAttribute("message", message);
		model.addAttribute("username", username);
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

	private Response getSaml2Response(String saml2ResponseXml)
			throws ParserConfigurationException, SAXException, IOException, UnmarshallingException {
		ByteArrayInputStream is = new ByteArrayInputStream(saml2ResponseXml.getBytes(StandardCharsets.UTF_8));

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();

		Document document = docBuilder.parse(is);
		Element element = document.getDocumentElement();

		Response saml2Response = (Response) Objects
				.requireNonNull(XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(element))
				.unmarshall(element);

		return saml2Response;
	}
}
