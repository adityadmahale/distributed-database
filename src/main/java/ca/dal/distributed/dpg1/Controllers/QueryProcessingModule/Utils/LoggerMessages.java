package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils;

import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.EventLogger;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.GeneralLogger;
import java.time.Duration;
import java.time.Instant;
import static ca.dal.distributed.dpg1.Utils.GlobalConstants.STRING_PIPE_SEPARATOR;

/**
 * @author Ankush Mudgal
 * Logger Messages - Has the methods to log the relevant messages during the Query Execution process.
 */
public class LoggerMessages {

    private LoggerMessages(){
        //Private Constructor - Cannot be instantiated.
    }

    private static final EventLogger eventLogger = new EventLogger();
    private static final GeneralLogger generalLogger = new GeneralLogger();

    /**
     * @author Ankush Mudgal
     * Gets query execution duration.
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the query execution duration
     */
    public static String getQueryExecutionDuration(Instant startTime, Instant endTime) {
        return STRING_PIPE_SEPARATOR + " Query Execution Duration : " + Duration.between(startTime, endTime).toMillis() + " milliseconds";
    }

    /**
     * @author Ankush Mudgal
     * Database does not exist.
     *
     * @param startTime    the start time
     * @param databaseName the database name
     * @return log message
     */
    public static String dataBaseDoesNotExist(Instant startTime, String databaseName) {
        final String message = "Error Occurred: Database " + databaseName + " does not exist in the system." +  getQueryExecutionDuration(startTime, Instant.now());
        eventLogger.logData(message);
        return message;
    }

    /**
     * @author Ankush Mudgal
     * Data base already exists.
     *
     * @param startTime    the start time
     * @param databaseName the database name
     * @return log message
     */
    public static String dataBaseAlreadyExists( Instant startTime, String databaseName){

        final String message = "Database " + databaseName + " already exists in the system." + getQueryExecutionDuration(startTime, Instant.now());
        eventLogger.logData(message);
        return message;
    }

    /**
     * @author Ankush Mudgal
     * Data base created.
     *
     * @param startTime    the start time
     * @param databaseName the database name
     * @return log message
     */
    public static String dataBaseCreated ( Instant startTime, String databaseName){

        final String message = "Database " + databaseName + " has been successfully created." + getQueryExecutionDuration(startTime, Instant.now());
        generalLogger.logData(message);
        eventLogger.logData(message);
        return message;
    }

    /**
     * @author Ankush Mudgal
     * Data base dropped.
     *
     * @param startTime    the start time
     * @param databaseName the database name
     * @return log message
     */
    public static String dataBaseDropped ( Instant startTime, String databaseName){

        final String message = "Database " + databaseName + " has been dropped successfully." + getQueryExecutionDuration(startTime, Instant.now());
        generalLogger.logData(message);
        eventLogger.logData(message);
        return message;
    }

    /**
     * @author Ankush Mudgal
     * Data base usage error.
     *
     * @param startTime    the start time
     * @param databaseName the database name
     * @return log message
     */
    public static String dataBaseUsageError(Instant startTime, String databaseName){

        final String message = "Error Occurred: USE " + databaseName + " encountered an error." + getQueryExecutionDuration(startTime, Instant.now());
        eventLogger.logData(message);
        return message;
    }

    /**
     * @author Ankush Mudgal
     * Data base usage successful.
     *
     * @param startTime    the start time
     * @param databaseName the database name
     * @return log message
     */
    public static String dataBaseUsageSuccessful(Instant startTime, String databaseName){

        final String message = "Database " + databaseName + " is currently in use." + getQueryExecutionDuration(startTime, Instant.now());
        generalLogger.logData(message);
        eventLogger.logData(message);
        return message;
    }

    /**
     * @author Ankush Mudgal
     * Data base creation failed.
     *
     * @param startTime    the start time
     * @param databaseName the database name
     * @return log message
     */
    public static String dataBaseCreationFailed(Instant startTime, String databaseName){

        final String message = "Error Occurred: Creation of Database " + databaseName + " failed." + getQueryExecutionDuration(startTime, Instant.now());
        eventLogger.logData(message);
        return message;
    }

    /**
     * @author Ankush Mudgal
     * No database selected.
     *
     * @param startTime the start time
     * @return log message
     */
    public static String noDatabaseSelected(Instant startTime){
        final String message = "Error Occurred: No database is selected for use. " + getQueryExecutionDuration(startTime, Instant.now());
        eventLogger.logData(message);
        return message;
    }

    /**
     * @author Ankush Mudgal
     * Table does not exist.
     *
     * @param startTime the start time
     * @param tableName the table name
     * @return log message
     */
    public static String tableDoesNotExist(Instant startTime, String tableName) {
        final String message = "Error Occurred: Table " + tableName + " does not exist in the system." + getQueryExecutionDuration(startTime, Instant.now());
        eventLogger.logData(message);
        return message;
    }

    /**
     * @author Ankush Mudgal
     * Unknown error.
     *
     * @param startTime the start time
     * @return log message
     */
    public static String unknownError(Instant startTime) {
        final String message = "Error Occurred: Something went wrong during Query Execution. Try Again." + getQueryExecutionDuration(startTime, Instant.now());
        eventLogger.logData(message);
        return message;
    }

