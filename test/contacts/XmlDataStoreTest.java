package contacts;

import contacts.helper.CalendarHelper;
import contacts.helper.ContactManagerHelper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;


/**
 * User: Sam Wright
 * Date: 05/01/2013
 * Time: 20:04
 */
public class XmlDataStoreTest {
    private DataStore doc;
    private Contact alice, bob, charlie;
    private FutureMeeting fm1, fm2, fm3;
    private PastMeeting pm1, pm2, pm3;
    private String filename = "/Users/eatmuchpie/Documents/XmlDataStoreTest.xml";
    private String notes1 = "Notes 1";
    private String notes2 = "Notes 2";
    private String notes3 = "Notes 3";

    @Before
    public void setUp() throws Exception {
        doc = new XmlDataStore();
        alice = new ContactImpl(1, "Alice");
        bob = new ContactImpl(2, "Bob");
        charlie = new ContactImpl(3, "Charlie");

        alice.addNotes(notes1);
        bob.addNotes(notes2);
        charlie.addNotes(notes3);

        fm1 = new FutureMeetingImpl(1, getFutureDate(1), setOf(alice, bob, charlie));
        fm2 = new FutureMeetingImpl(2, getFutureDate(2), setOf(alice, bob));
        fm3 = new FutureMeetingImpl(3, getFutureDate(3), setOf(alice));

        pm1 = new PastMeetingImpl(4, getPastDate(1), setOf(alice, bob, charlie), notes1);
        pm2 = new PastMeetingImpl(5, getPastDate(2), setOf(alice, bob), notes2);
        pm3 = new PastMeetingImpl(6, getPastDate(3), setOf(alice), notes3);
    }

    private Calendar getFutureDate(int seed) {
        Calendar date = Calendar.getInstance();
        date.set(2053, Calendar.JANUARY, seed);
        return date;
    }

    private Calendar getPastDate(int seed) {
        Calendar date = Calendar.getInstance();
        date.set(1953, Calendar.JANUARY, seed);
        return date;
    }

    private static <T> Set<T> setOf(T... contents) {
        return new HashSet<T>(Arrays.asList(contents));
    }

    @Test
    public void testSetContacts() throws Exception {
        doc.setContacts(setOf(alice, bob, charlie));
        assertEquals(setOf(alice, bob, charlie), doc.getContacts());
    }

    @Test(expected = NullPointerException.class)
    public void testSetNullContacts() throws Exception {
        doc.setContacts(null);
    }

    @Test
    public void testSetFutureMeetings() throws Exception {
        doc.setFutureMeetings(setOf(fm1, fm2, fm3));
        assertEquals(setOf(fm1, fm2, fm3), doc.getFutureMeetings());
    }

    @Test(expected = NullPointerException.class)
    public void testSetNullFutureMeetings() throws Exception {
        doc.setFutureMeetings(null);
    }

    @Test
    public void testSetPastMeetings() throws Exception {
        doc.setPastMeetings(setOf(pm1, pm2, pm3));
        assertEquals(setOf(pm1, pm2, pm3), doc.getPastMeetings());
    }

    @Test(expected = NullPointerException.class)
    public void testSetNullPastMeetings() throws Exception {
        doc.setPastMeetings(null);
    }

    @Test
    public void testSaveThenLoad() throws Exception {
        // Add data to doc
        testSetContacts();
        testSetFutureMeetings();
        testSetPastMeetings();

        // Save to file
        doc.writeToFilename(filename);

        // Recreate doc and load from filename
        doc = new XmlDataStore();
        doc.loadFromFilename(filename);
    }

    @Test
    public void testReloadContacts() throws Exception {
        testSaveThenLoad();

        // Check contacts loaded
        if (!ContactManagerHelper.areContactsSetsEqual(setOf(alice, bob, charlie), doc.getContacts()))
            throw new AssertionError("Contact sets not equal");
    }

    @Test
    public void testReloadPastMeetings() throws Exception {
        testSaveThenLoad();

        // Need to load contacts to see if meeting attendees loaded correctly


        // Check past meetings loaded
        assertMeetingsSetsEqual(setOf(pm1, pm2, pm3), doc.getPastMeetings());
    }

    @Test
    public void testReloadFutureMeetings() throws Exception {
        testSaveThenLoad();

        // Check future meetings loaded
        assertMeetingsSetsEqual(setOf(fm1, fm2, fm3), doc.getFutureMeetings());
    }

    private void assertContactsSetsEqual(Set<Contact> expected, Set<Contact> got) {
        assertEquals(expected.size(), got.size());
        for (Contact loaded_contact : got) {
            // Find the original Contact object the loaded contact should equal
            Contact saved_contact = null;
            for (Contact old_contact : expected) {
                if (old_contact.getId() == loaded_contact.getId()) {
                    saved_contact = old_contact;
                    break;
                }
            }

            // If no contact matches the saved contact's id, throw exception
            if (saved_contact == null)
                throw new RuntimeException("Contact ids did not match!");

            // Check that the loaded and saved contacts are equal
            assertEquals(saved_contact, loaded_contact);
        }
    }

    private <T extends Meeting> void assertMeetingsSetsEqual(Set<T> expected, Set<T> got) {
        assertEquals(expected.size(), got.size());
        for (Meeting loaded_meeting : got) {
            // Find the original meeting object the loaded meeting should equal
            Meeting saved_meeting = null;
            for (Meeting old_meeting : expected) {
                if (old_meeting.getId() == loaded_meeting.getId()) {
                    saved_meeting = old_meeting;
                    break;
                }
            }

            // If no meeting matches the saved meeting's id, throw exception
            if (saved_meeting == null)
                throw new RuntimeException("Meeting ids did not match!");

            // Check that the loaded and saved meetings are equal
            assertTrue(saved_meeting.equals(loaded_meeting));
        }
    }


    // TODO: test for writeToFilename and loadFromFilename exceptions...
}
