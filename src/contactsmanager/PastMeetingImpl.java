package contactsmanager;

import java.util.Calendar;
import java.util.Set;

/**
 * An implementation of PastMeeting, extending AbstractMeeting by allowing notes to be added.
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PastMeetingImpl that = (PastMeetingImpl) o;

        if (!notes.equals(that.notes)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + notes.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return super.toString() + " with notes: " + notes;
    }
}
