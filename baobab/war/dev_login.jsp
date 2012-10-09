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
    <form action="/auth/dev" method="get">
    <table>
    <tr><td>ID</td><td><input type="text" name="id"/></td></tr>
    <tr><td>Name</td><td><input type="text" name="name"/></td></tr>
    <tr><td></td><td><input type="submit" name="name"/></td></tr>        
    </table>
    </form>
  </c:if>
</body>
</html>

