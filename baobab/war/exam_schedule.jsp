<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="ru">
  <head>
    <meta charset="utf-8">
    <title>Расписание зимней сессии</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <style type="text/css">
      body {
        padding-top: 10px;
        padding-bottom: 40px;
      }
      .sidebar-nav {
        padding: 9px 0;
      }
      .sidebar-nav a {
        margin-right: 4px;
      }
      .datetime {
        width: 60px;
      }
      .time {
        color: #999;
      }
    </style>
    <link href="bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet">

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="bootstrap/js/html5.js"></script>
    <![endif]-->
  </head>

  <body>
    <div class="container-fluid">
      <div class="row-fluid">
        <div class="page-header">
          <h1>Расписание зимней сессии</h1>
        </div>
      </div>

      <div class="row-fluid">
        <div class="span3">
          <div class="well sidebar-nav">
            <ul class="nav nav-list">
              <li class="nav-header">1 курс</li>
              <li class="nav-header">2 курс</li>
              <p>
                <a data-toggle="tab" href="#g241">241</a>
                <a data-toggle="tab" href="#g242">242</a>
                <a data-toggle="tab" href="#g243">243</a>
                <a data-toggle="tab" href="#g244">244</a>
              </p>
              <li class="nav-header">3 курс</li>              
              <li class="nav-header">4 курс</li>
            </ul><!--/.nav-->
          </div><!--/.well-->
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
