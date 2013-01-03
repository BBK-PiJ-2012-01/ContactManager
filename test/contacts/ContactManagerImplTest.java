package contacts;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.*;


/**
 * User: Sam Wright
 * Date: 03/01/2013
 * Time: 19:12
 */
public class ContactManagerImplTest {
    private ContactManager manager;
    private Set<Contact> contacts;
    private Calendar date;
    private String note = "Note";
    private int meeting_id;
    private Contact alice, bob, charlie;

    @Before
    public void setUp() throws Exception {
        resetManager();

        date = Calendar.getInstance();
        setDateInPast();

        meeting_id = -1;
    }

    private void resetManager() {
        manager = new ContactManagerImpl();

        contacts = new HashSet<Contact>();
        contacts.add(alice = new ContactImpl(1, "Alice"));
        contacts.add(bob = new ContactImpl(2, "Bob"));
        contacts.add(charlie = new ContactImpl(3, "Charlie"));

        for (Contact c : contacts) {
            manager.addNewContact(c.getName(), c.getNotes());
        }
    }

    private void setDateInPast() {
        date.set(2000, Calendar.APRIL, 13);
    }

    private void setDateInFuture() {
        date.set(2014, Calendar.DECEMBER, 23);
    }

    @Test
    public void testAddNewPastMeeting() throws Exception {
        setDateInPast();
        manager.addNewPastMeeting(contacts, date, note);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNewPastMeetingInFuture() throws Exception {
        setDateInFuture();
        manager.addNewPastMeeting(contacts, date, note);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNewPastMeetingWithStranger() throws Exception {
        setDateInPast();
        contacts.add(new ContactImpl(4, "Stranger"));
        manager.addNewPastMeeting(contacts, date, note);
    }

    @Test
    public void testAddFutureMeeting() throws Exception {
        setDateInFuture();
        manager.addFutureMeeting(contacts, date);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddFutureMeetingInPast() throws Exception {
        setDateInPast();
        manager.addFutureMeeting(contacts, date);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddFutureMeetingWithStranger() throws Exception {
        setDateInFuture();
        contacts.add(new ContactImpl(4, "Stranger"));
        manager.addFutureMeeting(contacts, date);
    }

    @Test
    public void testGetPastMeeting() throws Exception {
        meeting_id = manager.addFutureMeeting(contacts, date);
        manager.addMeetingNotes(meeting_id, note);
        PastMeeting meeting = manager.getPastMeeting(meeting_id);

        checkMeeting(meeting);
        assertEquals(note, meeting.getNotes());

        // Check that getFutureMeeting fails
        try {
            manager.getFutureMeeting(meeting_id);
        } catch (IllegalArgumentException err) {
            System.out.println("Adding a past meeting doesn't add a future meeting.  Good.");
            return;
        }

        throw new Exception("Adding a past meeting added a future meeting!!!");
    }

    // TODO: add multiple meetings and check for duplicate ids

    @Test
    public void testGetFutureMeeting() throws Exception {
        int id = manager.addFutureMeeting(contacts, date);
        FutureMeeting meeting = manager.getFutureMeeting(id);

        checkMeeting(meeting);

        // Check that getPastMeeting fails
        try {
            manager.getPastMeeting(meeting_id);
        } catch (IllegalArgumentException err) {
            System.out.println("Adding a future meeting doesn't add a past meeting.  Good.");
            return;
        }

        throw new Exception("Adding a future meeting added a past meeting!!!");
    }

    private void checkMeeting(Meeting meeting) throws Exception {
        assertEquals(contacts, meeting.getContacts());
        assertEquals(date, meeting.getDate());
        if (meeting_id != -1)
            assertEquals(meeting_id, meeting.getId());
        else
            throw new Exception("Shouldn't have been given meeting_id of -1 ...");
    }

    @Test
    public void testGetMeeting() throws Exception {
        // Check that getPastMeeting and getMeeting are equivalent
        // for a past meeting
        testGetPastMeeting();
        checkMeeting(manager.getMeeting(meeting_id));

        // Make arbitrary changes to contacts and date
        // so the next meeting will be different.
        date.set(2015, Calendar.APRIL, 18);
        contacts.add(new ContactImpl(4, "Dave"));

        // Check that getFutureMeeting and getMeeting are equivalent
        // for a future meeting
        testGetFutureMeeting();
        checkMeeting(manager.getMeeting(meeting_id));
    }

    @Test
    public void testMeetingIdUniqueness() throws Exception {
        Set<Integer> meeting_ids = new HashSet<Integer>();

        for (int i = 0; i < 1000; ++i) {
            // Randomly add future and past meetings

            if (Math.abs(Math.random()) > 0.5) {
                testGetFutureMeeting();
            } else {
                testGetPastMeeting();
            }

            // Make sure the new meeting's id has not already been used
            if (meeting_ids.contains(meeting_id)) {
                throw new Exception(String.format("Meeting id '%d' already used", meeting_id));
            }

            // Add the meeting_id to our set of used ids
            meeting_ids.add(meeting_id);
        }
    }

    @Test
    public void testGetFutureMeetingListPerContact() throws Exception {
        setDateInFuture();
        int id1 = manager.addFutureMeeting(setOf(alice, bob, charlie), date);
        int id2 = manager.addFutureMeeting(setOf(alice, bob), date);
        int id3 = manager.addFutureMeeting(setOf(alice), date);

        checkMeetingsList(setOf(id1, id2, id3), manager.getFutureMeetingList(alice));
        checkMeetingsList(setOf(id1, id2), manager.getFutureMeetingList(bob));
        checkMeetingsList(setOf(id1), manager.getFutureMeetingList(charlie));
    }

    @Test
    public void testGetPastMeetingListPerContact() throws Exception {
        setDateInFuture();
        int id1 = manager.addFutureMeeting(setOf(alice, bob, charlie), date);
        int id2 = manager.addFutureMeeting(setOf(alice, bob), date);
        int id3 = manager.addFutureMeeting(setOf(alice), date);

        // Convert the meetings into past meetings
        for (int id : setOf(id1, id2, id3)) {
            manager.addMeetingNotes(id, note);
        }

        checkMeetingsList(setOf(id1, id2, id3), manager.getFutureMeetingList(alice));
        checkMeetingsList(setOf(id1, id2), manager.getFutureMeetingList(bob));
        checkMeetingsList(setOf(id1), manager.getFutureMeetingList(charlie));
    }

    @Test
    public void testMeetingListsPerContactAreUnique() throws Exception {
        testGetPastMeetingListPerContact();
        testGetFutureMeetingListPerContact();

        resetManager();

        testGetFutureMeetingListPerContact();
        testGetPastMeetingListPerContact();
    }

    private static <T> Set<T> setOf(T... contents) {
        return new HashSet<T>(Arrays.asList(contents));
    }

    private void checkMeetingsList(Set<Integer> expected_ids, List<Meeting> meetings) {
        Set<Integer> meeting_ids = new HashSet<Integer>();
        for (Meeting m : meetings) {
            meeting_ids.add(m.getId());
        }

        assertEquals(expected_ids, meeting_ids);
    }

    @Test
    public void testGetFutureMeetingListPerDate() throws Exception {

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
