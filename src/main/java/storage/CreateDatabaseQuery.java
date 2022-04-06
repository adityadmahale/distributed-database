package storage;

import save.SaveDBToFile;

import java.io.IOException;

public class CreateDatabaseQuery {

    public Database database;

    public CreateDatabaseQuery() {
    }

    public boolean addDatabase(String databaseName) throws IOException {
        databaseName = databaseName.toUpperCase();
        if(AllDatabases.getInstance().databaseMap.containsKey(databaseName)){
            System.out.println("Database exists");
            return false;
        }
        database = new Database();
        database.databaseName = databaseName;
        AllDatabases.getInstance().databaseMap.put(databaseName, database);
        SaveDBToFile saveDBToFile = new SaveDBToFile();
        saveDBToFile.persistDatabaseToFile(AllDatabases.getInstance());
        return true;
    }


}