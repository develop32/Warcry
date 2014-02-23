<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="com.jsneideris.warcry.stores.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Warcry - Registration</title>
</head>
<body>

<h1 class="title"><a href="/Warcry/home?auth=${user.token}">Warcry<a/></h1>
by Justinas Sneideris
<hr />

<%
UserStore user = (UserStore)request.getAttribute("user");

if (user == null || !user.getLoggedIn())
{
	if (user != null && user.getRegisterError() != RegisterError.none)
	{
		RegisterError error = user.getRegisterError();
	
		if (error == RegisterError.noPassword)
			%>Please provide password<br /><%
		else if (error == RegisterError.noUsername)
			%>Please provide user name<br /><%
		else if (error == RegisterError.userExists)
			%>User already exists<br /><%
		else if (error == RegisterError.passwordsDontMatch)
			%>Passwords don't match<br /><%
		else
			%>Error while processing your form<br /><%	
	}
%>

<form action="/Warcry/register" method="POST">
User name: <input type="text" name="name" value="${user.name}" />
<br />
Password: <input type="password" name="password1" />
<br />
Repeat password: <input type="password" name="password2" />
<br />
<input type="submit" value="Register" />
</form>
<%
}
else
{
%>
	Thank you for using Warcry, ${user.name}!<br />
	<a href="/Warcry/home?auth=${user.token}">Go back to the index page</a><br />
<%
}
%>

</body>
</html>