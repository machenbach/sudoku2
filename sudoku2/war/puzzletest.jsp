<%@page import="org.mike.sudoku.NoSolutionException"%>
<%@page import="java.util.Random"%>
<%@page import="org.mike.util.Range"%>
<%@page import="org.mike.sudoku.Puzzle"%>
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
	String board = request.getParameter("board");
	if (board == null) {
		board = "     5  75 7 8 3 19      256  29   8    5   37 8 31  9 1   3  4  5 7 1 6 7651   2";
	}
	Puzzle p = new Puzzle();
	p.readBoard(board);
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
						<td><%= p.printSquare(tr*3 + r, tc*3 + c)  %></td>
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
