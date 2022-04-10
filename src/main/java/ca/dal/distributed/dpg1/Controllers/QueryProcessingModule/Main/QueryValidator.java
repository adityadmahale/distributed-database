package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main;

import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Exceptions.QueryParseFailureException;
import static ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.QueryKeywordRegexen.*;
import static ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.TransactionQueryRegexen.*;

/**
 * @author Ankush Mudgal
 * Query Validator.
 * Validates the given query against valid SQL query syntax.
 */
public class QueryValidator {

    /**
     * @author Ankush Mudgal
     * Parse input query boolean.
     *
     * @param inputQuery the input query
     * @return the boolean
     * @throws QueryParseFailureException the query parse failure exception
     */
    public static boolean parseInputQuery(final String inputQuery) throws QueryParseFailureException {

        if (inputQuery == null || inputQuery.trim().isEmpty()) {

            throw new QueryParseFailureException("Invalid Query Format entered by the User.");
        }

        final String queryLowerCase = inputQuery.toLowerCase().trim();

        if (queryLowerCase.contains(CREATE_DATABASE_KEYWORD)) {

            final boolean isValidCreateDBQuery = QueryParser.parseCreateDBQuery(queryLowerCase);
            if (!isValidCreateDBQuery) {
                throw new QueryParseFailureException("Invalid CREATE DATABASE Query entered by the User.");
            } else {
                return true;
            }
        }

        if (queryLowerCase.contains(CREATE_TABLE_KEYWORD)) {

            final boolean isValidCreateTableQuery = QueryParser.parseCreateTableQuery(queryLowerCase);
            if (!isValidCreateTableQuery) {
                throw new QueryParseFailureException("Invalid CREATE TABLE Query entered by the User.");
            } else {
                return true;
            }
        }

        if (queryLowerCase.contains(USE_DATABASE_KEYWORD)) {

            final boolean isValidUseDBQuery = QueryParser.parseUseDBQuery(queryLowerCase);
            if (!isValidUseDBQuery) {
                throw new QueryParseFailureException("Invalid USE DATABASE Query entered by the User.");
            } else {
                return true;
            }
        }

        if (queryLowerCase.contains(DROP_DATABASE_KEYWORD)) {

            final boolean isValidDropDBQuery = QueryParser.parseDropDBQuery(queryLowerCase);
            if (!isValidDropDBQuery) {
                throw new QueryParseFailureException("Invalid DROP DATABASE Query entered by the User.");
            } else {
                return true;
            }
        }

        if (queryLowerCase.contains(DROP_TABLE_KEYWORD)) {

            final boolean isValidDropTableQuery = QueryParser.parseDropTableQuery(queryLowerCase);
            if (!isValidDropTableQuery) {
                throw new QueryParseFailureException("Invalid DROP TABLE Query entered by the User.");
            } else {
                return true;
            }
        }

        if (queryLowerCase.contains(TRUNCATE_TABLE_KEYWORD)) {

            final boolean isValidTruncateTableQuery = QueryParser.parseTruncateTableQuery(queryLowerCase);
            if (!isValidTruncateTableQuery) {
                throw new QueryParseFailureException("Invalid TRUNCATE TABLE Query entered by the User.");
            } else {
                return true;
            }
        }


        if (queryLowerCase.contains(SELECT_KEYWORD)) {

            if (queryLowerCase.contains(SELECT_ALL_KEYWORD)) {

                final boolean isSelectAllSyntaxCorrect = QueryParser.parseSelectAllQuery(queryLowerCase);
                if (!isSelectAllSyntaxCorrect) {
                    throw new QueryParseFailureException("Invalid SELECT ALL Query entered by the User.");
                } else {
                    return true;
                }
            } else if (queryLowerCase.contains(SELECT_DISTINCT_KEYWORD)) {

                final boolean isValidSelectDistinctQuery = QueryParser.parseSelectDistinctQuery(queryLowerCase);
                if (!isValidSelectDistinctQuery) {
                    throw new QueryParseFailureException("Invalid SELECT DISTINCT Query entered by the User.");
                } else {
                    return true;
                }
            } else {

                final boolean isValidSelectOneOrManyColumnsQuery = QueryParser.parseSelectQuery(queryLowerCase);
                if (!isValidSelectOneOrManyColumnsQuery) {
                    throw new QueryParseFailureException("Invalid SELECT Query entered by the User.");
                } else {
                    return true;
                }
            }
        }

        if (queryLowerCase.contains(INSERT_KEYWORD)) {

            final boolean isValidInsertOneOrManyQuery = QueryParser.parseInsertOneOrManyQuery(queryLowerCase);
            if (!isValidInsertOneOrManyQuery) {
                throw new QueryParseFailureException("Invalid INSERT Query entered by the User.");
            } else {
                return true;
            }
        }

        if (queryLowerCase.contains(START_TRANSACTION_KEYWORD)) {

            final boolean isValidTransactionStart = TransactionParser.parseStartTransactionCommand(queryLowerCase);
            if (!isValidTransactionStart) {
                throw new QueryParseFailureException("Invalid START TRANSACTION Command entered by the User.");
            } else {
                return true;
            }
        }

        if (queryLowerCase.contains(COMMIT_KEYWORD)) {

            final boolean isValidCommitCommand = TransactionParser.parseCommitCommand(queryLowerCase);
            if (!isValidCommitCommand) {
                throw new QueryParseFailureException("Invalid COMMIT Command entered by the User.");
            } else {
                return true;
            }
        }

        if (queryLowerCase.contains(ROLLBACK_KEYWORD)) {

            final boolean isStartTransactionKeywordCorrect = TransactionParser.parseRollbackCommand(queryLowerCase);
            if (!isStartTransactionKeywordCorrect) {
                throw new QueryParseFailureException("Invalid ROLLBACK Command entered by the User.");
            } else {
                return true;
            }
        }

        //Throw an exception if query doesn't match any syntax.
        throw new QueryParseFailureException("Invalid query syntax!");

    }

}
