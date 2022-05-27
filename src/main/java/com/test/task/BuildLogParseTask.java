package com.test.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.model.BuildLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildLogParseTask implements ParserTask<BuildLog> {
    private static final Logger log  = LoggerFactory.getLogger(BuildLogParseTask.class);

    @Override
    public BuildLog parse(String line) {
        log.debug("Enter parse: {}", line);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(line, BuildLog.class);
        } catch (JsonProcessingException e) {
            log.warn("Can't parse line: {}, will ignore!", line);
        }
        return null;
    }
}
