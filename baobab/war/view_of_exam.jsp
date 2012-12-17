<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html>
<html lang="ru">
  <jsp:include page="include_header.jsp"></jsp:include>
  <body>
      <div class="row-fluid page-header">
        <div class="span10">
          <h2>Информация по экзаменам</h2>
        </div>        
      </div>
    <div class="container-fluid">
      <div class="row-fluid">
        <div class="span3">
          <div class="row-fluid">
	          <div class="well sidebar-nav">
	            <ul class="nav nav-list">
	              <li class="nav-header">Экзамены</li>              
	                <c:forEach var="exam" items="${exams}">
                       <p><a data-toggle="tab" href="?id=${exam.ID}"> ${exam.name}</a></p>
                    </c:forEach>
	            </ul><!--/.nav-->
	          </div><!--/.well-->
          </div>
       </div><!--/span-->
        <div class="span9">
          <div class="row-fluid">
            <div class="tab-pane active" >
	          <div class="row-fluid">
                <div class="span4">
	              <h3>${exam_name}</h3>
	              <h4>${owners}</h4>                    
                  <c:forEach var="att" items="${attendees}">
                    <h5>${att}</h5>  
                  </c:forEach>               
                </div>
                <div class="span3">
                  <c:if test="${url != null}">
                    <iframe src="http://docs.google.com/viewer?url=${url}&embedded=true" width="670" height="780" style="border: none;"></iframe>
                  </c:if>
	            </div>
	          </div>
	        </div>
	      </div>	            
	    </div>
	  </div>
    </div>
  </body>
</html>
	  
	            
        