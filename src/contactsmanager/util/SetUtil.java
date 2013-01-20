package contactsmanager.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A class containing a utility function for creating sets of objects.
 */
public class SetUtil {
    /**
     * Returns of set of the objects given in the vararg.
     *
     * @param contents the objects to return in the set.
     * @param <T> the class the given objects are instantiations of.
     * @return a set containing the given contents.
     * @throws AssertionError if any of the contents are not the same type.
     */
    public static <T> Set<T> setOf(T... contents) {
        // Check all items in set are of same type...
        Class first_class = null;
        for (Object item : contents) {
            if (first_class == null) {
                first_class = item.getClass();
            } else {
                assert first_class == item.getClass();
            }
        }

        return new HashSet<T>(Arrays.asList(contents));
    }
}
