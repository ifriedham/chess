package dataaccess;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.*;
import server.Server;


public class MySQLTests {

    @Test
    public void testChessTableCreated() {
        // Start the server which should initialize the database with the three tables
        Server server = new Server();
        server.run(0);

        try (var conn = DatabaseManager.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, "%", null);

            boolean found = false;
            System.out.println("Tables in the database:");
            while (resultSet.next()) {
                String tableName = resultSet.getString(3);
                System.out.println(tableName);
                if (tableName.equals("chess")) {
                    found = true;
                }
            }

            Assertions.assertTrue(found, "Chess table not found");
        } catch (SQLException | DataAccessException e) {
            Assertions.fail("An error occurred while checking the tables", e);
        }
    }
}
