package ca.dal.distributed.dpg1.Controllers.AnalyticsModule.Main;
import ca.dal.distributed.dpg1.Controllers.AnalyticsModule.Utils.AnalyticsConstants;
import ca.dal.distributed.dpg1.Controllers.AnalyticsModule.Utils.AnalyticsUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;

/**
 * @author Aditya Mahale
 * @description Processor file for parsing analytics query
 */
public class AnalyticsProcessor {

    /**
     * @author Aditya Mahale
     * @description Parses analytics queries
     */
    public void parseQuery() {
        Matcher match;
        Scanner reader = new Scanner(System.in);
        String input = "";

        // Loop till the user enter the command.
        while (!input.equals(AnalyticsConstants.EXIT_COMMAND)) {

            // Prompt the user
            System.out.print("(analytics) >> ");
            input = reader.nextLine();
            match = AnalyticsConstants.queryPattern.matcher(input);

            // If the match is not found, then output invalid syntax on the console
            if (!match.find()) {
                System.out.println("invalid syntax");
                continue;
            }

            // Handle valid queries
            if(match.group(1) != null && match.group(2) != null) {
                String operation = match.group(1);
                String databaseName = match.group(2);
                // TODO: Get the isDBPresent method from the common class
//                if(!Utility.isDBPresent(databaseName)) {
//                    System.out.println("Database " + databaseName + " does not exist");
//                    continue;
//                }
                processOperationQuery(operation, databaseName);
            } else if (!input.equals(AnalyticsConstants.EXIT_COMMAND)) {
                // TODO: Get datbaseNames
                List<String> databaseNames = new ArrayList<>();
                databaseNames.add("db1");
                databaseNames.add("db2");
                processUserQuery(databaseNames);
            }
        }
        reader.close();
    }

    /**
     * @author Aditya Mahale
     * @description Processes user related queries
     */
    private void processUserQuery(List<String> databaseNames) {

        // Iterate over all the files to find the count
        for (var databaseName: databaseNames) {
            try (BufferedReader metaReader = new BufferedReader(new FileReader(AnalyticsUtils.getMetaFilePath(databaseName)))) {
                String row;
                String user;
                String[] information;
                int count;

                // Check each line in the analytics file
                while ((row = metaReader.readLine()) != null) {

                    // If the row represents metadata other that of the user, then skip it
                    if (!row.contains(AnalyticsConstants.USER_SEPARATOR)) {
                        continue;
                    }

                    // Get the user and the current count
                    information = row.split(AnalyticsConstants.USER_SEPARATOR);
                    user = information[0];
                    count = Integer.parseInt(information[1]);
                    // TODO: Update virtual machine
                    System.out.printf(
                            "user %s submitted %s queries for %s running on Virtual Machine 1%n",
                            user,
                            count,
                            databaseName);
                }
            } catch (IOException e) {
                throw new IllegalStateException(AnalyticsConstants.ERROR_MESSAGE_PARSING);
            }
        }
    }

    /**
     * @author Aditya Mahale
     * @description Processes operation related queries
     */
    private void processOperationQuery(String operation, String databaseName) {

        // Read the meta file for the specific database
        try (BufferedReader metaReader = new BufferedReader(
                new FileReader(AnalyticsUtils.getMetaFilePath(databaseName)))) {
            String row;
            String metaOperation;
            String[] information;
            int count;
            while ((row = metaReader.readLine()) != null) {

                // If the row represents metadata other that of the operation, then skip it
                if (!row.contains(AnalyticsConstants.OPERATION_SEPARATOR)) {
                    continue;
                }

                // Get the operation and the current count
                information = row.split(AnalyticsConstants.OPERATION_SEPARATOR);
                metaOperation = information[0];
                count = Integer.parseInt(information[1]);

                // Print the output on the console
                if (metaOperation.equals(operation)) {
                    System.out.printf(
                            "Total %s %s operations are performed on %s%n",
                            count,
                            metaOperation.substring(0, 1).toUpperCase() + metaOperation.substring(1),
                            databaseName);
                    break;
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}
