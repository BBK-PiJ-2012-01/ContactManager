package contactsmanager.helper;

import org.junit.Test;

import static contactsmanager.helper.SetHelper.*;
import static org.junit.Assert.*;

import java.util.Set;

/**
 * User: Sam Wright
 * Date: 07/01/2013
 * Time: 19:33
 */
public class SetHelperTest {

    @Test
    public void testSetOfStrings() throws Exception {
        Set<String> strings = setOf("a", "b", "c");

        assertEquals(3, strings.size());
        assertTrue(strings.contains("a"));
        assertTrue(strings.contains("b"));
        assertTrue(strings.contains("c"));
    }

    @Test
    public void testSetOfNothing() throws Exception {
        Set empty = setOf();
        assertTrue(empty.isEmpty());
    }

    @Test(expected = AssertionError.class)
    public void testSetOfManyTypes() throws Exception {
        setOf("a", new Object());
    }
}
