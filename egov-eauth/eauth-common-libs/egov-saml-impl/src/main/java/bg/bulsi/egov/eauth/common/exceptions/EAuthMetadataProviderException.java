/* 
#   Copyright (c) 2017 European Commission  
#   Licensed under the EUPL, Version 1.2 or – as soon they will be 
#   approved by the European Commission - subsequent versions of the 
#    EUPL (the "Licence"); 
#    You may not use this work except in compliance with the Licence. 
#    You may obtain a copy of the Licence at: 
#    * https://joinup.ec.europa.eu/page/eupl-text-11-12  
#    *
#    Unless required by applicable law or agreed to in writing, software 
#    distributed under the Licence is distributed on an "AS IS" basis, 
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
#    See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package bg.bulsi.egov.eauth.common.exceptions;

/**
 * EAuthMetadataProviderException
 *
 * @since 2.0.0
 */
public class EAuthMetadataProviderException extends EAuthMetadataException {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -3170936826098244230L;


    /**
     * Instantiates a new EAuthMetadataProviderException engine exception.
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A <tt>null</tt>
     *              value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public EAuthMetadataProviderException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new EAuthMetadataProviderException engine exception.
     *
     * @param errorMessage the error message
     */
    public EAuthMetadataProviderException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Instantiates a new EAuthMetadataProviderException engine exception.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A <tt>null</tt>
     */
    public EAuthMetadataProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new EAuthMetadataProviderException engine exception.
     *
     * @param newErrorCode the error code
     * @param errorMessage the error message
     */
    public EAuthMetadataProviderException(String newErrorCode, String errorMessage) {
        super(newErrorCode, errorMessage);
    }

    /**
     * Instantiates a new EAuthMetadataProviderException engine exception.
     *
     * @param newErrorCode the error code
     * @param errorMessage the error message
     * @param cause        the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A <tt>null</tt>
     */
    public EAuthMetadataProviderException(String newErrorCode, String errorMessage, Throwable cause) {
        super(newErrorCode, errorMessage, cause);
    }

    /**
     * Instantiates a new EAuthMetadataProviderException engine exception.
     *
     * @param newErrorCode   the error code
     * @param errorMessage   the error message
     * @param newErrorDetail the error detail
     */
    public EAuthMetadataProviderException(String newErrorCode, String errorMessage, String newErrorDetail) {
        super(newErrorCode, errorMessage, newErrorDetail);
    }
}
