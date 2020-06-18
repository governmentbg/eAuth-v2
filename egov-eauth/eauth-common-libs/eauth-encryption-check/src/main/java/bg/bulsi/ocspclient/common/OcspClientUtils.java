package bg.bulsi.ocspclient.common;

import bg.bulsi.ocspclient.exception.OCSPClientConfigurationException;
import bg.bulsi.ocspclient.exception.OCSPClientException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class OcspClientUtils {

	
	public static byte[] inputStreamToByteArray(InputStream in) throws IOException {
		byte[] buffer = new byte[2048];
		int length = 0;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		while ((length = in.read(buffer)) >= 0) {
			baos.write(buffer, 0, length);
		}

		return baos.toByteArray();
	}

	
	public static boolean isProviderInSelfSignedProviderList(String providerOrganizationName) throws OCSPClientException {
		List<String> selfSignedProvidersList = null;
		
		try {
			selfSignedProvidersList = OCSPClientConfiguration.getSelfSignedProvidersList();
		} catch (OCSPClientConfigurationException e) {
			throw new OCSPClientException("Can not find Self SignedcProviders List from configuration!", e);
		}

		boolean isProviderInList = selfSignedProvidersList.contains(providerOrganizationName);
		return isProviderInList;
	}

}
