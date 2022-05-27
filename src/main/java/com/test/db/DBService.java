package com.test.db;

import com.test.model.BuildEvent;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBService {
    private static final Logger log  = LoggerFactory.getLogger(DBService.class);

    private Connection connection;

    private DBService() {
    }

    public static DBService inst() {
        return new DBService();
    }

    public Connection getConnection()  {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:hsqldb:file:data/builds", "app1", "p1");
            } catch (SQLException ex) {
                log.error("Error in getConnection:", ex);
            }
        }
        return connection;
    }

    public void initDatabase() throws SQLException {
        try (Statement statement = getConnection().createStatement();) {
            statement.execute("CREATE TABLE IF NOT EXISTS BUILD_EVENT " +
                    "(id varchar(100) NOT NULL, " +
                    "started_time bigint, " +
                    "finished_time bigint, " +
                    "duration bigint, " +
                    "type varchar(100), " +
                    "host varchar(30), " +
                    "alert boolean)");
            connection.commit();
        }
    }

    public void insert(BuildEvent buildEvent) {
        QueryRunner run = new QueryRunner();
        try {
            run.update( getConnection(),"INSERT INTO BUILD_EVENT " +
                            "(id,started_time,finished_time, duration, type, host, alert) " +
                            "VALUES (?,?,?,?,?,?,?)",
                    buildEvent.getId(), buildEvent.getStartedTime(),
                    buildEvent.getFinishedTime(), buildEvent.getDuration(),
                    buildEvent.getType(), buildEvent.getHost(), buildEvent.isAlert());

            getConnection().commit();
        } catch (SQLException ex) {
            log.warn("Error when insert", ex);
        }
    }
}
