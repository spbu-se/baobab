package ru.spbu.math.baobab.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Event;

/**
 * Class for building xml response from specified data
 * 
 * @author cons
 */
public class XMLResponseBuilder {
  private DocumentBuilder myBuilder;

  public XMLResponseBuilder() throws ParserConfigurationException {
    myBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
  }

  public Document buildAttendeeNameList(Collection<Attendee> col, String listName) {
    Document document = myBuilder.newDocument();

    Element rootElement = document.createElement("Attendees");
    rootElement.setAttribute("name", listName);
    document.appendChild(rootElement);

    for (Attendee attendee : col) {
      Element element = document.createElement("Attendee");
      element.setAttribute("name", attendee.getName());
      rootElement.appendChild(element);
    }

    return document;
  }

  public Document buildEventList(Collection<Event> col, String listName) {
    Document document = myBuilder.newDocument();

    Element rootElement = document.createElement("Events");
    rootElement.setAttribute("name", listName);
    document.appendChild(rootElement);

    for (Event event : col) {
      Element eventElement = document.createElement("Event");
      eventElement.setAttribute("name", event.getTopic().getName());
      eventElement.setAttribute("auditorium", event.getAuditorium().getID());

      DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy hh:mm");
      eventElement.setAttribute("date", dateFormat.format(event.getStartDate()));
      eventElement.setAttribute("id", event.getTopic().getID());
      eventElement.setAttribute("type", event.getTopic().getType().toString());
      rootElement.appendChild(eventElement);

      for (Attendee owner : event.getTopic().getOwners()) {
        Element ownerElement = document.createElement("Owner");
        ownerElement.setAttribute("name", owner.getName());
        eventElement.appendChild(ownerElement);
      }
    }

    return document;
  }

  public Document buildError(String name, String description) {
    Document document = myBuilder.newDocument();

    Element rootElement = document.createElement("Error");
    rootElement.setAttribute("name", name);
    rootElement.setAttribute("description", description);
    document.appendChild(rootElement);

    return document;
  }
}
