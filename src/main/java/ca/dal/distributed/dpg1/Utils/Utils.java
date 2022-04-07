package ca.dal.distributed.dpg1.Utils;

import java.io.File;
import java.util.regex.Pattern;

/**
 * @author Bharatwaaj Shankaranarayanan
 * @description Common utilities class that can be used across the application
 * @implSpec Please maintain all the functions in this class to be static
 */
public final class Utils {

    private Utils() {
        // Required private constructor. Cannot be instantiated.
    }

    public static boolean isDatabaseNameValid(final String databaseName) {
        boolean isDatabaseNameValid;
        if (databaseName == null || databaseName.isEmpty()) {
            return false;
        } else {
            isDatabaseNameValid = Pattern.matches("[A-Za-z\\d]+", databaseName);
        }
        return isDatabaseNameValid;
    }

    public static File[] readAllTables(final String databasePath) {
        final File allTables = new File(databasePath);
        return allTables.listFiles();
    }
}
