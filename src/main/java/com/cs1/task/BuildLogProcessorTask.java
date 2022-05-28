package com.cs1.task;

import com.cs1.Config;
import com.cs1.db.DBService;
import com.cs1.model.BuildEvent;
import com.cs1.model.BuildLog;
import com.cs1.model.LogState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
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
        BuildEvent buildEvent;
        boolean isCompleted;
        synchronized (eventMap) {
            if (!eventMap.containsKey(buildLog.getId())) {
                buildEvent = new BuildEvent();
                eventMap.put(buildLog.getId(), buildEvent);
            } else {
                buildEvent = eventMap.get(buildLog.getId());
            }

            log2event(buildLog, buildEvent);
            isCompleted = buildEvent.checkIsCompleted();
        }

        if (isCompleted) {
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
            log.info("Done {} records, cache size: {}", count, eventMap.keySet().size());
        }

        buildEvent.setDuration(buildEvent.getFinishedTime()-buildEvent.getStartedTime());
        buildEvent.setAlert(buildEvent.getDuration() > Config.ALERT_THRESHOLD);

        if (buildEvent.isAlert()) {
            log.info("Alert event: {}", buildEvent);
        }

        try {
            DBService.inst().insert(buildEvent);
        } catch (SQLException ex) {
            log.warn("Error when insert !", ex);
        }

        log.debug("inserted!");

        eventMap.remove(buildEvent.getId());

    }
}
