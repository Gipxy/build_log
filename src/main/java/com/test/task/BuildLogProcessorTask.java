package com.test.task;

import com.test.Config;
import com.test.db.DBService;
import com.test.model.BuildEvent;
import com.test.model.BuildLog;
import com.test.model.LogState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class BuildLogProcessorTask implements ProcessorTask<BuildLog> {
    private static final Logger log  = LoggerFactory.getLogger(BuildLogProcessorTask.class);
    private final Map<String, BuildEvent> eventMap = new ConcurrentHashMap<>();

    private AtomicLong countCompleted = new AtomicLong();

    @Override
    public void process(BuildLog buildLog) {
        if (buildLog == null) {
            log.warn("Null object send to processor!");
            return;
        }

        log.debug("Process log line: {}", buildLog);
        BuildEvent buildEvent = eventMap.get(buildLog.getId());
        if (buildEvent==null) {
            buildEvent= new BuildEvent();
            eventMap.put(buildLog.getId(), buildEvent);
        }

        log2event(buildLog, buildEvent);

        if (buildEvent.checkIsCompleted()) {
            handleCompleted(buildEvent);
        }
    }



    private void log2event(BuildLog buildLog, BuildEvent buildEvent) {
        if (buildEvent.getId() == null) {
            buildEvent.setId(buildLog.getId());
        }

        if (buildLog.getState()== LogState.STARTED) {
            buildEvent.setStartedTime(buildLog.getTimestamp());
        } else {
            buildEvent.setFinishedTime(buildLog.getTimestamp());
        }

        if (buildLog.getHost()!=null) {
            buildEvent.setHost(buildLog.getHost());
        }

        if (buildLog.getHost()!=null) {
            buildEvent.setType(buildLog.getType());
        }
    }

    private void handleCompleted(BuildEvent buildEvent) {
        log.debug("Handle done: {}", buildEvent);

        long count = countCompleted.incrementAndGet();

        if (count %1000 == 0) {
            log.info("Done {} records ", count);
        }

        buildEvent.setDuration(buildEvent.getFinishedTime()-buildEvent.getStartedTime());
        buildEvent.setAlert(buildEvent.getDuration() > Config.ALERT_THRESHOLD);

        if (buildEvent.isAlert()) {
            log.info("Alert event: {}", buildEvent);
        }

        DBService.inst().insert(buildEvent);

        eventMap.remove(buildEvent.getId());

    }
}
