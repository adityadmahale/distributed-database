package ca.dal.distributed.dpg1.Controllers.AnalyticsModule.Exceptions;

/**
 * @author Aditya Mahale
 * @description Analytics common Exception class for throwing error messages
 */
public final class AnalyticsException extends Exception {

    private final String errorMessage;

    /**
     * @author Aditya Mahale
     * @description Constructor for the Analytics Exception Class
     */
    public AnalyticsException(final String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    /**
     * @author Aditya Mahale
     * @description Message to get the error message if required from the Exception
     *              Object
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @author Aditya Mahale
     * @description Overriding toString() to print the exception message
     */
    @Override
    public String toString() {
        return "AnalyticsException{" +
                "errorMessage='" + errorMessage + '\'' +
                '}';
    }
}