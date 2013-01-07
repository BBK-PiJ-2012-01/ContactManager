package contacts.helper;

import org.junit.Before;
import org.junit.Test;

import static contacts.helper.CalendarHelper.*;
import static org.junit.Assert.*;

import java.util.Calendar;

/**
 * User: Sam Wright
 * Date: 07/01/2013
 * Time: 19:09
 */
public class CalendarHelperTest {
    Calendar past, present, future;

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
        assertEquals("13/01/1956", getSimpleCalendarString(past));
        assertEquals("06/02/2083", getSimpleCalendarString(future));
    }

    @Test
    public void testGetCalendarFromString() throws Exception {
        Calendar extracted = getCalendarFromString("13/01/1956");
        assertEquals(1956, extracted.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, extracted.get(Calendar.MONTH));
        assertEquals(13, extracted.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testAreDatesEqual() throws Exception {
        Calendar extracted = getCalendarFromString("13/01/1956");
        assertTrue(areDatesEqual(past, extracted));
    }

    @Test
    public void testAreDatesInequal() throws Exception {
        Calendar extracted = getCalendarFromString("13/01/1956");
        assertFalse(areDatesEqual(future, extracted));
    }
}
