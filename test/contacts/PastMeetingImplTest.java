package contacts;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * User: Sam Wright
 * Date: 03/01/2013
 * Time: 18:44
 */
public class PastMeetingImplTest {
    private PastMeeting m;
    private String notes = "Initial Note";

    @Before
    public void setUp() throws Exception {
        m = new PastMeetingImpl(1, Calendar.getInstance(), new HashSet<Contact>(), notes);
    }

    @Test
    public void testGetNotes() throws Exception {
        assertEquals(notes, m.getNotes());
    }
}
