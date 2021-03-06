package contactsmanager;

import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract test class for Meeting (inherited by test classes for PastMeeting and FutureMeeting).
 */
@Ignore
public abstract class MeetingTest {
    private Meeting m;
    private final int id = 3;
    private Calendar date;
    private Set<Contact> contacts;

    @Before
    public void setUp() throws Exception {
        date = Calendar.getInstance();
        date.set(1956, Calendar.DECEMBER, 23);

        contacts = new HashSet<Contact>();
        contacts.add(DIFactory.getInstance().newContact(1, "Alice"));
        contacts.add(DIFactory.getInstance().newContact(2, "Bob"));
        contacts.add(DIFactory.getInstance().newContact(3, "Charlie"));

        m = createInstance(id, date, contacts);
    }

    abstract public Meeting createInstance(int id, Calendar date, Set<Contact> contacts);

    @Test
    public void testGetId() throws Exception {
        assertEquals(id, m.getId());
    }

    @Test
    public void testGetDate() throws Exception {
        assertEquals(date, m.getDate());
    }

    @Test
    public void testDateImmutability() throws Exception {
        Calendar original_date = (Calendar) date.clone();

        // Change the original date object passed on initialisation
        // to Meeting
        date.set(2000, Calendar.JANUARY, 1);
        assertFalse(original_date.equals(date));
        assertEquals(original_date, m.getDate());
    }

    @Test
    public void testGetDateImmutability() throws Exception {
        m.getDate().set(2000, Calendar.JANUARY, 1);
        assertEquals(date, m.getDate());
    }

    @Test
    public void testGetContacts() throws Exception {
        assertEquals(contacts, m.getContacts());
    }

    @Test
    public void testGetContactImmutability() throws Exception {
        m.getContacts().add(DIFactory.getInstance().newContact(4, "Dave"));
        assertEquals(contacts, m.getContacts());
    }

    @Test
    public void testContactsImmutability() throws Exception {
        Set<Contact> original_contacts = new HashSet<Contact>(contacts);

        contacts.add(DIFactory.getInstance().newContact(4, "Dave"));
        assertFalse(original_contacts.equals(contacts));

        assertEquals(original_contacts, m.getContacts());
    }

    @Test
    public void testEquals() throws Exception {
        Meeting m_copy = createInstance(3, date, new HashSet<Contact>(contacts));

        assertEquals(m, m_copy);
    }

    @Test
    public void testEqualsWithWrongId() throws Exception {
        Meeting m_copy = createInstance(1, date, new HashSet<Contact>(contacts));

        assertFalse(m.equals(m_copy));
    }

    @Test
    public void testNotEqualsWithWrongDate() throws Exception {
        Meeting m_copy = createInstance(3, Calendar.getInstance(), new HashSet<Contact>(contacts));

        assertFalse(m.equals(m_copy));
    }

    @Test
    public void testNotEqualsWithWrongTime() throws Exception {
        Calendar date_with_different_time = (Calendar) date.clone();
        date_with_different_time.set(Calendar.HOUR_OF_DAY, 0);

        Meeting m_copy = createInstance(3, date_with_different_time, new HashSet<Contact>(contacts));

        assertFalse(m.equals(m_copy));
    }

    @Test
    public void testEqualsWithWrongContacts() throws Exception {
        Meeting m_copy = createInstance(3, date, new HashSet<Contact>());

        assertFalse(m.equals(m_copy));
    }

    @Test
    public void testHash() throws Exception {
        Meeting m_copy = createInstance(3, date, new HashSet<Contact>(contacts));

        assertEquals(m.hashCode(), m_copy.hashCode());
    }

    @Test
    public void testHashWithWrongId() throws Exception {
        Meeting m_copy = createInstance(1, date, new HashSet<Contact>(contacts));

        assertTrue(m.hashCode() != m_copy.hashCode());
    }

    @Test
    public void testHashNotEqualsWithWrongDate() throws Exception {
        Meeting m_copy = createInstance(3, Calendar.getInstance(), new HashSet<Contact>(contacts));

        assertTrue(m.hashCode() != m_copy.hashCode());
    }

    @Test
    public void testHashNotEqualsWithWrongTime() throws Exception {
        Calendar date_with_different_time = (Calendar) date.clone();
        date_with_different_time.set(Calendar.HOUR_OF_DAY, 0);

        Meeting m_copy = createInstance(3, date_with_different_time, new HashSet<Contact>(contacts));

        assertTrue(m.hashCode() != m_copy.hashCode());
    }

    @Test
    public void testHashWithWrongContacts() throws Exception {
        Meeting m_copy = createInstance(3, date, new HashSet<Contact>());

        assertTrue(m.hashCode() != m_copy.hashCode());
    }

    @Test
    public void testPastAndFutureMeetingsNotEqual() throws Exception {
        Meeting pm = DIFactory.getInstance().newPastMeeting(1, date, contacts, "notes");
        Meeting fm = DIFactory.getInstance().newFutureMeeting(1, date, contacts);

        assertFalse(pm.equals(fm));
        assertFalse(fm.equals(pm));
    }
}
