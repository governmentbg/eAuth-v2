package bg.bulsi.ocspclient.common;

import bg.bulsi.ocspclient.exception.OCSPClientConfigurationException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class OCSPClientConfiguration {

	private static Properties configuration = null; 
    
    
    private OCSPClientConfiguration() {
        super();
    }
	
	
    private static void readConfiguration() throws OCSPClientConfigurationException {
    	String configurationPath = getConfigurationFilePath();
    	
    	if (configurationPath != null) {
    		File file = new File(configurationPath);
    		
    		if (!file.exists()) {
				return;
			}
    		
			InputStream inputStream = null;
			try {
				inputStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
    		try {
    			configuration = new Properties();
				configuration.load(inputStream);
			
    		} catch (IOException e) {
    			configuration = null;
    			e.printStackTrace();
			
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
    }
    
    
    private static String getParameter(String propertyKey) throws OCSPClientConfigurationException {
        if (configuration == null) {
        	readConfiguration();
        }
        
        String propertyValue = configuration.getProperty(propertyKey);
        return propertyValue;
    }
    
    
    private static String getConfigurationFilePath() throws OCSPClientConfigurationException {
		String configurationFilePath = System.getProperty(OCSPClientConstants.CONFIGURATION_FILE_PATH);
		
		if (configurationFilePath == null) {
			configurationFilePath = System.getenv(OCSPClientConstants.CONFIGURATION_FILE_PATH);
		}
		
    	if (configurationFilePath == null) {
			throw new OCSPClientConfigurationException("The configuration file defined with this system property" + OCSPClientConstants.CONFIGURATION_FILE_PATH + " can not be found, check if this property exists!");
		}
		
		return configurationFilePath;
	}
    
    
	public static boolean getEnableCheckValidityByPeriod() throws OCSPClientConfigurationException {
		String checkValidityByPeriodString = getParameter(OCSPClientConstants.ENABLE_CHECK_CERTIFICATE_VALIDITY_BY_PERIOD);
		
		Boolean toCheckValidityByPeriod = Boolean.valueOf(checkValidityByPeriodString);
		return toCheckValidityByPeriod;
	}
	
	
	public static String getTruststoreType() throws OCSPClientConfigurationException {
		String truststoreType = getParameter(OCSPClientConstants.TRUSTSTORE_TYPE);
		return truststoreType;
	}
	
	
	public static String getTruststoreFilePath() throws OCSPClientConfigurationException {
		String truststoreFilePath = getParameter(OCSPClientConstants.TRUSTSTORE_FILE_PATH);
		return truststoreFilePath;
	}
	
	
	public static String getTruststorePassword() throws OCSPClientConfigurationException {
		String truststorePassword = getParameter(OCSPClientConstants.TRUSTSTORE_PASSWORD);
		return truststorePassword;
	}


	public static String getOcspUrlByProvider(String providerName) throws OCSPClientConfigurationException {
		String configurationKey = OCSPClientConstants.OCSP_URL_PREFIX + providerName.toUpperCase();
		String ocspUrl = getParameter(configurationKey);
		return ocspUrl;
	}
	
	
	public static String getTestCertificatesPath() throws OCSPClientConfigurationException {
		String testCertificatesPath = getParameter(OCSPClientConstants.TEST_CERTIFICATES_PATH);
		return testCertificatesPath;
	}

	
	public static List<String> getSelfSignedProvidersList() throws OCSPClientConfigurationException {
		String selfSignedProviderListString = getParameter(OCSPClientConstants.SELF_SIGNED_PROVIDER_LIST);
		
		String[] selfSignedProvidersArray = selfSignedProviderListString.split(";");
		List<String> selfSignedProviderList = new ArrayList<String>();
		
		for (int i = 0; i < selfSignedProvidersArray.length; i++) {
			String selfSignedProvider = selfSignedProvidersArray[i].trim();
			selfSignedProviderList.add(selfSignedProvider);
		}
		
		return selfSignedProviderList;
	}
	
}
