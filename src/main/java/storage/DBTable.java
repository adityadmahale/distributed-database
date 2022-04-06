package storage;
import java.io.Serializable;
import java.util.*;

public class DBTable implements Serializable {

        public  Map<String, String> colNamesAndDataTypes;
        public  List<TableRow> DBTableRows;
        public  String DBTable_NAME = null;
        public  String DBTable_PRIMARY_KEY = null;
        public  String DBTable_FOREIGN_KEY = null;


    public DBTable() {
        //Constructor
    }

    public DBTable(Map<String, String> colNamesAndDataTypes, List<TableRow> DBTableRows) {

        this.colNamesAndDataTypes = colNamesAndDataTypes;
        this.DBTableRows = DBTableRows;
    }

    public String getCurrentDBTableData(){

            StringBuilder stringBuilder = new StringBuilder()
                    .append("{\n").append(CONSTANTS.QUADRUPLE_TAB).append(CONSTANTS.SINGLE_TAB).append("PrimaryKey").append(" : ").append(DBTable_PRIMARY_KEY).append("\n")
                    .append(CONSTANTS.QUADRUPLE_TAB).append(CONSTANTS.SINGLE_TAB).append("ForeignKey").append(" : ").append(DBTable_FOREIGN_KEY).append("\n")
                    .append(CONSTANTS.QUADRUPLE_TAB).append("\tColumnStructure").append(" : [\n");

            boolean firstRow = true;

            for(Map.Entry<String, String> entry : colNamesAndDataTypes.entrySet()){

                if(!firstRow){
                    stringBuilder.append(" , \n");
                }
                else{
                    firstRow = false;
                }

                stringBuilder
                        .append(CONSTANTS.QUADRUPLE_TAB).append(CONSTANTS.DOUBLE_TAB)
                        .append(entry.getKey()).append(" : '")
                        .append(entry.getValue()).append("'");
            }

            stringBuilder
                    .append("\n")
                    .append(CONSTANTS.QUADRUPLE_TAB).append(CONSTANTS.DOUBLE_TAB).append("]\n")
                    .append(CONSTANTS.QUADRUPLE_TAB).append(CONSTANTS.SINGLE_TAB).append(DBTable_NAME).append(" : [\n");

            firstRow = true;

            for(TableRow row : DBTableRows){

                if(!firstRow){
                    stringBuilder.append(" , \n");
                }
                else{
                    firstRow = false;
                }
                stringBuilder.append(CONSTANTS.QUADRUPLE_TAB).append(CONSTANTS.SINGLE_TAB).append(row.getCurrentRowData());
            }

            stringBuilder
                    .append("\n")
                    .append(CONSTANTS.QUADRUPLE_TAB).append(CONSTANTS.DOUBLE_TAB).append("]\n")
                    .append(CONSTANTS.QUADRUPLE_TAB).append("}");

            return stringBuilder.toString();
        }

    public static DBTable getDBTableByKeyword(String keyword){

        DBTable DBTable = new DBTable();
        boolean keywordReceived = false;

        while (true) {
            if (keyword == null) {
                return DBTable;
            }
            if(keyword.equals("]")){
                return DBTable;
            }
            if(keyword.equals("}")){
                keyword = CollectDataFromFile.getNextKeyword();
                continue;
            }
            if (keyword.equalsIgnoreCase("PrimaryKey")) {
                DBTable.DBTable_PRIMARY_KEY = CollectDataFromFile.getNextKeyword();

                assert DBTable.DBTable_PRIMARY_KEY != null;
                if (DBTable.DBTable_PRIMARY_KEY.equalsIgnoreCase(CONSTANTS.NULL)) {
                    DBTable.DBTable_PRIMARY_KEY = null;
                }
                keyword = CollectDataFromFile.getNextKeyword();
                continue;
            }
            else if (keyword.equalsIgnoreCase("ForeignKey")) {

                DBTable.DBTable_FOREIGN_KEY = CollectDataFromFile.getNextKeyword();

                assert DBTable.DBTable_FOREIGN_KEY != null;
                if (DBTable.DBTable_FOREIGN_KEY.equalsIgnoreCase(CONSTANTS.NULL)) {
                    DBTable.DBTable_FOREIGN_KEY = null;
                }
                keyword = CollectDataFromFile.getNextKeyword();
                continue;
            }
            else if(keyword.equalsIgnoreCase("ColumnStructure")){
                getColumnStructure(keyword, DBTable);
                keyword = CollectDataFromFile.getNextKeyword();
                continue;
            }
            if(!keywordReceived){
                DBTable.DBTable_NAME = keyword;
                keywordReceived = true;
                keyword = CollectDataFromFile.getNextKeyword();
                continue;
            }
            else{
                //keywordReceived = !keywordReceived;
                TableRow row = TableRow.getRowByKeyword(keyword);
                DBTable.DBTableRows.add(row);
            }

            keyword = CollectDataFromFile.getNextKeyword();
        }
    }

    public static String getColumnStructure(String keyword, DBTable table){

        String key = null;
        String value = null;
        boolean entryReceived = false;

        keyword = CollectDataFromFile.getNextKeyword();

        while (true) {

            if(key != null && value != null){

                if(value.equalsIgnoreCase(CONSTANTS.NULL)){
                    value = null;
                }
                table.colNamesAndDataTypes.put(key, value);
                key = null;
                value = null;
            }
            if (CollectDataFromFile.tokenList == null || keyword == null) {
                return keyword;
            }
            if(keyword.equals("]") || keyword.equals("}") ){
                return keyword;
            }
            if(!entryReceived){
                key = keyword;
                entryReceived = true;
                keyword = CollectDataFromFile.getNextKeyword();
                continue;
            }
            else{
                value = keyword;
                entryReceived = false;
                keyword = CollectDataFromFile.getNextKeyword();
                continue;
            }
        }
    }




}
