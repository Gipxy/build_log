package com.test;

public class App {
    public static void main(String[] args) {
        LogProcessor processor = LogProcessorFactory.createBuildLogProcessor();
        processor.process("logfile.txt");
    }
}
