package contactsmanager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Set;

import static contactsmanager.util.CollectionUtil.setOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: Sam Wright
 * Date: 24/01/2013
 * Time: 15:21
 */
public class DIFactoryTest {
    private static final String test_config_dir = "test"+File.separator+
            "contactsmanager"+File.separator+
            "DI_config_test_files"+File.separator;
    private static final String backup_config_filename = "config_backup.ini";
    private static final String config_filename = "config.ini";
    private DIFactory instance;

    /**
     * Copies a file from 'from' to 'to'.
     *
     * @param from source filename.
     * @param to output filename to copy to.
     * @throws IOException if something bad happens.
     */
    private static void copyFile(String  from, String to) throws IOException {
        File to_file = new File(to);
        if (to_file.exists())
            assertTrue(to_file.delete());

        Files.copy(new File(from).toPath(), to_file.toPath());
    }

    @BeforeClass
    public static void backupConfig() throws IOException {
        copyFile(config_filename, backup_config_filename);
    }

    @Before
    public void reloadConfigFromBackup() throws Exception {
        // Reload config file from backup
        copyFile(backup_config_filename, config_filename);

        // Force configuration reload (ie. circumvent singleton restriction):
        forceReloadFactory();
    }

    private void forceReloadFactory() throws Exception {
        // Get the private constructor;
        Constructor<DIFactory> constructor = DIFactory.class.getDeclaredConstructor();

        // Set the constructor as accessible (ie. public)
        constructor.setAccessible(true);

        // Recreate the instance
        try {
            instance = constructor.newInstance();
        } catch (InvocationTargetException err) {
            // There was an exception thrown during construction.
            // Since the only exception it can throw is IllegalStateException,
            // which InvocationTargetException wraps, I will wrap it with
            // IllegalStateException
            throw new IllegalStateException(err);
        }
    }

    @AfterClass
    public static void deleteBackup() throws IOException {
        File backup_file = new File(backup_config_filename);
        if (backup_file.exists()) {
            copyFile(backup_config_filename, config_filename);
            assertTrue(backup_file.delete());
        } else {
            throw new FileNotFoundException("Couldn't find the backup config file!");
        }

    }

    @Test
    public void testSingleton() throws Exception {
        assertEquals(DIFactory.getInstance(), DIFactory.getInstance());
    }

    @Test
    public void testNewContact() throws Exception {
        assertNotNull(instance.newContact(1, "name"));
    }

    @Test
    public void testNewPastMeeting() throws Exception {
        Set<Contact> contacts = setOf(DIFactory.getInstance().newContact(1, "name"));
        assertNotNull(instance.newPastMeeting(1, Calendar.getInstance(), contacts, "name"));
    }

    @Test
    public void testNewFutureMeeting() throws Exception {
        Set<Contact> contacts = setOf(DIFactory.getInstance().newContact(1, "name"));
        assertNotNull(instance.newFutureMeeting(1, Calendar.getInstance(), contacts));
    }

    @Test
    public void testNewContactManagerWithDefaultFile() throws Exception {
        assertNotNull(instance.newContactManager());
    }

    @Test
    public void testNewContactManagerWithSpecifiedFile() throws Exception {
        assertNotNull(instance.newContactManager("filename"));
    }

    @Test
    public void testNewDataStore() throws Exception {
        assertNotNull(instance.newDataStore());
    }

    @Test(expected = IllegalStateException.class)
    public void testNonexistentImplementation() throws Exception {
        copyFile(test_config_dir + "Contact_implementation_doesnt_exist.ini", config_filename);
        forceReloadFactory();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonexistentConfigFile() throws Exception {
        // Delete the config file

        File file = new File(config_filename);
        if (file.exists())
            assertTrue(file.delete());

        forceReloadFactory();
    }

    @Test(expected = IllegalStateException.class)
    public void testMalformedConfigFile() throws Exception {
        copyFile(test_config_dir + "Contact_interface_doesnt_exist.ini", config_filename);
        forceReloadFactory();
    }
}
