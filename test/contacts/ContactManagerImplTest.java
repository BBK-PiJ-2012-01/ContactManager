package contacts;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Sam Wright
 * Date: 03/01/2013
 * Time: 19:12
 */
public class ContactManagerImplTest {
    private ContactManager manager;
    private Set<Contact> contacts;
    private Calendar date;

    @Before
    public void setUp() throws Exception {
        manager = new ContactManagerImpl();

        contacts = new HashSet<Contact>();
        contacts.add(new ContactImpl(1, "Alice"));
        contacts.add(new ContactImpl(2, "Bob"));
        contacts.add(new ContactImpl(3, "Charlie"));

        date = Calendar.getInstance();
        date.set(2014, 12, 23);
    }

    @Test
    public void testAddFutureMeeting() throws Exception {
        manager.addFutureMeeting(contacts, date);
    }

    @Test
    public void testGetPastMeeting() throws Exception {

    }

    @Test
    public void testGetFutureMeeting() throws Exception {

    }

    @Test
    public void testGetMeeting() throws Exception {

    }

    @Test
    public void testGetFutureMeetingList() throws Exception {

    }

    @Test
    public void testGetFutureMeetingList() throws Exception {

    }

    @Test
    public void testGetPastMeetingList() throws Exception {

    }

    @Test
    public void testAddNewPastMeeting() throws Exception {
        manager.addFutureMeeting(contacts, date);
    }

    @Test
    public void testAddMeetingNotes() throws Exception {

    }

    @Test
    public void testAddNewContact() throws Exception {

    }

    @Test
    public void testGetContacts() throws Exception {

    }

    @Test
    public void testGetContacts() throws Exception {

    }

    @Test
    public void testFlush() throws Exception {

    }
}
