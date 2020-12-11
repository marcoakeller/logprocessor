package com.company.eventslog.processor;

import com.company.eventslog.entity.LogEntry;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class EventLogManagerTest {

    private final EventLogManager objectUnderTest = new EventLogManager();

    @Test
    public void whenLineInJsonFormat_thenParseToLogEntry() {
        String jsonLine = "{\"id\":\"scsmbstgra\", \"state\":\"FINISHED\", \"type\":\"APPLICATION_LOG\", \"host\":\"12345\",\"timestamp\":1491377495217}";

        LogEntry logEntry = objectUnderTest.parseJson(jsonLine);

        assertEquals("scsmbstgra", logEntry.getId());
        assertEquals("FINISHED", logEntry.getState());
        assertEquals("APPLICATION_LOG", logEntry.getType());
        assertEquals("12345", logEntry.getHost());
        assertEquals(1491377495217L, logEntry.getTimestamp());
    }

    @Test
    public void whenLogEntryDurationHigherThan4ms_thenAlertFlagIsTrue() {
        String eventStart = "{\"id\":\"scsmbstgrc\", \"state\":\"STARTED\", \"timestamp\":1491377495210}";
        String eventEnd = "{\"id\":\"scsmbstgrc\", \"state\":\"FINISHED\", \"timestamp\":1491377495218}";

        LogEntry logEntryStart = objectUnderTest.parseJson(eventStart);
        LogEntry logEntryEnd = objectUnderTest.parseJson(eventEnd);
        List<LogEntry> logEntryList = Arrays.asList(logEntryEnd, logEntryStart);

        objectUnderTest.filterEvents(logEntryList);
        assertTrue(logEntryStart.isAlert());
    }

    @Test
    public void whenLogEntryDurationLowerThan4ms_thenAlertFlagIsFalse() {
        String eventStart = "{\"id\":\"scsmbstgrb\", \"state\":\"STARTED\", \"timestamp\":1491377495213}";
        String eventEnd = "{\"id\":\"scsmbstgrb\", \"state\":\"FINISHED\", \"timestamp\":1491377495216}";

        LogEntry logEntryStart = objectUnderTest.parseJson(eventStart);
        LogEntry logEntryEnd = objectUnderTest.parseJson(eventEnd);
        List<LogEntry> logEntryList = Arrays.asList(logEntryEnd, logEntryStart);

        objectUnderTest.filterEvents(logEntryList);
        assertFalse(logEntryStart.isAlert());
    }
}
