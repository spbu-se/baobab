<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="navbar navbar-fixed-top">
  <div class="navbar-inner">
    <div class="container">
      <a class="brand" href="/">Baobab</a>
      <div class="nav-collapse collapse navbar-responsive-collapse">
        <ul class="nav">
          <c:forEach var="calendar" items="${calendarList}">
            <li><a href="/calendar/${calendar.ID}">${calendar.ID}</a></li>
          </c:forEach>
        </ul>
      </div>
    </div>
  </div>
</div>