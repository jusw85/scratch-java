package demo;

import com.google.common.base.Joiner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PostgreSQLDemo {

    public static void main(String[] args) throws Exception {

        Class.forName("org.postgresql.Driver");

        try (Connection connection =
                     DriverManager.getConnection("jdbc:postgresql://127.0.0.1/mydb", "username", "password");) {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30); // set timeout to 30 sec.

            statement.executeUpdate("drop table if exists pgdemo");
            statement.executeUpdate("create table pgdemo (" +
                    "id uuid PRIMARY KEY DEFAULT uuid_generate_v4(), " +
                    "created_timestamp timestamp with time zone DEFAULT now(), " +
                    "vals text[])");

            String[] vals = {"hello", "world"};
            PreparedStatement ps = connection.prepareStatement("insert into pgdemo(vals) values(?)", Statement.RETURN_GENERATED_KEYS);
            ps.setArray(1, connection.createArrayOf("text", vals));

            int numResults = ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                ResultSetMetaData rsm = generatedKeys.getMetaData();

                List<String> columnNames = new ArrayList<>();
                for (int i = 1; i <= rsm.getColumnCount(); i++)
                    columnNames.add(rsm.getColumnName(i));
                System.out.println(Joiner.on(",").join(columnNames));

                while (generatedKeys.next()) {
                    List<Object> values = new ArrayList<>();
                    for (int i = 1; i <= rsm.getColumnCount(); i++)
                        values.add(generatedKeys.getObject(i));
                    System.out.println(Joiner.on(",").join(values));
                }
            }
            System.out.println("Rows updated: " + numResults);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

}
