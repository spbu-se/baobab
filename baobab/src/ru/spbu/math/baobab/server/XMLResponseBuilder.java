package ru.spbu.math.baobab.server;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Event;

public class XMLResponseBuilder {
  private DocumentBuilder _builder;

  public XMLResponseBuilder() {
    try {
      _builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Document buildAttendeeNameList(Collection<Attendee> col, String listName) {
    Document document = _builder.newDocument();

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
    Document document = _builder.newDocument();

    Element rootElement = document.createElement("Events");
    rootElement.setAttribute("name", listName);
    document.appendChild(rootElement);

    for (Event event : col) {
      Element eventElement = document.createElement("Event");
      eventElement.setAttribute("name", event.getTopic().getName());
      eventElement.setAttribute("auditorium", event.getAuditorium().getID());

      // UK locale is needed for generating date in dd/mm/yy format
      DateFormat ukLocale = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.UK);
      eventElement.setAttribute("date", ukLocale.format(event.getStartDate()));
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
    Document document = _builder.newDocument();

    Element rootElement = document.createElement("Error");
    rootElement.setAttribute("name", name);
    rootElement.setAttribute("description", description);
    document.appendChild(rootElement);

    return document;
  }
}
