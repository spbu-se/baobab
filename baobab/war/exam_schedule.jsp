<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
	              <li class="nav-header">1 курс</li>
	              <li class="nav-header">2 курс</li>
	              <p>
	                <a href="#">241</a>
	                <a href="#">242</a>
	                <span class="label label-info active">243</span>
	                <a href="#">244</a>
	              </p>
	              <li class="nav-header">3 курс</li>              
	              <li class="nav-header">4 курс</li>
	            </ul><!--/.nav-->
	          </div><!--/.well-->
          </div>
          <div class="row-fluid">
            <div class="span4"><a href="/data/edit" class="link">Редактировать</a></div>
          </div>
        </div><!--/span-->
        <div class="span9">
          <div class="row-fluid">
            <div class="span9">
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
            </div><!--/span-->
          </div><!--/row-->
        </div><!--/span-->
      </div><!--/row-->

    </div><!--/.fluid-container-->

    <!-- Placed at the end of the document so the pages load faster -->
    <script src="bootstrap/js/jquery.min.js"></script>
  </body>
</html>
