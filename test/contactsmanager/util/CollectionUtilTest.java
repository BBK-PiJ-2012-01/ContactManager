package contactsmanager.util;

import org.junit.Test;

import static contactsmanager.util.CollectionUtil.listOf;
import static contactsmanager.util.CollectionUtil.setOf;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * User: Sam Wright
 * Date: 07/01/2013
 * Time: 19:33
 */
public class CollectionUtilTest {

    @Test
    public void testSetOfStrings() throws Exception {
        Set<String> strings = setOf("a", "b", "c");

        assertEquals(3, strings.size());
        assertTrue(strings.contains("a"));
        assertTrue(strings.contains("b"));
        assertTrue(strings.contains("c"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetOfNothing() throws Exception {
        Set empty = setOf();
        assertTrue(empty.isEmpty());
    }

    @Test(expected = AssertionError.class)
    public void testSetOfManyTypes() throws Exception {
        setOf("a", new Object());
    }

    @Test
    public void testListOrderMaintained() throws Exception {
        List<Integer> lst = new LinkedList<Integer>(Arrays.asList(1, 2, 3, 4));

        assertEquals(lst, listOf(1, 2, 3, 4));
    }
}
