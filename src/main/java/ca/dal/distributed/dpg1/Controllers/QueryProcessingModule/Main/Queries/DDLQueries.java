package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main.Queries;

import ca.dal.distributed.dpg1.Controllers.LoggerModule.Enums.LoggerType;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.EventLogger;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.GeneralLogger;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.LoggerFactory;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Exceptions.QueryExecutionRuntimeException;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Model.ExecutionResponse;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main.QueryManager;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.DatabaseConstants;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.MetaDataHandler;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.ResourceLockManager;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.LoggerMessages;
import ca.dal.distributed.dpg1.Utils.GlobalConstants;
import ca.dal.distributed.dpg1.Utils.GlobalUtils;

import java.io.*;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.DatabaseConstants.*;
import static ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.QueryProcessingUtil.*;
import static ca.dal.distributed.dpg1.Utils.GlobalConstants.*;


/**
 * @author Ankush Mudgal
 * DDL Queries - Class to process and execute SQL DDL Queries.
 */
public class DDLQueries {

    //Loggers
    private static final EventLogger eventLogger = (EventLogger) new LoggerFactory().getLogger(LoggerType.EVENT_LOGGER);
    private static final GeneralLogger generalLogger  = (GeneralLogger) new LoggerFactory().getLogger(LoggerType.GENERAL_LOGGER);

    /**
     * @author Ankush Mudgal
     * Method executes Create Database SQL Query.
     *
     * @param query the query
     * @return the execution response
     * @throws QueryExecutionRuntimeException the query execution runtime exception
     */
    public ExecutionResponse createDatabase (String query) throws QueryExecutionRuntimeException {

        final Instant queryStartTime = Instant.now();
        final String databaseName = preprocessing(query);
        final String dbPath = GlobalConstants.DB_PATH + databaseName;

        final File database = new File(dbPath);
        if(database.isDirectory()){

            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseAlreadyExists(queryStartTime, databaseName));
        }

        // Apply Exclusive Resource Lock
        ResourceLockManager.applyExclusiveLock(databaseName, STRING_NULL);

        final boolean isNewDBCreated = database.mkdir();

        // Release Lock
        ResourceLockManager.releaseExclusiveLock(databaseName, STRING_NULL);

