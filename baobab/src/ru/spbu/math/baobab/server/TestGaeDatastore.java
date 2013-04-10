package ru.spbu.math.baobab.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Attendee.Type;
import ru.spbu.math.baobab.model.AttendeeExtent;

/**
 * Testing ground for classes which use GAE datastore.
 * 
 * @author aoool
 */
@SuppressWarnings("serial")
public class TestGaeDatastore extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // testing Profile and ProfileExtent (it may throw exceptions on other computers because datastores can be
    // different).
    ProfileExtent profileExtent = new ProfileExtent();
    Profile profile = profileExtent.find("test_profile_3");
    AttendeeExtent attendeeExtent = new AttendeeExtentImpl();
    Attendee attendee = attendeeExtent.create("testAttendee", "testAttendee", Type.ACADEMIC_GROUP);
    Attendee attendeeChair = attendeeExtent.create("testAttendeeChair", "testAttendeChair", Type.CHAIR);
    try {
      profile = profileExtent.create("test_profile_1", "test@test.test", attendee, attendeeChair);
    } catch (Exception e) {
      resp.getWriter().println(e.getMessage());
    }
    if (profile != null) {
      resp.getWriter().println(profile.getEmail());
      profile.setEmail("test@test.test");
      resp.getWriter().println(profile.getEmail());
    }
    profile = profileExtent.findByEmail("test@test.test");
    resp.getWriter().println(profile.getEmail());
    profile = profileExtent.find("test_profile_5");
    if ((profile != null) && (profile.getAcademicGroupId() != null)) {
      resp.getWriter().println(profile.getAcademicGroupId());
    }
    if ((profile != null) && (profile.getChairId() != null)) {
      resp.getWriter().println(profile.getChairId());
    }
  }
}
