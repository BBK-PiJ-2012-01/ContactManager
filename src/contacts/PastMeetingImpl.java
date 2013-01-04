package contacts;

import java.util.Calendar;
import java.util.Set;

/**
 * User: Sam Wright
 * Date: 03/01/2013
 * Time: 16:46
 */
public class PastMeetingImpl extends AbstractMeeting implements PastMeeting {
    private final String notes;

    public PastMeetingImpl(int id, Calendar date, Set<Contact> contacts, String notes) {
        super(id, date, contacts);
        this.notes = notes;
    }

    @Override
    public String getNotes() {
        return notes;
    }
}
