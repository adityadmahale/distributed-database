package ca.dal.distributed.dpg1.Utils;

/**
 * @author Bharatwaaj Shankaranarayanan
 * @description Global Constants for Entire Application
 */
public class GlobalConstants {

    public static final String DELIMITER = "|+|";
    public static final String DATABASE_PATH = "db/";
    public static final String PRIMARY_KEY_CONSTRAINT = "PK";
    public static final String FOREIGN_KEY_CONSTRAINT = "FK";

    public static final String TABLE_NAME_DELIMITER = "\\.";

    public static final String COLUMN_NAME_DELIMITER = "\\(";
    public static final String COLUMN_ATTRIBUTES_DELIMITER = "\\|";
    public static final String COLUMN_EMPTY_SPACE = " ";


    public static final String PATTERN_DATABASE_NAME = "[A-Za-z\\d]+";

    public static final String STRING_ERROR_MESSAGE_PREFIX = "ERROR { ";
    public static final String STRING_ERROR_MESSAGE_SUFFIX = " }";

    /* Common String Constants */
    public static final String STRING_UNDERSCORE = "_";
    public static final String STRING_NEXT_LINE = "\n";
    public static final String STRING_COLON_SEPARATOR = " : ";
    public static final String STRING_PIPE_SEPARATOR = " | ";
    public static final String STRING_ARROW_SEPARATOR = " -> ";
    public static final String STRING_HYPHEN = "-";
    public static final String STRING_PERIOD = ".";

    /* Extension Constants */
    public static final String EXTENSION_DOT_TXT = ".txt";

    public static final String ERROR_MESSAGE_INVALID_DB_PATH = "Invalid database path or database does not exist.";
    public static final String STRING_NULL = null;
}
