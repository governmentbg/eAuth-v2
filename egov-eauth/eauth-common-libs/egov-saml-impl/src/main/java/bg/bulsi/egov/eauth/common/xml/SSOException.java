package bg.bulsi.egov.eauth.common.xml;

/**
 * Created by svitkov
 */
public class SSOException extends Exception {
    public SSOException() {
    }

    public SSOException(String message) {
        super(message);
    }

    public SSOException(String message, Throwable cause) {
        super(message, cause);
    }

    public SSOException(Throwable cause) {
        super(cause);
    }

    public SSOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause);
    }
}
