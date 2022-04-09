package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main.Queries;

import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.EventLogger;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Exceptions.QueryExecutionRuntimeException;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main.QueryManager;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Model.ExecutionResponse;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.LoggerMessages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import static ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.DatabaseConstants.*;

public class TransactionQueries {

    private static EventLogger eventLogger = new EventLogger();

    public ExecutionResponse startTransaction() throws QueryExecutionRuntimeException {
        final Instant startTime = Instant.now();
        if (!QueryManager.isdataBaseInUse()) {
            throw new QueryExecutionRuntimeException(LoggerMessages.noDatabaseSelected(startTime));
        }
        final String inServerDBPath = DATABASE_SERVER_PATH + FORWARD_SLASH + QueryManager.dataBaseInUse + FORWARD_SLASH;
        final String inMemoryDBPath = DATABASE_IN_MEMORY_PATH + FORWARD_SLASH + QueryManager.dataBaseInUse + FORWARD_SLASH;
        final File inMemoryDB = new File(inMemoryDBPath);
        if (inMemoryDB.mkdir()) {
            final File inServerDBPathFolder = new File(inServerDBPath);
            final File[] inServerDBPathTables = inServerDBPathFolder.listFiles();
            if (inServerDBPathTables == null) {
                throw new QueryExecutionRuntimeException(LoggerMessages.unknownError(startTime));
            }
            for (final File table : inServerDBPathTables) {
                final Path src = Paths.get(inServerDBPath + table.getName());
                final Path dest = Paths.get(inMemoryDBPath + table.getName());
                try {
                    Files.copy(src, dest);
                } catch (final IOException e) {
                    e.printStackTrace();
                    final String message = "Error: {" + e.getMessage() + "}!" + " | " + LoggerMessages.getQueryExecutionDuration(startTime, Instant.now());
                    eventLogger.logData(message);
                    throw new QueryExecutionRuntimeException(message);
                }
            }

            switchDatabasePaths(true);
            return new ExecutionResponse(true, LoggerMessages.transactionStarted(startTime, QueryManager.dataBaseInUse));
        } else {

            throw new QueryExecutionRuntimeException(LoggerMessages.transactionFailed(startTime, QueryManager.dataBaseInUse));
        }
    }

    public ExecutionResponse commitTransaction() throws QueryExecutionRuntimeException {

        final Instant startTime = Instant.now();
        if (!isTransactionActive) {

            throw new QueryExecutionRuntimeException(LoggerMessages.transactionFailedToStart(startTime, QueryManager.dataBaseInUse));
        }
        final String inMemoryDatabasePath = DATABASE_PATH_IN_USE + FORWARD_SLASH + QueryManager.dataBaseInUse + FORWARD_SLASH;
        final File tempDatabase = new File(inMemoryDatabasePath);
        final boolean isInMemoryDatabaseExists = tempDatabase.isDirectory();
        if (!isInMemoryDatabaseExists) {
            switchDatabasePaths(false);
            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseDoesNotExist(startTime, tempDatabase.toString()));
        }
        final String inServerDatabasePath = DATABASE_SERVER_PATH + FORWARD_SLASH + QueryManager.dataBaseInUse + FORWARD_SLASH;
        final File inServerDatabase = new File(inServerDatabasePath);
        final boolean isInServerDatabaseExists = inServerDatabase.isDirectory();
        if (!isInServerDatabaseExists) {

            switchDatabasePaths(false);
            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseDoesNotExist(startTime, QueryManager.dataBaseInUse));
        }
        final File[] allTables = inServerDatabase.listFiles();
        if (allTables == null) {

            switchDatabasePaths(false);
            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseDeletionFailed(startTime, QueryManager.dataBaseInUse));
        }
        for (final File table : allTables) {
            final boolean isTableDeleted = table.delete();
            if (!isTableDeleted) {

                throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseTablesDeletionFailed(startTime, table.toString()));
            }
        }

        final boolean isInServerDatabaseDeleted = inServerDatabase.delete();
        if (!isInServerDatabaseDeleted) {

            switchDatabasePaths(false);
            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseDeletionFailed(startTime, QueryManager.dataBaseInUse));
        }
        if (inServerDatabase.mkdir()) {
            final File[] inMemoryDatabaseTables = tempDatabase.listFiles();
            if (inMemoryDatabaseTables == null) {

                switchDatabasePaths(false);
                throw new QueryExecutionRuntimeException(LoggerMessages.transactionFailed(startTime, QueryManager.dataBaseInUse));
            }
            for (final File table : inMemoryDatabaseTables) {
                final Path src = Paths.get(inMemoryDatabasePath + table.getName());
                final Path dest = Paths.get(inServerDatabasePath + table.getName());
                try {
                    Files.copy(src, dest);
                } catch (final IOException e) {
                    e.printStackTrace();
                    final String message = "Error: {" + e.getMessage() + "} " + LoggerMessages.getQueryExecutionDuration(startTime, Instant.now());
                    eventLogger.logData(message);
                    switchDatabasePaths(false);
                    throw new QueryExecutionRuntimeException(message);
                }
            }
            for (final File table : inMemoryDatabaseTables) {
                final boolean isTableDeleted = table.delete();
                if (!isTableDeleted) {
                    switchDatabasePaths(false);
                    throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseTablesDeletionFailed(startTime, QueryManager.dataBaseInUse));
                }
            }
            final boolean isInMemoryDatabaseDeleted = tempDatabase.delete();
            if (!isInMemoryDatabaseDeleted) {
                switchDatabasePaths(false);
                throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseDeletionFailed(startTime, QueryManager.dataBaseInUse));
            }


            switchDatabasePaths(false);
            return new ExecutionResponse(true, LoggerMessages.transactionCommitted(startTime, QueryManager.dataBaseInUse));
        } else {

            switchDatabasePaths(false);
            throw new QueryExecutionRuntimeException(LoggerMessages.transactionFailed(startTime, QueryManager.dataBaseInUse));
        }
    }

    public ExecutionResponse rollbackTransaction()
            throws QueryExecutionRuntimeException {
        final Instant startTime = Instant.now();
        if (!isTransactionActive) {

            throw new QueryExecutionRuntimeException(LoggerMessages.transactionFailedToStart(startTime, QueryManager.dataBaseInUse));
        }
        final String inMemoryDatabasePath = DATABASE_PATH_IN_USE + QueryManager.dataBaseInUse;
        final File inMemoryDatabase = new File(inMemoryDatabasePath);
        final boolean isDatabaseExists = inMemoryDatabase.isDirectory();
        if (!isDatabaseExists) {
            switchDatabasePaths(false);
            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseDoesNotExist(startTime, QueryManager.dataBaseInUse));
        }
        final File[] allTables = inMemoryDatabase.listFiles();
        if (allTables == null) {

            switchDatabasePaths(false);
            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseDeletionFailed(startTime, QueryManager.dataBaseInUse));
        }
        for (final File table : allTables) {
            final boolean isTableDeleted = table.delete();
            if (!isTableDeleted) {

                switchDatabasePaths(false);
                throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseTablesDeletionFailed(startTime, table.toString()));
            }
        }
        final boolean isDatabaseDeleted = inMemoryDatabase.delete();
        if (isDatabaseDeleted) {

            switchDatabasePaths(false);
            return new ExecutionResponse(true, LoggerMessages.transactionRollback(startTime, QueryManager.dataBaseInUse));
        } else {

            switchDatabasePaths(false);
            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseDeletionFailed(startTime, QueryManager.dataBaseInUse));
        }
    }

}
