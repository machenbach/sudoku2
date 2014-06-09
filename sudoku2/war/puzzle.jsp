<%@page import="org.mike.sudoku.NoSolutionException"%>
<%@page import="java.util.Random"%>
<%@page import="org.mike.util.Range"%>
<%@page import="org.mike.sudoku.Builder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html>
<html>
<head>
<title>Sudoku for today</title>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
@media print {
	footer {page-break-after:always;}
}

tr, td
{
padding:0;
border-collapse:collapse;
text-align:center;
border: 1px solid black;
width:45px;
height:45px;
}

table {
padding:0;
border-collapse:collapse;
text-align:center;
width:135px;
height:135px;
font-family:sans-serif;
font-size:30px;
}
</style>
</head>
<body>
<%
	String level = request.getParameter("level");
	if (level == null || level.isEmpty()) {
		level = "35";
	}
	int num = Integer.parseInt(level);
	// brute force this
	Builder p; 
	int tries = 0;
	while (true) {
		try {
	p = new Builder(num); 
	break;
		}
		catch(NoSolutionException e) {
	tries++;
		};
	}
	Random rnd = new Random();
%>

<table style="width:405px; height:405px; align:center; border:2px solid black; margin-left:auto; margin-right:auto">
	<% for (int tr : new Range(3)) {%>
	<tr>
		<% for (int tc : new Range(3)) { %>
		<td>
			<table>
				<% for (int r : new Range(3)) { %>
					<tr>
					<% for (int c : new Range(3)) { %>
						<td><%= p.puzzleElement(tr*3 + r, tc*3 + c)  %></td>
					<% } %>
					</tr>
				<% } %>
			</table>
		</td>
		<% } %>
	</tr>
	<%} %>
</table>
<p style="align:center">
C: <%=Integer.toString(p.getSolverCoverage())%>, T: <%=Integer.toString(p.getSolverTries())%>, 
D: <%= Integer.toString(p.getSolverDepth()) %>, G: <%= Integer.toString(p.getSolverGuessLevel())%>, 
S: <%= Integer.toString(p.getSolveTries()) %> 
</p>

<% for (int i : new Range(40)) { %><br/><% } %> 

<table style="width:360px; height:360px; align:center; border:2px solid black; margin-left:auto; margin-right:auto;">
<% for (int tr : new Range(3)) {%>
<tr>
	<% for (int tc : new Range(3)) { %>
	<td>
	<table>
		<% for (int r : new Range(3)) { %>
			<tr>
			<% for (int c : new Range(3)) { %>
				<td><%= p.puzzleSolution(tr*3 + r, tc*3 + c)  %></td>
			<% } %>
			</tr>
		<% } %>
	</table>
	</td>
	<% } %>
</tr>
<%} %>
</table>
</body>
