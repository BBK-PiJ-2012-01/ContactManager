package contacts;

/**
 * User: Sam Wright
 * Date: 03/01/2013
 * Time: 16:45
 */
public class ContactImpl implements Contact {
    private final int id;
    private final String name;

    public ContactImpl(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getId() {
        return 0; // Dummy implementation
    }

    @Override
    public String getName() {
        return null; // Dummy implementation
    }

    @Override
    public String getNotes() {
        return null; // Dummy implementation
    }

    @Override
    public void addNotes(String note) {
        // Dummy implementation
    }
}
