package contactsmanager;

import contactsmanager.util.CalendarUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static contactsmanager.util.CollectionUtil.setOf;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * User: Sam Wright
 * Date: 03/01/2013
 * Time: 19:12
 */
public class ContactManagerTest {
    private ContactManager manager;
    private Set<Contact> contacts;
    private Calendar date;
    private final String note = "Note";
    private int meeting_id;
    private Contact alice, bob, charlie, dave;
    private final int ALICE_ID = 0, BOB_ID = 1, CHARLIE_ID = 2;
    private final String filename = "ContactManagerTest_output.xml";
    private final String default_filename = "contacts.txt";
    private final int MILLISECONDS_FOR_FUTURE_TO_BECOME_PAST = 50;

    @Before
    public void setUp() throws Exception {
        cleanUp();
        resetManager();

        dave = DIFactory.getInstance().newContact(4, "Dave");

        meeting_id = -1;
    }

    /**
     * Resets the manager to containing alice, bob and charlie, and no meetings.
     */
    private void resetManager() {
        manager = DIFactory.getInstance().newContactManager(filename);

        // Add contacts: alice, bob, charlie, to manager
        alice = addThenReturnContact("Alice", "Note A");
        bob = addThenReturnContact("Bob", "Note B");
        charlie = addThenReturnContact("Charlie", "Note C");

        // And also to 'contacts'
        contacts = setOf(alice, bob, charlie);
    }

    /**
     * Adds a contact to the manager, then returns the Contact object.
     *
     * @param name the name of the new contact to create.
     * @param note the note to add to the new contact.
     * @return the newly-created contact object that's registered with the manager.
     */
    private Contact addThenReturnContact(String name, String note) {
        // Check there isn't a similarly-named contact already
        assertTrue(manager.getContacts(name).isEmpty());

        // Add contact to manager
        manager.addNewContact(name, note);

        // Extract Contact object from manager
        return (Contact) manager.getContacts(name).toArray()[0];
    }

    /**
     * Sets date to the past.
     */
    private void setDateInPast() {
        date = Calendar.getInstance();
        date.add(Calendar.HOUR_OF_DAY, -1);
    }

    /**
     * Sets 'date' to the future.
     */
    private void setDateInFuture() {
        date = Calendar.getInstance();
        date.add(Calendar.HOUR_OF_DAY, +1);
    }

    /**
     * By setting the date to today, it is both in the future and in the past.
     */
    private void setDateToNow() {
        date = Calendar.getInstance();
    }

    /**
     * Checks that the given meetings have the given ids, and are sorted chronologically.
     *
     * @param expected_ids the meeting ids expected.
     * @param meetings the meetings to check.
     */
    private void checkMeetingsList(Set<Integer> expected_ids, List<? extends Meeting> meetings) {
        // Checks that the returned meetings match the given ids
        Set<Integer> meeting_ids = new HashSet<Integer>();
        for (Meeting m : meetings) {
            meeting_ids.add(m.getId());
        }
        assertEquals(expected_ids, meeting_ids);

        // Checks there were no duplicates in the given list
        assertEquals(meeting_ids.size(), meetings.size());

        // Checks the given meetings list is in chronological order
        // (ie. earliest-first)
        Meeting previous_meeting = null;
        for (Meeting next_meeting : meetings) {
            if (previous_meeting != null
                    && next_meeting.getDate().before(previous_meeting.getDate())) {
                throw new AssertionError("Meetings were not in chronological order");
            }

            previous_meeting = next_meeting;
        }
    }

    /**
     * Checks that the given meeting's contacts are equal to 'contacts'
     * and its date is equal to 'date', and its meeting_id is equal to
     * 'meeting_id'.
     *
     * @param meeting the meeting to check.
     * @throws Exception if the meeting_id to check against hasn't been set (ie. is -1)
     */
    private void checkMeeting(Meeting meeting) throws Exception {
        assertNotNull(meeting);

        assertEquals(contacts, meeting.getContacts());
        assertEquals(date, meeting.getDate());

        if (meeting_id != -1)
            assertEquals(meeting_id, meeting.getId());
        else
            throw new Exception("Shouldn't have been given meeting_id of -1 ...");
    }

