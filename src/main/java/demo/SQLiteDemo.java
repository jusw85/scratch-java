package demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteDemo {

    public static void main(String[] args) throws Exception {
        // load the sqlite-JDBC driver using the current class loader
        Class.forName("org.sqlite.JDBC");

        // create a database connection
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:etc/sqlite.db");) {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30); // set timeout to 30 sec.

            statement.executeUpdate("drop table if exists person");
            statement.executeUpdate("create table person (id integer, name string)");
            statement.executeUpdate("insert into person values(1, 'John Doe')");
            statement.executeUpdate("insert into person values(2, 'Mary Sue')");
            ResultSet rs = statement.executeQuery("select * from person");
            while (rs.next()) {
                // read the result set
                System.out.println("name = " + rs.getString("name"));
                System.out.println("id = " + rs.getInt("id"));
            }
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
    }

}
