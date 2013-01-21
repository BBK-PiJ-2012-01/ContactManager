package contactsmanager;

import contactsmanager.util.CalendarUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * An implementation of ContactManager.
 */
public class ContactManagerImpl implements ContactManager {
    private static final String DEFAULT_FILENAME = "contacts.txt";
    private final String filename;
    private int last_contact_id = -1;
    private int last_meeting_id = -1;
    private final Map<Integer,Contact> known_contacts = new HashMap<Integer, Contact>();
    private final Map<Integer, PastMeeting> past_meetings = new HashMap<Integer, PastMeeting>();
    private final Map<Integer, FutureMeeting> future_meetings = new HashMap<Integer, FutureMeeting>();

    /**
     * Create a new ContactManagerImpl object using the default filename ("contacts.txt") for storage.
     * If the file already exists, this will load from it.
     */
    public ContactManagerImpl() {
        this(DEFAULT_FILENAME);
    }

    /**
     * Creates a new ContactManagerImpl object using the given filename for storage.
     * If the file already exists, this will load from it.
     *
     * @param filename the file location to store data in.
     */
    public ContactManagerImpl(String filename) {
        this.filename = filename;

        if (new File(filename).isFile())
            loadFromFile();
    }

    /**
     * Loads 'known_contacts', 'past_meetings', and 'future_meetings' from the xml file at 'filename'.
     *
     * Meetings with unknown contacts aren't loaded (with a warning printed to stdout), but won't
     * prevent loading the rest of the meetings.
     */
    private void loadFromFile() {
        DataStore data = new XmlDataStore();
        try {
            data.loadFromFilename(filename);
        } catch (IOException e) {
            throw new IllegalArgumentException("File at filename was not accessible.");
        }

        // Load contacts
        for (Contact contact : data.getContacts()) {
            known_contacts.put(contact.getId(), contact);
        }

        if (!known_contacts.isEmpty())
            last_contact_id = Math.max(last_contact_id, Collections.max(known_contacts.keySet()));


        // Load past meetings
        for (PastMeeting meeting : data.getPastMeetings()) {
            // Ensure date is in past (inclusive of today)
            if (!CalendarUtil.isDateInPast(meeting.getDate())) {
                System.out.format("Couldn't load from file '%s'.  Past meeting %d is set in the future.\n",
                        filename, meeting.getId());
                continue;
            }

            try {
                // Ensure contacts are known
                ensureContactsAreKnown(meeting.getContacts());
            } catch (IllegalArgumentException err) {
                System.out.format("Couldn't load from file '%s'.  Contacts of meeting %d were unknown.\n",
                        filename, meeting.getId());
                continue;
            }

            past_meetings.put(meeting.getId(), meeting);
        }

        // Load future meetings
        for (FutureMeeting meeting : data.getFutureMeetings()) {
            // Ensure date is in future (inclusive of today)
            if (!CalendarUtil.isDateInFuture(meeting.getDate())) {
                System.out.format("Couldn't load from file '%s'.  Future meeting %d is set in the past.\n",
                        filename, meeting.getId());
                continue;
            }

            try {
                // Ensure contacts are known
                ensureContactsAreKnown(meeting.getContacts());
            } catch (IllegalArgumentException err) {
                System.out.format("Couldn't load from file '%s'.  Contacts of meeting %d were unknown.\n",
                        filename, meeting.getId());
                continue;
            }

            future_meetings.put(meeting.getId(), meeting);
        }

        if (!past_meetings.isEmpty())
            last_contact_id = Math.max(last_contact_id, Collections.max(past_meetings.keySet()));

        if (!future_meetings.isEmpty())
            last_contact_id = Math.max(last_contact_id, Collections.max(future_meetings.keySet()));
    }

    /**
     * Checks the given contacts set is not null or empty, and contains only
     * known contacts.
     *
     * @param contacts the set of contacts to check.
     * @throws NullPointerException if contacts is null.
     * @throws IllegalArgumentException if contacts is empty or contains unknown contacts.
     */
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

    /**
     * Checks that the given contact is not null and is known.
     *
     * @param contact the contact to check.
     * @throws NullPointerException if the contact is null.
     * @throws IllegalArgumentException if the contact is not known.
     */
    private void ensureContactIsKnown(Contact contact) {
        // Check that contact is not null
        if (contact == null)
            throw new NullPointerException("contact is null");

        if (!known_contacts.containsValue(contact))
            throw new IllegalArgumentException("contact '" + contact.getName() + "' does is not known");
    }

