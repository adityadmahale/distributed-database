package ca.dal.distributed.dpg1.Backend.LogGenerator.Controller;

public class LoggerFactory {

   public Logger getLogger(String logType){
      if(shapeType == null){
         return null;
      }
      switch(logType)

      case
         return new EventLogger();
         
      } else if(shapeType.equalsIgnoreCase("EVENT")){
         return new QueryLogger();
         
      } else if(shapeType.equalsIgnoreCase("GENERAL")){
         return new GeneralLogger();
      }
      
      return null;
   }
}