package contactsmanager;

import java.util.Calendar;
import java.util.Set;

/**
 * Test class for FutureMeeting
 */
public class FutureMeetingTest extends MeetingTest {

    @Override
    public Meeting createInstance(int id, Calendar date, Set<Contact> contacts) {
        return DIFactory.getInstance().newFutureMeeting(id, date, contacts);
    }
}
