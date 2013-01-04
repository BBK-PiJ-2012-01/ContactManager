package contacts;

/**
 * User: Sam Wright
 * Date: 03/01/2013
 * Time: 16:45
 */
public class ContactImpl implements Contact {
    private final int id;
    private final String name;
    private String notes = "";

    public ContactImpl(int id, String name) {
        this.id = id;
        this.name = String.valueOf(name);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return String.valueOf(name);
    }

    @Override
    public String getNotes() {
        return String.valueOf(notes);
    }

    @Override
    public void addNotes(String note) {
        if (notes.isEmpty()) {
            notes = String.valueOf(note.trim());
        } else {
            notes = notes + '\n' + note.trim();
        }
    }
}
