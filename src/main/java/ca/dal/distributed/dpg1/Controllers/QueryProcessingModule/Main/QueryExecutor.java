package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main;

import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.EventLogger;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.QueryLogger;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main.Queries.TransactionQueries;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Model.ExecutionResponse;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Exceptions.QueryExecutionRuntimeException;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Exceptions.QueryParseFailureException;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main.Queries.DDLQueries;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main.Queries.DMLQueries;
import ca.dal.distributed.dpg1.Utils.GlobalConstants;

import static ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.QueryKeywordRegexen.*;
import static ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.TransactionQueryRegexen.*;

/**
 * @author Ankush Mudgal
 * Query executor - Executes the query strings incoming from the CLI Interface.
 */
public class QueryExecutor {

    private static QueryLogger queryLogger = new QueryLogger();
    private static EventLogger eventLogger = new EventLogger();
    private  static DDLQueries ddlQueries = new DDLQueries();
    private static DMLQueries dmlQueries = new DMLQueries();
    private static TransactionQueries transactionQueries = new TransactionQueries();


    /**
     * @author Ankush Mudgal
     * Process the input query based on the type of query and its validity.
     *
     * @param inputQuery the input query string
     * @return the execution response type object
     * @throws QueryExecutionRuntimeException the query execution runtime exception
     * @throws QueryParseFailureException     the query parse failure exception
     */
    public ExecutionResponse processInputQuery(String inputQuery) throws QueryExecutionRuntimeException, QueryParseFailureException {

        //Log the query to log file.
        queryLogger.logData(inputQuery);

        //Check the validity of the query
        //call to QueryValidator
        boolean isValidInputQuery = QueryValidator.parseInputQuery(inputQuery);

        //If invalid then
        if (!isValidInputQuery) {

            eventLogger.logData("INVALID QUERY" + GlobalConstants.STRING_ARROW_SEPARATOR + inputQuery);
            throw new QueryExecutionRuntimeException("Invalid Query type or syntax.");

        }
        //If Valid then check which query and execute query.
        else {

            final String queryToLowercase = inputQuery.trim().toLowerCase();

            if (queryToLowercase.contains(CREATE_DATABASE_KEYWORD)) {
                return ddlQueries.createDatabase(inputQuery);
            } else if (queryToLowercase.contains(DROP_DATABASE_KEYWORD)) {
                return ddlQueries.dropDatabase(inputQuery);
            } else if (queryToLowercase.contains(USE_DATABASE_KEYWORD)) {
                return ddlQueries.useDatabase(inputQuery);
            } else if (queryToLowercase.contains(CREATE_TABLE_KEYWORD)) {
                return ddlQueries.createTable(inputQuery);
            } else if (queryToLowercase.contains(INSERT_KEYWORD)) {
                return dmlQueries.insertInto(inputQuery);
            } else if (queryToLowercase.contains(SELECT_KEYWORD)) {

                if (queryToLowercase.contains(SELECT_ALL_KEYWORD)) {
                    return dmlQueries.selectAll(inputQuery);
                } else if (queryToLowercase.contains(SELECT_DISTINCT_KEYWORD)) {
                    return dmlQueries.selectDistinct(inputQuery);
                }

            } else if (queryToLowercase.contains(TRUNCATE_TABLE_KEYWORD)) {
                return ddlQueries.truncateTable(inputQuery);
            } else if (queryToLowercase.contains(DROP_TABLE_KEYWORD)) {
                return ddlQueries.dropTable(inputQuery);
            } else if (queryToLowercase.contains(START_TRANSACTION_KEYWORD)) {
                return transactionQueries.startTransaction();
            } else if (queryToLowercase.contains(COMMIT_KEYWORD)) {
                return transactionQueries.commitTransaction();
            } else if (queryToLowercase.contains(ROLLBACK_KEYWORD)) {
                return transactionQueries.rollbackTransaction();
            }

            //If no match found for query, throw query processing exception.
            throw new QueryExecutionRuntimeException("Error Occurred  while processing the given query.");
        }
    }

}
