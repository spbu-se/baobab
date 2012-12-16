<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*"%>
<%@ page import="ru.spbu.math.baobab.model.*"%>
<%@ page import="ru.spbu.math.baobab.server.*"%>
<%@ page import="com.google.common.collect.*"%>
<%@ page import="com.google.common.base.*"%>
<%@ page import="java.text.*"%>

<%
Multimap<String, Attendee> groupsList = (Multimap<String, Attendee>) request.getAttribute("groups_list");
Multimap<Attendee, Event> schedule = (Multimap<Attendee, Event>) request.getAttribute("schedule");
DateFormat dfDate = new SimpleDateFormat("dd MMM", new Locale("ru"));
DateFormat dfTime = new SimpleDateFormat("HH:mm", new Locale("ru"));
%>
<%!
public String getOwnersNamesList(Collection<Attendee> owners) {
  List<String> names = Lists.newArrayList();
  for (Attendee owner : owners) {
    names.add(owner.getName());
  }
  Collections.sort(names);
  return Joiner.on(", ").join(names);
}
%>

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
              <% if (groupsList != null) { %>
                 <% for (String course : groupsList.keySet()) { %>
                   <li class="nav-header"><%= course %> курс</li>
                   <p>
                   <% for (Attendee group : groupsList.get(course)) { %>
                     <a data-toggle="tab" href="#g<%= group.getName() %>"><%= group.getName() %></a>
                   <% } %>
                   </p>
                 <% } %>
              <% } %>
              </ul><!--/.nav-->
            </div><!--/.well-->
          </div>
          <div class="row-fluid">
            <div class="span4"><a href="/data/edit" class="link">Редактировать</a></div>
          </div>
        </div><!--/span-->
        <div class="span9">
          <div class="row-fluid">
            <div class="span9 tab-content">
              <% if (groupsList != null) { %>
                <% for (Attendee group : groupsList.values() ) { %>
                <div class="tab-pane active" id="g<%= group.getName() %>">
                  <h2><%= group.getName() %> группа</h2>
                  <% for (Event event : schedule.get(group)) { %>
                  <div class="row-fluid">
                    <div class="span2" class="datetime">
                      <h4><%= dfDate.format(event.getStartDate()) %></h4>
                      <h5 class="time"><%= dfTime.format(event.getStartDate()) %></h5>
                    </div>
                    <div class="span7">
                      <h4><%= event.getTopic().getName() %></h4>
                      <p><%= getOwnersNamesList(event.getTopic().getOwners()) %>, ауд. <%= event.getAuditorium().getID() %></p>
                    </div>
                  </div>
                  <% } %>
                  <div class="row-fluid">
                    <div class="span6">
                      <hr>
                      <p>
                        <a class="btn btn-small" a href="#"><i class="icon-download"></i> PDF</a>
                      </p>
                    </div>
                  </div>
                </div>
                <% } %>
              <% } %>

            </div><!--/span-->
          </div><!--/row-->
        </div><!--/span-->
      </div><!--/row-->

    </div><!--/.fluid-container-->

    <!-- Placed at the end of the document so the pages load faster -->
    <script src="bootstrap/js/jquery.min.js"></script>
    <script src="bootstrap/js/bootstrap-tab.js"></script>
    <script type="text/javascript">
      var activeTab = {};

      $(function() { 
        activeTab = $(('.nav-list a[href=' + location.hash + ']').replace("#", "#g"));
        activeTab && activeTab.tab('show');
        activeTab.addClass('label label-info active');
      });

      $('.nav-list a').on('shown', function (e) {
        activeTab.removeClass('label label-info active');
        $(this).addClass('label label-info active');
        activeTab = $(this);

        document.location.hash = e.target.hash.replace("#g", "#");
      });
    </script>
  </body>
</html>
