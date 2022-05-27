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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

public class LogProcessor {
    private static final Logger log  = LoggerFactory.getLogger(LogProcessor.class);

    private final ParserTask parserTask;
    private final ProcessorTask processorTask;

    public LogProcessor(ParserTask parserTask, ProcessorTask processorTask) {
        this.parserTask = parserTask;
        this.processorTask = processorTask;
    }

    public void process(String filePath) {
        ForkJoinPool customThreadPool = new ForkJoinPool(4);

        try {
            customThreadPool.submit( () -> {
                try (Stream<String> lines = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
                    lines.map(parserTask::parse).forEach(processorTask::process);
                } catch (IOException e) {
                    log.error("Error reading file: ", e);
                }
            }).get();
        } catch (Exception e) {
            log.error("Error : ", e);
        }
    }
}
