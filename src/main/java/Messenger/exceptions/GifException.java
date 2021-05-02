package Messenger.exceptions;

public class GifException extends Exception {
    public GifException() {
    }

    public GifException(String message) {
        super(message);
    }

    public GifException(String message, Throwable cause) {
        super(message, cause);
    }

    public GifException(Throwable cause) {
        super(cause);
    }

    public GifException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
