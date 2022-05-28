package com.cs1;

import com.cs1.model.BuildLog;
import com.cs1.task.ParserTask;
import com.cs1.task.ProcessorTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class LogProcessor {
    private static final Logger log  = LoggerFactory.getLogger(LogProcessor.class);

    private final ParserTask<BuildLog> parserTask;
    private final ProcessorTask<BuildLog> processorTask;

    private final AtomicLong count = new AtomicLong();


    public LogProcessor(ParserTask<BuildLog> parserTask, ProcessorTask<BuildLog> processorTask) {
        this.parserTask = parserTask;
        this.processorTask = processorTask;
    }

    public void process(String filePath) {
        ForkJoinPool customThreadPool = new ForkJoinPool(4);

        try {
            customThreadPool.submit( () -> {
                try (Stream<String> lines = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8);){
                    lines.parallel().map(parserTask::parse).forEach( (e) -> {
                        count.incrementAndGet();
                        processorTask.process(e);
                    });
                } catch (IOException ex) {
                    log.error("Error when reading file: ", ex);
                }
            }).get();

            log.info("Processed: {} records", count);
        } catch (Exception e) {
            log.error("Error : ", e);
        }
    }
}
