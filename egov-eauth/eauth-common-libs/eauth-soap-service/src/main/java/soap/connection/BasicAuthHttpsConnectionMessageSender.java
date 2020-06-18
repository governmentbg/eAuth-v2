package soap.connection;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.http.HttpHeaders;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;

public class BasicAuthHttpsConnectionMessageSender extends HttpsUrlConnectionMessageSender {

	private String b64Creds;

	public BasicAuthHttpsConnectionMessageSender(String username, String password) {
		b64Creds = Base64.getUrlEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
	}

	@Override
	protected void prepareConnection(HttpURLConnection connection) throws IOException {
		connection.setRequestProperty(HttpHeaders.AUTHORIZATION, String.format("Basic %s", b64Creds));
		super.prepareConnection(connection);
	}
	
}
