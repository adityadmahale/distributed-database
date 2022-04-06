package save;

import storage.AllDatabases;
import storage.Database;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

//Save to DB
public class SaveDBToFile {

    public SaveDBToFile() {
    }

    public void persistDatabaseToFile(AllDatabases databases) throws IOException {

        File file = new File("src/main/java/DatabasesList.txt");
        FileWriter fileWriter = new FileWriter(file);

        fileWriter.write(databases.getAllMyDatabases());
        fileWriter.close();

    }

/*    public void updateTables(Database value, String path) throws IOException {
        FileWriter fileWriter = null;
        try {

            fileWriter = new FileWriter(path);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(value.getMyDatabase());
            bufferedWriter.close();
            fileWriter.close();
            System.out.println("File Written Successfully!!!!");

            File file = new File(path);
            if (file.exists()){
                file.delete();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }*/

}
