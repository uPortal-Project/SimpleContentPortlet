package org.jasig.portlet.cms.mvc.exception;

public class ContentPersistenceException extends RuntimeException {

    private static final long serialVersionUID = -2080327787225619040L;

    public ContentPersistenceException() {
        super();
    }

    public ContentPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContentPersistenceException(String message) {
        super(message);
    }

    public ContentPersistenceException(Throwable cause) {
        super(cause);
    }

}
