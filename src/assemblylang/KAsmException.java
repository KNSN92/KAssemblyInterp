package assemblylang;

/*
 * KAssemblyインタープリタ内でthrowErrorメソッドが呼ばれた際にThrowされます。
 */
public class KAsmException extends RuntimeException {

	public KAsmException() {
	}

	public KAsmException(String message) {
		super(message);
	}

	public KAsmException(Throwable cause) {
		super(cause);
	}

	public KAsmException(String message, Throwable cause) {
		super(message, cause);
	}

	public KAsmException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
