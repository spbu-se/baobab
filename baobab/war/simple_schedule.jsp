<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="ru.spbu.math.baobab.model.*"%>
<%@ page import="ru.spbu.math.baobab.server.*"%>
<%@ page import="java.util.*"%>
<html>
<body>
	<% Table table = (Table) request.getAttribute("table"); %>
	<% int DayOfWeek = 0; %>
	<% String[] Days = {"Mon","Thu","Wed","Tus","Fri","Sat"}; %>
	<table width="100%" border="1" cellpadding="4" cellspacing="0">
		<caption>Simple Schedule</caption>
		<tr>
			<th>&nbsp;</th>
			<th>&nbsp;</th>
			<% for(Attendee attendee: table.getHorHeaders()) { %>
			<th>
				<% out.print(attendee.getName()); %>
			</th>
			<% } %>
		</tr>
		<% for (TimeSlot timeslot: table.getVertHeader()) { %>
		<tr>
			<% if (timeslot.getDayOfWeek() != DayOfWeek) { %>
			<th><small> <% out.print(Days[DayOfWeek++]); %>
			</small> <%} else {%></th>
			<th>&nbsp;</th>
			<%} %>
			<th>
				<% out.print(timeslot.getName()); %>
			</th>
			<% for (int i = 1; i <= table.getHorHeaders().size(); i++) { %>
			<td>&nbsp;</td>
			<%  } %>
			<% } %>
		</tr>
	</table>
</body>
</html>