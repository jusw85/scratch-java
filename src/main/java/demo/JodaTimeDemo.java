package demo;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class JodaTimeDemo {

    public static void main(String[] args) {
        printDateByWeeks(2000, 1, 2001, 1);
    }

    /**
     * First day of week is hard Monday in JodaTime (ISO 8601)
     */
    public static void printDateByWeeks(int yearFrom, int weekFrom, int yearTo, int weekTo) {
        DateTime dt = new DateTime(String.format("%d-W%d-1", yearFrom, weekFrom), DateTimeZone.UTC);
        int maxweeks = dt.weekOfWeekyear().getMaximumValue();
        System.out.println("Max Weeks: " + maxweeks);

        int year = dt.getYear();
        int week = dt.getWeekOfWeekyear();
        int month, day;
        while (year < yearTo ||
                (year == yearTo && week <= weekTo)) {
            month = dt.getMonthOfYear();
            day = dt.getDayOfMonth();
            System.out.format("%s, y%dm%02dd%02dw%02d\n", dt, year, month, day, week);

            dt = dt.plusWeeks(1);
            year = dt.getYear();
            week = dt.getWeekOfWeekyear();
        }
    }

}
