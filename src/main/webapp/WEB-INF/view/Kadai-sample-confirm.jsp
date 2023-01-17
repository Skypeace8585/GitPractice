<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dto.KadaiAccount" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<p>下記の内容で登録します。よろしいですか？</p>
	<%
		KadaiAccount account = (KadaiAccount)session.getAttribute("input_data");
	%>
	名前：<%=account.getName() %><br>
	メール：<%=account.getMail() %><br>
	パスワード：********<br>
	<a href="KadaiRegisterExecuteServlet">OK</a><br>
	<a href="KadaiRegisterFormServlet">戻る</a>
</body>
</html>