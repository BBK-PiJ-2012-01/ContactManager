package contacts;

import java.util.Collection;
import java.util.Set;

/**
 * A class to serialise the data stored in ContactManager to be saved to file,
 * and also deserialise data stored in a file for use in ContactManager.
 *
 * Sanity/consistency checks for contacts and meetings are entirely left to
 * ContactManager.
 */
public interface DataStore {
    /**
     * Sets the contacts in the data store.
     *
     * @param contacts the contacts to add.
     * @throws NullPointerException if contacts is null.
     */
    void setContacts(Collection<Contact> contacts);

    /**
     * Gets the contacts from the data store.
     *
     * @return contacts.
     */
    Set<Contact> getContacts();

    /**
     * Sets the future meetings in the data store
     *
     * @param meetings the future meetings to add.
     * @throws NullPointerException if meetings is null.
     */
    void setFutureMeetings(Collection<FutureMeeting> meetings);

    /**
     * Gets the future meetings from the document.
     *
     * @return future meetings.
     */
    Set<FutureMeeting> getFutureMeetings();

    /**
     * Sets the past meetings in the data store
     *
     * @param meetings the past meetings to add.
     * @throws NullPointerException if meetings is null.
     */
    void setPastMeetings(Collection<PastMeeting> meetings);

    /**
     * Gets the past meetings from the document.
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
