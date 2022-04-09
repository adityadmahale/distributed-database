package ca.dal.distributed.dpg1.Controllers.TransactionModule.Main;

import ca.dal.distributed.dpg1.Controllers.LoggerModule.Enums.LoggerType;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.GeneralLogger;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.LoggerFactory;
import ca.dal.distributed.dpg1.Controllers.TransactionModule.Exceptions.TransactionExceptions;
import ca.dal.distributed.dpg1.Controllers.TransactionModule.Utils.TransactionConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import static ca.dal.distributed.dpg1.Controllers.TransactionModule.Utils.TransactionConstants.*;
import static ca.dal.distributed.dpg1.Controllers.TransactionModule.Utils.TransactionConstants.CURRENT_DB_PATH;

public class TransactionMain {

    private String databaseName = null;
    private final GeneralLogger generalLogger;

    private boolean dbCheck() {
        return this.databaseName != null;
    }

    public TransactionMain() {
        this.generalLogger = (GeneralLogger) new LoggerFactory().getLogger(LoggerType.GENERAL_LOGGER);
    }

    /**
     * @author Guryash Singh Dhall
     * @description Function if user enters start transaction
     */

    public void startTransaction() throws TransactionExceptions {
        String currentDBPath = CURRENT_DB_PATH + "/" + this.databaseName + "/";
        String cacheDBPath = CACHE_DB_PATH + "/" + this.databaseName + "/";
        if (!dbCheck()) {
            String error_message = TransactionConstants.ERROR_MESSAGE_DATABASE_NOT_SELECTED;
            generalLogger.logData(error_message);
            throw new TransactionExceptions(error_message);
        }
        File cacheDB = new File(cacheDBPath);
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
            String message = TransactionConstants.SUCCESS_MESSAGE_TXN_STARTED;
            generalLogger.logData(message);
            changeDBPath(true);
//            return new QueryProcessorResponse(true, message);
        } else {
            String error_message = TransactionConstants.ERROR_TRANSACTION_FAILED;
            generalLogger.logData(error_message);
            throw new TransactionExceptions(error_message);
        }


    }

    /**
     * @author Guryash Singh Dhall
     * @description Function if user wants to commit the transaction
     */
    public void commitTransaction()
            throws TransactionExceptions {
        if (!transactionIfPresent) {
            String error_message = TransactionConstants.ERROR_TRANSACTION_DOES_NOT_EXIST;
            generalLogger.logData(error_message);
            throw new TransactionExceptions(error_message);
        }
        String cache_dbpath = DB_PATH;
        cache_dbpath = cache_dbpath.concat("/" + this.databaseName + "/");
        File cacheDB = new File(cache_dbpath);
        if (!cacheDB.isDirectory()) {
            String error_message = ERROR_DATABASE_DOES_NOT_EXIST;
            generalLogger.logData(error_message);
            changeDBPath(false);
            throw new TransactionExceptions(error_message);
        }
        String currentDBPath = CURRENT_DB_PATH + "/" + this.databaseName + "/";
        currentDBPath = currentDBPath.concat("/" + this.databaseName + "/");
        final File currentDB = new File(currentDBPath);
        if (!currentDB.isDirectory()) {
            String error_message = ERROR_DATABASE_DOES_NOT_EXIST;
            generalLogger.logData(error_message);
            changeDBPath(false);
            throw new TransactionExceptions(error_message);
        }
        final File[] allTables = currentDB.listFiles();
        /**
         * @author Guryash Singh Dhall
         * @description Condition if tables are not present
         */
        if (currentDB.mkdir()) {
            final File[] cacheDbTables = cacheDB.listFiles();
            if (cacheDbTables == null) {
                String error_message = ERROR_TRANSACTION_FAILED;
                generalLogger.logData(error_message);
                changeDBPath(false);
                throw new TransactionExceptions(error_message);
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
                    String error_message = ERROR_TABLES_NOT_DELETED;
                    generalLogger.logData(error_message);
                    changeDBPath(false);
                    throw new TransactionExceptions(error_message);
                }
            }
            if (!cacheDB.delete()) {
                String error_message = ERROR_TABLES_NOT_DELETED;
                generalLogger.logData(error_message);
                changeDBPath(false);
                throw new TransactionExceptions(error_message);
            }
            String success_message = SUCCESS_MESSAGE_TXN_COMMITTED;
            generalLogger.logData(success_message);
            changeDBPath(false);
            //TODO:   return a success message that the transaction has been committed
        } else {
            String error_message = TransactionConstants.ERROR_TRANSACTION_FAILED;
            generalLogger.logData(error_message);
            changeDBPath(false);
            throw new TransactionExceptions(error_message);
        }
    }


    /**
     * @author Guryash Singh Dhall
     * @description Function if user wants to rollback the transaction
     */

    public void rollbackTransaction()
            throws TransactionExceptions {

        if (!transactionIfPresent) {
            String error_message = TransactionConstants.ERROR_TRANSACTION_DOES_NOT_EXIST;
            generalLogger.logData(error_message);
            throw new TransactionExceptions(error_message);
        }

        String cacheDBPath = DB_PATH;
        cacheDBPath = cacheDBPath.concat("/" + this.databaseName + "/");
        File cacheDB = new File(cacheDBPath);

        if (!cacheDB.isDirectory()) {
            String error_message = ERROR_DATABASE_DOES_NOT_EXIST;
            generalLogger.logData(error_message);
            changeDBPath(false);
            throw new TransactionExceptions(error_message);
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
                String error_message = ERROR_TABLES_NOT_DELETED;
                generalLogger.logData(error_message);
                changeDBPath(false);
                throw new TransactionExceptions(error_message);
            }
        }

        if (cacheDB.delete()) {
            String success_message = SUCCESS_MESSAGE_TXN_ROLLBACK;
            generalLogger.logData(success_message);
            changeDBPath(false);
            // TODO : return a success messgae that the transaction has been rolled back
        } else {
            String error_message = TransactionConstants.ERROR_TRANSACTION_FAILED;
            generalLogger.logData(error_message);
            changeDBPath(false);
            throw new TransactionExceptions(error_message);
        }
    }

}