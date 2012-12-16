<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*"%>
<%@ page import="com.google.common.collect.*"%>

<% LinkedListMultimap<String, String> groupsList = (LinkedListMultimap<String, String>) request.getAttribute("group_list"); %>

<!doctype html>
<html lang="ru">
  <jsp:include page="include_header.jsp"></jsp:include>
  <body>
    <div class="container-fluid">
      <div class="row-fluid page-header">
        <div class="span10">
          <h2>Расписание зимней сессии</h2>
          <%
          if (groupsList != null) {
            out.print("Groups List isn't null");
          }
          %>
        </div>        
      </div>

      <div class="row-fluid">
        <div class="span3">
          <div class="row-fluid">
	          <div class="well sidebar-nav">
	            <ul class="nav nav-list">
	               
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

              <div class="tab-pane active" id="g241">
	              <h2>241 группа</h2>
	              <div class="row-fluid">
	                <div class="span2" class="datetime">
	                  <h4>11 янв</h4>
	                  <h5 class="time">10:00</h5>
	                </div>
	                <div class="span7">
	                  <h4>Математический анализ</h4>
	                  <p>Макаров, ауд. 05</p>
	                </div>
	              </div>
	              <div class="row-fluid">
	                <div class="span6">
	                  <hr>
	                  <p>
	                    <a class="btn btn-small" a href="#"><i class="icon-download"></i> PDF</a>
	                  </p>
	                </div>
	              </div>
	            </div>

	            <div class="tab-pane" id="g242">
                <h2>242 группа</h2>
                <div class="row-fluid">
                  <div class="span2" class="datetime">
                    <h4>11 янв</h4>
                    <h5 class="time">10:00</h5>
                  </div>
                  <div class="span7">
                    <h4>Математический анализ</h4>
                    <p>Макаров, ауд. 05</p>
                  </div>
                </div>
                <div class="row-fluid">
                  <div class="span6">
                    <hr>
                    <p>
                      <a class="btn btn-small" a href="#"><i class="icon-download"></i> PDF</a>
                    </p>
                  </div>
                </div>
              </div>

              <div class="tab-pane" id="g243">
                <h2>243 группа</h2>
                <div class="row-fluid">
                  <div class="span2" class="datetime">
                    <h4>11 янв</h4>
                    <h5 class="time">10:00</h5>
                  </div>
                  <div class="span7">
                    <h4>Математический анализ</h4>
                    <p>Макаров, ауд. 05</p>
                  </div>
                </div>
                <div class="row-fluid">
                  <div class="span6">
                    <hr>
                    <p>
                      <a class="btn btn-small" a href="#"><i class="icon-download"></i> PDF</a>
                    </p>
                  </div>
                </div>
              </div>

              <div class="tab-pane" id="g244">
                <h2>244 группа</h2>
                <div class="row-fluid">
                  <div class="span2" class="datetime">
                    <h4>11 янв</h4>
                    <h5 class="time">10:00</h5>
                  </div>
                  <div class="span7">
                    <h4>Математический анализ</h4>
                    <p>Макаров, ауд. 05</p>
                  </div>
                </div>
                <div class="row-fluid">
                  <div class="span6">
                    <hr>
                    <p>
                      <a class="btn btn-small" a href="#"><i class="icon-download"></i> PDF</a>
                    </p>
                  </div>
                </div>
              </div>

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
