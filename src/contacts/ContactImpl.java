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
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNotes() {
        return notes;
    }

    @Override
    public void addNotes(String note) {
        if (notes.isEmpty()) {
            notes = note.trim();
        } else {
            notes = notes + '\n' + note.trim();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactImpl contact = (ContactImpl) o;

        if (id != contact.id) return false;
        if (!name.equals(contact.name)) return false;
        if (!notes.equals(contact.notes)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + notes.hashCode();
        return result;
    }
}