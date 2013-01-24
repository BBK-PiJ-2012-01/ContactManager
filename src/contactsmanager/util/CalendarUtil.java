package contactsmanager.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

/**
 * A class containing utility methods for dealing with Calendar objects.
 *
 * NB. This class actively ignores the time element of Calendar objects
 * (ie. only looks at the year, month and date).
 */
public class CalendarUtil {
    static final private SimpleDateFormat FULL_CALENDAR_FORMAT = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss.S z");
    static final private SimpleDateFormat CALENDAR_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Returns true if the given Calendar date (eg. "15/01/1956 at 12:00:00.000 GMT")
     * is in the future (ie. after this exact moment in time, with millisecond resolution).
     *
     * @param date the Calendar object to check
     * @return whether the given Calendar is in the future.
     */
    public static boolean isDateInFuture(Calendar date) {
        Calendar now = Calendar.getInstance();
        return date.after(now);
    }

    /**
     * Returns true if the given Calendar date (eg. "15/01/1956 at 12:00:00.000 GMT")
     * is in the past (ie. before this exact moment in time, with millisecond resolution).
     *
     * @param date the Calendar object to check
     * @return whether the given Calendar is in the past.
     */
    public static boolean isDateInPast(Calendar date) {
        Calendar now = Calendar.getInstance();
        return date.before(now);
    }



    /**
     * Gets the string representation of the given Calendar object's calendar date
     * (in UK-standard format), eg. "21/01/2012".
     *
     * @param date the Calendar object to extract the date from.
     * @return the string representation of the given date in "dd/MM/yyyy" format.
     */
    public static String getCalendarDateString(Calendar date) {
        return CALENDAR_DATE_FORMAT.format(date.getTime());
    }

    /**
     * Given a string formatted as "dd/MM/yyyy" (such as those returned by 'getCalendarDateString')
     * this returns a new Calendar object of that date.  NB. The time element of the Calendar object
     * is set to the current time.
     *
     * @param date_str the string formatted as "dd/MM/yyyy" to extract a date from.
     * @return the Calendar object with the given data.
     * @throws ParseException if the string was not formatted as "dd/MM/yyyy".
     */
    public static Calendar getCalendarDateFromString(String date_str) throws ParseException {
        Date date = CALENDAR_DATE_FORMAT.parse(date_str);
        Calendar calendar_date = Calendar.getInstance();
        calendar_date.setTime(date);
        calendar_date.clear(Calendar.HOUR_OF_DAY);
        calendar_date.clear(Calendar.MINUTE);
        calendar_date.clear(Calendar.SECOND);
        calendar_date.clear(Calendar.MILLISECOND);
        return calendar_date;
    }

    /**
     * Gets the string representation of the given Calendar object's date and time
     * (in UK-standard format, ie. "dd/MM/yyyy 'at' HH:mm:ss z").
     *
     * @param date the Calendar object to extract the date from.
     * @return the string representation of the given date in "dd/MM/yyyy 'at' HH:mm:ss z" format.
     */
    public static String getCalendarString(Calendar date) {
        return FULL_CALENDAR_FORMAT.format(date.getTime());
    }

    /**
     * Given a string formatted as "dd/MM/yyyy 'at' HH:mm:ss z" (such as those returned by 'getCalendarString')
     * this returns a new Calendar object of that date and time.
     *
     * @param date_str the string formatted as "dd/MM/yyyy 'at' HH:mm:ss z" to extract a date and time from.
     * @return the Calendar object with the given data.
     * @throws ParseException if the string was not formatted as "dd/MM/yyyy 'at' HH:mm:ss z".
     */
    public static Calendar getCalendarFromString(String date_str) throws ParseException {
        Date date = FULL_CALENDAR_FORMAT.parse(date_str);
        Calendar calendar_date = Calendar.getInstance();
        calendar_date.setTime(date);
        return calendar_date;
    }

    /**
     * Returns true if the given Calendar objects share the same calendar date, or false otherwise.
     * NB. this ignores the time elements of the objects.
     *
     * @param first the first Calendar object to check against the second.
     * @param second the second Calendar object to check against the first.
     * @return Whether the Calendar objects fall on the same calendar date.
     */
    public static boolean areDatesEqual(Calendar first, Calendar second) {
        return getCalendarDateString(first).equals(getCalendarDateString(second));
    }

    /**
     * Returns a comparator that compares the date element of Calendar objects.
     *
     * ie. two Calendar objects that fall on the same date are considered equal
     * (otherwise the 'Calendar.compareTo' method is used).
     *
     * eg1. adding two Calendar objects for the same calendar date but different times
     *      will cause the last-added to replace the first-added.
     *
     * eg2. sorting a list of Calendar objects for the same calendar date but different
     *      times will not sort those objects chronologically within the shared date.
     *
     * @return a comparator for the date elements of Calendar objects.
     */
    public static Comparator<Calendar> getDateComparator() {
        return new Comparator<Calendar>() {
            @Override
            public int compare(Calendar o1, Calendar o2) {
                if (areDatesEqual(o1, o2)) {
                    return 0;
                } else {
                    return o1.compareTo(o2);
                }
            }
        };
    }


}
