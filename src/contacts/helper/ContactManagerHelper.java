package contacts.helper;

import contacts.Contact;
import contacts.Meeting;

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

    public static <T extends Meeting> boolean areMeetingsSetsEqual(Set<T> expected, Set<T> got) {
        if (expected.size() != got.size())
            return false;

        for (Meeting got_meeting : got) {
            // Find the original meeting object the got_meeting should equal
            Meeting expected_meeting = null;
            for (Meeting old_meeting : expected) {
                if (old_meeting.getId() == got_meeting.getId()) {
                    expected_meeting = old_meeting;
                    break;
                }
            }

            // If no meeting matches the saved meeting's id, throw exception
            if (expected_meeting == null)
                return false;

            // Check that the loaded and saved meetings are equal
            if (!expected_meeting.equals(got_meeting))
                return false;
        }

        return true;
    }
}
