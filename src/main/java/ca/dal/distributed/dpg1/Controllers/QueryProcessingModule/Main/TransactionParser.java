package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main;

import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.TransactionQueryRegexen;

import java.util.regex.Pattern;

/**
 * @author Ankush Mudgal
 * Transaction Parser.
 * Parses the input strings to check if the input query has the valid SQL Transaction syntax.
 */
public class TransactionParser {


    /**
     * @author Ankush Mudgal
     * Parse start transaction command.
     *
     * @param startTransactionCommand the start transaction command
     * @return whether the command matches the valid syntax.
     */
    public static boolean parseStartTransactionCommand (String startTransactionCommand){

        if(!startTransactionCommand.trim().isEmpty()){
            return Pattern.matches(TransactionQueryRegexen.START_TRANSACTION_COMMAND, startTransactionCommand);
        }
        return false;
    }

    /**
     * @author Ankush Mudgal
     * Parse commit command.
     *
     * @param commitCommand the commit command
     * @return whether the command matches the valid syntax.
     */
    public static boolean parseCommitCommand (String commitCommand){

        if(!commitCommand.trim().isEmpty()){
            return Pattern.matches(TransactionQueryRegexen.COMMIT_COMMAND, commitCommand);
        }
        
        return false;
    }

    /**
     * @author Ankush Mudgal
     * Parse rollback command.
     *
     * @param rollbackCommand the rollback command
     * @return whether the command matches the valid syntax.
     */
    public static boolean parseRollbackCommand (String rollbackCommand){

        if(!rollbackCommand.trim().isEmpty()){
            return Pattern.matches(TransactionQueryRegexen.ROLLBACK_COMMAND, rollbackCommand);
        }

        return false;
    }
}
