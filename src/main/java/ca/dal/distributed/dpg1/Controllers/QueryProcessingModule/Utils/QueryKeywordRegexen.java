package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils;

/**
 * @author Ankush Mudgal
 * Query Keyword Regexen - Regex for SQL Query Keywords like CREATE, INSERT, SELECT, etc.
 */
public class QueryKeywordRegexen {


    //Create Keywords

    /**
     * The constant CREATE_DATABASE_KEYWORD.
     */
    public static final String CREATE_DATABASE_KEYWORD = "create database";

    /**
     * The constant CREATE_TABLE_KEYWORD.
     */
    public static final String CREATE_TABLE_KEYWORD = "create table";



    //Use Database Keyword

    /**
     * The constant USE_DATABASE_KEYWORD.
     */
    public static final String USE_DATABASE_KEYWORD = "use database";



    //Select Keywords

    /**
     * The constant SELECT_KEYWORD.
     */
    public static final String SELECT_KEYWORD = "select";

    /**
     * The constant SELECT_ALL_KEYWORD.
     */
    public static final String SELECT_ALL_KEYWORD= "select *";

    /**
     * The constant SELECT_DISTINCT_KEYWORD.
     */
    public static final String SELECT_DISTINCT_KEYWORD = "select distinct";



    //Insert Keyword

    /**
     * The constant INSERT_KEYWORD.
     */
    public static final String INSERT_KEYWORD = "insert";



    //Drop/Delete Keyword

    /**
     * The constant DROP_DATABASE_KEYWORD.
     */
    public static final String DROP_DATABASE_KEYWORD = "drop database";

    /**
     * The constant DROP_TABLE_KEYWORD.
     */
    public static final String DROP_TABLE_KEYWORD = "drop table";

    /**
     * The constant TRUNCATE_TABLE_KEYWORD.
     */
    public static final String TRUNCATE_TABLE_KEYWORD = "truncate table";
}
