package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main.Queries;

import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.EventLogger;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.GeneralLogger;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Exceptions.QueryExecutionRuntimeException;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Model.ExecutionResponse;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main.QueryManager;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.MetaDataHandler;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.ResourceLockManager;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.LoggerMessages;
import ca.dal.distributed.dpg1.Utils.GlobalConstants;
import ca.dal.distributed.dpg1.Utils.GlobalUtils;
import ca.dal.distributed.dpg1.Utils.RemoteConstants;
import ca.dal.distributed.dpg1.Utils.RemoteUtils;

import java.io.*;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.DatabaseConstants.*;


public class DDLQueries {

    
    private static EventLogger eventLogger = new EventLogger();
    private static GeneralLogger generalLogger = new GeneralLogger();

    public ExecutionResponse createDatabase (String query) throws QueryExecutionRuntimeException {

        final Instant startTime = Instant.now();
        final String queryProcessed = query.substring(0, query.length() - 1);
        final String[] temporaryArray = queryProcessed.split(" ");
        final String databaseName = temporaryArray[2];
        final String databasePath = GlobalConstants.DB_PATH + databaseName;

        final File database = new File(databasePath);
        final boolean databaseExists = database.isDirectory();

        if (databaseExists) {
            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseAlreadyExists(startTime, databaseName));
        }

        // Apply Exclusive ResourceLockManager
        ResourceLockManager.applyExclusiveLock(databaseName, null);

        final boolean isNewDirectoryCreated = database.mkdir();

        // Release Exclusive ResourceLockManager
        ResourceLockManager.releaseExclusiveLock(databaseName, null);

