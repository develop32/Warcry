<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="com.jsneideris.warcry.stores.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Warcry - Delete</title>
</head>
<body>

<h1 class="title"><a href="/Warcry/home?auth=${user.token}">Warcry<a/></h1>
by Justinas Sneideris
<hr />

<%
DeleteStore store = (DeleteStore)request.getAttribute("delete");

if (store == null || store.getResult() == DeleteResult.none)
{
	%>No action has been done.<%
}
else
{
	DeleteResult result = store.getResult();
	
	if (result == DeleteResult.noUser)
		%>User not found.<%
	else if (result == DeleteResult.noMessage)
		%>Message not found.<%
	else if (result == DeleteResult.noFriend)
		%>Friend not found.<%
	else if (result == DeleteResult.noAuthorization)
		%>You have no authorization<%
	else if (result == DeleteResult.userDeleted)
		%>User deleted.<%
	else if (result == DeleteResult.messageDeleted)
		%>Message deleted.<%
	else if (result == DeleteResult.friendDeleted)
		%>Friend deleted.<%
	else
		%>Unkonwn error<%
}
%>

<br />
<a href="/Warcry/home?auth=${user.token}">Go back to the index page</a><br />

</body>
</html>