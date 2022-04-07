package ca.dal.distributed.dpg1.Controllers.LoggerModule.Enums;

import ca.dal.distributed.dpg1.Controllers.LoggerModule.Utils.LoggerConstants;

/**
 * @author Bharatwaaj Shankaranarayanan
 * @description Logger Type enum for defining which type of logger is required
 */
public enum LoggerType {

    EVENT_LOGGER, QUERY_LOGGER, GENERAL_LOGGER;

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Gets Appropriate Logger based on the input provided by the
     *              Module
     */
    public String getLoggerFile() {
        switch (this) {
            case EVENT_LOGGER:
                return LoggerConstants.LOG_FILE_EVENT;
            case QUERY_LOGGER:
                return LoggerConstants.LOG_FILE_QUERY;
            case GENERAL_LOGGER:
                return LoggerConstants.LOG_FILE_GENERAL;
            default:
                throw new IllegalStateException();
        }
    }
}
