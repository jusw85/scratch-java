package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class PgTest {

    public static void main(String[] args) throws Exception {

        Class.forName("org.postgresql.Driver");
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://192.168.1.151/klaverdb",
                    "klaveradmin",
                    "klaveradm1n");
            UUID[] uuidArray = {UUID.randomUUID()};
            PreparedStatement ps = connection.prepareStatement("insert into api._test(grantuserid) values(?)", Statement.RETURN_GENERATED_KEYS);
            ps.setArray(1, connection.createArrayOf("uuid", uuidArray));
            int i = ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                ResultSetMetaData rsm = generatedKeys.getMetaData();
                for (int j = 1; j <= rsm.getColumnCount(); j++) {
                    System.out.println(rsm.getColumnName(j));
                }

                while (generatedKeys.next()) {
                    for (int j = 1; j <= rsm.getColumnCount(); j++) {
                        System.out.println(generatedKeys.getObject(j) + " ");
                    }
                }

            }

            System.out.println(i);

//            int i = statement.executeUpdate("insert into api._test(grantuserid) values(null)");
//            Statement statement = connection.createStatement();
//            statement.setQueryTimeout(30); // set timeout to 30 sec.
//
//            statement.executeUpdate("drop table if exists person");
//            statement.executeUpdate(
//                    "create table person (id integer, name string)");
//            statement.executeUpdate("insert into person values(1, 'leo')");
//            statement.executeUpdate("insert into person values(2, 'yui')");
//            ResultSet rs = statement.executeQuery("select * from person");
//            while (rs.next()) {
//                // read the result set
//                System.out.println("name = " + rs.getString("name"));
//                System.out.println("id = " + rs.getInt("id"));
//            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }

}
