<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="ru.spbu.math.baobab.model.*"%>
<%@ page import="ru.spbu.math.baobab.server.*"%>
<%@ page import="java.util.*"%>
<html>
<body>
	<% Table table = (Table) request.getAttribute("table"); %>
	<table width="100%" border="1" cellpadding="4" cellspacing="0">
		<caption>Simple Schedule</caption>
		<tr>
			<th>&nbsp;</th>
			<th>&nbsp;</th>
			<% for (Attendee attendee : table.getHorHeaders()) { %>
			<th>
				<% out.print(attendee.getName()); %>
			</th>
			<% } %>
		</tr>
		<% for (TimeSlot timeslot : table.getVertHeader()) { %>
		<tr>
			<th><small> <% out.print(table.getDayOfWeek().get(timeslot)); %>
			</small></th>
			<th>
				<% out.print(timeslot.getName()); %>
			</th>
			<% for (Attendee attendee: table.getHorHeaders()) { %>
			<td>&nbsp;</td>
			<% } %>
		</tr>
		<% } %>
	</table>
</body>
</html>