    @Test
    public void testAddNewPastMeeting() throws Exception {
        setDateInPast();
        manager.addNewPastMeeting(contacts, date, note);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPastMeetingInFuture() throws Exception {
        setDateInFuture();
        manager.addNewPastMeeting(contacts, date, note);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNewPastMeetingWithNobody() throws Exception {
        setDateInPast();
        manager.addNewPastMeeting(new HashSet<Contact>(), date, note);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNewPastMeetingWithStranger() throws Exception {
        setDateInPast();
        contacts.add(DIFactory.getInstance().newContact(4, "Stranger"));
        manager.addNewPastMeeting(contacts, date, note);
    }

    @Test(expected = NullPointerException.class)
    public void testAddNewPastMeetingWithNullContacts() throws Exception {
        setDateInPast();
        manager.addNewPastMeeting(null, date, note);
    }

    @Test(expected = NullPointerException.class)
    public void testAddNewPastMeetingWithNullDate() throws Exception {
        setDateInPast();
        manager.addNewPastMeeting(contacts, null, note);
    }

    @Test(expected = NullPointerException.class)
    public void testAddNewPastMeetingWithNullNote() throws Exception {
        setDateInPast();
        manager.addNewPastMeeting(contacts, date, null);
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
        contacts.add(DIFactory.getInstance().newContact(4, "Stranger"));
        manager.addFutureMeeting(contacts, date);
    }

    @Test(expected = NullPointerException.class)
    public void testAddFutureMeetingWithNullContacts() throws Exception {
        manager.addFutureMeeting(null, date);
    }

    @Test(expected = NullPointerException.class)
    public void testAddFutureMeetingWithNullDate() throws Exception {
        manager.addFutureMeeting(contacts, null);
    }

    /**
     * Adds a PastMeeting to the contact manager and returns the meeting's id.
     *
     * The tricky part that this method has to work around is that the only reliable
     * way of getting the id of a newly added PastMeeting is to add it as a FutureMeeting
     * (which gives the meeting's id) set to a time 300ms in the future, then wait 300ms
     * and convert it to a PastMeeting.
     *
     * @param contacts the contacts to add to the meeting.
     * @param note the notes to add to the meeting.
     * @return the id of the newly-added PastMeeting
     * @throws Exception if interrupted.
     */
    private int addPastMeeting(Set<Contact> contacts, String note) throws Exception {
        setDateToNow();
        date.add(Calendar.MILLISECOND, MILLISECONDS_FOR_FUTURE_TO_BECOME_PAST);
        int meeting_id = manager.addFutureMeeting(contacts, date);
        Thread.sleep(MILLISECONDS_FOR_FUTURE_TO_BECOME_PAST * 2);
        manager.addMeetingNotes(meeting_id, note);
        return meeting_id;
    }

    @Test
    public void testGetPastMeeting() throws Exception {
        setDateToNow();
        meeting_id = addPastMeeting(contacts, note);
        PastMeeting meeting = manager.getPastMeeting(meeting_id);

        checkMeeting(meeting);

        // Check that getFutureMeeting fails
        try {
            manager.getFutureMeeting(meeting_id);
        } catch (IllegalArgumentException err) {
            //System.out.println("Adding a past meeting doesn't add a future meeting.  Good.");
            return;
        }

        throw new Exception("Was able to get a past meeting with getFutureMeeting");
    }

    @Test
    public void testGetNonexistentPastMeeting() throws Exception {
        assertNull(manager.getPastMeeting(-99));
    }

    @Test
    public void testGetFutureMeeting() throws Exception {
        setDateInFuture();
        meeting_id = manager.addFutureMeeting(contacts, date);
        FutureMeeting meeting = manager.getFutureMeeting(meeting_id);

        checkMeeting(meeting);

        // Check that getPastMeeting fails
        try {
            manager.getPastMeeting(meeting_id);
        } catch (IllegalArgumentException err) {
            //System.out.println("Adding a future meeting doesn't add a past meeting.  Good.");
            return;
        }

        throw new Exception("Was able to get a future meeting with getPastMeeting");
    }

    @Test
    public void testGetNonexistentFutureMeeting() throws Exception {
        assertNull(manager.getFutureMeeting(-99));
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
        contacts.add(addThenReturnContact("Dave", note));

        // Check that getFutureMeeting and getMeeting are equivalent
        // for a future meeting
        testGetFutureMeeting();
        checkMeeting(manager.getMeeting(meeting_id));
    }

    @Test
    public void testGetNonexistentMeeting() throws Exception {
        assertNull(manager.getMeeting(-99));
    }

    @Test
    public void testMeetingIdUniqueness() throws Exception {
        Set<Integer> meeting_ids = new HashSet<Integer>();

        for (int i = 0; i < 100; ++i) {
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
        setDateToNow();
        int id1 = addPastMeeting(setOf(alice, bob, charlie), note);
        int id2 = addPastMeeting(setOf(alice, bob), note);
        int id3 = addPastMeeting(setOf(alice), note);

        checkMeetingsList(setOf(id1, id2, id3), manager.getPastMeetingList(alice));
        checkMeetingsList(setOf(id1, id2), manager.getPastMeetingList(bob));
        checkMeetingsList(setOf(id1), manager.getPastMeetingList(charlie));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFutureMeetingListOfStranger() throws Exception {
        manager.getFutureMeetingList(dave);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPastMeetingListOfStranger() throws Exception {
        manager.getPastMeetingList(dave);
    }

    @Test()
    public void testGetFutureMeetingListOfInactive() throws Exception {
        dave = addThenReturnContact("Dave", note);
        assertTrue(manager.getFutureMeetingList(dave).isEmpty());
    }

    @Test()
    public void testGetPastMeetingListOfInactive() throws Exception {
        dave = addThenReturnContact("Dave", note);
        assertTrue(manager.getPastMeetingList(dave).isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void testGetFutureMeetingListOfNull() throws Exception {
        manager.getFutureMeetingList((Contact) null);
    }

    @Test(expected = NullPointerException.class)
    public void testGetPastMeetingListOfNull() throws Exception {
        manager.getPastMeetingList(null);
    }

    @Test
    public void testMeetingListsPerContactAreUnique() throws Exception {
        testGetPastMeetingListPerContact();
        testGetFutureMeetingListPerContact();

        resetManager();

        testGetFutureMeetingListPerContact();
        testGetPastMeetingListPerContact();
    }

    @Test
    public void testGetFutureMeetingListPerDate() throws Exception {
        Calendar past = CalendarUtil.getCalendarDateFromString("02/01/1953");
        past.set(Calendar.HOUR_OF_DAY, 12);

        Calendar present = Calendar.getInstance();
        present.add(Calendar.MINUTE, 1);

        Calendar future = CalendarUtil.getCalendarDateFromString("03/01/2153");
        future.set(Calendar.HOUR_OF_DAY, 12);

        Calendar non_working_day = CalendarUtil.getCalendarDateFromString("05/05/1932");

        int id1 = manager.addFutureMeeting(contacts, future);
        future.add(Calendar.HOUR_OF_DAY, 1);
        int id2 = manager.addFutureMeeting(contacts, future);

        int id3 = manager.addFutureMeeting(contacts, present);
        int id4 = addPastMeeting(contacts, note);

        Contact earl = addThenReturnContact("Earl", note);
        Contact fred = addThenReturnContact("Fred", note);

        manager.addNewPastMeeting(setOf(earl), past, note);
        past.add(Calendar.HOUR_OF_DAY, 1);
        manager.addNewPastMeeting(setOf(fred), past, note);

        int id5 = manager.getPastMeetingList(earl).get(0).getId();
        int id6 = manager.getPastMeetingList(fred).get(0).getId();

        checkMeetingsList(setOf(id1, id2), manager.getFutureMeetingList(future));
        checkMeetingsList(setOf(id3, id4), manager.getFutureMeetingList(present));
        checkMeetingsList(setOf(id5, id6), manager.getFutureMeetingList(past));
        assertTrue(manager.getFutureMeetingList(non_working_day).isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void testGetFutureMeetingListOfNullDate() throws Exception {
        manager.getFutureMeetingList((Calendar) null);
    }

    @Test
    public void testAddMeetingNotesToFutureMeeting() throws Exception {
        setDateToNow();
        date.add(Calendar.MILLISECOND, MILLISECONDS_FOR_FUTURE_TO_BECOME_PAST);
        meeting_id = manager.addFutureMeeting(contacts, date);
        Thread.sleep(MILLISECONDS_FOR_FUTURE_TO_BECOME_PAST * 2);
        manager.addMeetingNotes(meeting_id, note);

        // Check that meeting is now in the past
        assertEquals(note, manager.getPastMeeting(meeting_id).getNotes());

        // Check that meeting is no longer in the future
        try {
            manager.getFutureMeeting(meeting_id);
        } catch (IllegalArgumentException err) {
            //System.out.println("Adding a note to future meeting made it a past meeting.  Good.");
            return;
        }

        throw new Exception("After adding a note to a future meeting, it remained a future meeting!!");
    }

    @Test
    public void testAddMeetingNotesWithWhitespace() throws Exception {
        meeting_id = addPastMeeting(contacts, "\t \nHello\n ");
        assertEquals("Hello", manager.getPastMeeting(meeting_id).getNotes());
    }

    @Test
    public void testAddMeetingNotesToPastMeeting() throws Exception {
        setDateToNow();
        meeting_id = addPastMeeting(contacts, "\t \n  Note One.");
        manager.addMeetingNotes(meeting_id, "\n Note Two. ");

        assertEquals("Note One.\nNote Two.", manager.getPastMeeting(meeting_id).getNotes());
    }

    @Test(expected = IllegalStateException.class)
    public void testAddMeetingNotesToMeetingNotHappenedYet() throws Exception {
        setDateInFuture();
        meeting_id = manager.addFutureMeeting(contacts, date);
        manager.addMeetingNotes(meeting_id, note);
    }

    @Test(expected = NullPointerException.class)
    public void testAddMeetingNotesWithNullNotes() throws Exception {
        addPastMeeting(contacts, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddMeetingNotesToNonexistentMeeting() throws Exception {
        manager.addMeetingNotes(-99, note);
    }

    @Test
    public void testAddNewContact() throws Exception {
        manager = DIFactory.getInstance().newContactManager(filename);
        manager.addNewContact("Alice", note);
    }

    @Test(expected = NullPointerException.class)
    public void testAddNullContact() throws Exception {
        manager.addNewContact(null, note);
    }

    @Test(expected = NullPointerException.class)
    public void testAddContactWithNullNote() throws Exception {
        manager.addNewContact("Alice", null);
    }

    @Test
    public void testGetContactsByName() throws Exception {
        assertEquals(setOf(alice), manager.getContacts("Alice"));
        assertEquals(setOf(bob), manager.getContacts("Bob"));
        assertEquals(setOf(charlie), manager.getContacts("Charlie"));

        // Both Alice and Charlie contain the string "li"
        assertEquals(setOf(alice, charlie), manager.getContacts("li"));
    }

    @Test(expected = NullPointerException.class)
    public void testGetContactsWithNullName() throws Exception {
        String null_name = null;
        manager.getContacts(null_name);
    }

    @Test
    public void testGetContactsBySingleId() throws Exception {
        // Check manager gives the correct Contact object for the given id
        assertEquals(setOf(alice), manager.getContacts(ALICE_ID));
        assertEquals(setOf(bob), manager.getContacts(BOB_ID));
        assertEquals(setOf(charlie), manager.getContacts(CHARLIE_ID));

        // Check id of Contact object matches expected id
        assertEquals(ALICE_ID, alice.getId());
        assertEquals(BOB_ID, bob.getId());
        assertEquals(CHARLIE_ID, charlie.getId());
    }

    @Test
    public void testGetContactsByMultipleIds() throws Exception {
        assertEquals(setOf(alice, bob), manager.getContacts(ALICE_ID, BOB_ID));
        assertEquals(setOf(bob, charlie), manager.getContacts(BOB_ID, CHARLIE_ID));
        assertEquals(setOf(charlie, alice), manager.getContacts(CHARLIE_ID, ALICE_ID));

        assertEquals(setOf(alice, bob, charlie), manager.getContacts(ALICE_ID, BOB_ID, CHARLIE_ID));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetContactsByBadId() throws Exception {
        manager.getContacts(-99);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetContactsByBadIdAmongstGood() throws Exception {
        manager.getContacts(ALICE_ID, -99);
    }

    @Test
    public void testFlushMeetings() throws Exception {
        // Create meetings (contacts already added)
        setDateInFuture();
        int future_meeting_id = manager.addFutureMeeting(setOf(alice, bob), date);
        FutureMeeting expected_future_meeting = manager.getFutureMeeting(future_meeting_id);

        int past_meeting_id = addPastMeeting(setOf(alice, charlie), note);
        PastMeeting expected_past_meeting = manager.getPastMeeting(past_meeting_id);

        // Save to file
        manager.flush();

        // Recreate manager (from file)
        manager = DIFactory.getInstance().newContactManager(filename);

        // Get flushed meetings
        FutureMeeting future_meeting = manager.getFutureMeeting(future_meeting_id);
        PastMeeting past_meeting = manager.getPastMeeting(past_meeting_id);

        assertTrue(future_meeting.equals(expected_future_meeting));

        assertEquals(expected_future_meeting, future_meeting);
        assertEquals(expected_past_meeting, past_meeting);
    }

    @Test
    public void testFlushUsers() throws Exception {
        // Save to file (contacts already in manager)
        manager.flush();

        // Recreate manager (from file)
        manager = DIFactory.getInstance().newContactManager(filename);

        // Check loaded contacts have same information as previous contacts
        testGetContactsBySingleId();
    }

    @Test
    public void testLoadThenAddContact() throws Exception {
        testFlushUsers();
        testFlushMeetings();

        // Add harold and check he's there
        Contact harold = addThenReturnContact("Harold", "note");

        // Check alice, bob, and charlie are still there
        testGetContactsByName();

        // Check alice, bob and charlie ids are as expected
        testGetContactsBySingleId();

        // Check harold's id is correct (ie. no id has been skipped or used twice)
        assertEquals(setOf(harold), manager.getContacts(3));
        assertEquals(3, harold.getId());
    }

    @Test
    public void testLoadThenAddMeeting() throws Exception {
        // Add a meeting
        setDateInFuture();
        int old_meeting_id = manager.addFutureMeeting(setOf(alice), date);
        Meeting old_meeting = manager.getMeeting(old_meeting_id);

        // Reload manager
        manager.flush();
        manager = DIFactory.getInstance().newContactManager(filename);

        // Add another meeting
        setDateInFuture();
        int new_meeting_id = manager.addFutureMeeting(setOf(alice, bob), date);

        // Load the old meeting
        Meeting loaded_old_meeting = manager.getMeeting(old_meeting_id);

        // Check that the new meeting's id is one more than the old meeting's id.
        assertEquals(old_meeting_id + 1, new_meeting_id);

        // Check the old meeting was properly loaded
        assertEquals(old_meeting, loaded_old_meeting);
    }

    @Test
    public void testLoadBadMeetings() throws Exception {
        DataStore bad_store = new XmlDataStore();

        // Set contacts of data store to not include bob or charlie
        bad_store.setContacts(setOf(alice));

        // Add future meeting with contacts: alice, bob, charlie
        testGetFutureMeeting();
        FutureMeeting bad_future_meeting = manager.getFutureMeeting(meeting_id);

        // Add past meeting with contacts: alice, bob, charlie
        testGetPastMeeting();
        PastMeeting bad_past_meeting = manager.getPastMeeting(meeting_id);

        // Set of contacts to add to meetings now just includes alice
        contacts = setOf(alice);

        // Add future meeting with contacts: alice
        testGetFutureMeeting();
        FutureMeeting good_future_meeting = manager.getFutureMeeting(meeting_id);

        // Add past meeting with contacts: alice
        testGetPastMeeting();
        PastMeeting good_past_meeting = manager.getPastMeeting(meeting_id);

        // Put meetings into data_store and write to file
        bad_store.setFutureMeetings(setOf(bad_future_meeting, good_future_meeting));
        bad_store.setPastMeetings(setOf(bad_past_meeting, good_past_meeting));
        bad_store.writeToFilename(filename);

        // Load from file into new manager object
        manager = DIFactory.getInstance().newContactManager(filename);

        System.out.println("expected:" + setOf(good_future_meeting));
        System.out.println("got:" + manager.getFutureMeetingList(alice));

        // Check only the good future meeting was loaded
        assertEquals(1, manager.getFutureMeetingList(alice).size());
        assertEquals(good_future_meeting, manager.getFutureMeetingList(alice).get(0));

        // Check only the good past meeting was loaded
        assertEquals(1, manager.getPastMeetingList(alice).size());
        assertEquals(good_past_meeting, manager.getPastMeetingList(alice).get(0));
    }

    @Test
    public void testDefaultSaveFilename() throws Exception {
        // Create manager without specifying filename
        manager = DIFactory.getInstance().newContactManager();
        Contact andy = addThenReturnContact("Andy", "completely unique note...");

        // Flush
        manager.flush();

        // Check file at default_filename exists
        File output_file = new File(default_filename);
        assertTrue(output_file.exists() && output_file.isFile());

        // Load from default filename
        manager = DIFactory.getInstance().newContactManager(default_filename);

        // Check andy was loaded
        assertEquals(setOf(andy), manager.getContacts(andy.getId()));
    }

    @Test
    public void testDefaultLoadFilename() throws Exception {
        // Save to default filename
        manager = DIFactory.getInstance().newContactManager(default_filename);
        Contact andy = addThenReturnContact("Andy", "completely unique note...");

        // Flush
        manager.flush();

        // Load (without specifying filename)
        manager = DIFactory.getInstance().newContactManager();

        // Check andy was loaded
        assertEquals(setOf(andy), manager.getContacts(andy.getId()));
    }

    @Test()
    public void testLoadFromNonexistentFile() throws Exception {
        manager = DIFactory.getInstance().newContactManager("does_not_exist.txt");
    }

    @Test()
    public void testLoadFromBadFile() throws Exception {
        FileWriter out = new FileWriter(filename);
        out.write("this should make no sense to the manager:\\}[';:_-+=");
        out.close();

        manager = DIFactory.getInstance().newContactManager(filename);
    }

    @Test()
    public void testSaveToBadFilename() throws Exception {
        String bad_filename = "nonexistent_folder/file.txt";
        manager = DIFactory.getInstance().newContactManager(bad_filename);

        // Interface prohibits throwing an exception if saving fails.
        // Instead it should fail silently
        File file = new File(bad_filename);
        assertFalse(file.exists());
    }


    @After
    public void cleanUp() {
        File file = new File(filename);
        if (file.exists()) {
            assertTrue(file.delete());
        }
        file = new File(default_filename);
        if (file.exists()) {
            assertTrue(file.delete());
        }
    }
}
