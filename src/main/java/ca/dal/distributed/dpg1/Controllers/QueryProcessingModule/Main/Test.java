package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main;

import ca.dal.distributed.dpg1.Controllers.TransactionModule.Exceptions.TransactionExceptions;
import com.sun.security.jgss.GSSUtil;

import java.nio.file.Files;

public class Test {

    public static void main(String[] args) throws TransactionExceptions {

        QueryExecutor executor = new QueryExecutor();
        //executor.processInputQuery("CREATE DATABASE testDB;");
        //executor.processInputQuery("drop database testDB;");
        executor.processInputQuery("use database testDB;");
        //executor.processInputQuery("CREATE TABLE students (id INT, name TEXT);");
       //executor.processInputQuery("INSERT INTO students (id, name) VALUES (2, TestName);");
        System.out.println(executor.processInputQuery("SELECT * FROM students;").toString());
        //System.out.println(executor.processInputQuery("SELECT DISTINCT id FROM students;"));
        //executor.processInputQuery("TRUNCATE TABLE students;");

    }
}
