package com.cs1;

import com.cs1.db.DBService;
import com.cs1.model.BuildEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.cs1.TestHelper.getAllBuildEvent;

public class LogProcessorTest {
    private static final Logger log  = LoggerFactory.getLogger(LogProcessorTest.class);

    @BeforeAll
    public static void beforeAll() throws SQLException {
        DBService.inst().initDatabase();
    }

    @BeforeEach
    public void clearAll() {
        log.info("clearAll............................");
        TestHelper.clearAll();
    }

    @Test
    public void testProcess_givenFinishedAfterStartedWith1Event_shouldSuccess() throws IOException {
        LogProcessor processor = LogProcessorFactory.createBuildLogProcessor();

        processor.process("./sample/file1.txt");

        List<BuildEvent> buildEventList = getAllBuildEvent();

        Assertions.assertEquals(1, buildEventList.size());
    }

    @Test
    public void testProcess_given3Event_shouldReturn3() throws IOException {
        LogProcessor processor = LogProcessorFactory.createBuildLogProcessor();

        processor.process("./sample/file4.txt");

        List<BuildEvent> buildEventList = getAllBuildEvent();

        Assertions.assertEquals(3, buildEventList.size());
    }

    @Test
    public void testProcess_givenFinishedBeforeStartedWith1Event_shouldSuccess() throws IOException {
        LogProcessor processor = LogProcessorFactory.createBuildLogProcessor();

        processor.process("./sample/file2.txt");

        List<BuildEvent> buildEventList = getAllBuildEvent();

        Assertions.assertEquals(1, buildEventList.size());
    }

    @Test
    public void testHost_givenHostInStarted_shouldReturn() throws IOException {
        LogProcessor processor = LogProcessorFactory.createBuildLogProcessor();

        processor.process("./sample/file1.txt");

        List<BuildEvent> buildEventList = getAllBuildEvent();

        Assertions.assertEquals(1, buildEventList.size());
        Assertions.assertEquals("12", buildEventList.get(0).getHost());

    }

    @Test
    public void testHost_givenHostInFinished_shouldReturn() throws IOException {
        LogProcessor processor = LogProcessorFactory.createBuildLogProcessor();

        processor.process("./sample/file2.txt");

        List<BuildEvent> buildEventList = getAllBuildEvent();

        Assertions.assertEquals(1, buildEventList.size());
        Assertions.assertEquals("12", buildEventList.get(0).getHost());

    }

    @Test
    public void testAlert_given4msDuration_shouldAlert() throws IOException {
        LogProcessor processor = LogProcessorFactory.createBuildLogProcessor();

        processor.process("./sample/file1.txt");

        List<BuildEvent> buildEventList = getAllBuildEvent();

        log.info("Verify:");
        Assertions.assertEquals(1, buildEventList.size());
        Assertions.assertFalse(buildEventList.get(0).isAlert());

    }

    @Test
    public void testAlert_given2msDuration_shouldNotAlert() throws IOException {
        LogProcessor processor = LogProcessorFactory.createBuildLogProcessor();

        processor.process("./sample/file2.txt");

        List<BuildEvent> buildEventList = getAllBuildEvent();

        Assertions.assertEquals(1, buildEventList.size());
        Assertions.assertFalse(buildEventList.get(0).isAlert());

    }

    @Test
    public void testAlert_given10msDuration_shouldAlert() throws IOException {
        LogProcessor processor = LogProcessorFactory.createBuildLogProcessor();

        processor.process("./sample/file3.txt");

        List<BuildEvent> buildEventList = getAllBuildEvent();

        Assertions.assertEquals(1, buildEventList.size());
        Assertions.assertTrue(buildEventList.get(0).isAlert());

    }
}
