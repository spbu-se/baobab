<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<%@ include file="../include_header.jsp" %>
<html>
<body>
  <div class="container-fluid">
    <div class="row-fluid">
      <div class="page-header span12">
        <h2>Парсер расписания</h2>
      </div>
    </div>
    <div class="row-fluid">
      <div class="row-fluid">
        <form class="navbar-form pull-left" action="/data/parser" method="post">
          <input type="text" name="url" class="input-xxlarge">
          <button type="submit" class="btn">Submit</button>
        </form>
      </div>
      <div class="row-fluid">
        <p>
          <pre>${script_text}</pre>
        </p>
      </div>
    </div>
  </div>

</body>
</html>
