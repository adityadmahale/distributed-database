package ca.dal.distributed.dpg1.Backend.LogGenerator.LoggerModule.classes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;

import ca.dal.distributed.dpg1.Backend.LogGenerator.LoggerModule.constants.Constants;
import ca.dal.distributed.dpg1.Backend.LogGenerator.LoggerModule.enums.LoggerType;

/**
 * @author Bharatwaaj Shankaranarayanan
 * @description Abstract class for the distributed logging functionality
 */
public abstract class DistributedLogger {

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Writes single lined log based on Logger Type and writes the
     *              message
     */
    private void writeLog(final String message, final LoggerType loggerType) {
        try (final FileWriter queryLogFileWriter = new FileWriter(Constants.QUERY_LOG_FILE, true)) {
            queryLogFileWriter.append("Timestamp: " + String.valueOf(System.currentTimeMillis()) + " | ");
            queryLogFileWriter.append("LogType: " + loggerType.toString() + " | ");
            // TODO : Once the backend session is complete get the host from the backend
            // session
            queryLogFileWriter.append("Hostname: hostname" + " | ");
            // queryLogFileWriter.append(BackendSession.getLoggedInUser().getHostName());
            // TODO : Once the backend session is complete get the user from the backend
            // session
            queryLogFileWriter.append("Username: Bharatwaaj" + " | ");
            // queryLogFileWriter.append(BackendSession.getLoggedInUser().getUserName());
            queryLogFileWriter.append("Query: " + message + " | ");
            queryLogFileWriter.append(Instant.now().toString() + " | ");
            queryLogFileWriter.append("\n");
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description This Method helps creating a file for log depending on the
     *              logger type and writes log using write log method
     */
    protected void storeQueryLog(final String message, final LoggerType loggerType) {
        final File file = new File(loggerType.getLoggerFile());
        if (!file.exists()) {
            try {
                final boolean isNewFileCreated = file.createNewFile();
                if (isNewFileCreated) {
                    writeLog(message, loggerType);
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        writeLog(message, loggerType);
    }

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description This method acts as the main method to call storeQueryLog 
     *              and logic is defined depending on the child
     */
    public abstract void logData(final String message);

}
