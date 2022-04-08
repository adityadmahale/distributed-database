package ca.dal.distributed.dpg1.Controllers.TransactionModule.Exceptions;

/**
 * @author Guryash Singh Dhall
 * @description Transaction common Exception class for throwing error messages
 */
public final class TransactionExceptions extends Exception {
    private final String errorMessage;
    /**
     * @author Guryash Singh Dhall
     * @description Constructor for the Transaction Exception Class
     */
    public TransactionExceptions(final String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }
    /**
     * @author Guryash Singh Dhall
     * @description Message to get the error message if required from the Exception
     *              Object
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @author Guryash Singh Dhall
     * @description Overriding toString() to print the exception message
     */
    @Override
    public String toString() {
        return "SQLERDException{" +
                "errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
