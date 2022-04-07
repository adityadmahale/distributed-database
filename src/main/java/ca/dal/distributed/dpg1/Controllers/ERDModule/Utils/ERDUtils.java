package ca.dal.distributed.dpg1.Controllers.ERDModule.Utils;

import ca.dal.distributed.dpg1.Controllers.ERDModule.Exceptions.ERDGeneratorException;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.EventLogger;

/**
 * @author Bharatwaaj Shankaranarayanan
 * @description ERD Utilities that are common to perform across ERD Module
 * @implSpec Please create only static functions here
 */
public final class ERDUtils {

    public static void handleERDError(final String databasePath, final String databaseName, final String message,
            final String cause, final EventLogger eventLogger) throws ERDGeneratorException {
        String finalMessage = "";
        if (databasePath == null) {
            finalMessage = message + databaseName + cause;
        } else {
            finalMessage = message + databasePath + databaseName + cause;
        }
        eventLogger.logData(finalMessage);
        throw new ERDGeneratorException(finalMessage);
    }
}
