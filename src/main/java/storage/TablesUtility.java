package storage;

import java.util.ArrayList;

public class TablesUtility {
    
    public DBTable createDuplicateTable(DBTable tableToCopy){
        
        DBTable duplicateTable = new DBTable();
        duplicateTable.colNamesAndDataTypes = tableToCopy.colNamesAndDataTypes;
        duplicateTable.DBTableRows = new ArrayList<>(tableToCopy.DBTableRows);
        duplicateTable.DBTable_NAME = tableToCopy.DBTable_NAME;
        return duplicateTable;
        
    }
}
