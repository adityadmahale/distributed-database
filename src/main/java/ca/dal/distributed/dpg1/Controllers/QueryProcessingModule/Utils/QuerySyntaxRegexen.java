package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils;

/**
 * @author Ankush Mudgal
 * Query Syntax Regexen - Regex for the syntax of SQL Queries.
 */
public class QuerySyntaxRegexen {


    //Create Statements

    /**
     * The constant CREATE_DATABASE_SYNTAX.
     */
    public static final String CREATE_DATABASE_SYNTAX = "^(?i)(CREATE\\sDATABASE\\s[a-zA-Z\\d]+;)$";

    /**
     * The constant CREATE_TABLE_SYNTAX.
     */
    public static final String CREATE_TABLE_SYNTAX = "^(?i)(CREATE\\sTABLE\\s[a-zA-Z\\d]+\\s\\(([a-zA-Z\\d]+\\s(INT|TEXT|FLOAT|BOOLEAN)(\\sPRIMARY KEY|\\sREFERENCES\\s[a-zA-Z\\d]+\\([a-zA-Z\\d]+\\))?(,\\s[a-zA-Z\\d]+\\s(INT|TEXT|FLOAT|BOOLEAN)(\\sPRIMARY KEY|\\sREFERENCES\\s[a-zA-Z\\d]+\\([a-zA-Z\\d]+\\))?)*)\\);)$";



    //Use Database Statement

    /**
     * The constant USE_DATABASE_SYNTAX.
     */
    public static final String USE_DATABASE_SYNTAX = "^(?i)(USE\\sDATABASE\\s[a-zA-Z\\d]+;)$";



    //Select Statements

    /**
     * The constant SELECT_SYNTAX.
     */
    public static final String SELECT_SYNTAX = "^(?i)(SELECT\\s[a-zA-Z\\d]+(,\\s[a-zA-Z\\d]+)*\\sFROM\\s[a-zA-Z\\d]+;)$";

    /**
     * The constant SELECT_ALL_SYNTAX.
     */
    public static final String SELECT_ALL_SYNTAX = "^(?i)(SELECT\\s\\*\\sFROM\\s[a-zA-Z\\d]+;)$";

    /**
     * The constant SELECT_DISTINCT_SYNTAX.
     */
    public static final String SELECT_DISTINCT_SYNTAX = "^(?i)(SELECT\\sDISTINCT\\s[a-zA-Z\\d]+\\sFROM\\s[a-zA-Z\\d]+;)$";



    //Insert Statements

    /**
     * The constant INSERT_SYNTAX.
     */
    public static final String INSERT_SYNTAX = "^(?i)(INSERT\\sINTO\\s[a-zA-Z\\d]+\\s\\([a-zA-Z\\d]+(,\\s[a-zA-Z\\d]+)*\\)\\sVALUES\\s\\(\\\"?[a-zA-Z\\d\\s~`!@#$^&*-_+=|':;.,?]+\\\"?(,\\s\\\"?[a-zA-Z\\d\\s~`!@#$^&*-_+=|':;.,?]+\\\"?)*\\);)$";



    //Drop/Delete Statements

    /**
     * The constant DROP_DATABASE_SYNTAX.
     */
    public static final String DROP_DATABASE_SYNTAX = "^(?i)(DROP\\sDATABASE\\s[a-zA-Z\\d]+;)$";

    /**
     * The constant DROP_TABLE_SYNTAX.
     */
    public static final String DROP_TABLE_SYNTAX = "^(?i)(DROP\\sTABLE\\s[a-zA-Z\\d]+;)$";

    /**
     * The constant TRUNCATE_TABLE_SYNTAX.
     */
    public static final String TRUNCATE_TABLE_SYNTAX = "^(?i)(TRUNCATE\\sTABLE\\s[a-zA-Z\\d]+;)$";

}
