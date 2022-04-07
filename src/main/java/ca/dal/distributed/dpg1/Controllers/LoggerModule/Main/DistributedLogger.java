package ca.dal.distributed.dpg1.Controllers.LoggerModule.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;

import ca.dal.distributed.dpg1.Controllers.LoggerModule.Utils.LoggerConstants;
import ca.dal.distributed.dpg1.Utils.GlobalConstants;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Enums.LoggerType;

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
        try (final FileWriter fileWriter = new FileWriter(LoggerConstants.LOG_FILE_QUERY, true)) {
            String hostname = "hostname"; // TODO: Once the backend session is complete get the host from the backend
            String username = "bharatwaaj"; // TODO: Once the backend session is complete get the user from the backend
            fileWriter.append(LoggerConstants.STRING_TIMESTAMP + GlobalConstants.STRING_COLON_SEPARATOR
                    + Instant.now() + GlobalConstants.STRING_PIPE_SEPARATOR);
            fileWriter.append(LoggerConstants.STRING_LOG_TYPE + GlobalConstants.STRING_COLON_SEPARATOR
                    + loggerType.toString() + GlobalConstants.STRING_PIPE_SEPARATOR);
            fileWriter.append(LoggerConstants.STRING_HOST_NAME + GlobalConstants.STRING_COLON_SEPARATOR + hostname
                    + GlobalConstants.STRING_PIPE_SEPARATOR);
            fileWriter.append(LoggerConstants.STRING_USER_NAME + GlobalConstants.STRING_COLON_SEPARATOR + username
                    + GlobalConstants.STRING_PIPE_SEPARATOR);
            fileWriter.append(LoggerConstants.STRING_MESSAGE + GlobalConstants.STRING_COLON_SEPARATOR + message
                    + message + GlobalConstants.STRING_PIPE_SEPARATOR);
            fileWriter.append(GlobalConstants.STRING_NEXT_LINE);
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
