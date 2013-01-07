package contacts;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * User: Sam Wright
 * Date: 03/01/2013
 * Time: 16:47
 */
public class ContactImplTest {
    private Contact c;
    private int id = 4;
    private String name = "Bob";
    private String note1 = "First Note";
    private String note2 = "Second Note";

    @Before
    public void setUp() throws Exception {
        c = new ContactImpl(id, name);
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals(id, c.getId());
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(name, c.getName());
    }

    @Test
    public void testGetNoNotes() throws Exception {
        assertEquals("", c.getNotes());
    }

    @Test
    public void testAddOneNote() throws Exception {
        c.addNotes(note1);
    }

    @Test
    public void testAddTwoNotes() throws Exception {
        c.addNotes(note1);
        c.addNotes(note2);
    }

    @Test
    public void testGetOneNote() throws Exception {
        testAddOneNote();
        assertEquals(note1, c.getNotes());
    }

    @Test
    public void testGetTwoNotes() throws Exception {
        testAddTwoNotes();
        assertEquals(note1 + "\n" + note2, c.getNotes());
    }

    @Test
    public void testNoteWhitespace() throws Exception {
        c.addNotes("  \n" + note1 + "\n  ");
        c.addNotes("\n " + note2 + " \n ");
        assertEquals(note1 + "\n" + note2, c.getNotes());
    }

    @Test
    public void testEquals() throws Exception {
        Contact c_copy = new ContactImpl(4, "Bob");

        assertTrue(c.equals(c_copy));
    }

    @Test
    public void testHashCode() throws Exception {
        Contact c_copy = new ContactImpl(id, name);

        assertEquals(c.hashCode(), c_copy.hashCode());
    }
}
