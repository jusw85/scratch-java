package demo.jooq;

import demo.jooq.generated.tables.records.MytableRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.jooq.util.GenerationTool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static demo.jooq.generated.Tables.MYTABLE;

public class JooqDemo {

    private static String username = "foo";
    private static String password = "bar";
    private static String url = "jdbc:postgresql://127.0.0.1:5432/foobar";

    public static void main(String[] args) throws Exception {
        Class.forName("org.postgresql.Driver");
        generateModel();
        testGenerateSql();
        testInsert();
        testSelect();
    }

    private static void testSelect() throws SQLException {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            DSLContext context = DSL.using(conn, SQLDialect.POSTGRES_9_5);
            Result<Record> records =
                    context.select().from(MYTABLE)
                            .where(MYTABLE.COUNT.gt(1))
                            .limit(5)
                            .fetch();

            for (Record record : records) {
                MytableRecord mytableRecord = (MytableRecord) record;
                System.out.println(mytableRecord);
            }
        }
    }

    private static void testInsert() throws SQLException {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            DSLContext context = DSL.using(conn, SQLDialect.POSTGRES_9_5);
            MytableRecord record = new MytableRecord();
            record.setTitle("My Title");
            record.setDescription("My Description");
            record.setCount(1);
            context.insertInto(MYTABLE)
                    .set(record)
                    .execute();

            context.insertInto(MYTABLE, MYTABLE.TITLE, MYTABLE.DESCRIPTION, MYTABLE.COUNT)
                    .values("My Title", "My Description", 2)
                    .execute();
        }
    }

    private static void testGenerateSql() {
        DSLContext context = DSL.using(SQLDialect.POSTGRES_9_5);
        String sql1 = context
                .insertInto(MYTABLE, MYTABLE.TITLE, MYTABLE.DESCRIPTION)
                .values("My Title", "My Description")
                .getSQL(ParamType.INLINED);
        String sql2 = context
                .select().from(MYTABLE)
                .where(MYTABLE.TITLE.contains("My Search"))
                .getSQL(ParamType.INLINED);
        System.out.println(sql1);
        System.out.println(sql2);
    }

    public static void generateModel() throws Exception {
        String[] args = {"etc/jooq.xml"};
        GenerationTool.main(args);
    }

}
