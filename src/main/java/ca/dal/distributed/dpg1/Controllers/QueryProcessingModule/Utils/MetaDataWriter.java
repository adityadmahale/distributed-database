package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils;

import ca.dal.distributed.dpg1.Utils.GlobalConstants;
import ca.dal.distributed.dpg1.Utils.LocalUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Ankush Mudgal
 * The type Meta data writer.
 */
public class MetaDataWriter {


    /**
     * Write to db local meta data boolean.
     *
     * @param tableName the table name
     * @param tablePath the table path
     * @return the boolean
     * @throws IOException the io exception
     */
    public static boolean writeToDBLocalMetaData(String tableName, String tablePath) {

        try{

            FileWriter fileWriter = new FileWriter(new File(tablePath + "local_metadata" + GlobalConstants.EXTENSION_DOT_TXT), true);
            fileWriter.append(tableName).append(GlobalConstants.STRING_NEXT_LINE);
            fileWriter.close();
            return true;

        } catch (IOException exception){
            exception.getMessage();
        }


        return false;
    }

    public static boolean writeToGlobalMetaData(String databaseName){

        try{

            FileWriter fileWriter = new FileWriter(new File(GlobalConstants.DB_PATH + "global_metadata" + GlobalConstants.EXTENSION_DOT_TXT), true);
            String stringBuilder = databaseName + GlobalConstants.STRING_AT_THE_RATE + LocalUtils.getLocalHostIp() + GlobalConstants.STRING_NEXT_LINE;
            fileWriter.append(stringBuilder);
            fileWriter.close();

            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
