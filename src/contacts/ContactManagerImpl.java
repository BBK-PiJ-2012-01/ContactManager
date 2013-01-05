package contacts;

import sun.text.resources.CollationData_el;

import java.text.SimpleDateFormat;
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
        if (contacts == null)
            throw new NullPointerException("contacts is null");

        // Ensure at least one contact will attend
        if (contacts.isEmpty())
            throw new IllegalArgumentException("No contacts at meeting");

        Set<Contact> unknown_contacts = new HashSet<Contact>(contacts);
        unknown_contacts.removeAll(known_contacts.values());

        if (!unknown_contacts.isEmpty())
            throw new IllegalArgumentException("Unknown contacts in meeting: " + unknown_contacts);
    }

    private void ensureContactIsKnown(Contact contact) {
        // Check that contact is not null
        if (contact == null)
            throw new NullPointerException("contact is null");

        if (!known_contacts.containsValue(contact))
            throw new IllegalArgumentException("contact '" + contact.getName() + "' does is not known");
    }

    private String getSimpleCalendarString(Calendar date) {
        SimpleDateFormat uk_date_format = new SimpleDateFormat("dd/MM/yyyy");
        return uk_date_format.format(date.getTime());
    }

    @Override
    public int addFutureMeeting(Set<Contact> contacts, Calendar date) {
        if (date == null)
            throw new NullPointerException("date is null");

        // Ensure date is in future (inclusive of today)
        checkDateInFuture(date);

        // Ensure contacts are known
        ensureContactsAreKnown(contacts);

        // Finally, add the meeting.
        int id = ++last_meeting_id;
        future_meetings.put(id, new FutureMeetingImpl(id, date, new HashSet<Contact>(contacts)));
        return id;
    }

    private void checkDateInFuture(Calendar date) {
        Calendar start_of_today = Calendar.getInstance();
        start_of_today.set(Calendar.HOUR_OF_DAY, 0);
        start_of_today.set(Calendar.MINUTE, 0);
        start_of_today.set(Calendar.SECOND, 0);
        if (date.before(start_of_today) && !date.equals(start_of_today))
            throw new IllegalArgumentException("Date " + getSimpleCalendarString(date) + " is in the past - now is " + getSimpleCalendarString(start_of_today));
    }

    private void checkDateInPast(Calendar date) {
        Calendar end_of_today = Calendar.getInstance();
        end_of_today.set(Calendar.HOUR_OF_DAY, 23);
        end_of_today.set(Calendar.MINUTE, 59);
        end_of_today.set(Calendar.SECOND, 59);
        if (date.after(end_of_today))
            throw new IllegalArgumentException("Date " + getSimpleCalendarString(date) + " is in the past - now is " + getSimpleCalendarString(end_of_today));
    }

    @Override
    public PastMeeting getPastMeeting(int id) {
        // Chech that id is not that of a future meeting
        if (future_meetings.containsKey(id)) {
            throw new IllegalArgumentException("Id " + id + " belongs to a future meeting");
        }

        // Return past meeting, or null if it doesn't exist
        return past_meetings.get(id);
    }

    @Override
    public FutureMeeting getFutureMeeting(int id) {
        // Chech that id is not that of a past meeting
        if (past_meetings.containsKey(id)) {
            throw new IllegalArgumentException("Id " + id + " belongs to a past meeting");
        }

        // Return past meeting, or null if it doesn't exist
        return future_meetings.get(id);
    }

    @Override
    public Meeting getMeeting(int id) {
        Meeting meeting = past_meetings.get(id);

        if (meeting == null) {
            return future_meetings.get(id);
        } else {
            return meeting;
        }
    }

    private <T extends Meeting> List<T> getSortedMeetingList(List<T> from_meetings_list) {
        // Use a TreeMap to sort meetings by date
        SortedMap<Calendar, List<T>> sorted_meetings_by_date = new TreeMap<Calendar, List<T>>();

        // Add meetings that the contact will attend
        for (T meeting : from_meetings_list) {
            List<T> meetings_on_date = sorted_meetings_by_date.get(meeting.getDate());

            if (meetings_on_date == null) {
                meetings_on_date = new LinkedList<T>();
                sorted_meetings_by_date.put(meeting.getDate(), meetings_on_date);
            }

            meetings_on_date.add(meeting);
        }

        // Collect sorted meetings into one list
        List<T> sorted_meetings = new LinkedList<T>();
        for (List<T> meetings_on_date : sorted_meetings_by_date.values()) {
            sorted_meetings.addAll(meetings_on_date);
        }

        return sorted_meetings;
    }

    /**
     * I would like to use this in getFutureMeetingList and getPastMeetingList, but the former
     * needs List<Meeting> and the latter needs List<PastMeeting>, complicating issues...
     *
     * As such, this isn't used. TODO: delete this.
     */
    private List<Meeting> filterMeetingsForContact(Collection<? extends Meeting> meetings, Contact contact) {
        List<Meeting> meetings_with_contact = new LinkedList<Meeting>();
        for (Meeting meeting : meetings) {
            if (meeting.getContacts().contains(contact)) {
                meetings_with_contact.add(meeting);
            }
        }
        return meetings_with_contact;
    }

    @Override
    public List<Meeting> getFutureMeetingList(Contact contact) {
        ensureContactIsKnown(contact);

        List<Meeting> meetings_with_contact = new LinkedList<Meeting>();
        for (Meeting meeting : future_meetings.values()) {
            if (meeting.getContacts().contains(contact)) {
                meetings_with_contact.add(meeting);
            }
        }

        return getSortedMeetingList(meetings_with_contact);
    }

    @Override
    public List<Meeting> getFutureMeetingList(Calendar date) {
        // Check that date is not null
        if (date == null)
            throw new NullPointerException("date is null");

        List<Meeting> meetings_on_date = new LinkedList<Meeting>();
        for (Meeting meeting : future_meetings.values()) {
            if (meeting.getDate().equals(date)) {
                meetings_on_date.add(meeting);
            }
        }
        return getSortedMeetingList(meetings_on_date);
    }

    @Override
    public List<PastMeeting> getPastMeetingList(Contact contact) {
        ensureContactIsKnown(contact);

        List<PastMeeting> meetings_with_contact = new LinkedList<PastMeeting>();
        for (PastMeeting meeting : past_meetings.values()) {
            if (meeting.getContacts().contains(contact)) {
                meetings_with_contact.add(meeting);
            }
        }
        return getSortedMeetingList(meetings_with_contact);
    }

    @Override
    public void addNewPastMeeting(Set<Contact> contacts, Calendar date, String text) {
        if (date == null)
            throw new NullPointerException("date is null");

        if (text == null)
            throw new NullPointerException("text is null");

        // Ensure date is in future (inclusive of today)
        checkDateInPast(date);

        // Ensure contacts are known
        ensureContactsAreKnown(contacts);

        // Finally, add the meeting.
        int id = ++last_meeting_id;
        past_meetings.put(id, new PastMeetingImpl(id, date, new HashSet<Contact>(contacts), text));
    }

    private void addExistingMeetingToPast(Meeting meeting, String text) {
        // Check meeting is in the past (inclusive of today)
        try {
            checkDateInPast(meeting.getDate());
        } catch (IllegalArgumentException err) {
            throw new IllegalStateException(err);
        }

        // Recreate as past meeting
        PastMeeting new_meeting = new PastMeetingImpl(meeting.getId(), meeting.getDate(), meeting.getContacts(), text);

        // Put in past_meetings (if meeting is already in past_meetings, this overwrites it).
        past_meetings.put(meeting.getId(), new_meeting);
    }

    @Override
    public void addMeetingNotes(int id, String text) {
        if (text == null)
            throw new NullPointerException("text is null");
        text = text.trim();

        if (future_meetings.containsKey(id)) {
            Meeting meeting = future_meetings.get(id);

            // If the meeting is in the future, this will throw the appropriate exception
            addExistingMeetingToPast(meeting, text);

            // If no exception was thrown, then it worked.  Can now remove from future_meetings
            future_meetings.remove(id);
        } else if (past_meetings.containsKey(id)) {
            PastMeeting meeting = past_meetings.remove(id);

            // Concatenate old and new notes
            String total_notes = meeting.getNotes() + '\n' + text;

            // If the meeting is in the future, this will throw the appropriate exception, and the original is kept.
            // If it succeeds, it will overwrite the previous 'meeting' object in the past_meetings map.
            addExistingMeetingToPast(meeting, total_notes);
        } else {
            throw new IllegalArgumentException("Meeting Id " + id + "does not exist");
        }
    }

    @Override
    public void addNewContact(String name, String notes) {
        // Check name is not null
        if (name == null)
            throw new NullPointerException("text is null");

        // Check notes is not null
        if (notes == null)
            throw new NullPointerException("notes is null");

        int id = ++last_contact_id;
        Contact contact = new ContactImpl(id, name);

        // Add notes to contact.  It will automatically remove whitespace.
        contact.addNotes(notes);

        known_contacts.put(id, contact);
    }

    @Override
    public Set<Contact> getContacts(int... ids) {
        Set<Contact> contacts = new HashSet<Contact>();

        for (int id : ids) {
            Contact contact = known_contacts.get(id);

            // Check that contact is known
            if (contact == null)
                throw new IllegalArgumentException("Contact with id " + id + " does not exist");

            contacts.add(contact);
        }

        return contacts;
    }

    @Override
    public Set<Contact> getContacts(String name) {
        Set<Contact> matching_contacts = new HashSet<Contact>();

        for (Contact contact : known_contacts.values()) {
            if (contact.getName().contains(name)) {
                matching_contacts.add(contact);
            }
        }

        return matching_contacts;
    }

    @Override
    public void flush() {
        // Dummy implementation
    }
}