        if (isNewDirectoryCreated) {

                //@author Ankush Mudgal - Writes Database to Global Metadata Metadata file.
            final boolean isGlobalMetaDataUpdated = MetaDataHandler.writeToGlobalMetaData(databaseName);
            if(isGlobalMetaDataUpdated){
                generalLogger.logData("Database " + databaseName + "added to global metadata file.");
            }

            return new ExecutionResponse(true, LoggerMessages.dataBaseCreated(startTime, databaseName));
        } else {

            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseCreationFailed(startTime, databaseName));
        }
    }

    public ExecutionResponse dropDatabase(String query) throws QueryExecutionRuntimeException {
        
        final Instant startTime = Instant.now();
        final String queryProcessed = query.substring(0, query.length() - 1);
        final String[] temporaryArray = queryProcessed.split(" ");
        final String databaseName = temporaryArray[2];
        
        final String databasePath = GlobalConstants.DB_PATH + databaseName;
        final File database = new File(databasePath);
        final boolean isDatabaseExists = GlobalUtils.isDatabasePresent(databaseName);
        
        if (!isDatabaseExists) {
            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseDoesNotExist(startTime, databaseName));
        }

        if (RemoteUtils.isDistributed() && GlobalUtils.isDatabasePresentRemotely(databaseName)) {
            String[] args = {RemoteConstants.COMMAND_REMOTE, RemoteConstants.COMMAND_EXECUTE_QUERY, databaseName, "\"" + query + "\""};
            RemoteUtils.executeInternalCommand(args);
            MetaDataHandler.deleteFromGlobalMetaData(databaseName);
            return null;
        }

        //Apply Exclusive ResourceLockManager
        ResourceLockManager.applyExclusiveLock(databaseName, null);
        final File[] allTables = database.listFiles();
        if (allTables == null) {

            // Release Exclusive ResourceLockManager
            ResourceLockManager.releaseExclusiveLock(databaseName, null);
            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseDeletionFailed(startTime, databaseName));
        }
        
        for (final File table : allTables) {
            
            final boolean isTableDeleted = table.delete();
            if (!isTableDeleted) {
                ResourceLockManager.releaseExclusiveLock(databaseName, null); // Release Exclusive ResourceLockManager
                throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseTablesDeletionFailed(startTime, databaseName));
            }
        }

        final boolean isDatabaseDeleted = database.delete();

        if (isDatabaseDeleted) {
            QueryManager.dataBaseInUse = null;
            ResourceLockManager.releaseExclusiveLock(databaseName, null); // Release Exclusive ResourceLockManager

            //@author Ankush Mudgal - Removes the database form the Global Metadata file
            final boolean isDeletedFromGlobalMD = MetaDataHandler.deleteFromGlobalMetaData(databaseName);
            if(isDeletedFromGlobalMD){
                generalLogger.logData("Database " + databaseName + "removed from the global metadata file.");
            }
            return new ExecutionResponse(true, LoggerMessages.dataBaseDropped(startTime, databaseName));
        } else {
            ResourceLockManager.releaseExclusiveLock(databaseName, null); // Release Exclusive ResourceLockManager
            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseDeletionFailed(startTime, databaseName));
        }
    }

    public ExecutionResponse useDatabase(final String query) throws QueryExecutionRuntimeException {
        final Instant startTime = Instant.now();
        final String queryProcessed = query.substring(0, query.length() - 1);
        final String[] temporaryArray = queryProcessed.split(" ");
        final String databaseName = temporaryArray[2];
        final String databasePath = GlobalConstants.DB_PATH + databaseName;
        final File database = new File(databasePath);
        final boolean isDatabaseExists = GlobalUtils.isDatabasePresent(databaseName);
        if (isDatabaseExists) {
//            final File[] files = new File(GlobalConstants.DB_PATH).listFiles();
//            if (files == null) {
//
//                throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseUsageError(startTime, databaseName));
//            }
//            for (final File file : files) {
//                if (file.getName().equalsIgnoreCase(databaseName)) {
//                    QueryManager.dataBaseInUse = file.getName();
//                }
//            }
            QueryManager.dataBaseInUse = databaseName;

            return new ExecutionResponse(true, LoggerMessages.dataBaseUsageSuccessful(startTime, databaseName));
        } else {

            throw new QueryExecutionRuntimeException( LoggerMessages.dataBaseDoesNotExist(startTime, databaseName));
        }
    }

    public ExecutionResponse createTable(final String query) throws QueryExecutionRuntimeException {
        final Instant startTime = Instant.now();
        if (!QueryManager.isdataBaseInUse()) {

            throw new QueryExecutionRuntimeException(LoggerMessages.noDatabaseSelected(startTime));
        }

        String databaseName = QueryManager.dataBaseInUse;
        if (RemoteUtils.isDistributed() && GlobalUtils.isDatabasePresentRemotely(databaseName)) {
            String[] args = {RemoteConstants.COMMAND_REMOTE, RemoteConstants.COMMAND_EXECUTE_QUERY, databaseName, "\"" + query + "\""};
            RemoteUtils.executeInternalCommand(args);
            return null;
        }

        final String queryProcessed = query.substring(0, query.length() - 1);
        final String[] temporaryArray = queryProcessed.split(" ");
        final String tableName = temporaryArray[2];
        final String databasePath = GlobalConstants.DB_PATH + QueryManager.dataBaseInUse;
        final File database = new File(databasePath);
        final boolean isDatabaseExists = database.isDirectory();
        if (!isDatabaseExists) {

            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseDoesNotExist(startTime, database.toString()));
        }
        final String tablePath = GlobalConstants.DB_PATH + QueryManager.dataBaseInUse + FORWARD_SLASH;
        final File allTablesPath = new File(tablePath);
        final File[] allTables = allTablesPath.listFiles();
        if (allTables == null) {

            throw new QueryExecutionRuntimeException(LoggerMessages.unknownError(startTime));
        }
        boolean isTableExists = false;
        for (final File table : allTables) {
            if (table.getName().equalsIgnoreCase(tableName + GlobalConstants.EXTENSION_DOT_TXT)) {
                isTableExists = true;
            }
        }
        if (isTableExists) {

            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseTableAlreadyExists(startTime, tableName));
        }
        final ExecutionResponse createdTableResponse = createTableWithColumnsUtility(queryProcessed, tableName, tablePath + tableName + GlobalConstants.EXTENSION_DOT_TXT);

        //@author Ankush Mudgal - Writes Table Metadata to the Local Metadata file.
        if(createdTableResponse.isExecutionSuccess()){

            final boolean isMetadataUpdated = MetaDataHandler.writeToDBLocalMetaData(tableName, tablePath);
            if(isMetadataUpdated){
                generalLogger.logData("Added new table " + tableName + " to local metadata for the Database " + QueryManager.dataBaseInUse);
            }
        }

        return createdTableResponse;
    }

 

    private ExecutionResponse createTableWithColumnsUtility(final String query, final String tableName, final String tablePath) throws QueryExecutionRuntimeException {
        final Instant startTime = Instant.now();
        ResourceLockManager.applyExclusiveLock(QueryManager.dataBaseInUse, tableName); // Apply Exclusive ResourceLockManager
        final Pattern pattern = Pattern.compile(CREATE_TABLE_COLUMN_REGEX);
        final Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            final String queryProcessed = matcher.group();
            final String[] columnTokens = queryProcessed.split(",");
            try (final FileWriter fileWriter = new FileWriter(tablePath)) {
                final StringBuilder createStringBuilder = new StringBuilder();
                for (final String columnToken : columnTokens) {
                    final String[] tokens = columnToken.trim().split(" ");
                    if (tokens.length == 2) {
                        createStringBuilder.append(tokens[0])
                                .append("(").append(tokens[1])
                                .append(")")
                                .append(DELIMITER);
                    }
                    if (tokens.length == 4 && tokens[2].equalsIgnoreCase("PRIMARY")) {
                        createStringBuilder.append(tokens[0])
                                .append("(").append(tokens[1]).append("|")
                                .append(GlobalConstants.PRIMARY_KEY_CONSTRAINT)
                                .append(")")
                                .append(DELIMITER);
                    }
                    if (tokens.length == 4 && tokens[2].equalsIgnoreCase("REFERENCES")) {
                        final String foreignKeyTable = tokens[3].split("\\(")[0];
                        String foreignKeyCol = tokens[3].split("\\(")[1].replaceAll("\\)", "");
                        createStringBuilder.append(tokens[0]).append("(")
                                .append(tokens[1]).append("|")
                                .append("FK").append("|")
                                .append(foreignKeyTable).append("|")
                                .append(foreignKeyCol)
                                .append(")")
                                .append(DELIMITER);
                    }
                }

                createStringBuilder.replace(createStringBuilder.length() - DELIMITER.length(), createStringBuilder.length(), "");
                createStringBuilder.append("\n");
                fileWriter.append(createStringBuilder.toString());

                ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
                return new ExecutionResponse(true, LoggerMessages.dataBaseTableCreated(startTime, tableName));

            } catch (final IOException e) {

                ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
                final String message = "Error: " + e.getMessage() + LoggerMessages.getQueryExecutionDuration(startTime, Instant.now());
                eventLogger.logData(message);
                throw new QueryExecutionRuntimeException(message);
            }
        } else {

            ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
            throw new QueryExecutionRuntimeException(LoggerMessages.unknownError(startTime));
        }
    }

    public ExecutionResponse truncateTable(final String query) throws QueryExecutionRuntimeException {
        final Instant startTime = Instant.now();
        if (!QueryManager.isdataBaseInUse()) {
            throw new QueryExecutionRuntimeException(LoggerMessages.noDatabaseSelected(startTime));
        }
        final String queryProcessed = query.substring(0, query.length() - 1);
        final String[] temporaryArray = queryProcessed.split(" ");
        final String tableName = temporaryArray[2];
        final String databasePath = GlobalConstants.DB_PATH + QueryManager.dataBaseInUse;
        final File database = new File(databasePath);
        final boolean isDatabaseExists = database.isDirectory();
        if (!isDatabaseExists) {

            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseDoesNotExist(startTime, QueryManager.dataBaseInUse));
        }

        final File[] allTables = database.listFiles();
        if (allTables == null) {

            throw new QueryExecutionRuntimeException(LoggerMessages.unknownError(startTime));
        }
        boolean isTableExists = false;
        for (final File table : allTables) {
            final String name = table.getName();
            if (name.equalsIgnoreCase(tableName + GlobalConstants.EXTENSION_DOT_TXT)) {
                isTableExists = true;
                break;
            }
        }
        if (!isTableExists) {

            throw new QueryExecutionRuntimeException(LoggerMessages.tableDoesNotExist(startTime, tableName));
        }
        ResourceLockManager.applyExclusiveLock(QueryManager.dataBaseInUse, tableName); // Apply Exclusive ResourceLockManager
        final String tablePath = GlobalConstants.DB_PATH + QueryManager.dataBaseInUse + FORWARD_SLASH + tableName + GlobalConstants.EXTENSION_DOT_TXT;
        try (final BufferedReader bufferedReader = new BufferedReader(new FileReader(tablePath))) {
            final String writeToFile = bufferedReader.readLine();
            if (writeToFile == null || writeToFile.isEmpty()) {

                ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
                throw new QueryExecutionRuntimeException(LoggerMessages.unknownError(startTime));
            }
            final FileWriter fileWriter = new FileWriter(tablePath, false);
            fileWriter.write(writeToFile + "\n");
            fileWriter.close();
        } catch (final IOException e) {

            ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
            throw new QueryExecutionRuntimeException(e.getMessage());
        }

        ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
        return new ExecutionResponse(true, "Table " + tableName + " truncated successfully!");
    }

    public ExecutionResponse dropTable(final String query) throws QueryExecutionRuntimeException {
        final Instant startTime = Instant.now();
        if (!QueryManager.isdataBaseInUse()) {

            throw new QueryExecutionRuntimeException(LoggerMessages.noDatabaseSelected(startTime));
        }
        final String queryProcessed = query.substring(0, query.length() - 1);
        final String[] temporaryArray = queryProcessed.split(" ");
        final String tableName = temporaryArray[2];
        final String databasePath = GlobalConstants.DB_PATH + QueryManager.dataBaseInUse;
        final File database = new File(databasePath);
        final boolean isDatabaseExists = database.isDirectory();
        if (!isDatabaseExists) {

            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseDoesNotExist(startTime, QueryManager.dataBaseInUse));
        }
        final File[] allTables = database.listFiles();
        if (allTables == null) {

            throw new QueryExecutionRuntimeException(LoggerMessages.unknownError(startTime));
        }
        boolean isTableExists = false;
        for (final File table : allTables) {
            if (table.getName().equalsIgnoreCase(tableName + GlobalConstants.EXTENSION_DOT_TXT)) {
                ResourceLockManager.applyExclusiveLock(QueryManager.dataBaseInUse, tableName); // Apply Exclusive ResourceLockManager
                final boolean isTableDeleted = table.delete();
                ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
                if (!isTableDeleted) {

                    throw new QueryExecutionRuntimeException(LoggerMessages.unableToDropTable(startTime, tableName));
                }
                isTableExists = true;
            }
        }
        if (!isTableExists) {

            throw new QueryExecutionRuntimeException(LoggerMessages.tableDoesNotExist(startTime,tableName));
        }

        //@author Ankush Mudgal - Removes the table from the local metadata file.
        final boolean isDeletedFromLocalMD = MetaDataHandler.deleteFromDBLocalMetaData(tableName, databasePath);
        if(isDeletedFromLocalMD){
            generalLogger.logData("Table " + tableName + "has been removed from the local metadata fot the DB " + QueryManager.dataBaseInUse);
        }

        return new ExecutionResponse(true, LoggerMessages.dataBaseTableDropped(startTime, tableName));
    }

}
