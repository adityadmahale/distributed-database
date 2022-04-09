package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils;

/**
 * @author Ankush Mudgal
 * Database constants - has the constants used for the processing of the database and the SQL queries.
 */
public class DatabaseConstants {


    public static boolean isTransactionActive = false;

    public static final String DATABASE_SERVER_PATH = "db/";

    public static final String DATABASE_IN_MEMORY_PATH = "db/in_memory/";

    public static String DATABASE_PATH_IN_USE = DATABASE_SERVER_PATH;

    public static final String DELIMITER = "|+|";

    public static final String DELIMITER_ESCAPED = "\\|\\+\\|";

    public static final String CREATE_TABLE_COLUMN_REGEX = "([a-zA-Z\\d]+\\s(INT|TEXT|FLOAT|BOOLEAN)(\\sPRIMARY KEY|\\sREFERENCES\\s[a-zA-Z\\d]+\\([a-zA-Z\\d]+\\))?(,\\s[a-zA-Z\\d]+\\s(INT|TEXT|FLOAT|BOOLEAN)(\\sPRIMARY KEY|\\sREFERENCES\\s[a-zA-Z\\d]+\\([a-zA-Z\\d]+\\))?)*)";

    public static final String INSERT_COLUMN_NAME_REGEX = "([a-zA-Z\\d]+(,\\s[a-zA-Z\\d]+)*\\))";

    public static final String INSERT_COLUMN_VALUE_REGEX = "VALUES\\s\\(\\\"?[a-zA-Z\\d\\s~`!@#$^&*-_+=|':;.,?]+\\\"?(,\\s\\\"?[a-zA-Z\\d\\s~`!@#$^&*-_+=|':;.,?]+\\\"?)*\\)";

    public static final String TEXT_DATATYPE = "TEXT";

    public static final String INT_DATATYPE = "INT";

    public static final String FLOAT_DATATYPE = "FLOAT";

    public static final String BOOLEAN_DATATYPE = "BOOLEAN";

    public static final String FORWARD_SLASH = "/";

    /**
     * @author Ankush Mudgal
     * Switch database paths based on whether there's a Transaction active or not.
     *
     * @param isTransactionFlow the is transaction flow
     */
    public static void switchDatabasePaths(boolean isTransactionFlow) {
        DatabaseConstants.isTransactionActive = isTransactionFlow;
        DATABASE_PATH_IN_USE = (isTransactionFlow) ? DATABASE_IN_MEMORY_PATH : DATABASE_SERVER_PATH;
    }
}
