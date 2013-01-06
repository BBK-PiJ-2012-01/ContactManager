package contacts;

import contacts.helper.CalendarHelper;
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
 * User: Sam Wright
 * Date: 05/01/2013
 * Time: 20:03
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
    public void writeToFilename(String filename) {
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
            e.printStackTrace();
        }
    }

    /**
     * Taken from 'http://docs.oracle.com/javaee/1.4/tutorial/doc/JAXPXSLT4.html'
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

    private void resetDocument() throws ParserConfigurationException {
        DocumentBuilderFactory doc_factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder doc_builder = doc_factory.newDocumentBuilder();
        doc = doc_builder.newDocument();
    }

    private void addDataUnderElement(String data_tag, String data, Element element) {
        // Create an element for this data type
        Element data_element = doc.createElement(data_tag);

        // Fill element with data
        data_element.appendChild(doc.createTextNode(data));

        element.appendChild(data_element);
    }

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

    private void addIdToElement(int id, Element element) {
        element.setAttribute("id", String.valueOf(id));
    }

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
    public void loadFromFilename(String filename) {
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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

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

    private void loadFutureMeetings() throws ParseException {
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

    private void loadPastMeetings() throws ParseException {
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

    private void loadContacts() {
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

    private int getIdFromNode(Node node) {
        return Integer.valueOf(((Element) node).getAttribute("id"));
    }

    private String getDataUnderNode(String data_tag, Node node) {
        // Get relevant data_tag node
        Node data_tag_node = ((Element) node).getElementsByTagName(data_tag).item(0);

        // Get data value node
        Node data_node = data_tag_node.getFirstChild();

        // Return value of node
        return data_node.getNodeValue();
    }

    private void loadXmlFromFile(String filename) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory doc_factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder doc_builder = doc_factory.newDocumentBuilder();
        doc = doc_builder.parse(new File(filename));
        //doc.normalize();
    }
}
