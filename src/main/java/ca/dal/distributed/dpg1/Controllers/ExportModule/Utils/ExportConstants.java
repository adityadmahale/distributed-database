package ca.dal.distributed.dpg1.Controllers.ExportModule.Utils;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class ExportConstants {
    // Cluster name
    private static final String CLUSTER_NAME = "db";
    // Name of the analytics file
    public static final Path CLUSTER_PATH = Paths.get(CLUSTER_NAME).toAbsolutePath();
    public static final Pattern columnPattern =
            Pattern.compile("^([a-zA-z0-9 ]+)\\(([a-zA-z ]+)\\)$");
}
