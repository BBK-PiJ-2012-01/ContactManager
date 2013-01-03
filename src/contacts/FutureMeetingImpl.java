package contacts;

import java.util.Calendar;
import java.util.Set;

/**
 * User: Sam Wright
 * Date: 03/01/2013
 * Time: 16:46
 */
public class FutureMeetingImpl extends AbstractMeeting implements FutureMeeting {

    public FutureMeetingImpl(int id, Calendar date, Set<Contact> contacts) {
        super(id, date, contacts);
    }
}
