package com.cs1;

import com.cs1.db.DBService;

import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws SQLException {
        DBService.inst().initDatabase();
        LogProcessor processor = LogProcessorFactory.createBuildLogProcessor();
        processor.process("logfile.txt");
    }
}
