package contacts;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: Sam Wright
 * Date: 05/01/2013
 * Time: 20:03
 */
public class XmlDataStore implements DataStore {
    private Map<Integer, Contact> known_contacts = new HashMap<Integer, Contact>();
    private Map<Integer, PastMeeting> past_meetings = new HashMap<Integer, PastMeeting>();
    private Map<Integer, FutureMeeting> future_meetings = new HashMap<Integer, FutureMeeting>();

    @Override
    public void setContacts(Collection<Contact> contacts) {
        for (Contact contact : contacts) {
            if ()
        }
    }

    @Override
    public Set<Contact> getContacts() {
        return null; // Dummy implementation
    }

    @Override
    public void setFutureMeetings(Collection<FutureMeeting> meetings) {
        // Dummy implementation
    }

    @Override
    public Set<FutureMeeting> getFutureMeetings() {
        return null; // Dummy implementation
    }

    @Override
    public void setPastMeetings(Collection<PastMeeting> meetings) {
        // Dummy implementation
    }

    @Override
    public Set<PastMeeting> getPastMeetings() {
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
