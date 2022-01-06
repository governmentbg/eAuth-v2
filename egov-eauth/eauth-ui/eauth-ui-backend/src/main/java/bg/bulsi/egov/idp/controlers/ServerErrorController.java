package bg.bulsi.egov.idp.controlers;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ServerErrorController implements ErrorController {

	@Autowired
	private ErrorAttributes errorAttributes;

	@RequestMapping("/error")
	public String handleError(Model model, 
			HttpServletRequest request) {

		ServletWebRequest servletWebRequest = new ServletWebRequest(request);
	    
		Set<Include> includes = new HashSet<>();
	    includes.add(Include.STACK_TRACE);
	    includes.add(Include.MESSAGE);
	    includes.add(Include.EXCEPTION);
	    includes.add(Include.BINDING_ERRORS);
		ErrorAttributeOptions options =  ErrorAttributeOptions.of(includes);
		
		Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(servletWebRequest, options);
	    final StringBuilder errorDetails = new StringBuilder();
	    errorAttributes.forEach((attribute, value) -> {
	        errorDetails.append("<tr><td>")
	                    .append(attribute)
	                    .append("</td><td><pre>")
	                    .append(value)
	                    .append("</pre></td></tr>");
	      });
		
	    errorAttributes.forEach((key, value) -> log.error(key + " : " + value));
	    
	    errorAttributes.forEach((key, value) -> model.addAttribute(key, value));
		
		return "error";
	}

/*	 
	@RequestMapping("/error")
	public ResponseEntity<Map<String, Object>> error(HttpServletRequest aRequest) {

		ServletWebRequest webRequest = new ServletWebRequest(aRequest);
		Map<String, Object> result = this.errorAttributes.getErrorAttributes(webRequest, true);

		HttpStatus statusCode = INTERNAL_SERVER_ERROR;
		Object status = result.get("status");

		if (status instanceof Integer) {
			statusCode = HttpStatus.valueOf((Integer) status);
		}

		return new ResponseEntity<>(result, statusCode);

	}
*/
	
	@Override
	public String getErrorPath() {
		return "/error";
	}
}