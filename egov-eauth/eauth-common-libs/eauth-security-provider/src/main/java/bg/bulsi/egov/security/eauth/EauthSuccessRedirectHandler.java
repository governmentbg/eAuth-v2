package bg.bulsi.egov.security.eauth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class EauthSuccessRedirectHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public EauthSuccessRedirectHandler() {
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        // TODO
/*        String relayStateURL = "http://localhost:8082/success";

        if (relayStateURL != null) {
            log.debug("Redirecting to RelayState Url: " + relayStateURL);

           *//* response.addHeader("Access-Control-Expose-Headers", "X-JWT-Token");
            response.addHeader("X-JWT-Token", "alabala");*//*

            getRedirectStrategy().sendRedirect(request, response, relayStateURL);
            return;
        }*/

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
