package contactsmanager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static contactsmanager.util.CollectionUtil.setOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import java.io.File;
import java.io.IOException;
import java.util.Calendar;


/**
 * Test class for DataStore
 */
public class DataStoreTest {
    private DataStore doc;
    private Contact alice, bob, charlie;
    private FutureMeeting fm1, fm2, fm3;
    private PastMeeting pm1, pm2, pm3;
    private final String filename = "DataStoreTest_output.xml";

    @Before
    public void setUp() throws Exception {
        doc = DIFactory.getInstance().newDataStore();

        alice = new ContactImpl(1, "Alice");
        bob = new ContactImpl(2, "Bob");
        charlie = new ContactImpl(3, "Charlie");

        alice.addNotes("Note 1");
        bob.addNotes("Note 2");
        charlie.addNotes("Note 3");

        fm1 = DIFactory.getInstance().newFutureMeeting(1, getFutureDate(1), setOf(alice, bob, charlie));
        fm2 = DIFactory.getInstance().newFutureMeeting(2, getFutureDate(2), setOf(alice, bob));
        fm3 = DIFactory.getInstance().newFutureMeeting(3, getFutureDate(3), setOf(alice));

        pm1 = DIFactory.getInstance().newPastMeeting(4, getPastDate(1), setOf(alice, bob, charlie), "Meeting note 1");
        pm2 = DIFactory.getInstance().newPastMeeting(5, getPastDate(2), setOf(alice, bob), "Meeting note 2");
        pm3 = DIFactory.getInstance().newPastMeeting(6, getPastDate(3), setOf(alice), "Meeting note 3");
    }

    /**
     * Gets a Calendar object set in the future, on the given date of the month.
     *
     * @param date_of_month the date of the month the to set the Calendar object to.
     * @return the Calendar object set in the future on the specified date of the month.
     */
    private Calendar getFutureDate(int date_of_month) {
        Calendar date = Calendar.getInstance();
        date.set(2053, Calendar.JANUARY, date_of_month);
        return date;
    }

    /**
     * Gets a Calendar object set in the past, on the given date of the month.
     *
     * @param date_of_month the date of the month the to set the Calendar object to.
     * @return the Calendar object set in the past on the specified date of the month.
     */
    private Calendar getPastDate(int date_of_month) {
        Calendar date = Calendar.getInstance();
        date.set(1953, Calendar.JANUARY, date_of_month);
        return date;
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
        doc = DIFactory.getInstance().newDataStore();
        doc.loadFromFilename(filename);
    }

    @Test
    public void testReloadContacts() throws Exception {
        testSaveThenLoad();

        // Check contacts loaded
        assertEquals(setOf(alice, bob, charlie), doc.getContacts());
    }

    @Test
    public void testReloadPastMeetings() throws Exception {
        testSaveThenLoad();

        // Need to load contacts to see if meeting attendees loaded correctly


        // Check past meetings loaded
        assertEquals(setOf(pm1, pm2, pm3), doc.getPastMeetings());
        assertEquals(setOf(pm1, pm2, pm3), doc.getPastMeetings());

    }

    @Test
    public void testReloadFutureMeetings() throws Exception {
        testSaveThenLoad();

        // Check future meetings loaded
        assertEquals(setOf(fm1, fm2, fm3), doc.getFutureMeetings());
    }

    @Test(expected = IOException.class)
    public void testWriteToBadFilename() throws Exception {
        doc.writeToFilename("\\/\\filename");
    }

    @Test
    public void testInjectionSafety() throws Exception {
        String injection_str = "Jane/>&\"\\\"";
        Contact injection_jane = new ContactImpl(5, injection_str);
        injection_jane.addNotes("notes");

        doc.setContacts(setOf(injection_jane));

        doc.writeToFilename(filename);
        doc = DIFactory.getInstance().newDataStore();
        doc.loadFromFilename(filename);

        Contact loaded_injection_jane = (Contact) doc.getContacts().toArray()[0];

        assertEquals(injection_jane, loaded_injection_jane);
    }

    @Test
    public void testEmptyStringSaving() throws Exception {
        Contact nemo = new ContactImpl(5, "");

        doc.setContacts(setOf(nemo));

        doc.writeToFilename(filename);
        doc = DIFactory.getInstance().newDataStore();
        doc.loadFromFilename(filename);

        Contact loaded_nemo = (Contact) doc.getContacts().toArray()[0];

        assertEquals(nemo, loaded_nemo);
    }


    @Test
    public void testSetTechnique() throws Exception {
        assertTrue(setOf(pm1, pm2, pm3).containsAll(doc.getPastMeetings()));
    }

    @After
    public void cleanUp() {
        File file = new File(filename);
        if (file.exists()) {
            assertTrue(file.delete());
        }
    }

}
