package bg.bulsi.egov.idp.exception;

import java.util.ArrayList;
import java.util.List;

import bg.bulsi.egov.eauth.eid.client.exception.ApiClientException;
import bg.bulsi.egov.eauth.eid.dto.CommonAuthException;
import bg.bulsi.egov.eauth.eid.dto.CommonAuthExceptionCause;
import lombok.Getter;
import lombok.Setter;

public class UiBackendException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private CommonAuthException intermediateStatus;
	
	@Getter
	@Setter
	private int responceCode;

	public UiBackendException(String message, Throwable cause, int code) {
		super(message);
		responceCode = code;
		intermediateStatus = fillAuthException(cause);
	}
	
	public UiBackendException(String message, Throwable cause) {
		super(message);
		intermediateStatus = fillAuthException(cause);
	}
	public UiBackendException(CommonAuthException cause) {
		super(ApiClientException.getAllMessage(cause));
		intermediateStatus = cause;
	}
	public UiBackendException(Throwable cause) {
		super(cause.getMessage());
		intermediateStatus = fillAuthException(cause);
	}
	
	public UiBackendException(Throwable cause, int code) {
		super(cause.getMessage());
		responceCode = code;
		intermediateStatus = fillAuthException(cause);
	}

	public UiBackendException(String message, int code) {
		super(message);
		responceCode = code;
		intermediateStatus = new CommonAuthException();
		CommonAuthExceptionCause justCause = new CommonAuthExceptionCause();
		justCause.setCode(""+code);
		justCause.setMessage(message);
		intermediateStatus.setCause(java.util.Arrays.asList(justCause));
	}

	private CommonAuthException fillAuthException(Throwable cause) {
		CommonAuthException newAuthException = new CommonAuthException();
		List<CommonAuthExceptionCause> send2Client = new ArrayList<>();
		StackTraceElement[] stacks = cause.getStackTrace();
		for (int i = 0; i < stacks.length; i++) {
			CommonAuthExceptionCause temp = new CommonAuthExceptionCause();
			temp.setCode("" + i);
			temp.setMessage(stacks[i].toString());
			send2Client.add(temp);
		}
		newAuthException.setCause(send2Client);
		return newAuthException;
	}
}
