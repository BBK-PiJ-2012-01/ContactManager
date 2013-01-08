package contacts;

import java.util.Calendar;
import java.util.Set;

/**
 * An implementation of AbstractMeeting.  No functionality is added, this is simply
 * a non-abstract verion of AbstractMeeting.
 */
public class FutureMeetingImpl extends AbstractMeeting implements FutureMeeting {

    public FutureMeetingImpl(int id, Calendar date, Set<Contact> contacts) {
        super(id, date, contacts);
    }

}
