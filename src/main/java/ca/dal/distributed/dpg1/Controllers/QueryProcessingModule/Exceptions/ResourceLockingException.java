package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Exceptions;

/**
 * @author Ankush Mudgal
 * Resource Locking Exception - Exception thrown during the Database/table level Locking process.
 */
public class ResourceLockingException extends RuntimeException {

    private static final String LOCK_STATUS = "LOCK Status : ";
    private final String ERROR_MESSAGE;

    /**
     * Instantiates a new Resource locking exception.
     *
     * @param message the detail message. The detail message is saved for                later retrieval by the {@link #getMessage()} method.
     * @author Ankush Mudgal Constructs a new runtime exception with the specified detail message. The cause is not initialized, and may subsequently be initialized by a call to {@link #initCause}.
     */
    public ResourceLockingException(String message) {
        super(LOCK_STATUS + message);
        this.ERROR_MESSAGE = message;
    }

    /**
     * @author Ankush Mudgal
     * Gets error message.
     *
     * @return the error message
     */
    public String getERROR_MESSAGE() {
        return ERROR_MESSAGE;
    }
}
