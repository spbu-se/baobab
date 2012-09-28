package ru.spbu.math.baobab.server;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bardsoftware.server.AppUrlService;
import com.bardsoftware.server.auth.AuthService;

public class LandingServlet extends HttpServlet {
  private final AppUrlService urlService = new AppUrlService(DevMode.IS_ENABLED);
  private final AuthService authService = new AuthService(urlService.getDomainName());
  
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    authService.setupUserAndMaintenance(req);
    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
    dispatcher.forward(req, resp);
  }
}
