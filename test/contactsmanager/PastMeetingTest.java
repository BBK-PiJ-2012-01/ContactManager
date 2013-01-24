package contactsmanager;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: Sam Wright
 * Date: 03/01/2013
 * Time: 18:44
 */
public class PastMeetingTest extends MeetingTest {
    private PastMeeting m;
    private final String notes = "Initial Note ";
    private Calendar date;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        date = Calendar.getInstance();
        m = DIFactory.getInstance().newPastMeeting(1, date, new HashSet<Contact>(), notes);
    }

    @Override
    public Meeting createInstance(int id, Calendar date, Set<Contact> contacts) {
        return DIFactory.getInstance().newPastMeeting(id, date, contacts, "");
    }

    @Test
    public void testGetNotes() throws Exception {
        assertEquals(notes, m.getNotes());
    }

    @Test
    public void testEquals() throws Exception {
        PastMeeting m_copy = DIFactory.getInstance().newPastMeeting(1, date, new HashSet<Contact>(), notes);

        assertEquals(m, m_copy);
    }

    @Test
    public void testEqualsWithWrongNotes() throws Exception {
        PastMeeting m_copy = DIFactory.getInstance().newPastMeeting(1, date, new HashSet<Contact>(), "other notes");

        assertFalse(m.equals(m_copy));
    }

    @Test
    public void testHash() throws Exception {
        PastMeeting m_copy = DIFactory.getInstance().newPastMeeting(1, date, new HashSet<Contact>(), notes);

        assertEquals(m.hashCode(), m_copy.hashCode());
    }

    @Test
    public void testHashWithWrongNotes() throws Exception {
        PastMeeting m_copy = DIFactory.getInstance().newPastMeeting(1, date, new HashSet<Contact>(), "other notes");

        assertTrue(m.hashCode() != m_copy.hashCode());
    }
}
