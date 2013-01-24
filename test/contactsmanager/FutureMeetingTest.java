package contactsmanager;

import java.util.Calendar;
import java.util.Set;

/**
 * User: Sam Wright
 * Date: 24/01/2013
 * Time: 08:31
 */
public class FutureMeetingTest extends AbstractMeetingTest {

    @Override
    public Meeting createInstance(int id, Calendar date, Set<Contact> contacts) {
        return DIFactory.getInstance().newFutureMeeting(id, date, contacts);
    }
}
