<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!doctype html>
<html lang="ru">
<jsp:include page="include_header.jsp" />
<body>
  <div class="container-fluid">
    <div class="row-fluid page-header">
      <div class="span10">
        <h2>${exam_name}</h2>
      </div>
    </div>
  </div>
 
  <div class="container-fluid">
    <div class="row-fluid">
      <div class="span3">
        <div class="row-fluid">
          <div class="well sidebar-nav">
            <ul class="nav nav-list">
              <li class="nav-header">Преподаватель</li>
              <p>
                <b>${owners}</b>
              </p>
              <li class="nav-header">Экзамены</li>
              <p>
                <c:forEach var="att" items="${attendees}">
                  <b>${att}</b><br>
                </c:forEach>
              </p>
            </ul>
            <!--/.nav-->
          </div>
          <!--/.well-->
        </div>
      </div>
      <!--/span-->
      <div class="span9">
        <div class="row-fluid">
          <div class="tab-pane active">
            <div class="row-fluid">
              <c:if test="${url != null}">
                <iframe src="http://docs.google.com/viewer?url=${url}&embedded=true" width="670" height="780" style="border: none;"></iframe>
              </c:if>
            </div>
          </div>
        </div>
        <div class="row-fluid">
          <div class="span9">
            <div id="vk_comments"></div>
            <script type="text/javascript">
              VK.Widgets.Comments('vk_comments', {limit: 10, width: "670", attach: "*", autoPublish: "0"});
            </script>
          </div>
        </div>
      </div>
    </div>
  </div>
</body>
</html>
