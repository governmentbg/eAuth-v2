package bg.bulsi.egov.eauth.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class CancelAuthController {
	
	private static final String SUCCESS_STATUS_CODE = "Success";

	@PostMapping("/cancel-auth")
	public String cancelAuth(HttpServletRequest request, Model model)
			throws ParserConfigurationException, SAXException, IOException, UnmarshallingException {

		String saml2ResponseEncoded = request.getParameter("SAMLResponse");
		byte[] saml2ResponseBytes = Base64.getDecoder().decode(saml2ResponseEncoded.getBytes(StandardCharsets.UTF_8));
		String saml2ResponseXml = new String(saml2ResponseBytes, StandardCharsets.UTF_8);
		log.info("saml2ResponseXml: [{}]", saml2ResponseXml);

		Response samlResponse = getSaml2Response(saml2ResponseXml);
		
		Status status = samlResponse.getStatus();
		String statusCode = status.getStatusCode().getValue();
		log.info("statusCode: [{}]", statusCode);
		
		String message = status.getStatusMessage().getMessage();
		log.info("message: [{}]", message);
		
		model.addAttribute("message", message);

		return "cancel";
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
