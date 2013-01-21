package contactsmanager;

import java.util.Calendar;
import java.util.Set;

/**
 * An implementation of AbstractMeeting.  No functionality is added, this is simply
 * a non-abstract version of AbstractMeeting.
 */
public class FutureMeetingImpl extends AbstractMeeting implements FutureMeeting {

    public FutureMeetingImpl(int id, Calendar date, Set<Contact> contacts) {
        super(id, date, contacts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return super.equals(o);
    }


}
