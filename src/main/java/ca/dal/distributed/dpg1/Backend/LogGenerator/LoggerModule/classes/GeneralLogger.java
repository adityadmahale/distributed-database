package ca.dal.distributed.dpg1.Backend.LogGenerator.LoggerModule.classes;

import ca.dal.distributed.dpg1.Backend.LogGenerator.LoggerModule.enums.LoggerType;

/**
 * @author Bharatwaaj Shankaranarayanan
 * @description General Logger class to log Distributed Database General Activities
 */
public class GeneralLogger extends DistributedLogger {

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Logs Data using parent's storeQueryLog sends General Logger Log Type
     */
    @Override
    public void logData(String message) {
        storeQueryLog(message, LoggerType.GENERAL_LOGGER);
    }

}
