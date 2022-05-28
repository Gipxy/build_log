package com.cs1;

import com.cs1.db.DBService;
import com.cs1.model.BuildEvent;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.cs1.TestHelper.getAllBuildEvent;

public class PerfTest {
    private static final Logger log  = LoggerFactory.getLogger(PerfTest.class);

    public static void main(String[] args) throws IOException, SQLException {
        DBService.inst().initDatabase();
        TestHelper.clearAll();

        LogProcessor processor = LogProcessorFactory.createBuildLogProcessor();
        bigFileTest(processor, 100);

        TestHelper.clearAll();
        bigFileTest(processor, 10000);
    }

    private static void bigFileTest(LogProcessor processor, int n) throws IOException {
        String filePath = "./sample/big-file.txt";
        long t1 = System.nanoTime();
        genBuildFile(n, filePath);
        long t2 = System.nanoTime();

        processor.process(filePath);
        long t3 = System.nanoTime();

        List<BuildEvent> buildEventList = getAllBuildEvent();
        long t4 = System.nanoTime();

        log.info("Time taken in ms (gen, process, fetch) with n={}: ({}, {}, {})",
                n, toMs(t2-t1), toMs(t3-t2), toMs(t4-t3));

//        Assertions.assertEquals(n, buildEventList.size());
    }

    public static long toMs(long takenInNs) {
        return TimeUnit.MILLISECONDS.convert(takenInNs, TimeUnit.NANOSECONDS);
    }

    private static void genBuildFile(int n, String filePath) throws IOException {
        String template1 = "{\"id\": \"%s\", \"state\" : \"FINISHED\", \"type\" : \"APPLICATION_LOG\", \"host\": \"12\", \"timestamp\": 1491377495216}";
        String template2 = "{\"id\": \"%s\", \"state\" : \"STARTED\", \"timestamp\": 1491377495215}";


        try (FileWriter writer = new FileWriter(filePath);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            for (int i=0; i<n; i++) {
                bufferedWriter.write(String.format(template1, i+"") + "\n");
                bufferedWriter.write(String.format(template2, i+"") + "\n");
            }
        }
    }

}
