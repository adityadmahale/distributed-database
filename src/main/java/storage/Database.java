package storage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Database implements Serializable {

    public Map<String, DBTable > tables;

    public String databaseName = null;
    public static String space = AllDatabases.space+"\t";

    public Database(){
        tables = new HashMap<>();
    }

    public String getMyDatabase(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append( "{\n");
        stringBuilder.append(space+"\t"+ databaseName +" : [\n");

        boolean first = true;
        for(Map.Entry<String, DBTable> entry : tables.entrySet()){
            if(!first){
                stringBuilder.append(" , \n");
            }
            else{
                first = false;
            }
            stringBuilder.append(space + "\t\t" + entry.getKey() + " : " + entry.getValue().getCurrentDBTableData());
        }
        stringBuilder.append("\n");

        stringBuilder.append(space + "\t"+"]\n");
        stringBuilder.append(space+ "}");

        return stringBuilder.toString();
    }

    public static Database fetchMyDatabase(String databaseNameOriginal){
        Database database  = new Database();
        database.databaseName = databaseNameOriginal;
        String tableName = null;
        DBTable table = null;
        String keyword = CollectDataFromFile.getNextKeyword();

        boolean keywordReceived = false;
        while (true) {
            if(tableName != null && table != null){
                database.tables.put(tableName, table);
                tableName = null;
                table = null;
            }

            if (keyword == null) {
                return database;
            }

            if(keyword.equals("]")){
                return database;
            }
            if(keyword.equals("}")){
                keyword = CollectDataFromFile.getNextKeyword();
                continue;
            }

            if(!keywordReceived){
                keywordReceived = true;
                tableName = keyword;
            }
            else{
                keywordReceived = false;
                table = DBTable.getDBTableByKeyword(keyword);
            }

            keyword = CollectDataFromFile.getNextKeyword();
        }

    }


}