    @Override
    public int addFutureMeeting(Set<Contact> contacts, Calendar date) {
        if (date == null)
            throw new NullPointerException("date is null");

        // Ensure date is in future (inclusive of today)
        if (!CalendarUtil.isDateInFuture(date))
            throw new IllegalArgumentException("Date " + CalendarUtil.getSimpleCalendarString(date) + " is in the past");

        // Ensure contacts are known
        ensureContactsAreKnown(contacts);

        // Finally, add the meeting.
        int id = ++last_meeting_id;
        future_meetings.put(id, new FutureMeetingImpl(id, date, new HashSet<Contact>(contacts)));
        return id;
    }

    @Override
    public PastMeeting getPastMeeting(int id) {
        // Check that id is not that of a future meeting
        if (future_meetings.containsKey(id)) {
            throw new IllegalArgumentException("Id " + id + " belongs to a future meeting");
        }

        // Return past meeting, or null if it doesn't exist
        return past_meetings.get(id);
    }

    @Override
    public FutureMeeting getFutureMeeting(int id) {
        // Check that id is not that of a past meeting
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

    /**
     * Takes a list of meetings and returns a new list of the same meetings ordered chronologically.
     *
     * @param from_meetings_list the list of meetings to put in the sorted list.
     * @param <T> the class the meetings belong to (either Meeting, PastMeeting, or FutureMeeting).
     * @return a new list of the given meetings ordered chronologically.
     */
    private <T extends Meeting> List<T> getSortedMeetingList(List<T> from_meetings_list) {
        // Use a TreeMap to sort meetings by date
        SortedMap<Calendar, List<T>> sorted_meetings_by_date = new TreeMap<Calendar, List<T>>();


        // Add meetings to the SortedMap
        for (T meeting : from_meetings_list) {
            // To avoid meetings on the same date overwriting each other, each date
            // maps to a list of meetings for that date.
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

    @Override
    public List<Meeting> getFutureMeetingList(Contact contact) {
        ensureContactIsKnown(contact);

        List<Meeting> meetings_with_contact = new LinkedList<Meeting>();
        for (Meeting meeting : future_meetings.values()) {
            if (meeting.getContacts().contains(contact)) {
                meetings_with_contact.add(meeting);
            }
        }
        System.out.format("Contact %s has %d meetings\n", contact.getName(), meetings_with_contact.size());
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
        System.out.format("Contact %s has %d meetings\n", contact.getName(), meetings_with_contact.size());
        return getSortedMeetingList(meetings_with_contact);
    }

    @Override
    public void addNewPastMeeting(Set<Contact> contacts, Calendar date, String text) {
        if (date == null)
            throw new NullPointerException("date is null");

        if (text == null)
            throw new NullPointerException("text is null");

        // Ensure date is in past (inclusive of today)
        if (!CalendarUtil.isDateInPast(date))
            throw new IllegalArgumentException("Date " + CalendarUtil.getSimpleCalendarString(date) + " is in the future");

        // Ensure contacts are known
        ensureContactsAreKnown(contacts);

        // Finally, add the meeting.
        int id = ++last_meeting_id;
        past_meetings.put(id, new PastMeetingImpl(id, date, new HashSet<Contact>(contacts), text));
    }

    /**
     * Adds a meeting to the past based on the data in the given Meeting object, and adds
     * the given text as its note.
     *
     * @param meeting the meeting to base the new one on.
     * @param text the notes to add to the old meeting.
     * @throws IllegalStateException if the given state is in the future.
     */
    private void addExistingMeetingToPast(Meeting meeting, String text) {
        // Check meeting is in the past (inclusive of today)
        if (!CalendarUtil.isDateInPast(meeting.getDate()))
            throw new IllegalStateException("Date " + CalendarUtil.getSimpleCalendarString(meeting.getDate()) + " is in the future");

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
            FutureMeeting meeting = future_meetings.get(id);

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
            throw new IllegalArgumentException("Meeting Id " + id + " does not exist");
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
        DataStore data = new XmlDataStore();

        // Put data in data store
        data.setContacts(known_contacts.values());
        data.setFutureMeetings(future_meetings.values());
        data.setPastMeetings(past_meetings.values());

        // Save data store to file
        try {
            data.writeToFilename(filename);
        } catch (IOException e) {
            System.out.println("Error! Couldn't write to filename: " + filename);
        }
    }
}
