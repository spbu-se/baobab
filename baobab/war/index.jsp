<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Тестовый Баобаб</title>
  </head>
<body>
  <c:if test="${empty user_id}">
	  <div id=login>
	  <div id='login_widget'>
      <a href="/auth/twitter">Зайти через Твиттер</a><br>
      <a href="/auth/vkontakte">Зайти через Контактик</a><br>
	  </div>
	  </div>
  </c:if>
  <c:if test="${not empty user_id}">
    Привет <c:out value="${nickname}"/>, твой ID: <c:out value="${user_id}"/>. Живи теперь с этим как хочешь, да
    <div></div><a href="/auth/logout">Выйти</a></div> 
  </c:if>
  
</body>
</html>