    /**
     * @author Ankush Mudgal
     * Unable to drop table.
     *
     * @param startTime the start time
     * @param tableName the table name
     * @return log message
     */
    public static String unableToDropTable(Instant startTime, String tableName) {
        final String message = "Error Occurred: Unable to drop the table " + tableName  + getQueryExecutionDuration(startTime, Instant.now());
        eventLogger.logData(message);
        return message;
    }

    /**
     * @author Ankush Mudgal
     * Data base deletion failed.
     *
     * @param startTime    the start time
     * @param databaseName the database name
     * @return log message
     */
    public static String dataBaseDeletionFailed(Instant startTime, String databaseName){

        final String message = "Error Occurred: Failed to delete the Database " + databaseName   + getQueryExecutionDuration(startTime, Instant.now());
        eventLogger.logData(message);
        return message;
    }

    /**
     * @author Ankush Mudgal
     * Data base tables deletion failed.
     *
     * @param startTime    the start time
     * @param databaseName the database name
     * @return log message
     */
    public static String dataBaseTablesDeletionFailed(Instant startTime, String databaseName){

        final String message = "Error Occurred: Failed to delete tables of the database " + databaseName  + getQueryExecutionDuration(startTime, Instant.now());
        eventLogger.logData(message);
        return message;
    }

    /**
     * @author Ankush Mudgal
     * Data base table dropped.
     *
     * @param startTime the start time
     * @param tableName the table name
     * @return log message
     */
    public static String dataBaseTableDropped( Instant startTime, String tableName){
        final String message = "Table " + tableName + " has been dropped successfully." + getQueryExecutionDuration(startTime, Instant.now());
        eventLogger.logData(message);
        generalLogger.logData(message);
        return message;
    }

    /**
     * @author Ankush Mudgal
     * Data base table created.
     *
     * @param startTime the start time
     * @param tableName the table name
     * @return log message
     */
    public static String dataBaseTableCreated(Instant startTime, String tableName){
        final String message = "Table " + tableName + " has been created successfully." + getQueryExecutionDuration(startTime, Instant.now());
        eventLogger.logData(message);
        return message;
    }

    /**
     * @author Ankush Mudgal
     * Data base table already exists.
     *
     * @param startTime the start time
     * @param tableName the table name
     * @return log message
     */
    public static String dataBaseTableAlreadyExists(Instant startTime, String tableName){

        final String message = "Error Occurred: Table " + tableName + " already exists in the system." + getQueryExecutionDuration(startTime, Instant.now());
        eventLogger.logData(message);
        return message;
    }


    /**
     * @author Ankush Mudgal
     * Truncate table failed during execution..
     *
     * @param startTime the start time
     * @param tableName the table name
     * @return log message
     */
    public static String truncateTableFailed(Instant startTime, String tableName){

        final String message = "Error Occurred: Table " + tableName + " could not be truncated. " + getQueryExecutionDuration(startTime, Instant.now());
        eventLogger.logData(message);
        return message;
    }

    /**
     * @author Ankush Mudgal
     * Transaction started.
     *
     * @param startTime    the start time
     * @param databaseName the database name
     * @return log message
     */
    public static String transactionStarted(Instant startTime, String databaseName){
        final String message = "Transaction started for database " + databaseName  + getQueryExecutionDuration(startTime, Instant.now());
        generalLogger.logData(message);
        eventLogger.logData(message);
        return message;
    }

    /**
     * @author Ankush Mudgal
     * Transaction failed.
     *
     * @param startTime    the start time
     * @param databaseName the database name
     * @return log message
     */
    public static String transactionFailed(Instant startTime, String databaseName){
        final String message = "Error Occurred: Something went wrong. Transaction execution failed for the database " + databaseName + getQueryExecutionDuration(startTime, Instant.now());
        eventLogger.logData(message);
        return message;
    }

    /**
     * @author Ankush Mudgal
     * Transaction failed to start.
     *
     * @param startTime    the start time
     * @param databaseName the database name
     * @return log message
     */
    public static String transactionFailedToStart(Instant startTime, String databaseName) {

        final String message = "Error: Transaction not started for database " + databaseName + getQueryExecutionDuration(startTime, Instant.now());
        eventLogger.logData(message);
        return message;
    }

    /**
     * @author Ankush Mudgal
     * Transaction committed.
     *
     * @param startTime    the start time
     * @param databaseName the database name
     * @return log message
     */
    public static String transactionCommitted(Instant startTime, String databaseName){

        final String message = "Transaction committed for database " + databaseName + getQueryExecutionDuration(startTime, Instant.now());
        generalLogger.logData(message);
        eventLogger.logData(message);
        return message;
    }

    /**
     * @author Ankush Mudgal
     * Transaction rollback.
     *
     * @param startTime    the start time
     * @param databaseName the database name
     * @return log message
     */
    public static String transactionRollback(Instant startTime, String databaseName){
        final String message = "Transaction rollback for database " + databaseName + getQueryExecutionDuration(startTime, Instant.now()); 
        generalLogger.logData(message);
        eventLogger.logData(message);
        return message;
    }

}
