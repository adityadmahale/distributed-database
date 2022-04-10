package ca.dal.distributed.dpg1.Utils;

/**
 * @author Aditya Mahale
 * @description Remote constants
 */
public class RemoteConstants {
    public static final String HOST_ENV_IP = "REMOTE_HOST_IP";
    public static final String HOST_ENV_PASSWORD = "REMOTE_HOST_PASSWORD";
    public static final String HOST_USERNAME = "ubuntu";
    public static final String HOST_ENV_IS_DISTRIBUTED = "IS_DISTRIBUTED";
    public static final String IS_DISTRIBUTED_VALUE = "true";
    public static final int HOST_SSH_PORT = 22;

    public static final String REMOTE_COMMAND_PREFIX = "/opt/jdk-17/bin/java -jar DistributedDatabase.jar ";
    public static final String ERROR_ENV_VARIABLE = "Environment variable not set: ";
    public static final String ERROR_INTERNAL_COMMAND = "Error while executing internal command";

    public static final String COMMAND_REMOTE = "remote";
    public static final String COMMAND_INCREMENT_USER_COUNT = "incrementUserCountData";
    public static final String COMMAND_INCREMENT_OPERATION_COUNT = "incrementOperationCountData";
    public static final String COMMAND_EXECUTE_QUERY = "executeQuery";
    public static final String COMMAND_UPDATE_GLOBAL_METADATA = "updateGlobal";
    public static final String DELETE_FROM_GLOBAL_METADATA = "deleteGlobal";
    public static final String COMMAND_EXPORT_SQL = "exportSQL";
    public static final String DUMP_LOCATION = "outputs/dumps/";
}
