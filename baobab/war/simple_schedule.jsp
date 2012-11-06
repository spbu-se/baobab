<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="ru.spbu.math.baobab.model.*"%>
<%@ page import="ru.spbu.math.baobab.server.*"%>
<%@ page import="java.util.*"%>
<html>
<body>
	<% List<TableRow> tableRows= (List<TableRow>) request.getAttribute("tableRows"); %>
	<table width="100%" border="1" cellpadding="4" cellspacing="0">
		<caption>Simple Schedule</caption>
		<% for (TableRow row : tableRows) { %>
		<tr>
		  <% for (TableCell cell : row.getRow()) { %>
		    <td> 
		      <% out.print(cell.getValue()); %>
		    </td>
		  <% } %>
		</tr>
		<% } %>
	</table>
</body>
</html>