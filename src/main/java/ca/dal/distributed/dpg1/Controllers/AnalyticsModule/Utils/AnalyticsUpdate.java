package ca.dal.distributed.dpg1.Controllers.AnalyticsModule.Utils;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aditya Mahale
 * @description Analytics Utilities for storing user operations
 */
public class AnalyticsUpdate {

    /**
     * @author Aditya Mahale
     * @description Increments user query count data
     */
    public static void incrementUserCountData(String databaseName, String user) {
        updateData(databaseName, user, "user");
    }

    /**
     * @author Aditya Mahale
     * @description Increments database operation count data
     */
    public static void incrementOperationCountData(String databaseName, String operation) {
        updateData(databaseName, operation, "operation");
    }

    /**
     * @author Aditya Mahale
     * @description A common method for updating the metadata file
     */
    private static void updateData(String databaseName, String operation, String type) {
        String separator;

        // Check the type of update
        if (type.equals("operation")) {
            separator = AnalyticsConstants.OPERATION_SEPARATOR;
        } else {
            separator = AnalyticsConstants.USER_SEPARATOR;
        }

        String metaFilePath = AnalyticsUtils.getMetaFilePath(databaseName);
        File file = new File(metaFilePath);
        List<String> updateList = new ArrayList<>();

        // Read individual lines and increment the count for the specific type
        try (BufferedReader metaReader = new BufferedReader(new FileReader(metaFilePath))) {
            String row;
            String metaOperation;
            int metaCount;
            String[] information;
            while ((row = metaReader.readLine()) != null) {
                if(row.contains(operation) && row.contains(separator)){
                    information = row.split(separator);
                    metaOperation = information[0];
                    metaCount = Integer.parseInt(information[1]) + 1;
                    updateList.add(metaOperation + separator + metaCount);
                } else {
                    updateList.add(row);
                }
            }
            Files.write(file.toPath(), updateList);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
