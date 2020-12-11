package com.company.eventslog.processor;

import com.company.eventslog.entity.LogEntry;
import com.company.eventslog.persistence.DBManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Class to parse and mange event log entries.
 */
public class EventLogManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventLogManager.class);
    private final DBManager dbManager = DBManager.getInstance();

    /**
     * Parse json event entry.
     *
     * @param line - The {@link String} line of log in JSON format
     * @return {@link LogEntry} entity
     */
    public LogEntry parseJson(String line) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(line, LogEntry.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error parsing json object '{}'", line, e);
        }
        return null;
    }

    /**
     * Flag events where duration is higher than 4ms.
     *
     * @param logMapValue The {@link List} of {@link LogEntry}
     */
    public void filterEvents(List<LogEntry> logMapValue) {
        if (logMapValue.size() == 2) {
            long eventDuration = Math.abs(logMapValue.get(0).getTimestamp() - logMapValue.get(1).getTimestamp());
            if (logMapValue.get(0).getTimestamp() - logMapValue.get(1).getTimestamp() > 4) {
                logMapValue.get(0).setAlert(true);
                logMapValue.get(1).setAlert(true);
                LOGGER.warn("Event '{}' took longer than 4 ms, event duration: {}", logMapValue.get(0).getId(), eventDuration);
            }
        }
    }

    /**
     * Persist events to DB.
     *
     * @param logMapValue The {@link List} of {@link LogEntry}
     */
    public void persistData(List<LogEntry> logMapValue) {
        if (logMapValue.isEmpty()) {
            return;
        }
        dbManager.persistData(logMapValue.get(0));
    }
}
