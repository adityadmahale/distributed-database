package ca.dal.distributed.dpg1.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @author Bharatwaaj Shankaranarayanan
 * @description Common utilities class that can be used across the application
 * @implSpec Please maintain all the functions in this class to be static
 */
public final class GlobalUtils {

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Checks if the database name is in valid format/pattern
     */
    public static boolean isDatabaseNameValid(final String databaseName) {
        boolean isDatabaseNameValid;
        if (databaseName == null || databaseName.isEmpty()) {
            return false;
        } else {
            isDatabaseNameValid = Pattern.matches(GlobalConstants.PATTERN_DATABASE_NAME, databaseName);
        }
        return isDatabaseNameValid;
    }

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Reads all tables from the specified database path
     */
    public static File[] readAllTables(final String databasePath) {
        final File allTables = new File(databasePath);
        return allTables.listFiles();
    }

    public static boolean deleteExistingDatabase(final File abstractDirPath){

        File[] cacheDbTables = abstractDirPath.listFiles();
        for(File currentFile: cacheDbTables){
            currentFile.delete();
        }
        abstractDirPath.delete();

        return true;
    }

    public static boolean writeToGlobalMetaData(String databaseName, String ip) {

        try {

            FileWriter fileWriter = new FileWriter(new File(GlobalConstants.DB_PATH + "global_metadata" + GlobalConstants.EXTENSION_DOT_TXT), true);
            String stringBuilder = databaseName + GlobalConstants.STRING_AT_THE_RATE + ip + GlobalConstants.STRING_NEXT_LINE;
            fileWriter.append(stringBuilder);
            fileWriter.close();

            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
