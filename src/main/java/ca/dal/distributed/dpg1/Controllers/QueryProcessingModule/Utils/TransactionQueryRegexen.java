package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils;

/**
 * @author Ankush Mudgal
 * Transaction Query Regexen.
 */
public class TransactionQueryRegexen {

    //Transaction DDLQueries

    /**
     * The constant START_TRANSACTION_KEYWORD.
     */
    public static final String START_TRANSACTION_KEYWORD = "start transaction";

    /**
     * The constant START_TRANSACTION_COMMAND.
     */
    public static final String START_TRANSACTION_COMMAND = "^(?i)START TRANSACTION;$";


    /**
     * The constant COMMIT_KEYWORD.
     */
    public static final String COMMIT_KEYWORD = "commit";


    /**
     * The constant COMMIT_COMMAND.
     */
    public static final String COMMIT_COMMAND = "^(?i)COMMIT;$";


    /**
     * The constant ROLLBACK_KEYWORD.
     */
    public static final String ROLLBACK_KEYWORD = "rollback";


    /**
     * The constant ROLLBACK_COMMAND.
     */
    public static final String ROLLBACK_COMMAND = "^(?i)ROLLBACK;$";
}
