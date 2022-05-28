package com.cs1;

import com.cs1.task.BuildLogParseTask;
import com.cs1.task.BuildLogProcessorTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogProcessorFactory {
    private static final Logger log  = LoggerFactory.getLogger(LogProcessorFactory.class);

    public static LogProcessor createBuildLogProcessor() {
        log.debug("Enter createBuildLogProcessor");
        return new LogProcessor(new BuildLogParseTask(), new BuildLogProcessorTask());
    }
}
