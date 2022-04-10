package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Main;

import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils.MetaDataHandler;
import ca.dal.distributed.dpg1.Controllers.TransactionModule.Exceptions.TransactionExceptions;

public class Test {

    public static void main(String[] args) throws TransactionExceptions {

        QueryExecutor executor = new QueryExecutor();
        //executor.processInputQuery("CREATE DATABASE localDBTest;");
        //executor.processInputQuery("drop database localDBTest;");
       // executor.processInputQuery("use database localDBTest;");
        //executor.processInputQuery("drop table TestTable;");
       //executor.processInputQuery("CREATE TABLE TestTable (id INT PRIMARY KEY, name TEXT REFERENCES TEST2(id));");
       /*executor.processInputQuery("INSERT INTO TestTable (id, name) VALUES (2, TestName);");
       System.out.println(executor.processInputQuery("SELECT * FROM TestTable;").toString());
       System.out.println(executor.processInputQuery("SELECT DISTINCT id FROM TestTable;"));*/
        //executor.processInputQuery("TRUNCATE TABLE TestTable;");

        //Test Commit
     /*   executor.processInputQuery("Start Transaction;");
        executor.processInputQuery("INSERT INTO TestTable (id, name) VALUES (9, TestTransaction);");
        executor.processInputQuery("rollback;");*/
        //executor.processInputQuery("commit;");



    }
}
