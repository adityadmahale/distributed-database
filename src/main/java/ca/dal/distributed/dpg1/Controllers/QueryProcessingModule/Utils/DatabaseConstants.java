package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils;

import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main.QueryManager;
import ca.dal.distributed.dpg1.Utils.GlobalConstants;

/**
 * @author Ankush Mudgal
 * Database constants - has the constants used for the processing of the database and the SQL queries.
 */
public class DatabaseConstants {


    //=================> DO NOT MOVE ANY OF THESE TO GLOBAL CONSTANTS - BREAKS FLOW. <==================.

    public static final String ABSOLUTE_CURRENT_DB_PATH = GlobalConstants.DB_PATH + QueryManager.dataBaseInUse;

    public static final String PRIMARY_KEYWORD = "PRIMARY";

    public static final String REFERENCES_KEYWORD = "REFERENCES";

    public static final String COLUMN_TYPE_OPEN_DELIMITER = "(";

    public static final String COLUMN_TYPE_CLOSE_DELIMITER = ")";

    public static final String CARDINALITY_SEPARATOR = "|";

    public static final String CREATE_TABLE_COLUMN_REGEX = "([a-zA-Z\\d]+\\s(INT|TEXT|FLOAT|BOOLEAN)(\\sPRIMARY KEY|\\sREFERENCES\\s[a-zA-Z\\d]+\\([a-zA-Z\\d]+\\))?(,\\s[a-zA-Z\\d]+\\s(INT|TEXT|FLOAT|BOOLEAN)(\\sPRIMARY KEY|\\sREFERENCES\\s[a-zA-Z\\d]+\\([a-zA-Z\\d]+\\))?)*)";

    public static final String INSERT_COLUMN_NAME_REGEX = "([a-zA-Z\\d]+(,\\s[a-zA-Z\\d]+)*\\))";

    public static final String INSERT_COLUMN_VALUE_REGEX = "VALUES\\s\\(\\\"?[a-zA-Z\\d\\s~`!@#$^&*-_+=|':;.,?]+\\\"?(,\\s\\\"?[a-zA-Z\\d\\s~`!@#$^&*-_+=|':;.,?]+\\\"?)*\\)";

    public static final String TEXT_DATATYPE = "TEXT";

    public static final String INT_DATATYPE = "INT";

    public static final String FLOAT_DATATYPE = "FLOAT";

    public static final String BOOLEAN_DATATYPE = "BOOLEAN";

    public static final String FORWARD_SLASH = "/";

}
