package contacts;

import java.util.Collection;

/**
 * User: Sam Wright
 * Date: 05/01/2013
 * Time: 20:03
 */
public class XmlDataStore implements DataStore {
    @Override
    public void addContacts(Collection<Contact> contacts) {
        // Dummy implementation
    }

    @Override
    public Collection<Contact> getContacts() {
        return null; // Dummy implementation
    }

    @Override
    public void addFutureMeetings(Collection<FutureMeeting> meetings) {
        // Dummy implementation
    }

    @Override
    public Collection<FutureMeeting> getFutureMeetings() {
        return null; // Dummy implementation
    }

    @Override
    public void addPastMeetings(Collection<PastMeeting> meetings) {
        // Dummy implementation
    }

    @Override
    public Collection<PastMeeting> getPastMeetings() {
        return null; // Dummy implementation
    }

    @Override
    public void writeToFilename(String filename) {
        // Dummy implementation
    }

    @Override
    public void loadFromFilename(String filename) {
        // Dummy implementation
    }
}
