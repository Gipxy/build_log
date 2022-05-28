package com.cs1.db;

import com.cs1.model.BuildEvent;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DBService {
    private static final Logger log  = LoggerFactory.getLogger(DBService.class);

    private Connection connection;

    private DBService() {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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

    public void insert(BuildEvent buildEvent) throws SQLException {
        String sql ="INSERT INTO BUILD_EVENT " +
                "(id,started_time,finished_time, duration, type, host, alert) " +
                "VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, buildEvent.getId());
            pstmt.setLong(2, buildEvent.getStartedTime());
            pstmt.setLong(3, buildEvent.getFinishedTime());
            pstmt.setLong(4, buildEvent.getDuration());
            pstmt.setString(5, buildEvent.getType());
            pstmt.setString(6, buildEvent.getHost());
            pstmt.setBoolean(7, buildEvent.isAlert());
            pstmt.executeUpdate();
            connection.commit();
        }
    }
}
