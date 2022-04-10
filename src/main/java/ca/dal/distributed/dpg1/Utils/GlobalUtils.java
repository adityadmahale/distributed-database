package ca.dal.distributed.dpg1.Utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
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
    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Reads all tables from the specified database path
     */
    public static File[] readAllTables(final String databasePath) {
        final File allTableDirs = new File(databasePath);
        List<File> results = new ArrayList();
        String[] tableNameDirs = allTableDirs.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        for(String tableName : tableNameDirs) {
            final File tableNameFile = new File(databasePath + tableName);
            File[] tableFiles = tableNameFile.listFiles();
            for(File tableFile: tableFiles){
                if(!tableFile.toString().contains(GlobalConstants.EXTENSION_METADATA_FILE)){
                    results.add(tableFile);
                }
            }
        }
        System.out.println(results.toString());
        return results.toArray(new File[0]);
    }

    public static boolean deleteExistingDatabase(final File abstractDirPath){

        File[] cacheDbTables = abstractDirPath.listFiles();
        for(File currentFile: cacheDbTables){
            currentFile.delete();
        }
        abstractDirPath.delete();

        return true;
    }
}
