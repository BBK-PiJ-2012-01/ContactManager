package contactsmanager;

import static contactsmanager.util.CalendarUtil.getCalendarString;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * An abstract implementation of Meeting.  FutureMeetingImpl and PastMeetingImpl
 * derive functionality from this.
 */
abstract public class AbstractMeeting implements Meeting {
    private final int id;
    private final Calendar date;
    private final Set<Contact> contacts;

    public AbstractMeeting(Integer id, Calendar date, Set<Contact> contacts) {
        this.id = id;
        this.date = (Calendar) date.clone();
        this.contacts = new HashSet<Contact>(contacts);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Calendar getDate() {
        return (Calendar) date.clone();
    }

    @Override
    public Set<Contact> getContacts() {
        return new HashSet<Contact>(contacts);
    }

    @Override
    public boolean equals(Object o) {
        // Adapted from code generated automatically by IntelliJ
        if (this == o) return true;
        if (!(o instanceof AbstractMeeting)) return false;

        AbstractMeeting that = (AbstractMeeting) o;

        if (this.getId() != that.getId()) return false;
        if (!this.getContacts().equals(that.getContacts())) return false;
        if (!this.getDate().equals(that.getDate())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        // Adapted from code generated automatically by IntelliJ
        int result = id;
        result = 31 * result + date.hashCode();
        result = 31 * result + contacts.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("Meeting with id=%d on date %s with contacts %s",
                id, getCalendarString(date), contacts.toString());
    }
}
