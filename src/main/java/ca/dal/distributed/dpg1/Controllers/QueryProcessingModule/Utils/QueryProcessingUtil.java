package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils;

import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Exceptions.QueryExecutionRuntimeException;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main.QueryManager;

import java.io.File;
import java.time.Instant;

/**
 * @author Ankush Mudgal
 * The type Query processing util.
 */
public class QueryProcessingUtil {

    /**
     * @author Ankush Mudgal
     * Preprocesses the input query string to isolate actual query tokens like database or table name.
     *
     * @param query the query
     * @return the string
     */
    public static String preprocessing(String query) {

        final String queryProcessed = query.substring(0, query.length() - 1);
        final String[] querySplitArray = queryProcessed.split(" ");
        final String databaseOrTableName = querySplitArray[2];

        return databaseOrTableName;

    }

    /**
     * @author Ankush Mudgal
     * Check if a database was selected by the user to perform queries on.
     *
     * @param queryStartTime the query start time
     */
    public static void checkIfDataBaseSelected(Instant queryStartTime) {

        if (!QueryManager.isDataBaseInUse()) {
            throw new QueryExecutionRuntimeException(LoggerMessages.noDatabaseSelected(queryStartTime));
        }
    }

    /**
     * @author Ankush Mudgal
     * Check if the database currently under processing exists.
     *
     * @param queryStartTime the query start time
     * @param databaseName   the database name
     */
    public static void checkIfDatabaseExists( Instant queryStartTime, File databaseName){

        if (!databaseName.isDirectory()) {
            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseDoesNotExist(queryStartTime, databaseName.toString()));
        }
    }

    /**
     * @author Ankush Mudgal
     * Check if the given database has tables.
     *
     * @param queryStartTime the query start time
     * @param allTablesInDb  the all tables in db
     */
    public static void checkIfDatabaseHasTables (Instant queryStartTime , File[] allTablesInDb){

        if (allTablesInDb == null) {
            throw new QueryExecutionRuntimeException(LoggerMessages.unknownError(queryStartTime));
        }
    }
}
