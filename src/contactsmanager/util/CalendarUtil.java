package contactsmanager.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A class containing utility methods for dealing with Calendar objects.
 *
 * NB. This class actively ignores the time element of Calendar objects
 * (ie. only looks at the year, month and date).
 */
public class CalendarUtil {

    /**
     * Returns true if the given Calendar date (eg. 1st of January, 2012)
     * is in the future (where today is considered both in the future
     * and in the past).  Ie. It ignores the time element of the Calendar object.
     *
     * @param date the Calendar object to check
     * @return whether the given Calendar's date is in the future (inclusive of today)
     */
    public static boolean isDateInFuture(Calendar date) {
        Calendar start_of_today = Calendar.getInstance();
        start_of_today.set(Calendar.HOUR_OF_DAY, 0);
        start_of_today.set(Calendar.MINUTE, 0);
        start_of_today.set(Calendar.SECOND, 0);

        return date.after(start_of_today);
    }

    /**
     * Returns true if the given Calendar date (eg. 1st of January, 2012)
     * is in the past (where today is considered both in the future
     * and in the past).  Ie. It ignores the time element of the Calendar object.
     *
     * @param date the Calendar object to check
     * @return whether the given Calendar's date is in the past (inclusive of today)
     */
    public static boolean isDateInPast(Calendar date) {
        Calendar start_of_tomorrow = Calendar.getInstance();
        start_of_tomorrow.add(Calendar.DATE, 1);
        start_of_tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        start_of_tomorrow.set(Calendar.MINUTE, 0);
        start_of_tomorrow.set(Calendar.SECOND, 0);

        return date.before(start_of_tomorrow);
    }

    /**
     * Gets the string representation of the given Calendar object's calendar date
     * (in UK-standard format), eg. "21/01/2012".
     *
     * @param date the Calendar object to extract the date from.
     * @return the string representation of the given date in "dd/MM/yyyy" format.
     */
    public static String getSimpleCalendarString(Calendar date) {
        SimpleDateFormat uk_date_format = new SimpleDateFormat("dd/MM/yyyy");
        return uk_date_format.format(date.getTime());
    }

    /**
     * Given a string formatted as "dd/MM/yyyy" (such as those returned by 'getSimpleCalendarString')
     * this returns a new Calendar object of that date.  NB. The time element of the Calendar object
     * is set to the current time.
     *
     * @param date_str the string formatted as "dd/MM/yyyy" to extract a date from.
     * @return the Calendar object with the given data.
     * @throws ParseException if the string was not formatted as "dd/MM/yyyy".
     */
    public static Calendar getCalendarFromString(String date_str) throws ParseException {
        SimpleDateFormat uk_date_format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = uk_date_format.parse(date_str);
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
        return getSimpleCalendarString(first).equals(getSimpleCalendarString(second));
    }
}
