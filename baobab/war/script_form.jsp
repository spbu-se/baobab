<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<%@ include file="../include_header.jsp" %>
<html>
<body>
  <div class="container-fluid">
    <div class="row-fluid">
      <div class="page-header span12">
        <h2>Редактирование данных</h2>
      </div>
    </div>
    <div class="row-fluid">
	    <div class="span3">
	      <h4>У нас уже есть</h4>
        <ul class="well nav nav-list">
          <li class="nav-header">Группы</li>
          <li><p>${group_list}</p></li>
          <li class="nav-header">Преподаватели</li>
          <li><p>${teacher_list}</p></li>
          <li class="nav-header">Аудитории</li>
          <li><p>${auditorium_list}</p></li>
          <li class="nav-header">Временные слоты</li>
          <li><p>${time_slot_list}</p></li>
        </ul>
	    </div>
	    <div class="span9">
		    <form action="/data/edit" method="post">
		      <div class="row-fluid">
		        <div class="span12">
		          <textarea class="span12" rows="10" name="script"></textarea>
		        </div>
		      </div>
		      <div class="row-fluid">
		            <span class="span10">${result}</span>
		            <span class="span2"><input type="submit" class="btn btn-primary" style="float: right" value="Поехали!"/></span>
		      </div>
		    </form>
	    </div>  
	  </div>
	</div>
</body>
</html>