<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%! public static String userId = "hello"; %>
<!doctype html>
<link href="../assets/css/bootstrap-responsive.css" rel="stylesheet">
<script src="bootstrap/js/bootstrap.min.js"></script>

<div class="navbar navbar-fixed-top">
	<div class="navbar-inner">
		<div class="container">
			<a class="brand" href="/">Baobab</a>
			<div class="nav-collapse collapse navbar-responsive-collapse">
				<ul class="nav">
					<c:forEach var="calendar" items="${calendarList}">
						<li><a href="/calendar/${calendar.ID}">${calendar.ID}</a></li>
					</c:forEach>
				</ul>
			</div>
			<div class="nav-collapse collapse pull-right">
				<ul class="nav">
					<div class="btn-group">
						<!--<c:if test="${empty user_id}">
							<a class="btn" href="/auth/twitter">Sign in</a>
						</c:if>
						<c:if test="${not empty user_id}">
							<a class="btn" href="/auth/logout">Sign out</a>
						</c:if>
						<c:if test="${empty user_id}">
							<button data-toggle="dropdown"
								class="btn dropdown-toggle disabled">
								<span class="caret"></span>
							</button>
						</c:if>
						<c:if test="${not empty user_id}">
							<button data-toggle="dropdown" class="btn dropdown-toggle">
								<span class="caret"></span>
							</button>
						</c:if>-->
						
						<!-- СТЕРЕТЬ ЭТО В ПРОДАКШН -->
						<a class="btn" href="/auth/logout">Sign out</a>
						<button data-toggle="dropdown" class="btn dropdown-toggle">
								<span class="caret"></span>
							</button>
						<!-- ДО СЮДА -->
						
						<ul class="dropdown-menu">
							<li><a href="/profile.jsp">Профиль</a></li>
							<li><a href="#">Меню</a></li>
						</ul>
					</div>
				</ul>
			</div>
		</div>
	</div>
</div>