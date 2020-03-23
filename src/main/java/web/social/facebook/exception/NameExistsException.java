package web.social.facebook.exception;

public class NameExistsException extends Exception {
    public NameExistsException(String errorMessage) {
        super(errorMessage);
    }
}
