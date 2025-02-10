package exceptions;

public class ValidateTimeException extends RuntimeException {
    public ValidateTimeException(String message) {
        super(message);
    }

    public ValidateTimeException() {
    }
}
