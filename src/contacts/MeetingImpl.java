package contacts;

import java.util.Calendar;
import java.util.Set;

/**
 * User: Sam Wright
 * Date: 03/01/2013
 * Time: 16:46
 */
public class MeetingImpl implements Meeting {
    private final int id;
    private final Calendar date;
    private final Set<Contact> contacts;

    public MeetingImpl(int id, Calendar date, Set<Contact> contacts) {
        this.id = id;
        this.date = date;
        this.contacts = contacts;
    }

    @Override
    public int getId() {
        return 0; // Dummy implementation
    }

    @Override
    public Calendar getDate() {
        return null; // Dummy implementation
    }

    @Override
    public Set<Contact> getContacts() {
        return null; // Dummy implementation
    }
}
