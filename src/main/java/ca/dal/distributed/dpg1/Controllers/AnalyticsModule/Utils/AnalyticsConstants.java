package ca.dal.distributed.dpg1.Controllers.AnalyticsModule.Utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * @author Aditya Mahale
 * @description Constants file for the Analytics Module
 */
public final class AnalyticsConstants {
    // Separators
    public static final String OPERATION_SEPARATOR = "@";
    public static final String USER_SEPARATOR = ":";
    // Exit command
    public static final String EXIT_COMMAND = "exit;";
    // Pattern for matching commands
    public static final Pattern queryPattern =
            Pattern.compile("^count *queries;$|^exit;$|^count *(insert|update|delete) *([a-zA-Z0-9]+);$");
    // Cluster name
    private static final String CLUSTER_NAME = "db";

    // Name of the analytics file
    public static final String ANALYTICS_FILE_NAME = "analytics.meta";
    public static final Path CLUSTER_PATH = Paths.get(CLUSTER_NAME).toAbsolutePath();

    // Error messages
    public static final String ERROR_MESSAGE_PARSING = "Error: Failed to parse user query";
}
