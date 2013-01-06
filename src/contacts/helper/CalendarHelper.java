package contacts.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * User: Sam Wright
 * Date: 05/01/2013
 * Time: 22:49
 */
public class CalendarHelper {

    public static boolean isDateInFuture(Calendar date) {
        Calendar start_of_today = Calendar.getInstance();
        start_of_today.set(Calendar.HOUR_OF_DAY, 0);
        start_of_today.set(Calendar.MINUTE, 0);
        start_of_today.set(Calendar.SECOND, 0);
        if (date.before(start_of_today) && !date.equals(start_of_today))
            return false;
        return true;
    }

    public static boolean isDateInPast(Calendar date) {
        Calendar end_of_today = Calendar.getInstance();
        end_of_today.set(Calendar.HOUR_OF_DAY, 23);
        end_of_today.set(Calendar.MINUTE, 59);
        end_of_today.set(Calendar.SECOND, 59);
        if (date.after(end_of_today))
            return false;
        return true;
    }

    public static String getSimpleCalendarString(Calendar date) {
        SimpleDateFormat uk_date_format = new SimpleDateFormat("dd/MM/yyyy");
        return uk_date_format.format(date.getTime());
    }

    public static Calendar getCalendarFromString(String date_str) throws ParseException {
        SimpleDateFormat uk_date_format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = uk_date_format.parse(date_str);
        Calendar calendar_date = Calendar.getInstance();
        calendar_date.setTime(date);
        return calendar_date;
    }

    public static boolean areDatesAlmostEqual(Calendar expected, Calendar got) {
        int difference = expected.compareTo(got);
        if (Math.abs(difference) > 1000)
            return false;
        return true;
    }
}
