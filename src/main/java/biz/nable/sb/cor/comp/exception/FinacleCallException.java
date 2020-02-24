/**
 * 
 */
package biz.nable.sb.cor.comp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author asel.meemeduma
 *
 */
@ResponseStatus(code=HttpStatus.INTERNAL_SERVER_ERROR)
public class FinacleCallException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public FinacleCallException(String message) {
		super(message);
	}
	
	
	public FinacleCallException(String message, Throwable cause) {
		super(message, cause);
	}
}
