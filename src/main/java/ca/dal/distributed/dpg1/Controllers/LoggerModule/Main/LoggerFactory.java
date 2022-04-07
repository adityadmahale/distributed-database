package ca.dal.distributed.dpg1.Controllers.LoggerModule.Main;

import ca.dal.distributed.dpg1.Controllers.LoggerModule.Enums.LoggerType;

/**
 * @author Bharatwaaj Shankaranarayanan
 * @description Logger Factory decided which logger has to be fulfilled based on
 *              the Logger Type
 */
public class LoggerFactory {

   /**
    * @author Bharatwaaj Shankaranarayanan
    * @description Gets the respective logger based on the logType
    */
   public DistributedLogger getLogger(LoggerType logType) {
      switch (logType) {
         case EVENT_LOGGER -> {
            return new EventLogger();
         }
         case QUERY_LOGGER -> {
            return new QueryLogger();
         }
         case GENERAL_LOGGER -> {
            return new GeneralLogger();
         }
      }
      return null;
   }
}