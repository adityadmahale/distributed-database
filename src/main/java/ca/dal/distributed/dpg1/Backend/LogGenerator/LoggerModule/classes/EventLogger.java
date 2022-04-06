package ca.dal.distributed.dpg1.Backend.LogGenerator.LoggerModule.classes;

import ca.dal.distributed.dpg1.Backend.LogGenerator.LoggerModule.enums.LoggerType;

/**
 * @author Bharatwaaj Shankaranarayanan
 * @description Event Logger class to log Distributed Database Events
 */
public class EventLogger extends DistributedLogger {

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Logs Data using parent's storeQueryLog sends Event Logger Log Type
     */
    @Override
    public void logData(String message) {
        storeQueryLog(message, LoggerType.EVENT_LOGGER);
    }

}
