package contacts.helper;

import contacts.Contact;

import java.util.Set;

/**
 * User: Sam Wright
 * Date: 06/01/2013
 * Time: 03:37
 */
public class ContactManagerHelper {

    public static boolean areContactsSetsEqual(Set<Contact> expected, Set<Contact> got) {
        if (expected.size() != got.size())
            return false;

        for (Contact got_contact : got) {
            // Find the original Contact object the loaded contact should equal
            Contact expected_contact = null;
            for (Contact old_contact : expected) {
                if (old_contact.getId() == got_contact.getId()) {
                    expected_contact = old_contact;
                    break;
                }
            }

            // If no contact matches the saved contact's id, not equal
            if (expected_contact == null)
                return false;

            // Check that the loaded and saved contacts are equal
            if (!expected_contact.equals(got_contact))
                return false;
        }

        return true;
    }
}
