package contacts;

import java.util.Collection;
import java.util.Set;

/**
 * A class to serialise the data stored in ContactManager to be saved to file,
 * and also deserialise data stored in a file for use in ContactManager.
 */
public interface DataStore {
    /**
     * Adds contacts to the document that aren't already there.
     *
     * @param contacts the contacts to add.
     * @throws NullPointerException if contacts (or any of the contacts) is null.
     * @throws IllegalArgumentException if a contact is added with the same id as
     *      a previously-added contact, but the contacts are not identical.
     */
    void addContacts(Collection<Contact> contacts);

    /**
     * Gets contacts from document.
     *
     * @return contacts.
     */
    Set<Contact> getContacts();

    /**
     * Adds future meetings to the document that aren't already there.
     *
     * @param meetings the future meetings to add.
     * @throws NullPointerException if meetings (or any of the meetings) is null.
     * @throws IllegalArgumentException if a meeting is added with the same id as
     *      a previously-added meeting, but the meetings are not identical.
     */
    void addFutureMeetings(Collection<FutureMeeting> meetings);

    /**
     * Gets future meetings from document.
     *
     * @return future meetings.
     */
    Set<FutureMeeting> getFutureMeetings();

    /**
     * Adds past meetings to the document that aren't already there.
     *
     * @param meetings the past meetings to add.
     * @throws NullPointerException if meetings (or any of the meetings) is null.
     * @throws IllegalArgumentException if a meeting is added with the same id as
     *      a previously-added meeting, but the meetings are not identical.
     */
    void addPastMeetings(Collection<PastMeeting> meetings);

    /**
     * Gets past meetings from document.
     *
     * @return past meetings.
     */
    Set<PastMeeting> getPastMeetings();

    /**
     * Writes the document to the given filename.
     *
     * @param filename the name of the file to be (over)written to.
     * @throws java.io.IOException if file cannot be written.
     */
    void writeToFilename(String filename);

    /**
     * Reads data from the file at the given filename.
     *
     * @param filename the name of the file to load.
     * @throws java.io.IOException if file cannot be read.
     * @throws IllegalArgumentException if file cannot be parsed.
     */
    void loadFromFilename(String filename);
}
