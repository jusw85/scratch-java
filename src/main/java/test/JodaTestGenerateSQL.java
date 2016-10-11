package test;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class JodaTestGenerateSQL {

    public static void main(String[] args) {
        createTables(2016, 1, 2017, 2);
//        createTrigger(2016, 1, 2017, 2);
    }

    public static void createTables(int yearFrom, int weekFrom, int yearTo, int weekTo) {
        int year = yearFrom;
        int week = weekFrom;
        while (year != yearTo || week != weekTo) {
            DateTime dateTime1 = new DateTime(year + "-W" + week + "-1", DateTimeZone.UTC);
            int year1 = dateTime1.getYear();
            int week1 = dateTime1.getWeekOfWeekyear();
            int month1 = dateTime1.getMonthOfYear();
            int day1 = dateTime1.getDayOfMonth();

            week += 1;
            int maxWeek = new DateTime().withYear(year).weekOfWeekyear().getMaximumValue();
            if (week > maxWeek) {
                week = 1;
                year += 1;
            }
            DateTime dateTime2 = new DateTime(year + "-W" + week + "-1", DateTimeZone.UTC);
            int year2 = dateTime2.getYear();
            int week2 = dateTime2.getWeekOfWeekyear();
            int month2 = dateTime2.getMonthOfYear();
            int day2 = dateTime2.getDayOfMonth();

            System.out.format("CREATE TABLE vessel_positions_y%dw%02d (\n", year1, week1);
            System.out.format("  CHECK (tme >= '%d-%02d-%02d 00:00:00'::timestamp without time zone AND tme < '%d-%02d-%02d 00:00:00'::timestamp without time zone)\n",
                    year1, month1, day1, year2, month2, day2);
            System.out.println(") INHERITS (vessel_positions);");
//            System.out.format("CREATE INDEX vessel_positions_y%dw%02d_tme_idx ON vessel_positions_y%dw%02d(tme);\n", year1, week1, year1, week1);
//            System.out.format("CREATE INDEX vessel_positions_y%dw%02d_mmsi_idx ON vessel_positions_y%dw%02d(mmsi);\n", year1, week1, year1, week1);
//            System.out.format("CREATE INDEX vessel_positions_y%dw%02d_mmsitme_idx ON vessel_positions_y%dw%02d(mmsi, tme);\n", year1, week1, year1, week1);
//            System.out.format("CREATE INDEX vessel_positions_y%dw%02d_geom_idx ON vessel_positions_y%dw%02d USING gist(geom);\n", year1, week1, year1, week1);
//            System.out.format("CREATE UNIQUE INDEX vessel_positions_y%dw%02d_pk_idx ON vessel_positions_y%dw%02d(pk);\n", year1, week1, year1, week1);
            System.out.format("CREATE INDEX ON vessel_positions_y%dw%02d(tme);\n", year1, week1, year1, week1);
            System.out.format("CREATE INDEX ON vessel_positions_y%dw%02d(mmsi);\n", year1, week1, year1, week1);
            System.out.format("CREATE INDEX ON vessel_positions_y%dw%02d(mmsi, tme);\n", year1, week1, year1, week1);
            System.out.format("CREATE INDEX ON vessel_positions_y%dw%02d USING gist(geom);\n", year1, week1, year1, week1);
            System.out.format("CREATE UNIQUE INDEX ON vessel_positions_y%dw%02d(pk);\n", year1, week1, year1, week1);

            System.out.println();
        }
    }

    public static void createTrigger(int yearFrom, int weekFrom, int yearTo, int weekTo) {
        int year = yearFrom;
        int week = weekFrom;
        while (year != yearTo || week != weekTo) {
            DateTime dateTime1 = new DateTime(year + "-W" + week + "-1", DateTimeZone.UTC);
            int year1 = dateTime1.getYear();
            int week1 = dateTime1.getWeekOfWeekyear();
            int month1 = dateTime1.getMonthOfYear();
            int day1 = dateTime1.getDayOfMonth();

            week += 1;
            int maxWeek = new DateTime().withYear(year).weekOfWeekyear().getMaximumValue();
            if (week > maxWeek) {
                week = 1;
                year += 1;
            }
            DateTime dateTime2 = new DateTime(year + "-W" + week + "-1", DateTimeZone.UTC);
            int year2 = dateTime2.getYear();
            int week2 = dateTime2.getWeekOfWeekyear();
            int month2 = dateTime2.getMonthOfYear();
            int day2 = dateTime2.getDayOfMonth();

            System.out.format("ELSIF (NEW.tme >= '%d-%02d-%02d 00:00:00' AND NEW.tme < '%d-%02d-%02d 00:00:00') THEN\n", year1, month1, day1, year2, month2, day2);
            System.out.format("  INSERT INTO vessel_positions_y%dw%02d VALUES (NEW.*);\n", year1, week1);
        }
    }

}
