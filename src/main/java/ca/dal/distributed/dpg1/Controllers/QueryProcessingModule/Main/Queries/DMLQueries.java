package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main.Queries;

import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.EventLogger;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.GeneralLogger;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Exceptions.QueryExecutionRuntimeException;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Model.ExecutionResponse;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main.QueryManager;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.ResourceLockManager;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.LoggerMessages;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.DatabaseConstants.*;

public class DMLQueries {

    private static EventLogger eventLogger = new EventLogger();
    private static GeneralLogger generalLogger = new GeneralLogger();

    public ExecutionResponse insertInto(final String query) throws QueryExecutionRuntimeException {

        if (!QueryManager.isdataBaseInUse()) {
            throw new QueryExecutionRuntimeException(LoggerMessages.noDatabaseSelected(Instant.now()));
        }

        final String queryProcessed = query.substring(0, query.length() - 1);
        final String[] temporaryArray = queryProcessed.split(" ");
        final String tableName = temporaryArray[2];
        final String databasePath = DATABASE_PATH_IN_USE + QueryManager.dataBaseInUse;
        final File database = new File(databasePath);
        final boolean isDatabaseExists = database.isDirectory();
        if (!isDatabaseExists) {
            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseDoesNotExist(Instant.now(), QueryManager.dataBaseInUse));
        }
        final String tablePath = DATABASE_PATH_IN_USE + QueryManager.dataBaseInUse + FORWARD_SLASH;
        final File allTablesPath = new File(tablePath);
        final File[] allTables = allTablesPath.listFiles();
        if (allTables == null) {
            throw new QueryExecutionRuntimeException(LoggerMessages.unknownError(Instant.now()));
        }
        boolean isTableExists = false;
        for (File table : allTables) {
            if (table.getName().equalsIgnoreCase(tableName + ".txt")) {
                isTableExists = true;
            }
        }
        if (!isTableExists) {
            throw new QueryExecutionRuntimeException(LoggerMessages.tableDoesNotExist(Instant.now(), tableName));
        }
        return insertRowsIntoTabkeUtility(queryProcessed, tableName, tablePath + tableName + ".txt");
    }

    private ExecutionResponse insertRowsIntoTabkeUtility(String queryProcessed, String tableName, String tablePath) throws  QueryExecutionRuntimeException{

        ResourceLockManager.applyExclusiveLock(QueryManager.dataBaseInUse, tableName); // Apply Exclusive ResourceLockManager
        final Pattern pattern1 = Pattern.compile(INSERT_COLUMN_NAME_REGEX);
        final Matcher matcher1 = pattern1.matcher(queryProcessed);

        if (matcher1.find()) {
            final String allColumns = matcher1.group();
            final String[] columnTokensArray = allColumns.replace(")", "").split(",");
            final Set<String> columnTokens = new HashSet<>(Arrays.asList(columnTokensArray));
            final int numberOfColumnTokens = columnTokens.size();
            final Pattern pattern2 = Pattern.compile(INSERT_COLUMN_VALUE_REGEX);
            final Matcher matcher2 = pattern2.matcher(queryProcessed);
            if (matcher2.find()) {
                final String allColumnValues = matcher2.group().substring(8, matcher2.group().length() - 1);
                final String[] columnValues = allColumnValues.replace("\"", "").split(",");
                if (numberOfColumnTokens != columnValues.length) {
                    ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
                    throw new QueryExecutionRuntimeException("Number of columns and values do not match!");
                }
                final Map<String, String> columnValueDetails = new LinkedHashMap<>();
                for (int i = 0; i < numberOfColumnTokens; i++) {
                    columnValueDetails.put(columnTokensArray[i].trim(), columnValues[i].trim());
                }
                try (final FileWriter fileWriter = new FileWriter(tablePath, true);
                     final BufferedReader bufferedReader = new BufferedReader(new FileReader(tablePath))) {
                    final String columnDefinition = bufferedReader.readLine();
                    if (columnDefinition == null || columnDefinition.isEmpty()) {
                        ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
                        throw new QueryExecutionRuntimeException("Fields in the table are not defined!");
                    }
                    final String[] columnDefinitionTokens = columnDefinition.split(DELIMITER_ESCAPED);
                    if (columnDefinitionTokens.length != numberOfColumnTokens) {
                        ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
                        throw new QueryExecutionRuntimeException("Number of fields in the table do not match!");
                    }
                    final LinkedHashMap<String, String> columnDetails = new LinkedHashMap<>();
                    for (final String columnDefinitionToken : columnDefinitionTokens) {
                        final String[] temporaryTokens = columnDefinitionToken.replace(")", "").split("\\(");
                        columnDetails.put(temporaryTokens[0].replace("(", ""), temporaryTokens[1].split("\\|")[0]);
                    }
                    for (int i = 0; i < columnDefinitionTokens.length; i++) {
                        String[] temporaryTokens = columnDefinitionTokens[i].replace(")", "").split("\\(");
                        if (!columnTokensArray[i].trim().equalsIgnoreCase(temporaryTokens[0].replace("(", ""))) {
                            ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
                            throw new QueryExecutionRuntimeException("Sequence of columns does not match!");
                        }
                    }
                    final StringBuilder insertStringBuilder = new StringBuilder();
                    for (final String columnName : columnValueDetails.keySet()) {
                        final String columnDataType = columnDetails.get(columnName);
                        final String columnValue = columnValueDetails.get(columnName);
                        if (columnDataType == null || columnDataType.isEmpty()) {
                            ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
                            throw new QueryExecutionRuntimeException("Invalid data type set for column!");
                        }
                        if (columnDataType.equalsIgnoreCase(INT_DATATYPE)) {
                            try {
                                Integer.parseInt(columnValue);
                            } catch (final NumberFormatException e) {
                                ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
                                throw new QueryExecutionRuntimeException(e.getMessage());
                            }
                        }
                        if (columnDataType.equalsIgnoreCase(FLOAT_DATATYPE)) {
                            try {
                                Float.parseFloat(columnValue);
                            } catch (final NumberFormatException e) {
                                ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
                                throw new QueryExecutionRuntimeException(e.getMessage());
                            }
                        }
                        if (columnDataType.equalsIgnoreCase(BOOLEAN_DATATYPE)) {
                            boolean value = Boolean.parseBoolean(columnValue);
                            if (!value) {
                                ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
                                throw new QueryExecutionRuntimeException("Invalid data type set for column!");
                            }
                        }
                        insertStringBuilder.append(columnValue).append(DELIMITER);
                    }
                    insertStringBuilder.replace(insertStringBuilder.length() - DELIMITER.length(), insertStringBuilder.length(), "");
                    insertStringBuilder.append("\n");
                    fileWriter.append(insertStringBuilder.toString());
                    ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
                    return new ExecutionResponse(true, "Data inserted in table " + tableName + " successfully!");
                } catch (final IOException e) {
                    ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
                    throw new QueryExecutionRuntimeException(e.getMessage());
                }
            } else {
                ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
                throw new QueryExecutionRuntimeException("Invalid INSERT query!");
            }
        } else {
            ResourceLockManager.releaseExclusiveLock(QueryManager.dataBaseInUse, tableName); // Release Exclusive ResourceLockManager
            throw new QueryExecutionRuntimeException("Invalid INSERT query!");
        }
    }

    public ExecutionResponse selectAll(String query) throws QueryExecutionRuntimeException {
        final Instant startTime = Instant.now();
        if (!QueryManager.isdataBaseInUse()) {
            throw new QueryExecutionRuntimeException(LoggerMessages.noDatabaseSelected(startTime));
        }
        final String queryProcessed = query.substring(0, query.length() - 1);
        final String[] temporaryArray = queryProcessed.split(" ");
        final String tableName = temporaryArray[3];
        final String databasePath = DATABASE_PATH_IN_USE + QueryManager.dataBaseInUse;
        final File database = new File(databasePath);
        final boolean isDatabaseExists = database.isDirectory();
        if (!isDatabaseExists) {

            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseDoesNotExist(startTime, QueryManager.dataBaseInUse));
        }
        final String tablePath = DATABASE_PATH_IN_USE + QueryManager.dataBaseInUse + FORWARD_SLASH;
        final File allTablesPath = new File(tablePath);
        final File[] allTables = allTablesPath.listFiles();
        if (allTables == null) {
            throw new QueryExecutionRuntimeException(LoggerMessages.unknownError(startTime));
        }
        boolean isTableExists = false;
        for (final File table : allTables) {
            if (table.getName().equalsIgnoreCase(tableName + ".txt")) {
                isTableExists = true;
            }
        }
        if (!isTableExists) {

            throw new QueryExecutionRuntimeException(LoggerMessages.tableDoesNotExist(startTime, tableName));
        }
        ResourceLockManager.applySharedLock(QueryManager.dataBaseInUse, tableName); // Apply Shared ResourceLockManager
        final String tableFullPath = tablePath + tableName + ".txt";
        try (final FileReader fileReader = new FileReader(tableFullPath);
             final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            final StringBuilder selectStringBuilder = new StringBuilder();
            String tuple;
            boolean isHeading = true;
            while ((tuple = bufferedReader.readLine()) != null) {
                final String[] rawColumns = tuple.split(DELIMITER_ESCAPED);
                selectStringBuilder.append("| ");
                if (isHeading) {
                    for (final String column : rawColumns) {
                        selectStringBuilder.append(column.split("\\(")[0]).append(" | ");
                    }
                    selectStringBuilder.append("\n");
                    isHeading = false;
                } else {
                    for (final String column : rawColumns) {
                        selectStringBuilder.append(column).append(" | ");
                    }
                    selectStringBuilder.append("\n");
                }
            }
            ResourceLockManager.releaseSharedLock(QueryManager.dataBaseInUse, tableName); // Release Shared ResourceLockManager
            final String message = "Data from table " + QueryManager.dataBaseInUse + "." + tableName + " read successfully!" + LoggerMessages.getQueryExecutionDuration(startTime, Instant.now());
            eventLogger.logData(message);
            return new ExecutionResponse(true, selectStringBuilder.toString());

        } catch (final IOException e) {
            ResourceLockManager.releaseSharedLock(QueryManager.dataBaseInUse, tableName); // Release Shared ResourceLockManager
            throw new QueryExecutionRuntimeException(LoggerMessages.unknownError(startTime));
        }
    }


    public ExecutionResponse selectDistinct(final String query) throws QueryExecutionRuntimeException {
        final Instant startTime = Instant.now();
        if (!QueryManager.isdataBaseInUse()) {
            throw new QueryExecutionRuntimeException(LoggerMessages.noDatabaseSelected(startTime));
        }
        final String queryProcessed = query.substring(0, query.length() - 1);
        final String[] temporaryArray = queryProcessed.split(" ");
        final String tableName = temporaryArray[4];
        final String databasePath = DATABASE_PATH_IN_USE + QueryManager.dataBaseInUse;
        final File database = new File(databasePath);
        final boolean isDatabaseExists = database.isDirectory();
        if (!isDatabaseExists) {

            throw new QueryExecutionRuntimeException(LoggerMessages.dataBaseDoesNotExist(startTime, QueryManager.dataBaseInUse));
        }
        final String tablePath = DATABASE_PATH_IN_USE + QueryManager.dataBaseInUse + FORWARD_SLASH;
        final File allTablesPath = new File(tablePath);
        final File[] allTables = allTablesPath.listFiles();
        if (allTables == null) {

            throw new QueryExecutionRuntimeException(LoggerMessages.unknownError(startTime));
        }
        boolean isTableExists = false;
        for (final File table : allTables) {
            if (table.getName().equalsIgnoreCase(tableName + ".txt")) {
                isTableExists = true;
            }
        }
        if (!isTableExists) {

            throw new QueryExecutionRuntimeException(LoggerMessages.tableDoesNotExist(startTime, tableName));
        }
        ResourceLockManager.applySharedLock(QueryManager.dataBaseInUse, tableName); // Apply Shared ResourceLockManager
        final String columnName = temporaryArray[2];
        final String tableFullPath = tablePath + tableName + ".txt";
        try (final FileReader fileReader = new FileReader(tableFullPath);
             final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            final StringBuilder selectStringBuilder = new StringBuilder();
            String tuple;
            boolean isHeading = true;
            int columnIndexInInterest = -1;
            final Set<String> uniqueElements = new LinkedHashSet<>();
            while ((tuple = bufferedReader.readLine()) != null) {
                final String[] rawColumns = tuple.split(DELIMITER_ESCAPED);
                if (isHeading) {
                    for (int i = 0; i < rawColumns.length; i++) {
                        final String column = rawColumns[i];
                        if (columnName.equals((column.split("\\(")[0]))) {
                            columnIndexInInterest = i;
                            selectStringBuilder.append("| ").append(column.split("\\(")[0]).append(" | ").append("\n");
                            break;
                        }
                    }
                    if (columnIndexInInterest == -1) {
                        throw new QueryExecutionRuntimeException("Column " + columnName + " does not exists!");
                    }
                    isHeading = false;
                } else {
                    uniqueElements.add(rawColumns[columnIndexInInterest]);
                }
            }
            for (String element : uniqueElements) {
                selectStringBuilder.append("| ").append(element).append(" | ").append("\n");
            }
            ResourceLockManager.releaseSharedLock(QueryManager.dataBaseInUse, tableName); // Release Shared ResourceLockManager
            final String message = "Data from table " + QueryManager.dataBaseInUse + "." + tableName + " read successfully" + LoggerMessages.getQueryExecutionDuration(startTime, Instant.now());
            generalLogger.logData(message);
            eventLogger.logData(message);
            return new ExecutionResponse(true, selectStringBuilder.toString());
        } catch (final IOException e) {
            ResourceLockManager.releaseSharedLock(QueryManager.dataBaseInUse, tableName); // Release Shared ResourceLockManager
            throw new QueryExecutionRuntimeException(LoggerMessages.unknownError(startTime));
        }
    }



}
