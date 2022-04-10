package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main.Queries;

import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Exceptions.QueryExecutionRuntimeException;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Model.ExecutionResponse;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main.QueryManager;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.ResourceLockManager;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.LoggerMessages;
import ca.dal.distributed.dpg1.Utils.GlobalConstants;
import ca.dal.distributed.dpg1.Utils.GlobalUtils;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.DatabaseConstants.*;
import static ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.QueryProcessingUtil.*;
import static ca.dal.distributed.dpg1.Utils.GlobalConstants.*;

/**
 * @author Ankush Mudgal
 * DML Queries - Class to process and execute SQL DML Queries.
 */
public class DMLQueries {

    /**
     * @author Ankush Mudgal
     * Method executes Insert Into SQL Query.
     *
     * @param query the query
     * @return the execution response
     * @throws QueryExecutionRuntimeException the query execution runtime exception
     */
    public ExecutionResponse insertInto(final String query) throws QueryExecutionRuntimeException {

        final Instant queryStartTime = Instant.now();
        final String tableName = preprocessing(query);

        //Bugfix
        final String databasePath = GlobalConstants.DB_PATH + QueryManager.dataBaseInUse;
        final File database = new File(databasePath);
        checkIfDatabaseExists(queryStartTime, database);

        final String absoluteTablePath = DB_PATH + QueryManager.dataBaseInUse + FORWARD_SLASH;
        final File[] allTablesInDB = GlobalUtils.readAllTables(absoluteTablePath);
        checkIfDatabaseHasTables(queryStartTime, allTablesInDB);

        boolean checkTableExists = false;

        for (File table : allTablesInDB) {

            if (table.getName().equalsIgnoreCase(tableName + EXTENSION_DOT_TXT)) {
                checkTableExists = true;
                break;
            }
        }

        if (!checkTableExists) {
            throw new QueryExecutionRuntimeException(LoggerMessages.tableDoesNotExist(Instant.now(), tableName));
        }

        String plainQuery = query.substring(0, query.length() - 1);
        return insertRowsIntoTableUtility(plainQuery, tableName, absoluteTablePath + tableName + EXTENSION_DOT_TXT);
    }


