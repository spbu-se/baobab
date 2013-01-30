<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!doctype html>
<html lang="ru">
  <jsp:include page="include_header.jsp" />
  <body>
    <c:set var="calendarList" scope="request" value="${calendarList}" />
    <jsp:include page="include_navbar.jsp" />

    <div class="container-fluid">
      <div class="row-fluid page-header">
        <div class="span10">
          <h2>Экзамены ${scheduleDate}</h2>
        </div>
      </div>
    </div><!--/.fluid-container-->

    <div class="container-fluid">
      <div class="row-fluid">
        <div class="span8">
          <table class="table table-hover">
            <thead>
              <tr><th>Ауд.</th><th>Предмет</th><th>Группы</th></tr>
            </thead>
            <tbody>
              <c:forEach items="${entries}" var="entry">
                <tr>
                  <td><b>${entry.auditorium}</b></td>
                  <td>${entry.title}</td>
                  <td>${entry.attendees}</td>
                </tr>
              </c:forEach>
            </tbody>  
          </table>
        </div>
      </div>
    </div><!--/.fluid-container-->

  <jsp:include page="include_footer.jsp"></jsp:include>
  </body>
</html>
