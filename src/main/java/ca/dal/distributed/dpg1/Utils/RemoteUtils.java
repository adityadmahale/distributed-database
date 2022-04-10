package ca.dal.distributed.dpg1.Utils;

import ca.dal.distributed.dpg1.Controllers.AnalyticsModule.Utils.AnalyticsUpdate;
import ca.dal.distributed.dpg1.Controllers.ExportModule.Main.ExportData;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main.QueryExecutor;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main.QueryManager;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.MetaDataHandler;
import com.jcraft.jsch.*;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;


/**
 * @author Aditya Mahale
 * @description File for executing distributed commands
 */
public class RemoteUtils {

    /**
     * @author Aditya Mahale
     * @description Downloads file from a remote machine
     */
    public static void download(String source, String destination) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;
        try {
            session = jsch.getSession(RemoteConstants.HOST_USERNAME, getRemoteHostIp(), RemoteConstants.HOST_SSH_PORT);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(getRemoteHostPassword());
            session.connect();
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            channelSftp.get(source, destination);
            channelSftp.rm(source);
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
        }
    }

    /**
     * @author Aditya Mahale
     * @description Helper method for executing command on the remote machine
     */
    private static void execute(String command) throws Exception {
        Session session = null;
        ChannelExec channel = null;

        try {
            session = new JSch().getSession(RemoteConstants.HOST_USERNAME, getRemoteHostIp(), RemoteConstants.HOST_SSH_PORT);
            session.setPassword(getRemoteHostPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOutputStream(responseStream);
            channel.connect();

            while (channel.isConnected()) {
                Thread.sleep(100);
            }

            String responseString = new String(responseStream.toByteArray());
            System.out.println(responseString);
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    /**
     * @author Aditya Mahale
     * @description Get remote host password
     */
    private static String getRemoteHostPassword() {
        String password = System.getenv(RemoteConstants.HOST_ENV_PASSWORD);
        if (password == null) {
            throw new IllegalStateException(RemoteConstants.ERROR_ENV_VARIABLE + RemoteConstants.HOST_ENV_PASSWORD);
        }
        return password;
    }

    /**
     * @author Aditya Mahale
     * @description Get remote host IP
     */
    public static String getRemoteHostIp() {
        String ip = System.getenv(RemoteConstants.HOST_ENV_IP);
        if (ip == null) {
            throw new IllegalStateException(RemoteConstants.ERROR_ENV_VARIABLE + RemoteConstants.HOST_ENV_IP);
        }
        return ip;
    }

    /**
     * @author Aditya Mahale
     * @description Checks if the database is distributed
     */
    public static boolean isDistributed() {
        String isDistributed = System.getenv(RemoteConstants.HOST_ENV_IS_DISTRIBUTED);
        return isDistributed != null && isDistributed.equals(RemoteConstants.IS_DISTRIBUTED_VALUE);
    }

    /**
     * @author Aditya Mahale
     * @description Checks if the command is an internal command
     */
    public static boolean isInternalCommand(String[] args) {
        return isDistributed() && args.length > 0;
    }

    public static void executeInternalCommand(String[] args) {
        try {
            // Executes remotely
            if (args[0].equals(RemoteConstants.COMMAND_REMOTE)) {
                String command = RemoteConstants.REMOTE_COMMAND_PREFIX + String.join(" ", Arrays.copyOfRange(args,1, args.length));
                execute(command);
            }

            // Executes locally
            String type = args[0];
            String databaseName;
            switch (type) {
                case RemoteConstants.COMMAND_INCREMENT_USER_COUNT:
                    databaseName = args[1];
                    String userName = args[2];
                    AnalyticsUpdate.incrementUserCountData(databaseName, userName);
                    break;
                case RemoteConstants.COMMAND_INCREMENT_OPERATION_COUNT:
                    databaseName = args[1];
                    String operation = args[2];
                    AnalyticsUpdate.incrementOperationCountData(databaseName, operation);
                    break;
                case RemoteConstants.COMMAND_EXECUTE_QUERY:
                    QueryExecutor executor = new QueryExecutor();
                    databaseName = args[1];
                    String query = args[2];
                    GlobalUtils.useDatabase(databaseName);
                    executor.processInputQuery(query);
                case RemoteConstants.COMMAND_UPDATE_GLOBAL_METADATA:
                    databaseName = args[1];
                    String ip = args[2];
                    GlobalUtils.writeToGlobalMetaData(databaseName, ip);
                case RemoteConstants.DELETE_FROM_GLOBAL_METADATA:
                    databaseName = args[1];
                    MetaDataHandler.deleteFromGlobalMetaData(databaseName);
                case RemoteConstants.COMMAND_EXPORT_SQL:
                    databaseName = args[1];
                    ExportData export = new ExportData(databaseName);
                    export.exportToFile();
                    break;
            }

        } catch (Exception e) {
            throw new IllegalStateException(RemoteConstants.ERROR_INTERNAL_COMMAND);
        }
    }
}
