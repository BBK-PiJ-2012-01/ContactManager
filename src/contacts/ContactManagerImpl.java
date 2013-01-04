package contacts;

import java.util.*;

/**
 * User: Sam Wright
 * Date: 03/01/2013
 * Time: 16:45
 */
public class ContactManagerImpl implements ContactManager {
    private final String filename;
    private int last_contact_id = -1;
    private int last_meeting_id = -1;
    private Map<Integer,Contact> known_contacts = new HashMap<Integer, Contact>();
    private Map<Integer, PastMeeting> past_meetings = new HashMap<Integer, PastMeeting>();
    private Map<Integer, FutureMeeting> future_meetings = new HashMap<Integer, FutureMeeting>();

    public ContactManagerImpl(String filename) {
        this.filename = filename;
        loadFromFile();
    }

    private void loadFromFile() {

    }

    private void ensureContactsAreKnown(Set<Contact> contacts) {
        Set<Contact> unknown_contacts = new HashSet<Contact>(contacts);
        unknown_contacts.removeAll(known_contacts.values());

        if (!unknown_contacts.isEmpty())
            throw new IllegalArgumentException("Unknown contacts in meeting: " + unknown_contacts);
    }

    @Override
    public int addFutureMeeting(Set<Contact> contacts, Calendar date) {
        /*
         TODO: fix ContactManager so addFutureMeeting throws IllegalArgumentException when contacts.isEmpty()
         TODO: fix ContactManager so addFutureMeeting throws NullPointerException when 'date == null'
          */

        // Ensure date is in future (inclusive of today)
        Calendar now = Calendar.getInstance();
        if (date.before(now))
            throw new IllegalArgumentException("Date " + date + " is in the past");

        // Ensure contacts are known
        ensureContactsAreKnown(contacts);

        // Finally, add the meeting.
        int id = ++last_meeting_id;
        future_meetings.put(id, new FutureMeetingImpl(id, date, new HashSet<Contact>(contacts)));
        return id;
    }

    @Override
    public PastMeeting getPastMeeting(int id) {
        return null; // Dummy implementation
    }

    @Override
    public FutureMeeting getFutureMeeting(int id) {
        return null; // Dummy implementation
    }

    @Override
    public Meeting getMeeting(int id) {
        return null; // Dummy implementation
    }

    @Override
    public List<Meeting> getFutureMeetingList(Contact contact) {
        return null; // Dummy implementation
    }

    @Override
    public List<Meeting> getFutureMeetingList(Calendar date) {
        return null; // Dummy implementation
    }

    @Override
    public List<PastMeeting> getPastMeetingList(Contact contact) {
        return null; // Dummy implementation
    }

    @Override
    public void addNewPastMeeting(Set<Contact> contacts, Calendar date, String text) {
        // Ensure date is in future (inclusive of today)
        Calendar now = Calendar.getInstance();
        if (date.after(now))
            throw new IllegalArgumentException("Date " + date + " is in the future");

        // Ensure at least one contact will attend
        if (contacts.isEmpty())
            throw new IllegalArgumentException("No contacts at meeting");

        // Ensure contacts are known
        ensureContactsAreKnown(contacts);

        // Finally, add the meeting.
        int id = ++last_meeting_id;
        past_meetings.put(id, new PastMeetingImpl(id, date, new HashSet<Contact>(contacts), text));
    }

    @Override
    public void addMeetingNotes(int id, String text) {
        // Dummy implementation
    }

    @Override
    public void addNewContact(String name, String notes) {
        // Dummy implementation
    }

    @Override
    public Set<Contact> getContacts(int... ids) {
        return null; // Dummy implementation
    }

    @Override
    public Set<Contact> getContacts(String name) {
        return null; // Dummy implementation
    }

    @Override
    public void flush() {
        // Dummy implementation
    }
}
