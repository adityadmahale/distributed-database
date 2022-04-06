package storage;

import java.io.Serializable;
import java.util.Map;

public class TableRow implements Serializable {

    public Map<String, Object> rowInputsMap = null;

    public TableRow() {
    }

    public TableRow(Map<String, Object> rowInputsMap) {
        this.rowInputsMap = rowInputsMap;
    }

    public String getCurrentRowData(){

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n");
        boolean first = true;

        for(Map.Entry<String, Object> entry : rowInputsMap.entrySet()){

            if(!first){
                stringBuilder.append(" , \n");
            }
            else{

                first = false;
            }

            stringBuilder.append(CONSTANTS.QUADRUPLE_TAB).append(CONSTANTS.DOUBLE_TAB).append("\t").append(entry.getKey()).append(" : '").append(entry.getValue()).append("'");
        }
        stringBuilder.append("\n").append(CONSTANTS.QUADRUPLE_TAB).append(CONSTANTS.DOUBLE_TAB).append("}");

        return stringBuilder.toString();
    }


    public static TableRow getRowByKeyword(String keyword){

        TableRow oneRow = new TableRow();
        String key = null;
        String value = null;
        boolean entryObtained = false;

        while (true){
            
            if(key != null && value != null){
                
                if(value.equalsIgnoreCase(CONSTANTS.NULL)){
                    value = null;
                }
                
                oneRow.rowInputsMap.put(key, value);
                key = null;
                value = null;
            }
            if (CollectDataFromFile.tokenList == null || keyword == null) {
                return oneRow;
            }
            if(keyword.equals("]") || keyword.equals("}") ){
                return oneRow;
            }
            if(!entryObtained){
                key = keyword;
                entryObtained = !entryObtained;
                keyword = CollectDataFromFile.getNextKeyword();
                continue;
            }
            else{
                value = keyword;
                entryObtained = !entryObtained;
                keyword = CollectDataFromFile.getNextKeyword();
                continue;
            }
        }
    }



}
