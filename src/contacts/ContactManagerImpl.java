package contacts;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

/**
 * User: Sam Wright
 * Date: 03/01/2013
 * Time: 16:45
 */
public class ContactManagerImpl implements ContactManager {

    public ContactManagerImpl(String filename) {

    }

    @Override
    public int addFutureMeeting(Set<Contact> contacts, Calendar date) {
        return 0; // Dummy implementation
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
        // Dummy implementation
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
