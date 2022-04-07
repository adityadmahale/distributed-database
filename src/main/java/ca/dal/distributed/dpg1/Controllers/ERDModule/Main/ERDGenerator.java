package ca.dal.distributed.dpg1.Controllers.ERDModule.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.dal.distributed.dpg1.Controllers.ERDModule.Exceptions.ERDGeneratorException;
import ca.dal.distributed.dpg1.Controllers.ERDModule.Utils.Constants;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Enums.LoggerType;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.EventLogger;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.GeneralLogger;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.LoggerFactory;
import ca.dal.distributed.dpg1.Utils.GlobalConstants;

public class ERDGenerator {

    private final GeneralLogger generalLogger;
    private final EventLogger eventLogger;

    public ERDGenerator() {
        this.generalLogger = (GeneralLogger) new LoggerFactory().getLogger(LoggerType.GENERAL_LOGGER);
        this.eventLogger = (EventLogger) new LoggerFactory().getLogger(LoggerType.EVENT_LOGGER);
    }

    private String createCardinality(final String sourceTable, final String sourceColumn, final String destinationTable,
            final String destinationColumn) {
        return sourceTable + "." + sourceColumn + " -> " + destinationTable + "." + destinationColumn;
    }

    private String getERDGeneratorOutputFile(final String databaseName){
        return Constants.SQL_ERD_PATH + Constants.SQL_ERD_FILE_PREFIX + databaseName + GlobalConstants.STRING_UNDERSCORE + System.currentTimeMillis() + ".txt";
    }

    private void createSQLERD(final File[] allTables,
            final String databaseName) throws ERDGeneratorException {
        final String erdGeneratorOutputFile = getERDGeneratorOutputFile(databaseName);
        for (final File tableName : allTables) {
            try (final FileWriter fileWriter = new FileWriter(erdGeneratorOutputFile, true);
                
                final FileReader fileReader = new FileReader(tableName);
                final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                final String tableNameString = tableName.getName().split("\\.")[0];
                
                fileWriter.append(tableNameString + GlobalConstants.STRING_NEXT_LINE);

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < tableNameString.length(); ++i) {
                    stringBuilder.append("-");
                }

                fileWriter.append(stringBuilder.toString() + GlobalConstants.STRING_NEXT_LINE);
                
                final String tableColumnNames = bufferedReader.readLine();
                final String[] tableColumnNamesSplit = tableColumnNames.split(GlobalConstants.DELIMITER);
                final List<String> cardinalities = new ArrayList<>();
                for (final String column : tableColumnNamesSplit) {
                    final String[] rawTableHeadingColumnTokens = column.split("\\(");
                    final String colName = rawTableHeadingColumnTokens[0];
                    final String colAttributes = rawTableHeadingColumnTokens[1].substring(0,
                            rawTableHeadingColumnTokens[1].length() - 1);
                    final String[] colAttributesToken = colAttributes.split("\\|");
                    if (colAttributesToken.length == 2
                            && colAttributesToken[1].equals(GlobalConstants.PRIMARY_KEY_CONSTRAINT)) {
                        fileWriter.append(GlobalConstants.PRIMARY_KEY_CONSTRAINT).append(" ")
                                .append("|").append(" ")
                                .append(colName).append(" ")
                                .append(colAttributesToken[0]);
                    } else if (colAttributesToken.length == 4
                            && colAttributesToken[1].equals(GlobalConstants.FOREIGN_KEY_CONSTRAINT)) {
                        fileWriter.append(GlobalConstants.FOREIGN_KEY_CONSTRAINT).append(" ")
                                .append("|").append(" ")
                                .append(colName).append(" ")
                                .append(colAttributesToken[0]);
                        cardinalities.add(createCardinality(tableNameString, colName, colAttributesToken[2],
                                colAttributesToken[3]));
                    } else {
                        fileWriter.append(colName).append(" ")
                                .append(colAttributesToken[0]);
                    }
                    fileWriter.append("\n");
                }
                fileWriter.append("\n");
                for (final String cardinality : cardinalities) {
                    fileWriter.append(cardinality);
                    fileWriter.append("\n");
                }
                fileWriter.append("\n");
            } catch (final IOException e) {
                final String message = "Error: {" + e.getMessage() + "}!";
                eventLogger.logData(message);
                throw new ERDGeneratorException(message);
            }
        }
    }
}
