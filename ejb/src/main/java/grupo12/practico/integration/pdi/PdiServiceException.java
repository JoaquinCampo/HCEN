package grupo12.practico.integration.pdi;

public class PdiServiceException extends Exception {

    private final Integer errorCode;

    public PdiServiceException(String message) {
        super(message);
        this.errorCode = null;
    }

    public PdiServiceException(String message, Integer errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public PdiServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }

    public PdiServiceException(String message, Integer errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}

