package ca.dal.distributed.dpg1.Controllers.AnalyticsModule.Utils;
import java.io.File;

/**
 * @author Aditya Mahale
 * @description Analytics Utilities that are common to perform across Analytics Module
 */
public class AnalyticsUtils {

    /**
     * @author Aditya Mahale
     * @description Gets analytics metadata file path for a given database
     */
    public static String getMetaFilePath(String databaseName) {
        return String.format("%s%s%s%s%s", AnalyticsConstants.CLUSTER_PATH, File.separator, databaseName,
                File.separator, AnalyticsConstants.ANALYTICS_FILE_NAME);
    }
}
