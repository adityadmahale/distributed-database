package ca.dal.distributed.dpg1.Backend.LogGenerator.LoggerModule.classes;

import ca.dal.distributed.dpg1.Backend.LogGenerator.LoggerModule.enums.LoggerType;

/**
 * @author Bharatwaaj Shankaranarayanan
 * @description Query Logger class to log Distributed Database Queries
 */
public class QueryLogger extends DistributedLogger {

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Logs Data using parent's storeQueryLog sends Query Logger Log Type
     */
    @Override
    public void logData(String message) {
        storeQueryLog(message, LoggerType.QUERY_LOGGER);
    }

}
