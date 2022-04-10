package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main;

import ca.dal.distributed.dpg1.Controllers.TransactionModule.Utils.TransactionConstants;
import ca.dal.distributed.dpg1.Utils.GlobalConstants;

/**
 * @author Ankush Mudgal
 * Query Manager - Class for maintaining runtime values of Database currently in use.
 */
public class QueryManager {

    /**
     * The constant dataBaseInUse - Current database in use by the user/system.
     */
    public static String dataBaseInUse = null;
    public static boolean isTransactionActive = false;

    /**
     * Checks if the dataBaseInUse has a valid value.
     *
     * @return the boolean
     */
    public static boolean isdataBaseInUse() {
        return dataBaseInUse != null && !dataBaseInUse.isEmpty();
    }


    /**
     * @author Guryash Singh Dhall
     * @description For checking if the transaction has started or not
     */
    public static void changeDBPath(boolean transactionIfPresent) {
        isTransactionActive = transactionIfPresent;
        GlobalConstants.DB_PATH = (transactionIfPresent) ? GlobalConstants.CACHE_DB_PATH : GlobalConstants.CURRENT_DB_PATH;

    }
}
