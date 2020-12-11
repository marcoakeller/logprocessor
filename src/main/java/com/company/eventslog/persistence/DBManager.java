package com.company.eventslog.persistence;

import com.company.eventslog.entity.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * A database manager class.
 */
public class DBManager {
    public static final String INSERT_QUERY = "INSERT INTO events (event_id, event_duration, event_type, host, alert) values (?,?,?,?,?)";

    private static final Logger LOGGER = LoggerFactory.getLogger(DBManager.class);
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS events (" +
            " event_id VARCHAR(50) NOT NULL, event_duration INT NOT NULL," +
            " event_type VARCHAR(50), host VARCHAR(100), alert BOOLEAN DEFAULT FALSE NOT NULL," +
            " PRIMARY KEY (event_id));";
    private static final DBManager dbManager = new DBManager();

    private DBManager() {
    }

    public static DBManager getInstance() {
        return dbManager;
    }

    /**
     * Persist log entries to the database.
     *
     * @param logEntry the {@link LogEntry} to be persisted on DB
     */
    public void persistData(LogEntry logEntry) {
        try (Connection connection = getDBConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY)) {

            preparedStatement.setString(1, logEntry.getId());
            preparedStatement.setLong(2, logEntry.getDuration());
            preparedStatement.setString(3, logEntry.getType());
            preparedStatement.setString(4, logEntry.getHost());
            preparedStatement.setBoolean(5, logEntry.isAlert());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Error writing event to DB, event id: '{}'", logEntry.getId(), e);
        }
    }

    /**
     * Create log entries table to the database.
     */
    public void createTable() {
        try (Connection connection = getDBConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(CREATE_TABLE_QUERY);
        } catch (SQLException e) {
            LOGGER.error("Error creating event table to DB", e);
        }
    }

    public static Connection getDBConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:eventsdb", "SA", "");
    }
}