    /**
     * @author Ankush Mudgal
     * Inserts rows into table.
     *
     * @param plainQuery the plain query
     * @param tableName  the table name
     * @param tablePath  the table path
     * @return the execution response
     * @throws QueryExecutionRuntimeException the query execution runtime exception
     */
    private ExecutionResponse insertRowsIntoTableUtility(String plainQuery, String tableName, String tablePath) throws  QueryExecutionRuntimeException{

        ResourceLockManager.applyExclusiveLock(QueryManager.dataBaseInUse, tableName); // Apply Exclusive ResourceLockManager
        final Pattern pattern = Pattern.compile(INSERT_COLUMN_NAME_REGEX);
        final Matcher matcher = pattern.matcher(plainQuery);

        if (matcher.find()) {
            final String allColumns = matcher.group();

            final String[] splitColTokens = allColumns.replace(COLUMN_TYPE_CLOSE_DELIMITER, STRING_EMPTY).split(STRING_COMMA);
            final Set<String> columnTokens = new HashSet<>(Arrays.asList(splitColTokens));

            final int numberOfTokens = columnTokens.size();

            final Pattern patternInsert = Pattern.compile(INSERT_COLUMN_VALUE_REGEX);
            final Matcher matcherInsert = patternInsert.matcher(plainQuery);
            if (matcherInsert.find()) {

                final String allColumnValues = matcherInsert.group().substring(8, matcherInsert.group().length() - 1);
                final String[] columnValArray = allColumnValues.replace("\"", STRING_EMPTY).split(STRING_COMMA);
                if (numberOfTokens != columnValArray.length) {

                    // Release Exclusive Lock
                    ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName);

                    throw new QueryExecutionRuntimeException(LoggerMessages.columnMappingFailed(Instant.now(), tableName));
                }

                final Map<String, String> columnValDetails = new LinkedHashMap<>();

                for (int i = 0; i < numberOfTokens; i++) {

                    columnValDetails.put(splitColTokens[i].trim(), columnValArray[i].trim());
                }

                try (final FileWriter fileWriter = new FileWriter(tablePath, true);

                     final BufferedReader bufferedReader = new BufferedReader(new FileReader(tablePath))) {
                        final String line = bufferedReader.readLine();
                    if (line == null || line.isEmpty()) {

                        // Release Exclusive ResourceLockManager
                        ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName);
                        throw new QueryExecutionRuntimeException(LoggerMessages.columnFieldsNotDefined(Instant.now(), tableName));
                    }

                    final String[] colDefinitionTokens = line.split(DELIMITER_ESCAPED);

                    if (colDefinitionTokens.length != numberOfTokens) {


                        ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
                        throw new QueryExecutionRuntimeException(LoggerMessages.columnFieldsDoNotMatch(Instant.now(), tableName));
                    }

                    final LinkedHashMap<String, String> columnDetailMap = new LinkedHashMap<>();

                    for (final String token : colDefinitionTokens) {

                        final String[] temporaryTokens = token.replace(COLUMN_TYPE_CLOSE_DELIMITER, STRING_EMPTY).split(COLUMN_NAME_DELIMITER);
                        columnDetailMap.put(temporaryTokens[0].replace(COLUMN_TYPE_OPEN_DELIMITER, STRING_EMPTY), temporaryTokens[1].split(COLUMN_ATTRIBUTES_DELIMITER)[0]);
                    }

                    for (int i = 0; i < colDefinitionTokens.length; i++) {

                        String[] tempTokenArray = colDefinitionTokens[i].replace(COLUMN_TYPE_CLOSE_DELIMITER, STRING_EMPTY).split(COLUMN_NAME_DELIMITER);
                        if (!splitColTokens[i].trim().equalsIgnoreCase(tempTokenArray[0].replace(COLUMN_TYPE_OPEN_DELIMITER, STRING_EMPTY))) {

                            ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
                            throw new QueryExecutionRuntimeException(LoggerMessages.columnSequenceMappingFailed(Instant.now(), tableName));
                        }
                    }
                    final StringBuilder builder = new StringBuilder();

                    for (final String columnName : columnValDetails.keySet()) {

                        final String columnDataType = columnDetailMap.get(columnName);
                        final String columnValue = columnValDetails.get(columnName);

                        if (columnDataType == null || columnDataType.isEmpty()) {

                            ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
                            throw new QueryExecutionRuntimeException(LoggerMessages.invalidDataTypeForColumn(Instant.now(), tableName));
                        }

                        if (columnDataType.equalsIgnoreCase(INT_DATATYPE)) {
                            isIntDataTypeValid(tableName, columnValue);
                        }

                        if (columnDataType.equalsIgnoreCase(FLOAT_DATATYPE)) {
                            isFloatDataTypeValid(tableName, columnValue);
                        }

                        if (columnDataType.equalsIgnoreCase(BOOLEAN_DATATYPE)) {
                            isBooleanDataTypeValid(tableName, columnValue);
                        }

                        builder.append(columnValue).append(DELIMITER);
                    }

                    builder.replace(builder.length() - DELIMITER.length(), builder.length(), STRING_EMPTY).append(STRING_NEXT_LINE);
                    fileWriter.append(builder.toString());

                    // Release Exclusive ResourceLockManager
                    ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName);
                    return new ExecutionResponse(true, "Data inserted in table " + tableName + " successfully.");

                } catch (final IOException e) {

                    // Release Exclusive Lock
                    ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName);
                    throw new QueryExecutionRuntimeException(e.getMessage());
                }

            } else {

                // Release Exclusive Lock
                ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName);
                throw new QueryExecutionRuntimeException("Invalid INSERT query entered by the User.");
            }
        } else {

            // Release Exclusive Lock
            ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName);
            throw new QueryExecutionRuntimeException("Invalid INSERT query entered by the User.");
        }
    }

    /**
     * @author Ankush Mudgal
     * Method executes Select all SQL Query.
     *
     * @param query the query
     * @return the execution response
     * @throws QueryExecutionRuntimeException the query execution runtime exception
     */
    public ExecutionResponse selectAll(String query) throws QueryExecutionRuntimeException {

        final Instant startTime = Instant.now();
        checkIfDataBaseSelected(startTime);

        final String strippedQuery = query.substring(0, query.length() - 1);
        final String[] temporaryArray = strippedQuery.split(COLUMN_EMPTY_SPACE);
        final String tableName = temporaryArray[3];

        final File database = new File(ABSOLUTE_CURRENT_DB_PATH);
        checkIfDatabaseExists(Instant.now(), database);

        final String absoluteTablePath = ABSOLUTE_CURRENT_DB_PATH + FORWARD_SLASH;
        final File[] allTablesInDB = GlobalUtils.readAllTables(absoluteTablePath);
        checkIfDatabaseHasTables(Instant.now(), allTablesInDB);

        boolean checkTableExists = false;

        for (final File table : allTablesInDB) {

            if (table.getName().equalsIgnoreCase(tableName + ".txt")) {
                checkTableExists = true;
                break;
            }
        }
        if (!checkTableExists) {

            throw new QueryExecutionRuntimeException(LoggerMessages.tableDoesNotExist(startTime, tableName));
        }

        // Apply Shared Lock
        ResourceLockManager.applySharedLock(QueryManager.dataBaseInUse, tableName);
        
        final String tableFullPath = absoluteTablePath + tableName + EXTENSION_DOT_TXT;
        
        try (final FileReader fileReader = new FileReader(tableFullPath);
             final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            
            final StringBuilder builder = new StringBuilder();
            String tupleString;
            boolean isColumnHeader = true;
            
            while ((tupleString = bufferedReader.readLine()) != null) {
                
                final String[] rawColumnValues = tupleString.split(DELIMITER_ESCAPED);
                builder.append("| ");
                
                if (isColumnHeader) {
                    
                    for (final String column : rawColumnValues) {
                        builder.append(column.split(COLUMN_NAME_DELIMITER)[0]).append(STRING_PIPE_SEPARATOR);
                    }
                    builder.append(STRING_NEXT_LINE);
                    isColumnHeader = false;
                } else {
                    
                    for (final String column : rawColumnValues) {
                        builder.append(column).append(STRING_PIPE_SEPARATOR);
                    }
                    builder.append(STRING_NEXT_LINE);
                }
            }

            // Release Shared Lock
            ResourceLockManager.releaseSharedLock(QueryManager.dataBaseInUse, tableName);
            LoggerMessages.dataReadSuccessful(Instant.now(), tableName);
            
            return new ExecutionResponse(true, builder.toString());

        } catch (final IOException e) {

            // Release Shared Lock
            ResourceLockManager.releaseSharedLock(QueryManager.dataBaseInUse, tableName); 
            throw new QueryExecutionRuntimeException(LoggerMessages.unknownError(startTime));
        }
    }


    /**
     * @author Ankush Mudgal
     * Method executes Select distinct SQL query.
     *
     * @param query the query
     * @return the execution response
     * @throws QueryExecutionRuntimeException the query execution runtime exception
     */
    public ExecutionResponse selectDistinct(final String query) throws QueryExecutionRuntimeException {
        
        final Instant startTime = Instant.now();
        checkIfDataBaseSelected(startTime);
        
        final String queryProcessed = query.substring(0, query.length() - 1);
        final String[] temporaryArray = queryProcessed.split(COLUMN_EMPTY_SPACE);
        final String tableName = temporaryArray[4];
        
        final File database = new File(ABSOLUTE_CURRENT_DB_PATH);
        checkIfDatabaseExists(Instant.now(), database);
        
        final String absoluteTablePath = ABSOLUTE_CURRENT_DB_PATH + FORWARD_SLASH;
        final File[] allTablesInDB = GlobalUtils.readAllTables(absoluteTablePath);
        checkIfDatabaseHasTables(Instant.now(), allTablesInDB);

        boolean checkIfTableExists = false;
        for (final File table : allTablesInDB) {
            if (table.getName().equalsIgnoreCase(tableName + EXTENSION_DOT_TXT)) {
                checkIfTableExists = true;
                break;
            }
        }

        if (!checkIfTableExists) {

            throw new QueryExecutionRuntimeException(LoggerMessages.tableDoesNotExist(startTime, tableName));
        }

        // Apply Shared Lock
        ResourceLockManager.applySharedLock(QueryManager.dataBaseInUse, tableName);
        final String columnName = temporaryArray[2];
        final String tableFullPath = absoluteTablePath + tableName + EXTENSION_DOT_TXT;

        try (final FileReader fileReader = new FileReader(tableFullPath);
             final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            final StringBuilder builder = new StringBuilder();
            String tupleString;
            boolean isColumnHeader = true;
            int columnIdx = -1;
            final Set<String> uniqueValues = new LinkedHashSet<>();

            while ((tupleString = bufferedReader.readLine()) != null) {

                final String[] rawColumns = tupleString.split(DELIMITER_ESCAPED);
                if (isColumnHeader) {
                    for (int i = 0; i < rawColumns.length; i++) {
                        final String column = rawColumns[i];
                        if (columnName.equals((column.split(COLUMN_NAME_DELIMITER)[0]))) {
                            columnIdx = i;
                            builder.append("| ").append(column.split(COLUMN_NAME_DELIMITER)[0]).append(STRING_PIPE_SEPARATOR).append(STRING_NEXT_LINE);
                            break;
                        }
                    }
                    if (columnIdx == -1) {
                        throw new QueryExecutionRuntimeException("Column with the " + columnName + " does not exists in the table " + tableName + " in DB " + QueryManager.dataBaseInUse) ;
                    }
                    isColumnHeader = false;

                } else {
                    uniqueValues.add(rawColumns[columnIdx]);
                }
            }

            for (String value : uniqueValues) {
                builder.append("| ").append(value).append(STRING_PIPE_SEPARATOR).append(STRING_NEXT_LINE);
            }

            // Release Shared Lock
            ResourceLockManager.releaseSharedLock(QueryManager.dataBaseInUse, tableName);
            LoggerMessages.dataReadSuccessful(Instant.now(), tableName);
            return new ExecutionResponse(true, builder.toString());

        } catch (final IOException e) {

            // Release Shared Lock
            ResourceLockManager.releaseSharedLock(QueryManager.dataBaseInUse, tableName);
            throw new QueryExecutionRuntimeException(LoggerMessages.unknownError(startTime));
        }
    }


    /**
     * @author Ankush Mudgal
     * Checks if column value is of boolean data type.
     *
     * @param tableName   the table name
     * @param columnValue the column value
     */
    private void isBooleanDataTypeValid(String tableName, String columnValue) {
        boolean value = Boolean.parseBoolean(columnValue);
        if (!value) {
            ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
            throw new QueryExecutionRuntimeException(LoggerMessages.invalidDataTypeForColumn(Instant.now(), tableName));
        }
    }

    /**
     * @author Ankush Mudgal
     * Checks if column value is of int data type.
     *
     * @param tableName   the table name
     * @param columnValue the column value
     */
    private void isIntDataTypeValid(String tableName, String columnValue) {
        try {
            Integer.parseInt(columnValue);
        } catch (final NumberFormatException e) {
            ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
            throw new QueryExecutionRuntimeException(e.getMessage());
        }
    }

    /**
     * @author Ankush Mudgal
     * Checks if column value is of float data type.
     *
     * @param tableName   the table name
     * @param columnValue the column value
     */
    public void isFloatDataTypeValid(String tableName, String columnValue) {
        try {
            Float.parseFloat(columnValue);
        } catch (final NumberFormatException e) {
            ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
            throw new QueryExecutionRuntimeException(e.getMessage());
        }
    }




}
