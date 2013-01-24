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
    private final Map<Integer, PastMeeting> past_meetings_by_id = new HashMap<Integer, PastMeeting>();
    private final Map<Integer, FutureMeeting> future_meetings_by_id = new HashMap<Integer, FutureMeeting>();
    private final Map<Calendar, List<Meeting>> meetings_by_date;

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
        meetings_by_date = new TreeMap<Calendar, List<Meeting>>(CalendarUtil.getDateComparator());

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
        DataStore data = DIFactory.getInstance().newDataStore();

        try {
            data.loadFromFilename(filename);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("File at '%s' was not accessible.", filename), e);
        }

        // Load contacts
        for (Contact contact : data.getContacts()) {
            known_contacts.put(contact.getId(), contact);
        }

        // Update the 'last_contact_id' so newly-created contacts' ids don't clash with those just loaded.
        if (!known_contacts.isEmpty())
            last_contact_id = Math.max(last_contact_id, Collections.max(known_contacts.keySet()));


        // Load past meetings
        for (PastMeeting meeting : data.getPastMeetings()) {
            try {
                // Ensure contacts are known
                addMeeting(meeting);
            } catch (IllegalArgumentException err) {
                System.out.format("Couldn't load meeting '%d' from file '%s'%n", meeting.getId(), filename);
                err.printStackTrace();
            }
        }

        // Load future meetings
        for (FutureMeeting meeting : data.getFutureMeetings()) {
            try {
                // Ensure contacts are known
                addMeeting(meeting);
            } catch (IllegalArgumentException err) {
                System.out.format("Couldn't load meeting '%d' from file '%s'%n", meeting.getId(), filename);
                err.printStackTrace();
            }
        }
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

    /**
     * Adds the given meeting object to the manager's internal data structures, and updates last_meeting_id.
     *
     * @throws IllegalArgumentException if any of the meeting's contacts are unknown,
     *      or no contacts will be at the meeting,
     *      or if the given meeting is neither a PastMeeting nor a FutureMeeting.
     */
    private void addMeeting(Meeting meeting) {
        // Ensure the meeting's contacts are valid
        ensureContactsAreKnown(meeting.getContacts());

        // Add the meeting to the correct data structure
        if (meeting instanceof FutureMeeting) {
            future_meetings_by_id.put(meeting.getId(), (FutureMeeting) meeting);
        } else if (meeting instanceof PastMeeting) {
            past_meetings_by_id.put(meeting.getId(), (PastMeeting) meeting);
        } else {
            throw new IllegalArgumentException("Given meeting was neither a PastMeeting nor a FutureMeeting");
        }

        // Add the meeting 'meetings_by_date':
        // Get the meetings list for the meeting's date
        List<Meeting> meetings_on_date = meetings_by_date.get(meeting.getDate());

        if (meetings_on_date == null) {
            // If no meetings have previously been added for this date,
            // register a new list for this date
            meetings_on_date = new LinkedList<Meeting>();
            meetings_by_date.put(meeting.getDate(), meetings_on_date);
        }

        // Add the given meeting to the list of meetings for the meeting's date
        meetings_on_date.add(meeting);

        // Update 'last_meeting_id'
        last_meeting_id = Math.max(last_meeting_id, meeting.getId());
    }

    /**
     * Returns the next available meeting id.  NB this doesn't change last_meeting_id.
     */
    private int getNextMeetingId() {
        return last_meeting_id + 1;
    }

    @Override
    public int addFutureMeeting(Set<Contact> contacts, Calendar date) {
        if (date == null)
            throw new NullPointerException("date is null");

        // Ensure date is in future (inclusive of today)
        if (!CalendarUtil.isDateInFuture(date))
            throw new IllegalArgumentException("Date " + CalendarUtil.getCalendarString(date) + " is in the past");

        // Finally, add the meeting.
        int id = getNextMeetingId();
        addMeeting(DIFactory.getInstance().newFutureMeeting(id, date, new HashSet<Contact>(contacts)));
        return id;
    }

    @Override
    public PastMeeting getPastMeeting(int id) {
        // Check that id is not that of a future meeting
        if (future_meetings_by_id.containsKey(id)) {
            throw new IllegalArgumentException("Id " + id + " belongs to a future meeting");
        }

        // Return past meeting, or null if it doesn't exist
        return past_meetings_by_id.get(id);
    }

    @Override
    public FutureMeeting getFutureMeeting(int id) {
        // Check that id is not that of a past meeting
        if (past_meetings_by_id.containsKey(id)) {
            throw new IllegalArgumentException("Id " + id + " belongs to a past meeting");
        }

        // Return past meeting, or null if it doesn't exist
        return future_meetings_by_id.get(id);
    }

    @Override
    public Meeting getMeeting(int id) {
        Meeting meeting = past_meetings_by_id.get(id);

        if (meeting == null) {
            return future_meetings_by_id.get(id);
        } else {
            return meeting;
        }
    }

    /**
     * Sorts the given list of meetings by date (chronologically).
     *
     * @param meetings_list the list of meetings to sort chronologically.
     */
    private void sortMeetingListChronologically(List<? extends Meeting> meetings_list) {
        Comparator<Meeting> meeting_date_comparator = new Comparator<Meeting>() {
            @Override
            public int compare(Meeting o1, Meeting o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        };

        Collections.sort(meetings_list, meeting_date_comparator);
    }

    @Override
    public List<Meeting> getFutureMeetingList(Contact contact) {
        ensureContactIsKnown(contact);

        List<Meeting> meetings_with_contact = new LinkedList<Meeting>();
        for (Meeting meeting : future_meetings_by_id.values()) {
            if (meeting.getContacts().contains(contact)) {
                meetings_with_contact.add(meeting);
            }
        }

        sortMeetingListChronologically(meetings_with_contact);
        return meetings_with_contact;
    }

    @Override
    public List<Meeting> getFutureMeetingList(Calendar date) {
        // Check that date is not null
        if (date == null)
            throw new NullPointerException("date is null");

        List<Meeting> meetings_on_date = meetings_by_date.get(date);
        if (meetings_on_date == null) {
            meetings_on_date = new LinkedList<Meeting>();
        } else {
            sortMeetingListChronologically(meetings_on_date);
        }

        return meetings_on_date;
    }

    @Override
    public List<PastMeeting> getPastMeetingList(Contact contact) {
        ensureContactIsKnown(contact);

        List<PastMeeting> meetings_with_contact = new LinkedList<PastMeeting>();
        for (PastMeeting meeting : past_meetings_by_id.values()) {
            if (meeting.getContacts().contains(contact)) {
                meetings_with_contact.add(meeting);
            }
        }

        sortMeetingListChronologically(meetings_with_contact);
        return meetings_with_contact;
    }

    @Override
    public void addNewPastMeeting(Set<Contact> contacts, Calendar date, String text) {
        if (date == null)
            throw new NullPointerException("date is null");

        if (text == null)
            throw new NullPointerException("text is null");

        // Ensure date is in past (inclusive of today)
        if (!CalendarUtil.isDateInPast(date))
            throw new IllegalArgumentException("Date " + CalendarUtil.getCalendarString(date) + " is in the future");

        // Finally, add the meeting.
        int id = getNextMeetingId();
        addMeeting(DIFactory.getInstance().newPastMeeting(id, date, new HashSet<Contact>(contacts), text));
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
            throw new IllegalStateException("Date " + CalendarUtil.getCalendarString(meeting.getDate()) + " is in the future");

        // Recreate as past meeting
        PastMeeting new_meeting = DIFactory.getInstance().newPastMeeting(meeting.getId(), meeting.getDate(), meeting.getContacts(), text);

        // Put in past_meetings_by_id (if meeting is already in past_meetings_by_id, this overwrites it).
        past_meetings_by_id.put(meeting.getId(), new_meeting);
    }

    @Override
    public void addMeetingNotes(int id, String text) {
        if (text == null)
            throw new NullPointerException("text is null");
        text = text.trim();

        if (future_meetings_by_id.containsKey(id)) {
            FutureMeeting meeting = future_meetings_by_id.get(id);

            // If the meeting is in the future, this will throw the appropriate exception
            addExistingMeetingToPast(meeting, text);

            // If no exception was thrown, then it worked.  Can now remove from future_meetings_by_id
            future_meetings_by_id.remove(id);
        } else if (past_meetings_by_id.containsKey(id)) {
            PastMeeting meeting = past_meetings_by_id.remove(id);

            // Concatenate old and new notes
            String total_notes = meeting.getNotes() + '\n' + text;

            // If the meeting is in the future, this will throw the appropriate exception, and the original is kept.
            // If it succeeds, it will overwrite the previous 'meeting' object in the past_meetings_by_id map.
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
        Contact contact = DIFactory.getInstance().newContact(id, name);

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
        DataStore data = DIFactory.getInstance().newDataStore();

        // Put data in data store
        data.setContacts(known_contacts.values());
        data.setFutureMeetings(future_meetings_by_id.values());
        data.setPastMeetings(past_meetings_by_id.values());

        // Save data store to file
        try {
            data.writeToFilename(filename);
        } catch (IOException e) {
            System.out.println("Error! Couldn't write to filename: " + filename);
        }
    }
}
