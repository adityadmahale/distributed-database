package ca.dal.distributed.dpg1.Controllers.ExportModule.Main;
import ca.dal.distributed.dpg1.Utils.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aditya Mahale
 * @description SQL dump
 */
public class ExportData {

    private final String databaseName;

    public ExportData(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * @author Aditya Mahale
     * @description Exports the dump file to the dumps directory
     */
    public void exportToFile() {
        if (!GlobalUtils.isDatabasePresent(databaseName)) {
            System.out.println("Database does not exist");
            return;
        }

        if (RemoteUtils.isDistributed() && GlobalUtils.isDatabasePresentRemotely(databaseName)) {
            String[] args = {RemoteConstants.COMMAND_REMOTE, RemoteConstants.COMMAND_EXPORT_SQL, databaseName};
            RemoteUtils.executeInternalCommand(args);
            RemoteUtils.download(RemoteConstants.DUMP_LOCATION + databaseName, RemoteConstants.DUMP_LOCATION + databaseName);
            return;
        }

        List<String> tableNames = GlobalUtils.getTableNames(databaseName);

        try (FileWriter writer = new FileWriter(RemoteConstants.DUMP_LOCATION + databaseName)) {
            // Loop for all the tables
            for (var tableName : tableNames) {
                // Read table data
                BufferedReader tableReader = new BufferedReader(
                        new FileReader(String.format("%s%s%s%s.txt", GlobalConstants.DB_PATH, databaseName, File.separator, tableName, GlobalConstants.EXTENSION_DOT_TXT)));

                List<String> columnTypes = writeStructure(writer, tableReader, tableName);
                writeRows(writer, tableReader, tableName, columnTypes);
                tableReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author Aditya Mahale
     * @description Exports structure
     */
    private List<String> writeStructure(FileWriter writer, BufferedReader tableReader, String tableName) throws IOException {
        List<String> columnTypes = new ArrayList<>();
        String tableStructure = tableReader.readLine();
        String[] columns = tableStructure.split(GlobalConstants.DELIMITER_ESCAPED);
        StringBuilder createTable = new StringBuilder();
        createTable.append("CREATE TABLE ").append(tableName).append("(");
        for (var column: columns) {
            String[] col = column.split("\\(");
            String columnName = col[0];
            String columnType = col[1].substring(0, col[1].length() - 1);
            columnTypes.add(columnType);
            createTable.append(columnName).append(" ").append(columnType).append(",");
        }

        createTable.setLength(createTable.length() - 1);
        createTable.append(");\n");
        writer.write(createTable.toString());
        return columnTypes;
    }

    /**
     * @author Aditya Mahale
     * @description Exports values
     */
    private void writeRows(FileWriter writer, BufferedReader tableReader, String tableName, List<String> columnTypes) throws IOException {
        String row;
        String[] columnValues;
        // Parse each row entry
        while ((row = tableReader.readLine()) != null) {
            StringBuilder rowBuilder = new StringBuilder();
            rowBuilder.append("INSERT INTO ").append(tableName).append(" VALUES(");
            columnValues = row.split(GlobalConstants.DELIMITER_ESCAPED);

            // Parse field values
            int index = 0;
            for (var columnValue : columnValues) {
                if (columnTypes.get(index).toLowerCase().equals("text")) {
                    rowBuilder.append("'").append(columnValue).append("'").append(",");
                } else {
                    rowBuilder.append(columnValue).append(",");
                }
                index++;
            }
            rowBuilder.setLength(rowBuilder.length() - 1);
            rowBuilder.append(");\n");
            writer.write(rowBuilder.toString());
        }
    }
}
