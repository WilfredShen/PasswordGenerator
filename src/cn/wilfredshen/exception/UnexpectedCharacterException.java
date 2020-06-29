package cn.wilfredshen.exception;

public class UnexpectedCharacterException extends Exception {

    public UnexpectedCharacterException() {
    }

    public UnexpectedCharacterException(String message) {
        super(message);
    }

    public UnexpectedCharacterException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedCharacterException(Throwable cause) {
        super(cause);
    }

    public UnexpectedCharacterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
