package init;

import util.AccessReader;
import util.DatabaseManager;
import util.ExcelReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Initializer {
     String dbConfigPath;
    Initializer(String dbConfigPath) {
        this.dbConfigPath = dbConfigPath;
    }

    void createTableAndInsertData(String databaseName, String typeOfInputFile) {
        System.out.println();
        System.out.println("Processing " + databaseName + ": ");
        //create tables
        System.out.println();
        System.out.println("Reading config from " + this.dbConfigPath + "...");

        String resourcePath = "inputs/";
        List<String> databaseConfig = readFile(this.dbConfigPath);
        assert databaseConfig.size() == 3;
        DatabaseManager manager = new DatabaseManager(databaseConfig.get(0), databaseConfig.get(1), databaseConfig.get(2));
        System.out.println("Done.");

        System.out.println();
        System.out.println("Creating database...");
        manager.execute("DROP DATABASE IF EXISTS " + databaseName);
        manager.execute("CREATE DATABASE " + databaseName);
        System.out.println("Done.");
        manager.executeQuery("USE " + databaseName);

        System.out.println();
        System.out.println("Creating tables...");

        List<String> roomSQL = readFile(resourcePath + "sqls/room.sql");
        List<String> studentSQL = readFile(resourcePath + "sqls/student.sql");

        assert roomSQL.size() == 2;
        assert studentSQL.size() == 2;

        roomSQL.forEach(sql -> manager.execute(sql));
        studentSQL.forEach(sql -> manager.execute(sql));
        System.out.println("Done.");

        System.out.println();
        System.out.println("Reading data from files and inserting into tables...");
        switch (typeOfInputFile) {
            case "xls":{
                ExcelReader roomReader = new ExcelReader(resourcePath + "xls/room.xls");
                ExcelReader studentReader = new ExcelReader(resourcePath + "xls/student.xls");
                List<List<String>> roomRows = roomReader.getRows();
                List<List<String>> studentRows = studentReader.getRows();
                assert roomRows.size() > 0;
                assert studentRows.size() > 0;
                try {
                    manager.insertDataIntoTable("room", roomRows);
                    manager.insertDataIntoTable("student", studentRows);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            break;
            case "accdb": {
                AccessReader accessReader = new AccessReader(resourcePath + "access/oralexam.accdb");
                List<List<String>> roomRows = accessReader.getRows("room");
                List<List<String>> studentRows = accessReader.getRows("student");
                assert roomRows.size() > 0;
                assert studentRows.size() > 0;
                try {
                    manager.insertDataIntoTable("room", roomRows);
                    manager.insertDataIntoTable("student", studentRows);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            break;

        }
        System.out.println("Done.");

        manager.close();
        System.out.println();
        System.out.println("Finish processing " + databaseName + ".");
        System.out.println();
    }

    public static List<String> readFile(String fileName) {
        List<String> fileLines  = new ArrayList<String>();
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                fileLines.add(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileLines;
    }
}
