<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="com.jsneideris.warcry.stores.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Warcry - Login</title>
</head>
<body>

<h1 class="title"><a href="/Warcry/home?auth=${user.token}">Warcry<a/></h1>
by Justinas Sneideris
<hr />

<%
UserStore user = (UserStore)request.getAttribute("user");

if (user == null || !user.getLoggedIn())
{
	if (user != null && user.getLoginError() != LoginError.none)
	{
		LoginError error = user.getLoginError();
		
		if (error == LoginError.noPassword)
			%>Please provide password<br /><%
		else if (error == LoginError.noUsername)
			%>Please provide user name<br /><%
		else if (error == LoginError.userDoesntExist)
			%>User does not exist<br /><%
		else if (error == LoginError.passwordDoesntMatch)
			%>Incorrect password<br /><%
		else
			%>Error while processing your form<br /><%	
	}
%>

<form action="/Warcry/login" method="POST">
User name: <input type="text" name="name" value="${user.name}" />
<br />
Password: <input type="password" name="password" />
<br />
<input type="submit" value="Login" />
</form>
<%
}
else
{
%>
	Thank you for using Warcry, ${user.fullName}!<br />
	<a href="/Warcry/home?auth=${user.token}">Go back to the index page</a><br />
<%
}
%>

</body>
</html>