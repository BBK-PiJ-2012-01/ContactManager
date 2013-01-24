package contactsmanager;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Properties;
import java.util.Set;

/**
 * User: Sam Wright
 * Date: 24/01/2013
 * Time: 08:37
 */
public class DIFactory {
    // Singleton
    static final DIFactory instance;
    static {
        instance = new DIFactory();
    }

    public static DIFactory getInstance() {
        return instance;
    }

    // Factory
    private final Class contact, past_meeting, future_meeting, contact_manager, data_store;
    private final Class<?>[] contact_constructor = new Class<?>[]{Integer.class, String.class};
    private final Class<?>[] past_meeting_constructor = new Class<?>[]{Integer.class, Calendar.class, Set.class, String.class};
    private final Class<?>[] future_meeting_constructor = new Class<?>[]{Integer.class, Calendar.class, Set.class};
    private final Class<?>[] default_constructor = new Class<?>[]{};
    private final Class<?>[] contact_manager_filename_constructor = new Class<?>[]{String.class};

    private Object newInstance(Class<?> clazz, Class<?>[] parameter_types, Object... parameters) {
//        Class<?>[] parameter_types = new Class<?>[parameters.length];
//        for (int i = 0; i < parameters.length; ++i) {
//            parameter_types[i] = parameters[i].getClass();
//        }

        try {
            return clazz.getConstructor(parameter_types).newInstance(parameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Contact newContact(int id, String name) {
        return (Contact) newInstance(contact, contact_constructor, id, name);
    }

    public PastMeeting newPastMeeting(int id, Calendar date, Set<Contact> contacts, String notes) {
        return (PastMeeting) newInstance(past_meeting, past_meeting_constructor, id, date, contacts, notes);
    }

    public FutureMeeting newFutureMeeting(int id, Calendar date, Set<Contact> contacts) {
        return (FutureMeeting) newInstance(future_meeting, future_meeting_constructor, id, date, contacts);
    }

    public ContactManager newContactManager() {
        return (ContactManager) newInstance(contact_manager, default_constructor);
    }

    public ContactManager newContactManager(String filename) {
        return (ContactManager) newInstance(contact_manager, contact_manager_filename_constructor, filename);
    }

    public DataStore newDataStore() {
        return (DataStore) newInstance(data_store, default_constructor);
    }

    private DIFactory() {
        try {
            Properties props = new Properties();
            InputStream props_file = new FileInputStream("config.ini");
            props.load(props_file);

            contact = Class.forName(props.getProperty("Contact"));
            past_meeting = Class.forName(props.getProperty("PastMeeting"));
            future_meeting = Class.forName(props.getProperty("FutureMeeting"));
            contact_manager = Class.forName(props.getProperty("ContactManager"));
            data_store = Class.forName(props.getProperty("DataStore"));

            props_file.close();

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }




}
