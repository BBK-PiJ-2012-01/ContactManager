package contactsmanager;

import contactsmanager.util.CalendarUtil;

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
    private final String date_str;
    private final Set<Contact> contacts;

    public AbstractMeeting(int id, Calendar date, Set<Contact> contacts) {
        this.id = id;
        this.date = (Calendar) date.clone();
        this.date_str = CalendarUtil.getSimpleCalendarString(date);
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
        if (this == o) return true;
        if (!(o instanceof AbstractMeeting)) return false;

        AbstractMeeting that = (AbstractMeeting) o;

        if (id != that.id) return false;
        if (!contacts.equals(that.contacts)) return false;
        if (!date_str.equals(that.date_str)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + date_str.hashCode();
        result = 31 * result + contacts.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("Meeting with id=%d on date %s with contacts %s", id, date_str, contacts.toString());
    }
}