package ru.spbu.math.baobab.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TeamServlet extends HttpServlet {
  protected String [] myMembers = new String [] {
	"Dmitry Barashev", "Anton Bondarev", "Dmitry Vadimovich Luciv", "Alexander Gudulin",
	"Ageev Denis", "Alexandr Lapitckiy", "Vita Loginova"
  };
	
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.getWriter().println("The Team:");
    for(String member : myMembers)
      resp.getWriter().println(" * " + member);
  }
}
