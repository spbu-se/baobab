<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!doctype html>
<html lang="ru">
  <jsp:include page="include_header.jsp"></jsp:include>
  <body>
    <div class="container-fluid">
      <div class="row-fluid page-header">
        <div class="span10">
          <h2>Расписание зимней сессии</h2>
        </div>
      </div>
  
      <div class="row-fluid">
        <div class="span3">
          <div class="row-fluid">
            <div class="well sidebar-nav">
              <ul class="nav nav-list">
                <c:if test="${groupsList != null}">
                  <c:forEach items="${groupsList}" var="course_group">
                    <li class="nav-header">${course_group.key} курс</li>
                    <p>
                      <c:forEach var="group" items="${course_group.value}">
                        <a data-toggle="tab" href="#g${group.name}">${group.name}</a>
                      </c:forEach>
                    </p>
                  </c:forEach>
                </c:if>
              </ul><!--/.nav-->
            </div><!--/.well-->
          </div>
          <div class="row-fluid">
            <div class="span12">
              <a href="/data/edit" class="link">Редактировать</a>&nbsp;|&nbsp;<a href="/exam/all/pdf" class="link">Скачать PDF</a>
            </div>
          </div>
        </div><!--/span-->
        <div class="span9">
          <div class="row-fluid">
            <div class="span9 tab-content">
              <c:if test="${groupsList != null}">
                <c:forEach items="${groupsList}" var="course_group">
                  <c:forEach items="${course_group.value}" var="group">
                    <div class="tab-pane active" id="g${group.name}">
                      <h2>
                        <c:out value="${group.name}" />
                        группа
                      </h2>
                      <c:forEach var="event" items="${schedule[group]}">
                        <div class="row-fluid">
                          <div class="span2" class="datetime">
                            <h4>
                              <fmt:setLocale value="ru_RU" scope="session"/>
                              <fmt:formatDate pattern="dd MMM" value="${event.startDate}" />
                            </h4>
                            <h5 class="time">
                              <fmt:formatDate pattern="HH:mm" value="${event.startDate}" />
                            </h5>
                          </div>
                          <div class="span7">
                            <h4>
                              <a href="/exam?id=${event.topic.ID}"> <c:out value="${event.topic.name}" /></a>
                            </h4>
                            <p>
                              <c:forEach items="${event.topic.owners}" var="owner">${owner.name}, </c:forEach>
                              ауд. ${event.auditorium.ID}
                            </p>
                          </div>
                        </div>
                      </c:forEach>
                      <div class="row-fluid">
                        <div class="span6">
                          <hr>
                          <p>
                            <a class="btn btn-small" a href="#"><i
                              class="icon-download"></i> PDF</a>
                          </p>
                        </div>
                      </div>
                    </div>
                  </c:forEach>
                </c:forEach>
              </c:if>
  
            </div><!--/span-->
          </div><!--/row-->
        </div><!--/span-->
      </div><!--/row-->
  
    </div><!--/.fluid-container-->

    <script src="bootstrap/js/bootstrap-tab.js"></script>
    <script type="text/javascript">
      var activeTab = {};
  
      $(function() {
        activeTab = $(('.nav-list a[href=' + location.hash + ']').replace("#", "#g"));
        activeTab && activeTab.tab('show');
        activeTab.addClass('label label-info active');
      });
  
      $('.nav-list a').on('shown', function(e) {
        activeTab.removeClass('label label-info active');
        $(this).addClass('label label-info active');
        activeTab = $(this);
  
        document.location.hash = e.target.hash.replace("#g", "#");
      });
    </script>
  </body>
</html>
