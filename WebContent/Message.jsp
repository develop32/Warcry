<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="com.jsneideris.warcry.stores.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Warcry - Compose</title>
</head>
<body>

<h1 class="title"><a href="/Warcry/home?auth=${user.token}">Warcry<a/></h1>
by Justinas Sneideris

<%

UserStore user = (UserStore)request.getAttribute("user");
String userName = "";

if (user != null && user.getLoggedIn())
{
	userName = user.getName();
	%>
	<h4><a href="/Warcry/profile?auth=${user.token}">${user.fullName}</a></h4>
	<h4><a href="/Warcry/logout?auth=${user.token}">Log out</a></h4>
	<hr />
	<%
}
else
{
	%>
	<h4><a href="/Warcry/register">Register</a></h4>
	<h4><a href="/Warcry/login">Login</a></h4>
	<hr />
	<%
}

MessageStore message = (MessageStore)request.getAttribute("message");

if (user == null || !user.getLoggedIn())
{
	%>You are not logged in!<%
}
else if (message != null && message.getResult() == MessageResult.success)
{
	%>Message sent successfully!<%
}
else
{
	if (message != null)
	{
		MessageResult result = message.getResult();
	
		if (result == MessageResult.noMessage)
			%>Please enter message<br /><%
		else if (result == MessageResult.tooLong)
			%>Message is too long<br /><%
		else if (result == MessageResult.unknown)
			%>Unknown error<br /><%
	}
%>

<form action="/Warcry/message?auth=${user.token}" method="POST">
<textarea name="text">${message.text}</textarea>
<br />
<input type="submit" value="Send" />
</form>
<%
}
%>

<br />
<a href="/Warcry/home?auth=${user.token}">Go back to the index page</a><br />

</body>
</html>