        if (isNewDBCreated) {

                //@author Ankush Mudgal - Writes Database to Global Metadata Metadata file.
            final boolean isGlobalMetaDataUpdated = MetaDataHandler.writeToGlobalMetaData(databaseName);
            if(isGlobalMetaDataUpdated){
                generalLogger.logData("Database " + databaseName + "added to global metadata file.");
            }

            return new ExecutionResponse(true, LoggerMessages.dataBaseCreated(queryStartTime, databaseName));

        } else {

            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseCreationFailed(queryStartTime, databaseName));
        }
    }

    /**
     * @author Ankush Mudgal
     * Method executes the Drop database SQL query.
     *
     * @param query the query
     * @return the execution response
     * @throws QueryExecutionRuntimeException the query execution runtime exception
     */
    public ExecutionResponse dropDatabase(String query) throws QueryExecutionRuntimeException {
        
        final Instant queryStartTime = Instant.now();
        final String databaseName = preprocessing(query);
        final String dbPath = GlobalConstants.DB_PATH + databaseName;
        
        final File database = new File(dbPath);
        checkIfDatabaseExists(queryStartTime, database);
        
        //Apply Exclusive Resource Lock
        ResourceLockManager.applyExclusiveLock(databaseName, STRING_NULL);
        
        final File[] allTablesInDB = GlobalUtils.readAllTables(dbPath);

        if (allTablesInDB == null) {

            // Release Lock
            ResourceLockManager.releaseExclusiveLock(databaseName, STRING_NULL);
            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseDeletionFailed(queryStartTime, databaseName));
        }
        
        for (final File table : allTablesInDB) {
            
            final boolean checkTableDeleted = table.delete();
            
            if (!checkTableDeleted) {
                
                ResourceLockManager.releaseExclusiveLock(databaseName, STRING_NULL); // Release Exclusive ResourceLockManager
                throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseTablesDeletionFailed(queryStartTime, databaseName));
            }
        }

        final boolean isDeleteDBSuccess = database.delete();

        if (isDeleteDBSuccess) {
            
            QueryManager.dataBaseInUse = STRING_NULL;

            // Release Exclusive Lock
            ResourceLockManager.releaseExclusiveLock(databaseName, STRING_NULL);

            //@author Ankush Mudgal - Removes the database form the Global Metadata file
            final boolean isDeletedFromGlobalMD = MetaDataHandler.deleteFromGlobalMetaData(databaseName);
            if(isDeletedFromGlobalMD){
                generalLogger.logData("Database " + databaseName + "removed from the global metadata file.");
            }
            
            return new ExecutionResponse(true, LoggerMessages.dataBaseDropped(queryStartTime, databaseName));
            
        } else {

            // Release Exclusive ResourceLockManager
            ResourceLockManager.releaseExclusiveLock(databaseName, STRING_NULL);
            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseDeletionFailed(queryStartTime, databaseName));
        }
    }

    /**
     * @author Ankush Mudgal
     * Method executes Use database SQL command. It updates the value of currently selected database on the @QueryManager Class.
     *
     * @param query the query
     * @return the execution response
     * @throws QueryExecutionRuntimeException the query execution runtime exception
     */
    public ExecutionResponse useDatabase(final String query) throws QueryExecutionRuntimeException {

        final Instant queryStartTime = Instant.now();
        final String databaseName = preprocessing(query);
        final String dbPath = GlobalConstants.DB_PATH + databaseName;
        
        final File database = new File(dbPath);
        checkIfDatabaseExists(queryStartTime, database);
            
        final File[] databases = new File(DB_PATH).listFiles();
        
        if (databases == null) {

            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseUsageError(queryStartTime, databaseName));
        }
        for (final File db : databases) {
            
            if (db.getName().equalsIgnoreCase(databaseName)) {
                QueryManager.dataBaseInUse = db.getName();
            }
        }

        return new ExecutionResponse(true, LoggerMessages.dataBaseUsageSuccessful(queryStartTime, databaseName));
    }

    /**
     * @author Ankush Mudgal
     * Method executes the Create table SQL query.
     *
     * @param query the query
     * @return the execution response
     * @throws QueryExecutionRuntimeException the query execution runtime exception
     */
    public ExecutionResponse createTable(final String query) throws QueryExecutionRuntimeException {
        
        final Instant queryStartTime = Instant.now();
        checkIfDataBaseSelected(queryStartTime);

        final String tableName = preprocessing(query);
        final File database = new File(ABSOLUTE_CURRENT_DB_PATH);
        checkIfDatabaseExists(queryStartTime, database);
        
        final String absoluteTablePath = ABSOLUTE_CURRENT_DB_PATH + FORWARD_SLASH;
        final File[] allTablesInDB = GlobalUtils.readAllTables(absoluteTablePath);
        checkIfDatabaseHasTables(queryStartTime, allTablesInDB);

        boolean checkTableExists = false;

        for (final File table : allTablesInDB) {

            if (table.getName().equalsIgnoreCase(tableName + GlobalConstants.EXTENSION_DOT_TXT)) {
                checkTableExists = true;
            }
        }
        if (checkTableExists) {

            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseTableAlreadyExists(queryStartTime, tableName));
        }

        String plainQuery = query.substring(0, query.length() - 1);
        final ExecutionResponse createdTableResponse = createTableWithColumnsUtility(plainQuery , tableName, absoluteTablePath + tableName + GlobalConstants.EXTENSION_DOT_TXT);

        //@author Ankush Mudgal - Writes Table Metadata to the Local Metadata file.
        if(createdTableResponse.isExecutionSuccess()){

            final boolean isMetadataUpdated = MetaDataHandler.writeToDBLocalMetaData(tableName, absoluteTablePath);
            if(isMetadataUpdated){
                generalLogger.logData("Added new table " + tableName + " to local metadata for the Database " + QueryManager.dataBaseInUse);
            }
        }

        return createdTableResponse;
    }


    /**
     * @author Ankush Mudgal
     * Creates a table with given columns.
     *
     * @param query     the query
     * @param tableName the table name
     * @param tablePath the table path
     * @return the execution response
     * @throws QueryExecutionRuntimeException the query execution runtime exception
     */
    private ExecutionResponse createTableWithColumnsUtility(final String query, final String tableName, final String tablePath) throws QueryExecutionRuntimeException {

        final Instant queryStartTime = Instant.now();

        // Apply Exclusive Lock
        ResourceLockManager.applyExclusiveLock(QueryManager.dataBaseInUse, tableName);

        final Pattern pattern = Pattern.compile(CREATE_TABLE_COLUMN_REGEX);
        final Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {

            final String queryMatched = matcher.group();
            final String[] colTokens = queryMatched.split(STRING_COMMA);

            try (final FileWriter fileWriter = new FileWriter(tablePath)) {

                final StringBuilder builder = new StringBuilder();

                for (final String token : colTokens) {

                    final String[] splitTokensArray = token.trim().split(GlobalConstants.COLUMN_EMPTY_SPACE);

                    if (splitTokensArray.length == 4 && splitTokensArray[2].equalsIgnoreCase(REFERENCES_KEYWORD)) {

                        final String fkReferenceTable = splitTokensArray[3].split(COLUMN_NAME_DELIMITER)[0];
                        String columnReferencedByFK = splitTokensArray[3].split(COLUMN_NAME_DELIMITER)[1].replaceAll(COLUMN_NAME_DELIMITER_CLOSE, GlobalConstants.STRING_EMPTY);

                        builder.append(splitTokensArray[0]).append(COLUMN_TYPE_OPEN_DELIMITER)
                                .append(splitTokensArray[1]).append(CARDINALITY_SEPARATOR)
                                .append("FK").append(CARDINALITY_SEPARATOR)
                                .append(fkReferenceTable).append(CARDINALITY_SEPARATOR)
                                .append(columnReferencedByFK).append(COLUMN_TYPE_CLOSE_DELIMITER)
                                .append(DELIMITER);
                    }

                    if (splitTokensArray.length == 4 && splitTokensArray[2].equalsIgnoreCase(PRIMARY_KEYWORD)) {

                        builder.append(splitTokensArray[0])
                                .append(COLUMN_TYPE_OPEN_DELIMITER)
                                .append(splitTokensArray[1]).append(CARDINALITY_SEPARATOR)
                                .append(GlobalConstants.PRIMARY_KEY_CONSTRAINT).append(COLUMN_TYPE_CLOSE_DELIMITER)
                                .append(DELIMITER);
                    }

                    if (splitTokensArray.length == 2) {

                        builder.append(splitTokensArray[0])
                                .append(COLUMN_TYPE_OPEN_DELIMITER)
                                    .append(splitTokensArray[1]).append(COLUMN_TYPE_CLOSE_DELIMITER)
                                .append(DELIMITER);
                    }

                }

                builder.replace(builder.length() - DELIMITER.length(), builder.length(), GlobalConstants.STRING_EMPTY).append(STRING_NEXT_LINE);
                fileWriter.append(builder.toString());

                // Release Exclusive ResourceLockManager
                ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName);
                return new ExecutionResponse(true, LoggerMessages.dataBaseTableCreated(queryStartTime, tableName));

            } catch (final IOException e) {

                // Release Exclusive ResourceLockManager
                ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName);

                final String message = "Error: " + e.getMessage() + LoggerMessages.getQueryExecutionDuration(queryStartTime, Instant.now());
                eventLogger.logData(message);

                throw new QueryExecutionRuntimeException(message);
            }
        } else {

            // Release Exclusive ResourceLockManager
            ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName);
            throw new QueryExecutionRuntimeException(LoggerMessages.unknownError(queryStartTime));
        }
    }

    /**
     * @author Ankush Mudgal
     * Method executes Truncate table Sql Query.
     *
     * @param query the query
     * @return the execution response
     * @throws QueryExecutionRuntimeException the query execution runtime exception
     */
    public ExecutionResponse truncateTable(final String query) throws QueryExecutionRuntimeException {
        
        final Instant queryStartTime = Instant.now();
        checkIfDataBaseSelected(queryStartTime);
        final File database = new File(DatabaseConstants.ABSOLUTE_CURRENT_DB_PATH);
        checkIfDatabaseExists(queryStartTime, database);

        final String tableName = preprocessing(query);
        final File[] allTablesInDB = GlobalUtils.readAllTables(DatabaseConstants.ABSOLUTE_CURRENT_DB_PATH);
        checkIfDatabaseHasTables(queryStartTime, allTablesInDB);

        boolean checkTableExists = false;

        for (final File table : allTablesInDB) {

            if (table.getName().equalsIgnoreCase(tableName + GlobalConstants.EXTENSION_DOT_TXT)) {
                checkTableExists = true;
                break;
            }
        }

        if (!checkTableExists) {

            throw new QueryExecutionRuntimeException(LoggerMessages.tableDoesNotExist(queryStartTime, tableName));
        }

        // Apply Exclusive Lock
        ResourceLockManager.applyExclusiveLock(QueryManager.dataBaseInUse, tableName);

        final String absoluteTablePath = ABSOLUTE_CURRENT_DB_PATH + FORWARD_SLASH + tableName + GlobalConstants.EXTENSION_DOT_TXT;

        try (final BufferedReader bufferedReader = new BufferedReader(new FileReader(absoluteTablePath))) {

            final String lineToWrite = bufferedReader.readLine();

            if (lineToWrite == null || lineToWrite.isEmpty()) {

                // Release Exclusive ResourceLockManager
                ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName);
                throw new QueryExecutionRuntimeException(LoggerMessages.unknownError(queryStartTime));
            }

            final FileWriter fileWriter = new FileWriter(absoluteTablePath, false);
            fileWriter.write(lineToWrite + STRING_NEXT_LINE);
            fileWriter.close();

        } catch (final IOException e) {

            // Release Exclusive ResourceLockManager
            ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName);
            throw new QueryExecutionRuntimeException(LoggerMessages.truncateTableFailed(queryStartTime, tableName));
        }

        ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
        return new ExecutionResponse(true, "Table " + tableName + " truncated successfully.");
    }

    /**
     * @author Ankush Mudgal
     * Method executes Drop table SQL query.
     *
     * @param query the query
     * @return the execution response
     * @throws QueryExecutionRuntimeException the query execution runtime exception
     */
    public ExecutionResponse dropTable(final String query) throws QueryExecutionRuntimeException {
        
        final Instant queryStartTime = Instant.now();
        checkIfDataBaseSelected(queryStartTime);

        final File database = new File(DatabaseConstants.ABSOLUTE_CURRENT_DB_PATH);
        checkIfDatabaseExists(queryStartTime, database);

        final String tableName = preprocessing(query);
        final File[] allTablesInDb = GlobalUtils.readAllTables(DatabaseConstants.ABSOLUTE_CURRENT_DB_PATH);
        checkIfDatabaseHasTables(queryStartTime, allTablesInDb);

        boolean checkTableExists = false;

        for (final File table : allTablesInDb) {

            if (table.getName().equalsIgnoreCase(tableName + GlobalConstants.EXTENSION_DOT_TXT)) {

                // Apply Exclusive ResourceLockManager
                ResourceLockManager.applyExclusiveLock(QueryManager.dataBaseInUse, tableName);

                final boolean checkDeletion = table.delete();

                // Release Exclusive ResourceLockManager
                ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName);

                if (!checkDeletion) {

                    throw new QueryExecutionRuntimeException(LoggerMessages.unableToDropTable(queryStartTime, tableName));
                }

                checkTableExists = true;
            }
        }
        if (!checkTableExists) {

            throw new QueryExecutionRuntimeException(LoggerMessages.tableDoesNotExist(queryStartTime,tableName));
        }

        //@author Ankush Mudgal - Removes the table from the local metadata file.
        final boolean isDeletedFromLocalMD = MetaDataHandler.deleteFromDBLocalMetaData(tableName, DatabaseConstants.ABSOLUTE_CURRENT_DB_PATH);
        if(isDeletedFromLocalMD){
            generalLogger.logData("Table " + tableName + "has been removed from the local metadata fot the DB " + QueryManager.dataBaseInUse);
        }

        return new ExecutionResponse(true, LoggerMessages.dataBaseTableDropped(queryStartTime, tableName));
    }

}
