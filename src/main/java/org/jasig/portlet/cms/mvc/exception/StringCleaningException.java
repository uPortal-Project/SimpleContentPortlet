package org.jasig.portlet.cms.mvc.exception;

public class StringCleaningException extends RuntimeException {

    private static final long serialVersionUID = 773741965998995908L;

    public StringCleaningException() {
        super();
    }

    public StringCleaningException(String message, Throwable cause) {
        super(message, cause);
    }

    public StringCleaningException(String message) {
        super(message);
    }

    public StringCleaningException(Throwable cause) {
        super(cause);
    }

}
