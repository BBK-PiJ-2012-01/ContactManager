package contacts;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * User: Sam Wright
 * Date: 05/01/2013
 * Time: 20:04
 */
public class ContactsDocumentImplTest {
    private DataStore doc;
    private Contact alice, bob, charlie;

    @Before
    public void setUp() throws Exception {
        doc = new XmlDataStore();
        alice = new ContactImpl(1, "Alice");
        bob = new ContactImpl(1, "Bob");
        charlie = new ContactImpl(1, "Charlie");
    }

    private static <T> Set<T> setOf(T... contents) {
        return new HashSet<T>(Arrays.asList(contents));
    }

    @Test
    public void testAddContacts() throws Exception {
        doc.addContacts(setOf(alice, bob));
        doc.addContacts(setOf(charlie));

        //assertEquals()
    }

    @Test
    public void testAddExistingContacts() throws Exception {
        doc.addContacts(setOf(alice, bob));
        doc.addContacts(setOf(bob, charlie));
    }

    @Test
    public void testGetContacts() throws Exception {

    }

    @Test
    public void testAddFutureMeetings() throws Exception {

    }

    @Test
    public void testGetFutureMeetings() throws Exception {

    }

    @Test
    public void testAddPastMeetings() throws Exception {

    }

    @Test
    public void testGetPastMeetings() throws Exception {

    }

    @Test
    public void testWriteToFilename() throws Exception {

    }

    @Test
    public void testLoadFromFilename() throws Exception {

    }
}
