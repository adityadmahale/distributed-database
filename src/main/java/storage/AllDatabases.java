package storage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AllDatabases implements Serializable {

    public Map<String, Database> databaseMap = null;
    private static AllDatabases instance = null;
    public static String space = "\t";

    private AllDatabases(){
        databaseMap = new HashMap<>();
    }

    //making class a singleton
    public static AllDatabases getInstance() {
        if (instance == null) {
            instance = new AllDatabases();
        }

        return instance;
    }

    public static void setInstance(AllDatabases allDatabases){
        instance = allDatabases;
    }

    public String getAllMyDatabases(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n");
        stringBuilder.append(space+"[\n");

        boolean first = true;
        for(Map.Entry<String, Database> entry : databaseMap.entrySet()){
            if(!first){
                stringBuilder.append(" , \n");
            }
            else{
                first = false;
            }
            stringBuilder.append(space + "\t" + entry.getKey() + " : " + entry.getValue().getMyDatabase());
        }
        stringBuilder.append("\n");

        stringBuilder.append(space +"]\n");
        stringBuilder.append("}\n");

        return stringBuilder.toString();
    }

    public static void loadAllMydatabases(){
        boolean keywordReceived = false;
        String databaseName = null;
        Database database = null;
        String keyword = CollectDataFromFile.getNextKeyword();
        while (true) {
            if(databaseName != null && database != null){
                AllDatabases.getInstance().databaseMap.put(databaseName, database);
                database = null;
                databaseName = null;
            }
            if (keyword == null || CollectDataFromFile.tokenList == null || CollectDataFromFile.tokenList.size() <= 0) {
                return;
            }

            if(keyword.equals("]")){
                continue;
            }
            if(keyword.equals("}")){
                keyword = CollectDataFromFile.getNextKeyword();
                continue;
            }


            if(!keywordReceived){
                keywordReceived = true;
                databaseName = keyword;
            }
            else{
                keywordReceived = false;
                database = Database.fetchMyDatabase(keyword);
            }
            keyword = CollectDataFromFile.getNextKeyword();
        }
    }
}
