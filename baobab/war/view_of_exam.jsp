<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html>
<html lang="ru">
  <jsp:include page="include_header.jsp"></jsp:include>
  <body>
    <div class="container-fluid">
      <div class="row-fluid page-header">
        <div class="span10">
          <h3>${exam_name}</h3>
          <h4>${owners}</h4>
          <c:forEach var="event" items="${events}">
            <h5>${event}</h5>
          </c:forEach>
       </div> 
       <div>
        <iframe src="http://docs.google.com/viewer?url=${url}&embedded=true" width="600" height="780" style="border: none;"></iframe>
       </div>       
      </div>
   </div>
 </body>
</html>