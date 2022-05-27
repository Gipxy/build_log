package com.test;

import com.test.task.BuildLogParseTask;
import com.test.task.BuildLogProcessorTask;
import com.test.task.ParserTask;
import com.test.task.ProcessorTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class LogProcessorFactory {
    private static final Logger log  = LoggerFactory.getLogger(LogProcessorFactory.class);

    public static LogProcessor createBuildLogProcessor() {
        log.debug("Enter createBuildLogProcessor");
        return new LogProcessor(new BuildLogParseTask(), new BuildLogProcessorTask());
    }
}
