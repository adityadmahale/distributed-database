package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Exceptions;

import ca.dal.distributed.dpg1.Utils.GlobalConstants;

/**
 * @author Ankush Mudgal
 * Query Execution Runtime Exception - thrown for exceptions in flow during query execution.
 */
public class QueryExecutionRuntimeException extends RuntimeException{


    private static final String QUERY_PARSE_FAILURE = "Query Execution Failed";
    private final String ERROR_MESSAGE;

    /**
     * @author Ankush Mudgal
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public QueryExecutionRuntimeException(final String message) {
        super(QUERY_PARSE_FAILURE + GlobalConstants.STRING_ARROW_SEPARATOR + message);
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
