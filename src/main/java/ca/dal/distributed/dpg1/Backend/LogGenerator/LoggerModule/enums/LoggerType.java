package ca.dal.distributed.dpg1.Backend.LogGenerator.LoggerModule.enums;

import ca.dal.distributed.dpg1.Backend.LogGenerator.LoggerModule.constants.Constants;


/**
 * @author Bharatwaaj Shankaranarayanan
 * @description Logger Type enum for defining which type of logger is required
 */
public enum LoggerType {
    
    EVENT_LOGGER, QUERY_LOGGER, GENERAL_LOGGER;

    public String getLoggerFile(){
        switch (this) { 
            case EVENT_LOGGER: return Constants.EVENT_LOG_FILE; 
            case QUERY_LOGGER: return Constants.QUERY_LOG_FILE; 
            case GENERAL_LOGGER: return Constants.GENERAL_LOG_FILE;  
            default: throw new IllegalStateException(); 
        } 
    }
}
