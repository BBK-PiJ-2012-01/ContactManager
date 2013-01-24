package contactsmanager;

/**
 * An implementation of Contact.
 *
 * Notes added are automatically trimmed, and subsequent notes are added on new lines.
 */
public class ContactImpl implements Contact {
    private final int id;
    private final String name;
    private String notes = "";

    public ContactImpl(Integer id, String name) {
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

        ContactImpl that = (ContactImpl) o;

        if (this.getId() != that.getId()) return false;
        if (!this.getName().equals(that.getName())) return false;
        if (!this.getNotes().equals(that.getNotes())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + notes.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("Contact with id=%d, name=%s, notes=%s", id, name, notes);
    }
}