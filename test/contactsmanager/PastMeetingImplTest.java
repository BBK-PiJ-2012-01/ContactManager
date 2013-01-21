package contactsmanager;

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
    private String notes = "Initial Note ";

    @Before
    public void setUp() throws Exception {
        m = new PastMeetingImpl(1, Calendar.getInstance(), new HashSet<Contact>(), notes);
    }

    @Test
    public void testGetNotes() throws Exception {
        assertEquals(notes, m.getNotes());
    }

    @Test
    public void testEquals() throws Exception {
        PastMeeting m_copy = new PastMeetingImpl(1, Calendar.getInstance(), new HashSet<Contact>(), notes);

        assertEquals(m, m_copy);
    }

    @Test
    public void testEqualsWithWrongNotes() throws Exception {
        PastMeeting m_copy = new PastMeetingImpl(1, Calendar.getInstance(), new HashSet<Contact>(), "other notes");

        assertFalse(m.equals(m_copy));
    }

    @Test
    public void testHash() throws Exception {
        PastMeeting m_copy = new PastMeetingImpl(1, Calendar.getInstance(), new HashSet<Contact>(), notes);

        assertEquals(m.hashCode(), m_copy.hashCode());
    }

    @Test
    public void testHashWithWrongNotes() throws Exception {
        PastMeeting m_copy = new PastMeetingImpl(1, Calendar.getInstance(), new HashSet<Contact>(), "other notes");

        assertTrue(m.hashCode() != m_copy.hashCode());
    }
}
