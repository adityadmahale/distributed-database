package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils;

import ca.dal.distributed.dpg1.Utils.GlobalConstants;
import ca.dal.distributed.dpg1.Utils.LocalUtils;
import ca.dal.distributed.dpg1.Utils.RemoteConstants;
import ca.dal.distributed.dpg1.Utils.RemoteUtils;

import java.io.*;
import java.util.*;

/**
 * Metadata Handler - Writes into and Deletes from Global and Local Metadata files.
 *
 * @author Ankush Mudgal
 */
public class MetaDataHandler {


    /**
     * @author Ankush Mudgal
     * Write to db local meta data.
     *
     * @param tableName the table name
     * @param tablePath the table path
     * @return the boolean
     * @throws IOException the io exception
     */
    public static boolean writeToDBLocalMetaData(String tableName, String tablePath) {

        try {

            FileWriter fileWriter = new FileWriter(new File(tablePath + "local_metadata" + GlobalConstants.EXTENSION_DOT_TXT), true);
            fileWriter.append(tableName).append(GlobalConstants.STRING_NEXT_LINE);
            fileWriter.close();
            return true;

        } catch (IOException exception) {
            exception.printStackTrace();
        }


        return false;
    }

    /**
     * @author Ankush Mudgal
     * Delete from db local meta data.
     *
     * @param tableName    the table name
     * @param databasePath the database path
     * @return the boolean
     */
    public static boolean deleteFromDBLocalMetaData(String tableName, String databasePath){

        String dbLocalMetaDataPath = databasePath + DatabaseConstants.FORWARD_SLASH + "local_metadata" + GlobalConstants.EXTENSION_DOT_TXT;
        List<String> tablesList = new ArrayList<>();

        try {
            BufferedReader bufferedReader = new BufferedReader( new FileReader(dbLocalMetaDataPath));
            String line;

            while ((line = bufferedReader.readLine()) != null){

                tablesList.add(line.trim());
            }

            tablesList.remove(tableName);

            FileWriter fileWriter = new FileWriter(new File(dbLocalMetaDataPath));

            tablesList.forEach(table -> {
                try {
                    fileWriter.append(table).append(GlobalConstants.STRING_NEXT_LINE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            fileWriter.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }


        return false;
    }

    /**
     * @author Ankush Mudgal
     * Write to global meta data.
     *
     * @param databaseName the database name
     * @return the boolean
     */
    public static boolean writeToGlobalMetaData(String databaseName) {

        try {

            FileWriter fileWriter = new FileWriter(new File(GlobalConstants.DB_PATH + "global_metadata" + GlobalConstants.EXTENSION_DOT_TXT), true);
            String stringBuilder = databaseName + GlobalConstants.STRING_AT_THE_RATE + LocalUtils.getLocalHostIp() + GlobalConstants.STRING_NEXT_LINE;
            fileWriter.append(stringBuilder);
            fileWriter.close();

            if (RemoteUtils.isDistributed()) {
                String[] args = {"remote", RemoteConstants.COMMAND_UPDATE_GLOBAL_METADATA, databaseName, LocalUtils.getLocalHostIp()};
                RemoteUtils.executeInternalCommand(args);
            }

            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @author Ankush Mudgal
     * Delete from global meta data.
     *
     * @param databaseName the database name
     * @return the boolean
     */
    public static boolean deleteFromGlobalMetaData(String databaseName) {

        try {

            String globalMDPath = GlobalConstants.DB_PATH + "global_metadata" + GlobalConstants.EXTENSION_DOT_TXT;
            Map<String, String> databaseHostnameMap = new TreeMap<>();

            BufferedReader bufferedReader = new BufferedReader(new FileReader(globalMDPath));
            String line;

            while ((line = bufferedReader.readLine()) != null){

                String[] selectDatabaseName = line.split(GlobalConstants.STRING_AT_THE_RATE);
                databaseHostnameMap.put(selectDatabaseName[0], selectDatabaseName[1]);
            }
            bufferedReader.close();

            if(databaseHostnameMap.containsKey(databaseName)){
                databaseHostnameMap.remove(databaseName);
            }

            FileWriter fileWriter = new FileWriter(new File(globalMDPath));
            databaseHostnameMap.forEach( (k,v) -> {

                try {
                    fileWriter.append(k).append(GlobalConstants.STRING_AT_THE_RATE).append(v).append(GlobalConstants.STRING_NEXT_LINE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            fileWriter.close();


            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;

    }

}
