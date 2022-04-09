package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main;

/**
 * @author Ankush Mudgal
 * Query Manager - Class for maintaining runtime values of Database currently in use.
 */
public class QueryManager {

    /**
     * The constant dataBaseInUse - Current database in use by the user/system.
     */
    public static String dataBaseInUse = null;

    /**
     * Checks if the dataBaseInUse has a valid value.
     *
     * @return the boolean
     */
    public static boolean isdataBaseInUse() {
        return dataBaseInUse != null && !dataBaseInUse.isEmpty();
    }
}
