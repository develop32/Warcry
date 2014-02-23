<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="com.jsneideris.warcry.stores.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Warcry - Friend</title>
</head>
<body>

<h1 class="title"><a href="/Warcry/home?auth=${user.token}">Warcry</a></h1>
by Justinas Sneideris
<hr />

<%
FriendStore friend = (FriendStore)request.getAttribute("friend");

if (friend == null)
{
	%>Unexpected error<br /><%
}
else if (friend.getError() != FriendError.none)
{
	FriendError error = friend.getError();
	
	if (error == FriendError.friendDoesntExist)
	{
		%>Friend '${friend.name}' doesn't exist<br /><%
	}
	else
		%>Error while processing<br /><%	
}
else
{
%>
	${friend.name} has been added to your friends!<br />
	<a href="/Warcry/home?auth=${user.token}">Go back to the index page</a><br />
<%
}
%>

</body>
</html>