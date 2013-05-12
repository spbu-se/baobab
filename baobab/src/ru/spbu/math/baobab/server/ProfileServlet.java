package ru.spbu.math.baobab.server;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Profile Servlet. Connects web interface form with Profile.
 *  
 * @author aoool
 */
@SuppressWarnings("serial")
public class ProfileServlet extends HttpServlet {
  
  private final ProfileExtent myProfileExtent = new ProfileExtent();
  private final static String alertNotFilled = "Хотя бы одно поле должно быть заполнено";
  private final static String alertSuccessCreate = "Настройки профиля успешно сохранены";
  private final static String alertSuccessChange = "Настройки профиля успешно изменены";
  
  private String changeProfile(String userKey, String email, String academicGroupId, String chairId) {
    if((email == null) && (academicGroupId == null) && (chairId == null)) {
      return alertNotFilled;
    }
    Profile profile = myProfileExtent.find(userKey);
    if(profile == null) {
      try {
        myProfileExtent.createWithIds(userKey, email, academicGroupId, chairId);
      } catch (IllegalArgumentException e) {
        return e.getMessage();
      }
      return alertSuccessCreate;
    } else {
      try {
        if(academicGroupId != "") {
          profile.setAcademicGroup(academicGroupId);
        }
        if(chairId != "") {
          profile.setChair(chairId);
        }
        if(email != "") {
          profile.setEmail(email);
        }
      } catch (IllegalArgumentException e) {
        return e.getMessage();
      }
      return alertSuccessChange;
    }
  }
  
  @Override 
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    String userId = null;
    Cookie[] cookies = null;
    cookies = req.getCookies();
    if (cookies != null) {
        for (int i = 0; i < cookies.length; i++) {
            if(cookies[i].getName().equals("userId")) {
              userId = cookies[i].getValue();
            }
        }
    };
    String email = req.getParameter("email");
    String academicGroupId = req.getParameter("academic_group");
    String chairId = req.getParameter("department");
    String result = changeProfile(userId, email, academicGroupId, chairId);
    boolean error = !((result == alertSuccessCreate) || (result == alertSuccessChange));
    req.setAttribute("error", error);
    req.setAttribute("alert", result);
    req.setAttribute("actionDone", true);
    RequestDispatcher view = req.getRequestDispatcher("/profile.jsp");
    view.forward(req, resp);
  }
  
}
