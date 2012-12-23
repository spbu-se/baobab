package ru.spbu.math.baobab.server.auth;

import ru.spbu.math.baobab.server.DevMode;

import com.bardsoftware.server.auth.AuthServlet;

public class BaobabAuthServlet extends AuthServlet {

  public BaobabAuthServlet() {
    super(DevMode.IS_ENABLED);
  }
}
