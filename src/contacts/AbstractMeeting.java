package contacts;

import contacts.helper.CalendarHelper;
import contacts.helper.ContactManagerHelper;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Sam Wright
 * Date: 03/01/2013
 * Time: 16:46
 */
abstract public class AbstractMeeting implements Meeting {
    private final int id;
    private final Calendar date;
    private final Set<Contact> contacts;

    public AbstractMeeting(int id, Calendar date, Set<Contact> contacts) {
        this.id = id;
        this.date = (Calendar) date.clone();
        this.contacts = new HashSet<Contact>(contacts);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Calendar getDate() {
        return (Calendar) date.clone();
    }

    @Override
    public Set<Contact> getContacts() {
        return new HashSet<Contact>(contacts);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractMeeting) {
            Meeting other = (Meeting) obj;

            // Check contact sets are equal
            if (!ContactManagerHelper.areContactsSetsEqual(this.getContacts(), other.getContacts()))
                return false;

            // Check calendar dates are (almost) equal
            if (!CalendarHelper.areDatesAlmostEqual(getDate(), other.getDate()))
                return false;

            // If meetings are past meetings, check their notes are equal
            if (this instanceof PastMeeting && other instanceof PastMeeting) {
                String expected_notes = ((PastMeeting) this).getNotes();
                String got_notes = ((PastMeeting) other).getNotes();

                if (!expected_notes.equals(got_notes))
                    return false;
            }

            return true;
        }
        return false;
    }
}
