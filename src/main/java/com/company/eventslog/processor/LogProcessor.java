package com.company.eventslog.processor;

import com.company.eventslog.entity.LogEntry;
import com.company.eventslog.persistence.DBManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Main class for log processing.
 */
public class LogProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogProcessor.class);
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        LOGGER.info("Starting log processor...\n");
        System.out.println("Enter file path of the logfile: ");
        String filePath = sc.next();

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LOGGER.warn("Source file not found, exiting");
                return;
            }

            LOGGER.info("Creating table events if not exists");
            DBManager.getInstance().createTable();

            processLogEvents(file);
        } catch (IOException e) {
            LOGGER.error("Error processing log file '{}'", filePath, e);
        }
    }

    private static void processLogEvents(File file) throws IOException {
        LOGGER.info("Parsing log events");
        EventLogManager eventLogManager = new EventLogManager();
        Files.lines(Paths.get(file.toURI()))
                .parallel()
                .map(eventLogManager::parseJson)
                .filter(Objects::nonNull)
                .collect(groupByEventId())
                .forEach((logMapKey, logMapValue) -> {
                    eventLogManager.filterEvents(logMapValue);
                    eventLogManager.persistData(logMapValue);
                });
        LOGGER.info("Finished log events processing");
    }

    private static Collector<LogEntry, ?, Map<String, List<LogEntry>>> groupByEventId() {
        return Collectors.groupingBy(LogEntry::getId,
                Collectors.mapping(logEntry -> logEntry, Collectors.toList()));
    }
}
