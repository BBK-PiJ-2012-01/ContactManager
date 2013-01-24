package contactsmanager.util;

import org.junit.Before;
import org.junit.Test;

import static contactsmanager.util.CalendarUtil.*;
import static contactsmanager.util.CollectionUtil.listOf;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import java.util.*;

/**
 * User: Sam Wright
 * Date: 07/01/2013
 * Time: 19:09
 */
public class CalendarUtilTest {
    private Calendar past, present, future;

    @Before
    public void setUp() throws Exception {
        past = Calendar.getInstance();
        past.set(1956, Calendar.JANUARY, 13);

        present = Calendar.getInstance();

        future = Calendar.getInstance();
        future.set(2083, Calendar.FEBRUARY, 6);
    }

    @Test
    public void testIsFutureInFuture() throws Exception {
        assertTrue(isDateInFuture(future));
    }

    @Test
    public void testIsPresentInFuture() throws Exception {
        assertTrue(isDateInFuture(present));
    }

    @Test
    public void testIsPastInFuture() throws Exception {
        assertFalse(isDateInFuture(past));
    }

    @Test
    public void testIsFutureInPast() throws Exception {
        assertFalse(isDateInPast(future));
    }

    @Test
    public void testIsPresentInPast() throws Exception {
        assertTrue(isDateInPast(present));
    }

    @Test
    public void testIsPastInPast() throws Exception {
        assertTrue(isDateInPast(past));
    }

    @Test
    public void testGetSimpleCalendarString() throws Exception {
        assertEquals("13/01/1956", getCalendarDateString(past));
        assertEquals("06/02/2083", getCalendarDateString(future));
    }

    @Test
    public void testGetCalendarFromString() throws Exception {
        Calendar extracted = getCalendarDateFromString("13/01/1956");
        assertEquals(1956, extracted.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, extracted.get(Calendar.MONTH));
        assertEquals(13, extracted.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testAreDatesEqual() throws Exception {
        Calendar extracted = getCalendarDateFromString("13/01/1956");
        assertTrue(areDatesEqual(past, extracted));
    }

    @Test
    public void testAreDatesUnequal() throws Exception {
        Calendar extracted = getCalendarDateFromString("13/01/1956");
        assertFalse(areDatesEqual(future, extracted));
    }

    @Test
    public void testDatesComparatorWithSort() throws Exception {
        Calendar t1 = getCalendarDateFromString("13/01/1956");
        Calendar t2 = getCalendarDateFromString("14/01/1956");
        Calendar t3 = getCalendarDateFromString("15/01/1956");

        List<Calendar> sorted_list = listOf(t3,t1,t2);
        Collections.sort(sorted_list, getDateComparator());

        assertEquals(listOf(t1, t2, t3), sorted_list);
    }

    @Test
    public void testDatesComparatorWithTreeMap() throws Exception {
        Calendar t1 = getCalendarDateFromString("13/01/1956");
        Calendar t2 = getCalendarDateFromString("14/01/1956");
        Calendar t3 = getCalendarFromString("15/01/1956 at 12:00:00.000 GMT");
        Calendar t4 = getCalendarFromString("15/01/1956 at 12:00:00.001 GMT");

        Map<Calendar, Integer> map = new TreeMap<Calendar, Integer>(getDateComparator());
        map.put(t3, 3);
        map.put(t1, 1);
        map.put(t2, 2);

        List<Integer> order = new LinkedList<Integer>(map.values());
        assertEquals(listOf(1, 2, 3), order);

        map.put(t4, 4);
        order = new LinkedList<Integer>(map.values());
        assertEquals(listOf(1, 2, 4), order);

        assertEquals((Integer) 4, map.get(t3));
        assertEquals((Integer) 4, map.get(t4));
    }

    @Test
    public void testDateAndTimeEquals() throws Exception {
        Calendar t3 = getCalendarFromString("15/01/1956 at 12:00:00.000 GMT");
        Calendar t4 = getCalendarFromString("15/01/1956 at 12:00:00.000 GMT");

        assertEquals(t3, t4);
    }

    @Test
    public void testDateAndTimeNotEquals() throws Exception {
        Calendar t3 = getCalendarFromString("15/01/1956 at 12:00:00.000 GMT");
        Calendar t4 = getCalendarFromString("15/01/1956 at 12:00:00.001 GMT");

        assertFalse(t3.equals(t4));
    }
}
