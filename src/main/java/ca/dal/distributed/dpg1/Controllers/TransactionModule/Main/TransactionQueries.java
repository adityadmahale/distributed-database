package ca.dal.distributed.dpg1.Controllers.TransactionModule.Main;

import ca.dal.distributed.dpg1.Controllers.LoggerModule.Enums.LoggerType;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.GeneralLogger;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.LoggerFactory;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Exceptions.QueryExecutionRuntimeException;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main.QueryManager;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Model.ExecutionResponse;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.DatabaseConstants;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.LoggerMessages;
import ca.dal.distributed.dpg1.Controllers.TransactionModule.Exceptions.TransactionExceptions;
import ca.dal.distributed.dpg1.Utils.GlobalConstants;
import ca.dal.distributed.dpg1.Utils.GlobalUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import static ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main.QueryManager.changeDBPath;
import static ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main.QueryManager.isTransactionActive;
import static ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.DatabaseConstants.FORWARD_SLASH;

public class TransactionQueries {

    private final GeneralLogger generalLogger;

    public TransactionQueries() {
        this.generalLogger = (GeneralLogger) new LoggerFactory().getLogger(LoggerType.GENERAL_LOGGER);
    }

    /**
     * @author Guryash Singh Dhall
     * @description Function if user enters start transaction
     */

    public ExecutionResponse startTransaction() throws TransactionExceptions {
        String currentDBPath = GlobalConstants.CURRENT_DB_PATH + QueryManager.dataBaseInUse + FORWARD_SLASH;
        String cacheDBPath = GlobalConstants.CACHE_DB_PATH + FORWARD_SLASH + QueryManager.dataBaseInUse + FORWARD_SLASH;
        if (!QueryManager.isDataBaseInUse()) {

            throw new TransactionExceptions(LoggerMessages.noDatabaseSelected(Instant.now()));
        }
        File cacheDB = new File(cacheDBPath);

        //@author Ankush Mudgal - BugFix for Cache DB Deletion Error: Deletes the directory in cache if it already exists.
        if(cacheDB.isDirectory()){
            GlobalUtils.deleteExistingDatabase(cacheDB);
        }
        if (cacheDB.mkdir()) {
            File dbFolder = new File(currentDBPath);
            //Copying all the tables to a cache
            for (final File searchTable : dbFolder.listFiles()) {
                Path source;
                String tableName = searchTable.getName();
                source = Paths.get(currentDBPath + tableName);
                Path destination;
                destination = Paths.get(cacheDBPath + tableName);
                try {
                    Files.copy(source, destination);
                } catch (IOException e) {
                    e.printStackTrace();
                    String error_message = "Error: {" + e.getMessage() + "}!";
                    generalLogger.logData(error_message);
                    throw new TransactionExceptions(error_message);
                }
            }

            changeDBPath(true);
            return new ExecutionResponse(true, LoggerMessages.transactionStarted(Instant.now(), QueryManager.dataBaseInUse));
        } else {

            GlobalUtils.deleteExistingDatabase(cacheDB);
            throw new TransactionExceptions(LoggerMessages.transactionFailed(Instant.now(), QueryManager.dataBaseInUse));
        }


    }

