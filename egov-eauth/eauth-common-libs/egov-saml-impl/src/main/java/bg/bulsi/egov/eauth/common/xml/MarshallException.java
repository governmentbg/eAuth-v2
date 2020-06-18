package bg.bulsi.egov.eauth.common.xml;

/**
 * Created by svitkov
 */
public class MarshallException extends Exception {
    public MarshallException() {
    }

    public MarshallException(String message) {
        super(message);
    }

    public MarshallException(String message, Throwable cause) {
        super(message, cause);
    }

    public MarshallException(Throwable cause) {
        super(cause);
    }

    public MarshallException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause);
    }
}
