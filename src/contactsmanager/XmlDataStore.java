package contactsmanager;

import contactsmanager.helper.CalendarHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * An implementation of DataStore that loads and saves data in xml files.
 */
public class XmlDataStore implements DataStore {
    private Set<Contact> contacts = new HashSet<Contact>();
    private Set<PastMeeting> past_meetings = new HashSet<PastMeeting>();
    private Set<FutureMeeting> future_meetings = new HashSet<FutureMeeting>();
    private Document doc;
    private Map<Integer, Contact> contacts_by_id = new HashMap<Integer, Contact>();

    @Override
    public void setContacts(Collection<Contact> contacts) {
        // Check for null contacts
        if (contacts == null)
            throw new NullPointerException("Null contacts");

        this.contacts.clear();
        this.contacts.addAll(contacts);
    }

    @Override
    public Set<Contact> getContacts() {
        return new HashSet<Contact>(contacts);
    }

    @Override
    public void setFutureMeetings(Collection<FutureMeeting> meetings) {
        // Check for null meetings
        if (contacts == null)
            throw new NullPointerException("Null future meetings");

        this.future_meetings.clear();
        this.future_meetings.addAll(meetings);
    }

    @Override
    public Set<FutureMeeting> getFutureMeetings() {
        return new HashSet<FutureMeeting>(future_meetings);
    }

    @Override
    public void setPastMeetings(Collection<PastMeeting> meetings) {
        // Check for null meetings
        if (contacts == null)
            throw new NullPointerException("Null past meetings");

        this.past_meetings.clear();
        this.past_meetings.addAll(meetings);
    }

    @Override
    public Set<PastMeeting> getPastMeetings() {
        return new HashSet<PastMeeting>(past_meetings);
    }

    @Override
    public void writeToFilename(String filename) throws IOException {
        try {
            // Create a new document
            resetDocument();

            // Add the top-level element
            Element top_element = doc.createElement("ContactManagerData");
            doc.appendChild(top_element);

            // Add data under this top-level element
            addContactsUnderElement(top_element);
            addFutureMeetingsUnderElement(top_element);
            addPastMeetingsUnderElement(top_element);

            writeXmlToFile(filename);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            throw new IOException("Xml file could not be written to " + filename, e);
        }
    }

    /**
     * Resets the 'doc' to a blank document.
     *
     * @throws ParserConfigurationException
     */
    private void resetDocument() throws ParserConfigurationException {
        DocumentBuilderFactory doc_factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder doc_builder = doc_factory.newDocumentBuilder();
        doc = doc_builder.newDocument();
    }

    /**
     * Writes the prepared document to an xml file at 'filename'
     *
     * Based heavily on 'http://docs.oracle.com/javaee/1.4/tutorial/doc/JAXPXSLT4.html'.
     *
     * @param filename the filename to save to.
     * @throws TransformerException if the document couldn't be converted to xml.
     */
    private void writeXmlToFile(String filename) throws TransformerException {
        // Create transformer, which converts 'doc' into xml
        TransformerFactory transformer_factory = TransformerFactory.newInstance();
        Transformer transformer = transformer_factory.newTransformer();

        // Make the xml nicely formatted (with multiple lines and indentation)
        // (taken from 'http://stackoverflow.com/questions/5142632/java-dom-xml-file-create-have-no-tabs-or-whitespaces-in-output-file')
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        // Prepare input to transformer (ie. doc)
        DOMSource source = new DOMSource(doc);

        // Prepare output from transformer (ie. the new file)
        StreamResult output = new StreamResult(new File(filename));

        // Perform the transformation
        transformer.transform(source, output);
    }