    /**
     * @author Guryash Singh Dhall
     * @description Function if user wants to commit the transaction
     */
    public ExecutionResponse commitTransaction() throws TransactionExceptions {

        if (!isTransactionActive) {
            throw new QueryExecutionRuntimeException(LoggerMessages.transactionFailedToStart(Instant.now(), QueryManager.dataBaseInUse));
        }
        String cache_dbpath = GlobalConstants.DB_PATH;
        cache_dbpath = cache_dbpath.concat(FORWARD_SLASH + QueryManager.dataBaseInUse + FORWARD_SLASH);
        File cacheDB = new File(cache_dbpath);
        if (!cacheDB.isDirectory()) {

            changeDBPath(false);
            throw new TransactionExceptions(LoggerMessages.dataBaseDoesNotExist(Instant.now(), QueryManager.dataBaseInUse));
        }
        String currentDBPath = GlobalConstants.CURRENT_DB_PATH + FORWARD_SLASH + QueryManager.dataBaseInUse + FORWARD_SLASH;
        final File currentDB = new File(currentDBPath);
        if (!currentDB.isDirectory()) {

            changeDBPath(false);
            throw new TransactionExceptions(LoggerMessages.dataBaseDoesNotExist(Instant.now(), QueryManager.dataBaseInUse));
        }

        //Delete the tables in current DB.
        if(!GlobalUtils.deleteExistingDatabase(currentDB)){
            throw new TransactionExceptions(LoggerMessages.dataBaseTablesDeletionFailed(Instant.now(), QueryManager.dataBaseInUse));
        }
        /**
         * @author Guryash Singh Dhall
         * @description Condition if tables are not present
         */
        if (currentDB.mkdir()) {
            final File[] cacheDbTables = cacheDB.listFiles();
            if (cacheDbTables == null) {

                changeDBPath(false);
                throw new TransactionExceptions(LoggerMessages.transactionFailed(Instant.now(), QueryManager.dataBaseInUse));
            }
            /**
             * @description Copy the cache database to current database , as we have the commit command
             */
            for (File searchTable : cacheDbTables) {
                String tableName = searchTable.getName();
                Path source;
                Path destination;
                source = Paths.get(cache_dbpath + tableName);
                destination = Paths.get(currentDBPath + tableName);
                try {
                    Files.copy(source, destination);
                } catch (final IOException e) {
                    e.printStackTrace();
                    String error_message = "Error: {" + e.getMessage() + "}!";
                    generalLogger.logData(error_message);
                    changeDBPath(false);
                    throw new TransactionExceptions(error_message);
                }
            }
            for (final File searchTable : cacheDbTables) {
                if (!searchTable.delete()) {

                    changeDBPath(false);
                    throw new TransactionExceptions(LoggerMessages.dataBaseTablesDeletionFailed(Instant.now(), QueryManager.dataBaseInUse));
                }
            }
            if (!cacheDB.delete()) {

                changeDBPath(false);
                throw new TransactionExceptions(LoggerMessages.dataBaseTablesDeletionFailed(Instant.now(), QueryManager.dataBaseInUse));
            }

            changeDBPath(false);
            return new ExecutionResponse(true, LoggerMessages.transactionCommitted(Instant.now(), QueryManager.dataBaseInUse));
        } else {

            changeDBPath(false);
            throw new TransactionExceptions(LoggerMessages.transactionFailed(Instant.now(), QueryManager.dataBaseInUse));
        }
    }


    /**
     * @author Guryash Singh Dhall
     * @description Function if user wants to rollback the transaction
     */

    public ExecutionResponse rollbackTransaction() throws TransactionExceptions {

        if (!isTransactionActive) {
            throw new QueryExecutionRuntimeException(LoggerMessages.transactionFailedToStart(Instant.now(), QueryManager.dataBaseInUse));
        }

        String cacheDBPath = GlobalConstants.DB_PATH;
        cacheDBPath = cacheDBPath.concat(FORWARD_SLASH + QueryManager.dataBaseInUse + FORWARD_SLASH);
        File cacheDB = new File(cacheDBPath);

        if (!cacheDB.isDirectory()) {

            changeDBPath(false);
            throw new TransactionExceptions(LoggerMessages.dataBaseDoesNotExist(Instant.now(), QueryManager.dataBaseInUse));
        }
        final File[] cacheTables = cacheDB.listFiles();
//        if (cacheTables == null) {
//            final String message = "Error: Database " + cacheDB + " failed to delete!" + " | " +
//                    "Execution Time: " + getQueryExecutionTime(startTime, Instant.now()) + "ms";
//            eventLogController.storeQueryLog(message, Instant.now());
//            updateDatabasePath(false);
//            throw new QueryProcessorException(message);
//        }
        for (File searchTable : cacheTables) {
            if (!searchTable.delete()) {

                changeDBPath(false);
                throw new TransactionExceptions(LoggerMessages.dataBaseTablesDeletionFailed(Instant.now(), QueryManager.dataBaseInUse));
            }
        }

        if (cacheDB.delete()) {

            changeDBPath(false);
            return new ExecutionResponse(true, LoggerMessages.transactionRollback(Instant.now(), QueryManager.dataBaseInUse));
        } else {

            changeDBPath(false);
            throw new TransactionExceptions(LoggerMessages.transactionFailed(Instant.now(), QueryManager.dataBaseInUse));
        }
    }

}