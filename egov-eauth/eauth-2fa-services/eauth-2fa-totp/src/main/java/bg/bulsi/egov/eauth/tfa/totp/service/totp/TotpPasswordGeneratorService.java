package bg.bulsi.egov.eauth.tfa.totp.service.totp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.ICredentialRepository;
import com.warrenstrange.googleauth.IGoogleAuthenticator;

import bg.bulsi.egov.eauth.tfa.totp.security.TotpAuthenticator;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TotpPasswordGeneratorService {

    private final IGoogleAuthenticator gAuth;

    public TotpPasswordGeneratorService(ICredentialRepository credentialRepository) {

        this.gAuth = new TotpAuthenticator(credentialRepository);
    }

    private static String generateKeyUri(String account, String issuer,
                                         String secret) throws URISyntaxException {

        URI uri = new URI("otpauth", "totp", "/" + issuer + ":" + account, "secret=" + secret + "&issuer=" + issuer, null);

        return uri.toASCIIString();
    }

    public TotpGeneratedData generate(String user, String appName) throws URISyntaxException {

        final GoogleAuthenticatorKey key = gAuth.createCredentials(user);

        String secret = key.getKey();
        String keyUri = generateKeyUri(user, appName, secret);

        return new TotpGeneratedData(user, secret, keyUri);
    }

    public boolean verify(String user, String value) {

        int verificationCode = Integer.parseInt((value.equals("") ? "-1" : value));
        return gAuth.authorizeUser(user, verificationCode);
    }

    // BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG, JPEG, WBMP, GIF, gif
    public static byte[] generateQRCodeImage(String text,String format, int width, int height)
            throws WriterException, IOException {

        log.debug(String.format("Generate QR code for secret %s", text ));

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();

        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        MatrixToImageWriter.writeToStream(bitMatrix, StringUtils.isBlank(format) ? "PNG" : format, outputStream);

        return outputStream.toByteArray();

    }

}
