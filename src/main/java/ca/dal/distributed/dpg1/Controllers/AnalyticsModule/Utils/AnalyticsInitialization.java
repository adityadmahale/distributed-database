package ca.dal.distributed.dpg1.Controllers.AnalyticsModule.Utils;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author Aditya Mahale
 * @description Analytics Utilities for initializing user operations data
 */
public class AnalyticsInitialization {

    /**
     * @author Aditya Mahale
     * @description Initializes analytics metadata file with all operations set to zero
     */
    // TODO: When a DB gets created
    public static void initializeMetaFile(String databaseName) {

        // Write the metadata file to the corresponding database directory
        // Initialize operations counts to zero
        try(FileWriter writer = new FileWriter(AnalyticsUtils.getMetaFilePath(databaseName))) {
            writer.write("update"+ AnalyticsConstants.OPERATION_SEPARATOR +
                            "0\ninsert" + AnalyticsConstants.OPERATION_SEPARATOR +
                            "0\ndelete" + AnalyticsConstants.OPERATION_SEPARATOR + "0\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author Aditya Mahale
     * @description Initializes analytics metadata file with all user data set to zero
     */
    // TODO: When a DB gets created
    public static void InitializeAllUserMetadata(List<String> users, String databaseName) {

        // Initialize metadata for all the users in the analytics.meta file when a DB is created.
        try (FileWriter writer = new FileWriter(AnalyticsUtils.getMetaFilePath(databaseName), true)) {
            for (var user : users) {
                writer.write(user + AnalyticsConstants.USER_SEPARATOR + 0 + "\n");
            }
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * @author Aditya Mahale
     * @description Initializes analytics metadata file when a user registers for the first time
     */
    // TODO: When a user registers for the first time
    public static void initializeUserMetadata(String user, List<String> databaseNames) {

        // Initialize metadata in all the database directories when a user registers for the first time
        for (var databaseName: databaseNames) {
            try (FileWriter writer = new FileWriter(AnalyticsUtils.getMetaFilePath(databaseName), true)) {
                writer.write(user + AnalyticsConstants.USER_SEPARATOR + 0 + "\n");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
