package bg.bulsi.egov.eauth.test.sp.security.saml;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;

import static java.nio.charset.StandardCharsets.UTF_8;

@Deprecated
// @Configuration
public class X509CredentialsConverters {

	@Component
	@ConfigurationPropertiesBinding
	public static class X509CertificateConverter implements Converter<String, X509Certificate> {
		@Override
		public X509Certificate convert(String source) {
			try {
				final CertificateFactory factory = CertificateFactory.getInstance("X.509");
				return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(source.getBytes(UTF_8)));
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	@Component
	@ConfigurationPropertiesBinding
	public static class RSAPrivateKeyConverter implements Converter<String, RSAPrivateKey> {
		@Override
		public RSAPrivateKey convert(String source) {
			return RsaKeyConverters.pkcs8().convert(new ByteArrayInputStream(source.getBytes(UTF_8)));
		}
	}
}