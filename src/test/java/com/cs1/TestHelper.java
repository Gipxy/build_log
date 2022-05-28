package com.cs1;

import com.cs1.db.DBService;
import com.cs1.model.BuildEvent;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class TestHelper {
    private static final Logger log = LoggerFactory.getLogger(DBService.class);

    public static List<BuildEvent> getAllBuildEvent() {
        Connection conn = DBService.inst().getConnection();
        QueryRunner run = new QueryRunner();
        ResultSetHandler<List<BuildEvent>> h = new BeanListHandler<>(BuildEvent.class);

        try {
            return run.query(conn, "SELECT * FROM BUILD_EVENT", h);
        } catch (SQLException ex) {
            log.error("getAllBuildEvent error: ", ex);
            throw new RuntimeException(ex);
        }
    }

    public static void clearAll() {
        Connection conn = DBService.inst().getConnection();
        try {
            try (Statement statement = conn.createStatement();) {
                statement.execute("delete from BUILD_EVENT ");
                conn.commit();
            }
        } catch (SQLException ex) {
            log.error("Error", ex);
        }

    }
}
