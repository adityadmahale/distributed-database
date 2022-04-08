package ca.dal.distributed.dpg1.Controllers.TransactionModule.Utils;
import ca.dal.distributed.dpg1.Utils.GlobalConstants;

/**
 * @author Guryash Singh Dhall
 * @description Constants file for the Transaction Module
 */
public final class TransactionConstants {

    public static final String CURRENT_DB_PATH = "./db";
    public static final String CACHE_DB_PATH = "./cache";
    public static final String ERROR_MESSAGE_DATABASE_NOT_SELECTED = "Error: No Database Selected ";
    public static final String ERROR_TABLES_NOT_FOUND_IN_DB= "Error: Tables not found in database, Txn failed";
    public static final String ERROR_TRANSACTION_FAILED= "Error: Transaction failed";
    public static final String ERROR_DATABASE_DOES_NOT_EXIST ="Database does not exist";
    public static final String ERROR_TABLES_NOT_DELETED =" Error : Unable to delete tables";
    public static final String ERROR_TRANSACTION_DOES_NOT_EXIST= "Transaction doesn't exist ";
    public static final String SUCCESS_MESSAGE_TXN_STARTED = "Transaction started ";
    public static final String SUCCESS_MESSAGE_TXN_COMMITTED = "Transaction committed ";
    public static final String SUCCESS_MESSAGE_TXN_ROLLBACK = "Transaction rolled back ";
    public static String DB_PATH = CURRENT_DB_PATH;
    public static boolean transactionIfPresent = false;
    /**
     * @author Guryash Singh Dhall
     * @description For checking if the transaction has started or not
     */
    public static void changeDBPath(boolean transactionIfPresent) {
        TransactionConstants.transactionIfPresent = transactionIfPresent;
        DB_PATH = (transactionIfPresent) ? CACHE_DB_PATH : CURRENT_DB_PATH;
    }

}
