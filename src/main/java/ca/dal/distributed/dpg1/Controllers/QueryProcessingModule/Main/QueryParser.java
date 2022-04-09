package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main;

import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.QuerySyntaxRegexen;
import java.util.regex.Pattern;

/**
 * @author Ankush Mudgal
 * Query Parser.
 * Parses the query strings to check if the input query has the valid SQL query syntax.
 */
public class QueryParser {

    /**
     * @author Ankush Mudgal
     * Parse create db query.
     *
     * @param createDBQuery the create db query
     * @return whether the query matches the valid syntax.
     */
    public static boolean parseCreateDBQuery(String createDBQuery){

        if(!createDBQuery.trim().isEmpty()){
            return Pattern.matches(QuerySyntaxRegexen.CREATE_DATABASE_SYNTAX, createDBQuery);
        }
        
        return false;
    }

    /**
     * @author Ankush Mudgal
     * @author Ankush Mudgal
     * Parse create table query.
     *
     * @param createTableQuery the create table query
     * @return whether the query matches the valid syntax.
     */
    public static boolean parseCreateTableQuery(String createTableQuery){

        if(!createTableQuery.trim().isEmpty()){
            return Pattern.matches(QuerySyntaxRegexen.CREATE_TABLE_SYNTAX, createTableQuery);
        }
        
        return false;
    }

    /**
     * @author Ankush Mudgal
     * Parse use db query.
     *
     * @param useDBQuery the use db query
     * @return whether the query matches the valid syntax.
     */
    public static boolean parseUseDBQuery(String useDBQuery){

        if(!useDBQuery.trim().isEmpty()){
            return Pattern.matches(QuerySyntaxRegexen.USE_DATABASE_SYNTAX, useDBQuery);
        }
        
        return false;
    }


    /**
     * @author Ankush Mudgal
     * Parse select query.
     *
     * @param selectQuery the select query
     * @return whether the query matches the valid syntax.
     */
    public static boolean parseSelectQuery(String selectQuery){

        if(!selectQuery.trim().isEmpty()){
            return Pattern.matches(QuerySyntaxRegexen.SELECT_SYNTAX, selectQuery);
        }

        return false;
    }

    /**
     * @author Ankush Mudgal
     * Parse select all query.
     *
     * @param selectAllQuery the select all query
     * @return whether the query matches the valid syntax.
     */
    public static boolean parseSelectAllQuery(String selectAllQuery){

        if(!selectAllQuery.trim().isEmpty()){
            return Pattern.matches(QuerySyntaxRegexen.SELECT_ALL_SYNTAX, selectAllQuery);
        }

        return false;
    }

    /**
     * @author Ankush Mudgal
     * Parse select distinct query.
     *
     * @param selectDistinctQuery the select distinct query
     * @return whether the query matches the valid syntax.
     */
    public static boolean parseSelectDistinctQuery(String selectDistinctQuery){

        if(!selectDistinctQuery.trim().isEmpty()){
            return Pattern.matches(QuerySyntaxRegexen.SELECT_DISTINCT_SYNTAX, selectDistinctQuery);
        }

        return false;
    }

    /**
     * @author Ankush Mudgal
     * Parse insert one or many query.
     *
     * @param insertRows the insert rows
     * @return whether the query matches the valid syntax.
     */
    public static boolean parseInsertOneOrManyQuery(String insertRows){

        if(!insertRows.trim().isEmpty()){
            return Pattern.matches(QuerySyntaxRegexen.INSERT_SYNTAX, insertRows);
        }
        return false;
    }


    /**
     * @author Ankush Mudgal
     * Parse drop db query.
     *
     * @param dropDBQuery the drop db query
     * @return whether the query matches the valid syntax.
     */
    public static boolean parseDropDBQuery(String dropDBQuery){

        if(!dropDBQuery.trim().isEmpty()){
            return Pattern.matches(QuerySyntaxRegexen.DROP_DATABASE_SYNTAX, dropDBQuery);
        }
        return false;
        
    }

    /**
     * @author Ankush Mudgal
     * Parse drop table query.
     *
     * @param dropTableQuery the drop table query
     * @return whether the query matches the valid syntax.
     */
    public static boolean parseDropTableQuery(String dropTableQuery){

        if(!dropTableQuery.trim().isEmpty()){
            return Pattern.matches(QuerySyntaxRegexen.DROP_TABLE_SYNTAX, dropTableQuery);
        }
        return false;
    }

    /**
     * @author Ankush Mudgal
     * Parse truncate table query.
     *
     * @param truncateTableQuery the truncate table query
     * @return whether the query matches the valid syntax.
     */
    public static boolean parseTruncateTableQuery(String truncateTableQuery){

        if(!truncateTableQuery.trim().isEmpty()){
            return Pattern.matches(QuerySyntaxRegexen.TRUNCATE_TABLE_SYNTAX, truncateTableQuery);
        }
        return false;
    }
}
