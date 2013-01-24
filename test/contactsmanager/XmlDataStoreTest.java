package contactsmanager;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Test class for XmlDataStore (containing xml-specific tests).
 */
public class XmlDataStoreTest {
    private DataStore doc;
    private final String xml_file_dir = "test" + File.separator +
            "contactsmanager" + File.separator +
            "xml_test_files" + File.separator;

    @Before
    public void setUp() throws Exception {
        doc = new XmlDataStore();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadMalformedDate() throws Exception {
        doc.loadFromFilename(xml_file_dir + "malformed_date.xml");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadMalformedId() throws Exception {
        doc.loadFromFilename(xml_file_dir + "malformed_id.xml");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadMalformedString() throws Exception {
        doc.loadFromFilename(xml_file_dir + "malformed_string.xml");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadContactWithTooManyNames() throws Exception {
        doc.loadFromFilename(xml_file_dir + "too_many_names.xml");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadMalformedTag() throws Exception {
        doc.loadFromFilename(xml_file_dir + "malformed_tag.xml");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadWrongTag() throws Exception {
        doc.loadFromFilename(xml_file_dir + "wrong_tag.xml");
    }

    @Test(expected = IOException.class)
    public void testLoadFromBadFile() throws Exception {
        doc.loadFromFilename(xml_file_dir + "this_file_doesnt_exist.xml");
    }
}
