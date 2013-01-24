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
 * User: Sam Wright
 * Date: 05/01/2013
 * Time: 20:04
 */
public class DataStoreTest {
    private DataStore doc;
    private Contact alice, bob, charlie;
    private FutureMeeting fm1, fm2, fm3;
    private PastMeeting pm1, pm2, pm3;
    private final String filename = "XmlDataStoreTest_output.xml";
    private final String notes1 = "Notes 1";
    private final String notes2 = "Notes 2";
    private final String notes3 = "Notes 3";
    private final String xml_file_dir = "test" + File.separator +
                                    "contactsmanager" + File.separator +
                                    "xml_test_files" + File.separator;

    @Before
    public void setUp() throws Exception {
        doc = new XmlDataStore();

        alice = new ContactImpl(1, "Alice");
        bob = new ContactImpl(2, "Bob");
        charlie = new ContactImpl(3, "Charlie");

        alice.addNotes(notes1);
        bob.addNotes(notes2);
        charlie.addNotes(notes3);

        fm1 = DIFactory.getInstance().newFutureMeeting(1, getFutureDate(1), setOf(alice, bob, charlie));
        fm2 = DIFactory.getInstance().newFutureMeeting(2, getFutureDate(2), setOf(alice, bob));
        fm3 = DIFactory.getInstance().newFutureMeeting(3, getFutureDate(3), setOf(alice));

        pm1 = DIFactory.getInstance().newPastMeeting(4, getPastDate(1), setOf(alice, bob, charlie), notes1);
        pm2 = DIFactory.getInstance().newPastMeeting(5, getPastDate(2), setOf(alice, bob), notes2);
        pm3 = DIFactory.getInstance().newPastMeeting(6, getPastDate(3), setOf(alice), notes3);
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
        doc = new XmlDataStore();
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
        doc.writeToFilename(xml_file_dir + "\\/\\filename");
    }

    @Test
    public void testXmlInjectionSafety() throws Exception {
        String xml_injection_str = "Jane/>&\"\\\"";
        Contact xml_jane = new ContactImpl(5, xml_injection_str);
        xml_jane.addNotes("notes");

        doc.setContacts(setOf(xml_jane));

        doc.writeToFilename(filename);
        doc = new XmlDataStore();
        doc.loadFromFilename(filename);

        Contact loaded_xml_jane = (Contact) doc.getContacts().toArray()[0];

        assertEquals(xml_jane, loaded_xml_jane);
    }

    @Test
    public void testEmptyStringSaving() throws Exception {
        Contact nemo = new ContactImpl(5, "");

        doc.setContacts(setOf(nemo));

        doc.writeToFilename(filename);
        doc = new XmlDataStore();
        doc.loadFromFilename(filename);

        Contact loaded_nemo = (Contact) doc.getContacts().toArray()[0];

        assertEquals(nemo, loaded_nemo);
    }


    @Test
    public void testSetTechnique() throws Exception {
        assertTrue(setOf(pm1, pm2, pm3).containsAll(doc.getPastMeetings()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadMalformedDate() throws Exception {
        doc.loadFromFilename(xml_file_dir + "malformed_date.xml");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadMalformedId() throws Exception {
        doc.loadFromFilename(xml_file_dir + "malformed_id.xml");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadMalformedString() throws Exception {
        doc.loadFromFilename(xml_file_dir + "malformed_string.xml");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadContactWithTooManyNames() throws Exception {
        doc.loadFromFilename(xml_file_dir + "too_many_names.xml");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadMalformedTag() throws Exception {
        doc.loadFromFilename(xml_file_dir + "malformed_tag.xml");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadWrongTag() throws Exception {
        doc.loadFromFilename(xml_file_dir + "wrong_tag.xml");
    }

    @Test(expected = IOException.class)
    public void testLoadFromBadFile() throws Exception {
        doc.loadFromFilename(xml_file_dir + "this_file_doesnt_exist.xml");
    }

    @After
    public void cleanUp() {
        File file = new File(filename);
        if (file.exists()) {
            //file.delete();
        }
    }

}
