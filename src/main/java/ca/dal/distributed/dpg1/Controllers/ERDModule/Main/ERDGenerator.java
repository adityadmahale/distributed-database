package ca.dal.distributed.dpg1.Controllers.ERDModule.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import ca.dal.distributed.dpg1.Controllers.ERDModule.Exceptions.ERDGeneratorException;
import ca.dal.distributed.dpg1.Controllers.ERDModule.Utils.ERDConstants;
import ca.dal.distributed.dpg1.Controllers.ERDModule.Utils.ERDUtils;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Enums.LoggerType;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.EventLogger;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.GeneralLogger;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.LoggerFactory;
import ca.dal.distributed.dpg1.Utils.GlobalConstants;
import ca.dal.distributed.dpg1.Utils.GlobalUtils;
import ca.dal.distributed.dpg1.Utils.RemoteConstants;
import ca.dal.distributed.dpg1.Utils.RemoteUtils;

/**
 * @author Bharatwaaj Shankaranarayanan
 * @description ERDGenerator Module
 */
public class ERDGenerator extends ERDGeneratorMain {

    private final GeneralLogger generalLogger;
    private final EventLogger eventLogger;

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Helps in initializing the loggers that we would need in this
     *              module
     */
    public ERDGenerator() {
        this.generalLogger = (GeneralLogger) new LoggerFactory().getLogger(LoggerType.GENERAL_LOGGER);
        this.eventLogger = (EventLogger) new LoggerFactory().getLogger(LoggerType.EVENT_LOGGER);
    }

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Helps in generating ERD Output file
     */
    private String getERDGeneratorOutputFile(final String databaseName) {
        return ERDConstants.SQL_ERD_PATH + ERDConstants.SQL_ERD_FILE_PREFIX + databaseName + GlobalConstants.EXTENSION_DOT_TXT;
    }

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Core main function that creates the ERD and writes into a file
     */
    @Override
    protected void createERD(final File[] allTables,
            final String databaseName) throws ERDGeneratorException {
        final String erdGeneratorOutputFile = getERDGeneratorOutputFile(databaseName);
        for (final File tableName : allTables) {
            try (final FileWriter fileWriter = new FileWriter(erdGeneratorOutputFile, true);
                    final FileReader fileReader = new FileReader(tableName);
                    final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                final String tableNameString = tableName.getName().split(GlobalConstants.TABLE_NAME_DELIMITER)[0];
                fileWriter.append(tableNameString + GlobalConstants.STRING_NEXT_LINE);
                StringBuilder stringBuilder = new StringBuilder();
                appendHyphens(tableNameString, stringBuilder);
                fileWriter.append(stringBuilder.toString() + GlobalConstants.STRING_NEXT_LINE);
                final String tableColumnNames = bufferedReader.readLine();
                final String[] tableColumnNamesSplit = tableColumnNames.split(GlobalConstants.DELIMITER);
                final List<String> cardinalities = new ArrayList<>();
                writeERDToFile(fileWriter, tableNameString, tableColumnNamesSplit, cardinalities);
                fileWriter.append(GlobalConstants.STRING_NEXT_LINE);
                handleCardinalities(fileWriter, cardinalities);
                fileWriter.append(GlobalConstants.STRING_NEXT_LINE);
            } catch (final IOException e) {
                final String message = GlobalConstants.STRING_ERROR_MESSAGE_PREFIX + e.getMessage()
                        + GlobalConstants.STRING_ERROR_MESSAGE_SUFFIX;
                eventLogger.logData(message);
                throw new ERDGeneratorException(message);
            }
        }
    }

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Helps in handling cardinality
     */
    private String cardinalityStringGenerator(final String sourceTable, final String sourceColumn,
            final String destinationTable,
            final String destinationColumn) {
        return sourceTable + GlobalConstants.STRING_PERIOD + sourceColumn + GlobalConstants.STRING_ARROW_SEPARATOR
                + destinationTable + GlobalConstants.STRING_PERIOD + destinationColumn;
    }

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Writing ERD to the file
     */
    @Override
    protected void writeERDToFile(final FileWriter fileWriter, final String tableNameString,
            final String[] tableColumnNamesSplit, final List<String> cardinalities) throws IOException {
        for (final String tableColumnName : tableColumnNamesSplit) {
            final String[] columns = tableColumnName.split(GlobalConstants.COLUMN_NAME_DELIMITER);
            final String columnName = columns[0];
            final String columnAttributes = columns[1].substring(0, columns[1].length() - 1);
            final String[] colAttributesToken = columnAttributes
                    .split(GlobalConstants.COLUMN_ATTRIBUTES_DELIMITER);
            if (colAttributesToken.length == 2
                    && colAttributesToken[1].equals(GlobalConstants.PRIMARY_KEY_CONSTRAINT)) {
                handlePrimaryKeyConstraintForERD(fileWriter, columnName, colAttributesToken);
            } else if (colAttributesToken.length == 4
                    && colAttributesToken[1].equals(GlobalConstants.FOREIGN_KEY_CONSTRAINT)) {
                handleForeignKeyConstraintForERD(fileWriter, tableNameString, cardinalities, columnName,
                        colAttributesToken);
            } else {
                handleNoConstraintForERD(fileWriter, columnName, colAttributesToken);
            }
            fileWriter.append(GlobalConstants.STRING_NEXT_LINE);
        }
    }

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Appends hypes on a string builder based on the tables Count
     */
    private void appendHyphens(final String tableNameString, StringBuilder stringBuilder) {
        for (int i = 0; i < tableNameString.length(); ++i) {
            stringBuilder.append(GlobalConstants.STRING_HYPHEN);
        }
    }

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Handles Cardinalities and helps in writing in the file
     */
    private void handleCardinalities(final FileWriter fileWriter, final List<String> cardinalities) throws IOException {
        for (final String cardinality : cardinalities) {
            fileWriter.append(cardinality);
            fileWriter.append(GlobalConstants.STRING_NEXT_LINE);
        }
    }

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Handles No Constraints common and normal ERD File writing
     */
    private void handleNoConstraintForERD(final FileWriter fileWriter, final String columnName,
            final String[] colAttributesToken)
            throws IOException {
        fileWriter.append(columnName).append(GlobalConstants.COLUMN_EMPTY_SPACE)
                .append(colAttributesToken[0]);
    }

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Helps in handling Foreign Key constraint for the ERD File
     *              Writing
     */
    private void handleForeignKeyConstraintForERD(final FileWriter fileWriter, final String tableNameString,
            final List<String> cardinalities,
            final String columnName, final String[] colAttributesToken) throws IOException {
        fileWriter.append(GlobalConstants.FOREIGN_KEY_CONSTRAINT)
                .append(GlobalConstants.COLUMN_EMPTY_SPACE)
                .append(GlobalConstants.STRING_PIPE_SEPARATOR)
                .append(GlobalConstants.COLUMN_EMPTY_SPACE)
                .append(columnName).append(GlobalConstants.COLUMN_EMPTY_SPACE)
                .append(colAttributesToken[0]);
        cardinalities.add(cardinalityStringGenerator(tableNameString, columnName, colAttributesToken[2],
                colAttributesToken[3]));
    }

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Helps in handling Primary Key constraint for the ERD File
     *              Writing
     */
    private void handlePrimaryKeyConstraintForERD(final FileWriter fileWriter, final String columnName,
            final String[] colAttributesToken)
            throws IOException {
        fileWriter.append(GlobalConstants.PRIMARY_KEY_CONSTRAINT)
                .append(GlobalConstants.COLUMN_EMPTY_SPACE)
                .append(GlobalConstants.STRING_PIPE_SEPARATOR)
                .append(GlobalConstants.COLUMN_EMPTY_SPACE)
                .append(columnName).append(GlobalConstants.COLUMN_EMPTY_SPACE)
                .append(colAttributesToken[0]);
    }

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Only accessible master function that will be called from other
     *              module to generate the ERD Diagram
     */
    @Override
    public boolean generateERD(final String databaseName)
            throws ERDGeneratorException {
        final boolean databaseNameValid = GlobalUtils.isDatabaseNameValid(databaseName);
        if (!databaseNameValid) {
            ERDUtils.handleERDError(GlobalConstants.STRING_NULL, databaseName,
                    ERDConstants.ERROR_MESSAGE_FAILED_TO_GENERATE_ERD,
                    ERDConstants.ERROR_CAUSE_MESSAGE_INVALID_DATABASE_NAME, eventLogger);
        }
        final String databasePath = GlobalConstants.DB_PATH + databaseName;
        if (!Files.exists(Paths.get(databasePath))) {
            ERDUtils.handleERDError(databasePath, databaseName, ERDConstants.ERROR_MESSAGE_FAILED_TO_GENERATE_ERD,
                    ERDConstants.ERROR_CAUSE_MESSAGE_INVALID_DATABASE_NAME, eventLogger);
        }
        if (RemoteUtils.isDistributed() && GlobalUtils.isDatabasePresentRemotely(databaseName)) {
            String[] args = {RemoteConstants.COMMAND_REMOTE, RemoteConstants.COMMAND_EXPORT_SQL, databaseName};
            RemoteUtils.executeInternalCommand(args);
            RemoteUtils.download(getERDGeneratorOutputFile(databaseName), ERDConstants.SQL_ERD_PATH + databaseName);
            return false;
        }
        final File[] allTables = GlobalUtils.readAllTables(databasePath);
        createERD(allTables, databaseName);
        generalLogger.logData(ERDConstants.SUCCESS_MESSAGE_ERD_GENERATION + databaseName);
        return true;
    }
}