    /**
     * Loads the xml file at 'filename' and converts it to a Document object
     * for further analysis.
     *
     * @param filename the location of the xml file.
     * @throws ParserConfigurationException if the xml file can't be parsed.
     * @throws IOException if the xml file can't be found.
     * @throws SAXException if the xml file can't be parsed.
     */
    private void loadXmlFromFile(String filename) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory doc_factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder doc_builder = doc_factory.newDocumentBuilder();
        doc = doc_builder.parse(new File(filename));
    }

    /**
     * Adds data under the given element.
     *
     * For example, if element represents a person ("<Person></Person>")
     * then given data_tag = "name" and data = "Bob", this method changes
     * element to "<Person><name>Bob</name></Person>"
     *
     * @param data_tag the name for the data type.
     * @param data the data to add.
     * @param element the element to add the data under.
     */
    private void addDataUnderElement(String data_tag, String data, Element element) {
        // Create an element for this data type
        Element data_element = doc.createElement(data_tag);

        // Fill element with data
        data_element.appendChild(doc.createTextNode(data));

        element.appendChild(data_element);
    }

    /**
     * Retrieves data from under the given element.
     *
     * For example, if element represents a person ("<Person><name>Bob</name></Person>")
     * then given data_tag = "name", this function would return "Bob".
     *
     * @param data_tag the name for the data type.
     * @param node the node to retrieve the data from.
     * @return the data in the node with the given data_tag.
     * @throws ParserConfigurationException if the data_tag was not found under the given node.
     */
    private String getDataUnderNode(String data_tag, Node node) throws ParserConfigurationException {
        // Get list of nodes with data_tag
        NodeList node_list = ((Element) node).getElementsByTagName(data_tag);

        if (node_list.getLength() == 0) {
            // if the data_tag wasn't found under the given node
            throw new ParserConfigurationException("Data tag " + data_tag + " wasn't found under " + node.getNodeValue());
        } else if (node_list.getLength() > 1) {
            // if too many matches for data_tag are found
            throw new ParserConfigurationException("Too many elements with data tag " + data_tag +
                    " were found under " + node.getNodeValue());
        }

        // Get relevant data_tag node
        Node data_tag_node = node_list.item(0);

        // Get data value node
        Node data_node = data_tag_node.getFirstChild();

        if (data_node == null) {
            // If there is no data under the data_tag (but the data_tag exists, eg.
            // "<name/>") then return empty string.
            return "";
        } else {
            if (data_node.getNextSibling() != null) {
                // If too many data values are found under the data_tag node
                throw new ParserConfigurationException("Too many data elements were found under data tag " + data_tag);
            }

            // Return value of node
            return data_node.getNodeValue();
        }
    }

    /**
     * Adds the given id to the given element as an attribute.
     *
     * For example, if element represents a contact ("<contact></contact>")
     * then with id = 5, this method changes element to "<contact id="5"></contact>"
     *
     * @param id the id to add as an attribute.
     * @param element the element to add the id attribute to.
     */
    private void addIdToElement(int id, Element element) {
        element.setAttribute("id", String.valueOf(id));
    }

    /**
     * Retrieves the id attribute from the given node (eg. if
     * the node represented "<contact id="5"></contact>", this would
     * return 5.
     *
     * @param node the node to retrieve the id attribute from.
     * @return the id of the node.
     */
    private int getIdFromNode(Node node) {
        return Integer.valueOf(((Element) node).getAttribute("id"));
    }

    /**
     * Adds all contacts under the given element.
     *
     * @param top_element the element under which to add all contact data.
     */
    private void addContactsUnderElement(Element top_element) {
        // Create top-level element for all contacts
        Element contacts_root = doc.createElement("Contacts");
        top_element.appendChild(contacts_root);

        // Add contact data under this element
        for (Contact contact : contacts) {
            // Create element for this contact
            Element contact_element = doc.createElement("contact");
            contacts_root.appendChild(contact_element);

            // Add contact data to this element
            addDataUnderElement("name", contact.getName(), contact_element);
            addDataUnderElement("notes", contact.getNotes(), contact_element);

            // Add contact id as attribute
            addIdToElement(contact.getId(), contact_element);
        }
    }

    /**
     * Adds all future meetings under the given element.
     *
     * @param top_element the element under which to add all future meetings data.
     */
    private void addFutureMeetingsUnderElement(Element top_element) {
        // Create top-level element for all future meetings
        Element future_meetings_root = doc.createElement("FutureMeetings");
        top_element.appendChild(future_meetings_root);

        // Add meeting data under this element
        for (FutureMeeting meeting : future_meetings) {
            // Create element for meeting
            Element meeting_element = createElementFromMeeting(meeting);

            // Add element to top-level future meetings element
            future_meetings_root.appendChild(meeting_element);
        }
    }

    /**
     * Adds all past meetings under the given element.
     *
     * @param top_element the element under which to add all past meetings data.
     */
    private void addPastMeetingsUnderElement(Element top_element) {
        // Create top-level element for all past meetings
        Element past_meetings_root = doc.createElement("PastMeetings");
        top_element.appendChild(past_meetings_root);

        // Add meeting data under this element
        for (PastMeeting meeting : past_meetings) {
            // Create element for meeting
            Element meeting_element = createElementFromMeeting(meeting);

            // Since this is a past meeting, we also need to add meeting notes
            addDataUnderElement("notes", meeting.getNotes(), meeting_element);

            // Add element to top-level future meetings element
            past_meetings_root.appendChild(meeting_element);
        }
    }

    /**
     * Creates an element that contains all of the data in the given meeting object.
     *
     * @param meeting the meeting to take the data from.
     * @return a new element containing all data from the given meeting.
     */
    private Element createElementFromMeeting(Meeting meeting) {
        // Create element for meeting
        Element meeting_element = doc.createElement("meeting");

        // Add date to meeting_element
        addDataUnderElement("date", CalendarHelper.getSimpleCalendarString(meeting.getDate()), meeting_element);

        // Add id to meeting_element as attribute
        addIdToElement(meeting.getId(), meeting_element);

        // Add a sub-element for contacts
        Element contacts_element = doc.createElement("contacts");
        meeting_element.appendChild(contacts_element);

        // Add contact ids to contacts_element
        for (Contact contact : meeting.getContacts()) {
            // Create element for the contact
            Element contact_element = doc.createElement("contact");
            contacts_element.appendChild(contact_element);

            // Add contact's id as attribute
            addIdToElement(contact.getId(), contact_element);
        }

        return meeting_element;
    }

    @Override
    public void loadFromFilename(String filename) throws IOException {
        try {
            // Clear data in this store
            contacts.clear();
            future_meetings.clear();
            past_meetings.clear();

            // Load xml from file
            loadXmlFromFile(filename);

            // Load data into store
            loadContacts();
            loadFutureMeetings();
            loadPastMeetings();


        } catch (ParserConfigurationException e) {
            throw new IllegalArgumentException("Xml file could not be parsed", e);
        } catch (SAXException e) {
            throw new IllegalArgumentException("Xml file could not be parsed", e);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Calendar date could not be parsed", e);
        }
    }

    /**
     * Returns contact objects from the given node that represents a meeting
     * (ie. returns the list of attendees for a given meeting).
     *
     * @param meeting_node the node representing a meeting.
     * @return the contact objects who attended the meeting.
     */
    private Set<Contact> getContactsFromMeetingNode(Node meeting_node) {
        Set<Contact> meeting_contacts = new HashSet<Contact>();

        // Get contacts container node
        Node contacts_node = ((Element) meeting_node).getElementsByTagName("contacts").item(0);

        // For each contact in this meeting...
        for (Node contact_node = contacts_node.getFirstChild();
             contact_node != null;
             contact_node = contact_node.getNextSibling()) {

            if (contact_node.getNodeType() == Node.ELEMENT_NODE) {
                int contact_id = getIdFromNode(contact_node);
                meeting_contacts.add(contacts_by_id.get(contact_id));
            }
        }

        return meeting_contacts;
    }

    /**
     * Loads future meetings from the loaded xml file.
     *
     * @throws ParseException if the calendar date couldn't be parsed.
     * @throws ParserConfigurationException if the xml couldn't be parsed.
     */
    private void loadFutureMeetings() throws ParserConfigurationException, ParseException {
        Node top_level_meetings_node = doc.getElementsByTagName("FutureMeetings").item(0);

        // For each meeting under top_level_meetings_node...
        for (Node meeting_node = top_level_meetings_node.getFirstChild();
             meeting_node != null;
             meeting_node = meeting_node.getNextSibling()) {

            if (meeting_node.getNodeType() == Node.ELEMENT_NODE) {
                // Get contact id from attribute
                int id = getIdFromNode(meeting_node);

                // Get rest of contact data
                Calendar date = CalendarHelper.getCalendarFromString(getDataUnderNode("date", meeting_node));
                Set<Contact> meeting_contacts = getContactsFromMeetingNode(meeting_node);

                // Create meeting object
                future_meetings.add(new FutureMeetingImpl(id, date, meeting_contacts));
            }
        }
    }

    /**
     * Loads past meetings from the loaded xml file.
     *
     * @throws ParseException if the calendar date couldn't be parsed.
     * @throws ParserConfigurationException if the xml couldn't be parsed.
     */
    private void loadPastMeetings() throws ParseException, ParserConfigurationException {
        Node top_level_meetings_node = doc.getElementsByTagName("PastMeetings").item(0);

        // For each meeting under top_level_meetings_node...
        for (Node meeting_node = top_level_meetings_node.getFirstChild();
             meeting_node != null;
             meeting_node = meeting_node.getNextSibling()) {

            if (meeting_node.getNodeType() == Node.ELEMENT_NODE) {
                // Get contact id from attribute
                int id = getIdFromNode(meeting_node);

                // Get rest of contact data
                Calendar date = CalendarHelper.getCalendarFromString(getDataUnderNode("date", meeting_node));
                String notes = getDataUnderNode("notes", meeting_node);
                Set<Contact> meeting_contacts = getContactsFromMeetingNode(meeting_node);

                // Create meeting object
                past_meetings.add(new PastMeetingImpl(id, date, meeting_contacts, notes));
            }
        }
    }

    /**
     * Loads contacts from the loaded xml file.
     *
     * @throws ParserConfigurationException if the xml couldn't be parsed.
     */
    private void loadContacts() throws ParserConfigurationException {
        Node top_level_contacts_node = doc.getElementsByTagName("Contacts").item(0);

        // For each contact_node under top_level_contacts_node...
        for (Node contact_node = top_level_contacts_node.getFirstChild();
             contact_node != null;
             contact_node = contact_node.getNextSibling()) {

            if (contact_node.getNodeType() == Node.ELEMENT_NODE) {
                // Get contact id from attribute
                int id = getIdFromNode(contact_node);

                // Get rest of contact data
                String name = getDataUnderNode("name", contact_node);
                String notes = getDataUnderNode("notes", contact_node);

                // Create contact object
                Contact contact = new ContactImpl(id, name);
                contact.addNotes(notes);
                contacts.add(contact);
            }
        }

        // Repopulate 'contacts_by_id'
        contacts_by_id.clear();
        for (Contact contact : contacts) {
            contacts_by_id.put(contact.getId(), contact);
        }
    }

}
