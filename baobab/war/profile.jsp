<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page import="ru.spbu.math.baobab.server.Profile" %>
<%@ page import="ru.spbu.math.baobab.server.ProfileExtent" %>

<!doctype html>
<html lang="ru">
<jsp:include page="include_header.jsp" />
<body>
	<c:set var="calendarList" scope="request" value="${calendarList}" />
	<jsp:include page="include_navbar.jsp" />




	<div class="container">

		<div class="well">
			<form id="profile" class="form-horizontal" method="get" action="/profile">
				<legend>Профиль</legend>

				<div class="control-group">
					<label class="control-label">Email</label>
					<div class="controls">
						<div class="input-prepend">
							<span class="add-on"><i class="icon-envelope"></i></span> <input
								type="text" class="input-xlarge" id="email" name="email"
								placeholder="Email для оповещений">
						</div>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label">Учебная группа</label>
					<div class="controls">
						<div class="input-prepend">
							<span class="add-on"><i class="icon-th"></i></span> <input
								type="text" id="academic_group" class="input-xlarge"
								name="academic_group" placeholder="Номер учебной группы">
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">Кафедра</label>
					<div class="controls">
						<div class="input-prepend">
							<span class="add-on"><i class="icon-th-large"></i></span> <input
								type="text" id="department" class="input-xlarge"
								name="department" placeholder="Кафедра">
						</div>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="input01"></label>
					<div class="controls">
						<button type="submit" class="btn btn-success" rel="tooltip"
							title="Сохранить изменения">Сохранить изменения</button>
					</div>

				</div>
		
		 <c:if test="${(actionDone) && (!error)}">
                <div class="alert alert-success">
                  <button type="button" class="close" data-dismiss="alert">&times;</button>
                  ${alert}
                </div>
              </c:if>   
              <c:if test="${(actionDone) && (error)}">
                <div class="alert alert-error">
                  <button type="button" class="close" data-dismiss="alert">&times;</button>
                  ${alert}
                </div>
              </c:if>  
               

			</form>
		</div>
		<div id="footer">
			<a href="/">На главную</a>
		</div>
	</div>
	</div>


	<!-- Javascript placed at the end of the file to make the  page load faster -->
	<script src="http://twitter.github.com/bootstrap/assets/js/jquery.js"></script>

	<script src="twitter-bootstrap-v2\docs\assets\js\bootstrap-button.js"></script>
	<script type="text/javascript" src="jquery.validate.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {

			$("#signup").validate({
				rules : {
					email : {
						required : false,
						email : true
					},
					passwd : {
						required : false,
						minlength : 8
					},
					conpasswd : {
						required : false
					}
				},

				errorClass : "help-inline"

			});
		});
	</script>





	<jsp:include page="include_footer.jsp"></jsp:include>
</body>
</html>
